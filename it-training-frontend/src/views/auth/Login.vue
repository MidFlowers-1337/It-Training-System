<template>
  <AuthLayout
    title="欢迎回来"
    subtitle="登录以继续你的学习之旅"
    footer-text="还没有账号？"
    footer-link-text="立即注册"
    footer-link-to="/register"
  >
    <FormLayout>
      <FormItem label="用户名" required :error="errors.username">
        <Input
          v-model="loginForm.username"
          placeholder="请输入用户名"
          :error="!!errors.username"
          @blur="validateField('username')"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <FormItem label="密码" required :error="errors.password">
        <Input
          v-model="loginForm.password"
          type="password"
          placeholder="请输入密码"
          :error="!!errors.password"
          @blur="validateField('password')"
          @keyup.enter="handleLogin"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <div class="forgot-link-row">
        <a href="#" class="forgot-link">忘记密码？</a>
      </div>

      <Button
        variant="primary"
        block
        :loading="loading"
        @click="handleLogin"
      >
        登 录
      </Button>
    </FormLayout>

    <!-- 演示账号区域 -->
    <template #extra>
      <div class="demo-section">
        <p class="demo-label">演示账号</p>
        <div class="demo-buttons">
          <button
            v-for="(label, type) in demoAccounts"
            :key="type"
            type="button"
            class="demo-btn"
            @click="fillDemo(type)"
          >
            {{ label }}
          </button>
        </div>
      </div>
    </template>
  </AuthLayout>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import { AuthLayout, FormLayout, FormItem, Input, Button } from '@/design-system';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
});

// 错误信息
const errors = reactive({
  username: '',
  password: '',
});

// 演示账号
const demoAccounts: Record<string, string> = {
  admin: '管理员',
  teacher1: '讲师',
  student1: '学员',
};

// 填充演示账号
const fillDemo = (type: string) => {
  loginForm.username = type;
  loginForm.password = '123456';
  // 清除错误
  errors.username = '';
  errors.password = '';
};

// 验证单个字段
const validateField = (field: 'username' | 'password') => {
  if (field === 'username') {
    errors.username = loginForm.username ? '' : '请输入用户名';
  } else if (field === 'password') {
    errors.password = loginForm.password ? '' : '请输入密码';
  }
};

// 验证表单
const validateForm = (): boolean => {
  validateField('username');
  validateField('password');
  return !errors.username && !errors.password;
};

// 登录处理
const handleLogin = async () => {
  if (!validateForm()) return;

  loading.value = true;
  try {
    await userStore.login(loginForm);

    if (userStore.isAdmin) {
      router.push('/admin/dashboard');
    } else {
      router.push('/home');
    }
  } catch (error: any) {
    // 显示错误信息在表单下方
    errors.password = error?.message || '登录失败，请重试';
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.forgot-link-row {
  display: flex;
  justify-content: flex-end;
}

.forgot-link {
  font-size: 13px;
  color: var(--primary-color);
  text-decoration: none;
  transition: opacity 0.2s ease;
}

.forgot-link:hover {
  opacity: 0.8;
}

/* ===== 演示账号区域 ===== */
.demo-section {
  margin-top: 32px;
  text-align: center;
}

.demo-label {
  font-size: 12px;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 12px;
}

.demo-buttons {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
}

.demo-btn {
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  background: var(--bg-tertiary);
  border: 0.5px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.demo-btn:hover {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  color: var(--primary-color);
  border-color: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.2);
}

/* ===== 深色模式 ===== */
[data-theme="dark"] .demo-btn {
  background: var(--bg-tertiary);
  border-color: rgba(255, 255, 255, 0.08);
}

[data-theme="dark"] .demo-btn:hover {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.15);
}
</style>
