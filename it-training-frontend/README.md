# IT 技能培训智能选课系统 - 前端

> Vue 3 + Vite + Element Plus 构建的培训选课平台前端

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架（Composition API） |
| Vite | 5.x | 构建工具 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Element Plus | 2.x | UI 组件库 |
| Axios | 1.x | HTTP 客户端 |
| ECharts | 5.x | 图表库 |

## 快速开始

### 环境要求

- Node.js 18+
- npm 或 pnpm

### 安装依赖

```bash
npm install
# 或
pnpm install
```

### 开发模式

```bash
npm run dev
```

启动后访问：http://localhost:5173

### 生产构建

```bash
npm run build
```

构建产物输出到 `dist/` 目录。

## 环境变量配置

创建 `.env.local` 文件配置本地环境变量：

```bash
# API 基础路径（默认: /api）
VITE_APP_BASE_API=/api

# 后端服务地址（仅开发环境代理使用，在 vite.config.js 中配置）
# 生产环境通过 nginx 反向代理配置
```

### 关于 VITE_APP_BASE_API

此变量定义前端 API 请求的基础路径前缀：

- **默认值**：`/api`
- **作用**：与 `axios` 实例的 `baseURL` 配合，构成完整请求路径
- **示例**：请求 `/v1/auth/login` 会发送到 `/api/v1/auth/login`

开发环境下，Vite 会将 `/api` 开头的请求代理到后端服务（见 `vite.config.js`）。

## 项目结构

```
src/
├── api/                  # API 接口定义
│   ├── auth.js          # 认证相关 API
│   ├── course.js        # 课程管理 API
│   ├── enrollment.js    # 报名管理 API
│   ├── session.js       # 班期管理 API
│   ├── user.js          # 用户管理 API
│   ├── ai.js            # AI 推荐 API
│   └── stats.js         # 统计数据 API
├── components/          # 公共组件
├── layouts/             # 布局组件
├── router/              # 路由配置
├── store/               # Pinia 状态管理
├── utils/               # 工具函数
│   ├── request.js       # axios 封装
│   └── auth.js          # 认证工具
└── views/               # 页面组件
    ├── admin/           # 管理端页面
    ├── instructor/      # 讲师端页面
    └── student/         # 学员端页面
```

## 角色权限

| 角色 | 权限 |
|------|------|
| ADMIN | 用户管理、课程管理、班期管理、数据统计 |
| INSTRUCTOR | 查看授课信息、学员管理 |
| STUDENT | 课程浏览、在线报名、AI 推荐、学习管理 |

## 开发规范

- 使用 Vue 3 Composition API + `<script setup>` 语法
- 组件命名采用 PascalCase
- API 请求统一通过 `@/api` 目录管理
- 状态管理使用 Pinia stores
- 样式优先使用 Element Plus 组件和主题变量

## 相关文档

- [Vue 3 文档](https://vuejs.org/)
- [Vite 文档](https://vitejs.dev/)
- [Element Plus 文档](https://element-plus.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
