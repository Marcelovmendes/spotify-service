package com.example.spotify.auth.infrastructure.adapter;

import com.example.spotify.auth.domain.entity.Token;
import com.example.spotify.auth.domain.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionTokenAdapter implements TokenRepository {
    private static final String ACCESS_TOKEN_KEY = "spotifyAccessToken";
    private static final String REFRESH_TOKEN_KEY = "spotifyRefreshToken";
    private static final String TOKEN_EXPIRY_KEY = "spotifyTokenExpiry";

    private final HttpServletRequest request;

    public SessionTokenAdapter(HttpServletRequest request) {
        this.request = request;
    }


    @Override
    public void save(String sessionId, Token token) {
        HttpSession session = request.getSession(false);
        if (session == null || !session.getId().equals(sessionId)) {
            session = request.getSession(true);
        }
        session.setAttribute(ACCESS_TOKEN_KEY, token.getAccessToken());
        session.setAttribute(REFRESH_TOKEN_KEY, token.getRefreshToken());
        session.setAttribute(TOKEN_EXPIRY_KEY, token.getExpiresAt().toEpochMilli());

    }

    @Override
    public Optional<Token> findBySessionId(String sessionId) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        String accessToken = (String) session.getAttribute(ACCESS_TOKEN_KEY);
        String refreshToken = (String) session.getAttribute(REFRESH_TOKEN_KEY);
        Long expiryMillis = (Long) session.getAttribute(TOKEN_EXPIRY_KEY);

        if (accessToken == null || refreshToken == null || expiryMillis == null) {
            return Optional.empty();
        }

        int expiresIn = (int) ((expiryMillis - System.currentTimeMillis()) / 1000);
        if (expiresIn <= 0) {
            return Optional.empty();
        }

        return Optional.of(Token.create(accessToken, refreshToken, expiresIn));
    }

    @Override
    public void remove(String sessionId) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getId().equals(sessionId)) {
            session.removeAttribute(ACCESS_TOKEN_KEY);
            session.removeAttribute(REFRESH_TOKEN_KEY);
            session.removeAttribute(TOKEN_EXPIRY_KEY);
        }
    }
}
