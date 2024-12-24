package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "shipping_info")
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_info_id")
    private Long shippingInfoId;

    @Column(name = "shipping_info_recipient", nullable = false)
    @NotNull(message = "收件人不能為空")
    @Size(max = 255, message = "收件人名稱不能超過 255 個字元")
    private String shippingInfoRecipient;

    @Column(name = "shipping_info_address", nullable = false)
    @NotNull(message = "地址不能為空")
    @Size(max = 500, message = "地址不能超過 500 個字元")
    private String shippingInfoAddress;

    @Column(name = "shipping_info_status", nullable = false)
    @NotNull(message = "物流狀態不能為空")
    private String shippingInfoStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_method_id", nullable = false)
    private ShippingMethod shippingMethod;

    @OneToOne(mappedBy = "shippingInfo", fetch = FetchType.LAZY)
    @JsonIgnore // 避免循環引用
    private Order order;


    @Column(name = "shipping_info_tracking_number")
    @Size(max = 100, message = "物流追蹤號不能超過 100 個字元")
    private String shippingInfoTrackingNumber;

    @Lob
    @Column(name = "shipping_info_image")
    private byte[] shippingInfoImage;

    public void setOrder(Order order) {
        this.order = order;
        if (order != null && order.getShippingInfo() != this) {
            order.setShippingInfo(this);
        }
    }


	public Long getShippingInfoId() {
		return shippingInfoId;
	}

	public void setShippingInfoId(Long shippingInfoId) {
		this.shippingInfoId = shippingInfoId;
	}

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

	public String getShippingInfoStatus() {
		return shippingInfoStatus;
	}

	public void setShippingInfoStatus(String shippingInfoStatus) {
		this.shippingInfoStatus = shippingInfoStatus;
	}

	public ShippingMethod getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(ShippingMethod shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public String getShippingInfoTrackingNumber() {
		return shippingInfoTrackingNumber;
	}

	public void setShippingInfoTrackingNumber(String shippingInfoTrackingNumber) {
		this.shippingInfoTrackingNumber = shippingInfoTrackingNumber;
	}

	public byte[] getShippingInfoImage() {
		return shippingInfoImage;
	}

	public void setShippingInfoImage(byte[] shippingInfoImage) {
		this.shippingInfoImage = shippingInfoImage;
	}

	public Order getOrder() {
		return order;
	}

}

