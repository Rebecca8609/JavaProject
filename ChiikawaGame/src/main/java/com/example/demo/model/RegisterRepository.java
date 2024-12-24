package com.example.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.RegisterBean;

@Repository
public interface RegisterRepository extends JpaRepository<RegisterBean, Integer>{
	
	// 根據電子郵件查詢是否已經存在該帳號
	boolean existsByUserEmail(String userEmail);
	
	// 根據電話號碼查詢是否已經存在該電話號碼
    boolean existsByUserTel(String userTel);

    // 根據身分證號碼查詢是否已經存在該身分證號碼
    boolean existsByUserIdNumber(String userIdNumber);
}