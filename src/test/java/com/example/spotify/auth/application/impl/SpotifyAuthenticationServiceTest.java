package com.example.spotify.auth.application.impl;

import com.example.spotify.auth.application.OAuthClient;
import com.example.spotify.auth.domain.entity.AuthState;
import com.example.spotify.auth.domain.entity.Token;
import com.example.spotify.auth.domain.entity.authorizationRequest;
import com.example.spotify.auth.domain.repository.AuthStateRepository;
import com.example.spotify.auth.domain.service.PkceGenerator;
import com.example.spotify.common.exception.AuthenticationException;
import com.example.spotify.common.exception.ErrorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpotifyAuthenticationService Unit Tests")
class SpotifyAuthenticationServiceTest {

    @Mock
    private AuthStateRepository authStateRepository;

    @Mock
    private OAuthClient oauthClient;

    @Mock
    private PkceGenerator pkceGenerator;

    @InjectMocks
    private SpotifyAuthenticationService service;

    private static final String CODE_VERIFIER = "test-code-verifier";
    private static final String CODE_CHALLENGE = "test-code-challenge";
    private static final String STATE_VALUE = "teststate12345";
    private static final String AUTH_CODE = "test-auth-code";
    private static final URI AUTHORIZATION_URI = URI.create("https://accounts.spotify.com/authorize?state=test");

    @BeforeEach
    void setUp() {
    
    }

    @Test
    @DisplayName("initiateAuthentication should successfully create authorization URI")
    void initiateAuthentication_Success() {
        // Given
        when(pkceGenerator.generateCodeVerifier()).thenReturn(CODE_VERIFIER);
        when(pkceGenerator.generateCodeChallenge(CODE_VERIFIER)).thenReturn(CODE_CHALLENGE);
        when(oauthClient.createAuthorizationUri(any(authorizationRequest.class))).thenReturn(AUTHORIZATION_URI);

        // When
        URI result = service.initiateAuthentication();

        // Then
        assertThat(result).isEqualTo(AUTHORIZATION_URI);

        verify(pkceGenerator).generateCodeVerifier();
        verify(pkceGenerator).generateCodeChallenge(CODE_VERIFIER);
        verify(authStateRepository).save(any(AuthState.class), eq(Duration.ofMinutes(10)));
        verify(oauthClient).createAuthorizationUri(any(authorizationRequest.class));
    }

    @Test
    @DisplayName("initiateAuthentication should save AuthState with correct timeout")
    void initiateAuthentication_SavesAuthStateWithCorrectTimeout() {
        // Given
        when(pkceGenerator.generateCodeVerifier()).thenReturn(CODE_VERIFIER);
        when(pkceGenerator.generateCodeChallenge(CODE_VERIFIER)).thenReturn(CODE_CHALLENGE);
        when(oauthClient.createAuthorizationUri(any(authorizationRequest.class))).thenReturn(AUTHORIZATION_URI);

        ArgumentCaptor<AuthState> stateCaptor = ArgumentCaptor.forClass(AuthState.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        // When
        service.initiateAuthentication();

        // Then
        verify(authStateRepository).save(stateCaptor.capture(), durationCaptor.capture());

        AuthState capturedState = stateCaptor.getValue();
        assertThat(capturedState.getCodeVerifier()).isEqualTo(CODE_VERIFIER);
        assertThat(capturedState.getStateValue()).isNotNull();
        assertThat(capturedState.isValid()).isTrue();

        Duration capturedDuration = durationCaptor.getValue();
        assertThat(capturedDuration).isEqualTo(Duration.ofMinutes(10));
    }

    @Test
    @DisplayName("initiateAuthentication should create authorization request with correct parameters")
    void initiateAuthentication_CreatesCorrectAuthorizationRequest() {
        // Given
        when(pkceGenerator.generateCodeVerifier()).thenReturn(CODE_VERIFIER);
        when(pkceGenerator.generateCodeChallenge(CODE_VERIFIER)).thenReturn(CODE_CHALLENGE);
        when(oauthClient.createAuthorizationUri(any(authorizationRequest.class))).thenReturn(AUTHORIZATION_URI);

        ArgumentCaptor<authorizationRequest> requestCaptor = ArgumentCaptor.forClass(authorizationRequest.class);

        // When
        service.initiateAuthentication();

        // Then
        verify(oauthClient).createAuthorizationUri(requestCaptor.capture());

        authorizationRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getCodeChallenge()).isEqualTo(CODE_CHALLENGE);
        assertThat(capturedRequest.getState()).isNotNull();
        assertThat(capturedRequest.getScopesAsString())
                .contains("user-read-private")
                .contains("user-read-email")
                .contains("playlist-read-private")
                .contains("playlist-read-collaborative")
                .contains("user-library-read");
    }

    @Test
    @DisplayName("initiateAuthentication should throw AuthenticationException when PKCE generation fails")
    void initiateAuthentication_ThrowsException_WhenPkceGenerationFails() {
        // Given
        when(pkceGenerator.generateCodeVerifier()).thenThrow(new RuntimeException("PKCE generation failed"));

        // When/Then
        Throwable thrown = catchThrowable(() -> service.initiateAuthentication());

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Failed to initiate authentication");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(authStateRepository, never()).save(any(), any());
        verify(oauthClient, never()).createAuthorizationUri(any());
    }

    @Test
    @DisplayName("initiateAuthentication should throw AuthenticationException when repository save fails")
    void initiateAuthentication_ThrowsException_WhenRepositorySaveFails() {
        // Given
        when(pkceGenerator.generateCodeVerifier()).thenReturn(CODE_VERIFIER);
        when(pkceGenerator.generateCodeChallenge(CODE_VERIFIER)).thenReturn(CODE_CHALLENGE);
        doThrow(new RuntimeException("Redis connection failed"))
                .when(authStateRepository).save(any(AuthState.class), any(Duration.class));

        // When/Then
        Throwable thrown = catchThrowable(() -> service.initiateAuthentication());

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Failed to initiate authentication");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(oauthClient, never()).createAuthorizationUri(any());
    }

    @Test
    @DisplayName("completeAuthentication should successfully exchange code for token")
    void completeAuthentication_Success() {
        // Given
        AuthState authState = AuthState.create(STATE_VALUE, CODE_VERIFIER);
        Token expectedToken = Token.create("access-token", "refresh-token", 3600);

        when(authStateRepository.findByStateValue(STATE_VALUE)).thenReturn(Optional.of(authState));
        when(oauthClient.exchangeCodeForToken(AUTH_CODE, CODE_VERIFIER)).thenReturn(expectedToken);

        // When
        Token result = service.completeAuthentication(AUTH_CODE, STATE_VALUE);

        // Then
        assertThat(result).isEqualTo(expectedToken);
        verify(authStateRepository).findByStateValue(STATE_VALUE);
        verify(oauthClient).exchangeCodeForToken(AUTH_CODE, CODE_VERIFIER);
        verify(authStateRepository).remove(STATE_VALUE);
    }

    @Test
    @DisplayName("completeAuthentication should remove state after successful token exchange")
    void completeAuthentication_RemovesStateAfterSuccess() {
        // Given
        AuthState authState = AuthState.create(STATE_VALUE, CODE_VERIFIER);
        Token token = Token.create("access-token", "refresh-token", 3600);

        when(authStateRepository.findByStateValue(STATE_VALUE)).thenReturn(Optional.of(authState));
        when(oauthClient.exchangeCodeForToken(AUTH_CODE, CODE_VERIFIER)).thenReturn(token);

        // When
        service.completeAuthentication(AUTH_CODE, STATE_VALUE);

        // Then
        verify(authStateRepository).remove(STATE_VALUE);
    }

    @Test
    @DisplayName("completeAuthentication should throw AuthenticationException when code is null")
    void completeAuthentication_ThrowsException_WhenCodeIsNull() {
        // When/Then
        Throwable thrown = catchThrowable(() -> service.completeAuthentication(null, STATE_VALUE));

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid code or state provided");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(authStateRepository, never()).findByStateValue(any());
        verify(oauthClient, never()).exchangeCodeForToken(any(), any());
    }

    @Test
    @DisplayName("completeAuthentication should throw AuthenticationException when state is null")
    void completeAuthentication_ThrowsException_WhenStateIsNull() {
        // When/Then
        Throwable thrown = catchThrowable(() -> service.completeAuthentication(AUTH_CODE, null));

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid code or state provided");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(authStateRepository, never()).findByStateValue(any());
        verify(oauthClient, never()).exchangeCodeForToken(any(), any());
    }

    @Test
    @DisplayName("completeAuthentication should throw AuthenticationException when both code and state are null")
    void completeAuthentication_ThrowsException_WhenCodeAndStateAreNull() {
        // When/Then
        Throwable thrown = catchThrowable(() -> service.completeAuthentication(null, null));

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid code or state provided");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(authStateRepository, never()).findByStateValue(any());
        verify(oauthClient, never()).exchangeCodeForToken(any(), any());
    }

    @Test
    @DisplayName("completeAuthentication should throw AuthenticationException when state is not found")
    void completeAuthentication_ThrowsException_WhenStateNotFound() {
        // Given
        when(authStateRepository.findByStateValue(STATE_VALUE)).thenReturn(Optional.empty());

        // When/Then
        Throwable thrown = catchThrowable(() -> service.completeAuthentication(AUTH_CODE, STATE_VALUE));

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid state provided");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(authStateRepository).findByStateValue(STATE_VALUE);
        verify(oauthClient, never()).exchangeCodeForToken(any(), any());
        verify(authStateRepository, never()).remove(any());
    }

    @Test
    @DisplayName("completeAuthentication should throw AuthenticationException when token exchange fails")
    void completeAuthentication_ThrowsException_WhenTokenExchangeFails() {
        // Given
        AuthState authState = AuthState.create(STATE_VALUE, CODE_VERIFIER);
        when(authStateRepository.findByStateValue(STATE_VALUE)).thenReturn(Optional.of(authState));
        when(oauthClient.exchangeCodeForToken(AUTH_CODE, CODE_VERIFIER))
                .thenThrow(new RuntimeException("Spotify API error"));

        // When/Then
        Throwable thrown = catchThrowable(() -> service.completeAuthentication(AUTH_CODE, STATE_VALUE));

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Failed to exchange code for token");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.AUTHENTICATION_EXCEPTION);

        verify(authStateRepository).findByStateValue(STATE_VALUE);
        verify(oauthClient).exchangeCodeForToken(AUTH_CODE, CODE_VERIFIER);
        verify(authStateRepository, never()).remove(any());
    }

    @Test
    @DisplayName("completeAuthentication should not remove state when token exchange fails")
    void completeAuthentication_DoesNotRemoveState_WhenTokenExchangeFails() {
        // Given
        AuthState authState = AuthState.create(STATE_VALUE, CODE_VERIFIER);
        when(authStateRepository.findByStateValue(STATE_VALUE)).thenReturn(Optional.of(authState));
        when(oauthClient.exchangeCodeForToken(AUTH_CODE, CODE_VERIFIER))
                .thenThrow(new RuntimeException("Network error"));

        // When/Then
        assertThatThrownBy(() -> service.completeAuthentication(AUTH_CODE, STATE_VALUE))
                .isInstanceOf(AuthenticationException.class);

        verify(authStateRepository, never()).remove(STATE_VALUE);
    }

    @Test
    @DisplayName("completeAuthentication should use correct code verifier from saved state")
    void completeAuthentication_UsesCorrectCodeVerifier() {
        // Given
        String customCodeVerifier = "custom-verifier-12345";
        AuthState authState = AuthState.create(STATE_VALUE, customCodeVerifier);
        Token token = Token.create("access-token", "refresh-token", 3600);

        when(authStateRepository.findByStateValue(STATE_VALUE)).thenReturn(Optional.of(authState));
        when(oauthClient.exchangeCodeForToken(AUTH_CODE, customCodeVerifier)).thenReturn(token);

        // When
        service.completeAuthentication(AUTH_CODE, STATE_VALUE);

        // Then
        verify(oauthClient).exchangeCodeForToken(AUTH_CODE, customCodeVerifier);
    }
}
