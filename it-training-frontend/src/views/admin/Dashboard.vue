<template>
  <PageLayout max-width="2xl">
    <!-- Page Header -->
    <PageHeader
      title="Dashboard"
      subtitle="System overview and key metrics."
      :show-divider="false"
    />

    <!-- Stats Grid -->
    <Section compact>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div v-for="stat in stats" :key="stat.label" class="card p-6">
          <div class="flex items-center justify-between mb-4">
            <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <component :is="stat.icon" />
            </div>
            <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60">
              <span :class="stat.trend > 0 ? 'text-success' : 'text-error'">
                {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
              </span>
            </span>
          </div>
          <h3 class="text-text-secondary text-sm font-medium">{{ stat.label }}</h3>
          <p class="text-2xl font-bold text-text-primary mt-1">{{ stat.value }}</p>
        </div>
      </div>
    </Section>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-16">
      <!-- Enrollment Trend -->
      <Section title="Enrollment Trends" subtitle="Last 7 days enrollment trend." compact>
        <div ref="enrollmentChartRef" class="h-80 w-full"></div>
      </Section>

      <!-- Popular Courses -->
      <Section title="Popular Courses" subtitle="Course popularity distribution." compact>
        <div ref="courseChartRef" class="h-80 w-full"></div>
      </Section>
    </div>

    <!-- Recent Activity -->
    <Section title="Recent Activity" compact>
      <div class="rounded-2xl border border-border-color/60 bg-bg-secondary/60 overflow-hidden">
        <ListRow
          v-for="(activity, index) in recentActivities"
          :key="index"
          :title="activity.message"
          :subtitle="activity.time"
          :show-arrow="false"
          :clickable="false"
          :show-divider="index < recentActivities.length - 1"
        />
      </div>
    </Section>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, h, onMounted, onUnmounted, nextTick, type Component } from 'vue';
import * as echarts from 'echarts';
import { PageLayout, PageHeader, Section, ListRow } from '@/design-system';

// Icon Components
const UsersIcon: Component = {
  render() {
    return h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' })
    ]);
  }
};

const BookOpenIcon: Component = {
  render() {
    return h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253' })
    ]);
  }
};

const GraduationCapIcon: Component = {
  render() {
    return h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 14l9-5-9-5-9 5 9 5z' }),
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z' }),
      h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2', d: 'M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222' })
    ]);
  }
};

const BarChartIcon: Component = {
  render() {
    return h('svg', { class: 'w-6 h-6', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' }, [
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
}

interface Activity {
  message: string;
  time: string;
}

// Stats Data
const stats: Stat[] = [
  {
    label: 'Total Students',
    value: '2,543',
    trend: 12.5,
    icon: UsersIcon
  },
  {
    label: 'Active Courses',
    value: '45',
    trend: 4.2,
    icon: BookOpenIcon
  },
  {
    label: 'Course Completions',
    value: '1,201',
    trend: 8.1,
    icon: GraduationCapIcon
  },
  {
    label: 'Total Revenue',
    value: '$45,231',
    trend: -2.4,
    icon: BarChartIcon
  }
];

// Recent Activity Data
const recentActivities: Activity[] = [
  { message: 'New student registered: John Doe', time: '2 minutes ago' },
  { message: 'Course "Vue.js Fundamentals" updated', time: '1 hour ago' },
  { message: 'System maintenance scheduled', time: '3 hours ago' },
  { message: 'New review posted for "Advanced React"', time: '5 hours ago' }
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
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '37 99 235');
  const primaryLightRgb = normalizeRgb(style.getPropertyValue('--primary-light-rgb'), '59 130 246');
  const infoRgb = normalizeRgb(style.getPropertyValue('--info-color-rgb'), primaryRgb);
  const successRgb = normalizeRgb(style.getPropertyValue('--success-color-rgb'), '5 150 105');
  const warningRgb = normalizeRgb(style.getPropertyValue('--warning-color-rgb'), '217 119 6');
  const errorRgb = normalizeRgb(style.getPropertyValue('--error-color-rgb'), '220 38 38');
  const textPrimaryRgb = normalizeRgb(style.getPropertyValue('--text-primary-rgb'), '17 24 39');
  const textSecondaryRgb = normalizeRgb(style.getPropertyValue('--text-secondary-rgb'), '75 85 99');
  const borderRgb = normalizeRgb(style.getPropertyValue('--border-color-rgb'), '229 231 235');
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
        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        axisLine: { lineStyle: { color: borderColor } },
        axisLabel: { color: textSecondary }
      },
      yAxis: {
        type: 'value',
        splitLine: { lineStyle: { color: borderColor } },
        axisLabel: { color: textSecondary }
      },
      series: [{
        name: 'Enrollments',
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
        name: 'Popular Courses',
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
