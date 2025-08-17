package com.hotelbooking.hotel_reservation_eu.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hotelbooking.hotel_reservation_eu.dto.HotelBranchDto;
import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.hotelbooking.hotel_reservation_eu.service.AdminHotelBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营端分店管理REST API控制器
 * 提供分店的CRUD操作接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/api/hotel-branches")
@RequiredArgsConstructor
@Validated
public class AdminHotelBranchController {

    private final AdminHotelBranchService adminHotelBranchService;

    /**
     * 分页查询分店列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHotelBranches(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        try {
            IPage<HotelBranch> branchPage = adminHotelBranchService.getHotelBranchPage(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", branchPage.getRecords());
            response.put("current", branchPage.getCurrent());
            response.put("pages", branchPage.getPages());
            response.put("total", branchPage.getTotal());
            response.put("size", branchPage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询分店列表失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 根据ID获取分店详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getHotelBranch(@PathVariable Long id) {
        try {
            HotelBranch hotelBranch = adminHotelBranchService.getById(id);
            if (hotelBranch == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", hotelBranch);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询分店详情失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 创建分店
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createHotelBranch(@Valid @RequestBody HotelBranchDto hotelBranchDto) {
        try {
            HotelBranch hotelBranch = new HotelBranch();
            BeanUtils.copyProperties(hotelBranchDto, hotelBranch);
            
            adminHotelBranchService.createHotelBranch(hotelBranch);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分店创建成功");
            response.put("data", hotelBranch);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("创建分店失败 - 参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("创建分店失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("创建失败"));
        }
    }

    /**
     * 更新分店
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateHotelBranch(
            @PathVariable Long id,
            @Valid @RequestBody HotelBranchDto hotelBranchDto) {
        
        try {
            HotelBranch hotelBranch = new HotelBranch();
            BeanUtils.copyProperties(hotelBranchDto, hotelBranch);
            hotelBranch.setId(id);
            
            adminHotelBranchService.updateHotelBranch(hotelBranch);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分店更新成功");
            response.put("data", hotelBranch);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("更新分店失败 - 参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("更新分店失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("更新失败"));
        }
    }

    /**
     * 删除分店
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteHotelBranch(@PathVariable Long id) {
        try {
            adminHotelBranchService.deleteHotelBranch(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分店删除成功");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("删除分店失败 - 参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("删除分店失败 - ID: {}", id, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("删除失败"));
        }
    }

    /**
     * 获取所有启用的分店列表（用于下拉选择）
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveBranches() {
        try {
            List<HotelBranch> branches = adminHotelBranchService.getAllActiveBranches();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", branches);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("查询启用分店列表失败", e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 根据国家获取分店列表
     */
    @GetMapping("/by-country/{country}")
    public ResponseEntity<Map<String, Object>> getBranchesByCountry(@PathVariable String country) {
        try {
            List<HotelBranch> branches = adminHotelBranchService.getHotelBranchesByCountry(country);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", branches);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("根据国家查询分店列表失败 - 国家: {}", country, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
    }

    /**
     * 根据城市获取分店列表
     */
    @GetMapping("/by-city/{city}")
    public ResponseEntity<Map<String, Object>> getBranchesByCity(@PathVariable String city) {
        try {
            List<HotelBranch> branches = adminHotelBranchService.getHotelBranchesByCity(city);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", branches);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("根据城市查询分店列表失败 - 城市: {}", city, e);
            return ResponseEntity.internalServerError().body(createErrorResponse("查询失败"));
        }
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