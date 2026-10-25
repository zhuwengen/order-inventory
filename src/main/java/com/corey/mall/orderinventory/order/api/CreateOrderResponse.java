package com.corey.mall.orderinventory.order.api;

public record CreateOrderResponse(
        String orderCode,
        String orderStatus
) {
}
