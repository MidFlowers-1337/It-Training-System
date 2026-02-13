<template>
  <div :class="['auth-card', `auth-card--${theme}`]">
    <!-- Title -->
    <h1 class="auth-title">登录到你的账户</h1>
    <p class="auth-subtitle">
      还没有账号？<router-link to="/register" class="auth-link">立即注册</router-link>
    </p>

    <!-- Form -->
    <form @submit.prevent="handleLogin" class="auth-form" novalidate>
      <div class="auth-field">
        <label class="auth-label" for="login-username">邮箱 / 用户名</label>
        <input
          id="login-username"
          v-model="form.username"
          type="text"
          autocomplete="username"
          placeholder="you@example.com"
          :class="['auth-input', errors.username && 'auth-input--error']"
          :aria-invalid="!!errors.username"
          :aria-describedby="errors.username ? 'login-username-error' : undefined"
          @blur="validateUsername"
        />
        <p v-if="errors.username" :id="'login-username-error'" class="auth-error">{{ errors.username }}</p>
      </div>
      <div class="auth-field">
        <div class="auth-label-row">
          <label class="auth-label" for="login-password">密码</label>
          <a href="javascript:void(0)" class="auth-forgot" @click="handleForgot">忘记密码？</a>
        </div>
        <input
          id="login-password"
          v-model="form.password"
          type="password"
          autocomplete="current-password"
          placeholder="输入密码"
          :class="['auth-input', errors.password && 'auth-input--error']"
          :aria-invalid="!!errors.password"
          :aria-describedby="errors.password ? 'login-password-error' : undefined"
          @blur="validatePassword"
        />
        <p v-if="errors.password" :id="'login-password-error'" class="auth-error">{{ errors.password }}</p>
      </div>

      <button type="submit" :disabled="loading" :class="['auth-submit', shaking && 'auth-shake']">
        <span v-if="!loading">继续</span>
        <span v-else class="auth-submit-loading">
          <svg class="animate-spin w-4 h-4" viewBox="0 0 24 24" fill="none">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          登录中…
        </span>
      </button>
    </form>

    <!-- Divider -->
    <div class="auth-divider"><span>或</span></div>

    <!-- Guest Login -->
    <button class="auth-social" @click="handleGuestLogin">
      <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
        <polyline points="10 17 15 12 10 7"/>
        <line x1="15" y1="12" x2="3" y2="12"/>
      </svg>
      以游客身份体验
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { useHead } from '@unhead/vue'
import { toast } from '@/composables/useToast'

useHead({ title: '登录 — IT 智能培训系统' })

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const form = ref({ username: '', password: '' })
const loading = ref(false)
const shaking = ref(false)

const errors = reactive({ username: '', password: '' })

function validateUsername() {
  errors.username = form.value.username ? '' : '请输入用户名或邮箱'
}

function validatePassword() {
  errors.password = form.value.password ? '' : '请输入密码'
}

function validate(): boolean {
  validateUsername()
  validatePassword()
  return !errors.username && !errors.password
}

function triggerShake() {
  shaking.value = true
  setTimeout(() => { shaking.value = false }, 500)
}

async function handleLogin() {
  if (!validate()) {
    triggerShake()
    return
  }
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    toast.success('登录成功')
    router.push((route.query.redirect as string) || '/student/home')
  } catch (e: any) {
    toast.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}

function handleForgot() {
  toast.info('密码找回功能开发中')
}

function handleGuestLogin() {
  router.push('/preview')
}
</script>

<style scoped>
/* ====================================================================
   Base — 共享基础样式
   ==================================================================== */
.auth-card {
  background: rgb(var(--color-surface));
  border-radius: var(--radius-lg);
  padding: 40px;
  box-shadow: var(--shadow-lg);
}

.auth-title {
  font-size: 20px;
  font-weight: 600;
  color: rgb(var(--color-text-primary));
  margin: 0 0 8px;
  letter-spacing: -0.01em;
}

.auth-subtitle {
  font-size: 14px;
  color: rgb(var(--color-text-secondary));
  margin: 0 0 28px;
}

.auth-link {
  color: rgb(var(--color-primary));
  font-weight: 500;
  text-decoration: none;
}
.auth-link:hover { text-decoration: underline; }

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.auth-field {
  display: flex;
  flex-direction: column;
}

.auth-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.auth-label {
  font-size: 13px;
  font-weight: 500;
  color: rgb(var(--color-text-secondary));
  margin-bottom: 6px;
  letter-spacing: 0.01em;
}

.auth-forgot {
  font-size: 12px;
  font-weight: 500;
  color: rgb(var(--color-primary));
  text-decoration: none;
  margin-bottom: 6px;
  cursor: pointer;
}
.auth-forgot:hover { text-decoration: underline; }

.auth-input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 6px;
  border: 1px solid rgb(var(--color-border));
  background: rgb(var(--color-surface));
  color: rgb(var(--color-text));
  font-size: 14px;
  line-height: 20px;
  outline: none;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;
}
.auth-input::placeholder { color: rgb(var(--color-text-tertiary)); }
.auth-input:focus {
  border-color: rgb(var(--color-primary));
  box-shadow: 0 0 0 3px rgb(var(--color-primary) / 0.10);
}
.auth-input--error {
  border-color: rgb(var(--color-danger)) !important;
  box-shadow: 0 0 0 3px rgb(var(--color-danger) / 0.10) !important;
}

.auth-error {
  font-size: 12px;
  color: rgb(var(--color-danger));
  margin: 4px 0 0;
}

.auth-submit {
  width: 100%;
  margin-top: 4px;
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  background: rgb(var(--color-primary));
  border: none;
  cursor: pointer;
  transition: all 0.15s ease;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08), 0 0 0 1px rgba(0,0,0,0.06), inset 0 1px 0 rgba(255,255,255,0.1);
}
.auth-submit:hover {
  filter: brightness(1.1);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15), 0 0 0 1px rgba(0,0,0,0.08);
}
.auth-submit:active {
  filter: brightness(0.95);
  box-shadow: 0 0 0 1px rgba(0,0,0,0.1), inset 0 2px 4px rgba(0,0,0,0.1);
}
.auth-submit:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  filter: none;
  box-shadow: none;
}
.auth-submit-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.auth-shake {
  animation: shake 0.4s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}

@keyframes shake {
  10%, 90% { transform: translateX(-1px); }
  20%, 80% { transform: translateX(2px); }
  30%, 50%, 70% { transform: translateX(-4px); }
  40%, 60% { transform: translateX(4px); }
}

.auth-divider {
  display: flex;
  align-items: center;
  margin: 24px 0;
  gap: 12px;
}
.auth-divider::before,
.auth-divider::after {
  content: '';
  flex: 1;
  height: 1px;
  background: rgb(var(--color-border) / 0.6);
}
.auth-divider span {
  font-size: 12px;
  font-weight: 500;
  color: rgb(var(--color-text-tertiary));
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.auth-social {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  color: rgb(var(--color-text-primary));
  background: rgb(var(--color-surface));
  border: 1px solid rgb(var(--color-border));
  cursor: pointer;
  transition: all 0.15s ease;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}
.auth-social:hover {
  background: rgb(var(--color-surface-alt));
  box-shadow: 0 2px 4px rgba(0,0,0,0.06), 0 0 0 1px rgba(0,0,0,0.08);
}
.auth-social:active { background: rgb(var(--color-border) / 0.2); }

/* ====================================================================
   ☀️ light/Notion — 无卡片边框, 黑色按钮, 干净克制
   ==================================================================== */
.auth-card--light {
  background: transparent;
  box-shadow: none;
  border-radius: 0;
}
.auth-card--light .auth-submit {
  background: #191919;
  border-radius: 8px;
  box-shadow: none;
}
.auth-card--light .auth-submit:hover {
  background: #333;
  filter: none;
  box-shadow: none;
}
.auth-card--light .auth-input {
  border-radius: 8px;
  border-color: #E5E5E5;
}
.auth-card--light .auth-input:focus {
  border-color: #191919;
  box-shadow: 0 0 0 2px rgba(25,25,25,0.1);
}
.auth-card--light .auth-social {
  border-radius: 8px;
  border-color: #E5E5E5;
  box-shadow: none;
}
.auth-card--light .auth-social:hover {
  background: #F5F5F5;
  box-shadow: none;
}
.auth-card--light .auth-link {
  color: #191919;
  text-decoration: underline;
  text-underline-offset: 2px;
}
.auth-card--light .auth-forgot {
  color: #191919;
  text-decoration: underline;
  text-underline-offset: 2px;
}

/* ====================================================================
   🌙 dark/Linear — 暗色卡片 + 白色发光按钮
   ==================================================================== */
.auth-card--dark {
  background: #111;
  border: 1px solid rgba(255,255,255,0.06);
  box-shadow: 0 0 40px rgba(0,0,0,0.3);
}
.auth-card--dark .auth-submit {
  background: #fff;
  color: #000;
  border-radius: 6px;
  box-shadow: none;
}
.auth-card--dark .auth-submit:hover {
  background: #e5e5e5;
  filter: none;
  box-shadow: 0 0 20px rgba(255,255,255,0.1);
}
.auth-card--dark .auth-input {
  background: rgba(255,255,255,0.04);
  border-color: rgba(255,255,255,0.08);
  color: #EDEDED;
}
.auth-card--dark .auth-input::placeholder {
  color: #6B6B6F;
}
.auth-card--dark .auth-input:focus {
  border-color: #818CF8;
  box-shadow: 0 0 0 2px rgba(129,140,248,0.15);
}
.auth-card--dark .auth-social {
  background: transparent;
  border-color: rgba(255,255,255,0.08);
  color: #A0A0A5;
}
.auth-card--dark .auth-social:hover {
  border-color: rgba(255,255,255,0.15);
  color: #EDEDED;
  background: rgba(255,255,255,0.04);
}
.auth-card--dark .auth-title { color: #EDEDED; }
.auth-card--dark .auth-subtitle { color: #6B6B6F; }
.auth-card--dark .auth-label { color: #A0A0A5; }
.auth-card--dark .auth-link { color: #818CF8; }
.auth-card--dark .auth-forgot { color: #818CF8; }
.auth-card--dark .auth-divider::before,
.auth-card--dark .auth-divider::after { background: rgba(255,255,255,0.06); }
.auth-card--dark .auth-divider span { color: #6B6B6F; }

/* ====================================================================
   🌅 warm/Slack — 暖白卡片 + 暖橙药丸按钮
   ==================================================================== */
.auth-card--warm {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 24px rgba(120,80,20,0.08);
}
.auth-card--warm .auth-submit {
  background: #D97706;
  border-radius: 9999px;
  font-weight: 700;
  padding: 12px 16px;
  box-shadow: 0 2px 8px rgba(217,119,6,0.25);
}
.auth-card--warm .auth-submit:hover {
  filter: brightness(1.1);
  box-shadow: 0 4px 16px rgba(217,119,6,0.3);
}
.auth-card--warm .auth-input {
  border-radius: 12px;
  border-color: #E7E5E4;
}
.auth-card--warm .auth-input:focus {
  border-color: #D97706;
  box-shadow: 0 0 0 3px rgba(217,119,6,0.1);
}
.auth-card--warm .auth-social {
  border-radius: 9999px;
  border: 2px solid #D97706;
  color: #92400E;
  background: #fff;
  box-shadow: none;
}
.auth-card--warm .auth-social:hover {
  background: #FFFBF5;
  box-shadow: none;
}
.auth-card--warm .auth-link { color: #D97706; }
.auth-card--warm .auth-forgot { color: #D97706; }

/* ====================================================================
   ❄️ pro/Vercel — 白色卡片 + 细边框 + monospace 元素
   ==================================================================== */
.auth-card--pro {
  background: #fff;
  border: 1px solid #EAEAEA;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.auth-card--pro .auth-submit {
  background: #0F172A;
  border-radius: 6px;
  box-shadow: none;
}
.auth-card--pro .auth-submit:hover {
  background: #1E293B;
  filter: none;
  box-shadow: none;
}
.auth-card--pro .auth-input {
  border-radius: 6px;
  border-color: #EAEAEA;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 13px;
}
.auth-card--pro .auth-input:focus {
  border-color: #0284C7;
  box-shadow: 0 0 0 2px rgba(2,132,199,0.1);
}
.auth-card--pro .auth-social {
  border-radius: 6px;
  border: 1px solid #EAEAEA;
  box-shadow: none;
}
.auth-card--pro .auth-social:hover {
  background: #FAFAFA;
  border-color: #999;
  box-shadow: none;
}
.auth-card--pro .auth-link {
  color: #666;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 13px;
}
.auth-card--pro .auth-forgot {
  color: #666;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
}
</style>
