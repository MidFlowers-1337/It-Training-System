-- 用户章节进度表
CREATE TABLE IF NOT EXISTS user_chapter_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    watch_duration INT DEFAULT 0 COMMENT '观看时长（秒）',
    last_position INT DEFAULT 0 COMMENT '最后观看位置（秒）',
    completed TINYINT DEFAULT 0 COMMENT '是否完成: 0-未完成, 1-已完成',
    completed_at DATETIME COMMENT '完成时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_chapter (user_id, chapter_id),
    INDEX idx_user_id (user_id),
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_course_id (course_id),
    INDEX idx_completed (completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户章节进度表';

-- 学习打卡表
CREATE TABLE IF NOT EXISTS user_learning_streak (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    streak_days INT DEFAULT 0 COMMENT '连续学习天数',
    max_streak_days INT DEFAULT 0 COMMENT '最大连续天数',
    last_checkin_date DATE COMMENT '最后打卡日期',
    total_checkin_days INT DEFAULT 0 COMMENT '总打卡天数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习打卡表';

-- 用户等级经验表
CREATE TABLE IF NOT EXISTS user_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    level INT DEFAULT 1 COMMENT '当前等级',
    experience INT DEFAULT 0 COMMENT '当前经验值',
    total_experience INT DEFAULT 0 COMMENT '累计经验值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户等级经验表';

-- 注意：achievement 和 user_achievement 表已在 V3 中创建
-- 如需插入成就数据，请创建新的迁移脚本 V11

