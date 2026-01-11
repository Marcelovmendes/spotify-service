package com.example.spotify.auth.infrastructure.adapter;


import com.example.spotify.auth.application.OAuthClient;
import com.example.spotify.auth.domain.entity.authorizationRequest;
import com.example.spotify.auth.domain.entity.Token;
import com.example.spotify.common.exception.*;
import com.example.spotify.common.infrastructure.adapter.ExternalServiceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERefreshRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.pkce.AuthorizationCodePKCERequest;

import java.net.URI;

@Component
public class SpotifyOAuthAdapter extends ExternalServiceAdapter implements OAuthClient {

    private static final Logger log = LoggerFactory.getLogger(SpotifyOAuthAdapter.class);

    public SpotifyOAuthAdapter(SpotifyApi spotifyApi, SpotifyApiExceptionTranslator exceptionTralator) {
        super(spotifyApi, exceptionTralator);
    }

    @Override
    public URI createAuthorizationUri(authorizationRequest authorizationRequestRequest) {
                 AuthorizationCodeUriRequest request = spotifyApi.authorizationCodePKCEUri(authorizationRequestRequest.getCodeChallenge())
                         .state(authorizationRequestRequest.getState())
                         .scope(authorizationRequestRequest.getScopesAsString())
                         .build();

                 return executeSync(request::execute,
                "creating authorization URI"
                  );

    }
    @Override
    public Token exchangeCodeForToken(String code, String codeVerifier) {
               AuthorizationCodePKCERequest request = spotifyApi
                       .authorizationCodePKCE(code, codeVerifier)
                       .build();
               AuthorizationCodeCredentials credentials = executeSync(request::execute,
                       "exchanging code for token"
               );
               return Token.create(
                       credentials.getAccessToken(),
                       credentials.getRefreshToken(),
                       credentials.getExpiresIn()
               );


    }

    @Override
    public Token refreshAccessToken(String refreshToken) {

              spotifyApi.setRefreshToken(refreshToken);

                 AuthorizationCodePKCERefreshRequest request = spotifyApi
                         .authorizationCodePKCERefresh()
                         .build();
                 AuthorizationCodeCredentials credentials = executeSync(request::execute,
                        "refreshing access token");
                 return Token.create(
                        credentials.getAccessToken(),
                        credentials.getRefreshToken(),
                        credentials.getExpiresIn()
                );

    };

}
