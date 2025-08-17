package com.hotelbooking.hotel_reservation_eu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotelbooking.hotel_reservation_eu.dto.RoomTypeBranchMappingDto;
import com.hotelbooking.hotel_reservation_eu.model.RoomTypeBranchMapping;
import com.hotelbooking.hotel_reservation_eu.service.AdminRoomTypeBranchMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营端房型分店关系管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/api/room-type-branch-mappings")
@RequiredArgsConstructor
@Validated
public class AdminRoomTypeBranchMappingController {

    private final AdminRoomTypeBranchMappingService mappingService;

    /**
     * 分页查询房型分店关联列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMappings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) Long hotelBranchId,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) Boolean isActive) {
        try {
            IPage<RoomTypeBranchMappingDto> mappingPage = mappingService.getMappingPage(
                page, size, hotelBranchId, roomTypeId, isActive);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mappingPage.getRecords());
            response.put("total", mappingPage.getTotal());
            response.put("pages", mappingPage.getPages());
            response.put("current", mappingPage.getCurrent());
            response.put("size", mappingPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询房型分店关联列表失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 根据ID获取房型分店关联详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMapping(@PathVariable Long id) {
        try {
            RoomTypeBranchMappingDto mapping = mappingService.getMappingDetails(id);
            if (mapping == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", mapping);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询房型分店关联详情失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 创建房型分店关联
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createMapping(
            @Valid @RequestBody RoomTypeBranchMappingDto mappingDto) {
        try {
            RoomTypeBranchMapping mapping = convertToEntity(mappingDto);
            mappingService.createMapping(mapping);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "房源创建成功");
            response.put("data", mapping);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("创建房源失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("创建失败"));
        }
    }

    /**
     * 更新房型分店关联
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMapping(
            @PathVariable Long id, 
            @Valid @RequestBody RoomTypeBranchMappingDto mappingDto) {
        try {
            mappingDto.setId(id);
            RoomTypeBranchMapping mapping = convertToEntity(mappingDto);
            mappingService.updateMapping(mapping);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "房型分店关联更新成功");
            response.put("data", mapping);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("更新房型分店关联失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("更新失败"));
        }
    }

    /**
     * 删除房型分店关联
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMapping(@PathVariable Long id) {
        try {
            mappingService.deleteMapping(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "房型分店关联删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("删除房型分店关联失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("删除失败"));
        }
    }

    /**
     * 启用/禁用房型分店关联
     */
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Map<String, Object>> toggleMappingStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        try {
            mappingService.toggleMappingStatus(id, isActive);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", isActive ? "关联已启用" : "关联已禁用");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("切换房型分店关联状态失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("操作失败"));
        }
    }

    /**
     * 根据分店ID查询房型列表
     */
    @GetMapping("/by-branch/{hotelBranchId}")
    public ResponseEntity<Map<String, Object>> getRoomTypesByBranch(@PathVariable Long hotelBranchId) {
        try {
            List<RoomTypeBranchMappingDto> roomTypes = mappingService.getRoomTypesByBranchId(hotelBranchId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roomTypes);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询分店房型列表失败 - 分店ID: {}", hotelBranchId, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 根据房型ID查询分店列表
     */
    @GetMapping("/by-room-type/{roomTypeId}")
    public ResponseEntity<Map<String, Object>> getBranchesByRoomType(@PathVariable Long roomTypeId) {
        try {
            List<RoomTypeBranchMappingDto> branches = mappingService.getBranchesByRoomTypeId(roomTypeId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", branches);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询房型分店列表失败 - 房型ID: {}", roomTypeId, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 获取分店统计信息
     */
    @GetMapping("/statistics/branch/{hotelBranchId}")
    public ResponseEntity<Map<String, Object>> getBranchStatistics(@PathVariable Long hotelBranchId) {
        try {
            AdminRoomTypeBranchMappingService.RoomTypeBranchStatistics statistics = 
                mappingService.getBranchStatistics(hotelBranchId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取分店统计信息失败 - 分店ID: {}", hotelBranchId, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("获取统计信息失败"));
        }
    }

    /**
     * 批量更新房间库存
     */
    @PutMapping("/batch-update-counts")
    public ResponseEntity<Map<String, Object>> batchUpdateRoomCounts(
            @Valid @RequestBody List<RoomTypeBranchMappingDto> mappings) {
        try {
            mappingService.batchUpdateRoomCounts(mappings);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量更新房间库存成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("批量更新房间库存失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("批量更新失败"));
        }
    }

    /**
     * 将DTO转换为实体
     */
    private RoomTypeBranchMapping convertToEntity(RoomTypeBranchMappingDto dto) {
        return RoomTypeBranchMapping.builder()
                .id(dto.getId())
                .roomTypeId(dto.getRoomTypeId())
                .hotelBranchId(dto.getHotelBranchId())
                .isActive(dto.getIsActive())
                .branchSpecificPrice(dto.getBranchSpecificPrice())
                .availableRoomsCount(dto.getAvailableRoomsCount())
                .maxRoomsCount(dto.getMaxRoomsCount())
                .sortOrder(dto.getSortOrder())
                .specialAmenities(dto.getSpecialAmenities())
                .notes(dto.getNotes())
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