<script setup lang="ts">
/**
 * StatsSection - Apple 风格数据统计区组件
 *
 * 设计特征：
 * - 动态数字滚动效果（easeOutCubic）
 * - 4列网格布局
 * - 滚动触发显示
 */

import { ref, onMounted, watch } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'

interface Stat {
  label: string
  value: number
  suffix: string
}

interface Props {
  stats?: Stat[]
}

const props = withDefaults(defineProps<Props>(), {
  stats: () => [
    { label: '精品课程', value: 200, suffix: '+' },
    { label: '注册学员', value: 50000, suffix: '+' },
    { label: '讲师团队', value: 100, suffix: '+' },
    { label: '满意度', value: 98, suffix: '%' }
  ]
})

// 滚动触发
const sectionRef = ref<HTMLElement | null>(null)
const isVisible = ref(false)
const hasAnimated = ref(false)
const displayValues = ref<number[]>(props.stats.map(() => 0))

// 使用 VueUse 的 IntersectionObserver
useIntersectionObserver(
  sectionRef,
  ([{ isIntersecting }]) => {
    if (isIntersecting && !hasAnimated.value) {
      isVisible.value = true
      hasAnimated.value = true
      startAnimation()
    }
  },
  { threshold: 0.3 }
)

// 数字动画
const animateNumber = (index: number, target: number) => {
  const duration = 2000
  const startTime = performance.now()

  const step = (currentTime: number) => {
    const elapsed = currentTime - startTime
    const progress = Math.min(elapsed / duration, 1)
    // easeOutCubic 缓动
    const eased = 1 - Math.pow(1 - progress, 3)

    displayValues.value[index] = Math.round(target * eased)

    if (progress < 1) {
      requestAnimationFrame(step)
    }
  }

  requestAnimationFrame(step)
}

const startAnimation = () => {
  props.stats.forEach((stat, index) => {
    // 错开每个数字的动画开始时间
    setTimeout(() => {
      animateNumber(index, stat.value)
    }, index * 100)
  })
}

// 格式化大数字
const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return (num / 10000).toFixed(0) + '万'
  }
  return num.toLocaleString()
}
</script>

<template>
  <section ref="sectionRef" class="stats-section">
    <div class="stats-container">
      <div class="stats-grid">
        <div
          v-for="(stat, index) in stats"
          :key="stat.label"
          class="stat-item"
          :class="{ 'is-visible': isVisible }"
          :style="{ '--delay': `${index * 0.1}s` }"
        >
          <span class="stat-value">
            {{ formatNumber(displayValues[index]) }}{{ stat.suffix }}
          </span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* ========================================
   Apple 风格数据统计区
   ======================================== */

.stats-section {
  padding: 80px 0;
  background: var(--bg-secondary);
}

.stats-container {
  max-width: var(--content-medium);
  margin: 0 auto;
  padding: 0 var(--page-padding-x);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32px;
}

.stat-item {
  text-align: center;
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s ease, transform 0.6s ease;
  transition-delay: var(--delay, 0s);
}

.stat-item.is-visible {
  opacity: 1;
  transform: translateY(0);
}

.stat-value {
  display: block;
  font-size: 48px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.stat-label {
  display: block;
  margin-top: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .stats-section {
    padding: 64px 0;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px 16px;
  }

  .stat-value {
    font-size: 36px;
  }

  .stat-label {
    font-size: 13px;
  }
}

/* ===== 深色模式 ===== */
[data-theme="dark"] .stats-section {
  background: var(--bg-secondary);
}
</style>
