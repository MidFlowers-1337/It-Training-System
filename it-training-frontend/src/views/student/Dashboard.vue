<template>
  <div class="max-w-7xl mx-auto px-4 md:px-8 py-8">
    <div v-loading="loading" class="space-y-10">
      <!-- Hero -->
      <section
        class="relative overflow-hidden rounded-3xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl shadow-sm"
      >
        <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>

        <div class="relative p-6 md:p-10 grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          <div class="lg:col-span-8">
            <div class="flex items-start gap-4">
              <div
                class="w-11 h-11 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary flex-shrink-0"
              >
                <Sparkles class="w-5 h-5" />
              </div>

              <div class="min-w-0">
                <h1 class="text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">
                  欢迎回来，{{ userInfo.realName || userInfo.username }}。
                </h1>
                <p class="mt-2 text-text-secondary">继续你的学习旅程，保持节奏。</p>
              </div>
            </div>

            <div class="mt-6 flex flex-wrap gap-3">
              <div class="flex items-center gap-3 rounded-2xl bg-bg-primary/40 border border-border-color/60 px-4 py-3">
                <Clock class="w-4 h-4 text-primary" />
                <div class="leading-tight">
                  <div class="text-xs text-text-muted">今日学习</div>
                  <div class="text-sm font-semibold text-text-primary tabular-nums">{{ todayStats.studyMinutes }} 分钟</div>
                </div>
              </div>

              <div class="flex items-center gap-3 rounded-2xl bg-bg-primary/40 border border-border-color/60 px-4 py-3">
                <Flame class="w-4 h-4 text-primary" />
                <div class="leading-tight">
                  <div class="text-xs text-text-muted">连续打卡</div>
                  <div class="text-sm font-semibold text-text-primary tabular-nums">{{ todayStats.streakDays }} 天</div>
                </div>
              </div>

              <div class="flex items-center gap-3 rounded-2xl bg-bg-primary/40 border border-border-color/60 px-4 py-3">
                <BadgeCheck class="w-4 h-4" :class="todayStats.checkedIn ? 'text-success' : 'text-warning'" />
                <div class="leading-tight">
                  <div class="text-xs text-text-muted">今日状态</div>
                  <div class="text-sm font-semibold" :class="todayStats.checkedIn ? 'text-success' : 'text-warning'">
                    {{ todayStats.checkedIn ? '已打卡' : '未打卡' }}
                  </div>
                </div>
              </div>
            </div>

            <div class="mt-6 flex flex-wrap gap-3">
              <button type="button" class="btn btn-primary" :disabled="todayStats.checkedIn" @click="checkin">
                {{ todayStats.checkedIn ? '今日已打卡' : '学习打卡' }}
              </button>
              <button type="button" class="btn btn-secondary" @click="goToLearningPlan">学习计划</button>
              <button type="button" class="btn btn-ghost" @click="router.push('/courses')">浏览课程</button>
            </div>
          </div>

          <div class="lg:col-span-4 flex items-center justify-center lg:justify-end">
            <div class="text-center">
              <ProgressRing :percentage="expPercentage" :size="140" type="primary">
                <div class="flex flex-col items-center justify-center">
                  <span class="text-3xl font-bold text-text-primary tabular-nums">{{ userInfo.level }}</span>
                  <span class="text-[10px] tracking-widest text-text-muted font-semibold mt-1">LEVEL</span>
                </div>
              </ProgressRing>

              <div class="mt-4 text-xs text-text-muted">
                <div class="font-medium text-text-secondary tabular-nums">
                  {{ userInfo.experience }} / {{ userInfo.nextLevelExp }} 经验
                </div>
                <div class="mt-1">
                  距离下一级还需
                  <span class="font-semibold text-text-primary tabular-nums">{{ userInfo.nextLevelExp - userInfo.experience }}</span>
                  经验
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
        <!-- Main -->
        <section class="lg:col-span-8 space-y-6">
          <div v-if="continueLearning" class="card-hover p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">继续学习</h2>
                <p class="text-sm text-text-secondary mt-1">从上次的位置继续。</p>
              </div>
              <button type="button" class="btn btn-ghost" @click="continueCourse">
                进入学习 <ArrowRight class="w-4 h-4 ml-1 inline" />
              </button>
            </div>

            <div class="mt-5 grid grid-cols-1 md:grid-cols-12 gap-5 items-center">
              <div class="md:col-span-5">
                <div class="relative overflow-hidden rounded-2xl border border-border-color/60 bg-bg-tertiary">
                  <img
                    :src="continueLearning.coverImage || getDefaultCover()"
                    class="w-full aspect-video object-cover"
                    alt="课程封面"
                  />
                </div>
              </div>
              <div class="md:col-span-7">
                <h3 class="text-xl font-semibold text-text-primary">{{ continueLearning.courseName }}</h3>
                <p class="mt-1 text-sm text-text-secondary">{{ continueLearning.currentChapter }}</p>

                <div class="mt-4 flex items-center gap-3">
                  <div class="h-2 flex-1 rounded-full bg-bg-tertiary overflow-hidden">
                    <div
                      class="h-full bg-gradient-to-r from-primary-light to-secondary rounded-full transition-[width] duration-300"
                      :style="{ width: `${continueLearning.progressPercent}%` }"
                    ></div>
                  </div>
                  <span class="text-xs font-semibold text-primary tabular-nums">{{ continueLearning.progressPercent }}%</span>
                </div>

                <div class="mt-5">
                  <button type="button" class="btn btn-primary" @click="continueCourse">继续学习</button>
                </div>
              </div>
            </div>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">我的课程</h2>
                <p class="text-sm text-text-secondary mt-1">快速返回你正在学习的内容。</p>
              </div>
              <button type="button" class="btn btn-ghost" @click="goToMyCourses">查看全部</button>
            </div>

            <div v-if="myCourses.length" class="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
              <button
                v-for="course in myCourses.slice(0, 4)"
                :key="course.courseId"
                type="button"
                class="group text-left rounded-2xl border border-border-color/60 bg-bg-secondary/60 hover:bg-bg-secondary transition shadow-sm hover:shadow-md px-4 py-4 flex gap-4"
                @click="goToCourse(course.courseId)"
              >
                <div class="w-16 h-16 rounded-2xl overflow-hidden border border-border-color/60 bg-bg-tertiary flex-shrink-0">
                  <img :src="course.coverImage || getDefaultCover()" alt="课程封面" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between gap-3">
                    <div class="min-w-0">
                      <div class="text-sm font-semibold text-text-primary line-clamp-1">{{ course.courseName }}</div>
                      <div class="mt-1 text-xs text-text-muted">{{ course.status }}</div>
                    </div>
                    <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold border" :class="statusBadgeClass(course.status)">
                      {{ course.progressPercent }}%
                    </span>
                  </div>
                  <div class="mt-3 h-1.5 rounded-full bg-bg-tertiary overflow-hidden">
                    <div
                      class="h-full bg-gradient-to-r from-primary-light to-secondary rounded-full"
                      :style="{ width: `${course.progressPercent}%` }"
                    ></div>
                  </div>
                </div>
              </button>
            </div>

            <EmptyState
              v-else
              :icon="BookOpen"
              title="暂无课程"
              description="去课程中心选择一门你感兴趣的课程开始学习。"
              action-text="去选课"
              @action="router.push('/courses')"
            />
          </div>
        </section>

        <!-- Side -->
        <aside class="lg:col-span-4 space-y-6">
          <div class="card p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">本周学习</h2>
                <p class="text-sm text-text-secondary mt-1">学习时长趋势</p>
              </div>
              <div class="text-right">
                <div class="text-2xl font-semibold text-text-primary tabular-nums">{{ weeklyStats.totalMinutes }}</div>
                <div class="text-xs text-text-muted">分钟</div>
              </div>
            </div>

            <div ref="weeklyChart" class="mt-4 h-56 w-full"></div>

            <div class="mt-4 flex items-center justify-between text-xs text-text-muted">
              <span>日均</span>
              <span class="font-semibold text-text-primary tabular-nums">{{ Math.round(weeklyStats.totalMinutes / 7) }} 分钟</span>
            </div>
          </div>

          <div class="card p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">最近成就</h2>
                <p class="text-sm text-text-secondary mt-1">记录你的进步</p>
              </div>
              <button type="button" class="btn btn-ghost" @click="goToAchievements">查看全部</button>
            </div>

            <div v-if="recentAchievements.length" class="mt-5 space-y-3">
              <div
                v-for="achievement in recentAchievements.slice(0, 5)"
                :key="achievement.achievementId"
                class="flex items-center gap-3 rounded-2xl bg-bg-tertiary/40 border border-border-color/60 px-4 py-3"
              >
                <div class="w-9 h-9 rounded-xl bg-bg-secondary/70 border border-border-color/60 flex items-center justify-center text-primary">
                  <Award class="w-5 h-5" />
                </div>
                <div class="min-w-0 flex-1">
                  <div class="text-sm font-semibold text-text-primary line-clamp-1">{{ achievement.name }}</div>
                  <div class="text-xs text-text-muted mt-0.5">{{ achievement.unlockedAt }}</div>
                </div>
              </div>
            </div>

            <EmptyState v-else :icon="Award" title="暂无成就" description="完成学习任务解锁成就徽章。" />
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { ArrowRight, Award, BadgeCheck, BookOpen, Clock, Flame, Sparkles } from 'lucide-vue-next'
import { getStudentDashboard } from '@/api/student'
import { checkin as learningCheckin } from '@/api/learning'
import ProgressRing from '@/components/ProgressRing.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()

const loading = ref(true)
const userInfo = ref({
  username: '',
  realName: '',
  level: 1,
  experience: 0,
  nextLevelExp: 100,
})

const todayStats = ref({
  studyMinutes: 0,
  streakDays: 0,
  checkedIn: false,
})

const continueLearning = ref(null)

const weeklyStats = ref({
  dailyMinutes: [0, 0, 0, 0, 0, 0, 0],
  totalMinutes: 0,
})

const myCourses = ref([])
const recentAchievements = ref([])
const weeklyChart = ref(null)
let chartInstance = null
let themeObserver = null

const expPercentage = computed(() => {
  return Math.round((userInfo.value.experience / userInfo.value.nextLevelExp) * 100)
})

const getDefaultCover = () => 'https://via.placeholder.com/400x240?text=Course'

const statusBadgeClass = (status) => {
  if (status === '已完成') return 'bg-success/10 text-success border-success/30'
  if (status === '进行中') return 'bg-primary/10 text-primary border-primary/30'
  if (status === '未开始') return 'bg-info/10 text-info border-info/30'
  return 'bg-bg-secondary text-text-secondary border-border-color/60'
}

const loadDashboardData = async () => {
  try {
    loading.value = true
    const res = await getStudentDashboard()

    if (res.code === 200 && res.data) {
      const data = res.data

      if (data.userInfo) userInfo.value = data.userInfo
      if (data.todayStats) todayStats.value = data.todayStats

      continueLearning.value = data.continueLearning || null
      if (data.weeklyStats) weeklyStats.value = data.weeklyStats

      myCourses.value = data.myCourses || []
      recentAchievements.value = data.recentAchievements || []

      await nextTick()
      initWeeklyChart()
    }
  } catch (error) {
    console.error('加载 Dashboard 数据失败:', error)
    ElMessage.error('加载数据失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

const checkin = async () => {
  try {
    const res = await learningCheckin({
      studyMinutes: todayStats.value.studyMinutes || 0,
      studyContent: '今日学习打卡',
    })

    if (res.code === 200 && res.data) {
      let message = `打卡成功！连续学习 ${res.data.currentStreak || 0} 天`
      if (res.data.newAchievementEarned && res.data.newAchievement) {
        message += `\n解锁新成就：${res.data.newAchievement.name}`
      }

      ElMessage.success(message)
      await loadDashboardData()
    }
  } catch (error) {
    console.error('打卡失败:', error)
    if (error.response?.data?.message) {
      ElMessage.warning(error.response.data.message)
      return
    }
    ElMessage.error(error.message || '打卡失败')
  }
}

const continueCourse = () => {
  if (!continueLearning.value) return
  router.push(`/course/${continueLearning.value.courseId}/study`)
}

const goToCourse = (courseId) => {
  router.push(`/course/${courseId}/study`)
}

const goToMyCourses = () => {
  router.push('/my-courses')
}

const goToLearningPlan = () => {
  router.push('/learning-plan')
}

const goToAchievements = () => {
  router.push('/achievements')
}

const initWeeklyChart = () => {
  if (!weeklyChart.value) return

  const style = getComputedStyle(document.documentElement)

  const readRgb = (name, fallback = '0 0 0') => style.getPropertyValue(name).trim() || fallback
  const toRgb = (rgb) => {
    const [r, g, b] = rgb.split(/\s+/).map((value) => Number(value))
    if (![r, g, b].every(Number.isFinite)) return 'rgb(0, 0, 0)'
    return `rgb(${r}, ${g}, ${b})`
  }

  const primaryRgb = readRgb('--primary-color-rgb', '79 70 229')
  const textPrimary = toRgb(readRgb('--text-primary-rgb', '17 24 39'))
  const textMuted = toRgb(readRgb('--text-muted-rgb', '107 114 128'))
  const border = toRgb(readRgb('--border-color-rgb', '229 231 235'))
  const grid = toRgb(readRgb('--border-light-rgb', '243 244 246'))
  const surface = toRgb(readRgb('--bg-secondary-rgb', '255 255 255'))
  const primary = toRgb(primaryRgb)

  const [r, g, b] = primaryRgb.split(/\s+/).map(Number)
  const areaTop = `rgba(${r}, ${g}, ${b}, 0.22)`
  const areaBottom = `rgba(${r}, ${g}, ${b}, 0.02)`

  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(weeklyChart.value)

  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} 分钟',
      backgroundColor: surface,
      borderColor: border,
      borderWidth: 1,
      textStyle: { color: textPrimary },
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: { lineStyle: { color: border } },
      axisLabel: { color: textMuted, fontSize: 12 },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      nameTextStyle: { color: textMuted, fontSize: 12 },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: textMuted, fontSize: 12 },
      splitLine: { lineStyle: { color: grid, type: 'dashed' } },
    },
    series: [
      {
        data: weeklyStats.value.dailyMinutes,
        type: 'line',
        smooth: true,
        showSymbol: false,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: areaTop },
            { offset: 1, color: areaBottom },
          ]),
        },
        itemStyle: { color: primary },
        lineStyle: { width: 2, color: primary },
      },
    ],
    grid: { left: '10%', right: '6%', bottom: '12%', top: '14%' },
  })
}

const handleResize = () => chartInstance?.resize()

onMounted(async () => {
  await loadDashboardData()
  window.addEventListener('resize', handleResize)

  themeObserver = new MutationObserver(() => {
    initWeeklyChart()
  })
  themeObserver.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['data-theme'],
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  themeObserver?.disconnect()
  themeObserver = null
  chartInstance?.dispose()
  chartInstance = null
})
</script>
