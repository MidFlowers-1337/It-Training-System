<template>
  <div class="home-page">
    <!-- Apple 风格 Hero 区域 -->
    <HeroSection />

    <!-- 数据统计区 -->
    <StatsSection :stats="statsData" />

    <!-- 特性展示区 -->
    <FeaturesSection />

    <!-- 热门课程区 -->
    <section class="courses-section">
      <div class="section-container">
        <div class="section-header-row">
          <div>
            <h2 class="section-title">热门课程</h2>
            <p class="section-desc">精选课程，快速建立你的技能栈</p>
          </div>
          <router-link to="/courses" class="view-all-link">
            查看全部
            <svg class="link-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
            </svg>
          </router-link>
        </div>

        <!-- 加载状态 -->
        <div v-if="loadingHot" class="courses-grid">
          <div v-for="i in 6" :key="i" class="course-skeleton" />
        </div>

        <!-- 课程列表 -->
        <div v-else-if="hotCourses.length > 0" class="courses-grid">
          <div
            v-for="(course, index) in hotCourses"
            :key="course.id"
            class="course-card reveal-item"
            :style="{ '--delay': `${index * 0.1}s` }"
            @click="$router.push(`/course/${course.id}`)"
          >
            <div class="course-cover">
              <component :is="getCategoryIcon(course.category)" class="cover-icon" />
            </div>

            <div class="course-content">
              <div class="course-tags">
                <span class="tag">{{ course.categoryName || getCategoryName(course.category) }}</span>
                <span class="tag">{{ course.difficultyName || getDifficultyName(course.difficulty) }}</span>
              </div>
              <h3 class="course-title">{{ course.name }}</h3>
              <p class="course-desc">{{ course.description || '暂无描述' }}</p>

              <div class="course-footer">
                <div class="course-meta">
                  <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                  </svg>
                  <span>{{ course.durationHours }} 课时</span>
                </div>
                <span class="view-detail">查看详情</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <EmptyState
          v-else
          emoji="📚"
          title="暂无热门课程"
          description="稍后再来看看，或先浏览全部课程。"
          action-text="浏览课程"
          @action="$router.push('/courses')"
        />
      </div>
    </section>

    <!-- 行动召唤区 -->
    <section class="cta-section">
      <div class="cta-container">
        <h2 class="cta-title">准备好开始学习了吗？</h2>
        <p class="cta-desc">加入我们，开启你的 IT 技能提升之旅</p>
        <div class="cta-actions">
          <Button variant="primary" size="lg" @click="$router.push('/courses')">
            立即开始
          </Button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, type Component, markRaw } from 'vue'
import { getMyEnrollments } from '@/api/enrollment'
import { getCourses } from '@/api/course'
import { Button, EmptyState } from '@/design-system'
import HeroSection from '@/components/home/HeroSection.vue'
import StatsSection from '@/components/home/StatsSection.vue'
import FeaturesSection from '@/components/home/FeaturesSection.vue'

// 图标组件
const IconServer = markRaw({ template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="8" rx="2" ry="2" /><rect x="2" y="14" width="20" height="8" rx="2" ry="2" /><line x1="6" y1="6" x2="6.01" y2="6" /><line x1="6" y1="18" x2="6.01" y2="18" /></svg>` })
const IconLayout = markRaw({ template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2" /><line x1="3" y1="9" x2="21" y2="9" /><line x1="9" y1="21" x2="9" y2="9" /></svg>` })
const IconDatabase = markRaw({ template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><ellipse cx="12" cy="5" rx="9" ry="3" /><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3" /><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5" /></svg>` })
const IconBrain = markRaw({ template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96.44 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 1.98-3A2.5 2.5 0 0 1 9.5 2Z" /><path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96.44 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-1.98-3A2.5 2.5 0 0 0 14.5 2Z" /></svg>` })
const IconCloud = markRaw({ template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z" /></svg>` })
const IconCode = markRaw({ template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6" /><polyline points="8 6 2 12 8 18" /></svg>` })

// 类型定义
interface Course {
  id: number
  name: string
  description?: string
  category: string
  categoryName?: string
  difficulty: number
  difficultyName?: string
  durationHours: number
}

// 状态
const stats = ref({
  enrolledCount: 0,
  totalCourses: 0,
})

const hotCourses = ref<Course[]>([])
const loadingHot = ref(false)

// 统计数据
const statsData = computed(() => [
  { label: '精品课程', value: stats.value.totalCourses || 200, suffix: '+' },
  { label: '正在学习', value: stats.value.enrolledCount || 0, suffix: '' },
  { label: '讲师团队', value: 50, suffix: '+' },
  { label: '满意度', value: 98, suffix: '%' }
])

// 分类图标映射
const categoryIcons: Record<string, Component> = {
  BACKEND: IconServer,
  FRONTEND: IconLayout,
  DATABASE: IconDatabase,
  AI: IconBrain,
  CLOUD: IconCloud,
}

const getCategoryIcon = (category: string): Component => categoryIcons[category] || IconCode

const getCategoryName = (category: string): string => {
  const names: Record<string, string> = {
    BACKEND: '后端开发',
    FRONTEND: '前端开发',
    DATABASE: '数据库',
    AI: '人工智能',
    CLOUD: '云计算',
  }
  return names[category] || '课程'
}

const getDifficultyName = (difficulty: number): string => {
  const names: Record<number, string> = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
  return names[difficulty] || '未知'
}

// 加载数据
const loadStats = async () => {
  loadingHot.value = true
  try {
    const enrollRes = await getMyEnrollments()
    const enrollments = enrollRes.data || []
    stats.value.enrolledCount = enrollments.filter((e: any) => e.status === 0).length

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

<style scoped>
/* ========================================
   首页样式 - Apple 风格
   ======================================== */

.home-page {
  background: var(--bg-primary);
}

/* ===== 热门课程区 ===== */
.courses-section {
  padding: var(--section-gap) 0;
  background: var(--bg-secondary);
}

.section-container {
  max-width: var(--content-wide);
  margin: 0 auto;
  padding: 0 var(--page-padding-x);
}

.section-header-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 48px;
}

.section-title {
  font-size: var(--text-h2);
  font-weight: var(--text-h2-weight);
  letter-spacing: var(--text-h2-tracking);
  color: var(--text-primary);
}

.section-desc {
  margin-top: 8px;
  font-size: 15px;
  color: var(--text-muted);
}

.view-all-link {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  transition: color 0.2s ease;
}

.view-all-link:hover {
  color: var(--text-primary);
}

.link-icon {
  width: 16px;
  height: 16px;
}

/* ===== 课程网格 ===== */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

/* ===== 骨架屏 ===== */
.course-skeleton {
  height: 360px;
  background: var(--bg-tertiary);
  border-radius: 16px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* ===== 课程卡片 ===== */
.course-card {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;

  /* Apple 风格阴影 */
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04),
    0 4px 8px rgba(0, 0, 0, 0.04);

  border: 0.5px solid rgba(0, 0, 0, 0.05);

  transition:
    transform 0.3s cubic-bezier(0.25, 0.1, 0.25, 1.0),
    box-shadow 0.3s cubic-bezier(0.25, 0.1, 0.25, 1.0);
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.05),
    0 8px 16px rgba(0, 0, 0, 0.06),
    0 16px 32px rgba(0, 0, 0, 0.08);
}

.course-cover {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-tertiary);
  border-bottom: 0.5px solid var(--border-color);
}

.cover-icon {
  width: 56px;
  height: 56px;
  color: var(--primary-color);
  opacity: 0.8;
}

.course-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 24px;
}

.course-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.tag {
  padding: 4px 10px;
  font-size: 12px;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  border-radius: 6px;
}

.course-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-desc {
  flex: 1;
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 16px;
}

.course-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 0.5px solid var(--border-light);
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-muted);
}

.meta-icon {
  width: 14px;
  height: 14px;
}

.view-detail {
  font-size: 14px;
  font-weight: 500;
  color: var(--primary-color);
}

/* ===== 行动召唤区 ===== */
.cta-section {
  padding: var(--section-gap) 0;
  background: var(--bg-primary);
}

.cta-container {
  max-width: var(--content-medium);
  margin: 0 auto;
  padding: 0 var(--page-padding-x);
  text-align: center;
}

.cta-title {
  font-size: var(--text-h2);
  font-weight: var(--text-h2-weight);
  letter-spacing: var(--text-h2-tracking);
  color: var(--text-primary);
}

.cta-desc {
  margin-top: 16px;
  font-size: 18px;
  color: var(--text-secondary);
}

.cta-actions {
  margin-top: 40px;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .section-header-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .courses-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .course-cover {
    height: 140px;
  }
}

/* ===== 深色模式 ===== */
[data-theme="dark"] .course-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.2),
    0 2px 4px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .course-card:hover {
  box-shadow:
    0 8px 16px rgba(0, 0, 0, 0.3),
    0 16px 32px rgba(0, 0, 0, 0.3);
}
</style>
