# IT技能培训智能选课系统 - 系统架构图

> 最后更新: 2025-12-14

---

## 1. 整体系统架构

```mermaid
flowchart TB
    subgraph Client["客户端层"]
        Web["Web 浏览器"]
        Mobile["移动端 (未来)"]
    end

    subgraph Frontend["前端层 - Vue 3"]
        Router["Vue Router<br/>路由管理"]
        Pinia["Pinia<br/>状态管理"]
        Components["组件库<br/>Element Plus"]
        API["API 服务层<br/>Axios"]
    end

    subgraph Gateway["网关层"]
        Nginx["Nginx<br/>反向代理/静态资源"]
    end

    subgraph Backend["后端层 - Spring Boot 3"]
        Controller["Controller 层<br/>REST API"]
        Security["Spring Security<br/>JWT 认证"]
        Service["Service 层<br/>业务逻辑"]
        Mapper["Mapper 层<br/>MyBatis-Plus"]
    end

    subgraph External["外部服务"]
        AI["DeepSeek AI<br/>智能推荐"]
    end

    subgraph Storage["数据层"]
        MySQL["MySQL 8.0<br/>TiDB 兼容"]
    end

    Web --> Nginx
    Mobile --> Nginx
    Nginx --> Router
    Router --> Pinia
    Pinia --> Components
    Components --> API
    API --> Nginx
    Nginx --> Controller
    Controller --> Security
    Security --> Service
    Service --> Mapper
    Service --> AI
    Mapper --> MySQL
```

---

## 2. 后端模块架构

```mermaid
flowchart LR
    subgraph Modules["业务模块"]
        Auth["auth<br/>认证模块"]
        User["user<br/>用户模块"]
        Course["course<br/>课程模块"]
        Enrollment["enrollment<br/>报名模块"]
        AI["ai<br/>AI推荐模块"]
        Stats["stats<br/>统计模块"]
    end

    subgraph Common["公共组件"]
        Config["config<br/>全局配置"]
        Exception["exception<br/>异常处理"]
        Security["security<br/>安全组件"]
        Entity["entity<br/>基础实体"]
    end

    subgraph Infrastructure["基础设施"]
        MyBatis["MyBatis-Plus<br/>ORM框架"]
        SpringAI["Spring AI<br/>AI集成"]
        JWT["JWT<br/>令牌管理"]
    end

    Auth --> Security
    Auth --> JWT
    User --> MyBatis
    Course --> MyBatis
    Enrollment --> MyBatis
    AI --> SpringAI
    Stats --> MyBatis

    Auth --> Config
    User --> Config
    Course --> Config
    Enrollment --> Config
    AI --> Config
    Stats --> Config

    Auth --> Exception
    User --> Exception
    Course --> Exception
    Enrollment --> Exception
    AI --> Exception
    Stats --> Exception
```

---

## 3. 前端模块架构

```mermaid
flowchart TB
    subgraph Views["页面视图"]
        Login["LoginView<br/>登录页"]
        Dashboard["DashboardView<br/>仪表盘"]
        CourseList["CourseListView<br/>课程列表"]
        CourseDetail["CourseDetailView<br/>课程详情"]
        MyEnroll["MyEnrollmentsView<br/>我的报名"]
        UserMgmt["UserManagementView<br/>用户管理"]
        CourseMgmt["CourseManagementView<br/>课程管理"]
        EnrollMgmt["EnrollmentManagementView<br/>报名管理"]
        Stats["StatisticsView<br/>统计分析"]
    end

    subgraph Stores["状态管理 - Pinia"]
        AuthStore["useAuthStore<br/>认证状态"]
        CourseStore["useCourseStore<br/>课程状态"]
        EnrollStore["useEnrollmentStore<br/>报名状态"]
    end

    subgraph APIs["API 服务"]
        AuthAPI["auth.js<br/>认证接口"]
        UserAPI["user.js<br/>用户接口"]
        CourseAPI["course.js<br/>课程接口"]
        EnrollAPI["enrollment.js<br/>报名接口"]
        AIAPI["ai.js<br/>AI接口"]
        StatsAPI["stats.js<br/>统计接口"]
    end

    subgraph Utils["工具库"]
        Request["request.js<br/>Axios封装"]
        Router["router/index.js<br/>路由配置"]
    end

    Login --> AuthStore
    Dashboard --> CourseStore
    CourseList --> CourseStore
    MyEnroll --> EnrollStore

    AuthStore --> AuthAPI
    CourseStore --> CourseAPI
    EnrollStore --> EnrollAPI

    AuthAPI --> Request
    UserAPI --> Request
    CourseAPI --> Request
    EnrollAPI --> Request
    AIAPI --> Request
    StatsAPI --> Request
```

---

## 4. 数据库 ER 图

```mermaid
erDiagram
    USER ||--o{ ENROLLMENT : "报名"
    COURSE ||--o{ ENROLLMENT : "被报名"
    USER ||--o{ AI_RECOMMEND_LOG : "获取推荐"

    USER {
        bigint id PK "主键"
        varchar username UK "用户名"
        varchar password "密码(BCrypt)"
        varchar real_name "真实姓名"
        varchar email "邮箱"
        varchar phone "手机号"
        enum role "角色(ADMIN/INSTRUCTOR/STUDENT)"
        tinyint status "状态(0禁用/1启用)"
        datetime created_at "创建时间"
        datetime updated_at "更新时间"
    }

    COURSE {
        bigint id PK "主键"
        varchar name "课程名称"
        text description "课程描述"
        bigint instructor_id FK "讲师ID"
        varchar category "课程分类"
        int credit "学分"
        int max_students "最大人数"
        int current_students "当前人数"
        date start_date "开始日期"
        date end_date "结束日期"
        enum status "状态(DRAFT/PUBLISHED/CLOSED)"
        datetime created_at "创建时间"
        datetime updated_at "更新时间"
    }

    ENROLLMENT {
        bigint id PK "主键"
        bigint user_id FK "用户ID"
        bigint course_id FK "课程ID"
        enum status "状态(PENDING/APPROVED/REJECTED/CANCELLED)"
        datetime enroll_time "报名时间"
        datetime approve_time "审批时间"
        bigint approver_id FK "审批人ID"
        varchar remark "备注"
        datetime created_at "创建时间"
        datetime updated_at "更新时间"
    }

    AI_RECOMMEND_LOG {
        bigint id PK "主键"
        bigint user_id FK "用户ID"
        text request_content "请求内容"
        text response_content "响应内容"
        varchar model "使用模型"
        tinyint is_fallback "是否降级"
        datetime created_at "创建时间"
    }
```

---

## 5. 认证流程时序图

```mermaid
sequenceDiagram
    autonumber
    participant C as 客户端
    participant N as Nginx
    participant A as AuthController
    participant S as AuthService
    participant J as JwtUtils
    participant D as 数据库

    C->>N: POST /api/auth/login
    N->>A: 转发请求
    A->>S: login(username, password)
    S->>D: 查询用户信息
    D-->>S: 返回用户数据
    S->>S: 验证密码(BCrypt)
    alt 验证成功
        S->>J: generateToken(user)
        J-->>S: JWT Token
        S-->>A: LoginResponse(token, user)
        A-->>N: 200 OK
        N-->>C: 返回Token
    else 验证失败
        S-->>A: 抛出异常
        A-->>N: 401 Unauthorized
        N-->>C: 登录失败
    end
```

---

## 6. 课程报名流程时序图

```mermaid
sequenceDiagram
    autonumber
    participant S as 学员
    participant C as EnrollmentController
    participant ES as EnrollmentService
    participant CS as CourseService
    participant D as 数据库

    S->>C: POST /api/enrollments
    C->>ES: enroll(userId, courseId)
    ES->>D: 检查是否已报名
    D-->>ES: 报名记录
    alt 已报名
        ES-->>C: 抛出BusinessException
        C-->>S: 400 已报名该课程
    else 未报名
        ES->>CS: 获取课程信息
        CS->>D: 查询课程
        D-->>CS: 课程数据
        CS-->>ES: 课程信息
        alt 课程已满
            ES-->>C: 抛出BusinessException
            C-->>S: 400 课程已满
        else 可以报名
            ES->>D: 创建报名记录(PENDING)
            D-->>ES: 保存成功
            ES-->>C: 报名成功
            C-->>S: 200 OK
        end
    end
```

---

## 7. AI 推荐流程时序图

```mermaid
sequenceDiagram
    autonumber
    participant U as 用户
    participant C as AiRecommendController
    participant S as AiRecommendService
    participant AI as DeepSeek AI
    participant D as 数据库

    U->>C: POST /api/ai/recommend
    C->>S: recommend(userId, interests)
    S->>D: 查询用户历史报名
    D-->>S: 报名记录
    S->>D: 查询可用课程列表
    D-->>S: 课程列表
    S->>S: 构建Prompt
    S->>AI: 调用AI接口
    alt AI调用成功
        AI-->>S: 推荐结果
        S->>D: 保存推荐日志(fallback=0)
        S-->>C: 推荐课程列表
        C-->>U: 200 OK
    else AI调用失败
        S->>S: 执行降级策略
        S->>D: 查询热门课程
        D-->>S: 热门课程列表
        S->>D: 保存推荐日志(fallback=1)
        S-->>C: 热门课程列表
        C-->>U: 200 OK (降级结果)
    end
```

---

## 8. 部署架构图

```mermaid
flowchart TB
    subgraph Internet["互联网"]
        Users["用户"]
    end

    subgraph Docker["Docker 环境"]
        subgraph FrontendContainer["前端容器"]
            Nginx["Nginx:80<br/>静态资源 + 反向代理"]
        end

        subgraph BackendContainer["后端容器"]
            SpringBoot["Spring Boot:8080<br/>REST API"]
        end

        subgraph DBContainer["数据库容器"]
            MySQL["MySQL:3306<br/>数据持久化"]
        end

        Volume["mysql_data<br/>数据卷"]
    end

    subgraph External["外部服务"]
        DeepSeek["DeepSeek API<br/>AI 推荐服务"]
    end

    Users --> Nginx
    Nginx --> SpringBoot
    SpringBoot --> MySQL
    SpringBoot --> DeepSeek
    MySQL --> Volume
```

---

## 图表说明

| 图表 | 用途 | 适用场景 |
|------|------|----------|
| 整体系统架构 | 展示系统各层之间的关系 | 项目概览、技术选型说明 |
| 后端模块架构 | 展示后端代码组织结构 | 开发指南、代码导航 |
| 前端模块架构 | 展示前端代码组织结构 | 前端开发指南 |
| 数据库 ER 图 | 展示数据模型和关系 | 数据库设计、数据字典 |
| 认证流程时序图 | 展示登录认证过程 | 安全设计、接口对接 |
| 课程报名流程时序图 | 展示核心业务流程 | 业务理解、功能测试 |
| AI 推荐流程时序图 | 展示 AI 集成流程 | AI 功能说明、降级策略 |
| 部署架构图 | 展示容器化部署方案 | 运维部署、环境搭建 |
