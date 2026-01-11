package com.example.spotify.common.exception;

public class UserProfileException extends ApplicationException {

    public UserProfileException(String message, ErrorType type) { super(message, type);}
    public UserProfileException(String message) {
       super(message, ErrorType.SERVER_ERROR);
    }
}
