-- 为中国BI系统数据库表添加region字段
-- 执行前请确保已备份数据库

USE china_bi_system;

-- 1. 为daily_total_income表添加region字段
ALTER TABLE daily_total_income 
ADD COLUMN region VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '地区标识 (EU, CHINA, US等)' AFTER report_date;

-- 2. 为popular_room_types_top5表添加region字段
ALTER TABLE popular_room_types_top5 
ADD COLUMN region VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '地区标识 (EU, CHINA, US等)' AFTER report_date;

-- 3. 为branch_performance表添加region字段
ALTER TABLE branch_performance 
ADD COLUMN region VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '地区标识 (EU, CHINA, US等)' AFTER report_date;

-- 4. 为reservation_trends表添加region字段
ALTER TABLE reservation_trends 
ADD COLUMN region VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '地区标识 (EU, CHINA, US等)' AFTER report_date;

-- 5. 为comprehensive_reports表添加region字段
ALTER TABLE comprehensive_reports 
ADD COLUMN region VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '地区标识 (EU, CHINA, US等)' AFTER report_date;

-- 6. 为data_sync_logs表添加region字段
ALTER TABLE data_sync_logs 
ADD COLUMN region VARCHAR(50) DEFAULT 'EU-HOTEL-SYSTEM' COMMENT '地区标识 (EU, CHINA, US等)' AFTER sync_date;

-- 7. 更新现有EU数据的region字段
UPDATE daily_total_income SET region = 'EU' WHERE region = 'EU-HOTEL-SYSTEM';
UPDATE popular_room_types_top5 SET region = 'EU' WHERE region = 'EU-HOTEL-SYSTEM';
UPDATE branch_performance SET region = 'EU' WHERE region = 'EU-HOTEL-SYSTEM';
UPDATE reservation_trends SET region = 'EU' WHERE region = 'EU-HOTEL-SYSTEM';
UPDATE comprehensive_reports SET region = 'EU' WHERE region = 'EU-HOTEL-SYSTEM';
UPDATE data_sync_logs SET region = 'EU' WHERE region = 'EU-HOTEL-SYSTEM';

-- 8. 为region字段添加索引以提高查询性能
CREATE INDEX idx_region ON daily_total_income (region);
CREATE INDEX idx_region ON popular_room_types_top5 (region);
CREATE INDEX idx_region ON branch_performance (region);
CREATE INDEX idx_region ON reservation_trends (region);
CREATE INDEX idx_region ON comprehensive_reports (region);
CREATE INDEX idx_region ON data_sync_logs (region);

-- 9. 验证字段添加成功
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'china_bi_system' 
AND COLUMN_NAME = 'region'
ORDER BY TABLE_NAME;
