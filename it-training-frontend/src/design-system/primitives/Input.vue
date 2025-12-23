<script setup lang="ts">
/**
 * Input - 原子输入框组件（Apple 风格精致化版）
 *
 * 符合 Apple-like 设计规范
 * - 灰色背景，细边框
 * - 聚焦时显示蓝色光晕
 * - 内阴影增加深度感
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
  /** 是否成功状态 */
  success?: boolean;
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
  success: false,
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

// 计算容器样式类
const containerClasses = computed(() => {
  const classes = [
    'ds-input-container',
    `ds-input-container--${props.size}`,
  ];

  if (props.disabled) {
    classes.push('ds-input-container--disabled');
  } else if (props.error) {
    classes.push('ds-input-container--error');
  } else if (props.success) {
    classes.push('ds-input-container--success');
  } else if (isFocused.value) {
    classes.push('ds-input-container--focused');
  }

  return classes.join(' ');
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
  <div class="ds-input-wrapper">
    <div :class="containerClasses">
      <!-- 前置图标插槽 -->
      <div v-if="$slots['icon-left']" class="ds-input-prefix">
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
        class="ds-input"
        @input="handleInput"
        @focus="handleFocus"
        @blur="handleBlur"
      />

      <!-- 清除按钮 -->
      <button
        v-if="showClearButton"
        type="button"
        class="ds-input-clear"
        @click="handleClear"
      >
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <path d="M15 9l-6 6M9 9l6 6" />
        </svg>
      </button>

      <!-- 后置图标插槽 -->
      <div v-if="$slots['icon-right']" class="ds-input-suffix">
        <slot name="icon-right" />
      </div>
    </div>

    <!-- 错误信息 -->
    <p v-if="error && errorMessage" class="ds-input-error-message">
      {{ errorMessage }}
    </p>
  </div>
</template>

<style scoped>
/* ========================================
   Apple-like Input - 精致化样式
   ======================================== */

.ds-input-wrapper {
  width: 100%;
}

/* ===== 容器样式 ===== */
.ds-input-container {
  position: relative;
  display: flex;
  align-items: center;
  background: var(--color-bg-tertiary);
  border: 1px solid var(--color-border);
  border-radius: 10px;

  /* 内阴影增加深度感 */
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.05);

  /* 流畅过渡 */
  transition:
    border-color 0.2s cubic-bezier(0.25, 0.1, 0.25, 1.0),
    box-shadow 0.2s cubic-bezier(0.25, 0.1, 0.25, 1.0),
    background-color 0.2s ease;
}

/* Hover 状态 */
.ds-input-container:hover:not(.ds-input-container--disabled):not(.ds-input-container--focused) {
  border-color: var(--color-border-hover, #c0c4cc);
}

/* Focus 状态：蓝色光晕 */
.ds-input-container--focused {
  border-color: var(--color-primary);
  box-shadow:
    0 0 0 3px rgba(0, 113, 227, 0.15),
    inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* Error 状态 */
.ds-input-container--error {
  border-color: var(--color-error);
}

.ds-input-container--error.ds-input-container--focused {
  box-shadow:
    0 0 0 3px rgba(255, 59, 48, 0.15),
    inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* Success 状态 */
.ds-input-container--success {
  border-color: var(--color-success);
}

.ds-input-container--success.ds-input-container--focused {
  box-shadow:
    0 0 0 3px rgba(48, 209, 88, 0.15),
    inset 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* Disabled 状态 */
.ds-input-container--disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: var(--color-bg-secondary);
}

/* ===== 尺寸变体 ===== */
.ds-input-container--sm {
  height: 36px;
  border-radius: 8px;
}

.ds-input-container--md {
  height: 44px;
  border-radius: 10px;
}

.ds-input-container--lg {
  height: 52px;
  border-radius: 12px;
}

/* ===== 输入框样式 ===== */
.ds-input {
  flex: 1;
  height: 100%;
  padding: 0 16px;
  background: transparent;
  border: none;
  outline: none;
  font-size: 17px;
  color: var(--color-text-primary);
  -webkit-appearance: none;
}

.ds-input-container--sm .ds-input {
  padding: 0 12px;
  font-size: 14px;
}

.ds-input-container--lg .ds-input {
  padding: 0 20px;
  font-size: 17px;
}

.ds-input::placeholder {
  color: var(--color-text-muted);
  opacity: 0.7;
}

.ds-input:disabled {
  cursor: not-allowed;
}

/* ===== 图标样式 ===== */
.ds-input-prefix {
  padding-left: 14px;
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
}

.ds-input-prefix + .ds-input {
  padding-left: 8px;
}

.ds-input-suffix {
  padding-right: 14px;
  color: var(--color-text-muted);
  display: flex;
  align-items: center;
}

/* ===== 清除按钮 ===== */
.ds-input-clear {
  padding: 0 12px;
  color: var(--color-text-muted);
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: color 0.15s ease;
}

.ds-input-clear:hover {
  color: var(--color-text-secondary);
}

/* ===== 错误信息 ===== */
.ds-input-error-message {
  margin-top: 6px;
  font-size: 13px;
  color: var(--color-error);
}

/* ===== 深色模式适配 ===== */
[data-theme="dark"] .ds-input-container {
  background: var(--color-bg-secondary);
  border-color: var(--color-border);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .ds-input-container:hover:not(.ds-input-container--disabled):not(.ds-input-container--focused) {
  border-color: var(--color-border-hover, #4a4a4a);
}

[data-theme="dark"] .ds-input-container--focused {
  border-color: var(--color-primary);
  box-shadow:
    0 0 0 3px rgba(0, 113, 227, 0.25),
    inset 0 1px 2px rgba(0, 0, 0, 0.2);
}

[data-theme="dark"] .ds-input-container--error.ds-input-container--focused {
  box-shadow:
    0 0 0 3px rgba(255, 59, 48, 0.25),
    inset 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* ===== 暖阳橙主题适配 ===== */
[data-theme="warm"] .ds-input-container {
  background: var(--color-bg-tertiary);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.03);
}

[data-theme="warm"] .ds-input-container--focused {
  box-shadow:
    0 0 0 3px rgba(0, 113, 227, 0.12),
    inset 0 1px 2px rgba(0, 0, 0, 0.03);
}
</style>
