package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

/**
 * 用户注册请求DTO
 * 用于接收用户注册表单数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6-100个字符之间")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "名字不能为空")
    @Size(max = 50, message = "名字长度不能超过50个字符")
    private String firstName;

    @NotBlank(message = "姓氏不能为空")
    @Size(max = 50, message = "姓氏长度不能超过50个字符")
    private String lastName;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "电话号码格式不正确")
    private String phoneNumber;

    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;

    @Past(message = "出生日期必须是过去的日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Size(max = 50, message = "国籍长度不能超过50个字符")
    private String nationality;

    // GDPR 同意选项
    @NotNull(message = "必须同意数据处理条款")
    private Boolean gdprProcessing; // 必需：数据处理同意

    private Boolean gdprMarketing; // 可选：营销邮件同意

    private Boolean gdprAnalytics; // 可选：数据分析同意

    /**
     * 验证密码和确认密码是否一致
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }

    /**
     * 验证GDPR必需同意是否已勾选
     */
    public boolean isGdprProcessingAgreed() {
        return gdprProcessing != null && gdprProcessing;
    }
} 