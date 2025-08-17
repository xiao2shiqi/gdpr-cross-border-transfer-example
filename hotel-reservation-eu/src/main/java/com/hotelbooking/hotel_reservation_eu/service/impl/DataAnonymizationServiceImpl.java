package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.model.Reservation;
import com.hotelbooking.hotel_reservation_eu.service.DataAnonymizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据脱敏服务实现类
 * 完全去除PII信息，确保数据匿名化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataAnonymizationServiceImpl implements DataAnonymizationService {

    @Override
    public List<Reservation> anonymizeReservations(List<Reservation> reservations) {
        log.info("开始对{}条预订数据进行脱敏处理", reservations.size());
        
        List<Reservation> anonymizedReservations = reservations.stream()
                .map(this::anonymizeReservation)
                .collect(Collectors.toList());
        
        log.info("数据脱敏完成，共处理{}条记录", anonymizedReservations.size());
        return anonymizedReservations;
    }

    @Override
    public Reservation anonymizeReservation(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        
        // 创建脱敏后的预订对象，只保留业务统计所需的数据
        Reservation anonymizedReservation = Reservation.builder()
                .id(reservation.getId())
                .roomTypeId(reservation.getRoomTypeId())
                .branchId(reservation.getBranchId())
                .checkinDate(reservation.getCheckinDate())
                .checkoutDate(reservation.getCheckoutDate())
                .guests(reservation.getGuests())
                .rooms(reservation.getRooms())
                .pricePerNight(reservation.getPricePerNight())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .paymentStatus(reservation.getPaymentStatus())
                .paymentMethod(reservation.getPaymentMethod())
                .paymentTime(reservation.getPaymentTime())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .cancelledAt(reservation.getCancelledAt())
                .cancellationReason(reservation.getCancellationReason())
                .build();
        
        // 完全去除PII信息
        // 注意：这里不设置userId、contactName、contactPhone、contactEmail、specialRequests等字段
        
        log.debug("预订数据脱敏完成: reservationId={}", anonymizedReservation.getId());
        return anonymizedReservation;
    }

    @Override
    public boolean isDataAnonymized(Reservation reservation) {
        if (reservation == null) {
            return true;
        }
        
        // 检查是否还包含PII信息
        return reservation.getUserId() == null &&
               reservation.getContactName() == null &&
               reservation.getContactPhone() == null &&
               reservation.getContactEmail() == null &&
               reservation.getSpecialRequests() == null;
    }
}
