package com.orderinventorymanagementsystem.notificationservice.mapper;

import com.orderinventorymanagementsystem.notificationservice.dto.NotificationResponseDTO;
import com.orderinventorymanagementsystem.notificationservice.entity.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO toDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getUserId(),
                notification.getType(),
                notification.getMessage(),
                notification.getStatus()
        );
    }
}