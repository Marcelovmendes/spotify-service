package com.example.spotify.auth.application;

import com.example.spotify.auth.domain.entity.authorizationRequest;
import com.example.spotify.auth.domain.entity.Token;

import java.net.URI;

public interface OAuthClient {
    URI createAuthorizationUri(authorizationRequest request);
    Token exchangeCodeForToken(String code, String codeVerifier);
    Token refreshAccessToken(String refreshToken);
}
