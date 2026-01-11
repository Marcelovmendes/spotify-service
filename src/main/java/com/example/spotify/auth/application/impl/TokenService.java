package com.example.spotify.auth.application.impl;

import com.example.spotify.auth.application.OAuthClient;
import com.example.spotify.auth.application.TokenQuery;
import com.example.spotify.auth.domain.entity.Token;
import com.example.spotify.auth.domain.repository.TokenRepository;
import com.example.spotify.common.exception.ApplicationException;
import com.example.spotify.common.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService implements TokenQuery {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);
    private final HttpServletRequest request;
    private final TokenRepository tokenRepository;
    private final OAuthClient oauthClient;

    public TokenService(HttpServletRequest request, TokenRepository tokenRepository, OAuthClient oauthClient) {
        this.request = request;
        this.tokenRepository = tokenRepository;
        this.oauthClient = oauthClient;
    }

    @Override
    public Optional<Token> getCurrentUserToken() {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            return Optional.of(Token.fromAccessToken(accessToken));
        }

        String sessionId = request.getSession(false) != null ? request.getSession(false).getId() : null;
        if (sessionId == null) {
            log.warn("Session ID is null");
            return Optional.empty();
        }
        return tokenRepository.findBySessionId(sessionId);
    }

    @Override
    public boolean isUserAuthenticated() {
        return getCurrentUserToken()
                .map(Token::isValid)
                .orElse(false);
    }

    @Override
    public void storeUserToken(String sessionId, Token token) {
        try {
            tokenRepository.save(sessionId, token);
        } catch (Exception e) {
            log.error("Error storing token for session ID: {}", sessionId, e);
            throw new ApplicationException("Failed to store token", ErrorType.SERVER_ERROR) {
            };
        }
    }

    @Override
    public void isValidateToken(String sessionId) {
        tokenRepository.remove(sessionId);
    }


    @Override
    public Token refreshToken(String sessionId) {
        Token existingToken = tokenRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalStateException("No token found for session ID: " + sessionId));
        if (existingToken.getRefreshToken() == null) {
            throw new IllegalStateException("No refresh token available for session ID: " + sessionId);
        }
        try {
            Token refhresdToken = oauthClient.refreshAccessToken(existingToken.getRefreshToken());
            tokenRepository.save(sessionId, refhresdToken);
            return refhresdToken;
        } catch (Exception e) {
            log.error("Error refreshing token for session ID: {}", sessionId, e);
            throw new ApplicationException("Failed to refresh token", ErrorType.SERVER_ERROR) {
            };
        }
    }
}
