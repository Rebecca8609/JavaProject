package com.example.demo.model;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "Item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemId;

    private String itemName;

    private BigDecimal itemPrice;
    
    private Date itemDate;
    
    private Date itemDue;
    
    private String itemLocation;

    private String itemInfo;
    
    private int itemSell = 0;

    private boolean itemDeleteStatus = false;

    // 建立多對一關聯：每個 Item 都對應一個 Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemCategoryId", referencedColumnName = "categoryId")
    private Category category;

    //品牌
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemBrandId", referencedColumnName = "brandId")
    private Brand brand;

    // 商品圖片
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item")
    @JsonIgnore
    private List<ItemPhoto> itemPhoto = new ArrayList<>();

    
    // 產品選項和庫存數量
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "item", orphanRemoval = true)
    private List<ItemOption> itemOption = new ArrayList<>();
    
    //Mantle
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemOption> itemOptions;
    
    // 多對多關聯到運送方式
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "Item_Transportation", // 中間表名稱
        joinColumns = @JoinColumn(name = "item_id"), // 商品的外鍵
        inverseJoinColumns = @JoinColumn(name = "transportation_id") // 運送方式的外鍵
    )
    private List<ItemTransportation> transportationMethods = new ArrayList<>();
    
    //賣家ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userId",nullable = false)
    private UserInfo userInfo;

    // 在插入之前自動設定 itemDate
    @PrePersist
    public void prePersist() {
        if (itemDate == null) {
            itemDate = new Date(System.currentTimeMillis()); // 設定為當前時間
        }
    }
    
    
}
