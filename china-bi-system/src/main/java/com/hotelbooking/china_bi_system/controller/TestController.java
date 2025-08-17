package com.hotelbooking.china_bi_system.controller;

import com.hotelbooking.china_bi_system.mapper.DailyTotalIncomeMapper;
import com.hotelbooking.china_bi_system.mapper.PopularRoomTypeMapper;
import com.hotelbooking.china_bi_system.model.DailyTotalIncome;
import com.hotelbooking.china_bi_system.model.PopularRoomType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证数据库连接和数据
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final DailyTotalIncomeMapper dailyTotalIncomeMapper;
    private final PopularRoomTypeMapper popularRoomTypeMapper;

    /**
     * 测试数据库连接
     */
    @GetMapping("/connection")
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 测试查询
            List<DailyTotalIncome> allIncome = dailyTotalIncomeMapper.selectAll();
            List<PopularRoomType> allRoomTypes = popularRoomTypeMapper.selectAll();
            
            result.put("success", true);
            result.put("message", "数据库连接成功");
            result.put("dailyTotalIncomeCount", allIncome.size());
            result.put("popularRoomTypeCount", allRoomTypes.size());
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("数据库连接测试成功: income={}, roomTypes={}", allIncome.size(), allRoomTypes.size());
            
        } catch (Exception e) {
            log.error("数据库连接测试失败", e);
            result.put("success", false);
            result.put("message", "数据库连接失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return result;
    }

    /**
     * 获取今日数据
     */
    @GetMapping("/today")
    public Map<String, Object> getTodayData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<DailyTotalIncome> todayIncome = dailyTotalIncomeMapper.selectToday();
            List<PopularRoomType> todayRoomTypes = popularRoomTypeMapper.selectToday();
            
            result.put("success", true);
            result.put("todayIncome", todayIncome);
            result.put("todayRoomTypes", todayRoomTypes);
            result.put("incomeCount", todayIncome.size());
            result.put("roomTypeCount", todayRoomTypes.size());
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("获取今日数据成功: income={}, roomTypes={}", todayIncome.size(), todayRoomTypes.size());
            
        } catch (Exception e) {
            log.error("获取今日数据失败", e);
            result.put("success", false);
            result.put("message", "获取今日数据失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return result;
    }

    /**
     * 获取所有地区数据
     */
    @GetMapping("/regions")
    public Map<String, Object> getAllRegionsData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<DailyTotalIncome> allRegionsIncome = dailyTotalIncomeMapper.selectTodayAllRegions();
            List<PopularRoomType> allRegionsRoomTypes = popularRoomTypeMapper.selectTodayAllRegions();
            
            result.put("success", true);
            result.put("allRegionsIncome", allRegionsIncome);
            result.put("allRegionsRoomTypes", allRegionsRoomTypes);
            result.put("incomeCount", allRegionsIncome.size());
            result.put("roomTypeCount", allRegionsRoomTypes.size());
            result.put("timestamp", System.currentTimeMillis());
            
            log.info("获取所有地区数据成功: income={}, roomTypes={}", allRegionsIncome.size(), allRegionsRoomTypes.size());
            
        } catch (Exception e) {
            log.error("获取所有地区数据失败", e);
            result.put("success", false);
            result.put("message", "获取所有地区数据失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
        }
        
        return result;
    }
}
