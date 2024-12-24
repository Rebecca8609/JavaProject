package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.ShippingMethod;
import com.example.demo.model.ShippingMethodRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ShippingMethodService {

    @Autowired
    private ShippingMethodRepository shippingMethodRepository;

    /**
     * 創建物流方式
     *
     * @param method 物流方式實體
     * @return 保存後的物流方式
     */
    @Transactional
    public ShippingMethod createShippingMethod(ShippingMethod method) {
        if (method == null || method.getMethodName() == null || method.getMethodName().isEmpty()) {
            throw new IllegalArgumentException("物流方式名稱不能為空");
        }
        return shippingMethodRepository.save(method);
    }

    /**
     * 更新物流方式
     *
     * @param method 更新的物流方式
     * @return 更新後的物流方式
     */
    @Transactional
    public ShippingMethod updateShippingMethod(ShippingMethod method) {
        if (method == null || method.getShippingMethodId() == null) {
            throw new IllegalArgumentException("物流方式 ID 不能為空");
        }

        if (!shippingMethodRepository.existsById(method.getShippingMethodId())) {
            throw new IllegalArgumentException("指定的物流方式不存在");
        }

        return shippingMethodRepository.save(method);
    }

    /**
     * 獲取所有物流方式
     *
     * @return 物流方式列表
     */
    @Transactional(readOnly = true)
    public List<ShippingMethod> getAllMethods() {
        return shippingMethodRepository.findAll();
    }

    /**
     * 根據 ID 獲取物流方式
     *
     * @param id 物流方式 ID
     * @return 物流方式（可選）
     */
    @Transactional(readOnly = true)
    public Optional<ShippingMethod> getMethodById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("物流方式 ID 不能為空");
        }
        return shippingMethodRepository.findById(id);
    }


    /**
     * 刪除物流方式
     *
     * @param id 物流方式 ID
     * @return 如果刪除成功返回 true，否則返回 false
     */
    @Transactional
    public boolean deleteMethod(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("物流方式 ID 不能為空");
        }

        if (shippingMethodRepository.existsById(id)) {
            shippingMethodRepository.deleteById(id);
            return true;
        } else {
            throw new IllegalArgumentException("指定的物流方式不存在");
        }
    }
}
