package com.example.spotify.auth.domain.entity;

import java.time.Instant;
import java.util.Objects;

public class AuthState {
    private final String stateValue;
    private final String codeVerifier;
    private final Instant createdAt;

    private AuthState(String stateValue, String codeVerifier, Instant createdAt) {
        this.stateValue = Objects.requireNonNull(stateValue, "State value cannot be null");
        this.codeVerifier = Objects.requireNonNull(codeVerifier, "Code verifier cannot be null");
        this.createdAt = createdAt;
    }

    public static AuthState create(String stateValue, String codeVerifier) {
        return new AuthState(stateValue, codeVerifier, Instant.now());
    }

    public String getStateValue() { return stateValue; }
    public String getCodeVerifier() { return codeVerifier; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isValid() {
        return !stateValue.isBlank() && !codeVerifier.isBlank();
    }
}
