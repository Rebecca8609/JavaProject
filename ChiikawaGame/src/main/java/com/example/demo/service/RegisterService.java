package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.model.RegisterBean;
import com.example.demo.model.RegisterRepository;


@Service
public class RegisterService {

	@Autowired
	private RegisterRepository registerRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;// 注入 PasswordEncoder
	
	// 密碼驗證正則表達式：最少8個字元，最多20個字元，必須包含至少一個大寫字母、一個小寫字母和一個數字
	private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
	
	public List<String> registerUser(RegisterBean user){
		List<String> errors = new ArrayList<>();
	
	
	 
		// 資料驗證（簡單檢查空值）
		if(user.getUserEmail() == null || user.getUserEmail().isEmpty() ||
				user.getUserEmail() == null || user.getUserEmail().isEmpty() ||
				user.getUserPassword() == null || user.getUserPassword().isEmpty() ||
				user.getUserName() == null || user.getUserName().isEmpty() ||
				user.getUserTel() == null || user.getUserTel().isEmpty() ||
				user.getUserIdNumber() == null || user.getUserIdNumber().isEmpty() ||
				user.getUserBirthday() ==null || user.getUserBirthday().isEmpty()) {
				errors.add("資料有空值，無法註冊。");
				
		}
		System.out.println("資料空值檢查完成。");
		
		// 檢查帳號是否已存在
		System.out.println("檢查帳號是否已存在...");
		if(registerRepository.existsByUserEmail(user.getUserEmail())) {
			errors.add("該帳號已被註冊(偵錯!!)");
		}
		System.out.println("帳號檢查完成。");
		
		// 檢查電話是否已存在
		System.out.println("檢查電話是否已存在...");
        if (registerRepository.existsByUserTel(user.getUserTel())) {
            errors.add("該電話號碼已被註冊(偵錯!!)");
        }
        System.out.println("電話檢查完成。");
        
     // 檢查身分證是否已存在
        System.out.println("檢查身分證是否已存在...");
        if (registerRepository.existsByUserIdNumber(user.getUserIdNumber())) {
            errors.add("該身分證號碼已被註冊(偵錯!!)");
        }
        System.out.println("身分證檢查完成。");
		
		// 密碼檢查：正則表達式驗證密碼是否符合規定
        System.out.println("檢查密碼是否符合規定...");
		if(!user.getUserPassword().matches(PASSWORD_PATTERN)) {
			errors.add("密碼不符合規定，必須包含至少一個大寫字母、一個小寫字母和一個數字，長度為8到20個字元");
		}
		System.out.println("密碼檢查完成。");
		
		// 如果有錯誤，返回錯誤訊息列表
		if(!errors.isEmpty()) {
			return errors;
		}
		
		// 如果資料正確，對密碼進行加密
		System.out.println("加密密碼...");
		String encryptedPassword = passwordEncoder.encode(user.getUserPassword());
		user.setUserPassword(encryptedPassword);// 將加密後的密碼設置回 user 對象
		
		// 設定 userStatus 為 1（已啟用）
        user.setUserStatus(1); // 預設帳號為啟用狀態
		
		// 使用 save() 方法將註冊資料儲存到資料庫
		registerRepository.save(user);
		// 註冊成功後打印訊息
        System.out.println("註冊成功，用戶資料已保存到資料庫！");
		
        // 返回註冊成功的訊息
        return new ArrayList<>();// 空列表代表成功
				
				
	}
	









}