package com.example.spotify.playlist.domain.entity;

import com.example.spotify.user.domain.entity.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistAggregate {

    private final Playlist playlist;
    private final List<Track> tracks;

    public PlaylistAggregate(Playlist playlist, List<Track> tracks) {
        this.playlist = Objects.requireNonNull(playlist, "Playlist cannot be null");
        this.tracks = new ArrayList<>(tracks != null ? tracks : List.of());
        boolean conversionRequested = false;
        String targetPlaylistForm = null;
    }

    public static PlaylistAggregate create(String id, String name, String ownerId, String ownerName, String description,
                                            boolean collaborative, boolean publicAccess, int trackCount, String imageUrl,
                                            List<Track> tracks, String externalUrl) {

        Playlist playlist = new Playlist(id, name, ownerId,ownerName, description, collaborative, publicAccess,
                trackCount, imageUrl, tracks, externalUrl);

        return new PlaylistAggregate(playlist, tracks);
    }
    public Boolean exists(String id) {
        return id != null && !id.isEmpty();
    }


    public boolean isEmpty() { return tracks.isEmpty(); }
    public Playlist getPlaylist() { return playlist; }
    public List<Track> getTracks() { return List.copyOf(tracks);}
    public String getId() { return playlist.getId(); }
    public String getName() { return playlist.getName(); }
    public String getOwnerId() { return playlist.getOwnerId(); }
    public Integer getTrackCount() { return playlist.getTrackCount(); }

}
