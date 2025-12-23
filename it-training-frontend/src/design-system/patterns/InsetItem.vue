<script setup lang="ts">
/**
 * InsetItem - iOS 风格分组项
 *
 * 配合 InsetGroup 使用
 * - 左侧标签
 * - 右侧值/控件
 * - 分割线
 */

interface Props {
  /** 标签文字 */
  label: string;
  /** 右侧值 */
  value?: string;
  /** 是否可点击 */
  clickable?: boolean;
  /** 是否显示箭头 */
  showArrow?: boolean;
  /** 是否最后一项（不显示分割线） */
  last?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  value: '',
  clickable: false,
  showArrow: false,
  last: false,
});

// Emits 定义
const emit = defineEmits<{
  click: [event: MouseEvent];
}>();

// 点击处理
const handleClick = (event: MouseEvent) => {
  if (props.clickable) {
    emit('click', event);
  }
};
</script>

<template>
  <div
    class="relative"
    :class="{
      'cursor-pointer hover:bg-bg-hover': clickable,
    }"
    @click="handleClick"
  >
    <div class="flex items-center justify-between gap-4 px-5 py-4">
      <!-- 左侧：图标 + 标签 -->
      <div class="flex items-center gap-3">
        <slot name="icon" />
        <span class="text-text-primary">{{ label }}</span>
      </div>

      <!-- 右侧：值 + 控件 + 箭头 -->
      <div class="flex items-center gap-2">
        <!-- 值 -->
        <span v-if="value" class="text-text-secondary">
          {{ value }}
        </span>

        <!-- 自定义控件插槽 -->
        <slot />

        <!-- 箭头 -->
        <svg
          v-if="showArrow || clickable"
          class="w-5 h-5 text-text-muted opacity-40"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <path d="M9 18l6-6-6-6" />
        </svg>
      </div>
    </div>

    <!-- 分割线 -->
    <div
      v-if="!last"
      class="absolute bottom-0 left-5 right-0 h-px bg-border-color"
    />
  </div>
</template>
