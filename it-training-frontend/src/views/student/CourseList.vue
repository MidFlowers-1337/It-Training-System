<template>
  <div class="course-list-page">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="header-text">
          <div class="breadcrumb">
            <svg class="breadcrumb-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" /><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
            </svg>
            课程中心
          </div>
          <h1 class="page-title">探索丰富的 IT 技能课程</h1>
          <p class="page-desc">选择分类与难度，找到适合你的下一门课。</p>
        </div>

        <div class="header-search">
          <Input
            v-model="searchKeyword"
            placeholder="搜索课程..."
            clearable
            @input="handleSearch"
          >
            <template #icon-left>
              <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
              </svg>
            </template>
          </Input>
        </div>
      </div>

      <!-- 装饰背景 -->
      <div class="header-bg">
        <div class="bg-orb bg-orb-1"></div>
      </div>
    </header>

    <div class="page-container">
      <div class="page-layout">
        <!-- 侧边筛选器 -->
        <aside class="filters-sidebar">
          <div class="filters-panel">
            <div class="filters-header">
              <svg class="filters-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="4" y1="21" x2="4" y2="14" /><line x1="4" y1="10" x2="4" y2="3" />
                <line x1="12" y1="21" x2="12" y2="12" /><line x1="12" y1="8" x2="12" y2="3" />
                <line x1="20" y1="21" x2="20" y2="16" /><line x1="20" y1="12" x2="20" y2="3" />
                <line x1="1" y1="14" x2="7" y2="14" /><line x1="9" y1="8" x2="15" y2="8" /><line x1="17" y1="16" x2="23" y2="16" />
              </svg>
              筛选
            </div>

            <!-- 分类筛选 -->
            <div class="filter-group">
              <h3 class="filter-group-title">分类</h3>
              <div class="filter-options">
                <button
                  class="filter-option"
                  :class="{ active: selectedCategory === null }"
                  @click="selectedCategory = null"
                >
                  <span>全部课程</span>
                  <svg v-if="selectedCategory === null" class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </button>
                <button
                  v-for="cat in categories"
                  :key="cat.value"
                  class="filter-option"
                  :class="{ active: selectedCategory === cat.value }"
                  @click="selectedCategory = cat.value"
                >
                  <span>{{ cat.label }}</span>
                  <svg v-if="selectedCategory === cat.value" class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </button>
              </div>
            </div>

            <!-- 难度筛选 -->
            <div class="filter-group">
              <h3 class="filter-group-title">难度</h3>
              <div class="filter-options">
                <button
                  class="filter-option"
                  :class="{ active: selectedDifficulty === null }"
                  @click="selectedDifficulty = null"
                >
                  <span>全部难度</span>
                  <svg v-if="selectedDifficulty === null" class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </button>
                <button
                  v-for="diff in difficulties"
                  :key="diff.value"
                  class="filter-option"
                  :class="{ active: selectedDifficulty === diff.value }"
                  @click="selectedDifficulty = diff.value"
                >
                  <span>{{ diff.label }}</span>
                  <svg v-if="selectedDifficulty === diff.value" class="check-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </button>
              </div>
            </div>

            <Button variant="ghost" class="reset-btn" @click="resetFilters">重置筛选</Button>
          </div>
        </aside>

        <!-- 课程列表主区域 -->
        <main class="courses-main">
          <!-- 工具栏 -->
          <div class="toolbar">
            <div class="result-count">共 {{ total }} 门课程</div>
            <div class="sort-wrapper">
              <span class="sort-label">排序</span>
              <Select v-model="sortBy" :options="sortOptions" class="sort-select" />
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="courses-grid">
            <div v-for="i in 6" :key="i" class="skeleton-card"></div>
          </div>

          <!-- 课程网格 -->
          <div v-else-if="courses.length > 0" class="courses-grid">
            <CourseCard
              v-for="course in courses"
              :key="course.id"
              :course="course"
              @click="handleCourseClick"
            />
          </div>

          <!-- 空状态 -->
          <EmptyState
            v-else
            emoji="🔍"
            title="未找到相关课程"
            description="试试调整筛选条件或搜索关键词。"
            action-text="重置筛选"
            @action="resetFilters"
          />

          <!-- 分页 -->
          <div v-if="total > pageSize" class="pagination">
            <Button
              variant="secondary"
              size="sm"
              :disabled="currentPage <= 1"
              @click="changePage(currentPage - 1)"
            >
              上一页
            </Button>
            <div class="page-numbers">
              <button
                v-for="page in visiblePages"
                :key="page"
                type="button"
                class="page-btn"
                :class="{ active: page === currentPage }"
                @click="changePage(page)"
              >
                {{ page }}
              </button>
            </div>
            <Button
              variant="secondary"
              size="sm"
              :disabled="currentPage >= totalPages"
              @click="changePage(currentPage + 1)"
            >
              下一页
            </Button>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Button, Input, Select, EmptyState } from '@/design-system'
import { getCourses } from '@/api/course'
import CourseCard from '@/components/CourseCard.vue'

interface Course {
  id: number
  name: string
  description?: string
  category: string
  categoryName?: string
  difficulty: number
  durationHours: number
  enrollmentCount?: number
  coverImage?: string
}

const router = useRouter()
const route = useRoute()

const selectedCategory = ref<string | null>(null)
const selectedDifficulty = ref<number | null>(null)
const searchKeyword = ref('')
const sortBy = ref('createTime')

const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const courses = ref<Course[]>([])
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

const sortOptions = [
  { label: '最新发布', value: 'createTime' },
  { label: '最热门', value: 'enrollmentCount' },
  { label: '课时最多', value: 'durationHours' },
]

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const visiblePages = computed(() => {
  const pages: number[] = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, start + 4)
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

const loadCourses = async () => {
  loading.value = true
  try {
    const params: Record<string, unknown> = {
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

let searchTimer: ReturnType<typeof setTimeout> | null = null
const handleSearch = () => {
  if (searchTimer) clearTimeout(searchTimer)
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

const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return
  currentPage.value = page
  loadCourses()
}

const handleCourseClick = (course: Course) => {
  router.push(`/course/${course.id}`)
}

watch([selectedCategory, selectedDifficulty, sortBy], () => {
  currentPage.value = 1
  loadCourses()
})

onMounted(() => {
  if (route.query.keyword) searchKeyword.value = route.query.keyword as string
  loadCourses()
})
</script>

<style scoped>
/* ========================================
   Apple 风格课程列表页
   ======================================== */

.course-list-page {
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ===== 页面头部 ===== */
.page-header {
  position: relative;
  padding: 80px 0 64px;
  background: var(--bg-secondary);
  overflow: hidden;
}

.header-content {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--page-padding-x, 48px);
  display: flex;
  flex-direction: column;
  gap: 32px;
}

@media (min-width: 1024px) {
  .header-content {
    flex-direction: row;
    align-items: flex-end;
    justify-content: space-between;
  }
}

.header-text {
  flex: 1;
}

.breadcrumb {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.breadcrumb-icon {
  width: 16px;
  height: 16px;
  color: var(--primary-color);
}

.page-title {
  margin-top: 12px;
  font-size: 40px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.page-desc {
  margin-top: 8px;
  font-size: 15px;
  color: var(--text-secondary);
}

.header-search {
  width: 100%;
  max-width: 400px;
}

.search-icon {
  width: 16px;
  height: 16px;
}

/* 装饰背景 */
.header-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
}

.bg-orb-1 {
  top: -100px;
  right: -100px;
  width: 300px;
  height: 300px;
  background: var(--primary-color);
  opacity: 0.1;
}

/* ===== 页面容器 ===== */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--page-padding-x, 48px);
}

.page-layout {
  display: flex;
  gap: 48px;
  padding: 48px 0 80px;
}

/* ===== 侧边筛选器 ===== */
.filters-sidebar {
  width: 260px;
  flex-shrink: 0;
}

@media (max-width: 1024px) {
  .filters-sidebar {
    display: none;
  }
}

.filters-panel {
  position: sticky;
  top: 72px;
  padding: 24px;
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.filters-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24px;
}

.filters-icon {
  width: 16px;
  height: 16px;
  color: var(--primary-color);
}

.filter-group {
  margin-bottom: 24px;
}

.filter-group-title {
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 12px;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: none;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.filter-option:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.filter-option.active {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  font-weight: 500;
}

.check-icon {
  width: 16px;
  height: 16px;
  color: var(--primary-color);
}

.reset-btn {
  width: 100%;
  justify-content: center;
  margin-top: 16px;
}

/* ===== 课程主区域 ===== */
.courses-main {
  flex: 1;
  min-width: 0;
}

/* 工具栏 */
.toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.result-count {
  font-size: 14px;
  color: var(--text-muted);
}

.sort-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-label {
  font-size: 12px;
  color: var(--text-muted);
}

.sort-select {
  width: 140px;
}

/* 课程网格 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

@media (max-width: 1200px) {
  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .courses-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }
}

/* 骨架屏 */
.skeleton-card {
  height: 360px;
  background: var(--bg-tertiary);
  border-radius: 16px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 48px;
}

.page-numbers {
  display: flex;
  align-items: center;
  gap: 4px;
}

.page-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  background: none;
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.page-btn:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.page-btn.active {
  background: var(--primary-color);
  color: white;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .page-header {
    padding: 64px 0 48px;
  }

  .page-title {
    font-size: 28px;
  }

  .page-layout {
    gap: 0;
    padding: 32px 0 64px;
  }
}

/* ===== 深色模式 ===== */
[data-theme="dark"] .filters-panel {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.2),
    0 2px 4px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .filter-option:hover,
[data-theme="dark"] .filter-option.active {
  background: var(--bg-tertiary);
}
</style>
