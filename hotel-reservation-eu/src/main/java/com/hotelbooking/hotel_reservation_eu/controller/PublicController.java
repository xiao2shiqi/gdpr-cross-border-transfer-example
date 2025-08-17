package com.hotelbooking.hotel_reservation_eu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 公开页面控制器
 * 处理不需要认证的公开页面请求，如隐私政策、服务条款等
 */
@Controller
public class PublicController {

    /**
     * 显示隐私政策页面
     */
    @GetMapping("/privacy-policy")
    public String showPrivacyPolicy() {
        return "privacy-policy";
    }

    /**
     * 显示服务条款页面
     */
    @GetMapping("/terms")
    public String showTerms() {
        return "terms";
    }
} 