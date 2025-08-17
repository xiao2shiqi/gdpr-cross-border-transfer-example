-- ===========================================
-- GDPR合规性数据库更新脚本
-- 为user表添加数据保护相关字段
-- ===========================================

-- 连接到数据库
-- USE hotel_reservation_eu;

-- 添加GDPR同意字段
ALTER TABLE user 
ADD COLUMN gdpr_processing_consent BOOLEAN DEFAULT FALSE COMMENT '是否同意数据处理（必需）',
ADD COLUMN gdpr_marketing_consent BOOLEAN DEFAULT FALSE COMMENT '是否同意营销邮件（可选）',
ADD COLUMN gdpr_analytics_consent BOOLEAN DEFAULT FALSE COMMENT '是否同意数据分析（可选）',
ADD COLUMN gdpr_consent_date DATETIME COMMENT 'GDPR同意时间';

-- 为现有用户设置默认的必需同意（假设他们已同意）
UPDATE user 
SET gdpr_processing_consent = TRUE,
    gdpr_consent_date = NOW()
WHERE gdpr_processing_consent IS NULL;

-- 验证更新结果
-- SELECT username, email, gdpr_processing_consent, gdpr_marketing_consent, 
--        gdpr_analytics_consent, gdpr_consent_date
-- FROM user 
-- LIMIT 5;

-- 显示更新后的表结构
-- DESCRIBE user; 