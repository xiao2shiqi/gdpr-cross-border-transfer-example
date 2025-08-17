package com.hotelbooking.hotel_reservation_eu.controller;

import com.hotelbooking.hotel_reservation_eu.model.HotelBranch;
import com.hotelbooking.hotel_reservation_eu.service.AdminHotelBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;

/**
 * 公开的分店查询API控制器
 * 提供给前端预订页面使用的分店数据接口
 */
@Slf4j
@RestController
@RequestMapping("/api/hotel-branches")
@RequiredArgsConstructor
public class PublicHotelBranchController {

    private final AdminHotelBranchService adminHotelBranchService;

    /**
     * 获取所有启用的分店列表（带缓存）
     */
    @GetMapping("/active")
    @Cacheable(value = "activeBranches", unless = "#result.body.success == false")
    public ResponseEntity<Map<String, Object>> getActiveBranches() {
        try {
            List<HotelBranch> branches = adminHotelBranchService.getAllActiveBranches();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", branches);
            
            log.info("获取启用分店列表成功，共{}个分店", branches.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取启用分店列表失败", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取分店列表失败");
            return ResponseEntity.internalServerError().body(response);
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
            log.error("根据国家获取分店列表失败 - 国家: {}", country, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "获取分店列表失败");
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 