package com.orderinventorymanagementsystem.orderservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorInfo> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorInfo> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
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