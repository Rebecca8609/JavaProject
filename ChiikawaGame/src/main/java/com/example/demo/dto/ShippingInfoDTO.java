package com.example.demo.dto;
public class ShippingInfoDTO {
    private String shippingInfoRecipient;
    private String shippingInfoAddress;
    private String shippingInfoStatus;
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

}
