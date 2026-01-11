package com.example.spotify.auth.application;

import com.example.spotify.auth.domain.entity.Token;

import java.net.URI;

public interface AuthUseCase {
    URI initiateAuthentication();
    Token completeAuthentication(String code, String state);
}
