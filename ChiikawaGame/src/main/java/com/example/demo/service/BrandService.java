package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Brand;
import com.example.demo.model.BrandRepository;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepo; // 引入 CategoryRepository
    
    public List<Brand> findAll() {
        return brandRepo.findAll();
    }



}