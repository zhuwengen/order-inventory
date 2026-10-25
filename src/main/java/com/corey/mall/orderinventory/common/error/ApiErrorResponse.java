package com.corey.mall.orderinventory.common.error;

/**
 * 对外暴露的稳定错误格式；客户端应根据 code 而非 message 判断业务错误类型。
 */
public record ApiErrorResponse(
        String code,
        String message
) {
}
