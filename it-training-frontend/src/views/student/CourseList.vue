<template>
  <div class="page">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col lg:flex-row lg:items-end justify-between gap-6">
        <div>
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <BookOpen class="w-4 h-4 text-primary" />
            课程中心
          </p>
          <h1 class="mt-3 text-4xl font-semibold tracking-tight text-text-primary">探索丰富的 IT 技能课程</h1>
          <p class="mt-2 text-sm text-text-secondary">选择分类与难度，找到适合你的下一门课。</p>
        </div>

        <div class="w-full lg:w-96">
          <div class="relative">
            <Search class="w-4 h-4 absolute left-4 top-1/2 -translate-y-1/2 text-text-muted" />
            <input
              v-model="searchKeyword"
              @input="handleSearch"
              placeholder="搜索课程..."
              class="w-full bg-bg-tertiary/60 border border-border-color/60 rounded-full py-2 pl-11 pr-4 text-sm text-text-primary focus:outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/15 transition-all placeholder-text-muted"
            />
          </div>
        </div>
      </div>
    </section>

    <div class="flex flex-col lg:flex-row gap-8">
      <!-- Filters -->
      <aside class="w-full lg:w-72 flex-shrink-0 lg:sticky lg:top-24 self-start">
        <div class="glass rounded-3xl p-6 border border-border-color/60 space-y-6">
          <div class="flex items-center gap-2 text-sm font-medium text-text-primary">
            <SlidersHorizontal class="w-4 h-4 text-primary" />
            筛选
          </div>

          <div>
            <h3 class="text-xs font-semibold text-text-muted tracking-widest uppercase">分类</h3>
            <div class="mt-3 inset-group">
              <button type="button" class="inset-item w-full text-left" @click="selectedCategory = null">
                <span class="font-medium" :class="selectedCategory === null ? 'text-text-primary' : 'text-text-secondary'">
                  全部课程
                </span>
                <Check v-if="selectedCategory === null" class="w-4 h-4 text-primary" />
              </button>

              <div v-for="cat in categories" :key="cat.value">
                <div class="inset-divider"></div>
                <button type="button" class="inset-item w-full text-left" @click="selectedCategory = cat.value">
                  <span class="font-medium" :class="selectedCategory === cat.value ? 'text-text-primary' : 'text-text-secondary'">
                    {{ cat.label }}
                  </span>
                  <Check v-if="selectedCategory === cat.value" class="w-4 h-4 text-primary" />
                </button>
              </div>
            </div>
          </div>

          <div>
            <h3 class="text-xs font-semibold text-text-muted tracking-widest uppercase">难度</h3>
            <div class="mt-3 inset-group">
              <button type="button" class="inset-item w-full text-left" @click="selectedDifficulty = null">
                <span class="font-medium" :class="selectedDifficulty === null ? 'text-text-primary' : 'text-text-secondary'">
                  全部难度
                </span>
                <Check v-if="selectedDifficulty === null" class="w-4 h-4 text-primary" />
              </button>

              <div v-for="diff in difficulties" :key="diff.value">
                <div class="inset-divider"></div>
                <button type="button" class="inset-item w-full text-left" @click="selectedDifficulty = diff.value">
                  <span class="font-medium" :class="selectedDifficulty === diff.value ? 'text-text-primary' : 'text-text-secondary'">
                    {{ diff.label }}
                  </span>
                  <Check v-if="selectedDifficulty === diff.value" class="w-4 h-4 text-primary" />
                </button>
              </div>
            </div>
          </div>

          <button type="button" class="btn btn-ghost w-full justify-center" @click="resetFilters">
            重置筛选
          </button>
        </div>
      </aside>

      <!-- List -->
      <main class="flex-1 min-w-0">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
          <div class="text-sm text-text-muted">共 {{ total }} 门课程</div>
          <div class="flex items-center gap-3">
            <span class="text-xs text-text-muted hidden sm:block">排序</span>
            <select
              v-model="sortBy"
              class="bg-bg-tertiary/60 border border-border-color/60 text-text-primary text-sm rounded-full focus:outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/15 block px-4 py-2"
            >
              <option value="createTime">最新发布</option>
              <option value="enrollmentCount">最热门</option>
              <option value="durationHours">课时最多</option>
            </select>
          </div>
        </div>

        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <div v-for="i in 6" :key="i" class="card h-80 animate-pulse"></div>
        </div>

        <div v-else-if="courses.length > 0" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <CourseCard v-for="course in courses" :key="course.id" :course="course" @click="handleCourseClick" />
        </div>

        <EmptyState
          v-else
          :icon="SearchX"
          title="未找到相关课程"
          description="试试调整筛选条件或搜索关键词。"
          action-text="重置筛选"
          @action="resetFilters"
        />

        <div v-if="total > pageSize" class="flex justify-center mt-10">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 48]"
            layout="prev, pager, next"
            background
            @current-change="loadCourses"
            @size-change="loadCourses"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { BookOpen, Check, Search, SearchX, SlidersHorizontal } from 'lucide-vue-next'
import { getCourses } from '@/api/course'
import CourseCard from '@/components/CourseCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()
const route = useRoute()

const selectedCategory = ref(null)
const selectedDifficulty = ref(null)
const searchKeyword = ref('')
const sortBy = ref('createTime')

const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const courses = ref([])
const loading = ref(false)

const categories = [
  { label: '后端开发', value: 'BACKEND' },
  { label: '前端开发', value: 'FRONTEND' },
  { label: '数据库', value: 'DATABASE' },
  { label: '人工智能', value: 'AI' },
  { label: '云计算', value: 'CLOUD' },
]

const difficulties = [
  { label: '入门', value: 1 },
  { label: '初级', value: 2 },
  { label: '中级', value: 3 },
  { label: '高级', value: 4 },
]

const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: 1,
    }

    if (selectedCategory.value) params.category = selectedCategory.value
    if (selectedDifficulty.value) params.difficulty = selectedDifficulty.value
    if (searchKeyword.value) params.keyword = searchKeyword.value

    if (sortBy.value) {
      params.sortBy = sortBy.value
      params.sortOrder = 'desc'
    }

    const res = await getCourses(params)
    courses.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载课程失败:', error)
  } finally {
    loading.value = false
  }
}

let searchTimer = null
const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    loadCourses()
  }, 500)
}

const resetFilters = () => {
  selectedCategory.value = null
  selectedDifficulty.value = null
  searchKeyword.value = ''
  sortBy.value = 'createTime'
  currentPage.value = 1
  loadCourses()
}

const handleCourseClick = (course) => {
  router.push(`/course/${course.id}`)
}

watch([selectedCategory, selectedDifficulty, sortBy], () => {
  currentPage.value = 1
  loadCourses()
})

onMounted(() => {
  if (route.query.keyword) searchKeyword.value = route.query.keyword
  loadCourses()
})
</script>
