# 08. Stats 模块设计

## 1. 页面设计 (Page Design)

### 1.1 仪表盘 (Dashboard)

**Pattern**: `PageLayout` + `Section`

-   **Header**: `PageHeader` (Title: "Dashboard")
-   **Section 1: Overview**
    -   `StatGrid` (自定义模式: 纯数字展示，无卡片背景)
        -   Item: Label (Small Gray) + Value (Huge Black)
-   **Section 2: Trends**
    -   `MinimalChart` (ECharts 封装，隐藏坐标轴线，仅保留数据曲线)
-   **Section 3: Top Courses**
    -   `ListRow` (Rank | Course Name | Enrollment Count)

**禁止**: Bento Grid (拼贴卡片)。
**强制**: 数据可视化必须极简，去除所有装饰性边框和背景色。

## 2. 组件拆分

```
src/features/stats/
├── components/
│   ├── StatValue.vue       # 纯数字展示组件
│   └── MinimalLineChart.vue # 极简折线图
├── views/
│   └── DashboardView.vue
```

---

**下一步**: 更新 `09-learning模块.md`。