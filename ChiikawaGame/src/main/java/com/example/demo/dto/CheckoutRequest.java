package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotBlank(message = "收件人名稱不能為空")
    private String shippingInfoRecipient; // 收件人名稱

    @NotBlank(message = "收件地址不能為空")
    private String shippingInfoAddress;   // 收件地址

    @NotNull(message = "物流方式 ID 不能為空")
    private Long shippingMethodId;        // 物流方式 ID

    @NotNull(message = "訂單 ID 不能為空")
    private Long orderId;                 // 訂單 ID

    // Constructor 無參數建構子（可選）
    public CheckoutRequest() {
    }

    // Getter 和 Setter
    public String getShippingInfoRecipient() {
        return shippingInfoRecipient;
    }

    public void setShippingInfoRecipient(String shippingInfoRecipient) {
        this.shippingInfoRecipient = shippingInfoRecipient;
    }

    public String getShippingInfoAddress() {
        return shippingInfoAddress;
    }

    public void setShippingInfoAddress(String shippingInfoAddress) {
        this.shippingInfoAddress = shippingInfoAddress;
    }

    public Long getShippingMethodId() {
        return shippingMethodId;
    }


    public void setShippingMethodId(Long shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
