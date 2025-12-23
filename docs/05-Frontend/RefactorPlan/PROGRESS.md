# 前端重构进度文档

> 最后更新：2025-12-23
> 当前阶段：**Phase 4 进行中 🚧**

---

## 📊 整体进度

| 阶段 | 状态 | 描述 |
|------|------|------|
| **Phase 1: Foundation** | ✅ 完成 | 搭建 design-system 目录，配置 Tailwind 和 PrimeVue Unstyled |
| **Phase 2: Patterns** | ✅ 完成 | 实现核心 UI 模式组件 |
| **Phase 3: Migration** | ✅ 完成 | 逐模块迁移至新架构 |
| **Phase 4: Optimization** | 🚧 进行中 | Apple 风格 UI 优化 + 性能优化 |

---

## ✅ Phase 1: Foundation（已完成）

### 1.1 目录结构

```
src/design-system/
├── tokens/                       # Design Tokens (TypeScript)
│   ├── colors.ts                 # 颜色系统（三套主题）
│   ├── typography.ts             # 字体排印系统
│   ├── spacing.ts                # 间距与网格系统
│   ├── motion.ts                 # 动效规范
│   └── index.ts                  # 统一导出
│
├── composables/
│   └── useTheme.ts               # 主题切换 Composable
│
├── primevue/
│   ├── passthrough.ts            # PrimeVue Unstyled PassThrough 预设
│   └── index.ts
│
├── primitives/                   # 原子组件
├── patterns/                     # UI 模式组件
├── init.ts                       # 初始化脚本
└── index.ts                      # 主入口
```

### 1.2 Design Tokens

#### 颜色系统 (`tokens/colors.ts`)

支持三套主题，通过 CSS 变量实现：

| 主题 | 代码 | 特点 |
|------|------|------|
| 晴空白 | `light` | Apple 风格，清新专业，默认主题 |
| 深空黑 | `dark` | OLED 友好，高对比度 |
| 暖阳橙 | `warm` | 护眼舒适，暖色调 |

#### 字体排印 (`tokens/typography.ts`)

```typescript
textStyles.h1    // 页面大标题 - 40px
textStyles.h2    // 区块标题 - 28px
textStyles.h3    // 分组标题 - 20px
textStyles.body  // 正文 - 17px
textStyles.caption // 辅助说明 - 14px
textStyles.label // 标签 - 12px
```

#### 间距系统 (`tokens/spacing.ts`)

- 基于 4px 网格
- 布局间距：`pagePadding: 40px`, `sectionGap: 64px`, `itemGap: 16px`
- 组件尺寸：`inputHeight: 44px`, `radius: 12px`

#### 动效规范 (`tokens/motion.ts`)

```typescript
duration.fast   // 150ms - 快速反馈
duration.normal // 200ms - 标准过渡
duration.slow   // 300ms - 复杂动画
```

### 1.3 主题切换 Composable

```typescript
import { useTheme } from '@/design-system';

const {
  currentTheme,    // Ref<Theme> - 当前主题
  themeInfo,       // 主题详情
  themeOptions,    // 主题选项列表
  setTheme,        // 设置主题
  toggleTheme,     // 循环切换
} = useTheme();
```

---

## ✅ Phase 2: Patterns（已完成）

### 2.1 Primitives（原子组件）

| 组件 | 文件 | 描述 |
|------|------|------|
| **Button** | `primitives/Button.vue` | 按钮组件，支持 primary/secondary/ghost/danger 变体 |
| **Input** | `primitives/Input.vue` | 输入框组件，支持前置/后置图标、清除按钮 |
| **Select** | `primitives/Select.vue` | 下拉选择组件 |
| **Checkbox** | `primitives/Checkbox.vue` | 复选框组件 |
| **Divider** | `primitives/Divider.vue` | 分割线组件，支持缩进 |
| **Modal** | `primitives/Modal.vue` | 模态框组件，支持头部/内容/底部区域 |

#### Button 使用示例

```vue
<script setup>
import { Button } from '@/design-system';
</script>

<template>
  <!-- 主要按钮 -->
  <Button variant="primary">确认</Button>

  <!-- 次要按钮 -->
  <Button variant="secondary">取消</Button>

  <!-- 幽灵按钮 -->
  <Button variant="ghost">更多</Button>

  <!-- 加载状态 -->
  <Button :loading="true">提交中...</Button>

  <!-- 图标按钮 -->
  <Button icon-only rounded>
    <template #icon>
      <IconPlus />
    </template>
  </Button>
</template>
```

#### Input 使用示例

```vue
<script setup>
import { Input } from '@/design-system';
import { ref } from 'vue';

const searchText = ref('');
</script>

<template>
  <Input
    v-model="searchText"
    placeholder="搜索..."
    clearable
  >
    <template #icon-left>
      <IconSearch />
    </template>
  </Input>
</template>
```

#### Modal 使用示例

```vue
<script setup>
import { Modal, Button } from '@/design-system';
import { ref } from 'vue';

const visible = ref(false);
</script>

<template>
  <Button @click="visible = true">打开弹窗</Button>

  <Modal
    v-model="visible"
    title="确认操作"
    @confirm="handleConfirm"
  >
    <p>确定要执行此操作吗？</p>
  </Modal>
</template>
```

### 2.2 Patterns（模式组件）

| 组件 | 文件 | 描述 |
|------|------|------|
| **PageLayout** | `patterns/PageLayout.vue` | 页面布局容器，限制最大宽度 |
| **PageHeader** | `patterns/PageHeader.vue` | 页面头部，包含标题和操作区 |
| **Section** | `patterns/Section.vue` | 区块容器，用于分隔内容 |
| **ListRow** | `patterns/ListRow.vue` | 列表行，替代卡片的核心组件 |
| **DescriptionList** | `patterns/DescriptionList.vue` | 详情列表，Label-Value 对 |
| **FormLayout** | `patterns/FormLayout.vue` | 表单布局容器 |
| **FormItem** | `patterns/FormItem.vue` | 表单项，配合 FormLayout |
| **InsetGroup** | `patterns/InsetGroup.vue` | iOS 风格分组容器 |
| **InsetItem** | `patterns/InsetItem.vue` | iOS 风格分组项 |

#### PageLayout + PageHeader 使用示例

```vue
<script setup>
import { PageLayout, PageHeader, Section, Button } from '@/design-system';
</script>

<template>
  <PageLayout>
    <PageHeader
      title="课程管理"
      subtitle="管理所有课程信息"
    >
      <template #actions>
        <Button variant="primary">新建课程</Button>
      </template>
    </PageHeader>

    <Section title="进行中的课程">
      <!-- 内容 -->
    </Section>
  </PageLayout>
</template>
```

#### ListRow 使用示例

```vue
<script setup>
import { ListRow } from '@/design-system';

const courses = [
  { id: 1, title: 'Vue 3 入门', subtitle: '12 课时', icon: '📚' },
  { id: 2, title: 'TypeScript 进阶', subtitle: '8 课时', icon: '📘' },
];
</script>

<template>
  <div>
    <ListRow
      v-for="course in courses"
      :key="course.id"
      :title="course.title"
      :subtitle="course.subtitle"
      @click="navigateTo(course.id)"
    >
      <template #icon>
        <span class="text-2xl">{{ course.icon }}</span>
      </template>
      <template #meta>
        <span class="text-sm">已完成 60%</span>
      </template>
    </ListRow>
  </div>
</template>
```

#### DescriptionList 使用示例

```vue
<script setup>
import { DescriptionList } from '@/design-system';

const userInfo = [
  { label: '姓名', value: '张三' },
  { label: '邮箱', value: 'zhangsan@example.com' },
  { label: '状态', value: '已激活', type: 'success' },
];
</script>

<template>
  <DescriptionList :items="userInfo" />
</template>
```

#### InsetGroup 使用示例（iOS 风格设置页）

```vue
<script setup>
import { InsetGroup, InsetItem } from '@/design-system';
</script>

<template>
  <InsetGroup title="账户设置">
    <InsetItem label="用户名" value="zhangsan" />
    <InsetItem label="邮箱" value="zhangsan@example.com" />
    <InsetItem label="修改密码" clickable show-arrow last />
  </InsetGroup>

  <InsetGroup title="偏好设置" footer="更改主题后立即生效">
    <InsetItem label="深色模式">
      <Checkbox v-model="darkMode" />
    </InsetItem>
    <InsetItem label="通知" clickable show-arrow last />
  </InsetGroup>
</template>
```

#### FormLayout 使用示例

```vue
<script setup>
import { FormLayout, FormItem, Input, Select, Button } from '@/design-system';
</script>

<template>
  <FormLayout>
    <FormItem label="课程名称" required>
      <Input v-model="form.name" placeholder="请输入课程名称" />
    </FormItem>

    <FormItem label="课程分类" required>
      <Select v-model="form.category" :options="categories" />
    </FormItem>

    <FormItem label="课程描述" help="简要描述课程内容">
      <Input v-model="form.description" placeholder="请输入描述" />
    </FormItem>

    <FormItem>
      <Button variant="primary" type="submit">保存</Button>
    </FormItem>
  </FormLayout>
</template>
```

---

## 📝 完整使用指南

### 导入方式

```typescript
// 推荐：按需导入
import {
  Button,
  Input,
  ListRow,
  PageLayout
} from '@/design-system';

// 导入主题切换
import { useTheme } from '@/design-system';

// 导入 Design Tokens
import { textStyles, spacing } from '@/design-system';
```

### 初始化 Design System

在 `main.ts` 中：

```typescript
import { createApp } from 'vue';
import PrimeVue from 'primevue/config';
import { initDesignSystem, primeVueConfig } from '@/design-system';

// 初始化 Design System
initDesignSystem();

const app = createApp(App);

// 注册 PrimeVue（Unstyled 模式）
app.use(PrimeVue, primeVueConfig);

app.mount('#app');
```

---

## ⏳ Phase 3: Migration（进行中）

### 3.1 Auth 模块迁移 ✅

| 任务 | 状态 | 说明 |
|------|------|------|
| 创建 AuthLayout.vue | ✅ 完成 | 认证页面居中布局组件 |
| 迁移 Login.vue | ✅ 完成 | 使用 Design System 组件重构 |
| 迁移 Register.vue | ✅ 完成 | 使用 Design System 组件重构 |

#### 新增组件

**AuthLayout** (`patterns/AuthLayout.vue`)

认证页面专用布局组件，特点：
- 极简居中设计
- 纯白背景，无复杂装饰
- 包含 Logo、标题、表单区域和底部链接
- 支持额外内容插槽（如演示账号）

```vue
<AuthLayout
  title="Welcome Back"
  subtitle="Sign in to continue"
  footer-text="Don't have an account?"
  footer-link-text="Sign up"
  footer-link-to="/register"
>
  <!-- 表单内容 -->
  <template #extra>
    <!-- 额外内容 -->
  </template>
</AuthLayout>
```

#### 迁移变更

**Login.vue / Register.vue 变更：**
- 移除 Element Plus 依赖（el-form, el-input, el-button）
- 使用 Design System 组件（AuthLayout, FormLayout, FormItem, Input, Button）
- 错误信息显示在输入框下方（红色小字），不使用弹窗
- 使用 TypeScript 重写
- 使用 SVG 图标替代 Element Plus Icons

### 3.2 通用组件迁移 ✅

| 任务 | 状态 | 说明 |
|------|------|------|
| 创建 EmptyState.vue | ✅ 完成 | 空状态展示组件 |
| 创建 ProgressRing.vue | ✅ 完成 | 环形进度条组件 |

#### 新增组件

**EmptyState** (`primitives/EmptyState.vue`)

空状态展示组件，特点：
- 支持图标/emoji 展示
- 支持标题和描述
- 支持操作按钮
- 支持三种尺寸（sm/md/lg）

```vue
<EmptyState
  emoji="📭"
  title="暂无消息"
  description="您还没有收到任何消息"
  action-text="刷新"
  @action="handleRefresh"
/>
```

**ProgressRing** (`primitives/ProgressRing.vue`)

环形进度条组件，特点：
- SVG 绘制，支持自定义尺寸
- 支持多种颜色类型（primary/success/warning/danger/info）
- 支持自定义中心内容
- 平滑动画过渡

```vue
<ProgressRing
  :percentage="75"
  :size="100"
  type="success"
/>
```

### 3.3 学生端页面迁移（进行中）

| 页面 | 状态 | 说明 |
|------|------|------|
| Achievements.vue | ✅ 完成 | 成就展示页 |
| MyCourses.vue | ✅ 完成 | 我的课程页 |
| Settings.vue | ✅ 完成 | 设置页面 |
| Home.vue | ✅ 完成 | 首页 |
| Dashboard.vue | ✅ 完成 | 仪表盘 |
| PersonalCenter.vue | ✅ 完成 | 个人中心 |
| UserProfile.vue | ✅ 完成 | 用户画像 |
| CourseList.vue | ✅ 完成 | 课程列表页 |
| CourseDetail.vue | ✅ 完成 | 课程详情页 |
| CourseStudy.vue | ✅ 完成 | 课程学习页 |
| LearningCenter.vue | ✅ 完成 | 学习中心页 |
| LearningPlan.vue | ✅ 完成 | 学习计划页 |
| LearningReport.vue | ✅ 完成 | 学习报告页 |
| SmartRecommend.vue | ✅ 完成 | 智能推荐页 |

#### 迁移变更

**Achievements.vue 变更：**
- 移除 Element Plus 依赖（el-dialog, el-button）
- 使用 Design System 组件（PageLayout, EmptyState, Modal, Button）
- 使用内联 SVG 图标替代 lucide-vue-next
- 使用 TypeScript 重写

**MyCourses.vue 变更：**
- 移除 Element Plus 依赖（el-select, el-pagination, el-dropdown）
- 使用 Design System 组件（PageLayout, Input, Button, Select, EmptyState）
- 自定义下拉菜单替代 el-dropdown
- 自定义分页组件替代 el-pagination
- 使用 TypeScript 重写

**Settings.vue 变更：**
- 移除 Element Plus 依赖（el-dialog, el-form, el-switch, el-alert）
- 使用 Design System 组件（PageLayout, PageHeader, InsetGroup, InsetItem, Switch, Modal, FormLayout, FormItem, Input, Button）
- 新增 Switch 组件到 Design System
- 使用内联 SVG 图标替代 @element-plus/icons-vue
- 使用 TypeScript 重写

**Home.vue 变更：**
- 移除 lucide-vue-next 依赖
- 使用 Design System 组件（PageLayout, Button, EmptyState）
- 使用内联 SVG 图标
- 使用 TypeScript 重写

**Dashboard.vue 变更：**
- 移除 Element Plus 依赖（v-loading, ElMessage）
- 移除 lucide-vue-next 依赖
- 使用 Design System 组件（PageLayout, Button, EmptyState, ProgressRing）
- 使用内联 SVG 图标
- 使用 TypeScript 重写
- 保留 ECharts 图表功能
n**PersonalCenter.vue 变更：**
- 移除 Element Plus 依赖（el-avatar, el-icon, el-form, el-input, el-dialog, el-button, el-alert）
- 使用 Design System 组件（PageLayout, Button, Input, Modal, FormLayout, FormItem, InsetGroup, InsetItem, Avatar, Alert）
- 新增 Avatar 组件到 Design System（支持图片、文字回退、多种尺寸）
- 新增 Alert 组件到 Design System（支持 info/success/warning/error 类型）
- 使用内联 SVG 图标替代 @element-plus/icons-vue
- 使用 TypeScript 重写
- 自定义 Tab 切换器替代 segmented 样式
n**UserProfile.vue 变更：**
- 移除 Element Plus 依赖（v-loading, el-avatar, el-tag, el-icon, el-rate, el-empty, el-descriptions, el-timeline, el-dialog, el-form, el-select, el-checkbox-group, el-slider, el-button）
- 使用 Design System 组件（PageLayout, Button, Input, Select, Checkbox, Modal, FormLayout, FormItem, InsetGroup, InsetItem, Avatar, Tag, Timeline, TimelineItem, EmptyState）
- 新增 Tag 组件到 Design System（支持多种类型和尺寸）
- 新增 Timeline/TimelineItem 组件到 Design System
- 使用内联 SVG 图标替代 @element-plus/icons-vue
- 使用 TypeScript 重写
- 保留 ECharts 图表功能（雷达图、柱状图）
n**CourseList.vue 变更：**
- 移除 Element Plus 依赖（el-pagination）
- 移除 lucide-vue-next 依赖（BookOpen, Check, Search, SearchX, SlidersHorizontal）
- 使用 Design System 组件（PageLayout, Button, Input, Select, InsetGroup, InsetItem, EmptyState）
- 使用内联 SVG 图标
- 使用 TypeScript 重写
- 自定义分页组件替代 el-pagination

**CourseDetail.vue 变更：**
- 移除 Element Plus 依赖（v-loading, ElMessage, ElMessageBox）
- 移除 lucide-vue-next 依赖（Calendar, CalendarX2, ChevronLeft, Clock, FileText, TriangleAlert, UserCircle, Users, Timer, Brain, Cloud, Code2, Database, Layout, Server）
- 使用 Design System 组件（PageLayout, Section, Button, Tag, Modal, EmptyState）
- 使用内联 SVG 图标（课程分类图标、时间图标、日历图标等）
- 使用 TypeScript 重写，添加 Course 和 Session 接口定义
- 使用自定义 Toast 替代 ElMessage
- 使用 Modal 组件替代 ElMessageBox.confirm

**CourseStudy.vue 变更：**
- 移除 Element Plus 依赖（el-button, el-card, el-tag, el-icon, el-dialog, el-form, el-form-item, el-input, el-empty, el-progress, ElMessage）
- 移除 @element-plus/icons-vue 依赖（ArrowLeft, Clock, VideoPlay, CircleCheck）
- 使用 Design System 组件（Button, Input, Modal, Tag, EmptyState, Section, FormLayout, FormItem）
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 Course, Chapter, Progress, Note 接口定义
- 使用自定义 Toast 替代 ElMessage
- 保留 video.js 视频播放器和 ECharts 图表功能

**LearningCenter.vue 变更：**
- 移除 ElMessage 依赖
- 移除 lucide-vue-next 依赖（CalendarDays, CheckCircle2, ChevronLeft, ChevronRight, Clock, Flame, GraduationCap, Sparkles, Trophy, BookOpen）
- 使用 Design System 组件（Button, EmptyState）
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 Dashboard, TodayCheckin 接口定义
- 使用自定义 Toast 替代 ElMessage

**LearningPlan.vue 变更：**
- 移除 Element Plus 依赖（v-loading, el-dropdown, el-dropdown-menu, el-dropdown-item, el-icon, el-dialog, el-form, el-form-item, el-input, el-date-picker, el-input-number, el-select, el-option, ElMessage, ElMessageBox）
- 移除 @element-plus/icons-vue 依赖（Plus, MoreFilled, Calendar, Check）和 lucide-vue-next 依赖（Clock, Target）
- 使用 Design System 组件（Button, Input, Select, Modal, Tag, EmptyState, FormLayout, FormItem）
- 使用内联 SVG 图标
- 使用自定义下拉菜单替代 el-dropdown
- 使用原生 input[type="date"] 替代 el-date-picker
- 使用 TypeScript 重写，添加 Plan, Course 接口定义
- 使用自定义 Toast 和 Confirm Dialog 替代 ElMessage 和 ElMessageBox

**LearningReport.vue 变更：**
- 移除 Element Plus 依赖（v-loading, el-date-picker, ElMessage）
- 移除 lucide-vue-next 依赖（ArrowDown, ArrowUp, Award, BarChart3, BookOpen, CalendarDays, CheckCircle2, Clock, Flame, Gauge, Lightbulb）
- 使用 Design System 组件（Select, EmptyState）
- 使用 Select 组件生成周/月/年选择器替代 el-date-picker
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 Report, SelectOption 接口定义
- 使用自定义 Toast 替代 ElMessage
- 使用自定义 Loading Overlay 替代 v-loading
- 保留 ECharts 图表功能（趋势图、饼图）

**SmartRecommend.vue 变更：**
- 移除 Element Plus 依赖（el-form, el-form-item, el-input, ElMessage）
- 移除 lucide-vue-next 依赖（ArrowRight, Brain, Info, Loader2, Sparkles）
- 使用 Design System 组件（Button, EmptyState）
- 使用原生 textarea 替代 el-input
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 Course, Recommendation 接口定义
- 使用自定义 Toast 替代 ElMessage

### 3.4 管理端页面迁移 ✅

| 页面 | 状态 | 说明 |
|------|------|------|
| Dashboard.vue | ✅ 完成 | 管理后台仪表盘 |
| Courses.vue | ✅ 完成 | 课程管理页 |
| Enrollments.vue | ✅ 完成 | 报名管理页 |
| Sessions.vue | ✅ 完成 | 班期管理页 |
| Users.vue | ✅ 完成 | 用户管理页 |

#### 迁移变更

**Dashboard.vue 变更：**
- 使用 Design System 组件（PageLayout, PageHeader, Section, ListRow）
- 使用内联 SVG 图标
- 使用 TypeScript 重写
- 保留 ECharts 图表功能

**Courses.vue 变更：**
- 移除 Element Plus 依赖（el-form, el-button, el-input, el-select, el-dialog）
- 保留 el-table, el-pagination, el-input-number（表格和分页功能）
- 使用 Design System 组件（PageLayout, PageHeader, Section, Button, Input, Select, Modal, FormLayout, FormItem, Tag）
- 实现自定义 Toast 函数替代 ElMessage
- 实现 confirmDialog 函数替代 ElMessageBox.confirm
- 使用内联 SVG 图标替代 @element-plus/icons-vue
- 使用 TypeScript 重写，添加 Course, SearchForm, CourseForm 接口定义

**Enrollments.vue 变更：**
- 移除 Element Plus 依赖（el-select）和 lucide-vue-next 依赖
- 保留 el-table, el-pagination（表格和分页功能）
- 使用 Design System 组件（PageLayout, PageHeader, Section, Button, Select, Tag）
- 实现自定义 Toast 函数替代 ElMessage
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 Enrollment, Session, Pagination 接口定义
- CSV 导出功能保留

**Sessions.vue 变更：**
- 移除 Element Plus 依赖（el-form, el-button, el-input, el-select, el-dialog, el-tag）
- 保留 el-table, el-pagination, el-date-picker, el-input-number（表格、分页、日期选择功能）
- 使用 Design System 组件（PageLayout, PageHeader, Section, Button, Input, Select, Modal, FormLayout, FormItem, Tag）
- 实现自定义 Toast 函数替代 ElMessage
- 实现 showConfirmDialog 函数替代 ElMessageBox.confirm
- 使用内联 SVG 图标替代 @element-plus/icons-vue
- 使用 TypeScript 重写，添加 SessionRow, SessionForm, CourseOption, InstructorOption 接口定义

**Users.vue 变更：**
- 移除 Element Plus 依赖（el-form, el-dialog, el-switch）和 lucide-vue-next 依赖
- 保留 el-table, el-pagination（表格和分页功能）
- 使用 Design System 组件（PageLayout, PageHeader, Section, Button, Input, Select, Modal, FormLayout, FormItem, Tag, Switch）
- 实现自定义 Toast 函数替代 ElMessage
- 实现 confirmDialog 函数替代 ElMessageBox.confirm
- 实现 promptDialog 函数替代 ElMessageBox.prompt（重置密码功能）
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 User, UserForm, SearchForm, FormErrors 接口定义

### 3.5 讲师端页面迁移 ✅

| 页面 | 状态 | 说明 |
|------|------|------|
| MySessions.vue | ✅ 完成 | 讲师班期管理页 |

#### 迁移变更

**MySessions.vue 变更：**
- 移除 Element Plus 依赖（el-dialog, el-button, el-tag, el-empty, el-icon）
- 移除 @element-plus/icons-vue 依赖（User, Download）
- 移除 lucide-vue-next 依赖（CalendarDays）
- 保留 el-table（表格功能）
- 使用 Design System 组件（PageLayout, PageHeader, Section, Button, Modal, Tag, EmptyState）
- 实现自定义 Toast 函数替代 ElMessage
- 使用内联 SVG 图标
- 使用 TypeScript 重写，添加 Session, StudentEnrollment, ToastState 接口定义
- CSV 导出功能保留

### 3.6 待迁移模块

| 模块 | 状态 | 优先级 |
|------|------|--------|
| student 模块 | ✅ 完成 | 高 |
| admin 模块 | ✅ 完成 | 中 |
| instructor 模块 | ✅ 完成 | 低 |

---

## 📝 完整使用指南

### Design System 目录结构

```
src/design-system/
├── tokens/
│   ├── colors.ts
│   ├── typography.ts
│   ├── spacing.ts
│   ├── motion.ts
│   └── index.ts
├── composables/
│   └── useTheme.ts
├── primevue/
│   ├── passthrough.ts
│   └── index.ts
├── primitives/
│   ├── Button.vue
│   ├── Input.vue
│   ├── Select.vue
│   ├── Checkbox.vue
│   ├── Switch.vue               # 新增：开关组件
│   ├── Divider.vue
│   ├── Modal.vue
│   ├── EmptyState.vue           # 新增：空状态组件
│   ├── ProgressRing.vue         # 新增：环形进度条
│   ├── Avatar.vue               # 新增：头像组件
│   ├── Alert.vue                # 新增：警告提示组件
│   ├── Tag.vue                  # 新增：标签组件
│   ├── Timeline.vue             # 新增：时间线组件
│   ├── TimelineItem.vue         # 新增：时间线项组件
│   └── index.ts
├── patterns/
│   ├── PageLayout.vue
│   ├── PageHeader.vue
│   ├── Section.vue
│   ├── ListRow.vue
│   ├── DescriptionList.vue
│   ├── FormLayout.vue
│   ├── FormItem.vue
│   ├── InsetGroup.vue
│   ├── InsetItem.vue
│   ├── AuthLayout.vue          # 新增：认证页面布局
│   └── index.ts
├── init.ts
└── index.ts
```

---

## ⚠️ 注意事项

1. **Element Plus 过渡期**：当前项目仍使用 Element Plus，迁移时需要两者共存
2. **TypeScript**：Design System 使用 TypeScript
3. **样式隔离**：业务组件应仅使用 patterns 中的组件，避免直接使用 Tailwind 任意值
4. **主题切换**：CSS 变量会在运行时动态更新，确保组件使用语义化颜色类

---

## 📌 下一步行动

1. [x] 创建示例页面验证设计系统
2. [x] 开始 auth 模块迁移（Login.vue, Register.vue）
3. [x] 迁移通用组件（EmptyState, ProgressRing）
4. [x] 迁移学生端页面（14个页面）
5. [x] 迁移管理端页面（5个页面）
6. [x] 迁移讲师端页面（1个页面）

---

## 🎉 重构完成总结

### 迁移统计

| 模块 | 页面数 | 状态 |
|------|--------|------|
| Auth | 2 | ✅ 完成 |
| Student | 14 | ✅ 完成 |
| Admin | 5 | ✅ 完成 |
| Instructor | 1 | ✅ 完成 |
| **总计** | **22** | **✅ 全部完成** |

### 新增 Design System 组件

**Primitives（原子组件）：**
- Button, Input, Select, Checkbox, Switch, Divider, Modal
- EmptyState, ProgressRing, Avatar, Alert, Tag, Timeline, TimelineItem

**Patterns（模式组件）：**
- PageLayout, PageHeader, Section, ListRow, DescriptionList
- FormLayout, FormItem, InsetGroup, InsetItem, AuthLayout

### 保留的 Element Plus 组件

由于 Design System 暂无替代方案，以下组件暂时保留：
- `el-table` / `el-table-column` - 表格组件
- `el-pagination` - 分页组件
- `el-date-picker` - 日期选择器
- `el-input-number` - 数字输入框
- `v-loading` - 加载指令
