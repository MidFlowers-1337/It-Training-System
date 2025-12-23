<script setup lang="ts">
/**
 * InsetGroup - iOS 风格分组容器
 *
 * 类似 iOS/macOS 设置页面的分组样式
 * - 圆角卡片
 * - 分割线
 * - 支持头部和尾部说明
 */

interface Props {
  /** 分组标题 */
  title?: string;
  /** 底部说明文字 */
  footer?: string;
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  footer: '',
});
</script>

<template>
  <div class="inset-group">
    <!-- 标题 -->
    <div
      v-if="title"
      class="px-4 pb-2 text-sm font-medium text-text-secondary uppercase tracking-wider"
    >
      {{ title }}
    </div>

    <!-- 内容区 -->
    <div class="rounded-2xl overflow-hidden bg-bg-secondary border border-border-color shadow-sm">
      <slot />
    </div>

    <!-- 底部说明 -->
    <div
      v-if="footer"
      class="px-4 pt-2 text-sm text-text-muted"
    >
      {{ footer }}
    </div>
  </div>
</template>
