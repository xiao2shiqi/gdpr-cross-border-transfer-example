package com.hotelbooking.hotel_reservation_eu.config;

import com.hotelbooking.hotel_reservation_eu.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 运营端安全配置 - 简化版
 * 独立于用户端的认证体系
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(1) // 优先级高于用户端配置
public class AdminSecurityConfig {

    private final AdminUserService adminUserService;

    /**
     * 运营端安全过滤器链配置 - 简化版
     */
    @Bean("adminSecurityFilterChain")
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {        
        http
            // 仅对运营端路径生效
            .securityMatcher("/admin/**")
            .userDetailsService(adminUserService)
            .authorizeHttpRequests(auth -> auth
                // 运营端登录页面允许匿名访问
                .requestMatchers("/admin/login", "/admin/assets/**", "/admin/css/**", "/admin/js/**").permitAll()
                // 其他运营端路径需要认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(adminAuthenticationSuccessHandler())
                .failureHandler(adminAuthenticationFailureHandler())
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // 临时禁用CSRF用于测试

        return http.build();
    }

    /**
     * 运营端登录成功处理器
     */
    @Bean
    public AuthenticationSuccessHandler adminAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            // 记录成功登录
            String username = authentication.getName();
            adminUserService.handleSuccessfulLogin(username);
            
            // 设置登录成功标识
            request.getSession().setAttribute("ADMIN_LOGIN_SUCCESS", true);
            
            // 重定向到管理端首页
            response.sendRedirect("/admin/dashboard");
        };
    }

    /**
     * 运营端登录失败处理器
     */
    @Bean
    public AuthenticationFailureHandler adminAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            // 记录失败登录
            String username = request.getParameter("username");
            if (username != null && !username.trim().isEmpty()) {
                adminUserService.handleFailedLogin(username);
            }
            
            // 重定向到登录页面，显示错误信息
            response.sendRedirect("/admin/login?error=true");
        };
    }
} 