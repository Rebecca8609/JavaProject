package com.example.demo.dto;

import java.math.BigDecimal;

public class OrderDTO {
    private Long orderId;
    private String orderStatus;
    private BigDecimal orderTotal;
    private String paymentStatus;
    private String shippingStatus;
    private ShippingInfoDTO shippingInfo;
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public BigDecimal getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(BigDecimal orderTotal) {
		this.orderTotal = orderTotal;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getShippingStatus() {
		return shippingStatus;
	}
	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}
	public ShippingInfoDTO getShippingInfo() {
		return shippingInfo;
	}
	public void setShippingInfo(ShippingInfoDTO shippingInfo) {
		this.shippingInfo = shippingInfo;
	}

}

