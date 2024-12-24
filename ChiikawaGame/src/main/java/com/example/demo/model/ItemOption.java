package com.example.demo.model;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Entity
@Table(name = "ItemOption")
public class ItemOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String optionName;  // 選項名稱

    @Column(nullable = false)
    private int quantity; // 庫存數量
    
    @Column(precision = 10, scale = 2)
    private BigDecimal optionPrice; // 選項額外價格

    // 關聯到商品，設定為懶加載
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId", nullable = false)
    private Item item;
}