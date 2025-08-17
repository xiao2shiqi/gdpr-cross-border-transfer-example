package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.mapper.HotelBranchMapper;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.hotelbooking.hotel_reservation_eu.service.HotelBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 酒店分店服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HotelBranchServiceImpl implements HotelBranchService {

    private final HotelBranchMapper hotelBranchMapper;

    @Override
    public HotelBranch getHotelBranchById(Long id) {
        log.info("根据ID获取分店信息: id={}", id);
        
        try {
            HotelBranch hotelBranch = hotelBranchMapper.selectByIdAndActive(id);
            if (hotelBranch == null) {
                log.warn("分店不存在或已停用: id={}", id);
                return null;
            }
            
            log.info("成功获取分店信息: id={}, name={}", id, hotelBranch.getBranchName());
            return hotelBranch;
            
        } catch (Exception e) {
            log.error("获取分店信息失败: id={}, error={}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean isHotelBranchAvailable(Long id) {
        log.info("检查分店可用性: id={}", id);
        
        try {
            HotelBranch hotelBranch = hotelBranchMapper.selectByIdAndActive(id);
            boolean available = hotelBranch != null;
            
            log.info("分店可用性检查结果: id={}, available={}", id, available);
            return available;
            
        } catch (Exception e) {
            log.error("检查分店可用性失败: id={}, error={}", id, e.getMessage(), e);
            return false;
        }
    }
}
