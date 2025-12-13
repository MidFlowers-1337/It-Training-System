<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-logo animate-float">
            <img src="@/assets/logo.svg" alt="Logo" class="logo-img" />
          </div>
          <h1 class="brand-title">IT技能培训智能选课系统</h1>
          <p class="brand-subtitle">基于AI的个性化学习路径推荐</p>
          
          <div class="features">
            <div class="feature-item">
              <div class="feature-icon">🎯</div>
              <div class="feature-text">
                <h4>智能推荐</h4>
                <p>AI分析学习目标，精准推荐课程</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">📚</div>
              <div class="feature-text">
                <h4>丰富课程</h4>
                <p>涵盖前端、后端、AI等热门方向</p>
              </div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🚀</div>
              <div class="feature-text">
                <h4>高效学习</h4>
                <p>科学规划学习路径，快速提升技能</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-container animate-scaleIn">
          <div class="form-header">
            <h2>欢迎回来</h2>
            <p>请登录您的账号继续学习之旅</p>
          </div>

          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <el-icon class="input-icon"><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                show-password
                class="custom-input"
                @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <el-icon class="input-icon"><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div class="form-options">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码？</a>
            </div>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                :loading="loading"
                @click="handleLogin"
              >
                <span v-if="!loading">登 录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-footer">
            <span class="divider-text">还没有账号？</span>
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>

          <!-- 演示账号提示 -->
          <div class="demo-accounts">
            <p class="demo-title">演示账号</p>
            <div class="demo-list">
              <span class="demo-item" @click="fillDemo('admin')">管理员</span>
              <span class="demo-item" @click="fillDemo('teacher1')">讲师</span>
              <span class="demo-item" @click="fillDemo('student1')">学员</span>
            </div>
          </div>
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
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

// 填充演示账号
const fillDemo = (type) => {
  loginForm.username = type
  loginForm.password = '123456'
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功，欢迎回来！')

    // 根据角色跳转到不同页面
    if (userStore.isAdmin) {
      router.push('/admin/dashboard')
    } else {
      router.push('/home')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 50%, #4c1d95 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
}

.circle-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  left: -100px;
  animation: float 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 30%;
  animation: float 10s ease-in-out infinite;
}

/* 主容器 */
.login-container {
  display: flex;
  width: 100%;
  max-width: 1100px;
  min-height: 600px;
  margin: 20px;
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-xl);
  position: relative;
  z-index: 1;
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.9) 0%, rgba(139, 92, 246, 0.9) 100%);
  padding: 60px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
}

.brand-content {
  max-width: 400px;
}

.brand-logo {
  width: 80px;
  height: 80px;
  margin-bottom: 24px;
}

.logo-img {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.2));
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  color: white;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
  color: rgba(255, 255, 255, 0.9);
}

.features {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.feature-icon {
  font-size: 32px;
  line-height: 1;
}

.feature-text h4 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  color: white;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.85;
  margin: 0;
  color: rgba(255, 255, 255, 0.85);
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  background: white;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-container {
  width: 100%;
  max-width: 360px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* 表单样式 */
.login-form {
  margin-bottom: 24px;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 4px 16px;
  border-radius: var(--radius-lg);
  box-shadow: none;
  border: 2px solid var(--border-color);
  transition: all var(--transition-fast);
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-light);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.input-icon {
  font-size: 18px;
  color: var(--text-muted);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.forgot-link {
  font-size: 14px;
  color: var(--primary-color);
}

.forgot-link:hover {
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-lg);
  background: var(--gradient-primary);
  border: none;
  transition: all var(--transition-normal);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(79, 70, 229, 0.35);
}

.login-btn:active {
  transform: translateY(0);
}

/* 表单底部 */
.form-footer {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
}

.divider-text {
  color: var(--text-secondary);
  font-size: 14px;
}

.register-link {
  color: var(--primary-color);
  font-weight: 600;
  margin-left: 8px;
}

.register-link:hover {
  text-decoration: underline;
}

/* 演示账号 */
.demo-accounts {
  margin-top: 24px;
  padding: 16px;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  text-align: center;
}

.demo-title {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 8px;
}

.demo-list {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.demo-item {
  font-size: 13px;
  color: var(--primary-color);
  cursor: pointer;
  padding: 4px 12px;
  border-radius: var(--radius-full);
  background: white;
  border: 1px solid var(--border-color);
  transition: all var(--transition-fast);
}

.demo-item:hover {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

/* 动画 */
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

/* 响应式 */
@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
    max-width: 450px;
  }
  
  .brand-section {
    padding: 40px 30px;
  }
  
  .features {
    display: none;
  }
  
  .form-section {
    padding: 40px 30px;
  }
}

@media (max-width: 480px) {
  .login-container {
    margin: 10px;
    border-radius: var(--radius-lg);
  }
  
  .brand-section {
    padding: 30px 20px;
  }
  
  .brand-title {
    font-size: 22px;
  }
  
  .form-section {
    padding: 30px 20px;
  }
}
</style>
