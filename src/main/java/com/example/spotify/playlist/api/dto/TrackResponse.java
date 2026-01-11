package com.example.spotify.playlist.api.dto;

import com.example.spotify.playlist.domain.entity.Track;

public class TrackResponse {
    private final String id;
    private final String name;
    private final String artist;
    private final String album;
    private final Integer durationMs;
    private final String externalUrl;
    private final String previewUrl;
    private final String imageUrl;

    public TrackResponse(String id, String name, String artist, String album, Integer durationMs,
                        String externalUrl, String previewUrl, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.durationMs = durationMs;
        this.externalUrl = externalUrl;
        this.previewUrl = previewUrl;
        this.imageUrl = imageUrl;
    }

    public static TrackResponse fromDomain(Track track) {
        return new TrackResponse(
            track.getId().spotifyId(),
            track.getName(),
            track.getArtist(),
            track.getAlbum(),
            track.getDurationMs(),
            track.getExternalUrl(),
            track.getPreviewUrl(),
            track.getImageUrl()
        );
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getAlbum() { return album; }
    public Integer getDurationMs() { return durationMs; }
    public String getExternalUrl() { return externalUrl; }
    public String getPreviewUrl() { return previewUrl; }
    public String getImageUrl() { return imageUrl; }
}
