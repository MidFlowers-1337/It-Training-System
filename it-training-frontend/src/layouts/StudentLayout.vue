<template>
  <div class="app-layout" :data-theme="currentTheme">
    <!-- Apple 风格顶部导航栏 -->
    <header class="navbar">
      <div class="navbar-inner">
        <!-- 左侧：Logo -->
        <router-link to="/home" class="navbar-logo">
          <span class="logo-icon">IT</span>
          <span class="logo-text">Training</span>
        </router-link>

        <!-- 桌面端主导航 -->
        <nav class="navbar-nav">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-link"
          >
            {{ item.name }}
          </router-link>
        </nav>

        <!-- 右侧操作区 -->
        <div class="navbar-actions">
          <!-- 搜索按钮 -->
          <button class="action-btn" @click="openSearch" title="搜索">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
            </svg>
          </button>

          <!-- 主题切换 -->
          <ThemeSwitcher />

          <!-- 用户菜单 -->
          <div class="user-menu" ref="userMenuRef">
            <button class="user-avatar" @click="toggleUserMenu">
              {{ userInitials }}
            </button>

            <!-- 下拉菜单 -->
            <Transition name="dropdown">
              <div v-if="showUserMenu" class="dropdown-menu">
                <div class="dropdown-header">
                  <p class="user-name">{{ userInfo?.realName || '用户' }}</p>
                  <p class="user-email">{{ userInfo?.username }}</p>
                </div>

                <div class="dropdown-body">
                  <router-link to="/profile" class="dropdown-item" @click="showUserMenu = false">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                      <circle cx="12" cy="7" r="4" />
                    </svg>
                    个人资料
                  </router-link>
                  <router-link to="/my-courses" class="dropdown-item" @click="showUserMenu = false">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                      <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                    </svg>
                    我的学习
                  </router-link>
                  <router-link to="/settings" class="dropdown-item" @click="showUserMenu = false">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <circle cx="12" cy="12" r="3" />
                      <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
                    </svg>
                    设置
                  </router-link>
                  <!-- 讲师工作台入口 -->
                  <router-link
                    v-if="userInfo?.role === 'INSTRUCTOR'"
                    to="/instructor"
                    class="dropdown-item instructor-link"
                    @click="showUserMenu = false"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M8 2v4" /><path d="M16 2v4" />
                      <rect width="18" height="18" x="3" y="4" rx="2" />
                      <path d="M3 10h18" />
                    </svg>
                    讲师工作台
                  </router-link>
                  <!-- 管理后台入口 -->
                  <router-link
                    v-if="userInfo?.role === 'ADMIN'"
                    to="/admin/dashboard"
                    class="dropdown-item admin-link"
                    @click="showUserMenu = false"
                  >
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" />
                      <rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" />
                    </svg>
                    管理后台
                  </router-link>
                </div>

                <div class="dropdown-footer">
                  <button class="logout-btn" @click="handleLogout">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                      <polyline points="16 17 21 12 16 7" />
                      <line x1="21" y1="12" x2="9" y2="12" />
                    </svg>
                    退出登录
                  </button>
                </div>
              </div>
            </Transition>
          </div>
        </div>
      </div>
    </header>

    <!-- 搜索弹窗 -->
    <Teleport to="body">
      <Transition name="search-modal">
        <div v-if="isSearchOpen" class="search-overlay" @click="closeSearch">
          <div class="search-modal" @click.stop>
            <div class="search-input-wrapper">
              <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
              </svg>
              <input
                ref="searchInputRef"
                v-model="searchKeyword"
                type="text"
                placeholder="搜索课程、讲师..."
                class="search-input"
                @keyup.enter="handleSearch"
                @keyup.esc="closeSearch"
              />
              <kbd class="search-shortcut">ESC</kbd>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- 主内容区域 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <FloatingActionButton />

    <!-- Apple 风格页脚 -->
    <footer class="footer">
      <div class="footer-inner">
        <!-- 链接区域 -->
        <div class="footer-links">
          <div v-for="group in footerLinks" :key="group.title" class="link-group">
            <h4 class="link-group-title">{{ group.title }}</h4>
            <ul class="link-list">
              <li v-for="link in group.links" :key="link.text">
                <router-link :to="link.href" class="footer-link">
                  {{ link.text }}
                </router-link>
              </li>
            </ul>
          </div>
        </div>

        <!-- 分隔线 -->
        <div class="footer-divider"></div>

        <!-- 底部版权 -->
        <div class="footer-bottom">
          <p class="copyright">
            Copyright © {{ currentYear }} IT Training. All rights reserved.
          </p>
          <div class="legal-links">
            <router-link to="/privacy">隐私政策</router-link>
            <span class="separator">|</span>
            <router-link to="/terms">使用条款</router-link>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useTheme } from '@/design-system'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'
import FloatingActionButton from '@/components/FloatingActionButton.vue'

const router = useRouter()
const userStore = useUserStore()
const { currentTheme } = useTheme()

// 状态
const searchKeyword = ref('')
const showUserMenu = ref(false)
const userMenuRef = ref<HTMLElement | null>(null)
const isSearchOpen = ref(false)
const searchInputRef = ref<HTMLInputElement | null>(null)

// 计算属性
const userInfo = computed(() => userStore.userInfo)
const userInitials = computed(() => {
  const name = userInfo.value?.realName || userInfo.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})
const currentYear = computed(() => new Date().getFullYear())

// 导航项
const navItems = [
  { name: '首页', path: '/home' },
  { name: '课程', path: '/courses' },
  { name: '学习中心', path: '/learning' },
  { name: '推荐', path: '/recommend' },
]

// 页脚链接
const footerLinks = [
  {
    title: '产品',
    links: [
      { text: '全部课程', href: '/courses' },
      { text: '学习路径', href: '/learning' },
      { text: '智能推荐', href: '/recommend' },
    ]
  },
  {
    title: '资源',
    links: [
      { text: '帮助中心', href: '/help' },
      { text: '成就徽章', href: '/achievements' },
    ]
  },
  {
    title: '关于',
    links: [
      { text: '关于我们', href: '/about' },
      { text: '联系我们', href: '/contact' },
    ]
  },
]

// 方法
const openSearch = () => {
  isSearchOpen.value = true
}

const closeSearch = () => {
  isSearchOpen.value = false
  searchKeyword.value = ''
}

watch(isSearchOpen, (value) => {
  if (value) {
    nextTick(() => {
      searchInputRef.value?.focus()
    })
  }
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/courses', query: { keyword: searchKeyword.value } })
    closeSearch()
  }
}

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const closeUserMenu = (e: MouseEvent) => {
  if (userMenuRef.value && !userMenuRef.value.contains(e.target as Node)) {
    showUserMenu.value = false
  }
}

const handleLogout = () => {
  userStore.logout()
  showUserMenu.value = false
  router.push('/login')
}

// 键盘快捷键
const handleKeydown = (e: KeyboardEvent) => {
  // Cmd/Ctrl + K 打开搜索
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    openSearch()
  }
}

onMounted(() => {
  document.addEventListener('click', closeUserMenu)
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('click', closeUserMenu)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
/* ========================================
   Apple 风格布局
   ======================================== */

.app-layout {
  min-height: 100vh;
  background: var(--bg-primary);
  color: var(--text-primary);
  font-family: -apple-system, BlinkMacSystemFont, 'SF Pro Text', 'Helvetica Neue', sans-serif;
}

/* ========================================
   导航栏
   ======================================== */

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 48px;
  z-index: 1000;

  /* Apple 风格毛玻璃 */
  background: rgba(var(--bg-secondary-rgb, 255, 255, 255) / 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);

  /* 极细底部边框 */
  border-bottom: 0.5px solid rgba(var(--border-color-rgb, 0, 0, 0) / 0.1);
}

.navbar-inner {
  max-width: 1024px;
  height: 100%;
  margin: 0 auto;
  padding: 0 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo */
.navbar-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  transition: opacity 0.2s ease;
}

.navbar-logo:hover {
  opacity: 0.8;
}

.logo-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
}

.logo-text {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

/* 导航链接 */
.navbar-nav {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-link {
  padding: 6px 12px;
  font-size: 14px;
  font-weight: 400;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 6px;
  transition: color 0.2s ease, background-color 0.2s ease;
}

.nav-link:hover {
  color: var(--text-primary);
  background: rgba(var(--text-primary-rgb, 0, 0, 0) / 0.06);
}

.nav-link.router-link-active,
.nav-link.router-link-exact-active {
  color: var(--primary-color);
  font-weight: 500;
}

/* 操作区 */
.navbar-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: var(--text-secondary);
  border-radius: 6px;
  cursor: pointer;
  transition: color 0.2s ease, background-color 0.2s ease;
}

.action-btn:hover {
  color: var(--text-primary);
  background: rgba(var(--text-primary-rgb, 0, 0, 0) / 0.06);
}

/* 用户头像 */
.user-menu {
  position: relative;
}

.user-avatar {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(var(--primary-color-rgb, 0, 122, 255) / 0.3);
}

/* 下拉菜单 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  width: 220px;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.05),
    0 10px 20px rgba(0, 0, 0, 0.1);
  border: 0.5px solid var(--border-color);
  overflow: hidden;
}

.dropdown-header {
  padding: 16px;
  border-bottom: 0.5px solid var(--border-light);
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.user-email {
  font-size: 12px;
  color: var(--text-muted);
  margin: 4px 0 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.dropdown-body {
  padding: 8px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  border-radius: 8px;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.dropdown-item:hover {
  background: var(--bg-tertiary);
  color: var(--text-primary);
}

.dropdown-item.instructor-link {
  color: var(--color-warning, #ff9500);
}

.dropdown-item.admin-link {
  color: var(--primary-color);
}

.dropdown-footer {
  padding: 8px;
  border-top: 0.5px solid var(--border-light);
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  font-size: 14px;
  color: var(--error);
  background: none;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.logout-btn:hover {
  background: rgba(var(--error-rgb, 255, 59, 48) / 0.1);
}

/* 下拉菜单动画 */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}

/* ========================================
   搜索弹窗
   ======================================== */

.search-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 100px;
  z-index: 2000;
}

.search-modal {
  width: 100%;
  max-width: 580px;
  margin: 0 20px;
  background: var(--bg-card);
  border-radius: 14px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.search-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
}

.search-icon {
  color: var(--text-muted);
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  font-size: 18px;
  color: var(--text-primary);
  outline: none;
}

.search-input::placeholder {
  color: var(--text-muted);
}

.search-shortcut {
  padding: 4px 8px;
  background: var(--bg-tertiary);
  border-radius: 4px;
  font-size: 11px;
  font-family: inherit;
  color: var(--text-muted);
}

/* 搜索弹窗动画 */
.search-modal-enter-active,
.search-modal-leave-active {
  transition: opacity 0.2s ease;
}

.search-modal-enter-active .search-modal,
.search-modal-leave-active .search-modal {
  transition: transform 0.2s ease;
}

.search-modal-enter-from,
.search-modal-leave-to {
  opacity: 0;
}

.search-modal-enter-from .search-modal {
  transform: scale(0.95) translateY(-20px);
}

.search-modal-leave-to .search-modal {
  transform: scale(0.95) translateY(-10px);
}

/* ========================================
   主内容区域
   ======================================== */

.main-content {
  padding-top: 48px;
  min-height: calc(100vh - 48px);
}

/* 页面过渡动画 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.2s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
}

/* ========================================
   页脚
   ======================================== */

.footer {
  background: var(--bg-tertiary);
  border-top: 0.5px solid var(--border-color);
  margin-top: 80px;
}

.footer-inner {
  max-width: 980px;
  margin: 0 auto;
  padding: 48px 22px 24px;
}

/* 链接网格 */
.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 32px;
  margin-bottom: 40px;
}

.link-group-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 12px;
}

.link-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-link {
  display: block;
  padding: 4px 0;
  font-size: 13px;
  color: var(--text-secondary);
  text-decoration: none;
  transition: color 0.15s ease;
}

.footer-link:hover {
  color: var(--text-primary);
}

/* 分隔线 */
.footer-divider {
  height: 0.5px;
  background: var(--border-color);
  margin-bottom: 20px;
}

/* 底部版权区 */
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.copyright {
  font-size: 12px;
  color: var(--text-muted);
  margin: 0;
}

.legal-links {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.legal-links a {
  color: var(--text-secondary);
  text-decoration: none;
  transition: color 0.15s ease;
}

.legal-links a:hover {
  color: var(--text-primary);
}

.separator {
  color: var(--text-muted);
}

/* ========================================
   响应式
   ======================================== */

@media (max-width: 768px) {
  .navbar-nav {
    display: none;
  }

  .navbar-inner {
    padding: 0 16px;
  }

  .logo-text {
    display: none;
  }

  .footer-links {
    grid-template-columns: repeat(2, 1fr);
    gap: 24px;
  }

  .footer-bottom {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .footer-links {
    grid-template-columns: 1fr;
  }

  .search-modal {
    margin: 0 16px;
  }

  .search-input {
    font-size: 16px;
  }
}

/* ========================================
   深色模式
   ======================================== */

[data-theme="dark"] .navbar {
  background: rgba(29, 29, 31, 0.72);
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

[data-theme="dark"] .nav-link:hover {
  background: rgba(255, 255, 255, 0.06);
}

[data-theme="dark"] .action-btn:hover {
  background: rgba(255, 255, 255, 0.06);
}

[data-theme="dark"] .dropdown-menu {
  background: var(--bg-secondary);
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.2),
    0 10px 20px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .search-overlay {
  background: rgba(0, 0, 0, 0.6);
}

[data-theme="dark"] .search-modal {
  background: var(--bg-secondary);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.4);
}
</style>
