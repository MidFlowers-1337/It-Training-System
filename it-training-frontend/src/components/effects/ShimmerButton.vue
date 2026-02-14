<template>
  <button
    :class="[
      'shimmer-btn relative overflow-hidden font-semibold text-sm transition-all duration-200',
      sizeClass,
      variant === 'primary'
        ? 'btn-primary-fill text-white'
        : 'bg-surface text-text-primary border border-border hover:bg-surface-alt',
    ]"
  >
    <span class="relative z-10"><slot /></span>
    <!-- Ultra-subtle shimmer overlay (opacity 0.05) -->
    <div v-if="variant === 'primary'" class="sh absolute inset-0" />
  </button>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{
  variant?: 'primary' | 'secondary'
  size?: 'sm' | 'md' | 'lg'
}>()
const sizeClass = computed(() => {
  switch (props.size) {
    case 'sm': return 'px-4 py-2 text-xs rounded-lg'
    case 'lg': return 'px-10 py-4 text-base rounded-xl'
    default: return 'px-8 py-3 rounded-lg'
  }
})
</script>
<style scoped>
/* Stripe-style primary button: solid color + brightness hover + shadow lift */
.btn-primary-fill {
  background: rgb(var(--color-primary));
  box-shadow: var(--shadow-sm);
}

.btn-primary-fill:hover {
  filter: brightness(1.1);
  box-shadow: var(--shadow);
}

.btn-primary-fill:active {
  filter: brightness(0.95);
  box-shadow: var(--shadow-sm);
}

/* Ultra-subtle shimmer — barely visible (Stripe 克制风格) */
.sh {
  background: linear-gradient(
    90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.05) 50%,
    transparent 100%
  );
  background-size: 200% 100%;
  animation: shimmer 4s infinite;
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

/* Dark mode: add subtle glow on hover */
[data-theme='dark'] .btn-primary-fill:hover {
  box-shadow: var(--glow-primary);
}
</style>
