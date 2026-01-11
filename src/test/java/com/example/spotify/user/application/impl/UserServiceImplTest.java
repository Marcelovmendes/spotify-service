package com.example.spotify.user.application.impl;

import com.example.spotify.auth.infrastructure.TokenProvider;
import com.example.spotify.common.exception.AuthenticationException;
import com.example.spotify.common.exception.ErrorType;
import com.example.spotify.common.exception.UserProfileException;
import com.example.spotify.user.api.dto.UserProfileDTO;
import com.example.spotify.user.domain.UserProfilePort;
import com.example.spotify.user.domain.entity.Email;
import com.example.spotify.user.domain.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

    @Mock
    private UserProfilePort userProfilePort;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private UserServiceImpl service;

    private static final String ACCESS_TOKEN = "test-access-token";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String USER_ID = "spotify123";
    private static final LocalDate BIRTHDATE = LocalDate.of(1990, 1, 1);

    @Test
    @DisplayName("getCurrentUserProfileAsync should successfully return user profile")
    void getCurrentUserProfileAsync_Success() {
        UserEntity userEntity = createValidUserEntity();

        when(tokenProvider.isTokenValid()).thenReturn(true);
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(userProfilePort.getCurrentUsersProfileAsync(ACCESS_TOKEN)).thenReturn(userEntity);

        UserProfileDTO result = service.getCurrentUserProfileAsync();

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(VALID_EMAIL);
        assertThat(result.displayName()).isEqualTo("Test User");
        assertThat(result.country()).isEqualTo("BR");
        assertThat(result.birthdate()).isEqualTo(BIRTHDATE);
        assertThat(result.followersCount()).isEqualTo(100);
        assertThat(result.externalUrls()).isEqualTo("https://open.spotify.com/user/test");
        assertThat(result.href()).isEqualTo("https://api.spotify.com/v1/users/test");
        assertThat(result.photoCover()).isEqualTo("https://i.scdn.co/image/test");
        assertThat(result.spotifyUri()).isEqualTo("spotify:user:test");
        assertThat(result.type()).isEqualTo("user");

        verify(tokenProvider).isTokenValid();
        verify(tokenProvider).getAccessToken();
        verify(userProfilePort).getCurrentUsersProfileAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should throw AuthenticationException when token is invalid")
    void getCurrentUserProfileAsync_ThrowsException_WhenTokenIsInvalid() {
        when(tokenProvider.isTokenValid()).thenReturn(false);

        Throwable thrown = catchThrowable(() -> service.getCurrentUserProfileAsync());

        assertThat(thrown)
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Token de acesso inválido");
        assertThat(((AuthenticationException) thrown).getType()).isEqualTo(ErrorType.SERVER_ERROR);

        verify(tokenProvider).isTokenValid();
        verify(tokenProvider, never()).getAccessToken();
        verify(userProfilePort, never()).getCurrentUsersProfileAsync(any());
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should throw UserProfileException when email is invalid")
    void getCurrentUserProfileAsync_ThrowsException_WhenEmailIsInvalid() {
        UserEntity userEntity = new UserEntity(
                USER_ID,
                BIRTHDATE,
                "BR",
                "Test User",
                new Email(""),
                "https://open.spotify.com/user/test",
                100,
                "https://api.spotify.com/v1/users/test",
                "https://i.scdn.co/image/test",
                "spotify:user:test",
                "user"
        );

        when(tokenProvider.isTokenValid()).thenReturn(true);
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(userProfilePort.getCurrentUsersProfileAsync(ACCESS_TOKEN)).thenReturn(userEntity);

        Throwable thrown = catchThrowable(() -> service.getCurrentUserProfileAsync());

        assertThat(thrown)
                .isInstanceOf(UserProfileException.class)
                .hasMessage("Email not found in Spotify user data");
        assertThat(((UserProfileException) thrown).getType()).isEqualTo(ErrorType.RESOURCE_NOT_FOUND_EXCEPTION);

        verify(tokenProvider).isTokenValid();
        verify(tokenProvider).getAccessToken();
        verify(userProfilePort).getCurrentUsersProfileAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should throw UserProfileException when mapping fails")
    void getCurrentUserProfileAsync_ThrowsException_WhenMappingFails() {
        UserEntity userEntity = mock(UserEntity.class);
        Email validEmail = Email.of(VALID_EMAIL);

        when(tokenProvider.isTokenValid()).thenReturn(true);
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(userProfilePort.getCurrentUsersProfileAsync(ACCESS_TOKEN)).thenReturn(userEntity);
        when(userEntity.getEmail()).thenReturn(validEmail);
        when(userEntity.getBirthdate()).thenThrow(new RuntimeException("Unexpected error"));

        Throwable thrown = catchThrowable(() -> service.getCurrentUserProfileAsync());

        assertThat(thrown)
                .isInstanceOf(UserProfileException.class)
                .hasMessage("Erro ao processar perfil do usuário");
        assertThat(((UserProfileException) thrown).getType()).isEqualTo(ErrorType.SERVER_ERROR);

        verify(tokenProvider).isTokenValid();
        verify(tokenProvider).getAccessToken();
        verify(userProfilePort).getCurrentUsersProfileAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should use correct access token")
    void getCurrentUserProfileAsync_UsesCorrectAccessToken() {
        String customToken = "custom-access-token-xyz";
        UserEntity userEntity = createValidUserEntity();

        when(tokenProvider.isTokenValid()).thenReturn(true);
        when(tokenProvider.getAccessToken()).thenReturn(customToken);
        when(userProfilePort.getCurrentUsersProfileAsync(customToken)).thenReturn(userEntity);

        service.getCurrentUserProfileAsync();

        verify(userProfilePort).getCurrentUsersProfileAsync(customToken);
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should map all user entity fields to DTO")
    void getCurrentUserProfileAsync_MapsAllFields() {
        UserEntity userEntity = new UserEntity(
                "user-456",
                LocalDate.of(1995, 5, 15),
                "US",
                "John Doe",
                Email.of("john@example.com"),
                "https://open.spotify.com/user/john",
                500,
                "https://api.spotify.com/v1/users/john",
                "https://i.scdn.co/image/john",
                "spotify:user:john",
                "premium"
        );

        when(tokenProvider.isTokenValid()).thenReturn(true);
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(userProfilePort.getCurrentUsersProfileAsync(ACCESS_TOKEN)).thenReturn(userEntity);

        UserProfileDTO result = service.getCurrentUserProfileAsync();

        assertThat(result.birthdate()).isEqualTo(LocalDate.of(1995, 5, 15));
        assertThat(result.country()).isEqualTo("US");
        assertThat(result.displayName()).isEqualTo("John Doe");
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.externalUrls()).isEqualTo("https://open.spotify.com/user/john");
        assertThat(result.followersCount()).isEqualTo(500);
        assertThat(result.href()).isEqualTo("https://api.spotify.com/v1/users/john");
        assertThat(result.photoCover()).isEqualTo("https://i.scdn.co/image/john");
        assertThat(result.spotifyUri()).isEqualTo("spotify:user:john");
        assertThat(result.type()).isEqualTo("premium");
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should not call getAccessToken when token is invalid")
    void getCurrentUserProfileAsync_DoesNotCallGetAccessToken_WhenTokenIsInvalid() {
        when(tokenProvider.isTokenValid()).thenReturn(false);

        catchThrowable(() -> service.getCurrentUserProfileAsync());

        verify(tokenProvider, never()).getAccessToken();
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should not call userProfilePort when token is invalid")
    void getCurrentUserProfileAsync_DoesNotCallUserProfilePort_WhenTokenIsInvalid() {
        when(tokenProvider.isTokenValid()).thenReturn(false);

        catchThrowable(() -> service.getCurrentUserProfileAsync());

        verify(userProfilePort, never()).getCurrentUsersProfileAsync(any());
    }

    @Test
    @DisplayName("getCurrentUserProfileAsync should handle null values in user entity gracefully")
    void getCurrentUserProfileAsync_HandlesNullValuesGracefully() {
        UserEntity userEntity = new UserEntity(
                USER_ID,
                null,
                null,
                null,
                Email.of(VALID_EMAIL),
                null,
                0,
                null,
                null,
                null,
                null
        );

        when(tokenProvider.isTokenValid()).thenReturn(true);
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(userProfilePort.getCurrentUsersProfileAsync(ACCESS_TOKEN)).thenReturn(userEntity);

        UserProfileDTO result = service.getCurrentUserProfileAsync();

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo(VALID_EMAIL);
        assertThat(result.birthdate()).isNull();
        assertThat(result.country()).isNull();
        assertThat(result.displayName()).isNull();
        assertThat(result.externalUrls()).isNull();
        assertThat(result.followersCount()).isZero();
        assertThat(result.href()).isNull();
        assertThat(result.photoCover()).isNull();
        assertThat(result.spotifyUri()).isNull();
        assertThat(result.type()).isNull();
    }

    private UserEntity createValidUserEntity() {
        return new UserEntity(
                USER_ID,
                BIRTHDATE,
                "BR",
                "Test User",
                Email.of(VALID_EMAIL),
                "https://open.spotify.com/user/test",
                100,
                "https://api.spotify.com/v1/users/test",
                "https://i.scdn.co/image/test",
                "spotify:user:test",
                "user"
        );
    }
}
