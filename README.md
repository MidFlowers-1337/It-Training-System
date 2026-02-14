# IT 技能培训智能选课系统

<p align="center">
  <strong>基于 Spring Boot 3 + Vue 3 的现代化 IT 培训平台</strong>
</p>

<p align="center">
  <a href="https://openjdk.org/"><img src="https://img.shields.io/badge/Java-17-orange.svg" alt="Java"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2.5-green.svg" alt="Spring Boot"></a>
  <a href="https://vuejs.org/"><img src="https://img.shields.io/badge/Vue-3.5-brightgreen.svg" alt="Vue"></a>
  <a href="https://www.typescriptlang.org/"><img src="https://img.shields.io/badge/TypeScript-5.9-blue.svg" alt="TypeScript"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License"></a>
</p>

<p align="center">
  <a href="#功能特性">功能特性</a> &bull;
  <a href="#技术栈">技术栈</a> &bull;
  <a href="#快速开始">快速开始</a> &bull;
  <a href="#项目结构">项目结构</a> &bull;
  <a href="#文档索引">文档</a>
</p>

---

## 项目简介

IT 技能培训智能选课系统是一个面向培训机构的现代化在线学习平台，采用前后端分离架构。系统解决了传统培训机构选课盲目、报名低效、缺乏个性化推荐等痛点，支持三种角色（管理员、讲师、学员），提供完整的课程管理、学习追踪和 AI 智能推荐功能。

### 核心亮点

- **智能推荐** - 基于 AI + 协同过滤 + 内容相似度的多策略混合推荐
- **学习追踪** - 完整的学习进度、打卡签到、学习报告与用户画像
- **成就系统** - 游戏化学习体验，激励持续学习
- **课程评价** - 学员评分与评价，帮助选课决策
- **通知系统** - 实时通知，及时掌握学习动态
- **多主题支持** - Light / Dark / Warm / Pro 四套主题 + 四种布局风格
- **安全可靠** - JWT 双 Token + Spring Security + 速率限制
- **容器化部署** - 支持 Docker 一键部署

---

## 功能特性

### 核心功能模块

| 模块 | 功能 | 角色 |
|------|------|------|
| 用户认证 | 注册、登录、JWT Token 认证、Token 刷新 | 全部 |
| 用户管理 | 用户 CRUD、角色分配、状态管理 | 管理员 |
| 课程管理 | 课程 CRUD、章节管理、发布/下架 | 管理员 |
| 班期管理 | 班期 CRUD、讲师分配、名额控制 | 管理员 |
| 报名管理 | 在线报名、取消报名、名额实时控制 | 学员 |
| 统计看板 | 数据概览、课程热度、报名趋势、完课率 | 管理员 |
| 成就管理 | 成就 CRUD、成就配置 | 管理员 |

### 学习管理模块

| 模块 | 功能 | 说明 |
|------|------|------|
| 学习进度 | 章节进度追踪、完成率统计 | 记录学员学习进度 |
| 学习计划 | 目标设定、计划管理、进度追踪 | 个性化学习规划 |
| 打卡签到 | 每日打卡、连续天数、月度日历 | 培养学习习惯 |
| 成就系统 | 徽章解锁、成就展示、积分体系 | 激励学习动力 |
| 学习报告 | 周报/月报/年报、数据分析 | 学习效果可视化 |
| 用户画像 | 技能标签、学习偏好、能力雷达图 | 了解自身学习状态 |
| 课程评价 | 评分评论、评分汇总 | 帮助选课决策 |
| 通知中心 | 消息通知、已读管理 | 及时获取动态 |

### 智能推荐模块

| 推荐策略 | 说明 |
|----------|------|
| 内容相似度推荐 | 基于课程标签和内容的相似度计算 |
| 协同过滤推荐 | 基于用户行为的协同过滤算法 |
| AI 智能推荐 | 基于 Spring AI 的大模型推荐 |
| 混合推荐策略 | 多策略融合，提供更精准的推荐结果 |

### 角色权限

| 角色 | 权限范围 |
|------|---------|
| 管理员 (ADMIN) | 用户管理、课程管理、班期管理、报名管理、统计看板、成就管理 |
| 讲师 (INSTRUCTOR) | 查看我的班期、查看学员名单 |
| 学员 (STUDENT) | 课程浏览、在线报名、AI 推荐、学习中心、个人中心、课程评价 |

---

## 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | LTS 版本 |
| Spring Boot | 3.2.5 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| JWT (jjwt) | 0.12.5 | 双 Token 认证 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0 | 关系数据库 |
| Redis | 7.x | 缓存（可选） |
| Flyway | - | 数据库版本管理 |
| Spring AI | 1.0.0-M4 | AI 集成 |
| SpringDoc | 2.3.0 | API 文档 |

### 前端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.5 | 前端框架（Composition API） |
| TypeScript | 5.9 | 类型安全 |
| Vite | 7.3 | 构建工具 |
| Pinia | 3.0 | 状态管理 |
| Vue Router | 4.6 | 路由管理 |
| TailwindCSS | 3.4 | CSS 框架 |
| Axios | 1.x | HTTP 客户端 |
| ECharts | 6.0 | 图表库 |
| Lucide Icons | - | 图标库 |
| VueUse | 14.x | 组合式工具库 |
| GSAP | 3.14 | 动画库 |
| Day.js | 1.11 | 日期处理 |

---

## 快速开始

### 环境要求

| 环境 | 版本要求 |
|------|---------|
| JDK | 17+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Maven | 3.8+ |
| Docker | 20.10+ (可选) |

### 方式一：Docker 部署（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/MidFlowers-1337/It-Training-System.git
cd It-Training-System

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，配置数据库密码和 AI 服务密钥

# 3. 启动服务
docker-compose up -d

# 4. 查看服务状态
docker-compose ps
```

服务启动后：
- 前端地址：http://localhost:5173
- 后端 API：http://localhost:8080
- API 文档：http://localhost:8080/swagger-ui.html

### 方式二：本地开发

#### 1. 克隆项目

```bash
git clone https://github.com/MidFlowers-1337/It-Training-System.git
cd It-Training-System
```

#### 2. 数据库初始化

```bash
# 创建数据库（Flyway 会自动执行迁移脚本）
mysql -u root -p -e "CREATE DATABASE it_training CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

#### 3. 启动后端

```bash
cd it-training-backend

# 配置环境变量（参见 it-training-backend/README.md）
# 启动服务
mvn spring-boot:run
```

#### 4. 启动前端

```bash
cd it-training-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端地址：http://localhost:5173

### 默认账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理权限 |
| 讲师 | instructor | 123456 | 讲师权限 |
| 学员 | student | 123456 | 学员权限 |

> **安全提示**：生产环境部署前请务必修改默认密码！

---

## 项目结构

```
It-Training-System/
├── it-training-backend/              # 后端 Spring Boot 项目
│   ├── src/main/java/com/itts/
│   │   ├── common/                   # 公共模块
│   │   │   ├── ai/                   #   AI 服务封装
│   │   │   ├── config/               #   配置类（CORS、Security、MyBatis 等）
│   │   │   ├── exception/            #   全局异常处理
│   │   │   ├── interceptor/          #   拦截器（速率限制）
│   │   │   ├── response/             #   统一响应体
│   │   │   ├── security/             #   JWT 认证与安全处理
│   │   │   └── util/                 #   工具类
│   │   ├── enums/                    # 枚举定义
│   │   └── modules/                  # 业务模块（16 个）
│   │       ├── achievement/          #   成就系统
│   │       ├── ai/                   #   AI 推荐
│   │       ├── auth/                 #   认证授权
│   │       ├── checkin/              #   学习签到
│   │       ├── course/               #   课程管理
│   │       ├── enrollment/           #   报名管理
│   │       ├── learning/             #   学习进度
│   │       ├── notification/         #   通知系统
│   │       ├── plan/                 #   学习计划
│   │       ├── profile/              #   学习画像
│   │       ├── recommend/            #   智能推荐
│   │       ├── report/               #   学习报告
│   │       ├── review/               #   课程评价
│   │       ├── session/              #   班期管理
│   │       ├── stats/                #   统计分析
│   │       ├── student/              #   学员中心
│   │       └── user/                 #   用户管理
│   ├── src/main/resources/
│   │   ├── db/migration/             # Flyway 数据库迁移脚本
│   │   └── mapper/                   # MyBatis XML 映射
│   ├── Dockerfile
│   └── pom.xml
│
├── it-training-frontend/             # 前端 Vue.js 项目
│   └── src/
│       ├── api/                      #   API 接口层（19 个模块）
│       ├── stores/                   #   Pinia 状态管理
│       ├── router/                   #   路由配置
│       ├── views/                    #   页面视图（31 个页面）
│       │   ├── admin/                #     管理端（5 页）
│       │   ├── auth/                 #     认证页（2 页）
│       │   ├── student/              #     学员端（16 页）
│       │   ├── instructor/           #     讲师端（1 页）
│       │   ├── landing/              #     着陆页（5 个主题变体）
│       │   ├── preview/              #     设计预览
│       │   └── error/                #     错误页
│       ├── components/               #   组件库（原子设计）
│       │   ├── primitives/           #     原子组件（18 个）
│       │   ├── patterns/             #     模式组件（16 个）
│       │   └── effects/              #     特效组件（12 个）
│       ├── composables/              #   组合式函数（11 个）
│       ├── design-system/            #   设计系统（tokens + 主题）
│       ├── layouts/                  #   布局组件（5 基础 + 4 主题变体）
│       ├── styles/                   #   样式系统（4 套主题 CSS）
│       └── utils/                    #   工具函数
│
├── docs/                             # 项目文档
│   └── API-Frontend-Integration.md   #   前后端 API 对接文档
│
├── docker-compose.yml
└── README.md
```

---

## 文档索引

| 文档 | 说明 |
|------|------|
| [前后端 API 对接文档](docs/API-Frontend-Integration.md) | 完整的 API 接口定义（20 个模块、100+ 端点） |
| [后端项目文档](it-training-backend/README.md) | 后端启动、配置、架构说明 |
| [前端项目文档](it-training-frontend/README.md) | 前端启动、组件库、主题系统说明 |

---

## 版本历史

### v2.0.0 (2026-02)
- **架构重构** - 后端 learning 模块拆分为 8 个独立子模块
- **前端重构** - JS 全面迁移至 TypeScript，组件库按原子设计重构
- **新增模块** - 通知系统、课程评价、学习画像、公开统计
- **多主题** - 支持 Light / Dark / Warm / Pro 四套主题
- **多布局** - 学员端支持 Notion / Linear / Vercel / Slack 四种布局风格
- **安全增强** - JWT 双 Token、AccessDenied/EntryPoint 处理器

### v1.1.0 (2025-12)
- 新增学习管理模块（进度追踪、学习计划、打卡系统、成就系统）
- 新增智能推荐模块（内容推荐、协同过滤、AI 推荐、混合策略）
- 新增学习报告功能
- 新增个人中心

### v1.0.0 (2025-12)
- 初始版本发布
- 核心功能：用户认证、课程管理、班期管理、报名管理
- AI 智能推荐、统计看板

---

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 许可证

本项目采用 [MIT License](LICENSE) 开源许可证。

---

<p align="center">
  Made with ❤️ for IT Training
</p>
