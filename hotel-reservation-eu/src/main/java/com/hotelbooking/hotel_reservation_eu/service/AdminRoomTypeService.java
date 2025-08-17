package com.hotelbooking.hotel_reservation_eu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 运营端房型管理服务接口
 * 继承MyBatis-Plus的IService接口，提供房型的CRUD操作
 */
public interface AdminRoomTypeService extends IService<RoomType> {

    /**
     * 分页查询房型列表
     */
    IPage<RoomType> getRoomTypePage(int pageNum, int pageSize);

    /**
     * 创建房型
     */
    void createRoomType(RoomType roomType);

    /**
     * 更新房型
     */
    void updateRoomType(RoomType roomType);

    /**
     * 删除房型（软删除）
     */
    void deleteRoomType(Long id);



    /**
     * 获取所有启用的房型
     */
    List<RoomType> getAllActiveRoomTypes();
} 