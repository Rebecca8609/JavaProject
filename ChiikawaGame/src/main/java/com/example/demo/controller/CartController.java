package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CartDto;
import com.example.demo.dto.CheckoutRequest;
import com.example.demo.model.Cart;
import com.example.demo.model.Item;
import com.example.demo.model.Order;
import com.example.demo.model.ShippingInfo;
import com.example.demo.model.ShippingMethod;
import com.example.demo.model.UserInfo;
import com.example.demo.service.CartService;
import com.example.demo.service.ItemService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ShippingInfoService;
import com.example.demo.service.ShippingMethodService;
import com.example.demo.service.UserInfoService;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserInfoService userInfoService;

    public CartController(CartService cartService, UserInfoService userInfoService) {
        this.cartService = cartService;
        this.userInfoService = userInfoService;
    }

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ShippingInfoService shippingInfoService;

    @Autowired
    private ShippingMethodService shippingMethodService;

    @GetMapping("/cart/item/{itemId}")
    public String viewItemFromCart(@PathVariable Integer itemId, Model model) {
        Item item = itemService.findItemById(itemId);
        if (item == null) {
            return "errorPage"; // 顯示錯誤頁面
        }
        model.addAttribute("item", item);
        model.addAttribute("photos", item.getItemPhoto());
        model.addAttribute("sizeOptions", item.getItemOption());
        return "item/itemView"; // 返回商品詳細頁面
    }

    @GetMapping("/cart")
    public String showCartPage() {
        return "cart/cart";
    }

    @GetMapping("/cart/checkout")
    public String showCheckoutPage() {
        return "cart/checkout";
    }

    @ResponseBody
    @GetMapping("/api/cart")
    public ResponseEntity<?> getCart(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入，請先登入！");
        }

        try {
            UserInfo userInfo = userInfoService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("無效的用戶ID"));

            Cart cart = cartService.getOrCreateCart(userInfo);
            CartDto cartDto = cartService.convertToCartDto(cart);
            return ResponseEntity.ok(cartDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法獲取購物車數據：" + e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping("/api/cart/items")
    public ResponseEntity<Map<String, String>> addItemToCart(@RequestBody AddCartItemRequest request, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return createErrorResponse("尚未登入，請先登入！", HttpStatus.UNAUTHORIZED);
        }

        try {
            UserInfo userInfo = userInfoService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("無效的用戶ID"));

            cartService.addItemToCart(userInfo, request.getOptionId(), request.getItemQuantity());
            return createSuccessResponse("商品已成功新增至購物車！");
        } catch (IllegalArgumentException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return createErrorResponse("加入購物車失敗：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ResponseBody
    @PostMapping("/api/cart/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest checkoutRequest, HttpSession session) {
        try {
            Integer currentUserId = (Integer) session.getAttribute("userId");
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("尚未登入，請先登入！");
            }

            Cart cart = cartService.getCartByBuyerId(currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("購物車不存在"));

            Long shippingMethodId = checkoutRequest.getShippingMethodId();
            ShippingMethod shippingMethod = shippingMethodService.getMethodById(shippingMethodId)
                    .orElseThrow(() -> new IllegalArgumentException("無效的物流方式"));

            // **保存物流資訊並關聯訂單**
            ShippingInfo shippingInfo = new ShippingInfo();
            shippingInfo.setShippingInfoRecipient(checkoutRequest.getShippingInfoRecipient());
            shippingInfo.setShippingInfoAddress(checkoutRequest.getShippingInfoAddress());
            shippingInfo.setShippingInfoStatus("Pending");
            shippingInfo.setShippingMethod(shippingMethod);

            // **創建訂單**
            Order order = orderService.createOrderFromCart(cart, checkoutRequest);

            // **關聯物流資訊與訂單**
            shippingInfo.setOrder(order);
            shippingInfoService.saveShippingInfo(shippingInfo);

            // **清空購物車**
            cartService.clearCart(cart.getBuyer());

            return ResponseEntity.ok(Map.of("orderId", order.getOrderId(), "redirectUrl", "/payment/" + order.getOrderId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("伺服器內部錯誤: " + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    public static class AddCartItemRequest {
        private int optionId;
        private int itemQuantity;

        public int getOptionId() {
            return optionId;
        }

        public void setOptionId(int optionId) {
            this.optionId = optionId;
        }

        public int getItemQuantity() {
            return itemQuantity;
        }

        public void setItemQuantity(int itemQuantity) {
            this.itemQuantity = itemQuantity;
        }
    }
}
