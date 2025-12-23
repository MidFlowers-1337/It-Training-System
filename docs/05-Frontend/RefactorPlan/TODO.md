# 前端重构遗留问题清单

> **创建时间**: 2025-12-22
> **更新时间**: 2025-12-23
> **状态**: ✅ 所有关键问题已解决

---

## ✅ P0 级别（阻断性问题）- 已完成

### 1. 核心入口文件未迁移 ✅

**问题描述**:
项目仍在使用 `src/main.js`，未迁移到 `src/main.ts`，导致 Design System 完全未初始化。

**解决方案**:
- ✅ 创建 `src/main.ts`，初始化 Design System 和 PrimeVue
- ✅ 更新 `index.html` 引用新的入口文件
- ✅ 删除旧的 `src/main.js`
- ✅ 保留 ElementPlus 兼容未重构页面

---

## ✅ P1 级别（必须修复）- 已完成

### 2. 404 错误页面未重构 ✅

**问题描述**:
`src/views/error/404.vue` 未纳入重构范围，仍依赖旧的图标库和样式系统。

**解决方案**:
- ✅ 移除 `lucide-vue-next` 依赖，改用内联 SVG
- ✅ 使用 Design System 的 Button 组件
- ✅ 统一样式规范

---

## ✅ P2 级别（优化项）- 已完成

### 3. 依赖清理 ✅

**问题描述**:
由于部分页面未完成重构，旧依赖无法完全移除。

**解决方案**:
- ✅ 清理所有使用 `lucide-vue-next` 的文件（共 5 个）：
  - `src/views/error/404.vue`
  - `src/components/CourseCard.vue`
  - `src/components/FloatingActionButton.vue`
  - `src/components/ThemeSwitcher.vue`
  - `src/layouts/InstructorLayout.vue`
- ✅ 从 `package.json` 中移除 `lucide-vue-next` 依赖
- ⚠️ `element-plus` 暂时保留（部分页面仍在使用 el-dropdown 等组件）

---

## 📋 验证清单

- [x] 项目能正常启动（`npm run dev`）
- [ ] 主题切换功能正常工作
- [ ] 所有重构页面样式显示正常
- [x] 404 页面样式符合 Design System 规范
- [ ] 无控制台错误或警告
- [ ] 构建成功（`npm run build`）
- [x] `lucide-vue-next` 已从 `package.json` 中移除

---

## 📝 备注

- 本清单基于 `PROGRESS.md` 与实际代码的差异分析生成
- P0、P1、P2 级别问题已全部解决
- `element-plus` 暂时保留，待后续完全迁移到 PrimeVue 后再移除
- 建议运行 `npm install` 更新依赖后进行完整测试
