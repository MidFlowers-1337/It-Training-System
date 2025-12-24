# IT 技能培训智能选课系统 - 数据库设计说明

> 文档版本：v1.1.0
> 最后更新：2025-12-24
> 文档状态：✅ 已完成
> 关联文档：[schema.sql](schema.sql) | [migration-v1.1-learning-tables.sql](migration-v1.1-learning-tables.sql) | [BusinessRules.md](../01-Requirements/BusinessRules.md)

---

## 目录

1. [设计概述](#1-设计概述)
2. [核心业务表](#2-核心业务表)
3. [学习管理表](#3-学习管理表)
4. [ER 图](#4-er-图)
5. [索引设计](#5-索引设计)
6. [数据字典](#6-数据字典)

---

## 1. 设计概述

### 1.1 数据库信息

| 项目 | 值 |
|------|-----|
| 数据库类型 | MySQL 8.0+ |
| 字符集 | utf8mb4 |
| 排序规则 | utf8mb4_unicode_ci |
| 存储引擎 | InnoDB |
| 数据库名 | it_training |

### 1.2 表清单

| 序号 | 表名 | 中文名 | 模块 |
|------|------|--------|------|
| 1 | sys_user | 用户表 | 核心 |
| 2 | course | 课程表 | 核心 |
| 3 | class_session | 班期表 | 核心 |
| 4 | enrollment | 报名记录表 | 核心 |
| 5 | ai_recommend_log | AI推荐日志表 | 核心 |
| 6 | user_skill_tag | 用户技能标签表 | 学习 |
| 7 | user_preference | 用户学习偏好表 | 学习 |
| 8 | course_similarity | 课程相似度表 | 学习 |
| 9 | learning_progress | 学习进度表 | 学习 |
| 10 | learning_plan | 学习计划表 | 学习 |
| 11 | study_checkin | 学习打卡记录表 | 学习 |
| 12 | achievement | 成就定义表 | 学习 |
| 13 | user_achievement | 用户成就表 | 学习 |
| 14 | user_learning_stats | 用户学习统计表 | 学习 |

### 1.3 命名规范

| 规范 | 说明 | 示例 |
|------|------|------|
| 表名 | 小写下划线（snake_case） | `sys_user`, `class_session` |
| 主键 | 统一使用 `id` | `id BIGINT AUTO_INCREMENT` |
| 外键 | 命名为 `xxx_id` | `course_id`, `user_id` |
| 索引 | `idx_` 前缀 + 字段名 | `idx_status`, `idx_course_id` |
| 唯一索引 | `uk_` 前缀 + 字段名 | `uk_username`, `uk_code` |
| 时间字段 | `xxx_at` 命名 | `created_at`, `updated_at` |

---

## 2. 核心业务表

### 2.1 用户表 (sys_user)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) | N | - | 用户名，唯一 |
| password | VARCHAR(255) | N | - | 密码（BCrypt） |
| real_name | VARCHAR(50) | N | - | 真实姓名 |
| phone | VARCHAR(20) | Y | NULL | 手机号 |
| email | VARCHAR(100) | Y | NULL | 邮箱 |
| avatar | VARCHAR(255) | Y | NULL | 头像 URL |
| role | VARCHAR(20) | N | 'STUDENT' | 角色 |
| status | TINYINT | N | 1 | 状态：0-禁用, 1-启用 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | N | 0 | 软删除 |

### 2.2 课程表 (course)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| code | VARCHAR(30) | N | - | 课程编码，唯一 |
| name | VARCHAR(100) | N | - | 课程名称 |
| category | VARCHAR(50) | N | - | 分类 |
| description | TEXT | Y | NULL | 课程简介 |
| cover_image | VARCHAR(255) | Y | NULL | 封面图 URL |
| difficulty | TINYINT | N | 1 | 难度：1-4 |
| duration_hours | INT | N | 0 | 课时数 |
| prerequisites | VARCHAR(500) | Y | NULL | 先修要求 |
| tags | VARCHAR(200) | Y | NULL | 标签 |
| status | TINYINT | N | 0 | 状态：0-下架, 1-上架 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | N | 0 | 软删除 |

### 2.3 班期表 (class_session)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| course_id | BIGINT | N | - | 所属课程 ID |
| instructor_id | BIGINT | N | - | 授课讲师 ID |
| session_code | VARCHAR(30) | N | - | 班期编码，唯一 |
| start_date | DATE | N | - | 开班日期 |
| end_date | DATE | N | - | 结束日期 |
| schedule | VARCHAR(200) | Y | NULL | 上课时间描述 |
| location | VARCHAR(200) | Y | NULL | 上课地点 |
| max_capacity | INT | N | 30 | 最大名额 |
| current_enrollment | INT | N | 0 | 当前报名人数 |
| status | TINYINT | N | 0 | 状态：0-4 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | N | 0 | 软删除 |

### 2.4 报名记录表 (enrollment)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 学员 ID |
| session_id | BIGINT | N | - | 班期 ID |
| status | TINYINT | N | 0 | 状态：0-已报名, 1-已取消 |
| enrolled_at | DATETIME | N | CURRENT_TIMESTAMP | 报名时间 |
| canceled_at | DATETIME | Y | NULL | 取消时间 |
| cancel_reason | VARCHAR(200) | Y | NULL | 取消原因 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 2.5 AI 推荐日志表 (ai_recommend_log)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 请求用户 ID |
| input_text | TEXT | N | - | 用户输入 |
| recommended_courses | TEXT | N | - | 推荐结果 JSON |
| reason | TEXT | N | - | 推荐理由 |
| model_used | VARCHAR(50) | N | - | AI 模型 |
| response_time_ms | INT | N | 0 | 响应耗时 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |

---

## 3. 学习管理表

### 3.1 用户技能标签表 (user_skill_tag)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| skill_name | VARCHAR(50) | N | - | 技能名称 |
| proficiency_level | INT | N | 0 | 熟练度(0-100) |
| source | VARCHAR(20) | N | 'self_report' | 来源 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.2 用户学习偏好表 (user_preference)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| preferred_categories | JSON | Y | NULL | 偏好分类 |
| preferred_difficulty | INT | Y | NULL | 偏好难度 |
| learning_goal | VARCHAR(500) | Y | NULL | 学习目标 |
| weekly_hours | INT | N | 5 | 每周学习时长目标 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.3 课程相似度表 (course_similarity)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| course_id_a | BIGINT | N | - | 课程A ID |
| course_id_b | BIGINT | N | - | 课程B ID |
| similarity_score | DECIMAL(5,4) | N | 0.0000 | 相似度(0-1) |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.4 学习进度表 (learning_progress)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| enrollment_id | BIGINT | N | - | 报名记录ID |
| course_id | BIGINT | N | - | 课程ID |
| progress_percent | INT | N | 0 | 进度百分比 |
| study_duration_minutes | INT | N | 0 | 累计学习时长 |
| last_study_at | DATETIME | Y | NULL | 最后学习时间 |
| status | VARCHAR(20) | N | 'not_started' | 状态 |
| completed_at | DATETIME | Y | NULL | 完成时间 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.5 学习计划表 (learning_plan)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| plan_name | VARCHAR(100) | N | - | 计划名称 |
| description | VARCHAR(500) | Y | NULL | 计划描述 |
| target_courses | JSON | Y | NULL | 目标课程ID列表 |
| start_date | DATE | N | - | 开始日期 |
| end_date | DATE | N | - | 结束日期 |
| weekly_goal_hours | INT | N | 5 | 每周目标时长 |
| status | VARCHAR(20) | N | 'active' | 状态 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.6 学习打卡记录表 (study_checkin)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| checkin_date | DATE | N | - | 打卡日期 |
| study_minutes | INT | N | 0 | 学习时长(分钟) |
| courses_studied | JSON | Y | NULL | 学习的课程ID |
| note | VARCHAR(500) | Y | NULL | 学习笔记 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.7 成就定义表 (achievement)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| code | VARCHAR(50) | N | - | 成就编码 |
| name | VARCHAR(100) | N | - | 成就名称 |
| description | VARCHAR(200) | Y | NULL | 成就描述 |
| icon_url | VARCHAR(255) | Y | NULL | 图标URL |
| category | VARCHAR(50) | N | 'general' | 分类 |
| condition_type | VARCHAR(50) | N | - | 条件类型 |
| condition_value | INT | N | - | 条件值 |
| points | INT | N | 10 | 成就积分 |
| status | TINYINT | N | 1 | 状态 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

### 3.8 用户成就表 (user_achievement)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| achievement_id | BIGINT | N | - | 成就ID |
| earned_at | DATETIME | N | CURRENT_TIMESTAMP | 获得时间 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |

### 3.9 用户学习统计表 (user_learning_stats)

| 字段 | 类型 | 可空 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | N | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | N | - | 用户ID |
| total_study_minutes | INT | N | 0 | 总学习时长 |
| total_courses_enrolled | INT | N | 0 | 报名课程总数 |
| total_courses_completed | INT | N | 0 | 完成课程总数 |
| current_streak_days | INT | N | 0 | 当前连续天数 |
| max_streak_days | INT | N | 0 | 最长连续天数 |
| total_checkin_days | INT | N | 0 | 总打卡天数 |
| total_achievement_points | INT | N | 0 | 总成就积分 |
| last_study_date | DATE | Y | NULL | 最后学习日期 |
| created_at | DATETIME | N | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | N | CURRENT_TIMESTAMP | 更新时间 |

---

## 4. ER 图

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              ER 关系图                                       │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                              ┌──────────────┐                               │
│                              │   sys_user   │                               │
│                              └──────┬───────┘                               │
│                                     │                                       │
│     ┌───────────────┬───────────────┼───────────────┬───────────────┐       │
│     │               │               │               │               │       │
│     ▼               ▼               ▼               ▼               ▼       │
│ ┌────────┐   ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌──────────┐  │
│ │enroll- │   │class_      │  │ai_recommend│  │user_skill_ │  │user_     │  │
│ │ment    │   │session     │  │_log        │  │tag         │  │preference│  │
│ └────┬───┘   └─────┬──────┘  └────────────┘  └────────────┘  └──────────┘  │
│      │             │                                                        │
│      │             │ N                                                      │
│      │             │                                                        │
│      │             │ 1                                                      │
│      │             ▼                                                        │
│      │      ┌──────────────┐                                                │
│      └─────>│    course    │<─────┐                                         │
│             └──────────────┘      │                                         │
│                    │              │                                         │
│                    ▼              │                                         │
│             ┌──────────────┐      │                                         │
│             │course_       │──────┘                                         │
│             │similarity    │                                                │
│             └──────────────┘                                                │
│                                                                             │
│  学习管理表关系：                                                            │
│  • sys_user 1:N user_skill_tag                                              │
│  • sys_user 1:1 user_preference                                             │
│  • sys_user 1:N learning_progress                                           │
│  • sys_user 1:N learning_plan                                               │
│  • sys_user 1:N study_checkin                                               │
│  • sys_user 1:N user_achievement                                            │
│  • sys_user 1:1 user_learning_stats                                         │
│  • enrollment 1:1 learning_progress                                         │
│  • achievement 1:N user_achievement                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. 索引设计

### 5.1 关键查询与索引

| 查询场景 | SQL 条件 | 使用索引 |
|----------|----------|----------|
| 用户登录 | `WHERE username = ?` | `uk_username` |
| 课程列表 | `WHERE category = ? AND status = 1` | `idx_category`, `idx_status` |
| 报名查重 | `WHERE user_id = ? AND session_id = ?` | `idx_user_session` |
| 学习进度 | `WHERE user_id = ? AND status = ?` | `idx_user_id`, `idx_status` |
| 打卡记录 | `WHERE user_id = ? AND checkin_date = ?` | `uk_user_date` |
| 用户成就 | `WHERE user_id = ?` | `idx_user_id` |

---

## 6. 数据字典

### 6.1 角色枚举 (role)

| 值 | 说明 |
|----|------|
| ADMIN | 系统管理员 |
| INSTRUCTOR | 讲师 |
| STUDENT | 学员 |

### 6.2 课程分类 (category)

| 值 | 说明 |
|----|------|
| BACKEND | 后端开发 |
| FRONTEND | 前端开发 |
| DATABASE | 数据库 |
| CLOUD | 云计算 |
| AI | 人工智能 |
| OTHER | 其他 |

### 6.3 课程难度 (difficulty)

| 值 | 说明 |
|----|------|
| 1 | 入门 |
| 2 | 初级 |
| 3 | 中级 |
| 4 | 高级 |

### 6.4 班期状态 (class_session.status)

| 值 | 说明 |
|----|------|
| 0 | 未开放 |
| 1 | 报名中 |
| 2 | 已满员 |
| 3 | 进行中 |
| 4 | 已结束 |

### 6.5 学习进度状态 (learning_progress.status)

| 值 | 说明 |
|----|------|
| not_started | 未开始 |
| in_progress | 进行中 |
| completed | 已完成 |

### 6.6 学习计划状态 (learning_plan.status)

| 值 | 说明 |
|----|------|
| active | 进行中 |
| completed | 已完成 |
| paused | 已暂停 |
| canceled | 已取消 |

### 6.7 成就分类 (achievement.category)

| 值 | 说明 |
|----|------|
| general | 通用 |
| streak | 连续学习 |
| course | 课程完成 |
| skill | 技能 |

### 6.8 成就条件类型 (achievement.condition_type)

| 值 | 说明 |
|----|------|
| streak_days | 连续天数 |
| courses_completed | 完成课程数 |
| hours_studied | 学习时长 |

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | Architect | 初始版本 |
| v1.1.0 | 2025-12-24 | Architect | 新增学习管理模块表（9张表） |
