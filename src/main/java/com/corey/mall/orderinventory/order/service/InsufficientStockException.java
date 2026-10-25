package com.corey.mall.orderinventory.order.service;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String sku) {
        super("Insufficient available inventory for sku: " + sku);
    }
}
