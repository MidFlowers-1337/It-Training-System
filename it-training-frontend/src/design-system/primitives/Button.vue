<script setup lang="ts">
/**
 * Button - 原子按钮组件（Apple 风格精致化版）
 *
 * 基于 PrimeVue Unstyled，符合 Apple-like 设计规范
 * - 高度：44px（Touch-friendly）
 * - 圆角：12px
 * - 变体：primary / secondary / ghost / danger
 * - 精致交互：hover 上浮、active 按压、微妙阴影
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
    'ds-button',
    'inline-flex items-center justify-center gap-2',
    'font-medium',
    'focus:outline-none',
    'disabled:opacity-50 disabled:cursor-not-allowed',
  ];

  // 尺寸
  const sizes = {
    sm: props.iconOnly
      ? 'w-9 h-9 text-sm ds-button--sm'
      : 'h-9 px-4 text-sm ds-button--sm',
    md: props.iconOnly
      ? 'w-11 h-11 text-base ds-button--md'
      : 'h-11 px-5 text-base ds-button--md',
    lg: props.iconOnly
      ? 'w-13 h-13 text-lg ds-button--lg'
      : 'h-13 px-6 text-lg ds-button--lg',
  };

  // 圆角
  const radius = props.rounded || props.iconOnly
    ? 'rounded-full'
    : 'rounded-xl';

  // 变体样式类
  const variants = {
    primary: 'ds-button--primary',
    secondary: 'ds-button--secondary',
    ghost: 'ds-button--ghost',
    danger: 'ds-button--danger',
  };

  // 块级
  const blockClass = props.block ? 'w-full' : '';

  // 加载状态
  const loadingClass = props.loading ? 'ds-button--loading' : '';

  return [
    ...base,
    sizes[props.size],
    radius,
    variants[props.variant],
    blockClass,
    loadingClass,
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
      class="ds-button-spinner"
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
    <span v-if="!iconOnly && $slots.default" class="ds-button-text truncate">
      <slot />
    </span>

    <!-- 后置图标插槽 -->
    <slot v-if="!loading" name="icon-right" />

    <!-- 仅图标模式 -->
    <slot v-if="iconOnly && !loading" name="icon" />
  </button>
</template>

<style scoped>
/* ========================================
   Apple-like Button - 精致化样式
   ======================================== */

.ds-button {
  position: relative;
  cursor: pointer;
  border: none;
  user-select: none;
  -webkit-tap-highlight-color: transparent;

  /* 微妙的内阴影提升质感 */
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);

  /* 流畅的过渡动画 - Apple 风格缓动 */
  transition:
    transform 0.15s cubic-bezier(0.25, 0.1, 0.25, 1.0),
    box-shadow 0.15s cubic-bezier(0.25, 0.1, 0.25, 1.0),
    background-color 0.15s cubic-bezier(0.25, 0.1, 0.25, 1.0),
    opacity 0.15s ease;
}

/* ===== Primary 变体 ===== */
.ds-button--primary {
  background: var(--primary-color);
  color: white;
}

.ds-button--primary:hover:not(:disabled) {
  background: var(--primary-light, #3395ff);
  transform: translateY(-1px);
  box-shadow:
    0 4px 12px rgba(0, 113, 227, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.ds-button--primary:active:not(:disabled) {
  background: var(--primary-dark, #005bb5);
  transform: translateY(0) scale(0.98);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.ds-button--primary:focus-visible {
  box-shadow:
    0 0 0 3px rgba(0, 113, 227, 0.4),
    0 1px 3px rgba(0, 0, 0, 0.1);
}

/* ===== Secondary 变体 ===== */
.ds-button--secondary {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.ds-button--secondary:hover:not(:disabled) {
  background: var(--bg-hover);
  border-color: var(--border-color);
  transform: translateY(-1px);
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.ds-button--secondary:active:not(:disabled) {
  background: var(--bg-tertiary);
  transform: translateY(0) scale(0.98);
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

.ds-button--secondary:focus-visible {
  box-shadow:
    0 0 0 3px rgba(0, 113, 227, 0.2),
    0 1px 2px rgba(0, 0, 0, 0.05);
}

/* ===== Ghost 变体 ===== */
.ds-button--ghost {
  background: transparent;
  color: var(--primary-color);
  box-shadow: none;
}

.ds-button--ghost:hover:not(:disabled) {
  background: rgba(0, 113, 227, 0.08);
  box-shadow: none;
  transform: none;
}

.ds-button--ghost:active:not(:disabled) {
  background: rgba(0, 113, 227, 0.15);
  transform: scale(0.98);
}

.ds-button--ghost:focus-visible {
  box-shadow: 0 0 0 3px rgba(0, 113, 227, 0.2);
}

/* ===== Danger 变体 ===== */
.ds-button--danger {
  background: var(--error-color);
  color: white;
}

.ds-button--danger:hover:not(:disabled) {
  background: #e53935;
  transform: translateY(-1px);
  box-shadow:
    0 4px 12px rgba(244, 67, 54, 0.35),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

.ds-button--danger:active:not(:disabled) {
  background: #c62828;
  transform: translateY(0) scale(0.98);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
}

.ds-button--danger:focus-visible {
  box-shadow:
    0 0 0 3px rgba(244, 67, 54, 0.4),
    0 1px 3px rgba(0, 0, 0, 0.1);
}

/* ===== 尺寸变体 ===== */
.ds-button--sm {
  border-radius: 8px;
}

.ds-button--md {
  border-radius: 10px;
}

.ds-button--lg {
  border-radius: 12px;
}

/* ===== 加载状态 ===== */
.ds-button--loading {
  pointer-events: none;
}

.ds-button--loading .ds-button-text {
  opacity: 0;
}

.ds-button-spinner {
  width: 16px;
  height: 16px;
  animation: ds-button-spin 0.6s linear infinite;
}

@keyframes ds-button-spin {
  to {
    transform: rotate(360deg);
  }
}

/* ===== 深色模式适配 ===== */
[data-theme="dark"] .ds-button {
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .ds-button--primary:hover:not(:disabled) {
  box-shadow:
    0 4px 12px rgba(0, 113, 227, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

[data-theme="dark"] .ds-button--secondary {
  background: var(--bg-secondary);
  border-color: var(--border-color);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.03);
}

[data-theme="dark"] .ds-button--secondary:hover:not(:disabled) {
  box-shadow:
    0 4px 8px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.03);
}

[data-theme="dark"] .ds-button--ghost:hover:not(:disabled) {
  background: rgba(0, 113, 227, 0.12);
}

[data-theme="dark"] .ds-button--ghost:active:not(:disabled) {
  background: rgba(0, 113, 227, 0.2);
}

/* ===== 暖阳橙主题适配 ===== */
[data-theme="warm"] .ds-button {
  box-shadow:
    0 1px 3px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.15);
}

[data-theme="warm"] .ds-button--secondary {
  background: var(--bg-tertiary);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.6);
}
</style>
