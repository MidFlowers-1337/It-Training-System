<template>
  <div class="progress-ring" :style="{ width: size + 'px', height: size + 'px' }">
    <svg :width="size" :height="size" class="progress-ring-svg">
      <!-- 背景圆环 -->
      <circle
        :cx="center"
        :cy="center"
        :r="radius"
        :stroke-width="strokeWidth"
        class="progress-ring-bg"
      />

      <!-- 进度圆环 -->
      <circle
        :cx="center"
        :cy="center"
        :r="radius"
        :stroke-width="strokeWidth"
        :stroke-dasharray="circumference"
        :stroke-dashoffset="dashOffset"
        class="progress-ring-progress"
        :class="progressClass"
      />
    </svg>

    <!-- 中心内容 -->
    <div class="progress-ring-content">
      <slot>
        <div class="progress-text">
          <span class="progress-value">{{ percentage }}</span>
          <span class="progress-unit">%</span>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  // 进度百分比 0-100
  percentage: {
    type: Number,
    default: 0,
    validator: (value) => value >= 0 && value <= 100
  },
  // 圆环大小
  size: {
    type: Number,
    default: 120
  },
  // 线条宽度
  strokeWidth: {
    type: Number,
    default: 8
  },
  // 颜色类型
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'success', 'warning', 'danger', 'info'].includes(value)
  }
})

// 计算属性
const center = computed(() => props.size / 2)
const radius = computed(() => (props.size - props.strokeWidth) / 2)
const circumference = computed(() => 2 * Math.PI * radius.value)
const dashOffset = computed(() => {
  const progress = Math.min(Math.max(props.percentage, 0), 100)
  return circumference.value * (1 - progress / 100)
})

const progressClass = computed(() => `progress-${props.type}`)
</script>

<style scoped>
.progress-ring {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.progress-ring-svg {
  transform: rotate(-90deg);
}

.progress-ring-bg {
  fill: none;
  stroke: var(--bg-tertiary);
}

.progress-ring-progress {
  fill: none;
  stroke-linecap: round;
  transition: stroke-dashoffset 0.6s ease;
}

.progress-primary {
  stroke: var(--primary-color);
}

.progress-success {
  stroke: var(--success-color);
}

.progress-warning {
  stroke: var(--warning-color);
}

.progress-danger {
  stroke: var(--error-color);
}

.progress-info {
  stroke: var(--info-color);
}

.progress-ring-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.progress-text {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.progress-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
}

.progress-unit {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-left: 2px;
}
</style>
