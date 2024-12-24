package com.example.demo.dto;

public class BrandDTO {
    private Integer brandId;
    private String brandName;
    private String brandInfo;
    private String brandPhoto;  // 用來存放 Base64 編碼的圖片

    // 建構子
    public BrandDTO(Integer brandId, String brandName, String brandInfo, String brandPhoto) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.brandInfo = brandInfo;
        this.brandPhoto = brandPhoto;
    }

    // Getter 和 Setter 方法
    public Integer getbrandId() {
        return brandId;
    }

    public void setbrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getbrandName() {
        return brandName;
    }

    public void setbrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getbrandInfo() {
        return brandInfo;
    }

    public void setbrandInfo(String brandInfo) {
        this.brandInfo = brandInfo;
    }

    public String getbrandPhoto() {
        return brandPhoto;
    }

    public void setbrandPhoto(String brandPhoto) {
        this.brandPhoto = brandPhoto;
    }
}