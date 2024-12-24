package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import com.example.demo.model.LoginBean;
import com.example.demo.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    // 顯示登入頁面，重定向邏輯。
    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
    	// 檢查 session 中是否有 user 資料，表示用戶已經登入
    	if(session.getAttribute("user") != null) {
    		// 如果已經登入，重定向到首頁（或其他頁面）
    		return "redirect:/memberCenter";// 可以根據需求修改重定向的頁面
    	}
    	
    	// 如果沒有登入，顯示登入頁面
    	return "login";  // 返回login.html頁面
    }

	
 // 用戶登入處理
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody LoginBean loginDetails, HttpSession session) {
        String userEmail = loginDetails.getUserEmail();
        String userPassword = loginDetails.getUserPassword();
    	
    	// 驗證登入
        LoginBean user = loginService.authenticate(userEmail, userPassword);
        
        // 返回給前端的簡化消息
        String messageToFrontend = "";
        
        if("success".equals(user.getStatus())) {
        	// 登入成功，將用戶信息放入 session
        	session.setAttribute("user", user);
        	messageToFrontend = "{\"status\":\"success\",\"message\":\"登入成功.\"}";
        	return ResponseEntity.status(HttpStatus.OK).body(messageToFrontend);
        }else {
        	// 根據後端的詳細錯誤訊息，決定簡化後的前端訊息
        	switch(user.getMessage()) {
        	case "帳號不存在":
        		messageToFrontend = "{\"status\":\"fail\",\"message\":\"帳號或密碼錯誤.\"}";
                break;
        	case "帳號未啟用":
                messageToFrontend = "{\"status\":\"fail\",\"message\":\"帳號未啟用.\"}";
                break;
            case "帳號或密碼錯誤":
                messageToFrontend = "{\"status\":\"fail\",\"message\":\"帳號或密碼錯誤.\"}";
                break;
            default:
                messageToFrontend = "{\"status\":\"fail\",\"message\":\"未知錯誤.\"}";
                break;
        	}
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageToFrontend);
        }
    }
    
 // 顯示會員中心頁面
    @GetMapping("/memberCenter")
    public String showMemberCenterPage() {
        return "MemberCenter";  // 返回 MemberCenter.html 頁面
    }
    
 // 獲取用戶資訊
    @GetMapping("/getUserInfo")
    @ResponseBody
    public String getUserInfo(HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        if (user != null) {
        	// 返回用戶信息的 JSON 格式
            return "{\"success\": true, \"username\": \"" + user.getUserName() + "\", \"userEmail\": \"" + user.getUserEmail() + "\"}";
        } else {
        	// 用戶未登入，返回失敗的 JSON 格式
            return "{\"success\": false, \"message\": \"User is not logged in.\"}";
        }
    }
    
    //登出
    @PostMapping("/logout")
	public String logout(HttpServletRequest request) {
		// 獲取當前 session，false 表示如果 session 不存在則不創建新的 session
		HttpSession session = request.getSession(false);
		if (session != null) {
            // 使 session 無效，這樣就登出了
            session.invalidate();
        }
		// 重定向到登入頁面
        return "redirect:/login"; // 重定向到登入頁面
	}
    
    
}



    
