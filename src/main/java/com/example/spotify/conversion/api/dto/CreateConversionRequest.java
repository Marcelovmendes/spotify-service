package com.example.spotify.conversion.api.dto;

import java.util.List;

public record CreateConversionRequest(
        String sourcePlaylistId,
        String targetPlatform,
        String targetPlaylistName,
        List<String> selectedTrackIds
) {}
