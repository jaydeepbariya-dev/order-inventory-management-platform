package com.orderinventorymanagementsystem.notificationservice.exception;

public class InvalidNotificationRequestException extends RuntimeException {
    public InvalidNotificationRequestException(String message) {
        super(message);
    }
}
