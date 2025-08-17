package com.hotelbooking.hotel_reservation_eu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 运营端页面控制器
 * 处理管理界面的页面路由
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * 仪表板页面
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    /**
     * 分店管理页面
     */
    @GetMapping("/hotel-branches")
    public String hotelBranches() {
        return "admin/hotel-branches";
    }

    /**
     * 房型管理页面
     */
    @GetMapping("/room-types")
    public String roomTypes() {
        return "admin/room-types";
    }

    /**
     * 房源管理页面
     */
    @GetMapping("/room-type-branch-mappings")
    public String roomTypeBranchMappings() {
        return "admin/room-type-branch-mappings";
    }

    /**
     * 订单管理页面
     */
    @GetMapping("/orders")
    public String orders() {
        return "admin/orders";
    }

    /**
     * 客房管理页面
     */
    @GetMapping("/rooms")
    public String rooms() {
        return "admin/rooms";
    }

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }
}