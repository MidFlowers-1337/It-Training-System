<script setup lang="ts">
/**
 * Section - 区块容器组件
 *
 * 用于分隔页面内容
 * - 底部间距：64px
 * - 支持标题和副标题
 * - 支持操作按钮
 */

interface Props {
  /** 区块标题 */
  title?: string;
  /** 区块副标题 */
  subtitle?: string;
  /** 是否显示分割线 */
  showDivider?: boolean;
  /** 是否紧凑模式（减少间距） */
  compact?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  subtitle: '',
  showDivider: false,
  compact: false,
});
</script>

<template>
  <section
    :class="[
      compact ? 'mb-8' : 'mb-16',
    ]"
  >
    <!-- 区块头部 -->
    <div
      v-if="title || $slots.header"
      class="flex items-end justify-between gap-4 mb-6"
    >
      <div>
        <slot name="header">
          <h2 class="text-[28px] font-semibold text-text-primary tracking-tight">
            {{ title }}
          </h2>
          <p v-if="subtitle" class="mt-1 text-text-secondary">
            {{ subtitle }}
          </p>
        </slot>
      </div>

      <!-- 操作区 -->
      <div v-if="$slots.actions" class="flex items-center gap-3 flex-shrink-0">
        <slot name="actions" />
      </div>
    </div>

    <!-- 分割线 -->
    <div
      v-if="showDivider && (title || $slots.header)"
      class="h-px bg-border-color mb-6"
    />

    <!-- 内容 -->
    <div>
      <slot />
    </div>
  </section>
</template>
