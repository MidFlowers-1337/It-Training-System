<template>
  <div class="student-layout">
    <el-container>
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-content">
          <div class="header-left">
            <!-- Logo -->
            <div class="logo" @click="$router.push('/home')">
              <img src="@/assets/logo.svg" alt="Logo" class="logo-img" />
              <span class="logo-text">IT智能选课</span>
            </div>
            
            <!-- 导航菜单 -->
            <el-menu
              :default-active="activeMenu"
              mode="horizontal"
              :ellipsis="false"
              router
              class="nav-menu"
            >
              <el-menu-item index="/home">
                <el-icon><HomeFilled /></el-icon>
                <span>首页</span>
              </el-menu-item>
              <el-menu-item index="/courses">
                <el-icon><Reading /></el-icon>
                <span>课程中心</span>
              </el-menu-item>
              <el-menu-item index="/recommend" class="ai-menu-item">
                <el-icon><MagicStick /></el-icon>
                <span>AI智能选课</span>
                <span class="ai-badge">AI</span>
              </el-menu-item>
              <el-menu-item index="/my-courses">
                <el-icon><Collection /></el-icon>
                <span>我的课程</span>
              </el-menu-item>
              <el-menu-item index="/learning">
                <el-icon><TrendCharts /></el-icon>
                <span>学习中心</span>
              </el-menu-item>
              <el-menu-item index="/profile">
                <el-icon><UserFilled /></el-icon>
                <span>我的画像</span>
              </el-menu-item>
            </el-menu>
          </div>
          
          <div class="header-right">
            <!-- 搜索框 -->
            <div class="search-box">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索课程..."
                size="default"
                class="search-input"
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
            
            <!-- 通知 -->
            <el-badge :value="3" :max="99" class="notification-badge">
              <el-button :icon="Bell" circle class="icon-btn" />
            </el-badge>
            
            <!-- 用户下拉 -->
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-info">
                <el-avatar :size="36" class="user-avatar">
                  {{ userInfo?.realName?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="user-details">
                  <span class="user-name">{{ userInfo?.realName || '用户' }}</span>
                  <span class="user-role">{{ getRoleName(userInfo?.role) }}</span>
                </div>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <div class="dropdown-header">
                    <el-avatar :size="48" class="dropdown-avatar">
                      {{ userInfo?.realName?.charAt(0) || 'U' }}
                    </el-avatar>
                    <div class="dropdown-user-info">
                      <span class="dropdown-name">{{ userInfo?.realName }}</span>
                      <span class="dropdown-username">@{{ userInfo?.username }}</span>
                    </div>
                  </div>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    账号设置
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userInfo?.role === 'INSTRUCTOR'" command="instructor" divided>
                    <el-icon><OfficeBuilding /></el-icon>
                    讲师工作台
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userInfo?.role === 'ADMIN'" command="admin" divided>
                    <el-icon><Monitor /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <div class="content-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade-slide" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer">
        <div class="footer-content">
          <div class="footer-left">
            <span>© 2025 IT技能培训智能选课系统</span>
            <span class="divider">|</span>
            <span>毕业设计项目</span>
          </div>
          <div class="footer-right">
            <a href="#">关于我们</a>
            <a href="#">使用帮助</a>
            <a href="#">联系客服</a>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowDown, User, SwitchButton, MagicStick, OfficeBuilding, Setting,
  HomeFilled, Reading, Collection, Search, Bell, Monitor, TrendCharts, UserFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const userInfo = computed(() => userStore.userInfo)

const activeMenu = computed(() => route.path)

const getRoleName = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'INSTRUCTOR': '讲师',
    'STUDENT': '学员'
  }
  return roleMap[role] || '用户'
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/courses', query: { keyword: searchKeyword.value } })
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      userStore.logout()
      ElMessage.success('退出成功')
      router.push('/login')
      break
    case 'instructor':
      router.push('/instructor/sessions')
      break
    case 'admin':
      router.push('/admin/dashboard')
      break
    case 'profile':
      router.push('/personal-center')
      break
    case 'settings':
      router.push('/settings')
      break
  }
}
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  background: var(--bg-secondary);
}

/* 顶部导航 */
.header {
  height: 64px;
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: opacity var(--transition-fast);
}

.logo:hover {
  opacity: 0.8;
}

.logo-img {
  width: 36px;
  height: 36px;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 导航菜单 */
.nav-menu {
  border-bottom: none;
  background: transparent;
}

.nav-menu :deep(.el-menu-item) {
  font-size: 15px;
  height: 64px;
  line-height: 64px;
  padding: 0 20px;
  color: var(--text-secondary);
  border-bottom: 3px solid transparent;
  transition: all var(--transition-fast);
}

.nav-menu :deep(.el-menu-item:hover) {
  background: transparent;
  color: var(--primary-color);
}

.nav-menu :deep(.el-menu-item.is-active) {
  font-weight: 600;
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
  background: transparent;
}

.nav-menu :deep(.el-menu-item .el-icon) {
  margin-right: 6px;
}

/* AI菜单项特殊样式 */
.ai-menu-item :deep(.el-menu-item) {
  position: relative;
}

.nav-menu :deep(.ai-menu-item) {
  position: relative;
}

.ai-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 6px;
  font-size: 10px;
  padding: 2px 6px;
  background: var(--gradient-primary);
  color: white;
  border-radius: var(--radius-full);
  font-weight: 600;
  line-height: 1;
  vertical-align: middle;
}

/* 右侧区域 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 搜索框 */
.search-box {
  width: 220px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: var(--radius-full);
  background: var(--bg-tertiary);
  box-shadow: none;
  border: none;
}

.search-input :deep(.el-input__wrapper:hover),
.search-input :deep(.el-input__wrapper.is-focus) {
  background: var(--bg-primary);
  box-shadow: var(--shadow-sm);
}

/* 图标按钮 */
.icon-btn {
  border: none;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

.icon-btn:hover {
  background: var(--primary-bg);
  color: var(--primary-color);
}

.notification-badge :deep(.el-badge__content) {
  background: var(--danger-color);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius-lg);
  transition: background var(--transition-fast);
}

.user-info:hover {
  background: var(--bg-tertiary);
}

.user-avatar {
  background: var(--gradient-primary);
  color: white;
  font-weight: 600;
}

.user-details {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 12px;
  color: var(--text-muted);
}

.dropdown-arrow {
  color: var(--text-muted);
  font-size: 12px;
}

/* 下拉菜单头部 */
.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 8px;
}

.dropdown-avatar {
  background: var(--gradient-primary);
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
}

.dropdown-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.dropdown-username {
  font-size: 13px;
  color: var(--text-muted);
}

/* 主内容区 */
.main-content {
  min-height: calc(100vh - 124px);
  padding: 0;
  background: var(--bg-secondary);
}

.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

/* 页面切换动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 底部 */
.footer {
  height: 60px;
  background: var(--bg-primary);
  border-top: 1px solid var(--border-color);
  padding: 0;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--text-muted);
}

.footer-left .divider {
  color: var(--border-color);
}

.footer-right {
  display: flex;
  gap: 24px;
}

.footer-right a {
  font-size: 13px;
  color: var(--text-secondary);
  transition: color var(--transition-fast);
}

.footer-right a:hover {
  color: var(--primary-color);
}

/* 响应式 */
@media (max-width: 1024px) {
  .search-box {
    display: none;
  }
  
  .user-details {
    display: none;
  }
}

@media (max-width: 768px) {
  .header-left {
    gap: 20px;
  }
  
  .logo-text {
    display: none;
  }
  
  .nav-menu :deep(.el-menu-item) {
    padding: 0 12px;
  }
  
  .nav-menu :deep(.el-menu-item span) {
    display: none;
  }
  
  .ai-badge {
    display: none;
  }
  
  .content-wrapper {
    padding: 16px;
  }
  
  .footer-right {
    display: none;
  }
}
</style>
