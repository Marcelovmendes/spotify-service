package com.example.spotify.conversion.application;

import com.example.spotify.conversion.domain.entity.ConversionJobStatus;
import com.example.spotify.conversion.domain.entity.Platform;

import java.util.List;

public interface ConversionUseCase {

    record CreateConversionCommand(
            String sourcePlaylistId,
            Platform targetPlatform,
            String targetPlaylistName,
            List<String> selectedTrackIds
    ) {}

    record ConversionCreated(String jobId, String status) {}

    ConversionCreated createConversion(String sessionId, CreateConversionCommand command);

    ConversionJobStatus getConversionStatus(String jobId);
}
