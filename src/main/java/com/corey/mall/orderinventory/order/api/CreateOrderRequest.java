package com.corey.mall.orderinventory.order.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "idempotencyKey must not be blank")
        @Size(max = 64, message = "idempotencyKey must not exceed 64 characters")
        String idempotencyKey,

        @NotBlank(message = "sku must not be blank")
        @Size(max = 32, message = "sku must not exceed 32 characters")
        String sku,

        @NotNull(message = "quantity is required")
        @Positive(message = "quantity must be greater than zero")
        Integer quantity
) {
}
