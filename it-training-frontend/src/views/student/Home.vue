<template>
  <div class="page">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-12">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col lg:flex-row items-start lg:items-center justify-between gap-10">
        <div class="max-w-2xl">
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <Sparkles class="w-4 h-4 text-primary" />
            用 AI 更高效学习
          </p>
          <h1 class="mt-4 text-4xl md:text-6xl font-semibold tracking-tight text-text-primary leading-tight">
            掌握 <span class="text-primary">IT 核心技能</span><br />
            开启职业新篇章
          </h1>
          <p class="mt-5 text-lg md:text-xl text-text-secondary leading-relaxed max-w-xl">
            课程 + AI 路径推荐 + 数据化反馈，帮助你系统化提升。
          </p>

          <div class="mt-8 flex flex-col sm:flex-row gap-3">
            <button type="button" class="btn btn-primary px-8" @click="$router.push('/courses')">
              浏览全部课程
              <ArrowRight class="w-4 h-4 ml-2" />
            </button>
            <button type="button" class="btn btn-secondary px-8" @click="$router.push('/recommend')">
              <Wand2 class="w-5 h-5 mr-2" />
              AI 智能选课
            </button>
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4 w-full max-w-md">
          <div class="card p-5">
            <p class="text-xs text-text-muted">精品课程</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">{{ stats.totalCourses }}+</p>
          </div>
          <div class="card p-5">
            <p class="text-xs text-text-muted">智能辅助</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">AI</p>
          </div>
          <div class="card p-5">
            <p class="text-xs text-text-muted">正在学习</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">{{ stats.enrolledCount }}</p>
          </div>
          <div class="card p-5">
            <p class="text-xs text-text-muted">实战导向</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">100%</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Hot -->
    <section class="space-y-6">
      <div class="flex items-end justify-between gap-6">
        <div>
          <h2 class="text-2xl font-semibold tracking-tight text-text-primary">热门课程</h2>
          <p class="mt-1 text-sm text-text-muted">精选课程，快速建立你的技能栈。</p>
        </div>
        <router-link
          to="/courses"
          class="inline-flex items-center gap-2 text-sm font-medium text-text-secondary hover:text-text-primary transition-colors"
        >
          查看全部
          <ArrowRight class="w-4 h-4" />
        </router-link>
      </div>

      <div v-if="loadingHot" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="i in 6" :key="i" class="card h-80 animate-pulse"></div>
      </div>

      <div v-else-if="hotCourses.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="course in hotCourses"
          :key="course.id"
          class="card-hover overflow-hidden cursor-pointer flex flex-col h-full"
          @click="$router.push(`/course/${course.id}`)"
        >
          <div class="h-44 bg-bg-tertiary/60 border-b border-border-color/60 flex items-center justify-center">
            <component :is="getCategoryIcon(course.category)" class="w-14 h-14 text-primary/80" />
          </div>

          <div class="p-6 flex-1 flex flex-col">
            <div class="flex flex-wrap items-center gap-2 mb-3">
              <span class="badge badge-secondary">{{ course.categoryName || getCategoryName(course.category) }}</span>
              <span class="badge badge-secondary">{{ course.difficultyName || getDifficultyName(course.difficulty) }}</span>
            </div>
            <h3 class="text-lg font-semibold text-text-primary mb-2 line-clamp-2 hover:text-primary transition-colors">
              {{ course.name }}
            </h3>
            <p class="text-text-secondary text-sm mb-4 line-clamp-2 flex-1">
              {{ course.description || '暂无描述' }}
            </p>

            <div class="flex items-center justify-between pt-4 border-t border-border-color/60 mt-auto">
              <div class="flex items-center gap-2 text-xs text-text-muted">
                <Clock class="w-4 h-4" />
                <span>{{ course.durationHours }} 课时</span>
              </div>
              <span class="text-primary font-medium text-sm">查看详情</span>
            </div>
          </div>
        </div>
      </div>

      <EmptyState
        v-else
        :icon="BookOpen"
        title="暂无热门课程"
        description="稍后再来看看，或先浏览全部课程。"
        action-text="浏览课程"
        @action="$router.push('/courses')"
      />
    </section>

    <!-- Features -->
    <section class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="card-hover p-8 flex flex-col justify-between gap-6" @click="$router.push('/recommend')">
        <div>
          <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
            <Route class="w-6 h-6" />
          </div>
          <h3 class="mt-5 text-xl font-semibold text-text-primary tracking-tight">AI 智能学习路径</h3>
          <p class="mt-2 text-sm text-text-secondary leading-relaxed">
            让 AI 根据你的基础与目标，推荐更合适的课程顺序与学习节奏。
          </p>
        </div>
        <div class="inline-flex items-center gap-2 text-sm font-medium text-primary">
          开始规划
          <ArrowRight class="w-4 h-4" />
        </div>
      </div>

      <div class="card-hover p-8 flex flex-col justify-between gap-6" @click="$router.push('/learning')">
        <div>
          <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
            <BarChart3 class="w-6 h-6" />
          </div>
          <h3 class="mt-5 text-xl font-semibold text-text-primary tracking-tight">学习进度追踪</h3>
          <p class="mt-2 text-sm text-text-secondary leading-relaxed">
            记录学习时长、完成度与趋势，让你知道下一步该怎么提升。
          </p>
        </div>
        <div class="inline-flex items-center gap-2 text-sm font-medium text-primary">
          查看进度
          <ArrowRight class="w-4 h-4" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import {
  ArrowRight,
  BarChart3,
  BookOpen,
  Brain,
  Cloud,
  Clock,
  Code2,
  Database,
  Layout,
  Route,
  Server,
  Sparkles,
  Wand2,
} from 'lucide-vue-next'
import { getMyEnrollments } from '@/api/enrollment'
import { getCourses } from '@/api/course'
import EmptyState from '@/components/EmptyState.vue'

const stats = ref({
  enrolledCount: 0,
  totalCourses: 0,
})

const hotCourses = ref([])
const loadingHot = ref(false)

const categoryIcons = {
  BACKEND: Server,
  FRONTEND: Layout,
  DATABASE: Database,
  AI: Brain,
  CLOUD: Cloud,
}

const getCategoryIcon = (category) => categoryIcons[category] || Code2

const getCategoryName = (category) => {
  const names = {
    BACKEND: '后端开发',
    FRONTEND: '前端开发',
    DATABASE: '数据库',
    AI: '人工智能',
    CLOUD: '云计算',
  }
  return names[category] || '课程'
}

const getDifficultyName = (difficulty) => {
  const names = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
  return names[difficulty] || '未知'
}

const loadStats = async () => {
  loadingHot.value = true
  try {
    const enrollRes = await getMyEnrollments()
    const enrollments = enrollRes.data || []
    stats.value.enrolledCount = enrollments.filter((e) => e.status === 0).length

    const courseRes = await getCourses({ page: 1, size: 6, status: 1 })
    stats.value.totalCourses = courseRes.data?.total || 0
    hotCourses.value = courseRes.data?.records || []
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loadingHot.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>
