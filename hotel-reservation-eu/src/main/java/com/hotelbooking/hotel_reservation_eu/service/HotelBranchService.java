package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;

/**
 * 酒店分店服务接口
 */
public interface HotelBranchService {
    
    /**
     * 根据ID获取分店信息
     */
    HotelBranch getHotelBranchById(Long id);
    
    /**
     * 检查分店是否存在且可用
     */
    boolean isHotelBranchAvailable(Long id);
}
