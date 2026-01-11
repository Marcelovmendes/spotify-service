package com.example.spotify.common.exception;


import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.exceptions.detailed.NotFoundException;

import java.io.IOException;
import java.text.ParseException;

@Component
public class SpotifyApiExceptionTranslator {

    public RuntimeException translate (Throwable e) {
        if (e instanceof Error) throw (Error) e;

        if (e instanceof ApplicationException) return (ApplicationException) e;

        if (e instanceof NotFoundException) {
            return new SpotifyApiException(
                    String.format("Spotify API operation failed: %s", e.getMessage()),
                    e, ErrorType.RESOURCE_NOT_FOUND_EXCEPTION);
        }
        if (e instanceof IOException) {
            return new InfrastructureException(
                  String.format("Network communication with Spotify API failed: %s", e.getMessage()),
                    e, ErrorType.API_UNAVAILABLE_EXCEPTION);
        }
        if (e instanceof SpotifyWebApiException) {
            if (e.getMessage().contains("401")) {
                return new AuthenticationException(
                        "Spotify authentication failed: token is invalid or expired. Please refresh credentials",
                        e, ErrorType.AUTHENTICATION_EXCEPTION);
            }
            return new SpotifyApiException(
                    String.format("Spotify API operation failed: %s", e.getMessage()),
                    e, ErrorType.SPOTIFY_API_EXCEPTION);
        }
        if (e instanceof ParseException) {
            return new InfrastructureException(
                   String.format("Failed to parse Spotify API response: potentially incompatible data format: %s", e.getMessage()),
                    e, ErrorType.SPOTIFY_API_EXCEPTION);
        }
        return new InfrastructureException(
                "Unexpected error during Spotify API operation: ", e, ErrorType.SERVER_ERROR);
    }
}
