package com.zunftwerk.app.zunftwerkapi.service;

import com.azure.security.keyvault.secrets.SecretClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private String secretKey;
    private final SecretClient secretClient;

    private final String keyName;

    @Autowired
    public JwtService(SecretClient secretClient,
                      @Value("${AZURE_JWT_KEY_NAME}") String keyName)
    {
        this.secretClient = secretClient;
        this.keyName = keyName;
    }

    @PostConstruct
    public void init()
    {
        String keyBase64 = secretClient.getSecret(keyName).getValue();

        if (keyBase64 == null || keyBase64.isEmpty())
        {
            throw new IllegalStateException("Secret does not exist");
        }

        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);

        if (keyBytes.length != 32)
        {
            throw new IllegalArgumentException("Secret does not contain 32 bytes");
        }

        SecretKey encodedSecretKey = new SecretKeySpec(keyBytes, "AES");

        this.secretKey = Base64.getEncoder().encodeToString(encodedSecretKey.getEncoded());
    }

    public String generateToken(String username, Long organizationId, List<String> roles, List<String> modules)
    {
        Map<String, Object> claims = new HashMap<>();

        claims.put("username", username);
        claims.put("organizationId", organizationId);
        claims.put("roles", roles);
        claims.put("modules", modules);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(String username)
    {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh_token");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);

        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }
}

