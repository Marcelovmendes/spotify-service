package com.example.spotify.playlist.application.impl;


import com.example.spotify.auth.infrastructure.TokenProvider;
import com.example.spotify.playlist.application.PlaylistsService;
import com.example.spotify.playlist.domain.PlaylistPort;
import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Playlist;
import com.example.spotify.playlist.domain.entity.SavedTrack;
import com.example.spotify.playlist.domain.entity.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaylistSyncServiceImpl implements PlaylistsService {

    private static final Logger log = LoggerFactory.getLogger(PlaylistSyncServiceImpl.class);
    private final PlaylistPort playlistPort;
    private final TokenProvider tokenProvider;


    public PlaylistSyncServiceImpl(PlaylistPort playlist, TokenProvider tokenProvider) {
        this.playlistPort = playlist;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<Playlist> getListOfCurrentUsersPlaylistsAsync() {
        String accessToken = tokenProvider.getAccessToken();

        return playlistPort.getListOfCurrentUsersPlaylistsAsync(accessToken);
    }

    @Override
    public PageResult<Track> getPlaylistTracksAsync(String playlistId, int offset, int limit) {
        String accessToken = tokenProvider.getAccessToken();
        log.info("access token para pegar playlist: {}", accessToken);
        return playlistPort.getPlaylistTracksAsync(accessToken, playlistId, offset, limit);
    }

    @Override
    public PageResult<SavedTrack> getCurrentUserSavedTracksAsync() {
        String accessToken = tokenProvider.getAccessToken();
        return playlistPort.getCurrentUserSavedTracksAsync(accessToken);
    }

}
