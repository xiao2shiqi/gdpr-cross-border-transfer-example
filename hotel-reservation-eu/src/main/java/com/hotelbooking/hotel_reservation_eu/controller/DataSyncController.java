package com.hotelbooking.hotel_reservation_eu.controller;

import com.hotelbooking.hotel_reservation_eu.service.BiDataWriteService;
import com.hotelbooking.hotel_reservation_eu.service.DataSyncScheduledService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据同步控制器
 * 提供数据同步状态查询和手动触发同步的API
 */
@Slf4j
@RestController
@RequestMapping("/api/data-sync")
@RequiredArgsConstructor
public class DataSyncController {

    private final DataSyncScheduledService dataSyncScheduledService;
    private final BiDataWriteService biDataWriteService;

    /**
     * 获取数据同步状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSyncStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("syncStatus", dataSyncScheduledService.getSyncStatus());
        status.put("lastSyncTime", dataSyncScheduledService.getLastSyncTime());
        status.put("biDatabaseConnection", biDataWriteService.checkConnection());
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * 获取数据同步详细信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSyncInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("serviceName", "EU Hotel Data Sync Service");
        info.put("description", "欧盟酒店数据同步服务 - 生成匿名化统计报告并同步到BI数据库");
        info.put("syncStatus", dataSyncScheduledService.getSyncStatus());
        info.put("lastSyncTime", dataSyncScheduledService.getLastSyncTime());
        info.put("scheduleInterval", "15秒");
        info.put("biDatabaseConnection", biDataWriteService.checkConnection());
        info.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(info);
    }

    /**
     * 手动触发数据同步
     */
    @PostMapping("/manual-sync")
    public ResponseEntity<Map<String, Object>> manualSync(@RequestParam(required = false) String date) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            LocalDate syncDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            log.info("手动触发数据同步: date={}", syncDate);
            
            // 检查BI数据库连接
            if (!biDataWriteService.checkConnection()) {
                response.put("success", false);
                response.put("message", "BI数据库连接失败，无法执行同步");
                response.put("syncDate", syncDate.toString());
                response.put("timestamp", System.currentTimeMillis());
                response.put("error", "BI_DATABASE_CONNECTION_FAILED");
                
                return ResponseEntity.badRequest().body(response);
            }
            
            dataSyncScheduledService.manualDataSync(syncDate);
            
            response.put("success", true);
            response.put("message", "手动数据同步已触发");
            response.put("syncDate", syncDate.toString());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("手动触发数据同步失败", e);
            
            response.put("success", false);
            response.put("message", "手动数据同步失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取数据同步健康状态
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "EU Hotel Data Sync Service");
        health.put("syncStatus", dataSyncScheduledService.getSyncStatus());
        health.put("lastSyncTime", dataSyncScheduledService.getLastSyncTime());
        health.put("biDatabaseConnection", biDataWriteService.checkConnection());
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }

    /**
     * 检查BI数据库连接状态
     */
    @GetMapping("/bi-database/connection")
    public ResponseEntity<Map<String, Object>> checkBiDatabaseConnection() {
        log.info("检查BI数据库连接状态");
        
        boolean isConnected = biDataWriteService.checkConnection();
        
        Map<String, Object> response = new HashMap<>();
        response.put("connected", isConnected);
        response.put("database", "china_bi_system");
        response.put("timestamp", System.currentTimeMillis());
        response.put("message", isConnected ? "连接正常" : "连接失败");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 测试BI数据库写入功能
     */
    @PostMapping("/bi-database/test-write")
    public ResponseEntity<Map<String, Object>> testBiDatabaseWrite() {
        log.info("测试BI数据库写入功能");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 创建一个测试报告
            Map<String, Object> testReport = new HashMap<>();
            testReport.put("reportType", "test-report");
            testReport.put("dataCount", 0);
            
            // 测试写入功能
            boolean writeSuccess = biDataWriteService.writeAllStatistics(testReport, LocalDate.now());
            
            response.put("success", writeSuccess);
            response.put("message", writeSuccess ? "BI数据库写入测试成功" : "BI数据库写入测试失败");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("BI数据库写入测试失败", e);
            
            response.put("success", false);
            response.put("message", "BI数据库写入测试失败: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
