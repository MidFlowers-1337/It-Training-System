<template>
  <div class="min-h-screen bg-bg-primary flex items-center justify-center px-4 py-10 transition-colors duration-300">
    <div class="relative w-full max-w-md">
      <div class="absolute inset-0 -z-10 blur-3xl opacity-60" style="background: var(--gradient-hero)"></div>

      <div class="glass rounded-3xl border border-border-color/60 shadow-lg p-8 md:p-10">
        <div class="flex flex-col items-center text-center">
          <div class="w-14 h-14 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center">
            <img src="@/assets/logo.svg" alt="Logo" class="w-8 h-8" />
          </div>
          <h1 class="mt-6 text-2xl font-semibold tracking-tight text-text-primary">Welcome Back</h1>
          <p class="mt-2 text-sm text-text-secondary">Sign in to continue your learning journey</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          label-position="top"
          class="mt-8 space-y-5"
          @submit.prevent
        >
          <el-form-item label="Username" prop="username">
            <el-input v-model="loginForm.username" placeholder="Enter your username" size="large">
              <template #prefix>
                <el-icon class="text-text-muted"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Password" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="Enter your password"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <el-icon class="text-text-muted"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="flex items-center justify-end">
            <a href="#" class="text-sm text-primary hover:text-primary-light transition-colors">Forgot password?</a>
          </div>

          <el-button type="primary" size="large" class="w-full !h-12 !text-base !font-bold" :loading="loading" @click="handleLogin">
            Sign In
          </el-button>
        </el-form>

        <div class="mt-6 text-center text-sm text-text-secondary">
          Don't have an account?
          <router-link to="/register" class="text-primary hover:text-primary-light font-medium ml-1 transition-colors">
            Sign up
          </router-link>
        </div>
      </div>

      <div class="mt-8 text-center">
        <p class="text-xs text-text-muted uppercase tracking-wider mb-3">Demo Accounts</p>
        <div class="flex justify-center flex-wrap gap-2">
          <button
            v-for="(label, type) in demoAccounts"
            :key="type"
            type="button"
            @click="fillDemo(type)"
            class="badge badge-secondary hover:bg-primary/10 hover:text-primary transition-colors"
          >
            {{ label }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
})

const demoAccounts = {
  admin: 'Admin',
  teacher1: 'Instructor',
  student1: 'Student',
}

const rules = {
  username: [{ required: true, message: 'Please enter username', trigger: 'blur' }],
  password: [{ required: true, message: 'Please enter password', trigger: 'blur' }],
}

const fillDemo = (type) => {
  loginForm.username = type
  loginForm.password = '123456'
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('Welcome back!')

        if (userStore.isAdmin) {
          router.push('/admin/dashboard')
        } else {
          router.push('/home')
        }
      } catch (error) {
        console.error('Login failed:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>
