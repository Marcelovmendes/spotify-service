package com.example.spotify.common.infrastructure.adapter;

import com.example.spotify.common.exception.SpotifyApiExceptionTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class ExternalServiceAdapter {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final SpotifyApi spotifyApi;
    protected final SpotifyApiExceptionTranslator exceptionTranslator;

    protected ExternalServiceAdapter(SpotifyApi spotifyApi, SpotifyApiExceptionTranslator exceptionTranslator) {
        this.spotifyApi = spotifyApi;
        this.exceptionTranslator = exceptionTranslator;
    }


    protected <T> T executeSync(SpotifyOperation<T> operation, String operationName) {
        try {
            return operation.execute();
        } catch (Throwable exception) {
            log.error("ExecuteSyncError {}: {}", operationName, exception.getMessage());
            throw exceptionTranslator.translate(exception);
        }
    }

    protected <T> T executeAsync(CompletableFuture<T> future, String operation) {
        return future
                .orTimeout(15, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    log.error("ExecuteAsyncError {}: {}", operation, throwable.getMessage());
                    throw exceptionTranslator.translate(throwable);
                })
                .join();
    }
    @FunctionalInterface
    public interface SpotifyOperation<T> {
        T execute() throws IOException, SpotifyWebApiException, org.apache.hc.core5.http.ParseException;
    }


}
