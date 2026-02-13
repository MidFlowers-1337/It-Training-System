<template>
  <button :class="classes" :disabled="disabled || loading" v-bind="$attrs">
    <span v-if="loading" class="inline-block w-4 h-4 border-2 border-current border-t-transparent rounded-full animate-spin mr-2" />
    <slot />
  </button>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger' | 'success'
  size?: 'sm' | 'md' | 'lg'
  block?: boolean
  rounded?: boolean
  loading?: boolean
  disabled?: boolean
}>(), { variant: 'primary', size: 'md' })

const classes = computed(() => [
  // Base — Stripe-quality interactive feel
  'inline-flex items-center justify-center font-semibold transition-all duration-150 ease-in-out',
  'focus:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-primary/40',
  'cursor-pointer select-none',
  props.block && 'w-full',
  props.rounded ? 'rounded-full' : sizeRadius.value,
  props.disabled && '!opacity-50 !cursor-not-allowed !shadow-none',
  sizeClass.value,
  variantClass.value,
])

const sizeRadius = computed(() => ({
  sm: 'rounded-lg',
  md: 'rounded-[10px]',
  lg: 'rounded-xl',
}[props.size]))

const sizeClass = computed(() => ({
  sm: 'px-3 py-1.5 text-[13px] gap-1.5',
  md: 'px-5 py-2.5 text-sm gap-2',
  lg: 'px-7 py-3 text-[15px] gap-2.5',
}[props.size]))

const variantClass = computed(() => ({
  primary: [
    'bg-primary text-white',
    'shadow-[0_1px_2px_rgba(0,0,0,0.05),0_0_0_1px_rgba(0,0,0,0.08),inset_0_1px_0_rgba(255,255,255,0.12)]',
    'hover:brightness-110 hover:shadow-[0_2px_6px_rgba(0,0,0,0.12),0_0_0_1px_rgba(0,0,0,0.1),inset_0_1px_0_rgba(255,255,255,0.12)]',
    'active:brightness-95 active:shadow-[0_0_0_1px_rgba(0,0,0,0.12),inset_0_2px_4px_rgba(0,0,0,0.1)]',
  ].join(' '),
  secondary: [
    'bg-white text-text-primary',
    'shadow-[0_1px_2px_rgba(0,0,0,0.05),0_0_0_1px_rgba(0,0,0,0.08)]',
    'hover:bg-surface-alt hover:shadow-[0_2px_4px_rgba(0,0,0,0.08),0_0_0_1px_rgba(0,0,0,0.1)]',
    'active:bg-surface active:shadow-[0_0_0_1px_rgba(0,0,0,0.12),inset_0_1px_3px_rgba(0,0,0,0.06)]',
  ].join(' '),
  ghost: [
    'text-text-secondary bg-transparent',
    'hover:bg-surface-alt hover:text-text-primary',
    'active:bg-border/30',
  ].join(' '),
  danger: [
    'bg-danger text-white',
    'shadow-[0_1px_2px_rgba(0,0,0,0.05),0_0_0_1px_rgba(0,0,0,0.08),inset_0_1px_0_rgba(255,255,255,0.12)]',
    'hover:brightness-110 hover:shadow-[0_2px_6px_rgba(0,0,0,0.12),0_0_0_1px_rgba(0,0,0,0.1)]',
    'active:brightness-95 active:shadow-[inset_0_2px_4px_rgba(0,0,0,0.1)]',
  ].join(' '),
  success: [
    'bg-success text-white',
    'shadow-[0_1px_2px_rgba(0,0,0,0.05),0_0_0_1px_rgba(0,0,0,0.08),inset_0_1px_0_rgba(255,255,255,0.12)]',
    'hover:brightness-110 hover:shadow-[0_2px_6px_rgba(0,0,0,0.12),0_0_0_1px_rgba(0,0,0,0.1)]',
    'active:brightness-95 active:shadow-[inset_0_2px_4px_rgba(0,0,0,0.1)]',
  ].join(' '),
}[props.variant]))
</script>
