package com.hotelbooking.hotel_reservation_eu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 运营端分店管理服务接口（移除分店代码相关方法）
 * 继承MyBatis-Plus的IService接口，提供分店的CRUD操作
 */
public interface AdminHotelBranchService extends IService<HotelBranch> {

    /**
     * 分页查询分店列表
     */
    IPage<HotelBranch> getHotelBranchPage(int pageNum, int pageSize);

    /**
     * 创建分店
     */
    void createHotelBranch(HotelBranch hotelBranch);

    /**
     * 更新分店
     */
    void updateHotelBranch(HotelBranch hotelBranch);

    /**
     * 删除分店（软删除）
     */
    void deleteHotelBranch(Long id);

    /**
     * 根据城市获取分店列表
     */
    List<HotelBranch> getHotelBranchesByCity(String city);

    /**
     * 获取所有启用的分店
     */
    List<HotelBranch> getAllActiveBranches();

    /**
     * 根据国家获取分店列表
     */
    List<HotelBranch> getHotelBranchesByCountry(String country);
} 