-- ============================================================================
-- Flyway 迁移脚本 V3: 创建成就系统表
-- 描述: 创建成就定义和用户成就关联表
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- ============================================================================
-- 1. 成就定义表 (achievement)
-- ============================================================================
CREATE TABLE `achievement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(50) NOT NULL COMMENT '成就编码',
    `name` VARCHAR(100) NOT NULL COMMENT '成就名称',
    `description` TEXT NULL COMMENT '成就描述',
    `icon_url` VARCHAR(255) NULL COMMENT '图标URL',
    `category` VARCHAR(50) NOT NULL COMMENT '分类: general-通用, streak-连续, course-课程, skill-技能',
    `condition_type` VARCHAR(50) NOT NULL COMMENT '条件类型: streak_days-连续天数, courses_completed-完成课程数, hours_studied-学习时长',
    `condition_value` INT NOT NULL COMMENT '条件值',
    `points` INT NOT NULL DEFAULT 0 COMMENT '成就积分',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category` (`category`),
    KEY `idx_condition_type` (`condition_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成就定义表';

-- ============================================================================
-- 2. 用户成就表 (user_achievement)
-- ============================================================================
CREATE TABLE `user_achievement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `achievement_id` BIGINT NOT NULL COMMENT '成就ID',
    `earned_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_achievement_id` (`achievement_id`),
    KEY `idx_earned_at` (`earned_at`),
    CONSTRAINT `fk_user_achievement_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_user_achievement_achievement` FOREIGN KEY (`achievement_id`) REFERENCES `achievement` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户成就表';
