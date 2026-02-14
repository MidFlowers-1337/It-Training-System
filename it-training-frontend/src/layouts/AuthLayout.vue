<template>
  <div :class="['auth-page', `auth-page--${theme}`]">
    <!-- Top Nav — 根据主题调整品牌样式 -->
    <header class="auth-header">
      <router-link to="/" class="auth-brand">
        <div :class="['auth-brand-icon', `auth-brand-icon--${theme}`]">
          <span>IT</span>
        </div>
        <span class="auth-brand-text">{{ theme === 'pro' ? 'IT-Training' : '智能培训' }}</span>
      </router-link>
      <div class="auth-header-right">
        <ThemeSwitcher />
        <router-link to="/" class="auth-nav-link">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
          返回首页
        </router-link>
      </div>
    </header>

    <!-- Card Container -->
    <main class="auth-main">
      <div class="auth-container">
        <slot />
      </div>
    </main>

    <!-- Footer -->
    <footer class="auth-footer">
      <span>© 2025 IT 智能培训系统</span>
      <span class="auth-footer-sep">·</span>
      <router-link to="/" class="auth-footer-link">首页</router-link>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useThemeStore } from '@/stores/theme'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
</script>

<style scoped>
/* ====================================================================
   Base — 通用布局（不变）
   ==================================================================== */
.auth-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: rgb(var(--color-bg));
  position: relative;
}

.auth-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 32px;
  flex-shrink: 0;
}

.auth-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  transition: opacity 0.15s ease;
}
.auth-brand:hover { opacity: 0.8; }

.auth-brand-icon {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  background: rgb(var(--color-primary));
  display: flex;
  align-items: center;
  justify-content: center;
}
.auth-brand-icon span {
  color: #fff;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.auth-brand-text {
  font-size: 15px;
  font-weight: 600;
  color: rgb(var(--color-text-primary));
  letter-spacing: -0.01em;
}

.auth-header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.auth-nav-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
  color: rgb(var(--color-text-secondary));
  text-decoration: none;
  transition: color 0.15s ease;
}
.auth-nav-link:hover { color: rgb(var(--color-primary)); }

.auth-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 1rem 2rem;
}

.auth-container {
  width: 100%;
  max-width: 420px;
  animation: auth-enter 0.45s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes auth-enter {
  0% { opacity: 0; transform: translateY(10px) scale(0.99); }
  100% { opacity: 1; transform: translateY(0) scale(1); }
}

.auth-footer {
  padding: 16px 32px;
  text-align: center;
  font-size: 12px;
  color: rgb(var(--color-text-tertiary));
  flex-shrink: 0;
}
.auth-footer-sep { margin: 0 6px; }
.auth-footer-link {
  color: rgb(var(--color-text-tertiary));
  text-decoration: none;
  transition: color 0.15s ease;
}
.auth-footer-link:hover { color: rgb(var(--color-text-secondary)); }

/* ====================================================================
   ☀️ light/Notion — 纯净白, 无多余装饰
   ==================================================================== */
.auth-page--light {
  background: #FFFFFF;
}
.auth-brand-icon--light {
  background: #191919;
  border-radius: 8px;
}

/* ====================================================================
   🌙 dark/Linear — 近黑 + 微弱渐变
   ==================================================================== */
.auth-page--dark {
  background: #08090A;
  background-image: radial-gradient(ellipse at 50% 0%, rgba(129,140,248,0.06) 0%, transparent 60%);
}
.auth-brand-icon--dark {
  background: #fff;
}
.auth-page--dark .auth-brand-icon--dark span {
  color: #000;
}

/* ====================================================================
   🌅 warm/Slack — 暖米白
   ==================================================================== */
.auth-page--warm {
  background: #FFFBF5;
}
.auth-brand-icon--warm {
  background: #D97706;
  border-radius: 10px;
}

/* ====================================================================
   ❄️ pro/Vercel — 浅灰
   ==================================================================== */
.auth-page--pro {
  background: #FAFAFA;
}
.auth-brand-icon--pro {
  background: transparent;
  width: auto;
  height: auto;
}
.auth-brand-icon--pro span {
  color: #0F172A;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 18px;
  font-weight: 900;
}
.auth-page--pro .auth-brand-text {
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 14px;
}
</style>
