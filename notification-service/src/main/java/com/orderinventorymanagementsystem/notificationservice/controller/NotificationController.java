package com.orderinventorymanagementsystem.notificationservice.controller;

import com.orderinventorymanagementsystem.notificationservice.dto.*;
import com.orderinventorymanagementsystem.notificationservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> sendNotification(
            @RequestBody NotificationRequestDTO request) {

        NotificationResponseDTO res = notificationService.sendNotification(request);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}