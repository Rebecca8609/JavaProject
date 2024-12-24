package com.example.demo.service;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Order;
import com.example.demo.model.OrderRepository;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import jakarta.transaction.Transactional;

@Service
public class ECPayService {	
	
    @Autowired
    private OrderRepository orderRepository;
    

    // 用於暫存 checkMacValue 的 Map
    private final Map<String, String> orderCheckMacValues = new ConcurrentHashMap<>();

    public String ecpayCheckout(Long orderId) {
    	//找到訂單
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        //找到訂單的金額
        String orderTotal = String.valueOf(
                order.getOrderTotal().setScale(0, RoundingMode.HALF_UP).intValue()
            );
        
        // 生成訂單編號
        String orderIDuuId = generateOrderNumber(order.getOrderId().toString());
        
        // 將 CheckMacValue 加入支付參數
        AllInOne all = new AllInOne("");
        AioCheckOutALL obj = new AioCheckOutALL();
        obj.setMerchantID("3002607");  // 請替換為您的商店代號
        obj.setMerchantTradeNo(orderIDuuId); //假訂單編號(綠界格式)
        obj.setMerchantTradeDate(getCurrentFormattedDateTime());
        obj.setTotalAmount(orderTotal);
        obj.setTradeDesc("訂單交易");
        obj.setItemName("商品名稱ABC");
        obj.setReturnURL("https://93ff-114-27-2-3.ngrok-free.app/callback");
        obj.setClientBackURL("http://localhost:8080/ecpay/showECPay");
        obj.setCustomField1(orderId.toString()); //真訂單編號
        obj.setNeedExtraPaidInfo("Y");
        obj.setIgnorePayment("WebATM#ATM#BNPL#ApplePay#TWQR");

        return all.aioCheckOut(obj, null);
    }
    
    // 產生MerchantTradeNo廠商交易編號 要用[訂單編號]＋[一組n碼]不重覆編號生成
    private String generateOrderNumber(String orderId) {
        return ("AA" + orderId + UUID.randomUUID().toString().replaceAll("-", ""))
            .substring(0, 20);
    }

    // 取得當前時間
    private String getCurrentFormattedDateTime() {
        return LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
    
    // 取得特定訂單的 checkMacValue
    public String getStoredCheckMacValue(String merchantTradeNo) {
        return orderCheckMacValues.get(merchantTradeNo);
    }

    //更新訂單付款狀態
    @Transactional
    public String updatePaymentStatus(Long orderId) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (!orderOptional.isPresent()) {
                return "Order not found with ID " + orderId;
            }

            Order order = orderOptional.get();
            if (!"Paid".equals(order.getPaymentStatus())) {
                order.setPaymentStatus("Paid");
                orderRepository.save(order);
                System.out.println("Payment status updated successfully for order ID " + orderId);
                return "Payment status updated to 'Paid' for order ID " + orderId;
            } else {
                System.out.println("Order already paid for orderID: " + orderId);
                return "Order already paid for ID " + orderId;
            }
        } catch (Exception e) {
            System.err.println("Failed to update payment status for order ID " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            return "Error occurred while updating payment status for order ID " + orderId;
        }
    } 
    
    
}
