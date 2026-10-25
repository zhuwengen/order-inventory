package com.corey.mall.orderinventory.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.corey.mall.orderinventory.order.api.CreateOrderRequest;
import com.corey.mall.orderinventory.order.api.CreateOrderResponse;
import com.corey.mall.orderinventory.order.persistence.entity.OrderEntity;
import com.corey.mall.orderinventory.order.persistence.entity.OrderItemEntity;
import com.corey.mall.orderinventory.order.persistence.entity.SkuEntity;
import com.corey.mall.orderinventory.order.persistence.mapper.OrderItemMapper;
import com.corey.mall.orderinventory.order.persistence.mapper.OrderMapper;
import com.corey.mall.orderinventory.order.persistence.mapper.SkuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
class OrderTransactionService {

    private static final int PENDING_PAYMENT = 0;
    private static final String PENDING_PAYMENT_STATUS = "PENDING_PAYMENT";
    private static final int NOT_DELETED = 0;

    private final SkuMapper skuMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    OrderTransactionService(SkuMapper skuMapper, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.skuMapper = skuMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    Optional<CreateOrderResponse> findByIdempotencyKey(String idempotencyKey) {
        // 幂等键受数据库全局唯一索引约束，因此回读不能排除软删除订单，否则重复请求会触发重复键异常。
        OrderEntity order = orderMapper.selectOne(new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getIdempotencyKey, idempotencyKey));
        return Optional.ofNullable(order)
                .map(existingOrder -> new CreateOrderResponse(
                        existingOrder.getOrderCode(),
                        toOrderStatus(existingOrder.getOrderStatus())
                ));
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Optional<CreateOrderResponse> existingOrder = findByIdempotencyKey(request.idempotencyKey());
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        // 条件更新将可用库存原子转为冻结库存，待支付订单不应提前计入实际消耗。
        int updatedRows = skuMapper.freezeAvailableQuantity(request.sku(), request.quantity());
        if (updatedRows != 1) {
            throw new InsufficientStockException(request.sku());
        }

        SkuEntity sku = skuMapper.selectOne(new LambdaQueryWrapper<SkuEntity>()
                .eq(SkuEntity::getSku, request.sku())
                .eq(SkuEntity::getDeleteFlag, NOT_DELETED));
        if (sku == null) {
            throw new IllegalStateException("SKU disappeared after inventory deduction: " + request.sku());
        }

        String orderCode = "ORD-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        OrderEntity order = createPendingOrder(orderCode, request.idempotencyKey());
        OrderItemEntity orderItem = createOrderItem(orderCode, sku.getSku(), sku.getSkuName(), request.quantity());

        // 库存扣减、订单头和订单明细必须在同一事务中提交；任一写入失败会回滚全部修改。
        requireSingleRow(orderMapper.insert(order), "Failed to create order");
        requireSingleRow(orderItemMapper.insert(orderItem), "Failed to create order item");

        return new CreateOrderResponse(orderCode, PENDING_PAYMENT_STATUS);
    }

    private static OrderEntity createPendingOrder(String orderCode, String idempotencyKey) {
        OrderEntity order = new OrderEntity();
        order.setOrderCode(orderCode);
        order.setIdempotencyKey(idempotencyKey);
        order.setOrderStatus(PENDING_PAYMENT);
        // 当前请求未包含定价信息；使用零金额满足既有非空字段，价格计算留待后续独立流程实现。
        order.setOriginalPrice(BigDecimal.ZERO);
        order.setDiscountPrice(BigDecimal.ZERO);
        order.setPayPrice(BigDecimal.ZERO);
        order.setDeleteFlag(NOT_DELETED);
        return order;
    }

    private static OrderItemEntity createOrderItem(String orderCode, String sku, String skuName, int quantity) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderCode(orderCode);
        orderItem.setSku(sku);
        orderItem.setSkuName(skuName);
        orderItem.setOriginalUnitPrice(BigDecimal.ZERO);
        orderItem.setActualUnitPrice(BigDecimal.ZERO);
        orderItem.setQuantity(quantity);
        return orderItem;
    }

    private static void requireSingleRow(int affectedRows, String message) {
        if (affectedRows != 1) {
            throw new IllegalStateException(message);
        }
    }

    private static String toOrderStatus(Integer status) {
        return switch (status) {
            case PENDING_PAYMENT -> PENDING_PAYMENT_STATUS;
            case 1 -> "PAID";
            case 2 -> "CANCELLED";
            default -> "UNKNOWN";
        };
    }
}
