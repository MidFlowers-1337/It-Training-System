<script setup lang="ts">
/**
 * Modal - 模态框组件
 *
 * 符合 Apple-like 设计规范
 * - 毛玻璃遮罩
 * - 居中显示
 * - 支持头部、内容、底部区域
 * - 支持关闭按钮和 ESC 键关闭
 */

import { computed, watch, onMounted, onUnmounted } from 'vue';
import Button from './Button.vue';

// Props 定义
interface Props {
  /** 是否显示 */
  modelValue?: boolean;
  /** 标题 */
  title?: string;
  /** 宽度 */
  width?: string;
  /** 是否显示关闭按钮 */
  closable?: boolean;
  /** 点击遮罩是否关闭 */
  closeOnClickMask?: boolean;
  /** 按 ESC 是否关闭 */
  closeOnEsc?: boolean;
  /** 是否显示底部 */
  showFooter?: boolean;
  /** 确认按钮文字 */
  confirmText?: string;
  /** 取消按钮文字 */
  cancelText?: string;
  /** 确认按钮加载状态 */
  confirmLoading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: false,
  title: '',
  width: '480px',
  closable: true,
  closeOnClickMask: true,
  closeOnEsc: true,
  showFooter: true,
  confirmText: '确认',
  cancelText: '取消',
  confirmLoading: false,
});

// Emits 定义
const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  confirm: [];
  cancel: [];
  close: [];
}>();

// 关闭模态框
const close = () => {
  emit('update:modelValue', false);
  emit('close');
};

// 点击遮罩
const handleMaskClick = () => {
  if (props.closeOnClickMask) {
    close();
  }
};

// 确认
const handleConfirm = () => {
  emit('confirm');
};

// 取消
const handleCancel = () => {
  emit('cancel');
  close();
};

// ESC 键监听
const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Escape' && props.closeOnEsc && props.modelValue) {
    close();
  }
};

// 锁定/解锁 body 滚动
watch(
  () => props.modelValue,
  (visible) => {
    if (visible) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
  }
);

onMounted(() => {
  document.addEventListener('keydown', handleKeydown);
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown);
  document.body.style.overflow = '';
});

// 模态框样式
const modalStyle = computed(() => ({
  width: props.width,
  maxWidth: '90vw',
}));
</script>

<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div
        v-if="modelValue"
        class="fixed inset-0 z-modal flex items-center justify-center p-4"
      >
        <!-- 遮罩 -->
        <div
          class="absolute inset-0 bg-black/50 backdrop-blur-sm"
          @click="handleMaskClick"
        />

        <!-- 模态框内容 -->
        <Transition name="modal-scale">
          <div
            v-if="modelValue"
            class="relative bg-bg-secondary rounded-2xl shadow-2xl border border-border-color overflow-hidden"
            :style="modalStyle"
            @click.stop
          >
            <!-- 头部 -->
            <div
              v-if="title || closable || $slots.header"
              class="flex items-center justify-between px-6 py-4 border-b border-border-color"
            >
              <slot name="header">
                <h3 class="text-xl font-semibold text-text-primary">
                  {{ title }}
                </h3>
              </slot>

              <!-- 关闭按钮 -->
              <button
                v-if="closable"
                type="button"
                class="w-8 h-8 rounded-full flex items-center justify-center text-text-muted hover:text-text-primary hover:bg-bg-hover transition-all"
                @click="close"
              >
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M18 6L6 18M6 6l12 12" />
                </svg>
              </button>
            </div>

            <!-- 内容 -->
            <div class="p-6 max-h-[60vh] overflow-auto">
              <slot />
            </div>

            <!-- 底部 -->
            <div
              v-if="showFooter || $slots.footer"
              class="flex items-center justify-end gap-3 px-6 py-4 border-t border-border-color"
            >
              <slot name="footer">
                <Button variant="secondary" @click="handleCancel">
                  {{ cancelText }}
                </Button>
                <Button
                  variant="primary"
                  :loading="confirmLoading"
                  @click="handleConfirm"
                >
                  {{ confirmText }}
                </Button>
              </slot>
            </div>
          </div>
        </Transition>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
/* 遮罩淡入淡出 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 200ms ease-out;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

/* 模态框缩放 */
.modal-scale-enter-active {
  transition: all 200ms ease-out;
}

.modal-scale-leave-active {
  transition: all 150ms ease-in;
}

.modal-scale-enter-from {
  opacity: 0;
  transform: scale(0.95);
}

.modal-scale-leave-to {
  opacity: 0;
  transform: scale(0.95);
}
</style>
