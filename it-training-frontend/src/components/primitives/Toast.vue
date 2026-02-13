<template>
  <div
    :class="['toast-item', `toast-item--${type}`, !visible && 'toast-item--leaving']"
    role="alert"
    aria-live="assertive"
  >
    <component :is="iconComponent" class="w-[18px] h-[18px] flex-shrink-0" :stroke-width="1.75" />
    <p class="flex-1 min-w-0 text-sm leading-snug">{{ message }}</p>
    <button
      class="flex-shrink-0 p-0.5 rounded-md opacity-60 hover:opacity-100 transition-opacity"
      aria-label="关闭通知"
      @click="$emit('close')"
    >
      <X class="w-3.5 h-3.5" :stroke-width="2" />
    </button>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ToastType } from '@/composables/useToast'
import { CheckCircle, XCircle, AlertTriangle, Info, X } from 'lucide-vue-next'

const props = defineProps<{
  type: ToastType
  message: string
  visible: boolean
}>()

defineEmits<{ close: [] }>()

const iconComponent = computed(() => ({
  success: CheckCircle,
  error: XCircle,
  warning: AlertTriangle,
  info: Info,
}[props.type]))
</script>

<style scoped>
.toast-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 12px;
  border: 1px solid;
  width: 360px;
  max-width: calc(100vw - 32px);
  backdrop-filter: blur(12px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08), 0 1px 4px rgba(0, 0, 0, 0.04);
  animation: toast-enter 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  pointer-events: auto;
}

.toast-item--leaving {
  animation: toast-leave 0.25s ease-in forwards;
}

/* ── Variants (use semantic CSS custom properties) ── */
.toast-item--success {
  background: rgb(var(--color-success) / 0.08);
  border-color: rgb(var(--color-success) / 0.15);
  color: rgb(var(--color-success));
}

.toast-item--error {
  background: rgb(var(--color-danger) / 0.08);
  border-color: rgb(var(--color-danger) / 0.15);
  color: rgb(var(--color-danger));
}

.toast-item--warning {
  background: rgb(var(--color-warning) / 0.08);
  border-color: rgb(var(--color-warning) / 0.15);
  color: rgb(var(--color-warning));
}

.toast-item--info {
  background: rgb(var(--color-info) / 0.08);
  border-color: rgb(var(--color-info) / 0.15);
  color: rgb(var(--color-info));
}

/* ── Animations ── */
@keyframes toast-enter {
  0% {
    opacity: 0;
    transform: translateX(20px) scale(0.96);
  }
  100% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

@keyframes toast-leave {
  0% {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
  100% {
    opacity: 0;
    transform: translateX(20px) scale(0.96);
  }
}

/* ── Reduced motion ── */
@media (prefers-reduced-motion: reduce) {
  .toast-item {
    animation: none;
  }
  .toast-item--leaving {
    animation: none;
    opacity: 0;
  }
}
</style>
