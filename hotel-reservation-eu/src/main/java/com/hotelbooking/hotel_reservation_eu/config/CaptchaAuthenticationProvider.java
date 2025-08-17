package com.hotelbooking.hotel_reservation_eu.config;

import com.hotelbooking.hotel_reservation_eu.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 自定义认证提供者
 * 在用户名密码验证基础上增加验证码验证
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaAuthenticationProvider extends DaoAuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 初始化方法，设置UserDetailsService和PasswordEncoder
     */
    @PostConstruct
    public void init() {
        setUserDetailsService(customUserDetailsService);
        setPasswordEncoder(passwordEncoder);
    }

    /**
     * 执行认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            
            // 获取用户输入的验证码
            String captcha = request.getParameter("captcha");
            // 获取session中存储的验证码
            String sessionCaptcha = (String) request.getSession().getAttribute("captcha");
            
            log.info("用户输入验证码: {}, Session验证码: {}", captcha, sessionCaptcha);
            
            // 验证码校验
            if (captcha == null || captcha.trim().isEmpty()) {
                log.warn("验证码为空");
                throw new BadCredentialsException("请输入验证码");
            }
            
            if (sessionCaptcha == null) {
                log.warn("Session中无验证码，可能已过期");
                throw new BadCredentialsException("验证码已过期，请刷新页面重试");
            }
            
            if (!captcha.trim().equalsIgnoreCase(sessionCaptcha.trim())) {
                log.warn("验证码不匹配: 输入={}, 期望={}", captcha, sessionCaptcha);
                // 验证失败后清除session中的验证码
                request.getSession().removeAttribute("captcha");
                throw new BadCredentialsException("验证码错误");
            }
            
            // 验证码正确，清除session中的验证码（防止重复使用）
            request.getSession().removeAttribute("captcha");
            log.info("验证码验证成功");
        }
        
        // 验证码通过后，执行用户名密码验证
        return super.authenticate(authentication);
    }

    /**
     * 检查是否支持该认证类型
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
} 