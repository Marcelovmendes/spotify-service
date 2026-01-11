package com.example.spotify.playlist.domain.entity;

import reactor.util.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

public record TrackId(String value, UUID internalId) {
    public static TrackId fromSpotifyId(String spotifyId) {
        Objects.requireNonNull(spotifyId, "Spotify Track ID cannot be null");
        if (spotifyId.isBlank()) {
            throw new IllegalArgumentException("Spotify Track ID cannot be blank");
        }
        UUID internalId = UUID.nameUUIDFromBytes(("spotify:" + spotifyId).getBytes());
        return new TrackId(spotifyId, internalId);
    }

    public static TrackId fromInternalId(UUID internalId) {
        Objects.requireNonNull(internalId, "Internal ID cannot be null");
        return new TrackId(internalId.toString(), internalId);
    }

    public static TrackId reconstitute(String spotifyId, UUID internalId) {
        return new TrackId(spotifyId, internalId);
    }

    public String spotifyId() {
        return value;
    }

    @Override
    @NonNull
    public String toString() {
        return value;
    }
}
