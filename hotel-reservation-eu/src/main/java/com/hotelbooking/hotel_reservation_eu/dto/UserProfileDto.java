package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

/**
 * 用户个人资料数据传输对象
 * 用于用户查看和更新个人信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    private String username;
    
    @NotBlank(message = "名不能为空")
    @Size(max = 50, message = "名长度不能超过50个字符")
    private String firstName;
    
    @NotBlank(message = "姓不能为空")
    @Size(max = 50, message = "姓长度不能超过50个字符")
    private String lastName;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Size(max = 20, message = "电话号码长度不能超过20个字符")
    private String phoneNumber;
    
    @Size(max = 255, message = "地址长度不能超过255个字符")
    private String address;
    
    @Past(message = "出生日期必须是过去的日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    
    @Size(max = 50, message = "国籍长度不能超过50个字符")
    private String nationality;
    
    private Boolean gdprMarketingConsent;
    
    private Boolean gdprAnalyticsConsent;
}