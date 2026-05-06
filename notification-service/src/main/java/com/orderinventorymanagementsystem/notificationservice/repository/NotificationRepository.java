package com.orderinventorymanagementsystem.notificationservice.repository;

import com.orderinventorymanagementsystem.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}