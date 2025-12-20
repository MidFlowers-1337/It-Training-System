-- ============================================================================
-- Flyway 迁移脚本 V7: 修复学习进度表字段约束
-- 描述: 将 enrollment_id 改为可空，因为学习进度可能在正式报名前就开始追踪
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- 1. 修改 learning_progress 表的 enrollment_id 字段为可空
ALTER TABLE `learning_progress` 
    MODIFY COLUMN `enrollment_id` BIGINT NULL COMMENT '报名记录ID(可空)';

-- 2. 删除原有的外键约束和唯一约束，添加新的约束
-- 注意：由于 Flyway 不支持条件执行，这里直接执行，如果约束不存在会报错
-- 建议在执行前手动检查或使用 baseline

-- 如果需要手动执行，请运行以下 SQL：
-- SET FOREIGN_KEY_CHECKS = 0;
-- ALTER TABLE `learning_progress` DROP FOREIGN KEY IF EXISTS `fk_progress_enrollment`;
-- ALTER TABLE `learning_progress` DROP INDEX IF EXISTS `uk_user_enrollment`;
-- SET FOREIGN_KEY_CHECKS = 1;
-- ALTER TABLE `learning_progress` ADD UNIQUE KEY `uk_user_course` (`user_id`, `course_id`);