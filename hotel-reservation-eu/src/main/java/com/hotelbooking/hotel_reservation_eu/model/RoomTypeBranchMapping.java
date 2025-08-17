package com.hotelbooking.hotel_reservation_eu.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型分店关联实体类
 * 用于管理房型和分店之间的多对多关系
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("room_type_branch_mapping")
public class RoomTypeBranchMapping {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 房型ID */
    private Long roomTypeId;

    /** 分店ID */
    private Long hotelBranchId;

    /** 是否启用：1-启用，0-停用 */
    private Boolean isActive;

    /** 分店特定价格（可选，为空则使用房型基础价格） */
    private BigDecimal branchSpecificPrice;

    /** 该分店该房型的可用房间数量 */
    private Integer availableRoomsCount;

    /** 该分店该房型的最大房间数量 */
    private Integer maxRoomsCount;

    /** 显示排序，数值越小越靠前 */
    private Integer sortOrder;

    /** 该分店特有的设施（JSON格式，补充房型基础设施） */
    private String specialAmenities;

    /** 备注信息 */
    private String notes;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 创建人ID */
    private Long createdBy;

    /** 更新人ID */
    private Long updatedBy;

    // 关联对象（非数据库字段）
    /** 房型对象 */
    @TableField(exist = false)
    private RoomType roomType;

    /** 分店对象 */
    @TableField(exist = false)
    private HotelBranch hotelBranch;

    /**
     * 获取有效价格
     * 如果设置了分店特定价格则使用特定价格，否则使用房型基础价格
     */
    public BigDecimal getEffectivePrice() {
        if (branchSpecificPrice != null && branchSpecificPrice.compareTo(BigDecimal.ZERO) > 0) {
            return branchSpecificPrice;
        }
        return roomType != null ? roomType.getBasePrice() : BigDecimal.ZERO;
    }

    /**
     * 检查是否有可用房间
     */
    public boolean hasAvailableRooms() {
        return availableRoomsCount != null && availableRoomsCount > 0;
    }

    /**
     * 检查房间是否满房
     */
    public boolean isFullyBooked() {
        return availableRoomsCount != null && availableRoomsCount <= 0;
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
     * 检查是否启用
     */
    public boolean isEnabled() {
        return isActive != null && isActive;
    }

    /**
     * 获取房型和分店的描述性标识
     */
    public String getDisplayIdentifier() {
        String roomTypeName = (roomType != null && roomType.getTypeName() != null) ? 
                             roomType.getTypeName() : "未知房型";
        String branchName = (hotelBranch != null && hotelBranch.getBranchName() != null) ? 
                           hotelBranch.getBranchName() : "未知分店";
        return String.format("%s - %s", branchName, roomTypeName);
    }
}