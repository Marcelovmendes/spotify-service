package com.example.spotify.common.exception;

public class InfrastructureException extends ApplicationException{

    public InfrastructureException(String message, ErrorType type) {
        super(message, type);
    }

    public InfrastructureException(String message, Throwable cause, ErrorType type) {
        super(message, cause, type);
    }

    public InfrastructureException(String message) {
        super(message, ErrorType.SERVER_ERROR);
    }
}
