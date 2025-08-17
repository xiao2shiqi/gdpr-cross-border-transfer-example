package com.hotelbooking.hotel_reservation_eu.service.impl;

import com.hotelbooking.hotel_reservation_eu.mapper.ReservationMapper;
import com.hotelbooking.hotel_reservation_eu.model.Reservation;
import com.hotelbooking.hotel_reservation_eu.service.BiDataWriteService;
import com.hotelbooking.hotel_reservation_eu.service.DataAnonymizationService;
import com.hotelbooking.hotel_reservation_eu.service.DataSyncScheduledService;
import com.hotelbooking.hotel_reservation_eu.service.StatisticsReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 数据同步定时任务服务实现类
 * 负责定时执行数据同步任务，生成匿名化统计报告并写入BI数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSyncScheduledServiceImpl implements DataSyncScheduledService {

    private final ReservationMapper reservationMapper;
    private final DataAnonymizationService dataAnonymizationService;
    private final StatisticsReportService statisticsReportService;
    private final BiDataWriteService biDataWriteService;

    // 同步状态和最后同步时间
    private final AtomicReference<String> syncStatus = new AtomicReference<>("IDLE");
    private final AtomicReference<LocalDateTime> lastSyncTime = new AtomicReference<>();

    @Override
    @Scheduled(fixedRate = 15000) // 15秒 = 15000毫秒
    public void executeDataSyncTask() {
        log.info("开始执行定时数据同步任务");
        long startTime = System.currentTimeMillis();

        try {
            // 更新同步状态
            syncStatus.set("RUNNING");

            // 获取当前日期
            LocalDate currentDate = LocalDate.now();

            // 检查BI数据库连接
            if (!biDataWriteService.checkConnection()) {
                log.error("BI数据库连接失败，跳过本次同步");
                syncStatus.set("FAILED_CONNECTION");
                return;
            }

            // 从数据库获取今日的预订数据
            List<Reservation> todayReservations = getTodayReservations(currentDate);
            log.info("获取到今日预订数据: {}条", todayReservations.size());

            if (todayReservations.isEmpty()) {
                log.info("今日无预订数据，跳过同步");
                syncStatus.set("COMPLETED_NO_DATA");
                return;
            }

            // 对数据进行脱敏处理
            List<Reservation> anonymizedReservations = dataAnonymizationService.anonymizeReservations(todayReservations);
            log.info("数据脱敏完成，共处理{}条记录", anonymizedReservations.size());

            // 验证脱敏结果
            boolean allAnonymized = anonymizedReservations.stream()
                    .allMatch(dataAnonymizationService::isDataAnonymized);

            if (!allAnonymized) {
                log.error("数据脱敏验证失败，存在未脱敏的PII信息");
                syncStatus.set("FAILED_ANONYMIZATION");
                return;
            }

            // 生成统计报告
            Map<String, Object> comprehensiveReport = statisticsReportService
                    .generateReportFromAnonymizedData(anonymizedReservations, currentDate);

            log.info("统计报告生成完成: {}", comprehensiveReport.get("reportType"));

            // 写入统计数据到BI数据库
            boolean writeSuccess = biDataWriteService.writeAllStatistics(comprehensiveReport, currentDate);
            
            if (writeSuccess) {
                // 记录同步成功
                lastSyncTime.set(LocalDateTime.now());
                syncStatus.set("COMPLETED_SUCCESS");
                
                long executionTime = System.currentTimeMillis() - startTime;
                log.info("定时数据同步任务执行完成，生成报告类型: {}, 数据量: {}, 执行时间: {}ms, BI写入: 成功",
                        comprehensiveReport.get("reportType"),
                        comprehensiveReport.get("dataCount"),
                        executionTime);
            } else {
                syncStatus.set("FAILED_BI_WRITE");
                log.error("统计数据写入BI数据库失败");
            }

        } catch (Exception e) {
            log.error("定时数据同步任务执行失败", e);
            syncStatus.set("FAILED_ERROR");
        }
    }

    @Override
    public void manualDataSync(LocalDate date) {
        log.info("手动触发数据同步: date={}", date);
        long startTime = System.currentTimeMillis();

        try {
            syncStatus.set("MANUAL_RUNNING");

            // 检查BI数据库连接
            if (!biDataWriteService.checkConnection()) {
                log.error("BI数据库连接失败，手动同步失败");
                syncStatus.set("MANUAL_FAILED_CONNECTION");
                return;
            }

            // 获取指定日期的预订数据
            List<Reservation> dateReservations = getDateReservations(date);
            log.info("获取到{}的预订数据: {}条", date, dateReservations.size());

            if (dateReservations.isEmpty()) {
                log.info("{}无预订数据，跳过同步", date);
                syncStatus.set("MANUAL_COMPLETED_NO_DATA");
                return;
            }

            // 对数据进行脱敏处理
            List<Reservation> anonymizedReservations = dataAnonymizationService.anonymizeReservations(dateReservations);
            log.info("手动同步数据脱敏完成，共处理{}条记录", anonymizedReservations.size());

            // 生成统计报告
            Map<String, Object> comprehensiveReport = statisticsReportService
                    .generateReportFromAnonymizedData(anonymizedReservations, date);

            log.info("手动同步统计报告生成完成: {}", comprehensiveReport.get("reportType"));

            // 写入统计数据到BI数据库
            boolean writeSuccess = biDataWriteService.writeAllStatistics(comprehensiveReport, date);
            
            if (writeSuccess) {
                // 记录同步成功
                lastSyncTime.set(LocalDateTime.now());
                syncStatus.set("MANUAL_COMPLETED_SUCCESS");
                
                long executionTime = System.currentTimeMillis() - startTime;
                log.info("手动数据同步执行完成，执行时间: {}ms, BI写入: 成功", executionTime);
            } else {
                syncStatus.set("MANUAL_FAILED_BI_WRITE");
                log.error("手动同步统计数据写入BI数据库失败");
            }

        } catch (Exception e) {
            log.error("手动数据同步执行失败", e);
            syncStatus.set("MANUAL_FAILED_ERROR");
        }
    }

    @Override
    public LocalDate getLastSyncTime() {
        LocalDateTime lastTime = lastSyncTime.get();
        return lastTime != null ? lastTime.toLocalDate() : null;
    }

    @Override
    public String getSyncStatus() {
        return syncStatus.get();
    }

    /**
     * 获取今日的预订数据
     */
    private List<Reservation> getTodayReservations(LocalDate date) {
        return reservationMapper.selectTodayReservations();
    }

    /**
     * 获取指定日期的预订数据
     */
    private List<Reservation> getDateReservations(LocalDate date) {
        return reservationMapper.selectByDate(date);
    }
}
