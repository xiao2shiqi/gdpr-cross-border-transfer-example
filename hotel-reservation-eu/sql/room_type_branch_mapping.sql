-- ====================================================================
-- 房型分店关联表 DDL
-- 用于管理房型和分店之间的多对多关系
-- ====================================================================

-- 创建房型分店关联表
CREATE TABLE `room_type_branch_mapping` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_type_id` BIGINT NOT NULL COMMENT '房型ID，关联room_type表',
  `hotel_branch_id` BIGINT NOT NULL COMMENT '分店ID，关联hotel_branch表',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用，0-停用',
  `branch_specific_price` DECIMAL(10,2) NULL COMMENT '分店特定价格（可选，为空则使用房型基础价格）',
  `available_rooms_count` INT NOT NULL DEFAULT 0 COMMENT '该分店该房型的可用房间数量',
  `max_rooms_count` INT NOT NULL DEFAULT 0 COMMENT '该分店该房型的最大房间数量',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '显示排序，数值越小越靠前',
  `special_amenities` TEXT NULL COMMENT '该分店特有的设施（JSON格式，补充房型基础设施）',
  `notes` VARCHAR(500) NULL COMMENT '备注信息',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` BIGINT NULL COMMENT '创建人ID',
  `updated_by` BIGINT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_type_branch` (`room_type_id`, `hotel_branch_id`) COMMENT '房型分店唯一约束',
  KEY `idx_room_type_id` (`room_type_id`),
  KEY `idx_hotel_branch_id` (`hotel_branch_id`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_sort_order` (`sort_order`),
  CONSTRAINT `fk_mapping_room_type` FOREIGN KEY (`room_type_id`) REFERENCES `room_type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_mapping_hotel_branch` FOREIGN KEY (`hotel_branch_id`) REFERENCES `hotel_branch` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房型分店关联表';

-- ====================================================================
-- 插入示例数据（可选）
-- ====================================================================

-- 假设已经有房型和分店数据，插入一些关联关系示例
-- 注意：需要根据实际的房型和分店ID进行调整

-- 示例：分店1发布标准间和豪华间
-- INSERT INTO `room_type_branch_mapping` 
-- (`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
-- VALUES 
-- (1, 1, 1, 120.00, 5, 10, 1, 1),  -- 分店1的标准间
-- (2, 1, 1, 180.00, 3, 5, 2, 1);   -- 分店1的豪华间

-- 示例：分店2发布豪华间和套房
-- INSERT INTO `room_type_branch_mapping` 
-- (`room_type_id`, `hotel_branch_id`, `is_active`, `branch_specific_price`, `available_rooms_count`, `max_rooms_count`, `sort_order`, `created_by`)
-- VALUES 
-- (2, 2, 1, 200.00, 4, 8, 1, 1),   -- 分店2的豪华间
-- (3, 2, 1, 350.00, 2, 3, 2, 1);   -- 分店2的套房

-- ====================================================================
-- 常用查询示例
-- ====================================================================

-- 查询某分店的所有可用房型
-- SELECT 
--     m.id as mapping_id,
--     rt.id as room_type_id,
--     rt.type_name,
--     rt.description,
--     rt.bed_type,
--     rt.size,
--     rt.max_guests,
--     COALESCE(m.branch_specific_price, rt.base_price) as effective_price,
--     m.available_rooms_count,
--     m.max_rooms_count,
--     m.sort_order
-- FROM room_type_branch_mapping m
-- JOIN room_type rt ON m.room_type_id = rt.id
-- WHERE m.hotel_branch_id = ? 
--   AND m.is_active = 1 
--   AND rt.status = 1
-- ORDER BY m.sort_order, rt.type_name;

-- 查询某房型在哪些分店可用
-- SELECT 
--     hb.id as branch_id,
--     hb.branch_name,
--     hb.city,
--     hb.country,
--     COALESCE(m.branch_specific_price, rt.base_price) as effective_price,
--     m.available_rooms_count,
--     m.max_rooms_count
-- FROM room_type_branch_mapping m
-- JOIN hotel_branch hb ON m.hotel_branch_id = hb.id
-- JOIN room_type rt ON m.room_type_id = rt.id
-- WHERE m.room_type_id = ? 
--   AND m.is_active = 1 
--   AND hb.status = 1 
--   AND rt.status = 1
-- ORDER BY hb.branch_name;

-- ====================================================================
-- 索引说明
-- ====================================================================
-- 1. uk_room_type_branch: 确保同一房型在同一分店只能有一条记录
-- 2. idx_room_type_id: 快速查询某房型的分店分布
-- 3. idx_hotel_branch_id: 快速查询某分店的房型列表
-- 4. idx_is_active: 快速过滤启用状态
-- 5. idx_sort_order: 支持排序查询

-- ====================================================================
-- 业务约束说明
-- ====================================================================
-- 1. 同一房型在同一分店只能有一条有效记录
-- 2. branch_specific_price为空时使用房型的base_price
-- 3. available_rooms_count不能超过max_rooms_count
-- 4. 删除房型或分店时，关联记录会被级联删除
-- 5. sort_order用于控制房型在分店页面的显示顺序