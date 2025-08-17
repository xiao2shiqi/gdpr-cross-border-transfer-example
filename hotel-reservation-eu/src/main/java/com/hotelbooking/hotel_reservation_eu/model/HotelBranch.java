package com.hotelbooking.hotel_reservation_eu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 酒店分店实体类（简化版 - 移除分店代码字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("hotel_branch")
public class HotelBranch {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 分店名称 */
    private String branchName;
    
    /** 城市 */
    private String city;
    
    /** 国家 */
    private String country;
    
    /** 状态 1-正常 0-停用 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
} 