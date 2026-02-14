<template>
  <Teleport to="body">
    <Transition name="wizard">
      <div v-if="visible" class="wizard-overlay">
        <div class="wizard-backdrop" />
        <div class="wizard-dialog">
          <!-- Header -->
          <div class="wizard-header">
            <div class="wizard-steps">
              <span
                v-for="s in totalSteps"
                :key="s"
                :class="['wizard-step-dot', s <= step && 'wizard-step-dot--active']"
              />
            </div>
            <button class="wizard-skip" @click="skip">跳过</button>
          </div>

          <!-- Step content -->
          <div class="wizard-body">
            <!-- Step 1: Welcome + interests -->
            <div v-if="step === 1" class="wizard-step">
              <div class="wizard-icon-wrapper">
                <Sparkles class="wizard-icon" :stroke-width="1.5" />
              </div>
              <h2 class="wizard-heading">欢迎来到智能培训平台</h2>
              <p class="wizard-desc">选择你感兴趣的技术方向，我们将为你推荐合适的课程</p>
              <div class="wizard-tags">
                <button
                  v-for="tag in interestTags"
                  :key="tag"
                  :class="['wizard-tag', selectedInterests.includes(tag) && 'wizard-tag--active']"
                  @click="toggleInterest(tag)"
                >
                  {{ tag }}
                </button>
              </div>
            </div>

            <!-- Step 2: Weekly goal -->
            <div v-if="step === 2" class="wizard-step">
              <div class="wizard-icon-wrapper">
                <Target class="wizard-icon" :stroke-width="1.5" />
              </div>
              <h2 class="wizard-heading">设定每周学习目标</h2>
              <p class="wizard-desc">选择你每周想投入的学习时间</p>
              <div class="wizard-slider-section">
                <input
                  type="range"
                  v-model.number="weeklyHours"
                  min="1"
                  max="20"
                  step="1"
                  class="wizard-slider"
                />
                <div class="wizard-slider-label">
                  <span class="wizard-hours">{{ weeklyHours }}</span>
                  <span class="wizard-hours-unit">小时 / 周</span>
                </div>
                <p class="wizard-slider-hint">
                  {{ weeklyHours <= 3 ? '轻松入门，每天学一点' : weeklyHours <= 8 ? '稳步前行，效果显著' : weeklyHours <= 14 ? '高效学习，快速提升' : '全力冲刺，技术飞跃' }}
                </p>
              </div>
            </div>

            <!-- Step 3: Recommended courses -->
            <div v-if="step === 3" class="wizard-step">
              <div class="wizard-icon-wrapper">
                <BookOpen class="wizard-icon" :stroke-width="1.5" />
              </div>
              <h2 class="wizard-heading">为你推荐的课程</h2>
              <p class="wizard-desc">基于你的兴趣，我们挑选了这些课程</p>
              <div class="wizard-courses">
                <div v-for="course in recommendedCourses" :key="course.title" class="wizard-course-card">
                  <div class="wizard-course-emoji">{{ course.emoji }}</div>
                  <div class="wizard-course-info">
                    <p class="wizard-course-title">{{ course.title }}</p>
                    <p class="wizard-course-meta">{{ course.level }} · {{ course.duration }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="wizard-footer">
            <button v-if="step > 1" class="wizard-btn wizard-btn--secondary" @click="step--">
              上一步
            </button>
            <div class="wizard-footer-spacer" />
            <button v-if="step < totalSteps" class="wizard-btn wizard-btn--primary" @click="step++">
              下一步
            </button>
            <button v-else class="wizard-btn wizard-btn--primary" @click="complete">
              开始学习
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Sparkles, Target, BookOpen } from 'lucide-vue-next'

const STORAGE_KEY = 'onboarding_completed'

const visible = ref(false)
const step = ref(1)
const totalSteps = 3

/* ── Step 1: Interests ── */
const interestTags = [
  '前端开发', '后端开发', 'Java', 'Python',
  '数据分析', '人工智能', '云计算', 'DevOps',
  '数据库', '网络安全', '移动开发', '微服务',
]
const selectedInterests = ref<string[]>([])

function toggleInterest(tag: string) {
  const idx = selectedInterests.value.indexOf(tag)
  if (idx >= 0) {
    selectedInterests.value.splice(idx, 1)
  } else {
    selectedInterests.value.push(tag)
  }
}

/* ── Step 2: Weekly goal ── */
const weeklyHours = ref(5)

/* ── Step 3: Recommended courses (展示性数据) ── */
const recommendedCourses = computed(() => {
  // Map some interests to courses for demo
  const courseMap: Record<string, { title: string; emoji: string; level: string; duration: string }> = {
    '前端开发': { title: 'Vue 3 实战进阶', emoji: '💚', level: '中级', duration: '24课时' },
    '后端开发': { title: 'Spring Boot 微服务架构', emoji: '🍃', level: '中级', duration: '32课时' },
    'Java': { title: 'Java 核心技术精讲', emoji: '☕', level: '入门', duration: '28课时' },
    'Python': { title: 'Python 数据科学实战', emoji: '🐍', level: '入门', duration: '20课时' },
    '数据分析': { title: '商业数据分析方法论', emoji: '📊', level: '入门', duration: '16课时' },
    '人工智能': { title: '深度学习与 NLP 入门', emoji: '🤖', level: '中级', duration: '36课时' },
    '云计算': { title: 'Docker & K8s 容器编排', emoji: '☁️', level: '中级', duration: '24课时' },
    'DevOps': { title: 'CI/CD 流水线实践', emoji: '🔧', level: '中级', duration: '18课时' },
    '数据库': { title: 'MySQL 高性能优化', emoji: '🗄️', level: '进阶', duration: '20课时' },
    '网络安全': { title: 'Web 安全攻防基础', emoji: '🔒', level: '入门', duration: '22课时' },
    '移动开发': { title: 'Flutter 跨平台开发', emoji: '📱', level: '入门', duration: '26课时' },
    '微服务': { title: '微服务架构设计模式', emoji: '🏗️', level: '进阶', duration: '30课时' },
  }

  const selected = selectedInterests.value
  if (selected.length === 0) {
    // Default 3 courses — keys are guaranteed to exist in courseMap
    return [
      courseMap['前端开发']!,
      courseMap['Java']!,
      courseMap['Python']!,
    ]
  }

  return selected.slice(0, 3).map(tag => courseMap[tag] ?? courseMap['前端开发']!)
})

/* ── Actions ── */
function complete() {
  localStorage.setItem(STORAGE_KEY, 'true')
  visible.value = false
}

function skip() {
  localStorage.setItem(STORAGE_KEY, 'true')
  visible.value = false
}

/* ── Init ── */
onMounted(() => {
  if (!localStorage.getItem(STORAGE_KEY)) {
    visible.value = true
  }
})
</script>

<style scoped>
/* ── Overlay ── */
.wizard-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.wizard-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
}

/* ── Dialog ── */
.wizard-dialog {
  position: relative;
  width: 100%;
  max-width: 480px;
  background: rgb(var(--color-surface));
  border: 1px solid rgb(var(--color-border));
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
}

/* ── Header ── */
.wizard-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0;
}

.wizard-steps {
  display: flex;
  gap: 6px;
}

.wizard-step-dot {
  width: 24px;
  height: 4px;
  border-radius: 2px;
  background: rgb(var(--color-border));
  transition: background 0.2s, width 0.2s;
}

.wizard-step-dot--active {
  background: rgb(var(--color-primary));
}

.wizard-skip {
  font-size: 13px;
  color: rgb(var(--color-text-tertiary));
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.15s;
}

.wizard-skip:hover {
  color: rgb(var(--color-text-secondary));
  background: rgb(var(--color-surface-alt));
}

/* ── Body ── */
.wizard-body {
  padding: 32px 24px 24px;
}

.wizard-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.wizard-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: rgb(var(--color-primary) / 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
}

.wizard-icon {
  width: 28px;
  height: 28px;
  color: rgb(var(--color-primary));
}

.wizard-heading {
  font-size: 20px;
  font-weight: 700;
  color: rgb(var(--color-text));
  margin-bottom: 8px;
}

.wizard-desc {
  font-size: 14px;
  color: rgb(var(--color-text-secondary));
  margin-bottom: 24px;
  max-width: 360px;
}

/* ── Tags (Step 1) ── */
.wizard-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
  max-width: 400px;
}

.wizard-tag {
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 999px;
  border: 1px solid rgb(var(--color-border));
  background: transparent;
  color: rgb(var(--color-text-secondary));
  cursor: pointer;
  transition: all 0.15s;
}

.wizard-tag:hover {
  border-color: rgb(var(--color-primary) / 0.4);
  color: rgb(var(--color-text));
}

.wizard-tag--active {
  border-color: rgb(var(--color-primary));
  background: rgb(var(--color-primary) / 0.1);
  color: rgb(var(--color-primary));
  font-weight: 500;
}

/* ── Slider (Step 2) ── */
.wizard-slider-section {
  width: 100%;
  max-width: 320px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.wizard-slider {
  width: 100%;
  height: 6px;
  appearance: none;
  -webkit-appearance: none;
  border-radius: 3px;
  background: rgb(var(--color-border));
  outline: none;
}

.wizard-slider::-webkit-slider-thumb {
  appearance: none;
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgb(var(--color-primary));
  cursor: pointer;
  box-shadow: 0 2px 8px rgb(var(--color-primary) / 0.3);
}

.wizard-slider::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: rgb(var(--color-primary));
  cursor: pointer;
  box-shadow: 0 2px 8px rgb(var(--color-primary) / 0.3);
}

.wizard-slider-label {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.wizard-hours {
  font-size: 36px;
  font-weight: 700;
  color: rgb(var(--color-primary));
  line-height: 1;
}

.wizard-hours-unit {
  font-size: 14px;
  color: rgb(var(--color-text-secondary));
}

.wizard-slider-hint {
  font-size: 13px;
  color: rgb(var(--color-text-tertiary));
}

/* ── Courses (Step 3) ── */
.wizard-courses {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.wizard-course-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: var(--radius-lg);
  border: 1px solid rgb(var(--color-border));
  background: rgb(var(--color-surface-alt));
  text-align: left;
  transition: border-color 0.15s;
}

.wizard-course-card:hover {
  border-color: rgb(var(--color-primary) / 0.3);
}

.wizard-course-emoji {
  font-size: 28px;
  flex-shrink: 0;
}

.wizard-course-info {
  flex: 1;
  min-width: 0;
}

.wizard-course-title {
  font-size: 14px;
  font-weight: 600;
  color: rgb(var(--color-text));
  margin-bottom: 2px;
}

.wizard-course-meta {
  font-size: 12px;
  color: rgb(var(--color-text-tertiary));
}

/* ── Footer ── */
.wizard-footer {
  display: flex;
  align-items: center;
  padding: 16px 24px 24px;
  gap: 12px;
}

.wizard-footer-spacer {
  flex: 1;
}

.wizard-btn {
  padding: 10px 24px;
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius);
  border: none;
  cursor: pointer;
  transition: all 0.15s;
}

.wizard-btn--primary {
  background: rgb(var(--color-primary));
  color: #fff;
}

.wizard-btn--primary:hover {
  opacity: 0.9;
  box-shadow: 0 4px 12px rgb(var(--color-primary) / 0.3);
}

.wizard-btn--secondary {
  background: rgb(var(--color-surface-alt));
  color: rgb(var(--color-text-secondary));
  border: 1px solid rgb(var(--color-border));
}

.wizard-btn--secondary:hover {
  background: rgb(var(--color-border) / 0.3);
  color: rgb(var(--color-text));
}

/* ── Transitions ── */
.wizard-enter-active {
  transition: opacity 0.3s ease;
}
.wizard-enter-active .wizard-dialog {
  transition: transform 0.3s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.3s ease;
}
.wizard-leave-active {
  transition: opacity 0.2s ease;
}
.wizard-leave-active .wizard-dialog {
  transition: transform 0.2s ease, opacity 0.2s ease;
}
.wizard-enter-from {
  opacity: 0;
}
.wizard-enter-from .wizard-dialog {
  opacity: 0;
  transform: scale(0.95) translateY(12px);
}
.wizard-leave-to {
  opacity: 0;
}
.wizard-leave-to .wizard-dialog {
  opacity: 0;
  transform: scale(0.97);
}
</style>
