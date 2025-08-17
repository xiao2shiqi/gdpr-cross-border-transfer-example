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
 * 热门房型统计模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("popular_room_types_top5")
public class PopularRoomType {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 报告日期 */
    private LocalDate reportDate;
    
    /** 地区标识 (EU, CHINA, US等) */
    private String region;
    
    /** 房型ID */
    private Long roomTypeId;
    
    /** 房型名称 */
    private String roomTypeName;
    
    /** 预订数量 */
    private Integer reservationCount;
    
    /** 总收入 */
    private BigDecimal totalRevenue;
    
    /** 排名 */
    private Integer ranking;
    
    /** 数据来源 */
    private String dataSource;
    
    /** 同步状态 */
    private String syncStatus;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
}
