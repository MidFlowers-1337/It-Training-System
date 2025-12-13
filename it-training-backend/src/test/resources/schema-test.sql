-- 测试数据库 Schema (H2 兼容)

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT',
    status TINYINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT,
    cover_image VARCHAR(255),
    difficulty TINYINT NOT NULL DEFAULT 1,
    duration_hours INT NOT NULL DEFAULT 0,
    prerequisites VARCHAR(500),
    tags VARCHAR(200),
    status TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

-- 班期表
CREATE TABLE IF NOT EXISTS class_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    instructor_id BIGINT NOT NULL,
    session_code VARCHAR(30) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    schedule VARCHAR(200),
    location VARCHAR(200),
    max_capacity INT NOT NULL DEFAULT 30,
    current_enrollment INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0
);

-- 报名记录表
CREATE TABLE IF NOT EXISTS enrollment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id BIGINT NOT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    canceled_at TIMESTAMP,
    cancel_reason VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI推荐日志表
CREATE TABLE IF NOT EXISTS ai_recommend_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    input_text TEXT NOT NULL,
    recommended_courses TEXT NOT NULL,
    reason TEXT NOT NULL,
    model_used VARCHAR(50) NOT NULL,
    response_time_ms INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 初始测试数据
-- 管理员账号（密码: 123456）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 1);

-- 讲师账号
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张老师', '13800138001', 'INSTRUCTOR', 1);

-- 学员账号
INSERT INTO sys_user (username, password, real_name, phone, role, status) VALUES
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王小明', '13900139001', 'STUDENT', 1);

-- 示例课程
INSERT INTO course (code, name, category, description, difficulty, duration_hours, tags, status) VALUES
('JAVA-001', 'Java 基础入门', 'BACKEND', 'Java 语言基础课程', 1, 40, 'Java,入门', 1),
('VUE-001', 'Vue 3 前端开发', 'FRONTEND', 'Vue 3 框架开发', 2, 50, 'Vue,前端', 1);

-- 示例班期
INSERT INTO class_session (course_id, instructor_id, session_code, start_date, end_date, schedule, max_capacity, current_enrollment, status) VALUES
(1, 2, 'JAVA-001-2025S1', '2025-01-15', '2025-03-15', '每周六 9:00-12:00', 30, 0, 1);
