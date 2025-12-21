# 12. Phase 3: Migration 总览

> 本文档定义 Phase 3 迁移阶段的整体策略、原则和执行计划。

---

## 1. 迁移目标

将现有基于 Element Plus 的页面逐步迁移到新的 Design System 架构，实现：

- **视觉一致性**：所有页面采用 Apple-like Minimal 风格
- **代码规范化**：业务组件仅使用 `design-system/patterns` 中的组件
- **渐进式迁移**：Element Plus 和 Design System 共存，逐页面替换

---

## 2. 迁移原则

### 2.1 共存策略

```
┌─────────────────────────────────────────────────────────────┐
│                     迁移过渡期架构                           │
├─────────────────────────────────────────────────────────────┤
│  Element Plus (旧)          │  Design System (新)           │
│  - el-button               │  - Button                     │
│  - el-input                │  - Input                      │
│  - el-form                 │  - FormLayout + FormItem      │
│  - el-card                 │  - ListRow / Section          │
│  - el-dialog               │  - Modal                      │
│  - ElMessage               │  - Toast (待实现)              │
├─────────────────────────────────────────────────────────────┤
│  两套系统可同时存在于不同页面，但单个页面应尽量使用单一系统    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 迁移优先级

| 优先级 | 模块 | 理由 |
|--------|------|------|
| P0 | auth | 入口页面，用户第一印象，页面简单适合试水 |
| P1 | 通用组件 | EmptyState, ProgressRing 等被多处引用 |
| P2 | student | 主要用户群体，页面数量多 |
| P3 | admin | 管理后台，功能复杂 |
| P4 | instructor | 讲师端，页面较少 |

### 2.3 单页面迁移流程

```
1. 分析现有页面
   ├── 识别使用的 Element Plus 组件
   ├── 分析页面结构和布局
   └── 列出需要替换的组件映射

2. 创建迁移分支 (可选)
   └── git checkout -b refactor/migrate-xxx

3. 逐组件替换
   ├── 导入 Design System 组件
   ├── 替换模板中的组件
   ├── 调整 props 和事件绑定
   └── 移除 Element Plus 导入

4. 样式清理
   ├── 移除自定义 CSS
   ├── 移除任意 Tailwind 类
   └── 使用语义化 token 类

5. 功能验证
   ├── 表单提交正常
   ├── 交互反馈正常
   └── 响应式布局正常

6. 代码审查
   └── 确保符合设计规范
```

---

## 3. 组件映射表

### 3.1 基础组件映射

| Element Plus | Design System | 备注 |
|--------------|---------------|------|
| `el-button` | `Button` | 支持 primary/secondary/ghost/danger |
| `el-input` | `Input` | 支持 clearable, icon slots |
| `el-select` | `Select` | 基于 PrimeVue Unstyled |
| `el-checkbox` | `Checkbox` | 简化 API |
| `el-dialog` | `Modal` | 支持 header/content/footer slots |
| `el-divider` | `Divider` | 支持 indented 变体 |

### 3.2 布局组件映射

| Element Plus | Design System | 备注 |
|--------------|---------------|------|
| `el-card` | `Section` / `ListRow` | 根据内容类型选择 |
| `el-form` | `FormLayout` | 配合 FormItem 使用 |
| `el-form-item` | `FormItem` | 简化的表单项 |
| 自定义布局 | `PageLayout` | 页面容器，限制最大宽度 |
| 自定义标题 | `PageHeader` | 统一页面头部样式 |

### 3.3 复合组件映射

| 场景 | Element Plus | Design System |
|------|--------------|---------------|
| 列表展示 | `el-table` / `el-card` 循环 | `ListRow` 循环 |
| 详情展示 | Grid + `el-descriptions` | `DescriptionList` |
| 设置页 | `el-form` + `el-card` | `InsetGroup` + `InsetItem` |
| 空状态 | 自定义 | `EmptyState` (待迁移) |

---

## 4. 迁移文档索引

| 文档 | 模块 | 状态 |
|------|------|------|
| [13-Migration-Auth.md](./13-Migration-Auth.md) | auth 模块 | ⏳ 待编写 |
| [14-Migration-Common.md](./14-Migration-Common.md) | 通用组件 | ⏳ 待编写 |
| [15-Migration-Student.md](./15-Migration-Student.md) | student 模块 | ⏳ 待编写 |
| [16-Migration-Admin.md](./16-Migration-Admin.md) | admin 模块 | ⏳ 待编写 |
| [17-Migration-Instructor.md](./17-Migration-Instructor.md) | instructor 模块 | ⏳ 待编写 |

---

## 5. 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Element Plus 与 DS 样式冲突 | 视觉异常 | 使用 scoped styles，避免全局污染 |
| 表单验证逻辑迁移 | 功能缺失 | 保留 el-form 验证逻辑，仅替换 UI |
| 复杂组件无对应 | 迁移受阻 | 在 DS 中新增所需 pattern |
| 主题切换兼容性 | 样式不一致 | 确保所有组件使用 CSS 变量 |

---

## 6. 验收标准

### 6.1 单页面验收

- [ ] 无 Element Plus 组件导入（除特殊说明外）
- [ ] 无自定义 CSS / style 块
- [ ] 无任意 Tailwind 类（如 `w-[200px]`）
- [ ] 所有颜色使用语义化 token
- [ ] 响应式布局正常（mobile/tablet/desktop）
- [ ] 主题切换正常（light/dark/warm）

### 6.2 模块验收

- [ ] 模块内所有页面完成迁移
- [ ] 迁移文档更新完成
- [ ] PROGRESS.md 进度更新
- [ ] 无功能回归

---

**下一步**: 开始编写 `13-Migration-Auth.md`，完成 auth 模块迁移指南。
