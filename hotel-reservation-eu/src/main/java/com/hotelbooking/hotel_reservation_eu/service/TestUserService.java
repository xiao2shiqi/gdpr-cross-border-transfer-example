package com.hotelbooking.hotel_reservation_eu.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.hotelbooking.hotel_reservation_eu.dto.RegisterRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * 测试用户服务
 * 用于从test_user.json文件中读取测试用户数据
 */
@Slf4j
@Service
public class TestUserService {

    private final UserService userService;
    private final Random random;

    public TestUserService(UserService userService) {
        this.userService = userService;
        this.random = new Random();
    }

    /**
     * 获取随机测试用户信息
     * 确保返回的用户在当前系统中不存在
     * 
     * @return 随机测试用户信息，如果没有可用的测试用户则返回null
     */
    public RegisterRequestDto getRandomTestUser() {
        try {
            List<TestUser> testUsers = loadTestUsers();
            if (testUsers == null || testUsers.isEmpty()) {
                log.warn("测试用户数据为空或无法加载");
                return null;
            }

            log.info("成功加载测试用户数据，共 {} 个用户", testUsers.size());
            
            // 随机选择测试用户，直到找到一个不存在的用户
            List<TestUser> shuffledUsers = new java.util.ArrayList<>(testUsers);
            java.util.Collections.shuffle(shuffledUsers);

            for (TestUser testUser : shuffledUsers) {
                // 检查用户名和邮箱是否已存在
                if (!userService.isUsernameExists(testUser.getUsername()) && 
                    !userService.isEmailExists(testUser.getEmail())) {
                    
                    log.info("找到可用的测试用户: {}", testUser.getUsername());
                    log.debug("测试用户详细信息: username={}, email={}, firstName={}, lastName={}, phoneNumber={}, dateOfBirth={}", 
                        testUser.getUsername(), testUser.getEmail(), testUser.getFirstName(), 
                        testUser.getLastName(), testUser.getPhoneNumber(), testUser.getDateOfBirth());
                    
                    RegisterRequestDto dto = convertToRegisterRequestDto(testUser);
                    log.debug("转换后的DTO: confirmPassword={}, firstName={}, lastName={}, phoneNumber={}, dateOfBirth={}", 
                        dto.getConfirmPassword(), dto.getFirstName(), dto.getLastName(), 
                        dto.getPhoneNumber(), dto.getDateOfBirth());
                    
                    return dto;
                }
            }

            log.warn("所有测试用户都已存在于系统中");
            return null;

        } catch (Exception e) {
            log.error("获取随机测试用户时发生错误", e);
            return null;
        }
    }

    /**
     * 加载测试用户数据
     */
    private List<TestUser> loadTestUsers() throws IOException {
        try {
            // 尝试从classpath加载（如果文件在resources目录下）
            ClassPathResource resource = new ClassPathResource("test_user.json");
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    log.debug("从classpath加载test_user.json成功，内容长度: {}", jsonContent.length());
                    return JSON.parseObject(jsonContent, new TypeReference<List<TestUser>>() {});
                }
            }
        } catch (Exception e) {
            log.debug("无法从classpath加载test_user.json: {}", e.getMessage());
        }

        // 如果classpath中没有，尝试从根目录加载
        try {
            java.nio.file.Path rootPath = java.nio.file.Paths.get("test_user.json");
            if (java.nio.file.Files.exists(rootPath)) {
                String jsonContent = java.nio.file.Files.readString(rootPath, StandardCharsets.UTF_8);
                log.debug("从根目录加载test_user.json成功，内容长度: {}", jsonContent.length());
                return JSON.parseObject(jsonContent, new TypeReference<List<TestUser>>() {});
            }
        } catch (Exception e) {
            log.debug("无法从根目录加载test_user.json: {}", e.getMessage());
        }

        log.error("无法找到或加载test_user.json文件");
        return null;
    }

    /**
     * 将TestUser转换为RegisterRequestDto
     */
    private RegisterRequestDto convertToRegisterRequestDto(TestUser testUser) {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setUsername(testUser.getUsername());
        dto.setEmail(testUser.getEmail());
        dto.setPassword(testUser.getPassword());
        dto.setConfirmPassword(testUser.getConfirmPassword());
        dto.setFirstName(testUser.getFirstName());
        dto.setLastName(testUser.getLastName());
        dto.setPhoneNumber(testUser.getPhoneNumber());
        dto.setDateOfBirth(parseDate(testUser.getDateOfBirth()));
        dto.setNationality(testUser.getNationality());
        dto.setAddress(testUser.getAddress());
        
        // 设置GDPR同意选项
        dto.setGdprProcessing(true);  // 必需同意
        dto.setGdprMarketing(true);   // 可选同意
        dto.setGdprAnalytics(true);   // 可选同意
        
        return dto;
    }
    
    /**
     * 解析日期字符串为LocalDate
     * 支持 yyyy/MM/dd 和 yyyy-MM-dd 格式
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 尝试解析 yyyy/MM/dd 格式
            if (dateStr.contains("/")) {
                String[] parts = dateStr.split("/");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);
                    return LocalDate.of(year, month, day);
                }
            }
            
            // 尝试解析 yyyy-MM-dd 格式
            if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr);
            }
            
            log.warn("无法解析日期格式: {}", dateStr);
            return null;
        } catch (Exception e) {
            log.warn("解析日期失败: {}, 错误: {}", dateStr, e.getMessage());
            return null;
        }
    }

    /**
     * 测试用户内部类
     * 使用 @JSONField 注解来映射下划线格式的JSON字段名到驼峰格式的Java属性名
     */
    public static class TestUser {
        private String username;
        private String email;
        private String password;
        
        @com.alibaba.fastjson2.annotation.JSONField(name = "confirm_password")
        private String confirmPassword;
        
        @com.alibaba.fastjson2.annotation.JSONField(name = "first_name")
        private String firstName;
        
        @com.alibaba.fastjson2.annotation.JSONField(name = "last_name")
        private String lastName;
        
        @com.alibaba.fastjson2.annotation.JSONField(name = "phone_number")
        private String phoneNumber;
        
        @com.alibaba.fastjson2.annotation.JSONField(name = "date_of_birth")
        private String dateOfBirth;
        
        private String nationality;
        private String address;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

        public String getNationality() { return nationality; }
        public void setNationality(String nationality) { this.nationality = nationality; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}
