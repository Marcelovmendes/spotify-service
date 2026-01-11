package com.example.spotify.auth.infrastructure.repository;

import com.example.spotify.auth.domain.entity.AuthState;
import com.example.spotify.auth.domain.repository.AuthStateRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisAuthStateRepository implements AuthStateRepository {

    private static final String REDIS_AUTH_STATE = "oauth:state:";
    private final StringRedisTemplate redisTemplate;

    public RedisAuthStateRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(AuthState state, Duration timeout) {
        String key = REDIS_AUTH_STATE + state.getStateValue();
        redisTemplate.opsForValue().set(key, state.getCodeVerifier(), timeout);
    }

    @Override
    public Optional<AuthState>
    findByStateValue(String stateValue) {
        String key = REDIS_AUTH_STATE + stateValue;
        String codeVerifier = redisTemplate.opsForValue().get(key);

        if (codeVerifier == null) {
            return Optional.empty();
        }

        return Optional.of(AuthState.create(stateValue, codeVerifier));
    }


    @Override
    public void remove(String stateValue) {
        String key = REDIS_AUTH_STATE + stateValue;
        redisTemplate.delete(key);
    }
}

