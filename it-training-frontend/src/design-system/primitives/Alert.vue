<script setup lang="ts">
/**
 * Alert - 警告提示组件
 * 支持 info/success/warning/error 类型
 */
defineOptions({ name: 'DsAlert' });

interface Props {
  type?: 'info' | 'success' | 'warning' | 'error';
  title?: string;
  closable?: boolean;
  showIcon?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'info',
  closable: false,
  showIcon: true,
});

const emit = defineEmits<{
  close: [];
}>();

const visible = ref(true);

const typeStyles: Record<string, string> = {
  info: 'bg-info/10 border-info/30 text-info',
  success: 'bg-success/10 border-success/30 text-success',
  warning: 'bg-warning/10 border-warning/30 text-warning',
  error: 'bg-error/10 border-error/30 text-error',
};

const handleClose = () => {
  visible.value = false;
  emit('close');
};
</script>

<template>
  <div
    v-if="visible"
    class="flex items-start gap-3 p-4 rounded-xl border"
    :class="typeStyles[type]"
  >
    <!-- Icon -->
    <div v-if="showIcon" class="flex-shrink-0 mt-0.5">
      <!-- Info -->
      <svg v-if="type === 'info'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10" />
        <path d="M12 16v-4M12 8h.01" />
      </svg>
      <!-- Success -->
      <svg v-else-if="type === 'success'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
        <polyline points="22 4 12 14.01 9 11.01" />
      </svg>
      <!-- Warning -->
      <svg v-else-if="type === 'warning'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
        <line x1="12" y1="9" x2="12" y2="13" />
        <line x1="12" y1="17" x2="12.01" y2="17" />
      </svg>
      <!-- Error -->
      <svg v-else class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="12" cy="12" r="10" />
        <line x1="15" y1="9" x2="9" y2="15" />
        <line x1="9" y1="9" x2="15" y2="15" />
      </svg>
    </div>

    <!-- Content -->
    <div class="flex-1 min-w-0">
      <div v-if="title" class="font-medium text-sm">{{ title }}</div>
      <div class="text-sm opacity-90" :class="{ 'mt-1': title }">
        <slot />
      </div>
    </div>

    <!-- Close -->
    <button
      v-if="closable"
      type="button"
      class="flex-shrink-0 p-1 rounded-lg opacity-60 hover:opacity-100 transition-opacity"
      @click="handleClose"
    >
      <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="18" y1="6" x2="6" y2="18" />
        <line x1="6" y1="6" x2="18" y2="18" />
      </svg>
    </button>
  </div>
</template>
