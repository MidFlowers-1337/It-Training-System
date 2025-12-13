<template>
  <el-container class="admin-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="aside">
      <!-- Logo区域 -->
      <div class="logo-section">
        <img src="@/assets/logo.svg" alt="Logo" class="logo-img" />
        <transition name="fade">
          <span v-if="!isCollapse" class="logo-text">IT培训管理</span>
        </transition>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="admin-menu"
        router
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataBoard /></el-icon>
          <template #title>
            <span>控制台</span>
          </template>
        </el-menu-item>

        <el-sub-menu index="user-management">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/users">
            <span>用户列表</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="course-management">
          <template #title>
            <el-icon><Reading /></el-icon>
            <span>课程管理</span>
          </template>
          <el-menu-item index="/admin/courses">
            <span>课程列表</span>
          </el-menu-item>
          <el-menu-item index="/admin/sessions">
            <span>班期管理</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/admin/enrollments">
          <el-icon><Document /></el-icon>
          <template #title>
            <span>报名管理</span>
          </template>
        </el-menu-item>
      </el-menu>

      <!-- 底部折叠按钮 -->
      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon :size="18">
          <DArrowLeft v-if="!isCollapse" />
          <DArrowRight v-else />
        </el-icon>
      </div>
    </el-aside>

    <!-- 右侧区域 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRouteName">
              {{ currentRouteName }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 快捷操作 -->
          <el-tooltip content="返回前台" placement="bottom">
            <el-button :icon="Monitor" circle class="header-btn" @click="goToFrontend" />
          </el-tooltip>
          
          <el-tooltip content="刷新" placement="bottom">
            <el-button :icon="Refresh" circle class="header-btn" @click="refreshPage" />
          </el-tooltip>
          
          <el-tooltip content="全屏" placement="bottom">
            <el-button :icon="FullScreen" circle class="header-btn" @click="toggleFullscreen" />
          </el-tooltip>

          <!-- 通知 -->
          <el-badge :value="5" :max="99" class="notification-badge">
            <el-button :icon="Bell" circle class="header-btn" />
          </el-badge>

          <!-- 用户下拉 -->
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-dropdown">
              <el-avatar :size="36" class="user-avatar">
                {{ userStore.realName?.charAt(0) || 'A' }}
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{ userStore.realName || userStore.username }}</span>
                <span class="user-role">系统管理员</span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 标签页导航 -->
      <div class="tabs-nav">
        <el-tag
          v-for="tag in visitedTags"
          :key="tag.path"
          :closable="tag.path !== '/admin/dashboard'"
          :effect="activeMenu === tag.path ? 'dark' : 'plain'"
          class="nav-tag"
          @click="goToTag(tag)"
          @close="closeTag(tag)"
        >
          {{ tag.title }}
        </el-tag>
      </div>

      <!-- 主内容区 -->
      <el-main class="main">
        <div class="main-wrapper">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import {
  DataBoard, User, Reading, Calendar, Document,
  ArrowDown, HomeFilled, Monitor, Refresh, FullScreen, Bell, Setting, SwitchButton,
  DArrowLeft, DArrowRight
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const visitedTags = ref([
  { path: '/admin/dashboard', title: '控制台' }
])

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 当前路由名称
const currentRouteName = computed(() => {
  const nameMap = {
    '/admin/dashboard': '控制台',
    '/admin/users': '用户管理',
    '/admin/courses': '课程管理',
    '/admin/sessions': '班期管理',
    '/admin/enrollments': '报名管理'
  }
  return nameMap[route.path] || ''
})

// 监听路由变化，添加标签
watch(() => route.path, (newPath) => {
  if (newPath.startsWith('/admin/')) {
    const title = currentRouteName.value
    if (title && !visitedTags.value.find(tag => tag.path === newPath)) {
      visitedTags.value.push({ path: newPath, title })
    }
  }
}, { immediate: true })

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 跳转到标签
const goToTag = (tag) => {
  router.push(tag.path)
}

// 关闭标签
const closeTag = (tag) => {
  const index = visitedTags.value.findIndex(t => t.path === tag.path)
  if (index > -1) {
    visitedTags.value.splice(index, 1)
    if (activeMenu.value === tag.path) {
      const lastTag = visitedTags.value[visitedTags.value.length - 1]
      router.push(lastTag.path)
    }
  }
}

// 返回前台
const goToFrontend = () => {
  router.push('/home')
}

// 刷新页面
const refreshPage = () => {
  location.reload()
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  } else if (command === 'settings') {
    ElMessage.info('系统设置功能开发中')
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background: var(--bg-secondary);
}

/* 侧边栏 */
.aside {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  position: relative;
  overflow: hidden;
}

/* Logo区域 */
.logo-section {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-img {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.logo-text {
  margin-left: 12px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  white-space: nowrap;
}

/* 菜单样式 */
.admin-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding: 12px 0;
}

.admin-menu :deep(.el-menu-item),
.admin-menu :deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  margin: 4px 12px;
  border-radius: var(--radius-md);
  color: rgba(255, 255, 255, 0.7);
  transition: all var(--transition-fast);
}

.admin-menu :deep(.el-menu-item:hover),
.admin-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: var(--primary-color);
  color: white;
}

.admin-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 52px !important;
  margin: 2px 12px;
  height: 42px;
  line-height: 42px;
}

.admin-menu :deep(.el-menu-item .el-icon),
.admin-menu :deep(.el-sub-menu__title .el-icon) {
  color: inherit;
}

/* 折叠按钮 */
.collapse-btn {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.5);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  transition: all var(--transition-fast);
}

.collapse-btn:hover {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

/* 主容器 */
.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航 */
.header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--bg-primary);
  padding: 0 24px;
  box-shadow: var(--shadow-sm);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-left :deep(.el-breadcrumb) {
  font-size: 14px;
}

.header-left :deep(.el-breadcrumb__item) {
  display: flex;
  align-items: center;
}

.header-left :deep(.el-breadcrumb__inner) {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-btn {
  border: none;
  background: var(--bg-tertiary);
  color: var(--text-secondary);
}

.header-btn:hover {
  background: var(--primary-bg);
  color: var(--primary-color);
}

.notification-badge :deep(.el-badge__content) {
  background: var(--danger-color);
}

/* 用户下拉 */
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius-lg);
  margin-left: 8px;
  transition: background var(--transition-fast);
}

.user-dropdown:hover {
  background: var(--bg-tertiary);
}

.user-avatar {
  background: var(--gradient-primary);
  color: white;
  font-weight: 600;
}

.user-info {
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

.dropdown-icon {
  color: var(--text-muted);
  font-size: 12px;
}

/* 标签页导航 */
.tabs-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 24px;
  background: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
  overflow-x: auto;
}

.nav-tag {
  cursor: pointer;
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.nav-tag:hover {
  background: var(--primary-bg);
}

/* 主内容区 */
.main {
  flex: 1;
  padding: 0;
  overflow-y: auto;
  background: var(--bg-secondary);
}

.main-wrapper {
  padding: 24px;
  min-height: 100%;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s ease;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* 文字淡入淡出 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .user-info {
    display: none;
  }
  
  .tabs-nav {
    padding: 8px 16px;
  }
  
  .main-wrapper {
    padding: 16px;
  }
}
</style>
