package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.model.Reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 统计报告生成服务接口
 * 负责生成各种业务统计报告，所有数据都是匿名化的
 */
public interface StatisticsReportService {
    
    /**
     * 生成每日总收入统计报告
     */
    Map<String, Object> generateDailyTotalIncomeReport(LocalDate date);
    
    /**
     * 生成热门房型Top5统计报告
     */
    Map<String, Object> generatePopularRoomTypesTop5Report(LocalDate date);
    
    /**
     * 生成分店业绩统计报告
     */
    Map<String, Object> generateBranchPerformanceReport(LocalDate date);
    
    /**
     * 生成预订趋势统计报告
     */
    Map<String, Object> generateReservationTrendsReport(LocalDate date);
    
    /**
     * 生成综合统计报告（包含所有维度）
     */
    Map<String, Object> generateComprehensiveReport(LocalDate date);
    
    /**
     * 根据脱敏后的预订数据生成统计报告
     */
    Map<String, Object> generateReportFromAnonymizedData(List<Reservation> anonymizedReservations, LocalDate date);
}
