package com.zunftwerk.app.zunftwerkapi.service;

import com.azure.security.keyvault.secrets.SecretClient;
import com.zunftwerk.app.zunftwerkapi.exception.DecryptionFailedException;
import com.zunftwerk.app.zunftwerkapi.exception.EncryptionFailedException;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CryptService {

    private SecretKey secretKey;
    private static final int IV_SIZE = 12;

    private final SecretClient secretClient;

    private final String keyName;

    @Autowired
    public CryptService(SecretClient secretClient,
                        @Value("${AZURE_KEY_NAME}") String keyName) {
        this.secretClient = secretClient;
        this.keyName = keyName;
    }

    @PostConstruct
    public void init() {
        String keyBase64 = secretClient.getSecret(keyName).getValue();
        if (keyBase64 == null || keyBase64.isEmpty()) {
            throw new IllegalStateException("No SecretKey found!");
        }
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid key length! At least 32bytes!");
        }
        this.secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    @SneakyThrows
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_SIZE];
            new java.security.SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParam = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParam);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new EncryptionFailedException("Encryption failed" + e.getMessage());
        }
    }

    @SneakyThrows
    public String decrypt(String base64Ciphertext) {
        try {
            byte[] data = Base64.getDecoder().decode(base64Ciphertext);
            ByteBuffer byteBuffer = ByteBuffer.wrap(data);

            byte[] iv = new byte[IV_SIZE];
            byteBuffer.get(iv);
            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParam = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParam);

            byte[] plainBytes = cipher.doFinal(ciphertext);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionFailedException("Decryption failed" + e.getMessage());
        }
    }
}
