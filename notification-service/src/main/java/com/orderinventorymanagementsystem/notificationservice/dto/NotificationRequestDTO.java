package com.orderinventorymanagementsystem.notificationservice.dto;

import com.orderinventorymanagementsystem.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class NotificationRequestDTO {

    @NotNull
    private UUID userId;

    @NotNull
    private NotificationType type;

    @NotNull
    private String message;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}