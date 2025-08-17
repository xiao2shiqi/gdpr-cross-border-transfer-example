package com.hotelbooking.hotel_reservation_eu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import org.apache.ibatis.annotations.Mapper;

/**
 * 运营端分店管理Mapper
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface AdminHotelBranchMapper extends BaseMapper<HotelBranch> {
    // MyBatis-Plus会自动提供基础的CRUD方法
    // 无需手写SQL，通过Service层的QueryWrapper来构建查询条件
} 