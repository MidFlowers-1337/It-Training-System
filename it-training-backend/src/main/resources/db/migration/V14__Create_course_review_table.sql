-- 课程评价表
CREATE TABLE IF NOT EXISTS course_review (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    course_id   BIGINT       NOT NULL COMMENT '课程ID',
    rating      TINYINT      NOT NULL COMMENT '评分(1-5)',
    comment     TEXT                  COMMENT '评价内容',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 同一用户对同一课程只能评价一次
    CONSTRAINT uk_user_course UNIQUE (user_id, course_id),

    INDEX idx_course_id (course_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程评价表';
