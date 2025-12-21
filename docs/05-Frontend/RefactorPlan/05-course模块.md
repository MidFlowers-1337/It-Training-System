# 05. Course 模块设计

## 1. 页面设计 (Page Design)

### 1.1 课程列表 (Course List)

**Pattern**: `PageLayout` + `ListRow`

-   **Header**: `PageHeader` (Title: "Courses", Action: "Create Course")
-   **Filter**: 简单的 Tab 切换 (All / Frontend / Backend)，不使用复杂的侧边栏过滤器。
-   **List**:
    -   `ListRow`
        -   Icon: 课程封面缩略图 (48x48 rounded)
        -   Title: 课程名称
        -   Subtitle: 讲师 • 难度 • 时长
        -   Action: "View" (Chevron)

**禁止**: Grid Card 布局。
**强制**: 即使有图片，也必须使用 `ListRow` 模式，图片作为左侧图标展示。

### 1.2 课程详情 (Course Detail)

**Pattern**: `SplitLayout` (左 2/3 内容，右 1/3 信息栏)

-   **Left (Content)**:
    -   `SectionHeader` ("About this course")
    -   Text Body (课程介绍)
    -   `SectionHeader` ("Syllabus")
    -   `ListRow` (章节列表，无图标，仅序号)
-   **Right (Sidebar)**:
    -   Sticky Container
    -   `Button` (Primary: "Enroll Now")
    -   `DescriptionList` (Price, Duration, Level)

## 2. 组件拆分

```
src/features/course/
├── components/
│   ├── CourseListRow.vue    # 列表项
│   └── ChapterListRow.vue   # 章节列表项
├── views/
│   ├── CourseListView.vue
│   └── CourseDetailView.vue
```

---

**下一步**: 更新 `06-session模块.md`。