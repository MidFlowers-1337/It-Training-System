<script setup lang="ts">
/**
 * FeaturesSection - Apple 风格特性展示区组件
 *
 * 设计特征：
 * - 3列网格布局
 * - 精致的卡片 hover 效果
 * - 滚动触发动画（staggered）
 */

import { ref, type Component, markRaw } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'

interface Feature {
  icon: Component
  title: string
  description: string
}

// 图标组件
const IconPath = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <circle cx="6" cy="19" r="3" /><path d="M9 19h8.5a3.5 3.5 0 0 0 0-7h-11a3.5 3.5 0 0 1 0-7H15" />
    <circle cx="18" cy="5" r="3" />
  </svg>`
})

const IconChart = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M3 3v18h18" /><path d="M18 17V9" /><path d="M13 17V5" /><path d="M8 17v-3" />
  </svg>`
})

const IconUsers = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
    <circle cx="9" cy="7" r="4" />
    <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
    <path d="M16 3.13a4 4 0 0 1 0 7.75" />
  </svg>`
})

const IconBrain = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96.44 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 1.98-3A2.5 2.5 0 0 1 9.5 2Z" />
    <path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96.44 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-1.98-3A2.5 2.5 0 0 0 14.5 2Z" />
  </svg>`
})

const IconCode = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <polyline points="16 18 22 12 16 6" /><polyline points="8 6 2 12 8 18" />
  </svg>`
})

const IconAward = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <circle cx="12" cy="8" r="6" />
    <path d="M15.477 12.89 17 22l-5-3-5 3 1.523-9.11" />
  </svg>`
})

const features: Feature[] = [
  {
    icon: IconPath,
    title: 'AI 智能学习路径',
    description: '根据你的基础与目标，AI 智能推荐最适合的课程顺序与学习节奏。'
  },
  {
    icon: IconChart,
    title: '数据化学习反馈',
    description: '记录学习时长、完成度与趋势，让你知道下一步该怎么提升。'
  },
  {
    icon: IconCode,
    title: '项目驱动学习',
    description: '每门课程都有实战项目，在真实场景中巩固所学知识。'
  },
  {
    icon: IconBrain,
    title: 'AI 辅助答疑',
    description: '遇到问题随时提问，AI 助教 24 小时在线为你解答。'
  },
  {
    icon: IconUsers,
    title: '优质讲师团队',
    description: '来自一线大厂的资深工程师，分享真实工作经验与最佳实践。'
  },
  {
    icon: IconAward,
    title: '权威认证证书',
    description: '完成课程即可获得认证证书，为你的职业发展添砖加瓦。'
  }
]

// 滚动触发
const sectionRef = ref<HTMLElement | null>(null)
const isVisible = ref(false)

useIntersectionObserver(
  sectionRef,
  ([{ isIntersecting }]) => {
    if (isIntersecting) {
      isVisible.value = true
    }
  },
  { threshold: 0.1 }
)
</script>

<template>
  <section ref="sectionRef" class="features-section">
    <div class="features-container">
      <!-- 标题区 -->
      <div
        class="section-header"
        :class="{ 'is-visible': isVisible }"
      >
        <h2 class="section-title">为什么选择我们</h2>
        <p class="section-desc">体系化的课程设计，助你快速成长</p>
      </div>

      <!-- 特性网格 -->
      <div class="features-grid">
        <div
          v-for="(feature, index) in features"
          :key="feature.title"
          class="feature-card"
          :class="{ 'is-visible': isVisible }"
          :style="{ '--delay': `${0.2 + index * 0.1}s` }"
        >
          <div class="feature-icon">
            <component :is="feature.icon" />
          </div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* ========================================
   Apple 风格特性展示区
   ======================================== */

.features-section {
  padding: var(--section-gap) 0;
  background: var(--bg-primary);
}

.features-container {
  max-width: var(--content-wide);
  margin: 0 auto;
  padding: 0 var(--page-padding-x);
}

/* ===== 标题区 ===== */
.section-header {
  text-align: center;
  margin-bottom: 64px;
  opacity: 0;
  transform: translateY(30px);
  transition: opacity 0.6s ease, transform 0.6s ease;
}

.section-header.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.section-title {
  font-size: var(--text-h2);
  font-weight: var(--text-h2-weight);
  letter-spacing: var(--text-h2-tracking);
  color: var(--text-primary);
}

.section-desc {
  margin-top: 16px;
  font-size: 18px;
  color: var(--text-secondary);
}

/* ===== 特性网格 ===== */
.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
}

/* ===== 特性卡片 ===== */
.feature-card {
  padding: 32px;
  background: var(--bg-card);
  border-radius: 16px;
  text-align: center;

  /* Apple 风格多层阴影 */
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04),
    0 4px 8px rgba(0, 0, 0, 0.04);

  /* 极细边框 */
  border: 0.5px solid rgba(0, 0, 0, 0.05);

  /* 动画初始状态 */
  opacity: 0;
  transform: translateY(30px);
  transition:
    opacity 0.5s ease,
    transform 0.5s ease,
    box-shadow 0.3s cubic-bezier(0.25, 0.1, 0.25, 1.0);
  transition-delay: var(--delay, 0s);
}

.feature-card.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow:
    0 2px 2px rgba(0, 0, 0, 0.03),
    0 4px 8px rgba(0, 0, 0, 0.04),
    0 8px 16px rgba(0, 0, 0, 0.05),
    0 16px 32px rgba(0, 0, 0, 0.06);
}

.feature-card.is-visible:hover {
  transition-delay: 0s;
}

/* ===== 图标 ===== */
.feature-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color-alpha);
  border-radius: 16px;
  color: var(--primary-color);
}

.feature-icon svg {
  width: 28px;
  height: 28px;
}

/* ===== 标题和描述 ===== */
.feature-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.feature-desc {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* ===== 响应式 ===== */
@media (max-width: 1024px) {
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .features-section {
    padding: var(--section-gap-sm) 0;
  }

  .section-header {
    margin-bottom: 48px;
  }

  .section-title {
    font-size: 28px;
  }

  .section-desc {
    font-size: 16px;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .feature-card {
    padding: 24px;
  }

  .feature-icon {
    width: 56px;
    height: 56px;
  }
}

/* ===== 深色模式 ===== */
[data-theme="dark"] .feature-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.2),
    0 2px 4px rgba(0, 0, 0, 0.2),
    0 4px 8px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .feature-card:hover {
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.25),
    0 8px 16px rgba(0, 0, 0, 0.25),
    0 16px 32px rgba(0, 0, 0, 0.25);
}
</style>
