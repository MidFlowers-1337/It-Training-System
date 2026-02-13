<template>
  <div class="relative inline-flex items-center justify-center" :style="{ width: size + 'px', height: size + 'px' }">
    <svg class="transform -rotate-90" :width="size" :height="size">
      <circle :cx="center" :cy="center" :r="radius" fill="none" stroke="rgb(var(--color-surface-alt))" :stroke-width="strokeWidth" />
      <circle :cx="center" :cy="center" :r="radius" fill="none" :stroke="`rgb(var(--color-${color}))`" :stroke-width="strokeWidth" :stroke-dasharray="circumference" :stroke-dashoffset="offset" stroke-linecap="round" class="transition-all duration-700 ease-out" />
    </svg>
    <div class="absolute inset-0 flex items-center justify-center">
      <slot>
        <span v-if="showValue" class="font-semibold text-text-primary" :style="{ fontSize: (size * 0.25) + 'px' }">{{ Math.round(percent) }}%</span>
      </slot>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  value: number; max?: number; size?: number; strokeWidth?: number
  color?: 'primary' | 'success' | 'warning' | 'danger'; showValue?: boolean
}>(), { max: 100, size: 80, strokeWidth: 6, color: 'primary', showValue: true })

const percent = computed(() => Math.min(100, Math.max(0, (props.value / props.max) * 100)))
const center = computed(() => props.size / 2)
const radius = computed(() => (props.size - props.strokeWidth) / 2)
const circumference = computed(() => 2 * Math.PI * radius.value)
const offset = computed(() => circumference.value * (1 - percent.value / 100))
</script>
