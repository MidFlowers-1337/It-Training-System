<script setup lang="ts">
/**
 * Tag - 标签组件
 * 支持多种类型和尺寸
 */
defineOptions({ name: 'DsTag' });

interface Props {
  type?: 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info';
  size?: 'sm' | 'md';
  closable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  size: 'md',
});

const emit = defineEmits<{
  close: [];
}>();

const typeStyles: Record<string, string> = {
  default: 'bg-fill-tertiary text-text-secondary',
  primary: 'bg-primary/10 text-primary',
  success: 'bg-success/10 text-success',
  warning: 'bg-warning/10 text-warning',
  danger: 'bg-error/10 text-error',
  info: 'bg-info/10 text-info',
};

const sizeStyles: Record<string, string> = {
  sm: 'px-1.5 py-0.5 text-xs',
  md: 'px-2 py-1 text-xs',
};
</script>

<template>
  <span
    class="inline-flex items-center gap-1 rounded-md font-medium"
    :class="[typeStyles[type], sizeStyles[size]]"
  >
    <slot />
    <button
      v-if="closable"
      type="button"
      class="ml-0.5 opacity-60 hover:opacity-100"
      @click.stop="emit('close')"
    >
      <svg class="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="18" y1="6" x2="6" y2="18" />
        <line x1="6" y1="6" x2="18" y2="18" />
      </svg>
    </button>
  </span>
</template>
