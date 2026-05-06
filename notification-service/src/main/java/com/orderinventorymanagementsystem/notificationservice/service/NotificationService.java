package com.orderinventorymanagementsystem.notificationservice.service;

import com.orderinventorymanagementsystem.notificationservice.dto.*;

public interface NotificationService {

    NotificationResponseDTO sendNotification(NotificationRequestDTO request);

}