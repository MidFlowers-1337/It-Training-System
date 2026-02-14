# IT 技能培训智能选课系统 - 前端

> Vue 3 + TypeScript + Vite + TailwindCSS + 原子设计组件库

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue.js | 3.5 | Composition API + `<script setup>` |
| TypeScript | 5.9 | 全量类型覆盖 |
| Vite | 7.3 | 构建工具，支持 HMR |
| Pinia | 3.0 | 状态管理 |
| Vue Router | 4.6 | 路由管理 |
| TailwindCSS | 3.4 | 原子化 CSS |
| Axios | 1.x | HTTP 客户端（带拦截器） |
| ECharts | 6.0 | 数据图表 |
| VueUse | 14.x | 组合式工具库 |
| Lucide Icons | - | SVG 图标库 |
| GSAP | 3.14 | 高性能动画 |
| Day.js | 1.11 | 日期处理 |
| canvas-confetti | 1.9 | 庆祝动画（成就解锁等） |

---

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器（默认 http://localhost:5173）
npm run dev

# 构建生产版本
npm run build

# 预览构建结果
npm run preview
```

### 环境配置

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境配置（API 代理至 localhost:8080） |
| `.env.production` | 生产环境配置 |

---

## 项目结构

```
src/
├── api/                        # API 接口层（19 个模块）
│   ├── request.ts              #   Axios 实例 + 拦截器（Token 自动注入、401 处理）
│   ├── auth.ts                 #   认证（登录/注册/刷新 Token）
│   ├── user.ts                 #   用户管理
│   ├── profile.ts              #   个人资料
│   ├── course.ts               #   课程
│   ├── session.ts              #   班期
│   ├── enrollment.ts           #   报名
│   ├── learning.ts             #   学习进度
│   ├── checkin.ts              #   打卡签到
│   ├── achievement.ts          #   成就
│   ├── plan.ts                 #   学习计划
│   ├── recommend.ts            #   智能推荐
│   ├── report.ts               #   学习报告
│   ├── learningProfile.ts      #   用户画像
│   ├── student.ts              #   学员 Dashboard
│   ├── stats.ts                #   管理统计
│   ├── publicStats.ts          #   公开统计（着陆页）
│   ├── ai.ts                   #   AI 聊天
│   └── notification.ts         #   通知
│
├── stores/                     # Pinia 状态管理
│   ├── index.ts                #   Store 入口
│   ├── user.ts                 #   用户状态（登录态/角色/Token）
│   ├── theme.ts                #   主题状态
│   └── app.ts                  #   应用全局状态
│
├── router/                     # 路由配置
│   └── index.ts                #   路由定义 + 导航守卫
│
├── views/                      # 页面视图（31 页）
│   ├── admin/                  #   管理端（5 页）
│   │   ├── Dashboard.vue       #     管理仪表盘
│   │   ├── Users.vue           #     用户管理
│   │   ├── Courses.vue         #     课程管理
│   │   ├── Sessions.vue        #     班期管理
│   │   └── Enrollments.vue     #     报名管理
│   ├── auth/                   #   认证页（2 页）
│   │   ├── Login.vue           #     登录
│   │   └── Register.vue        #     注册
│   ├── student/                #   学员端（16 页）
│   │   ├── Home.vue            #     首页
│   │   ├── Dashboard.vue       #     学习仪表盘
│   │   ├── CourseList.vue      #     课程列表
│   │   ├── CourseDetail.vue    #     课程详情
│   │   ├── CourseStudy.vue     #     课程学习
│   │   ├── MyCourses.vue       #     我的课程
│   │   ├── MyLearning.vue      #     我的学习
│   │   ├── LearningCenter.vue  #     学习中心
│   │   ├── LearningPlan.vue    #     学习计划
│   │   ├── LearningReport.vue  #     学习报告
│   │   ├── Achievements.vue    #     成就展示
│   │   ├── SmartRecommend.vue  #     智能推荐
│   │   ├── AiChat.vue          #     AI 助手
│   │   ├── PersonalCenter.vue  #     个人中心
│   │   ├── UserProfile.vue     #     用户画像
│   │   └── Settings.vue        #     设置
│   ├── instructor/             #   讲师端（1 页）
│   │   └── MySessions.vue      #     我的班期
│   ├── landing/                #   着陆页（5 个主题变体）
│   │   ├── LandingPage.vue     #     默认着陆页
│   │   ├── LandingLight.vue    #     浅色主题
│   │   ├── LandingDark.vue     #     深色主题
│   │   ├── LandingWarm.vue     #     暖色主题
│   │   └── LandingPro.vue      #     专业主题
│   ├── preview/                #   设计预览
│   │   └── DesignPreview.vue
│   └── error/                  #   错误页
│       └── NotFound.vue        #     404
│
├── components/                 # 组件库（原子设计，52 个组件）
│   ├── primitives/             #   原子组件（18 个）
│   │   ├── Button.vue, Input.vue, Select.vue, Checkbox.vue
│   │   ├── Switch.vue, Avatar.vue, Badge.vue, Tag.vue
│   │   ├── Divider.vue, Modal.vue, Tooltip.vue
│   │   ├── ProgressBar.vue, ProgressRing.vue, Skeleton.vue
│   │   ├── Toast.vue, ToastContainer.vue, Alert.vue
│   │   └── EmptyState.vue
│   ├── patterns/               #   模式组件（16+ 个）
│   │   ├── PageLayout.vue, PageHeader.vue, Section.vue
│   │   ├── InsetGroup.vue, InsetItem.vue
│   │   ├── FormLayout.vue, FormItem.vue, ListRow.vue
│   │   ├── DataTable.vue, Pagination.vue, SearchBar.vue
│   │   ├── StatCard.vue, ThemeSwitcher.vue, Breadcrumb.vue
│   │   ├── NotificationPanel.vue, ErrorBoundary.vue
│   │   └── CommandPalette.vue, WelcomeWizard.vue
│   └── effects/                #   特效组件（12 个）
│       ├── AuroraBackground.vue, GradientText.vue
│       ├── ParallaxSection.vue, FloatingParticles.vue
│       ├── BentoGrid.vue, BentoItem.vue
│       ├── GlassCard.vue, ShimmerButton.vue
│       ├── MeshGradient.vue, NumberCounter.vue
│       ├── ScrollReveal.vue, GlowCard.vue
│       └── DescriptionList.vue
│
├── composables/                # 组合式函数（11 个 hooks）
│   ├── useAsyncRequest.ts      #   异步请求封装
│   ├── usePagination.ts        #   分页逻辑
│   ├── usePermission.ts        #   权限判断
│   ├── useStudentNav.ts        #   学员导航
│   ├── useLandingData.ts       #   着陆页数据
│   ├── useScrolled.ts          #   滚动监听
│   ├── useToast.ts             #   Toast 消息
│   ├── useNotifications.ts     #   通知管理
│   ├── useECharts.ts           #   ECharts 图表
│   └── useConfetti.ts          #   庆祝动画
│
├── design-system/              # 设计令牌系统
│   ├── composables/useTheme.ts #   主题切换 composable
│   └── tokens/                 #   设计令牌
│       ├── colors.ts           #     颜色系统
│       ├── typography.ts       #     排版系统
│       ├── spacing.ts          #     间距系统
│       └── motion.ts           #     动效系统
│
├── layouts/                    # 布局组件
│   ├── BlankLayout.vue         #   空白布局
│   ├── AuthLayout.vue          #   认证页布局
│   ├── StudentLayout.vue       #   学员主布局
│   ├── AdminLayout.vue         #   管理员布局
│   ├── InstructorLayout.vue    #   讲师布局
│   └── student-themes/         #   学员布局变体
│       ├── StudentNotion.vue   #     Notion 风格
│       ├── StudentLinear.vue   #     Linear 风格
│       ├── StudentVercel.vue   #     Vercel 风格
│       └── StudentSlack.vue    #     Slack 风格
│
├── styles/                     # 样式系统
│   ├── base.css                #   基础重置
│   ├── animations.css          #   动画定义
│   ├── utilities.css           #   工具类
│   └── themes/                 #   主题 CSS
│       ├── tokens.css          #     CSS 变量 Token
│       ├── light.css           #     浅色主题
│       ├── dark.css            #     深色主题
│       ├── warm.css            #     暖色主题
│       └── pro.css             #     专业主题
│
└── utils/                      # 工具函数
    ├── index.ts                #   通用工具
    ├── echarts-theme.ts        #   ECharts 主题配置
    └── categoryColors.ts       #   课程分类颜色映射
```

---

## 主题系统

支持 **4 套主题**，通过 `useTheme()` composable 切换：

| 主题 | 文件 | 风格 |
|------|------|------|
| Light | `styles/themes/light.css` | 明亮清爽 |
| Dark | `styles/themes/dark.css` | 深色护眼 |
| Warm | `styles/themes/warm.css` | 暖色温馨 |
| Pro | `styles/themes/pro.css` | 专业沉稳 |

### 布局变体

学员端支持 **4 种布局风格**：

| 风格 | 组件 | 特点 |
|------|------|------|
| Notion | `StudentNotion.vue` | 简洁文档风 |
| Linear | `StudentLinear.vue` | 现代工具风 |
| Vercel | `StudentVercel.vue` | 极简科技风 |
| Slack | `StudentSlack.vue` | 协作沟通风 |

---

## 组件库设计

采用 **原子设计（Atomic Design）** 三层架构：

| 层级 | 目录 | 数量 | 说明 |
|------|------|------|------|
| **Primitives** | `components/primitives/` | 18 | 基础 UI 原子：Button、Input、Modal 等 |
| **Patterns** | `components/patterns/` | 16+ | 业务模式：PageLayout、DataTable、SearchBar 等 |
| **Effects** | `components/effects/` | 12 | 视觉特效：AuroraBackground、GlassCard、ParallaxSection 等 |

通过 `unplugin-vue-components` 实现**组件自动导入**，无需手动 import。

---

## API 层设计

- `api/request.ts` 封装 Axios 实例，统一处理：
  - **Token 自动注入**：从 Pinia store 读取 accessToken
  - **401 处理**：自动尝试 refreshToken，失败则跳转登录
  - **错误标准化**：统一为 `R<T>` 响应格式
- 每个 API 模块独立一个 `.ts` 文件，类型安全

---

## 构建优化

```typescript
// vite.config.ts - 手动分包
manualChunks: {
  'vue-vendor': ['vue', 'vue-router', 'pinia'],
  'chart-vendor': ['echarts', 'vue-echarts'],
  'util-vendor': ['axios', '@vueuse/core', 'gsap'],
}
```

---

## 许可证

MIT License
