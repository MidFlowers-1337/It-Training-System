# Design System v6 - Apple UI 风格（落地版）

> 目标：在 **三套主题（default / warm / dark）不变** 的前提下，将前端整体视觉与交互统一到更接近 Apple 官网的“克制、留白、轻分隔、玻璃质感、内容优先”的 UI 语言，并给出可直接执行的改造指南。

---

## 0. 范围与约束

### 不变项（必须保持）
- 主题 token 不改：`it-training-frontend/src/styles/variables.css`
- 主题切换机制不改（data-theme + localStorage）：`it-training-frontend/src/composables/useTheme.js`

### 允许变更
- 增加语义层 token（从现有 token 派生，不引入“第四套主题”）
- 重构全局样式与组件实现
- 替换缺失依赖（例如移除未安装的 heroicons）

---

## 1. Apple UI 风格落地规则（可执行）

### 1.1 视觉层级
- 页面背景：使用 `bg-bg-primary`，内容容器使用 `bg-bg-secondary` + 1px 边框（`border-border-color`）
- 分隔线：优先使用 1px hairline（`border-border-light` / `border-border-color`），避免粗分割
- 阴影：默认 `--shadow-sm`，hover 才提升为 `--shadow-md`，避免“厚重投影”

### 1.2 排版与留白
- 标题：更紧凑的字距（`tracking-tight`），更明确的字号层级（h1/h2/h3）
- 内容：优先 `text-text-secondary`，强调信息用 `text-text-primary`
- 留白：默认块级间距至少 `space-6`，卡片内边距建议 `p-5/p-6`

### 1.3 交互与可访问性
- Focus：统一 `:focus-visible` 轮廓，避免在鼠标交互时闪烁（已在全局样式中处理）
- Motion：默认过渡时长 `--transition-normal`，避免长而花哨的动画

---

## 2. 主题与语义层（你要改 UI 的“正确入口”）

### 2.1 主题 token（不改）
三套主题定义在：
- `it-training-frontend/src/styles/variables.css`

### 2.2 v6 语义桥接层（新增）
为“Apple 风格 + Element Plus + 旧代码兼容”提供统一入口：
- `it-training-frontend/src/styles/semantic.css`

内容包含：
1. **Element Plus 变量桥接**：把 `--el-*` 映射到现有主题 token（字体、背景、文字、边框、圆角、阴影）
2. **Legacy 兼容变量**：补齐旧页面依赖的 `--gradient-primary` / `--gradient-hero` / `--primary-bg`（均由 primary token 派生）

> 以后如果你要“统一组件风格”，优先改 `semantic.css`（全局生效），其次才改某个页面/组件的 scoped CSS。

### 2.3 RGB token 与透明度（关键）
Apple 官网风格大量依赖“半透明 / 毛玻璃 / 轻分隔”，需要 Tailwind 的 `bg-*/70`、`border-*/60` 等 alpha 类 **真正生效**。  
因此 v6 增加了 `--*-rgb` 派生变量（不改变三套主题的色值，只把 hex 映射为 rgb 三元组），并在 Tailwind 里统一用：

`rgb(var(--xxx-rgb) / <alpha-value>)`

落点：
- RGB 语义变量：`it-training-frontend/src/styles/semantic.css`
- Tailwind 映射：`it-training-frontend/tailwind.config.js`

推荐用法（Apple 风格最常用三件套）：
- `bg-bg-secondary/70`：玻璃卡底色
- `border-border-color/60`：hairline 边/轻分隔
- `bg-primary/10`：轻强调（选中态/Badge/Active menu）

---

## 3. Tailwind 约定（避免样式失控）

### 3.1 颜色
Tailwind 已映射主题 token（背景、文字、边框、primary 等），并补了 `secondary` 用于渐变：
- `it-training-frontend/tailwind.config.js`

推荐用法：
- `bg-bg-primary` / `bg-bg-secondary`
- `text-text-primary` / `text-text-secondary` / `text-text-muted`
- `border-border-color` / `border-border-light`
- `bg-primary` / `text-primary` / `from-primary-light to-secondary`

### 3.2 全局可复用 class（建议优先使用）
这些 class 在全局样式里维护，适合作为“设计系统组件基座”：
- `btn` / `btn-primary` / `btn-secondary` / `btn-ghost`
- `card` / `card-hover`
- `input`
- `badge` / `badge-primary` / `badge-secondary`
- `page` / `page-hero`
- `glass`
- `inset-group` / `inset-item` / `inset-divider`
- `segmented` / `segmented-item`

位置：
- `it-training-frontend/src/style.css`

---

## 4. 页面模板（直接套用）

### 4.1 标准页（Hero + 内容块）
适用：大多数业务页（学生端/管理端/讲师端）。

```vue
<template>
  <div class="page">
    <section class="page-hero glass p-7 md:p-10">
      <!-- 标题 / 描述 / 主操作按钮 -->
    </section>

    <section class="card p-6 md:p-8">
      <!-- 主要内容 -->
    </section>
  </div>
</template>
```

要点：
- Hero 只做“氛围 + 信息架构”，避免大色块铺满；用轻微光斑（`bg-primary/10` + blur）即可。
- 信息密度高的区域优先用 `inset-group`（iOS Settings 风），避免一堆厚边框卡片。

### 4.2 Settings / Inset（iOS 风列表）
适用：个人中心、设置页、详情页的“属性列表”。

```vue
<div class="inset-group">
  <div class="inset-item">
    <span class="text-sm text-text-secondary">邮箱</span>
    <span class="text-sm font-medium text-text-primary">xxx@xx.com</span>
  </div>
  <div class="inset-divider"></div>
  <div class="inset-item">...</div>
</div>
```

### 4.3 列表/表格页（Filter + Table）
适用：管理端的 Users / Courses / Enrollments 等。

建议结构：
- 顶部 `page-hero glass`：标题、统计、主操作
- Filters：`glass` 容器 + `input` / `el-select` / `btn`
- Table：`card` 包裹 `el-table`，避免在 scoped CSS 里硬覆盖表格颜色

### 4.4 空状态（EmptyState）
统一使用 `EmptyState`，不要放 emoji 占位。

- 组件：`it-training-frontend/src/components/EmptyState.vue`
- 用法示例：`it-training-frontend/src/views/student/LearningPlan.vue`

### 4.5 图表（ECharts）主题适配
规则：
1. 颜色只从 `--*-rgb` 读（禁止写死 `#xxxxxx`）
2. 监听 `data-theme` 变化，重绘图表（MutationObserver）

参考实现：
- `it-training-frontend/src/views/student/Dashboard.vue:1`
- `it-training-frontend/src/views/student/LearningReport.vue:1`
- `it-training-frontend/src/views/admin/Dashboard.vue:1`

---

## 5. 组件重写基线（已经改到位的示例）

### 5.1 CourseCard（卡片组件）
- `it-training-frontend/src/components/CourseCard.vue`
- 改动点：去掉未定义 CSS 变量依赖，统一使用 `card-hover`、语义色与 Lucide 图标，进度条使用 Tailwind 渐变

### 5.2 EmptyState（空状态）
- `it-training-frontend/src/components/EmptyState.vue`
- 改动点：移除对 Element Plus Button 的强依赖，默认使用 `btn btn-primary`，保留 slot 扩展

### 5.3 FloatingActionButton（悬浮入口）
- `it-training-frontend/src/components/FloatingActionButton.vue`
- 改动点：Apple 风格的“克制浮层”，轻提示，不依赖旧 `--gradient-primary`

### 5.4 ProgressRing（进度环）
- `it-training-frontend/src/components/ProgressRing.vue`
- 改动点：修复 danger 使用未定义变量的问题，统一为 `--error-color`

### 5.5 StudentLayout（学生端壳）
- `it-training-frontend/src/layouts/StudentLayout.vue`
- 改动点：挂载 `FloatingActionButton`，提供统一的 AI 入口

---

## 6. 页面改造顺序（可直接照做）

建议按“用户可感知度 + 复用收益”排序：
1. 全局（已完成）：`semantic.css`、`style.css`
2. 组件库（已完成一部分）：CourseCard / EmptyState / FloatingActionButton / ProgressRing
3. 学生端关键路径：登录、首页、课程列表、课程详情、学习中心
4. 管理端：Dashboard / Users / Enrollments（优先统一图标与表格）
5. 讲师端：移除 hardcode 颜色，改用主题 token（下一步建议）

---

## 7. 开发与构建命令（Windows PowerShell 注意）

由于 PowerShell 执行策略可能禁用 `npm.ps1`，建议使用：
- `npm.cmd run dev`
- `npm.cmd run build`

---

## 8. 常见坑（避免返工）

- Tailwind `@apply` 引用的 class 必须存在；如果要用渐变 `to-secondary`，确保 `tailwind.config.js` 有 `colors.secondary`
- 不要在页面里写死颜色（例如 `#2c3e50`），否则三主题切换会失效
- 旧页面若引用了 `--gradient-primary` 等变量，优先在 `semantic.css` 补齐，再逐步迁移到 Tailwind/语义 class

---

## 9. 迁移检查清单（每页提交前自检）
- 结构：是否统一使用 `page`（可选 `page-hero glass`）？
- 色值：`it-training-frontend/src` 中是否只在 `styles/variables.css` 出现 `#xxxxxx`？
- 透明度：是否用 `bg-*/70`、`border-*/60`、`bg-primary/10` 替代“厚边框/重底色”？
- 分隔线：是否优先 hairline（`--separator` / `border-*/60`），避免粗分割？
- scoped CSS：是否只保留必要布局，不做主题色/组件皮肤覆盖？
- 三主题：切换 default / warm / dark 后，文字可读、对比正常、图表同步？
- 构建：`npm.cmd run build` 是否通过？

---

## 参考（关键落点）
- `it-training-frontend/src/styles/variables.css:1`
- `it-training-frontend/src/styles/semantic.css:1`
- `it-training-frontend/src/style.css:1`
- `it-training-frontend/tailwind.config.js:1`
- `it-training-frontend/src/components/CourseCard.vue:1`
- `it-training-frontend/src/components/EmptyState.vue:1`
- `it-training-frontend/src/components/FloatingActionButton.vue:1`
- `it-training-frontend/src/components/ProgressRing.vue:1`
- `it-training-frontend/src/layouts/StudentLayout.vue:1`
