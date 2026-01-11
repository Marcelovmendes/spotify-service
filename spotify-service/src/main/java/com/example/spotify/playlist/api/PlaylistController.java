package com.example.spotify.playlist.api;

import com.example.spotify.playlist.api.dto.PlaylistTracksResponse;
import com.example.spotify.playlist.application.PlaylistsService;
import com.example.spotify.playlist.domain.PlaylistPort;
import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Playlist;
import com.example.spotify.playlist.domain.entity.PlaylistAggregate;
import com.example.spotify.playlist.domain.entity.SavedTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private static final Logger log = LoggerFactory.getLogger(PlaylistController.class);
    private final PlaylistsService playlistsService;
    private final PlaylistPort playlistPort;

    public PlaylistController(PlaylistsService playlistsService, PlaylistPort playlistPort) {
        this.playlistsService = playlistsService;
        this.playlistPort = playlistPort;
    }

     @GetMapping("/")
    public ResponseEntity<List<Playlist>> getPlaylists() {
       List<Playlist> playLists = playlistsService.getListOfCurrentUsersPlaylistsAsync();

        return ResponseEntity.ok(playLists);
    }

    @GetMapping("/tracks")
    public ResponseEntity<Track[]> getTracks () {
        Track[] tracks = playlistPort.getSeveralTracksAsync();

        return ResponseEntity.ok(tracks);
    }
    @GetMapping("/{playlistId}/tracks")
    public ResponseEntity<PlaylistTracksResponse> getPlaylistTracks(
            @PathVariable String playlistId,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "50") int limit) {
        PageResult<com.example.spotify.playlist.domain.entity.Track> pageResult =
            playlistsService.getPlaylistTracksAsync(playlistId, offset, limit);
        return ResponseEntity.ok(PlaylistTracksResponse.fromPageResult(pageResult));
    }

    @GetMapping("/saved-tracks")
    public ResponseEntity<PageResult<SavedTrack>> getCurrentUserSavedTracks() {
        PageResult<SavedTrack> savedTracks = playlistsService.getCurrentUserSavedTracksAsync();
        return ResponseEntity.ok(savedTracks);
    }


}
