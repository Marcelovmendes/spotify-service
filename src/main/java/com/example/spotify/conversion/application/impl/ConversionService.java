package com.example.spotify.conversion.application.impl;

import com.example.spotify.conversion.application.ConversionUseCase;
import com.example.spotify.conversion.domain.ConversionException;
import com.example.spotify.conversion.domain.entity.ConversionJob;
import com.example.spotify.conversion.domain.entity.ConversionJobStatus;
import com.example.spotify.conversion.domain.entity.ConversionStatus;
import com.example.spotify.conversion.infrastructure.adapter.RedisConversionAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConversionService implements ConversionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConversionService.class);

    private final RedisConversionAdapter redisAdapter;

    public ConversionService(RedisConversionAdapter redisAdapter) {
        this.redisAdapter = redisAdapter;
    }

    @Override
    public ConversionCreated createConversion(String sessionId, CreateConversionCommand command) {
        log.info("Creating conversion job for playlist: {}", command.sourcePlaylistId());

        ConversionJob job = ConversionJob.create(
                sessionId,
                command.sourcePlaylistId(),
                command.targetPlatform(),
                command.targetPlaylistName(),
                command.selectedTrackIds()
        );

        redisAdapter.enqueueJob(job);

        log.info("Conversion job created: {}", job.jobId());

        return new ConversionCreated(job.jobId(), ConversionStatus.PENDING.name());
    }

    @Override
    public ConversionJobStatus getConversionStatus(String jobId) {
        log.info("Getting status for job: {}", jobId);

        return redisAdapter.getJobStatus(jobId)
                .orElseThrow(() -> ConversionException.jobNotFound(jobId));
    }
}
