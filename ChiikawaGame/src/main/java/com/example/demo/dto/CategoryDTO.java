package com.example.demo.dto;

public class CategoryDTO {
    private Integer categoryId;
    private String categoryName;
    private String categoryInfo;
    private String categoryPhoto;  // 用來存放 Base64 編碼的圖片

    // 建構子
    public CategoryDTO(Integer categoryId, String categoryName, String categoryInfo, String categoryPhoto) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryInfo = categoryInfo;
        this.categoryPhoto = categoryPhoto;
    }

    // Getter 和 Setter 方法
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getcategoryInfo() {
        return categoryInfo;
    }

    public void setcategoryInfo(String categoryInfo) {
        this.categoryInfo = categoryInfo;
    }

    public String getCategoryPhoto() {
        return categoryPhoto;
    }

    public void setCategoryPhoto(String categoryPhoto) {
        this.categoryPhoto = categoryPhoto;
    }
}