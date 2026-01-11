package com.example.spotify.common.exception;

public abstract class ApplicationException extends RuntimeException {
    private final ErrorType type;

    protected ApplicationException(String message, ErrorType type) {
        super(message);
        this.type = type;
    }
    public ApplicationException(String message, Throwable cause, ErrorType type) {
        super(message, cause);
        this.type = type;
    }
    public ApplicationException(String message) {
        super(message);
        this.type = ErrorType.SERVER_ERROR;
    }
    public ErrorType getType() {
        return type;
    }
}
