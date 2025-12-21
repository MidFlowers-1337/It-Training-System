<script setup lang="ts">
/**
 * AuthLayout - 认证页面布局组件
 *
 * 用于登录/注册等认证页面的居中布局
 * - 极简居中设计
 * - 纯白背景，无复杂装饰
 * - 包含 Logo、标题、表单区域和底部链接
 */

import { computed } from 'vue';

// Props 定义
interface Props {
  /** 页面标题 */
  title: string;
  /** 页面副标题 */
  subtitle?: string;
  /** 底部链接文本 */
  footerText?: string;
  /** 底部链接目标 */
  footerLinkText?: string;
  /** 底部链接路由 */
  footerLinkTo?: string;
}

const props = withDefaults(defineProps<Props>(), {
  subtitle: '',
  footerText: '',
  footerLinkText: '',
  footerLinkTo: '',
});

// 是否显示底部链接
const showFooter = computed(() => {
  return props.footerText || props.footerLinkText;
});
</script>

<template>
  <div class="min-h-screen bg-bg-primary flex items-center justify-center px-4 py-10">
    <div class="w-full max-w-md">
      <!-- 主卡片区域 -->
      <div class="bg-bg-secondary rounded-2xl border border-border-color p-8 md:p-10 shadow-sm">
        <!-- Logo 和标题区域 -->
        <div class="flex flex-col items-center text-center">
          <!-- Logo 插槽 -->
          <div class="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center">
            <slot name="logo">
              <img src="@/assets/logo.svg" alt="Logo" class="w-8 h-8" />
            </slot>
          </div>

          <!-- 标题 -->
          <h1 class="mt-6 text-2xl font-semibold tracking-tight text-text-primary">
            {{ title }}
          </h1>

          <!-- 副标题 -->
          <p v-if="subtitle" class="mt-2 text-sm text-text-secondary">
            {{ subtitle }}
          </p>
        </div>

        <!-- 表单内容区域 -->
        <div class="mt-8">
          <slot />
        </div>

        <!-- 底部链接 -->
        <div v-if="showFooter" class="mt-6 text-center text-sm text-text-secondary">
          {{ footerText }}
          <router-link
            v-if="footerLinkTo"
            :to="footerLinkTo"
            class="text-primary hover:text-primary-light font-medium ml-1 transition-colors"
          >
            {{ footerLinkText }}
          </router-link>
        </div>
      </div>

      <!-- 额外内容插槽（如演示账号） -->
      <slot name="extra" />
    </div>
  </div>
</template>
