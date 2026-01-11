package com.example.spotify.auth.application;

import com.example.spotify.auth.domain.entity.Token;

import java.util.Optional;

public interface TokenQuery {
    Optional<Token> getCurrentUserToken();
    boolean isUserAuthenticated();
    void storeUserToken(String sessionId, Token token);
    void isValidateToken(String sessionId);
    Token refreshToken(String sessionId);
}
