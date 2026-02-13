-- ============================================================================
-- Flyway 迁移脚本 V6: 插入初始种子数据
-- 描述: 插入系统必需的初始数据（管理员、示例用户、基础成就等）
-- 作者: IT Training System Team
-- 日期: 2025-12-19
-- ============================================================================

-- ============================================================================
-- 1. 初始用户数据
-- ============================================================================
-- ⚠️ 安全警告：以下为开发/演示环境种子数据，生产环境部署前必须：
-- 1. 删除或禁用这些默认账号
-- 2. 创建新的管理员账号并使用强密码
-- 密码已使用 BCrypt 加密存储（明文: 123456）
-- 注意: 使用 $2a$ 前缀（Java BCryptPasswordEncoder 默认版本）

-- 管理员账号
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role`, `status`, `created_at`, `updated_at`, `deleted`) VALUES
(1, 'admin', '$2a$10$I115tnj/xbtbXN9GQTyRs..NDtvu9i99vECjfrSJmjTk2SrFQOX4a', '系统管理员', '13800138000', 'admin@itts.com', 'ADMIN', 1, NOW(), NOW(), 0);

-- 示例讲师账号
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role`, `status`, `created_at`, `updated_at`, `deleted`) VALUES
(2, 'teacher1', '$2a$10$I115tnj/xbtbXN9GQTyRs..NDtvu9i99vECjfrSJmjTk2SrFQOX4a', '张老师', '13800138001', 'zhang@itts.com', 'INSTRUCTOR', 1, NOW(), NOW(), 0),
(3, 'teacher2', '$2a$10$I115tnj/xbtbXN9GQTyRs..NDtvu9i99vECjfrSJmjTk2SrFQOX4a', '李老师', '13800138002', 'li@itts.com', 'INSTRUCTOR', 1, NOW(), NOW(), 0);

-- 示例学员账号
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `role`, `status`, `created_at`, `updated_at`, `deleted`) VALUES
(4, 'student1', '$2a$10$I115tnj/xbtbXN9GQTyRs..NDtvu9i99vECjfrSJmjTk2SrFQOX4a', '王同学', '13800138003', 'wang@student.com', 'STUDENT', 1, NOW(), NOW(), 0),
(5, 'student2', '$2a$10$I115tnj/xbtbXN9GQTyRs..NDtvu9i99vECjfrSJmjTk2SrFQOX4a', '赵同学', '13800138004', 'zhao@student.com', 'STUDENT', 1, NOW(), NOW(), 0);

-- ============================================================================
-- 2. 示例课程数据
-- ============================================================================
INSERT INTO `course` (`id`, `code`, `name`, `category`, `description`, `difficulty`, `duration_hours`, `tags`, `status`, `created_at`, `updated_at`, `deleted`) VALUES
(1, 'JAVA-001', 'Java 基础编程', 'BACKEND', 'Java 语言基础，面向对象编程，集合框架等核心知识', 1, 40, 'Java,编程基础,面向对象', 1, NOW(), NOW(), 0),
(2, 'JAVA-002', 'Spring Boot 实战', 'BACKEND', '基于 Spring Boot 的企业级应用开发，包含 Spring MVC、MyBatis、RESTful API 等', 3, 60, 'Java,Spring Boot,微服务', 1, NOW(), NOW(), 0),
(3, 'WEB-001', 'HTML/CSS/JavaScript 基础', 'FRONTEND', 'Web 前端三剑客，从零开始学习网页开发', 1, 30, 'HTML,CSS,JavaScript,前端', 1, NOW(), NOW(), 0),
(4, 'VUE-001', 'Vue 3 前端开发', 'FRONTEND', 'Vue 3 组合式 API、路由、状态管理、组件化开发', 2, 50, 'Vue,前端框架,组件化', 1, NOW(), NOW(), 0),
(5, 'DB-001', 'MySQL 数据库基础', 'DATABASE', 'SQL 语法、数据库设计、索引优化、事务处理', 2, 35, 'MySQL,SQL,数据库', 1, NOW(), NOW(), 0);

-- ============================================================================
-- 3. 基础成就定义
-- ============================================================================
INSERT INTO `achievement` (`id`, `code`, `name`, `description`, `category`, `condition_type`, `condition_value`, `points`, `status`, `created_at`, `updated_at`) VALUES
-- 连续学习成就
(1, 'STREAK_3', '三天打卡', '连续学习3天', 'streak', 'streak_days', 3, 10, 1, NOW(), NOW()),
(2, 'STREAK_7', '一周坚持', '连续学习7天', 'streak', 'streak_days', 7, 30, 1, NOW(), NOW()),
(3, 'STREAK_30', '月度学霸', '连续学习30天', 'streak', 'streak_days', 30, 100, 1, NOW(), NOW()),
(4, 'STREAK_100', '百日筑基', '连续学习100天', 'streak', 'streak_days', 100, 500, 1, NOW(), NOW()),

-- 课程完成成就
(5, 'COURSE_1', '初出茅庐', '完成第1门课程', 'course', 'courses_completed', 1, 20, 1, NOW(), NOW()),
(6, 'COURSE_5', '学有所成', '完成5门课程', 'course', 'courses_completed', 5, 100, 1, NOW(), NOW()),
(7, 'COURSE_10', '博学多才', '完成10门课程', 'course', 'courses_completed', 10, 300, 1, NOW(), NOW()),

-- 学习时长成就
(8, 'HOURS_10', '十小时修炼', '累计学习10小时', 'general', 'hours_studied', 10, 15, 1, NOW(), NOW()),
(9, 'HOURS_50', '五十小时精进', '累计学习50小时', 'general', 'hours_studied', 50, 80, 1, NOW(), NOW()),
(10, 'HOURS_100', '百小时大师', '累计学习100小时', 'general', 'hours_studied', 100, 200, 1, NOW(), NOW()),
(11, 'HOURS_500', '五百小时宗师', '累计学习500小时', 'general', 'hours_studied', 500, 1000, 1, NOW(), NOW());

-- ============================================================================
-- 4. 初始化用户学习统计（为示例学员创建）
-- ============================================================================
INSERT INTO `user_learning_stats` (`user_id`, `total_study_minutes`, `total_courses_enrolled`, `total_courses_completed`, `current_streak_days`, `max_streak_days`, `total_checkin_days`, `total_achievement_points`, `created_at`, `updated_at`) VALUES
(4, 0, 0, 0, 0, 0, 0, 0, NOW(), NOW()),
(5, 0, 0, 0, 0, 0, 0, 0, NOW(), NOW());

-- ============================================================================
-- ⚠️ 重要安全说明
-- ============================================================================
-- 此脚本仅用于开发和演示环境！
-- 生产环境部署前，必须：
-- 1. 删除所有种子账号或重置密码
-- 2. 使用符合安全策略的强密码
-- 3. 审计所有用户账号
-- ============================================================================
