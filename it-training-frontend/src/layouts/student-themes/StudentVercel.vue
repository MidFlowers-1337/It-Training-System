<template>
  <div class="vercel-layout">
    <!-- ─── Top Navbar ─── -->
    <header :class="['vercel-navbar', scrolled && 'shadow-sm']">
      <div class="flex items-center gap-6">
        <!-- Logo -->
        <router-link to="/student/dashboard" class="flex items-center gap-2 hover:opacity-70 transition-opacity">
          <span class="text-[#0F172A] font-black text-lg tracking-tight font-mono">IT</span>
          <span class="text-[#94A3B8] font-mono text-sm hidden sm:inline">/</span>
          <span class="text-[#475569] font-mono text-sm hidden sm:inline">培训系统</span>
        </router-link>

        <!-- Desktop Nav -->
        <nav class="hidden lg:flex items-center">
          <router-link v-for="item in mainNavItems" :key="item.path" :to="item.path"
            :class="['vercel-nav-link', isNavActive(item) && 'vercel-nav-link--active']">
            {{ item.label }}
            <div v-if="isNavActive(item)" class="vercel-active-line"></div>
          </router-link>
        </nav>
      </div>

      <div class="flex items-center gap-2">
        <button class="vercel-icon-btn">
          <Bell class="w-4 h-4" :stroke-width="1.5" />
        </button>
        <ThemeSwitcher />
        <!-- Avatar -->
        <div class="relative" ref="avatarRef">
          <button @click="avatarMenuOpen = !avatarMenuOpen"
            class="w-7 h-7 rounded-full bg-[#0F172A] text-white flex items-center justify-center text-[11px] font-mono font-bold cursor-pointer hover:bg-[#1E293B] transition-colors">
            {{ userInitial }}
          </button>
          <Transition name="vercel-pop">
            <div v-if="avatarMenuOpen" class="vercel-dropdown">
              <div class="px-3 py-2.5">
                <p class="text-sm font-medium text-[#0F172A] font-mono">{{ userName }}</p>
                <p class="text-xs text-[#94A3B8] mt-0.5 font-mono">{{ userEmail || 'student@itts.edu' }}</p>
              </div>
              <div class="vercel-divider"></div>
              <router-link v-for="item in dropdownItems" :key="item.path" :to="item.path" @click="closeMenus"
                class="vercel-dropdown-item">
                <component :is="item.icon" class="w-3.5 h-3.5 text-[#94A3B8]" :stroke-width="1.5" />
                {{ item.label }}
              </router-link>
              <div class="vercel-divider"></div>
              <button @click="handleLogout" class="vercel-dropdown-item vercel-dropdown-danger">
                <LogOut class="w-3.5 h-3.5" :stroke-width="1.5" />
                退出登录
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </header>

    <!-- ─── Content ─── -->
    <main class="vercel-content">
      <div class="max-w-5xl mx-auto px-6 lg:px-8 py-6 lg:py-8">
        <slot />
      </div>
    </main>

    <!-- ─── Mobile Bottom Tab Bar ─── -->
    <nav class="vercel-tab-bar lg:hidden">
      <router-link v-for="item in mobileTabItems" :key="item.path" :to="item.path"
        :class="['vercel-tab-item', isNavActive(item) && 'vercel-tab-item--active']">
        <component :is="item.icon" class="w-[18px] h-[18px]" :stroke-width="1.5" />
        <span>{{ item.label }}</span>
      </router-link>
    </nav>
    <div class="lg:hidden h-14"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onClickOutside } from '@vueuse/core'
import { useStudentNav } from '@/composables/useStudentNav'
import { useScrolled } from '@/composables/useScrolled'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import { Bell, LogOut } from 'lucide-vue-next'

const { scrolled } = useScrolled()

const {
  avatarMenuOpen,
  userInitial, userName, userEmail,
  mainNavItems, dropdownItems, mobileTabItems,
  isNavActive, handleLogout, closeMenus,
} = useStudentNav()

const avatarRef = ref<HTMLElement>()
onClickOutside(avatarRef, () => { avatarMenuOpen.value = false })
</script>

<style scoped>
/* ── Layout ── */
.vercel-layout {
  min-height: 100vh;
  background: #FAFAFA;
  color: #0F172A;
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
}

/* ── Navbar ── */
.vercel-navbar {
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #EAEAEA;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  position: sticky;
  top: 0;
  z-index: 40;
}

.vercel-nav-link {
  position: relative;
  padding: 10px 12px;
  font-size: 13px;
  font-weight: 400;
  color: #6B7280;
  text-decoration: none;
  transition: color 0.12s;
}
.vercel-nav-link:hover { color: #0F172A; }
.vercel-nav-link--active {
  color: #0F172A;
  font-weight: 500;
}

.vercel-active-line {
  position: absolute;
  bottom: -1px;
  left: 12px;
  right: 12px;
  height: 1px;
  background: #0284C7;
}

.vercel-icon-btn {
  padding: 6px;
  border-radius: 6px;
  color: #6B7280;
  transition: all 0.12s;
  background: none;
  border: none;
  cursor: pointer;
}
.vercel-icon-btn:hover {
  color: #0F172A;
  background: #F1F5F9;
}

/* ── Content ── */
.vercel-content {
  flex: 1;
  min-height: calc(100vh - 3rem);
}

/* ── Dropdown ── */
.vercel-dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + 6px);
  width: 220px;
  background: #FFFFFF;
  border: 1px solid #EAEAEA;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  z-index: 50;
  padding: 4px 0;
}

.vercel-dropdown-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  font-size: 13px;
  color: #374151;
  transition: background 0.1s;
  cursor: pointer;
  text-decoration: none;
  width: 100%;
  border: none;
  background: none;
}
.vercel-dropdown-item:hover { background: #F9FAFB; }
.vercel-dropdown-danger { color: #DC2626; }
.vercel-dropdown-danger:hover { background: #FEF2F2; }

.vercel-divider {
  height: 1px;
  background: #EAEAEA;
  margin: 4px 0;
}

/* ── Mobile Tab Bar ── */
.vercel-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 40;
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 56px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  border-top: 1px solid #EAEAEA;
  padding-bottom: env(safe-area-inset-bottom);
}

.vercel-tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 8px;
  font-size: 10px;
  color: #94A3B8;
  text-decoration: none;
  transition: color 0.12s;
}
.vercel-tab-item--active {
  color: #0284C7;
}

/* ── Transitions ── */
.vercel-pop-enter-active { transition: all 0.12s ease-out; }
.vercel-pop-leave-active { transition: all 0.08s ease-in; }
.vercel-pop-enter-from { opacity: 0; transform: translateY(-4px); }
.vercel-pop-leave-to { opacity: 0; }
</style>
