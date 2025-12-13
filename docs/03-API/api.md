# IT 技能培训智能选课系统 - REST API 接口文档

> 文档版本：v1.0.0
> 最后更新：2025-12-14
> 文档状态：✅ 已完成
> 基础路径：`/api`
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
8. [统计接口](#8-统计接口)
9. [错误码定义](#9-错误码定义)

---

## 1. 接口规范

### 1.1 基础信息

| 项目 | 值 |
|------|-----|
| 基础路径 | `/api` |
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

**POST** `/api/auth/register`

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

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|----------|
| username | String | Y | 3-50 字符，字母数字下划线 |
| password | String | Y | 6-20 字符 |
| confirmPassword | String | Y | 与 password 一致 |
| realName | String | Y | 2-50 字符 |
| phone | String | N | 手机号格式 |

**响应**：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

**错误码**：
- 4001: 用户名已存在
- 4002: 两次密码不一致

---

### 2.2 用户登录

**POST** `/api/auth/login`

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
      "role": "STUDENT",
      "avatar": null
    }
  }
}
```

**错误码**：
- 4003: 用户名或密码错误
- 4004: 账户已被禁用
- 4005: 账户已被锁定

---

### 2.3 刷新 Token

**POST** `/api/auth/refresh`

**权限**：需登录

**响应**：
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 86400
  }
}
```

---

### 2.4 获取当前用户信息

**GET** `/api/auth/me`

**权限**：需登录

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "student1",
    "realName": "张三",
    "phone": "13800138000",
    "email": null,
    "avatar": null,
    "role": "STUDENT"
  }
}
```

---

## 3. 用户管理接口

### 3.1 用户列表（分页）

**GET** `/api/users`

**权限**：ADMIN

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | N | 页码 |
| size | Integer | N | 每页条数 |
| keyword | String | N | 用户名/姓名搜索 |
| role | String | N | 角色筛选：ADMIN/INSTRUCTOR/STUDENT |
| status | Integer | N | 状态筛选：0/1 |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "phone": "13800138000",
        "email": "admin@example.com",
        "role": "ADMIN",
        "status": 1,
        "createdAt": "2025-01-01 00:00:00"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10
  }
}
```

---

### 3.2 新增用户

**POST** `/api/users`

**权限**：ADMIN

**请求体**：
```json
{
  "username": "teacher1",
  "password": "123456",
  "realName": "李老师",
  "phone": "13800138001",
  "email": "teacher@example.com",
  "role": "INSTRUCTOR"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 2
  }
}
```

---

### 3.3 获取用户详情

**GET** `/api/users/{id}`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 2,
    "username": "teacher1",
    "realName": "李老师",
    "phone": "13800138001",
    "email": "teacher@example.com",
    "avatar": null,
    "role": "INSTRUCTOR",
    "status": 1,
    "createdAt": "2025-01-01 00:00:00",
    "updatedAt": "2025-01-01 00:00:00"
  }
}
```

---

### 3.4 更新用户

**PUT** `/api/users/{id}`

**权限**：ADMIN

**请求体**：
```json
{
  "realName": "李老师（高级）",
  "phone": "13800138001",
  "email": "teacher@example.com",
  "role": "INSTRUCTOR",
  "status": 1
}
```

**响应**：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 3.5 删除用户

**DELETE** `/api/users/{id}`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误码**：
- 4010: 不能删除自己

---

### 3.6 获取讲师列表

**GET** `/api/users/instructors`

**权限**：ADMIN

**说明**：用于班期创建时选择讲师

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 2,
      "realName": "李老师"
    },
    {
      "id": 3,
      "realName": "王老师"
    }
  ]
}
```

---

## 4. 课程管理接口

### 4.1 课程列表（分页）

**GET** `/api/courses`

**权限**：ADMIN（全部）、STUDENT（仅上架）

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | N | 页码 |
| size | Integer | N | 每页条数 |
| keyword | String | N | 课程名称搜索 |
| category | String | N | 分类筛选 |
| difficulty | Integer | N | 难度筛选：1-4 |
| status | Integer | N | 状态筛选：0/1（仅 ADMIN） |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "code": "JAVA-001",
        "name": "Java 基础入门",
        "category": "BACKEND",
        "coverImage": "/images/java.png",
        "difficulty": 1,
        "difficultyText": "入门",
        "durationHours": 40,
        "tags": "Java,入门,编程基础",
        "status": 1,
        "sessionCount": 3,
        "enrollmentCount": 50
      }
    ],
    "total": 20,
    "page": 1,
    "size": 10,
    "pages": 2
  }
}
```

---

### 4.2 课程详情

**GET** `/api/courses/{id}`

**权限**：需登录

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "code": "JAVA-001",
    "name": "Java 基础入门",
    "category": "BACKEND",
    "categoryText": "后端开发",
    "description": "Java 语言基础...",
    "coverImage": "/images/java.png",
    "difficulty": 1,
    "difficultyText": "入门",
    "durationHours": 40,
    "prerequisites": null,
    "tags": "Java,入门,编程基础",
    "status": 1,
    "createdAt": "2025-01-01 00:00:00",
    "updatedAt": "2025-01-01 00:00:00",
    "sessions": [
      {
        "id": 1,
        "sessionCode": "JAVA-001-2025S1",
        "instructorName": "李老师",
        "startDate": "2025-01-15",
        "endDate": "2025-03-15",
        "schedule": "每周六 9:00-12:00",
        "maxCapacity": 30,
        "currentEnrollment": 10,
        "remainingQuota": 20,
        "status": 1,
        "statusText": "报名中"
      }
    ]
  }
}
```

---

### 4.3 新增课程

**POST** `/api/courses`

**权限**：ADMIN

**请求体**：
```json
{
  "code": "JAVA-001",
  "name": "Java 基础入门",
  "category": "BACKEND",
  "description": "Java 语言基础...",
  "coverImage": "/images/java.png",
  "difficulty": 1,
  "durationHours": 40,
  "prerequisites": null,
  "tags": "Java,入门,编程基础"
}
```

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|----------|
| code | String | Y | 格式：XXX-NNN |
| name | String | Y | 2-100 字符 |
| category | String | Y | 枚举值 |
| description | String | N | - |
| difficulty | Integer | Y | 1-4 |
| durationHours | Integer | Y | > 0 |

**响应**：
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

**错误码**：
- 4020: 课程编码已存在

---

### 4.4 更新课程

**PUT** `/api/courses/{id}`

**权限**：ADMIN

**请求体**：
```json
{
  "name": "Java 基础入门（第2版）",
  "category": "BACKEND",
  "description": "更新后的描述...",
  "difficulty": 1,
  "durationHours": 45,
  "tags": "Java,入门"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

### 4.5 删除课程

**DELETE** `/api/courses/{id}`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误码**：
- 4021: 该课程下存在班期，无法删除

---

### 4.6 上架课程

**PUT** `/api/courses/{id}/publish`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "上架成功",
  "data": null
}
```

---

### 4.7 下架课程

**PUT** `/api/courses/{id}/unpublish`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "下架成功",
  "data": null
}
```

---

## 5. 班期管理接口

### 5.1 班期列表（分页）

**GET** `/api/sessions`

**权限**：ADMIN（全部）、INSTRUCTOR（自己的）、STUDENT（报名中的）

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | N | 页码 |
| size | Integer | N | 每页条数 |
| courseId | Long | N | 课程 ID 筛选 |
| status | Integer | N | 状态筛选：0-4 |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "courseId": 1,
        "courseName": "Java 基础入门",
        "courseCode": "JAVA-001",
        "instructorId": 2,
        "instructorName": "李老师",
        "sessionCode": "JAVA-001-2025S1",
        "startDate": "2025-01-15",
        "endDate": "2025-03-15",
        "schedule": "每周六 9:00-12:00",
        "location": "线上直播",
        "maxCapacity": 30,
        "currentEnrollment": 10,
        "remainingQuota": 20,
        "status": 1,
        "statusText": "报名中"
      }
    ],
    "total": 10,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 5.2 班期详情

**GET** `/api/sessions/{id}`

**权限**：需登录

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "courseId": 1,
    "courseName": "Java 基础入门",
    "courseCode": "JAVA-001",
    "instructorId": 2,
    "instructorName": "李老师",
    "sessionCode": "JAVA-001-2025S1",
    "startDate": "2025-01-15",
    "endDate": "2025-03-15",
    "schedule": "每周六 9:00-12:00",
    "location": "线上直播",
    "maxCapacity": 30,
    "currentEnrollment": 10,
    "remainingQuota": 20,
    "status": 1,
    "statusText": "报名中",
    "createdAt": "2025-01-01 00:00:00"
  }
}
```

---

### 5.3 新增班期

**POST** `/api/sessions`

**权限**：ADMIN

**请求体**：
```json
{
  "courseId": 1,
  "instructorId": 2,
  "sessionCode": "JAVA-001-2025S1",
  "startDate": "2025-01-15",
  "endDate": "2025-03-15",
  "schedule": "每周六 9:00-12:00",
  "location": "线上直播",
  "maxCapacity": 30
}
```

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|----------|
| courseId | Long | Y | 存在的课程 |
| instructorId | Long | Y | 存在的讲师 |
| sessionCode | String | Y | 格式唯一 |
| startDate | Date | Y | > 当前日期 |
| endDate | Date | Y | >= startDate |
| maxCapacity | Integer | Y | > 0 |

**响应**：
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1
  }
}
```

**错误码**：
- 4030: 班期编码已存在
- 4031: 开始日期必须大于当前日期

---

### 5.4 更新班期

**PUT** `/api/sessions/{id}`

**权限**：ADMIN

**请求体**：
```json
{
  "instructorId": 3,
  "startDate": "2025-01-20",
  "endDate": "2025-03-20",
  "schedule": "每周日 9:00-12:00",
  "location": "线上直播",
  "maxCapacity": 35,
  "status": 1
}
```

**响应**：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

**错误码**：
- 4032: 最大名额不能小于当前报名人数

---

### 5.5 删除班期

**DELETE** `/api/sessions/{id}`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误码**：
- 4033: 该班期已有学员报名，无法删除

---

### 5.6 班期学员名单

**GET** `/api/sessions/{id}/students`

**权限**：ADMIN、INSTRUCTOR（仅自己的班期）

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "enrollmentId": 1,
      "userId": 5,
      "username": "student1",
      "realName": "张三",
      "phone": "13900139000",
      "enrolledAt": "2025-01-10 10:00:00",
      "status": 0,
      "statusText": "已报名"
    }
  ]
}
```

---

## 6. 报名管理接口

### 6.1 报名列表（分页）

**GET** `/api/enrollments`

**权限**：ADMIN（全部）、STUDENT（自己的）

**请求参数**：
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | Integer | N | 页码 |
| size | Integer | N | 每页条数 |
| courseId | Long | N | 课程筛选（ADMIN） |
| sessionId | Long | N | 班期筛选（ADMIN） |
| userId | Long | N | 学员筛选（ADMIN） |
| status | Integer | N | 状态筛选：0/1 |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 5,
        "userName": "张三",
        "sessionId": 1,
        "sessionCode": "JAVA-001-2025S1",
        "courseId": 1,
        "courseName": "Java 基础入门",
        "instructorName": "李老师",
        "startDate": "2025-01-15",
        "endDate": "2025-03-15",
        "schedule": "每周六 9:00-12:00",
        "status": 0,
        "statusText": "已报名",
        "enrolledAt": "2025-01-10 10:00:00",
        "canceledAt": null,
        "cancelReason": null
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

---

### 6.2 创建报名

**POST** `/api/enrollments`

**权限**：STUDENT

**请求体**：
```json
{
  "sessionId": 1
}
```

**响应（成功）**：
```json
{
  "code": 200,
  "message": "报名成功",
  "data": {
    "id": 1
  }
}
```

**错误码**：
- 4001: 名额不足
- 4002: 重复报名
- 4003: 班期不可报名

---

### 6.3 取消报名

**PUT** `/api/enrollments/{id}/cancel`

**权限**：STUDENT（自己的）、ADMIN

**请求体**：
```json
{
  "reason": "时间冲突"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "取消成功",
  "data": null
}
```

**错误码**：
- 4004: 状态不允许取消
- 4005: 班期已开始，无法取消

---

### 6.4 报名详情

**GET** `/api/enrollments/{id}`

**权限**：STUDENT（自己的）、ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "userId": 5,
    "userName": "张三",
    "sessionId": 1,
    "sessionCode": "JAVA-001-2025S1",
    "courseId": 1,
    "courseName": "Java 基础入门",
    "instructorName": "李老师",
    "startDate": "2025-01-15",
    "endDate": "2025-03-15",
    "schedule": "每周六 9:00-12:00",
    "location": "线上直播",
    "status": 0,
    "statusText": "已报名",
    "enrolledAt": "2025-01-10 10:00:00",
    "canceledAt": null,
    "cancelReason": null
  }
}
```

---

## 7. AI 推荐接口

### 7.1 获取课程推荐

**POST** `/api/ai/recommend`

**权限**：STUDENT

**请求体**：
```json
{
  "input": "想学后端开发，目前零基础"
}
```

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|----------|
| input | String | Y | 5-500 字符 |

**响应（正常）**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "courses": [
      {
        "id": 1,
        "code": "JAVA-001",
        "name": "Java 基础入门",
        "category": "BACKEND",
        "difficulty": 1,
        "order": 1
      },
      {
        "id": 2,
        "code": "JAVA-002",
        "name": "Spring Boot 实战",
        "category": "BACKEND",
        "difficulty": 3,
        "order": 2
      }
    ],
    "reason": "根据您的学习目标，建议从 Java 基础入门开始，掌握语言核心后再学习 Spring Boot 框架。",
    "learningOrder": ["JAVA-001", "JAVA-002"],
    "fallback": false
  }
}
```

**响应（降级）**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "courses": [ ... ],
    "reason": "AI 服务暂时不可用，为您推荐热门课程",
    "learningOrder": [...],
    "fallback": true
  }
}
```

---

## 8. 统计接口

### 8.1 数据概览

**GET** `/api/statistics/overview`

**权限**：ADMIN

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCourses": 20,
    "totalSessions": 50,
    "totalStudents": 500,
    "totalEnrollments": 1200,
    "activeEnrollments": 800
  }
}
```

---

### 8.2 课程热度排行

**GET** `/api/statistics/course-ranking`

**权限**：ADMIN

**请求参数**：
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| limit | Integer | N | 10 | 返回数量 |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "courseId": 1,
      "courseName": "Java 基础入门",
      "enrollmentCount": 150
    },
    {
      "courseId": 2,
      "courseName": "Spring Boot 实战",
      "enrollmentCount": 120
    }
  ]
}
```

---

### 8.3 报名趋势

**GET** `/api/statistics/enrollment-trend`

**权限**：ADMIN

**请求参数**：
| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| days | Integer | N | 7 | 天数：7 或 30 |

**响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "date": "2025-01-01",
      "count": 15
    },
    {
      "date": "2025-01-02",
      "count": 22
    }
  ]
}
```

---

## 9. 错误码定义

### 9.1 通用错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 9.2 业务错误码

| 错误码 | 模块 | 说明 |
|--------|------|------|
| 4001 | 认证 | 用户名已存在 |
| 4002 | 认证 | 两次密码不一致 |
| 4003 | 认证 | 用户名或密码错误 |
| 4004 | 认证 | 账户已被禁用 |
| 4005 | 认证 | 账户已被锁定 |
| 4010 | 用户 | 不能删除自己 |
| 4020 | 课程 | 课程编码已存在 |
| 4021 | 课程 | 该课程下存在班期，无法删除 |
| 4030 | 班期 | 班期编码已存在 |
| 4031 | 班期 | 开始日期必须大于当前日期 |
| 4032 | 班期 | 最大名额不能小于当前报名人数 |
| 4033 | 班期 | 该班期已有学员报名，无法删除 |
| 4040 | 报名 | 名额不足 |
| 4041 | 报名 | 重复报名 |
| 4042 | 报名 | 班期不可报名 |
| 4043 | 报名 | 状态不允许取消 |
| 4044 | 报名 | 班期已开始，无法取消 |

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | Architect | 初始版本 |
