package com.hotelbooking.hotel_reservation_eu.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 数据同步监控页面控制器
 * 提供监控页面的访问入口
 */
@Slf4j
@Controller
public class DataSyncMonitorController {

    /**
     * 数据同步监控页面
     */
    @GetMapping("/data-sync-monitor")
    public String dataSyncMonitor() {
        log.info("访问数据同步监控页面");
        return "data-sync-monitor";
    }
}
