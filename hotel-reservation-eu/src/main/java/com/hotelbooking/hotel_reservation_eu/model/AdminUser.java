package com.hotelbooking.hotel_reservation_eu.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 运营人员实体类
 * 注意：此表不包含PII信息，符合数据保护要求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUser {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 用户名（登录名）
     */
    private String username;
    
    /**
     * 密码（BCrypt加密）
     */
    private String password;
    
    /**
     * 显示名称（工作名称，非真实姓名）
     */
    private String displayName;
    
    /**
     * 工作邮箱
     */
    private String email;
    
    /**
     * 角色类型
     */
    private AdminRole role;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 账户状态：1-启用 0-禁用
     */
    private Integer status;
    
    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
    
    /**
     * 失败登录次数
     */
    private Integer failedLoginAttempts;
    
    /**
     * 账户锁定到期时间
     */
    private LocalDateTime accountLockedUntil;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 更新人ID
     */
    private Long updatedBy;
    
    /**
     * 运营人员角色枚举
     */
    public enum AdminRole {
        ADMIN("管理员"),
        MANAGER("运营经理"), 
        OPERATOR("操作员");
        
        private final String displayName;
        
        AdminRole(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * 检查账户是否启用
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
    
    /**
     * 检查账户是否被锁定
     */
    public boolean isAccountLocked() {
        return accountLockedUntil != null && LocalDateTime.now().isBefore(accountLockedUntil);
    }
    
    /**
     * 获取角色显示名称
     */
    public String getRoleDisplayName() {
        return role != null ? role.getDisplayName() : "未知角色";
    }
} 