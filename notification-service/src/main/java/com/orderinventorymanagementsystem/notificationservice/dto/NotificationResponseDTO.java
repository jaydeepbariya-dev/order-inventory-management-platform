package com.orderinventorymanagementsystem.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.orderinventorymanagementsystem.notificationservice.enums.NotificationStatus;
import com.orderinventorymanagementsystem.notificationservice.enums.NotificationType;

import java.util.UUID;

public class NotificationResponseDTO {

    @Schema(description = "Notification identifier", example = "770e8400-e29b-41d4-a716-446655440000")
    private UUID id;
    @Schema(description = "User identifier for the notification", example = "99aadde0-96ad-4c86-8194-226673f806b9")
    private UUID userId;
    @Schema(description = "Notification type", example = "EMAIL")
    private NotificationType type;
    @Schema(description = "Notification message", example = "Your order has been placed successfully")
    private String message;
    @Schema(description = "Notification delivery status", example = "SENT")
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