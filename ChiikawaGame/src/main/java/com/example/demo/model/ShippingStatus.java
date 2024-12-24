package com.example.demo.model;

public enum ShippingStatus {
    NOT_SHIPPED("未出貨"),
    SHIPPED("運送中"),
    PENDING_RECEIPT("待收貨"),
    RECEIVED("已收貨");

    private final String displayName;

    ShippingStatus(String displayName) {
        this.displayName = displayName;
    }

    // Getter
    public String getDisplayName() {
        return displayName;
    }
}
