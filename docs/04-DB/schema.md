# IT 技能培训智能选课系统 - 数据库设计说明

> 文档版本：v1.0.0
> 最后更新：2025-12-14
> 文档状态：✅ 已完成
> 关联文档：[schema.sql](schema.sql) | [BusinessRules.md](../01-Requirements/BusinessRules.md)

---

## 目录

1. [设计概述](#1-设计概述)
2. [表结构详解](#2-表结构详解)
3. [ER 图](#3-er-图)
4. [索引设计](#4-索引设计)
5. [数据字典](#5-数据字典)
6. [参考风格说明](#6-参考风格说明)

---

## 1. 设计概述

### 1.1 数据库信息

| 项目 | 值 |
|------|-----|
| 数据库类型 | MySQL 8.0+ / TiDB |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |
| 存储引擎 | InnoDB |
| 数据库名 | it_training |

### 1.2 表清单

| 序号 | 表名 | 中文名 | 说明 |
|------|------|--------|------|
| 1 | sys_user | 用户表 | 存储所有用户信息（管理员、讲师、学员） |
| 2 | course | 课程表 | 存储课程基本信息 |
| 3 | class_session | 班期表 | 存储具体开班信息 |
| 4 | enrollment | 报名记录表 | 存储学员报名记录 |
| 5 | ai_recommend_log | AI推荐日志表 | 记录 AI 推荐请求 |

### 1.3 命名规范

| 规范 | 说明 | 示例 |
|------|------|------|
| 表名 | 小写下划线（snake_case） | `sys_user`, `class_session` |
| 主键 | 统一使用 `id` | `id BIGINT AUTO_INCREMENT` |
| 外键 | 命名为 `xxx_id` | `course_id`, `user_id` |
| 索引 | `idx_` 前缀 + 字段名 | `idx_status`, `idx_course_id` |
| 唯一索引 | `uk_` 前缀 + 字段名 | `uk_username`, `uk_code` |
| 外键约束 | `fk_` 前缀 + 表名_字段 | `fk_session_course` |
| 时间字段 | `xxx_at` 命名 | `created_at`, `updated_at` |

---

## 2. 表结构详解

### 2.1 用户表 (sys_user)

> 存储系统所有用户，通过 role 字段区分角色

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) | N | - | 用户名（登录账号），唯一 |
| password | VARCHAR(255) | N | - | 密码（BCrypt 加密） |
| real_name | VARCHAR(50) | N | - | 真实姓名 |
| phone | VARCHAR(20) | Y | NULL | 手机号 |
| email | VARCHAR(100) | Y | NULL | 邮箱 |
| avatar | VARCHAR(255) | Y | NULL | 头像 URL |
| role | VARCHAR(20) | N | 'STUDENT' | 角色：ADMIN/INSTRUCTOR/STUDENT |
| status | TINYINT | N | 1 | 状态：0-禁用, 1-启用 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间（自动更新） |
| deleted | TINYINT | N | 0 | 软删除：0-正常, 1-已删除 |

**索引：**
- `PRIMARY KEY (id)`
- `UNIQUE KEY uk_username (username)`
- `KEY idx_role (role)`
- `KEY idx_status (status)`

---

### 2.2 课程表 (course)

> 存储课程基本信息，是班期的模板

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| code | VARCHAR(30) | N | - | 课程编码（如 JAVA-001），唯一 |
| name | VARCHAR(100) | N | - | 课程名称 |
| category | VARCHAR(50) | N | - | 分类（枚举值） |
| description | TEXT | Y | NULL | 课程简介 |
| cover_image | VARCHAR(255) | Y | NULL | 封面图 URL |
| difficulty | TINYINT | N | 1 | 难度：1-4 |
| duration_hours | INT | N | 0 | 课时数（小时） |
| prerequisites | VARCHAR(500) | Y | NULL | 先修要求（课程编码，逗号分隔） |
| tags | VARCHAR(200) | Y | NULL | 标签（逗号分隔） |
| status | TINYINT | N | 0 | 状态：0-下架, 1-上架 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | N | 0 | 软删除 |

**索引：**
- `PRIMARY KEY (id)`
- `UNIQUE KEY uk_code (code)`
- `KEY idx_category (category)`
- `KEY idx_difficulty (difficulty)`
- `KEY idx_status (status)`

**分类枚举值：**
- BACKEND - 后端开发
- FRONTEND - 前端开发
- DATABASE - 数据库
- CLOUD - 云计算
- AI - 人工智能
- OTHER - 其他

---

### 2.3 班期表 (class_session)

> 课程的具体开班实例，学员报名的实际对象

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| course_id | BIGINT | N | - | 所属课程 ID（外键） |
| instructor_id | BIGINT | N | - | 授课讲师 ID（外键） |
| session_code | VARCHAR(30) | N | - | 班期编码，唯一 |
| start_date | DATE | N | - | 开班日期 |
| end_date | DATE | N | - | 结束日期 |
| schedule | VARCHAR(200) | Y | NULL | 上课时间描述 |
| location | VARCHAR(200) | Y | NULL | 上课地点/线上链接 |
| max_capacity | INT | N | 30 | 最大名额 |
| current_enrollment | INT | N | 0 | 当前报名人数（冗余） |
| status | TINYINT | N | 0 | 状态：0-4（见下） |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | N | 0 | 软删除 |

**索引：**
- `PRIMARY KEY (id)`
- `UNIQUE KEY uk_session_code (session_code)`
- `KEY idx_course_id (course_id)`
- `KEY idx_instructor_id (instructor_id)`
- `KEY idx_status (status)`
- `KEY idx_start_date (start_date)`

**外键约束：**
- `fk_session_course` → `course(id)` ON DELETE RESTRICT
- `fk_session_instructor` → `sys_user(id)` ON DELETE RESTRICT

**状态枚举值：**
- 0 - 未开放
- 1 - 报名中
- 2 - 已满员
- 3 - 进行中
- 4 - 已结束

---

### 2.4 报名记录表 (enrollment)

> 学员报名记录，核心业务表

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 学员 ID（外键） |
| session_id | BIGINT | N | - | 班期 ID（外键） |
| status | TINYINT | N | 0 | 状态：0-已报名, 1-已取消 |
| enrolled_at | DATETIME | N | CURRENT_TIMESTAMP | 报名时间 |
| canceled_at | DATETIME | Y | NULL | 取消时间 |
| cancel_reason | VARCHAR(200) | Y | NULL | 取消原因 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

**索引：**
- `PRIMARY KEY (id)`
- `KEY idx_user_id (user_id)`
- `KEY idx_session_id (session_id)`
- `KEY idx_status (status)`
- `KEY idx_user_session (user_id, session_id)` — 用于快速查重

**外键约束：**
- `fk_enrollment_user` → `sys_user(id)` ON DELETE RESTRICT
- `fk_enrollment_session` → `class_session(id)` ON DELETE RESTRICT

**状态枚举值：**
- 0 - 已报名 (ENROLLED)
- 1 - 已取消 (CANCELED)

---

### 2.5 AI 推荐日志表 (ai_recommend_log)

> 记录每次 AI 推荐请求，用于分析和审计

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 请求用户 ID（外键） |
| input_text | TEXT | N | - | 用户输入的学习目标 |
| recommended_courses | TEXT | N | - | 推荐结果 JSON |
| reason | TEXT | N | - | 推荐理由 |
| model_used | VARCHAR(50) | N | - | 使用的 AI 模型 |
| response_time_ms | INT | N | 0 | 响应耗时（毫秒） |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |

**索引：**
- `PRIMARY KEY (id)`
- `KEY idx_user_id (user_id)`
- `KEY idx_created_at (created_at)`

**外键约束：**
- `fk_ai_log_user` → `sys_user(id)` ON DELETE CASCADE

**recommended_courses JSON 格式：**
```json
{
  "courseIds": [1, 2, 3],
  "order": ["JAVA-001", "VUE-001", "JAVA-002"]
}
```

---

## 3. ER 图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ER 关系图                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                              ┌──────────────┐                               │
│                              │   sys_user   │                               │
│                              ├──────────────┤                               │
│                              │ PK id        │                               │
│                              │    username  │                               │
│                              │    password  │                               │
│                              │    real_name │                               │
│                              │    role      │                               │
│                              │    status    │                               │
│                              │    ...       │                               │
│                              └──────┬───────┘                               │
│                                     │                                       │
│           ┌─────────────────────────┼─────────────────────────┐             │
│           │                         │                         │             │
│           │ 1                       │ 1                       │ 1           │
│           │                         │                         │             │
│           ▼                         ▼                         ▼             │
│  ┌──────────────┐          ┌──────────────┐          ┌──────────────┐      │
│  │ enrollment   │          │class_session │          │ai_recommend_ │      │
│  │   (报名)     │          │   (班期)     │          │    log       │      │
│  ├──────────────┤          ├──────────────┤          ├──────────────┤      │
│  │ PK id        │          │ PK id        │          │ PK id        │      │
│  │ FK user_id   │──────────│ FK instructor│          │ FK user_id   │      │
│  │ FK session_id│──────────│    _id       │          │    input_text│      │
│  │    status    │          │ FK course_id │          │    recommend │      │
│  │    enrolled_ │          │    session_  │          │    _courses  │      │
│  │    at        │          │    code      │          │    reason    │      │
│  │    canceled_ │          │    start_date│          │    model_used│      │
│  │    at        │          │    end_date  │          │    response_ │      │
│  │    ...       │          │    max_      │          │    time_ms   │      │
│  └──────────────┘          │    capacity  │          └──────────────┘      │
│           │                │    current_  │                                 │
│           │ N              │    enrollment│                                 │
│           │                │    status    │                                 │
│           │                │    ...       │                                 │
│           │                └──────┬───────┘                                 │
│           │                       │                                         │
│           │                       │ N                                       │
│           │                       │                                         │
│           │                       │ 1                                       │
│           │                       ▼                                         │
│           │               ┌──────────────┐                                  │
│           └──────────────>│    course    │                                  │
│                           │   (课程)     │                                  │
│                           ├──────────────┤                                  │
│                           │ PK id        │                                  │
│                           │    code      │                                  │
│                           │    name      │                                  │
│                           │    category  │                                  │
│                           │    difficulty│                                  │
│                           │    status    │                                  │
│                           │    ...       │                                  │
│                           └──────────────┘                                  │
│                                                                             │
│  关系说明：                                                                  │
│  • sys_user 1:N class_session（一个讲师负责多个班期）                        │
│  • sys_user 1:N enrollment（一个学员有多条报名记录）                         │
│  • sys_user 1:N ai_recommend_log（一个用户有多条推荐日志）                   │
│  • course 1:N class_session（一门课程有多个班期）                            │
│  • class_session 1:N enrollment（一个班期有多条报名）                        │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 索引设计

### 4.1 索引策略

| 索引类型 | 使用场景 | 示例 |
|----------|----------|------|
| 主键索引 | 记录唯一标识 | `PRIMARY KEY (id)` |
| 唯一索引 | 业务唯一约束 | `UNIQUE KEY uk_username` |
| 普通索引 | 查询条件优化 | `KEY idx_status` |
| 组合索引 | 多条件查询优化 | `KEY idx_user_session` |
| 外键索引 | 关联查询优化 | `KEY idx_course_id` |

### 4.2 关键查询与索引对应

| 查询场景 | SQL 条件 | 使用索引 |
|----------|----------|----------|
| 用户登录 | `WHERE username = ?` | `uk_username` |
| 课程列表（分类筛选） | `WHERE category = ? AND status = 1` | `idx_category`, `idx_status` |
| 班期列表（课程筛选） | `WHERE course_id = ?` | `idx_course_id` |
| 报名查重 | `WHERE user_id = ? AND session_id = ? AND status = 0` | `idx_user_session` |
| 我的报名 | `WHERE user_id = ?` | `idx_user_id` |
| 班期学员 | `WHERE session_id = ?` | `idx_session_id` |

---

## 5. 数据字典

### 5.1 角色枚举 (role)

| 值 | 说明 | 权限范围 |
|----|------|----------|
| ADMIN | 系统管理员 | 全部功能 |
| INSTRUCTOR | 讲师 | 查看自己班期和学员 |
| STUDENT | 学员 | 浏览课程、报名、AI推荐 |

### 5.2 课程分类 (category)

| 值 | 说明 |
|----|------|
| BACKEND | 后端开发 |
| FRONTEND | 前端开发 |
| DATABASE | 数据库 |
| CLOUD | 云计算 |
| AI | 人工智能 |
| OTHER | 其他 |

### 5.3 课程难度 (difficulty)

| 值 | 说明 |
|----|------|
| 1 | 入门 |
| 2 | 初级 |
| 3 | 中级 |
| 4 | 高级 |

### 5.4 班期状态 (class_session.status)

| 值 | 说明 | 可报名 |
|----|------|--------|
| 0 | 未开放 | ❌ |
| 1 | 报名中 | ✅ |
| 2 | 已满员 | ❌ |
| 3 | 进行中 | ❌ |
| 4 | 已结束 | ❌ |

### 5.5 报名状态 (enrollment.status)

| 值 | 说明 |
|----|------|
| 0 | 已报名 (ENROLLED) |
| 1 | 已取消 (CANCELED) |

---

## 6. 参考风格说明

> 本数据库设计参考 easy-enroll 的以下命名和设计风格：

| # | 参考点 | 说明 |
|---|--------|------|
| 1 | 表名 snake_case | 如 `sys_user`, `class_session` |
| 2 | 主键统一 `id` BIGINT | 自增主键，便于分布式扩展 |
| 3 | 外键命名 `xxx_id` | 明确关联关系 |
| 4 | 审计字段 `created_at`, `updated_at` | 记录创建和修改时间 |
| 5 | 软删除 `deleted` 字段 | 逻辑删除，保留历史数据 |
| 6 | 冗余计数字段 `current_enrollment` | 避免 COUNT 查询，提升性能 |
| 7 | 状态字段用 TINYINT | 配合枚举注释，节省空间 |

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | Architect | 初始版本 |
