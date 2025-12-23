<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="admin-sidebar" :class="{ collapsed: isCollapsed }">
      <!-- Logo -->
      <div class="sidebar-logo">
        <div class="logo-icon">IT</div>
        <span v-if="!isCollapsed" class="logo-text">管理后台</span>
      </div>

      <!-- Navigation -->
      <nav class="sidebar-nav">
        <div class="nav-group">
          <div v-if="!isCollapsed" class="nav-group-title">概览</div>
          <router-link to="/admin/dashboard" class="nav-item" active-class="active">
            <span class="nav-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="7" height="7" rx="1" />
                <rect x="14" y="3" width="7" height="7" rx="1" />
                <rect x="14" y="14" width="7" height="7" rx="1" />
                <rect x="3" y="14" width="7" height="7" rx="1" />
              </svg>
            </span>
            <span v-if="!isCollapsed" class="nav-label">控制台</span>
          </router-link>
        </div>

        <div class="nav-group">
          <div v-if="!isCollapsed" class="nav-group-title">内容管理</div>
          <router-link to="/admin/users" class="nav-item" active-class="active">
            <span class="nav-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
            </span>
            <span v-if="!isCollapsed" class="nav-label">用户管理</span>
          </router-link>
          <router-link to="/admin/courses" class="nav-item" active-class="active">
            <span class="nav-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
              </svg>
            </span>
            <span v-if="!isCollapsed" class="nav-label">课程管理</span>
          </router-link>
          <router-link to="/admin/sessions" class="nav-item" active-class="active">
            <span class="nav-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                <line x1="16" y1="2" x2="16" y2="6" />
                <line x1="8" y1="2" x2="8" y2="6" />
                <line x1="3" y1="10" x2="21" y2="10" />
              </svg>
            </span>
            <span v-if="!isCollapsed" class="nav-label">班期管理</span>
          </router-link>
          <router-link to="/admin/enrollments" class="nav-item" active-class="active">
            <span class="nav-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                <polyline points="14 2 14 8 20 8" />
                <line x1="16" y1="13" x2="8" y2="13" />
                <line x1="16" y1="17" x2="8" y2="17" />
                <polyline points="10 9 9 9 8 9" />
              </svg>
            </span>
            <span v-if="!isCollapsed" class="nav-label">报名管理</span>
          </router-link>
        </div>
      </nav>

      <!-- Collapse Toggle -->
      <button type="button" class="collapse-btn" @click="isCollapsed = !isCollapsed">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline v-if="isCollapsed" points="9 18 15 12 9 6" />
          <polyline v-else points="15 18 9 12 15 6" />
        </svg>
      </button>
    </aside>

    <!-- Main Area -->
    <div class="admin-main">
      <!-- Header -->
      <header class="admin-header">
        <div class="header-breadcrumb">
          <span class="breadcrumb-prefix">当前位置：</span>
          <span class="breadcrumb-current">{{ currentRouteName }}</span>
        </div>

        <div class="header-actions">
          <ThemeSwitcher />

          <!-- User Menu -->
          <div class="user-menu" @click="showUserMenu = !showUserMenu" v-click-outside="() => showUserMenu = false">
            <div class="user-avatar">
              {{ userStore.realName?.charAt(0) || 'A' }}
            </div>
            <div class="user-info">
              <span class="user-name">{{ userStore.realName || '管理员' }}</span>
              <span class="user-role">系统管理员</span>
            </div>
            <svg class="dropdown-arrow" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="6 9 12 15 18 9" />
            </svg>

            <!-- Dropdown -->
            <Transition name="dropdown">
              <div v-if="showUserMenu" class="user-dropdown">
                <button type="button" class="dropdown-item" @click="handleCommand('frontend')">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                    <polyline points="9 22 9 12 15 12 15 22" />
                  </svg>
                  返回前台
                </button>
                <div class="dropdown-divider"></div>
                <button type="button" class="dropdown-item danger" @click="handleCommand('logout')">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                    <polyline points="16 17 21 12 16 7" />
                    <line x1="21" y1="12" x2="9" y2="12" />
                  </svg>
                  退出登录
                </button>
              </div>
            </Transition>
          </div>
        </div>
      </header>

      <!-- Content -->
      <main class="admin-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';
import ThemeSwitcher from '@/components/ThemeSwitcher.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const isCollapsed = ref(false);
const showUserMenu = ref(false);

const menuItems = [
  { path: '/admin/dashboard', title: '控制台' },
  { path: '/admin/users', title: '用户管理' },
  { path: '/admin/courses', title: '课程管理' },
  { path: '/admin/sessions', title: '班期管理' },
  { path: '/admin/enrollments', title: '报名管理' },
];

const currentRouteName = computed(() => {
  const item = menuItems.find((i) => i.path === route.path);
  return item ? item.title : '控制台';
});

const handleCommand = (command: string) => {
  showUserMenu.value = false;
  if (command === 'logout') {
    userStore.logout();
    router.push('/login');
  } else if (command === 'frontend') {
    router.push('/home');
  }
};

// Custom directive for click outside
const vClickOutside = {
  mounted(el: HTMLElement, binding: { value: () => void }) {
    el._clickOutside = (event: MouseEvent) => {
      if (!(el === event.target || el.contains(event.target as Node))) {
        binding.value();
      }
    };
    document.addEventListener('click', el._clickOutside);
  },
  unmounted(el: HTMLElement) {
    document.removeEventListener('click', el._clickOutside);
  },
};
</script>

<style scoped>
/* ========================================
   Apple Workbench 风格管理后台布局
   ======================================== */

.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ===== Sidebar ===== */
.admin-sidebar {
  width: 240px;
  background: var(--bg-secondary);
  border-right: 0.5px solid var(--border-color);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 100;
  transition: width 0.2s ease;
}

.admin-sidebar.collapsed {
  width: 64px;
}

/* Logo */
.sidebar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-bottom: 0.5px solid var(--border-color);
  min-height: 64px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-color);
  color: white;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;
}

.logo-text {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
}

/* Navigation */
.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.nav-group {
  margin-bottom: 24px;
}

.nav-group-title {
  padding: 0 12px;
  margin-bottom: 8px;
  font-size: 11px;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all 0.15s ease;
}

.nav-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.nav-item.active {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  color: var(--primary-color);
}

.nav-icon {
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.nav-icon svg {
  width: 100%;
  height: 100%;
}

.nav-label {
  white-space: nowrap;
}

/* Collapse Button */
.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 48px;
  background: none;
  border: none;
  border-top: 0.5px solid var(--border-color);
  color: var(--text-muted);
  cursor: pointer;
  transition: color 0.15s ease, background-color 0.15s ease;
}

.collapse-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.collapse-btn svg {
  width: 18px;
  height: 18px;
}

/* ===== Main Area ===== */
.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: margin-left 0.2s ease;
}

.admin-sidebar.collapsed + .admin-main {
  margin-left: 64px;
}

/* Header */
.admin-header {
  height: 64px;
  background: rgba(var(--bg-secondary-rgb, 255, 255, 255) / 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-breadcrumb {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.breadcrumb-prefix {
  color: var(--text-muted);
}

.breadcrumb-current {
  color: var(--text-primary);
  font-weight: 500;
  margin-left: 8px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* User Menu */
.user-menu {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: background-color 0.15s ease;
}

.user-menu:hover {
  background: var(--bg-hover);
}

.user-avatar {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  border: 1px solid rgba(var(--primary-color-rgb, 0, 122, 255) / 0.2);
  border-radius: 50%;
  font-size: 13px;
  font-weight: 600;
  color: var(--primary-color);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
}

.user-role {
  font-size: 11px;
  color: var(--text-muted);
}

.dropdown-arrow {
  width: 14px;
  height: 14px;
  color: var(--text-muted);
}

/* User Dropdown */
.user-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 180px;
  background: var(--bg-card);
  border: 0.5px solid var(--border-color);
  border-radius: 12px;
  padding: 8px;
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.05),
    0 10px 20px rgba(0, 0, 0, 0.08);
  z-index: 200;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 12px;
  background: none;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.15s ease;
  text-align: left;
}

.dropdown-item:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.dropdown-item.danger {
  color: var(--error);
}

.dropdown-item.danger:hover {
  background: rgba(var(--error-rgb, 255, 59, 48) / 0.1);
  color: var(--error);
}

.dropdown-item svg {
  width: 16px;
  height: 16px;
}

.dropdown-divider {
  height: 0.5px;
  background: var(--border-light);
  margin: 8px 0;
}

/* Dropdown Animation */
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.15s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* ===== Content ===== */
.admin-content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

/* Fade Animation */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* ===== Responsive ===== */
@media (max-width: 1024px) {
  .admin-sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  .admin-sidebar.open {
    transform: translateX(0);
  }

  .admin-main {
    margin-left: 0;
  }

  .user-info {
    display: none;
  }
}

@media (max-width: 768px) {
  .admin-content {
    padding: 16px;
  }

  .admin-header {
    padding: 0 16px;
  }
}

/* ===== Dark Mode ===== */
[data-theme="dark"] .admin-sidebar {
  background: var(--bg-secondary);
}

[data-theme="dark"] .admin-header {
  background: rgba(29, 29, 31, 0.72);
}

[data-theme="dark"] .user-dropdown {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow:
    0 4px 6px rgba(0, 0, 0, 0.2),
    0 10px 20px rgba(0, 0, 0, 0.25);
}
</style>
