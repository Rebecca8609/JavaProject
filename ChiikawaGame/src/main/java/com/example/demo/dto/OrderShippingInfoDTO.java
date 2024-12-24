package com.example.demo.dto;

import com.example.demo.model.Order;
import com.example.demo.model.ShippingInfo;

public class OrderShippingInfoDTO {
    private Order order;
    private ShippingInfo shippingInfo;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }
}
