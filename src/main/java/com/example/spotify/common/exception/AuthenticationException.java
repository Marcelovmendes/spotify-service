package com.example.spotify.common.exception;

public class AuthenticationException extends ApplicationException {
    public AuthenticationException(String message, ErrorType type) {

        super(message, type);
    }
    public AuthenticationException(String message, Throwable cause, ErrorType type) {

        super(message, cause, type);
    }
    public AuthenticationException(String message) {

        super(message, ErrorType.AUTHENTICATION_EXCEPTION);
    }

}
