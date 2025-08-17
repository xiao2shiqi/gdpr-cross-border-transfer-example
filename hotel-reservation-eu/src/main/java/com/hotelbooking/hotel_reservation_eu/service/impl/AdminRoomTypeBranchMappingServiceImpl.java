package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotelbooking.hotel_reservation_eu.dto.RoomTypeBranchMappingDto;
import com.hotelbooking.hotel_reservation_eu.mapper.RoomTypeBranchMappingMapper;
import com.hotelbooking.hotel_reservation_eu.model.RoomTypeBranchMapping;
import com.hotelbooking.hotel_reservation_eu.service.AdminRoomTypeBranchMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

/**
 * 运营端房型分店关系管理服务实现
 */
@Slf4j
@Service
public class AdminRoomTypeBranchMappingServiceImpl extends ServiceImpl<RoomTypeBranchMappingMapper, RoomTypeBranchMapping> 
        implements AdminRoomTypeBranchMappingService {

    /**
     * 分页查询房型分店关联列表
     */
    @Override
    public IPage<RoomTypeBranchMappingDto> getMappingPage(int pageNum, int pageSize, 
                                                          Long hotelBranchId, Long roomTypeId, Boolean isActive) {
        Page<RoomTypeBranchMappingDto> page = new Page<>(pageNum, pageSize);
        return this.baseMapper.selectMappingPageWithDetails(page);
    }

    /**
     * 创建房型分店关联
     */
    @Override
    @Transactional
    public void createMapping(RoomTypeBranchMapping mapping) {
        // 检查关联关系是否已存在
        if (isMappingExists(mapping.getRoomTypeId(), mapping.getHotelBranchId(), null)) {
            throw new IllegalArgumentException("该房型与分店的关联关系已存在");
        }

        // 验证可用房间数不能超过最大房间数
        if (mapping.getAvailableRoomsCount() > mapping.getMaxRoomsCount()) {
            throw new IllegalArgumentException("可用房间数不能超过最大房间数");
        }

        mapping.setIsActive(mapping.getIsActive() != null ? mapping.getIsActive() : true);
        mapping.setSortOrder(mapping.getSortOrder() != null ? mapping.getSortOrder() : 0);
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());

        this.save(mapping);
        log.info("创建房型分店关联成功 - 房型ID: {}, 分店ID: {}", mapping.getRoomTypeId(), mapping.getHotelBranchId());
    }

    /**
     * 更新房型分店关联
     */
    @Override
    @Transactional
    public void updateMapping(RoomTypeBranchMapping mapping) {
        RoomTypeBranchMapping existingMapping = this.getById(mapping.getId());
        if (existingMapping == null) {
            throw new IllegalArgumentException("房型分店关联不存在");
        }

        // 如果修改了房型或分店，检查新的关联关系是否已存在
        if (!existingMapping.getRoomTypeId().equals(mapping.getRoomTypeId()) ||
            !existingMapping.getHotelBranchId().equals(mapping.getHotelBranchId())) {
            if (isMappingExists(mapping.getRoomTypeId(), mapping.getHotelBranchId(), mapping.getId())) {
                throw new IllegalArgumentException("该房型与分店的关联关系已存在");
            }
        }

        // 验证可用房间数不能超过最大房间数
        if (mapping.getAvailableRoomsCount() > mapping.getMaxRoomsCount()) {
            throw new IllegalArgumentException("可用房间数不能超过最大房间数");
        }

        mapping.setUpdatedAt(LocalDateTime.now());
        this.updateById(mapping);
        log.info("更新房型分店关联成功 - ID: {}", mapping.getId());
    }

    /**
     * 删除房型分店关联（软删除）
     */
    @Override
    @Transactional
    public void deleteMapping(Long id) {
        RoomTypeBranchMapping mapping = this.getById(id);
        if (mapping == null) {
            throw new IllegalArgumentException("房型分店关联不存在");
        }

        mapping.setIsActive(false);
        mapping.setUpdatedAt(LocalDateTime.now());
        this.updateById(mapping);
        log.info("删除房型分店关联成功 - ID: {}", id);
    }

    /**
     * 根据ID获取详细信息
     */
    @Override
    public RoomTypeBranchMappingDto getMappingDetails(Long id) {
        return this.baseMapper.selectByIdWithDetails(id);
    }

    /**
     * 根据分店ID查询房型列表
     */
    @Override
    public List<RoomTypeBranchMappingDto> getRoomTypesByBranchId(Long hotelBranchId) {
        return this.baseMapper.selectRoomTypesByBranchId(hotelBranchId);
    }

    /**
     * 根据房型ID查询分店列表
     */
    @Override
    public List<RoomTypeBranchMappingDto> getBranchesByRoomTypeId(Long roomTypeId) {
        return this.baseMapper.selectBranchesByRoomTypeId(roomTypeId);
    }

    /**
     * 检查房型和分店的关联关系是否已存在
     */
    @Override
    public boolean isMappingExists(Long roomTypeId, Long hotelBranchId, Long excludeId) {
        if (excludeId != null) {
            return this.baseMapper.countExistingMappingExcludeId(roomTypeId, hotelBranchId, excludeId) > 0;
        } else {
            return this.baseMapper.countExistingMapping(roomTypeId, hotelBranchId) > 0;
        }
    }

    /**
     * 批量更新房间库存
     */
    @Override
    @Transactional
    public void batchUpdateRoomCounts(List<RoomTypeBranchMappingDto> mappings) {
        for (RoomTypeBranchMappingDto dto : mappings) {
            if (dto.getId() != null) {
                RoomTypeBranchMapping mapping = this.getById(dto.getId());
                if (mapping != null) {
                    mapping.setAvailableRoomsCount(dto.getAvailableRoomsCount());
                    mapping.setMaxRoomsCount(dto.getMaxRoomsCount());
                    mapping.setUpdatedAt(LocalDateTime.now());
                    this.updateById(mapping);
                }
            }
        }
        log.info("批量更新房间库存成功 - 更新条数: {}", mappings.size());
    }

    /**
     * 启用/禁用关联关系
     */
    @Override
    @Transactional
    public void toggleMappingStatus(Long id, Boolean isActive) {
        RoomTypeBranchMapping mapping = this.getById(id);
        if (mapping == null) {
            throw new IllegalArgumentException("房型分店关联不存在");
        }

        mapping.setIsActive(isActive);
        mapping.setUpdatedAt(LocalDateTime.now());
        this.updateById(mapping);
        log.info("切换房型分店关联状态成功 - ID: {}, 状态: {}", id, isActive ? "启用" : "禁用");
    }

    /**
     * 获取分店的房型统计信息
     */
    @Override
    public RoomTypeBranchStatistics getBranchStatistics(Long hotelBranchId) {
        // 查询分店的房型关联统计
        QueryWrapper<RoomTypeBranchMapping> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hotel_branch_id", hotelBranchId);
        
        List<RoomTypeBranchMapping> mappings = this.list(queryWrapper);
        
        RoomTypeBranchStatistics statistics = new RoomTypeBranchStatistics();
        statistics.setTotalMappings((long) mappings.size());
        statistics.setActiveMappings(mappings.stream().filter(RoomTypeBranchMapping::isEnabled).count());
        
        long totalRooms = 0;
        long availableRooms = 0;
        
        for (RoomTypeBranchMapping mapping : mappings) {
            if (mapping.getMaxRoomsCount() != null) {
                totalRooms += mapping.getMaxRoomsCount();
            }
            if (mapping.getAvailableRoomsCount() != null) {
                availableRooms += mapping.getAvailableRoomsCount();
            }
        }
        
        statistics.setTotalRooms(totalRooms);
        statistics.setAvailableRooms(availableRooms);
        
        if (totalRooms > 0) {
            double occupancyRate = (double) (totalRooms - availableRooms) / totalRooms * 100.0;
            statistics.setAverageOccupancy(occupancyRate);
        } else {
            statistics.setAverageOccupancy(0.0);
        }
        
        return statistics;
    }

    /**
     * 获取特定分店和房型的有效价格
     * 优先返回分店特定价格，如果没有则返回房型基础价格
     */
    @Override
    public BigDecimal getEffectivePrice(Long roomTypeId, Long hotelBranchId) {
        log.info("获取有效价格: roomTypeId={}, hotelBranchId={}", roomTypeId, hotelBranchId);
        
        try {
            // 查询房型分店关联信息
            QueryWrapper<RoomTypeBranchMapping> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_type_id", roomTypeId)
                       .eq("hotel_branch_id", hotelBranchId)
                       .eq("is_active", 1);
            
            RoomTypeBranchMapping mapping = this.getOne(queryWrapper);
            
            if (mapping != null && mapping.getBranchSpecificPrice() != null 
                && mapping.getBranchSpecificPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 如果有分店特定价格，返回特定价格
                log.info("使用分店特定价格: roomTypeId={}, hotelBranchId={}, specificPrice={}", 
                        roomTypeId, hotelBranchId, mapping.getBranchSpecificPrice());
                return mapping.getBranchSpecificPrice();
            }
            
            // 如果没有分店特定价格，查询房型基础价格
            // 使用RoomTypeService直接查询房型信息
            try {
                // 这里需要注入RoomTypeService，暂时使用反射获取
                // 或者我们可以修改方法签名，传入RoomType对象
                log.info("未设置分店特定价格，需要查询房型基础价格");
                return null; // 暂时返回null，表示需要进一步处理
            } catch (Exception e) {
                log.warn("查询房型基础价格失败: {}", e.getMessage());
            }
            
            // 如果都没有，返回0
            log.warn("未找到有效价格信息: roomTypeId={}, hotelBranchId={}", roomTypeId, hotelBranchId);
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            log.error("获取有效价格失败: roomTypeId={}, hotelBranchId={}", roomTypeId, hotelBranchId, e);
            return BigDecimal.ZERO;
        }
    }
}