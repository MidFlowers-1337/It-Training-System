<template>
  <div class="linear-layout">
    <!-- ─── Top Navbar ─── -->
    <header :class="['linear-navbar', scrolled && 'shadow-sm']">
      <div class="flex items-center gap-8">
        <!-- Logo -->
        <router-link to="/student/dashboard" class="flex items-center gap-2.5 hover:opacity-80 transition-opacity">
          <div class="w-7 h-7 rounded-md bg-white flex items-center justify-center">
            <span class="text-black font-bold text-xs">IT</span>
          </div>
          <span class="text-[#EDEDED] font-semibold text-sm tracking-tight">智能培训</span>
        </router-link>

        <!-- Desktop Nav -->
        <nav class="hidden lg:flex items-center gap-1">
          <router-link v-for="item in mainNavItems" :key="item.path" :to="item.path"
            :class="['linear-nav-link', isNavActive(item) && 'linear-nav-link--active']">
            {{ item.label }}
            <div v-if="isNavActive(item)" class="linear-active-bar"></div>
          </router-link>
        </nav>
      </div>

      <div class="flex items-center gap-3">
        <button class="linear-icon-btn">
          <Bell class="w-4 h-4" :stroke-width="1.75" />
        </button>
        <ThemeSwitcher />
        <!-- Avatar -->
        <div class="relative" ref="avatarRef">
          <button @click="avatarMenuOpen = !avatarMenuOpen"
            class="w-7 h-7 rounded-full bg-[#818CF8]/20 text-[#818CF8] flex items-center justify-center text-xs font-semibold cursor-pointer hover:bg-[#818CF8]/30 transition-colors">
            {{ userInitial }}
          </button>
          <Transition name="linear-pop">
            <div v-if="avatarMenuOpen" class="linear-dropdown">
              <div class="px-3 py-2.5">
                <p class="text-sm font-medium text-[#EDEDED]">{{ userName }}</p>
                <p class="text-xs text-[#6B6B6E] mt-0.5">{{ userEmail || 'student@itts.edu' }}</p>
              </div>
              <div class="linear-divider"></div>
              <router-link v-for="item in dropdownItems" :key="item.path" :to="item.path" @click="closeMenus"
                class="linear-dropdown-item">
                <component :is="item.icon" class="w-4 h-4 text-[#6B6B6E]" :stroke-width="1.75" />
                {{ item.label }}
              </router-link>
              <div class="linear-divider"></div>
              <button @click="handleLogout" class="linear-dropdown-item linear-dropdown-danger">
                <LogOut class="w-4 h-4" :stroke-width="1.75" />
                退出登录
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </header>

    <!-- ─── Content ─── -->
    <main class="linear-content">
      <div class="max-w-5xl mx-auto px-6 lg:px-8 py-6 lg:py-8">
        <slot />
      </div>
    </main>

    <!-- ─── Mobile Bottom Tab Bar ─── -->
    <nav class="linear-tab-bar lg:hidden">
      <router-link v-for="item in mobileTabItems" :key="item.path" :to="item.path"
        :class="['linear-tab-item', isNavActive(item) && 'linear-tab-item--active']">
        <component :is="item.icon" class="w-5 h-5" :stroke-width="1.75" />
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
  avatarMenuOpen, mobileMenuOpen,
  userInitial, userName, userEmail,
  mainNavItems, dropdownItems, mobileTabItems,
  isNavActive, handleLogout, closeMenus,
} = useStudentNav()

const avatarRef = ref<HTMLElement>()
onClickOutside(avatarRef, () => { avatarMenuOpen.value = false })
</script>

<style scoped>
/* ── Layout ── */
.linear-layout {
  min-height: 100vh;
  background: #08090A;
  color: #EDEDED;
}

/* ── Navbar ── */
.linear-navbar {
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
  background: rgba(8, 9, 10, 0.85);
  backdrop-filter: blur(12px);
  position: sticky;
  top: 0;
  z-index: 40;
}

.linear-nav-link {
  position: relative;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 500;
  color: #6B6B6E;
  text-decoration: none;
  transition: color 0.15s;
}
.linear-nav-link:hover { color: #A0A0A5; }
.linear-nav-link--active { color: #EDEDED; }

.linear-active-bar {
  position: absolute;
  bottom: -9px;
  left: 14px;
  right: 14px;
  height: 2px;
  background: #818CF8;
  border-radius: 1px;
  box-shadow: 0 0 8px rgba(129, 140, 248, 0.5), 0 0 20px rgba(129, 140, 248, 0.2);
}

.linear-icon-btn {
  padding: 6px;
  border-radius: 6px;
  color: #6B6B6E;
  transition: all 0.15s;
  background: none;
  border: none;
  cursor: pointer;
}
.linear-icon-btn:hover {
  color: #EDEDED;
  background: rgba(255, 255, 255, 0.04);
}

/* ── Content ── */
.linear-content {
  flex: 1;
  min-height: calc(100vh - 3rem);
}

/* ── Dropdown ── */
.linear-dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  width: 240px;
  background: #161618;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
  z-index: 50;
  padding: 4px 0;
}

.linear-dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 12px;
  font-size: 13px;
  color: #A0A0A5;
  transition: all 0.1s;
  cursor: pointer;
  text-decoration: none;
  width: 100%;
  border: none;
  background: none;
}
.linear-dropdown-item:hover {
  color: #EDEDED;
  background: rgba(255, 255, 255, 0.04);
}
.linear-dropdown-danger { color: #F87171; }
.linear-dropdown-danger:hover { background: rgba(248, 113, 113, 0.08); }

.linear-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.04);
  margin: 4px 0;
}

/* ── Mobile Tab Bar ── */
.linear-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 40;
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: rgba(8, 9, 10, 0.92);
  backdrop-filter: blur(12px);
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  padding-bottom: env(safe-area-inset-bottom);
}

.linear-tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 8px 12px;
  font-size: 10px;
  color: #6B6B6E;
  text-decoration: none;
  transition: color 0.15s;
}
.linear-tab-item--active {
  color: #818CF8;
}

/* ── Transitions ── */
.linear-pop-enter-active { transition: all 0.15s cubic-bezier(0.16, 1, 0.3, 1); }
.linear-pop-leave-active { transition: all 0.1s ease-in; }
.linear-pop-enter-from { opacity: 0; transform: translateY(-6px) scale(0.97); }
.linear-pop-leave-to { opacity: 0; transform: translateY(-3px); }
</style>
