package com.hotelbooking.hotel_reservation_eu.service;

import com.hotelbooking.hotel_reservation_eu.model.Reservation;

import java.util.List;

/**
 * 数据脱敏服务接口
 * 负责去除个人身份信息(PII)，确保数据匿名化
 */
public interface DataAnonymizationService {
    
    /**
     * 对预订数据进行脱敏处理
     * 去除所有PII信息，保留业务统计所需的数据
     */
    List<Reservation> anonymizeReservations(List<Reservation> reservations);
    
    /**
     * 对单个预订数据进行脱敏处理
     */
    Reservation anonymizeReservation(Reservation reservation);
    
    /**
     * 验证数据是否已完全脱敏
     */
    boolean isDataAnonymized(Reservation reservation);
}
