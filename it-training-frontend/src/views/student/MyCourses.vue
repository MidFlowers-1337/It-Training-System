<template>
  <div class="max-w-7xl mx-auto px-4 md:px-8 py-8" v-loading="loading">
    <!-- Header / Hero -->
    <section class="relative overflow-hidden rounded-3xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl shadow-sm">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>

      <div class="relative p-6 md:p-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 class="text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">我的课程</h1>
          <p class="mt-2 text-text-secondary">共 {{ totalCourses }} 门课程，继续你的学习。</p>
        </div>

        <div class="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
          <div class="relative w-full sm:w-80">
            <Search class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-text-muted" />
            <input
              v-model="searchKeyword"
              placeholder="搜索课程..."
              class="w-full pl-10 pr-4 py-2.5 rounded-full bg-bg-tertiary/50 border border-border-color/60 text-sm text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-primary/15 focus:border-primary/40 transition"
              @input="handleSearch"
            />
          </div>

          <button type="button" class="btn btn-primary whitespace-nowrap" @click="goToCourses">去选课</button>
        </div>
      </div>
    </section>

    <!-- Filters -->
    <div class="mt-8 flex flex-col lg:flex-row lg:items-center justify-between gap-4">
      <div class="inline-flex flex-wrap items-center gap-2 rounded-full bg-bg-secondary/60 border border-border-color/60 p-1">
        <button
          v-for="filter in filters"
          :key="filter.value"
          type="button"
          class="px-4 py-2 rounded-full text-sm font-medium transition-all"
          :class="activeFilter === filter.value ? 'bg-bg-secondary shadow-sm text-text-primary' : 'text-text-secondary hover:text-text-primary hover:bg-bg-tertiary/60'"
          @click="activeFilter = filter.value; handleFilterChange()"
        >
          <span>{{ filter.label }}</span>
          <span
            class="ml-2 inline-flex items-center justify-center min-w-6 h-6 px-2 rounded-full text-xs font-semibold"
            :class="activeFilter === filter.value ? 'bg-primary/10 text-primary' : 'bg-bg-tertiary/70 text-text-muted'"
          >
            {{ filter.count }}
          </span>
        </button>
      </div>

      <div class="flex items-center gap-3">
        <span class="text-sm text-text-muted">排序</span>
        <el-select v-model="sortBy" placeholder="排序方式" class="w-40" @change="handleSort">
          <el-option label="最近学习" value="recent" />
          <el-option label="进度最高" value="progress" />
          <el-option label="课程名称" value="name" />
        </el-select>
      </div>
    </div>

    <!-- List -->
    <div class="mt-6">
      <div v-if="paginatedCourses.length === 0" class="card p-8">
        <EmptyState :icon="emptyIcon" :title="emptyTitle" :description="emptyDescription" action-text="去选课" @action="goToCourses" />
      </div>

      <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
        <CourseCard v-for="course in paginatedCourses" :key="course.id" :course="course" show-progress @click="goToCourseDetail(course.id)">
          <template #actions>
            <button
              type="button"
              class="inline-flex items-center justify-center px-4 py-2 rounded-full text-sm font-semibold bg-primary text-white hover:bg-primary/90 transition disabled:opacity-50 disabled:cursor-not-allowed"
              @click.stop="goToStudy(course.id)"
            >
              {{ course.progressPercent >= 100 ? '复习课程' : course.progressPercent > 0 ? '继续学习' : '开始学习' }}
            </button>

            <el-dropdown @command="handleCommand($event, course)" trigger="click" @click.stop>
              <button
                type="button"
                class="inline-flex items-center justify-center w-10 h-10 rounded-full border border-border-color/60 bg-bg-secondary/70 hover:bg-bg-tertiary/60 transition"
              >
                <MoreHorizontal class="w-4 h-4 text-text-secondary" />
              </button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="notes">我的笔记</el-dropdown-item>
                  <el-dropdown-item command="certificate" :disabled="course.progressPercent < 100">查看证书</el-dropdown-item>
                  <el-dropdown-item command="share">分享课程</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </CourseCard>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="filteredCourses.length > pageSize" class="mt-10 flex justify-center">
      <div class="card px-6 py-4">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="filteredCourses.length"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CheckCircle2, CircleDashed, MoreHorizontal, PlayCircle, Search } from 'lucide-vue-next'
import { getMyEnrollments } from '@/api/enrollment'
import { getCourses } from '@/api/course'
import { getCourseProgress } from '@/api/learning'
import CourseCard from '@/components/CourseCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()

const loading = ref(false)
const allCourses = ref([])
const searchKeyword = ref('')
const activeFilter = ref('all')
const sortBy = ref('recent')
const currentPage = ref(1)
const pageSize = ref(12)

const totalCourses = computed(() => allCourses.value.length)

const learningCourses = computed(() => allCourses.value.filter((c) => c.progressPercent > 0 && c.progressPercent < 100))
const completedCourses = computed(() => allCourses.value.filter((c) => c.progressPercent >= 100))
const notStartedCourses = computed(() => allCourses.value.filter((c) => !c.progressPercent || c.progressPercent === 0))

const filters = computed(() => [
  { label: '全部', value: 'all', count: allCourses.value.length },
  { label: '进行中', value: 'learning', count: learningCourses.value.length },
  { label: '已完成', value: 'completed', count: completedCourses.value.length },
  { label: '未开始', value: 'notStarted', count: notStartedCourses.value.length },
])

const filteredCourses = computed(() => {
  let courses = allCourses.value

  if (activeFilter.value === 'learning') courses = learningCourses.value
  else if (activeFilter.value === 'completed') courses = completedCourses.value
  else if (activeFilter.value === 'notStarted') courses = notStartedCourses.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    courses = courses.filter(
      (c) => c.name.toLowerCase().includes(keyword) || (c.description && c.description.toLowerCase().includes(keyword)),
    )
  }

  if (sortBy.value === 'recent') {
    courses = [...courses].sort((a, b) => {
      const timeA = a.lastStudyAt ? new Date(a.lastStudyAt).getTime() : 0
      const timeB = b.lastStudyAt ? new Date(b.lastStudyAt).getTime() : 0
      return timeB - timeA
    })
  } else if (sortBy.value === 'progress') {
    courses = [...courses].sort((a, b) => (b.progressPercent || 0) - (a.progressPercent || 0))
  } else if (sortBy.value === 'name') {
    courses = [...courses].sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
  }

  return courses
})

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredCourses.value.slice(start, start + pageSize.value)
})

const emptyIcon = computed(() => {
  if (searchKeyword.value) return Search
  if (activeFilter.value === 'completed') return CheckCircle2
  if (activeFilter.value === 'learning') return PlayCircle
  if (activeFilter.value === 'notStarted') return CircleDashed
  return CircleDashed
})

const emptyTitle = computed(() => {
  if (searchKeyword.value) return '未找到相关课程'
  if (activeFilter.value === 'learning') return '暂无进行中的课程'
  if (activeFilter.value === 'completed') return '暂无已完成的课程'
  if (activeFilter.value === 'notStarted') return '暂无未开始的课程'
  return '暂无课程'
})

const emptyDescription = computed(() => {
  if (searchKeyword.value) return `未找到包含“${searchKeyword.value}”的课程`
  if (activeFilter.value === 'learning') return '开始学习一门课程吧'
  if (activeFilter.value === 'completed') return '完成课程学习解锁成就'
  if (activeFilter.value === 'notStarted') return '开始你的第一门课程'
  return '快去选课开启学习之旅吧！'
})

const loadMyCourses = async () => {
  loading.value = true
  try {
    const enrollRes = await getMyEnrollments()
    const enrollments = enrollRes.data || []

    const coursePromises = enrollments
      .filter((e) => e.status === 0)
      .map(async (enrollment) => {
        try {
          const courseRes = await getCourses({ name: enrollment.courseName })
          const course = courseRes.data?.records?.[0]

          if (!course) {
            console.warn('未找到课程:', enrollment.courseName)
            return null
          }

          let progress = null
          try {
            const progressRes = await getCourseProgress(course.id)
            progress = progressRes.data
          } catch (err) {
            console.warn('获取进度失败:', err)
          }

          return {
            ...course,
            progressPercent: progress?.progressPercent || 0,
            studyDuration: progress?.studyDurationMinutes || 0,
            lastStudyAt: progress?.updatedAt || enrollment.enrolledAt,
            chapterCount: 0,
            sessionId: enrollment.sessionId,
            sessionCode: enrollment.sessionCode,
            startDate: enrollment.startDate,
            endDate: enrollment.endDate,
          }
        } catch (err) {
          console.error('获取课程失败:', err)
          return null
        }
      })

    const courses = await Promise.all(coursePromises)
    allCourses.value = courses.filter((c) => c !== null)
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleSort = () => {
  currentPage.value = 1
}

const handleSizeChange = () => {
  currentPage.value = 1
}

const handlePageChange = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToCourses = () => router.push('/courses')
const goToStudy = (courseId) => router.push(`/course/${courseId}/study`)
const goToCourseDetail = (courseId) => router.push(`/course/${courseId}`)

const handleCommand = (command, course) => {
  switch (command) {
    case 'notes':
      ElMessage.info('笔记功能开发中...')
      break
    case 'certificate':
      ElMessage.success('证书功能开发中...')
      break
    case 'share':
      ElMessage.info('分享功能开发中...')
      break
  }
}

onMounted(() => {
  loadMyCourses()
})
</script>

