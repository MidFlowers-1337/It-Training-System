<template>
  <AuthLayout
    title="Welcome Back"
    subtitle="Sign in to continue your learning journey"
    footer-text="Don't have an account?"
    footer-link-text="Sign up"
    footer-link-to="/register"
  >
    <FormLayout>
      <FormItem label="Username" required :error="errors.username">
        <Input
          v-model="loginForm.username"
          placeholder="Enter your username"
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

      <FormItem label="Password" required :error="errors.password">
        <Input
          v-model="loginForm.password"
          type="password"
          placeholder="Enter your password"
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

      <div class="flex items-center justify-end">
        <a href="#" class="text-sm text-primary hover:text-primary-light transition-colors">
          Forgot password?
        </a>
      </div>

      <Button
        variant="primary"
        block
        :loading="loading"
        @click="handleLogin"
      >
        Sign In
      </Button>
    </FormLayout>

    <!-- 演示账号区域 -->
    <template #extra>
      <div class="mt-8 text-center">
        <p class="text-xs text-text-muted uppercase tracking-wider mb-3">Demo Accounts</p>
        <div class="flex justify-center flex-wrap gap-2">
          <button
            v-for="(label, type) in demoAccounts"
            :key="type"
            type="button"
            class="px-3 py-1.5 text-sm rounded-lg bg-bg-tertiary text-text-secondary hover:bg-primary/10 hover:text-primary transition-colors"
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
  admin: 'Admin',
  teacher1: 'Instructor',
  student1: 'Student',
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
    errors.username = loginForm.username ? '' : 'Please enter username';
  } else if (field === 'password') {
    errors.password = loginForm.password ? '' : 'Please enter password';
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
    errors.password = error?.message || 'Login failed, please try again';
  } finally {
    loading.value = false;
  }
};
</script>
