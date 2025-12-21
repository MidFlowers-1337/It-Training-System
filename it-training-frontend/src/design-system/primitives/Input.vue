<script setup lang="ts">
/**
 * Input - 原子输入框组件
 *
 * 符合 Apple-like 设计规范
 * - 灰色背景，无边框
 * - 聚焦时显示蓝色光环
 * - 字号 17px（防止 iOS 缩放）
 * - 支持前置/后置图标
 */

import { computed, ref } from 'vue';

// Props 定义
interface Props {
  /** 输入值 */
  modelValue?: string | number;
  /** 输入类型 */
  type?: 'text' | 'password' | 'email' | 'number' | 'tel' | 'url' | 'search';
  /** 占位符 */
  placeholder?: string;
  /** 是否禁用 */
  disabled?: boolean;
  /** 是否只读 */
  readonly?: boolean;
  /** 是否必填 */
  required?: boolean;
  /** 是否错误状态 */
  error?: boolean;
  /** 错误信息 */
  errorMessage?: string;
  /** 最大长度 */
  maxlength?: number;
  /** 自动聚焦 */
  autofocus?: boolean;
  /** 自动完成 */
  autocomplete?: string;
  /** 尺寸 */
  size?: 'sm' | 'md' | 'lg';
  /** 是否可清除 */
  clearable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  type: 'text',
  placeholder: '',
  disabled: false,
  readonly: false,
  required: false,
  error: false,
  errorMessage: '',
  autofocus: false,
  autocomplete: 'off',
  size: 'md',
  clearable: false,
});

// Emits 定义
const emit = defineEmits<{
  'update:modelValue': [value: string | number];
  focus: [event: FocusEvent];
  blur: [event: FocusEvent];
  input: [event: Event];
  clear: [];
}>();

// 内部状态
const isFocused = ref(false);
const inputRef = ref<HTMLInputElement | null>(null);

// 输入处理
const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement;
  emit('update:modelValue', target.value);
  emit('input', event);
};

// 聚焦处理
const handleFocus = (event: FocusEvent) => {
  isFocused.value = true;
  emit('focus', event);
};

// 失焦处理
const handleBlur = (event: FocusEvent) => {
  isFocused.value = false;
  emit('blur', event);
};

// 清除内容
const handleClear = () => {
  emit('update:modelValue', '');
  emit('clear');
  inputRef.value?.focus();
};

// 计算容器样式
const containerClasses = computed(() => {
  const base = [
    'relative flex items-center',
    'bg-bg-tertiary rounded-xl',
    'transition-all duration-fast',
  ];

  // 尺寸
  const sizes = {
    sm: 'h-9',
    md: 'h-11',
    lg: 'h-13',
  };

  // 状态样式
  const states = [];
  if (props.disabled) {
    states.push('opacity-50 cursor-not-allowed');
  } else if (props.error) {
    states.push('ring-2 ring-error');
  } else if (isFocused.value) {
    states.push('ring-2 ring-primary');
  }

  return [...base, sizes[props.size], ...states].join(' ');
});

// 计算输入框样式
const inputClasses = computed(() => {
  const base = [
    'w-full h-full px-4',
    'bg-transparent border-none outline-none',
    'text-[17px] text-text-primary',
    'placeholder:text-text-muted',
  ];

  if (props.disabled) {
    base.push('cursor-not-allowed');
  }

  return base.join(' ');
});

// 是否显示清除按钮
const showClearButton = computed(() => {
  return props.clearable && props.modelValue && !props.disabled && !props.readonly;
});

// 暴露方法
defineExpose({
  focus: () => inputRef.value?.focus(),
  blur: () => inputRef.value?.blur(),
});
</script>

<template>
  <div class="w-full">
    <div :class="containerClasses">
      <!-- 前置图标插槽 -->
      <div v-if="$slots['icon-left']" class="pl-4 text-text-muted">
        <slot name="icon-left" />
      </div>

      <!-- 输入框 -->
      <input
        ref="inputRef"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :required="required"
        :maxlength="maxlength"
        :autofocus="autofocus"
        :autocomplete="autocomplete"
        :class="inputClasses"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
      />

      <!-- 清除按钮 -->
      <button
        v-if="showClearButton"
        type="button"
        class="pr-3 text-text-muted hover:text-text-secondary transition-colors"
        @click="handleClear"
      >
        <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <path d="M15 9l-6 6M9 9l6 6" />
        </svg>
      </button>

      <!-- 后置图标插槽 -->
      <div v-if="$slots['icon-right']" class="pr-4 text-text-muted">
        <slot name="icon-right" />
      </div>
    </div>

    <!-- 错误信息 -->
    <p v-if="error && errorMessage" class="mt-1.5 text-sm text-error">
      {{ errorMessage }}
    </p>
  </div>
</template>
