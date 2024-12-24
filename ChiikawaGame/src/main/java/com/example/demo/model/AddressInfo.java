package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "addressInfo")
public class AddressInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressId")
    private Integer addressId; 

    @Column(name = "userId", nullable = false)
    private Integer userId; 

    @Column(name = "userTel", nullable = false)
    private String userTel; 

    @Column(name = "addressee", nullable = false)
    private String addressee; 

    @Column(name = "addressCategory", nullable = false)
    private Integer addressCategory; 
    
    @Column (name = "storeId", nullable = false) //postId
    private Integer storeId;
    
    @Column (name = "storeName", nullable = false) //addressName
    private String  storeName;

    @Column(name = "storeAddress", nullable = false)
    private String storeAddress; 

    @Column(name = "addressStatus", nullable = false)
    private Integer addressStatus; 

    // 無參數建構子
    public AddressInfo() {
    }

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserTel() {
		return userTel;
	}

	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

	public String getAddressee() {
		return addressee;
	}

	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}

	public Integer getAddressCategory() {
		return addressCategory;
	}

	public void setAddressCategory(Integer addressCategory) {
		this.addressCategory = addressCategory;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getStoreAddress() {
		return storeAddress;
	}

	public void setStoreAddress(String storeAddress) {
		this.storeAddress = storeAddress;
	}

	public Integer getAddressStatus() {
		return addressStatus;
	}

	public void setAddressStatus(Integer addressStatus) {
		this.addressStatus = addressStatus;
	}
}