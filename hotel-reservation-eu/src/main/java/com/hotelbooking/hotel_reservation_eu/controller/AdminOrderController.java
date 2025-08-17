package com.hotelbooking.hotel_reservation_eu.controller;

import com.hotelbooking.hotel_reservation_eu.model.AdminUser;
import com.hotelbooking.hotel_reservation_eu.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 运营端订单管理控制器
 * 处理订单管理相关的页面路由和业务逻辑
 */
@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminUserService adminUserService;

    /**
     * 订单管理主页面
     */
    @GetMapping(value = {"", "/"})
    public String ordersIndex(Model model, Authentication authentication) {
        log.info("访问运营端订单管理页面");
        
        // 获取当前登录的运营人员信息
        if (authentication != null && authentication.getPrincipal() instanceof AdminUserService.AdminUserDetails) {
            AdminUserService.AdminUserDetails adminUserDetails = (AdminUserService.AdminUserDetails) authentication.getPrincipal();
            AdminUser currentUser = adminUserDetails.getAdminUser();
            
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userRole", currentUser.getRole());
            model.addAttribute("userDisplayName", currentUser.getDisplayName());
            model.addAttribute("userDepartment", currentUser.getDepartment());
            
            log.info("运营人员访问订单管理 - 用户: {}, 角色: {}", currentUser.getUsername(), currentUser.getRole());
        }
        
        // TODO: 后续在这里加载订单管理的数据
        // model.addAttribute("pendingOrders", orderService.getPendingOrders());
        // model.addAttribute("todayCheckIns", orderService.getTodayCheckIns());
        // model.addAttribute("todayCheckOuts", orderService.getTodayCheckIns());
        // model.addAttribute("orderStats", statisticsService.getOrderStats());
        
        return "admin/orders";
    }
} 