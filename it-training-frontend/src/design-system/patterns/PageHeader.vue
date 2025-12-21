<script setup lang="ts">
/**
 * PageHeader - 页面头部组件
 *
 * 统一的页面头部，包含大标题和操作栏
 * - 标题：40px 大字
 * - 支持副标题
 * - 右侧操作区
 * - 底部分割线
 */

import Divider from '../primitives/Divider.vue';

interface Props {
  /** 页面标题 */
  title: string;
  /** 副标题/描述 */
  subtitle?: string;
  /** 是否显示分割线 */
  showDivider?: boolean;
  /** 是否显示返回按钮 */
  showBack?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  subtitle: '',
  showDivider: true,
  showBack: false,
});

// Emits 定义
const emit = defineEmits<{
  back: [];
}>();

// 返回处理
const handleBack = () => {
  emit('back');
};
</script>

<template>
  <header class="mb-8">
    <div class="flex items-start justify-between gap-4">
      <!-- 左侧：标题区 -->
      <div class="flex-1 min-w-0">
        <!-- 返回按钮 -->
        <button
          v-if="showBack"
          type="button"
          class="inline-flex items-center gap-1 mb-2 text-primary hover:text-primary-light transition-colors"
          @click="handleBack"
        >
          <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 18l-6-6 6-6" />
          </svg>
          <span class="text-sm font-medium">返回</span>
        </button>

        <!-- 标题 -->
        <h1 class="text-[40px] font-semibold text-text-primary tracking-tight leading-tight">
          {{ title }}
        </h1>

        <!-- 副标题 -->
        <p v-if="subtitle" class="mt-2 text-lg text-text-secondary">
          {{ subtitle }}
        </p>
      </div>

      <!-- 右侧：操作区 -->
      <div v-if="$slots.actions" class="flex items-center gap-3 flex-shrink-0">
        <slot name="actions" />
      </div>
    </div>

    <!-- 分割线 -->
    <Divider v-if="showDivider" class="mt-6" />
  </header>
</template>
