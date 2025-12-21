<template>
  <div class="space-y-6">
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative">
        <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
          <BarChart3 class="w-4 h-4 text-primary" />
          管理后台
        </p>
        <h1 class="mt-3 text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">Dashboard</h1>
        <p class="mt-2 text-sm md:text-base text-text-secondary">系统概览与关键指标。</p>
      </div>
    </section>

    <!-- Stats Grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div v-for="stat in stats" :key="stat.label" class="card p-6">
        <div class="flex items-center justify-between mb-4">
          <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
            <component :is="stat.icon" class="w-6 h-6" />
          </div>
          <span class="badge badge-secondary">
            <span :class="stat.trend > 0 ? 'text-success' : 'text-error'">
              {{ stat.trend > 0 ? '+' : '' }}{{ stat.trend }}%
            </span>
          </span>
        </div>
        <h3 class="text-text-secondary text-sm font-medium">{{ stat.label }}</h3>
        <p class="text-2xl font-bold text-text-primary mt-1">{{ stat.value }}</p>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Enrollment Trend -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold tracking-tight text-text-primary mb-1">Enrollment Trends</h3>
        <p class="text-sm text-text-muted mb-6">近 7 天报名趋势。</p>
        <div ref="enrollmentChartRef" class="h-80 w-full"></div>
      </div>

      <!-- Popular Courses -->
      <div class="card p-6">
        <h3 class="text-lg font-semibold tracking-tight text-text-primary mb-1">Popular Courses</h3>
        <p class="text-sm text-text-muted mb-6">热门课程占比。</p>
        <div ref="courseChartRef" class="h-80 w-full"></div>
      </div>
    </div>

    <!-- Recent Activity -->
    <div class="card p-6">
      <h3 class="text-lg font-semibold tracking-tight text-text-primary">Recent Activity</h3>
      <div class="mt-4 inset-group">
        <div v-for="(activity, index) in recentActivities" :key="index">
          <div class="inset-item">
            <div class="min-w-0">
              <p class="text-sm text-text-primary truncate">{{ activity.message }}</p>
              <p class="text-xs text-text-muted mt-1">{{ activity.time }}</p>
            </div>
          </div>
          <div v-if="index < recentActivities.length - 1" class="inset-divider"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { Users, BookOpen, GraduationCap, BarChart3 } from 'lucide-vue-next'

// Stats Data
const stats = [
  { 
    label: 'Total Students', 
    value: '2,543', 
    trend: 12.5, 
    icon: Users
  },
  { 
    label: 'Active Courses', 
    value: '45', 
    trend: 4.2, 
    icon: BookOpen
  },
  { 
    label: 'Course Completions', 
    value: '1,201', 
    trend: 8.1, 
    icon: GraduationCap
  },
  { 
    label: 'Total Revenue', 
    value: '$45,231', 
    trend: -2.4, 
    icon: BarChart3
  }
]

// Recent Activity Data
const recentActivities = [
  { message: 'New student registered: John Doe', time: '2 minutes ago' },
  { message: 'Course "Vue.js Fundamentals" updated', time: '1 hour ago' },
  { message: 'System maintenance scheduled', time: '3 hours ago' },
  { message: 'New review posted for "Advanced React"', time: '5 hours ago' }
]

// Charts
const enrollmentChartRef = ref(null)
const courseChartRef = ref(null)
let enrollmentChart = null
let courseChart = null
let themeObserver = null

const normalizeRgb = (value, fallback) => {
  const cleaned = (value || '').trim()
  if (!cleaned) return fallback
  return cleaned.replace(/\\s+/g, ' ')
}

const rgba = (rgb, alpha) => `rgba(${rgb.replace(/\\s+/g, ',')}, ${alpha})`

const getThemeColors = () => {
  const style = getComputedStyle(document.documentElement)
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '37 99 235')
  const primaryLightRgb = normalizeRgb(style.getPropertyValue('--primary-light-rgb'), '59 130 246')
  const infoRgb = normalizeRgb(style.getPropertyValue('--info-color-rgb'), primaryRgb)
  const successRgb = normalizeRgb(style.getPropertyValue('--success-color-rgb'), '5 150 105')
  const warningRgb = normalizeRgb(style.getPropertyValue('--warning-color-rgb'), '217 119 6')
  const errorRgb = normalizeRgb(style.getPropertyValue('--error-color-rgb'), '220 38 38')
  const textPrimaryRgb = normalizeRgb(style.getPropertyValue('--text-primary-rgb'), '17 24 39')
  const textSecondaryRgb = normalizeRgb(style.getPropertyValue('--text-secondary-rgb'), '75 85 99')
  const borderRgb = normalizeRgb(style.getPropertyValue('--border-color-rgb'), '229 231 235')
  const bgSecondaryRgb = normalizeRgb(style.getPropertyValue('--bg-secondary-rgb'), '255 255 255')

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
  }
}

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
  } = getThemeColors()

  const primary = rgba(primaryRgb, 0.95)
  const primarySoft = rgba(primaryRgb, 0.18)
  const primaryLight = rgba(primaryLightRgb, 0.9)
  const textColor = rgba(textPrimaryRgb, 1)
  const textSecondary = rgba(textSecondaryRgb, 0.9)
  const borderColor = rgba(borderRgb, 0.75)
  const surface = rgba(bgSecondaryRgb, 0.92)

  // Enrollment Chart
  if (enrollmentChartRef.value) {
    enrollmentChart = echarts.init(enrollmentChartRef.value)
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
    })
  }

  // Course Chart
  if (courseChartRef.value) {
    courseChart = echarts.init(courseChartRef.value)
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
    })
  }
}

const handleResize = () => {
  enrollmentChart?.resize()
  courseChart?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
  themeObserver = new MutationObserver(() => {
    nextTick(() => {
      enrollmentChart?.dispose()
      courseChart?.dispose()
      initCharts()
    })
  })
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  themeObserver?.disconnect()
  enrollmentChart?.dispose()
  courseChart?.dispose()
})
</script>
