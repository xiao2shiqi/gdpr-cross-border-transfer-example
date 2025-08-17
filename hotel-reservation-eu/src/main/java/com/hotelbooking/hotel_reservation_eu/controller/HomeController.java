package com.hotelbooking.hotel_reservation_eu.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主页控制器
 * 处理主页和仪表板相关请求
 */
@Controller
public class HomeController {

    /**
     * 主页重定向到登录页面
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // 已登录用户重定向到dashboard
            return "redirect:/dashboard";
        } else {
            // 未登录用户重定向到登录页面
            return "redirect:/auth/login";
        }
    }

    /**
     * 用户仪表板页面
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        return "dashboard";
    }
} 