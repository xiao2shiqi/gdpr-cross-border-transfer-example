package com.hotelbooking.hotel_reservation_eu.config;

import com.hotelbooking.hotel_reservation_eu.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * 用户端 Spring Security 配置类
 * 配置认证和授权规则
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(2) // 优先级低于运营端配置
public class SecurityConfig {

    private final CaptchaAuthenticationProvider captchaAuthenticationProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * 用户端认证管理器Bean
     */
    @Bean("userAuthenticationManager")
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 用户端安全过滤器链配置
     */
    @Bean("userSecurityFilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 排除运营端路径，仅处理用户端路径
            .securityMatcher(request -> !request.getRequestURI().startsWith("/admin"))
            .authenticationProvider(captchaAuthenticationProvider)
            .authorizeHttpRequests(authz -> authz
                // 公开访问的路径
                .requestMatchers(
                    "/css/**", 
                    "/js/**", 
                    "/images/**", 
                    "/webjars/**",
                    "/auth/login",
                    "/auth/register", 
                    "/auth/forgot-password",
                    "/auth/reset-password",
                    "/auth/check-username",
                    "/auth/check-email",
                    "/auth/get-random-test-user", // 获取测试用户数据API
                    "/captcha",         // 验证码图片
                    "/privacy-policy",  // GDPR隐私政策页面
                    "/terms",           // 服务条款页面
                    "/error",
                    "/api/hotel-branches/**" // 公开的分店查询API
                ).permitAll()
                // 需要认证的搜索相关路径
                .requestMatchers("/search/**").authenticated()
                .requestMatchers("/api/search/**").authenticated()
                // 预订相关路径需要认证
                .requestMatchers("/reservations/**").authenticated()
                .requestMatchers("/api/reservations/**").authenticated()
                // 确保预订API路径被正确匹配
                .requestMatchers("/reservations/api/**").authenticated()
                // 根路径重定向到dashboard
                .requestMatchers("/").authenticated()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")
                .tokenValiditySeconds(86400) // 24小时
                .userDetailsService(customUserDetailsService)
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/profile/delete", // 允许删除账户端点不使用CSRF
                    "/reservations/api/**" // 临时允许预订API不使用CSRF（仅用于测试）
                )
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 使用Cookie存储CSRF令牌
            );

        return http.build();
    }
} 