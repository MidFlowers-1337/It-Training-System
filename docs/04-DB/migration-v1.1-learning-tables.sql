-- ============================================================================
-- IT 技能培训智能选课系统 - 数据库增量迁移脚本
-- 版本: v1.1.0 - 学习管理模块
-- 日期: 2025-12-14
-- 说明: 此脚本用于在现有数据库上添加学习管理相关的表
-- ============================================================================

USE `it_training`;

-- ============================================================================
-- 1. 用户技能标签表 (user_skill_tag)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `user_skill_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `skill_name` VARCHAR(50) NOT NULL COMMENT '技能名称',
    `proficiency_level` INT NOT NULL DEFAULT 0 COMMENT '熟练度(0-100)',
    `source` VARCHAR(20) NOT NULL DEFAULT 'self_report' COMMENT '来源: self_report-自评, course_complete-课程完成, assessment-测评',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_skill_name` (`skill_name`),
    UNIQUE KEY `uk_user_skill` (`user_id`, `skill_name`),
    CONSTRAINT `fk_skill_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户技能标签表';

-- ============================================================================
-- 2. 用户学习偏好表 (user_preference)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `user_preference` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `preferred_categories` JSON DEFAULT NULL COMMENT '偏好分类JSON数组',
    `preferred_difficulty` INT DEFAULT NULL COMMENT '偏好难度(1-4)',
    `learning_goal` VARCHAR(500) DEFAULT NULL COMMENT '学习目标',
    `weekly_hours` INT NOT NULL DEFAULT 5 COMMENT '每周学习时长目标(小时)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_pref_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习偏好表';

-- ============================================================================
-- 3. 课程相似度表 (course_similarity)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `course_similarity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id_a` BIGINT NOT NULL COMMENT '课程A ID',
    `course_id_b` BIGINT NOT NULL COMMENT '课程B ID',
    `similarity_score` DECIMAL(5,4) NOT NULL DEFAULT 0.0000 COMMENT '相似度分数(0-1)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_pair` (`course_id_a`, `course_id_b`),
    KEY `idx_course_a` (`course_id_a`),
    KEY `idx_course_b` (`course_id_b`),
    CONSTRAINT `fk_sim_course_a` FOREIGN KEY (`course_id_a`) REFERENCES `course` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sim_course_b` FOREIGN KEY (`course_id_b`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程相似度表';

-- ============================================================================
-- 4. 学习进度表 (learning_progress)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `learning_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `enrollment_id` BIGINT NOT NULL COMMENT '报名记录ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `progress_percent` INT NOT NULL DEFAULT 0 COMMENT '进度百分比(0-100)',
    `study_duration_minutes` INT NOT NULL DEFAULT 0 COMMENT '累计学习时长(分钟)',
    `last_study_at` DATETIME DEFAULT NULL COMMENT '最后学习时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'not_started' COMMENT '状态: not_started-未开始, in_progress-进行中, completed-已完成',
    `completed_at` DATETIME DEFAULT NULL COMMENT '完成时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_enrollment` (`enrollment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_progress_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_progress_enrollment` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_progress_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习进度表';

-- ============================================================================
-- 5. 学习计划表 (learning_plan)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `learning_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_name` VARCHAR(100) NOT NULL COMMENT '计划名称',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '计划描述',
    `target_courses` JSON DEFAULT NULL COMMENT '目标课程ID列表JSON',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `weekly_goal_hours` INT NOT NULL DEFAULT 5 COMMENT '每周目标学习时长(小时)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-进行中, completed-已完成, paused-已暂停, canceled-已取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_date_range` (`start_date`, `end_date`),
    CONSTRAINT `fk_plan_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习计划表';

-- ============================================================================
-- 6. 学习打卡记录表 (study_checkin)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `study_checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `checkin_date` DATE NOT NULL COMMENT '打卡日期',
    `study_minutes` INT NOT NULL DEFAULT 0 COMMENT '学习时长(分钟)',
    `courses_studied` JSON DEFAULT NULL COMMENT '学习的课程ID列表JSON',
    `note` VARCHAR(500) DEFAULT NULL COMMENT '学习笔记',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_checkin_date` (`checkin_date`),
    CONSTRAINT `fk_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习打卡记录表';

-- ============================================================================
-- 7. 成就定义表 (achievement)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `achievement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(50) NOT NULL COMMENT '成就编码',
    `name` VARCHAR(100) NOT NULL COMMENT '成就名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '成就描述',
    `icon_url` VARCHAR(255) DEFAULT NULL COMMENT '图标URL',
    `category` VARCHAR(50) NOT NULL DEFAULT 'general' COMMENT '分类: general-通用, streak-连续, course-课程, skill-技能',
    `condition_type` VARCHAR(50) NOT NULL COMMENT '条件类型: streak_days-连续天数, courses_completed-完成课程数, hours_studied-学习时长',
    `condition_value` INT NOT NULL COMMENT '条件值',
    `points` INT NOT NULL DEFAULT 10 COMMENT '成就积分',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category` (`category`),
    KEY `idx_condition_type` (`condition_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成就定义表';

-- ============================================================================
-- 8. 用户成就表 (user_achievement)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `user_achievement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `achievement_id` BIGINT NOT NULL COMMENT '成就ID',
    `earned_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_achievement` (`user_id`, `achievement_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_achievement_id` (`achievement_id`),
    CONSTRAINT `fk_ua_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ua_achievement` FOREIGN KEY (`achievement_id`) REFERENCES `achievement` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户成就表';

-- ============================================================================
-- 9. 用户学习统计表 (user_learning_stats)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `user_learning_stats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_study_minutes` INT NOT NULL DEFAULT 0 COMMENT '总学习时长(分钟)',
    `total_courses_enrolled` INT NOT NULL DEFAULT 0 COMMENT '报名课程总数',
    `total_courses_completed` INT NOT NULL DEFAULT 0 COMMENT '完成课程总数',
    `current_streak_days` INT NOT NULL DEFAULT 0 COMMENT '当前连续学习天数',
    `max_streak_days` INT NOT NULL DEFAULT 0 COMMENT '最长连续学习天数',
    `total_checkin_days` INT NOT NULL DEFAULT 0 COMMENT '总打卡天数',
    `total_achievement_points` INT NOT NULL DEFAULT 0 COMMENT '总成就积分',
    `last_study_date` DATE DEFAULT NULL COMMENT '最后学习日期',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_stats_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习统计表';

-- ============================================================================
-- 初始数据 - 成就定义
-- ============================================================================
INSERT IGNORE INTO `achievement` (`code`, `name`, `description`, `category`, `condition_type`, `condition_value`, `points`) VALUES
-- 连续学习成就
('STREAK_3', '初露锋芒', '连续学习3天', 'streak', 'streak_days', 3, 10),
('STREAK_7', '坚持不懈', '连续学习7天', 'streak', 'streak_days', 7, 30),
('STREAK_14', '学习达人', '连续学习14天', 'streak', 'streak_days', 14, 60),
('STREAK_30', '学霸养成', '连续学习30天', 'streak', 'streak_days', 30, 150),
('STREAK_100', '百日精进', '连续学习100天', 'streak', 'streak_days', 100, 500),
-- 课程完成成就
('COURSE_1', '初学乍练', '完成第1门课程', 'course', 'courses_completed', 1, 20),
('COURSE_3', '小有所成', '完成3门课程', 'course', 'courses_completed', 3, 50),
('COURSE_5', '技能多面手', '完成5门课程', 'course', 'courses_completed', 5, 100),
('COURSE_10', '知识渊博', '完成10门课程', 'course', 'courses_completed', 10, 250),
-- 学习时长成就
('HOURS_10', '十小时起步', '累计学习10小时', 'general', 'hours_studied', 10, 15),
('HOURS_50', '五十小时进阶', '累计学习50小时', 'general', 'hours_studied', 50, 80),
('HOURS_100', '百小时精通', '累计学习100小时', 'general', 'hours_studied', 100, 200),
('HOURS_500', '五百小时大师', '累计学习500小时', 'general', 'hours_studied', 500, 600);

-- ============================================================================
-- 初始数据 - 课程相似度
-- ============================================================================
INSERT IGNORE INTO `course_similarity` (`course_id_a`, `course_id_b`, `similarity_score`) VALUES
(1, 2, 0.8500),  -- Java基础 与 Spring Boot 高度相关
(1, 4, 0.4000),  -- Java基础 与 MySQL 中度相关
(2, 4, 0.5000),  -- Spring Boot 与 MySQL 中度相关
(3, 1, 0.2000),  -- Vue 与 Java基础 低度相关
(3, 2, 0.3000),  -- Vue 与 Spring Boot 低度相关（前后端配合）
(5, 1, 0.3000),  -- AI 与 Java基础 低度相关
(5, 4, 0.4000);  -- AI 与 MySQL 中度相关（数据处理）

-- ============================================================================
-- 为现有用户初始化学习统计记录
-- ============================================================================
INSERT IGNORE INTO `user_learning_stats` (`user_id`, `total_study_minutes`, `total_courses_enrolled`, `total_courses_completed`, `current_streak_days`, `max_streak_days`, `total_checkin_days`, `total_achievement_points`)
SELECT `id`, 0, 0, 0, 0, 0, 0, 0 FROM `sys_user` WHERE `deleted` = 0;

-- ============================================================================
-- 说明
-- ============================================================================
-- 执行方式:
-- mysql -u root -p it_training < migration-v1.1-learning-tables.sql
-- 
-- 或在 MySQL 客户端中执行:
-- source /path/to/migration-v1.1-learning-tables.sql
-- ============================================================================