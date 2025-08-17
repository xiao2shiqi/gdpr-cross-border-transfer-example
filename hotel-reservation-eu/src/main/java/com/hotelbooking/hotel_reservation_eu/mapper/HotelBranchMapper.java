package com.hotelbooking.hotel_reservation_eu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 酒店分店Mapper接口
 */
@Mapper
public interface HotelBranchMapper extends BaseMapper<HotelBranch> {
    
    /**
     * 根据ID查询分店信息
     */
    @Select("SELECT * FROM hotel_branch WHERE id = #{id} AND status = 1")
    HotelBranch selectByIdAndActive(Long id);
}
