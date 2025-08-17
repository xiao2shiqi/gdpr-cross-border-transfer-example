package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预订响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponseDto {

    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 房型和分店ID */
    private Long roomTypeId;
    private Long branchId;
    
    /** 房型信息 */
    private String roomTypeName;
    private String roomTypeDescription;
    private String roomTypeImageUrl;
    
    /** 分店信息 */
    private String branchName;
    private String branchCity;
    private String branchCountry;
    
    /** 预订详情 */
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private Integer guests;
    private Integer rooms;
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;
    
    /** 状态信息 */
    private String status;
    private String statusDisplay;
    private String paymentStatus;
    private String paymentStatusDisplay;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    
    /** 联系信息 */
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private String specialRequests;
    
    /** 时间信息 */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /** 取消信息 */
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    
    /** 计算入住天数 */
    public long getNights() {
        if (checkinDate != null && checkoutDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(checkinDate, checkoutDate);
        }
        return 0;
    }
    
    /** 获取状态显示名称 */
    public String getStatusDisplayName() {
        switch (status) {
            case "PENDING": return "待确认";
            case "CONFIRMED": return "已确认";
            case "CANCELLED": return "已取消";
            case "COMPLETED": return "已完成";
            default: return status;
        }
    }
    
    /** 获取支付状态显示名称 */
    public String getPaymentStatusDisplayName() {
        switch (paymentStatus) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "REFUNDED": return "已退款";
            default: return paymentStatus;
        }
    }
}
