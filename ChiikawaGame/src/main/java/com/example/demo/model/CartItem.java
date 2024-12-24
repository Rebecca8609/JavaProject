package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor

public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_size", nullable = false) // Renamed to item_size
    private ItemOption itemSize; // Reflects size-related association

    @NotNull
    @Positive
    @Column(name = "item_quantity", nullable = false)
    private Integer itemQuantity;

    @NotNull
    @Column(name = "item_price", nullable = false)
    private BigDecimal itemPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @JsonBackReference // Prevents JSON serialization issues
    private Cart cart;

    // Method to calculate total price for this cart item
    public BigDecimal calculateItemTotal() {
        return itemPrice.multiply(new BigDecimal(itemQuantity));
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private UserInfo seller;

    // Getter
    public UserInfo getSeller() {
        return seller;
    }

    // Setter
    public void setSeller(UserInfo seller) {
        this.seller = seller;
    }
    	
	public Long getCartItemId() {
		return cartItemId;
	}

	public void setCartItemId(Long cartItemId) {
		this.cartItemId = cartItemId;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public ItemOption getItemSize() {
		return itemSize;
	}

	public void setItemSize(ItemOption itemSize) {
		this.itemSize = itemSize;
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

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}
}
