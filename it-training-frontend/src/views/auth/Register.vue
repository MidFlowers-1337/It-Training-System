<template>
  <div :class="['auth-card', `auth-card--${theme}`]">
    <!-- Title -->
    <h1 class="auth-title">创建你的账号</h1>
    <p class="auth-subtitle">
      已有账号？<router-link to="/login" class="auth-link">立即登录</router-link>
    </p>

    <!-- Form -->
    <form @submit.prevent="handleRegister" class="auth-form" novalidate>
      <div class="auth-field">
        <label class="auth-label" for="reg-username">用户名 <span class="auth-required">*</span></label>
        <input
          id="reg-username"
          v-model="form.username"
          type="text"
          autocomplete="username"
          placeholder="请输入用户名"
          :class="['auth-input', errors.username && 'auth-input--error']"
          :aria-invalid="!!errors.username"
          :aria-describedby="errors.username ? 'reg-username-error' : undefined"
          @blur="validateField('username')"
        />
        <p v-if="errors.username" id="reg-username-error" class="auth-error">{{ errors.username }}</p>
      </div>
      <div class="auth-row">
        <div class="auth-field auth-field-half">
          <label class="auth-label" for="reg-realname">真实姓名</label>
          <input id="reg-realname" v-model="form.realName" type="text" autocomplete="name" placeholder="选填" class="auth-input" />
        </div>
        <div class="auth-field auth-field-half">
          <label class="auth-label" for="reg-email">邮箱</label>
          <input
            id="reg-email"
            v-model="form.email"
            type="email"
            autocomplete="email"
            placeholder="选填"
            :class="['auth-input', errors.email && 'auth-input--error']"
            :aria-invalid="!!errors.email"
            @blur="validateField('email')"
          />
          <p v-if="errors.email" class="auth-error">{{ errors.email }}</p>
        </div>
      </div>
      <div class="auth-field">
        <label class="auth-label" for="reg-password">密码 <span class="auth-required">*</span></label>
        <input
          id="reg-password"
          v-model="form.password"
          type="password"
          autocomplete="new-password"
          placeholder="至少 6 位字符"
          :class="['auth-input', errors.password && 'auth-input--error']"
          :aria-invalid="!!errors.password"
          :aria-describedby="errors.password ? 'reg-password-error' : undefined"
          @blur="validateField('password')"
        />
        <p v-if="errors.password" id="reg-password-error" class="auth-error">{{ errors.password }}</p>
        <p v-else-if="form.password && form.password.length >= 6" class="auth-hint auth-hint-ok">
          密码强度 OK
        </p>
      </div>
      <div class="auth-field">
        <label class="auth-label" for="reg-confirm">确认密码 <span class="auth-required">*</span></label>
        <input
          id="reg-confirm"
          v-model="confirmPw"
          type="password"
          autocomplete="new-password"
          placeholder="再次输入密码"
          :class="['auth-input', errors.confirm && 'auth-input--error']"
          :aria-invalid="!!errors.confirm"
          :aria-describedby="errors.confirm ? 'reg-confirm-error' : undefined"
          @blur="validateField('confirm')"
        />
        <p v-if="errors.confirm" id="reg-confirm-error" class="auth-error">{{ errors.confirm }}</p>
      </div>

      <button type="submit" :disabled="loading" :class="['auth-submit', shaking && 'auth-shake']">
        <span v-if="!loading">创建账号</span>
        <span v-else class="auth-submit-loading">
          <svg class="animate-spin w-4 h-4" viewBox="0 0 24 24" fill="none">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" />
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
          </svg>
          注册中…
        </span>
      </button>
    </form>

    <!-- Terms hint -->
    <p class="auth-terms">
      注册即表示你同意我们的服务条款和隐私政策
    </p>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { useHead } from '@unhead/vue'
import { toast } from '@/composables/useToast'

useHead({ title: '注册 — IT 智能培训系统' })

const router = useRouter()
const userStore = useUserStore()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const form = ref({ username: '', password: '', email: '', realName: '' })
const confirmPw = ref('')
const loading = ref(false)
const shaking = ref(false)

const errors = reactive({ username: '', password: '', email: '', confirm: '' })

function validateField(field: string) {
  switch (field) {
    case 'username':
      errors.username = form.value.username ? '' : '请输入用户名'
      break
    case 'password':
      errors.password = !form.value.password
        ? '请输入密码'
        : form.value.password.length < 6
          ? '密码至少需要 6 位'
          : ''
      break
    case 'email':
      if (form.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.value.email)) {
        errors.email = '邮箱格式不正确'
      } else {
        errors.email = ''
      }
      break
    case 'confirm':
      errors.confirm = !confirmPw.value
        ? '请再次输入密码'
        : form.value.password !== confirmPw.value
          ? '两次密码不一致'
          : ''
      break
  }
}

function validate(): boolean {
  validateField('username')
  validateField('password')
  validateField('email')
  validateField('confirm')
  return !errors.username && !errors.password && !errors.email && !errors.confirm
}

function triggerShake() {
  shaking.value = true
  setTimeout(() => { shaking.value = false }, 500)
}

async function handleRegister() {
  if (!validate()) {
    triggerShake()
    return
  }
  loading.value = true
  try {
    await userStore.register(form.value)
    toast.success('注册成功，请登录')
    router.push('/login')
  } catch (e: any) {
    toast.error(e.message || '注册失败')
  } finally {
    loading.value = false
  }
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

.auth-field { display: flex; flex-direction: column; }
.auth-row { display: flex; gap: 12px; }
.auth-field-half { flex: 1; min-width: 0; }

.auth-label {
  font-size: 13px;
  font-weight: 500;
  color: rgb(var(--color-text-secondary));
  margin-bottom: 6px;
  letter-spacing: 0.01em;
}

.auth-required { color: rgb(var(--color-danger)); }

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

.auth-hint { font-size: 12px; margin: 4px 0 0; }
.auth-hint-ok { color: rgb(var(--color-success)); }

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

.auth-terms {
  margin: 20px 0 0;
  font-size: 12px;
  color: rgb(var(--color-text-tertiary));
  text-align: center;
  line-height: 1.5;
}

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
.auth-card--light .auth-link {
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
.auth-card--dark .auth-input::placeholder { color: #6B6B6F; }
.auth-card--dark .auth-input:focus {
  border-color: #818CF8;
  box-shadow: 0 0 0 2px rgba(129,140,248,0.15);
}
.auth-card--dark .auth-title { color: #EDEDED; }
.auth-card--dark .auth-subtitle { color: #6B6B6F; }
.auth-card--dark .auth-label { color: #A0A0A5; }
.auth-card--dark .auth-link { color: #818CF8; }
.auth-card--dark .auth-divider::before,
.auth-card--dark .auth-divider::after { background: rgba(255,255,255,0.06); }
.auth-card--dark .auth-terms { color: #6B6B6F; }

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
.auth-card--warm .auth-link { color: #D97706; }

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
.auth-card--pro .auth-link {
  color: #666;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 13px;
}
</style>
