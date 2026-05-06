package com.orderinventorymanagementsystem.productservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorInfo> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorInfo> handleProductAlreadyExists(
            ProductAlreadyExistsException ex,
            HttpServletRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleProductNotFound(
            ProductNotFoundException ex,
            HttpServletRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InventoryServiceException.class)
    public ResponseEntity<ErrorInfo> handleInventoryServiceError(
            InventoryServiceException ex,
            HttpServletRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorInfo> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        String message = "Database error: Data integrity violation occurred";
        return buildResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorInfo> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {
        String message = "An unexpected error occurred: " + ex.getMessage();
        return buildResponse(message, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorInfo> buildResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request) {

        ErrorInfo error = new ErrorInfo(
                Instant.now(),
                message,
                status.value(),
                request.getRequestURI());

        return new ResponseEntity<>(error, status);
    }
}
