package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 用户详情服务实现
 * 用于加载用户信息进行认证
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled() != null ? user.getEnabled() : true,
                user.getAccountNonExpired() != null ? user.getAccountNonExpired() : true,
                user.getCredentialsNonExpired() != null ? user.getCredentialsNonExpired() : true,
                user.getAccountNonLocked() != null ? user.getAccountNonLocked() : true,
                getAuthorities(user)
        );
    }

    /**
     * 获取用户权限
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        String role = user.getRole() != null ? user.getRole() : "USER";
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
} 