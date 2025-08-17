package com.hotelbooking.hotel_reservation_eu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotelbooking.hotel_reservation_eu.dto.RoomTypeDto;
import com.hotelbooking.hotel_reservation_eu.model.RoomType;
import com.hotelbooking.hotel_reservation_eu.service.AdminRoomTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 运营端房型管理控制器
 * 提供房型的CRUD API接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/api/room-types")
@RequiredArgsConstructor
@Validated
public class AdminRoomTypeController {

    private final AdminRoomTypeService adminRoomTypeService;

    /**
     * 分页查询房型列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoomTypes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            IPage<RoomType> roomTypePage = adminRoomTypeService.getRoomTypePage(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roomTypePage.getRecords());
            response.put("total", roomTypePage.getTotal());
            response.put("pages", roomTypePage.getPages());
            response.put("current", roomTypePage.getCurrent());
            response.put("size", roomTypePage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询房型列表失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 根据ID获取房型详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomType(@PathVariable Long id) {
        try {
            RoomType roomType = adminRoomTypeService.getById(id);
            if (roomType == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roomType);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询房型详情失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 创建房型
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRoomType(@Valid @RequestBody RoomTypeDto roomTypeDto) {
        try {
            RoomType roomType = convertToEntity(roomTypeDto);
            adminRoomTypeService.createRoomType(roomType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "房型创建成功");
            response.put("data", roomType);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("创建房型失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("创建失败"));
        }
    }

    /**
     * 更新房型
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRoomType(
            @PathVariable Long id, 
            @Valid @RequestBody RoomTypeDto roomTypeDto) {
        try {
            roomTypeDto.setId(id);
            RoomType roomType = convertToEntity(roomTypeDto);
            adminRoomTypeService.updateRoomType(roomType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "房型更新成功");
            response.put("data", roomType);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("更新房型失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("更新失败"));
        }
    }

    /**
     * 删除房型
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRoomType(@PathVariable Long id) {
        try {
            adminRoomTypeService.deleteRoomType(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "房型删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("删除房型失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("删除失败"));
        }
    }

    /**
     * 将DTO转换为实体
     */
    private RoomType convertToEntity(RoomTypeDto dto) {
        return RoomType.builder()
                .id(dto.getId())
                .typeName(dto.getTypeName())
                .description(dto.getDescription())
                .size(dto.getSize())
                .bedType(dto.getBedType())
                .maxGuests(dto.getMaxGuests())
                .basePrice(dto.getBasePrice())
                .imageUrl(dto.getImageUrl())
                .amenities(dto.getAmenities())
                .build();
    }

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
} 