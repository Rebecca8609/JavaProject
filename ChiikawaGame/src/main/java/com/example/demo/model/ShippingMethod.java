package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "shipping_method")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShippingMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_method_id")
    private Long shippingMethodId;

    @Column(name = "method_name", nullable = false, unique = true)
    @NotNull(message = "物流方式名稱不能為空")
    @Size(max = 255, message = "名稱不能超過255字元")
    private String methodName;

    @Column(name = "description")
    @Size(max = 500, message = "描述不能超過500字元")
    private String description;

    public Long getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(Long shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
