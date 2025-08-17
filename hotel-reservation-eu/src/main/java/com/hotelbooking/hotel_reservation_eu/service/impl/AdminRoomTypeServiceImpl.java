package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotelbooking.hotel_reservation_eu.mapper.AdminRoomTypeMapper;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import com.hotelbooking.hotel_reservation_eu.service.AdminRoomTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 运营端房型管理服务实现
 * 继承ServiceImpl，使用MyBatis-Plus提供的通用CRUD方法
 */
@Slf4j
@Service
public class AdminRoomTypeServiceImpl extends ServiceImpl<AdminRoomTypeMapper, RoomType> implements AdminRoomTypeService {

    /**
     * 分页查询房型列表
     */
    @Override
    public IPage<RoomType> getRoomTypePage(int pageNum, int pageSize) {
        Page<RoomType> page = new Page<>(pageNum, pageSize);
        QueryWrapper<RoomType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                    .orderByDesc("id");
        return this.page(page, queryWrapper);
    }

    /**
     * 创建房型
     */
    @Override
    @Transactional
    public void createRoomType(RoomType roomType) {
        roomType.setStatus(1); // 启用状态
        roomType.setCreatedAt(LocalDateTime.now());
        roomType.setUpdatedAt(LocalDateTime.now());
        
        this.save(roomType);
        log.info("创建房型成功 - 房型名称: {}", roomType.getTypeName());
    }

    /**
     * 更新房型
     */
    @Override
    @Transactional
    public void updateRoomType(RoomType roomType) {
        RoomType existingRoomType = this.getById(roomType.getId());
        if (existingRoomType == null) {
            throw new IllegalArgumentException("房型不存在");
        }

        roomType.setUpdatedAt(LocalDateTime.now());
        this.updateById(roomType);
        log.info("更新房型成功 - 房型ID: {}, 房型名称: {}", roomType.getId(), roomType.getTypeName());
    }

    /**
     * 删除房型（软删除）
     */
    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        RoomType roomType = this.getById(id);
        if (roomType == null) {
            throw new IllegalArgumentException("房型不存在");
        }

        roomType.setStatus(0); // 设置为禁用状态
        roomType.setUpdatedAt(LocalDateTime.now());
        this.updateById(roomType);
        log.info("删除房型成功 - 房型ID: {}, 房型名称: {}", id, roomType.getTypeName());
    }



    /**
     * 获取所有启用的房型
     */
    @Override
    public List<RoomType> getAllActiveRoomTypes() {
        QueryWrapper<RoomType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                    .orderByDesc("id");
        return this.list(queryWrapper);
    }
} 