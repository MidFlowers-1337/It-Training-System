<script setup lang="ts">
/**
 * Select - 下拉选择组件
 *
 * 符合 Apple-like 设计规范
 * - 灰色背景
 * - 下拉面板带阴影
 */

import { ref, computed, watch, onMounted, onUnmounted } from 'vue';

// 选项类型
interface Option {
  label: string;
  value: string | number;
  disabled?: boolean;
}

// Props 定义
interface Props {
  /** 选中值 */
  modelValue?: string | number | null;
  /** 选项列表 */
  options: Option[];
  /** 占位符 */
  placeholder?: string;
  /** 是否禁用 */
  disabled?: boolean;
  /** 是否错误状态 */
  error?: boolean;
  /** 错误信息 */
  errorMessage?: string;
  /** 是否可清除 */
  clearable?: boolean;
  /** 尺寸 */
  size?: 'sm' | 'md' | 'lg';
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: null,
  placeholder: '请选择',
  disabled: false,
  error: false,
  errorMessage: '',
  clearable: false,
  size: 'md',
});

// Emits 定义
const emit = defineEmits<{
  'update:modelValue': [value: string | number | null];
  change: [value: string | number | null];
}>();

// 内部状态
const isOpen = ref(false);
const selectRef = ref<HTMLElement | null>(null);

// 当前选中的选项
const selectedOption = computed(() => {
  return props.options.find((opt) => opt.value === props.modelValue);
});

// 显示文本
const displayText = computed(() => {
  return selectedOption.value?.label || props.placeholder;
});

// 切换下拉
const toggleDropdown = () => {
  if (props.disabled) return;
  isOpen.value = !isOpen.value;
};

// 选择选项
const selectOption = (option: Option) => {
  if (option.disabled) return;
  emit('update:modelValue', option.value);
  emit('change', option.value);
  isOpen.value = false;
};

// 清除选择
const clearSelection = (event: Event) => {
  event.stopPropagation();
  emit('update:modelValue', null);
  emit('change', null);
};

// 点击外部关闭
const handleClickOutside = (event: MouseEvent) => {
  if (selectRef.value && !selectRef.value.contains(event.target as Node)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

// 容器样式
const containerClasses = computed(() => {
  const base = [
    'relative flex items-center',
    'bg-bg-tertiary rounded-xl cursor-pointer',
    'transition-all duration-fast',
  ];

  const sizes = {
    sm: 'h-9 px-3',
    md: 'h-11 px-4',
    lg: 'h-13 px-5',
  };

  const states = [];
  if (props.disabled) {
    states.push('opacity-50 cursor-not-allowed');
  } else if (props.error) {
    states.push('ring-2 ring-error');
  } else if (isOpen.value) {
    states.push('ring-2 ring-primary');
  }

  return [...base, sizes[props.size], ...states].join(' ');
});
</script>

<template>
  <div ref="selectRef" class="w-full relative">
    <!-- 触发器 -->
    <div :class="containerClasses" @click="toggleDropdown">
      <span
        class="flex-1 text-[17px] truncate"
        :class="selectedOption ? 'text-text-primary' : 'text-text-muted'"
      >
        {{ displayText }}
      </span>

      <!-- 清除按钮 -->
      <button
        v-if="clearable && selectedOption && !disabled"
        type="button"
        class="mr-2 text-text-muted hover:text-text-secondary"
        @click="clearSelection"
      >
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="10" />
          <path d="M15 9l-6 6M9 9l6 6" />
        </svg>
      </button>

      <!-- 箭头图标 -->
      <svg
        class="w-5 h-5 text-text-muted transition-transform duration-fast"
        :class="{ 'rotate-180': isOpen }"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
      >
        <path d="M6 9l6 6 6-6" />
      </svg>
    </div>

    <!-- 下拉面板 -->
    <Transition name="dropdown">
      <div
        v-if="isOpen"
        class="absolute z-dropdown mt-1 w-full bg-bg-secondary rounded-xl shadow-lg border border-border-color overflow-hidden"
      >
        <div class="max-h-60 overflow-auto py-1">
          <div
            v-for="option in options"
            :key="option.value"
            class="px-4 py-2.5 cursor-pointer transition-colors duration-fast"
            :class="{
              'bg-primary/10 text-primary': option.value === modelValue,
              'hover:bg-bg-hover': option.value !== modelValue && !option.disabled,
              'opacity-50 cursor-not-allowed': option.disabled,
            }"
            @click="selectOption(option)"
          >
            {{ option.label }}
          </div>
        </div>
      </div>
    </Transition>

    <!-- 错误信息 -->
    <p v-if="error && errorMessage" class="mt-1.5 text-sm text-error">
      {{ errorMessage }}
    </p>
  </div>
</template>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 150ms ease-out;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
