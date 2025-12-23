<script setup lang="ts">
/**
 * ListRow - 列表行组件
 *
 * 应用中最核心的模式，用于替代卡片
 * - 左侧：图标/缩略图
 * - 中间：主要信息（标题 + 副标题）
 * - 右侧：元数据/操作
 */

interface Props {
  /** 标题 */
  title: string;
  /** 副标题 */
  subtitle?: string;
  /** 是否显示右侧箭头 */
  showArrow?: boolean;
  /** 是否可点击 */
  clickable?: boolean;
  /** 是否显示分割线 */
  showDivider?: boolean;
  /** 是否激活状态 */
  active?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 图标/缩略图尺寸 */
  iconSize?: 'sm' | 'md' | 'lg';
}

const props = withDefaults(defineProps<Props>(), {
  subtitle: '',
  showArrow: true,
  clickable: true,
  showDivider: true,
  active: false,
  disabled: false,
  iconSize: 'md',
});

// Emits 定义
const emit = defineEmits<{
  click: [event: MouseEvent];
}>();

// 点击处理
const handleClick = (event: MouseEvent) => {
  if (props.disabled) return;
  emit('click', event);
};

// 图标尺寸映射
const iconSizeClasses: Record<string, string> = {
  sm: 'w-10 h-10',
  md: 'w-12 h-12',
  lg: 'w-16 h-16',
};
</script>

<template>
  <div
    class="group relative"
    :class="{
      'cursor-pointer': clickable && !disabled,
      'cursor-not-allowed opacity-50': disabled,
    }"
    @click="handleClick"
  >
    <div
      class="flex items-center py-4 transition-colors duration-fast"
      :class="{
        'hover:bg-bg-hover': clickable && !disabled,
        'bg-primary/5': active,
      }"
    >
      <!-- 左侧：图标/缩略图 -->
      <div
        v-if="$slots.icon"
        class="rounded-xl bg-bg-tertiary mr-4 flex-shrink-0 flex items-center justify-center overflow-hidden"
        :class="iconSizeClasses[iconSize]"
      >
        <slot name="icon" />
      </div>

      <!-- 中间：主要信息 -->
      <div class="flex-1 min-w-0">
        <h3 class="text-[17px] font-medium text-text-primary truncate">
          {{ title }}
        </h3>
        <p v-if="subtitle" class="text-[14px] text-text-secondary truncate mt-0.5">
          {{ subtitle }}
        </p>
        <!-- 额外内容插槽 -->
        <slot name="content" />
      </div>

      <!-- 右侧：元数据/操作 -->
      <div class="ml-4 flex items-center gap-2 text-text-secondary flex-shrink-0">
        <!-- 元数据插槽 -->
        <slot name="meta" />

        <!-- 操作按钮插槽 -->
        <slot name="action" />

        <!-- 箭头图标 -->
        <svg
          v-if="showArrow && clickable"
          class="w-5 h-5 text-text-muted opacity-40 transition-transform duration-fast"
          :class="{ 'group-hover:translate-x-0.5': !disabled }"
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
      v-if="showDivider"
      class="h-px bg-border-color"
      :class="$slots.icon ? 'ml-16' : ''"
    />
  </div>
</template>
