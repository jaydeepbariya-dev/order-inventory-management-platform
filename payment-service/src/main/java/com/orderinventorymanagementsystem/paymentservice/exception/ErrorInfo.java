package com.orderinventorymanagementsystem.paymentservice.exception;

import java.time.Instant;

public class ErrorInfo {

    private Instant timestamp;
    private String message;
    private int status;
    private String path;

    public ErrorInfo(Instant timestamp, String message, int status, String path) {
        this.timestamp = timestamp;
        this.message = message;
        this.status = status;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }
}
