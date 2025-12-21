<script setup lang="ts">
/**
 * Divider - 分割线组件
 *
 * 用于分隔内容区块
 * - 默认：全宽分割线
 * - 变体：indented（左侧缩进，用于列表项之间）
 */

interface Props {
  /** 是否缩进（左侧 16px） */
  indented?: boolean;
  /** 自定义左侧间距 */
  indent?: number;
  /** 垂直方向分割 */
  vertical?: boolean;
  /** 自定义颜色 */
  color?: string;
}

const props = withDefaults(defineProps<Props>(), {
  indented: false,
  indent: 16,
  vertical: false,
});
</script>

<template>
  <div
    v-if="vertical"
    class="w-px bg-border-color self-stretch"
    :style="color ? { backgroundColor: color } : {}"
  />
  <div
    v-else
    class="h-px bg-border-color w-full"
    :class="{ 'ml-4': indented }"
    :style="{
      marginLeft: indented ? `${indent}px` : undefined,
      backgroundColor: color,
    }"
  />
</template>
