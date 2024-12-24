package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.model.LoginBean;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		// CSRF 設定 (方便開發期間測試，可視需求停用)
        .csrf(csrf -> csrf.disable())
        
		// OAuth2 登入設定，這是 Spring Security 內建的 OAuth2 登入功能
        .oauth2Login(oauth2 -> oauth2
        		.loginPage("/login")// 自定義登入頁面
        		.successHandler((request, response, authentication) -> {
        			// 取得 Google OAuth2 登入後的用戶資訊
        			OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        			String email = oauth2User.getAttribute("email");
        			String name = oauth2User.getAttribute("name");
        			
        			// 將用戶資訊包裝成 LoginBean
        			LoginBean userBean = new LoginBean();
        			userBean.setUserEmail(email);
        			userBean.setUserName(name);
        			userBean.setStatus("success");
        			
        			// 存入 Session
        			HttpSession session = request.getSession();
        			session.setAttribute("user", userBean);
        			
        			// 重定向到會員中心
        			response.sendRedirect("/memberCenter");
        			
        			
        			
        			
        			
            })
        )
     
     // 登出設定
        .logout(logout -> logout
        	.logoutUrl("/logout")
            .logoutSuccessUrl("/login")  // 登出後跳轉到登入頁面
            .invalidateHttpSession(true)  // 清除 Session
        );
    
    return http.build();
	}

}
