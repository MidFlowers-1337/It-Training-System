<script setup lang="ts">
/**
 * EmptyState - 空状态组件
 *
 * 用于展示无数据、无结果等空状态
 * - 支持图标/emoji 展示
 * - 支持标题和描述
 * - 支持操作按钮
 */

import { computed, useSlots } from 'vue';
import Button from './Button.vue';

// Props 定义
interface Props {
  /** 图标组件 */
  icon?: object;
  /** Emoji 字符 */
  emoji?: string;
  /** 标题 */
  title?: string;
  /** 描述文字 */
  description?: string;
  /** 操作按钮文字 */
  actionText?: string;
  /** 尺寸 */
  size?: 'sm' | 'md' | 'lg';
}

const props = withDefaults(defineProps<Props>(), {
  emoji: '—',
  title: '暂无数据',
  description: '',
  actionText: '',
  size: 'md',
});

// Emits 定义
const emit = defineEmits<{
  action: [];
}>();

const slots = useSlots();

// 是否有图标组件
const hasIcon = computed(() => !!props.icon);

// 是否显示操作区域
const showAction = computed(() => !!slots.action || !!props.actionText);

// 尺寸样式
const sizeClasses = computed(() => {
  const sizes = {
    sm: {
      container: 'py-8 px-4',
      iconBox: 'w-16 h-16',
      iconSize: 'w-8 h-8',
      emoji: 'text-3xl',
      title: 'text-sm',
      description: 'text-xs',
    },
    md: {
      container: 'py-16 px-6',
      iconBox: 'w-24 h-24',
      iconSize: 'w-12 h-12',
      emoji: 'text-5xl',
      title: 'text-base',
      description: 'text-sm',
    },
    lg: {
      container: 'py-24 px-8',
      iconBox: 'w-32 h-32',
      iconSize: 'w-16 h-16',
      emoji: 'text-6xl',
      title: 'text-lg',
      description: 'text-base',
    },
  };
  return sizes[props.size];
});

// 处理操作点击
const handleAction = () => {
  emit('action');
};
</script>

<template>
  <div
    class="flex flex-col items-center justify-center text-center"
    :class="sizeClasses.container"
  >
    <!-- 图标区域 -->
    <div
      class="rounded-full bg-bg-tertiary border border-border-color flex items-center justify-center text-text-muted"
      :class="sizeClasses.iconBox"
    >
      <!-- 自定义图标插槽 -->
      <slot name="icon">
        <component
          v-if="hasIcon"
          :is="icon"
          :class="sizeClasses.iconSize"
        />
        <span v-else :class="sizeClasses.emoji" class="leading-none">
          {{ emoji }}
        </span>
      </slot>
    </div>

    <!-- 标题 -->
    <h3
      class="mt-6 font-semibold text-text-primary"
      :class="sizeClasses.title"
    >
      {{ title }}
    </h3>

    <!-- 描述 -->
    <p
      v-if="description"
      class="mt-2 text-text-secondary leading-relaxed max-w-md"
      :class="sizeClasses.description"
    >
      {{ description }}
    </p>

    <!-- 操作区域 -->
    <div v-if="showAction" class="mt-6">
      <slot name="action">
        <Button
          v-if="actionText"
          variant="primary"
          @click="handleAction"
        >
          {{ actionText }}
        </Button>
      </slot>
    </div>
  </div>
</template>
