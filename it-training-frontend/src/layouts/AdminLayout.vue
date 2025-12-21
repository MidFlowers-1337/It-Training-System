<template>
  <div class="min-h-screen bg-bg-primary flex">
    <!-- 侧边栏 -->
    <aside 
      class="bg-bg-secondary/70 backdrop-blur-xl border-r border-border-color/60 flex flex-col transition-all duration-300"
      :class="isCollapse ? 'w-16' : 'w-64'"
    >
      <!-- Logo -->
      <div class="h-16 flex items-center justify-center border-b border-border-color">
        <div class="w-8 h-8 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-bold text-base">
          IT
        </div>
        <span v-if="!isCollapse" class="ml-3 font-bold text-text-primary text-lg">管理后台</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="flex-1 py-4 overflow-y-auto">
        <ul class="space-y-1 px-2">
          <li v-for="item in menuItems" :key="item.path">
            <router-link 
              :to="item.path"
              class="flex items-center px-3 py-2.5 rounded-xl text-text-secondary hover:text-text-primary hover:bg-bg-tertiary/60 transition-all border border-transparent"
              active-class="bg-primary/10 text-primary border-primary/20"
            >
              <el-icon :size="20" class="flex-shrink-0"><component :is="item.icon" /></el-icon>
              <span v-if="!isCollapse" class="ml-3 font-medium">{{ item.title }}</span>
            </router-link>
          </li>
        </ul>
      </nav>

      <!-- 底部折叠按钮 -->
      <button 
        @click="isCollapse = !isCollapse"
        class="h-12 flex items-center justify-center border-t border-border-color text-text-secondary hover:text-text-primary hover:bg-bg-tertiary transition-colors"
      >
        <el-icon><component :is="isCollapse ? 'Expand' : 'Fold'" /></el-icon>
      </button>
    </aside>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col min-w-0">
      <!-- 顶部导航 -->
      <header class="h-16 bg-bg-secondary/70 backdrop-blur-xl border-b border-border-color/60 shadow-sm flex items-center justify-between px-6 sticky top-0 z-40">
        <div class="flex items-center text-text-secondary text-sm">
          <span class="text-text-muted">当前位置：</span>
          <span class="text-text-primary ml-2 font-medium">{{ currentRouteName }}</span>
        </div>

        <div class="flex items-center gap-4">
          <ThemeSwitcher />
          
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="flex items-center gap-3 cursor-pointer hover:bg-bg-tertiary/60 px-3 py-1.5 rounded-xl transition-colors">
              <div class="w-8 h-8 rounded-full bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-semibold text-sm">
                {{ userStore.realName?.charAt(0) || 'A' }}
              </div>
              <div class="hidden md:block text-left">
                <div class="text-sm font-medium text-text-primary">{{ userStore.realName || '管理员' }}</div>
                <div class="text-xs text-text-muted">系统管理员</div>
              </div>
              <el-icon class="text-text-muted"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="frontend">返回前台</el-dropdown-item>
                <el-dropdown-item command="logout" divided class="text-error">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto p-6">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import {
  DataBoard, User, Reading, Calendar, Document,
  ArrowDown, Expand, Fold
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const menuItems = [
  { path: '/admin/dashboard', title: '控制台', icon: DataBoard },
  { path: '/admin/users', title: '用户管理', icon: User },
  { path: '/admin/courses', title: '课程管理', icon: Reading },
  { path: '/admin/sessions', title: '班期管理', icon: Calendar },
  { path: '/admin/enrollments', title: '报名管理', icon: Document },
]

const currentRouteName = computed(() => {
  const item = menuItems.find(i => i.path === route.path)
  return item ? item.title : '控制台'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'frontend') {
    router.push('/home')
  }
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
