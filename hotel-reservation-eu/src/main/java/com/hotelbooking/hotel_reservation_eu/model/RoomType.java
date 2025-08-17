package com.hotelbooking.hotel_reservation_eu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型实体类（简化版 - 移除库存字段）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("room_type")
public class RoomType {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 房型名称 */
    private String typeName;
    
    /** 房型描述 */
    private String description;
    
    /** 房间面积(平方米) */
    private Integer size;
    
    /** 床型 */
    private String bedType;
    
    /** 最大入住人数 */
    private Integer maxGuests;
    
    /** 基础价格 */
    private BigDecimal basePrice;
    
    /** 房型图片URL */
    private String imageUrl;
    
    /** 房间设施 */
    private String amenities; // JSON字符串
    
    /** 总房间数 */
    private Integer totalRooms;
    
    /** 状态 1-正常 0-停用 */
    private Integer status;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
} 