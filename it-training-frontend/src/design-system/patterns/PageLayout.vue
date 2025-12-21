<script setup lang="ts">
/**
 * PageLayout - 页面布局容器
 *
 * 标准页面容器，限制最大宽度，居中显示
 * - 最大宽度：1280px（可配置）
 * - 内边距：响应式
 * - 支持全宽模式
 */

interface Props {
  /** 最大宽度 */
  maxWidth?: 'sm' | 'md' | 'lg' | 'xl' | '2xl' | 'full';
  /** 是否去除内边距 */
  noPadding?: boolean;
  /** 是否居中 */
  centered?: boolean;
  /** 自定义类名 */
  class?: string;
}

const props = withDefaults(defineProps<Props>(), {
  maxWidth: 'xl',
  noPadding: false,
  centered: true,
});

// 最大宽度映射
const maxWidthClasses: Record<string, string> = {
  sm: 'max-w-screen-sm',    // 640px
  md: 'max-w-screen-md',    // 768px
  lg: 'max-w-screen-lg',    // 1024px
  xl: 'max-w-screen-xl',    // 1280px
  '2xl': 'max-w-screen-2xl', // 1536px
  full: 'max-w-none',
};
</script>

<template>
  <div
    class="w-full min-h-screen"
    :class="[
      maxWidthClasses[maxWidth],
      {
        'mx-auto': centered,
        'px-4 md:px-6 lg:px-10': !noPadding,
        'py-6 md:py-8': !noPadding,
      },
      props.class,
    ]"
  >
    <slot />
  </div>
</template>
