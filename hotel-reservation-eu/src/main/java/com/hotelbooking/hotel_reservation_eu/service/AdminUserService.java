package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.mapper.AdminUserMapper;
import com.hotelbooking.hotel_reservation_eu.model.AdminUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 运营人员服务类
 * 实现UserDetailsService用于Spring Security认证
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService implements UserDetailsService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security认证接口实现
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("运营端用户认证 - 用户名: {}", username);
        
        AdminUser adminUser = adminUserMapper.findByUsername(username);
        if (adminUser == null) {
            log.warn("运营端用户认证失败 - 用户不存在: {}", username);
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 检查账户状态
        if (!adminUser.isEnabled()) {
            log.warn("运营端用户认证失败 - 账户已禁用: {}", username);
            throw new UsernameNotFoundException("账户已被禁用");
        }

        if (adminUser.isAccountLocked()) {
            log.warn("运营端用户认证失败 - 账户已锁定: {}", username);
            throw new UsernameNotFoundException("账户已被锁定，请联系管理员");
        }

        // 构建用户权限
        Collection<GrantedAuthority> authorities = getAuthorities(adminUser);

        return new AdminUserDetails(adminUser, authorities);
    }

    /**
     * 构建用户权限
     */
    private Collection<GrantedAuthority> getAuthorities(AdminUser adminUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // 基于角色的权限
        authorities.add(new SimpleGrantedAuthority("ROLE_" + adminUser.getRole().name()));
        
        // 基于角色的具体权限
        switch (adminUser.getRole()) {
            case ADMIN:
                authorities.add(new SimpleGrantedAuthority("PERM_ADMIN_ALL"));
                authorities.add(new SimpleGrantedAuthority("PERM_ROOM_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERM_ORDER_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERM_USER_MANAGE"));
                break;
            case MANAGER:
                authorities.add(new SimpleGrantedAuthority("PERM_ROOM_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERM_ORDER_MANAGE"));
                authorities.add(new SimpleGrantedAuthority("PERM_REPORT_VIEW"));
                break;
            case OPERATOR:
                authorities.add(new SimpleGrantedAuthority("PERM_ROOM_VIEW"));
                authorities.add(new SimpleGrantedAuthority("PERM_ORDER_VIEW"));
                break;
        }
        
        return authorities;
    }

    /**
     * 登录成功处理
     */
    @Transactional
    public void handleSuccessfulLogin(String username) {
        AdminUser adminUser = adminUserMapper.findByUsername(username);
        if (adminUser != null) {
            adminUserMapper.updateLastLoginTime(adminUser.getId(), LocalDateTime.now());
            log.info("运营人员登录成功 - 用户: {}, 显示名: {}", username, adminUser.getDisplayName());
        }
    }

    /**
     * 登录失败处理
     */
    @Transactional
    public void handleFailedLogin(String username) {
        AdminUser adminUser = adminUserMapper.findByUsername(username);
        if (adminUser != null) {
            adminUserMapper.incrementFailedLoginAttempts(adminUser.getId());
            
            // 检查是否需要锁定账户（连续失败5次）
            AdminUser updatedUser = adminUserMapper.findById(adminUser.getId());
            if (updatedUser.getFailedLoginAttempts() >= 5) {
                LocalDateTime lockUntil = LocalDateTime.now().plusHours(1); // 锁定1小时
                adminUserMapper.lockAccount(adminUser.getId(), lockUntil);
                log.warn("运营人员账户已锁定 - 用户: {}, 锁定到: {}", username, lockUntil);
            }
            
            log.warn("运营人员登录失败 - 用户: {}, 失败次数: {}", username, updatedUser.getFailedLoginAttempts());
        }
    }

    /**
     * 获取所有启用的运营人员
     */
    public List<AdminUser> getAllEnabledUsers() {
        return adminUserMapper.findAllEnabled();
    }

    /**
     * 根据ID获取运营人员
     */
    public AdminUser getById(Long id) {
        return adminUserMapper.findById(id);
    }

    /**
     * 创建新的运营人员
     */
    @Transactional
    public void createAdminUser(AdminUser adminUser, Long createdBy) {
        // 密码加密
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setCreatedBy(createdBy);
        adminUser.setStatus(1); // 默认启用
        
        adminUserMapper.insertAdminUser(adminUser);
        log.info("创建运营人员成功 - 用户名: {}, 创建者: {}", adminUser.getUsername(), createdBy);
    }

    /**
     * 更新运营人员信息
     */
    @Transactional
    public void updateAdminUser(AdminUser adminUser, Long updatedBy) {
        adminUser.setUpdatedBy(updatedBy);
        adminUserMapper.updateAdminUser(adminUser);
        log.info("更新运营人员成功 - 用户名: {}, 更新者: {}", adminUser.getUsername(), updatedBy);
    }

    /**
     * 自定义UserDetails实现
     */
    public static class AdminUserDetails implements UserDetails {
        private final AdminUser adminUser;
        private final Collection<GrantedAuthority> authorities;

        public AdminUserDetails(AdminUser adminUser, Collection<GrantedAuthority> authorities) {
            this.adminUser = adminUser;
            this.authorities = authorities;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return adminUser.getPassword();
        }

        @Override
        public String getUsername() {
            return adminUser.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return !adminUser.isAccountLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return adminUser.isEnabled();
        }

        public AdminUser getAdminUser() {
            return adminUser;
        }
    }
} 