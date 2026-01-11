package com.example.spotify.auth.domain.entity;

import java.time.Instant;

public class Token {

    private final String accessToken;
    private final String refreshToken;
    private final Instant expiresAt;

    private Token(String accessToken, String refreshToken, Instant expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public static Token create(String accessToken, String refreshToken, int expiresIn) {
        Instant expiresAt = Instant.now().plusSeconds(expiresIn);
        return new Token(accessToken, refreshToken, expiresAt);
    }

    public static Token fromAccessToken(String accessToken) {
        return new Token(accessToken, null, Instant.now().plusSeconds(3600));
    }

    public String getAccessToken() {return accessToken;}
    public String getRefreshToken() { return refreshToken; }
    public Instant getExpiresAt() { return expiresAt; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    public boolean isValid() {return accessToken != null && !accessToken.isBlank() && !isExpired(); }

}
