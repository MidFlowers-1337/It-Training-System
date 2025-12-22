<script setup lang="ts">
/**
 * TimelineItem - 时间线项组件
 */
defineOptions({ name: 'DsTimelineItem' });

interface Props {
  type?: 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info';
  timestamp?: string;
  hollow?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'default',
  hollow: false,
});

const dotStyles: Record<string, string> = {
  default: 'bg-fill-tertiary border-border-primary',
  primary: props.hollow ? 'bg-transparent border-primary' : 'bg-primary border-primary',
  success: props.hollow ? 'bg-transparent border-success' : 'bg-success border-success',
  warning: props.hollow ? 'bg-transparent border-warning' : 'bg-warning border-warning',
  danger: props.hollow ? 'bg-transparent border-error' : 'bg-error border-error',
  info: props.hollow ? 'bg-transparent border-info' : 'bg-info border-info',
};

const computedDotStyle = computed(() => {
  if (props.hollow) {
    const hollowStyles: Record<string, string> = {
      default: 'bg-transparent border-border-primary',
      primary: 'bg-transparent border-primary',
      success: 'bg-transparent border-success',
      warning: 'bg-transparent border-warning',
      danger: 'bg-transparent border-error',
      info: 'bg-transparent border-info',
    };
    return hollowStyles[props.type];
  }
  return dotStyles[props.type];
});
</script>

<template>
  <div class="relative pb-6 last:pb-0">
    <!-- Line -->
    <div class="absolute left-[-18px] top-3 bottom-0 w-px bg-border-primary last:hidden"></div>
    <!-- Dot -->
    <div
      class="absolute left-[-22px] top-1.5 w-2.5 h-2.5 rounded-full border-2"
      :class="computedDotStyle"
    ></div>
    <!-- Content -->
    <div>
      <div v-if="timestamp" class="text-xs text-text-tertiary mb-1">{{ timestamp }}</div>
      <slot />
    </div>
  </div>
</template>
