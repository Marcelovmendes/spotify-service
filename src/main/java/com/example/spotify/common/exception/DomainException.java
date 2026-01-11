package com.example.spotify.common.exception;

public class DomainException extends ApplicationException {

    public DomainException(String message, ErrorType type) {
        super(message, type);
    }
    public DomainException(String message, Throwable cause, ErrorType type) {
        super(message, cause, type);
    }
    public DomainException(String message) {
        super(message, ErrorType.DOMAIN_EXCEPTION);
    }
}
