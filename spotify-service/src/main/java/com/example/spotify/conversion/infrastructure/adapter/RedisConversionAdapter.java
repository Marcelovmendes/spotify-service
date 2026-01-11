package com.example.spotify.conversion.infrastructure.adapter;

import com.example.spotify.conversion.domain.ConversionException;
import com.example.spotify.conversion.domain.entity.ConversionJob;
import com.example.spotify.conversion.domain.entity.ConversionJobStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RedisConversionAdapter {

    private static final Logger log = LoggerFactory.getLogger(RedisConversionAdapter.class);
    private static final String JOBS_QUEUE_KEY = "conversion:jobs";
    private static final String STATUS_KEY_PREFIX = "conversion:";
    private static final String STATUS_KEY_SUFFIX = ":status";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisConversionAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void enqueueJob(ConversionJob job) {
        try {
            String jobJson = serializeJob(job);
            redisTemplate.opsForList().leftPush(JOBS_QUEUE_KEY, jobJson);
            log.info("Job enqueued: {}", job.jobId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize job: {}", job.jobId(), e);
            throw ConversionException.queueError("Serialization failed", e);
        } catch (Exception e) {
            log.error("Failed to enqueue job: {}", job.jobId(), e);
            throw ConversionException.queueError(e.getMessage(), e);
        }
    }

    public Optional<ConversionJobStatus> getJobStatus(String jobId) {
        try {
            String statusKey = STATUS_KEY_PREFIX + jobId + STATUS_KEY_SUFFIX;
            String statusJson = redisTemplate.opsForValue().get(statusKey);

            if (statusJson == null) {
                return Optional.empty();
            }

            return Optional.of(deserializeStatus(jobId, statusJson));
        } catch (Exception e) {
            log.error("Failed to fetch status for job: {}", jobId, e);
            throw ConversionException.statusFetchError(jobId, e);
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

    private ConversionJobStatus deserializeStatus(String jobId, String json) throws JsonProcessingException {
        JsonNode node = objectMapper.readTree(json);

        return ConversionJobStatus.fromJson(
                jobId,
                node.path("status").asText("PENDING"),
                node.path("progress").asInt(0),
                node.path("totalTracks").asInt(0),
                node.path("processedTracks").asInt(0),
                node.path("matchedTracks").asInt(0),
                node.path("failedTracks").asInt(0),
                node.has("estimatedSecondsRemaining") ? node.path("estimatedSecondsRemaining").asInt() : null,
                node.path("targetPlaylistUrl").isNull() ? null : node.path("targetPlaylistUrl").asText(),
                node.path("error").isNull() ? null : node.path("error").asText()
        );
    }
}
