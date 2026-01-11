package com.example.spotify.playlist.domain;


import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Playlist;
import com.example.spotify.playlist.domain.entity.SavedTrack;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import com.example.spotify.playlist.domain.entity.Track;

import java.util.List;

public interface PlaylistPort {
    Paging<PlaylistSimplified> getListOfCurrentUsersPlaylistsSync(String accessToken);
    List<Playlist> getListOfCurrentUsersPlaylistsAsync(String accessToken);
    se.michaelthelin.spotify.model_objects.specification.Track[] getSeveralTracksAsync();
    PageResult<Track> getPlaylistTracksAsync(String accessToken, String playlistId, int offset, int limit);
    PageResult<SavedTrack> getCurrentUserSavedTracksAsync(String accessToken);
}
