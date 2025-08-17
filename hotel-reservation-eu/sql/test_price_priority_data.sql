-- ====================================================================
-- 价格优先级测试数据
-- 用于测试分店特定价格和房型基础价格的优先级
-- ====================================================================

-- 插入房型分店关联测试数据
-- 注意：需要根据实际的房型和分店ID进行调整

-- 测试场景1：分店1的标准间设置特定价格（高于基础价格）
INSERT INTO `room_type_branch_mapping` 
(`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
VALUES 
(1, 1, 1, 150.00, 5, 10, 1, 1);  -- 分店1的标准间，特定价格150.00（基础价格120.00）

-- 测试场景2：分店1的豪华间不设置特定价格（使用基础价格）
INSERT INTO `room_type_branch_mapping` 
(`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
VALUES 
(2, 1, 1, NULL, 3, 5, 2, 1);   -- 分店1的豪华间，使用基础价格180.00

-- 测试场景3：分店2的豪华间设置特定价格（低于基础价格）
INSERT INTO `room_type_branch_mapping` 
(`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
VALUES 
(2, 2, 1, 160.00, 4, 8, 1, 1);   -- 分店2的豪华间，特定价格160.00（基础价格180.00）

-- 测试场景4：分店2的套房设置特定价格（高于基础价格）
INSERT INTO `room_type_branch_mapping` 
(`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
VALUES 
(3, 2, 1, 500.00, 2, 3, 2, 1);   -- 分店2的套房，特定价格500.00（基础价格450.00）

-- 测试场景5：分店3的标准间不设置特定价格（使用基础价格）
INSERT INTO `room_type_branch_mapping` 
(`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
VALUES 
(1, 3, 1, NULL, 6, 12, 1, 1);   -- 分店3的标准间，使用基础价格120.00

-- ====================================================================
-- 验证查询SQL
-- ====================================================================

-- 查询所有分店的房型价格信息
SELECT 
    hb.branch_name as '分店名称',
    rt.type_name as '房型名称',
    rt.base_price as '房型基础价格',
    m.branch_specific_price as '分店特定价格',
    COALESCE(m.branch_specific_price, rt.base_price) as '最终使用价格',
    CASE 
        WHEN m.branch_specific_price IS NOT NULL THEN '分店特定价格'
        ELSE '房型基础价格'
    END as '价格来源',
    m.available_rooms_count as '可用房间数',
    m.max_rooms_count as '最大房间数'
FROM room_type_branch_mapping m
JOIN room_type rt ON m.room_type_id = rt.id
JOIN hotel_branch hb ON m.hotel_branch_id = hb.id
WHERE m.is_active = 1 
  AND rt.status = 1 
  AND hb.status = 1
ORDER BY hb.branch_name, m.sort_order;

-- 查询特定分店和房型的有效价格
-- 替换下面的参数值进行测试
-- SELECT 
--     hb.branch_name as '分店名称',
--     rt.type_name as '房型名称',
--     rt.base_price as '房型基础价格',
--     m.branch_specific_price as '分店特定价格',
--     COALESCE(m.branch_specific_price, rt.base_price) as '最终使用价格'
-- FROM room_type_branch_mapping m
-- JOIN room_type rt ON m.room_type_id = rt.id
-- JOIN hotel_branch hb ON m.hotel_branch_id = hb.id
-- WHERE m.room_type_id = ?  -- 替换为实际的房型ID
--   AND m.hotel_branch_id = ?  -- 替换为实际的分店ID
--   AND m.is_active = 1 
--   AND rt.status = 1 
--   AND hb.status = 1;
