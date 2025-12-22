# 前端重构遗留问题清单

> **创建时间**: 2025-12-22
> **状态**: 🔴 关键问题待解决

---

## 🔴 P0 级别（阻断性问题）

### 1. 核心入口文件未迁移

**问题描述**:
项目仍在使用 `src/main.js`，未迁移到 `src/main.ts`，导致 Design System 完全未初始化。

**当前状态**:
```javascript
// src/main.js (旧代码)
import ElementPlus from 'element-plus'
app.use(ElementPlus)
```

**期望状态**:
```typescript
// src/main.ts (新代码)
import { initDesignSystem, primeVueConfig } from '@/design-system'
import PrimeVue from 'primevue/config'

initDesignSystem()
app.use(PrimeVue, primeVueConfig)
```

**影响范围**:
- ❌ Design System 的主题切换功能未生效
- ❌ CSS 变量初始化未执行
- ❌ PrimeVue 样式配置未加载
- ❌ 所有重构页面的样式可能显示异常

**修复步骤**:
1. 将 `src/main.js` 重命名为 `src/main.ts`
2. 添加 Design System 初始化代码
3. 移除 ElementPlus 全量引入，改为按需引入或完全移除
4. 更新 `index.html` 中的入口文件引用（如有）
5. 验证项目启动和主题切换功能

---

## ⚠️ P1 级别（必须修复）

### 2. 404 错误页面未重构

**问题描述**:
`src/views/error/404.vue` 未纳入重构范围，仍依赖旧的图标库和样式系统。

**当前问题**:
- 仍使用 `lucide-vue-next` 图标库（重构目标是移除该依赖）
- 使用 `btn btn-primary` 等类名（未确认是否符合新规范）
- 未使用 Design System 的原子组件

**修复步骤**:
1. 检查 404.vue 的当前实现
2. 移除 `lucide-vue-next` 依赖，改用内联 SVG 或 Design System 图标
3. 使用 Design System 的 Button 组件替换旧的按钮样式
4. 统一样式规范

---

## 📦 P2 级别（优化项）

### 3. 依赖清理未完成

**问题描述**:
由于部分页面未完成重构，旧依赖无法完全移除。

**待清理依赖**:
- `lucide-vue-next`: 404.vue 等页面可能仍在使用
- `element-plus`: 如果已全部迁移到 PrimeVue，可考虑移除全量引入

**修复步骤**:
1. 全局搜索 `lucide-vue-next` 的使用情况
2. 全局搜索 `element-plus` 的使用情况
3. 确认所有页面已迁移后，更新 `package.json`
4. 执行 `npm prune` 清理未使用的依赖

---

## 📋 验证清单

完成上述修复后，需要进行以下验证：

- [ ] 项目能正常启动（`npm run dev`）
- [ ] 主题切换功能正常工作
- [ ] 所有重构页面样式显示正常
- [ ] 404 页面样式符合 Design System 规范
- [ ] 无控制台错误或警告
- [ ] 构建成功（`npm run build`）
- [ ] 旧依赖已从 `package.json` 中移除

---

## 📝 备注

- 本清单基于 `PROGRESS.md` 与实际代码的差异分析生成
- 建议按 P0 → P1 → P2 的优先级依次修复
- 每完成一项修复，请在对应项前打勾 ✅
