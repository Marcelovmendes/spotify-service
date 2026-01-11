package com.example.spotify.playlist.domain.entity;

import java.time.Instant;
import java.util.Objects;

public class SavedTrack {
    private final Track track;
    private final Instant addedAt;

    public SavedTrack(Track track, Instant addedAt) {
        this.track = Objects.requireNonNull(track, "Track cannot be null");
        this.addedAt = Objects.requireNonNull(addedAt, "Added at timestamp cannot be null");
    }

    public Track getTrack() {
        return track;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedTrack that = (SavedTrack) o;
        return Objects.equals(track, that.track) && Objects.equals(addedAt, that.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(track, addedAt);
    }
}
