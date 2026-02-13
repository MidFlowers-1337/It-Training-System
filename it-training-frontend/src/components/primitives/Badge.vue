<template>
  <span :class="['inline-flex items-center rounded-full font-medium', sizeClass, variantClass]"><slot /></span>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  variant?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'neutral'
  size?: 'sm' | 'md'
}>(), { variant: 'primary', size: 'sm' })
const sizeClass = computed(() => ({ sm: 'px-2 py-0.5 text-xs', md: 'px-2.5 py-1 text-sm' }[props.size]))
const variantClass = computed(() => ({
  primary: 'bg-primary/10 text-primary', success: 'bg-success/10 text-success',
  warning: 'bg-warning/10 text-warning', danger: 'bg-danger/10 text-danger',
  info: 'bg-info/10 text-info', neutral: 'bg-surface-alt text-text-secondary',
}[props.variant]))
</script>
