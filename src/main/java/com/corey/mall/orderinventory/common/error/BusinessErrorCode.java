package com.corey.mall.orderinventory.common.error;

public enum BusinessErrorCode {

    INSUFFICIENT_STOCK("INSUFFICIENT_STOCK", "库存不足");

    private final String code;
    private final String defaultMessage;

    BusinessErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
