package com.example.spotify.conversion.api.dto;

import com.example.spotify.conversion.domain.entity.ConversionJobStatus;

public record ConversionStatusResponse(
        String jobId,
        String status,
        int progress,
        int totalTracks,
        int processedTracks,
        int matchedTracks,
        int failedTracks,
        Integer estimatedSecondsRemaining,
        String targetPlaylistUrl,
        String error
) {
    public static ConversionStatusResponse fromDomain(ConversionJobStatus status) {
        return new ConversionStatusResponse(
                status.jobId(),
                status.status().name(),
                status.progress(),
                status.totalTracks(),
                status.processedTracks(),
                status.matchedTracks(),
                status.failedTracks(),
                status.estimatedSecondsRemaining(),
                status.targetPlaylistUrl(),
                status.error()
        );
    }
}
