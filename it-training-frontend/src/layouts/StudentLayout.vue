<template>
  <div class="min-h-screen bg-bg-primary text-text-primary font-sans transition-colors duration-300">
    <!-- 顶部导航栏 -->
    <header class="fixed top-0 left-0 right-0 h-16 z-50 px-4 md:px-8 flex items-center justify-between bg-bg-secondary/70 backdrop-blur-xl border-b border-border-color/60 shadow-sm">
      <!-- 左侧：Logo -->
      <div class="flex items-center gap-4">
        <router-link to="/home" class="flex items-center gap-3 group">
          <div class="w-8 h-8 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-bold text-base transition-colors group-hover:bg-primary/15">
            IT
          </div>
          <span class="text-lg font-bold tracking-tight hidden md:block text-text-primary group-hover:text-primary transition-colors">
            IT Training
          </span>
        </router-link>
        
        <!-- 桌面端主导航 -->
        <nav class="hidden md:flex items-center ml-8 gap-1">
          <router-link 
            v-for="item in navItems" 
            :key="item.path" 
            :to="item.path"
            class="px-4 py-2 rounded-full text-sm font-medium text-text-secondary hover:text-text-primary hover:bg-bg-tertiary/60 transition-all"
            active-class="text-text-primary bg-bg-tertiary/70 font-semibold"
          >
            {{ item.name }}
          </router-link>
        </nav>
      </div>

      <!-- 右侧：搜索 + 用户 -->
      <div class="flex items-center gap-4">
        <!-- 搜索框 -->
        <div class="relative hidden md:block w-64 group">
          <input 
            type="text" 
            v-model="searchKeyword"
            @keyup.enter="handleSearch"
            placeholder="搜索课程、路径..." 
            class="w-full bg-bg-tertiary/60 border border-border-color/60 rounded-full py-1.5 pl-10 pr-4 text-sm text-text-primary focus:outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/15 transition-all placeholder-text-muted group-hover:border-border-color"
          />
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-text-muted group-hover:text-text-secondary transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
        </div>

        <!-- 主题切换 -->
        <ThemeSwitcher />

        <!-- 用户菜单 -->
        <div class="relative" ref="userMenuRef">
          <button 
            @click="toggleUserMenu"
            class="flex items-center gap-2 focus:outline-none"
          >
            <div class="w-8 h-8 rounded-full bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-semibold text-sm transition-all hover:bg-primary/15">
              {{ userInitials }}
            </div>
          </button>

          <!-- 下拉菜单 -->
          <transition
            enter-active-class="transition ease-out duration-100"
            enter-from-class="transform opacity-0 scale-95"
            enter-to-class="transform opacity-100 scale-100"
            leave-active-class="transition ease-in duration-75"
            leave-from-class="transform opacity-100 scale-100"
            leave-to-class="transform opacity-0 scale-95"
          >
            <div v-if="showUserMenu" class="absolute right-0 mt-2 w-56 bg-bg-secondary/90 backdrop-blur-xl border border-border-color/60 rounded-2xl shadow-xl py-1 z-50">
              <div class="px-4 py-3 border-b border-border-color">
                <p class="text-sm font-medium text-text-primary">{{ userInfo?.realName || '用户' }}</p>
                <p class="text-xs text-text-muted truncate">{{ userInfo?.username }}</p>
              </div>
              
              <div class="py-1">
                <router-link to="/profile" class="block px-4 py-2 text-sm text-text-secondary hover:bg-bg-tertiary hover:text-text-primary">
                  个人资料
                </router-link>
                <router-link to="/my-courses" class="block px-4 py-2 text-sm text-text-secondary hover:bg-bg-tertiary hover:text-text-primary">
                  我的学习
                </router-link>
                <router-link v-if="userInfo?.role === 'ADMIN'" to="/admin/dashboard" class="block px-4 py-2 text-sm text-primary hover:bg-bg-tertiary">
                  管理后台
                </router-link>
              </div>
              
              <div class="border-t border-border-color py-1">
                <button @click="handleLogout" class="block w-full text-left px-4 py-2 text-sm text-error hover:bg-bg-tertiary">
                  退出登录
                </button>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </header>

    <!-- 主内容区域 -->
    <main class="pt-16 min-h-[calc(100vh-64px)]">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <FloatingActionButton />

    <!-- 极简页脚 -->
    <footer class="bg-bg-secondary/70 backdrop-blur-xl border-t border-border-color/60 py-12 mt-12">
      <div class="max-w-7xl mx-auto px-4 md:px-8 flex flex-col md:flex-row justify-between items-center gap-6">
        <div class="flex items-center gap-2 text-text-muted text-sm">
          <span class="font-bold text-text-secondary">IT Training</span>
          <span>&copy; 2025</span>
        </div>
        <div class="flex gap-6 text-sm text-text-muted">
          <a href="#" class="hover:text-text-primary transition-colors">关于我们</a>
          <a href="#" class="hover:text-text-primary transition-colors">课程</a>
          <a href="#" class="hover:text-text-primary transition-colors">讲师</a>
          <a href="#" class="hover:text-text-primary transition-colors">隐私政策</a>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import FloatingActionButton from '@/components/FloatingActionButton.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const showUserMenu = ref(false)
const userMenuRef = ref(null)

const userInfo = computed(() => userStore.userInfo)
const userInitials = computed(() => {
  const name = userInfo.value?.realName || userInfo.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const navItems = [
  { name: '课程', path: '/courses' },
  { name: '路径', path: '/learning' },
  { name: '推荐', path: '/recommend' },
  { name: '我的', path: '/my-courses' },
]

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/courses', query: { keyword: searchKeyword.value } })
  }
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const closeUserMenu = (e) => {
  if (userMenuRef.value && !userMenuRef.value.contains(e.target)) {
    showUserMenu.value = false
  }
}

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  document.addEventListener('click', closeUserMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', closeUserMenu)
})
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
