# IT 技能培训智能选课系统

> 基于 Spring Boot 3 + Vue 3 的毕业设计项目，实现培训机构在线选课与 AI 智能推荐

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5-brightgreen.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 项目简介

本系统是一个面向 IT 培训机构的智能选课平台，解决传统培训机构选课盲目、报名低效、缺乏个性化推荐等痛点。系统支持三种角色（管理员、讲师、学员），提供课程管理、班期管理、在线报名、AI 智能推荐、学习进度追踪等功能。

## 功能模块

### 核心模块

| 模块 | 功能 | 角色 |
|------|------|------|
| 认证模块 | 注册、登录、JWT Token 认证 | 全部 |
| 用户管理 | 用户 CRUD、角色分配、个人资料 | 管理员 |
| 课程管理 | 课程 CRUD、章节管理、上下架 | 管理员 |
| 班期管理 | 班期 CRUD、讲师分配、名额控制 | 管理员 |
| 报名管理 | 在线报名、取消报名、名额实时控制 | 学员 |
| AI 推荐 | 基于学习目标的智能推荐、降级策略 | 学员 |
| 统计看板 | 数据概览、课程热度、报名趋势 | 管理员 |

### 学习管理模块 (v1.1)

| 模块 | 功能 | 说明 |
|------|------|------|
| 学习进度 | 章节进度追踪、完成率统计 | 记录学员学习进度 |
| 学习计划 | 目标设定、计划管理 | 个性化学习规划 |
| 打卡系统 | 每日打卡、连续天数、学习时长 | 培养学习习惯 |
| 成就系统 | 徽章解锁、成就展示 | 激励学习动力 |
| 学习报告 | 周报/月报、数据分析 | 学习效果可视化 |
| 混合推荐 | 内容推荐 + 协同过滤 | 更精准的课程推荐 |

### 角色权限

| 角色 | 权限范围 |
|------|---------|
| 管理员 (ADMIN) | 用户管理、课程管理、班期管理、报名管理、统计看板 |
| 讲师 (INSTRUCTOR) | 查看我的班期、查看学员名单 |
| 学员 (STUDENT) | 课程浏览、在线报名、AI 推荐、学习中心、个人中心 |

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | LTS 版本 |
| Spring Boot | 3.2.5 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| JWT (jjwt) | 0.12.5 | Token 认证 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0+ | 关系数据库 |
| Redis | 7.x | 缓存（可选） |
| Spring AI | 1.0.0-M4 | AI 集成 |
| SpringDoc | 2.3.0 | API 文档 |

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.24 | 前端框架 |
| Vite | 7.2.4 | 构建工具 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Element Plus | 2.12 | UI 组件库 |
| PrimeVue | 4.5.4 | UI 组件库 |
| Axios | 1.x | HTTP 客户端 |
| ECharts | 6.x | 图表库 |
| TailwindCSS | 3.x | CSS 框架 |

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/it-training-system.git
cd it-training-system
```

### 2. 数据库初始化

```bash
mysql -u root -p -e "CREATE DATABASE it_training CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p it_training < docs/04-DB/schema.sql
mysql -u root -p it_training < docs/04-DB/migration-v1.1-learning-tables.sql
```

### 3. 配置后端

```bash
cd it-training-backend
cp .env.example .env
```

编辑 `.env` 文件配置数据库和 AI 服务。

### 4. 启动后端

```bash
mvn spring-boot:run
```

- API 地址：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

### 5. 启动前端

```bash
cd it-training-frontend
npm install
npm run dev
```

前端地址：http://localhost:5173

### 6. 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 讲师 | instructor | 123456 |
| 学员 | student | 123456 |

## Docker 部署

```bash
cp .env.example .env
docker-compose up -d
```

## 项目结构

```
it-training-system/
├── it-training-backend/          # 后端项目
│   └── src/main/java/com/itts/
│       ├── common/               # 公共模块（配置、安全、异常）
│       ├── modules/              # 业务模块
│       │   ├── ai/               # AI 推荐
│       │   ├── auth/             # 认证授权
│       │   ├── course/           # 课程管理
│       │   ├── enrollment/       # 报名管理
│       │   ├── learning/         # 学习管理
│       │   ├── notification/     # 通知服务
│       │   ├── session/          # 班期管理
│       │   ├── stats/            # 统计模块
│       │   ├── student/          # 学员模块
│       │   └── user/             # 用户管理
│       └── enums/                # 枚举定义
│
├── it-training-frontend/         # 前端项目
│   └── src/
│       ├── api/                  # API 接口
│       ├── components/           # 公共组件
│       ├── design-system/        # 设计系统
│       ├── views/                # 页面组件
│       │   ├── admin/            # 管理端
│       │   ├── instructor/       # 讲师端
│       │   └── student/          # 学员端
│       ├── router/               # 路由配置
│       └── store/                # 状态管理
│
└── docs/                         # 项目文档
```

## 文档索引

| 文档 | 说明 |
|------|------|
| [文档索引](docs/00-Index.md) | 所有文档入口 |
| [PRD](docs/01-Requirements/PRD.md) | 产品需求文档 |
| [架构设计](docs/02-Architecture/Architecture.md) | 系统架构 |
| [API 文档](docs/03-API/api.md) | 接口定义 |
| [数据库设计](docs/04-DB/schema.md) | 表结构设计 |
| [部署指南](docs/08-Deploy/Deployment.md) | 部署说明 |

## 版本历史

### v1.1.0 (2025-12-14)
- 新增学习管理模块（进度、计划、打卡、成就）
- 新增混合推荐算法
- 新增个人中心
- 安全加固

### v1.0.0 (2025-12-13)
- 初始版本
- 核心功能：认证、用户、课程、班期、报名
- AI 智能推荐
- 统计看板

## 许可证

MIT License
