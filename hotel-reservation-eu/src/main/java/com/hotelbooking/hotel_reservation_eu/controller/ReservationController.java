package com.hotelbooking.hotel_reservation_eu.controller;

import com.hotelbooking.hotel_reservation_eu.dto.ReservationRequestDto;
import com.hotelbooking.hotel_reservation_eu.dto.ReservationResponseDto;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import com.hotelbooking.hotel_reservation_eu.model.User;
import com.hotelbooking.hotel_reservation_eu.model.RoomTypeBranchMapping;
import com.hotelbooking.hotel_reservation_eu.service.AdminRoomTypeBranchMappingService;
import com.hotelbooking.hotel_reservation_eu.service.HotelBranchService;
import com.hotelbooking.hotel_reservation_eu.service.ReservationService;
import com.hotelbooking.hotel_reservation_eu.service.RoomTypeService;
import com.hotelbooking.hotel_reservation_eu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * 预订控制器
 */
@Slf4j
@Controller
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final RoomTypeService roomTypeService;
    private final HotelBranchService hotelBranchService;
    private final AdminRoomTypeBranchMappingService adminRoomTypeBranchMappingService; // 新增

    /**
     * 显示预订页面
     */
    @GetMapping("/book")
    public String showBookingPage(@RequestParam Long roomTypeId,
                                 @RequestParam Long branchId,
                                 @RequestParam String checkinDate,
                                 @RequestParam String checkoutDate,
                                 @RequestParam Integer guests,
                                 @RequestParam Integer rooms,
                                 @RequestParam String price,
                                 Model model) {
        
        log.info("显示预订页面: roomTypeId={}, branchId={}, checkin={}, checkout={}, guests={}, rooms={}, price={}", 
                roomTypeId, branchId, checkinDate, checkoutDate, guests, rooms, price);
        
        try {
            // 获取当前用户信息
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.error("用户未登录");
                model.addAttribute("error", "请先登录后再进行预订");
                return "reservations/error";
            }
            
            User currentUser = userService.getUserById(userId);
            if (currentUser == null) {
                log.error("用户信息不存在: userId={}", userId);
                model.addAttribute("error", "用户信息不存在，请重新登录");
                return "reservations/error";
            }
            
            // 从数据库获取真实的房型和分店信息
            RoomType roomType = roomTypeService.getRoomTypeById(roomTypeId);
            HotelBranch hotelBranch = hotelBranchService.getHotelBranchById(branchId);
            
            // 验证数据有效性
            if (roomType == null) {
                log.error("房型不存在或已停用: roomTypeId={}", roomTypeId);
                model.addAttribute("error", "所选房型不存在或已停用，请重新选择");
                return "reservations/error";
            }
            
            if (hotelBranch == null) {
                log.error("分店不存在或已停用: branchId={}", branchId);
                model.addAttribute("error", "所选分店不存在或已停用，请重新选择");
                return "reservations/error";
            }
            
            // 获取有效价格：优先使用分店特定价格，如果没有则使用房型基础价格
            BigDecimal effectivePrice = getEffectivePrice(roomTypeId, branchId, roomType);
            
            if (effectivePrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("未找到有效价格: roomTypeId={}, branchId={}", roomTypeId, branchId);
                model.addAttribute("error", "价格信息获取失败，请重新选择");
                return "reservations/error";
            }
            
            log.info("获取到有效价格: roomTypeId={}, branchId={}, effectivePrice={}, basePrice={}", 
                    roomTypeId, branchId, effectivePrice, roomType.getBasePrice());
            
            // 将真实数据传递给页面
            model.addAttribute("roomType", roomType);
            model.addAttribute("hotelBranch", hotelBranch);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("roomTypeId", roomTypeId);
            model.addAttribute("branchId", branchId);
            model.addAttribute("checkinDate", checkinDate);
            model.addAttribute("checkoutDate", checkoutDate);
            model.addAttribute("guests", guests);
            model.addAttribute("rooms", rooms);
            model.addAttribute("price", effectivePrice); // 使用有效价格
            
            log.info("成功获取预订页面数据: userId={}, roomTypeId={}, branchId={}, roomType={}, branch={}, effectivePrice={}", 
                    userId, roomTypeId, branchId, roomType.getTypeName(), hotelBranch.getBranchName(), effectivePrice);
            
            return "reservations/booking";
            
        } catch (Exception e) {
            log.error("获取预订页面数据失败: {}", e.getMessage(), e);
            model.addAttribute("error", "获取预订信息失败，请返回重试");
            return "reservations/error";
        }
    }



    /**
     * 显示我的预订页面
     */
    @GetMapping("/my")
    public String showMyReservations(Model model) {
        log.info("显示我的预订页面");
        
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.error("用户未登录");
                model.addAttribute("error", "请先登录后再查看预订");
                return "reservations/error";
            }
            
            // 获取用户的所有预订
            List<ReservationResponseDto> reservations = reservationService.getUserReservations(userId);
            
            log.info("成功获取用户预订列表: userId={}, count={}", userId, reservations.size());
            
            // 将预订数据传递给页面
            model.addAttribute("reservations", reservations);
            
            return "reservations/my";
            
        } catch (Exception e) {
            log.error("获取用户预订列表失败: {}", e.getMessage(), e);
            model.addAttribute("error", "获取预订信息失败，请稍后重试");
            return "reservations/error";
        }
    }

    /**
     * 显示预订详情页面
     */
    @GetMapping("/{reservationId}")
    public String showReservationDetail(@PathVariable Long reservationId, Model model) {
        log.info("显示预订详情页面: reservationId={}", reservationId);
        
        // 获取当前用户ID
        Long userId = getCurrentUserId();
        if (userId == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 获取预订详情
            ReservationResponseDto reservation = reservationService.getReservationById(reservationId, userId);
            model.addAttribute("reservation", reservation);
            return "reservations/detail";
        } catch (IllegalArgumentException e) {
            log.error("获取预订详情失败: {}", e.getMessage());
            model.addAttribute("error", "预订不存在或无权限访问");
            return "reservations/error";
        }
    }

    /**
     * 创建预订API
     */
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationRequestDto requestDto,
                                             BindingResult bindingResult) {
        
        log.info("创建预订API调用: {}", requestDto);
        
        // 调试：检查当前认证状态
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("当前认证状态: authenticated={}, principal={}, authorities={}", 
                auth != null ? auth.isAuthenticated() : false,
                auth != null ? auth.getPrincipal() : "null",
                auth != null ? auth.getAuthorities() : "null");
        
        // 验证请求数据
        if (bindingResult.hasErrors()) {
            log.error("预订请求数据验证失败: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body("请求数据验证失败");
        }
        
        if (!requestDto.isValidDateRange()) {
            return ResponseEntity.badRequest().body("入住和退房日期无效");
        }
        
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
            }
            
            // 创建预订
            ReservationResponseDto reservation = reservationService.createReservation(requestDto, userId);
            
            log.info("预订创建成功: reservationId={}", reservation.getId());
            return ResponseEntity.ok(reservation);
            
        } catch (Exception e) {
            log.error("创建预订失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("创建预订失败: " + e.getMessage());
        }
    }

    /**
     * 取消预订API
     */
    @PostMapping("/api/{reservationId}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId,
                                                  @RequestParam String reason) {
        
        log.info("取消预订API调用: reservationId={}, reason={}", reservationId, reason);
        
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.warn("取消预订失败: 用户未登录, reservationId={}", reservationId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
            }
            
            // 取消预订
            boolean success = reservationService.cancelReservation(reservationId, userId, reason);
            
            if (success) {
                log.info("预订取消成功: reservationId={}, userId={}, reason={}", 
                        reservationId, userId, reason);
                return ResponseEntity.ok("预订取消成功");
            } else {
                log.warn("取消预订失败: 业务逻辑返回false, reservationId={}, userId={}", 
                        reservationId, userId);
                return ResponseEntity.badRequest().body("预订取消失败");
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("取消预订失败: 参数验证失败, reservationId={}, error={}", 
                    reservationId, e.getMessage());
            return ResponseEntity.badRequest().body("预订取消失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("取消预订失败: 系统异常, reservationId={}, error={}", 
                    reservationId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("预订取消失败: 系统异常");
        }
    }

    /**
     * 处理支付API
     */
    @PostMapping("/api/{reservationId}/payment")
    @ResponseBody
    public ResponseEntity<String> processPayment(@PathVariable Long reservationId,
                                               @RequestParam String paymentMethod) {
        
        log.info("处理支付API调用: reservationId={}, paymentMethod={}", reservationId, paymentMethod);
        
        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                log.warn("支付处理失败: 用户未登录, reservationId={}", reservationId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未登录");
            }
            
            // 处理支付
            boolean success = reservationService.processPayment(reservationId, userId, paymentMethod);
            
            if (success) {
                log.info("支付处理成功: reservationId={}, userId={}, paymentMethod={}", 
                        reservationId, userId, paymentMethod);
                // 前端期望的精确字符串："支付成功"
                return ResponseEntity.ok("支付成功");
            } else {
                log.warn("支付处理失败: 业务逻辑返回false, reservationId={}, userId={}", 
                        reservationId, userId);
                return ResponseEntity.badRequest().body("支付失败");
            }
            
        } catch (IllegalArgumentException e) {
            log.warn("支付处理失败: 参数验证失败, reservationId={}, error={}", 
                    reservationId, e.getMessage());
            return ResponseEntity.badRequest().body("支付失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("支付处理失败: 系统异常, reservationId={}, error={}", 
                    reservationId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("支付失败: 系统异常");
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getName())) {
                
                // 从认证对象中获取用户名
                String username = authentication.getName();
                log.debug("当前认证用户: username={}", username);
                
                // 根据用户名查找用户，获取用户ID
                User user = userService.findByUsername(username);
                if (user != null) {
                    log.debug("成功获取用户信息: userId={}, username={}", user.getId(), username);
                    return user.getId();
                } else {
                    log.error("根据用户名未找到用户: username={}", username);
                    return null;
                }
            } else {
                log.debug("用户未认证或为匿名用户");
                return null;
            }
        } catch (Exception e) {
            log.error("获取当前用户ID失败: {}", e.getMessage(), e);
            return null;
        }
    }
    

    /**
     * 获取有效价格：优先使用分店特定价格，如果没有则使用房型基础价格
     */
    private BigDecimal getEffectivePrice(Long roomTypeId, Long branchId, RoomType roomType) {
        log.info("获取有效价格: roomTypeId={}, branchId={}", roomTypeId, branchId);
        
        try {
            // 查询房型分店关联信息
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<RoomTypeBranchMapping> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("room_type_id", roomTypeId)
                       .eq("hotel_branch_id", branchId)
                       .eq("is_active", 1);
            
            RoomTypeBranchMapping mapping = adminRoomTypeBranchMappingService.getOne(queryWrapper);
            
            if (mapping != null && mapping.getBranchSpecificPrice() != null 
                && mapping.getBranchSpecificPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 如果有分店特定价格，返回特定价格
                log.info("使用分店特定价格: roomTypeId={}, branchId={}, specificPrice={}", 
                        roomTypeId, branchId, mapping.getBranchSpecificPrice());
                return mapping.getBranchSpecificPrice();
            }
            
            // 如果没有分店特定价格，使用房型基础价格
            if (roomType != null && roomType.getBasePrice() != null) {
                log.info("使用房型基础价格: roomTypeId={}, branchId={}, basePrice={}", 
                        roomTypeId, branchId, roomType.getBasePrice());
                return roomType.getBasePrice();
            }
            
            // 如果都没有，返回0
            log.warn("未找到有效价格信息: roomTypeId={}, branchId={}", roomTypeId, branchId);
            return BigDecimal.ZERO;
            
        } catch (Exception e) {
            log.error("获取有效价格失败: roomTypeId={}, branchId={}", roomTypeId, branchId, e);
            return BigDecimal.ZERO;
        }
    }
}
