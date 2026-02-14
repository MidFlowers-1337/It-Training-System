<template>
  <div class="notion-layout">
    <!-- ─── Top Bar ─── -->
    <header :class="['notion-topbar', scrolled && 'shadow-sm']">
      <div class="flex items-center gap-3">
        <button @click="mobileMenuOpen = !mobileMenuOpen" class="lg:hidden p-1.5 rounded-md hover:bg-[#F0F0EF] text-[#91918E] transition-colors">
          <Menu class="w-[18px] h-[18px]" :stroke-width="1.75" />
        </button>
        <router-link to="/student/dashboard" class="flex items-center gap-2 hover:opacity-80 transition-opacity">
          <div class="w-6 h-6 rounded bg-[#191919] flex items-center justify-center">
            <span class="text-white font-bold text-[10px]">IT</span>
          </div>
          <span class="text-[#37352F] font-semibold text-sm hidden sm:inline">智能培训</span>
        </router-link>
      </div>
      <div class="flex items-center gap-2">
        <ThemeSwitcher />
        <button class="p-1.5 rounded-md hover:bg-[#F0F0EF] text-[#91918E] transition-colors">
          <Bell class="w-4 h-4" :stroke-width="1.75" />
        </button>
        <!-- Avatar -->
        <div class="relative" ref="avatarRef">
          <button @click="avatarMenuOpen = !avatarMenuOpen"
            class="w-7 h-7 rounded-full bg-[#E8DEEE] text-[#9B59B6] flex items-center justify-center text-xs font-semibold cursor-pointer hover:ring-2 hover:ring-[#E8DEEE]/50 transition-all">
            {{ userInitial }}
          </button>
          <Transition name="notion-pop">
            <div v-if="avatarMenuOpen" class="notion-dropdown">
              <div class="px-3 py-2.5">
                <p class="text-sm font-medium text-[#37352F]">{{ userName }}</p>
                <p class="text-xs text-[#91918E] mt-0.5">{{ userEmail || 'student@itts.edu' }}</p>
              </div>
              <div class="notion-divider"></div>
              <router-link v-for="item in dropdownItems" :key="item.path" :to="item.path" @click="closeMenus"
                class="notion-dropdown-item">
                <component :is="item.icon" class="w-4 h-4 text-[#91918E]" :stroke-width="1.75" />
                {{ item.label }}
              </router-link>
              <div class="notion-divider"></div>
              <button @click="handleLogout" class="notion-dropdown-item notion-dropdown-danger">
                <LogOut class="w-4 h-4" :stroke-width="1.75" />
                退出登录
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </header>

    <div class="flex min-h-[calc(100vh-3rem)]">
      <!-- ─── Sidebar ─── -->
      <aside :class="['notion-sidebar', mobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0']">
        <nav class="p-3">
          <!-- Main Nav -->
          <div class="mb-6">
            <p class="notion-section-label">导航</p>
            <router-link v-for="item in mainNavItems" :key="item.path" :to="item.path" @click="mobileMenuOpen = false"
              :class="['notion-nav-item', isNavActive(item) && 'notion-nav-item--active']">
              <component :is="item.icon" class="w-[15px] h-[15px] flex-shrink-0 opacity-60" :stroke-width="1.75" />
              {{ item.label }}
            </router-link>
          </div>
          <!-- Secondary Nav -->
          <div>
            <p class="notion-section-label">更多</p>
            <router-link v-for="item in dropdownItems" :key="item.path" :to="item.path" @click="mobileMenuOpen = false"
              :class="['notion-nav-item', route.path === item.path && 'notion-nav-item--active']">
              <component :is="item.icon" class="w-[15px] h-[15px] flex-shrink-0 opacity-50" :stroke-width="1.75" />
              {{ item.label }}
            </router-link>
          </div>
        </nav>
      </aside>

      <!-- Mobile Overlay -->
      <Transition name="fade">
        <div v-if="mobileMenuOpen" class="fixed inset-0 bg-black/20 z-20 lg:hidden" @click="mobileMenuOpen = false"></div>
      </Transition>

      <!-- ─── Content ─── -->
      <main class="flex-1 min-w-0">
        <div class="max-w-5xl mx-auto px-6 lg:px-8 py-6 lg:py-8">
          <slot />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onClickOutside } from '@vueuse/core'
import { useStudentNav } from '@/composables/useStudentNav'
import { useScrolled } from '@/composables/useScrolled'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import { Menu, Bell, LogOut } from 'lucide-vue-next'

const { scrolled } = useScrolled()

const {
  route, avatarMenuOpen, mobileMenuOpen,
  userInitial, userName, userEmail,
  mainNavItems, dropdownItems,
  isNavActive, handleLogout, closeMenus,
} = useStudentNav()

const avatarRef = ref<HTMLElement>()
onClickOutside(avatarRef, () => { avatarMenuOpen.value = false })
</script>

<style scoped>
/* ── Layout ── */
.notion-layout {
  min-height: 100vh;
  background: #FFFFFF;
  color: #37352F;
}

/* ── Top Bar ── */
.notion-topbar {
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid #F0F0EF;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  position: sticky;
  top: 0;
  z-index: 40;
}

/* ── Sidebar ── */
.notion-sidebar {
  position: fixed;
  top: 3rem;
  left: 0;
  width: 220px;
  height: calc(100vh - 3rem);
  background: #FBFBFA;
  border-right: 1px solid #F0F0EF;
  overflow-y: auto;
  z-index: 30;
  transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
@media (min-width: 1024px) {
  .notion-sidebar { position: sticky; }
}

.notion-section-label {
  padding: 0 8px;
  margin-bottom: 4px;
  font-size: 11px;
  font-weight: 500;
  color: #91918E;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.notion-nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  color: #73726E;
  transition: background 0.1s, color 0.1s;
  text-decoration: none;
  line-height: 1.5;
}
.notion-nav-item:hover {
  background: #F0F0EF;
  color: #37352F;
}
.notion-nav-item--active {
  background: #F0F0EF;
  color: #37352F;
  font-weight: 500;
}

/* ── Dropdown ── */
.notion-dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + 6px);
  width: 240px;
  background: #FFFFFF;
  border-radius: 8px;
  box-shadow:
    0 0 0 1px rgba(15, 15, 15, 0.04),
    0 3px 6px rgba(15, 15, 15, 0.08),
    0 9px 24px rgba(15, 15, 15, 0.12);
  z-index: 50;
  padding: 4px 0;
}

.notion-dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  font-size: 14px;
  color: #37352F;
  transition: background 0.1s;
  cursor: pointer;
  text-decoration: none;
  width: 100%;
  border: none;
  background: none;
}
.notion-dropdown-item:hover { background: #F5F5F5; }
.notion-dropdown-danger { color: #EB5757; }
.notion-dropdown-danger:hover { background: #FBE4E4; }

.notion-divider {
  height: 1px;
  background: #F0F0EF;
  margin: 4px 0;
}

/* ── Transitions ── */
.notion-pop-enter-active { transition: all 0.15s cubic-bezier(0.16, 1, 0.3, 1); }
.notion-pop-leave-active { transition: all 0.1s ease-in; }
.notion-pop-enter-from { opacity: 0; transform: translateY(-4px) scale(0.98); }
.notion-pop-leave-to { opacity: 0; transform: translateY(-2px); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
