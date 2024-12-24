package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CartDto;
import com.example.demo.dto.CartItemDto;
import com.example.demo.dto.CheckoutRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.CartItem;
import com.example.demo.model.CartRepository;
import com.example.demo.model.ItemOption;
import com.example.demo.model.ItemOptionRepositry;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderRepository;
import com.example.demo.model.ShippingInfo;
import com.example.demo.model.ShippingMethod;
import com.example.demo.model.UserInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemOptionRepositry itemOptionRepository;

    @Autowired
    private ShippingMethodService shippingMethodService;

    @Autowired
    private ShippingInfoService shippingInfoService;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public Cart getOrCreateCart(UserInfo buyer) {
        return cartRepository.findByBuyer(buyer)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setBuyer(buyer);
                    newCart.setCartTotal(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItemToCart(UserInfo buyer, int itemSizeId, int quantity) {
        Cart cart = getOrCreateCart(buyer);

        Optional<ItemOption> itemSizeOpt = itemOptionRepository.findById(itemSizeId);
        if (itemSizeOpt.isEmpty()) {
            throw new IllegalArgumentException("無效的商品選項ID");
        }
        ItemOption itemSize = itemSizeOpt.get();

        if (itemSize.getQuantity() < quantity) {
            throw new IllegalArgumentException("商品庫存不足！");
        }

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getItemSize().getId() == itemSizeId)
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setItemQuantity(existingItem.getItemQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setItem(itemSize.getItem());
            cartItem.setItemSize(itemSize);
            cartItem.setItemQuantity(quantity);
            cartItem.setItemPrice(itemSize.getOptionPrice());
            cartItem.setSeller(itemSize.getItem().getUserInfo());
            cart.addCartItem(cartItem);
        }

        itemSize.setQuantity(itemSize.getQuantity() - quantity);
        itemOptionRepository.save(itemSize);

        cart.recalculateCartTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public CartDto updateItemQuantity(UserInfo buyer, int itemSizeId, int newQuantity) {
        Cart cart = getOrCreateCart(buyer);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getItemSize().getId() == itemSizeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("購物車中未找到該商品選項"));

        int deltaQuantity = newQuantity - cartItem.getItemQuantity();
        if (deltaQuantity > 0 && cartItem.getItemSize().getQuantity() < deltaQuantity) {
            throw new IllegalArgumentException("庫存不足，無法更新商品數量！");
        }

        cartItem.getItemSize().setQuantity(cartItem.getItemSize().getQuantity() - deltaQuantity);
        cartItem.setItemQuantity(newQuantity);

        cart.recalculateCartTotal();
        itemOptionRepository.save(cartItem.getItemSize());
        cartRepository.save(cart);

        return convertToCartDto(cart);
    }

    @Transactional
    public CartDto removeItemFromCart(UserInfo buyer, int itemSizeId) {
        Cart cart = getOrCreateCart(buyer);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getItemSize().getId() == itemSizeId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("購物車中未找到該商品選項"));

        cartItem.getItemSize().setQuantity(cartItem.getItemSize().getQuantity() + cartItem.getItemQuantity());
        itemOptionRepository.save(cartItem.getItemSize());

        cart.removeCartItem(cartItem);

        cartRepository.save(cart);

        return convertToCartDto(cart);
    }

    @Transactional
    public CartDto clearCart(UserInfo buyer) {
        Cart cart = getOrCreateCart(buyer);
        cart.getCartItems().forEach(item -> {
            item.getItemSize().setQuantity(item.getItemSize().getQuantity() + item.getItemQuantity());
            itemOptionRepository.save(item.getItemSize());
        });
        cart.getCartItems().clear();
        cart.setCartTotal(BigDecimal.ZERO);
        cartRepository.save(cart);

        return convertToCartDto(cart);
    }

   
    @Transactional
    public Order checkoutCart(UserInfo buyer, CheckoutRequest checkoutRequest) {
        Cart cart = getCartByBuyerId(buyer.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("購物車不存在"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalStateException("購物車為空，無法結帳");
        }

        // 構建 ShippingInfo
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setShippingInfoRecipient(checkoutRequest.getShippingInfoRecipient());
        shippingInfo.setShippingInfoAddress(checkoutRequest.getShippingInfoAddress());
        shippingInfo.setShippingInfoStatus("Pending");

        // 設置物流方式
        ShippingMethod shippingMethod = shippingMethodService.getMethodById(checkoutRequest.getShippingMethodId())
                .orElseThrow(() -> new IllegalArgumentException("無效的物流方式"));
        shippingInfo.setShippingMethod(shippingMethod);

        // 創建訂單
        Order order = new Order();
        order.setBuyer(buyer);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus("Pending");
        order.setPaymentStatus("Unpaid");
        order.setShippingStatus("Not Shipped");
        order.setOrderTotal(cart.getCartTotal());
        order.setShippingInfo(shippingInfo);

        // 設置訂單項目
        cart.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(cartItem.getItem());
            orderItem.setItemSize(cartItem.getItemSize());
            orderItem.setItemPrice(cartItem.getItemPrice());
            orderItem.setItemQuantity(cartItem.getItemQuantity());
            orderItem.setSeller(cartItem.getSeller());
            order.addOrderItem(orderItem);
        });

        // 保存物流信息和訂單
        shippingInfo.setOrder(order);
        shippingInfoService.saveShippingInfo(shippingInfo);
        clearCart(buyer);

        return orderRepository.save(order);
    }


    public CartDto convertToCartDto(Cart cart) {
        CartDto cartDto = new CartDto();

        List<CartItemDto> cartItemDtos = cart.getCartItems().stream().map(cartItem -> {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setCartItemId(cartItem.getCartItemId());
            itemDto.setItemName(cartItem.getItem().getItemName());
            itemDto.setItemPrice(cartItem.getItemPrice());
            itemDto.setItemQuantity(cartItem.getItemQuantity());

            if (cartItem.getItem().getItemPhoto() != null && !cartItem.getItem().getItemPhoto().isEmpty()) {
                byte[] photoData = cartItem.getItem().getItemPhoto().get(0).getPhotoFile();
                if (photoData != null) {
                    String base64Image = Base64.getEncoder().encodeToString(photoData);
                    itemDto.setImageUrl("data:image/jpeg;base64," + base64Image);
                }
            } else {
                itemDto.setImageUrl("/images/default-placeholder.png");
            }

            return itemDto;
        }).collect(Collectors.toList());

        cartDto.setCartItems(cartItemDtos);
        cartDto.setCartTotal(cart.getCartTotal());

        return cartDto;
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto(UserInfo buyer) {
        Cart cart = getOrCreateCart(buyer);
        return convertToCartDto(cart);
    }

    @Transactional(readOnly = true)
    public Optional<Cart> getCartByBuyerId(Integer buyerId) {
        return cartRepository.findByBuyerUserId(buyerId);
    }
}
