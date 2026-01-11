package com.example.spotify.user.domain.entity;

import java.time.LocalDate;
import java.util.Objects;



public final class UserEntity {
    private final String id;
    private final LocalDate birthdate;
    private final String country;
    private final String displayName;
    private final Email email;
    private final String externalUrls;
    private final int followersCount;
    private final String href;
    private final String photoCover;
    private final String spotifyUri;
    private final String type;

    public UserEntity(String id, LocalDate birthdate, String country, String displayName, Email email, String externalUrls,
            int followersCount, String href, String photoCover, String spotifyUri, String type) {

        this.id = Objects.requireNonNull(id, "ID não pode ser nulo");
        this.email = Objects.requireNonNull(email, "Email não pode ser nulo");
        this.birthdate = birthdate;
        this.country = country;
        this.displayName = displayName;
        this.externalUrls = externalUrls;
        this.followersCount = followersCount;
        this.href = href;
        this.photoCover = photoCover;
        this.spotifyUri = spotifyUri;
        this.type = type;

    }
    public String getId() { return id; }
    public LocalDate getBirthdate() { return birthdate; }
    public String getCountry() { return country; }
    public String getDisplayName() { return displayName; }
    public Email getEmail() { return email; }
    public String getEmailAddress() { return email.value(); }
    public String getExternalUrls() { return externalUrls; }
    public int getFollowersCount() { return followersCount; }
    public String getHref() { return href; }
    public String getPhotoCover() { return photoCover; }
    public String getSpotifyUri() { return spotifyUri; }
    public String getType() { return type; }
    
}
