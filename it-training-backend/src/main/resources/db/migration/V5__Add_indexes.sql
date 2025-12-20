-- ============================================================================
-- Flyway 迁移脚本 V5: 添加索引和性能优化
-- 描述: 添加复合索引和性能优化索引
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- ============================================================================
-- 复合索引优化
-- ============================================================================

-- 用户表：按角色和状态查询活跃用户
CREATE INDEX `idx_role_status` ON `sys_user` (`role`, `status`, `deleted`);

-- 课程表：按分类和难度筛选上架课程
CREATE INDEX `idx_category_difficulty_status` ON `course` (`category`, `difficulty`, `status`, `deleted`);

-- 班次表：按课程查询进行中的班次
CREATE INDEX `idx_course_status_start` ON `class_session` (`course_id`, `status`, `start_date`, `deleted`);

-- 报名表：按用户查询有效报名
CREATE INDEX `idx_user_status_enrolled` ON `enrollment` (`user_id`, `status`, `enrolled_at`);

-- 学习进度表：按用户查询进行中的课程
CREATE INDEX `idx_user_status_progress` ON `learning_progress` (`user_id`, `status`, `progress_percent`);

-- 学习计划表：按用户查询活跃计划
CREATE INDEX `idx_user_status_dates` ON `learning_plan` (`user_id`, `status`, `start_date`, `end_date`);

-- 打卡记录表：按用户查询最近打卡
CREATE INDEX `idx_user_date_desc` ON `study_checkin` (`user_id`, `checkin_date` DESC);

-- 用户成就表：按用户查询最近获得的成就
CREATE INDEX `idx_user_earned` ON `user_achievement` (`user_id`, `earned_at` DESC);

-- AI 推荐日志表：按用户查询最近推荐
CREATE INDEX `idx_user_created_desc` ON `ai_recommend_log` (`user_id`, `created_at` DESC);

-- ============================================================================
-- 全文搜索优化（MySQL 5.7+）
-- ============================================================================

-- 课程表：课程名称和描述全文搜索
-- 注意：TiDB 不支持全文索引，此索引仅在 MySQL 环境生效
-- CREATE FULLTEXT INDEX `ft_course_search` ON `course` (`name`, `description`);

-- ============================================================================
-- 性能说明
-- ============================================================================
-- 1. 复合索引遵循最左前缀原则，查询时应按索引顺序使用条件
-- 2. 索引字段顺序：等值查询 > 范围查询 > 排序字段
-- 3. TiDB 环境下，索引会自动分布到多个 TiKV 节点
-- 4. 定期使用 ANALYZE TABLE 更新统计信息以优化查询计划
-- ============================================================================
