-- 中国BI系统数据库初始化脚本
-- 用于存储EU酒店预订系统的匿名化统计数据

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `china_bi_system` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `china_bi_system`;

-- 1. 每日总收入统计表
CREATE TABLE IF NOT EXISTS `daily_total_income` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `report_date` DATE NOT NULL COMMENT '报告日期',
    `total_income` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '当日总收入',
    `total_reservations` INT NOT NULL DEFAULT 0 COMMENT '当日总预订数',
    `avg_price_per_night` DECIMAL(10,2) DEFAULT 0.00 COMMENT '平均每晚价格',
    `currency` VARCHAR(10) DEFAULT 'EUR' COMMENT '货币类型',
    `data_source` VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '数据来源',
    `sync_status` VARCHAR(20) DEFAULT 'SYNCED' COMMENT '同步状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_date` (`report_date`),
    INDEX `idx_report_date` (`report_date`),
    INDEX `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日总收入统计表';

-- 2. 热门房型Top5统计表
CREATE TABLE IF NOT EXISTS `popular_room_types_top5` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `report_date` DATE NOT NULL COMMENT '报告日期',
    `room_type_id` BIGINT NOT NULL COMMENT '房型ID',
    `room_type_name` VARCHAR(100) COMMENT '房型名称',
    `reservation_count` INT NOT NULL DEFAULT 0 COMMENT '预订数量',
    `total_revenue` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '总收入',
    `ranking` INT NOT NULL COMMENT '排名',
    `data_source` VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '数据来源',
    `sync_status` VARCHAR(20) DEFAULT 'SYNCED' COMMENT '同步状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_report_date` (`report_date`),
    INDEX `idx_room_type_id` (`room_type_id`),
    INDEX `idx_ranking` (`ranking`),
    INDEX `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='热门房型Top5统计表';

-- 3. 分店业绩统计表
CREATE TABLE IF NOT EXISTS `branch_performance` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `report_date` DATE NOT NULL COMMENT '报告日期',
    `branch_id` BIGINT NOT NULL COMMENT '分店ID',
    `branch_name` VARCHAR(100) COMMENT '分店名称',
    `reservation_count` INT NOT NULL DEFAULT 0 COMMENT '预订数量',
    `total_revenue` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '总收入',
    `avg_revenue_per_reservation` DECIMAL(10,2) DEFAULT 0.00 COMMENT '平均每单收入',
    `data_source` VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '数据来源',
    `sync_status` VARCHAR(20) DEFAULT 'SYNCED' COMMENT '同步状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_report_date` (`report_date`),
    INDEX `idx_branch_id` (`branch_id`),
    INDEX `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分店业绩统计表';

-- 4. 预订趋势统计表
CREATE TABLE IF NOT EXISTS `reservation_trends` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `report_date` DATE NOT NULL COMMENT '报告日期',
    `total_reservations` INT NOT NULL DEFAULT 0 COMMENT '总预订数',
    `confirmed_reservations` INT NOT NULL DEFAULT 0 COMMENT '已确认预订数',
    `cancelled_reservations` INT NOT NULL DEFAULT 0 COMMENT '已取消预订数',
    `completion_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成率(%)',
    `data_source` VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '数据来源',
    `sync_status` VARCHAR(20) DEFAULT 'SYNCED' COMMENT '同步状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_date` (`report_date`),
    INDEX `idx_report_date` (`report_date`),
    INDEX `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预订趋势统计表';

-- 5. 综合统计报告表（存储完整的JSON格式报告）
CREATE TABLE IF NOT EXISTS `comprehensive_reports` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `report_date` DATE NOT NULL COMMENT '报告日期',
    `report_type` VARCHAR(50) NOT NULL COMMENT '报告类型',
    `report_data` JSON NOT NULL COMMENT '报告数据(JSON格式)',
    `data_count` INT DEFAULT 0 COMMENT '数据量',
    `data_source` VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '数据来源',
    `sync_status` VARCHAR(20) DEFAULT 'SYNCED' COMMENT '同步状态',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_report_date` (`report_date`),
    INDEX `idx_report_type` (`report_type`),
    INDEX `idx_sync_status` (`sync_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='综合统计报告表';

-- 6. 数据同步日志表
CREATE TABLE IF NOT EXISTS `data_sync_logs` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `sync_date` DATE NOT NULL COMMENT '同步日期',
    `sync_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
    `sync_status` VARCHAR(20) NOT NULL COMMENT '同步状态',
    `data_count` INT DEFAULT 0 COMMENT '同步数据量',
    `error_message` TEXT COMMENT '错误信息',
    `execution_time_ms` BIGINT DEFAULT 0 COMMENT '执行时间(毫秒)',
    `data_source` VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '数据来源',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_sync_date` (`sync_date`),
    INDEX `idx_sync_status` (`sync_status`),
    INDEX `idx_sync_time` (`sync_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据同步日志表';

-- 插入一些示例数据（可选）
INSERT IGNORE INTO `daily_total_income` (`report_date`, `total_income`, `total_reservations`, `avg_price_per_night`, `currency`, `data_source`) 
VALUES (CURDATE(), 0.00, 0, 0.00, 'EUR', 'EU-HOTEL-SYSTEM');

-- 创建视图：今日统计概览
CREATE OR REPLACE VIEW `today_statistics_overview` AS
SELECT 
    dti.report_date,
    dti.total_income,
    dti.total_reservations,
    dti.avg_price_per_night,
    rt.total_revenue as room_type_revenue,
    bp.total_revenue as branch_revenue,
    rt.completion_rate
FROM daily_total_income dti
LEFT JOIN (
    SELECT report_date, SUM(total_revenue) as total_revenue 
    FROM popular_room_types_top5 
    WHERE report_date = CURDATE()
) rt ON dti.report_date = rt.report_date
LEFT JOIN (
    SELECT report_date, SUM(total_revenue) as total_revenue 
    FROM branch_performance 
    WHERE report_date = CURDATE()
) bp ON dti.report_date = bp.report_date
LEFT JOIN reservation_trends rt ON dti.report_date = rt.report_date
WHERE dti.report_date = CURDATE();

-- 显示创建的表
SHOW TABLES;

-- 显示表结构
DESCRIBE daily_total_income;
DESCRIBE popular_room_types_top5;
DESCRIBE branch_performance;
DESCRIBE reservation_trends;
DESCRIBE comprehensive_reports;
DESCRIBE data_sync_logs;
