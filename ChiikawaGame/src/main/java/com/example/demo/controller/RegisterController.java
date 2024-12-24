package com.example.demo.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.example.demo.model.RegisterBean;

import com.example.demo.service.RegisterService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegisterController {
	
	@Autowired
	private RegisterService registerService;
	
	// 顯示註冊頁面，重定向邏輯。
    @GetMapping("/register")
    public String showRegisterPage(HttpSession session) {
    	// 檢查 session 中是否有 user 資料（已登入）。
    	if(session.getAttribute("user") != null) {
    		return "redirect:/memberCenter";// 可以根據需求修改重定向的頁面
    	}
        return "register";  // 返回register.html頁面
    }
	
 // 處理註冊表單
    @PostMapping("/register")
    public String register(@RequestBody RegisterBean user, Model model) {
        // 呼叫Service層的registerUser方法，返回錯誤訊息列表
        List<String> errors = registerService.registerUser(user);

        // 如果有錯誤，將錯誤訊息放入model並返回註冊頁面
        if(!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register";  // 若註冊失敗，返回註冊頁面並顯示錯誤
        }

        // 若註冊成功，重定向到登入頁面
        return "redirect:/login";
    }
}
