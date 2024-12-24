package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderRepository;
import com.example.demo.model.UserInfo;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserInfoService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 顯示訂單管理頁面（管理員）
     */
    @GetMapping("/order-management")
    public String adminOrderManagement() {
        return "order/adminOrderManagement";
    }

    /**
     * 獲取訂單列表（支持篩選條件和分頁）
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(required = false) Long buyerId,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        var result = orderService.getOrdersByFilters(
                buyerId, sellerId, orderId, orderStatus, startDate, endDate, PageRequest.of(page - 1, size));

        var orders = result.getContent().stream().map(order -> Map.of(
                "orderId", order.getOrderId(),
                "buyerId", order.getBuyer() != null ? order.getBuyer().getUserId() : null,
                "sellerId", order.getOrderItems() != null && !order.getOrderItems().isEmpty()
                        ? order.getOrderItems().get(0).getSeller().getUserId()
                        : null,
                "orderTotal", order.getOrderTotal(),
                "paymentStatus", order.getPaymentStatus(),
                "shippingStatus", order.getShippingStatus(),
                "orderStatus", order.getOrderStatus()
        )).toList();

        return ResponseEntity.ok(Map.of(
                "content", orders,
                "totalPages", result.getTotalPages(),
                "currentPage", result.getNumber() + 1
        ));
    }


    /**
     * 根據訂單ID獲取訂單詳情
     */
    @GetMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        System.out.println("Fetching order details for orderId: " + orderId);

        Optional<Order> order = orderRepository.findByIdWithItems(orderId);

        if (order.isPresent()) {
            Order foundOrder = order.get();
            System.out.println("Order found:");
            System.out.println("Order ID: " + foundOrder.getOrderId());
            System.out.println("Order Total: " + foundOrder.getOrderTotal());
            System.out.println("Order Status: " + foundOrder.getOrderStatus());

            // 構建返回的 JSON 結構
            Map<String, Object> response = Map.of(
                "orderId", foundOrder.getOrderId(),
                "orderDate", foundOrder.getOrderDate(),
                "orderTotal", foundOrder.getOrderTotal(),
                "orderStatus", foundOrder.getOrderStatus(),
                "paymentStatus", foundOrder.getPaymentStatus(),
                "shippingStatus", foundOrder.getShippingStatus(),
                "buyer", Map.of(
                    "buyerId", foundOrder.getBuyer() != null ? foundOrder.getBuyer().getUserId() : null,
                    "buyerName", foundOrder.getBuyer() != null ? foundOrder.getBuyer().getUserName() : "未知"
                ),
                "orderItems", foundOrder.getOrderItems().stream().map(item -> Map.of(
                    "sellerId", item.getSeller() != null ? item.getSeller().getUserId() : null,
                    "itemId", item.getItem() != null ? item.getItem().getItemId() : null,
                    "itemName", item.getItem() != null ? item.getItem().getItemName() : "未知",
                    "itemQuantity", item.getItemQuantity(),
                    "itemPrice", item.getItemPrice(),
                    "itemSize", item.getItemSize() != null ? item.getItemSize().getOptionName() : "無"
                )).toList()
            );

            return ResponseEntity.ok(response);
        } else {
            System.out.println("No order found with orderId: " + orderId);
            return ResponseEntity.notFound().build();
        }
    }



    /**
     * 更新訂單詳情（支持付款狀態、物流狀態和訂單狀態）
     */
    @PutMapping("/{orderId}")
    @ResponseBody
    public ResponseEntity<?> updateOrderDetails(@PathVariable Long orderId, @RequestBody Map<String, Object> updatedFields) {
        try {
            boolean updated = orderService.updateOrderDetails(
                    orderId,
                    (String) updatedFields.get("paymentStatus"),
                    (String) updatedFields.get("shippingStatus"),
                    (String) updatedFields.get("orderStatus")
            );
            return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 建立新訂單
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Order> createOrder(@RequestParam Integer buyerId, @RequestBody Order order) {
        UserInfo buyer = userInfoService.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("無效的買家ID"));
        order.setBuyer(buyer);

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getItem() == null || orderItem.getItem().getUserInfo() == null) {
                throw new IllegalArgumentException("商品信息或賣家信息無效");
            }
            orderItem.setSeller(orderItem.getItem().getUserInfo());
            orderItem.setOrder(order);
        }

        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    /**
     * 棄單功能
     */
    @PostMapping("/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            boolean canceled = orderService.cancelOrder(orderId);
            return canceled ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
