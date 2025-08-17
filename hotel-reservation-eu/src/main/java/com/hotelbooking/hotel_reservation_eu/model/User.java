package com.hotelbooking.hotel_reservation_eu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用于存储用户基本信息和认证相关信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户名（唯一） */
    private String username;
    
    /** 加密后的密码 */
    private String password;
    
    /** 名 */
    private String firstName;
    
    /** 姓 */
    private String lastName;
    
    /** 邮箱地址（唯一） */
    private String email;
    
    /** 电话号码 */
    private String phoneNumber;
    
    /** 地址 */
    private String address;
    
    /** 出生日期 */
    private LocalDate dateOfBirth;
    
    /** 国籍 */
    private String nationality;
    
    /** 用户角色（USER, ADMIN） */
    private String role;
    
    /** 账户是否启用 */
    private Boolean enabled;
    
    /** 账户是否未过期 */
    private Boolean accountNonExpired;
    
    /** 凭证是否未过期 */
    private Boolean credentialsNonExpired;
    
    /** 账户是否未锁定 */
    private Boolean accountNonLocked;
    
    /** 密码重置令牌 */
    private String resetPasswordToken;
    
    /** 密码重置令牌过期时间 */
    private LocalDateTime resetPasswordTokenExpiry;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;

    // GDPR 相关字段
    /** 是否同意数据处理（必需） */
    private Boolean gdprProcessingConsent;
    
    /** 是否同意营销邮件（可选） */
    private Boolean gdprMarketingConsent;
    
    /** 是否同意数据分析（可选） */
    private Boolean gdprAnalyticsConsent;
    
    /** GDPR同意时间 */
    private LocalDateTime gdprConsentDate;

    /**
     * 获取用户全名
     */
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    /**
     * 检查密码重置令牌是否有效
     */
    public boolean isResetTokenValid() {
        return resetPasswordToken != null && 
               resetPasswordTokenExpiry != null && 
               resetPasswordTokenExpiry.isAfter(LocalDateTime.now());
    }

    /**
     * 检查是否已同意必需的数据处理
     */
    public boolean hasGdprProcessingConsent() {
        return gdprProcessingConsent != null && gdprProcessingConsent;
    }
}
