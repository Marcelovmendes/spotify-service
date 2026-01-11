package com.example.spotify.playlist.api.internal;

import com.example.spotify.common.exception.AuthenticationException;
import com.example.spotify.playlist.api.dto.PlaylistTracksResponse;
import com.example.spotify.playlist.domain.PlaylistPort;
import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/playlists")
public class InternalPlaylistController {

    private static final Logger log = LoggerFactory.getLogger(InternalPlaylistController.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final PlaylistPort playlistPort;

    public InternalPlaylistController(PlaylistPort playlistPort) {
        this.playlistPort = playlistPort;
    }

    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<PlaylistTracksResponse> getPlaylistTracks(
            @PathVariable String playlistId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit,
            @RequestHeader("Authorization") String authHeader
    ) {
        String accessToken = extractBearerToken(authHeader);
        log.info("Internal request - Playlist: {}, offset: {}, limit: {}", playlistId, offset, limit);

        PageResult<Track> pageResult = playlistPort.getPlaylistTracksAsync(accessToken, playlistId, offset, limit);

        return ResponseEntity.ok(PlaylistTracksResponse.fromPageResult(pageResult));
    }

    private String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("Missing or invalid Authorization header");
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}
