package com.example.spotify.auth.domain.service;

public interface PkceGenerator {
    String generateCodeVerifier();
    String generateCodeChallenge(String codeVerifier);
}
