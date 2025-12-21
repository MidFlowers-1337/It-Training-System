# 04. User 模块设计

## 1. 页面设计 (Page Design)

### 1.1 个人中心 (Profile)

**Pattern**: `SidebarLayout` (左侧导航，右侧内容)

-   **Sidebar**:
    -   Menu: "Profile", "Security", "Settings"
-   **Content Area**:
    -   **Header**: `PageHeader` (Title: "Personal Information")
    -   **Section**: "Basic Info"
        -   `DescriptionList` (Avatar, Name, Email, Role)
        -   Action: "Edit" (Button Ghost)

### 1.2 用户管理 (Admin)

**Pattern**: `PageLayout` + `ListRow`

-   **Header**: `PageHeader` (Title: "Users", Action: "Add User")
-   **List**:
    -   `ListRow` (Avatar | Name/Email | Role | Status | Action: Edit/Delete)

**禁止**: 使用表格 (Table) 展示简单用户列表。
**强制**: 使用 `ListRow`，手机端自动适配。

## 2. 组件拆分

```
src/features/user/
├── components/
│   └── UserListRow.vue  # 封装 ListRow，展示用户特定数据
├── views/
│   ├── ProfileView.vue  # 组合 SidebarLayout + DescriptionList
│   └── UserListView.vue # 组合 PageLayout + UserListRow
```

---

**下一步**: 更新 `05-course模块.md`。