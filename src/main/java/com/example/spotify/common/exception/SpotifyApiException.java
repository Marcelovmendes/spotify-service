package com.example.spotify.common.exception;

public class SpotifyApiException extends ApplicationException {
    public SpotifyApiException(String message, ErrorType type) {

        super(message, type);
    }
    public SpotifyApiException(String message, Throwable cause, ErrorType type) {
        super(message, cause, type);
    }

    public SpotifyApiException(String message) {
        super(message, ErrorType.SPOTIFY_API_EXCEPTION);
    }
}
