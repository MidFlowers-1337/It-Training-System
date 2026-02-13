# IT Training System - 前后端 API 对接文档

> **Base URL**: `/api/v1`
> **认证方式**: JWT Bearer Token（在 Header 中携带 `Authorization: Bearer <token>`）
> **通用响应格式**: `R<T>` 结构

---

## 通用响应体 `R<T>`

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1700000000000
}
```

| 字段        | 类型     | 说明              |
| ----------- | -------- | ----------------- |
| `code`      | `int`    | 200=成功，其他=失败 |
| `message`   | `String` | 响应消息            |
| `data`      | `T`      | 泛型数据体          |
| `timestamp` | `long`   | 时间戳              |

---

## 权限说明

| 标记            | 含义                                 |
| --------------- | ------------------------------------ |
| `Public`        | 无需认证即可访问                       |
| `Authenticated` | 需要登录（任何角色）                    |
| `ADMIN`         | 仅管理员可访问                         |
| `STUDENT`       | 仅学生可访问                           |
| `INSTRUCTOR`    | 仅讲师可访问                           |

---

## 目录

1. [认证模块 Auth](#1-认证模块-auth)
2. [用户管理 Users（ADMIN）](#2-用户管理-usersadmin)
3. [个人中心 Profile](#3-个人中心-profile)
4. [课程管理 Courses](#4-课程管理-courses)
5. [班期管理 Sessions](#5-班期管理-sessions)
6. [报名管理 Enrollments](#6-报名管理-enrollments)
7. [学习进度 Learning Progress](#7-学习进度-learning-progress)
8. [学习打卡 Checkin](#8-学习打卡-checkin)
9. [成就系统 Achievements](#9-成就系统-achievements)
10. [学习计划 Plans](#10-学习计划-plans)
11. [智能推荐 Recommend](#11-智能推荐-recommend)
12. [学习报告 Reports](#12-学习报告-reports)
13. [用户画像 Profile（学习）](#13-用户画像-profile学习)
14. [学生端 Student](#14-学生端-student)
15. [统计看板 Stats（ADMIN）](#15-统计看板-statsadmin)
16. [AI 智能推荐](#16-ai-智能推荐)
17. [AI 服务测试（ADMIN）](#17-ai-服务测试admin)
18. [通知中心 Notifications](#18-通知中心-notifications)
19. [课程评价 Reviews](#19-课程评价-reviews)
20. [公开接口 Public](#20-公开接口-public)
21. [成就管理 Achievement Admin（ADMIN）](#21-成就管理-achievement-adminadmin)

---

## 1. 认证模块 Auth

**Base Path**: `/api/v1/auth`

### 1.1 用户登录

```
POST /api/v1/auth/login           [Public]
```

**Request Body** (`LoginRequest`):

| 字段       | 类型     | 必填 | 说明   |
| ---------- | -------- | ---- | ------ |
| `username` | `String` | 是   | 用户名 |
| `password` | `String` | 是   | 密码   |

**Response** (`TokenResponse`):

| 字段               | 类型     | 说明                          |
| ------------------ | -------- | ----------------------------- |
| `accessToken`      | `String` | Access Token                  |
| `refreshToken`     | `String` | Refresh Token                 |
| `tokenType`        | `String` | 令牌类型（固定 `Bearer`）       |
| `expiresIn`        | `Long`   | Access Token 过期时间（秒）     |
| `refreshExpiresIn` | `Long`   | Refresh Token 过期时间（秒）    |
| `userId`           | `Long`   | 用户ID                         |
| `username`         | `String` | 用户名                         |
| `realName`         | `String` | 真实姓名                       |
| `role`             | `String` | 角色（ADMIN/INSTRUCTOR/STUDENT）|

---

### 1.2 用户注册

```
POST /api/v1/auth/register        [Public]
```

**Request Body** (`RegisterRequest`):

| 字段              | 类型     | 必填 | 说明     |
| ----------------- | -------- | ---- | -------- |
| `username`        | `String` | 是   | 用户名   |
| `password`        | `String` | 是   | 密码     |
| `confirmPassword` | `String` | 是   | 确认密码 |
| `realName`        | `String` | 是   | 真实姓名 |
| `phone`           | `String` | 否   | 手机号   |
| `email`           | `String` | 否   | 邮箱     |

**Response**: `TokenResponse`（同登录）

---

### 1.3 获取当前用户信息

```
GET /api/v1/auth/me               [Authenticated]
```

**Response**: `TokenResponse`

---

### 1.4 刷新 Token

```
POST /api/v1/auth/refresh         [Public]
```

**Request Body** (`RefreshTokenRequest`):

| 字段           | 类型     | 必填 | 说明          |
| -------------- | -------- | ---- | ------------- |
| `refreshToken` | `String` | 是   | Refresh Token |

**Response**: `TokenResponse`

---

### 1.5 用户登出

```
POST /api/v1/auth/logout          [Authenticated]
```

**Response**: `R<Void>`（无数据体）

---

## 2. 用户管理 Users（ADMIN）

**Base Path**: `/api/v1/users`

> 除 `GET /api/v1/users/{id}` 外，所有接口均需要 **ADMIN** 角色

### 2.1 分页查询用户列表

```
GET /api/v1/users                 [ADMIN]
```

**Query Params**:

| 参数      | 类型      | 默认值 | 说明       |
| --------- | --------- | ------ | ---------- |
| `page`    | `int`     | `1`    | 页码       |
| `size`    | `int`     | `10`   | 每页大小   |
| `role`    | `String`  | -      | 角色过滤   |
| `keyword` | `String`  | -      | 搜索关键词 |

**Response**: `IPage<UserResponse>`

```json
{
  "records": [ ... ],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10
}
```

**`UserResponse`**:

| 字段        | 类型            | 说明                     |
| ----------- | --------------- | ------------------------ |
| `id`        | `Long`          | 用户ID                   |
| `username`  | `String`        | 用户名                   |
| `realName`  | `String`        | 真实姓名                 |
| `phone`     | `String`        | 手机号                   |
| `email`     | `String`        | 邮箱                     |
| `avatar`    | `String`        | 头像URL                  |
| `role`      | `String`        | 角色                     |
| `status`    | `Integer`       | 状态 (0-禁用, 1-启用)     |
| `createdAt` | `LocalDateTime` | 创建时间                 |
| `updatedAt` | `LocalDateTime` | 更新时间                 |

---

### 2.2 根据 ID 获取用户

```
GET /api/v1/users/{id}            [ADMIN]
```

**Path Params**: `id` - 用户ID

**Response**: `UserResponse`

---

### 2.3 创建用户

```
POST /api/v1/users                [ADMIN]
```

**Request Body** (`UserCreateRequest`):

| 字段       | 类型     | 必填 | 说明                              |
| ---------- | -------- | ---- | --------------------------------- |
| `username` | `String` | 是   | 用户名                            |
| `password` | `String` | 是   | 密码                              |
| `realName` | `String` | 是   | 真实姓名                          |
| `phone`    | `String` | 否   | 手机号                            |
| `email`    | `String` | 否   | 邮箱                              |
| `role`     | `String` | 是   | 角色 (ADMIN/INSTRUCTOR/STUDENT)   |

**Response**: `UserResponse`

---

### 2.4 更新用户

```
PUT /api/v1/users/{id}            [ADMIN]
```

**Request Body** (`UserUpdateRequest`):

| 字段       | 类型      | 必填 | 说明                    |
| ---------- | --------- | ---- | ----------------------- |
| `realName` | `String`  | 否   | 真实姓名                |
| `phone`    | `String`  | 否   | 手机号                  |
| `email`    | `String`  | 否   | 邮箱                    |
| `avatar`   | `String`  | 否   | 头像URL                 |
| `role`     | `String`  | 否   | 角色                    |
| `status`   | `Integer` | 否   | 状态 (0-禁用, 1-启用)    |

**Response**: `UserResponse`

---

### 2.5 删除用户

```
DELETE /api/v1/users/{id}         [ADMIN]
```

**Response**: `R<Void>`

---

### 2.6 重置用户密码

```
PATCH /api/v1/users/{id}/password [ADMIN]
```

**Request Body** (`PasswordResetRequest`):

| 字段          | 类型     | 必填 | 说明   |
| ------------- | -------- | ---- | ------ |
| `newPassword` | `String` | 是   | 新密码 |

**Response**: `R<Void>`

---

### 2.7 更新用户状态

```
PATCH /api/v1/users/{id}/status   [ADMIN]
```

**Query Params**:

| 参数     | 类型      | 必填 | 说明                   |
| -------- | --------- | ---- | ---------------------- |
| `status` | `Integer` | 是   | 状态: 0-禁用, 1-启用    |

**Response**: `R<Void>`

---

## 3. 个人中心 Profile

**Base Path**: `/api/v1/profile`

> 所有接口均需要 **Authenticated**

### 3.1 获取当前用户信息

```
GET /api/v1/profile               [Authenticated]
```

**Response**: `UserResponse`

---

### 3.2 更新个人资料

```
PUT /api/v1/profile               [Authenticated]
```

**Request Body** (`ProfileUpdateRequest`):

| 字段       | 类型     | 必填 | 说明     |
| ---------- | -------- | ---- | -------- |
| `realName` | `String` | 否   | 真实姓名 |
| `email`    | `String` | 否   | 邮箱     |
| `phone`    | `String` | 否   | 手机号   |
| `avatar`   | `String` | 否   | 头像URL  |
| `bio`      | `String` | 否   | 个人简介 |

**Response**: `UserResponse`

---

### 3.3 修改密码

```
POST /api/v1/profile/password     [Authenticated]
```

**Request Body** (`ChangePasswordRequest`):

| 字段              | 类型     | 必填 | 说明     |
| ----------------- | -------- | ---- | -------- |
| `currentPassword` | `String` | 是   | 当前密码 |
| `newPassword`     | `String` | 是   | 新密码   |
| `confirmPassword` | `String` | 是   | 确认密码 |

**Response**: `R<Void>`

---

### 3.4 上传头像

```
POST /api/v1/profile/avatar       [Authenticated]
```

**Query Params**:

| 参数        | 类型     | 必填 | 说明      |
| ----------- | -------- | ---- | --------- |
| `avatarUrl` | `String` | 是   | 头像URL   |

**Response**: `R<String>`（返回 URL）

---

### 3.5 获取账号安全信息

```
GET /api/v1/profile/security      [Authenticated]
```

**Response**: `R<Object>`（安全信息对象）

---

### 3.6 绑定邮箱

```
POST /api/v1/profile/bind-email   [Authenticated]
```

**Query Params**:

| 参数    | 类型     | 必填 | 说明     |
| ------- | -------- | ---- | -------- |
| `email` | `String` | 是   | 邮箱地址 |
| `code`  | `String` | 是   | 验证码   |

**Response**: `R<Void>`

---

### 3.7 绑定手机

```
POST /api/v1/profile/bind-phone   [Authenticated]
```

**Query Params**:

| 参数    | 类型     | 必填 | 说明     |
| ------- | -------- | ---- | -------- |
| `phone` | `String` | 是   | 手机号   |
| `code`  | `String` | 是   | 验证码   |

**Response**: `R<Void>`

---

### 3.8 发送邮箱验证码

```
POST /api/v1/profile/send-email-code [Authenticated]
```

**Query Params**: `email: String`

**Response**: `R<Void>`

---

### 3.9 发送手机验证码

```
POST /api/v1/profile/send-phone-code [Authenticated]
```

**Query Params**: `phone: String`

**Response**: `R<Void>`

---

### 3.10 停用账号（软删除）

```
DELETE /api/v1/profile            [Authenticated]
```

**Request Body** (`PasswordConfirmRequest`):

| 字段       | 类型     | 必填 | 说明         |
| ---------- | -------- | ---- | ------------ |
| `password` | `String` | 是   | 当前密码确认 |

**Response**: `R<Void>`

---

### 3.11 清除学习数据

```
POST /api/v1/profile/clear-data   [Authenticated]
```

**Request Body** (`PasswordConfirmRequest`):

| 字段       | 类型     | 必填 | 说明         |
| ---------- | -------- | ---- | ------------ |
| `password` | `String` | 是   | 当前密码确认 |

**Response**: `R<Void>`

---

## 4. 课程管理 Courses

**Base Path**: `/api/v1/courses`

### 4.1 分页查询课程列表

```
GET /api/v1/courses               [Authenticated]
```

**Query Params**:

| 参数       | 类型      | 默认值 | 说明       |
| ---------- | --------- | ------ | ---------- |
| `page`     | `int`     | `1`    | 页码       |
| `size`     | `int`     | `10`   | 每页大小   |
| `category` | `String`  | -      | 分类过滤   |
| `status`   | `Integer` | -      | 状态过滤   |
| `keyword`  | `String`  | -      | 搜索关键词 |

**Response**: `IPage<CourseResponse>`

**`CourseResponse`**:

| 字段             | 类型            | 说明                          |
| ---------------- | --------------- | ----------------------------- |
| `id`             | `Long`          | 课程ID                        |
| `code`           | `String`        | 课程编码                      |
| `name`           | `String`        | 课程名称                      |
| `category`       | `String`        | 课程分类编码                   |
| `categoryName`   | `String`        | 课程分类名称                   |
| `description`    | `String`        | 课程描述                      |
| `coverImage`     | `String`        | 封面图片URL                   |
| `difficulty`     | `Integer`       | 难度等级 (1-4)                |
| `difficultyName` | `String`        | 难度名称                      |
| `durationHours`  | `Integer`       | 课时数                        |
| `tags`           | `String`        | 技能标签                      |
| `status`         | `Integer`       | 状态 (0-草稿, 1-发布)         |
| `statusName`     | `String`        | 状态名称                      |
| `createdAt`      | `LocalDateTime` | 创建时间                      |
| `updatedAt`      | `LocalDateTime` | 更新时间                      |

---

### 4.2 获取所有已发布课程

```
GET /api/v1/courses/published     [Authenticated]
```

**Response**: `List<CourseResponse>`

---

### 4.3 根据 ID 获取课程

```
GET /api/v1/courses/{id}          [Authenticated]
```

**Response**: `CourseResponse`

---

### 4.4 创建课程

```
POST /api/v1/courses              [ADMIN]
```

**Request Body** (`CourseCreateRequest`):

| 字段            | 类型      | 必填 | 说明       |
| --------------- | --------- | ---- | ---------- |
| `code`          | `String`  | 是   | 课程编码   |
| `name`          | `String`  | 是   | 课程名称   |
| `category`      | `String`  | 是   | 课程分类   |
| `description`   | `String`  | 否   | 课程描述   |
| `coverImage`    | `String`  | 否   | 封面图片   |
| `difficulty`    | `Integer` | 是   | 难度等级   |
| `durationHours` | `Integer` | 是   | 课时数     |
| `tags`          | `String`  | 否   | 技能标签   |

**Response**: `CourseResponse`

---

### 4.5 更新课程

```
PUT /api/v1/courses/{id}          [ADMIN]
```

**Request Body** (`CourseUpdateRequest`): 同 `CourseCreateRequest`（无 `code`）

**Response**: `CourseResponse`

---

### 4.6 删除课程

```
DELETE /api/v1/courses/{id}       [ADMIN]
```

**Response**: `R<Void>`

---

### 4.7 发布课程

```
PATCH /api/v1/courses/{id}/publish    [ADMIN]
```

**Response**: `R<Void>`

---

### 4.8 下架课程

```
PATCH /api/v1/courses/{id}/unpublish  [ADMIN]
```

**Response**: `R<Void>`

---

### 4.9 获取课程章节列表

```
GET /api/v1/courses/{courseId}/chapters [Authenticated]
```

**Response**: `List<CourseChapterResponse>`

**`CourseChapterResponse`**:

| 字段            | 类型      | 说明                 |
| --------------- | --------- | -------------------- |
| `id`            | `Long`    | 章节ID               |
| `courseId`      | `Long`    | 课程ID               |
| `title`         | `String`  | 章节标题             |
| `description`   | `String`  | 章节描述             |
| `videoUrl`      | `String`  | 视频URL              |
| `duration`      | `Integer` | 视频时长（秒）        |
| `sortOrder`     | `Integer` | 章节顺序             |
| `isFree`        | `Boolean` | 是否免费试看          |
| `completed`     | `Boolean` | 当前用户是否已完成     |
| `watchDuration` | `Integer` | 观看时长（秒）        |
| `lastPosition`  | `Integer` | 上次播放位置（秒）     |

---

### 4.10 获取章节详情

```
GET /api/v1/courses/chapters/{chapterId} [Authenticated]
```

**Response**: `CourseChapterResponse`

---

### 4.11 标记章节完成

```
POST /api/v1/courses/chapters/{chapterId}/complete [Authenticated]
```

**Response**: `R<Void>`

---

### 4.12 更新章节观看进度

```
POST /api/v1/courses/chapters/{chapterId}/progress [Authenticated]
```

**Query Params**:

| 参数            | 类型      | 必填 | 说明              |
| --------------- | --------- | ---- | ----------------- |
| `watchDuration` | `Integer` | 是   | 观看时长（秒）     |
| `lastPosition`  | `Integer` | 是   | 播放位置（秒）     |

**Response**: `R<Void>`

---

## 5. 班期管理 Sessions

**Base Path**: `/api/v1/sessions`

### 5.1 分页查询班期列表

```
GET /api/v1/sessions              [Authenticated]
```

**Query Params**:

| 参数           | 类型      | 默认值 | 说明       |
| -------------- | --------- | ------ | ---------- |
| `page`         | `int`     | `1`    | 页码       |
| `size`         | `int`     | `10`   | 每页大小   |
| `courseId`     | `Long`    | -      | 课程ID     |
| `instructorId` | `Long`    | -      | 讲师ID     |
| `status`       | `Integer` | -      | 状态过滤   |

**Response**: `IPage<SessionResponse>`

**`SessionResponse`**:

| 字段                | 类型            | 说明                                           |
| ------------------- | --------------- | ---------------------------------------------- |
| `id`                | `Long`          | 班期ID                                          |
| `courseId`          | `Long`          | 课程ID                                          |
| `courseName`        | `String`        | 课程名称                                        |
| `instructorId`      | `Long`          | 讲师ID                                          |
| `instructorName`    | `String`        | 讲师姓名                                        |
| `sessionCode`       | `String`        | 班期编码                                        |
| `startDate`         | `LocalDate`     | 开班日期                                        |
| `endDate`           | `LocalDate`     | 结束日期                                        |
| `schedule`          | `String`        | 上课时间描述                                     |
| `location`          | `String`        | 上课地点/链接                                    |
| `maxCapacity`       | `Integer`       | 最大名额                                        |
| `currentEnrollment` | `Integer`       | 当前报名人数                                     |
| `remainingQuota`    | `Integer`       | 剩余名额                                        |
| `status`            | `Integer`       | 状态 (0-未开放,1-报名中,2-已满员,3-进行中,4-已结束) |
| `statusName`        | `String`        | 状态名称                                        |
| `createdAt`         | `LocalDateTime` | 创建时间                                        |
| `updatedAt`         | `LocalDateTime` | 更新时间                                        |

---

### 5.2 获取可报名的班期列表（学员端）

```
GET /api/v1/sessions/enrollable   [Authenticated]
```

**Query Params**: `courseId: Long`（可选）

**Response**: `List<SessionResponse>`

---

### 5.3 根据 ID 获取班期

```
GET /api/v1/sessions/{id}         [Authenticated]
```

**Response**: `SessionResponse`

---

### 5.4 创建班期

```
POST /api/v1/sessions             [ADMIN]
```

**Request Body** (`SessionCreateRequest`):

| 字段           | 类型        | 必填 | 说明       |
| -------------- | ----------- | ---- | ---------- |
| `courseId`     | `Long`      | 是   | 课程ID     |
| `instructorId` | `Long`      | 是   | 讲师ID     |
| `sessionCode`  | `String`    | 是   | 班期编码   |
| `startDate`    | `LocalDate` | 是   | 开班日期   |
| `endDate`      | `LocalDate` | 是   | 结束日期   |
| `schedule`     | `String`    | 否   | 上课时间   |
| `location`     | `String`    | 否   | 上课地点   |
| `maxCapacity`  | `Integer`   | 是   | 最大名额   |

**Response**: `SessionResponse`

---

### 5.5 更新班期

```
PUT /api/v1/sessions/{id}         [ADMIN]
```

**Request Body** (`SessionUpdateRequest`):

| 字段           | 类型        | 必填 | 说明     |
| -------------- | ----------- | ---- | -------- |
| `instructorId` | `Long`      | 否   | 讲师ID   |
| `startDate`    | `LocalDate` | 否   | 开班日期 |
| `endDate`      | `LocalDate` | 否   | 结束日期 |
| `schedule`     | `String`    | 否   | 上课时间 |
| `location`     | `String`    | 否   | 上课地点 |
| `maxCapacity`  | `Integer`   | 否   | 最大名额 |
| `status`       | `Integer`   | 否   | 状态     |

**Response**: `SessionResponse`

---

### 5.6 删除班期

```
DELETE /api/v1/sessions/{id}      [ADMIN]
```

**Response**: `R<Void>`

---

### 5.7 开放报名

```
PATCH /api/v1/sessions/{id}/open  [ADMIN]
```

**Response**: `R<Void>`

---

### 5.8 关闭报名

```
PATCH /api/v1/sessions/{id}/close [ADMIN]
```

**Response**: `R<Void>`

---

### 5.9 获取我的班期（讲师端）

```
GET /api/v1/sessions/my           [INSTRUCTOR]
```

**Response**: `List<SessionResponse>`

---

## 6. 报名管理 Enrollments

**Base Path**: `/api/v1/enrollments`

### 6.1 学员报名

```
POST /api/v1/enrollments          [STUDENT]
```

**Request Body** (`EnrollRequest`):

| 字段        | 类型   | 必填 | 说明   |
| ----------- | ------ | ---- | ------ |
| `sessionId` | `Long` | 是   | 班期ID |

**Response**: `EnrollmentResponse`

**`EnrollmentResponse`**:

| 字段              | 类型            | 说明                      |
| ----------------- | --------------- | ------------------------- |
| `id`              | `Long`          | 报名ID                    |
| `userId`          | `Long`          | 学员ID                    |
| `userName`        | `String`        | 用户名                    |
| `realName`        | `String`        | 真实姓名                  |
| `sessionId`       | `Long`          | 班期ID                    |
| `sessionCode`     | `String`        | 班期编码                  |
| `courseName`      | `String`        | 课程名称                  |
| `instructorName`  | `String`        | 讲师姓名                  |
| `startDate`       | `LocalDate`     | 开班日期                  |
| `endDate`         | `LocalDate`     | 结束日期                  |
| `schedule`        | `String`        | 上课时间                  |
| `status`          | `Integer`       | 状态 (0-已报名, 1-已取消)  |
| `statusName`      | `String`        | 状态名称                  |
| `enrolledAt`      | `LocalDateTime` | 报名时间                  |
| `canceledAt`      | `LocalDateTime` | 取消时间                  |
| `cancelReason`    | `String`        | 取消原因                  |
| `studentEmail`    | `String`        | 学员邮箱                  |
| `studentPhone`    | `String`        | 学员手机号                |
| `studentName`     | `String`        | 学员姓名                  |
| `studentUsername` | `String`        | 学员用户名                |

---

### 6.2 取消报名

```
PATCH /api/v1/enrollments/{id}/cancel [Authenticated]
```

**Request Body**（可选）:

| 字段     | 类型     | 必填 | 说明     |
| -------- | -------- | ---- | -------- |
| `reason` | `String` | 否   | 取消原因 |

**Response**: `R<Void>`

---

### 6.3 获取我的报名列表

```
GET /api/v1/enrollments/my        [Authenticated]
```

**Response**: `List<EnrollmentResponse>`

---

### 6.4 分页查询报名列表（管理端）

```
GET /api/v1/enrollments           [ADMIN]
```

**Query Params**:

| 参数        | 类型      | 默认值 | 说明       |
| ----------- | --------- | ------ | ---------- |
| `page`      | `int`     | `1`    | 页码       |
| `size`      | `int`     | `10`   | 每页大小   |
| `userId`    | `Long`    | -      | 用户ID过滤 |
| `sessionId` | `Long`    | -      | 班期ID过滤 |
| `status`    | `Integer` | -      | 状态过滤   |

**Response**: `IPage<EnrollmentResponse>`

---

### 6.5 根据 ID 获取报名详情

```
GET /api/v1/enrollments/{id}      [ADMIN]
```

**Response**: `EnrollmentResponse`

---

## 7. 学习进度 Learning Progress

**Base Path**: `/api/v1/learning`

> 所有接口均需要 **Authenticated**

### 7.1 获取所有课程学习进度

```
GET /api/v1/learning/progress     [Authenticated]
```

**Response**: `List<LearningProgressResponse>`

**`LearningProgressResponse`**:

| 字段                     | 类型            | 说明                  |
| ------------------------ | --------------- | --------------------- |
| `id`                     | `Long`          | 进度ID                |
| `userId`                 | `Long`          | 用户ID                |
| `enrollmentId`           | `Long`          | 报名记录ID            |
| `courseId`               | `Long`          | 课程ID                |
| `courseName`             | `String`        | 课程名称              |
| `courseCategory`         | `String`        | 课程分类              |
| `courseDifficulty`       | `Integer`       | 课程难度              |
| `courseCoverImage`       | `String`        | 课程封面              |
| `progressPercent`        | `Integer`       | 进度百分比 (0-100)     |
| `studyDurationMinutes`   | `Integer`       | 累计学习时长（分钟）    |
| `studyDurationFormatted` | `String`        | 累计学习时长（格式化）  |
| `lastStudyAt`            | `LocalDateTime` | 最后学习时间          |
| `status`                 | `String`        | 状态                  |
| `statusName`             | `String`        | 状态名称              |
| `completedAt`            | `LocalDateTime` | 完成时间              |

---

### 7.2 获取指定课程学习进度

```
GET /api/v1/learning/progress/course/{courseId} [Authenticated]
```

**Response**: `LearningProgressResponse`

---

### 7.3 更新学习进度

```
POST /api/v1/learning/progress/update [Authenticated]
```

**Request Body** (`UpdateProgressRequest`):

| 字段              | 类型      | 必填 | 说明                  |
| ----------------- | --------- | ---- | --------------------- |
| `courseId`        | `Long`    | 是   | 课程ID                |
| `progressPercent` | `Integer` | 否   | 进度百分比 (0-100)     |
| `studyMinutes`    | `Integer` | 否   | 本次学习时长（分钟）    |
| `lastPosition`    | `String`  | 否   | 最后学习位置           |
| `notes`           | `String`  | 否   | 学习笔记              |

**Response**: `LearningProgressResponse`

---

### 7.4 标记课程完成

```
POST /api/v1/learning/progress/complete/{courseId} [Authenticated]
```

**Response**: `LearningProgressResponse`

---

### 7.5 获取进行中的课程

```
GET /api/v1/learning/progress/in-progress [Authenticated]
```

**Response**: `List<LearningProgressResponse>`

---

## 8. 学习打卡 Checkin

**Base Path**: `/api/v1/learning`

> 所有接口均需要 **Authenticated**

### 8.1 学习打卡

```
POST /api/v1/learning/checkin     [Authenticated]
```

**Request Body** (`StudyCheckinRequest`):

| 字段           | 类型      | 必填 | 说明                                 |
| -------------- | --------- | ---- | ------------------------------------ |
| `courseId`     | `Long`    | 否   | 关联课程ID                            |
| `studyMinutes` | `Integer` | 是   | 学习时长（分钟）                       |
| `studyContent` | `String`  | 否   | 学习内容/笔记                         |
| `mood`         | `String`  | 否   | 心情 (happy/normal/tired/frustrated)  |

**Response**: `StudyCheckinResponse`

**`StudyCheckinResponse`**:

| 字段                    | 类型            | 说明             |
| ----------------------- | --------------- | ---------------- |
| `id`                    | `Long`          | 打卡ID           |
| `userId`                | `Long`          | 用户ID           |
| `checkinDate`           | `LocalDate`     | 打卡日期         |
| `studyMinutes`          | `Integer`       | 学习时长（分钟）  |
| `studyDurationFormatted`| `String`        | 时长（格式化）    |
| `studyContent`          | `String`        | 学习内容         |
| `mood`                  | `String`        | 心情             |
| `moodIcon`              | `String`        | 心情图标         |
| `courseId`              | `Long`          | 关联课程ID       |
| `courseName`            | `String`        | 课程名称         |
| `currentStreak`         | `Integer`       | 当前连续天数     |
| `newAchievementEarned`  | `Boolean`       | 是否获新成就     |
| `newAchievement`        | `Object`        | 新成就详情       |
| `createdAt`             | `LocalDateTime` | 创建时间         |

---

### 8.2 检查今日是否已打卡

```
GET /api/v1/learning/checkin/today [Authenticated]
```

**Response**: `R<Boolean>`

---

### 8.3 获取今日打卡详情

```
GET /api/v1/learning/checkin/today/detail [Authenticated]
```

**Response**: `StudyCheckinResponse`

---

### 8.4 获取打卡历史

```
GET /api/v1/learning/checkin/history [Authenticated]
```

**Query Params**:

| 参数        | 类型        | 必填 | 说明             |
| ----------- | ----------- | ---- | ---------------- |
| `startDate` | `LocalDate` | 是   | 开始日期 (ISO格式) |
| `endDate`   | `LocalDate` | 是   | 结束日期 (ISO格式) |

> 日期格式示例: `2024-01-01`

**Response**: `List<StudyCheckinResponse>`

---

### 8.5 获取当前连续打卡天数

```
GET /api/v1/learning/checkin/streak [Authenticated]
```

**Response**: `R<Integer>`

---

### 8.6 获取月度打卡日历

```
GET /api/v1/learning/checkin/calendar/{year}/{month} [Authenticated]
```

**Path Params**: `year` (年), `month` (月)

**Response**: `R<List<LocalDate>>`（该月打卡的日期列表）

---

## 9. 成就系统 Achievements

**Base Path**: `/api/v1/learning`

> 所有接口均需要 **Authenticated**

### 9.1 获取所有成就（含获得状态）

```
GET /api/v1/learning/achievements [Authenticated]
```

**Response**: `List<AchievementResponse>`

**`AchievementResponse`**:

| 字段              | 类型            | 说明                                           |
| ----------------- | --------------- | ---------------------------------------------- |
| `id`              | `Long`          | 成就ID                                          |
| `name`            | `String`        | 成就名称                                        |
| `description`     | `String`        | 成就描述                                        |
| `icon`            | `String`        | 成就图标                                        |
| `points`          | `Integer`       | 成就积分                                        |
| `category`        | `String`        | 成就类别                                        |
| `conditionType`   | `String`        | 条件类型 (streak_days/courses_completed/hours_studied) |
| `conditionValue`  | `Integer`       | 条件值                                          |
| `earned`          | `Boolean`       | 是否已获得                                      |
| `earnedAt`        | `LocalDateTime` | 获得时间                                        |
| `currentProgress` | `Integer`       | 当前进度值                                      |
| `progressPercent` | `Integer`       | 进度百分比 (0-100)                               |

---

### 9.2 获取已获得的成就

```
GET /api/v1/learning/achievements/earned [Authenticated]
```

**Response**: `List<AchievementResponse>`

---

### 9.3 获取最近获得的成就

```
GET /api/v1/learning/achievements/recent [Authenticated]
```

**Query Params**: `limit: int`（默认 5）

**Response**: `List<AchievementResponse>`

---

### 9.4 获取成就详情

```
GET /api/v1/learning/achievements/{achievementId} [Authenticated]
```

**Response**: `AchievementResponse`

---

### 9.5 获取成就积分

```
GET /api/v1/learning/achievements/points [Authenticated]
```

**Response**: `R<Integer>`

---

## 10. 学习计划 Plans

**Base Path**: `/api/v1/learning`

> 所有接口均需要 **Authenticated**

### 10.1 创建学习计划

```
POST /api/v1/learning/plans       [Authenticated]
```

**Request Body** (`LearningPlanRequest`):

| 字段                 | 类型         | 必填 | 说明               |
| -------------------- | ------------ | ---- | ------------------ |
| `planName`           | `String`     | 是   | 计划名称           |
| `description`        | `String`     | 否   | 计划描述           |
| `targetCourseIds`    | `List<Long>` | 是   | 目标课程ID列表     |
| `startDate`          | `LocalDate`  | 是   | 计划开始日期       |
| `endDate`            | `LocalDate`  | 是   | 计划结束日期       |
| `dailyTargetMinutes` | `Integer`    | 否   | 每日目标（分钟）    |
| `weeklyTargetDays`   | `Integer`    | 否   | 每周目标学习天数   |

**Response**: `LearningPlanResponse`

**`LearningPlanResponse`**:

| 字段                 | 类型            | 说明                                 |
| -------------------- | --------------- | ------------------------------------ |
| `id`                 | `Long`          | 计划ID                               |
| `userId`             | `Long`          | 用户ID                               |
| `planName`           | `String`        | 计划名称                             |
| `description`        | `String`        | 计划描述                             |
| `targetCourses`      | `List<Object>`  | 目标课程列表                         |
| `startDate`          | `LocalDate`     | 开始日期                             |
| `endDate`            | `LocalDate`     | 结束日期                             |
| `dailyTargetMinutes` | `Integer`       | 每日目标                             |
| `weeklyTargetDays`   | `Integer`       | 每周目标天数                         |
| `status`             | `String`        | 状态 (active/completed/paused/cancelled) |
| `progressPercent`    | `Integer`       | 完成进度百分比                       |
| `completedCourses`   | `Integer`       | 已完成课程数                         |
| `totalCourses`       | `Integer`       | 总课程数                             |
| `remainingDays`      | `Integer`       | 剩余天数                             |
| `createdAt`          | `LocalDateTime` | 创建时间                             |

---

### 10.2 获取所有学习计划

```
GET /api/v1/learning/plans        [Authenticated]
```

**Response**: `List<LearningPlanResponse>`

---

### 10.3 获取当前进行中的计划

```
GET /api/v1/learning/plans/active [Authenticated]
```

**Response**: `LearningPlanResponse`

---

### 10.4 获取计划详情

```
GET /api/v1/learning/plans/{planId} [Authenticated]
```

**Response**: `LearningPlanResponse`

---

### 10.5 更新学习计划

```
PUT /api/v1/learning/plans/{planId} [Authenticated]
```

**Request Body**: 同 `LearningPlanRequest`

**Response**: `LearningPlanResponse`

---

### 10.6 暂停计划

```
POST /api/v1/learning/plans/{planId}/pause [Authenticated]
```

**Response**: `LearningPlanResponse`

---

### 10.7 恢复计划

```
POST /api/v1/learning/plans/{planId}/resume [Authenticated]
```

**Response**: `LearningPlanResponse`

---

### 10.8 取消计划

```
POST /api/v1/learning/plans/{planId}/cancel [Authenticated]
```

**Response**: `LearningPlanResponse`

---

### 10.9 完成计划

```
POST /api/v1/learning/plans/{planId}/complete [Authenticated]
```

**Response**: `LearningPlanResponse`

---

## 11. 智能推荐 Recommend

**Base Path**: `/api/v1/learning/recommend`

> 所有接口均需要 **Authenticated**

**通用响应 `CourseRecommendResponse`**:

| 字段               | 类型           | 说明                                            |
| ------------------ | -------------- | ----------------------------------------------- |
| `courseId`         | `Long`         | 课程ID                                           |
| `courseName`       | `String`       | 课程名称                                         |
| `description`      | `String`       | 课程描述                                         |
| `category`         | `String`       | 课程类别编码                                     |
| `categoryName`     | `String`       | 类别名称                                         |
| `difficulty`       | `String`       | 难度级别                                         |
| `difficultyName`   | `String`       | 难度名称                                         |
| `durationHours`    | `Integer`      | 课程时长（小时）                                  |
| `coverImage`       | `String`       | 封面图片                                         |
| `recommendScore`   | `Double`       | 推荐分数 (0-100)                                 |
| `recommendReasons` | `List<String>` | 推荐理由                                         |
| `recommendType`    | `String`       | 推荐类型 (content_based/collaborative/hybrid/popular/new) |
| `similarityScore`  | `Double`       | 相似度分数                                       |
| `predictedRating`  | `Double`       | 预测评分                                         |
| `tags`             | `List<String>` | 课程标签                                         |
| `enrollmentCount`  | `Integer`      | 报名人数                                         |
| `avgRating`        | `Double`       | 平均评分                                         |

### 基于内容的推荐

| 接口 | 方法 | 路径 | 参数 |
| --- | --- | --- | --- |
| 基于学习历史推荐 | `GET` | `/history` | `limit` (默认10) |
| 推荐相似课程 | `GET` | `/similar/{courseId}` | `limit` (默认5) |
| 基于技能标签推荐 | `GET` | `/skills` | `limit` (默认10) |
| 基于偏好推荐 | `GET` | `/preference` | `limit` (默认10) |

### 协同过滤推荐

| 接口 | 方法 | 路径 | 参数 |
| --- | --- | --- | --- |
| 基于用户协同过滤 | `GET` | `/user-based` | `limit` (默认10) |
| 基于物品协同过滤 | `GET` | `/item-based` | `limit` (默认10) |
| 获取相似用户 | `GET` | `/similar-users` | `limit` (默认10)，返回 `List<Long>` |

### 混合推荐

| 接口 | 方法 | 路径 | 参数 |
| --- | --- | --- | --- |
| 混合推荐 | `GET` | `/hybrid` | `limit` (默认10) |
| 首页推荐 | `GET` | `/home` | - |
| 猜你喜欢 | `GET` | `/you-may-like` | `limit` (默认6) |
| 热门课程 | `GET` | `/popular` | `limit` (默认10) |
| 新上架课程 | `GET` | `/new` | `limit` (默认10) |

---

## 12. 学习报告 Reports

**Base Path**: `/api/v1/learning/reports`

> 所有接口均需要 **Authenticated**

**通用响应 `LearningReportResponse`**:

| 字段                     | 类型           | 说明                 |
| ------------------------ | -------------- | -------------------- |
| `periodType`             | `String`       | 报告类型 (weekly/monthly/yearly) |
| `startDate`              | `LocalDate`    | 报告开始日期         |
| `endDate`                | `LocalDate`    | 报告结束日期         |
| `totalStudyMinutes`      | `Integer`      | 总学习时长（分钟）    |
| `studyDays`              | `Integer`      | 学习天数             |
| `completedCourses`       | `Integer`      | 完成课程数           |
| `earnedAchievements`     | `Integer`      | 获得成就数           |
| `streakDays`             | `Integer`      | 连续打卡天数         |
| `avgDailyMinutes`        | `Integer`      | 日均学习时长         |
| `studyTimeChangePercent` | `Integer`      | 学习时长环比变化(%)  |
| `dailyStudyTrend`        | `List<Object>` | 每日学习趋势         |
| `hourlyDistribution`     | `Map<Int,Int>` | 时段学习分布         |
| `categoryDistribution`   | `List<Object>` | 类别学习分布         |
| `inProgressCourses`      | `List<Object>` | 进行中课程           |
| `completedCourseList`    | `List<Object>` | 已完成课程           |
| `newAchievements`        | `List<Object>` | 新获成就             |
| `milestones`             | `List<Object>` | 学习里程碑           |
| `suggestions`            | `List<String>` | 学习建议             |

### 12.1 获取周报

```
GET /api/v1/learning/reports/weekly [Authenticated]
```

**Query Params**: `weekStart: LocalDate`（ISO格式，如 `2024-01-01`）

---

### 12.2 获取月报

```
GET /api/v1/learning/reports/monthly/{year}/{month} [Authenticated]
```

---

### 12.3 获取年报

```
GET /api/v1/learning/reports/yearly/{year} [Authenticated]
```

---

### 12.4 获取自定义时间段报告

```
GET /api/v1/learning/reports/custom [Authenticated]
```

**Query Params**: `startDate`, `endDate`（ISO格式）

---

## 13. 用户画像 Profile（学习）

**Base Path**: `/api/v1/learning`

> 所有接口均需要 **Authenticated**

### 13.1 获取用户画像

```
GET /api/v1/learning/profile      [Authenticated]
```

**Response**: `UserProfileResponse`

**`UserProfileResponse`** 核心字段:

| 字段                   | 类型           | 说明           |
| ---------------------- | -------------- | -------------- |
| `userId`               | `Long`         | 用户ID         |
| `username`             | `String`       | 用户名         |
| `realName`             | `String`       | 真实姓名       |
| `avatar`               | `String`       | 头像URL        |
| `learningLevel`        | `Integer`      | 学习等级       |
| `levelName`            | `String`       | 等级名称       |
| `totalStudyMinutes`    | `Integer`      | 总学习时长     |
| `completedCourses`     | `Integer`      | 完成课程数     |
| `inProgressCourses`    | `Integer`      | 进行中课程     |
| `achievementCount`     | `Integer`      | 成就数         |
| `achievementPoints`    | `Integer`      | 成就积分       |
| `currentStreak`        | `Integer`      | 连续打卡天数   |
| `maxStreak`            | `Integer`      | 最长连续天数   |
| `skillTags`            | `List<Object>` | 技能标签       |
| `preference`           | `Object`       | 学习偏好       |
| `abilityRadar`         | `Object`       | 能力雷达图数据 |
| `timeDistribution`     | `List<Object>` | 时间分布       |
| `categoryDistribution` | `List<Object>` | 类别分布       |
| `milestones`           | `List<Object>` | 学习里程碑     |

**`abilityRadar`** 子结构:

| 字段              | 类型      | 说明            |
| ----------------- | --------- | --------------- |
| `learningSpeed`   | `Integer` | 学习速度 (0-100) |
| `persistence`     | `Integer` | 坚持度 (0-100)   |
| `comprehension`   | `Integer` | 理解力 (0-100)   |
| `practiceAbility` | `Integer` | 实践能力 (0-100) |
| `breadth`         | `Integer` | 知识广度 (0-100) |
| `depth`           | `Integer` | 知识深度 (0-100) |

---

### 13.2 更新技能标签

```
POST /api/v1/learning/profile/skills [Authenticated]
```

**Request Body**: `List<String>`（技能标签数组）

```json
["Java", "Spring Boot", "Vue.js"]
```

**Response**: `R<Void>`

---

### 13.3 更新学习偏好

```
POST /api/v1/learning/profile/preferences [Authenticated]
```

**Request Body** (`UpdatePreferencesRequest`):

| 字段                  | 类型           | 必填 | 说明            |
| --------------------- | -------------- | ---- | --------------- |
| `preferredCategories` | `List<String>` | 否   | 偏好类别列表    |
| `preferredDifficulty` | `String`       | 否   | 偏好难度        |
| `dailyStudyGoal`      | `Integer`      | 否   | 每日学习目标(分) |

**Response**: `R<Void>`

---

### 13.4 获取学习能力评估

```
GET /api/v1/learning/profile/ability-assessment [Authenticated]
```

**Response**: `R<Map<String, Object>>`

---

### 13.5 分析学习行为

```
POST /api/v1/learning/profile/analyze [Authenticated]
```

**Response**: `R<Void>`

---

## 14. 学生端 Student

**Base Path**: `/api/v1/student`

> 所有接口均需要 **Authenticated**（面向学生端的聚合接口）

### 14.1 获取学生 Dashboard 数据

```
GET /api/v1/student/dashboard     [Authenticated]
```

**Response**: `StudentDashboardResponse`

**核心结构**:

```json
{
  "userInfo": {
    "username": "string",
    "realName": "string",
    "level": 3,
    "levelName": "中级学员",
    "totalStudyMinutes": 1200,
    "nextLevelMinutes": 2000,
    "experience": 850,
    "nextLevelExp": 1000
  },
  "todayStats": {
    "studyMinutes": 45,
    "streakDays": 7,
    "checkedIn": true
  },
  "continueLearning": {
    "courseId": 1,
    "courseName": "Spring Boot 实战",
    "coverImage": "url",
    "progressPercent": 65,
    "currentChapter": "第5章 安全认证",
    "currentChapterId": 15
  },
  "weeklyStats": {
    "dailyMinutes": [30, 45, 0, 60, 20, 0, 40],
    "totalMinutes": 195
  },
  "myCourses": [ ... ],
  "recentAchievements": [ ... ]
}
```

---

### 14.2 获取学习统计

```
GET /api/v1/student/stats         [Authenticated]
```

**Response**: `StudentStatsResponse`

| 字段                | 类型      | 说明             |
| ------------------- | --------- | ---------------- |
| `totalStudyMinutes` | `Integer` | 总学习时长（分钟）|
| `streakDays`        | `Integer` | 连续打卡天数     |
| `maxStreakDays`      | `Integer` | 最大连续天数     |
| `totalCheckinDays`  | `Integer` | 总打卡天数       |
| `completedCourses`  | `Integer` | 已完成课程       |
| `inProgressCourses` | `Integer` | 进行中课程       |
| `achievementsEarned`| `Integer` | 已获成就数       |
| `level`             | `Integer` | 用户等级         |
| `experience`        | `Integer` | 当前经验值       |
| `nextLevelExp`      | `Integer` | 下一级经验       |
| `levelName`         | `String`  | 等级名称         |
| `nextLevelMinutes`  | `Integer` | 下一级所需分钟   |

---

### 14.3 获取我的成就列表

```
GET /api/v1/student/achievements  [Authenticated]
```

**Response**: `List<AchievementResponse>`

---

## 15. 统计看板 Stats（ADMIN）

**Base Path**: `/api/v1/stats`

> 所有接口均需要 **ADMIN** 角色

### 15.1 获取统计概览

```
GET /api/v1/stats/overview        [ADMIN]
```

**Response**: `StatsOverviewResponse`

| 字段                     | 类型   | 说明           |
| ------------------------ | ------ | -------------- |
| `courseCount`            | `Long` | 课程总数       |
| `sessionCount`           | `Long` | 班期总数       |
| `studentCount`           | `Long` | 学员总数       |
| `instructorCount`        | `Long` | 讲师总数       |
| `enrollmentCount`        | `Long` | 报名总数       |
| `monthlyEnrollmentCount` | `Long` | 本月报名数     |
| `activeSessionCount`     | `Long` | 进行中班期数   |

---

### 15.2 获取课程热度排行

```
GET /api/v1/stats/course-hot      [ADMIN]
```

**Query Params**: `limit: int`（默认 10）

**Response**: `List<CourseHotItem>`

| 字段              | 类型     | 说明     |
| ----------------- | -------- | -------- |
| `courseId`        | `Long`   | 课程ID   |
| `courseName`      | `String` | 课程名称 |
| `category`        | `String` | 课程分类 |
| `enrollmentCount` | `Long`   | 报名人数 |

---

### 15.3 获取报名趋势

```
GET /api/v1/stats/enrollment-trend [ADMIN]
```

**Query Params**: `days: int`（默认 7）

**Response**: `List<EnrollmentTrendItem>`

| 字段    | 类型     | 说明         |
| ------- | -------- | ------------ |
| `date`  | `String` | 日期         |
| `count` | `Long`   | 报名数量     |

---

## 16. AI 智能推荐

**Base Path**: `/api/v1/ai`

### 16.1 获取 AI 课程推荐

```
POST /api/v1/ai/recommend         [STUDENT]
```

**Request Body** (`AiRecommendRequest`):

| 字段           | 类型     | 必填 | 说明                 |
| -------------- | -------- | ---- | -------------------- |
| `learningGoal` | `String` | 是   | 用户输入的学习目标   |

**Response**: `AiRecommendResponse`

| 字段              | 类型           | 说明                 |
| ----------------- | -------------- | -------------------- |
| `courses`         | `List<Object>` | 推荐课程列表         |
| `overallReason`   | `String`       | 整体推荐理由         |
| `learningPath`    | `String`       | 建议学习顺序         |
| `fallback`        | `boolean`      | 是否为降级结果       |
| `fallbackMessage` | `String`       | 降级提示信息         |

**推荐课程子对象**:

| 字段             | 类型      | 说明     |
| ---------------- | --------- | -------- |
| `courseId`       | `Long`    | 课程ID   |
| `courseCode`     | `String`  | 课程编码 |
| `courseName`     | `String`  | 课程名称 |
| `category`       | `String`  | 课程分类 |
| `difficulty`     | `Integer` | 难度等级 |
| `difficultyName` | `String`  | 难度名称 |
| `order`          | `Integer` | 推荐顺序 |
| `reason`         | `String`  | 推荐理由 |
| `tags`           | `String`  | 课程标签 |

---

## 17. AI 服务测试（ADMIN）

**Base Path**: `/api/v1/ai/test`

> 仅 **ADMIN** 可访问，用于调试 AI 服务

| 接口 | 方法 | 路径 | 说明 |
| --- | --- | --- | --- |
| 健康检查 | `GET` | `/health` | 返回 `{ available, model }` |
| 简单对话 | `POST` | `/chat` | Body: `{ prompt }` |
| 带系统提示对话 | `POST` | `/chat-with-system` | Body: `{ systemPrompt, userPrompt }` |
| 流式对话 (SSE) | `GET` | `/stream?prompt=xxx` | 返回 `text/event-stream` |
| JSON 格式响应 | `POST` | `/chat-json` | Body: `{ prompt }` |
| 自定义参数对话 | `POST` | `/chat-with-options` | Body: `{ systemPrompt, userPrompt, temperature, maxTokens }` |

---

## 18. 通知中心 Notifications

**Base Path**: `/api/v1/notifications`

> 所有接口均需要 **Authenticated**

### 18.1 获取通知列表（分页）

```
GET /api/v1/notifications              [Authenticated]
```

**Query Params**:

| 参数   | 类型  | 默认值 | 说明     |
| ------ | ----- | ------ | -------- |
| `page` | `int` | `1`    | 页码     |
| `size` | `int` | `10`   | 每页大小 |

**Response**: `IPage<NotificationResponse>`

**`NotificationResponse`**:

| 字段        | 类型            | 说明     |
| ----------- | --------------- | -------- |
| `id`        | `Long`          | 通知ID   |
| `title`     | `String`        | 标题     |
| `content`   | `String`        | 内容     |
| `type`      | `String`        | 通知类型 |
| `isRead`    | `Boolean`       | 是否已读 |
| `createdAt` | `LocalDateTime` | 创建时间 |

---

### 18.2 获取未读通知数

```
GET /api/v1/notifications/unread-count [Authenticated]
```

**Response**: `R<Long>`

---

### 18.3 标记通知为已读

```
PATCH /api/v1/notifications/{id}/read  [Authenticated]
```

**Path Params**: `id` - 通知ID

**Response**: `R<Void>`

---

### 18.4 全部标记为已读

```
PATCH /api/v1/notifications/read-all   [Authenticated]
```

**Response**: `R<Void>`

---

## 19. 课程评价 Reviews

**Base Path**: `/api/v1/courses/{courseId}/reviews`

> 所有接口均需要 **Authenticated**

### 19.1 添加课程评价

```
POST /api/v1/courses/{courseId}/reviews [Authenticated]
```

**Path Params**: `courseId` - 课程ID

**Request Body** (`CourseReviewRequest`):

| 字段      | 类型      | 必填 | 说明                   |
| --------- | --------- | ---- | ---------------------- |
| `rating`  | `Integer` | 是   | 评分 (1-5)             |
| `comment` | `String`  | 否   | 评价内容（最长1000字）  |

**Response**: `CourseReviewResponse`

**`CourseReviewResponse`**:

| 字段        | 类型            | 说明       |
| ----------- | --------------- | ---------- |
| `id`        | `Long`          | 评价ID     |
| `userId`    | `Long`          | 用户ID     |
| `username`  | `String`        | 用户名     |
| `courseId`   | `Long`          | 课程ID     |
| `rating`    | `Integer`       | 评分 (1-5) |
| `comment`   | `String`        | 评价内容   |
| `createdAt` | `LocalDateTime` | 创建时间   |

---

### 19.2 获取课程评价列表（分页）

```
GET /api/v1/courses/{courseId}/reviews  [Authenticated]
```

**Path Params**: `courseId` - 课程ID

**Query Params**:

| 参数   | 类型  | 默认值 | 说明     |
| ------ | ----- | ------ | -------- |
| `page` | `int` | `1`    | 页码     |
| `size` | `int` | `10`   | 每页大小 |

**Response**: `IPage<CourseReviewResponse>`

---

### 19.3 获取课程评分摘要

```
GET /api/v1/courses/{courseId}/reviews/summary [Authenticated]
```

**Path Params**: `courseId` - 课程ID

**Response**: `CourseReviewSummaryResponse`

| 字段                 | 类型                 | 说明                              |
| -------------------- | -------------------- | --------------------------------- |
| `averageRating`      | `Double`             | 平均评分                          |
| `totalReviews`       | `Long`               | 评价总数                          |
| `ratingDistribution` | `Map<Integer, Long>` | 评分分布（key: 1-5星, value: 数量）|

---

## 20. 公开接口 Public

**Base Path**: `/api/v1/public`

> 所有接口均为 **Public**，无需认证

### 20.1 获取平台概览统计

```
GET /api/v1/public/stats               [Public]
```

**Response**: `PublicStatsResponse`

| 字段             | 类型     | 说明                 |
| ---------------- | -------- | -------------------- |
| `courseCount`    | `Long`   | 课程总数             |
| `studentCount`  | `Long`   | 学员总数             |
| `instructorCount`| `Long`  | 讲师总数             |
| `completionRate` | `Double` | 平均完课率（百分比）  |

---

### 20.2 获取精选课程列表

```
GET /api/v1/public/courses/featured    [Public]
```

**Query Params**:

| 参数    | 类型  | 默认值 | 说明               |
| ------- | ----- | ------ | ------------------ |
| `limit` | `int` | `8`    | 返回数量（上限20） |

**Response**: `List<CourseResponse>`（复用 4.1 中的 `CourseResponse`）

---

## 21. 成就管理 Achievement Admin（ADMIN）

**Base Path**: `/api/v1/admin/achievements`

> 所有接口均需要 **ADMIN** 角色

### 21.1 分页查询成就列表

```
GET /api/v1/admin/achievements         [ADMIN]
```

**Query Params**:

| 参数       | 类型      | 默认值 | 说明     |
| ---------- | --------- | ------ | -------- |
| `page`     | `int`     | `1`    | 页码     |
| `size`     | `int`     | `10`   | 每页大小 |
| `category` | `String`  | -      | 分类过滤 |
| `status`   | `Integer` | -      | 状态过滤 |

**Response**: `IPage<AchievementAdminResponse>`

**`AchievementAdminResponse`**:

| 字段             | 类型            | 说明                   |
| ---------------- | --------------- | ---------------------- |
| `id`             | `Long`          | 成就ID                 |
| `code`           | `String`        | 成就编码               |
| `name`           | `String`        | 成就名称               |
| `description`    | `String`        | 成就描述               |
| `iconUrl`        | `String`        | 图标URL                |
| `category`       | `String`        | 分类 (general/streak/course/skill) |
| `conditionType`  | `String`        | 条件类型               |
| `conditionValue` | `Integer`       | 条件值                 |
| `points`         | `Integer`       | 积分                   |
| `status`         | `Integer`       | 状态 (0-禁用, 1-启用)   |
| `createdAt`      | `LocalDateTime` | 创建时间               |
| `updatedAt`      | `LocalDateTime` | 更新时间               |

---

### 21.2 获取成就详情

```
GET /api/v1/admin/achievements/{id}    [ADMIN]
```

**Path Params**: `id` - 成就ID

**Response**: `AchievementAdminResponse`

---

### 21.3 创建成就

```
POST /api/v1/admin/achievements        [ADMIN]
```

**Request Body** (`AchievementCreateRequest`):

| 字段             | 类型      | 必填 | 说明                                              |
| ---------------- | --------- | ---- | ------------------------------------------------- |
| `code`           | `String`  | 是   | 成就编码（最长50）                                  |
| `name`           | `String`  | 是   | 成就名称（最长100）                                 |
| `description`    | `String`  | 否   | 成就描述（最长500）                                 |
| `iconUrl`        | `String`  | 否   | 图标URL（最长500）                                  |
| `category`       | `String`  | 是   | 分类: `general`/`streak`/`course`/`skill`          |
| `conditionType`  | `String`  | 是   | 条件类型: `streak_days`/`courses_completed`/`hours_studied`/`checkin_days` |
| `conditionValue` | `Integer` | 是   | 条件值（≥1）                                       |
| `points`         | `Integer` | 是   | 积分（≥0）                                         |

**Response**: `AchievementAdminResponse`

---

### 21.4 更新成就

```
PUT /api/v1/admin/achievements/{id}    [ADMIN]
```

**Path Params**: `id` - 成就ID

**Request Body** (`AchievementUpdateRequest`):

| 字段             | 类型      | 必填 | 说明                                              |
| ---------------- | --------- | ---- | ------------------------------------------------- |
| `name`           | `String`  | 否   | 成就名称（最长100）                                 |
| `description`    | `String`  | 否   | 成就描述（最长500）                                 |
| `iconUrl`        | `String`  | 否   | 图标URL（最长500）                                  |
| `category`       | `String`  | 否   | 分类: `general`/`streak`/`course`/`skill`          |
| `conditionType`  | `String`  | 否   | 条件类型: `streak_days`/`courses_completed`/`hours_studied`/`checkin_days` |
| `conditionValue` | `Integer` | 否   | 条件值（≥1）                                       |
| `points`         | `Integer` | 否   | 积分（≥0）                                         |
| `status`         | `Integer` | 否   | 状态: 0-禁用, 1-启用                               |

**Response**: `AchievementAdminResponse`

---

### 21.5 删除成就

```
DELETE /api/v1/admin/achievements/{id} [ADMIN]
```

**Path Params**: `id` - 成就ID

**Response**: `R<Void>`

---

## 附录: 已废弃接口

> 以下接口保留向后兼容，请使用替代接口

| 废弃接口 | 替代接口 |
| --- | --- |
| `GET /api/v1/learning/dashboard` | `GET /api/v1/student/dashboard` |
| `GET /api/v1/learning/stats` | `GET /api/v1/student/stats` |

---

## 附录: 分页响应 `IPage<T>` 结构

```json
{
  "records": [],
  "total": 0,
  "size": 10,
  "current": 1,
  "pages": 0
}
```

| 字段      | 类型    | 说明       |
| --------- | ------- | ---------- |
| `records` | `T[]`   | 数据列表   |
| `total`   | `long`  | 总记录数   |
| `size`    | `long`  | 每页大小   |
| `current` | `long`  | 当前页码   |
| `pages`   | `long`  | 总页数     |

---

## 附录: 错误码对照表

前端应根据 `code` 字段判断业务状态：

| code | 含义 |
| --- | --- |
| `200` | 成功 |
| `400` | 请求参数错误 |
| `401` | 未认证/Token 过期 |
| `403` | 无权限 |
| `404` | 资源不存在 |
| `409` | 业务冲突（如重复报名） |
| `429` | 请求频率限制 |
| `500` | 服务器内部错误 |

---

## 附录: 前端 Axios 请求示例

```javascript
// 登录
const login = (username, password) =>
  request.post('/auth/login', { username, password })

// 获取课程列表（带分页）
const getCourses = (params) =>
  request.get('/courses', { params })

// 报名
const enroll = (sessionId) =>
  request.post('/enrollments', { sessionId })

// 获取 Dashboard
const getDashboard = () =>
  request.get('/student/dashboard')

// AI 推荐
const getAiRecommend = (learningGoal) =>
  request.post('/ai/recommend', { learningGoal })
```

> **注意**: 以上示例使用的是不带 `/api/v1` 前缀的路径，前端 Axios 实例中应配置 `baseURL` 为 `/api/v1`。
