package com.example.demo.model;

public enum PaymentStatus {
    UNPAID("待付款"),
    PAID("已付款");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    // Getter
    public String getDisplayName() {
        return displayName;
    }
}
