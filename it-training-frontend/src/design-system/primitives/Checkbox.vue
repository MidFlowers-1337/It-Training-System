<script setup lang="ts">
/**
 * Checkbox - 复选框组件
 *
 * 符合 Apple-like 设计规范
 */

import { computed } from 'vue';

// Props 定义
interface Props {
  /** 选中状态 */
  modelValue?: boolean;
  /** 标签文字 */
  label?: string;
  /** 是否禁用 */
  disabled?: boolean;
  /** 不确定状态 */
  indeterminate?: boolean;
  /** 尺寸 */
  size?: 'sm' | 'md' | 'lg';
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  label: '',
  disabled: false,
  indeterminate: false,
  size: 'md',
});

// Emits 定义
const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  change: [value: boolean];
}>();

// 切换状态
const toggle = () => {
  if (props.disabled) return;
  const newValue = !props.modelValue;
  emit('update:modelValue', newValue);
  emit('change', newValue);
};

// 复选框尺寸
const boxSize = computed(() => {
  const sizes = {
    sm: 'w-4 h-4',
    md: 'w-5 h-5',
    lg: 'w-6 h-6',
  };
  return sizes[props.size];
});

// 图标尺寸
const iconSize = computed(() => {
  const sizes = {
    sm: 'w-3 h-3',
    md: 'w-3.5 h-3.5',
    lg: 'w-4 h-4',
  };
  return sizes[props.size];
});

// 复选框样式
const boxClasses = computed(() => {
  const base = [
    'flex items-center justify-center',
    'rounded-md border-2',
    'transition-all duration-fast',
  ];

  if (props.disabled) {
    base.push('opacity-50 cursor-not-allowed');
  } else {
    base.push('cursor-pointer');
  }

  if (props.modelValue || props.indeterminate) {
    base.push('bg-primary border-primary');
  } else {
    base.push('bg-bg-tertiary border-border-color');
    if (!props.disabled) {
      base.push('hover:border-primary');
    }
  }

  return [boxSize.value, ...base].join(' ');
});
</script>

<template>
  <label
    class="inline-flex items-center gap-2 select-none"
    :class="{ 'cursor-pointer': !disabled, 'cursor-not-allowed': disabled }"
    @click.prevent="toggle"
  >
    <!-- 复选框 -->
    <span :class="boxClasses">
      <!-- 选中图标 -->
      <svg
        v-if="modelValue && !indeterminate"
        :class="iconSize"
        viewBox="0 0 24 24"
        fill="none"
        stroke="white"
        stroke-width="3"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M5 12l5 5L19 7" />
      </svg>

      <!-- 不确定状态图标 -->
      <svg
        v-else-if="indeterminate"
        :class="iconSize"
        viewBox="0 0 24 24"
        fill="none"
        stroke="white"
        stroke-width="3"
        stroke-linecap="round"
      >
        <path d="M5 12h14" />
      </svg>
    </span>

    <!-- 标签 -->
    <span
      v-if="label || $slots.default"
      class="text-text-primary"
      :class="{
        'text-sm': size === 'sm',
        'text-base': size === 'md',
        'text-lg': size === 'lg',
        'opacity-50': disabled,
      }"
    >
      <slot>{{ label }}</slot>
    </span>
  </label>
</template>
