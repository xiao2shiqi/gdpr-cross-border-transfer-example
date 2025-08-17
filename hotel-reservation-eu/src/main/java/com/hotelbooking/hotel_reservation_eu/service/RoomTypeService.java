package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.model.RoomType;

/**
 * 房型服务接口
 */
public interface RoomTypeService {
    
    /**
     * 根据ID获取房型信息
     */
    RoomType getRoomTypeById(Long id);
    
    /**
     * 检查房型是否存在且可用
     */
    boolean isRoomTypeAvailable(Long id);
}
