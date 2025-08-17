package com.hotelbooking.hotel_reservation_eu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotelbooking.hotel_reservation_eu.model.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 预订数据访问接口
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
    
    /**
     * 根据用户ID查询所有预订
     */
    @Select("SELECT * FROM reservation WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Reservation> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据预订ID和用户ID查询预订（确保用户只能查看自己的预订）
     */
    @Select("SELECT * FROM reservation WHERE id = #{reservationId} AND user_id = #{userId}")
    Reservation selectByIdAndUserId(@Param("reservationId") Long reservationId, @Param("userId") Long userId);
    
    /**
     * 检查指定日期范围内房型是否可用
     */
    @Select("SELECT COUNT(*) FROM reservation WHERE room_type_id = #{roomTypeId} AND branch_id = #{branchId} " +
            "AND status NOT IN ('CANCELLED', 'COMPLETED') " +
            "AND ((checkin_date <= #{checkoutDate} AND checkout_date >= #{checkinDate}))")
    int countConflictingReservations(@Param("roomTypeId") Long roomTypeId, 
                                   @Param("branchId") Long branchId,
                                   @Param("checkinDate") LocalDate checkinDate, 
                                   @Param("checkoutDate") LocalDate checkoutDate);
    
    /**
     * 根据分店ID查询所有预订
     */
    @Select("SELECT * FROM reservation WHERE branch_id = #{branchId} ORDER BY created_at DESC")
    List<Reservation> selectByBranchId(@Param("branchId") Long branchId);
    
    /**
     * 根据房型ID查询所有预订
     */
    @Select("SELECT * FROM reservation WHERE room_type_id = #{roomTypeId} ORDER BY created_at DESC")
    List<Reservation> selectByRoomTypeId(@Param("roomTypeId") Long roomTypeId);
    
    /**
     * 查询指定日期的所有预订数据（用于数据同步）
     */
    @Select("SELECT * FROM reservation WHERE DATE(created_at) = #{date} ORDER BY created_at DESC")
    List<Reservation> selectByDate(@Param("date") LocalDate date);
    
    /**
     * 查询今日的所有预订数据（用于定时同步）
     */
    @Select("SELECT * FROM reservation WHERE DATE(created_at) = CURDATE() ORDER BY created_at DESC")
    List<Reservation> selectTodayReservations();
}