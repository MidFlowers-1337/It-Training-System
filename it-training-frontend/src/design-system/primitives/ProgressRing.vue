<script setup lang="ts">
/**
 * ProgressRing - 环形进度条组件
 *
 * 用于展示进度百分比
 * - SVG 绘制，支持自定义尺寸
 * - 支持多种颜色类型
 * - 支持自定义中心内容
 */

import { computed } from 'vue';

// Props 定义
interface Props {
  /** 进度百分比 0-100 */
  percentage?: number;
  /** 圆环大小（像素） */
  size?: number;
  /** 线条宽度（像素） */
  strokeWidth?: number;
  /** 颜色类型 */
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info';
  /** 是否显示百分比文字 */
  showText?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  percentage: 0,
  size: 120,
  strokeWidth: 8,
  type: 'primary',
  showText: true,
});

// 计算属性
const center = computed(() => props.size / 2);
const radius = computed(() => (props.size - props.strokeWidth) / 2);
const circumference = computed(() => 2 * Math.PI * radius.value);

// 进度偏移量
const dashOffset = computed(() => {
  const progress = Math.min(Math.max(props.percentage, 0), 100);
  return circumference.value * (1 - progress / 100);
});

// 进度条颜色
const strokeColor = computed(() => {
  const colors: Record<string, string> = {
    primary: 'var(--primary-color)',
    success: 'var(--success-color)',
    warning: 'var(--warning-color)',
    danger: 'var(--error-color)',
    info: 'var(--info-color)',
  };
  return colors[props.type] || colors.primary;
});

// 格式化百分比
const formattedPercentage = computed(() => {
  return Math.round(props.percentage);
});
</script>

<template>
  <div
    class="relative inline-flex items-center justify-center"
    :style="{ width: size + 'px', height: size + 'px' }"
  >
    <!-- SVG 圆环 -->
    <svg
      :width="size"
      :height="size"
      class="-rotate-90"
    >
      <!-- 背景圆环 -->
      <circle
        :cx="center"
        :cy="center"
        :r="radius"
        :stroke-width="strokeWidth"
        fill="none"
        class="stroke-bg-tertiary"
      />

      <!-- 进度圆环 -->
      <circle
        :cx="center"
        :cy="center"
        :r="radius"
        :stroke-width="strokeWidth"
        :stroke-dasharray="circumference"
        :stroke-dashoffset="dashOffset"
        :stroke="strokeColor"
        fill="none"
        stroke-linecap="round"
        class="transition-all duration-slow"
      />
    </svg>

    <!-- 中心内容 -->
    <div class="absolute inset-0 flex items-center justify-center">
      <slot>
        <div v-if="showText" class="flex items-baseline justify-center">
          <span class="text-2xl font-bold text-text-primary leading-none">
            {{ formattedPercentage }}
          </span>
          <span class="text-sm font-semibold text-text-secondary ml-0.5">
            %
          </span>
        </div>
      </slot>
    </div>
  </div>
</template>
