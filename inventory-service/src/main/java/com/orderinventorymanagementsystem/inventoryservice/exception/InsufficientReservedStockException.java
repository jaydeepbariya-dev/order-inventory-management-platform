package com.orderinventorymanagementsystem.inventoryservice.exception;

public class InsufficientReservedStockException extends RuntimeException {
    public InsufficientReservedStockException(String message) {
        super(message);
    }
}
