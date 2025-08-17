package com.hotelbooking.hotel_reservation_eu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hotelbooking.hotel_reservation_eu.dto.UserProfileDto;
import com.hotelbooking.hotel_reservation_eu.model.User;
import com.hotelbooking.hotel_reservation_eu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户个人资料控制器
 * 处理用户个人资料查看和更新
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    /**
     * 显示用户个人资料页面
     */
    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return "redirect:/auth/login";
        }

        UserProfileDto profileDto = convertToDto(user);
        model.addAttribute("userProfile", profileDto);
        model.addAttribute("username", authentication.getName());
        
        return "profile";
    }

    /**
     * 处理用户个人资料更新
     */
    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("userProfile") UserProfileDto profileDto,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null) {
            return "redirect:/auth/login";
        }

        // 如果有验证错误，返回表单
        if (result.hasErrors()) {
            model.addAttribute("username", authentication.getName());
            return "profile";
        }

        User currentUser = userService.findByUsername(authentication.getName());
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        // 检查邮箱是否已被其他用户使用
        if (!currentUser.getEmail().equals(profileDto.getEmail()) && 
            userService.isEmailExists(profileDto.getEmail())) {
            result.rejectValue("email", "error.email", "该邮箱已被使用");
            model.addAttribute("username", authentication.getName());
            return "profile";
        }

        // 更新用户信息
        updateUserFromDto(currentUser, profileDto);
        userService.updateUser(currentUser);
        
        log.info("用户 {} 更新了个人资料", authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "个人资料已成功更新");
        
        return "redirect:/profile";
    }

    /**
     * 导出用户个人数据（GDPR数据可携权）
     */
    @GetMapping("/profile/export")
    public ResponseEntity<String> exportUserData(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("未授权访问");
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            return ResponseEntity.badRequest().body("用户不存在");
        }

        try {
            // 构建用户数据导出对象
            Map<String, Object> userData = buildUserDataExport(user);
            
            // 转换为JSON字符串
            String jsonData = convertToJson(userData);
            
            // 生成文件名
            String filename = String.format("user_data_%s_%s.json", 
                user.getUsername(), 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            log.info("用户 {} 导出了个人数据", authentication.getName());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonData);
                    
        } catch (Exception e) {
            log.error("导出用户数据失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("导出失败，请稍后重试");
        }
    }

    /**
     * 删除用户账户（GDPR被遗忘权）
     */
    @PostMapping("/profile/delete")
    public ResponseEntity<Map<String, Object>> deleteUserAccount(
            Authentication authentication,
            @RequestParam(defaultValue = "false") boolean confirmDeletion) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("success", false);
            response.put("message", "未授权访问");
            return ResponseEntity.badRequest().body(response);
        }

        if (!confirmDeletion) {
            response.put("success", false);
            response.put("message", "请确认删除操作");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 执行账户删除（数据匿名化）
            userService.deleteUserAccount(user.getId());
            
            log.info("用户 {} 删除了账户（数据匿名化）", authentication.getName());
            
            response.put("success", true);
            response.put("message", "账户已成功删除，您的个人数据已被匿名化处理");
            response.put("redirectUrl", "/auth/logout");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("删除用户账户失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "删除失败，请稍后重试");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 构建用户数据导出对象
     */
    private Map<String, Object> buildUserDataExport(User user) {
        Map<String, Object> userData = new HashMap<>();
        
        // 基本信息
        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("用户ID", user.getId());
        basicInfo.put("用户名", user.getUsername());
        basicInfo.put("名", user.getFirstName());
        basicInfo.put("姓", user.getLastName());
        basicInfo.put("邮箱", user.getEmail());
        basicInfo.put("电话号码", user.getPhoneNumber());
        basicInfo.put("地址", user.getAddress());
        basicInfo.put("出生日期", user.getDateOfBirth());
        basicInfo.put("国籍", user.getNationality());
        basicInfo.put("用户角色", user.getRole());
        basicInfo.put("账户状态", user.getEnabled());
        basicInfo.put("创建时间", user.getCreatedAt());
        basicInfo.put("最后更新时间", user.getUpdatedAt());
        
        userData.put("基本信息", basicInfo);
        
        // GDPR同意设置
        Map<String, Object> gdprConsent = new HashMap<>();
        gdprConsent.put("数据处理同意", user.getGdprProcessingConsent());
        gdprConsent.put("营销邮件同意", user.getGdprMarketingConsent());
        gdprConsent.put("数据分析同意", user.getGdprAnalyticsConsent());
        gdprConsent.put("GDPR同意时间", user.getGdprConsentDate());
        
        userData.put("GDPR同意设置", gdprConsent);
        
        // 账户安全信息
        Map<String, Object> securityInfo = new HashMap<>();
        securityInfo.put("账户未过期", user.getAccountNonExpired());
        securityInfo.put("凭证未过期", user.getCredentialsNonExpired());
        securityInfo.put("账户未锁定", user.getAccountNonLocked());
        securityInfo.put("密码重置令牌", user.getResetPasswordToken());
        securityInfo.put("密码重置令牌过期时间", user.getResetPasswordTokenExpiry());
        
        userData.put("账户安全信息", securityInfo);
        
        // 导出元数据
        Map<String, Object> exportMetadata = new HashMap<>();
        exportMetadata.put("导出时间", LocalDateTime.now());
        exportMetadata.put("导出原因", "GDPR数据可携权请求");
        exportMetadata.put("数据格式", "JSON");
        exportMetadata.put("数据版本", "1.0");
        exportMetadata.put("GDPR条款", "第20条 - 数据可携权");
        
        userData.put("导出元数据", exportMetadata);
        
        return userData;
    }

    /**
     * 将数据转换为JSON字符串
     */
    private String convertToJson(Map<String, Object> data) {
        try {
            // 配置ObjectMapper以正确处理LocalDateTime
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (Exception e) {
            log.error("JSON转换失败: {}", e.getMessage(), e);
            // 如果Jackson失败，回退到手动JSON生成
            return fallbackJsonGeneration(data);
        }
    }

    /**
     * 手动JSON生成（备用方案）
     */
    private String fallbackJsonGeneration(Map<String, Object> data) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        
        boolean firstSection = true;
        for (Map.Entry<String, Object> section : data.entrySet()) {
            if (!firstSection) {
                json.append(",\n");
            }
            firstSection = false;
            
            json.append("  \"").append(section.getKey()).append("\": {\n");
            
            if (section.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> sectionData = (Map<String, Object>) section.getValue();
                boolean firstField = true;
                
                for (Map.Entry<String, Object> field : sectionData.entrySet()) {
                    if (!firstField) {
                        json.append(",\n");
                    }
                    firstField = false;
                    
                    json.append("    \"").append(field.getKey()).append("\": ");
                    
                    if (field.getValue() == null) {
                        json.append("null");
                    } else if (field.getValue() instanceof String) {
                        json.append("\"").append(field.getValue()).append("\"");
                    } else if (field.getValue() instanceof Boolean || field.getValue() instanceof Number) {
                        json.append(field.getValue());
                    } else {
                        json.append("\"").append(field.getValue().toString()).append("\"");
                    }
                }
            }
            
            json.append("\n  }");
        }
        
        json.append("\n}");
        return json.toString();
    }

    /**
     * 将用户实体转换为DTO
     */
    private UserProfileDto convertToDto(User user) {
        log.info("转换用户数据 - 用户ID: {}, 出生日期: {}", user.getId(), user.getDateOfBirth());
        
        UserProfileDto dto = UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .nationality(user.getNationality())
                .gdprMarketingConsent(user.getGdprMarketingConsent())
                .gdprAnalyticsConsent(user.getGdprAnalyticsConsent())
                .build();
        
        log.info("DTO创建完成 - 出生日期: {}", dto.getDateOfBirth());
        return dto;
    }

    /**
     * 从DTO更新用户实体
     */
    private void updateUserFromDto(User user, UserProfileDto dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setNationality(dto.getNationality());
        user.setGdprMarketingConsent(dto.getGdprMarketingConsent());
        user.setGdprAnalyticsConsent(dto.getGdprAnalyticsConsent());
        user.setUpdatedAt(java.time.LocalDateTime.now());
    }
}