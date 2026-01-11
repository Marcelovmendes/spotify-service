package com.example.spotify.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;

import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final String TRACE_ID = MDC.get("traceId");

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex, WebRequest request) {
        log.error("ApplicationException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getType().toString(),
                ex.getMessage(),
                ex.getType().getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error,ex.getType().getHttpStatus());
    };
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        log.error("GeneralException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
               "SERVER_ERROR",
                "Internal Server Error",
                ErrorType.SERVER_ERROR.getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        log.error("AuthException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getType().toString(),
                ex.getMessage(),
                ex.getType().getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error,ex.getType().getHttpStatus());
    }
    @ExceptionHandler(SpotifyApiException.class)
    public ResponseEntity<ErrorResponse> handleSpotifyApiException(SpotifyApiException ex, WebRequest request) {
        log.error("SpotifyApiException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getType().toString(),
                ex.getMessage(),
                ex.getType().getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error,ex.getType().getHttpStatus());
    }
    @ExceptionHandler(UserProfileException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileException(UserProfileException ex, WebRequest request) {

        log.error("ProfileException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getType().toString(),
                ex.getMessage(),
                ex.getType().getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error,ex.getType().getHttpStatus());
    }
    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfrastructureException(InfrastructureException ex, WebRequest request) {

        log.error("InfrastructureException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getType().toString(),
                ex.getMessage(),
                ex.getType().getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error,ex.getType().getHttpStatus());
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<ErrorResponse> handleCompletionException(ApplicationException ex, WebRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof ApplicationException) {
            return handleCompletionException((ApplicationException) cause, request);
        }

        log.error("CompletionException: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getType().toString(),
                ex.getMessage(),
                ex.getType().getHttpStatus().value(),
                LocalDateTime.now(),
                Map.of("exception", ex.getClass().getSimpleName()));

        return new ResponseEntity<>(error,ex.getType().getHttpStatus());
    }


    private  Map<String, Object> getErrorDetails(Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("exception", ex.getClass().getName());
        errorDetails.put("path", request.getContextPath());
        return errorDetails;
    }
}
