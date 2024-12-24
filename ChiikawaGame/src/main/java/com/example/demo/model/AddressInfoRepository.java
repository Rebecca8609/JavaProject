package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.AddressInfo;

@Repository
public interface AddressInfoRepository extends JpaRepository<AddressInfo, Integer> {
    List<AddressInfo> findByUserId(Integer userId);
}