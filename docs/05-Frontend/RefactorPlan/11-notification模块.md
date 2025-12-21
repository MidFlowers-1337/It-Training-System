# 11. Notification 模块设计

## 1. 页面设计 (Page Design)

### 1.1 通知中心 (Notification Center)

**Pattern**: `Popover` (下拉列表) + `ListRow`

-   **Entry**: 顶部导航栏的 "Bell" 图标。
-   **UI**:
    -   点击图标，弹出一个极简的下拉列表 (Width: 320px)。
    -   **Header**: "Notifications" + "Mark all as read" (Text Button).
    -   **List**:
        -   `ListRow` (Compact Mode)
            -   Left: Dot (Blue = Unread, Transparent = Read)
            -   Middle: Title (e.g., "Course Started") + Time (e.g., "2h ago")
            -   Action: Click to navigate.

**禁止**: 全屏的通知管理页面（除非通知非常多，否则 Popover 足够）。
**强制**: 保持轻量级，不要打断用户当前操作。

## 2. 组件拆分

```
src/features/notification/
├── components/
│   └── NotificationPopover.vue  # 封装 Popover + ListRow
```

## 3. API 对接

-   `GET /api/notification/list`
-   `POST /api/notification/read/{id}`

---

**下一步**: 更新 `10-ai模块.md`，增加 AI 测验功能。