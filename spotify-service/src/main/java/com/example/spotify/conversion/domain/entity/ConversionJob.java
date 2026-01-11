package com.example.spotify.conversion.domain.entity;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ConversionJob {

    private final String jobId;
    private final String userId;
    private final Platform sourcePlatform;
    private final Platform targetPlatform;
    private final String sourcePlaylistId;
    private final List<String> selectedTrackIds;
    private final String targetPlaylistName;
    private final Instant createdAt;

    private ConversionJob(
            String jobId,
            String userId,
            Platform sourcePlatform,
            Platform targetPlatform,
            String sourcePlaylistId,
            List<String> selectedTrackIds,
            String targetPlaylistName,
            Instant createdAt
    ) {
        this.jobId = jobId;
        this.userId = userId;
        this.sourcePlatform = sourcePlatform;
        this.targetPlatform = targetPlatform;
        this.sourcePlaylistId = sourcePlaylistId;
        this.selectedTrackIds = selectedTrackIds;
        this.targetPlaylistName = targetPlaylistName;
        this.createdAt = createdAt;
    }

    public static ConversionJob create(
            String userId,
            String sourcePlaylistId,
            Platform targetPlatform,
            String targetPlaylistName,
            List<String> selectedTrackIds
    ) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(sourcePlaylistId, "sourcePlaylistId cannot be null");
        Objects.requireNonNull(targetPlatform, "targetPlatform cannot be null");
        Objects.requireNonNull(targetPlaylistName, "targetPlaylistName cannot be null");

        return new ConversionJob(
                UUID.randomUUID().toString(),
                userId,
                Platform.SPOTIFY,
                targetPlatform,
                sourcePlaylistId,
                selectedTrackIds != null ? List.copyOf(selectedTrackIds) : List.of(),
                targetPlaylistName,
                Instant.now()
        );
    }

    public String jobId() {
        return jobId;
    }

    public String userId() {
        return userId;
    }

    public Platform sourcePlatform() {
        return sourcePlatform;
    }

    public Platform targetPlatform() {
        return targetPlatform;
    }

    public String sourcePlaylistId() {
        return sourcePlaylistId;
    }

    public List<String> selectedTrackIds() {
        return selectedTrackIds;
    }

    public String targetPlaylistName() {
        return targetPlaylistName;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
