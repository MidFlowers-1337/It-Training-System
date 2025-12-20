-- ============================================================================
-- Flyway 迁移脚本 V4: 创建 AI 推荐相关表
-- 描述: 创建 AI 推荐日志表
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- ============================================================================
-- 1. AI 推荐日志表 (ai_recommend_log)
-- ============================================================================
CREATE TABLE `ai_recommend_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '请求用户ID',
    `input_text` TEXT NOT NULL COMMENT '用户输入的学习目标',
    `recommended_courses` TEXT NULL COMMENT '推荐结果JSON（包含课程ID列表和顺序）',
    `reason` TEXT NULL COMMENT '推荐理由',
    `model_used` VARCHAR(100) NULL COMMENT '使用的AI模型（或FALLBACK表示降级）',
    `response_time_ms` INT NULL COMMENT '响应耗时(毫秒)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_model_used` (`model_used`),
    CONSTRAINT `fk_ai_log_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI推荐日志表';
