package com.example.spotify.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    AUTHENTICATION_EXCEPTION(HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED),
    SPOTIFY_API_EXCEPTION(HttpStatus.BAD_GATEWAY),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND),
    DOMAIN_EXCEPTION(HttpStatus.BAD_REQUEST),
    API_UNAVAILABLE_EXCEPTION(HttpStatus.SERVICE_UNAVAILABLE);

    private final HttpStatus httpStatus;

    ErrorType(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
