package com.example.spotify.user.api.dto;

import java.time.LocalDate;

public record UserProfileDTO (
        LocalDate birthdate,
        String country,
        String displayName,
        String email,
        String externalUrls,
        Integer followersCount,
        String href,
        String photoCover,
        String spotifyUri,
        String type
) {

}
