package com.hotelbooking.hotel_reservation_eu.service;

import java.time.LocalDate;

/**
 * 定时数据同步服务接口
 * 负责定时执行数据同步任务，生成匿名化统计报告
 */
public interface DataSyncScheduledService {
    
    /**
     * 执行数据同步任务
     * 每15秒执行一次，处理业务数据并生成统计报告
     */
    void executeDataSyncTask();
    
    /**
     * 手动触发数据同步
     */
    void manualDataSync(LocalDate date);
    
    /**
     * 获取上次同步时间
     */
    LocalDate getLastSyncTime();
    
    /**
     * 获取同步状态
     */
    String getSyncStatus();
}
