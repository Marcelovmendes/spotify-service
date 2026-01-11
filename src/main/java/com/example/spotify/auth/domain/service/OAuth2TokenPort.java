package com.example.spotify.auth.domain.service;

import com.example.spotify.auth.domain.entity.Token;

public interface OAuth2TokenPort {
    Token createFromSession(String accessToken, String refreshToken, Long expiryMillis);
    Token createFromOAuth2Token(Token originalToken);
}
