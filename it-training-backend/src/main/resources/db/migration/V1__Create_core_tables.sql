-- ============================================================================
-- Flyway 迁移脚本 V1: 创建核心业务表
-- 描述: 创建用户、课程、班次、报名等核心业务表
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- ============================================================================
-- 1. 用户表 (sys_user)
-- ============================================================================
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) NULL COMMENT '手机号',
    `email` VARCHAR(100) NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) NULL COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: ADMIN-管理员, INSTRUCTOR-讲师, STUDENT-学员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除: 0-正常, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================================
-- 2. 课程表 (course)
-- ============================================================================
CREATE TABLE `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(50) NOT NULL COMMENT '课程编码（如: JAVA-001）',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `category` VARCHAR(50) NOT NULL COMMENT '分类: BACKEND-后端开发, FRONTEND-前端开发, DATABASE-数据库, CLOUD-云计算, AI-人工智能, OTHER-其他',
    `description` TEXT NULL COMMENT '课程简介',
    `cover_image` VARCHAR(255) NULL COMMENT '封面图URL',
    `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度: 1-入门, 2-初级, 3-中级, 4-高级',
    `duration_hours` INT NOT NULL DEFAULT 0 COMMENT '课时数（小时）',
    `prerequisites` VARCHAR(500) NULL COMMENT '先修要求（课程编码，逗号分隔）',
    `tags` VARCHAR(500) NULL COMMENT '标签（逗号分隔）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除: 0-正常, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category` (`category`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ============================================================================
-- 3. 班次表 (class_session)
-- ============================================================================
CREATE TABLE `class_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '所属课程ID',
    `instructor_id` BIGINT NOT NULL COMMENT '授课讲师ID',
    `session_code` VARCHAR(50) NOT NULL COMMENT '班期编码（如: JAVA-001-2024S1）',
    `start_date` DATE NOT NULL COMMENT '开班日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `schedule` VARCHAR(200) NULL COMMENT '上课时间描述（如: 每周六 9:00-12:00）',
    `location` VARCHAR(200) NULL COMMENT '上课地点/线上链接',
    `max_capacity` INT NOT NULL DEFAULT 30 COMMENT '最大名额',
    `current_enrollment` INT NOT NULL DEFAULT 0 COMMENT '当前报名人数',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-未开放, 1-报名中, 2-已满员, 3-进行中, 4-已结束',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除: 0-正常, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_code` (`session_code`),
    KEY `idx_course_id` (`course_id`),
    KEY `idx_instructor_id` (`instructor_id`),
    KEY `idx_status` (`status`),
    KEY `idx_start_date` (`start_date`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_session_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_session_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班次表';

-- ============================================================================
-- 4. 报名记录表 (enrollment)
-- ============================================================================
CREATE TABLE `enrollment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '班期ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-已报名, 1-已取消',
    `enrolled_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `canceled_at` DATETIME NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(500) NULL COMMENT '取消原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_session` (`user_id`, `session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_status` (`status`),
    KEY `idx_enrolled_at` (`enrolled_at`),
    CONSTRAINT `fk_enrollment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_enrollment_session` FOREIGN KEY (`session_id`) REFERENCES `class_session` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名记录表';
