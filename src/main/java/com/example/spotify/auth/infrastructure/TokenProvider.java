package com.example.spotify.auth.infrastructure;

public interface TokenProvider {
    String getAccessToken();
    boolean isTokenValid();
}
