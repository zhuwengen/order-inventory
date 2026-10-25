package com.corey.mall.orderinventory.common.web;

import com.corey.mall.orderinventory.common.error.ApiErrorResponse;
import com.corey.mall.orderinventory.common.error.BusinessErrorCode;
import com.corey.mall.orderinventory.order.service.InsufficientStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 库存不足是可预期的业务冲突，返回稳定错误码而非依赖可能变更的异常文本。
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientStock(InsufficientStockException exception) {
        BusinessErrorCode errorCode = BusinessErrorCode.INSUFFICIENT_STOCK;
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiErrorResponse(errorCode.getCode(), errorCode.getDefaultMessage()));
    }
}
