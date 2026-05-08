package com.orderinventorymanagementsystem.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.orderinventorymanagementsystem.notificationservice.enums.NotificationType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class NotificationRequestDTO {

    @Schema(description = "Identifier of the user receiving the notification", example = "99aadde0-96ad-4c86-8194-226673f806b9")
    @NotNull
    private UUID userId;

    @Schema(description = "Type of notification", example = "EMAIL")
    @NotNull
    private NotificationType type;

    @Schema(description = "Notification message content", example = "Your order has been placed successfully")
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