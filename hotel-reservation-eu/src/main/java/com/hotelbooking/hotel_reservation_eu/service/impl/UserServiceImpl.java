package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hotelbooking.hotel_reservation_eu.dto.RegisterRequestDto;
import com.hotelbooking.hotel_reservation_eu.mapper.UserMapper;
import com.hotelbooking.hotel_reservation_eu.model.User;
import com.hotelbooking.hotel_reservation_eu.service.DataEncryptionService;
import com.hotelbooking.hotel_reservation_eu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final DataEncryptionService encryptionService;

    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        
        if (user != null) {
            // 解密PII数据
            decryptUserPiiData(user);
        }
        
        return user;
    }

    @Override
    public User findByEmail(String email) {
        // 由于邮箱已加密存储，需要遍历查找
        // 注意：这种方式效率较低，生产环境可考虑邮箱哈希索引
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        var users = userMapper.selectList(queryWrapper);
        
        for (User user : users) {
            try {
                String decryptedEmail = encryptionService.decrypt(user.getEmail());
                if (email.equals(decryptedEmail)) {
                    decryptUserPiiData(user);
                    return user;
                }
            } catch (Exception e) {
                log.warn("解密邮箱失败，跳过用户: {}", user.getId());
                continue;
            }
        }
        
        return null;
    }

    @Override
    public User getUserById(Long id) {
        log.info("根据ID获取用户信息: id={}", id);
        
        try {
            User user = userMapper.selectById(id);
            if (user != null) {
                // 解密PII数据
                decryptUserPiiData(user);
                log.info("成功获取用户信息: id={}, username={}", id, user.getUsername());
            } else {
                log.warn("用户不存在: id={}", id);
            }
            
            return user;
            
        } catch (Exception e) {
            log.error("获取用户信息失败: id={}, error={}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        // 验证密码匹配
        if (!registerRequestDto.isPasswordMatching()) {
            throw new IllegalArgumentException("密码和确认密码不匹配");
        }

        // 检查用户名是否已存在
        if (isUsernameExists(registerRequestDto.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (isEmailExists(registerRequestDto.getEmail())) {
            throw new IllegalArgumentException("邮箱已被注册");
        }

        // 创建新用户 - 加密PII数据
        User user = User.builder()
                .username(registerRequestDto.getUsername()) // 用户名不加密（用于登录）
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))   // BCrypt 加密
                .firstName(encryptionService.encrypt(registerRequestDto.getFirstName()))
                .lastName(encryptionService.encrypt(registerRequestDto.getLastName()))
                .email(encryptionService.encrypt(registerRequestDto.getEmail()))
                .phoneNumber(encryptionService.encrypt(registerRequestDto.getPhoneNumber()))
                .address(encryptionService.encrypt(registerRequestDto.getAddress()))
                .dateOfBirth(registerRequestDto.getDateOfBirth()) // 出生日期不加密（用于年龄计算）
                .nationality(registerRequestDto.getNationality()) // 国籍不加密（用于统计分析）
                .role("USER")
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .gdprProcessingConsent(registerRequestDto.getGdprProcessing())
                .gdprMarketingConsent(registerRequestDto.getGdprMarketing())
                .gdprAnalyticsConsent(registerRequestDto.getGdprAnalytics())
                .gdprConsentDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);
        log.info("User registered successfully with username: {}", registerRequestDto.getUsername());
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        // 加密PII数据
        encryptUserPiiData(user);
        
        if (user.getId() == null) {
            // 新用户，插入
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
            log.info("New user saved with username: {}", user.getUsername());
        } else {
            // 现有用户，更新
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
            log.info("Existing user updated with username: {}", user.getUsername());
        }
        
        // 解密PII数据（保持对象状态一致）
        decryptUserPiiData(user);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        return findByEmail(email) != null;
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = findByEmail(email);
        if (user == null) {
            log.warn("Password reset attempted for non-existent email: {}", email);
            return; // 不暴露用户是否存在
        }

        // 生成重置令牌
        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(24)); // 24小时有效
        user.setUpdatedAt(LocalDateTime.now());

        // 加密后保存
        encryptUserPiiData(user);
        userMapper.updateById(user);

        // TODO: 发送重置密码邮件
        log.info("Password reset token generated for user: {} with email: {}", user.getUsername(), email);
    }

    @Override
    public boolean isValidResetToken(String token) {
        User user = findByResetToken(token);
        return user != null && user.isResetTokenValid();
    }

    @Override
    public User findByResetToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reset_password_token", token);
        User user = userMapper.selectOne(queryWrapper);
        
        if (user != null) {
            decryptUserPiiData(user);
        }
        
        return user;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = findByResetToken(token);
        if (user == null || !user.isResetTokenValid()) {
            throw new IllegalArgumentException("无效或已过期的重置令牌");
        }

        // 更新密码并清除重置令牌
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        user.setUpdatedAt(LocalDateTime.now());

        // 加密后保存
        encryptUserPiiData(user);
        userMapper.updateById(user);
        
        log.info("Password reset successfully for user: {}", user.getUsername());
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        
        // 加密PII数据
        encryptUserPiiData(user);
        userMapper.updateById(user);
        
        // 解密PII数据（保持对象状态一致）
        decryptUserPiiData(user);
        
        log.info("User updated successfully: {}", user.getUsername());
    }

    /**
     * 加密用户PII数据
     */
    private void encryptUserPiiData(User user) {
        if (user == null) return;
        
        try {
            // 加密敏感PII字段
            if (user.getFirstName() != null) {
                user.setFirstName(encryptionService.encrypt(user.getFirstName()));
            }
            if (user.getLastName() != null) {
                user.setLastName(encryptionService.encrypt(user.getLastName()));
            }
            if (user.getEmail() != null) {
                user.setEmail(encryptionService.encrypt(user.getEmail()));
            }
            if (user.getPhoneNumber() != null) {
                user.setPhoneNumber(encryptionService.encrypt(user.getPhoneNumber()));
            }
            if (user.getAddress() != null) {
                user.setAddress(encryptionService.encrypt(user.getAddress()));
            }
        } catch (Exception e) {
            log.error("加密用户PII数据失败: {}", e.getMessage());
            throw new RuntimeException("用户数据加密失败", e);
        }
    }

    /**
     * 解密用户PII数据
     */
    private void decryptUserPiiData(User user) {
        if (user == null) return;
        
        try {
            // 解密敏感PII字段
            if (user.getFirstName() != null) {
                user.setFirstName(encryptionService.decrypt(user.getFirstName()));
            }
            if (user.getLastName() != null) {
                user.setLastName(encryptionService.decrypt(user.getLastName()));
            }
            if (user.getEmail() != null) {
                user.setEmail(encryptionService.decrypt(user.getEmail()));
            }
            if (user.getPhoneNumber() != null) {
                user.setPhoneNumber(encryptionService.decrypt(user.getPhoneNumber()));
            }
            if (user.getAddress() != null) {
                user.setAddress(encryptionService.decrypt(user.getAddress()));
            }
        } catch (Exception e) {
            log.error("解密用户PII数据失败: {}", e.getMessage());
            throw new RuntimeException("用户数据解密失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteUserAccount(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        log.info("开始删除用户账户（数据匿名化）- 用户ID: {}, 用户名: {}", userId, user.getUsername());

        // 生成匿名化数据
        String anonymizedUsername = "deleted_user_" + userId + "_" + System.currentTimeMillis();
        String anonymizedEmail = "deleted_" + userId + "_" + System.currentTimeMillis() + "@deleted.example.com";
        
        // 匿名化用户数据（保留用户记录但清除所有PII）
        user.setUsername(anonymizedUsername);
        user.setFirstName("已删除");
        user.setLastName("用户");
        user.setEmail(anonymizedEmail);
        user.setPhoneNumber(null);
        user.setAddress(null);
        user.setDateOfBirth(null);
        user.setNationality(null);
        
        // 禁用账户
        user.setEnabled(false);
        user.setAccountNonExpired(false);
        user.setCredentialsNonExpired(false);
        user.setAccountNonLocked(false);
        
        // 清除GDPR同意设置
        user.setGdprProcessingConsent(false);
        user.setGdprMarketingConsent(false);
        user.setGdprAnalyticsConsent(false);
        user.setGdprConsentDate(null);
        
        // 清除密码重置令牌
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        
        // 更新删除时间
        user.setUpdatedAt(LocalDateTime.now());
        
        // 加密匿名化后的数据（保持数据一致性）
        encryptUserPiiData(user);
        
        // 保存到数据库
        userMapper.updateById(user);
        
        log.info("用户账户删除完成（数据匿名化）- 用户ID: {}, 原用户名: {}", userId, user.getUsername());
    }
} 