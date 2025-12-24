<template>
  <div class="course-detail-page">
    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
    </div>

    <template v-else-if="course.id">
      <!-- Hero Section (沉浸式顶部) -->
      <section class="course-hero">
        <!-- Background -->
        <div class="hero-bg">
          <img
            v-if="course.coverImage"
            :src="course.coverImage"
            :alt="course.name"
            class="hero-bg-image"
          />
          <div v-else class="hero-bg-placeholder">
            <component :is="getCategoryIcon(course.category)" class="hero-bg-icon" />
          </div>
          <div class="hero-overlay"></div>
        </div>

        <!-- Content -->
        <div class="hero-content">
          <!-- Back Button -->
          <button type="button" class="back-btn" @click="$router.back()">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M15 18l-6-6 6-6" />
            </svg>
            返回列表
          </button>

          <!-- Meta Tags -->
          <div class="course-meta">
            <span class="meta-tag category-tag">
              {{ course.categoryName || getCategoryName(course.category) }}
            </span>
            <span class="meta-tag" :class="`difficulty-${course.difficulty}`">
              {{ course.difficultyName || getDifficultyName(course.difficulty) }}
            </span>
          </div>

          <!-- Title -->
          <h1 class="course-title">{{ course.name }}</h1>

          <!-- Description -->
          <p class="course-desc">{{ course.description }}</p>

          <!-- Stats -->
          <div class="course-stats">
            <div class="stat">
              <span class="stat-value">{{ course.durationHours || 0 }}</span>
              <span class="stat-label">课时</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ course.enrollmentCount || 0 }}</span>
              <span class="stat-label">学员</span>
            </div>
            <div class="stat">
              <span class="stat-value">{{ sessions.length }}</span>
              <span class="stat-label">班期</span>
            </div>
          </div>

          <!-- Skill Tags -->
          <div v-if="skillTags.length" class="skill-tags">
            <span v-for="tag in skillTags" :key="tag" class="skill-tag">{{ tag }}</span>
          </div>
        </div>
      </section>

      <!-- Tab Navigation -->
      <div class="tab-container">
        <nav class="tab-nav">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            :class="['tab-btn', { active: activeTab === tab.id }]"
            @click="activeTab = tab.id"
          >
            {{ tab.name }}
          </button>
          <div
            class="tab-indicator"
            :style="{ transform: `translateX(${tabs.findIndex(t => t.id === activeTab) * 100}%)` }"
          ></div>
        </nav>
      </div>

      <!-- Tab Content -->
      <div class="content-container">
        <div class="content-layout">
          <!-- Main Content -->
          <main class="main-content">
            <!-- Overview Tab -->
            <div v-show="activeTab === 'overview'" class="tab-panel">
              <div class="section-card">
                <h2 class="section-title">课程介绍</h2>
                <div v-if="course.content" class="prose-content" v-html="course.content"></div>
                <div v-else class="empty-content">
                  <span class="empty-emoji">📄</span>
                  <p class="empty-text">课程介绍正在完善中</p>
                </div>
              </div>

              <!-- Instructor Card -->
              <div v-if="course.instructorName" class="section-card instructor-card">
                <h2 class="section-title">讲师信息</h2>
                <div class="instructor-info">
                  <div class="instructor-avatar">
                    {{ course.instructorName?.charAt(0) || 'T' }}
                  </div>
                  <div class="instructor-details">
                    <h3 class="instructor-name">{{ course.instructorName }}</h3>
                    <p class="instructor-role">资深讲师</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Outline Tab -->
            <div v-show="activeTab === 'outline'" class="tab-panel">
              <div class="section-card">
                <h2 class="section-title">课程大纲</h2>
                <div v-if="course.content" class="prose-content" v-html="course.content"></div>
                <div v-else class="empty-content">
                  <span class="empty-emoji">📚</span>
                  <p class="empty-text">课程大纲正在完善中</p>
                </div>
              </div>
            </div>

            <!-- Sessions Tab -->
            <div v-show="activeTab === 'sessions'" class="tab-panel">
              <div class="section-card">
                <h2 class="section-title">开班信息</h2>
                <div v-if="sessions.length === 0" class="empty-content">
                  <span class="empty-emoji">📅</span>
                  <p class="empty-text">暂无可报名班期</p>
                </div>
                <div v-else class="sessions-list">
                  <div
                    v-for="session in sessions"
                    :key="session.id"
                    class="session-item"
                    :class="{ disabled: session.remainingQuota <= 0 }"
                  >
                    <div class="session-info">
                      <h4 class="session-code">{{ session.sessionCode }}</h4>
                      <div class="session-details">
                        <span class="session-detail">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                            <line x1="16" y1="2" x2="16" y2="6" />
                            <line x1="8" y1="2" x2="8" y2="6" />
                            <line x1="3" y1="10" x2="21" y2="10" />
                          </svg>
                          {{ session.startDate }} 开课
                        </span>
                        <span v-if="session.schedule" class="session-detail">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="10" />
                            <path d="M12 6v6l4 2" />
                          </svg>
                          {{ session.schedule }}
                        </span>
                      </div>
                    </div>
                    <div class="session-action">
                      <span
                        class="quota-badge"
                        :class="session.remainingQuota < 10 ? 'warning' : 'normal'"
                      >
                        剩 {{ session.remainingQuota }} 名额
                      </span>
                      <Button
                        variant="primary"
                        size="sm"
                        :disabled="session.remainingQuota <= 0"
                        @click="openEnrollModal(session)"
                      >
                        {{ session.remainingQuota > 0 ? '立即报名' : '已满员' }}
                      </Button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </main>

          <!-- Sidebar -->
          <aside class="sidebar">
            <div class="sidebar-card">
              <h3 class="sidebar-title">选择班期报名</h3>
              <p class="sidebar-subtitle">选择合适的开课时间与学习安排</p>

              <div v-if="sessions.length === 0" class="sidebar-empty">
                <span class="empty-emoji">📅</span>
                <p class="empty-text">暂无可报名班期</p>
              </div>

              <div v-else class="sidebar-sessions">
                <div
                  v-for="session in sessions.slice(0, 3)"
                  :key="session.id"
                  class="sidebar-session"
                  :class="{ disabled: session.remainingQuota <= 0 }"
                >
                  <div class="session-header">
                    <span class="session-code">{{ session.sessionCode }}</span>
                    <span
                      class="quota-badge"
                      :class="session.remainingQuota < 10 ? 'warning' : 'normal'"
                    >
                      剩 {{ session.remainingQuota }}
                    </span>
                  </div>
                  <div class="session-date">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                      <line x1="16" y1="2" x2="16" y2="6" />
                      <line x1="8" y1="2" x2="8" y2="6" />
                      <line x1="3" y1="10" x2="21" y2="10" />
                    </svg>
                    {{ session.startDate }}
                  </div>
                  <Button
                    variant="primary"
                    size="sm"
                    class="session-btn"
                    :disabled="session.remainingQuota <= 0"
                    @click="openEnrollModal(session)"
                  >
                    {{ session.remainingQuota > 0 ? '报名' : '已满' }}
                  </Button>
                </div>

                <button
                  v-if="sessions.length > 3"
                  class="view-all-btn"
                  @click="activeTab = 'sessions'"
                >
                  查看全部 {{ sessions.length }} 个班期
                </button>
              </div>
            </div>
          </aside>
        </div>
      </div>

      <!-- Fixed Bottom CTA (Mobile) -->
      <div class="fixed-cta">
        <div class="cta-content">
          <div class="cta-info">
            <span class="cta-label">{{ sessions.length }} 个班期可选</span>
          </div>
          <Button
            variant="primary"
            size="lg"
            :disabled="sessions.length === 0"
            @click="activeTab = 'sessions'"
          >
            查看班期
          </Button>
        </div>
      </div>
    </template>

    <!-- Not Found -->
    <div v-else class="not-found">
      <span class="not-found-emoji">⚠️</span>
      <h2 class="not-found-title">课程不存在</h2>
      <p class="not-found-desc">该课程可能已被删除或不存在。</p>
      <Button variant="secondary" @click="$router.push('/courses')">
        返回课程列表
      </Button>
    </div>

    <!-- Enroll Confirm Modal -->
    <EnrollConfirmModal
      v-model="enrollModalVisible"
      :session="selectedSession"
      @success="loadSessions"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Button } from '@/design-system'
import { getCourseById } from '@/api/course'
import { getEnrollableSessions } from '@/api/session'
import { getCategoryName, getDifficultyName } from '@/utils/courseMapping'
import { getCategoryIcon } from '@/utils/categoryIcons'
import { EnrollConfirmModal } from './course-detail'

interface Course {
  id?: number
  name?: string
  description?: string
  content?: string
  category?: string
  categoryName?: string
  difficulty?: number
  difficultyName?: string
  durationHours?: number
  enrollmentCount?: number
  instructorName?: string
  coverImage?: string
  tags?: string
}

interface Session {
  id: number
  sessionCode: string
  startDate: string
  schedule?: string
  remainingQuota: number
}

const route = useRoute()

// State
const course = ref<Course>({})
const sessions = ref<Session[]>([])
const loading = ref(false)
const enrollModalVisible = ref(false)
const selectedSession = ref<Session | null>(null)
const activeTab = ref('overview')

// Tabs
const tabs = [
  { id: 'overview', name: '课程详情' },
  { id: 'outline', name: '课程大纲' },
  { id: 'sessions', name: '开班信息' },
]

// Computed
const skillTags = computed(() => {
  if (!course.value.tags) return []
  return course.value.tags.split(',').filter((t) => t.trim())
})

// API calls
const loadCourse = async () => {
  loading.value = true
  try {
    const res = await getCourseById(route.params.id as string)
    course.value = res.data
    await loadSessions()
  } catch (error) {
    console.error('加载课程详情失败:', error)
  } finally {
    loading.value = false
  }
}

const loadSessions = async () => {
  try {
    const res = await getEnrollableSessions(route.params.id as string)
    sessions.value = res.data || []
  } catch (error) {
    console.error('加载班期失败:', error)
  }
}

const openEnrollModal = (session: Session) => {
  if (session.remainingQuota <= 0) return
  selectedSession.value = session
  enrollModalVisible.value = true
}

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
/* ========================================
   Apple 风格课程详情页
   ======================================== */

.course-detail-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding-bottom: 100px;
}

/* ===== Loading ===== */
.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 2px solid var(--border-color);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ===== Hero Section ===== */
.course-hero {
  position: relative;
  min-height: 480px;
  display: flex;
  align-items: flex-end;
  padding: 48px 0;
}

.hero-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.hero-bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-bg-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color) 0%, #5856d6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-bg-icon {
  width: 120px;
  height: 120px;
  color: rgba(255, 255, 255, 0.3);
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.2) 0%,
    rgba(0, 0, 0, 0.6) 100%
  );
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 900px;
  margin: 0 auto;
  padding: 0 var(--page-padding-x, 48px);
  color: white;
}

/* Back Button */
.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  margin-bottom: 24px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* Meta Tags */
.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.meta-tag {
  padding: 5px 12px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.difficulty-1 { background: rgba(52, 199, 89, 0.3); }
.difficulty-2 { background: rgba(0, 122, 255, 0.3); }
.difficulty-3 { background: rgba(255, 149, 0, 0.3); }
.difficulty-4 { background: rgba(255, 59, 48, 0.3); }

/* Title */
.course-title {
  font-size: clamp(28px, 5vw, 44px);
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: -0.02em;
  margin-bottom: 12px;
}

/* Description */
.course-desc {
  font-size: 16px;
  line-height: 1.6;
  opacity: 0.9;
  max-width: 600px;
  margin-bottom: 24px;
}

/* Stats */
.course-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 20px;
}

.stat {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

.stat-label {
  font-size: 13px;
  opacity: 0.8;
}

/* Skill Tags */
.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  padding: 4px 10px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 4px;
  font-size: 12px;
}

/* ===== Tab Navigation ===== */
.tab-container {
  background: var(--bg-secondary);
  border-bottom: 0.5px solid var(--border-color);
  position: sticky;
  top: 48px;
  z-index: 100;
}

.tab-nav {
  position: relative;
  display: flex;
  max-width: 900px;
  margin: 0 auto;
  padding: 0 var(--page-padding-x, 48px);
}

.tab-btn {
  flex: 1;
  padding: 16px 0;
  background: none;
  border: none;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: color 0.2s ease;
  text-align: center;
}

.tab-btn:hover {
  color: var(--text-primary);
}

.tab-btn.active {
  color: var(--primary-color);
}

.tab-indicator {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  width: calc(100% / 3);
  background: var(--primary-color);
  transition: transform 0.25s cubic-bezier(0.25, 0.1, 0.25, 1.0);
}

/* ===== Content Container ===== */
.content-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 48px var(--page-padding-x, 48px);
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 48px;
  align-items: start;
}

@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .sidebar {
    display: none;
  }
}

/* ===== Main Content ===== */
.main-content {
  min-width: 0;
}

.tab-panel {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.prose-content {
  font-size: 15px;
  line-height: 1.7;
  color: var(--text-secondary);
}

.prose-content :deep(h1),
.prose-content :deep(h2),
.prose-content :deep(h3) {
  color: var(--text-primary);
  margin-top: 24px;
  margin-bottom: 12px;
}

.prose-content :deep(p) {
  margin-bottom: 12px;
}

.prose-content :deep(ul),
.prose-content :deep(ol) {
  padding-left: 20px;
  margin-bottom: 12px;
}

.empty-content {
  text-align: center;
  padding: 40px 20px;
}

.empty-emoji {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.empty-text {
  font-size: 14px;
  color: var(--text-muted);
}

/* Instructor Card */
.instructor-card {
  background: var(--bg-secondary);
}

.instructor-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.instructor-avatar {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  font-size: 20px;
  font-weight: 600;
}

.instructor-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.instructor-role {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 2px;
}

/* Sessions List */
.sessions-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.session-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  background: var(--bg-tertiary);
  border-radius: 12px;
  transition: background-color 0.2s ease;
}

.session-item:hover {
  background: var(--bg-secondary);
}

.session-item.disabled {
  opacity: 0.6;
}

.session-info {
  flex: 1;
  min-width: 0;
}

.session-code {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.session-details {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.session-detail {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.session-action {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.quota-badge {
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
}

.quota-badge.normal {
  color: var(--success);
  background: rgba(52, 199, 89, 0.1);
}

.quota-badge.warning {
  color: var(--warning);
  background: rgba(255, 149, 0, 0.1);
}

/* ===== Sidebar ===== */
.sidebar {
  position: sticky;
  top: 120px;
}

.sidebar-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04),
    0 4px 8px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.sidebar-subtitle {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 4px;
}

.sidebar-empty {
  text-align: center;
  padding: 32px 0;
}

.sidebar-sessions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.sidebar-session {
  padding: 16px;
  background: var(--bg-tertiary);
  border-radius: 12px;
}

.sidebar-session.disabled {
  opacity: 0.6;
}

.session-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.session-date {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 12px;
}

.session-btn {
  width: 100%;
}

.view-all-btn {
  width: 100%;
  padding: 12px;
  background: none;
  border: 1px dashed var(--border-color);
  border-radius: 8px;
  font-size: 13px;
  color: var(--primary-color);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.view-all-btn:hover {
  background: var(--bg-tertiary);
}

/* ===== Fixed Bottom CTA ===== */
.fixed-cta {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--bg-card);
  border-top: 0.5px solid var(--border-color);
  padding: 16px var(--page-padding-x, 24px);
  z-index: 100;
  backdrop-filter: saturate(180%) blur(20px);
}

@media (max-width: 1024px) {
  .fixed-cta {
    display: block;
  }

  .course-detail-page {
    padding-bottom: 120px;
  }
}

.cta-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 600px;
  margin: 0 auto;
}

.cta-label {
  font-size: 14px;
  color: var(--text-secondary);
}

/* ===== Not Found ===== */
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
  padding: 48px 24px;
}

.not-found-emoji {
  font-size: 64px;
  margin-bottom: 16px;
}

.not-found-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.not-found-desc {
  font-size: 15px;
  color: var(--text-secondary);
  margin-bottom: 24px;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .course-hero {
    min-height: 400px;
    padding: 32px 0;
  }

  .hero-content {
    padding: 0 24px;
  }

  .course-title {
    font-size: 28px;
  }

  .course-stats {
    gap: 24px;
  }

  .stat-value {
    font-size: 24px;
  }

  .tab-nav {
    padding: 0 24px;
  }

  .content-container {
    padding: 32px 24px;
  }

  .section-card {
    padding: 20px;
  }
}

/* ===== Dark Mode ===== */
[data-theme="dark"] .section-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .sidebar-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.2),
    0 2px 4px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .session-item:hover {
  background: var(--bg-tertiary);
}

[data-theme="dark"] .fixed-cta {
  background: rgba(29, 29, 31, 0.72);
  border-top-color: rgba(255, 255, 255, 0.08);
}

[data-theme="dark"] .tab-container {
  background: rgba(29, 29, 31, 0.72);
  backdrop-filter: saturate(180%) blur(20px);
}
</style>
