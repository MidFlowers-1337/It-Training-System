<template>
  <div class="w-full">
    <div v-if="showLabel" class="flex justify-between items-center mb-1.5">
      <span class="text-sm text-text-secondary">{{ label }}</span>
      <span class="text-sm font-medium text-text-primary">{{ displayValue }}</span>
    </div>
    <div class="w-full rounded-full overflow-hidden" :class="heightClass" :style="{ backgroundColor: 'rgb(var(--color-surface-alt))' }">
      <div class="h-full rounded-full transition-all duration-700 ease-out" :class="colorClass" :style="{ width: clampedPercent + '%' }" />
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  value: number; max?: number; label?: string; showLabel?: boolean
  size?: 'sm' | 'md' | 'lg'; color?: 'primary' | 'success' | 'warning' | 'danger'
}>(), { max: 100, showLabel: false, size: 'md', color: 'primary' })

const clampedPercent = computed(() => Math.min(100, Math.max(0, (props.value / props.max) * 100)))
const displayValue = computed(() => `${Math.round(clampedPercent.value)}%`)
const heightClass = computed(() => ({ sm: 'h-1.5', md: 'h-2.5', lg: 'h-4' }[props.size]))
const colorClass = computed(() => ({
  primary: 'bg-primary', success: 'bg-success', warning: 'bg-warning', danger: 'bg-danger',
}[props.color]))
</script>
