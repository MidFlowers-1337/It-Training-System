<script setup lang="ts">
/**
 * FormLayout - 表单布局组件
 *
 * 统一的表单布局
 * - 支持垂直/水平布局
 * - 标签对齐
 * - 响应式
 */

interface Props {
  /** 布局方向 */
  layout?: 'vertical' | 'horizontal';
  /** 标签宽度（仅 horizontal 布局） */
  labelWidth?: string;
  /** 标签对齐方式 */
  labelAlign?: 'left' | 'right';
  /** 是否显示必填星号 */
  showRequired?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  layout: 'vertical',
  labelWidth: '100px',
  labelAlign: 'right',
  showRequired: true,
});

// 提供给子组件的上下文
provide('formLayout', {
  layout: props.layout,
  labelWidth: props.labelWidth,
  labelAlign: props.labelAlign,
  showRequired: props.showRequired,
});

import { provide } from 'vue';
</script>

<template>
  <form class="space-y-6" @submit.prevent>
    <slot />
  </form>
</template>
