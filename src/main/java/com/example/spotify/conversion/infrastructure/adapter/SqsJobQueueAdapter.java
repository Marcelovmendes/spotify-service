package com.example.spotify.conversion.infrastructure.adapter;

import com.example.spotify.conversion.application.port.JobQueuePort;
import com.example.spotify.conversion.domain.ConversionException;
import com.example.spotify.conversion.domain.entity.ConversionJob;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SqsJobQueueAdapter implements JobQueuePort {

    private static final Logger log = LoggerFactory.getLogger(SqsJobQueueAdapter.class);

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SqsJobQueueAdapter(SqsClient sqsClient, ObjectMapper objectMapper,
            @Value("${aws.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    @Override
    public void enqueue(ConversionJob job) {
        try {
            String body = serializeJob(job);
            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .build());
            log.info("Job enqueued: {}", job.jobId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize job: {}", job.jobId(), e);
            throw ConversionException.queueError("Serialization failed", e);
        } catch (Exception e) {
            log.error("Failed to enqueue job: {}", job.jobId(), e);
            throw ConversionException.queueError(e.getMessage(), e);
        }
    }

    private String serializeJob(ConversionJob job) throws JsonProcessingException {
        var jobNode = objectMapper.createObjectNode()
                .put("jobId", job.jobId())
                .put("userId", job.userId())
                .put("sourcePlatform", job.sourcePlatform().name())
                .put("targetPlatform", job.targetPlatform().name())
                .put("sourcePlaylistId", job.sourcePlaylistId())
                .put("targetPlaylistName", job.targetPlaylistName())
                .put("createdAt", job.createdAt().toString());

        var trackIdsArray = jobNode.putArray("selectedTrackIds");
        job.selectedTrackIds().forEach(trackIdsArray::add);

        return objectMapper.writeValueAsString(jobNode);
    }
}
