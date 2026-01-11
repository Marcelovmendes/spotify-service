package com.example.spotify.common.exception;

public class PlaylistException extends ApplicationException {
    public PlaylistException(String message, ErrorType type) {
        super(message, type);
    }

    public PlaylistException(String message, Throwable cause, ErrorType type) {
        super(message, cause, type);
    }

    public PlaylistException(String message) {
        super(message, ErrorType.SERVER_ERROR);
    }
}
