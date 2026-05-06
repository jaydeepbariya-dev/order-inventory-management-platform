package com.orderinventorymanagementsystem.inventoryservice.service.impl;

import com.orderinventorymanagementsystem.inventoryservice.dto.*;
import com.orderinventorymanagementsystem.inventoryservice.entity.Inventory;
import com.orderinventorymanagementsystem.inventoryservice.repository.InventoryRepository;
import com.orderinventorymanagementsystem.inventoryservice.service.InventoryService;

import jakarta.transaction.Transactional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public InventoryResponseDTO checkStock(InventoryRequestDTO request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        return new InventoryResponseDTO(
                inventory.getProductId(),
                inventory.getAvailableQty(),
                inventory.getReservedQty());
    }

    @Override
    @Transactional
    public InventoryResponseDTO reserveStock(InventoryRequestDTO request) {
        try {
            Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            if (inventory.getAvailableQty() < request.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            // move stock
            inventory.setAvailableQty(inventory.getAvailableQty() - request.getQuantity());
            inventory.setReservedQty(inventory.getReservedQty() + request.getQuantity());

            Inventory saved = inventoryRepository.save(inventory);

            return new InventoryResponseDTO(
                    saved.getProductId(),
                    saved.getAvailableQty(),
                    saved.getReservedQty());

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Concurrent update detected, retry");
        }
    }

    @Override
    @Transactional
    public InventoryResponseDTO releaseStock(InventoryRequestDTO request) {

        try {
            Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            if (inventory.getReservedQty() < request.getQuantity()) {
                throw new RuntimeException("Invalid release quantity");
            }

            inventory.setReservedQty(inventory.getReservedQty() - request.getQuantity());
            inventory.setAvailableQty(inventory.getAvailableQty() + request.getQuantity());

            Inventory saved = inventoryRepository.save(inventory);

            return new InventoryResponseDTO(
                    saved.getProductId(),
                    saved.getAvailableQty(),
                    saved.getReservedQty());

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Concurrent update detected, retry");
        }
    }

    @Transactional
    public InventoryResponseDTO deductStock(InventoryRequestDTO request) {

        try {
            Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Inventory not found"));

            if (inventory.getReservedQty() < request.getQuantity()) {
                throw new RuntimeException("Not enough reserved stock");
            }

            inventory.setReservedQty(inventory.getReservedQty() - request.getQuantity());

            Inventory saved = inventoryRepository.save(inventory);

            return new InventoryResponseDTO(
                    saved.getProductId(),
                    saved.getAvailableQty(),
                    saved.getReservedQty());

        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new RuntimeException("Concurrent update detected, retry");
        }
    }

    @Override
    public String initInventory(InventoryRequestDTO request) {

        inventoryRepository.findByProductId(request.getProductId())
                .ifPresent(i -> {
                    throw new RuntimeException("Inventory already exists");
                });

        Inventory inventory = new Inventory();
        inventory.setProductId(request.getProductId());
        inventory.setAvailableQty(request.getQuantity());
        inventory.setReservedQty(0);

        Inventory saved = inventoryRepository.save(inventory);

        return "Inventory for productId: " + saved.getProductId() + " initiated successfully";
    }
}