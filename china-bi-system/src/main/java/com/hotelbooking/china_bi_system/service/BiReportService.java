package com.hotelbooking.china_bi_system.service;

import com.hotelbooking.china_bi_system.model.DailyTotalIncome;
import com.hotelbooking.china_bi_system.model.PopularRoomType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BI报表服务接口
 * 负责提供各种业务统计数据的查询服务
 */
public interface BiReportService {
    
    /**
     * 获取今日收入统计
     */
    List<DailyTotalIncome> getTodayIncome();
    
    /**
     * 获取指定地区的今日收入统计
     */
    DailyTotalIncome getTodayIncomeByRegion(String region);
    
    /**
     * 获取最近7天的收入统计
     */
    List<DailyTotalIncome> getLast7DaysIncome();
    
    /**
     * 获取指定地区最近7天的收入统计
     */
    List<DailyTotalIncome> getLast7DaysIncomeByRegion(String region);
    
    /**
     * 获取最近30天的收入统计
     */
    List<DailyTotalIncome> getLast30DaysIncome();
    
    /**
     * 获取今日热门房型Top5
     */
    List<PopularRoomType> getTodayPopularRoomTypes();
    
    /**
     * 获取指定地区的今日热门房型Top5
     */
    List<PopularRoomType> getTodayPopularRoomTypesByRegion(String region);
    
    /**
     * 获取指定日期的热门房型Top5
     */
    List<PopularRoomType> getPopularRoomTypesByDate(LocalDate date);
    
    /**
     * 获取最近7天的热门房型统计
     */
    List<PopularRoomType> getLast7DaysPopularRoomTypes();
    
    /**
     * 获取指定地区最近7天的热门房型统计
     */
    List<PopularRoomType> getLast7DaysPopularRoomTypesByRegion(String region);
    
    /**
     * 获取综合统计概览
     */
    Map<String, Object> getComprehensiveOverview();
    
    /**
     * 获取指定地区的综合统计概览
     */
    Map<String, Object> getComprehensiveOverviewByRegion(String region);
}
