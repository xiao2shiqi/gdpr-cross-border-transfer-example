package com.hotelbooking.china_bi_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotelbooking.china_bi_system.model.PopularRoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 热门房型统计数据访问接口
 */
@Mapper
public interface PopularRoomTypeMapper extends BaseMapper<PopularRoomType> {
    
    /**
     * 查询指定日期的热门房型Top5
     */
    @Select("SELECT * FROM popular_room_types_top5 WHERE report_date = #{date} ORDER BY ranking ASC")
    List<PopularRoomType> selectByDate(LocalDate date);
    
    /**
     * 查询今日的热门房型Top5
     */
    @Select("SELECT * FROM popular_room_types_top5 WHERE report_date = CURDATE() ORDER BY ranking ASC")
    List<PopularRoomType> selectToday();
    
    /**
     * 查询最近7天的热门房型统计
     */
    @Select("SELECT * FROM popular_room_types_top5 WHERE report_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ORDER BY report_date DESC, ranking ASC")
    List<PopularRoomType> selectLast7Days();
    
    /**
     * 查询指定地区的今日热门房型Top5
     */
    @Select("SELECT * FROM popular_room_types_top5 WHERE report_date = CURDATE() AND region = #{region} ORDER BY ranking ASC")
    List<PopularRoomType> selectTodayByRegion(@Param("region") String region);
    
    /**
     * 查询指定地区最近7天的热门房型统计
     */
    @Select("SELECT * FROM popular_room_types_top5 WHERE report_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND region = #{region} ORDER BY report_date DESC, ranking ASC")
    List<PopularRoomType> selectLast7DaysByRegion(@Param("region") String region);
    
    /**
     * 查询所有地区的今日热门房型统计
     */
    @Select("SELECT * FROM popular_room_types_top5 WHERE report_date = CURDATE() ORDER BY region, ranking ASC")
    List<PopularRoomType> selectTodayAllRegions();
    
    /**
     * 查询所有热门房型统计数据
     */
    @Select("SELECT * FROM popular_room_types_top5 ORDER BY report_date DESC, region, ranking ASC")
    List<PopularRoomType> selectAll();
}
