<template>
  <AuthLayout
    title="Create Account"
    subtitle="Join our community and start learning today"
    footer-text="Already have an account?"
    footer-link-text="Sign in"
    footer-link-to="/login"
  >
    <FormLayout>
      <FormItem label="Username" required :error="errors.username">
        <Input
          v-model="registerForm.username"
          placeholder="4-20 characters"
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

      <FormItem label="Full Name" required :error="errors.realName">
        <Input
          v-model="registerForm.realName"
          placeholder="Your real name"
          :error="!!errors.realName"
          @blur="validateField('realName')"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" fill="currentColor" fill-opacity="0.2" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <FormItem label="Phone Number" :error="errors.phone">
        <Input
          v-model="registerForm.phone"
          type="tel"
          placeholder="Mobile number"
          :error="!!errors.phone"
          @blur="validateField('phone')"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <FormItem label="Email (Optional)" :error="errors.email">
        <Input
          v-model="registerForm.email"
          type="email"
          placeholder="email@example.com"
          :error="!!errors.email"
          @blur="validateField('email')"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
              <polyline points="22,6 12,13 2,6" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <FormItem label="Password" required :error="errors.password">
        <Input
          v-model="registerForm.password"
          type="password"
          placeholder="6-20 characters"
          :error="!!errors.password"
          @blur="validateField('password')"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <FormItem label="Confirm Password" required :error="errors.confirmPassword">
        <Input
          v-model="registerForm.confirmPassword"
          type="password"
          placeholder="Repeat password"
          :error="!!errors.confirmPassword"
          @blur="validateField('confirmPassword')"
          @keyup.enter="handleRegister"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
          </template>
        </Input>
      </FormItem>

      <Button
        variant="primary"
        block
        :loading="loading"
        @click="handleRegister"
      >
        Create Account
      </Button>
    </FormLayout>
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
const registerForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
});

// 错误信息
const errors = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
});

// 验证规则
const validators = {
  username: (value: string): string => {
    if (!value) return 'Please enter username';
    if (value.length < 4 || value.length > 20) return 'Length should be 4 to 20';
    if (!/^[a-zA-Z0-9_]+$/.test(value)) return 'Letters, numbers and underscore only';
    return '';
  },
  realName: (value: string): string => {
    if (!value) return 'Please enter your real name';
    return '';
  },
  phone: (value: string): string => {
    if (value && !/^1[3-9]\d{9}$/.test(value)) return 'Invalid phone number';
    return '';
  },
  email: (value: string): string => {
    if (value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) return 'Invalid email address';
    return '';
  },
  password: (value: string): string => {
    if (!value) return 'Please enter password';
    if (value.length < 6 || value.length > 20) return 'Length should be 6 to 20';
    return '';
  },
  confirmPassword: (value: string): string => {
    if (!value) return 'Please confirm password';
    if (value !== registerForm.password) return 'Passwords do not match';
    return '';
  },
};

// 验证单个字段
type FieldName = keyof typeof validators;
const validateField = (field: FieldName) => {
  errors[field] = validators[field](registerForm[field]);
};

// 验证表单
const validateForm = (): boolean => {
  (Object.keys(validators) as FieldName[]).forEach(validateField);
  return !Object.values(errors).some(Boolean);
};

// 注册处理
const handleRegister = async () => {
  if (!validateForm()) return;

  loading.value = true;
  try {
    await userStore.register(registerForm);
    router.push('/home');
  } catch (error: any) {
    // 显示错误信息
    errors.confirmPassword = error?.message || 'Registration failed, please try again';
  } finally {
    loading.value = false;
  }
};
</script>
