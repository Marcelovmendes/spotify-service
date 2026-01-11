package com.example.spotify.playlist.domain.entity;

import com.example.spotify.user.domain.entity.UserId;

import java.util.List;
import java.util.Objects;

public class Playlist {
    private final String id;
    private final String name;
    private final String ownerId;
    private final String ownerName;
    private final String description;
    private final boolean collaborative;
    private final boolean publicAccess;
    private final int trackCount;
    private final String imageUrl;
    private final List<Track> tracks;
    private final String externalUrl;

    public Playlist(String id, String name, String ownerId, String ownerName, String description, boolean collaborative, boolean publicAccess,
                    int trackCount, String imageUrl, List<Track> tracks, String externalUrl) {

        this.id = Objects.requireNonNull(id, "Playlist ID cannot be null");
        this.name = Objects.requireNonNull(name, "Playlist name cannot be null");
        this.ownerId = Objects.requireNonNull(ownerId, "Owner ID cannot be null");
        this.ownerName = ownerName;
        this.description = description;
        this.collaborative = collaborative;
        this.publicAccess = publicAccess;
        this.trackCount = trackCount;
        this.imageUrl = imageUrl;
        this.tracks = tracks == null ? List.of() : List.copyOf(tracks);
        this.externalUrl = externalUrl;
    }
    public Playlist withTracks(List<Track> newTracks) {
        return new Playlist(
                this.id,
                this.name,
                this.ownerId,
                this.ownerName,
                this.description,
                this.collaborative,
                this.publicAccess,
                newTracks.size(),
                this.imageUrl,
                newTracks,
                this.externalUrl);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public String getDescription() { return description; }
    public boolean isCollaborative() { return collaborative; }
    public boolean isPublicAccess() { return publicAccess; }
    public int getTrackCount() { return trackCount; }
    public String getImageUrl() { return imageUrl; }
    public List<Track> getTracks() { return tracks; }
    public String getExternalUrl() { return externalUrl; }
}
