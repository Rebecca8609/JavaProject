package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference // 防止循環
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private UserInfo seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_size", nullable = false)
    @JsonIgnore // 防止序列化遞歸
    private ItemOption itemSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_photo_id", nullable = true)
    @JsonIgnore // 防止序列化遞歸
    private ItemPhoto itemPhoto;

    @NotNull
    @Positive
    @Column(name = "item_quantity", nullable = false)
    private Integer itemQuantity;

    @NotNull
    @Column(name = "item_price", nullable = false)
    private BigDecimal itemPrice;

	public Long getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Long orderItemId) {
		this.orderItemId = orderItemId;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public UserInfo getSeller() {
		return seller;
	}

	public void setSeller(UserInfo seller) {
		this.seller = seller;
	}

	public ItemOption getItemSize() {
		return itemSize;
	}

	public void setItemSize(ItemOption itemSize) {
		this.itemSize = itemSize;
	}

	public ItemPhoto getItemPhoto() {
		return itemPhoto;
	}

	public void setItemPhoto(ItemPhoto itemPhoto) {
		this.itemPhoto = itemPhoto;
	}

	public Integer getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(Integer itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public BigDecimal getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(BigDecimal itemPrice) {
		this.itemPrice = itemPrice;
	}


}

