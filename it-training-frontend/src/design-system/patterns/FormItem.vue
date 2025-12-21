<script setup lang="ts">
/**
 * FormItem - 表单项组件
 *
 * 配合 FormLayout 使用
 * - 标签 + 输入控件 + 错误提示
 */

import { inject, computed } from 'vue';

interface Props {
  /** 标签文字 */
  label?: string;
  /** 是否必填 */
  required?: boolean;
  /** 错误信息 */
  error?: string;
  /** 帮助文字 */
  help?: string;
}

const props = withDefaults(defineProps<Props>(), {
  label: '',
  required: false,
  error: '',
  help: '',
});

// 从 FormLayout 获取上下文
const formLayout = inject('formLayout', {
  layout: 'vertical',
  labelWidth: '100px',
  labelAlign: 'right',
  showRequired: true,
});

// 是否垂直布局
const isVertical = computed(() => formLayout.layout === 'vertical');
</script>

<template>
  <div
    class="form-item"
    :class="{
      'flex items-start gap-4': !isVertical,
    }"
  >
    <!-- 标签 -->
    <label
      v-if="label"
      class="block text-text-secondary"
      :class="{
        'mb-2': isVertical,
        'py-2.5 flex-shrink-0': !isVertical,
        'text-right': !isVertical && formLayout.labelAlign === 'right',
      }"
      :style="!isVertical ? { width: formLayout.labelWidth } : {}"
    >
      <span
        v-if="required && formLayout.showRequired"
        class="text-error mr-1"
      >*</span>
      {{ label }}
    </label>

    <!-- 控件区域 -->
    <div class="flex-1">
      <slot />

      <!-- 帮助文字 -->
      <p v-if="help && !error" class="mt-1.5 text-sm text-text-muted">
        {{ help }}
      </p>

      <!-- 错误提示 -->
      <p v-if="error" class="mt-1.5 text-sm text-error">
        {{ error }}
      </p>
    </div>
  </div>
</template>
