package com.example.spotify.conversion.domain;

import com.example.spotify.common.exception.ApplicationException;
import com.example.spotify.common.exception.ErrorType;

public class ConversionException extends ApplicationException {

    public ConversionException(String message) {
        super(message, ErrorType.DOMAIN_EXCEPTION);
    }

    public ConversionException(String message, Throwable cause) {
        super(message, cause, ErrorType.SERVER_ERROR);
    }

    public static ConversionException jobNotFound(String jobId) {
        return new ConversionException("Conversion job not found: " + jobId);
    }

    public static ConversionException queueError(String message, Throwable cause) {
        return new ConversionException("Failed to queue conversion job: " + message, cause);
    }

    public static ConversionException statusFetchError(String jobId, Throwable cause) {
        return new ConversionException("Failed to fetch status for job: " + jobId, cause);
    }
}
