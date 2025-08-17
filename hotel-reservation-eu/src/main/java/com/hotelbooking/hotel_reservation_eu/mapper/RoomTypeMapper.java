package com.hotelbooking.hotel_reservation_eu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 房型Mapper接口
 */
@Mapper
public interface RoomTypeMapper extends BaseMapper<RoomType> {
    
    /**
     * 根据ID查询房型信息
     */
    @Select("SELECT * FROM room_type WHERE id = #{id} AND status = 1")
    RoomType selectByIdAndActive(Long id);
} 