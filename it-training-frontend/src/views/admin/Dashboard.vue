<template>
  <div class="dashboard-page">
    <!-- Page Header -->
    <header class="page-header">
      <h1 class="page-title">控制台</h1>
      <p class="page-subtitle">系统概览与关键指标</p>
    </header>

    <!-- Stats Grid -->
    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card">
        <div class="stat-top">
          <div class="stat-icon" :class="stat.colorClass">
            <component :is="stat.icon" />
          </div>
          <span class="stat-trend" :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
            {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
          </span>
        </div>
        <h3 class="stat-label">{{ stat.label }}</h3>
        <p class="stat-value">{{ stat.value }}</p>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-grid">
      <!-- Enrollment Trend -->
      <section class="chart-card">
        <div class="card-header">
          <div class="card-header-text">
            <h2 class="card-title">报名趋势</h2>
            <p class="card-subtitle">近 7 天报名统计</p>
          </div>
        </div>
        <div ref="enrollmentChartRef" class="chart-container"></div>
      </section>

      <!-- Popular Courses -->
      <section class="chart-card">
        <div class="card-header">
          <div class="card-header-text">
            <h2 class="card-title">热门课程</h2>
            <p class="card-subtitle">课程报名分布</p>
          </div>
        </div>
        <div ref="courseChartRef" class="chart-container"></div>
      </section>
    </div>

    <!-- Recent Activity -->
    <section class="activity-section">
      <div class="card-header">
        <h2 class="card-title">最近动态</h2>
      </div>
      <div class="activity-list">
        <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
          <div class="activity-content">
            <span class="activity-message">{{ activity.message }}</span>
            <span class="activity-time">{{ activity.time }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, h, onMounted, onUnmounted, nextTick, type Component } from 'vue';
import * as echarts from 'echarts';

// Icon Components
const UsersIcon: Component = {
  render() {
    return h('svg', { class: 'icon-svg', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' })
    ]);
  }
};

const BookOpenIcon: Component = {
  render() {
    return h('svg', { class: 'icon-svg', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253' })
    ]);
  }
};

const GraduationCapIcon: Component = {
  render() {
    return h('svg', { class: 'icon-svg', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 14l9-5-9-5-9 5 9 5z' }),
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z' }),
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222' })
    ]);
  }
};

const BarChartIcon: Component = {
  render() {
    return h('svg', { class: 'icon-svg', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z' })
    ]);
  }
};

// Types
interface Stat {
  label: string;
  value: string;
  trend: number;
  icon: Component;
  colorClass: string;
}

interface Activity {
  message: string;
  time: string;
}

// Stats Data
const stats: Stat[] = [
  {
    label: '总学员数',
    value: '2,543',
    trend: 12.5,
    icon: UsersIcon,
    colorClass: 'icon-primary'
  },
  {
    label: '活跃课程',
    value: '45',
    trend: 4.2,
    icon: BookOpenIcon,
    colorClass: 'icon-info'
  },
  {
    label: '课程完成',
    value: '1,201',
    trend: 8.1,
    icon: GraduationCapIcon,
    colorClass: 'icon-success'
  },
  {
    label: '总收入',
    value: '¥45,231',
    trend: -2.4,
    icon: BarChartIcon,
    colorClass: 'icon-warning'
  }
];

// Recent Activity Data
const recentActivities: Activity[] = [
  { message: '新学员注册：张三', time: '2 分钟前' },
  { message: '课程「Vue.js 入门」已更新', time: '1 小时前' },
  { message: '系统维护计划已安排', time: '3 小时前' },
  { message: '「高级 React」课程收到新评价', time: '5 小时前' }
];

// Charts
const enrollmentChartRef = ref<HTMLElement | null>(null);
const courseChartRef = ref<HTMLElement | null>(null);
let enrollmentChart: echarts.ECharts | null = null;
let courseChart: echarts.ECharts | null = null;
let themeObserver: MutationObserver | null = null;

const normalizeRgb = (value: string, fallback: string): string => {
  const cleaned = (value || '').trim();
  if (!cleaned) return fallback;
  return cleaned.replace(/\s+/g, ' ');
};

const rgba = (rgb: string, alpha: number): string => `rgba(${rgb.replace(/\s+/g, ',')}, ${alpha})`;

const getThemeColors = () => {
  const style = getComputedStyle(document.documentElement);
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '0 122 255');
  const primaryLightRgb = normalizeRgb(style.getPropertyValue('--primary-light-rgb'), '90 200 250');
  const infoRgb = normalizeRgb(style.getPropertyValue('--info-rgb'), primaryRgb);
  const successRgb = normalizeRgb(style.getPropertyValue('--success-rgb'), '52 199 89');
  const warningRgb = normalizeRgb(style.getPropertyValue('--warning-rgb'), '255 149 0');
  const errorRgb = normalizeRgb(style.getPropertyValue('--error-rgb'), '255 59 48');
  const textPrimaryRgb = normalizeRgb(style.getPropertyValue('--text-primary-rgb'), '0 0 0');
  const textSecondaryRgb = normalizeRgb(style.getPropertyValue('--text-secondary-rgb'), '142 142 147');
  const borderRgb = normalizeRgb(style.getPropertyValue('--border-color-rgb'), '209 209 214');
  const bgSecondaryRgb = normalizeRgb(style.getPropertyValue('--bg-secondary-rgb'), '255 255 255');

  return {
    primaryRgb,
    primaryLightRgb,
    infoRgb,
    successRgb,
    warningRgb,
    errorRgb,
    textPrimaryRgb,
    textSecondaryRgb,
    borderRgb,
    bgSecondaryRgb,
  };
};

const initCharts = () => {
  const {
    primaryRgb,
    primaryLightRgb,
    infoRgb,
    successRgb,
    warningRgb,
    errorRgb,
    textPrimaryRgb,
    textSecondaryRgb,
    borderRgb,
    bgSecondaryRgb,
  } = getThemeColors();

  const primary = rgba(primaryRgb, 0.95);
  const primarySoft = rgba(primaryRgb, 0.18);
  const primaryLight = rgba(primaryLightRgb, 0.9);
  const textColor = rgba(textPrimaryRgb, 1);
  const textSecondary = rgba(textSecondaryRgb, 0.9);
  const borderColor = rgba(borderRgb, 0.75);
  const surface = rgba(bgSecondaryRgb, 0.92);

  // Enrollment Chart
  if (enrollmentChartRef.value) {
    enrollmentChart = echarts.init(enrollmentChartRef.value);
    enrollmentChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'line' },
        backgroundColor: surface,
        borderColor: borderColor,
        textStyle: { color: textColor },
      },
      grid: {
        top: '10%',
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        axisLine: { lineStyle: { color: borderColor } },
        axisLabel: { color: textSecondary }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: borderColor } },
        axisLabel: { color: textSecondary }
      },
      series: [{
        name: '报名数',
        type: 'line',
        smooth: true,
        data: [120, 132, 101, 134, 90, 230, 210],
        itemStyle: { color: primary },
        lineStyle: { color: primary, width: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: primarySoft },
            { offset: 1, color: rgba(primaryRgb, 0.0) },
          ])
        }
      }]
    });
  }

  // Course Chart
  if (courseChartRef.value) {
    courseChart = echarts.init(courseChartRef.value);
    courseChart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'item',
        backgroundColor: surface,
        borderColor: borderColor,
        textStyle: { color: textColor }
      },
      legend: {
        bottom: '0%',
        textStyle: { color: textSecondary }
      },
      series: [{
        name: '热门课程',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: surface,
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          label: {
            show: true,
            fontSize: '16',
            fontWeight: 'bold',
            color: textColor
          }
        },
        color: [primaryLight, rgba(infoRgb, 0.9), rgba(successRgb, 0.9), rgba(warningRgb, 0.9), rgba(errorRgb, 0.9)],
        data: [
          { value: 1048, name: 'Vue.js' },
          { value: 735, name: 'React' },
          { value: 580, name: 'TypeScript' },
          { value: 484, name: 'Node.js' },
          { value: 300, name: 'Python' }
        ]
      }]
    });
  }
};

const handleResize = () => {
  enrollmentChart?.resize();
  courseChart?.resize();
};

onMounted(() => {
  initCharts();
  window.addEventListener('resize', handleResize);
  themeObserver = new MutationObserver(() => {
    nextTick(() => {
      enrollmentChart?.dispose();
      courseChart?.dispose();
      initCharts();
    });
  });
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  themeObserver?.disconnect();
  enrollmentChart?.dispose();
  courseChart?.dispose();
});
</script>

<style scoped>
/* ========================================
   Apple Workbench 风格 Dashboard
   ======================================== */

.dashboard-page {
  padding: 0;
}

/* ===== Page Header ===== */
.page-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.page-subtitle {
  margin-top: 4px;
  font-size: 15px;
  color: var(--text-secondary);
}

/* ===== Stats Grid ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 20px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
}

.icon-svg {
  width: 24px;
  height: 24px;
}

.stat-icon.icon-primary {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  color: var(--primary-color);
  border: 1px solid rgba(var(--primary-color-rgb, 0, 122, 255) / 0.2);
}

.stat-icon.icon-info {
  background: rgba(var(--info-rgb, 0, 122, 255) / 0.1);
  color: var(--info);
  border: 1px solid rgba(var(--info-rgb, 0, 122, 255) / 0.2);
}

.stat-icon.icon-success {
  background: rgba(var(--success-rgb, 52, 199, 89) / 0.1);
  color: var(--success);
  border: 1px solid rgba(var(--success-rgb, 52, 199, 89) / 0.2);
}

.stat-icon.icon-warning {
  background: rgba(var(--warning-rgb, 255, 149, 0) / 0.1);
  color: var(--warning);
  border: 1px solid rgba(var(--warning-rgb, 255, 149, 0) / 0.2);
}

.stat-trend {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  font-variant-numeric: tabular-nums;
}

.stat-trend.trend-up {
  background: rgba(var(--success-rgb, 52, 199, 89) / 0.1);
  color: var(--success);
}

.stat-trend.trend-down {
  background: rgba(var(--error-rgb, 255, 59, 48) / 0.1);
  color: var(--error);
}

.stat-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.stat-value {
  margin-top: 4px;
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  font-variant-numeric: tabular-nums;
}

/* ===== Charts Grid ===== */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 32px;
}

@media (max-width: 1024px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

.chart-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-subtitle {
  margin-top: 2px;
  font-size: 13px;
  color: var(--text-secondary);
}

.chart-container {
  height: 280px;
  width: 100%;
}

/* ===== Activity Section ===== */
.activity-section {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  padding: 14px 0;
  border-bottom: 0.5px solid var(--border-light);
}

.activity-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.activity-item:first-child {
  padding-top: 0;
}

.activity-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.activity-message {
  font-size: 14px;
  color: var(--text-primary);
}

.activity-time {
  font-size: 13px;
  color: var(--text-muted);
  flex-shrink: 0;
}

/* ===== Dark Mode ===== */
[data-theme="dark"] .stat-card,
[data-theme="dark"] .chart-card,
[data-theme="dark"] .activity-section {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}
</style>
