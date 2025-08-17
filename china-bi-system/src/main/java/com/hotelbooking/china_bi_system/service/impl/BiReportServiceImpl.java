package com.hotelbooking.china_bi_system.service.impl;

import com.hotelbooking.china_bi_system.mapper.DailyTotalIncomeMapper;
import com.hotelbooking.china_bi_system.mapper.PopularRoomTypeMapper;
import com.hotelbooking.china_bi_system.model.DailyTotalIncome;
import com.hotelbooking.china_bi_system.model.PopularRoomType;
import com.hotelbooking.china_bi_system.service.BiReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * BI报表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BiReportServiceImpl implements BiReportService {

    private final DailyTotalIncomeMapper dailyTotalIncomeMapper;
    private final PopularRoomTypeMapper popularRoomTypeMapper;

    @Override
    public List<DailyTotalIncome> getTodayIncome() {
        return dailyTotalIncomeMapper.selectToday();
    }

    @Override
    public DailyTotalIncome getTodayIncomeByRegion(String region) {
        return dailyTotalIncomeMapper.selectTodayByRegion(region);
    }

    @Override
    public List<DailyTotalIncome> getLast7DaysIncome() {
        return dailyTotalIncomeMapper.selectLast7Days();
    }

    @Override
    public List<DailyTotalIncome> getLast7DaysIncomeByRegion(String region) {
        return dailyTotalIncomeMapper.selectLast7DaysByRegion(region);
    }

    @Override
    public List<DailyTotalIncome> getLast30DaysIncome() {
        return dailyTotalIncomeMapper.selectLast30Days();
    }

    @Override
    public List<PopularRoomType> getTodayPopularRoomTypes() {
        return popularRoomTypeMapper.selectToday();
    }

    @Override
    public List<PopularRoomType> getTodayPopularRoomTypesByRegion(String region) {
        return popularRoomTypeMapper.selectTodayByRegion(region);
    }

    @Override
    public List<PopularRoomType> getPopularRoomTypesByDate(LocalDate date) {
        return popularRoomTypeMapper.selectByDate(date);
    }

    @Override
    public List<PopularRoomType> getLast7DaysPopularRoomTypes() {
        return popularRoomTypeMapper.selectLast7Days();
    }

    @Override
    public List<PopularRoomType> getLast7DaysPopularRoomTypesByRegion(String region) {
        return popularRoomTypeMapper.selectLast7DaysByRegion(region);
    }

    @Override
    public Map<String, Object> getComprehensiveOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // 获取所有地区的今日数据
        List<DailyTotalIncome> todayAllRegions = dailyTotalIncomeMapper.selectTodayAllRegions();
        List<PopularRoomType> todayAllRegionsRoomTypes = popularRoomTypeMapper.selectTodayAllRegions();
        
        // 按地区分组
        Map<String, List<DailyTotalIncome>> incomeByRegion = todayAllRegions.stream()
                .collect(Collectors.groupingBy(DailyTotalIncome::getRegion));
        
        Map<String, List<PopularRoomType>> roomTypesByRegion = todayAllRegionsRoomTypes.stream()
                .collect(Collectors.groupingBy(PopularRoomType::getRegion));
        
        // 为每个地区生成统计概览
        Map<String, Map<String, Object>> regionOverviews = new HashMap<>();
        
        for (String region : incomeByRegion.keySet()) {
            Map<String, Object> regionOverview = new HashMap<>();
            
            // 今日收入统计
            List<DailyTotalIncome> regionIncome = incomeByRegion.get(region);
            if (!regionIncome.isEmpty()) {
                DailyTotalIncome todayIncome = regionIncome.get(0);
                regionOverview.put("todayIncome", todayIncome);
                
                // 计算7天累计
                List<DailyTotalIncome> last7Days = getLast7DaysIncomeByRegion(region);
                BigDecimal totalRevenue = last7Days.stream()
                        .map(DailyTotalIncome::getTotalIncome)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                int totalReservations = last7Days.stream()
                        .mapToInt(DailyTotalIncome::getTotalReservations)
                        .sum();
                
                regionOverview.put("totalRevenue7Days", totalRevenue);
                regionOverview.put("totalReservations7Days", totalReservations);
            }
            
            // 今日热门房型
            List<PopularRoomType> regionRoomTypes = roomTypesByRegion.get(region);
            if (regionRoomTypes != null) {
                regionOverview.put("todayPopularRoomTypes", regionRoomTypes);
            }
            
            regionOverviews.put(region, regionOverview);
        }
        
        overview.put("regionOverviews", regionOverviews);
        overview.put("allRegions", new ArrayList<>(incomeByRegion.keySet()));
        
        return overview;
    }

    @Override
    public Map<String, Object> getComprehensiveOverviewByRegion(String region) {
        Map<String, Object> overview = new HashMap<>();
        
        // 今日收入统计
        DailyTotalIncome todayIncome = getTodayIncomeByRegion(region);
        overview.put("todayIncome", todayIncome);
        
        // 最近7天收入统计
        List<DailyTotalIncome> last7DaysIncome = getLast7DaysIncomeByRegion(region);
        overview.put("last7DaysIncome", last7DaysIncome);
        
        // 今日热门房型
        List<PopularRoomType> todayPopularRoomTypes = getTodayPopularRoomTypesByRegion(region);
        overview.put("todayPopularRoomTypes", todayPopularRoomTypes);
        
        // 最近7天热门房型
        List<PopularRoomType> last7DaysPopularRoomTypes = getLast7DaysPopularRoomTypesByRegion(region);
        overview.put("last7DaysPopularRoomTypes", last7DaysPopularRoomTypes);
        
        // 计算总收入
        BigDecimal totalRevenue = last7DaysIncome.stream()
                .map(DailyTotalIncome::getTotalIncome)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        overview.put("totalRevenue7Days", totalRevenue);
        
        // 计算总预订数
        int totalReservations = last7DaysIncome.stream()
                .mapToInt(DailyTotalIncome::getTotalReservations)
                .sum();
        overview.put("totalReservations7Days", totalReservations);
        
        return overview;
    }
}
