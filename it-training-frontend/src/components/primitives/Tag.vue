<template>
  <span :class="['inline-flex items-center gap-1 rounded-lg font-medium', sizeClass, variantClass, closable && 'pr-1']">
    <slot />
    <button v-if="closable" class="ml-0.5 hover:opacity-70 transition-opacity" @click="$emit('close')">
      <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
    </button>
  </span>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  variant?: 'primary' | 'success' | 'warning' | 'danger' | 'neutral'
  size?: 'sm' | 'md'; closable?: boolean
}>(), { variant: 'neutral', size: 'sm' })
defineEmits<{ close: [] }>()
const sizeClass = computed(() => ({ sm: 'px-2 py-0.5 text-xs', md: 'px-3 py-1 text-sm' }[props.size]))
const variantClass = computed(() => ({
  primary: 'bg-primary/10 text-primary', success: 'bg-success/10 text-success',
  warning: 'bg-warning/10 text-warning', danger: 'bg-danger/10 text-danger',
  neutral: 'bg-surface-alt text-text-secondary border border-border',
}[props.variant]))
</script>
