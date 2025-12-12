# IT技能培训智能选课系统 - 实施指南

## 一、数据库设计（续）

### 1.1 证书表（续）
```sql
-- 注意：TiDB完全兼容MySQL语法，所有表结构无需修改
CREATE TABLE `certificate` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '证书ID',
  `certificate_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '证书编号',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `certificate_type` TINYINT COMMENT '证书类型：1-课程完成，2-技能认证，3-项目认证',
  `certificate_name` VARCHAR(100) NOT NULL COMMENT '证书名称',
  `certificate_url` VARCHAR(255) COMMENT '证书文件URL',
  `issue_date` DATE COMMENT '颁发日期',
  `expiry_date` DATE COMMENT '有效期至',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已撤销，1-有效',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (`student_id`),
  INDEX idx_course_id (`course_id`),
  INDEX idx_certificate_no (`certificate_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书表';
```

### 1.2 AI推荐相关表

**16. 学员技能表（student_skill）**
```sql
CREATE TABLE `student_skill` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '技能ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `skill_name` VARCHAR(50) NOT NULL COMMENT '技能名称',
  `skill_level` TINYINT COMMENT '技能等级：1-入门，2-初级，3-中级，4-高级，5-专家',
  `skill_score` DECIMAL(5,2) COMMENT '技能分数',
  `acquire_time` DATETIME COMMENT '获得时间',
  `last_practice_time` DATETIME COMMENT '最后练习时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_student_skill (`student_id`, `skill_name`),
  INDEX idx_student_id (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员技能表';
```

**17. 学习路径表（learning_path）**
```sql
CREATE TABLE `learning_path` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路径ID',
  `path_name` VARCHAR(100) NOT NULL COMMENT '路径名称',
  `path_type` TINYINT COMMENT '路径类型：1-前端开发，2-后端开发，3-全栈开发，4-数据分析等',
  `target_position` VARCHAR(50) COMMENT '目标职位',
  `difficulty_level` TINYINT COMMENT '难度等级',
  `description` TEXT COMMENT '路径描述',
  `course_sequence` JSON COMMENT '课程序列（JSON格式）',
  `estimated_duration` INT COMMENT '预计学习时长（天）',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习路径表';
```

**18. 学员学习路径表（student_learning_path）**
```sql
CREATE TABLE `student_learning_path` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `path_id` BIGINT NOT NULL COMMENT '路径ID',
  `current_stage` INT DEFAULT 1 COMMENT '当前阶段',
  `progress` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成进度',
  `start_time` DATETIME COMMENT '开始时间',
  `expected_complete_time` DATETIME COMMENT '预计完成时间',
  `actual_complete_time` DATETIME COMMENT '实际完成时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已放弃，1-进行中，2-已完成',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (`student_id`),
  INDEX idx_path_id (`path_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员学习路径表';
```

**19. AI推荐记录表（ai_recommendation）**
```sql
CREATE TABLE `ai_recommendation` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '推荐ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `recommendation_type` TINYINT COMMENT '推荐类型：1-基于内容，2-协同过滤，3-学习路径，4-AI智能',
  `recommendation_score` DECIMAL(5,2) COMMENT '推荐分数',
  `recommendation_reason` TEXT COMMENT '推荐理由',
  `is_accepted` TINYINT DEFAULT 0 COMMENT '是否接受：0-未操作，1-已接受，2-已拒绝',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_student_id (`student_id`),
  INDEX idx_course_id (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI推荐记录表';
```

**20. AI对话记录表（ai_conversation）**
```sql
CREATE TABLE `ai_conversation` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '对话ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `session_id` VARCHAR(100) NOT NULL COMMENT '会话ID',
  `user_message` TEXT NOT NULL COMMENT '用户消息',
  `ai_response` TEXT COMMENT 'AI回复',
  `message_type` TINYINT COMMENT '消息类型：1-咨询，2-推荐，3-管理指令',
  `context_data` JSON COMMENT '上下文数据',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_session_id (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话记录表';
```

### 1.3 互动相关表

**21. 课程评价表（course_review）**
```sql
CREATE TABLE `course_review` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `rating` TINYINT NOT NULL COMMENT '评分（1-5）',
  `review_content` TEXT COMMENT '评价内容',
  `is_anonymous` TINYINT DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_course_student (`course_id`, `student_id`),
  INDEX idx_course_id (`course_id`),
  INDEX idx_student_id (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程评价表';
```

**22. 讨论区帖子表（forum_post）**
```sql
CREATE TABLE `forum_post` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID',
  `course_id` BIGINT COMMENT '课程ID',
  `user_id` BIGINT NOT NULL COMMENT '发帖人ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '内容',
  `post_type` TINYINT COMMENT '帖子类型：1-提问，2-分享，3-讨论',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `reply_count` INT DEFAULT 0 COMMENT '回复数',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_essence` TINYINT DEFAULT 0 COMMENT '是否精华：0-否，1-是',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_course_id (`course_id`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论区帖子表';
```

**23. 帖子回复表（forum_reply）**
```sql
CREATE TABLE `forum_reply` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回复ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '回复人ID',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父回复ID，0表示直接回复帖子',
  `reply_content` TEXT NOT NULL COMMENT '回复内容',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_post_id (`post_id`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_parent_id (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子回复表';
```

**24. 学习笔记表（study_note）**
```sql
CREATE TABLE `study_note` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '笔记ID',
  `student_id` BIGINT NOT NULL COMMENT '学员ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `chapter_id` BIGINT COMMENT '章节ID',
  `note_title` VARCHAR(200) COMMENT '笔记标题',
  `note_content` TEXT NOT NULL COMMENT '笔记内容',
  `video_time` INT COMMENT '视频时间点（秒）',
  `is_public` TINYINT DEFAULT 0 COMMENT '是否公开：0-私有，1-公开',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_student_id (`student_id`),
  INDEX idx_course_id (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习笔记表';
```

### 1.4 支付相关表

**25. 订单表（order_info）**
```sql
CREATE TABLE `order_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `course_id` BIGINT NOT NULL COMMENT '课程ID',
  `order_amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  `payment_method` TINYINT COMMENT '支付方式：1-支付宝，2-微信，3-余额',
  `payment_status` TINYINT DEFAULT 0 COMMENT '支付状态：0-未支付，1-已支付，2-已退款',
  `trade_no` VARCHAR(100) COMMENT '第三方交易号',
  `payment_time` DATETIME COMMENT '支付时间',
  `refund_time` DATETIME COMMENT '退款时间',
  `order_status` TINYINT DEFAULT 0 COMMENT '订单状态：0-待支付，1-已完成，2-已取消，3-已退款',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_no (`order_no`),
  INDEX idx_user_id (`user_id`),
  INDEX idx_payment_status (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

### 1.5 系统管理表

**26. 系统公告表（system_notice）**
```sql
CREATE TABLE `system_notice` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` VARCHAR(200) NOT NULL COMMENT '公告标题',
  `notice_content` TEXT NOT NULL COMMENT '公告内容',
  `notice_type` TINYINT COMMENT '公告类型：1-系统通知，2-活动公告，3-维护通知',
  `priority` TINYINT DEFAULT 0 COMMENT '优先级：0-普通，1-重要，2-紧急',
  `target_role` VARCHAR(50) COMMENT '目标角色（逗号分隔）',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `publish_time` DATETIME COMMENT '发布时间',
  `expire_time` DATETIME COMMENT '过期时间',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-草稿，1-已发布，2-已过期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_status (`status`),
  INDEX idx_publish_time (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';
```

**27. 操作日志表（operation_log）**
```sql
CREATE TABLE `operation_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT COMMENT '操作人ID',
  `username` VARCHAR(50) COMMENT '操作人用户名',
  `operation_type` VARCHAR(50) COMMENT '操作类型',
  `operation_desc` VARCHAR(500) COMMENT '操作描述',
  `request_method` VARCHAR(10) COMMENT '请求方法',
  `request_url` VARCHAR(500) COMMENT '请求URL',
  `request_params` TEXT COMMENT '请求参数',
  `response_result` TEXT COMMENT '响应结果',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `user_agent` VARCHAR(500) COMMENT '用户代理',
  `execute_time` INT COMMENT '执行时长（毫秒）',
  `status` TINYINT COMMENT '状态：0-失败，1-成功',
  `error_message` TEXT COMMENT '错误信息',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_user_id (`user_id`),
  INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
```

---

## 二、项目目录结构

### 2.1 后端项目结构（it-training-backend）

```
it-training-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ittraining/
│   │   │           ├── ItTrainingApplication.java          # 启动类
│   │   │           ├── common/                             # 公共模块
│   │   │           │   ├── constant/                       # 常量定义
│   │   │           │   │   ├── RedisKeyConstant.java
│   │   │           │   │   ├── RoleConstant.java
│   │   │           │   │   └── StatusConstant.java
│   │   │           │   ├── enums/                          # 枚举类
│   │   │           │   │   ├── CourseStatusEnum.java
│   │   │           │   │   ├── UserRoleEnum.java
│   │   │           │   │   └── PaymentStatusEnum.java
│   │   │           │   ├── exception/                      # 异常处理
│   │   │           │   │   ├── BusinessException.java
│   │   │           │   │   ├── GlobalExceptionHandler.java
│   │   │           │   │   └── ErrorCode.java
│   │   │           │   ├── response/                       # 统一响应
│   │   │           │   │   ├── Result.java
│   │   │           │   │   └── PageResult.java
│   │   │           │   └── utils/                          # 工具类
│   │   │           │       ├── JwtUtil.java
│   │   │           │       ├── RedisUtil.java
│   │   │           │       ├── PasswordUtil.java
│   │   │           │       └── FileUtil.java
│   │   │           ├── config/                             # 配置类
│   │   │           │   ├── SecurityConfig.java             # 安全配置
│   │   │           │   ├── RedisConfig.java                # Redis配置
│   │   │           │   ├── RocketMQConfig.java             # RocketMQ配置
│   │   │           │   ├── RedissonConfig.java             # Redisson配置
│   │   │           │   ├── CorsConfig.java                 # 跨域配置
│   │   │           │   ├── SwaggerConfig.java              # Swagger配置
│   │   │           │   ├── OllamaConfig.java               # Ollama配置
│   │   │           │   └── WebMvcConfig.java               # MVC配置
│   │   │           ├── controller/                         # 控制器
│   │   │           │   ├── admin/                          # 管理端
│   │   │           │   │   ├── AdminUserController.java
│   │   │           │   │   ├── AdminCourseController.java
│   │   │           │   │   ├── AdminStatisticsController.java
│   │   │           │   │   └── AdminSystemController.java
│   │   │           │   ├── teacher/                        # 教师端
│   │   │           │   │   ├── TeacherCourseController.java
│   │   │           │   │   ├── TeacherExamController.java
│   │   │           │   │   └── TeacherStatisticsController.java
│   │   │           │   └── student/                        # 学生端
│   │   │           │       ├── AuthController.java         # 认证
│   │   │           │       ├── CourseController.java       # 课程
│   │   │           │       ├── EnrollmentController.java   # 选课
│   │   │           │       ├── StudyController.java        # 学习
│   │   │           │       ├── ExamController.java         # 考试
│   │   │           │       ├── ForumController.java        # 论坛
│   │   │           │       ├── AiController.java           # AI推荐
│   │   │           │       └── PaymentController.java      # 支付
│   │   │           ├── service/                            # 服务层
│   │   │           │   ├── IUserService.java
│   │   │           │   ├── ICourseService.java
│   │   │           │   ├── IEnrollmentService.java
│   │   │           │   ├── IStudyService.java
│   │   │           │   ├── IExamService.java
│   │   │           │   ├── IAiRecommendService.java
│   │   │           │   ├── IPaymentService.java
│   │   │           │   ├── IForumService.java
│   │   │           │   └── impl/                           # 实现类
│   │   │           │       ├── UserServiceImpl.java
│   │   │           │       ├── CourseServiceImpl.java
│   │   │           │       ├── EnrollmentServiceImpl.java
│   │   │           │       ├── StudyServiceImpl.java
│   │   │           │       ├── ExamServiceImpl.java
│   │   │           │       ├── AiRecommendServiceImpl.java
│   │   │           │       ├── PaymentServiceImpl.java
│   │   │           │       └── ForumServiceImpl.java
│   │   │           ├── mapper/                             # 数据访问层
│   │   │           │   ├── UserMapper.java
│   │   │           │   ├── CourseMapper.java
│   │   │           │   ├── EnrollmentMapper.java
│   │   │           │   ├── StudyProgressMapper.java
│   │   │           │   ├── ExamMapper.java
│   │   │           │   ├── QuestionMapper.java
│   │   │           │   ├── ForumPostMapper.java
│   │   │           │   └── OrderMapper.java
│   │   │           ├── entity/                             # 实体类
│   │   │           │   ├── User.java
│   │   │           │   ├── Role.java
│   │   │           │   ├── Course.java
│   │   │           │   ├── CourseCategory.java
│   │   │           │   ├── CourseChapter.java
│   │   │           │   ├── Enrollment.java
│   │   │           │   ├── StudyProgress.java
│   │   │           │   ├── Question.java
│   │   │           │   ├── Exam.java
│   │   │           │   ├── ExamRecord.java
│   │   │           │   ├── Certificate.java
│   │   │           │   ├── ForumPost.java
│   │   │           │   ├── ForumReply.java
│   │   │           │   └── OrderInfo.java
│   │   │           ├── dto/                                # 数据传输对象
│   │   │           │   ├── request/
│   │   │           │   │   ├── LoginRequest.java
│   │   │           │   │   ├── RegisterRequest.java
│   │   │           │   │   ├── CourseQueryRequest.java
│   │   │           │   │   ├── EnrollRequest.java
│   │   │           │   │   └── ExamSubmitRequest.java
│   │   │           │   └── response/
│   │   │           │       ├── UserInfoResponse.java
│   │   │           │       ├── CourseDetailResponse.java
│   │   │           │       ├── EnrollmentResponse.java
│   │   │           │       └── ExamResultResponse.java
│   │   │           ├── vo/                                 # 视图对象
│   │   │           │   ├── CourseVO.java
│   │   │           │   ├── EnrollmentVO.java
│   │   │           │   ├── ExamVO.java
│   │   │           │   └── StatisticsVO.java
│   │   │           ├── mq/                                 # 消息队列
│   │   │           │   ├── producer/
│   │   │           │   │   └── EnrollmentProducer.java
│   │   │           │   └── consumer/
│   │   │           │       └── EnrollmentConsumer.java
│   │   │           ├── task/                               # 定时任务
│   │   │           │   ├── CourseStatusTask.java
│   │   │           │   └── DataStatisticsTask.java
│   │   │           ├── interceptor/                        # 拦截器
│   │   │           │   ├── AuthInterceptor.java
│   │   │           │   └── LogInterceptor.java
│   │   │           ├── aspect/                             # 切面
│   │   │           │   ├── LogAspect.java
│   │   │           │   └── RateLimitAspect.java
│   │   │           └── ai/                                 # AI模块
│   │   │               ├── service/
│   │   │               │   ├── AiChatService.java
│   │   │               │   └── AiRecommendService.java
│   │   │               ├── prompt/
│   │   │               │   └── PromptTemplate.java
│   │   │               └── strategy/
│   │   │                   ├── ContentBasedStrategy.java
│   │   │                   ├── CollaborativeStrategy.java
│   │   │                   └── PathBasedStrategy.java
│   │   └── resources/
│   │       ├── application.yml                             # 主配置文件
│   │       ├── application-dev.yml                         # 开发环境
│   │       ├── application-prod.yml                        # 生产环境
│   │       ├── mapper/                                     # MyBatis映射文件
│   │       │   ├── UserMapper.xml
│   │       │   ├── CourseMapper.xml
│   │       │   ├── EnrollmentMapper.xml
│   │       │   └── ...
│   │       ├── db/
│   │       │   └── migration/                              # 数据库迁移脚本
│   │       │       ├── V1__init_user_tables.sql
│   │       │       ├── V2__init_course_tables.sql
│   │       │       ├── V3__init_enrollment_tables.sql
│   │       │       └── ...
│   │       ├── static/                                     # 静态资源
│   │       └── templates/                                  # 模板文件
│   └── test/                                               # 测试代码
│       └── java/
│           └── com/
│               └── ittraining/
│                   ├── service/
│                   ├── controller/
│                   └── mapper/
├── docker/                                                 # Docker配置
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── nginx/
│       └── nginx.conf
├── docs/                                                   # 文档
│   ├── API文档.md
│   ├── 数据库设计.md
│   └── 部署文档.md
├── pom.xml                                                 # Maven配置
└── README.md                                               # 项目说明
```

### 2.2 前端项目结构（it-training-frontend）

```
it-training-frontend/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── main.js                                             # 入口文件
│   ├── App.vue                                             # 根组件
│   ├── assets/                                             # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   │   ├── common.scss
│   │   │   ├── variables.scss
│   │   │   └── reset.scss
│   │   └── icons/
│   ├── components/                                         # 公共组件
│   │   ├── common/
│   │   │   ├── Header.vue
│   │   │   ├── Footer.vue
│   │   │   ├── Sidebar.vue
│   │   │   ├── Breadcrumb.vue
│   │   │   └── Pagination.vue
│   │   ├── course/
│   │   │   ├── CourseCard.vue
│   │   │   ├── CourseList.vue
│   │   │   ├── CourseFilter.vue
│   │   │   └── VideoPlayer.vue
│   │   ├── exam/
│   │   │   ├── QuestionItem.vue
│   │   │   ├── ExamTimer.vue
│   │   │   └── ExamResult.vue
│   │   └── ai/
│   │       ├── AiChatBox.vue
│   │       └── RecommendCard.