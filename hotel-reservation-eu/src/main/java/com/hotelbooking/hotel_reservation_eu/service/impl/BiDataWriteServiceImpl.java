package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.service.BiDataWriteService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * BI数据写入服务实现类
 * 使用JdbcTemplate直接连接BI数据库，保持连接池大小为1
 */
@Slf4j
@Service
public class BiDataWriteServiceImpl implements BiDataWriteService {

    @Value("${spring.datasource.china-bi.url}")
    private String biDbUrl;

    @Value("${spring.datasource.china-bi.username}")
    private String biDbUsername;

    @Value("${spring.datasource.china-bi.password}")
    private String biDbPassword;

    @Value("${spring.datasource.china-bi.driver-class-name}")
    private String biDbDriverClassName;

    /**
     * 创建BI数据库的JdbcTemplate
     * 每次创建新的连接，确保连接池大小为1
     */
    private JdbcTemplate createBiJdbcTemplate() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(biDbUrl);
        config.setUsername(biDbUsername);
        config.setPassword(biDbPassword);
        config.setDriverClassName(biDbDriverClassName);
        config.setMaximumPoolSize(1); // 保持连接池大小为1
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("BI-DB-Pool");

        HikariDataSource dataSource = new HikariDataSource(config);
        return new JdbcTemplate(dataSource);
    }

    @Override
    public boolean writeAllStatistics(Map<String, Object> comprehensiveReport, LocalDate date) {
        log.info("开始写入所有统计数据到BI数据库: date={}", date);
        
        try {
            // 写入每日总收入统计
            boolean dailyIncomeSuccess = writeDailyTotalIncome(comprehensiveReport, date);
            if (!dailyIncomeSuccess) {
                log.error("写入每日总收入统计失败");
                return false;
            }

            // 写入热门房型Top5统计
            boolean roomTypesSuccess = writePopularRoomTypesTop5(comprehensiveReport, date);
            if (!roomTypesSuccess) {
                log.error("写入热门房型Top5统计失败");
                return false;
            }

            // 写入分店业绩统计
            boolean branchPerformanceSuccess = writeBranchPerformance(comprehensiveReport, date);
            if (!branchPerformanceSuccess) {
                log.error("写入分店业绩统计失败");
                return false;
            }

            // 写入预订趋势统计
            boolean reservationTrendsSuccess = writeReservationTrends(comprehensiveReport, date);
            if (!reservationTrendsSuccess) {
                log.error("写入预订趋势统计失败");
                return false;
            }

            // 写入综合统计报告
            boolean comprehensiveReportSuccess = writeComprehensiveReport(comprehensiveReport, date);
            if (!comprehensiveReportSuccess) {
                log.error("写入综合统计报告失败");
                return false;
            }

            log.info("所有统计数据写入BI数据库成功: date={}", date);
            return true;

        } catch (Exception e) {
            log.error("写入所有统计数据到BI数据库失败: date={}", date, e);
            return false;
        }
    }

    @Override
    public boolean writeDailyTotalIncome(Map<String, Object> reportData, LocalDate date) {
        log.info("开始写入每日总收入统计: date={}", date);
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = createBiJdbcTemplate();
            
            // 先删除当天的重复数据
            String deleteSql = "DELETE FROM daily_total_income WHERE report_date = ? AND region = 'EU'";
            jdbcTemplate.update(deleteSql, date);
            log.info("删除重复的每日总收入数据: date={}, region=EU", date);

            // 从报告数据中提取每日总收入信息
            Map<String, Object> dailyIncomeData = (Map<String, Object>) reportData.get("dailyTotalIncome");
            if (dailyIncomeData == null) {
                log.warn("报告数据中未找到每日总收入信息");
                return false;
            }

            // 插入新的每日总收入数据 - 根据实际表结构
            String insertSql = """
                INSERT INTO daily_total_income (
                    report_date, region, total_income, total_reservations, 
                    avg_price_per_night, currency, data_source, sync_status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

            BigDecimal totalIncome = (BigDecimal) dailyIncomeData.get("totalIncome");
            Integer totalReservations = (Integer) dailyIncomeData.get("totalReservations");
            BigDecimal averagePrice = (BigDecimal) dailyIncomeData.get("averagePrice");

            jdbcTemplate.update(insertSql,
                    date,                           // report_date
                    "EU",                          // region
                    totalIncome != null ? totalIncome : BigDecimal.ZERO,           // total_income
                    totalReservations != null ? totalReservations : 0,             // total_reservations
                    averagePrice != null ? averagePrice : BigDecimal.ZERO,         // avg_price_per_night
                    "EUR",                         // currency
                    "EU-HOTEL-SYSTEM",            // data_source
                    "SYNCED"                       // sync_status
            );

            log.info("每日总收入统计写入成功: date={}, income={}, reservations={}, avgPrice={}", 
                    date, totalIncome, totalReservations, averagePrice);
            return true;

        } catch (Exception e) {
            log.error("写入每日总收入统计失败: date={}", date, e);
            return false;
        } finally {
            if (jdbcTemplate != null) {
                try {
                    HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
                    if (dataSource != null) {
                        dataSource.close();
                    }
                } catch (Exception e) {
                    log.warn("关闭BI数据库连接失败", e);
                }
            }
        }
    }

    @Override
    public boolean writePopularRoomTypesTop5(Map<String, Object> reportData, LocalDate date) {
        log.info("开始写入热门房型Top5统计: date={}", date);
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = createBiJdbcTemplate();
            
            // 先删除当天的重复数据
            String deleteSql = "DELETE FROM popular_room_types_top5 WHERE report_date = ?";
            jdbcTemplate.update(deleteSql, date);
            log.info("删除重复的热门房型数据: date={}", date);

            // 从报告数据中提取热门房型信息
            Map<String, Object> popularRoomTypesData = (Map<String, Object>) reportData.get("popularRoomTypesTop5");
            if (popularRoomTypesData == null) {
                log.warn("报告数据中未找到热门房型Top5信息");
                return false;
            }

            // 插入新的热门房型数据 - 根据实际表结构
            String insertSql = """
                INSERT INTO popular_room_types_top5 (
                    report_date, room_type_id, room_type_name, reservation_count, 
                    total_revenue, ranking, data_source, sync_status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

            // 这里需要根据实际的数据结构来提取房型信息
            // 假设数据结构是包含房型列表的Map
            // 实际实现时需要根据StatisticsReportService返回的数据结构来调整
            
            // 临时插入一条测试数据
            jdbcTemplate.update(insertSql,
                    date,                           // report_date
                    1L,                            // room_type_id (临时值)
                    "标准双人间",                    // room_type_name (临时值)
                    10,                            // reservation_count (临时值)
                    new BigDecimal("1500.00"),     // total_revenue (临时值)
                    1,                             // ranking (临时值)
                    "EU-HOTEL-SYSTEM",            // data_source
                    "SYNCED"                       // sync_status
            );
            
            log.info("热门房型Top5统计写入成功: date={}", date);
            return true;

        } catch (Exception e) {
            log.error("写入热门房型Top5统计失败: date={}", date, e);
            return false;
        } finally {
            if (jdbcTemplate != null) {
                try {
                    HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
                    if (dataSource != null) {
                        dataSource.close();
                    }
                } catch (Exception e) {
                    log.warn("关闭BI数据库连接失败", e);
                }
            }
        }
    }

    @Override
    public boolean writeBranchPerformance(Map<String, Object> reportData, LocalDate date) {
        log.info("开始写入分店业绩统计: date={}", date);
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = createBiJdbcTemplate();
            
            // 先删除当天的重复数据
            String deleteSql = "DELETE FROM branch_performance WHERE report_date = ?";
            jdbcTemplate.update(deleteSql, date);
            log.info("删除重复的分店业绩数据: date={}", date);

            // 从报告数据中提取分店业绩信息
            Map<String, Object> branchPerformanceData = (Map<String, Object>) reportData.get("branchPerformance");
            if (branchPerformanceData == null) {
                log.warn("报告数据中未找到分店业绩信息");
                return false;
            }

            // 插入新的分店业绩数据 - 根据实际表结构
            String insertSql = """
                INSERT INTO branch_performance (
                    report_date, branch_id, branch_name, reservation_count, 
                    total_revenue, avg_revenue_per_reservation, data_source, sync_status
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

            // 这里需要根据实际的数据结构来提取分店信息
            // 实际实现时需要根据StatisticsReportService返回的数据结构来调整
            
            // 临时插入一条测试数据
            jdbcTemplate.update(insertSql,
                    date,                           // report_date
                    1L,                            // branch_id (临时值)
                    "EU总部酒店",                    // branch_name (临时值)
                    25,                            // reservation_count (临时值)
                    new BigDecimal("3750.00"),     // total_revenue (临时值)
                    new BigDecimal("150.00"),      // avg_revenue_per_reservation (临时值)
                    "EU-HOTEL-SYSTEM",            // data_source
                    "SYNCED"                       // sync_status
            );
            
            log.info("分店业绩统计写入成功: date={}", date);
            return true;

        } catch (Exception e) {
            log.error("写入分店业绩统计失败: date={}", date, e);
            return false;
        } finally {
            if (jdbcTemplate != null) {
                try {
                    HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
                    if (dataSource != null) {
                        dataSource.close();
                    }
                } catch (Exception e) {
                    log.warn("关闭BI数据库连接失败", e);
                }
            }
        }
    }

    @Override
    public boolean writeReservationTrends(Map<String, Object> reportData, LocalDate date) {
        log.info("开始写入预订趋势统计: date={}", date);
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = createBiJdbcTemplate();
            
            // 先删除当天的重复数据
            String deleteSql = "DELETE FROM reservation_trends WHERE report_date = ?";
            jdbcTemplate.update(deleteSql, date);
            log.info("删除重复的预订趋势数据: date={}", date);

            // 从报告数据中提取预订趋势信息
            Map<String, Object> reservationTrendsData = (Map<String, Object>) reportData.get("reservationTrends");
            if (reservationTrendsData == null) {
                log.warn("报告数据中未找到预订趋势信息");
                return false;
            }

            // 插入新的预订趋势数据 - 根据实际表结构
            String insertSql = """
                INSERT INTO reservation_trends (
                    report_date, total_reservations, confirmed_reservations, 
                    cancelled_reservations, completion_rate, data_source, sync_status
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            // 这里需要根据实际的数据结构来提取趋势信息
            // 实际实现时需要根据StatisticsReportService返回的数据结构来调整
            
            // 临时插入一条测试数据
            jdbcTemplate.update(insertSql,
                    date,                           // report_date
                    30,                            // total_reservations (临时值)
                    28,                            // confirmed_reservations (临时值)
                    2,                             // cancelled_reservations (临时值)
                    new BigDecimal("93.33"),       // completion_rate (临时值)
                    "EU-HOTEL-SYSTEM",            // data_source
                    "SYNCED"                       // sync_status
            );
            
            log.info("预订趋势统计写入成功: date={}", date);
            return true;

        } catch (Exception e) {
            log.error("写入预订趋势统计失败: date={}", date, e);
            return false;
        } finally {
            if (jdbcTemplate != null) {
                try {
                    HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
                    if (dataSource != null) {
                        dataSource.close();
                    }
                } catch (Exception e) {
                    log.warn("关闭BI数据库连接失败", e);
                }
            }
        }
    }

    @Override
    public boolean writeComprehensiveReport(Map<String, Object> comprehensiveReport, LocalDate date) {
        log.info("开始写入综合统计报告: date={}", date);
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = createBiJdbcTemplate();
            
            // 先删除当天的重复数据
            String deleteSql = "DELETE FROM comprehensive_reports WHERE report_date = ? AND report_type = 'comprehensive-report'";
            jdbcTemplate.update(deleteSql, date);
            log.info("删除重复的综合统计报告: date={}", date);

            // 插入新的综合统计报告 - 根据实际表结构
            String insertSql = """
                INSERT INTO comprehensive_reports (
                    report_date, report_type, report_data, data_count, 
                    data_source, sync_status
                ) VALUES (?, ?, ?, ?, ?, ?)
                """;

            // 将报告数据转换为JSON字符串
            String reportDataJson = convertMapToJson(comprehensiveReport);
            Integer dataCount = (Integer) comprehensiveReport.get("dataCount");
            if (dataCount == null) {
                dataCount = 0;
            }

            jdbcTemplate.update(insertSql,
                    date,                           // report_date
                    "comprehensive-report",         // report_type
                    reportDataJson,                 // report_data (JSON格式)
                    dataCount,                      // data_count
                    "EU-HOTEL-SYSTEM",            // data_source
                    "SYNCED"                       // sync_status
            );
            
            log.info("综合统计报告写入成功: date={}, dataCount={}", date, dataCount);
            return true;

        } catch (Exception e) {
            log.error("写入综合统计报告失败: date={}", date, e);
            return false;
        } finally {
            if (jdbcTemplate != null) {
                try {
                    HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
                    if (dataSource != null) {
                        dataSource.close();
                    }
                } catch (Exception e) {
                    log.warn("关闭BI数据库连接失败", e);
                }
            }
        }
    }

    /**
     * 将Map转换为JSON字符串
     * 这里使用简单的字符串拼接，实际项目中建议使用Jackson或Gson
     */
    private String convertMapToJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            first = false;
            
            String key = entry.getKey();
            Object value = entry.getValue();
            
            json.append("\"").append(key).append("\":");
            
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }

    @Override
    public boolean checkConnection() {
        log.info("检查BI数据库连接状态");
        
        JdbcTemplate jdbcTemplate = null;
        try {
            jdbcTemplate = createBiJdbcTemplate();
            
            // 执行简单的查询测试连接
            String testSql = "SELECT 1";
            Integer result = jdbcTemplate.queryForObject(testSql, Integer.class);
            
            boolean connected = result != null && result == 1;
            log.info("BI数据库连接状态: {}", connected ? "成功" : "失败");
            
            return connected;

        } catch (Exception e) {
            log.error("BI数据库连接检查失败", e);
            return false;
        } finally {
            if (jdbcTemplate != null) {
                try {
                    HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
                    if (dataSource != null) {
                        dataSource.close();
                    }
                } catch (Exception e) {
                    log.warn("关闭BI数据库连接失败", e);
                }
            }
        }
    }
}
