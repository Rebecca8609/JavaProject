package com.example.demo.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.LoginBean;

@Repository
public interface LoginRepository extends JpaRepository<LoginBean, Integer> {	
	
	// 根據 userEmail 查詢用戶
	LoginBean findByUserEmail(String userEmail);
}