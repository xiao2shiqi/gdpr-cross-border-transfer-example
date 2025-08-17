package com.hotelbooking.china_bi_system.controller;

import com.hotelbooking.china_bi_system.service.BiReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * BI报表控制器
 * 负责展示全球酒店运营数据
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class BiReportController {

    private final BiReportService biReportService;

    /**
     * 首页 - 显示全球业务数据概览
     */
    @GetMapping("/")
    public String index(Model model) {
        log.info("访问BI系统首页");
        
        try {
            // 获取综合统计概览（包含所有地区）
            var overview = biReportService.getComprehensiveOverview();
            model.addAttribute("overview", overview);
            
            // 添加页面标题
            model.addAttribute("pageTitle", "全球酒店运营数据概览");
            model.addAttribute("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
            
            log.info("首页数据加载完成");
            return "index";
            
        } catch (Exception e) {
            log.error("首页数据加载失败", e);
            model.addAttribute("error", "数据加载失败，请稍后重试");
            return "error";
        }
    }

    /**
     * 指定地区的业务数据概览
     */
    @GetMapping("/region/{region}")
    public String regionOverview(@PathVariable String region, Model model) {
        log.info("访问{}地区业务数据概览", region);
        
        try {
            var overview = biReportService.getComprehensiveOverviewByRegion(region);
            model.addAttribute("overview", overview);
            model.addAttribute("region", region);
            model.addAttribute("pageTitle", region + "地区业务数据概览");
            model.addAttribute("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
            
            return "region-overview";
            
        } catch (Exception e) {
            log.error("{}地区数据加载失败", region, e);
            model.addAttribute("error", "数据加载失败，请稍后重试");
            return "error";
        }
    }

    /**
     * 收入统计页面
     */
    @GetMapping("/income")
    public String incomeReport(Model model, @RequestParam(required = false) String region) {
        log.info("访问收入统计页面: region={}", region);
        
        try {
            if (region != null && !region.isEmpty()) {
                // 指定地区的收入统计
                var todayIncome = biReportService.getTodayIncomeByRegion(region);
                var last7DaysIncome = biReportService.getLast7DaysIncomeByRegion(region);
                var last30DaysIncome = biReportService.getLast30DaysIncome();
                
                model.addAttribute("todayIncome", todayIncome);
                model.addAttribute("last7DaysIncome", last7DaysIncome);
                model.addAttribute("last30DaysIncome", last30DaysIncome);
                model.addAttribute("region", region);
                model.addAttribute("pageTitle", region + "地区收入统计报表");
            } else {
                // 所有地区的收入统计
                var todayIncome = biReportService.getTodayIncome();
                var last7DaysIncome = biReportService.getLast7DaysIncome();
                var last30DaysIncome = biReportService.getLast30DaysIncome();
                
                model.addAttribute("todayIncome", todayIncome);
                model.addAttribute("last7DaysIncome", last7DaysIncome);
                model.addAttribute("last30DaysIncome", last30DaysIncome);
                model.addAttribute("pageTitle", "收入统计报表");
            }
            
            return "income";
            
        } catch (Exception e) {
            log.error("收入统计页面数据加载失败", e);
            model.addAttribute("error", "数据加载失败，请稍后重试");
            return "error";
        }
    }

    /**
     * 房型分析页面
     */
    @GetMapping("/room-types")
    public String roomTypeAnalysis(Model model, @RequestParam(required = false) String region) {
        log.info("访问房型分析页面: region={}", region);
        
        try {
            if (region != null && !region.isEmpty()) {
                // 指定地区的房型分析
                var todayPopularRoomTypes = biReportService.getTodayPopularRoomTypesByRegion(region);
                var last7DaysPopularRoomTypes = biReportService.getLast7DaysPopularRoomTypesByRegion(region);
                
                model.addAttribute("todayPopularRoomTypes", todayPopularRoomTypes);
                model.addAttribute("last7DaysPopularRoomTypes", last7DaysPopularRoomTypes);
                model.addAttribute("region", region);
                model.addAttribute("pageTitle", region + "地区房型分析报表");
            } else {
                // 所有地区的房型分析
                var todayPopularRoomTypes = biReportService.getTodayPopularRoomTypes();
                var last7DaysPopularRoomTypes = biReportService.getLast7DaysPopularRoomTypes();
                
                model.addAttribute("todayPopularRoomTypes", todayPopularRoomTypes);
                model.addAttribute("last7DaysPopularRoomTypes", last7DaysPopularRoomTypes);
                model.addAttribute("pageTitle", "房型分析报表");
            }
            
            return "room-types";
            
        } catch (Exception e) {
            log.error("房型分析页面数据加载失败", e);
            model.addAttribute("error", "数据加载失败，请稍后重试");
            return "error";
        }
    }

    /**
     * 数据管理页面
     */
    @GetMapping("/data-management")
    public String dataManagement(Model model) {
        log.info("访问数据管理页面");
        
        model.addAttribute("pageTitle", "数据管理");
        return "data-management";
    }
}
