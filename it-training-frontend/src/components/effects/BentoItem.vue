<template>
  <div
    :class="[
      'bento-item transition-all duration-200',
      glass ? 'bento-glass' : 'bento-solid',
      hover ? 'hover:shadow-theme-lg hover:-translate-y-0.5 cursor-pointer' : '',
      spanClass,
    ]"
    :style="customStyle"
  >
    <div :class="padding || 'p-6'">
      <slot />
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{
  /** Column span: 1 | 2 (default 1) */
  colSpan?: 1 | 2
  /** Row span: 1 | 2 (default 1) */
  rowSpan?: 1 | 2
  hover?: boolean
  padding?: string
  /** Minimum height in px (for visual balance in Bento layout) */
  minHeight?: number
  /** Enable glassmorphism mode (default false — solid surface) */
  glass?: boolean
}>()
const spanClass = computed(() => {
  const col = props.colSpan === 2 ? 'sm:col-span-2' : ''
  const row = props.rowSpan === 2 ? 'sm:row-span-2' : ''
  return `${col} ${row}`.trim()
})
const customStyle = computed(() => {
  const s: Record<string, string> = {}
  if (props.minHeight) s.minHeight = `${props.minHeight}px`
  return s
})
</script>
<style scoped>
/* Solid mode — 默认：实色 surface + 多层 shadow（Stripe 风格） */
.bento-solid {
  background: rgb(var(--color-surface));
  border: 1px solid rgb(var(--color-border));
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
}

/* Glass mode — 可选：毛玻璃效果（仅 Navbar/Modal/Hero 场景） */
.bento-glass {
  background: var(--glass-bg);
  backdrop-filter: blur(var(--glass-blur));
  -webkit-backdrop-filter: blur(var(--glass-blur));
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
  box-shadow: var(--glass-shadow);
}

/* Dark mode glow — 自动为 solid 卡片添加 Linear 风格微光 */
[data-theme='dark'] .bento-solid {
  border-color: rgba(255, 255, 255, 0.06);
  box-shadow: var(--shadow);
}

[data-theme='dark'] .bento-solid:hover {
  border-color: rgba(255, 255, 255, 0.10);
  box-shadow: var(--shadow-lg);
}
</style>
