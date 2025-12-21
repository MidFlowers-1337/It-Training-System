<template>
  <div class="page" v-loading="loading">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col lg:flex-row lg:items-end justify-between gap-6">
        <div>
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <BarChart3 class="w-4 h-4 text-primary" />
            学习报告
          </p>
          <h1 class="mt-3 text-3xl md:text-5xl font-semibold tracking-tight text-text-primary">数据驱动成长</h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">可视化你的学习轨迹与关键指标。</p>
        </div>

        <div class="flex flex-col sm:flex-row gap-3">
          <div class="segmented w-full sm:w-auto">
            <button
              type="button"
              class="segmented-item"
              :class="{ 'is-active': reportType === 'weekly' }"
              @click="reportType = 'weekly'"
            >
              周报
            </button>
            <button
              type="button"
              class="segmented-item"
              :class="{ 'is-active': reportType === 'monthly' }"
              @click="reportType = 'monthly'"
            >
              月报
            </button>
            <button
              type="button"
              class="segmented-item"
              :class="{ 'is-active': reportType === 'yearly' }"
              @click="reportType = 'yearly'"
            >
              年报
            </button>
          </div>

          <el-date-picker
            v-if="reportType === 'weekly'"
            v-model="selectedWeek"
            type="week"
            format="YYYY 第 ww 周"
            placeholder="选择周"
            @change="loadReport"
            class="w-full sm:w-44"
          />
          <el-date-picker
            v-else-if="reportType === 'monthly'"
            v-model="selectedMonth"
            type="month"
            format="YYYY年MM月"
            placeholder="选择月份"
            @change="loadReport"
            class="w-full sm:w-40"
          />
          <el-date-picker
            v-else
            v-model="selectedYear"
            type="year"
            format="YYYY年"
            placeholder="选择年份"
            @change="loadReport"
            class="w-full sm:w-32"
          />
        </div>
      </div>
    </section>

    <template v-if="report">
      <!-- KPI -->
      <section class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-6 gap-4">
        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <Clock class="w-5 h-5" />
            </div>
            <span v-if="report.studyTimeChangePercent !== null" class="badge badge-secondary">
              <ArrowUp v-if="report.studyTimeChangePercent >= 0" class="w-4 h-4 mr-1 text-success" />
              <ArrowDown v-else class="w-4 h-4 mr-1 text-error" />
              {{ Math.abs(report.studyTimeChangePercent) }}%
            </span>
          </div>
          <p class="mt-4 text-xs text-text-muted">学习时长</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">
            {{ formatMinutes(report.totalStudyMinutes) }}
          </p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <CalendarDays class="w-5 h-5" />
            </div>
            <span v-if="report.studyDaysChange !== null" class="badge badge-secondary">
              <ArrowUp v-if="report.studyDaysChange >= 0" class="w-4 h-4 mr-1 text-success" />
              <ArrowDown v-else class="w-4 h-4 mr-1 text-error" />
              {{ Math.abs(report.studyDaysChange) }}
            </span>
          </div>
          <p class="mt-4 text-xs text-text-muted">学习天数</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.studyDays || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <CheckCircle2 class="w-5 h-5" />
            </div>
            <span v-if="report.completedCoursesChange !== null" class="badge badge-secondary">
              <ArrowUp v-if="report.completedCoursesChange >= 0" class="w-4 h-4 mr-1 text-success" />
              <ArrowDown v-else class="w-4 h-4 mr-1 text-error" />
              {{ Math.abs(report.completedCoursesChange) }}
            </span>
          </div>
          <p class="mt-4 text-xs text-text-muted">完成课程</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.completedCourses || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <Award class="w-5 h-5" />
            </div>
          </div>
          <p class="mt-4 text-xs text-text-muted">获得成就</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.earnedAchievements || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <Flame class="w-5 h-5" />
            </div>
          </div>
          <p class="mt-4 text-xs text-text-muted">连续打卡</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.streakDays || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <Gauge class="w-5 h-5" />
            </div>
          </div>
          <p class="mt-4 text-xs text-text-muted">日均分钟</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.avgDailyMinutes || 0 }}</p>
        </div>
      </section>

      <!-- Charts -->
      <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card p-6">
          <div class="flex items-end justify-between gap-6">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习趋势</h2>
              <p class="mt-1 text-sm text-text-muted">每日学习分钟与打卡状态</p>
            </div>
          </div>
          <div ref="trendChartRef" class="h-80 w-full mt-4"></div>
        </div>

        <div class="card p-6">
          <div class="flex items-end justify-between gap-6">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">类别分布</h2>
              <p class="mt-1 text-sm text-text-muted">你把时间花在了哪些方向</p>
            </div>
          </div>
          <div ref="categoryChartRef" class="h-80 w-full mt-4"></div>

          <div v-if="report.categoryDistribution?.length" class="mt-4 space-y-2">
            <div
              v-for="(item, index) in report.categoryDistribution"
              :key="item.categoryName"
              class="flex items-center gap-3"
            >
              <span class="w-2 h-2 rounded-full" :style="{ backgroundColor: categorySwatches[index % categorySwatches.length] }"></span>
              <span class="flex-1 font-medium text-sm text-text-primary">{{ item.categoryName }}</span>
              <span class="text-sm text-text-secondary">{{ formatMinutes(item.minutes) }}</span>
              <span class="text-sm font-semibold text-text-primary w-12 text-right">{{ item.percent }}%</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Courses -->
      <section class="card p-6">
        <div class="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-5">
          <div>
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">课程进度</h2>
            <p class="mt-1 text-sm text-text-muted">进行中与已完成课程概览</p>
          </div>
          <div class="segmented">
            <button type="button" class="segmented-item" :class="{ 'is-active': activeTab === 'inProgress' }" @click="activeTab = 'inProgress'">
              进行中
            </button>
            <button type="button" class="segmented-item" :class="{ 'is-active': activeTab === 'completed' }" @click="activeTab = 'completed'">
              已完成
            </button>
          </div>
        </div>

        <div v-if="activeTab === 'inProgress'">
          <div v-if="report.inProgressCourses?.length" class="space-y-3">
            <div v-for="course in report.inProgressCourses" :key="course.courseId" class="card p-4">
              <div class="flex items-start justify-between gap-6">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ course.courseName }}</p>
                  <p class="mt-1 text-sm text-text-muted truncate">{{ course.category }}</p>
                </div>
                <span class="badge badge-secondary">{{ course.progressPercent }}%</span>
              </div>
              <div class="mt-3">
                <div class="h-2 rounded-full bg-bg-tertiary/70 overflow-hidden">
                  <div class="h-full bg-primary/80 rounded-full transition-all" :style="{ width: course.progressPercent + '%' }"></div>
                </div>
                <p class="mt-2 text-xs text-text-muted">已学习 {{ formatMinutes(course.studyMinutes) }}</p>
              </div>
            </div>
          </div>
          <EmptyState v-else :icon="BookOpen" title="暂无进行中的课程" description="从课程中心选择一门课程开始学习。" />
        </div>

        <div v-else>
          <div v-if="report.completedCourseList?.length" class="space-y-3">
            <div v-for="course in report.completedCourseList" :key="course.courseId" class="card p-4">
              <div class="flex items-start justify-between gap-6">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ course.courseName }}</p>
                  <p class="mt-1 text-xs text-text-muted">完成时间：{{ course.lastStudyDate || '—' }}</p>
                </div>
                <span class="badge bg-success/10 text-success border border-success/20">已完成</span>
              </div>
              <p class="mt-2 text-sm text-text-secondary">学习时长：{{ formatMinutes(course.studyMinutes) }}</p>
            </div>
          </div>
          <EmptyState v-else :icon="CheckCircle2" title="本期暂无完成的课程" description="持续学习，很快就会看到成果。" />
        </div>
      </section>

      <!-- Achievements & Suggestions -->
      <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card p-6">
          <div class="flex items-center justify-between gap-6">
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">新获得成就</h2>
          </div>
          <div v-if="report.newAchievements?.length" class="mt-4 space-y-3">
            <div v-for="achievement in report.newAchievements" :key="achievement.id" class="card p-4">
              <div class="flex items-start justify-between gap-6">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ achievement.name }}</p>
                  <p class="mt-1 text-sm text-text-secondary">{{ achievement.description }}</p>
                </div>
                <span class="badge badge-secondary">+{{ achievement.points || 0 }}</span>
              </div>
            </div>
          </div>
          <EmptyState v-else :icon="Award" title="本期暂无新成就" description="继续保持，成就会不断解锁。" />
        </div>

        <div class="card p-6">
          <div class="flex items-center justify-between gap-6">
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习建议</h2>
          </div>
          <div v-if="report.suggestions?.length" class="mt-4 space-y-3">
            <div v-for="(suggestion, index) in report.suggestions" :key="index" class="inset-group">
              <div class="inset-item">
                <div class="flex items-start gap-3">
                  <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary flex-shrink-0">
                    <Lightbulb class="w-5 h-5" />
                  </div>
                  <p class="text-sm text-text-secondary leading-relaxed">{{ suggestion }}</p>
                </div>
              </div>
            </div>
          </div>
          <EmptyState v-else :icon="Lightbulb" title="暂无建议" description="当前学习节奏良好，继续保持。" />
        </div>
      </section>
    </template>

    <EmptyState
      v-else
      :icon="BarChart3"
      title="暂无报告数据"
      description="选择时间范围后会生成学习报告。"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  ArrowDown,
  ArrowUp,
  Award,
  BarChart3,
  BookOpen,
  CalendarDays,
  CheckCircle2,
  Clock,
  Flame,
  Gauge,
  Lightbulb,
} from 'lucide-vue-next'
import { getWeeklyReport, getMonthlyReport, getYearlyReport } from '@/api/learning'
import EmptyState from '@/components/EmptyState.vue'

const loading = ref(false)
const reportType = ref('weekly')
const selectedWeek = ref(new Date())
const selectedMonth = ref(new Date())
const selectedYear = ref(new Date())
const report = ref(null)
const activeTab = ref('inProgress')

const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null
let themeObserver = null

const normalizeRgb = (value, fallback) => {
  const cleaned = (value || '').trim()
  if (!cleaned) return fallback
  return cleaned.replace(/\s+/g, ' ')
}

const rgba = (rgb, alpha) => `rgba(${rgb.replace(/\s+/g, ',')}, ${alpha})`

const getThemeColors = () => {
  const style = getComputedStyle(document.documentElement)
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '37 99 235')
  const primaryLightRgb = normalizeRgb(style.getPropertyValue('--primary-light-rgb'), '59 130 246')
  const infoRgb = normalizeRgb(style.getPropertyValue('--info-color-rgb'), primaryRgb)
  const successRgb = normalizeRgb(style.getPropertyValue('--success-color-rgb'), '5 150 105')
  const warningRgb = normalizeRgb(style.getPropertyValue('--warning-color-rgb'), '217 119 6')
  const errorRgb = normalizeRgb(style.getPropertyValue('--error-color-rgb'), '220 38 38')
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
    textSecondaryRgb,
    borderRgb,
    bgSecondaryRgb,
  }
}

const categorySwatches = ref([])

const updateSwatches = () => {
  const { primaryRgb, primaryLightRgb, infoRgb, successRgb, warningRgb, errorRgb } = getThemeColors()
  const palette = [primaryRgb, primaryLightRgb, infoRgb, successRgb, warningRgb, errorRgb]
  categorySwatches.value = palette.map((rgb) => rgba(rgb, 0.9))
}

const formatMinutes = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

const getMonday = (date) => {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  d.setDate(diff)
  return d.toISOString().split('T')[0]
}

const loadReport = async () => {
  loading.value = true
  try {
    let res
    if (reportType.value === 'weekly') {
      const weekStart = getMonday(selectedWeek.value)
      res = await getWeeklyReport(weekStart)
    } else if (reportType.value === 'monthly') {
      const year = selectedMonth.value.getFullYear()
      const month = selectedMonth.value.getMonth() + 1
      res = await getMonthlyReport(year, month)
    } else {
      const year = selectedYear.value.getFullYear()
      res = await getYearlyReport(year)
    }

    report.value = res.data
    updateSwatches()

    await nextTick()
    renderTrendChart()
    renderCategoryChart()
  } catch (error) {
    console.error('加载报告失败:', error)
    ElMessage.error('加载报告失败')
  } finally {
    loading.value = false
  }
}

const renderTrendChart = () => {
  if (!trendChartRef.value || !report.value?.dailyStudyTrend) return

  trendChart?.dispose()
  trendChart = echarts.init(trendChartRef.value)

  const { primaryRgb, primaryLightRgb, textSecondaryRgb, borderRgb } = getThemeColors()
  const dates = report.value.dailyStudyTrend.map((item) => item.date)
  const minutes = report.value.dailyStudyTrend.map((item) => item.minutes)
  const checkedIn = report.value.dailyStudyTrend.map((item) => item.checkedIn)

  trendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        const isCheckedIn = checkedIn[data.dataIndex]
        return `${data.name}<br/>学习时长：${data.value} 分钟<br/>打卡：${isCheckedIn ? '已打卡' : '未打卡'}`
      },
    },
    grid: { left: 24, right: 12, top: 18, bottom: 24, containLabel: true },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        color: rgba(textSecondaryRgb, 0.9),
        formatter: (value) => {
          const date = new Date(value)
          return `${date.getMonth() + 1}/${date.getDate()}`
        },
      },
      axisLine: { lineStyle: { color: rgba(borderRgb, 0.8) } },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      minInterval: 1,
      axisLabel: { color: rgba(textSecondaryRgb, 0.85) },
      nameTextStyle: { color: rgba(textSecondaryRgb, 0.7) },
      splitLine: { lineStyle: { color: rgba(borderRgb, 0.6) } },
    },
    series: [
      {
        name: '学习时长',
        type: 'bar',
        data: minutes,
        barWidth: 16,
        itemStyle: {
          borderRadius: [10, 10, 6, 6],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: rgba(primaryLightRgb, 0.9) },
            { offset: 1, color: rgba(primaryRgb, 0.18) },
          ]),
        },
      },
    ],
  })
}

const renderCategoryChart = () => {
  if (!categoryChartRef.value || !report.value?.categoryDistribution) return

  categoryChart?.dispose()
  categoryChart = echarts.init(categoryChartRef.value)

  const { bgSecondaryRgb } = getThemeColors()
  const data = report.value.categoryDistribution.map((item, index) => ({
    name: item.categoryName,
    value: item.minutes,
    itemStyle: { color: categorySwatches.value[index % categorySwatches.value.length] },
  }))

  categoryChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', formatter: '{b}: {c}分钟 ({d}%)' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: rgba(bgSecondaryRgb, 0.9),
          borderWidth: 2,
        },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data,
      },
    ],
  })
}

const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
}

watch(reportType, () => {
  loadReport()
})

onMounted(() => {
  loadReport()
  window.addEventListener('resize', handleResize)
  themeObserver = new MutationObserver(() => {
    nextTick(() => {
      updateSwatches()
      renderTrendChart()
      renderCategoryChart()
    })
  })
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  themeObserver?.disconnect()
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>
