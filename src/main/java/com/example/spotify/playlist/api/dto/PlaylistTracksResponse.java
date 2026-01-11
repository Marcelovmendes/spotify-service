package com.example.spotify.playlist.api.dto;

import com.example.spotify.playlist.domain.entity.PageResult;
import com.example.spotify.playlist.domain.entity.Track;

import java.util.List;

public class PlaylistTracksResponse {
    private final List<TrackResponse> items;
    private final int total;
    private final int limit;
    private final int offset;
    private final boolean hasNext;

    public PlaylistTracksResponse(List<TrackResponse> items, int total, int limit, int offset) {
        this.items = items;
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.hasNext = (offset + limit) < total;
    }

    public static PlaylistTracksResponse fromPageResult(PageResult<Track> pageResult) {
        List<TrackResponse> trackResponses = pageResult.getItems().stream()
            .map(TrackResponse::fromDomain)
            .toList();

        return new PlaylistTracksResponse(
            trackResponses,
            pageResult.getTotal(),
            pageResult.getLimit(),
            pageResult.getOffset()
        );
    }

    public List<TrackResponse> getItems() { return items; }
    public int getTotal() { return total; }
    public int getLimit() { return limit; }
    public int getOffset() { return offset; }
    public boolean isHasNext() { return hasNext; }
}
