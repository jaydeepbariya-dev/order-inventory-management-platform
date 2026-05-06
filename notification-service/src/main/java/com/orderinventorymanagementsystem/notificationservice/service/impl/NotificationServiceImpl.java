package com.orderinventorymanagementsystem.notificationservice.service.impl;

import com.orderinventorymanagementsystem.notificationservice.dto.*;
import com.orderinventorymanagementsystem.notificationservice.entity.Notification;
import com.orderinventorymanagementsystem.notificationservice.enums.NotificationStatus;
import com.orderinventorymanagementsystem.notificationservice.exception.*;
import com.orderinventorymanagementsystem.notificationservice.repository.NotificationRepository;
import com.orderinventorymanagementsystem.notificationservice.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponseDTO sendNotification(NotificationRequestDTO request) {

        // 1. Basic validation
        if (request.getUserId() == null) {
            throw new InvalidNotificationRequestException("User ID is required for sending notification");
        }

        if (request.getType() == null) {
            throw new InvalidNotificationRequestException("Notification type is required");
        }

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new InvalidNotificationRequestException("Notification message cannot be empty");
        }

        // 2. Create entity
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setMessage(request.getMessage().trim());

        // 3. Mock sending logic (simulate real-world failure)
        boolean success = Math.random() > 0.1; // 90% success rate

        if (success) {
            notification.setStatus(NotificationStatus.SENT);
        } else {
            notification.setStatus(NotificationStatus.FAILED);
        }

        // 4. Save to DB
        Notification saved = notificationRepository.save(notification);

        // 5. Return response
        return new NotificationResponseDTO(
                saved.getId(),
                saved.getUserId(),
                saved.getType(),
                saved.getMessage(),
                saved.getStatus());
    }
}