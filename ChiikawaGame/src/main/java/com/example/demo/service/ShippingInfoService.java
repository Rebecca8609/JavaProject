package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ShippingInfoRequest;
import com.example.demo.model.Order;
import com.example.demo.model.OrderRepository;
import com.example.demo.model.ShippingInfo;
import com.example.demo.model.ShippingInfoRepository;
import com.example.demo.model.ShippingMethod;
import com.example.demo.model.ShippingMethodRepository;

import java.io.IOException;
import java.util.*;

@Service
public class ShippingInfoService {

    @Autowired
    private ShippingInfoRepository shippingInfoRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    /**
     * 更新物流信息，保證訂單只能有一個物流信息，支持圖片上傳及設定物流方式
     */
    public boolean updateShippingInfo(Long shippingInfoId, ShippingInfo updatedInfo, MultipartFile imageFile) {
        ShippingInfo existingInfo = shippingInfoRepository.findById(shippingInfoId)
                .orElseThrow(() -> new RuntimeException("找不到指定的物流信息"));


        updateBasicShippingInfo(existingInfo, updatedInfo);

        if (updatedInfo.getShippingMethod() != null && updatedInfo.getShippingMethod().getShippingMethodId() != null) {
            ShippingMethod method = findShippingMethodById(updatedInfo.getShippingMethod().getShippingMethodId());
            existingInfo.setShippingMethod(method);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            existingInfo.setShippingInfoImage(uploadImage(imageFile));
        }

        shippingInfoRepository.save(existingInfo);
        return true;
    }

    private void validateUniqueShippingInfoForOrder(Long orderId, Long shippingInfoId) {
        Optional<ShippingInfo> existingShippingInfo = shippingInfoRepository.findByOrderOrderId(orderId);
        if (existingShippingInfo.isPresent() && !existingShippingInfo.get().getShippingInfoId().equals(shippingInfoId)) {
            throw new IllegalArgumentException("該訂單已經存在物流信息，無法重複設置。");
        }
    }

    private void updateBasicShippingInfo(ShippingInfo existingInfo, ShippingInfo updatedInfo) {
        existingInfo.setShippingInfoRecipient(updatedInfo.getShippingInfoRecipient());
        existingInfo.setShippingInfoAddress(updatedInfo.getShippingInfoAddress());
        existingInfo.setShippingInfoStatus(updatedInfo.getShippingInfoStatus());
        existingInfo.setShippingInfoTrackingNumber(updatedInfo.getShippingInfoTrackingNumber());
    }

    private byte[] uploadImage(MultipartFile imageFile) {
        if (imageFile.getContentType() == null || !imageFile.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("只支持圖片檔案格式");
        }
        try {
            return imageFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("圖片上傳失敗: " + e.getMessage(), e);
        }
    }


    private ShippingMethod findShippingMethodById(Long methodId) {
        return shippingMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("指定的物流方式不存在"));
    }

    /**
     * 保存物流信息（直接使用 ShippingInfo 對象）
     */
    public ShippingInfo saveShippingInfo(ShippingInfo shippingInfo) {
        validateOrderAssociation(shippingInfo.getOrder());
        return shippingInfoRepository.save(shippingInfo);
    }

    /**
     * 保存物流信息（基於 DTO 與用戶數據）
     */
    public ShippingInfo saveShippingInfoFromRequest(ShippingInfoRequest request, Long orderId) {
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setShippingInfoRecipient(request.getRecipientName());
        shippingInfo.setShippingInfoAddress(request.getAddress());
        shippingInfo.setShippingInfoStatus("Pending"); // 初始物流狀態

        ShippingMethod shippingMethod = findShippingMethodById(request.getShippingMethodId());
        shippingInfo.setShippingMethod(shippingMethod);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("找不到對應的訂單"));
        shippingInfo.setOrder(order);

        return shippingInfoRepository.save(shippingInfo);
    }

    private void validateOrderAssociation(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("物流信息必須關聯到一個訂單");
        }
    }

    /**
     * 根據物流追蹤號查詢物流信息
     */
    public List<ShippingInfo> getShippingInfoByAllTrackingNumber(String trackingNumber) {
        return shippingInfoRepository.findByShippingInfoTrackingNumber(trackingNumber);
    }
    
    public Optional<ShippingInfo> getShippingInfoByTrackingNumber(String trackingNumber) {
        List<ShippingInfo> resultList = shippingInfoRepository.findByShippingInfoTrackingNumber(trackingNumber);
        return resultList.stream().findFirst(); // 回傳第一筆 wrapped in Optional
    }



    /**
     * 根據 ID 獲取物流信息
     */
    public Optional<ShippingInfo> getShippingInfoById(Long shippingInfoId) {
        return shippingInfoRepository.findById(shippingInfoId);
    }

    /**
     * 查詢訂單與物流信息的關聯數據
     */
    public List<Map<String, Object>> getOrderShippingInfo(Long orderId, String shippingInfoStatus, String trackingNumber) {
        Specification<ShippingInfo> spec = createShippingInfoSpecification(orderId, shippingInfoStatus, trackingNumber);
        
        List<ShippingInfo> shippingInfos = shippingInfoRepository.findAll(spec);

        List<Map<String, Object>> result = new ArrayList<>();
        for (ShippingInfo info : shippingInfos) {

            // 1. 準備 shippingInfoMap
            Map<String, Object> shippingInfoMap = new HashMap<>();
            shippingInfoMap.put("shippingInfoId", info.getShippingInfoId());
            shippingInfoMap.put("shippingInfoRecipient", info.getShippingInfoRecipient());
            shippingInfoMap.put("shippingInfoAddress", info.getShippingInfoAddress());
            shippingInfoMap.put("shippingInfoStatus", info.getShippingInfoStatus());
            shippingInfoMap.put("shippingInfoTrackingNumber", info.getShippingInfoTrackingNumber());

            // 如果有圖片，轉成 Base64 放進去
            if (info.getShippingInfoImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(info.getShippingInfoImage());
                shippingInfoMap.put("shippingInfoImage", base64Image);
            }

            // 2. 準備 orderMap
            Map<String, Object> orderMap = new HashMap<>();
            if (info.getOrder() != null) {
                orderMap.put("orderId", info.getOrder().getOrderId());
                // 若想多傳其他欄位，如 order 的日期或其他資訊，也可放進來
            }

            // 3. 準備 shippingMethodMap
            Map<String, Object> shippingMethodMap = new HashMap<>();
            if (info.getShippingMethod() != null) {
                shippingMethodMap.put("shippingMethodId", info.getShippingMethod().getShippingMethodId());
                shippingMethodMap.put("methodName", info.getShippingMethod().getMethodName());
            }

            // 4. 把三個 Map 合併到一個 combined Map
            Map<String, Object> combined = new HashMap<>();
            combined.put("shippingInfo", shippingInfoMap);
            combined.put("order", orderMap);
            combined.put("shippingMethod", shippingMethodMap);

            // 5. 放進 result
            result.add(combined);
        }
        return result;
    }

    
 // public方法，給Controller呼叫
    public Map<String, Object> getShippingInfoMapById(Long shippingInfoId) {
        ShippingInfo info = shippingInfoRepository.findById(shippingInfoId)
            .orElseThrow(() -> new RuntimeException("找不到指定的物流信息 ID=" + shippingInfoId));
        // 在 Service 內部呼叫 private toShippingInfoMap(...)
        return toShippingInfoMap(info);
    }

    
    
    /**
     * 將單筆 ShippingInfo 打包成 Map（含 orderId, shippingInfoXX, shippingMethod等）
     */
    private Map<String, Object> toShippingInfoMap(ShippingInfo info) {
        Map<String, Object> shippingInfoMap = new HashMap<>();
        shippingInfoMap.put("shippingInfoId", info.getShippingInfoId());
        shippingInfoMap.put("shippingInfoRecipient", info.getShippingInfoRecipient());
        shippingInfoMap.put("shippingInfoAddress", info.getShippingInfoAddress());
        shippingInfoMap.put("shippingInfoStatus", info.getShippingInfoStatus());
        shippingInfoMap.put("shippingInfoTrackingNumber", info.getShippingInfoTrackingNumber());

        if (info.getShippingInfoImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(info.getShippingInfoImage());
            shippingInfoMap.put("shippingInfoImage", base64Image);
        }

        Map<String, Object> orderMap = new HashMap<>();
        if (info.getOrder() != null) {
            orderMap.put("orderId", info.getOrder().getOrderId());
        }

        Map<String, Object> shippingMethodMap = new HashMap<>();
        if (info.getShippingMethod() != null) {
            shippingMethodMap.put("shippingMethodId", info.getShippingMethod().getShippingMethodId());
            shippingMethodMap.put("methodName", info.getShippingMethod().getMethodName());
        }

        // 最終的combined
        Map<String, Object> combined = new HashMap<>();
        combined.put("shippingInfo", shippingInfoMap);
        combined.put("order", orderMap);
        combined.put("shippingMethod", shippingMethodMap);

        return combined;
    }

    
    
    private Specification<ShippingInfo> createShippingInfoSpecification(Long orderId, String status, String trackingNumber) {
        Specification<ShippingInfo> spec = Specification.where(null);

        if (orderId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.join("order").get("orderId"), orderId));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("shippingInfoStatus"), status));
        }
        if (trackingNumber != null && !trackingNumber.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("shippingInfoTrackingNumber"), trackingNumber));
        }

        return spec;
    }
}

