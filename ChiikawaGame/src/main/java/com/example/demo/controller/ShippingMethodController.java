package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.ShippingMethod;
import com.example.demo.service.ShippingMethodService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/shipping-method")
public class ShippingMethodController {

    @Autowired
    private ShippingMethodService shippingMethodService;

    @GetMapping("/management")
    public String shippingMethodManagementPage() {
        return "shipment/adminshippingMethod"; // 對應模板文件
    }

    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ShippingMethod> getMethodById(@PathVariable Long id) {
        Optional<ShippingMethod> methodOpt = shippingMethodService.getMethodById(id);
        return methodOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<ShippingMethod>> getAllMethods() {
        // 使用注入的 Service 實例來調用方法
        List<ShippingMethod> methods = shippingMethodService.getAllMethods();
        return ResponseEntity.ok(methods);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ShippingMethod> updateMethod(@PathVariable Long id, @RequestBody ShippingMethod method) {
        Optional<ShippingMethod> existingMethodOpt = shippingMethodService.getMethodById(id);
        if (existingMethodOpt.isPresent()) {
            ShippingMethod existingMethod = existingMethodOpt.get();
            existingMethod.setMethodName(method.getMethodName());
            existingMethod.setDescription(method.getDescription());

            ShippingMethod updatedMethod = shippingMethodService.updateShippingMethod(existingMethod);
            return ResponseEntity.ok(updatedMethod);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    @PostMapping
    @ResponseBody
    public ResponseEntity<ShippingMethod> createMethod(@RequestBody ShippingMethod method) {
        // 使用注入的 Service 實例來調用方法
        ShippingMethod newMethod = shippingMethodService.createShippingMethod(method);
        return ResponseEntity.ok(newMethod);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMethod(@PathVariable Long id) {
        // 使用注入的 Service 實例來調用方法
        boolean deleted = shippingMethodService.deleteMethod(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}

