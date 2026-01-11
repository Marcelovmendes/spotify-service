package com.example.spotify.auth.infrastructure.adapter;

import com.example.spotify.auth.domain.service.PkceGenerator;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class DefaultPkceAdapter implements PkceGenerator {

    @Override
    public String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);

        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return encoded.replaceAll("[^a-zA-Z0-9\\-._~]", "");
    }

    @Override
    public String generateCodeChallenge(String codeVerifier) {
        try {
            byte[] verifierByte = codeVerifier.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(verifierByte);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
