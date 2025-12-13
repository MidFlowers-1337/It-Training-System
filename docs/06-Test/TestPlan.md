# IT 技能培训智能选课系统 - 测试计划

> 文档版本：v1.0.0
> 最后更新：2025-12-14
> 文档状态：✅ 已完成
> 关联文档：[api.md](../03-API/api.md) | [Architecture.md](../02-Architecture/Architecture.md) | [BusinessRules.md](../01-Requirements/BusinessRules.md)

---

## 目录

1. [测试概述](#1-测试概述)
2. [测试环境](#2-测试环境)
3. [测试策略](#3-测试策略)
4. [权限测试](#4-权限测试)
5. [报名名额并发控制测试](#5-报名名额并发控制测试)
6. [状态机测试](#6-状态机测试)
7. [幂等性与重复提交测试](#7-幂等性与重复提交测试)
8. [AI 推荐降级测试](#8-ai-推荐降级测试)
9. [接口测试用例](#9-接口测试用例)
10. [E2E 测试场景](#10-e2e-测试场景)
11. [性能测试](#11-性能测试)
12. [测试报告模板](#12-测试报告模板)

---

## 1. 测试概述

### 1.1 测试目标

| 目标 | 说明 |
|------|------|
| 功能完整性 | 验证所有功能点按需求正常工作 |
| 权限安全性 | 确保三角色权限隔离正确 |
| 数据一致性 | 报名名额控制、状态流转正确 |
| 系统稳定性 | 异常场景下系统正常响应 |
| 用户体验 | 界面交互流畅，提示信息准确 |

### 1.2 测试范围

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              测试范围总览                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  核心模块                                                                    │
│  ├── 认证模块（Auth）          登录、注册、JWT Token                         │
│  ├── 用户模块（User）          CRUD、角色权限                                │
│  ├── 课程模块（Course）        CRUD、上下架                                  │
│  ├── 班期模块（Session）       CRUD、状态流转                                │
│  ├── 报名模块（Enrollment）    报名、取消、名额控制                          │
│  ├── AI 模块（AI）             推荐、降级策略                                │
│  └── 统计模块（Statistics）    数据统计、图表                                │
│                                                                             │
│  关键场景                                                                    │
│  ├── 权限控制                  三角色访问控制                                │
│  ├── 并发控制                  名额抢占、库存超卖                            │
│  ├── 状态机                    班期状态、报名状态流转                        │
│  ├── 幂等性                    重复提交防护                                  │
│  └── 降级策略                  AI 服务不可用时的兜底                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.3 测试类型

| 类型 | 工具 | 覆盖率目标 | 负责人 |
|------|------|------------|--------|
| 单元测试 | JUnit 5 + Mockito | ≥70% | 后端开发 |
| 集成测试 | Spring Boot Test | 核心流程 100% | 后端开发 |
| 接口测试 | Postman / REST Assured | 全部 API | 测试人员 |
| E2E 测试 | Cypress / Playwright | 核心场景 | 测试人员 |
| 性能测试 | JMeter | 关键接口 | 测试人员 |

---

## 2. 测试环境

### 2.1 环境配置

| 环境 | 用途 | 配置 |
|------|------|------|
| 开发环境 | 开发自测 | localhost，H2/MySQL，单节点 |
| 测试环境 | 集成测试 | Docker Compose，MySQL，模拟数据 |
| 预发环境 | 验收测试 | 与生产一致配置 |

### 2.2 测试数据

```sql
-- 测试账号（密码统一为：test123）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('test_admin', '$2a$10$...', '测试管理员', 'ADMIN', 1),
('test_teacher1', '$2a$10$...', '测试讲师1', 'INSTRUCTOR', 1),
('test_teacher2', '$2a$10$...', '测试讲师2', 'INSTRUCTOR', 1),
('test_student1', '$2a$10$...', '测试学员1', 'STUDENT', 1),
('test_student2', '$2a$10$...', '测试学员2', 'STUDENT', 1),
('test_student3', '$2a$10$...', '测试学员3', 'STUDENT', 1),
('disabled_user', '$2a$10$...', '禁用用户', 'STUDENT', 0);

-- 测试课程
INSERT INTO course (code, name, category, difficulty, duration_hours, status) VALUES
('TEST-001', '测试课程A', 'BACKEND', 1, 40, 1),   -- 上架
('TEST-002', '测试课程B', 'FRONTEND', 2, 50, 0);  -- 下架

-- 测试班期
INSERT INTO class_session (course_id, instructor_id, session_code, start_date, end_date, max_capacity, current_enrollment, status) VALUES
(1, 2, 'TEST-001-S1', '2025-02-01', '2025-04-01', 3, 0, 1),   -- 报名中，名额3
(1, 2, 'TEST-001-S2', '2025-02-01', '2025-04-01', 3, 3, 2),   -- 已满员
(1, 2, 'TEST-001-S3', '2025-01-01', '2025-01-15', 30, 5, 3),  -- 进行中
(1, 2, 'TEST-001-S4', '2024-01-01', '2024-03-01', 30, 10, 4); -- 已结束
```

---

## 3. 测试策略

### 3.1 测试金字塔

```
                    ┌───────────┐
                    │   E2E     │  5-10 个核心场景
                    │   测试    │
                   ─┴───────────┴─
                  ┌───────────────┐
                  │   接口测试    │  覆盖全部 API
                  │  (API Test)  │
                 ─┴───────────────┴─
                ┌───────────────────┐
                │    集成测试       │  Service 层 + 数据库
                │(Integration Test)│
               ─┴───────────────────┴─
              ┌───────────────────────┐
              │       单元测试        │  核心业务逻辑
              │    (Unit Test)       │
              └───────────────────────┘
```

### 3.2 测试优先级

| 优先级 | 模块/场景 | 说明 |
|--------|-----------|------|
| P0 | 登录认证 | 系统入口，必须稳定 |
| P0 | 报名名额控制 | 核心业务，防止超卖 |
| P0 | 权限控制 | 安全底线 |
| P1 | 课程/班期 CRUD | 基础功能 |
| P1 | 状态流转 | 业务正确性 |
| P2 | AI 推荐 | 有降级兜底 |
| P2 | 统计看板 | 展示类功能 |

---

## 4. 权限测试

### 4.1 权限矩阵测试用例

> 测试目标：验证三角色（ADMIN/INSTRUCTOR/STUDENT）对各 API 的访问控制

#### 4.1.1 用户模块权限测试

| 用例ID | API | 方法 | ADMIN | INSTRUCTOR | STUDENT | 匿名 | 预期 |
|--------|-----|------|-------|------------|---------|------|------|
| AUTH-001 | /api/users | GET | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 | 仅管理员可访问用户列表 |
| AUTH-002 | /api/users | POST | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 | 仅管理员可创建用户 |
| AUTH-003 | /api/users/{id} | PUT | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 | 仅管理员可编辑用户 |
| AUTH-004 | /api/users/{id} | DELETE | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 | 仅管理员可删除用户 |
| AUTH-005 | /api/users/me | GET | ✅ 200 | ✅ 200 | ✅ 200 | ❌ 401 | 所有登录用户可获取个人信息 |

#### 4.1.2 课程模块权限测试

| 用例ID | API | 方法 | ADMIN | INSTRUCTOR | STUDENT | 预期 |
|--------|-----|------|-------|------------|---------|------|
| AUTH-010 | /api/courses | GET | ✅ 全部 | ✅ 全部 | ✅ 仅上架 | 学员只能看上架课程 |
| AUTH-011 | /api/courses | POST | ✅ 200 | ❌ 403 | ❌ 403 | 仅管理员可创建 |
| AUTH-012 | /api/courses/{id} | PUT | ✅ 200 | ❌ 403 | ❌ 403 | 仅管理员可编辑 |
| AUTH-013 | /api/courses/{id} | DELETE | ✅ 200 | ❌ 403 | ❌ 403 | 仅管理员可删除 |
| AUTH-014 | /api/courses/{id}/publish | PUT | ✅ 200 | ❌ 403 | ❌ 403 | 仅管理员可上架 |

#### 4.1.3 班期模块权限测试

| 用例ID | API | 方法 | ADMIN | INSTRUCTOR | STUDENT | 预期 |
|--------|-----|------|-------|------------|---------|------|
| AUTH-020 | /api/sessions | GET | ✅ 全部 | ✅ 仅自己 | ✅ 仅报名中 | 角色数据隔离 |
| AUTH-021 | /api/sessions | POST | ✅ 200 | ❌ 403 | ❌ 403 | 仅管理员可创建 |
| AUTH-022 | /api/sessions/{id}/students | GET | ✅ 200 | ✅ 仅自己 | ❌ 403 | 讲师只能看自己班期学员 |

#### 4.1.4 报名模块权限测试

| 用例ID | API | 方法 | ADMIN | INSTRUCTOR | STUDENT | 预期 |
|--------|-----|------|-------|------------|---------|------|
| AUTH-030 | /api/enrollments | GET | ✅ 全部 | ❌ 403 | ✅ 仅自己 | 学员只能看自己报名 |
| AUTH-031 | /api/enrollments | POST | ❌ 403 | ❌ 403 | ✅ 200 | 仅学员可报名 |
| AUTH-032 | /api/enrollments/{id}/cancel | PUT | ✅ 任意 | ❌ 403 | ✅ 仅自己 | 学员只能取消自己的 |

#### 4.1.5 AI 模块权限测试

| 用例ID | API | 方法 | ADMIN | INSTRUCTOR | STUDENT | 预期 |
|--------|-----|------|-------|------------|---------|------|
| AUTH-040 | /api/ai/recommend | POST | ❌ 403 | ❌ 403 | ✅ 200 | 仅学员可使用 AI 推荐 |

### 4.2 权限测试代码示例

```java
@SpringBootTest
@AutoConfigureMockMvc
class PermissionTest {

    @Autowired
    private MockMvc mockMvc;

    private String adminToken;
    private String instructorToken;
    private String studentToken;

    @BeforeEach
    void setup() {
        // 获取各角色 Token
        adminToken = loginAndGetToken("test_admin", "test123");
        instructorToken = loginAndGetToken("test_teacher1", "test123");
        studentToken = loginAndGetToken("test_student1", "test123");
    }

    // AUTH-001: 仅管理员可访问用户列表
    @Test
    void getUserList_asAdmin_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void getUserList_asInstructor_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserList_asStudent_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserList_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    // AUTH-022: 讲师只能查看自己班期的学员
    @Test
    void getStudentList_asInstructor_ownSession_shouldReturn200() throws Exception {
        // test_teacher1 的班期 ID
        Long ownSessionId = 1L;
        mockMvc.perform(get("/api/sessions/" + ownSessionId + "/students")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentList_asInstructor_otherSession_shouldReturn403() throws Exception {
        // test_teacher2 的班期 ID
        Long otherSessionId = 2L;
        mockMvc.perform(get("/api/sessions/" + otherSessionId + "/students")
                .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isForbidden());
    }
}
```

### 4.3 讲师数据隔离测试

| 用例ID | 场景 | 操作 | 预期结果 |
|--------|------|------|----------|
| ISO-001 | 讲师A查看班期列表 | GET /api/sessions | 只返回讲师A负责的班期 |
| ISO-002 | 讲师A查看讲师B班期学员 | GET /api/sessions/{B的ID}/students | 返回 403 |
| ISO-003 | 讲师A统计自己班期 | GET /api/instructor/statistics | 只统计讲师A的数据 |

---

## 5. 报名名额并发控制测试

### 5.1 测试目标

- 验证名额扣减的原子性
- 验证高并发下不会超卖
- 验证满员后状态正确更新

### 5.2 测试用例

#### 5.2.1 基本名额控制

| 用例ID | 场景 | 前置条件 | 操作 | 预期结果 |
|--------|------|----------|------|----------|
| QUOTA-001 | 正常报名 | 班期名额 3/30 | 学员报名 | 成功，名额变 4/30 |
| QUOTA-002 | 名额已满 | 班期名额 30/30 | 学员报名 | 失败，返回 4040（名额不足） |
| QUOTA-003 | 最后一个名额 | 班期名额 29/30 | 学员报名 | 成功，名额 30/30，状态变"已满员" |
| QUOTA-004 | 取消后释放 | 学员已报名 | 取消报名 | 成功，名额 -1，状态可能变回"报名中" |

#### 5.2.2 并发抢名额测试

| 用例ID | 场景 | 前置条件 | 操作 | 预期结果 |
|--------|------|----------|------|----------|
| CONC-001 | 2人抢1个名额 | 名额 2/3 | 同时发起2个报名请求 | 只有1人成功，另1人返回 4040 |
| CONC-002 | 10人抢3个名额 | 名额 0/3 | 同时发起10个报名请求 | 只有3人成功，7人返回 4040 |
| CONC-003 | 并发取消+报名 | 名额 3/3（已满） | 1人取消，同时10人报名 | 最多1人报名成功 |

### 5.3 并发测试代码示例

```java
@SpringBootTest
class EnrollmentConcurrencyTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private ClassSessionMapper sessionMapper;

    // CONC-002: 10人抢3个名额
    @Test
    void testConcurrentEnrollment_10UsersCompeteFor3Slots() throws InterruptedException {
        // 准备：班期 ID=1，名额 0/3，状态=报名中
        Long sessionId = 1L;
        resetSession(sessionId, 0, 3, 1);

        // 10 个学员并发报名
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (100 + i); // 学员 ID: 100-109
            executor.submit(() -> {
                try {
                    enrollmentService.enroll(userId, sessionId);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    if (e.getCode() == 4040) { // 名额不足
                        failCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证：只有3人成功
        assertEquals(3, successCount.get(), "应该只有3人报名成功");
        assertEquals(7, failCount.get(), "应该有7人报名失败");

        // 验证数据库状态
        ClassSession session = sessionMapper.selectById(sessionId);
        assertEquals(3, session.getCurrentEnrollment(), "当前报名人数应为3");
        assertEquals(2, session.getStatus(), "状态应为已满员(2)");
    }

    // CONC-003: 并发取消+报名
    @Test
    void testConcurrentCancelAndEnroll() throws InterruptedException {
        // 准备：班期已满 3/3
        Long sessionId = 1L;
        Long cancelUserId = 100L; // 要取消的用户
        resetSession(sessionId, 3, 3, 2);
        createEnrollment(cancelUserId, sessionId);

        int newUserCount = 10;
        CountDownLatch latch = new CountDownLatch(newUserCount + 1);
        AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(newUserCount + 1);

        // 1个用户取消
        executor.submit(() -> {
            try {
                enrollmentService.cancel(cancelUserId, sessionId, "测试取消");
            } finally {
                latch.countDown();
            }
        });

        // 10个新用户同时报名
        for (int i = 0; i < newUserCount; i++) {
            final Long userId = (long) (200 + i);
            executor.submit(() -> {
                try {
                    Thread.sleep(10); // 稍微延迟，确保取消先开始
                    enrollmentService.enroll(userId, sessionId);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    // 名额不足，忽略
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证：最多1人成功
        assertTrue(successCount.get() <= 1, "最多只有1人能抢到释放的名额");

        // 验证数据库一致性
        ClassSession session = sessionMapper.selectById(sessionId);
        assertTrue(session.getCurrentEnrollment() <= session.getMaxCapacity(),
                "当前报名人数不能超过最大名额");
    }
}
```

### 5.4 名额控制 SQL 验证

```sql
-- 验证名额扣减原子性的 SQL（应在事务中执行）
UPDATE class_session
SET current_enrollment = current_enrollment + 1,
    status = CASE WHEN current_enrollment + 1 >= max_capacity THEN 2 ELSE status END
WHERE id = ?
  AND current_enrollment < max_capacity
  AND status = 1;

-- 验证：affected_rows = 1 表示成功，= 0 表示失败（名额不足或状态不对）
```

---

## 6. 状态机测试

### 6.1 班期状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           班期状态机测试                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│   ┌──────────┐    开放报名     ┌──────────┐    满员      ┌──────────┐      │
│   │ 0-未开放 │ ──────────────> │ 1-报名中 │ ──────────> │ 2-已满员 │      │
│   └──────────┘                 └────┬─────┘             └────┬─────┘      │
│                                     │                        │             │
│                                     │ 开班                   │ 开班        │
│                                     ▼                        ▼             │
│                                ┌──────────┐             ┌──────────┐      │
│                                │ 3-进行中 │ <────────── │          │      │
│                                └────┬─────┘   有人取消   └──────────┘      │
│                                     │                                      │
│                                     │ 结束                                 │
│                                     ▼                                      │
│                                ┌──────────┐                                │
│                                │ 4-已结束 │                                │
│                                └──────────┘                                │
│                                                                             │
│   合法流转：                                                                │
│   • 0 → 1（管理员开放报名）                                                 │
│   • 1 → 2（报名人数达到上限，系统自动）                                     │
│   • 1 → 3（到达开班日期，系统自动或手动）                                   │
│   • 2 → 1（有人取消报名，名额释放）                                         │
│   • 2 → 3（到达开班日期）                                                   │
│   • 3 → 4（到达结束日期）                                                   │
│                                                                             │
│   非法流转：                                                                │
│   • 4 → 任何状态（已结束不能回退）                                          │
│   • 3 → 1/2（进行中不能回到报名状态）                                       │
│   • 0 → 2/3/4（必须先开放报名）                                             │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 状态机测试用例

#### 6.2.1 合法状态流转

| 用例ID | 初始状态 | 操作 | 预期结果 |
|--------|----------|------|----------|
| STATE-001 | 未开放(0) | 管理员开放报名 | 状态变为报名中(1) |
| STATE-002 | 报名中(1) | 学员报名至满员 | 状态自动变为已满员(2) |
| STATE-003 | 已满员(2) | 学员取消报名 | 状态变回报名中(1) |
| STATE-004 | 报名中(1) | 到达开班日期 | 状态变为进行中(3) |
| STATE-005 | 进行中(3) | 到达结束日期 | 状态变为已结束(4) |

#### 6.2.2 非法状态流转（应拒绝）

| 用例ID | 初始状态 | 尝试操作 | 预期结果 |
|--------|----------|----------|----------|
| STATE-010 | 已结束(4) | 尝试开放报名 | 拒绝，返回错误 |
| STATE-011 | 进行中(3) | 尝试重新开放报名 | 拒绝，返回错误 |
| STATE-012 | 未开放(0) | 学员尝试报名 | 拒绝，返回 4030（班期不可报名） |
| STATE-013 | 已满员(2) | 学员尝试报名 | 拒绝，返回 4040（名额不足） |
| STATE-014 | 进行中(3) | 学员尝试报名 | 拒绝，返回 4030 |
| STATE-015 | 已结束(4) | 学员尝试报名 | 拒绝，返回 4030 |

### 6.3 报名状态机

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           报名状态机测试                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│          报名成功              取消报名                                      │
│   (无) ─────────────> [0-已报名] ─────────────> [1-已取消]                   │
│                           │                         │                       │
│                           │                         │                       │
│                           │ ✗ 无法回退              │ ✗ 无法回退            │
│                           │                         │                       │
│                           ▼                         ▼                       │
│                       (终态)                    (终态)                      │
│                                                                             │
│   合法操作：                                                                │
│   • 创建报名 → 状态=已报名(0)                                               │
│   • 已报名 → 取消 → 状态=已取消(1)                                          │
│                                                                             │
│   非法操作：                                                                │
│   • 已取消(1) → 尝试再次取消（幂等返回成功或提示已取消）                     │
│   • 已取消(1) → 尝试恢复报名（不允许，需重新报名）                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 6.4 状态机测试代码示例

```java
@SpringBootTest
class StateMachineTest {

    @Autowired
    private ClassSessionService sessionService;

    @Autowired
    private EnrollmentService enrollmentService;

    // STATE-012: 未开放班期不能报名
    @Test
    void enroll_whenSessionNotOpen_shouldFail() {
        // 班期状态=0（未开放）
        Long sessionId = createSession(SessionStatus.NOT_OPEN);
        Long studentId = 100L;

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            enrollmentService.enroll(studentId, sessionId);
        });

        assertEquals(4030, ex.getCode());
        assertEquals("该班期暂不可报名", ex.getMessage());
    }

    // STATE-014: 进行中班期不能报名
    @Test
    void enroll_whenSessionInProgress_shouldFail() {
        Long sessionId = createSession(SessionStatus.IN_PROGRESS);
        Long studentId = 100L;

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            enrollmentService.enroll(studentId, sessionId);
        });

        assertEquals(4030, ex.getCode());
    }

    // STATE-003: 满员后取消，状态回到报名中
    @Test
    void cancel_whenSessionFull_shouldChangeStatusToEnrolling() {
        // 准备：班期 3/3 已满
        Long sessionId = createSession(SessionStatus.FULL, 3, 3);
        Long studentId = 100L;
        createEnrollment(studentId, sessionId);

        // 取消报名
        enrollmentService.cancel(studentId, sessionId, "测试");

        // 验证班期状态变回报名中
        ClassSession session = sessionService.getById(sessionId);
        assertEquals(SessionStatus.ENROLLING.getValue(), session.getStatus());
        assertEquals(2, session.getCurrentEnrollment());
    }

    // 已取消的报名不能再取消（幂等处理）
    @Test
    void cancel_whenAlreadyCanceled_shouldBeIdempotent() {
        Long sessionId = createSession(SessionStatus.ENROLLING);
        Long studentId = 100L;
        Long enrollmentId = createEnrollment(studentId, sessionId);

        // 第一次取消
        enrollmentService.cancel(enrollmentId, "第一次取消");

        // 第二次取消（应幂等，不报错）
        assertDoesNotThrow(() -> {
            enrollmentService.cancel(enrollmentId, "第二次取消");
        });
    }
}
```

---

## 7. 幂等性与重复提交测试

### 7.1 测试目标

- 防止重复报名同一班期
- 防止重复取消同一报名
- 防止表单重复提交

### 7.2 测试用例

#### 7.2.1 报名幂等性

| 用例ID | 场景 | 操作 | 预期结果 |
|--------|------|------|----------|
| IDEM-001 | 学员已报名班期A | 再次报名班期A | 失败，返回 4041（已报名该班期） |
| IDEM-002 | 学员已取消班期A报名 | 再次报名班期A | 成功（允许重新报名） |
| IDEM-003 | 快速双击报名按钮 | 发送2次报名请求 | 只有1次成功，另1次返回 4041 |

#### 7.2.2 取消幂等性

| 用例ID | 场景 | 操作 | 预期结果 |
|--------|------|------|----------|
| IDEM-010 | 报名已取消 | 再次取消 | 幂等返回成功（或提示已取消） |
| IDEM-011 | 快速双击取消按钮 | 发送2次取消请求 | 只有1次生效，名额只释放1次 |

#### 7.2.3 创建幂等性

| 用例ID | 场景 | 操作 | 预期结果 |
|--------|------|------|----------|
| IDEM-020 | 创建重复课程编码 | POST /api/courses (code=JAVA-001) | 失败，返回 4020（编码已存在） |
| IDEM-021 | 创建重复用户名 | POST /api/users (username=admin) | 失败，返回 4001（用户名已存在） |
| IDEM-022 | 创建重复班期编码 | POST /api/sessions (code=XXX) | 失败，返回 4031（编码已存在） |

### 7.3 并发重复提交测试

```java
@SpringBootTest
class IdempotencyTest {

    @Autowired
    private EnrollmentService enrollmentService;

    // IDEM-003: 并发重复报名
    @Test
    void testConcurrentDuplicateEnrollment() throws InterruptedException {
        Long sessionId = 1L;
        Long studentId = 100L;

        // 2个线程同时报名
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger duplicateCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    enrollmentService.enroll(studentId, sessionId);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    if (e.getCode() == 4041) { // 重复报名
                        duplicateCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证：只有1次成功
        assertEquals(1, successCount.get());
        assertEquals(1, duplicateCount.get());

        // 验证数据库只有1条记录
        int count = enrollmentService.countByUserAndSession(studentId, sessionId);
        assertEquals(1, count);
    }

    // IDEM-011: 并发取消
    @Test
    void testConcurrentCancel() throws InterruptedException {
        Long sessionId = 1L;
        Long studentId = 100L;

        // 先创建报名
        enrollmentService.enroll(studentId, sessionId);
        int initialEnrollment = getSessionEnrollment(sessionId);

        // 2个线程同时取消
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    enrollmentService.cancel(studentId, sessionId, "并发取消");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证：名额只释放1次
        int finalEnrollment = getSessionEnrollment(sessionId);
        assertEquals(initialEnrollment - 1, finalEnrollment);
    }
}
```

### 7.4 前端防重复提交

| 机制 | 实现方式 | 测试方法 |
|------|----------|----------|
| 按钮 loading | 点击后禁用按钮，显示 loading | 手动测试 |
| 防抖/节流 | 连续点击只触发一次请求 | 快速点击测试 |
| Token 机制 | 提交时携带一次性 token（可选） | 接口测试 |

---

## 8. AI 推荐降级测试

### 8.1 测试目标

- 验证 AI 正常响应时的推荐功能
- 验证 AI 超时时的降级策略
- 验证 AI 异常时的兜底方案

### 8.2 降级策略

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           AI 推荐降级策略                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│                        ┌───────────────┐                                    │
│                        │ 调用 AI 服务  │                                    │
│                        │ (超时 10s)    │                                    │
│                        └───────┬───────┘                                    │
│                                │                                            │
│               ┌────────────────┼────────────────┐                          │
│               │                │                │                          │
│               ▼                ▼                ▼                          │
│         [正常响应]        [超时/异常]       [返回空]                        │
│               │                │                │                          │
│               ▼                ▼                ▼                          │
│        ┌──────────┐     ┌──────────────┐  ┌──────────────┐                 │
│        │ 解析返回 │     │   降级处理    │  │   降级处理    │                 │
│        │ AI推荐   │     │              │  │              │                 │
│        └──────────┘     │ 1. 查询热门课 │  │ 1. 查询热门课 │                 │
│                         │    程 TOP 5   │  │    程 TOP 5   │                 │
│                         │ 2. 标记降级   │  │ 2. 标记降级   │                 │
│                         │    fallback   │  │    fallback   │                 │
│                         └──────────────┘  └──────────────┘                 │
│                                                                             │
│   响应格式：                                                                │
│   {                                                                         │
│     "courses": [...],                                                       │
│     "reason": "推荐理由 / AI服务暂时不可用，为您推荐热门课程",              │
│     "fallback": false / true                                                │
│   }                                                                         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 8.3 测试用例

| 用例ID | 场景 | 模拟方式 | 预期结果 |
|--------|------|----------|----------|
| AI-001 | AI 正常响应 | 正常调用 | 返回 AI 推荐结果，fallback=false |
| AI-002 | AI 响应超时 | Mock 延迟 15s | 返回热门课程，fallback=true |
| AI-003 | AI 服务异常 | Mock 抛出异常 | 返回热门课程，fallback=true |
| AI-004 | AI 返回空结果 | Mock 返回空 | 返回热门课程，fallback=true |
| AI-005 | AI 返回无效格式 | Mock 返回乱码 | 返回热门课程，fallback=true |
| AI-006 | 无热门课程 | 数据库无上架课程 | 返回空列表，提示无课程 |

### 8.4 降级测试代码示例

```java
@SpringBootTest
class AiRecommendFallbackTest {

    @Autowired
    private AiRecommendService aiRecommendService;

    @MockBean
    private AiClient aiClient;

    @Autowired
    private CourseMapper courseMapper;

    // AI-001: 正常响应
    @Test
    void recommend_whenAiNormal_shouldReturnAiResult() {
        // Mock AI 正常返回
        when(aiClient.chat(anyString())).thenReturn(
            "{\"courseIds\": [1, 2, 3], \"reason\": \"根据您的目标推荐\"}"
        );

        RecommendResponse response = aiRecommendService.recommend(
            100L, "想学后端开发"
        );

        assertFalse(response.isFallback());
        assertEquals(3, response.getCourses().size());
        assertNotNull(response.getReason());
    }

    // AI-002: 超时降级
    @Test
    void recommend_whenAiTimeout_shouldFallback() {
        // Mock AI 超时
        when(aiClient.chat(anyString())).thenAnswer(invocation -> {
            Thread.sleep(15000); // 超过 10s 超时
            return null;
        });

        RecommendResponse response = aiRecommendService.recommend(
            100L, "想学后端开发"
        );

        assertTrue(response.isFallback());
        assertTrue(response.getReason().contains("暂时不可用"));
        // 验证返回的是热门课程
        assertFalse(response.getCourses().isEmpty());
    }

    // AI-003: 异常降级
    @Test
    void recommend_whenAiException_shouldFallback() {
        // Mock AI 异常
        when(aiClient.chat(anyString())).thenThrow(
            new RuntimeException("AI service unavailable")
        );

        RecommendResponse response = aiRecommendService.recommend(
            100L, "想学后端开发"
        );

        assertTrue(response.isFallback());
    }

    // AI-005: 无效格式降级
    @Test
    void recommend_whenAiInvalidFormat_shouldFallback() {
        // Mock AI 返回无效 JSON
        when(aiClient.chat(anyString())).thenReturn("这不是JSON");

        RecommendResponse response = aiRecommendService.recommend(
            100L, "想学后端开发"
        );

        assertTrue(response.isFallback());
    }

    // 验证降级日志记录
    @Test
    void recommend_whenFallback_shouldLogWithFallbackModel() {
        when(aiClient.chat(anyString())).thenThrow(new RuntimeException());

        aiRecommendService.recommend(100L, "测试");

        // 验证日志记录了 model_used = "FALLBACK"
        AiRecommendLog log = logMapper.selectLatestByUserId(100L);
        assertEquals("FALLBACK", log.getModelUsed());
    }
}
```

### 8.5 前端降级提示测试

| 用例ID | 场景 | 预期 UI |
|--------|------|---------|
| AI-UI-001 | 正常推荐 | 显示推荐理由 + 课程卡片（带序号） |
| AI-UI-002 | 降级推荐 | 显示黄色提示条 "AI 服务暂时不可用，为您推荐热门课程" |
| AI-UI-003 | 加载中 | 显示骨架屏/Loading 动画 |

---

## 9. 接口测试用例

### 9.1 认证模块

| 用例ID | 接口 | 场景 | 请求 | 预期响应 |
|--------|------|------|------|----------|
| API-AUTH-001 | POST /api/auth/login | 正确账号密码 | `{username, password}` | 200, 返回 token |
| API-AUTH-002 | POST /api/auth/login | 错误密码 | `{username, wrongPwd}` | 401, code=4003 |
| API-AUTH-003 | POST /api/auth/login | 账号禁用 | `{disabledUser, pwd}` | 401, code=4004 |
| API-AUTH-004 | POST /api/auth/login | 用户不存在 | `{notExist, pwd}` | 401, code=4003 |
| API-AUTH-005 | POST /api/auth/register | 正常注册 | `{username, password, realName}` | 200 |
| API-AUTH-006 | POST /api/auth/register | 用户名已存在 | `{existUser, ...}` | 400, code=4001 |
| API-AUTH-007 | POST /api/auth/register | 密码太短 | `{user, "123", ...}` | 400, 校验失败 |

### 9.2 课程模块

| 用例ID | 接口 | 场景 | 预期响应 |
|--------|------|------|----------|
| API-COURSE-001 | GET /api/courses | 管理员查看 | 返回全部课程（含下架） |
| API-COURSE-002 | GET /api/courses | 学员查看 | 只返回上架课程 |
| API-COURSE-003 | GET /api/courses?keyword=Java | 关键字搜索 | 返回名称含 Java 的课程 |
| API-COURSE-004 | GET /api/courses?category=BACKEND | 分类筛选 | 返回后端课程 |
| API-COURSE-005 | POST /api/courses | 创建课程 | 201, 返回课程 ID |
| API-COURSE-006 | POST /api/courses | 编码重复 | 400, code=4020 |
| API-COURSE-007 | DELETE /api/courses/{id} | 有班期的课程 | 400, code=4021 |
| API-COURSE-008 | PUT /api/courses/{id}/publish | 上架课程 | 200, status=1 |
| API-COURSE-009 | PUT /api/courses/{id}/unpublish | 下架课程 | 200, status=0 |

### 9.3 班期模块

| 用例ID | 接口 | 场景 | 预期响应 |
|--------|------|------|----------|
| API-SESSION-001 | GET /api/sessions | 管理员查看 | 返回全部班期 |
| API-SESSION-002 | GET /api/sessions | 学员查看 | 只返回报名中的班期 |
| API-SESSION-003 | GET /api/sessions | 讲师查看 | 只返回自己负责的班期 |
| API-SESSION-004 | POST /api/sessions | 创建班期 | 201, 返回班期 ID |
| API-SESSION-005 | POST /api/sessions | 结束日期早于开始 | 400, 校验失败 |
| API-SESSION-006 | DELETE /api/sessions/{id} | 有报名的班期 | 400, code=4033 |
| API-SESSION-007 | GET /api/sessions/{id}/students | 查看学员名单 | 返回报名学员列表 |

### 9.4 报名模块

| 用例ID | 接口 | 场景 | 预期响应 |
|--------|------|------|----------|
| API-ENROLL-001 | POST /api/enrollments | 正常报名 | 201, 返回报名 ID |
| API-ENROLL-002 | POST /api/enrollments | 重复报名 | 400, code=4041 |
| API-ENROLL-003 | POST /api/enrollments | 名额已满 | 400, code=4040 |
| API-ENROLL-004 | POST /api/enrollments | 班期未开放 | 400, code=4030 |
| API-ENROLL-005 | PUT /api/enrollments/{id}/cancel | 正常取消 | 200 |
| API-ENROLL-006 | PUT /api/enrollments/{id}/cancel | 班期已开始 | 400, code=4044 |
| API-ENROLL-007 | GET /api/enrollments | 学员查看自己 | 返回自己的报名记录 |

### 9.5 接口测试 Postman Collection 结构

```
IT-Training-System/
├── Auth/
│   ├── 登录-正常
│   ├── 登录-密码错误
│   ├── 登录-账号禁用
│   ├── 注册-正常
│   └── 注册-用户名重复
├── User/
│   ├── 用户列表
│   ├── 创建用户
│   ├── 编辑用户
│   └── 删除用户
├── Course/
│   ├── 课程列表-管理员
│   ├── 课程列表-学员
│   ├── 课程详情
│   ├── 创建课程
│   ├── 编辑课程
│   ├── 删除课程
│   ├── 上架课程
│   └── 下架课程
├── Session/
│   ├── 班期列表-管理员
│   ├── 班期列表-讲师
│   ├── 班期列表-学员
│   ├── 创建班期
│   ├── 编辑班期
│   ├── 删除班期
│   └── 学员名单
├── Enrollment/
│   ├── 报名-正常
│   ├── 报名-重复
│   ├── 报名-名额满
│   ├── 取消报名
│   └── 我的报名
├── AI/
│   ├── AI推荐-正常
│   └── AI推荐-降级
└── Statistics/
    ├── 数据概览
    ├── 课程热度
    └── 报名趋势
```

---

## 10. E2E 测试场景

### 10.1 核心场景清单

| 场景ID | 场景名称 | 优先级 | 覆盖模块 |
|--------|----------|--------|----------|
| E2E-001 | 学员完整报名流程 | P0 | Auth + Course + Session + Enrollment |
| E2E-002 | 管理员课程管理流程 | P0 | Auth + Course |
| E2E-003 | 管理员班期管理流程 | P0 | Auth + Session |
| E2E-004 | 讲师查看学员流程 | P1 | Auth + Session + Enrollment |
| E2E-005 | AI 推荐流程 | P1 | Auth + AI |
| E2E-006 | 统计看板验证 | P2 | Auth + Statistics |

### 10.2 E2E-001: 学员完整报名流程

```gherkin
Feature: 学员报名流程

  Background:
    Given 系统中存在上架课程 "Java基础"
    And 该课程有报名中的班期 "JAVA-001-S1"，名额 10/30
    And 存在学员账号 "student1"

  Scenario: 学员成功报名课程
    Given 我以 "student1" 身份登录
    When 我访问首页
    Then 我应该看到导航栏包含 "课程中心"

    When 我点击 "课程中心"
    Then 我应该看到课程 "Java基础"

    When 我点击课程 "Java基础"
    Then 我应该看到课程详情页
    And 我应该看到可报名班期 "JAVA-001-S1"
    And 剩余名额显示 "20"

    When 我点击班期 "JAVA-001-S1" 的报名按钮
    Then 我应该看到确认弹窗
    And 弹窗中显示班期信息

    When 我点击确认报名
    Then 我应该看到 "报名成功" 提示
    And 剩余名额显示 "19"

    When 我访问 "我的报名"
    Then 我应该看到报名记录 "JAVA-001-S1"
    And 状态显示 "已报名"

  Scenario: 学员取消报名
    Given 我已报名班期 "JAVA-001-S1"
    And 我以 "student1" 身份登录

    When 我访问 "我的报名"
    And 我点击 "JAVA-001-S1" 的取消按钮
    Then 我应该看到确认弹窗

    When 我输入取消原因 "时间冲突"
    And 我点击确认取消
    Then 我应该看到 "取消成功" 提示
    And 报名状态变为 "已取消"

  Scenario: 名额已满无法报名
    Given 班期 "JAVA-001-S1" 名额已满
    And 我以 "student1" 身份登录

    When 我访问课程详情
    Then 班期 "JAVA-001-S1" 的按钮应该显示 "已满"
    And 按钮应该禁用
```

### 10.3 E2E 测试代码示例 (Cypress)

```javascript
// cypress/e2e/enrollment.cy.js

describe('学员报名流程', () => {
  beforeEach(() => {
    // 重置测试数据
    cy.task('db:reset');
    cy.task('db:seed');
  });

  it('E2E-001: 学员成功报名课程', () => {
    // 登录
    cy.visit('/login');
    cy.get('[data-testid="username"]').type('student1');
    cy.get('[data-testid="password"]').type('test123');
    cy.get('[data-testid="login-btn"]').click();

    // 验证跳转到首页
    cy.url().should('include', '/student/home');

    // 进入课程中心
    cy.get('[data-testid="nav-courses"]').click();
    cy.url().should('include', '/student/courses');

    // 查找并点击课程
    cy.contains('.course-card', 'Java基础').click();
    cy.url().should('match', /\/student\/courses\/\d+/);

    // 验证课程详情
    cy.get('[data-testid="course-title"]').should('contain', 'Java基础');
    cy.get('[data-testid="session-list"]').should('exist');

    // 点击报名
    cy.get('[data-testid="enroll-btn-JAVA-001-S1"]').click();

    // 验证确认弹窗
    cy.get('.el-dialog').should('be.visible');
    cy.get('.el-dialog').should('contain', 'JAVA-001-S1');

    // 确认报名
    cy.get('[data-testid="confirm-enroll"]').click();

    // 验证成功提示
    cy.get('.el-message--success').should('contain', '报名成功');

    // 验证我的报名
    cy.visit('/student/enrollments');
    cy.contains('.enrollment-card', 'JAVA-001-S1').should('exist');
    cy.contains('.enrollment-card', '已报名').should('exist');
  });

  it('名额已满显示禁用按钮', () => {
    // 设置班期已满
    cy.task('db:setSessionFull', 'JAVA-001-S1');

    cy.login('student1', 'test123');
    cy.visit('/student/courses/1');

    // 验证按钮状态
    cy.get('[data-testid="enroll-btn-JAVA-001-S1"]')
      .should('be.disabled')
      .should('contain', '已满');
  });
});
```

---

## 11. 性能测试

### 11.1 性能指标

| 指标 | 目标值 | 说明 |
|------|--------|------|
| 响应时间（P95） | < 500ms | 95% 请求响应时间 |
| 响应时间（P99） | < 1000ms | 99% 请求响应时间 |
| 吞吐量 | > 100 TPS | 每秒事务数 |
| 并发用户 | 50 | 同时在线用户 |
| 错误率 | < 0.1% | 请求失败率 |

### 11.2 压测场景

| 场景 | 接口 | 并发数 | 持续时间 | 目标 |
|------|------|--------|----------|------|
| 登录压测 | POST /api/auth/login | 50 | 5min | RT<500ms |
| 课程列表 | GET /api/courses | 100 | 5min | RT<200ms |
| 报名压测 | POST /api/enrollments | 50 | 5min | 无超卖 |
| AI 推荐 | POST /api/ai/recommend | 20 | 5min | RT<10s |

### 11.3 JMeter 测试计划结构

```
Test Plan
├── Thread Group - 登录压测
│   ├── HTTP Request - Login
│   ├── JSON Extractor - Token
│   └── Assertion - Response Code 200
├── Thread Group - 报名并发压测
│   ├── setUp Thread Group - 准备数据
│   ├── HTTP Request - Enroll
│   ├── Assertion - No Oversell
│   └── tearDown Thread Group - 验证数据
└── Summary Report
```

### 11.4 报名并发压测验证

```java
// 压测后验证数据一致性
@Test
void verifyNoOversellAfterLoadTest() {
    // 1. 查询班期当前报名人数
    ClassSession session = sessionMapper.selectById(testSessionId);
    int currentEnrollment = session.getCurrentEnrollment();
    int maxCapacity = session.getMaxCapacity();

    // 2. 统计实际报名记录数
    int actualEnrollments = enrollmentMapper.countBySessionId(testSessionId);

    // 3. 验证一致性
    assertEquals(currentEnrollment, actualEnrollments, "冗余字段与实际记录不一致");
    assertTrue(currentEnrollment <= maxCapacity, "报名人数超过最大名额！");
}
```

---

## 12. 测试报告模板

### 12.1 测试报告结构

```markdown
# 测试报告 - Sprint X

## 概要
- 测试周期：2025-XX-XX ~ 2025-XX-XX
- 测试版本：v1.x.x
- 测试环境：测试服务器

## 测试范围
- [x] 权限测试（xx 条用例）
- [x] 功能测试（xx 条用例）
- [x] 接口测试（xx 条用例）
- [x] E2E 测试（xx 条用例）

## 测试结果汇总
| 类型 | 总数 | 通过 | 失败 | 阻塞 | 通过率 |
|------|------|------|------|------|--------|
| 单元测试 | xx | xx | xx | 0 | xx% |
| 接口测试 | xx | xx | xx | 0 | xx% |
| E2E测试 | xx | xx | xx | 0 | xx% |

## 缺陷统计
| 严重程度 | 数量 | 已修复 | 待修复 |
|----------|------|--------|--------|
| 致命 | 0 | 0 | 0 |
| 严重 | x | x | 0 |
| 一般 | x | x | x |
| 轻微 | x | x | x |

## 关键问题
1. [BUG-001] 问题描述...
2. [BUG-002] 问题描述...

## 风险与建议
1. 风险点...
2. 建议...

## 结论
- [ ] 达到上线标准
- [ ] 需要延期修复
```

### 12.2 缺陷报告模板

```markdown
## 缺陷报告

**缺陷ID**: BUG-XXX
**严重程度**: 严重 / 一般 / 轻微
**优先级**: P0 / P1 / P2
**发现人**: xxx
**发现日期**: 2025-XX-XX

### 环境
- 浏览器：Chrome 120
- 测试账号：student1
- 测试环境：测试服务器

### 重现步骤
1. 步骤 1
2. 步骤 2
3. 步骤 3

### 预期结果
描述预期结果...

### 实际结果
描述实际结果...

### 截图/日志
[附件]

### 备注
其他补充信息...
```

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-14 | Test Architect | 初始版本 |
