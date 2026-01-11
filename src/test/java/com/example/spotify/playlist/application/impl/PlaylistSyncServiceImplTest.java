package com.example.spotify.playlist.application.impl;

import com.example.spotify.auth.infrastructure.TokenProvider;
import com.example.spotify.playlist.domain.PlaylistPort;
import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Playlist;
import com.example.spotify.playlist.domain.entity.SavedTrack;
import com.example.spotify.playlist.domain.entity.Track;
import com.example.spotify.playlist.domain.entity.TrackId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PlaylistSyncServiceImpl Unit Tests")
class PlaylistSyncServiceImplTest {

    @Mock
    private PlaylistPort playlistPort;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private PlaylistSyncServiceImpl service;

    private static final String ACCESS_TOKEN = "test-access-token";
    private static final String PLAYLIST_ID = "playlist123";

    @Test
    @DisplayName("getListOfCurrentUsersPlaylistsAsync should successfully return playlists")
    void getListOfCurrentUsersPlaylistsAsync_Success() {
        List<Playlist> expectedPlaylists = List.of(
                createPlaylist("1", "My Playlist 1"),
                createPlaylist("2", "My Playlist 2")
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN)).thenReturn(expectedPlaylists);

        List<Playlist> result = service.getListOfCurrentUsersPlaylistsAsync();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("My Playlist 1");
        assertThat(result.get(1).getName()).isEqualTo("My Playlist 2");

        verify(tokenProvider).getAccessToken();
        verify(playlistPort).getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getListOfCurrentUsersPlaylistsAsync should return empty list when no playlists exz'ist")
    void getListOfCurrentUsersPlaylistsAsync_ReturnsEmptyList() {
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN)).thenReturn(List.of());

        List<Playlist> result = service.getListOfCurrentUsersPlaylistsAsync();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(tokenProvider).getAccessToken();
        verify(playlistPort).getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getListOfCurrentUsersPlaylistsAsync should use correct access token")
    void getListOfCurrentUsersPlaylistsAsync_UsesCorrectAccessToken() {
        String customToken = "custom-access-token-xyz";
        List<Playlist> playlists = List.of(createPlaylist("1", "Test"));

        when(tokenProvider.getAccessToken()).thenReturn(customToken);
        when(playlistPort.getListOfCurrentUsersPlaylistsAsync(customToken)).thenReturn(playlists);

        service.getListOfCurrentUsersPlaylistsAsync();

        verify(playlistPort).getListOfCurrentUsersPlaylistsAsync(customToken);
    }

    @Test
    @DisplayName("getListOfCurrentUsersPlaylistsAsync should handle multiple playlists")
    void getListOfCurrentUsersPlaylistsAsync_HandlesMultiplePlaylists() {
        List<Playlist> expectedPlaylists = List.of(
                createPlaylist("1", "Workout Mix"),
                createPlaylist("2", "Chill Vibes"),
                createPlaylist("3", "Road Trip"),
                createPlaylist("4", "Study Focus"),
                createPlaylist("5", "Party Hits")
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN)).thenReturn(expectedPlaylists);

        List<Playlist> result = service.getListOfCurrentUsersPlaylistsAsync();

        assertThat(result).hasSize(5);
        assertThat(result).extracting(Playlist::getName)
                .containsExactly("Workout Mix", "Chill Vibes", "Road Trip", "Study Focus", "Party Hits");
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should successfully return tracks")
    void getPlaylistTracksAsync_Success() {
        List<Track> expectedTracks = List.of(
                createTrack("track1", "Song One"),
                createTrack("track2", "Song Two")
        );
        PageResult<Track> pageResult = new PageResult<>(expectedTracks, 2, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50)).thenReturn(pageResult);

        PageResult<Track> result = service.getPlaylistTracksAsync(PLAYLIST_ID, 0, 50);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Song One");
        assertThat(result.getItems().get(1).getName()).isEqualTo("Song Two");

        verify(tokenProvider).getAccessToken();
        verify(playlistPort).getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50);
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should return empty list when playlist has no tracks")
    void getPlaylistTracksAsync_ReturnsEmptyList() {
        PageResult<Track> pageResult = new PageResult<>(List.of(), 0, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50)).thenReturn(pageResult);

        PageResult<Track> result = service.getPlaylistTracksAsync(PLAYLIST_ID, 0, 50);

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();

        verify(tokenProvider).getAccessToken();
        verify(playlistPort).getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50);
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should use correct access token and playlist ID")
    void getPlaylistTracksAsync_UsesCorrectParameters() {
        String customToken = "custom-token-abc";
        String customPlaylistId = "custom-playlist-999";
        List<Track> tracks = List.of(createTrack("track1", "Test Track"));
        PageResult<Track> pageResult = new PageResult<>(tracks, 1, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(customToken);
        when(playlistPort.getPlaylistTracksAsync(customToken, customPlaylistId, 0, 50)).thenReturn(pageResult);

        service.getPlaylistTracksAsync(customPlaylistId, 0, 50);

        verify(playlistPort).getPlaylistTracksAsync(customToken, customPlaylistId, 0, 50);
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should handle multiple tracks")
    void getPlaylistTracksAsync_HandlesMultipleTracks() {
        List<Track> expectedTracks = List.of(
                createTrack("track1", "Song A"),
                createTrack("track2", "Song B"),
                createTrack("track3", "Song C"),
                createTrack("track4", "Song D")
        );
        PageResult<Track> pageResult = new PageResult<>(expectedTracks, 4, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50)).thenReturn(pageResult);

        PageResult<Track> result = service.getPlaylistTracksAsync(PLAYLIST_ID, 0, 50);

        assertThat(result.getItems()).hasSize(4);
        assertThat(result.getItems()).extracting(Track::getName)
                .containsExactly("Song A", "Song B", "Song C", "Song D");
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should pass playlist ID correctly")
    void getPlaylistTracksAsync_PassesPlaylistIdCorrectly() {
        String specificPlaylistId = "specific-playlist-id-789";
        List<Track> tracks = List.of(createTrack("track1", "Track"));
        PageResult<Track> pageResult = new PageResult<>(tracks, 1, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getPlaylistTracksAsync(ACCESS_TOKEN, specificPlaylistId, 0, 50)).thenReturn(pageResult);

        service.getPlaylistTracksAsync(specificPlaylistId, 0, 50);

        verify(playlistPort).getPlaylistTracksAsync(ACCESS_TOKEN, specificPlaylistId, 0, 50);
    }

    @Test
    @DisplayName("getListOfCurrentUsersPlaylistsAsync should call tokenProvider exactly once")
    void getListOfCurrentUsersPlaylistsAsync_CallsTokenProviderOnce() {
        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN)).thenReturn(List.of());

        service.getListOfCurrentUsersPlaylistsAsync();

        verify(tokenProvider, times(1)).getAccessToken();
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should call tokenProvider exactly once")
    void getPlaylistTracksAsync_CallsTokenProviderOnce() {
        PageResult<Track> pageResult = new PageResult<>(List.of(), 0, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50)).thenReturn(pageResult);

        service.getPlaylistTracksAsync(PLAYLIST_ID, 0, 50);

        verify(tokenProvider, times(1)).getAccessToken();
    }

    @Test
    @DisplayName("getListOfCurrentUsersPlaylistsAsync should preserve playlist order")
    void getListOfCurrentUsersPlaylistsAsync_PreservesOrder() {
        List<Playlist> orderedPlaylists = List.of(
                createPlaylist("1", "First"),
                createPlaylist("2", "Second"),
                createPlaylist("3", "Third")
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getListOfCurrentUsersPlaylistsAsync(ACCESS_TOKEN)).thenReturn(orderedPlaylists);

        List<Playlist> result = service.getListOfCurrentUsersPlaylistsAsync();

        assertThat(result).extracting(Playlist::getId)
                .containsExactly("1", "2", "3");
    }

    @Test
    @DisplayName("getPlaylistTracksAsync should preserve track order")
    void getPlaylistTracksAsync_PreservesOrder() {
        List<Track> orderedTracks = List.of(
                createTrack("track1", "First Track"),
                createTrack("track2", "Second Track"),
                createTrack("track3", "Third Track")
        );
        PageResult<Track> pageResult = new PageResult<>(orderedTracks, 3, 50, 0, null, null);

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getPlaylistTracksAsync(ACCESS_TOKEN, PLAYLIST_ID, 0, 50)).thenReturn(pageResult);

        PageResult<Track> result = service.getPlaylistTracksAsync(PLAYLIST_ID, 0, 50);

        assertThat(result.getItems()).extracting(Track::getName)
                .containsExactly("First Track", "Second Track", "Third Track");
    }

    private Playlist createPlaylist(String id, String name) {
        return new Playlist(
                id,
                name,
                "owner123",
                "Owner Name",
                "Test playlist description",
                false,
                true,
                0,
                "https://i.scdn.co/image/test",
                List.of(),
                "https://open.spotify.com/playlist/" + id
        );
    }

    private Track createTrack(String trackId, String name) {
        return new Track(
                TrackId.fromSpotifyId(trackId),
                name,
                "Test Artist",
                "Test Album",
                180000,
                "https://open.spotify.com/track/" + trackId,
                "https://p.scdn.co/mp3-preview/" + trackId,
                "https://i.scdn.co/image/" + trackId
        );
    }

    @Test
    @DisplayName("getCurrentUserSavedTracksAsync should successfully return saved tracks")
    void getCurrentUserSavedTracksAsync_Success() {
        List<SavedTrack> savedTracks = List.of(
                createSavedTrack("track1", "Saved Song 1", Instant.parse("2024-01-01T00:00:00Z")),
                createSavedTrack("track2", "Saved Song 2", Instant.parse("2024-01-02T00:00:00Z"))
        );
        PageResult<SavedTrack> expectedPageResult = new PageResult<>(
                savedTracks,
                100,
                50,
                0,
                "https://api.spotify.com/v1/next",
                null
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getCurrentUserSavedTracksAsync(ACCESS_TOKEN)).thenReturn(expectedPageResult);

        PageResult<SavedTrack> result = service.getCurrentUserSavedTracksAsync();

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(100);
        assertThat(result.getLimit()).isEqualTo(50);
        assertThat(result.getOffset()).isEqualTo(0);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isFalse();

        verify(tokenProvider).getAccessToken();
        verify(playlistPort).getCurrentUserSavedTracksAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getCurrentUserSavedTracksAsync should return empty page when no saved tracks exist")
    void getCurrentUserSavedTracksAsync_ReturnsEmptyPage() {
        PageResult<SavedTrack> emptyPageResult = new PageResult<>(
                List.of(),
                0,
                50,
                0,
                null,
                null
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getCurrentUserSavedTracksAsync(ACCESS_TOKEN)).thenReturn(emptyPageResult);

        PageResult<SavedTrack> result = service.getCurrentUserSavedTracksAsync();

        assertThat(result).isNotNull();
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isFalse();

        verify(tokenProvider).getAccessToken();
        verify(playlistPort).getCurrentUserSavedTracksAsync(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("getCurrentUserSavedTracksAsync should use correct access token")
    void getCurrentUserSavedTracksAsync_UsesCorrectAccessToken() {
        String customToken = "custom-token-xyz";
        PageResult<SavedTrack> pageResult = new PageResult<>(
                List.of(createSavedTrack("track1", "Song", Instant.now())),
                1,
                50,
                0,
                null,
                null
        );

        when(tokenProvider.getAccessToken()).thenReturn(customToken);
        when(playlistPort.getCurrentUserSavedTracksAsync(customToken)).thenReturn(pageResult);

        service.getCurrentUserSavedTracksAsync();

        verify(playlistPort).getCurrentUserSavedTracksAsync(customToken);
    }

    @Test
    @DisplayName("getCurrentUserSavedTracksAsync should handle pagination metadata correctly")
    void getCurrentUserSavedTracksAsync_HandlesPaginationMetadata() {
        PageResult<SavedTrack> pageResult = new PageResult<>(
                List.of(createSavedTrack("track1", "Song", Instant.now())),
                200,
                50,
                50,
                "https://api.spotify.com/v1/next",
                "https://api.spotify.com/v1/previous"
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getCurrentUserSavedTracksAsync(ACCESS_TOKEN)).thenReturn(pageResult);

        PageResult<SavedTrack> result = service.getCurrentUserSavedTracksAsync();

        assertThat(result.getTotal()).isEqualTo(200);
        assertThat(result.getLimit()).isEqualTo(50);
        assertThat(result.getOffset()).isEqualTo(50);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isTrue();
        assertThat(result.getNext()).isEqualTo("https://api.spotify.com/v1/next");
        assertThat(result.getPrevious()).isEqualTo("https://api.spotify.com/v1/previous");
    }

    @Test
    @DisplayName("getCurrentUserSavedTracksAsync should call tokenProvider exactly once")
    void getCurrentUserSavedTracksAsync_CallsTokenProviderOnce() {
        PageResult<SavedTrack> pageResult = new PageResult<>(
                List.of(),
                0,
                50,
                0,
                null,
                null
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getCurrentUserSavedTracksAsync(ACCESS_TOKEN)).thenReturn(pageResult);

        service.getCurrentUserSavedTracksAsync();

        verify(tokenProvider, times(1)).getAccessToken();
    }

    @Test
    @DisplayName("getCurrentUserSavedTracksAsync should preserve saved tracks order")
    void getCurrentUserSavedTracksAsync_PreservesOrder() {
        List<SavedTrack> orderedSavedTracks = List.of(
                createSavedTrack("track1", "First Song", Instant.parse("2024-01-01T00:00:00Z")),
                createSavedTrack("track2", "Second Song", Instant.parse("2024-01-02T00:00:00Z")),
                createSavedTrack("track3", "Third Song", Instant.parse("2024-01-03T00:00:00Z"))
        );
        PageResult<SavedTrack> pageResult = new PageResult<>(
                orderedSavedTracks,
                3,
                50,
                0,
                null,
                null
        );

        when(tokenProvider.getAccessToken()).thenReturn(ACCESS_TOKEN);
        when(playlistPort.getCurrentUserSavedTracksAsync(ACCESS_TOKEN)).thenReturn(pageResult);

        PageResult<SavedTrack> result = service.getCurrentUserSavedTracksAsync();

        assertThat(result.getItems()).extracting(st -> st.getTrack().getName())
                .containsExactly("First Song", "Second Song", "Third Song");
    }

    private SavedTrack createSavedTrack(String trackId, String name, Instant addedAt) {
        Track track = createTrack(trackId, name);
        return new SavedTrack(track, addedAt);
    }
}
