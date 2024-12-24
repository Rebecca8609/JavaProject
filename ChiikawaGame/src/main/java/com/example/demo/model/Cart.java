package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", referencedColumnName = "userId", nullable = false)
    @JsonIgnoreProperties({"carts"})
    private UserInfo buyer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"cart"})
    private List<CartItem> cartItems = new ArrayList<>();

    @NotNull
    @Column(name = "cart_total", nullable = false)
    private BigDecimal cartTotal = BigDecimal.ZERO;

    // Helper method to recalculate cart total
    public void recalculateCartTotal() {
        cartTotal = cartItems.stream()
                .map(CartItem::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Helper methods to manage bidirectional relationship
    public void addCartItem(CartItem item) {
        item.setCart(this);
        cartItems.add(item);
        recalculateCartTotal();
    }

    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
        recalculateCartTotal();
    }

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public UserInfo getBuyer() {
		return buyer;
	}

	public void setBuyer(UserInfo buyer) {
		this.buyer = buyer;
	}

	public List<CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public BigDecimal getCartTotal() {
		return cartTotal;
	}

	public void setCartTotal(BigDecimal cartTotal) {
		this.cartTotal = cartTotal;
	}
}
