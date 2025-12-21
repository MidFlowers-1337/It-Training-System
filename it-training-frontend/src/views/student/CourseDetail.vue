<template>
  <div class="min-h-screen bg-bg-primary" v-loading="loading">
    <div v-if="course.id" class="page">
      <button
        type="button"
        @click="$router.back()"
        class="inline-flex items-center gap-2 text-sm font-medium text-text-secondary hover:text-text-primary transition-colors"
      >
        <ChevronLeft class="w-4 h-4" />
        返回列表
      </button>

      <!-- Hero -->
      <section class="page-hero glass p-8 md:p-10">
        <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
        <div class="relative grid grid-cols-1 md:grid-cols-[112px,1fr] gap-8 items-start">
          <div class="w-28 h-28 rounded-3xl bg-bg-tertiary/60 border border-border-color/60 flex items-center justify-center">
            <component :is="getCategoryIcon(course.category)" class="w-12 h-12 text-primary/85" />
          </div>

          <div class="min-w-0">
            <div class="flex flex-wrap items-center gap-2 mb-4">
              <span class="badge badge-secondary">{{ course.categoryName || getCategoryName(course.category) }}</span>
              <span class="badge" :class="getDifficultyBadge(course.difficulty)">{{ course.difficultyName || '难度' }}</span>
            </div>

            <h1 class="text-3xl md:text-5xl font-semibold tracking-tight text-text-primary leading-tight">
              {{ course.name }}
            </h1>
            <p class="mt-4 text-base md:text-lg text-text-secondary leading-relaxed max-w-3xl">
              {{ course.description }}
            </p>

            <div class="mt-6 flex flex-wrap gap-6 text-sm text-text-secondary">
              <div class="inline-flex items-center gap-2">
                <Clock class="w-4 h-4 text-text-muted" />
                <span>{{ course.durationHours }} 课时</span>
              </div>
              <div class="inline-flex items-center gap-2">
                <Users class="w-4 h-4 text-text-muted" />
                <span>{{ course.enrollmentCount || 0 }} 人已报名</span>
              </div>
              <div class="inline-flex items-center gap-2">
                <UserCircle class="w-4 h-4 text-text-muted" />
                <span>{{ course.instructorName || '讲师' }}</span>
              </div>
            </div>

            <div v-if="skillTags.length" class="mt-6 flex flex-wrap gap-2">
              <span v-for="tag in skillTags" :key="tag" class="badge badge-secondary">{{ tag }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Content -->
      <div class="grid grid-cols-1 lg:grid-cols-[1fr,360px] gap-8 items-start">
        <section class="card p-6 md:p-8">
          <h2 class="text-xl font-semibold tracking-tight text-text-primary mb-4">课程大纲</h2>

          <div v-if="course.content" class="text-text-secondary leading-relaxed" v-html="course.content"></div>
          <EmptyState v-else :icon="FileText" title="暂无课程大纲" description="课程大纲正在完善中。" />
        </section>

        <aside class="glass rounded-3xl border border-border-color/60 p-6 sticky top-24">
          <h3 class="text-base font-semibold text-text-primary">选择班期报名</h3>
          <p class="mt-1 text-sm text-text-muted">选择合适的开课时间与学习安排。</p>

          <EmptyState v-if="sessions.length === 0" :icon="CalendarX2" title="暂无可报名班期" description="可以稍后再来查看。" />

          <div v-else class="mt-5 space-y-3">
            <div
              v-for="session in sessions"
              :key="session.id"
              class="card p-4"
              :class="session.remainingQuota > 0 ? 'hover:shadow-md' : 'opacity-60'"
            >
              <div class="flex items-start justify-between gap-4">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ session.sessionCode }}</p>
                  <div class="mt-2 space-y-1 text-sm text-text-secondary">
                    <div class="flex items-center gap-2">
                      <Calendar class="w-4 h-4 text-text-muted" />
                      <span>{{ session.startDate }} 开课</span>
                    </div>
                    <div v-if="session.schedule" class="flex items-center gap-2">
                      <Timer class="w-4 h-4 text-text-muted" />
                      <span>{{ session.schedule }}</span>
                    </div>
                  </div>
                </div>

                <span
                  class="text-xs font-medium"
                  :class="session.remainingQuota < 10 ? 'text-warning' : 'text-success'"
                >
                  剩 {{ session.remainingQuota }} 名额
                </span>
              </div>

              <button
                type="button"
                class="btn btn-primary w-full mt-4"
                :disabled="session.remainingQuota <= 0"
                @click="handleEnroll(session)"
              >
                {{ session.remainingQuota > 0 ? '立即报名' : '已满员' }}
              </button>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <div v-else-if="!loading" class="page">
      <EmptyState
        :icon="TriangleAlert"
        title="课程不存在"
        description="该课程可能已被删除或不存在。"
        action-text="返回课程列表"
        @action="$router.push('/courses')"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Calendar,
  CalendarX2,
  ChevronLeft,
  Clock,
  FileText,
  TriangleAlert,
  UserCircle,
  Users,
  Timer,
  Brain,
  Cloud,
  Code2,
  Database,
  Layout,
  Server,
} from 'lucide-vue-next'
import { getCourseById } from '@/api/course'
import { getEnrollableSessions } from '@/api/session'
import { enroll } from '@/api/enrollment'
import EmptyState from '@/components/EmptyState.vue'

const route = useRoute()
const router = useRouter()

const course = ref({})
const sessions = ref([])
const loading = ref(false)

const skillTags = computed(() => {
  if (!course.value.tags) return []
  return course.value.tags.split(',').filter((t) => t.trim())
})

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

const getDifficultyBadge = (difficulty) => {
  const map = {
    1: 'bg-success/10 text-success border border-success/20',
    2: 'bg-info/10 text-info border border-info/20',
    3: 'bg-warning/10 text-warning border border-warning/20',
    4: 'bg-error/10 text-error border border-error/20',
  }
  return map[difficulty] || 'bg-bg-tertiary text-text-secondary border border-border-color'
}

const loadCourse = async () => {
  loading.value = true
  try {
    const res = await getCourseById(route.params.id)
    course.value = res.data
    await loadSessions()
  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('课程不存在')
  } finally {
    loading.value = false
  }
}

const loadSessions = async () => {
  try {
    const res = await getEnrollableSessions(route.params.id)
    sessions.value = res.data || []
  } catch (error) {
    console.error('加载班期失败:', error)
  }
}

const handleEnroll = async (session) => {
  if (session.remainingQuota <= 0) return

  try {
    await ElMessageBox.confirm(
      `确定要报名「${session.sessionCode}」班期吗？\n开班日期: ${session.startDate}\n上课时间: ${session.schedule || '待定'}`,
      '确认报名',
      {
        confirmButtonText: '确认报名',
        cancelButtonText: '取消',
        type: 'info',
      }
    )
    await enroll(session.id)
    ElMessage.success('报名成功！可在“我的课程”中查看')
    loadSessions()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.response?.data?.message || '报名失败，请稍后重试'
      ElMessage.error(errorMsg)
      console.error('报名失败:', error)
    }
  }
}

onMounted(() => {
  loadCourse()
})
</script>
