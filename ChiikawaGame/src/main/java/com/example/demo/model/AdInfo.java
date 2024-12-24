package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "adInfo")
public class AdInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AdID")
    private Integer adId;

    @Column(name = "AdName", nullable = false)
    private String adName;

    @Lob
    @Column(name = "AdImage", nullable = true)
    private byte[] adImage; // 儲存二進位資料（圖片）

    @Column(name = "AdInfo", nullable = true)
    private String adInfo;

    @Column(name = "AdCreatedTime", nullable = false, updatable = false)
    private LocalDateTime adCreatedTime;

    // 自動設定 AdCreatedTime
    @PrePersist
    protected void onCreate() {
        this.adCreatedTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getAdId() {
        return adId;
    }

    public void setAdId(Integer adId) {
        this.adId = adId;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public byte[] getAdImage() {
        return adImage;
    }

    public void setAdImage(byte[] adImage) {
        this.adImage = adImage;
    }

    public String getAdInfo() {
        return adInfo;
    }

    public void setAdInfo(String adInfo) {
        this.adInfo = adInfo;
    }

    public LocalDateTime getAdCreatedTime() {
        return adCreatedTime;
    }
}