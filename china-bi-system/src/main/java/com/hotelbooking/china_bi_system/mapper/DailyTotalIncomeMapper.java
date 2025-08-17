package com.hotelbooking.china_bi_system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotelbooking.china_bi_system.model.DailyTotalIncome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 每日总收入统计数据访问接口
 */
@Mapper
public interface DailyTotalIncomeMapper extends BaseMapper<DailyTotalIncome> {
    
    /**
     * 查询指定日期范围的收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date BETWEEN #{startDate} AND #{endDate} ORDER BY report_date DESC")
    List<DailyTotalIncome> selectByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 查询最近7天的收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ORDER BY report_date DESC")
    List<DailyTotalIncome> selectLast7Days();
    
    /**
     * 查询最近30天的收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ORDER BY report_date DESC")
    List<DailyTotalIncome> selectLast30Days();
    
    /**
     * 查询今日收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date = CURDATE()")
    List<DailyTotalIncome> selectToday();
    
    /**
     * 查询指定地区的今日收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date = CURDATE() AND region = #{region}")
    DailyTotalIncome selectTodayByRegion(@Param("region") String region);
    
    /**
     * 查询指定地区最近7天的收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND region = #{region} ORDER BY report_date DESC")
    List<DailyTotalIncome> selectLast7DaysByRegion(@Param("region") String region);
    
    /**
     * 查询所有地区的今日收入统计
     */
    @Select("SELECT * FROM daily_total_income WHERE report_date = CURDATE() ORDER BY region, report_date DESC")
    List<DailyTotalIncome> selectTodayAllRegions();
    
    /**
     * 查询所有收入统计数据
     */
    @Select("SELECT * FROM daily_total_income ORDER BY report_date DESC, region")
    List<DailyTotalIncome> selectAll();
}
