package com.example.spotify.conversion.domain.entity;

public final class ConversionJobStatus {

    private final String jobId;
    private final ConversionStatus status;
    private final int progress;
    private final int totalTracks;
    private final int processedTracks;
    private final int matchedTracks;
    private final int failedTracks;
    private final Integer estimatedSecondsRemaining;
    private final String targetPlaylistUrl;
    private final String error;

    private ConversionJobStatus(
            String jobId,
            ConversionStatus status,
            int progress,
            int totalTracks,
            int processedTracks,
            int matchedTracks,
            int failedTracks,
            Integer estimatedSecondsRemaining,
            String targetPlaylistUrl,
            String error
    ) {
        this.jobId = jobId;
        this.status = status;
        this.progress = progress;
        this.totalTracks = totalTracks;
        this.processedTracks = processedTracks;
        this.matchedTracks = matchedTracks;
        this.failedTracks = failedTracks;
        this.estimatedSecondsRemaining = estimatedSecondsRemaining;
        this.targetPlaylistUrl = targetPlaylistUrl;
        this.error = error;
    }

    public static ConversionJobStatus pending(String jobId) {
        return new ConversionJobStatus(
                jobId,
                ConversionStatus.PENDING,
                0,
                0,
                0,
                0,
                0,
                null,
                null,
                null
        );
    }

    public static ConversionJobStatus fromJson(
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
        return new ConversionJobStatus(
                jobId,
                ConversionStatus.valueOf(status),
                progress,
                totalTracks,
                processedTracks,
                matchedTracks,
                failedTracks,
                estimatedSecondsRemaining,
                targetPlaylistUrl,
                error
        );
    }

    public String jobId() {
        return jobId;
    }

    public ConversionStatus status() {
        return status;
    }

    public int progress() {
        return progress;
    }

    public int totalTracks() {
        return totalTracks;
    }

    public int processedTracks() {
        return processedTracks;
    }

    public int matchedTracks() {
        return matchedTracks;
    }

    public int failedTracks() {
        return failedTracks;
    }

    public Integer estimatedSecondsRemaining() {
        return estimatedSecondsRemaining;
    }

    public String targetPlaylistUrl() {
        return targetPlaylistUrl;
    }

    public String error() {
        return error;
    }

    public boolean isCompleted() {
        return status == ConversionStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ConversionStatus.FAILED;
    }
}
