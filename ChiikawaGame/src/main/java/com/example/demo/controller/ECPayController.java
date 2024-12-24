package com.example.demo.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.ECPayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j  // 加入這個註解就可以用log
@Controller
public class ECPayController {
	
    @Autowired
    ECPayService ecpayService;
    

    // http://localhost:8080/ecpay/showECPay
    @GetMapping("/ecpay/showECPay")
    public String showECPay() {
        return "ecpay/ecpayTest";
    }

    // http://localhost:8080/ecpay/submitOrder
    @PostMapping("/ecpay/submitOrder")
    @ResponseBody
    public String submitOrder(@RequestBody Map<String, Object> payload) {
        if (payload == null || !payload.containsKey("orderId")) {
            throw new IllegalArgumentException("Invalid payload: orderId is missing");
        }

        Object orderIdObj = payload.get("orderId");
        Long orderId;
        try {
            orderId = Long.parseLong(orderIdObj.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid orderId format", e);
        }

        return ecpayService.ecpayCheckout(orderId);
    }

    @PostMapping("/callback")
    @ResponseBody
    public String handleECPayCallback(HttpServletRequest request) {
        // 將 form-data 參數提取為 Map
        Map<String, String> requestData = extractParameters(request);
        
        // 提取回傳的資料
        String receivedCheckMacValue = requestData.get("CheckMacValue");
        String merchantTradeNo = requestData.get("MerchantTradeNo");
        String RtnCode = requestData.get("RtnCode");
        
        if (receivedCheckMacValue == null || merchantTradeNo == null) {
            log.error("Missing CheckMacValue");
            System.out.println("Missing CheckMacValue");
            return "0|Error";
        }
        
        System.out.println("requestData:"+requestData);
        System.out.println("receivedCheckMacValue:"+receivedCheckMacValue);
        System.out.println("RtnCode 付款結果(1=成功/10300066=付款結果待確認中):"+RtnCode);      
        
        //不驗證(請把下面if關起來)
        String orderIdStr = requestData.get("CustomField1"); // 取得訂單 ID
        Long orderId = Long.parseLong(orderIdStr); // 將 String 轉為 Long
        
        //若付款成功，則更新付款狀態
        ecpayService.updatePaymentStatus(orderId);      	
        
        //回傳結果給綠界
        return "1|OK";
        
    }

    /**
     * 將 HttpServletRequest 的參數提取為 Map
     *
     * @param request HttpServletRequest
     * @return 包含所有請求參數的 Map
     */
    private Map<String, String> extractParameters(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }

        return params;
    }
    
}
