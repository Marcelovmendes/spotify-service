package com.example.spotify.playlist.application;

import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Playlist;
import com.example.spotify.playlist.domain.entity.PlaylistAggregate;
import com.example.spotify.playlist.domain.entity.SavedTrack;
import com.example.spotify.playlist.domain.entity.Track;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PlaylistsService {
    List<Playlist> getListOfCurrentUsersPlaylistsAsync();
    PageResult<Track> getPlaylistTracksAsync(String playlistId, int offset, int limit);
    PageResult<SavedTrack> getCurrentUserSavedTracksAsync();
}
