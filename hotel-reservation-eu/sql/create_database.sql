-- 创建数据库
CREATE DATABASE IF NOT EXISTS `hotel-reservation-eu` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `hotel-reservation-eu`;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '加密密码',
    `first_name` VARCHAR(50) NOT NULL COMMENT '名字',
    `last_name` VARCHAR(50) NOT NULL COMMENT '姓氏',
    `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱地址',
    `phone_number` VARCHAR(20) COMMENT '电话号码',
    `address` VARCHAR(200) COMMENT '地址',
    `date_of_birth` DATE COMMENT '出生日期',
    `nationality` VARCHAR(50) COMMENT '国籍',
    `role` VARCHAR(20) DEFAULT 'USER' COMMENT '用户角色',
    `enabled` BOOLEAN DEFAULT TRUE COMMENT '账户是否启用',
    `account_non_expired` BOOLEAN DEFAULT TRUE COMMENT '账户是否未过期',
    `credentials_non_expired` BOOLEAN DEFAULT TRUE COMMENT '凭证是否未过期',
    `account_non_locked` BOOLEAN DEFAULT TRUE COMMENT '账户是否未锁定',
    `reset_password_token` VARCHAR(255) COMMENT '密码重置令牌',
    `reset_password_token_expiry` DATETIME COMMENT '密码重置令牌过期时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_reset_token` (`reset_password_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 清理可能存在的旧数据
DELETE FROM `user` WHERE username IN ('admin', 'testuser');

-- 插入测试用户数据（密码为 'password123'）
INSERT INTO `user` (`username`, `password`, `first_name`, `last_name`, `email`, `role`, `enabled`, `account_non_expired`, `credentials_non_expired`, `account_non_locked`) 
VALUES 
('admin', '$2a$10$ZLzeiAV5LtjP/qoBviO/WugEegxKQNy8u9nNVfgnVWZp57x1qjkji', 'Admin', 'User', 'admin@hotel-eu.com', 'ADMIN', TRUE, TRUE, TRUE, TRUE),
('testuser', '$2a$10$ZLzeiAV5LtjP/qoBviO/WugEegxKQNy8u9nNVfgnVWZp57x1qjkji', 'Test', 'User', 'test@hotel-eu.com', 'USER', TRUE, TRUE, TRUE, TRUE);

-- 验证插入结果
SELECT username, email, role, enabled FROM `user` WHERE username IN ('admin', 'testuser');


-- 添加GDPR字段
ALTER TABLE user ADD COLUMN gdpr_processing_consent BOOLEAN DEFAULT FALSE COMMENT '是否同意数据处理（必需）';
ALTER TABLE user ADD COLUMN gdpr_marketing_consent BOOLEAN DEFAULT FALSE COMMENT '是否同意营销邮件（可选）';
ALTER TABLE user ADD COLUMN gdpr_analytics_consent BOOLEAN DEFAULT FALSE COMMENT '是否同意数据分析（可选）';
ALTER TABLE user ADD COLUMN gdpr_consent_date DATETIME COMMENT 'GDPR同意时间';

-- 为现有用户设置默认同意
UPDATE user SET gdpr_processing_consent = TRUE, gdpr_consent_date = NOW() WHERE gdpr_processing_consent IS NULL;


-- 创建酒店分店表
CREATE TABLE hotel_branch (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              branch_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分店代码',
                              branch_name VARCHAR(100) NOT NULL COMMENT '分店名称',
                              city VARCHAR(50) NOT NULL COMMENT '城市',
                              country VARCHAR(50) NOT NULL COMMENT '国家',
                              address VARCHAR(200) COMMENT '地址',
                              phone VARCHAR(20) COMMENT '电话',
                              email VARCHAR(100) COMMENT '邮箱',
                              status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-停用',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒店分店表';

-- 创建房型表
CREATE TABLE room_type (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           branch_id BIGINT NOT NULL COMMENT '分店ID',
                           type_code VARCHAR(50) NOT NULL COMMENT '房型代码',
                           type_name VARCHAR(100) NOT NULL COMMENT '房型名称',
                           description TEXT COMMENT '房型描述',
                           size INT NOT NULL COMMENT '房间面积(平方米)',
                           bed_type VARCHAR(50) NOT NULL COMMENT '床型',
                           max_guests INT NOT NULL COMMENT '最大入住人数',
                           base_price DECIMAL(10,2) NOT NULL COMMENT '基础价格',
                           image_url VARCHAR(500) COMMENT '房型图片URL',
                           amenities JSON COMMENT '房间设施',
                           total_rooms INT NOT NULL DEFAULT 0 COMMENT '总房间数',
                           available_rooms INT NOT NULL DEFAULT 0 COMMENT '可用房间数',
                           status TINYINT DEFAULT 1 COMMENT '状态 1-正常 0-停用',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX idx_branch_id (branch_id),
                           INDEX idx_type_code (type_code),
                           INDEX idx_status (status),
                           FOREIGN KEY (branch_id) REFERENCES hotel_branch(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';

-- 插入酒店分店数据
INSERT INTO hotel_branch (branch_code, branch_name, city, country) VALUES
                                                                       ('paris-center', '巴黎市中心', '巴黎', '法国'),
                                                                       ('paris-airport', '巴黎机场', '巴黎', '法国'),
                                                                       ('london-city', '伦敦金融城', '伦敦', '英国'),
                                                                       ('london-west', '伦敦西区', '伦敦', '英国'),
                                                                       ('rome-center', '罗马历史中心', '罗马', '意大利'),
                                                                       ('rome-vatican', '罗马梵蒂冈', '罗马', '意大利'),
                                                                       ('barcelona-gothic', '巴塞罗那哥特区', '巴塞罗那', '西班牙'),
                                                                       ('barcelona-beach', '巴塞罗那海滩', '巴塞罗那', '西班牙'),
                                                                       ('amsterdam-canal', '阿姆斯特丹运河', '阿姆斯特丹', '荷兰'),
                                                                       ('vienna-center', '维也纳中央', '维也纳', '奥地利'),
                                                                       ('berlin-mitte', '柏林米特区', '柏林', '德国'),
                                                                       ('prague-castle', '布拉格城堡', '布拉格', '捷克');

-- 为每个分店插入6种房型数据（移除库存字段）
INSERT INTO room_type (branch_id, type_code, type_name, description, size, bed_type, max_guests, base_price, image_url, amenities)
SELECT
    b.id as branch_id,
    CONCAT(b.branch_code, '-', rt.type_code) as type_code,
    rt.type_name,
    rt.description,
    rt.size,
    rt.bed_type,
    rt.max_guests,
    rt.base_price + (FLOOR(RAND() * 50)) as base_price,
    rt.image_url,
    rt.amenities
FROM hotel_branch b
         CROSS JOIN (
    SELECT 'SINGLE' as type_code, '标准单人间' as type_name, '舒适单人间，配备单人床，适合商务出行' as description,
           25 as size, '单人床' as bed_type, 1 as max_guests, 120 as base_price,
           'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=200&h=150&fit=crop&crop=center' as image_url,
           JSON_ARRAY('免费WiFi', '空调', '24小时热水') as amenities

    UNION ALL
    SELECT 'DOUBLE' as type_code, '标准双人间' as type_name, '宽敞双人间，配备两张单人床或一张双人床' as description,
        35 as size, '双床/大床' as bed_type, 2 as max_guests, 180 as base_price,
        'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=200&h=150&fit=crop&crop=center' as image_url,
        JSON_ARRAY('免费WiFi', '空调', '24小时热水') as amenities

    UNION ALL
    SELECT 'DELUXE' as type_code, '豪华大床房' as type_name, '豪华大床房，配备特大床和城市景观' as description,
        45 as size, '特大床' as bed_type, 2 as max_guests, 280 as base_price,
        'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=200&h=150&fit=crop&crop=center' as image_url,
        JSON_ARRAY('免费WiFi', '空调', '24小时热水', '迷你吧') as amenities

    UNION ALL
    SELECT 'EXECUTIVE' as type_code, '行政套房' as type_name, '行政楼层套房，享受专属服务和会议设施' as description,
        65 as size, '大床+沙发床' as bed_type, 2 as max_guests, 450 as base_price,
        'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=200&h=150&fit=crop&crop=center' as image_url,
        JSON_ARRAY('免费WiFi', '空调', '24小时热水', '迷你吧', '行政酒廊') as amenities

    UNION ALL
    SELECT 'PRESIDENTIAL' as type_code, '总统套房' as type_name, '顶级总统套房，奢华装修，全景落地窗' as description,
        120 as size, '特大床+客厅' as bed_type, 2 as max_guests, 800 as base_price,
        'https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=200&h=150&fit=crop&crop=center' as image_url,
        JSON_ARRAY('免费WiFi', '空调', '24小时热水', '迷你吧', '行政酒廊', '管家服务') as amenities

    UNION ALL
    SELECT 'FAMILY' as type_code, '家庭房' as type_name, '适合家庭的大空间房型，配备儿童设施' as description,
        55 as size, '大床+儿童床' as bed_type, 4 as max_guests, 350 as base_price,
        'https://images.unsplash.com/photo-1595576508898-0ad5c879a061?w=200&h=150&fit=crop&crop=center' as image_url,
        JSON_ARRAY('免费WiFi', '空调', '24小时热水', '儿童乐园') as amenities
) rt;