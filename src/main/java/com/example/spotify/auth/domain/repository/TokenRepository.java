package com.example.spotify.auth.domain.repository;

import com.example.spotify.auth.domain.entity.Token;

import java.util.Optional;

public interface TokenRepository {
    void save(String sessionId, Token token);
    Optional<Token> findBySessionId(String sessionId);
    void remove(String sessionId);
}
