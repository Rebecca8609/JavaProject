package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {
	
	@Bean  // 宣告這是 Spring 容器管理的 Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // 返回 BCryptPasswordEncoder 實例
    }
}
