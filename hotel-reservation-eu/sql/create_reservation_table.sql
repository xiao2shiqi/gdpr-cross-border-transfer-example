-- 创建预订表
CREATE TABLE IF NOT EXISTS reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预订ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    room_type_id BIGINT NOT NULL COMMENT '房型ID',
    branch_id BIGINT NOT NULL COMMENT '分店ID',
    checkin_date DATE NOT NULL COMMENT '入住日期',
    checkout_date DATE NOT NULL COMMENT '退房日期',
    guests INT NOT NULL COMMENT '入住人数',
    rooms INT NOT NULL COMMENT '房间数量',
    price_per_night DECIMAL(10,2) NOT NULL COMMENT '每晚价格',
    total_price DECIMAL(10,2) NOT NULL COMMENT '总价格',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '预订状态：PENDING-待确认, CONFIRMED-已确认, CANCELLED-已取消, COMPLETED-已完成',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态：PENDING-待支付, PAID-已支付, REFUNDED-已退款',
    payment_method VARCHAR(20) COMMENT '支付方式：CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER',
    payment_time DATETIME COMMENT '支付时间',
    special_requests TEXT COMMENT '特殊要求',
    contact_name VARCHAR(100) NOT NULL COMMENT '联系人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系人电话',
    contact_email VARCHAR(100) NOT NULL COMMENT '联系人邮箱',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    cancelled_at DATETIME COMMENT '取消时间',
    cancellation_reason TEXT COMMENT '取消原因',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_room_type_id (room_type_id),
    INDEX idx_branch_id (branch_id),
    INDEX idx_checkin_date (checkin_date),
    INDEX idx_status (status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预订信息表';
