package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRepository;
import java.util.List;

@Controller
public class UserInfoController {

	@Autowired
	private UserInfoRepository userInfoRepository;

	// 顯示會員資料總覽頁面 (http://localhost:8080/userInfo)
	@GetMapping("/userInfo")
	public String showUserSaInfo() {
		return "userInfo/userInfo";
	}

	// 取得所有會員資料，回傳 JSON 格式 (http://localhost:8080/userInfo/json)
	@GetMapping("/userInfo/json")
	@ResponseBody
	public List<UserInfo> getUserInfoJson() {
	    List<UserInfo> users = userInfoRepository.findAll();
	    System.out.println("Returned Users: " + users); // 調試訊息
	    return users;
	}
	
	 private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


	 // 新增會員資料 (http://localhost:8080/userInfo/add)
	    @PostMapping("/userInfo/add")
	    @ResponseBody
	    public ResponseEntity<String> addUserInfo(@RequestBody UserInfo newUser) {
	        try {
	            // 將密碼加密後存入資料庫
	            newUser.setUserPassword(passwordEncoder.encode(newUser.getUserPassword()));
	            userInfoRepository.save(newUser);
	            return ResponseEntity.ok("新增成功");
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("新增失敗：" + e.getMessage());
	        }
	    }

	// 更新會員資料 (http://localhost:8080/userInfo/update)
	@PostMapping("/userInfo/update")
	@ResponseBody
	public ResponseEntity<String> updateUserInfo(@RequestBody UserInfo updatedUser) {
		try {
			// 根據 ID 查找現有的會員資料
			UserInfo user = userInfoRepository.findById(updatedUser.getUserId())
					.orElseThrow(() -> new RuntimeException("會員資料不存在"));

			// 正確對應欄位更新
			user.setUserName(updatedUser.getUserName());
			user.setUserEmail(updatedUser.getUserEmail());
			user.setUserPassword(updatedUser.getUserPassword());
			user.setUserTel(updatedUser.getUserTel());
			user.setUserStatus(updatedUser.getUserStatus());
			user.setUserBirthday(updatedUser.getUserBirthday()); // 新增此行

			// 儲存更新後的會員資料
			userInfoRepository.save(user);
			return ResponseEntity.ok("更新成功");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("更新失敗：" + e.getMessage());
		}
	}
}
