<template>
  <div class="min-h-screen bg-bg-primary flex items-center justify-center px-4 py-10 transition-colors duration-300">
    <div class="relative w-full max-w-md">
      <div class="absolute inset-0 -z-10 blur-3xl opacity-60" style="background: var(--gradient-hero)"></div>

      <div class="glass rounded-3xl border border-border-color/60 shadow-lg p-8 md:p-10">
        <div class="flex flex-col items-center text-center">
          <div class="w-14 h-14 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center">
            <img src="@/assets/logo.svg" alt="Logo" class="w-8 h-8" />
          </div>
          <h1 class="mt-6 text-2xl font-semibold tracking-tight text-text-primary">Create Account</h1>
          <p class="mt-2 text-sm text-text-secondary">Join our community and start learning today</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="rules"
          label-position="top"
          class="mt-8 space-y-5"
          @submit.prevent
        >
          <el-form-item label="Username" prop="username">
            <el-input v-model="registerForm.username" placeholder="4-20 characters" size="large">
              <template #prefix>
                <el-icon class="text-text-muted"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Full Name" prop="realName">
            <el-input v-model="registerForm.realName" placeholder="Your real name" size="large">
              <template #prefix>
                <el-icon class="text-text-muted"><UserFilled /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Phone Number" prop="phone">
            <el-input v-model="registerForm.phone" placeholder="Mobile number" size="large">
              <template #prefix>
                <el-icon class="text-text-muted"><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Email (Optional)" prop="email">
            <el-input v-model="registerForm.email" placeholder="email@example.com" size="large">
              <template #prefix>
                <el-icon class="text-text-muted"><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Password" prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="6-20 characters" size="large" show-password>
              <template #prefix>
                <el-icon class="text-text-muted"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Confirm Password" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="Repeat password"
              size="large"
              show-password
              @keyup.enter="handleRegister"
            >
              <template #prefix>
                <el-icon class="text-text-muted"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-button type="primary" size="large" class="w-full !h-12 !text-base !font-bold mt-2" :loading="loading" @click="handleRegister">
            Create Account
          </el-button>
        </el-form>

        <div class="mt-6 text-center text-sm text-text-secondary">
          Already have an account?
          <router-link to="/login" class="text-primary hover:text-primary-light font-medium ml-1 transition-colors">
            Sign in
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, UserFilled, Phone, Message, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('Passwords do not match'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: 'Please enter username', trigger: 'blur' },
    { min: 4, max: 20, message: 'Length should be 4 to 20', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: 'Letters, numbers and underscore only', trigger: 'blur' },
  ],
  realName: [{ required: true, message: 'Please enter your real name', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\\d{9}$/, message: 'Invalid phone number', trigger: 'blur' }],
  email: [{ type: 'email', message: 'Invalid email address', trigger: 'blur' }],
  password: [
    { required: true, message: 'Please enter password', trigger: 'blur' },
    { min: 6, max: 20, message: 'Length should be 6 to 20', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: 'Please confirm password', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.register(registerForm)
        ElMessage.success('Registration successful')
        router.push('/home')
      } catch (error) {
        console.error('Registration failed:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>
