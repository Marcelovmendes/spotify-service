package com.example.spotify.playlist.infrastructure.adapter;

import com.example.spotify.common.exception.SpotifyApiExceptionTranslator;
import com.example.spotify.common.infrastructure.adapter.ExternalServiceAdapter;
import com.example.spotify.playlist.domain.PlaylistPort;
import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Playlist;
import com.example.spotify.playlist.domain.entity.SavedTrack;
import com.example.spotify.playlist.domain.entity.Track;
import com.example.spotify.playlist.domain.entity.TrackId;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.requests.data.playlists.GetListOfCurrentUsersPlaylistsRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SpotifyPlaylistAdapter extends ExternalServiceAdapter implements PlaylistPort {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SpotifyPlaylistAdapter.class);
    private static final String[] ids = new String[]{"7sTyAjxDXq9afwfSQy6D0s"};


    public SpotifyPlaylistAdapter(SpotifyApi spotifyApi, SpotifyApiExceptionTranslator exceptionTranslator) {
        super(spotifyApi, exceptionTranslator);
    }
    @Override
    public Paging<PlaylistSimplified> getListOfCurrentUsersPlaylistsSync(String accessToken){

        spotifyApi.setAccessToken(accessToken);
        GetListOfCurrentUsersPlaylistsRequest request = spotifyApi.getListOfCurrentUsersPlaylists()
                .limit(10)
                .build();

            return executeSync(request::execute,
                    "fetching user playlists"
            );


    }

    @Override
    public List<Playlist> getListOfCurrentUsersPlaylistsAsync(String accessToken) {
        spotifyApi.setAccessToken(accessToken);

        Paging<PlaylistSimplified> spotifyPlaylists = executeAsync(
                spotifyApi.getListOfCurrentUsersPlaylists()
                        .limit(10)
                        .build()
                        .executeAsync(),
                "fetching user spotifyPlaylists"
        );
        List<Playlist> playlists = new ArrayList<>();
        for ( PlaylistSimplified playlist : spotifyPlaylists.getItems()) {
            log.info("Playlist name: {}, ID: {}", playlist.getName(), playlist.getId());
            playlists.add(convertToPlaylist(playlist));
        }
        return playlists;

    }

    @Override
    public se.michaelthelin.spotify.model_objects.specification.Track[] getSeveralTracksAsync() {
        return new se.michaelthelin.spotify.model_objects.specification.Track[0];
    }


    @Override
    public PageResult<Track> getPlaylistTracksAsync(String accessToken, String playlistId, int offset, int limit) {
        spotifyApi.setAccessToken(accessToken);
        Paging<PlaylistTrack> spotifyTracks =  executeAsync(
               spotifyApi.getPlaylistsItems(playlistId)
                       .offset(offset)
                       .limit(limit)
                       .build()
                       .executeAsync(),
                "fetching playlist tracks"
       );

        return convertPlaylistTracksToPageResult(spotifyTracks);

    }

    @Override
    public PageResult<SavedTrack> getCurrentUserSavedTracksAsync(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        Paging<se.michaelthelin.spotify.model_objects.specification.SavedTrack> spotifySavedTracks = executeAsync(
                spotifyApi.getUsersSavedTracks()
                        .limit(50)
                        .offset(0)
                        .build()
                        .executeAsync(),
                "fetching current user saved tracks"
        );

        return convertToPageResult(spotifySavedTracks);
    }

    private Playlist convertToPlaylist(PlaylistSimplified spotifyPlaylist) {
        String imageUrl = null;
        if (spotifyPlaylist.getImages() != null && spotifyPlaylist.getImages().length > 0) {
            imageUrl = spotifyPlaylist.getImages()[0].getUrl();
        }

        return new Playlist(
              spotifyPlaylist.getId(),
                spotifyPlaylist.getName(),
                spotifyPlaylist.getOwner().getId(),
                spotifyPlaylist.getOwner().getDisplayName(),
                "spotifyPlaylist",
                spotifyPlaylist.getIsCollaborative(),
                spotifyPlaylist.getIsPublicAccess(),
                spotifyPlaylist.getTracks().getTotal(),
                imageUrl,
                List.of(),
                spotifyPlaylist.getExternalUrls().get("spotify"));
    }
    private List<Track> convertPlaylistTracks(Paging<PlaylistTrack> playlistTracks) {
        List<Track> tracks = new ArrayList<>();

        for (PlaylistTrack playlistTrack : playlistTracks.getItems()) {
            if (playlistTrack.getTrack() instanceof se.michaelthelin.spotify.model_objects.specification.Track) {
                tracks.add(convertTrack((se.michaelthelin.spotify.model_objects.specification.Track) playlistTrack.getTrack()));
            }
        }

        return tracks;
    }

    private PageResult<Track> convertPlaylistTracksToPageResult(Paging<PlaylistTrack> playlistTracks) {
        List<Track> tracks = convertPlaylistTracks(playlistTracks);

        return new PageResult<>(
                tracks,
                playlistTracks.getTotal(),
                playlistTracks.getLimit(),
                playlistTracks.getOffset(),
                playlistTracks.getNext(),
                playlistTracks.getPrevious()
        );
    }
    private Track convertTrack(se.michaelthelin.spotify.model_objects.specification.Track spotifyTrack) {
        String imageUrl = null;
        if (spotifyTrack.getAlbum() != null && spotifyTrack.getAlbum().getImages() != null
                && spotifyTrack.getAlbum().getImages().length > 0) {
            imageUrl = spotifyTrack.getAlbum().getImages()[0].getUrl();
        }

        String artist = "";
        if (spotifyTrack.getArtists() != null && spotifyTrack.getArtists().length > 0) {
            artist = spotifyTrack.getArtists()[0].getName();
        }

        String album = spotifyTrack.getAlbum() != null ? spotifyTrack.getAlbum().getName() : "";

        return new Track(
                TrackId.fromSpotifyId(spotifyTrack.getId()),
                spotifyTrack.getName(),
                artist,
                album,
                spotifyTrack.getDurationMs(),
                spotifyTrack.getExternalUrls().get("spotify"),
                spotifyTrack.getPreviewUrl(),
                imageUrl
        );
    }
    private Playlist convertToPlaylistWithTracks(se.michaelthelin.spotify.model_objects.specification.Playlist spotifyPlaylist) {
        String imageUrl = null;
        if (spotifyPlaylist.getImages() != null && spotifyPlaylist.getImages().length > 0) {
            imageUrl = spotifyPlaylist.getImages()[0].getUrl();
        }

        List<Track> tracks = new ArrayList<>();
        if (spotifyPlaylist.getTracks() != null && spotifyPlaylist.getTracks().getItems() != null) {
            for (PlaylistTrack playlistTrack : spotifyPlaylist.getTracks().getItems()) {
                if (playlistTrack.getTrack() instanceof se.michaelthelin.spotify.model_objects.specification.Track) {
                    tracks.add(convertTrack((se.michaelthelin.spotify.model_objects.specification.Track) playlistTrack.getTrack()));
                }
            }
        }

        return new Playlist(
                spotifyPlaylist.getId(),
                spotifyPlaylist.getName(),
                spotifyPlaylist.getOwner().getId(),
                spotifyPlaylist.getOwner().getDisplayName(),
                spotifyPlaylist.getDescription(),
                spotifyPlaylist.getIsCollaborative(),
                spotifyPlaylist.getIsPublicAccess(),
                spotifyPlaylist.getTracks().getTotal(),
                imageUrl,
                tracks,
                spotifyPlaylist.getExternalUrls().get("spotify"));
    }

    private PageResult<SavedTrack> convertToPageResult(Paging<se.michaelthelin.spotify.model_objects.specification.SavedTrack> spotifyPaging) {
        List<SavedTrack> savedTracks = new ArrayList<>();

        for (se.michaelthelin.spotify.model_objects.specification.SavedTrack spotifySavedTrack : spotifyPaging.getItems()) {
            savedTracks.add(convertSavedTrack(spotifySavedTrack));
        }

        return new PageResult<>(
                savedTracks,
                spotifyPaging.getTotal(),
                spotifyPaging.getLimit(),
                spotifyPaging.getOffset(),
                spotifyPaging.getNext(),
                spotifyPaging.getPrevious()
        );
    }

    private SavedTrack convertSavedTrack(se.michaelthelin.spotify.model_objects.specification.SavedTrack spotifySavedTrack) {
        se.michaelthelin.spotify.model_objects.specification.Track spotifyTrack = spotifySavedTrack.getTrack();
        Track track = convertTrack(spotifyTrack);

        Date addedAtDate = spotifySavedTrack.getAddedAt();
        Instant addedAt = addedAtDate != null ? addedAtDate.toInstant() : Instant.now();

        return new SavedTrack(track, addedAt);
    }

}
