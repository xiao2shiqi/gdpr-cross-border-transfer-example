package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.dto.ReservationRequestDto;
import com.hotelbooking.hotel_reservation_eu.dto.ReservationResponseDto;
import com.hotelbooking.hotel_reservation_eu.mapper.ReservationMapper;
import com.hotelbooking.hotel_reservation_eu.mapper.RoomTypeMapper;
import com.hotelbooking.hotel_reservation_eu.mapper.HotelBranchMapper;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.hotelbooking.hotel_reservation_eu.model.Reservation;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import com.hotelbooking.hotel_reservation_eu.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预订服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final RoomTypeMapper roomTypeMapper;
    private final HotelBranchMapper hotelBranchMapper;

    @Override
    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId) {
        log.info("创建预订: userId={}, roomTypeId={}, branchId={}",
                userId, requestDto.getRoomTypeId(), requestDto.getBranchId());

        // 验证请求数据
        if (!requestDto.isValidDateRange()) {
            throw new IllegalArgumentException("入住和退房日期无效");
        }


        // 创建预订实体
        Reservation reservation = Reservation.builder()
                .userId(userId)
                .roomTypeId(requestDto.getRoomTypeId())
                .branchId(requestDto.getBranchId())
                .checkinDate(requestDto.getCheckinDate())
                .checkoutDate(requestDto.getCheckoutDate())
                .guests(requestDto.getGuests())
                .rooms(requestDto.getRooms())
                .pricePerNight(requestDto.getPricePerNight())
                .totalPrice(requestDto.calculateTotalPrice())
                .status("PENDING")
                .paymentStatus("PENDING")
                .specialRequests(requestDto.getSpecialRequests())
                .contactName(requestDto.getContactName())
                .contactPhone(requestDto.getContactPhone())
                .contactEmail(requestDto.getContactEmail())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 保存预订到数据库
        int result = reservationMapper.insert(reservation);
        if (result != 1) {
            throw new RuntimeException("保存预订失败");
        }

        log.info("预订创建成功: reservationId={}", reservation.getId());

        // 返回预订响应
        return buildReservationResponse(reservation);
    }

    @Override
    public ReservationResponseDto getReservationById(Long reservationId, Long userId) {
        log.info("获取预订详情: reservationId={}, userId={}", reservationId, userId);

        // 从数据库获取预订
        Reservation reservation = reservationMapper.selectByIdAndUserId(reservationId, userId);
        if (reservation == null) {
            throw new IllegalArgumentException("预订不存在或无权限访问");
        }

        return buildReservationResponse(reservation);
    }

    @Override
    public List<ReservationResponseDto> getUserReservations(Long userId) {
        log.info("获取用户预订列表: userId={}", userId);

        // 从数据库获取用户的所有预订
        List<Reservation> reservations = reservationMapper.selectByUserId(userId);

        return reservations.stream()
                .map(this::buildReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelReservation(Long reservationId, Long userId, String reason) {
        log.info("取消预订: reservationId={}, userId={}, reason={}", reservationId, userId, reason);

        // 获取预订信息
        Reservation reservation = reservationMapper.selectByIdAndUserId(reservationId, userId);
        if (reservation == null) {
            throw new IllegalArgumentException("预订不存在或无权限访问");
        }

        // 检查预订状态
        if ("CANCELLED".equals(reservation.getStatus()) || "COMPLETED".equals(reservation.getStatus())) {
            throw new IllegalArgumentException("预订状态不允许取消");
        }

        // 更新预订状态
        reservation.setStatus("CANCELLED");
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(reason);
        reservation.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        int result = reservationMapper.updateById(reservation);
        if (result != 1) {
            throw new RuntimeException("取消预订失败");
        }

        log.info("预订取消成功: reservationId={}", reservationId);
        return true;
    }

    @Override
    @Transactional
    public boolean processPayment(Long reservationId, Long userId, String paymentMethod) {
        log.info("处理支付: reservationId={}, userId={}, paymentMethod={}", reservationId, userId, paymentMethod);

        // 获取预订信息
        Reservation reservation = reservationMapper.selectByIdAndUserId(reservationId, userId);
        if (reservation == null) {
            throw new IllegalArgumentException("预订不存在或无权限访问");
        }

        // 检查预订状态
        if (!"PENDING".equals(reservation.getStatus())) {
            throw new IllegalArgumentException("预订状态不允许支付");
        }

        // 更新预订状态和支付信息
        reservation.setStatus("CONFIRMED");
        reservation.setPaymentStatus("PAID");
        reservation.setPaymentMethod(paymentMethod);
        reservation.setPaymentTime(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        // 保存到数据库
        log.info("开始更新预订到数据库: reservationId={}", reservationId);
        int result = reservationMapper.updateById(reservation);
        log.info("数据库更新结果: result={}", result);
        
        if (result != 1) {
            log.error("数据库更新失败: reservationId={}, result={}", reservationId, result);
            throw new RuntimeException("支付处理失败");
        }

        log.info("支付处理成功: reservationId={}", reservationId);
        return true;
    }


    @Override
    public BigDecimal calculateTotalPrice(Long roomTypeId, Long branchId,
                                          LocalDate checkinDate, LocalDate checkoutDate, Integer rooms) {
        log.debug("计算总价格: roomTypeId={}, branchId={}, checkin={}, checkout={}, rooms={}",
                roomTypeId, branchId, checkinDate, checkoutDate, rooms);

        // 获取房型信息
        RoomType roomType = roomTypeMapper.selectByIdAndActive(roomTypeId);
        if (roomType == null) {
            throw new IllegalArgumentException("房型不存在或已停用");
        }

        // 计算入住天数
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkinDate, checkoutDate);

        // 计算总价格
        BigDecimal totalPrice = roomType.getBasePrice()
                .multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(rooms));

        log.debug("价格计算: basePrice={}, nights={}, rooms={}, totalPrice={}",
                roomType.getBasePrice(), nights, rooms, totalPrice);

        return totalPrice;
    }

    /**
     * 构建预订响应DTO
     */
    private ReservationResponseDto buildReservationResponse(Reservation reservation) {
        return ReservationResponseDto.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .roomTypeId(reservation.getRoomTypeId())
                .branchId(reservation.getBranchId())
                .checkinDate(reservation.getCheckinDate())
                .checkoutDate(reservation.getCheckoutDate())
                .guests(reservation.getGuests())
                .rooms(reservation.getRooms())
                .pricePerNight(reservation.getPricePerNight())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus())
                .statusDisplay(getStatusDisplay(reservation.getStatus()))
                .paymentStatus(reservation.getPaymentStatus())
                .paymentStatusDisplay(getPaymentStatusDisplay(reservation.getPaymentStatus()))
                .paymentMethod(reservation.getPaymentMethod())
                .paymentTime(reservation.getPaymentTime())
                .specialRequests(reservation.getSpecialRequests())
                .contactName(reservation.getContactName())
                .contactPhone(reservation.getContactPhone())
                .contactEmail(reservation.getContactEmail())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .cancelledAt(reservation.getCancelledAt())
                .cancellationReason(reservation.getCancellationReason())
                .build();
    }

    /**
     * 获取状态显示文本
     */
    private String getStatusDisplay(String status) {
        switch (status) {
            case "PENDING": return "待确认";
            case "CONFIRMED": return "已确认";
            case "CANCELLED": return "已取消";
            case "COMPLETED": return "已完成";
            default: return status;
        }
    }

    /**
     * 获取支付状态显示文本
     */
    private String getPaymentStatusDisplay(String paymentStatus) {
        switch (paymentStatus) {
            case "PENDING": return "待支付";
            case "PAID": return "已支付";
            case "REFUNDED": return "已退款";
            default: return paymentStatus;
        }
    }
}