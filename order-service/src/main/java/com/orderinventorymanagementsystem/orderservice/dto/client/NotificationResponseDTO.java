package com.orderinventorymanagementsystem.orderservice.dto.client;

import java.util.UUID;

import com.orderinventorymanagementsystem.orderservice.enums.NotificationStatus;
import com.orderinventorymanagementsystem.orderservice.enums.NotificationType;

public class NotificationResponseDTO {

    private UUID id;
    private UUID userId;
    private NotificationType type;
    private String message;
    private NotificationStatus status;

    public NotificationResponseDTO(UUID id, UUID userId, NotificationType type, String message,
            NotificationStatus status) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

}