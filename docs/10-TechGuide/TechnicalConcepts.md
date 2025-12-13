# IT技能培训智能选课系统 - 技术概念解释

> 最后更新: 2025-12-14
> 本文档面向希望理解系统技术实现的开发者

---

## 目录

1. [认证与授权](#1-认证与授权)
2. [后端架构模式](#2-后端架构模式)
3. [前端架构模式](#3-前端架构模式)
4. [AI 集成](#4-ai-集成)
5. [数据库设计](#5-数据库设计)
6. [容器化部署](#6-容器化部署)

---

## 1. 认证与授权

### 1.1 JWT (JSON Web Token) 认证

**什么是 JWT？**

JWT 是一种无状态的认证机制。用户登录后，服务器生成一个加密的 Token 返回给客户端，后续请求携带此 Token 进行身份验证。

**JWT 结构**

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTczNDMwMDAwMH0.xxxxx
└────── Header ──────┘.└──────────────── Payload ────────────────┘.└ Signature ┘
```

- **Header**: 算法类型 (HS256)
- **Payload**: 用户信息 (用户名、角色、过期时间)
- **Signature**: 签名验证数据完整性

**本项目实现**

```java
// JwtUtils.java - Token 生成
public String generateToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("role", user.getRole())
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(secretKey)
        .compact();
}
```

**为什么使用 JWT？**

- **无状态**：服务器不存储会话，易于水平扩展
- **跨域支持**：Token 可在不同域名间传递
- **自包含**：Token 本身携带用户信息，减少数据库查询

### 1.2 Spring Security 权限控制

**什么是 RBAC？**

RBAC (Role-Based Access Control) 基于角色的访问控制。用户被分配角色，角色拥有权限，系统根据角色决定访问权限。

**本项目角色设计**

```
ADMIN (管理员)
├── 用户管理 (CRUD)
├── 课程管理 (CRUD)
├── 报名审批
└── 统计查看

INSTRUCTOR (讲师)
├── 查看分配的课程
└── 查看学员名单

STUDENT (学员)
├── 浏览课程
├── 报名申请
├── AI 推荐
└── 查看个人报名
```

**权限配置示例**

```java
// SecurityConfig.java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()      // 公开
    .requestMatchers("/api/users/**").hasRole("ADMIN") // 仅管理员
    .requestMatchers("/api/ai/**").hasRole("STUDENT")  // 仅学员
    .anyRequest().authenticated()                      // 其他需登录
);
```

---

## 2. 后端架构模式

### 2.1 三层架构

**为什么使用三层架构？**

三层架构将代码按职责分离，提高可维护性和可测试性：

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller 层                           │
│  职责: 接收请求、参数校验、调用 Service、返回响应              │
│  示例: @RestController, @GetMapping, @PostMapping           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Service 层                             │
│  职责: 业务逻辑、事务管理、调用多个 Mapper                    │
│  示例: @Service, @Transactional                             │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Mapper 层                              │
│  职责: 数据访问、SQL 执行、ORM 映射                           │
│  示例: @Mapper, BaseMapper<T>                               │
└─────────────────────────────────────────────────────────────┘
```

**代码示例**

```java
// Controller - 处理 HTTP 请求
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public R<Page<Course>> list(CourseQueryDTO query) {
        return R.ok(courseService.page(query));
    }
}

// Service - 业务逻辑
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper;

    @Override
    public Page<Course> page(CourseQueryDTO query) {
        // 构建查询条件、分页、返回结果
    }
}

// Mapper - 数据访问
@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    // MyBatis-Plus 提供基础 CRUD
}
```

### 2.2 MyBatis-Plus

**什么是 MyBatis-Plus？**

MyBatis-Plus 是 MyBatis 的增强工具，提供开箱即用的 CRUD 操作，减少重复代码。

**核心功能**

```java
// 1. 继承 BaseMapper 自动获得 CRUD 方法
public interface UserMapper extends BaseMapper<User> {
    // 自动拥有: selectById, insert, updateById, deleteById 等
}

// 2. Lambda 条件构造器 - 类型安全的查询
LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(Course::getStatus, "PUBLISHED")
       .like(Course::getName, keyword)
       .orderByDesc(Course::getCreatedAt);
List<Course> courses = courseMapper.selectList(wrapper);

// 3. 自动填充 - 创建/更新时间
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createdAt;

@TableField(fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updatedAt;
```

### 2.3 统一响应格式

**为什么需要统一响应？**

统一响应格式使前端处理逻辑一致，便于错误处理和数据解析。

**响应结构**

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

**实现方式**

```java
@Data
public class R<T> {
    private int code;
    private String message;
    private T data;

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> R<T> fail(String message) {
        R<T> r = new R<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }
}
```

---

## 3. 前端架构模式

### 3.1 Vue 3 组合式 API

**什么是组合式 API？**

组合式 API 允许按逻辑功能组织代码，而非按选项类型分散。

**对比示例**

```vue
<!-- 选项式 API (Vue 2 风格) - 按选项分散 -->
<script>
export default {
  data() {
    return { count: 0 }
  },
  methods: {
    increment() { this.count++ }
  },
  computed: {
    double() { return this.count * 2 }
  }
}
</script>

<!-- 组合式 API (Vue 3 推荐) - 按功能聚合 -->
<script setup>
import { ref, computed } from 'vue'

// 计数器功能 - 相关代码放在一起
const count = ref(0)
const increment = () => count.value++
const double = computed(() => count.value * 2)
</script>
```

### 3.2 Pinia 状态管理

**为什么需要状态管理？**

多个组件需要共享状态（如用户登录信息），状态管理提供集中式存储。

**Pinia vs Vuex**

- Pinia 是 Vue 3 官方推荐，更简洁
- 移除 mutations，直接修改 state
- 完美支持 TypeScript

**本项目示例**

```javascript
// stores/auth.js
export const useAuthStore = defineStore('auth', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role)

  // Actions
  async function login(credentials) {
    const res = await authApi.login(credentials)
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', res.data.token)
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isLoggedIn, userRole, login, logout }
})
```

### 3.3 Axios 请求封装

**为什么封装 Axios？**

- 统一添加请求头 (Token)
- 统一处理错误响应
- 统一 Loading 状态

**封装示例**

```javascript
// utils/request.js
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截 - 添加 Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截 - 统一错误处理
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // Token 过期，跳转登录
      router.push('/login')
    }
    ElMessage.error(error.response?.data?.message || '请求失败')
    return Promise.reject(error)
  }
)
```

---

## 4. AI 集成

### 4.1 Spring AI 概述

**什么是 Spring AI？**

Spring AI 是 Spring 官方提供的 AI 抽象层，支持多种 AI 提供商 (OpenAI, Azure, 本地模型等)。

**核心优势**

- 统一 API，切换模型只需改配置
- 与 Spring 生态无缝集成
- 内置重试和错误处理

### 4.2 AI 推荐实现

**流程说明**

```
用户输入兴趣
     │
     ▼
┌────────────────────────────────────┐
│ 1. 构建 Prompt                      │
│    - 用户历史报名课程                │
│    - 当前可报名课程列表              │
│    - 用户输入的兴趣描述              │
└────────────────┬───────────────────┘
                 │
                 ▼
┌────────────────────────────────────┐
│ 2. 调用 AI API                      │
│    - DeepSeek / OpenAI 兼容接口     │
│    - 设置 10 秒超时                 │
└────────────────┬───────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
   成功响应           超时/异常
        │                 │
        ▼                 ▼
┌──────────────┐  ┌──────────────────┐
│ 3a. 解析结果 │  │ 3b. 降级策略      │
│ 提取课程ID   │  │ 返回热门课程 TOP5 │
└──────┬───────┘  └────────┬─────────┘
       │                   │
       └─────────┬─────────┘
                 │
                 ▼
┌────────────────────────────────────┐
│ 4. 记录推荐日志                     │
│    - 请求内容                       │
│    - 响应结果                       │
│    - 是否降级                       │
└────────────────────────────────────┘
```

**代码实现**

```java
@Service
public class AiRecommendServiceImpl implements AiRecommendService {

    private final ChatModel chatModel;

    @Override
    public AiRecommendResponse recommend(Long userId, String interests) {
        // 1. 构建 Prompt
        String prompt = buildPrompt(userId, interests);

        try {
            // 2. 调用 AI (10 秒超时)
            String response = chatModel.call(prompt);

            // 3a. 解析响应，提取推荐课程
            List<Long> courseIds = parseResponse(response);
            return buildResponse(courseIds, response, false);

        } catch (Exception e) {
            // 3b. 降级：返回热门课程
            log.warn("AI 推荐失败，执行降级策略", e);
            List<Course> hotCourses = courseMapper.selectHotCourses(5);
            return buildFallbackResponse(hotCourses);
        }
    }
}
```

### 4.3 降级策略

**为什么需要降级？**

AI 服务可能不稳定（网络问题、API 限流、服务宕机），降级确保功能可用。

**降级策略**

| 触发条件 | 降级行为 |
|----------|----------|
| AI 调用超时 (>10s) | 返回热门课程 TOP5 |
| AI API 返回错误 | 返回热门课程 TOP5 |
| AI 响应解析失败 | 返回热门课程 TOP5 |

**前端提示**

```javascript
// 检查是否为降级结果
if (response.data.fallback) {
  ElMessage.warning('AI 服务暂时不可用，为您推荐热门课程')
}
```

---

## 5. 数据库设计

### 5.1 表结构设计原则

**命名规范**

- 表名：小写 + 下划线 (snake_case)
- 主键：统一使用 `id` (BIGINT 自增)
- 外键：`xxx_id` 格式
- 时间：`created_at`, `updated_at`

**审计字段**

每张表都包含以下审计字段：

```sql
created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
```

### 5.2 状态设计

**课程状态**

```sql
status ENUM('DRAFT', 'PUBLISHED', 'CLOSED') DEFAULT 'DRAFT'
```

| 状态 | 含义 | 允许操作 |
|------|------|----------|
| DRAFT | 草稿 | 编辑、发布、删除 |
| PUBLISHED | 已发布 | 编辑、下架 |
| CLOSED | 已关闭 | 重新发布 |

**报名状态**

```sql
status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING'
```

| 状态 | 含义 | 触发条件 |
|------|------|----------|
| PENDING | 待审批 | 学员提交报名 |
| APPROVED | 已通过 | 管理员审批通过 |
| REJECTED | 已拒绝 | 管理员拒绝 |
| CANCELLED | 已取消 | 学员/管理员取消 |

### 5.3 并发控制

**报名名额控制**

使用乐观锁思想，通过 `WHERE` 条件控制并发：

```sql
-- 原子操作：只有当前人数小于最大人数时才更新
UPDATE course
SET current_students = current_students + 1
WHERE id = ?
  AND current_students < max_students
  AND status = 'PUBLISHED';

-- 检查影响行数
-- affected = 0 表示名额已满或课程状态不对
```

---

## 6. 容器化部署

### 6.1 Docker 基础概念

**镜像 (Image)**

镜像是只读的模板，包含运行应用所需的一切（代码、运行时、库、配置）。

**容器 (Container)**

容器是镜像的运行实例，相互隔离，共享宿主机内核。

**类比**

- 镜像 = 类 (Class)
- 容器 = 对象 (Instance)

### 6.2 多阶段构建

**什么是多阶段构建？**

多阶段构建在一个 Dockerfile 中使用多个 `FROM` 指令，前阶段用于构建，最终阶段只复制必要产物，减小镜像体积。

**后端 Dockerfile 示例**

```dockerfile
# 阶段 1：构建
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# 阶段 2：运行（只复制 JAR）
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**体积对比**

| 构建方式 | 镜像大小 |
|----------|----------|
| 单阶段 (含 JDK + Maven) | ~800MB |
| 多阶段 (仅 JRE + JAR) | ~300MB |

### 6.3 Docker Compose 编排

**为什么使用 Docker Compose？**

Docker Compose 用于定义和运行多容器应用，通过 YAML 文件描述服务依赖。

**本项目架构**

```yaml
services:
  mysql:      # 数据库
    depends_on: []

  backend:    # 后端服务
    depends_on: [mysql]

  frontend:   # 前端 + Nginx
    depends_on: [backend]
```

**服务依赖**

`depends_on` 控制启动顺序，但不保证服务就绪。使用 `healthcheck` 确保依赖真正可用：

```yaml
backend:
  depends_on:
    mysql:
      condition: service_healthy  # 等待 MySQL 健康检查通过
```

### 6.4 环境变量管理

**为什么使用环境变量？**

- 敏感信息不写入代码
- 不同环境使用不同配置
- 遵循十二要素应用原则

**配置方式**

```yaml
# docker-compose.yml
services:
  backend:
    environment:
      SPRING_DATASOURCE_URL: ${DATABASE_URL:-jdbc:mysql://mysql:3306/it_training}
      JWT_SECRET: ${JWT_SECRET}  # 从 .env 文件读取
```

```bash
# .env 文件
DATABASE_URL=jdbc:mysql://mysql:3306/it_training
JWT_SECRET=your-super-secret-key-at-least-32-chars
AI_API_KEY=sk-xxxxxxxxxx
```

---

## 总结

本文档解释了项目中使用的核心技术概念：

1. **认证授权**：JWT 无状态认证 + Spring Security RBAC
2. **后端架构**：三层架构 + MyBatis-Plus + 统一响应
3. **前端架构**：Vue 3 组合式 API + Pinia + Axios 封装
4. **AI 集成**：Spring AI + 降级策略
5. **数据库**：状态机设计 + 并发控制
6. **容器化**：多阶段构建 + Docker Compose

如有疑问，请参考各技术的官方文档或项目源代码。
