package com.example.demo.model;

public enum OrderStatus {
    PENDING("待處理"),
    PROCESSING("處理中"),
    COMPLETED("已完成"),
    CANCELLED("取消"),
    REFUNDED("退貨/退款");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    // Getter
    public String getDisplayName() {
        return displayName;
    }
}
