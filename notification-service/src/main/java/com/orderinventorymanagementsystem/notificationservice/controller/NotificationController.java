package com.orderinventorymanagementsystem.notificationservice.controller;

import com.orderinventorymanagementsystem.notificationservice.dto.*;
import com.orderinventorymanagementsystem.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification Controller", description = "Notification APIs")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Send Notification", description = "Sends a notification message")
    @PostMapping
    public ResponseEntity<NotificationResponseDTO> sendNotification(
            @RequestBody NotificationRequestDTO request) {

        NotificationResponseDTO res = notificationService.sendNotification(request);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}