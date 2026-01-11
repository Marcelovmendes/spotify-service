package com.example.spotify.user.domain.entity;

import reactor.util.annotation.NonNull;

import java.util.UUID;

public record UserId(String value, UUID internalId) {

    public static UserId fromSpotifyId(String spotifyId) {
        if (spotifyId == null || spotifyId.isBlank()) {
            throw new IllegalArgumentException("Spotify User ID cannot be blank");
        }
        UUID internalId = UUID.nameUUIDFromBytes(("spotify:" + spotifyId).getBytes());
        return new UserId(spotifyId, internalId);
    }

    public static UserId fromInternalId(UUID internalId) {
        if (internalId == null) {
            throw new IllegalArgumentException("Internal ID cannot be null");
        }
        return new UserId(internalId.toString(), internalId);
    }

    public static UserId reconstitute(String spotifyId, UUID internalId) {
        return new UserId(spotifyId, internalId);
    }

    public String spotifyId() {
        return value;
    }

    public UUID getInternalId() {
        return internalId;
    }
}
