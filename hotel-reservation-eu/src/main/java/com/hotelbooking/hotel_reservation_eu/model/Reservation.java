package com.hotelbooking.hotel_reservation_eu.model;

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
 * 预订实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("reservation")
public class Reservation {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 房型ID */
    private Long roomTypeId;
    
    /** 分店ID */
    private Long branchId;
    
    /** 入住日期 */
    private LocalDate checkinDate;
    
    /** 退房日期 */
    private LocalDate checkoutDate;
    
    /** 入住人数 */
    private Integer guests;
    
    /** 房间数量 */
    private Integer rooms;
    
    /** 每晚价格 */
    private BigDecimal pricePerNight;
    
    /** 总价格 */
    private BigDecimal totalPrice;
    
    /** 预订状态：PENDING-待确认, CONFIRMED-已确认, CANCELLED-已取消, COMPLETED-已完成 */
    private String status;
    
    /** 支付状态：PENDING-待支付, PAID-已支付, REFUNDED-已退款 */
    private String paymentStatus;
    
    /** 支付方式：CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER */
    private String paymentMethod;
    
    /** 支付时间 */
    private LocalDateTime paymentTime;
    
    /** 特殊要求 */
    private String specialRequests;
    
    /** 联系人姓名 */
    private String contactName;
    
    /** 联系人电话 */
    private String contactPhone;
    
    /** 联系人邮箱 */
    private String contactEmail;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 取消时间 */
    private LocalDateTime cancelledAt;
    
    /** 取消原因 */
    private String cancellationReason;
}
