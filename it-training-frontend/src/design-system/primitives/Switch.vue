<script setup lang="ts">
/**
 * Switch - 开关组件
 *
 * iOS 风格的开关切换组件
 * - 支持 v-model 双向绑定
 * - 支持禁用状态
 */

import { computed } from 'vue';

// Props 定义
interface Props {
  /** 开关状态 */
  modelValue?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 尺寸 */
  size?: 'sm' | 'md' | 'lg';
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  disabled: false,
  size: 'md',
});

// Emits 定义
const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  change: [value: boolean];
}>();

// 切换处理
const toggle = () => {
  if (props.disabled) return;
  const newValue = !props.modelValue;
  emit('update:modelValue', newValue);
  emit('change', newValue);
};

// 尺寸样式
const sizeClasses = computed(() => {
  const sizes = {
    sm: {
      track: 'w-9 h-5',
      thumb: 'w-4 h-4',
      translate: 'translate-x-4',
    },
    md: {
      track: 'w-11 h-6',
      thumb: 'w-5 h-5',
      translate: 'translate-x-5',
    },
    lg: {
      track: 'w-14 h-8',
      thumb: 'w-7 h-7',
      translate: 'translate-x-6',
    },
  };
  return sizes[props.size];
});
</script>

<template>
  <button
    type="button"
    role="switch"
    :aria-checked="modelValue"
    :disabled="disabled"
    class="relative inline-flex flex-shrink-0 rounded-full transition-colors duration-fast focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 focus:ring-offset-bg-primary"
    :class="[
      sizeClasses.track,
      modelValue ? 'bg-primary' : 'bg-bg-tertiary',
      disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer',
    ]"
    @click="toggle"
  >
    <span
      class="pointer-events-none inline-block rounded-full bg-white shadow-sm ring-0 transition-transform duration-fast"
      :class="[
        sizeClasses.thumb,
        modelValue ? sizeClasses.translate : 'translate-x-0.5',
      ]"
      style="margin-top: 2px;"
    />
  </button>
</template>
