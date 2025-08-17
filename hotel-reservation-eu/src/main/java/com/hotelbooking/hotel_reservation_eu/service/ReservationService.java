package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.dto.ReservationRequestDto;
import com.hotelbooking.hotel_reservation_eu.dto.ReservationResponseDto;
import com.hotelbooking.hotel_reservation_eu.model.Reservation;

import java.util.List;

/**
 * 预订服务接口
 */
public interface ReservationService {
    
    /**
     * 创建预订
     */
    ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId);
    
    /**
     * 根据ID获取预订详情
     */
    ReservationResponseDto getReservationById(Long reservationId, Long userId);
    
    /**
     * 获取用户的所有预订
     */
    List<ReservationResponseDto> getUserReservations(Long userId);
    
    /**
     * 取消预订
     */
    boolean cancelReservation(Long reservationId, Long userId, String reason);
    
    /**
     * 处理支付
     */
    boolean processPayment(Long reservationId, Long userId, String paymentMethod);

    
    /**
     * 计算预订总价格
     */
    java.math.BigDecimal calculateTotalPrice(Long roomTypeId, Long branchId,
                                           java.time.LocalDate checkinDate,
                                           java.time.LocalDate checkoutDate,
                                           Integer rooms);
}
