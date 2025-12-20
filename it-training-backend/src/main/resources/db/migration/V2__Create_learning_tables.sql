-- ============================================================================
-- Flyway 迁移脚本 V2: 创建学习管理表
-- 描述: 创建学习进度、学习计划、打卡记录、用户统计等表
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- ============================================================================
-- 1. 学习进度表 (learning_progress)
-- ============================================================================
CREATE TABLE `learning_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `enrollment_id` BIGINT NOT NULL COMMENT '报名记录ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `progress_percent` TINYINT NOT NULL DEFAULT 0 COMMENT '进度百分比(0-100)',
    `study_duration_minutes` INT NOT NULL DEFAULT 0 COMMENT '累计学习时长(分钟)',
    `last_study_at` DATETIME NULL COMMENT '最后学习时间',
    `status` VARCHAR(20) NOT NULL DEFAULT 'not_started' COMMENT '状态: not_started-未开始, in_progress-进行中, completed-已完成',
    `completed_at` DATETIME NULL COMMENT '完成时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_enrollment` (`user_id`, `enrollment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_enrollment_id` (`enrollment_id`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_status` (`status`),
    KEY `idx_last_study_at` (`last_study_at`),
    CONSTRAINT `fk_progress_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_progress_enrollment` FOREIGN KEY (`enrollment_id`) REFERENCES `enrollment` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_progress_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习进度表';

-- ============================================================================
-- 2. 学习计划表 (learning_plan)
-- ============================================================================
CREATE TABLE `learning_plan` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_name` VARCHAR(100) NOT NULL COMMENT '计划名称',
    `description` TEXT NULL COMMENT '计划描述',
    `target_courses` TEXT NULL COMMENT '目标课程ID列表JSON',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `weekly_goal_hours` INT NOT NULL DEFAULT 0 COMMENT '每周目标学习时长(小时)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态: active-进行中, completed-已完成, paused-已暂停, canceled-已取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_date` (`start_date`),
    KEY `idx_end_date` (`end_date`),
    CONSTRAINT `fk_plan_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习计划表';

-- ============================================================================
-- 3. 学习打卡记录表 (study_checkin)
-- ============================================================================
CREATE TABLE `study_checkin` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `checkin_date` DATE NOT NULL COMMENT '打卡日期',
    `study_minutes` INT NOT NULL DEFAULT 0 COMMENT '学习时长(分钟)',
    `courses_studied` TEXT NULL COMMENT '学习的课程ID列表JSON',
    `note` TEXT NULL COMMENT '学习笔记',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_checkin_date` (`checkin_date`),
    CONSTRAINT `fk_checkin_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习打卡记录表';

-- ============================================================================
-- 4. 用户学习统计表 (user_learning_stats)
-- ============================================================================
CREATE TABLE `user_learning_stats` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_study_minutes` INT NOT NULL DEFAULT 0 COMMENT '总学习时长(分钟)',
    `total_courses_enrolled` INT NOT NULL DEFAULT 0 COMMENT '报名课程总数',
    `total_courses_completed` INT NOT NULL DEFAULT 0 COMMENT '完成课程总数',
    `current_streak_days` INT NOT NULL DEFAULT 0 COMMENT '当前连续学习天数',
    `max_streak_days` INT NOT NULL DEFAULT 0 COMMENT '最长连续学习天数',
    `total_checkin_days` INT NOT NULL DEFAULT 0 COMMENT '总打卡天数',
    `total_achievement_points` INT NOT NULL DEFAULT 0 COMMENT '总成就积分',
    `last_study_date` DATE NULL COMMENT '最后学习日期',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_total_study_minutes` (`total_study_minutes`),
    KEY `idx_current_streak_days` (`current_streak_days`),
    KEY `idx_total_achievement_points` (`total_achievement_points`),
    CONSTRAINT `fk_stats_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习统计表';

-- ============================================================================
-- 5. 用户学习偏好表 (user_preference)
-- ============================================================================
CREATE TABLE `user_preference` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `preferred_categories` TEXT NULL COMMENT '偏好分类JSON数组',
    `preferred_difficulty` TINYINT NULL COMMENT '偏好难度(1-4)',
    `learning_goal` TEXT NULL COMMENT '学习目标',
    `weekly_hours` INT NULL COMMENT '每周学习时长目标(小时)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_preference_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习偏好表';

-- ============================================================================
-- 6. 用户技能标签表 (user_skill_tag)
-- ============================================================================
CREATE TABLE `user_skill_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `skill_name` VARCHAR(100) NOT NULL COMMENT '技能名称',
    `proficiency_level` TINYINT NOT NULL DEFAULT 0 COMMENT '熟练度(0-100)',
    `source` VARCHAR(50) NOT NULL COMMENT '来源: self_report-自评, course_complete-课程完成, assessment-测评',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_skill_name` (`skill_name`),
    KEY `idx_proficiency_level` (`proficiency_level`),
    CONSTRAINT `fk_skill_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户技能标签表';

-- ============================================================================
-- 7. 课程相似度表 (course_similarity)
-- ============================================================================
CREATE TABLE `course_similarity` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id_a` BIGINT NOT NULL COMMENT '课程A ID',
    `course_id_b` BIGINT NOT NULL COMMENT '课程B ID',
    `similarity_score` DECIMAL(5,4) NOT NULL DEFAULT 0.0000 COMMENT '相似度分数(0-1)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_course_pair` (`course_id_a`, `course_id_b`),
    KEY `idx_course_id_a` (`course_id_a`),
    KEY `idx_course_id_b` (`course_id_b`),
    KEY `idx_similarity_score` (`similarity_score`),
    CONSTRAINT `fk_similarity_course_a` FOREIGN KEY (`course_id_a`) REFERENCES `course` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_similarity_course_b` FOREIGN KEY (`course_id_b`) REFERENCES `course` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程相似度表';
