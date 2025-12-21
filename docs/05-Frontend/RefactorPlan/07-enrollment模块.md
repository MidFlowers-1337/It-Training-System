# 07. Enrollment 模块设计

## 1. 页面设计 (Page Design)

### 1.1 报名流程 (Enrollment Flow)

**Pattern**: `StepWizard` (分步向导)

-   **Step 1: Confirm Course**
    -   `ListRow` (Selected Course Info)
-   **Step 2: Select Session**
    -   `ListRow` (Session Options - Radio Selection)
-   **Step 3: Payment/Confirm**
    -   `DescriptionList` (Summary)
    -   `Button` (Primary: "Confirm Enrollment")

**禁止**: 复杂的模态框。
**强制**: 使用全页面的分步流程，每一步只做一件事。

## 2. 组件拆分

```
src/features/enrollment/
├── components/
│   └── SessionSelector.vue  # 封装 ListRow 用于选择
├── views/
│   └── EnrollmentWizard.vue # 状态机控制步骤
```

---

**下一步**: 更新 `08-stats模块.md`。