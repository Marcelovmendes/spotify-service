package com.example.spotify.user.infrastructure.adapter;

import com.example.spotify.common.exception.*;
import com.example.spotify.common.infrastructure.adapter.ExternalServiceAdapter;
import com.example.spotify.user.domain.UserProfilePort;
import com.example.spotify.user.domain.entity.Email;
import com.example.spotify.user.domain.entity.UserEntity;
import com.example.spotify.user.domain.entity.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class SpotifyUserAdapter extends ExternalServiceAdapter implements UserProfilePort {

    private final static Logger log = LoggerFactory.getLogger(SpotifyUserAdapter.class);

    public SpotifyUserAdapter(SpotifyApi spotifyApi, SpotifyApiExceptionTranslator exceptionTranslator) {
        super(spotifyApi, exceptionTranslator);
    }

    @Override
    public User getCurrentUsersProfileSync(String tokenAccess) {
        spotifyApi.setAccessToken(tokenAccess);
        GetCurrentUsersProfileRequest request = spotifyApi.getCurrentUsersProfile().build();

            return  executeSync(request::execute,
                    "fetching user profile"
            );

    }

    @Override
    public UserEntity getCurrentUsersProfileAsync(String tokenAccess) {
        spotifyApi.setAccessToken(tokenAccess);
        GetCurrentUsersProfileRequest request = spotifyApi.getCurrentUsersProfile().build();

        User user = executeAsync(
                request.executeAsync(),
                "fetching user profile"
        );

        return convertUserToUserEntity(user);
    }


    private UserEntity convertUserToUserEntity(User spotifyUser) {
        LocalDate birthdate = null;
        if (spotifyUser.getBirthdate() != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                birthdate = LocalDate.parse(spotifyUser.getBirthdate(), formatter);
            } catch (Exception e) {
                log.warn("Data de nascimento não pôde ser parseada: {}", spotifyUser.getBirthdate());
            }
        }
        String photoUrl = null;
        if (spotifyUser.getImages() != null && spotifyUser.getImages().length > 0) {
            photoUrl = spotifyUser.getImages()[0].getUrl();
        }

        return new UserEntity(
                spotifyUser.getId(),
                birthdate,
                spotifyUser.getCountry() != null ? spotifyUser.getCountry().getAlpha3() : null,
                spotifyUser.getDisplayName(),
                Email.of(spotifyUser.getEmail()),
                spotifyUser.getExternalUrls().get("spotify"),
                spotifyUser.getFollowers() != null ? spotifyUser.getFollowers().getTotal() : 0,
                spotifyUser.getHref(),
                photoUrl,
                spotifyUser.getUri(),
                spotifyUser.getType().getType()
        );

    }

}