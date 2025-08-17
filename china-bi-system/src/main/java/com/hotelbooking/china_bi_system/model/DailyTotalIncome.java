package com.hotelbooking.china_bi_system.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日总收入统计模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("daily_total_income")
public class DailyTotalIncome {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 报告日期 */
    private LocalDate reportDate;
    
    /** 地区标识 (EU, CHINA, US等) */
    private String region;
    
    /** 当日总收入 */
    private BigDecimal totalIncome;
    
    /** 当日总预订数 */
    private Integer totalReservations;
    
    /** 平均每晚价格 */
    private BigDecimal avgPricePerNight;
    
    /** 货币类型 */
    private String currency;
    
    /** 数据来源 */
    private String dataSource;
    
    /** 同步状态 */
    private String syncStatus;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
