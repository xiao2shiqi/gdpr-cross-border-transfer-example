package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.mapper.RoomTypeMapper;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import com.hotelbooking.hotel_reservation_eu.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 房型服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeMapper roomTypeMapper;

    @Override
    public RoomType getRoomTypeById(Long id) {
        log.info("根据ID获取房型信息: id={}", id);
        
        try {
            RoomType roomType = roomTypeMapper.selectByIdAndActive(id);
            if (roomType == null) {
                log.warn("房型不存在或已停用: id={}", id);
                return null;
            }
            
            log.info("成功获取房型信息: id={}, name={}", id, roomType.getTypeName());
            return roomType;
            
        } catch (Exception e) {
            log.error("获取房型信息失败: id={}, error={}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean isRoomTypeAvailable(Long id) {
        log.info("检查房型可用性: id={}", id);
        
        try {
            RoomType roomType = roomTypeMapper.selectByIdAndActive(id);
            boolean available = roomType != null;
            
            log.info("房型可用性检查结果: id={}, available={}", id, available);
            return available;
            
        } catch (Exception e) {
            log.error("检查房型可用性失败: id={}, error={}", id, e.getMessage(), e);
            return false;
        }
    }
}
