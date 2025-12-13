-- ============================================================================
-- IT 技能培训智能选课系统 - 数据库初始化脚本
-- 版本: v1.0.0
-- 日期: 2025-12-14
-- 数据库: MySQL 8.0+ / TiDB
-- 字符集: utf8mb4
-- ============================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `it_training`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `it_training`;

-- ============================================================================
-- 1. 用户表 (sys_user)
-- ============================================================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `role` VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色: ADMIN-管理员, INSTRUCTOR-讲师, STUDENT-学员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除: 0-正常, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================================
-- 2. 课程表 (course)
-- ============================================================================
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `code` VARCHAR(30) NOT NULL COMMENT '课程编码（如: JAVA-001）',
    `name` VARCHAR(100) NOT NULL COMMENT '课程名称',
    `category` VARCHAR(50) NOT NULL COMMENT '分类: BACKEND-后端开发, FRONTEND-前端开发, DATABASE-数据库, CLOUD-云计算, AI-人工智能, OTHER-其他',
    `description` TEXT DEFAULT NULL COMMENT '课程简介',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
    `difficulty` TINYINT NOT NULL DEFAULT 1 COMMENT '难度: 1-入门, 2-初级, 3-中级, 4-高级',
    `duration_hours` INT NOT NULL DEFAULT 0 COMMENT '课时数（小时）',
    `prerequisites` VARCHAR(500) DEFAULT NULL COMMENT '先修要求（课程编码，逗号分隔）',
    `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签（逗号分隔，如: Java,Spring,微服务）',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-下架, 1-上架',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除: 0-正常, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_category` (`category`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ============================================================================
-- 3. 班期表 (class_session)
-- ============================================================================
DROP TABLE IF EXISTS `class_session`;
CREATE TABLE `class_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `course_id` BIGINT NOT NULL COMMENT '所属课程ID',
    `instructor_id` BIGINT NOT NULL COMMENT '授课讲师ID',
    `session_code` VARCHAR(30) NOT NULL COMMENT '班期编码（如: JAVA-001-2024S1）',
    `start_date` DATE NOT NULL COMMENT '开班日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `schedule` VARCHAR(200) DEFAULT NULL COMMENT '上课时间描述（如: 每周六 9:00-12:00）',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '上课地点/线上链接',
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
    CONSTRAINT `fk_session_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_session_instructor` FOREIGN KEY (`instructor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班期表';

-- ============================================================================
-- 4. 报名记录表 (enrollment)
-- ============================================================================
DROP TABLE IF EXISTS `enrollment`;
CREATE TABLE `enrollment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '学员ID',
    `session_id` BIGINT NOT NULL COMMENT '班期ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-已报名, 1-已取消',
    `enrolled_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `canceled_at` DATETIME DEFAULT NULL COMMENT '取消时间',
    `cancel_reason` VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_status` (`status`),
    KEY `idx_user_session` (`user_id`, `session_id`),
    CONSTRAINT `fk_enrollment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_enrollment_session` FOREIGN KEY (`session_id`) REFERENCES `class_session` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报名记录表';

-- ============================================================================
-- 5. AI推荐日志表 (ai_recommend_log)
-- ============================================================================
DROP TABLE IF EXISTS `ai_recommend_log`;
CREATE TABLE `ai_recommend_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '请求用户ID',
    `input_text` TEXT NOT NULL COMMENT '用户输入的学习目标',
    `recommended_courses` TEXT NOT NULL COMMENT '推荐结果JSON（包含课程ID列表和顺序）',
    `reason` TEXT NOT NULL COMMENT '推荐理由',
    `model_used` VARCHAR(50) NOT NULL COMMENT '使用的AI模型（或FALLBACK表示降级）',
    `response_time_ms` INT NOT NULL DEFAULT 0 COMMENT '响应耗时(毫秒)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_ai_log_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI推荐日志表';

-- ============================================================================
-- 6. 用户技能标签表 (user_skill_tag)
-- ============================================================================
DROP TABLE IF EXISTS `user_skill_tag`;
CREATE TABLE `user_skill_tag` (
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
-- 7. 用户学习偏好表 (user_preference)
-- ============================================================================
DROP TABLE IF EXISTS `user_preference`;
CREATE TABLE `user_preference` (
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
-- 8. 课程相似度表 (course_similarity)
-- ============================================================================
DROP TABLE IF EXISTS `course_similarity`;
CREATE TABLE `course_similarity` (
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
-- 9. 学习进度表 (learning_progress)
-- ============================================================================
DROP TABLE IF EXISTS `learning_progress`;
CREATE TABLE `learning_progress` (
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
-- 10. 学习计划表 (learning_plan)
-- ============================================================================
DROP TABLE IF EXISTS `learning_plan`;
CREATE TABLE `learning_plan` (
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
-- 11. 学习打卡记录表 (study_checkin)
-- ============================================================================
DROP TABLE IF EXISTS `study_checkin`;
CREATE TABLE `study_checkin` (
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
-- 12. 成就定义表 (achievement)
-- ============================================================================
DROP TABLE IF EXISTS `achievement`;
CREATE TABLE `achievement` (
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
-- 13. 用户成就表 (user_achievement)
-- ============================================================================
DROP TABLE IF EXISTS `user_achievement`;
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
    CONSTRAINT `fk_ua_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_ua_achievement` FOREIGN KEY (`achievement_id`) REFERENCES `achievement` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户成就表';

-- ============================================================================
-- 14. 用户学习统计表 (user_learning_stats)
-- ============================================================================
DROP TABLE IF EXISTS `user_learning_stats`;
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
    `last_study_date` DATE DEFAULT NULL COMMENT '最后学习日期',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    CONSTRAINT `fk_stats_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户学习统计表';

-- ============================================================================
-- 初始数据
-- ============================================================================

-- 插入管理员账号（密码: 123456，BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 1);

-- 插入测试讲师账号（密码: 123456）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张老师', '13800138001', 'INSTRUCTOR', 1),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李老师', '13800138002', 'INSTRUCTOR', 1);

-- 插入测试学员账号（密码: 123456）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '王小明', '13900139001', 'STUDENT', 1),
('student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '李小红', '13900139002', 'STUDENT', 1);

-- 插入示例课程
INSERT INTO `course` (`code`, `name`, `category`, `description`, `difficulty`, `duration_hours`, `tags`, `status`) VALUES
('JAVA-001', 'Java 基础入门', 'BACKEND', 'Java 语言基础，包括语法、面向对象、集合、IO、多线程等核心知识点。', 1, 40, 'Java,入门,编程基础', 1),
('JAVA-002', 'Spring Boot 实战', 'BACKEND', '基于 Spring Boot 3.x 的企业级应用开发，包括 Web开发、数据访问、安全认证等。', 3, 60, 'Java,Spring,Spring Boot,微服务', 1),
('VUE-001', 'Vue 3 前端开发', 'FRONTEND', 'Vue 3 组合式 API、响应式原理、组件化开发、状态管理、路由等。', 2, 50, 'Vue,前端,JavaScript,TypeScript', 1),
('DB-001', 'MySQL 数据库基础', 'DATABASE', 'MySQL 安装配置、SQL 语法、索引优化、事务、存储过程等。', 2, 30, 'MySQL,数据库,SQL', 1),
('AI-001', '人工智能入门', 'AI', 'AI 基础概念、机器学习入门、深度学习基础、常用框架介绍。', 2, 40, 'AI,机器学习,深度学习,Python', 1);

-- 插入示例班期
INSERT INTO `class_session` (`course_id`, `instructor_id`, `session_code`, `start_date`, `end_date`, `schedule`, `location`, `max_capacity`, `current_enrollment`, `status`) VALUES
(1, 2, 'JAVA-001-2025S1', '2025-01-15', '2025-03-15', '每周六 9:00-12:00', '线上直播', 30, 0, 1),
(1, 3, 'JAVA-001-2025S2', '2025-02-01', '2025-04-01', '每周日 14:00-17:00', '线上直播', 25, 0, 1),
(2, 2, 'JAVA-002-2025S1', '2025-02-15', '2025-05-15', '每周六 14:00-17:00', '线上直播', 20, 0, 1),
(3, 3, 'VUE-001-2025S1', '2025-01-20', '2025-04-20', '每周日 9:00-12:00', '线上直播', 30, 0, 1),
(4, 2, 'DB-001-2025S1', '2025-03-01', '2025-04-30', '每周六 9:00-11:00', '线上直播', 35, 0, 1);

-- 插入成就定义
INSERT INTO `achievement` (`code`, `name`, `description`, `category`, `condition_type`, `condition_value`, `points`) VALUES
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

-- 插入课程相似度数据（基于分类和标签）
INSERT INTO `course_similarity` (`course_id_a`, `course_id_b`, `similarity_score`) VALUES
(1, 2, 0.8500),  -- Java基础 与 Spring Boot 高度相关
(1, 4, 0.4000),  -- Java基础 与 MySQL 中度相关
(2, 4, 0.5000),  -- Spring Boot 与 MySQL 中度相关
(3, 1, 0.2000),  -- Vue 与 Java基础 低度相关
(3, 2, 0.3000),  -- Vue 与 Spring Boot 低度相关（前后端配合）
(5, 1, 0.3000),  -- AI 与 Java基础 低度相关
(5, 4, 0.4000);  -- AI 与 MySQL 中度相关（数据处理）

-- ============================================================================
-- 说明
-- ============================================================================
-- 1. 所有表使用 InnoDB 引擎，支持事务
-- 2. 字符集使用 utf8mb4，支持 emoji
-- 3. 主键统一使用 BIGINT 自增
-- 4. 时间字段使用 DATETIME，便于跨时区
-- 5. 软删除字段 deleted 用于逻辑删除
-- 6. 外键用于保证数据完整性，生产环境可根据需要调整
-- 7. 初始密码需要使用 BCrypt 重新生成
-- ============================================================================
