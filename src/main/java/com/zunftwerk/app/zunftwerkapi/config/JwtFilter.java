package com.zunftwerk.app.zunftwerkapi.config;

import com.zunftwerk.app.zunftwerkapi.security.CustomAuthenticationToken;
import com.zunftwerk.app.zunftwerkapi.service.JwtService;
import com.zunftwerk.app.zunftwerkapi.service.MyUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ApplicationContext context;

    public JwtFilter(JwtService jwtService, ApplicationContext context) {
        this.jwtService = jwtService;
        this.context = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                token = authHeader.substring(7);
                username = jwtService.extractUserName(token);
            } catch (Exception e) {
                log.error("Error extracting JWT username: {}", e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Extrahiere zusätzliche Claims
                Claims claims = jwtService.extractAllClaims(token);
                Long organizationId = claims.get("organizationId", Long.class);
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                @SuppressWarnings("unchecked")
                List<String> modules = claims.get("modules", List.class);

                // Hole die UserDetails (ggf. kannst du diese erweitern, um zusätzliche Felder zu berücksichtigen)
                UserDetails userDetails = context.getBean(MyUserDetailsService.class)
                        .loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    // Erstelle ein benutzerdefiniertes Authentication-Objekt, das zusätzliche Informationen enthält
                    CustomAuthenticationToken authToken = new CustomAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setOrganizationId(organizationId);
                    authToken.setRoles(roles);
                    authToken.setModules(modules);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                log.error("Error validating JWT token: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
