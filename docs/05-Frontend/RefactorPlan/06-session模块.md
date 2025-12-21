# 06. Session 模块设计

## 1. 页面设计 (Page Design)

### 1.1 班期管理 (Session Management)

**Pattern**: `PageLayout` + `ListRow`

-   **Header**: `PageHeader` (Title: "Sessions")
-   **List**:
    -   `ListRow`
        -   Title: 班期名称 (e.g., "2023 Winter Bootcamp")
        -   Subtitle: Start Date - End Date
        -   Right: Status Badge (Open/Closed)

### 1.2 日程安排 (Schedule)

**Pattern**: `CalendarList` (自定义模式)

-   **Structure**: 简单的垂直列表，按日期分组。
-   **Group Header**: 日期 (e.g., "Mon, Oct 23")
-   **Item**: `ListRow` (Time | Event Name | Location)

**禁止**: 使用复杂的日历网格组件 (FullCalendar)。
**强制**: 使用垂直列表展示日程，类似手机日历的 "列表视图"。

## 2. 组件拆分

```
src/features/session/
├── components/
│   ├── SessionListRow.vue
│   └── ScheduleList.vue    # 日程列表模式
├── views/
│   └── SessionListView.vue
```

---

**下一步**: 更新 `07-enrollment模块.md`。