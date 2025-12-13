# IT 技能培训智能选课系统

> 基于 Spring Boot 3 + Vue 3 的智能培训选课平台，集成 AI 课程推荐功能

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-brightgreen.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📋 项目简介

IT 技能培训智能选课系统是一个面向培训机构的在线选课平台，支持：

- 🎓 **三角色管理**：管理员、讲师、学员
- 📚 **课程管理**：课程 CRUD、班期管理、名额控制
- 📝 **在线报名**：学员自主报名、取消，名额实时更新
- 🤖 **AI 智能推荐**：基于学习目标的个性化课程推荐
- 📊 **数据统计**：报名趋势、课程热度、学习进度
- 🏆 **学习管理**：学习计划、打卡、成就系统、学习报告

---

## 🚀 快速开始

### 环境要求

| 组件 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Maven | 3.8+ |

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/it-training-system.git
cd it-training-system
```

### 2. 配置环境变量

```bash
# 后端配置
cd it-training-backend
cp .env.example .env
# 编辑 .env 文件，配置数据库和 JWT 密钥
```

**必需的环境变量：**

```bash
# 数据库配置
DB_URL=jdbc:mysql://localhost:3306/it_training?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT 配置（至少 256 位随机字符串）
JWT_SECRET=your-super-secret-key-at-least-256-bits-long-for-security

# CORS 配置（生产环境必需）
CORS_ALLOWED_ORIGINS=http://localhost:5173

# AI 配置（可选）
SPRING_AI_OPENAI_API_KEY=your-api-key
SPRING_AI_OPENAI_BASE_URL=https://api.openai.com
```

### 3. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE it_training CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行建表脚本
mysql -u root -p it_training < docs/04-DB/schema.sql

# 执行学习管理模块迁移脚本（可选）
mysql -u root -p it_training < docs/04-DB/migration-v1.1-learning-tables.sql
```

### 4. 启动后端

```bash
cd it-training-backend

# Windows
set DB_URL=jdbc:mysql://localhost:3306/it_training
set DB_USERNAME=root
set DB_PASSWORD=your_password
set JWT_SECRET=your-super-secret-key-at-least-256-bits-long-for-security
mvn spring-boot:run

# Linux/Mac
export DB_URL=jdbc:mysql://localhost:3306/it_training
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-super-secret-key-at-least-256-bits-long-for-security
mvn spring-boot:run
```

后端启动后访问：
- API 文档：http://localhost:8080/swagger-ui.html
- 健康检查：http://localhost:8080/actuator/health

### 5. 启动前端

```bash
cd it-training-frontend
npm install
npm run dev
```

前端启动后访问：http://localhost:5173

### 6. 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 讲师 | instructor | 123456 |
| 学员 | student | 123456 |

---

## 🐳 Docker 部署

### 使用 Docker Compose 一键部署

```bash
# 配置环境变量
cp .env.example .env
# 编辑 .env 文件

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 服务端口

| 服务 | 端口 |
|------|------|
| 前端 | 80 |
| 后端 | 8080 |
| MySQL | 3306 |

---

## 📁 项目结构

```
it-training-system/
├── it-training-backend/          # 后端 Spring Boot 项目
│   ├── src/main/java/com/itts/
│   │   ├── common/               # 公共模块（配置、异常、安全）
│   │   ├── modules/
│   │   │   ├── ai/               # AI 推荐模块
│   │   │   ├── auth/             # 认证模块
│   │   │   ├── course/           # 课程管理
│   │   │   ├── enrollment/       # 报名管理
│   │   │   ├── learning/         # 学习管理（进度、计划、成就）
│   │   │   ├── session/          # 班期管理
│   │   │   ├── stats/            # 统计模块
│   │   │   └── user/             # 用户管理
│   │   └── enums/                # 枚举定义
│   └── src/main/resources/
│       ├── application.yaml      # 应用配置
│       └── mapper/               # MyBatis XML
│
├── it-training-frontend/         # 前端 Vue 3 项目
│   ├── src/
│   │   ├── api/                  # API 接口
│   │   ├── components/           # 公共组件
│   │   ├── layouts/              # 布局组件
│   │   ├── router/               # 路由配置
│   │   ├── store/                # Pinia 状态管理
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面组件
│   │       ├── admin/            # 管理端页面
│   │       ├── instructor/       # 讲师端页面
│   │       └── student/          # 学员端页面
│   └── vite.config.js
│
├── docs/                         # 项目文档
│   ├── 00-Index.md               # 文档索引
│   ├── 01-Requirements/          # 需求文档
│   ├── 02-Architecture/          # 架构设计
│   ├── 03-API/                   # API 文档
│   ├── 04-DB/                    # 数据库设计
│   ├── 05-Frontend/              # 前端设计
│   ├── 06-Test/                  # 测试文档
│   ├── 07-Plan/                  # 项目计划
│   └── 08-Deploy/                # 部署文档
│
└── docker-compose.yml            # Docker 编排
```

---

## ✨ 功能特性

### 核心功能

| 模块 | 功能 | 状态 |
|------|------|------|
| 认证模块 | 注册、登录、JWT 认证 | ✅ 已完成 |
| 用户管理 | 用户 CRUD、角色管理 | ✅ 已完成 |
| 课程管理 | 课程 CRUD、上下架 | ✅ 已完成 |
| 班期管理 | 班期 CRUD、状态管理 | ✅ 已完成 |
| 报名管理 | 在线报名、取消、名额控制 | ✅ 已完成 |
| AI 推荐 | 智能课程推荐、降级策略 | ✅ 已完成 |
| 统计看板 | 数据概览、图表展示 | ✅ 已完成 |

### 扩展功能（学习管理）

| 模块 | 功能 | 状态 |
|------|------|------|
| 学习进度 | 进度追踪、完成率统计 | ✅ 已完成 |
| 学习计划 | 计划创建、目标设定 | ✅ 已完成 |
| 打卡系统 | 每日打卡、连续天数 | ✅ 已完成 |
| 成就系统 | 成就解锁、徽章展示 | ✅ 已完成 |
| 学习报告 | 周报/月报、数据分析 | ✅ 已完成 |
| 个人中心 | 资料管理、账号设置 | ✅ 已完成 |
| 混合推荐 | 内容推荐 + 协同过滤 | ✅ 已完成 |

---

## 🔧 技术栈

### 后端

| 技术 | 说明 |
|------|------|
| Spring Boot 3.2.5 | 基础框架 |
| Spring Security | 安全框架 |
| JWT (jjwt 0.12.5) | Token 认证 |
| MyBatis-Plus 3.5.5 | ORM 框架 |
| MySQL 8.0 | 数据库 |
| Spring AI 1.0.0-M4 | AI 集成 |
| SpringDoc OpenAPI | API 文档 |
| Log4j2 | 日志框架 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架 |
| Vite | 构建工具 |
| Pinia | 状态管理 |
| Vue Router | 路由管理 |
| Element Plus | UI 组件库 |
| Axios | HTTP 客户端 |
| ECharts | 图表库 |

---

## 📖 文档

| 文档 | 说明 |
|------|------|
| [文档索引](docs/00-Index.md) | 所有文档入口 |
| [PRD](docs/01-Requirements/PRD.md) | 产品需求文档 |
| [架构设计](docs/02-Architecture/Architecture.md) | 系统架构 |
| [API 文档](docs/03-API/api.md) | 接口定义 |
| [数据库设计](docs/04-DB/schema.md) | 表结构设计 |
| [部署指南](docs/08-Deploy/Deployment.md) | 部署说明 |
| [测试报告](docs/06-Test/TestReviewReport.md) | 测试质量报告 |
| [安全报告](docs/06-Test/SecurityReviewReport.md) | 安全审查报告 |

---

## 🔒 安全说明

本项目已完成安全审查，主要安全措施：

- ✅ BCrypt 密码加密
- ✅ JWT Token 认证
- ✅ RBAC 角色权限控制
- ✅ 输入参数校验
- ✅ SQL 注入防护（MyBatis-Plus）
- ✅ 敏感数据脱敏
- ✅ 环境变量配置敏感信息

**生产环境部署前请确保：**
1. 配置强密码的 `JWT_SECRET`
2. 配置正确的 `CORS_ALLOWED_ORIGINS`
3. 使用非 root 数据库用户
4. 启用 HTTPS

详见 [安全审查报告](docs/06-Test/SecurityReviewReport.md)

---

## 🧪 测试

```bash
# 运行后端测试
cd it-training-backend
mvn test

# 查看测试覆盖率
mvn jacoco:report
```

测试覆盖模块：
- 认证模块（AuthController, AuthService）
- 课程模块（CourseController, CourseService）
- 报名模块（EnrollmentController, EnrollmentService）
- 用户模块（UserService）
- AI 推荐模块（AiRecommendService）
- 统计模块（StatsService）

---

## 📝 更新日志

### v1.1.0 (2025-12-14)
- ✨ 新增学习管理模块（进度、计划、打卡、成就）
- ✨ 新增混合推荐算法（内容推荐 + 协同过滤）
- ✨ 新增个人中心和账号设置
- 🔒 安全加固：环境变量配置、CORS 收紧
- 📝 完善项目文档

### v1.0.0 (2025-12-13)
- 🎉 初始版本发布
- ✅ 完成核心功能：认证、用户、课程、班期、报名
- ✅ 完成 AI 智能推荐功能
- ✅ 完成统计看板

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👥 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。
