package com.corey.mall.orderinventory.order.service;

import com.corey.mall.orderinventory.order.api.CreateOrderRequest;
import com.corey.mall.orderinventory.order.api.CreateOrderResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderTransactionService orderTransactionService;

    public OrderService(OrderTransactionService orderTransactionService) {
        this.orderTransactionService = orderTransactionService;
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return orderTransactionService.findByIdempotencyKey(request.idempotencyKey())
                .orElseGet(() -> createNewOrder(request));
    }

    private CreateOrderResponse createNewOrder(CreateOrderRequest request) {
        try {
            return orderTransactionService.createOrder(request);
        } catch (DuplicateKeyException | InsufficientStockException exception) {
            return orderTransactionService.findByIdempotencyKey(request.idempotencyKey())
                    .orElseThrow(() -> exception);
        }
    }
}
