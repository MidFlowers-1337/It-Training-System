# IT 技能培训智能选课系统 - REST API 接口文档

> 文档版本：v1.1.0
> 最后更新：2025-12-24
> 文档状态：✅ 已完成
> 基础路径：`/api/v1`
> 关联文档：[Architecture.md](../02-Architecture/Architecture.md) | [schema.md](../04-DB/schema.md)

---

## 目录

1. [接口规范](#1-接口规范)
2. [认证接口](#2-认证接口)
3. [用户管理接口](#3-用户管理接口)
4. [课程管理接口](#4-课程管理接口)
5. [班期管理接口](#5-班期管理接口)
6. [报名管理接口](#6-报名管理接口)
7. [AI 推荐接口](#7-ai-推荐接口)
8. [学习管理接口](#8-学习管理接口)
9. [学员专属接口](#9-学员专属接口)
10. [统计接口](#10-统计接口)
11. [错误码定义](#11-错误码定义)

---

## 1. 接口规范

### 1.1 基础信息

| 项目 | 值 |
|------|-----|
| 基础路径 | `/api/v1` |
| 协议 | HTTP/HTTPS |
| 数据格式 | JSON |
| 字符编码 | UTF-8 |
| 认证方式 | JWT Bearer Token |

### 1.2 请求头

| Header | 必填 | 说明 |
|--------|------|------|
| Content-Type | Y | `application/json` |
| Authorization | N* | `Bearer {token}`（需认证接口必填） |

### 1.3 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码，200 成功，其他为错误码 |
| message | String | 提示信息 |
| data | Object/Array | 响应数据（可为 null） |

### 1.4 分页请求参数

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| page | Integer | N | 1 | 页码（从 1 开始） |
| size | Integer | N | 10 | 每页条数（最大 100） |
| keyword | String | N | - | 搜索关键词 |

### 1.5 分页响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [ ... ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  }
}
```

---

## 2. 认证接口

### 2.1 用户注册

**POST** `/api/v1/auth/register`

**权限**：匿名

**请求体**：
```json
{
  "username": "student1",
  "password": "123456",
  "confirmPassword": "123456",
  "realName": "张三",
  "phone": "13800138000"
}
```

### 2.2 用户登录

**POST** `/api/v1/auth/login`

**权限**：匿名

**请求体**：
```json
{
  "username": "student1",
  "password": "123456"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "user": {
      "id": 1,
      "username": "student1",
      "realName": "张三",
      "role": "STUDENT"
    }
  }
}
```

### 2.3 刷新 Token

**POST** `/api/v1/auth/refresh`

**权限**：需登录

### 2.4 获取当前用户信息

**GET** `/api/v1/auth/me`

**权限**：需登录

---

## 3. 用户管理接口

### 3.1 用户列表（分页）

**GET** `/api/v1/users`

**权限**：ADMIN

### 3.2 新增用户

**POST** `/api/v1/users`

**权限**：ADMIN

### 3.3 获取用户详情

**GET** `/api/v1/users/{id}`

**权限**：ADMIN

### 3.4 更新用户

**PUT** `/api/v1/users/{id}`

**权限**：ADMIN

### 3.5 删除用户

**DELETE** `/api/v1/users/{id}`

**权限**：ADMIN

### 3.6 获取讲师列表

**GET** `/api/v1/users/instructors`

**权限**：ADMIN

---

## 4. 课程管理接口

### 4.1 课程列表（分页）

**GET** `/api/v1/courses`

**权限**：ADMIN（全部）、STUDENT（仅上架）

### 4.2 课程详情

**GET** `/api/v1/courses/{id}`

**权限**：需登录

### 4.3 新增课程

**POST** `/api/v1/courses`

**权限**：ADMIN

### 4.4 更新课程

**PUT** `/api/v1/courses/{id}`

**权限**：ADMIN

### 4.5 删除课程

**DELETE** `/api/v1/courses/{id}`

**权限**：ADMIN

### 4.6 上架课程

**PUT** `/api/v1/courses/{id}/publish`

**权限**：ADMIN

### 4.7 下架课程

**PUT** `/api/v1/courses/{id}/unpublish`

**权限**：ADMIN

---

## 5. 班期管理接口

### 5.1 班期列表（分页）

**GET** `/api/v1/sessions`

**权限**：ADMIN（全部）、INSTRUCTOR（自己的）、STUDENT（报名中的）

### 5.2 班期详情

**GET** `/api/v1/sessions/{id}`

**权限**：需登录

### 5.3 新增班期

**POST** `/api/v1/sessions`

**权限**：ADMIN

### 5.4 更新班期

**PUT** `/api/v1/sessions/{id}`

**权限**：ADMIN

### 5.5 删除班期

**DELETE** `/api/v1/sessions/{id}`

**权限**：ADMIN

### 5.6 班期学员名单

**GET** `/api/v1/sessions/{id}/students`

**权限**：ADMIN、INSTRUCTOR（仅自己的班期）

---

## 6. 报名管理接口

### 6.1 报名列表（分页）

**GET** `/api/v1/enrollments`

**权限**：ADMIN（全部）、STUDENT（自己的）

### 6.2 创建报名

**POST** `/api/v1/enrollments`

**权限**：STUDENT

**请求体**：
```json
{
  "sessionId": 1
}
```

### 6.3 取消报名

**PUT** `/api/v1/enrollments/{id}/cancel`

**权限**：STUDENT（自己的）、ADMIN

### 6.4 报名详情

**GET** `/api/v1/enrollments/{id}`

**权限**：STUDENT（自己的）、ADMIN

---

## 7. AI 推荐接口

### 7.1 获取课程推荐

**POST** `/api/v1/ai/recommend`

**权限**：STUDENT

**请求体**：
```json
{
  "input": "想学后端开发，目前零基础"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "courses": [...],
    "reason": "根据您的学习目标...",
    "learningOrder": ["JAVA-001", "JAVA-002"],
    "fallback": false
  }
}
```

---

## 8. 学习管理接口

> 基础路径：`/api/v1/learning`
> 权限：STUDENT

### 8.1 学习仪表盘

#### 获取学习仪表盘

**GET** `/api/v1/learning/dashboard`

**响应**：
```json
{
  "code": 200,
  "data": {
    "totalCourses": 5,
    "completedCourses": 2,
    "inProgressCourses": 3,
    "totalStudyHours": 120,
    "currentStreak": 7,
    "todayCheckedIn": true
  }
}
```

#### 获取用户学习统计

**GET** `/api/v1/learning/stats`

### 8.2 学习进度

#### 获取所有课程学习进度

**GET** `/api/v1/learning/progress`

#### 获取指定课程学习进度

**GET** `/api/v1/learning/progress/course/{courseId}`

#### 更新学习进度

**POST** `/api/v1/learning/progress/update`

**请求体**：
```json
{
  "courseId": 1,
  "chapterId": 5,
  "progress": 75,
  "studyDuration": 30
}
```

#### 标记课程完成

**POST** `/api/v1/learning/progress/complete/{courseId}`

#### 获取进行中的课程

**GET** `/api/v1/learning/progress/in-progress`

### 8.3 学习打卡

#### 学习打卡

**POST** `/api/v1/learning/checkin`

**请求体**：
```json
{
  "studyDuration": 60,
  "courseId": 1,
  "note": "今天学习了Java基础"
}
```

#### 检查今日是否已打卡

**GET** `/api/v1/learning/checkin/today`

#### 获取今日打卡详情

**GET** `/api/v1/learning/checkin/today/detail`

#### 获取打卡历史

**GET** `/api/v1/learning/checkin/history`

**参数**：
- `startDate`: 开始日期 (ISO 格式)
- `endDate`: 结束日期 (ISO 格式)

#### 获取当前连续打卡天数

**GET** `/api/v1/learning/checkin/streak`

#### 获取月度打卡日历

**GET** `/api/v1/learning/checkin/calendar/{year}/{month}`

### 8.4 成就系统

#### 获取所有成就（含获得状态）

**GET** `/api/v1/learning/achievements`

#### 获取已获得的成就

**GET** `/api/v1/learning/achievements/earned`

#### 获取最近获得的成就

**GET** `/api/v1/learning/achievements/recent`

**参数**：
- `limit`: 返回数量，默认 5

#### 获取成就详情

**GET** `/api/v1/learning/achievements/{achievementId}`

#### 获取成就积分

**GET** `/api/v1/learning/achievements/points`

### 8.5 学习计划

#### 创建学习计划

**POST** `/api/v1/learning/plans`

**请求体**：
```json
{
  "name": "Java学习计划",
  "courseIds": [1, 2, 3],
  "startDate": "2025-01-01",
  "endDate": "2025-03-31",
  "dailyStudyGoal": 60
}
```

#### 获取所有学习计划

**GET** `/api/v1/learning/plans`

#### 获取当前进行中的学习计划

**GET** `/api/v1/learning/plans/active`

#### 获取学习计划详情

**GET** `/api/v1/learning/plans/{planId}`

#### 更新学习计划

**PUT** `/api/v1/learning/plans/{planId}`

#### 暂停学习计划

**POST** `/api/v1/learning/plans/{planId}/pause`

#### 恢复学习计划

**POST** `/api/v1/learning/plans/{planId}/resume`

#### 取消学习计划

**POST** `/api/v1/learning/plans/{planId}/cancel`

#### 完成学习计划

**POST** `/api/v1/learning/plans/{planId}/complete`

### 8.6 学习报告

#### 获取周报

**GET** `/api/v1/learning/reports/weekly`

**参数**：
- `weekStart`: 周开始日期 (ISO 格式)

#### 获取月报

**GET** `/api/v1/learning/reports/monthly/{year}/{month}`

#### 获取年报

**GET** `/api/v1/learning/reports/yearly/{year}`

#### 获取自定义时间段报告

**GET** `/api/v1/learning/reports/custom`

**参数**：
- `startDate`: 开始日期
- `endDate`: 结束日期

### 8.7 智能推荐

#### 基于学习历史推荐课程

**GET** `/api/v1/learning/recommend/history`

**参数**：
- `limit`: 返回数量，默认 10

#### 推荐相似课程

**GET** `/api/v1/learning/recommend/similar/{courseId}`

**参数**：
- `limit`: 返回数量，默认 5

#### 基于技能标签推荐课程

**GET** `/api/v1/learning/recommend/skills`

#### 基于用户偏好推荐课程

**GET** `/api/v1/learning/recommend/preference`

### 8.8 协同过滤推荐

#### 基于用户的协同过滤推荐

**GET** `/api/v1/learning/recommend/user-based`

#### 基于物品的协同过滤推荐

**GET** `/api/v1/learning/recommend/item-based`

#### 获取相似用户

**GET** `/api/v1/learning/recommend/similar-users`

### 8.9 混合推荐

#### 获取混合推荐课程

**GET** `/api/v1/learning/recommend/hybrid`

#### 获取首页推荐课程

**GET** `/api/v1/learning/recommend/home`

#### 获取猜你喜欢课程

**GET** `/api/v1/learning/recommend/you-may-like`

#### 获取热门课程

**GET** `/api/v1/learning/recommend/popular`

#### 获取新上架课程

**GET** `/api/v1/learning/recommend/new`

### 8.10 用户画像

#### 获取用户画像

**GET** `/api/v1/learning/profile`

**响应**：
```json
{
  "code": 200,
  "data": {
    "skillTags": ["Java", "Spring Boot"],
    "preferredCategories": ["BACKEND"],
    "preferredDifficulty": 2,
    "dailyStudyGoal": 60,
    "learningStyle": "视频学习"
  }
}
```

#### 更新技能标签

**POST** `/api/v1/learning/profile/skills`

**请求体**：
```json
["Java", "Spring Boot", "MySQL"]
```

#### 更新学习偏好

**POST** `/api/v1/learning/profile/preferences`

**请求体**：
```json
{
  "preferredCategories": ["BACKEND", "DATABASE"],
  "preferredDifficulty": 2,
  "dailyStudyGoal": 90
}
```

#### 获取学习能力评估

**GET** `/api/v1/learning/profile/ability-assessment`

#### 分析学习行为

**POST** `/api/v1/learning/profile/analyze`

---

## 9. 学员专属接口

> 基础路径：`/api/v1/student`
> 权限：STUDENT

### 9.1 获取学生 Dashboard 数据

**GET** `/api/v1/student/dashboard`

**响应**：
```json
{
  "code": 200,
  "data": {
    "enrolledCourses": 5,
    "completedCourses": 2,
    "totalStudyHours": 120,
    "currentStreak": 7,
    "level": 3,
    "levelName": "进阶学员",
    "experiencePoints": 1500,
    "nextLevelPoints": 2000
  }
}
```

### 9.2 获取学习统计

**GET** `/api/v1/student/stats`

### 9.3 获取我的成就列表

**GET** `/api/v1/student/achievements`

---

## 10. 统计接口

### 10.1 数据概览

**GET** `/api/v1/statistics/overview`

**权限**：ADMIN

### 10.2 课程热度排行

**GET** `/api/v1/statistics/course-ranking`

**权限**：ADMIN

### 10.3 报名趋势

**GET** `/api/v1/statistics/enrollment-trend`

**权限**：ADMIN

---

## 11. 错误码定义

### 11.1 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 11.2 业务错误码

| 错误码 | 模块 | 说明 |
|--------|------|------|
| 4001 | 认证 | 用户名已存在 |
| 4002 | 认证 | 两次密码不一致 |
| 4003 | 认证 | 用户名或密码错误 |
| 4004 | 认证 | 账户已被禁用 |
| 4010 | 用户 | 不能删除自己 |
| 4020 | 课程 | 课程编码已存在 |
| 4021 | 课程 | 该课程下存在班期，无法删除 |
| 4030 | 班期 | 班期编码已存在 |
| 4031 | 班期 | 开始日期必须大于当前日期 |
| 4040 | 报名 | 名额不足 |
| 4041 | 报名 | 重复报名 |
| 4042 | 报名 | 班期不可报名 |
| 4050 | 学习 | 学习计划不存在 |
| 4051 | 学习 | 今日已打卡 |
| 4052 | 学习 | 课程进度不存在 |

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | Architect | 初始版本 |
| v1.1.0 | 2025-12-24 | Architect | 新增学习管理接口、学员专属接口 |
