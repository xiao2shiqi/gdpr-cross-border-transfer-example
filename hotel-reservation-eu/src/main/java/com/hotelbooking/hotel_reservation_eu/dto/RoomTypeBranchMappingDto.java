package com.hotelbooking.hotel_reservation_eu.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 房型分店关联数据传输对象
 * 用于前端表单数据传输和API接口
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeBranchMappingDto {

    private Long id;

    @NotNull(message = "房型ID不能为空")
    private Long roomTypeId;

    @NotNull(message = "分店ID不能为空")
    private Long hotelBranchId;

    private Boolean isActive = true;

    @PositiveOrZero(message = "分店特定价格不能为负数")
    private BigDecimal branchSpecificPrice;

    @NotNull(message = "可用房间数量不能为空")
    @PositiveOrZero(message = "可用房间数量不能为负数")
    private Integer availableRoomsCount;

    @NotNull(message = "最大房间数量不能为空")
    @Min(value = 1, message = "最大房间数量至少为1")
    private Integer maxRoomsCount;

    private Integer sortOrder = 0;

    private String specialAmenities;

    private String notes;

    // 关联对象信息（用于显示）
    private String roomTypeName;
    private String roomTypeDescription;
    private String bedType;
    private Integer size;
    private Integer maxGuests;
    private String imageUrl;
    private String amenities;
    private String hotelBranchName;
    private String hotelBranchCity;
    private String hotelBranchCountry;
    private BigDecimal roomTypeBasePrice;

    /**
     * 获取有效价格
     */
    public BigDecimal getEffectivePrice() {
        if (branchSpecificPrice != null && branchSpecificPrice.compareTo(BigDecimal.ZERO) > 0) {
            return branchSpecificPrice;
        }
        return roomTypeBasePrice != null ? roomTypeBasePrice : BigDecimal.ZERO;
    }

    /**
     * 获取入住率百分比
     */
    public double getOccupancyRate() {
        if (maxRoomsCount == null || maxRoomsCount <= 0) {
            return 0.0;
        }
        if (availableRoomsCount == null) {
            return 100.0;
        }
        int occupiedRooms = maxRoomsCount - availableRoomsCount;
        return (double) occupiedRooms / maxRoomsCount * 100.0;
    }

    /**
     * 检查数据有效性
     */
    public boolean isValid() {
        return roomTypeId != null && 
               hotelBranchId != null && 
               maxRoomsCount != null && maxRoomsCount > 0 &&
               availableRoomsCount != null && availableRoomsCount >= 0 &&
               availableRoomsCount <= maxRoomsCount;
    }

    /**
     * 获取显示标识
     */
    public String getDisplayIdentifier() {
        String roomName = (roomTypeName != null) ? roomTypeName : "未知房型";
        String branchName = (hotelBranchName != null) ? hotelBranchName : "未知分店";
        return String.format("%s - %s", branchName, roomName);
    }
}