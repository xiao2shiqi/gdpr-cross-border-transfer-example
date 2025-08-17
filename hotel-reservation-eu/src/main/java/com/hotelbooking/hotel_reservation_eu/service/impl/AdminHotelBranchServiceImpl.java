package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotelbooking.hotel_reservation_eu.mapper.AdminHotelBranchMapper;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.hotelbooking.hotel_reservation_eu.service.AdminHotelBranchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 运营端分店管理服务实现
 * 继承ServiceImpl，使用MyBatis-Plus提供的通用CRUD方法
 */
@Slf4j
@Service
public class AdminHotelBranchServiceImpl extends ServiceImpl<AdminHotelBranchMapper, HotelBranch> implements AdminHotelBranchService {

    /**
     * 分页查询分店列表
     */
    @Override
    public IPage<HotelBranch> getHotelBranchPage(int pageNum, int pageSize) {
        Page<HotelBranch> page = new Page<>(pageNum, pageSize);
        QueryWrapper<HotelBranch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                    .orderByDesc("id");
        return this.page(page, queryWrapper);
    }

    /**
     * 创建分店
     */
    @Override
    @Transactional
    public void createHotelBranch(HotelBranch hotelBranch) {
        hotelBranch.setStatus(1); // 启用状态
        hotelBranch.setCreatedAt(LocalDateTime.now());
        hotelBranch.setUpdatedAt(LocalDateTime.now());
        
        this.save(hotelBranch);
        log.info("创建分店成功 - 分店名称: {}", hotelBranch.getBranchName());
    }

    /**
     * 更新分店
     */
    @Override
    @Transactional
    public void updateHotelBranch(HotelBranch hotelBranch) {
        HotelBranch existingBranch = this.getById(hotelBranch.getId());
        if (existingBranch == null) {
            throw new IllegalArgumentException("分店不存在");
        }

        hotelBranch.setUpdatedAt(LocalDateTime.now());
        this.updateById(hotelBranch);
        log.info("更新分店成功 - 分店ID: {}, 分店名称: {}", hotelBranch.getId(), hotelBranch.getBranchName());
    }

    /**
     * 删除分店（软删除）
     */
    @Override
    @Transactional
    public void deleteHotelBranch(Long id) {
        HotelBranch hotelBranch = this.getById(id);
        if (hotelBranch == null) {
            throw new IllegalArgumentException("分店不存在");
        }

        hotelBranch.setStatus(0); // 设置为禁用状态
        hotelBranch.setUpdatedAt(LocalDateTime.now());
        this.updateById(hotelBranch);
        log.info("删除分店成功 - 分店ID: {}, 分店名称: {}", id, hotelBranch.getBranchName());
    }

    /**
     * 根据城市获取分店列表
     */
    @Override
    public List<HotelBranch> getHotelBranchesByCity(String city) {
        QueryWrapper<HotelBranch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city", city)
                    .eq("status", 1)
                    .orderByAsc("branch_name");
        return this.list(queryWrapper);
    }

    /**
     * 获取所有启用的分店
     */
    @Override
    public List<HotelBranch> getAllActiveBranches() {
        QueryWrapper<HotelBranch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)
                    .orderByAsc("country", "city", "branch_name");
        return this.list(queryWrapper);
    }

    /**
     * 根据国家获取分店列表
     */
    @Override
    public List<HotelBranch> getHotelBranchesByCountry(String country) {
        QueryWrapper<HotelBranch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("country", country)
                    .eq("status", 1)
                    .orderByAsc("city", "branch_name");
        return this.list(queryWrapper);
    }
} 