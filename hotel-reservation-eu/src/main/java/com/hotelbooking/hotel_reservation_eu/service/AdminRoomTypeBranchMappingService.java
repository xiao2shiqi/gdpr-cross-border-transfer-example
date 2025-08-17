package com.hotelbooking.hotel_reservation_eu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hotelbooking.hotel_reservation_eu.dto.RoomTypeBranchMappingDto;
import com.hotelbooking.hotel_reservation_eu.model.RoomTypeBranchMapping;

import java.util.List;
import java.math.BigDecimal;

/**
 * 运营端房型分店关系管理服务接口
 */
public interface AdminRoomTypeBranchMappingService extends IService<RoomTypeBranchMapping> {

    /**
     * 分页查询房型分店关联列表
     */
    IPage<RoomTypeBranchMappingDto> getMappingPage(
        int pageNum, 
        int pageSize, 
        Long hotelBranchId, 
        Long roomTypeId, 
        Boolean isActive
    );

    /**
     * 创建房型分店关联
     */
    void createMapping(RoomTypeBranchMapping mapping);

    /**
     * 更新房型分店关联
     */
    void updateMapping(RoomTypeBranchMapping mapping);

    /**
     * 删除房型分店关联（软删除）
     */
    void deleteMapping(Long id);

    /**
     * 根据ID获取详细信息
     */
    RoomTypeBranchMappingDto getMappingDetails(Long id);

    /**
     * 根据分店ID查询房型列表
     */
    List<RoomTypeBranchMappingDto> getRoomTypesByBranchId(Long hotelBranchId);

    /**
     * 根据房型ID查询分店列表
     */
    List<RoomTypeBranchMappingDto> getBranchesByRoomTypeId(Long roomTypeId);

    /**
     * 检查房型和分店的关联关系是否已存在
     */
    boolean isMappingExists(Long roomTypeId, Long hotelBranchId, Long excludeId);

    /**
     * 批量更新房间库存
     */
    void batchUpdateRoomCounts(List<RoomTypeBranchMappingDto> mappings);

    /**
     * 启用/禁用关联关系
     */
    void toggleMappingStatus(Long id, Boolean isActive);

    /**
     * 获取分店的房型统计信息
     */
    RoomTypeBranchStatistics getBranchStatistics(Long hotelBranchId);

    /**
     * 获取特定分店和房型的有效价格
     * 优先返回分店特定价格，如果没有则返回房型基础价格
     */
    BigDecimal getEffectivePrice(Long roomTypeId, Long hotelBranchId);

    /**
     * 房型分店统计信息类
     */
    class RoomTypeBranchStatistics {
        private Long totalMappings;      // 总关联数
        private Long activeMappings;     // 启用关联数
        private Long totalRooms;         // 总房间数
        private Long availableRooms;     // 可用房间数
        private Double averageOccupancy; // 平均入住率

        // Getters and Setters
        public Long getTotalMappings() { return totalMappings; }
        public void setTotalMappings(Long totalMappings) { this.totalMappings = totalMappings; }

        public Long getActiveMappings() { return activeMappings; }
        public void setActiveMappings(Long activeMappings) { this.activeMappings = activeMappings; }

        public Long getTotalRooms() { return totalRooms; }
        public void setTotalRooms(Long totalRooms) { this.totalRooms = totalRooms; }

        public Long getAvailableRooms() { return availableRooms; }
        public void setAvailableRooms(Long availableRooms) { this.availableRooms = availableRooms; }

        public Double getAverageOccupancy() { return averageOccupancy; }
        public void setAverageOccupancy(Double averageOccupancy) { this.averageOccupancy = averageOccupancy; }
    }
}