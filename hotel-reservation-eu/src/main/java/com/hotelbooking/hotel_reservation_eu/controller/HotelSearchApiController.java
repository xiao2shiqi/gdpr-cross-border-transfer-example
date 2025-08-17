package com.hotelbooking.hotel_reservation_eu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelbooking.hotel_reservation_eu.mapper.RoomTypeBranchMappingMapper;
import com.hotelbooking.hotel_reservation_eu.dto.RoomTypeBranchMappingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 酒店搜索API控制器
 * 处理AJAX酒店搜索请求，返回JSON数据
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class HotelSearchApiController {

    private final RoomTypeBranchMappingMapper roomTypeBranchMappingMapper;
    private final ObjectMapper objectMapper;

    /**
     * 处理酒店搜索API请求
     */
    @GetMapping("/hotels")
    public ResponseEntity<Map<String, Object>> searchHotelsApi(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) String checkinDate,
            @RequestParam(required = false) String checkoutDate,
            @RequestParam(required = false, defaultValue = "2") Integer guests,
            @RequestParam(required = false, defaultValue = "1") Integer rooms) {

        log.info("API酒店搜索请求 - 分店ID: {}, 入住: {}, 退房: {}, 客人: {}, 房间: {}", 
                branchId, checkinDate, checkoutDate, guests, rooms);

        // 验证必需参数
        if (branchId == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("分店ID不能为空"));
        }

        if (checkinDate == null || checkoutDate == null) {
            return ResponseEntity.badRequest().body(createErrorResponse("入住和退房日期不能为空"));
        }

        try {
            // 验证日期格式和逻辑
            LocalDate checkin = LocalDate.parse(checkinDate);
            LocalDate checkout = LocalDate.parse(checkoutDate);
            
            if (checkout.isBefore(checkin) || checkout.isEqual(checkin)) {
                return ResponseEntity.badRequest().body(createErrorResponse("退房日期必须晚于入住日期"));
            }

            if (checkin.isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body(createErrorResponse("入住日期不能早于今天"));
            }

            // 构建搜索参数
            Map<String, Object> searchParams = buildSearchParams(
                branchId, checkin, checkout, guests, rooms);

            // 从数据库查询房源数据
            List<Map<String, Object>> hotels = loadRoomTypesFromDatabase(branchId);
            int totalResults = hotels.size();

            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("searchParams", searchParams);
            response.put("hotels", hotels);
            response.put("totalResults", totalResults);
            response.put("message", totalResults > 0 ? "搜索完成" : "该分店暂无可用房源");

            log.info("API搜索完成，找到 {} 个房源", totalResults);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("API搜索过程中发生错误", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("搜索失败，请稍后重试"));
        }
    }

    /**
     * 从数据库加载房源数据（基于房型分店关联）
     */
    private List<Map<String, Object>> loadRoomTypesFromDatabase(Long branchId) {
        List<Map<String, Object>> roomTypes = new ArrayList<>();
        
        try {
            // 查询房型分店关联数据
            List<RoomTypeBranchMappingDto> mappings = roomTypeBranchMappingMapper.selectByBranchId(branchId);
            
            for (RoomTypeBranchMappingDto mapping : mappings) {
                Map<String, Object> roomTypeMap = new HashMap<>();
                roomTypeMap.put("id", mapping.getRoomTypeId());
                roomTypeMap.put("roomTypeName", mapping.getRoomTypeName());
                roomTypeMap.put("description", mapping.getRoomTypeDescription());
                roomTypeMap.put("size", mapping.getSize());
                roomTypeMap.put("bedType", mapping.getBedType());
                roomTypeMap.put("maxGuests", mapping.getMaxGuests());
                
                // 优先使用分店特定价格，如果为空则使用房型基础价格
                BigDecimal effectivePrice = mapping.getEffectivePrice();
                roomTypeMap.put("price", effectivePrice);
                
                // 添加价格来源标识，便于调试
                String priceSource = mapping.getBranchSpecificPrice() != null && 
                                   mapping.getBranchSpecificPrice().compareTo(BigDecimal.ZERO) > 0 ? 
                                   "分店特定价格" : "房型基础价格";
                roomTypeMap.put("priceSource", priceSource);
                
                // 显示实际可用房间数
                String availableRoomsText = mapping.getAvailableRoomsCount() > 0 ? 
                    "剩余" + mapping.getAvailableRoomsCount() + "间" : "满房";
                roomTypeMap.put("availableRooms", availableRoomsText);
                roomTypeMap.put("imageUrl", mapping.getImageUrl());
                
                // 解析JSON格式的设施数据（房型基础设施）
                List<String> amenitiesList = parseAmenities(mapping.getAmenities());
                roomTypeMap.put("amenities", amenitiesList);
                
                // 添加分店信息
                roomTypeMap.put("branchName", mapping.getHotelBranchName());
                roomTypeMap.put("branchCity", mapping.getHotelBranchCity());
                roomTypeMap.put("branchCountry", mapping.getHotelBranchCountry());
                
                roomTypes.add(roomTypeMap);
            }
            
        } catch (Exception e) {
            log.error("从数据库加载房源数据失败", e);
        }
        
        return roomTypes;
    }

    /**
     * 解析设施JSON字符串
     */
    private List<String> parseAmenities(String amenitiesJson) {
        try {
            if (amenitiesJson != null && !amenitiesJson.trim().isEmpty()) {
                return objectMapper.readValue(amenitiesJson, new TypeReference<List<String>>() {});
            }
        } catch (JsonProcessingException e) {
            log.warn("解析设施JSON失败: {}", amenitiesJson, e);
        }
        return new ArrayList<>();
    }

    /**
     * 构建搜索参数
     */
    private Map<String, Object> buildSearchParams(Long branchId, LocalDate checkin, 
                                                 LocalDate checkout, Integer guests, Integer rooms) {
        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("branchId", branchId);
        searchParams.put("checkinDate", checkin.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        searchParams.put("checkoutDate", checkout.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        searchParams.put("checkinDateRaw", checkin.toString());
        searchParams.put("checkoutDateRaw", checkout.toString());
        searchParams.put("guests", guests);
        searchParams.put("rooms", rooms);
        searchParams.put("nights", java.time.temporal.ChronoUnit.DAYS.between(checkin, checkout));
        
        return searchParams;
    }



    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("hotels", new ArrayList<>());
        response.put("totalResults", 0);
        return response;
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String hashedPassword = "$2a$10$N.xzxm/fXEYR6FqGLvtHjObYjNafoWaWFxG5oL5sOHDYx8/6CiKL.";

        boolean matches = encoder.matches(rawPassword, hashedPassword);
        System.out.println("密码匹配: " + matches); // 应该输出 true
    }
} 