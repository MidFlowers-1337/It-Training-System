# IT 技能培训智能选课系统 - 后端服务

> Spring Boot 3 + MyBatis-Plus + Spring Security + JWT + Spring AI

---

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，配置以下必需变量
```

**必需环境变量：**

| 变量名 | 说明 | 示例 |
|--------|------|------|
| `DB_URL` | 数据库连接 URL | `jdbc:mysql://localhost:3306/it_training` |
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `your_password` |
| `JWT_SECRET` | JWT 签名密钥（至少256位） | `your-super-secret-key...` |

**可选环境变量：**

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `CORS_ALLOWED_ORIGINS` | 允许的跨域来源 | `http://localhost:5173,http://localhost:3000` |
| `SPRING_AI_OPENAI_API_KEY` | OpenAI API Key | - |
| `SPRING_AI_OPENAI_BASE_URL` | AI API 地址 | `https://api.openai.com` |

### 启动服务

**方式一：使用 Maven**

```bash
# Windows CMD
set DB_URL=jdbc:mysql://localhost:3306/it_training
set DB_USERNAME=root
set DB_PASSWORD=your_password
set JWT_SECRET=your-super-secret-key-at-least-256-bits-long
mvn spring-boot:run

# Windows PowerShell
$env:DB_URL="jdbc:mysql://localhost:3306/it_training"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your-super-secret-key-at-least-256-bits-long"
mvn spring-boot:run

# Linux/Mac
export DB_URL=jdbc:mysql://localhost:3306/it_training
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-super-secret-key-at-least-256-bits-long
mvn spring-boot:run
```

**方式二：使用 JAR 包**

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/it-training-system-1.0.0-SNAPSHOT.jar
```

### 验证启动

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs

---

## 项目结构

```
src/main/java/com/itts/
├── ItTrainingApplication.java          # 启动类
│
├── common/                              # 公共基础设施
│   ├── ai/                              #   AI 客户端封装
│   │   ├── OpenAiService.java
│   │   └── impl/OpenAiServiceImpl.java
│   ├── config/                          #   配置类
│   │   ├── AiClientConfig.java          #     AI 客户端配置
│   │   ├── CorsConfig.java              #     CORS 跨域配置
│   │   ├── MyBatisPlusConfig.java       #     MyBatis-Plus 配置
│   │   ├── RedisCacheConfig.java        #     Redis 缓存配置
│   │   ├── SecurityConfig.java          #     Spring Security 配置
│   │   ├── SwaggerConfig.java           #     API 文档配置
│   │   └── WebMvcConfig.java            #     Web MVC 配置
│   ├── exception/                       #   异常处理
│   │   ├── BusinessException.java       #     业务异常
│   │   ├── ErrorCode.java               #     错误码枚举
│   │   └── GlobalExceptionHandler.java  #     全局异常处理器
│   ├── interceptor/                     #   拦截器
│   │   └── RateLimitInterceptor.java    #     速率限制
│   ├── response/                        #   统一响应
│   │   └── R.java                       #     通用响应体
│   ├── security/                        #   安全认证
│   │   ├── CustomAccessDeniedHandler.java     # 403 处理器
│   │   ├── JwtAuthenticationEntryPoint.java   # 401 处理器
│   │   ├── JwtAuthenticationFilter.java       # JWT 过滤器
│   │   ├── JwtTokenProvider.java              # Token 生成/解析
│   │   └── UserDetailsServiceImpl.java        # 用户详情加载
│   └── util/                            #   工具类
│       ├── LevelDifficultyUtils.java    #     等级难度转换
│       ├── SecurityUtils.java           #     安全上下文工具
│       ├── TimeFormatUtils.java         #     时间格式化
│       ├── UserCourseQueryHelper.java   #     用户课程查询辅助
│       └── VerificationCodeUtil.java    #     验证码工具
│
├── enums/                               # 枚举定义
│   ├── CourseCategory.java              #   课程分类
│   ├── CourseDifficulty.java            #   课程难度
│   ├── CourseStatus.java                #   课程状态
│   ├── DeleteFlag.java                  #   删除标记
│   ├── EnrollmentStatus.java            #   报名状态
│   ├── LearningStatus.java              #   学习状态
│   ├── PlanStatus.java                  #   计划状态
│   ├── RoleEnum.java                    #   角色枚举
│   ├── SessionStatus.java              #   班期状态
│   └── UserStatus.java                  #   用户状态
│
└── modules/                             # 业务模块（16 个）
    ├── achievement/                     #   成就系统
    │   ├── controller/                  #     AchievementController + AdminController
    │   ├── dto/                         #     4 个 DTO
    │   ├── entity/                      #     Achievement + UserAchievement
    │   ├── event/                       #     LearningActivityEvent
    │   ├── mapper/
    │   └── service/
    ├── ai/                              #   AI 推荐
    │   ├── controller/                  #     AiRecommendController + AiTestController
    │   ├── dto/
    │   ├── entity/
    │   ├── mapper/
    │   └── service/
    ├── auth/                            #   认证授权
    │   ├── controller/AuthController.java
    │   ├── dto/                         #     LoginRequest, RegisterRequest,
    │   │                                #     RefreshTokenRequest, TokenResponse
    │   └── service/
    ├── checkin/                          #   学习签到
    │   ├── controller/CheckinController.java
    │   ├── dto/
    │   ├── entity/StudyCheckin.java
    │   ├── mapper/
    │   └── service/
    ├── course/                          #   课程管理
    │   ├── controller/CourseController.java
    │   ├── dto/                         #     Course + Chapter CRUD DTO
    │   ├── entity/                      #     Course + CourseChapter
    │   ├── mapper/
    │   └── service/
    ├── enrollment/                      #   报名管理
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── mapper/
    │   └── service/
    ├── learning/                        #   学习进度（核心）
    │   ├── controller/ProgressController.java
    │   ├── dto/
    │   ├── entity/                      #     LearningProgress + UserLearningStats
    │   ├── mapper/
    │   └── service/
    ├── notification/                    #   通知系统
    │   ├── controller/NotificationController.java
    │   ├── dto/NotificationResponse.java
    │   ├── entity/Notification.java
    │   ├── mapper/
    │   └── service/                     #     NotificationService + SystemNotificationService
    ├── plan/                            #   学习计划
    │   ├── controller/LearningPlanController.java
    │   ├── dto/
    │   ├── entity/LearningPlan.java
    │   ├── mapper/
    │   └── service/
    ├── profile/                         #   学习画像
    │   ├── controller/LearningProfileController.java
    │   ├── dto/
    │   └── service/                     #     复用 recommend 模块实体
    ├── recommend/                       #   智能推荐
    │   ├── controller/RecommendController.java
    │   ├── dto/
    │   ├── entity/                      #     CourseSimilarity, RecommendFeedback,
    │   │                                #     UserPreference, UserSkillTag
    │   ├── mapper/
    │   └── service/                     #     Content + Collaborative + Hybrid
    ├── report/                          #   学习报告
    │   ├── controller/LearningReportController.java
    │   ├── dto/LearningReportResponse.java
    │   └── service/
    ├── review/                          #   课程评价
    │   ├── controller/CourseReviewController.java
    │   ├── dto/                         #     Request + Response + Summary
    │   ├── entity/CourseReview.java
    │   ├── mapper/
    │   └── service/
    ├── session/                         #   班期管理
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/ClassSession.java
    │   ├── mapper/
    │   └── service/
    ├── stats/                           #   统计分析
    │   ├── controller/                  #     StatsController + PublicStatsController
    │   │                                #     + PublicCourseController
    │   ├── dto/                         #     7 个统计 DTO
    │   └── service/
    ├── student/                         #   学员中心
    │   ├── controller/StudentController.java
    │   ├── dto/
    │   ├── entity/                      #     UserChapterProgress, UserLearningStreak,
    │   │                                #     UserLevel
    │   ├── mapper/
    │   └── service/                     #     StudentService + StudentDashboardService
    └── user/                            #   用户管理
        ├── controller/                  #     UserController + ProfileController
        ├── dto/                         #     7 个用户 DTO
        ├── entity/SysUser.java
        ├── mapper/
        └── service/                     #     UserService + ProfileService
```

---

## API 接口概览

> 完整 API 文档请参见 [docs/API-Frontend-Integration.md](../docs/API-Frontend-Integration.md)

### 公开接口（无需认证）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/auth/login` | 用户登录 |
| POST | `/api/v1/auth/register` | 用户注册 |
| POST | `/api/v1/auth/refresh` | 刷新 Token |
| GET | `/api/v1/public/stats` | 平台公开统计 |
| GET | `/api/v1/public/courses/featured` | 精选课程 |

### 认证接口

| 模块 | Base Path | 说明 |
|------|-----------|------|
| 学习进度 | `/api/v1/learning/progress` | 进度查询与更新 |
| 学习打卡 | `/api/v1/learning/checkin` | 打卡签到 |
| 成就系统 | `/api/v1/learning/achievements` | 成就查询 |
| 学习计划 | `/api/v1/learning/plans` | 计划 CRUD |
| 智能推荐 | `/api/v1/learning/recommend` | 多策略推荐 |
| 学习报告 | `/api/v1/learning/reports` | 周/月/年报 |
| 用户画像 | `/api/v1/learning/profile` | 画像与偏好 |
| 个人中心 | `/api/v1/profile` | 资料与安全 |
| 课程评价 | `/api/v1/courses/{id}/reviews` | 评分评论 |
| 通知中心 | `/api/v1/notifications` | 消息通知 |
| 学员 Dashboard | `/api/v1/student` | 数据聚合 |

### 管理员接口

| 模块 | Base Path | 说明 |
|------|-----------|------|
| 用户管理 | `/api/v1/users` | 用户 CRUD |
| 课程管理 | `/api/v1/courses` | 课程 CRUD + 章节 |
| 班期管理 | `/api/v1/sessions` | 班期 CRUD |
| 报名管理 | `/api/v1/enrollments` | 报名查询 |
| 统计看板 | `/api/v1/stats` | 数据统计 |
| 成就管理 | `/api/v1/admin/achievements` | 成就 CRUD |
| AI 测试 | `/api/v1/ai/test` | AI 服务调试 |

---

## 安全配置

### JWT 认证

- **Access Token** 有效期：24 小时
- **Refresh Token** 有效期：7 天
- 签名算法：HMAC-SHA256
- Token 位置：Authorization Header (`Bearer <token>`)

### 安全端点配置

```java
// 公开接口（无需认证）
/api/v1/auth/**          - 认证相关
/api/v1/public/**        - 公开数据
/swagger-ui/**           - API 文档
/v3/api-docs/**          - OpenAPI 规范

// 管理员专属
/api/v1/users/**         - 用户管理
/api/v1/admin/**         - 管理后台
/api/v1/stats/**         - 统计看板

// 需要认证
其他所有接口
```

---

## 数据库

### 数据库迁移

项目使用 **Flyway** 管理数据库版本，启动时自动执行迁移脚本。

迁移脚本位置：`src/main/resources/db/migration/`

| 脚本 | 说明 |
|------|------|
| V1 ~ V5 | 核心表结构（用户、课程、班期、报名、学习相关） |
| V6 | 种子数据 |
| V12 | 用户成就唯一约束 |
| V13 | 通知表 |
| V14 | 课程评价表 |
| V15 | 推荐反馈表 |
| V16 | 打卡唯一约束 |

### 核心表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `course` | 课程表 |
| `course_chapter` | 课程章节表 |
| `class_session` | 班期表 |
| `enrollment` | 报名表 |
| `ai_recommend_log` | AI 推荐日志 |

### 学习管理表

| 表名 | 说明 |
|------|------|
| `learning_progress` | 学习进度 |
| `learning_plan` | 学习计划 |
| `study_checkin` | 打卡记录 |
| `achievement` | 成就定义 |
| `user_achievement` | 用户成就 |
| `user_learning_stats` | 学习统计 |
| `user_preference` | 用户偏好 |
| `user_skill_tag` | 用户技能标签 |
| `course_similarity` | 课程相似度 |
| `notification` | 通知记录 |
| `course_review` | 课程评价 |
| `recommend_feedback` | 推荐反馈 |

---

## 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=AuthServiceTest

# 生成测试覆盖率报告
mvn jacoco:report
# 报告位置：target/site/jacoco/index.html
```

### 测试覆盖

| 模块 | 测试类 |
|------|--------|
| 认证 | AuthServiceTest |
| 学习进度 | LearningProgressServiceTest |
| 打卡签到 | CheckinServiceTest |
| 通知 | NotificationServiceImplTest |
| 学员 | StudentServiceImplTest |

---

## Docker

### 构建镜像

```bash
docker build -t it-training-backend:latest .
```

### 运行容器

```bash
docker run -d \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/it_training \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  -e JWT_SECRET=your-secret-key \
  --name it-training-backend \
  it-training-backend:latest
```

---

## 日志

日志文件位置：`logs/`

| 文件 | 说明 |
|------|------|
| it-training-system.log | 主日志 |
| it-training-system-error.log | 错误日志 |
| it-training-system-security.log | 安全日志 |
| it-training-system-ai.log | AI 模块日志 |

---

## 常见问题

### 1. Maven 构建报错：multiple main class

```bash
mvn clean spring-boot:run
```

### 2. 数据库连接失败

检查：
1. MySQL 服务是否启动
2. 数据库 `it_training` 是否创建
3. 环境变量是否正确配置

### 3. JWT 认证失败

检查：
1. `JWT_SECRET` 环境变量是否配置
2. Token 是否过期（Access Token 24h，Refresh Token 7d）
3. Authorization Header 格式：`Bearer <token>`

---

## 许可证

MIT License
