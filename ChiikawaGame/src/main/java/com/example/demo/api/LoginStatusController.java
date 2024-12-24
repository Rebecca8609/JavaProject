package com.example.demo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class LoginStatusController {
	
	@GetMapping("/api/check-login")
    public boolean checkLoginStatus(HttpSession session) {
		return session.getAttribute("user") != null;
	
	}
}