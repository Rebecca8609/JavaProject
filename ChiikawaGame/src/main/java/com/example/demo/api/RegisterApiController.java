package com.example.demo.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RegisterBean;
import com.example.demo.service.RegisterService;

//這個控制器負責處理註冊請求，進行後端的驗證，並返回 JSON 格式的錯誤訊息或成功訊息
@RestController
@RequestMapping("/api")
public class RegisterApiController {
	
	@Autowired
	private RegisterService registerService;
	
	//註冊API
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterBean registerBean){
		System.out.println("API請求進來了");
		System.out.println("資料: " + registerBean);
		
		// 呼叫 Service 層進行註冊處理
		List<String> errors = registerService.registerUser(registerBean);
		
		// 檢查是否有錯誤
		if(!errors.isEmpty()) {
			// 如果有錯誤，返回400和錯誤訊息
			System.out.println("錯誤清單: " + errors);
			return ResponseEntity.badRequest().body(
	                Map.of("status", "fail", "errors", errors)
	            );
		}
			// 註冊成功，返回200和成功訊息
		return ResponseEntity.ok(Map.of("status", "success", "message", "註冊成功")
		);
	}
}

