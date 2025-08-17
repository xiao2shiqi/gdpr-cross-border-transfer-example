package com.hotelbooking.hotel_reservation_eu.service;

import java.time.LocalDate;
import java.util.Map;

/**
 * BI数据写入服务接口
 * 负责将EU系统的统计数据写入中国BI数据库
 */
public interface BiDataWriteService {

    /**
     * 写入所有统计数据到BI数据库
     * 
     * @param comprehensiveReport 综合统计报告
     * @param date 报告日期
     * @return 是否写入成功
     */
    boolean writeAllStatistics(Map<String, Object> comprehensiveReport, LocalDate date);

    /**
     * 写入每日总收入统计
     * 
     * @param reportData 报告数据
     * @param date 报告日期
     * @return 是否写入成功
     */
    boolean writeDailyTotalIncome(Map<String, Object> reportData, LocalDate date);

    /**
     * 写入热门房型Top5统计
     * 
     * @param reportData 报告数据
     * @param date 报告日期
     * @return 是否写入成功
     */
    boolean writePopularRoomTypesTop5(Map<String, Object> reportData, LocalDate date);

    /**
     * 写入分店业绩统计
     * 
     * @param reportData 报告数据
     * @param date 报告日期
     * @return 是否写入成功
     */
    boolean writeBranchPerformance(Map<String, Object> reportData, LocalDate date);

    /**
     * 写入预订趋势统计
     * 
     * @param reportData 报告数据
     * @param date 报告日期
     * @return 是否写入成功
     */
    boolean writeReservationTrends(Map<String, Object> reportData, LocalDate date);

    /**
     * 写入综合统计报告
     * 
     * @param comprehensiveReport 综合统计报告
     * @param date 报告日期
     * @return 是否写入成功
     */
    boolean writeComprehensiveReport(Map<String, Object> comprehensiveReport, LocalDate date);

    /**
     * 检查BI数据库连接状态
     * 
     * @return 是否连接成功
     */
    boolean checkConnection();
}
