<template>
  <div class="animate-pulse bg-surface-alt" :class="[!customSize && sizeClass]" :style="customStyle" />
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  variant?: 'text' | 'title' | 'avatar' | 'card' | 'button'
  width?: string; height?: string; borderRadius?: string
}>(), { variant: 'text' })

const customSize = computed(() => !!props.width || !!props.height)
const customStyle = computed(() => ({
  width: props.width || undefined,
  height: props.height || undefined,
  borderRadius: props.borderRadius || (props.variant === 'avatar' ? '50%' : '12px'),
}))
const sizeClass = computed(() => ({
  text: 'h-4 w-full rounded-lg',
  title: 'h-6 w-3/4 rounded-lg',
  avatar: 'h-10 w-10 rounded-full',
  card: 'h-32 w-full rounded-2xl',
  button: 'h-10 w-24 rounded-xl',
}[props.variant]))
</script>
