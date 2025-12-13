# IT 技能培训智能选课系统 - 后端服务

> Spring Boot 3 + MyBatis-Plus + Spring Security + JWT + Spring AI

---

## 🚀 快速启动

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

## 📁 项目结构

```
src/main/java/com/itts/
├── ItTrainingApplication.java    # 启动类
├── common/                       # 公共模块
│   ├── config/                   # 配置类
│   │   ├── CorsConfig.java       # CORS 配置
│   │   ├── MyBatisPlusConfig.java
│   │   ├── SecurityConfig.java   # Spring Security 配置
│   │   └── SwaggerConfig.java    # API 文档配置
│   ├── exception/                # 异常处理
│   │   ├── BusinessException.java
│   │   ├── ErrorCode.java
│   │   └── GlobalExceptionHandler.java
│   ├── response/                 # 统一响应
│   │   └── R.java
│   └── security/                 # 安全模块
│       ├── JwtAuthenticationFilter.java
│       ├── JwtTokenProvider.java
│       └── UserDetailsServiceImpl.java
├── enums/                        # 枚举定义
│   ├── CourseCategory.java
│   ├── CourseDifficulty.java
│   ├── EnrollmentStatus.java
│   ├── RoleEnum.java
│   └── SessionStatus.java
└── modules/                      # 业务模块
    ├── ai/                       # AI 推荐模块
    │   ├── controller/
    │   ├── dto/
    │   ├── entity/
    │   ├── mapper/
    │   └── service/
    ├── auth/                     # 认证模块
    ├── course/                   # 课程管理
    ├── enrollment/               # 报名管理
    ├── learning/                 # 学习管理
    │   ├── controller/
    │   ├── dto/
    │   ├── mapper/
    │   └── service/
    │       ├── AchievementService.java
    │       ├── CollaborativeFilteringService.java
    │       ├── ContentBasedRecommendService.java
    │       ├── HybridRecommendService.java
    │       ├── LearningPlanService.java
    │       ├── LearningProgressService.java
    │       ├── LearningReportService.java
    │       ├── StudyCheckinService.java
    │       ├── UserLearningStatsService.java
    │       └── UserProfileService.java
    ├── session/                  # 班期管理
    ├── stats/                    # 统计模块
    └── user/                     # 用户管理
        ├── controller/
        │   ├── ProfileController.java
        │   └── UserController.java
        ├── dto/
        ├── entity/
        ├── mapper/
        └── service/
            ├── ProfileService.java
            └── UserService.java
```

---

## 🔌 API 接口

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |
| POST | `/api/auth/logout` | 用户登出 |

### 用户管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/users` | 用户列表 | ADMIN |
| POST | `/api/users` | 创建用户 | ADMIN |
| PUT | `/api/users/{id}` | 更新用户 | ADMIN |
| DELETE | `/api/users/{id}` | 删除用户 | ADMIN |

### 课程管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/courses` | 课程列表 | ALL |
| POST | `/api/courses` | 创建课程 | ADMIN |
| PUT | `/api/courses/{id}` | 更新课程 | ADMIN |
| DELETE | `/api/courses/{id}` | 删除课程 | ADMIN |

### 报名管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/enrollments` | 报名 | STUDENT |
| GET | `/api/enrollments/my` | 我的报名 | STUDENT |
| POST | `/api/enrollments/{id}/cancel` | 取消报名 | STUDENT |

### AI 推荐

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/ai/recommend` | 智能推荐 | STUDENT |

### 学习管理

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/learning/dashboard` | 学习仪表盘 | STUDENT |
| POST | `/api/learning/progress` | 更新进度 | STUDENT |
| POST | `/api/learning/checkin` | 打卡 | STUDENT |
| GET | `/api/learning/achievements` | 成就列表 | STUDENT |
| GET | `/api/learning/report` | 学习报告 | STUDENT |
| GET | `/api/learning/recommend` | 课程推荐 | STUDENT |

### 个人中心

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/profile` | 获取个人信息 | ALL |
| PUT | `/api/profile` | 更新个人信息 | ALL |
| POST | `/api/profile/password` | 修改密码 | ALL |
| POST | `/api/profile/avatar` | 上传头像 | ALL |

---

## 🔒 安全配置

### JWT 认证

- Token 有效期：24 小时
- 签名算法：HMAC-SHA256
- Token 位置：Authorization Header (`Bearer <token>`)

### 角色权限

| 角色 | 权限范围 |
|------|----------|
| ADMIN | 全部功能 |
| INSTRUCTOR | 查看班期、学员名单 |
| STUDENT | 浏览课程、报名、AI 推荐、学习管理 |

### 安全端点配置

```java
// 公开接口
/api/auth/** - 认证相关
/swagger-ui/** - API 文档
/v3/api-docs/** - OpenAPI 规范

// 需要认证
其他所有接口
```

---

## 🧪 测试

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
| 认证 | AuthControllerTest, AuthServiceTest |
| 课程 | CourseControllerTest, CourseServiceTest |
| 报名 | EnrollmentControllerTest, EnrollmentServiceTest |
| 用户 | UserControllerTest, UserServiceTest |
| AI | AiRecommendServiceTest |
| 统计 | StatsControllerTest, StatsServiceTest |
| 班期 | SessionControllerTest, SessionServiceTest |

---

## 📊 数据库

### 核心表

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| course | 课程表 |
| class_session | 班期表 |
| enrollment | 报名表 |
| ai_recommend_log | AI 推荐日志 |

### 学习管理表

| 表名 | 说明 |
|------|------|
| learning_progress | 学习进度 |
| learning_plan | 学习计划 |
| study_checkin | 打卡记录 |
| achievement | 成就定义 |
| user_achievement | 用户成就 |
| user_learning_stats | 学习统计 |
| user_preference | 用户偏好 |
| user_skill_tag | 用户技能标签 |
| course_similarity | 课程相似度 |

---

## 🐳 Docker

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

## 📝 日志

日志文件位置：`logs/`

| 文件 | 说明 |
|------|------|
| it-training-system.log | 主日志 |
| it-training-system-error.log | 错误日志 |
| it-training-system-security.log | 安全日志 |
| it-training-system-ai.log | AI 模块日志 |

---

## 🔧 常见问题

### 1. Maven 构建报错：multiple main class

```bash
# 清理 target 目录后重新编译
mvn clean spring-boot:run
```

### 2. 数据库连接失败

检查：
1. MySQL 服务是否启动
2. 数据库是否创建
3. 环境变量是否正确配置

### 3. JWT 认证失败

检查：
1. `JWT_SECRET` 环境变量是否配置
2. Token 是否过期
3. Authorization Header 格式是否正确

---

## 📄 许可证

MIT License