# IT 技能培训智能选课系统 - 架构设计文档

> 文档版本：v1.0.0
> 最后更新：2025-12-14
> 文档状态：✅ 已完成
> 关联文档：[PRD.md](../01-Requirements/PRD.md) | [BusinessRules.md](../01-Requirements/BusinessRules.md)

---

## 目录

1. [架构概览](#1-架构概览)
2. [技术选型](#2-技术选型)
3. [模块设计](#3-模块设计)
4. [权限设计](#4-权限设计)
5. [关键流程](#5-关键流程)
6. [部署架构](#6-部署架构)
7. [参考风格说明](#7-参考风格说明)

---

## 1. 架构概览

### 1.1 系统架构图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              客户端层 (Client)                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                │
│    │   管理端      │    │   讲师端      │    │   学员端      │                │
│    │  (Admin)     │    │ (Instructor) │    │  (Student)   │                │
│    │              │    │              │    │              │                │
│    │ • 用户管理   │    │ • 我的班期   │    │ • 课程浏览   │                │
│    │ • 课程管理   │    │ • 学员名单   │    │ • 在线报名   │                │
│    │ • 班期管理   │    │              │    │ • 我的报名   │                │
│    │ • 报名管理   │    │              │    │ • AI 推荐    │                │
│    │ • 统计看板   │    │              │    │              │                │
│    └──────┬───────┘    └──────┬───────┘    └──────┬───────┘                │
│           │                   │                   │                        │
│           └───────────────────┼───────────────────┘                        │
│                               │                                            │
│                               ▼                                            │
│    ┌─────────────────────────────────────────────────────────────────┐    │
│    │                    Vue 3 + Element Plus                          │    │
│    │         (Vite + Pinia + Vue Router + Axios)                     │    │
│    └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    │ HTTP/HTTPS (RESTful API)
                                    │ JWT Token
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              网关层 (Gateway)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────┐    │
│    │                      Nginx (可选)                                │    │
│    │            静态资源 + 反向代理 + 负载均衡                         │    │
│    └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              应用层 (Application)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌─────────────────────────────────────────────────────────────────┐    │
│    │                   Spring Boot 3.x Application                    │    │
│    ├─────────────────────────────────────────────────────────────────┤    │
│    │                                                                  │    │
│    │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐│    │
│    │  │   Auth     │  │   User     │  │  Course    │  │  Session   ││    │
│    │  │  Module    │  │  Module    │  │  Module    │  │  Module    ││    │
│    │  │            │  │            │  │            │  │            ││    │
│    │  │ • 登录     │  │ • CRUD     │  │ • CRUD     │  │ • CRUD     ││    │
│    │  │ • 注册     │  │ • 权限     │  │ • 上下架   │  │ • 状态     ││    │
│    │  │ • JWT      │  │            │  │            │  │            ││    │
│    │  └────────────┘  └────────────┘  └────────────┘  └────────────┘│    │
│    │                                                                  │    │
│    │  ┌────────────┐  ┌────────────┐  ┌────────────┐                │    │
│    │  │ Enrollment │  │    AI      │  │ Statistics │                │    │
│    │  │  Module    │  │  Module    │  │  Module    │                │    │
│    │  │            │  │            │  │            │                │    │
│    │  │ • 报名     │  │ • 推荐     │  │ • 概览     │                │    │
│    │  │ • 取消     │  │ • 降级     │  │ • 图表     │                │    │
│    │  │ • 名额     │  │ • 日志     │  │            │                │    │
│    │  └────────────┘  └────────────┘  └────────────┘                │    │
│    │                                                                  │    │
│    ├─────────────────────────────────────────────────────────────────┤    │
│    │                        公共组件                                  │    │
│    │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐           │    │
│    │  │ Security │ │ Exception│ │  Utils   │ │  Config  │           │    │
│    │  │  (JWT)   │ │ Handler  │ │          │ │          │           │    │
│    │  └──────────┘ └──────────┘ └──────────┘ └──────────┘           │    │
│    │                                                                  │    │
│    └─────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              数据层 (Data)                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                │
│    │    MySQL     │    │    Redis     │    │   AI API     │                │
│    │   (TiDB)     │    │   (可选)     │    │  (Spring AI) │                │
│    │              │    │              │    │              │                │
│    │ • sys_user   │    │ • 会话缓存   │    │ • OpenAI     │                │
│    │ • course     │    │ • 名额预扣   │    │ • 其他模型   │                │
│    │ • class_     │    │   (二期)     │    │              │                │
│    │   session    │    │              │    │              │                │
│    │ • enrollment │    │              │    │              │                │
│    │ • ai_log     │    │              │    │              │                │
│    └──────────────┘    └──────────────┘    └──────────────┘                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 分层说明

| 层级 | 职责 | 技术 |
|------|------|------|
| 客户端层 | 用户界面、交互逻辑 | Vue 3 + Element Plus |
| 网关层 | 静态资源、反向代理 | Nginx（可选） |
| 应用层 | 业务逻辑、API 服务 | Spring Boot 3.x |
| 数据层 | 数据持久化、外部服务 | MySQL + Redis(可选) + AI API |

---

## 2. 技术选型

### 2.1 后端技术栈

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| Java | 17 | 开发语言 | LTS 版本，Spring Boot 3.x 要求 |
| Spring Boot | 3.x | 应用框架 | 主流企业级框架，生态完善 |
| Spring Security | 6.x | 安全框架 | 认证授权一体化 |
| JWT | - | Token 认证 | 无状态，易于扩展 |
| MyBatis-Plus | 3.5.x | ORM 框架 | 简化 CRUD，代码生成 |
| MySQL | 8.0 | 关系数据库 | 成熟稳定，社区活跃 |
| Spring AI | 1.0.x | AI 集成 | Spring 官方 AI 抽象层 |
| Lombok | 1.18.x | 代码简化 | 减少样板代码 |
| Validation | - | 参数校验 | JSR-303 标准 |
| Swagger/OpenAPI | 3.x | 接口文档 | 自动生成 API 文档 |

### 2.2 前端技术栈

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| Vue | 3.x | 前端框架 | 渐进式，学习曲线平缓 |
| Vite | 5.x | 构建工具 | 快速冷启动，HMR |
| Pinia | 2.x | 状态管理 | Vue 3 官方推荐 |
| Vue Router | 4.x | 路由管理 | SPA 路由 |
| Element Plus | 2.x | UI 组件库 | 企业级组件，文档完善 |
| Axios | 1.x | HTTP 客户端 | Promise 风格，拦截器 |
| ECharts | 5.x | 图表库 | 丰富图表类型 |

### 2.3 开发与部署

| 技术 | 用途 |
|------|------|
| Git | 版本控制 |
| Docker | 容器化部署 |
| Docker Compose | 多容器编排 |
| Maven | 后端构建 |
| pnpm | 前端包管理 |

---

## 3. 模块设计

### 3.1 后端模块结构

```
backend/
├── src/main/java/com/its/
│   ├── ItsApplication.java                 # 启动类
│   │
│   ├── common/                             # 公共模块
│   │   ├── config/                         # 配置类
│   │   │   ├── SecurityConfig.java         # Security 配置
│   │   │   ├── SwaggerConfig.java          # Swagger 配置
│   │   │   ├── CorsConfig.java             # 跨域配置
│   │   │   └── MyBatisPlusConfig.java      # MP 配置
│   │   │
│   │   ├── security/                       # 安全相关
│   │   │   ├── JwtTokenProvider.java       # JWT 工具类
│   │   │   ├── JwtAuthenticationFilter.java# JWT 过滤器
│   │   │   └── UserDetailsServiceImpl.java # 用户详情服务
│   │   │
│   │   ├── exception/                      # 异常处理
│   │   │   ├── BusinessException.java      # 业务异常
│   │   │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │   └── ErrorCode.java              # 错误码枚举
│   │   │
│   │   ├── response/                       # 统一响应
│   │   │   └── R.java                      # 响应包装类
│   │   │
│   │   └── util/                           # 工具类
│   │       └── SecurityUtils.java          # 安全工具类
│   │
│   ├── modules/                            # 业务模块
│   │   ├── auth/                           # 认证模块
│   │   │   ├── controller/
│   │   │   │   └── AuthController.java
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   └── TokenResponse.java
│   │   │   └── service/
│   │   │       └── AuthService.java
│   │   │
│   │   ├── user/                           # 用户模块
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   └── service/
│   │   │
│   │   ├── course/                         # 课程模块
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   └── service/
│   │   │
│   │   ├── session/                        # 班期模块
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   └── service/
│   │   │
│   │   ├── enrollment/                     # 报名模块
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   └── service/
│   │   │
│   │   ├── ai/                             # AI 推荐模块
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   └── service/
│   │   │
│   │   └── statistics/                     # 统计模块
│   │       ├── controller/
│   │       ├── dto/
│   │       └── service/
│   │
│   └── enums/                              # 枚举定义
│       ├── RoleEnum.java
│       ├── CourseCategory.java
│       ├── CourseDifficulty.java
│       ├── SessionStatus.java
│       └── EnrollmentStatus.java
│
└── src/main/resources/
    ├── application.yml                     # 主配置
    ├── application-dev.yml                 # 开发环境
    ├── application-prod.yml                # 生产环境
    └── mapper/                             # MyBatis XML
```

### 3.2 前端模块结构

```
frontend/
├── src/
│   ├── main.ts                             # 入口文件
│   ├── App.vue                             # 根组件
│   │
│   ├── api/                                # API 接口
│   │   ├── auth.ts                         # 认证接口
│   │   ├── user.ts                         # 用户接口
│   │   ├── course.ts                       # 课程接口
│   │   ├── session.ts                      # 班期接口
│   │   ├── enrollment.ts                   # 报名接口
│   │   ├── ai.ts                           # AI 接口
│   │   └── statistics.ts                   # 统计接口
│   │
│   ├── components/                         # 公共组件
│   │   ├── layout/                         # 布局组件
│   │   │   ├── AdminLayout.vue             # 管理端布局
│   │   │   ├── InstructorLayout.vue        # 讲师端布局
│   │   │   └── StudentLayout.vue           # 学员端布局
│   │   └── common/                         # 通用组件
│   │       ├── Pagination.vue
│   │       └── ConfirmDialog.vue
│   │
│   ├── views/                              # 页面组件
│   │   ├── auth/                           # 认证页面
│   │   │   ├── Login.vue
│   │   │   └── Register.vue
│   │   │
│   │   ├── admin/                          # 管理端页面
│   │   │   ├── Dashboard.vue               # 统计看板
│   │   │   ├── user/                       # 用户管理
│   │   │   ├── course/                     # 课程管理
│   │   │   ├── session/                    # 班期管理
│   │   │   └── enrollment/                 # 报名管理
│   │   │
│   │   ├── instructor/                     # 讲师端页面
│   │   │   ├── MySessions.vue              # 我的班期
│   │   │   └── StudentList.vue             # 学员名单
│   │   │
│   │   └── student/                        # 学员端页面
│   │       ├── Home.vue                    # 首页
│   │       ├── CourseList.vue              # 课程列表
│   │       ├── CourseDetail.vue            # 课程详情
│   │       ├── MyEnrollments.vue           # 我的报名
│   │       └── AiRecommend.vue             # AI 推荐
│   │
│   ├── router/                             # 路由配置
│   │   └── index.ts
│   │
│   ├── stores/                             # 状态管理
│   │   ├── auth.ts                         # 认证状态
│   │   └── user.ts                         # 用户状态
│   │
│   ├── utils/                              # 工具函数
│   │   ├── request.ts                      # Axios 封装
│   │   ├── auth.ts                         # Token 管理
│   │   └── validate.ts                     # 校验规则
│   │
│   └── types/                              # 类型定义
│       └── index.ts
│
├── index.html
├── vite.config.ts
├── tsconfig.json
└── package.json
```

### 3.3 模块依赖关系

```
┌─────────────────────────────────────────────────────────────────┐
│                        模块依赖关系图                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                      ┌──────────────┐                           │
│                      │    Common    │                           │
│                      │  (公共模块)   │                           │
│                      └──────┬───────┘                           │
│                             │                                   │
│          ┌──────────────────┼──────────────────┐               │
│          │                  │                  │               │
│          ▼                  ▼                  ▼               │
│    ┌──────────┐      ┌──────────┐      ┌──────────┐           │
│    │   Auth   │      │   User   │      │  Course  │           │
│    │  认证模块 │      │  用户模块 │      │  课程模块 │           │
│    └────┬─────┘      └────┬─────┘      └────┬─────┘           │
│         │                 │                  │                 │
│         │                 │                  ▼                 │
│         │                 │           ┌──────────┐            │
│         │                 │           │ Session  │            │
│         │                 │           │  班期模块 │            │
│         │                 │           └────┬─────┘            │
│         │                 │                │                   │
│         │                 └────────┬───────┘                   │
│         │                          │                           │
│         │                          ▼                           │
│         │                   ┌──────────┐                       │
│         │                   │Enrollment│                       │
│         │                   │  报名模块 │                       │
│         │                   └────┬─────┘                       │
│         │                        │                             │
│         ▼                        ▼                             │
│    ┌──────────┐           ┌──────────┐                        │
│    │    AI    │           │Statistics│                        │
│    │  AI模块  │           │  统计模块 │                        │
│    └──────────┘           └──────────┘                        │
│                                                                 │
│  依赖说明：                                                     │
│  • Auth 依赖 User（获取用户信息）                               │
│  • Session 依赖 Course、User（关联课程和讲师）                  │
│  • Enrollment 依赖 Session、User（关联班期和学员）              │
│  • AI 依赖 Course（推荐课程数据）                               │
│  • Statistics 依赖 Enrollment（统计报名数据）                   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. 权限设计

### 4.1 角色定义

| 角色 | 英文标识 | 角色码 | 说明 |
|------|----------|--------|------|
| 管理员 | ADMIN | ROLE_ADMIN | 系统管理权限 |
| 讲师 | INSTRUCTOR | ROLE_INSTRUCTOR | 班期和学员查看权限 |
| 学员 | STUDENT | ROLE_STUDENT | 课程浏览和报名权限 |

### 4.2 权限矩阵

| API 路径 | 方法 | ADMIN | INSTRUCTOR | STUDENT | 匿名 | 说明 |
|----------|------|-------|------------|---------|------|------|
| `/api/auth/register` | POST | - | - | - | ✅ | 注册 |
| `/api/auth/login` | POST | - | - | - | ✅ | 登录 |
| `/api/auth/refresh` | POST | ✅ | ✅ | ✅ | - | 刷新Token |
| `/api/users` | GET | ✅ | - | - | - | 用户列表 |
| `/api/users` | POST | ✅ | - | - | - | 新增用户 |
| `/api/users/{id}` | PUT | ✅ | - | - | - | 编辑用户 |
| `/api/users/{id}` | DELETE | ✅ | - | - | - | 删除用户 |
| `/api/users/me` | GET | ✅ | ✅ | ✅ | - | 个人信息 |
| `/api/courses` | GET | ✅ | ✅(只读) | ✅(上架) | - | 课程列表 |
| `/api/courses` | POST | ✅ | - | - | - | 新增课程 |
| `/api/courses/{id}` | PUT | ✅ | - | - | - | 编辑课程 |
| `/api/courses/{id}` | DELETE | ✅ | - | - | - | 删除课程 |
| `/api/courses/{id}/publish` | PUT | ✅ | - | - | - | 上架课程 |
| `/api/courses/{id}/unpublish` | PUT | ✅ | - | - | - | 下架课程 |
| `/api/sessions` | GET | ✅ | ✅(自己) | ✅(报名中) | - | 班期列表 |
| `/api/sessions` | POST | ✅ | - | - | - | 新增班期 |
| `/api/sessions/{id}` | PUT | ✅ | - | - | - | 编辑班期 |
| `/api/sessions/{id}` | DELETE | ✅ | - | - | - | 删除班期 |
| `/api/sessions/{id}/students` | GET | ✅ | ✅(自己) | - | - | 学员名单 |
| `/api/enrollments` | GET | ✅ | - | ✅(自己) | - | 报名列表 |
| `/api/enrollments` | POST | - | - | ✅ | - | 报名 |
| `/api/enrollments/{id}/cancel` | PUT | ✅ | - | ✅(自己) | - | 取消报名 |
| `/api/ai/recommend` | POST | - | - | ✅ | - | AI推荐 |
| `/api/statistics/overview` | GET | ✅ | - | - | - | 数据概览 |
| `/api/statistics/course-ranking` | GET | ✅ | - | - | - | 课程热度 |
| `/api/statistics/enrollment-trend` | GET | ✅ | - | - | - | 报名趋势 |

### 4.3 Spring Security 配置思路

```java
// SecurityConfig.java 配置示意
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // 公开接口
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

            // 管理员专属
            .requestMatchers("/api/users/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/courses/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/courses/**").hasRole("ADMIN")
            .requestMatchers("/api/sessions/**").hasRole("ADMIN")
            .requestMatchers("/api/statistics/**").hasRole("ADMIN")

            // 学员专属
            .requestMatchers("/api/ai/**").hasRole("STUDENT")
            .requestMatchers(HttpMethod.POST, "/api/enrollments").hasRole("STUDENT")

            // 其他需要认证
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

## 5. 关键流程

### 5.1 用户认证流程

```
┌─────────────────────────────────────────────────────────────────┐
│                        JWT 认证流程                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  用户                   前端                    后端             │
│   │                      │                      │               │
│   │  1. 输入用户名密码    │                      │               │
│   │ ─────────────────────>│                      │               │
│   │                      │  2. POST /api/auth/login             │
│   │                      │ ─────────────────────>│               │
│   │                      │                      │  3. 验证密码   │
│   │                      │                      │  4. 生成JWT   │
│   │                      │  5. 返回 Token        │               │
│   │                      │ <─────────────────────│               │
│   │                      │  6. 存储 Token        │               │
│   │                      │     (localStorage)   │               │
│   │  7. 跳转首页         │                      │               │
│   │ <─────────────────────│                      │               │
│   │                      │                      │               │
│   │  ═══════════════ 后续请求 ═══════════════   │               │
│   │                      │                      │               │
│   │  8. 访问功能页面      │                      │               │
│   │ ─────────────────────>│                      │               │
│   │                      │  9. 请求携带 Token    │               │
│   │                      │  (Authorization:     │               │
│   │                      │   Bearer xxx)        │               │
│   │                      │ ─────────────────────>│               │
│   │                      │                      │  10. 验证Token│
│   │                      │                      │  11. 解析角色 │
│   │                      │                      │  12. 权限检查 │
│   │                      │  13. 返回数据        │               │
│   │                      │ <─────────────────────│               │
│   │  14. 展示数据        │                      │               │
│   │ <─────────────────────│                      │               │
│   │                      │                      │               │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 报名流程（含名额控制）

```
┌─────────────────────────────────────────────────────────────────┐
│                        报名流程（名额控制）                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  学员                   前端                    后端             │
│   │                      │                      │               │
│   │  1. 点击"立即报名"   │                      │               │
│   │ ─────────────────────>│                      │               │
│   │                      │  2. 弹出确认框       │               │
│   │                      │ <──                  │               │
│   │  3. 确认报名         │                      │               │
│   │ ─────────────────────>│                      │               │
│   │                      │  4. POST /api/enrollments            │
│   │                      │     {sessionId: xxx}  │               │
│   │                      │ ─────────────────────>│               │
│   │                      │                      │               │
│   │                      │         ┌────────────┴────────────┐  │
│   │                      │         │      开启事务           │  │
│   │                      │         │                         │  │
│   │                      │         │  5. 检查用户是否已报名  │  │
│   │                      │         │     SELECT FROM enrollment │
│   │                      │         │     WHERE user_id=? AND │  │
│   │                      │         │     session_id=? AND    │  │
│   │                      │         │     status=0            │  │
│   │                      │         │     ↓                   │  │
│   │                      │         │  已存在 → 返回"重复报名" │  │
│   │                      │         │                         │  │
│   │                      │         │  6. 原子扣减名额        │  │
│   │                      │         │     UPDATE class_session│  │
│   │                      │         │     SET current = current+1│
│   │                      │         │     WHERE id=? AND      │  │
│   │                      │         │     current < max AND   │  │
│   │                      │         │     status=1            │  │
│   │                      │         │     ↓                   │  │
│   │                      │         │  affected=0 → 返回"名额不足"│
│   │                      │         │                         │  │
│   │                      │         │  7. 创建报名记录        │  │
│   │                      │         │     INSERT INTO enrollment │
│   │                      │         │                         │  │
│   │                      │         │  8. 检查是否满员        │  │
│   │                      │         │     IF current >= max   │  │
│   │                      │         │     UPDATE status=2     │  │
│   │                      │         │                         │  │
│   │                      │         │      提交事务           │  │
│   │                      │         └────────────┬────────────┘  │
│   │                      │                      │               │
│   │                      │  9. 返回成功         │               │
│   │                      │ <─────────────────────│               │
│   │  10. 提示"报名成功"  │                      │               │
│   │ <─────────────────────│                      │               │
│   │                      │                      │               │
└─────────────────────────────────────────────────────────────────┘
```

### 5.3 AI 推荐流程（含降级）

```
┌─────────────────────────────────────────────────────────────────┐
│                    AI 推荐流程（含降级策略）                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  学员                   前端                    后端             │
│   │                      │                      │               │
│   │  1. 输入学习目标     │                      │               │
│   │     "想学后端开发"   │                      │               │
│   │ ─────────────────────>│                      │               │
│   │                      │  2. 显示 Loading     │               │
│   │                      │                      │               │
│   │                      │  3. POST /api/ai/recommend           │
│   │                      │     {input: "想学后端开发"}          │
│   │                      │ ─────────────────────>│               │
│   │                      │                      │               │
│   │                      │         ┌────────────┴────────────┐  │
│   │                      │         │                         │  │
│   │                      │         │  4. 调用 Spring AI      │  │
│   │                      │         │     设置超时 10s        │  │
│   │                      │         │                         │  │
│   │                      │         │  ┌─── 正常响应 ───┐     │  │
│   │                      │         │  │                │     │  │
│   │                      │         │  │ 5a. 解析 AI 返回│     │  │
│   │                      │         │  │ 5b. 提取课程ID │     │  │
│   │                      │         │  │ 5c. 查询课程详情│     │  │
│   │                      │         │  │ 5d. 组装推荐结果│     │  │
│   │                      │         │  │                │     │  │
│   │                      │         │  └────────────────┘     │  │
│   │                      │         │                         │  │
│   │                      │         │  ┌─── 超时/异常 ───┐    │  │
│   │                      │         │  │                 │    │  │
│   │                      │         │  │ 6a. 触发降级    │    │  │
│   │                      │         │  │ 6b. 查询热门课程│    │  │
│   │                      │         │  │     TOP 5       │    │  │
│   │                      │         │  │ 6c. 标记降级    │    │  │
│   │                      │         │  │                 │    │  │
│   │                      │         │  └─────────────────┘    │  │
│   │                      │         │                         │  │
│   │                      │         │  7. 记录推荐日志        │  │
│   │                      │         │     INSERT ai_recommend_log│
│   │                      │         │                         │  │
│   │                      │         └────────────┬────────────┘  │
│   │                      │                      │               │
│   │                      │  8. 返回推荐结果     │               │
│   │                      │     {courses, reason,│               │
│   │                      │      order, fallback}│               │
│   │                      │ <─────────────────────│               │
│   │                      │                      │               │
│   │  9. 展示推荐课程     │                      │               │
│   │     + 理由 + 顺序    │                      │               │
│   │    (降级时显示提示)  │                      │               │
│   │ <─────────────────────│                      │               │
│   │                      │                      │               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 6. 部署架构

### 6.1 开发环境

```
┌─────────────────────────────────────────────────────────────────┐
│                        开发环境                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │  前端开发     │    │  后端开发     │    │   MySQL      │      │
│  │              │    │              │    │              │      │
│  │  npm run dev │    │  mvn spring- │    │  localhost:  │      │
│  │  localhost:  │    │  boot:run    │    │  3306        │      │
│  │  5173        │───>│  localhost:  │───>│              │      │
│  │              │    │  8080        │    │              │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 生产环境（Docker Compose）

```
┌─────────────────────────────────────────────────────────────────┐
│                    生产环境 (Docker Compose)                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                         ┌──────────────┐                        │
│                         │    Nginx     │ :80                    │
│                         │   (前端静态  │                        │
│                         │   + 反向代理)│                        │
│                         └──────┬───────┘                        │
│                                │                                │
│              ┌─────────────────┼─────────────────┐              │
│              │                 │                 │              │
│              ▼                 ▼                 ▼              │
│     ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│     │    静态资源   │  │  /api/*      │  │   其他       │       │
│     │    (Vue 构建) │  │  代理到后端  │  │              │       │
│     └──────────────┘  └──────┬───────┘  └──────────────┘       │
│                              │                                  │
│                              ▼                                  │
│                      ┌──────────────┐                           │
│                      │   Backend    │ :8080                     │
│                      │ (Spring Boot)│                           │
│                      └──────┬───────┘                           │
│                             │                                   │
│              ┌──────────────┼──────────────┐                    │
│              │              │              │                    │
│              ▼              ▼              ▼                    │
│     ┌──────────────┐ ┌──────────────┐ ┌──────────────┐         │
│     │    MySQL     │ │    Redis     │ │   AI API     │         │
│     │    :3306     │ │    :6379     │ │  (外部)      │         │
│     │              │ │   (可选)     │ │              │         │
│     └──────────────┘ └──────────────┘ └──────────────┘         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 6.3 Docker Compose 配置示意

```yaml
# docker-compose.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: its-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: it_training
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./docs/04-DB/schema.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    build: ./backend
    container_name: its-backend
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/it_training
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
    ports:
      - "8080:8080"
    depends_on:
      - mysql

  frontend:
    build: ./frontend
    container_name: its-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql-data:
```

---

## 7. 参考风格说明

> 以下说明本项目参考 easy-enroll 的哪些架构/设计风格（仅风格参考，代码独立实现）

### 7.1 后端架构风格参考

| # | 参考点 | 说明 |
|---|--------|------|
| 1 | Controller-Service-Mapper 三层架构 | 职责清晰，便于维护 |
| 2 | 按业务域划分 modules | user/course/enrollment 等独立模块 |
| 3 | 统一响应体包装 | `{ code, message, data }` 格式 |
| 4 | 全局异常处理 | `@ControllerAdvice` 统一捕获 |
| 5 | MyBatis-Plus 使用方式 | 继承 BaseMapper，简化 CRUD |

### 7.2 前端架构风格参考

| # | 参考点 | 说明 |
|---|--------|------|
| 1 | Vue 3 组合式 API | `<script setup>` 风格 |
| 2 | 分角色路由布局 | admin/instructor/student 各自布局 |
| 3 | API 模块化组织 | 按业务域拆分 api 文件 |
| 4 | Element Plus 组件使用 | 表格、表单、弹窗标准用法 |
| 5 | Axios 封装 | 拦截器、Token 注入、错误处理 |

### 7.3 数据库设计风格参考

| # | 参考点 | 说明 |
|---|--------|------|
| 1 | 表名 snake_case | `sys_user`, `class_session` |
| 2 | 主键统一 id BIGINT | 自增主键 |
| 3 | 外键命名 xxx_id | `course_id`, `user_id` |
| 4 | 审计字段 | `created_at`, `updated_at` |
| 5 | 软删除 deleted | `deleted TINYINT DEFAULT 0` |

### 7.4 接口设计风格参考

| # | 参考点 | 说明 |
|---|--------|------|
| 1 | RESTful 资源命名 | `/api/courses`, `/api/enrollments` |
| 2 | HTTP 方法语义 | GET/POST/PUT/DELETE |
| 3 | 分页参数 | `page`, `size`, `keyword` |
| 4 | 状态操作子资源 | `/api/courses/{id}/publish` |
| 5 | 嵌套资源 | `/api/sessions/{id}/students` |

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | Architect | 初始版本 |
