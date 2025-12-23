<script setup lang="ts">
/**
 * AuthLayout - 认证页面布局组件
 *
 * 用于登录/注册等认证页面的居中布局
 * - 渐变浮动球装饰背景
 * - 极简居中卡片设计
 * - 包含 Logo、标题、表单区域和底部链接
 * - 支持深色模式
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
  <div class="auth-page">
    <!-- 渐变浮动球背景 -->
    <div class="auth-bg">
      <div class="bg-orb bg-orb-1"></div>
      <div class="bg-orb bg-orb-2"></div>
      <div class="bg-orb bg-orb-3"></div>
    </div>

    <!-- 主内容区 -->
    <div class="auth-content">
      <!-- 主卡片区域 -->
      <div class="auth-card">
        <!-- Logo 和标题区域 -->
        <div class="auth-header">
          <!-- Logo 插槽 -->
          <div class="auth-logo">
            <slot name="logo">
              <svg class="logo-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 14l9-5-9-5-9 5 9 5z" />
                <path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
                <path d="M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222" />
              </svg>
            </slot>
          </div>

          <!-- 标题 -->
          <h1 class="auth-title">{{ title }}</h1>

          <!-- 副标题 -->
          <p v-if="subtitle" class="auth-subtitle">{{ subtitle }}</p>
        </div>

        <!-- 表单内容区域 -->
        <div class="auth-form">
          <slot />
        </div>

        <!-- 底部链接 -->
        <div v-if="showFooter" class="auth-footer">
          {{ footerText }}
          <router-link v-if="footerLinkTo" :to="footerLinkTo" class="auth-footer-link">
            {{ footerLinkText }}
          </router-link>
        </div>
      </div>

      <!-- 额外内容插槽（如演示账号） -->
      <slot name="extra" />
    </div>
  </div>
</template>

<style scoped>
/* ========================================
   Apple 风格认证页面布局
   渐变浮动球装饰 + 居中卡片
   ======================================== */

.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 16px;
  background: var(--bg-primary);
  overflow: hidden;
}

/* ===== 渐变浮动球背景 ===== */
.auth-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  animation: float 8s ease-in-out infinite;
}

.bg-orb-1 {
  top: -20%;
  left: -10%;
  width: 500px;
  height: 500px;
  background: var(--primary-color);
  opacity: 0.12;
  animation-delay: 0s;
}

.bg-orb-2 {
  top: 50%;
  right: -15%;
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #5856D6, #AF52DE);
  opacity: 0.1;
  animation-delay: -2.5s;
}

.bg-orb-3 {
  bottom: -10%;
  left: 30%;
  width: 350px;
  height: 350px;
  background: linear-gradient(135deg, #5AC8FA, #34C759);
  opacity: 0.08;
  animation-delay: -5s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-30px) scale(1.05);
  }
}

/* ===== 主内容区 ===== */
.auth-content {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 420px;
}

/* ===== 认证卡片 ===== */
.auth-card {
  background: rgba(var(--bg-secondary-rgb, 255, 255, 255) / 0.85);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-radius: 20px;
  border: 0.5px solid rgba(var(--border-color-rgb, 0, 0, 0) / 0.08);
  padding: 40px 32px;
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.04),
    0 4px 8px rgba(0, 0, 0, 0.04),
    0 12px 24px rgba(0, 0, 0, 0.06);
}

/* ===== 头部区域 ===== */
.auth-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.auth-logo {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  border: 1px solid rgba(var(--primary-color-rgb, 0, 122, 255) / 0.15);
  border-radius: 18px;
  color: var(--primary-color);
}

.logo-icon {
  width: 32px;
  height: 32px;
}

.auth-title {
  margin-top: 24px;
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
}

.auth-subtitle {
  margin-top: 8px;
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.5;
}

/* ===== 表单区域 ===== */
.auth-form {
  margin-top: 32px;
}

/* ===== 底部链接 ===== */
.auth-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: var(--text-secondary);
}

.auth-footer-link {
  color: var(--primary-color);
  font-weight: 500;
  text-decoration: none;
  margin-left: 4px;
  transition: opacity 0.2s ease;
}

.auth-footer-link:hover {
  opacity: 0.8;
}

/* ===== 响应式 ===== */
@media (max-width: 480px) {
  .auth-page {
    padding: 24px 16px;
  }

  .auth-card {
    padding: 32px 24px;
    border-radius: 16px;
  }

  .auth-logo {
    width: 56px;
    height: 56px;
  }

  .logo-icon {
    width: 28px;
    height: 28px;
  }

  .auth-title {
    font-size: 24px;
  }

  .bg-orb-1 {
    width: 300px;
    height: 300px;
  }

  .bg-orb-2 {
    width: 250px;
    height: 250px;
  }

  .bg-orb-3 {
    width: 200px;
    height: 200px;
  }
}

/* ===== 深色模式 ===== */
[data-theme="dark"] .auth-card {
  background: rgba(28, 28, 30, 0.85);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow:
    0 1px 2px rgba(0, 0, 0, 0.2),
    0 4px 8px rgba(0, 0, 0, 0.2),
    0 12px 24px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .bg-orb-1 {
  opacity: 0.15;
}

[data-theme="dark"] .bg-orb-2 {
  opacity: 0.12;
}

[data-theme="dark"] .bg-orb-3 {
  opacity: 0.1;
}

/* ===== 暖色模式 ===== */
[data-theme="warm"] .bg-orb-1 {
  background: var(--primary-color);
}

[data-theme="warm"] .bg-orb-2 {
  background: linear-gradient(135deg, #FF9500, #FFCC00);
}

[data-theme="warm"] .bg-orb-3 {
  background: linear-gradient(135deg, #FF6B6B, #FF9500);
}
</style>
