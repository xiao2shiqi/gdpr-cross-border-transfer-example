package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.dto.RegisterRequestDto;
import com.hotelbooking.hotel_reservation_eu.model.User;

/**
 * 用户服务接口
 * 定义用户相关的业务逻辑方法
 */
public interface UserService {
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);
    
    /**
     * 根据ID查找用户
     */
    User getUserById(Long id);
    
    /**
     * 用户注册
     */
    void register(RegisterRequestDto registerRequestDto);
    
    /**
     * 保存用户
     */
    void saveUser(User user);
    
    /**
     * 检查用户名是否已存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 检查邮箱是否已存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 生成密码重置令牌并发送邮件
     */
    void initiatePasswordReset(String email);
    
    /**
     * 验证密码重置令牌
     */
    boolean isValidResetToken(String token);
    
    /**
     * 根据重置令牌查找用户
     */
    User findByResetToken(String token);
    
    /**
     * 重置用户密码
     */
    void resetPassword(String token, String newPassword);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user);
    
    /**
     * 删除用户账户（GDPR被遗忘权）
     * 采用数据匿名化方案，保留用户记录但清除所有PII数据
     */
    void deleteUserAccount(Long userId);
}
