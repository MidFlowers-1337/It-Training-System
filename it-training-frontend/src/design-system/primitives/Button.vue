<script setup lang="ts">
/**
 * Button - 原子按钮组件
 *
 * 基于 PrimeVue Unstyled，符合 Apple-like 设计规范
 * - 高度：44px（Touch-friendly）
 * - 圆角：12px
 * - 变体：primary / secondary / ghost / danger
 */

import { computed } from 'vue';

// Props 定义
interface Props {
  /** 按钮变体 */
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger';
  /** 按钮尺寸 */
  size?: 'sm' | 'md' | 'lg';
  /** 是否禁用 */
  disabled?: boolean;
  /** 是否加载中 */
  loading?: boolean;
  /** 是否块级（占满宽度） */
  block?: boolean;
  /** 按钮类型 */
  type?: 'button' | 'submit' | 'reset';
  /** 是否圆形按钮 */
  rounded?: boolean;
  /** 仅图标模式 */
  iconOnly?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  block: false,
  type: 'button',
  rounded: false,
  iconOnly: false,
});

// Emits 定义
const emit = defineEmits<{
  click: [event: MouseEvent];
}>();

// 点击处理
const handleClick = (event: MouseEvent) => {
  if (props.disabled || props.loading) return;
  emit('click', event);
};

// 计算样式类
const buttonClasses = computed(() => {
  const base = [
    'inline-flex items-center justify-center gap-2',
    'font-medium transition-all duration-fast',
    'focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-bg-primary',
    'disabled:opacity-50 disabled:cursor-not-allowed',
  ];

  // 尺寸
  const sizes = {
    sm: props.iconOnly
      ? 'w-9 h-9 text-sm'
      : 'h-9 px-4 text-sm',
    md: props.iconOnly
      ? 'w-11 h-11 text-base'
      : 'h-11 px-5 text-base',
    lg: props.iconOnly
      ? 'w-13 h-13 text-lg'
      : 'h-13 px-6 text-lg',
  };

  // 圆角
  const radius = props.rounded || props.iconOnly
    ? 'rounded-full'
    : 'rounded-xl';

  // 变体样式
  const variants = {
    primary: [
      'bg-primary text-white',
      'hover:bg-primary-light',
      'active:bg-primary-dark',
      'focus:ring-primary',
    ],
    secondary: [
      'bg-bg-tertiary text-text-primary',
      'hover:bg-bg-hover',
      'active:bg-bg-tertiary',
      'focus:ring-border-color',
    ],
    ghost: [
      'bg-transparent text-primary',
      'hover:bg-primary/10',
      'active:bg-primary/20',
      'focus:ring-primary',
    ],
    danger: [
      'bg-error text-white',
      'hover:bg-error/90',
      'active:bg-error/80',
      'focus:ring-error',
    ],
  };

  // 块级
  const blockClass = props.block ? 'w-full' : '';

  return [
    ...base,
    sizes[props.size],
    radius,
    ...variants[props.variant],
    blockClass,
  ].filter(Boolean).join(' ');
});
</script>

<template>
  <button
    :type="type"
    :class="buttonClasses"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <!-- 加载图标 -->
    <svg
      v-if="loading"
      class="animate-spin h-4 w-4"
      xmlns="http://www.w3.org/2000/svg"
      fill="none"
      viewBox="0 0 24 24"
    >
      <circle
        class="opacity-25"
        cx="12"
        cy="12"
        r="10"
        stroke="currentColor"
        stroke-width="4"
      />
      <path
        class="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      />
    </svg>

    <!-- 前置图标插槽 -->
    <slot v-if="!loading" name="icon-left" />

    <!-- 默认内容 -->
    <span v-if="!iconOnly && $slots.default" class="truncate">
      <slot />
    </span>

    <!-- 后置图标插槽 -->
    <slot v-if="!loading" name="icon-right" />

    <!-- 仅图标模式 -->
    <slot v-if="iconOnly && !loading" name="icon" />
  </button>
</template>
