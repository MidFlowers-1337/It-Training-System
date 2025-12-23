<template>
  <div class="course-card" @click="handleClick">
    <!-- 封面区 -->
    <div class="card-cover">
      <img
        v-if="course.coverImage"
        :src="course.coverImage"
        :alt="course.name"
        class="cover-image"
      />
      <div v-else class="cover-placeholder">
        <div class="cover-icon">
          <component :is="categoryIcon" />
        </div>
      </div>

      <!-- 难度标签 -->
      <span class="difficulty-badge" :class="`difficulty-${course.difficulty}`">
        {{ difficultyName }}
      </span>
    </div>

    <!-- 内容区 -->
    <div class="card-content">
      <!-- 分类 -->
      <div class="category-tag">
        <span class="category-dot" :style="{ backgroundColor: categoryColor }"></span>
        <span>{{ course.categoryName || '未分类' }}</span>
      </div>

      <!-- 标题 -->
      <h3 class="card-title">{{ course.name }}</h3>

      <!-- 描述 -->
      <p v-if="course.description" class="card-desc">{{ course.description }}</p>

      <!-- 标签 -->
      <div v-if="course.tags && course.tags.length > 0" class="card-tags">
        <span v-for="(tag, index) in course.tags.slice(0, 3)" :key="index" class="tag">
          {{ tag }}
        </span>
      </div>

      <!-- 底部元信息 -->
      <div class="card-footer">
        <div class="meta-row">
          <span class="meta-item">
            <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
            </svg>
            {{ course.durationHours }}课时
          </span>
          <span v-if="course.enrollmentCount" class="meta-item">
            <svg class="meta-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
              <circle cx="9" cy="7" r="4" />
              <path d="M22 21v-2a4 4 0 0 0-3-3.87" /><path d="M16 3.13a4 4 0 0 1 0 7.75" />
            </svg>
            {{ course.enrollmentCount }}人
          </span>
        </div>

        <!-- 进度条 -->
        <div v-if="showProgress && course.progress !== undefined" class="progress-row">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: `${course.progress}%` }"></div>
          </div>
          <span class="progress-text">{{ course.progress }}%</span>
        </div>

        <!-- 操作按钮插槽 -->
        <div v-if="$slots.actions" class="actions-row">
          <slot name="actions" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, h, type Component } from 'vue'

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
  tags?: string[]
  progress?: number
}

const props = defineProps<{
  course: Course
  showProgress?: boolean
}>()

const emit = defineEmits<{
  click: [course: Course]
}>()

const handleClick = () => {
  emit('click', props.course)
}

// 分类图标
const IconServer: Component = {
  render: () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('rect', { width: '20', height: '8', x: '2', y: '2', rx: '2' }),
    h('rect', { width: '20', height: '8', x: '2', y: '14', rx: '2' }),
    h('line', { x1: '6', x2: '6.01', y1: '6', y2: '6' }),
    h('line', { x1: '6', x2: '6.01', y1: '18', y2: '18' })
  ])
}

const IconCode: Component = {
  render: () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('polyline', { points: '16 18 22 12 16 6' }),
    h('polyline', { points: '8 6 2 12 8 18' })
  ])
}

const IconDatabase: Component = {
  render: () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('ellipse', { cx: '12', cy: '5', rx: '9', ry: '3' }),
    h('path', { d: 'M3 5V19A9 3 0 0 0 21 19V5' }),
    h('path', { d: 'M3 12A9 3 0 0 0 21 12' })
  ])
}

const IconBot: Component = {
  render: () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('path', { d: 'M12 8V4H8' }),
    h('rect', { width: '16', height: '12', x: '4', y: '8', rx: '2' }),
    h('path', { d: 'M2 14h2' }), h('path', { d: 'M20 14h2' }),
    h('path', { d: 'M15 13v2' }), h('path', { d: 'M9 13v2' })
  ])
}

const IconCloud: Component = {
  render: () => h('svg', { viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2' }, [
    h('path', { d: 'M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z' })
  ])
}

const categoryColor = computed(() => {
  const colors: Record<string, string> = {
    BACKEND: 'var(--primary-color)',
    FRONTEND: '#10b981',
    DATABASE: '#6366f1',
    AI: '#8b5cf6',
    CLOUD: '#f59e0b',
  }
  return colors[props.course.category] || 'var(--primary-color)'
})

const categoryIcon = computed(() => {
  const map: Record<string, Component> = {
    BACKEND: IconServer,
    FRONTEND: IconCode,
    DATABASE: IconDatabase,
    AI: IconBot,
    CLOUD: IconCloud,
  }
  return map[props.course.category] || IconCode
})

const difficultyName = computed(() => {
  const names: Record<number, string> = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
  return names[props.course.difficulty] || '未知'
})
</script>

<style scoped>
/* ========================================
   Apple 风格课程卡片
   ======================================== */

.course-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: var(--bg-card);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;

  /* Apple 风格多层阴影 */
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

/* ===== 封面区 ===== */
.card-cover {
  position: relative;
  aspect-ratio: 16 / 9;
  background: var(--bg-tertiary);
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.course-card:hover .cover-image {
  transform: scale(1.03);
}

.cover-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(var(--bg-secondary-rgb, 255, 255, 255) / 0.7);
  backdrop-filter: blur(10px);
  border: 0.5px solid var(--border-color);
  border-radius: 16px;
  color: var(--primary-color);
}

.cover-icon svg {
  width: 28px;
  height: 28px;
}

/* 难度标签 */
.difficulty-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.difficulty-1 {
  background: rgba(52, 199, 89, 0.15);
  color: #34c759;
  border: 0.5px solid rgba(52, 199, 89, 0.3);
}

.difficulty-2 {
  background: rgba(0, 122, 255, 0.15);
  color: #007aff;
  border: 0.5px solid rgba(0, 122, 255, 0.3);
}

.difficulty-3 {
  background: rgba(255, 149, 0, 0.15);
  color: #ff9500;
  border: 0.5px solid rgba(255, 149, 0, 0.3);
}

.difficulty-4 {
  background: rgba(255, 59, 48, 0.15);
  color: #ff3b30;
  border: 0.5px solid rgba(255, 59, 48, 0.3);
}

/* ===== 内容区 ===== */
.card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

/* 分类 */
.category-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
  color: var(--text-secondary);
}

.category-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

/* 标题 */
.card-title {
  margin-top: 8px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s ease;
}

.course-card:hover .card-title {
  color: var(--primary-color);
}

/* 描述 */
.card-desc {
  margin-top: 8px;
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 标签 */
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}

.tag {
  padding: 3px 8px;
  font-size: 11px;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  border-radius: 4px;
}

/* ===== 底部 ===== */
.card-footer {
  margin-top: auto;
  padding-top: 16px;
  border-top: 0.5px solid var(--border-light);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

.meta-icon {
  width: 14px;
  height: 14px;
}

/* 进度条 */
.progress-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-bar {
  flex: 1;
  height: 4px;
  background: var(--bg-tertiary);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light, #5ac8fa));
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 12px;
  font-weight: 600;
  color: var(--primary-color);
  font-variant-numeric: tabular-nums;
}

.actions-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
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

[data-theme="dark"] .cover-icon {
  background: rgba(29, 29, 31, 0.7);
}
</style>
