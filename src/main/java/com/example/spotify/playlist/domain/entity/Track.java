package com.example.spotify.playlist.domain.entity;

import java.util.Objects;

public class Track {
    private final TrackId id;
    private final String name;
    private final String artist;
    private final String album;
    private final Integer durationMs;
    private final String externalUrl;
    private final String previewUrl;
    private final String imageUrl;

    public Track(TrackId id, String name, String artist, String album, Integer durationMs, String externalUrl, String previewUrl, String imageUrl) {
        this.id = Objects.requireNonNull(id, "Track ID cannot be null");
        this.name = Objects.requireNonNull(name, "Track name cannot be null");
        this.artist = artist;
        this.album = album;
        this.durationMs = durationMs;
        this.externalUrl = externalUrl;
        this.previewUrl = previewUrl;
        this.imageUrl = imageUrl;
    }

    public TrackId getId() { return id; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public Integer getDurationMs() { return durationMs; }
    public String getExternalUrl() { return externalUrl; }
    public String getPreviewUrl() { return previewUrl; }
    public String getImageUrl() { return imageUrl; }
}
