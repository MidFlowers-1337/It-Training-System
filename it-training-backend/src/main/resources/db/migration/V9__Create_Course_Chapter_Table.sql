-- 课程章节表
CREATE TABLE IF NOT EXISTS course_chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    title VARCHAR(200) NOT NULL COMMENT '章节标题',
    description TEXT COMMENT '章节描述',
    video_url VARCHAR(500) COMMENT '视频URL',
    duration INT DEFAULT 0 COMMENT '视频时长（秒）',
    sort_order INT DEFAULT 0 COMMENT '章节顺序',
    is_free TINYINT DEFAULT 0 COMMENT '是否免费试看: 0-否, 1-是',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-未发布, 1-已发布',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '软删除: 0-正常, 1-已删除',
    INDEX idx_course_id (course_id),
    INDEX idx_sort_order (sort_order),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节表';

-- 插入示例数据
INSERT INTO course_chapter (course_id, title, description, video_url, duration, sort_order, is_free, status) VALUES
(1, '第1章：Java基础入门', 'Java语言基础知识介绍', 'https://vjs.zencdn.net/v/oceans.mp4', 600, 1, 1, 1),
(1, '第2章：面向对象编程', '类、对象、继承、多态等概念', 'https://vjs.zencdn.net/v/oceans.mp4', 1200, 2, 0, 1),
(1, '第3章：Java集合框架', 'List、Set、Map等集合的使用', 'https://vjs.zencdn.net/v/oceans.mp4', 1800, 3, 0, 1),
(1, '第4章：异常处理', 'try-catch、自定义异常', 'https://vjs.zencdn.net/v/oceans.mp4', 900, 4, 0, 1),
(1, '第5章：IO流操作', '文件读写、序列化', 'https://vjs.zencdn.net/v/oceans.mp4', 1500, 5, 0, 1),

(2, '第1章：Spring Boot简介', 'Spring Boot框架介绍', 'https://vjs.zencdn.net/v/oceans.mp4', 800, 1, 1, 1),
(2, '第2章：依赖注入与IoC', 'Spring核心概念', 'https://vjs.zencdn.net/v/oceans.mp4', 1400, 2, 0, 1),
(2, '第3章：RESTful API开发', '构建REST接口', 'https://vjs.zencdn.net/v/oceans.mp4', 2000, 3, 0, 1),
(2, '第4章：数据库集成', 'MyBatis Plus使用', 'https://vjs.zencdn.net/v/oceans.mp4', 1600, 4, 0, 1),
(2, '第5章：项目实战', '完整项目开发', 'https://vjs.zencdn.net/v/oceans.mp4', 2400, 5, 0, 1);
