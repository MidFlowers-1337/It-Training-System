# 09. Learning 模块设计

## 1. 页面设计 (Page Design)

### 1.1 我的学习 (My Learning)

**Pattern**: `PageLayout` + `ListRow`

-   **Header**: `PageHeader` (Title: "My Learning")
-   **Daily Check-in (每日打卡)**:
    -   **Pattern**: `Section` (Top of page)
    -   **UI**:
        -   Left: "Day 5" (Current Streak)
        -   Right: `Button` (Primary: "Check In") -> Changes to "Checked In" (Green)
        -   Visual: 极简的 7 天小圆点进度条 (Mon-Sun)，已打卡为实心，未打卡为空心。
-   **List**:
    -   `ListRow` (Course Progress)
        -   Left: Course Icon
        -   Middle: Course Title + "Last studied: Chapter 3"
        -   Right: `ProgressBar` (Thin Line, 2px height) + "Continue" Button

### 1.2 成就 (Achievements)

**Pattern**: `ListRow`

-   **List**:
    -   `ListRow`
        -   Left: Minimal Icon (SVG)
        -   Middle: Achievement Name + Date
        -   Right: Share Button

**禁止**: 拟物化徽章、复杂的 3D 效果。
**强制**: 使用扁平化、单色图标。

## 2. 组件拆分

```
src/features/learning/
├── components/
│   ├── LearningListRow.vue  # 带进度条的列表行
│   ├── AchievementRow.vue
│   └── DailyCheckin.vue     # [新增] 打卡组件
├── views/
│   └── LearningCenterView.vue
```

## 3. API 对接

-   `POST /api/learning/checkin` (每日打卡)
-   `GET /api/learning/progress` (我的课程进度)
-   `GET /api/learning/achievements` (我的成就)

---

**下一步**: 新增 `11-notification模块.md`。