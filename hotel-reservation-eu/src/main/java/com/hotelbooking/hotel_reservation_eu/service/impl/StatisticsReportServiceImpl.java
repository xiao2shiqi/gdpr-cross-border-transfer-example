package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.model.Reservation;
import com.hotelbooking.hotel_reservation_eu.service.StatisticsReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计报告生成服务实现类
 * 生成各种业务统计报告，确保数据完全匿名化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsReportServiceImpl implements StatisticsReportService {

    @Override
    public Map<String, Object> generateDailyTotalIncomeReport(LocalDate date) {
        log.info("生成每日总收入统计报告: date={}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", date);
        report.put("reportType", "daily-total-income");
        report.put("region", "EU"); // 添加地区标识
        report.put("generatedAt", new Date());
        
        // 这里的数据将在定时任务中从数据库获取并传入
        // 目前返回示例数据结构
        report.put("totalIncome", BigDecimal.ZERO);
        report.put("totalReservations", 0);
        report.put("avgPricePerNight", BigDecimal.ZERO);
        report.put("currency", "EUR");
        
        log.info("每日总收入统计报告生成完成");
        return report;
    }

    @Override
    public Map<String, Object> generatePopularRoomTypesTop5Report(LocalDate date) {
        log.info("生成热门房型Top5统计报告: date={}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", date);
        report.put("reportType", "popular-room-types-top-5");
        report.put("region", "EU"); // 添加地区标识
        report.put("generatedAt", new Date());
        
        // 热门房型Top5列表
        List<Map<String, Object>> top5List = new ArrayList<>();
        report.put("top5RoomTypes", top5List);
        
        log.info("热门房型Top5统计报告生成完成");
        return report;
    }

    @Override
    public Map<String, Object> generateBranchPerformanceReport(LocalDate date) {
        log.info("生成分店业绩统计报告: date={}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", date);
        report.put("reportType", "branch-performance");
        report.put("region", "EU"); // 添加地区标识
        report.put("generatedAt", new Date());
        
        // 分店业绩列表
        List<Map<String, Object>> branchList = new ArrayList<>();
        report.put("branchPerformance", branchList);
        
        log.info("分店业绩统计报告生成完成");
        return report;
    }

    @Override
    public Map<String, Object> generateReservationTrendsReport(LocalDate date) {
        log.info("生成预订趋势统计报告: date={}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", date);
        report.put("reportType", "reservation-trends");
        report.put("region", "EU"); // 添加地区标识
        report.put("generatedAt", new Date());
        
        // 预订趋势数据
        Map<String, Object> trends = new HashMap<>();
        trends.put("totalReservations", 0);
        trends.put("confirmedReservations", 0);
        trends.put("cancelledReservations", 0);
        trends.put("completionRate", BigDecimal.ZERO);
        
        report.put("trends", trends);
        
        log.info("预订趋势统计报告生成完成");
        return report;
    }

    @Override
    public Map<String, Object> generateComprehensiveReport(LocalDate date) {
        log.info("生成综合统计报告: date={}", date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", date);
        report.put("reportType", "comprehensive-report");
        report.put("region", "EU"); // 添加地区标识
        report.put("generatedAt", new Date());
        
        // 这里的数据将在定时任务中从数据库获取并传入
        // 目前返回示例数据结构
        report.put("dailyTotalIncome", generateDailyTotalIncomeReport(date));
        report.put("popularRoomTypesTop5", generatePopularRoomTypesTop5Report(date));
        report.put("branchPerformance", generateBranchPerformanceReport(date));
        report.put("reservationTrends", generateReservationTrendsReport(date));
        
        log.info("综合统计报告生成完成");
        return report;
    }

    @Override
    public Map<String, Object> generateReportFromAnonymizedData(List<Reservation> anonymizedReservations, LocalDate date) {
        log.info("根据脱敏数据生成综合统计报告: date={}, dataCount={}", date, anonymizedReservations.size());
        
        Map<String, Object> report = new HashMap<>();
        report.put("reportDate", date);
        report.put("reportType", "comprehensive-report-from-anonymized-data");
        report.put("region", "EU"); // 添加地区标识
        report.put("generatedAt", new Date());
        report.put("dataCount", anonymizedReservations.size());
        
        // 生成每日总收入统计
        Map<String, Object> dailyIncome = generateDailyTotalIncomeFromData(anonymizedReservations, date);
        report.put("dailyTotalIncome", dailyIncome);
        
        // 生成热门房型Top5统计
        Map<String, Object> popularRoomTypes = generatePopularRoomTypesTop5FromData(anonymizedReservations, date);
        report.put("popularRoomTypesTop5", popularRoomTypes);
        
        // 生成分店业绩统计
        Map<String, Object> branchPerformance = generateBranchPerformanceFromData(anonymizedReservations, date);
        report.put("branchPerformance", branchPerformance);
        
        // 生成预订趋势统计
        Map<String, Object> reservationTrends = generateReservationTrendsFromData(anonymizedReservations, date);
        report.put("reservationTrends", reservationTrends);
        
        log.info("根据脱敏数据生成综合统计报告完成");
        return report;
    }
    
    /**
     * 根据脱敏数据生成每日总收入统计
     */
    private Map<String, Object> generateDailyTotalIncomeFromData(List<Reservation> reservations, LocalDate date) {
        Map<String, Object> dailyIncome = new HashMap<>();
        dailyIncome.put("reportDate", date);
        dailyIncome.put("reportType", "daily-total-income");
        dailyIncome.put("region", "EU"); // 添加地区标识
        
        // 计算总收入
        BigDecimal totalIncome = reservations.stream()
                .filter(r -> "PAID".equals(r.getPaymentStatus()))
                .map(Reservation::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 计算总预订数
        int totalReservations = reservations.size();
        
        // 计算平均每晚价格
        BigDecimal avgPricePerNight = totalReservations > 0 ? 
                totalIncome.divide(BigDecimal.valueOf(totalReservations), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;
        
        dailyIncome.put("totalIncome", totalIncome);
        dailyIncome.put("totalReservations", totalReservations);
        dailyIncome.put("avgPricePerNight", avgPricePerNight);
        dailyIncome.put("currency", "EUR");
        
        return dailyIncome;
    }
    
    /**
     * 根据脱敏数据生成热门房型Top5统计
     */
    private Map<String, Object> generatePopularRoomTypesTop5FromData(List<Reservation> reservations, LocalDate date) {
        Map<String, Object> popularRoomTypes = new HashMap<>();
        popularRoomTypes.put("reportDate", date);
        popularRoomTypes.put("reportType", "popular-room-types-top-5");
        popularRoomTypes.put("region", "EU"); // 添加地区标识
        
        // 按房型分组统计
        Map<Long, List<Reservation>> roomTypeGroups = reservations.stream()
                .filter(r -> "PAID".equals(r.getPaymentStatus()))
                .collect(Collectors.groupingBy(Reservation::getRoomTypeId));
        
        // 计算每个房型的统计信息
        List<Map<String, Object>> top5List = roomTypeGroups.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> roomTypeStats = new HashMap<>();
                    roomTypeStats.put("roomTypeId", entry.getKey());
                    roomTypeStats.put("reservationCount", entry.getValue().size());
                    
                    BigDecimal totalRevenue = entry.getValue().stream()
                            .map(Reservation::getTotalPrice)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    roomTypeStats.put("totalRevenue", totalRevenue);
                    
                    return roomTypeStats;
                })
                .sorted((a, b) -> {
                    Integer countA = (Integer) a.get("reservationCount");
                    Integer countB = (Integer) b.get("reservationCount");
                    return countB.compareTo(countA); // 降序排列
                })
                .limit(5)
                .collect(Collectors.toList());
        
        // 添加排名
        for (int i = 0; i < top5List.size(); i++) {
            top5List.get(i).put("ranking", i + 1);
        }
        
        popularRoomTypes.put("top5RoomTypes", top5List);
        return popularRoomTypes;
    }
    
    /**
     * 根据脱敏数据生成分店业绩统计
     */
    private Map<String, Object> generateBranchPerformanceFromData(List<Reservation> reservations, LocalDate date) {
        Map<String, Object> branchPerformance = new HashMap<>();
        branchPerformance.put("reportDate", date);
        branchPerformance.put("reportType", "branch-performance");
        branchPerformance.put("region", "EU"); // 添加地区标识
        
        // 按分店分组统计
        Map<Long, List<Reservation>> branchGroups = reservations.stream()
                .filter(r -> "PAID".equals(r.getPaymentStatus()))
                .collect(Collectors.groupingBy(Reservation::getBranchId));
        
        List<Map<String, Object>> branchList = branchGroups.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> branchStats = new HashMap<>();
                    branchStats.put("branchId", entry.getKey());
                    branchStats.put("reservationCount", entry.getValue().size());
                    
                    BigDecimal totalRevenue = entry.getValue().stream()
                            .map(Reservation::getTotalPrice)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    branchStats.put("totalRevenue", totalRevenue);
                    
                    return branchStats;
                })
                .collect(Collectors.toList());
        
        branchPerformance.put("branchPerformance", branchList);
        return branchPerformance;
    }
    
    /**
     * 根据脱敏数据生成预订趋势统计
     */
    private Map<String, Object> generateReservationTrendsFromData(List<Reservation> reservations, LocalDate date) {
        Map<String, Object> reservationTrends = new HashMap<>();
        reservationTrends.put("reportDate", date);
        reservationTrends.put("reportType", "reservation-trends");
        reservationTrends.put("region", "EU"); // 添加地区标识
        
        long totalReservations = reservations.size();
        long confirmedReservations = reservations.stream()
                .filter(r -> "CONFIRMED".equals(r.getStatus()))
                .count();
        long cancelledReservations = reservations.stream()
                .filter(r -> "CANCELLED".equals(r.getStatus()))
                .count();
        
        BigDecimal completionRate = totalReservations > 0 ? 
                BigDecimal.valueOf(confirmedReservations)
                        .divide(BigDecimal.valueOf(totalReservations), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)) : 
                BigDecimal.ZERO;
        
        Map<String, Object> trends = new HashMap<>();
        trends.put("totalReservations", totalReservations);
        trends.put("confirmedReservations", confirmedReservations);
        trends.put("cancelledReservations", cancelledReservations);
        trends.put("completionRate", completionRate);
        
        reservationTrends.put("trends", trends);
        return reservationTrends;
    }
}
