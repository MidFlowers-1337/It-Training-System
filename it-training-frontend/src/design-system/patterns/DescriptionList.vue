<script setup lang="ts">
/**
 * DescriptionList - 详情列表组件
 *
 * 用于详情页展示信息，替代 Grid 卡片
 * - Label-Value 对齐排列
 * - 支持分割线
 */

// 列表项类型
interface DescriptionItem {
  label: string;
  value: string | number;
  /** 是否隐藏该项 */
  hidden?: boolean;
  /** 值的样式类型 */
  type?: 'default' | 'primary' | 'success' | 'warning' | 'error';
}

interface Props {
  /** 列表数据 */
  items?: DescriptionItem[];
  /** 布局方向 */
  direction?: 'horizontal' | 'vertical';
  /** 是否显示分割线 */
  showDivider?: boolean;
  /** 列数（仅 horizontal 布局） */
  columns?: 1 | 2 | 3;
}

const props = withDefaults(defineProps<Props>(), {
  items: () => [],
  direction: 'horizontal',
  showDivider: true,
  columns: 1,
});

// 过滤隐藏项
const visibleItems = computed(() => {
  return props.items.filter((item) => !item.hidden);
});

// 值的颜色映射
const valueColorClasses: Record<string, string> = {
  default: 'text-text-primary',
  primary: 'text-primary',
  success: 'text-success',
  warning: 'text-warning',
  error: 'text-error',
};

import { computed } from 'vue';
</script>

<template>
  <!-- 水平布局（Label 在左，Value 在右） -->
  <div v-if="direction === 'horizontal'" class="space-y-0">
    <div
      v-for="(item, index) in visibleItems"
      :key="index"
      class="flex justify-between items-start py-3 gap-4"
      :class="{
        'border-b border-border-color': showDivider && index < visibleItems.length - 1,
      }"
    >
      <span class="text-text-secondary flex-shrink-0">
        {{ item.label }}
      </span>
      <span
        class="font-medium text-right"
        :class="valueColorClasses[item.type || 'default']"
      >
        <slot :name="`value-${index}`" :item="item">
          {{ item.value }}
        </slot>
      </span>
    </div>
  </div>

  <!-- 垂直布局（Label 在上，Value 在下） -->
  <div
    v-else
    class="grid gap-6"
    :class="{
      'grid-cols-1': columns === 1,
      'grid-cols-2': columns === 2,
      'grid-cols-3': columns === 3,
    }"
  >
    <div
      v-for="(item, index) in visibleItems"
      :key="index"
      class="space-y-1"
    >
      <dt class="text-sm text-text-secondary">
        {{ item.label }}
      </dt>
      <dd
        class="text-base font-medium"
        :class="valueColorClasses[item.type || 'default']"
      >
        <slot :name="`value-${index}`" :item="item">
          {{ item.value }}
        </slot>
      </dd>
    </div>
  </div>

  <!-- 插槽方式使用 -->
  <slot v-if="!items.length" />
</template>
