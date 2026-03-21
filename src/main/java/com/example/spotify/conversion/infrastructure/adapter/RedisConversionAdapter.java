package com.example.spotify.conversion.infrastructure.adapter;

import com.example.spotify.conversion.domain.ConversionException;
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
    private static final String STATUS_KEY_PREFIX = "conversion:";
    private static final String STATUS_KEY_SUFFIX = ":status";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisConversionAdapter(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
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
