package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * 酒店搜索请求DTO
 * 用于接收酒店搜索表单数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSearchRequestDto {

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "入住日期不能为空")
    @FutureOrPresent(message = "入住日期不能早于今天")
    private LocalDate checkinDate;

    @NotNull(message = "退房日期不能为空")
    @Future(message = "退房日期必须晚于今天")
    private LocalDate checkoutDate;

    @NotNull(message = "客人数量不能为空")
    @Min(value = 1, message = "至少需要1位客人")
    @Max(value = 10, message = "客人数量不能超过10位")
    private Integer guests;

    @NotNull(message = "房间数量不能为空")
    @Min(value = 1, message = "至少需要1间房")
    @Max(value = 5, message = "房间数量不能超过5间")
    private Integer rooms;

    // 可选筛选条件
    private String priceRange;
    private String hotelType;
    private String starRating;
    private String[] amenities;

    /**
     * 验证退房日期是否晚于入住日期
     */
    public boolean isDateRangeValid() {
        if (checkinDate == null || checkoutDate == null) {
            return false;
        }
        return checkoutDate.isAfter(checkinDate);
    }

    /**
     * 获取住宿天数
     */
    public long getNumberOfNights() {
        if (checkinDate == null || checkoutDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(checkinDate, checkoutDate);
    }

    /**
     * 检查是否为长期住宿（超过30天）
     */
    public boolean isLongTermStay() {
        return getNumberOfNights() > 30;
    }
} 