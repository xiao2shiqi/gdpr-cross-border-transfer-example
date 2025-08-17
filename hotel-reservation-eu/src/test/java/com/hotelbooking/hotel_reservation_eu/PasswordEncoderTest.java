package com.hotelbooking.hotel_reservation_eu;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器测试类
 * 用于生成和验证BCrypt密码哈希
 */
public class PasswordEncoderTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void generatePasswordHash() {
        String plainPassword = "admin12345!";
        String encodedPassword = passwordEncoder.encode(plainPassword);
        
        System.out.println("原始密码: " + plainPassword);
        System.out.println("BCrypt哈希: " + encodedPassword);
        
        // 验证密码匹配
        boolean matches = passwordEncoder.matches(plainPassword, encodedPassword);
        System.out.println("密码验证结果: " + matches);
    }

    @Test
    public void testExistingHash() {
        String plainPassword = "password123";
        String existingHash = "$2a$10$X3r0LQO8g5LfxC8VW0RfzuPmOTXfX0HvEp3xQDQEFt5Y9JL0sYFwK";
        
        boolean matches = passwordEncoder.matches(plainPassword, existingHash);
        System.out.println("现有哈希验证结果: " + matches);
        System.out.println("原始密码: " + plainPassword);
        System.out.println("现有哈希: " + existingHash);
    }

    @Test
    public void generateMultipleHashes() {
        String[] passwords = {"password123", "admin123", "test123"};
        
        for (String password : passwords) {
            String hash = passwordEncoder.encode(password);
            System.out.println("密码: " + password + " -> 哈希: " + hash);
        }
    }
} 