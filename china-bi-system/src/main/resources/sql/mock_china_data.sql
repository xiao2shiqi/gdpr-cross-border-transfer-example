-- 虚拟中国地区运营数据
-- 执行前请确保已执行add_region_field.sql添加region字段

USE china_bi_system;

-- 1. 插入中国地区每日总收入数据（最近7天）
INSERT INTO daily_total_income (
    report_date, region, total_income, total_reservations, 
    avg_price_per_night, currency, data_source, sync_status
) VALUES 
-- 今天
(CURDATE(), 'CHINA', 15800.00, 45, 351.11, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED'),
-- 昨天
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 14200.00, 38, 373.68, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED'),
-- 前天
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 16500.00, 42, 392.86, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED'),
-- 3天前
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'CHINA', 13800.00, 35, 394.29, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED'),
-- 4天前
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'CHINA', 15200.00, 40, 380.00, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED'),
-- 5天前
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'CHINA', 16900.00, 44, 384.09, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED'),
-- 6天前
(DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'CHINA', 14500.00, 37, 391.89, 'CNY', 'MOCK-CHINA-DATA', 'SYNCED');

-- 2. 插入中国地区热门房型Top5数据（最近7天）
INSERT INTO popular_room_types_top5 (
    report_date, region, room_type_id, room_type_name, 
    reservation_count, total_revenue, ranking, data_source, sync_status
) VALUES 
-- 今天的房型数据
(CURDATE(), 'CHINA', 101, '豪华大床房', 12, 4800.00, 1, 'MOCK-CHINA-DATA', 'SYNCED'),
(CURDATE(), 'CHINA', 102, '商务双床房', 10, 3800.00, 2, 'MOCK-CHINA-DATA', 'SYNCED'),
(CURDATE(), 'CHINA', 103, '家庭套房', 8, 3200.00, 3, 'MOCK-CHINA-DATA', 'SYNCED'),
(CURDATE(), 'CHINA', 104, '总统套房', 6, 2400.00, 4, 'MOCK-CHINA-DATA', 'SYNCED'),
(CURDATE(), 'CHINA', 105, '标准间', 9, 1600.00, 5, 'MOCK-CHINA-DATA', 'SYNCED'),

-- 昨天的房型数据
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 101, '豪华大床房', 11, 4400.00, 1, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 102, '商务双床房', 9, 3420.00, 2, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 103, '家庭套房', 7, 2800.00, 3, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 104, '总统套房', 5, 2000.00, 4, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 105, '标准间', 6, 1200.00, 5, 'MOCK-CHINA-DATA', 'SYNCED'),

-- 前天的房型数据
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 101, '豪华大床房', 13, 5200.00, 1, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 102, '商务双床房', 11, 4180.00, 2, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 103, '家庭套房', 9, 3600.00, 3, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 104, '总统套房', 7, 2800.00, 4, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 105, '标准间', 2, 400.00, 5, 'MOCK-CHINA-DATA', 'SYNCED');

-- 3. 插入中国地区分店业绩数据（最近7天）
INSERT INTO branch_performance (
    report_date, region, branch_id, branch_name, reservation_count, 
    total_revenue, avg_revenue_per_reservation, data_source, sync_status
) VALUES 
-- 今天的分店数据
(CURDATE(), 'CHINA', 201, '北京朝阳店', 18, 6800.00, 377.78, 'MOCK-CHINA-DATA', 'SYNCED'),
(CURDATE(), 'CHINA', 202, '上海浦东店', 15, 5600.00, 373.33, 'MOCK-CHINA-DATA', 'SYNCED'),
(CURDATE(), 'CHINA', 203, '广州天河店', 12, 3400.00, 283.33, 'MOCK-CHINA-DATA', 'SYNCED'),

-- 昨天的分店数据
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 201, '北京朝阳店', 16, 6000.00, 375.00, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 202, '上海浦东店', 13, 4800.00, 369.23, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 203, '广州天河店', 9, 3400.00, 377.78, 'MOCK-CHINA-DATA', 'SYNCED'),

-- 前天的分店数据
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 201, '北京朝阳店', 19, 7200.00, 378.95, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 202, '上海浦东店', 14, 5200.00, 371.43, 'MOCK-CHINA-DATA', 'SYNCED'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 203, '广州天河店', 9, 4100.00, 455.56, 'MOCK-CHINA-DATA', 'SYNCED');

-- 4. 插入中国地区预订趋势数据（最近7天）
INSERT INTO reservation_trends (
    report_date, region, total_reservations, confirmed_reservations, 
    cancelled_reservations, completion_rate, data_source, sync_status
) VALUES 
-- 今天的趋势数据
(CURDATE(), 'CHINA', 45, 42, 3, 93.33, 'MOCK-CHINA-DATA', 'SYNCED'),
-- 昨天的趋势数据
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 38, 35, 3, 92.11, 'MOCK-CHINA-DATA', 'SYNCED'),
-- 前天的趋势数据
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 42, 39, 3, 92.86, 'MOCK-CHINA-DATA', 'SYNCED'),
-- 3天前的趋势数据
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'CHINA', 35, 32, 3, 91.43, 'MOCK-CHINA-DATA', 'SYNCED'),
-- 4天前的趋势数据
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'CHINA', 40, 37, 3, 92.50, 'MOCK-CHINA-DATA', 'SYNCED'),
-- 5天前的趋势数据
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'CHINA', 44, 41, 3, 93.18, 'MOCK-CHINA-DATA', 'SYNCED'),
-- 6天前的趋势数据
(DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'CHINA', 37, 34, 3, 91.89, 'MOCK-CHINA-DATA', 'SYNCED');

-- 5. 插入中国地区综合报告数据
INSERT INTO comprehensive_reports (
    report_date, region, report_type, report_data, data_count, data_source, sync_status
) VALUES 
(CURDATE(), 'CHINA', 'CHINA_DAILY_SUMMARY', 
 '{"totalIncome": 15800.00, "totalReservations": 45, "avgPrice": 351.11, "currency": "CNY", "topRoomTypes": 5, "branchCount": 3}', 
 5, 'MOCK-CHINA-DATA', 'SYNCED'),

(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', 'CHINA_DAILY_SUMMARY', 
 '{"totalIncome": 14200.00, "totalReservations": 38, "avgPrice": 373.68, "currency": "CNY", "topRoomTypes": 5, "branchCount": 3}', 
 5, 'MOCK-CHINA-DATA', 'SYNCED'),

(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', 'CHINA_DAILY_SUMMARY', 
 '{"totalIncome": 16500.00, "totalReservations": 42, "avgPrice": 392.86, "currency": "CNY", "topRoomTypes": 5, "branchCount": 3}', 
 5, 'MOCK-CHINA-DATA', 'SYNCED');

-- 6. 插入中国地区数据同步日志
INSERT INTO data_sync_logs (
    sync_date, region, sync_time, sync_status, data_count, 
    error_message, execution_time_ms, data_source
) VALUES 
(CURDATE(), 'CHINA', NOW(), 'SUCCESS', 25, NULL, 150, 'MOCK-CHINA-DATA'),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'CHINA', NOW(), 'SUCCESS', 22, NULL, 120, 'MOCK-CHINA-DATA'),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'CHINA', NOW(), 'SUCCESS', 24, NULL, 135, 'MOCK-CHINA-DATA');

-- 7. 验证数据插入成功
SELECT 'daily_total_income' as table_name, COUNT(*) as count FROM daily_total_income WHERE region = 'CHINA'
UNION ALL
SELECT 'popular_room_types_top5' as table_name, COUNT(*) as count FROM popular_room_types_top5 WHERE region = 'CHINA'
UNION ALL
SELECT 'branch_performance' as table_name, COUNT(*) as count FROM branch_performance WHERE region = 'CHINA'
UNION ALL
SELECT 'reservation_trends' as table_name, COUNT(*) as count FROM reservation_trends WHERE region = 'CHINA'
UNION ALL
SELECT 'comprehensive_reports' as table_name, COUNT(*) as count FROM comprehensive_reports WHERE region = 'CHINA'
UNION ALL
SELECT 'data_sync_logs' as table_name, COUNT(*) as count FROM data_sync_logs WHERE region = 'CHINA';

-- 8. 查看中国地区今日数据概览
SELECT 
    '今日收入' as metric,
    SUM(total_income) as value,
    'CNY' as unit
FROM daily_total_income 
WHERE region = 'CHINA' AND report_date = CURDATE()

UNION ALL

SELECT 
    '今日预订数' as metric,
    SUM(total_reservations) as value,
    '个' as unit
FROM daily_total_income 
WHERE region = 'CHINA' AND report_date = CURDATE()

UNION ALL

SELECT 
    '7天累计收入' as metric,
    SUM(total_income) as value,
    'CNY' as unit
FROM daily_total_income 
WHERE region = 'CHINA' AND report_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)

UNION ALL

SELECT 
    '7天累计预订数' as metric,
    SUM(total_reservations) as value,
    '个' as unit
FROM daily_total_income 
WHERE region = 'CHINA' AND report_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
