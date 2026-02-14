<template>
  <div class="duo-layout">
    <!-- ─── Top Bar ─── -->
    <header :class="['duo-topbar', scrolled && 'shadow-sm']">
      <div class="flex items-center gap-3">
        <button @click="mobileMenuOpen = !mobileMenuOpen" class="lg:hidden p-2 rounded-xl hover:bg-[#FEF3C7] text-[#92400E] transition-colors">
          <Menu class="w-5 h-5" :stroke-width="2" />
        </button>
        <router-link to="/student/dashboard" class="flex items-center gap-2.5 hover:opacity-80 transition-opacity">
          <div class="w-8 h-8 rounded-xl bg-[#D97706] flex items-center justify-center shadow-sm">
            <span class="text-white font-bold text-xs">IT</span>
          </div>
          <span class="text-[#292524] font-bold text-base hidden sm:inline">智能培训</span>
        </router-link>
      </div>
      <div class="flex items-center gap-3">
        <button class="p-2 rounded-xl hover:bg-[#FEF3C7] text-[#A8A29E] transition-colors">
          <Bell class="w-5 h-5" :stroke-width="2" />
        </button>
        <ThemeSwitcher />
        <!-- Avatar -->
        <div class="relative" ref="avatarRef">
          <button @click="avatarMenuOpen = !avatarMenuOpen"
            class="w-8 h-8 rounded-full bg-[#FBBF24] text-[#78350F] flex items-center justify-center text-sm font-bold cursor-pointer hover:ring-2 hover:ring-[#FDE68A] transition-all">
            {{ userInitial }}
          </button>
          <Transition name="duo-pop">
            <div v-if="avatarMenuOpen" class="duo-dropdown">
              <div class="px-4 py-3">
                <p class="text-sm font-bold text-[#292524]">{{ userName }}</p>
                <p class="text-xs text-[#A8A29E] mt-0.5">{{ userEmail || 'student@itts.edu' }}</p>
              </div>
              <div class="duo-divider"></div>
              <router-link v-for="item in dropdownItems" :key="item.path" :to="item.path" @click="closeMenus"
                class="duo-dropdown-item">
                <component :is="item.icon" class="w-4 h-4 text-[#A8A29E]" :stroke-width="2" />
                {{ item.label }}
              </router-link>
              <div class="duo-divider"></div>
              <button @click="handleLogout" class="duo-dropdown-item duo-dropdown-danger">
                <LogOut class="w-4 h-4" :stroke-width="2" />
                退出登录
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </header>

    <div class="flex min-h-[calc(100vh-3rem)]">
      <!-- ─── Icon Navigation Strip ─── -->
      <aside :class="['duo-iconbar', mobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0']">
        <nav class="flex flex-col items-center gap-2 py-4">
          <!-- Main Nav Icons -->
          <router-link v-for="(item, idx) in mainNavItems" :key="item.path" :to="item.path" @click="mobileMenuOpen = false"
            :class="['duo-icon-item', isNavActive(item) && 'duo-icon-item--active']">
            <div :class="['duo-icon-circle', isNavActive(item) ? iconColors[idx]?.active : iconColors[idx]?.idle]">
              <component :is="item.icon" class="w-5 h-5" :stroke-width="2" />
            </div>
            <span class="duo-icon-label">{{ item.label }}</span>
          </router-link>

          <div class="w-8 h-px bg-[#E7E5E4] my-2"></div>

          <!-- Secondary Nav Icons -->
          <router-link v-for="item in dropdownItems" :key="item.path" :to="item.path" @click="mobileMenuOpen = false"
            :class="['duo-icon-item', route.path === item.path && 'duo-icon-item--active']">
            <div :class="['duo-icon-circle-sm', route.path === item.path ? 'bg-[#D97706]/15 text-[#92400E]' : 'text-[#A8A29E] hover:bg-[#FEF3C7]']">
              <component :is="item.icon" class="w-4 h-4" :stroke-width="2" />
            </div>
            <span class="duo-icon-label text-[11px]">{{ item.label }}</span>
          </router-link>
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

    <!-- ─── Mobile Bottom Tab Bar ─── -->
    <nav class="duo-tab-bar lg:hidden">
      <router-link v-for="(item, idx) in mobileTabItems" :key="item.path" :to="item.path"
        :class="['duo-tab-item', isNavActive(item) && 'duo-tab-item--active']">
        <div :class="['duo-tab-icon', isNavActive(item) ? mobileColors[idx] : 'text-[#A8A29E]']">
          <component :is="item.icon" class="w-5 h-5" :stroke-width="2" />
        </div>
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
import { Menu, Bell, LogOut } from 'lucide-vue-next'

const { scrolled } = useScrolled()

const {
  route, avatarMenuOpen, mobileMenuOpen,
  userInitial, userName, userEmail,
  mainNavItems, dropdownItems, mobileTabItems,
  isNavActive, handleLogout, closeMenus,
} = useStudentNav()

const avatarRef = ref<HTMLElement>()
onClickOutside(avatarRef, () => { avatarMenuOpen.value = false })

/* Duolingo-style colorful icon backgrounds */
const iconColors = [
  { idle: 'bg-[#DBEAFE] text-[#2563EB]', active: 'bg-[#2563EB] text-white shadow-[0_4px_12px_rgba(37,99,235,0.3)]' },
  { idle: 'bg-[#D1FAE5] text-[#059669]', active: 'bg-[#059669] text-white shadow-[0_4px_12px_rgba(5,150,105,0.3)]' },
  { idle: 'bg-[#FEF3C7] text-[#D97706]', active: 'bg-[#D97706] text-white shadow-[0_4px_12px_rgba(217,119,6,0.3)]' },
  { idle: 'bg-[#EDE9FE] text-[#7C3AED]', active: 'bg-[#7C3AED] text-white shadow-[0_4px_12px_rgba(124,58,237,0.3)]' },
]

const mobileColors = [
  'text-[#2563EB]', 'text-[#059669]', 'text-[#D97706]', 'text-[#7C3AED]', 'text-[#92400E]',
]
</script>

<style scoped>
/* ── Layout ── */
.duo-layout {
  min-height: 100vh;
  background: #FFFBF5;
  color: #292524;
}

/* ── Top Bar ── */
.duo-topbar {
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  background: rgba(255, 251, 245, 0.9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #F5F0EB;
  position: sticky;
  top: 0;
  z-index: 40;
}

/* ── Icon Navigation Strip ── */
.duo-iconbar {
  position: fixed;
  top: 3rem;
  left: 0;
  width: 80px;
  height: calc(100vh - 3rem);
  background: #FEFCF8;
  border-right: 1px solid #F5F0EB;
  overflow-y: auto;
  z-index: 30;
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}
@media (min-width: 1024px) {
  .duo-iconbar { position: sticky; }
}

.duo-icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 4px;
  text-decoration: none;
  transition: transform 0.15s;
}
.duo-icon-item:hover { transform: scale(1.05); }
.duo-icon-item--active { transform: scale(1.08); }

.duo-icon-circle {
  width: 44px;
  height: 44px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.duo-icon-circle-sm {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
}

.duo-icon-label {
  font-size: 10px;
  font-weight: 600;
  color: #78716C;
  text-align: center;
  line-height: 1.2;
}
.duo-icon-item--active .duo-icon-label {
  color: #292524;
}

/* ── Dropdown ── */
.duo-dropdown {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  width: 240px;
  background: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(120, 80, 20, 0.12);
  z-index: 50;
  padding: 4px 0;
  border: 1px solid #F5F0EB;
}

.duo-dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #44403C;
  transition: background 0.1s;
  cursor: pointer;
  text-decoration: none;
  width: 100%;
  border: none;
  background: none;
}
.duo-dropdown-item:hover { background: #FEF3C7; }
.duo-dropdown-danger { color: #DC2626; }
.duo-dropdown-danger:hover { background: #FEF2F2; }

.duo-divider {
  height: 1px;
  background: #F5F0EB;
  margin: 4px 8px;
}

/* ── Mobile Tab Bar ── */
.duo-tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 40;
  display: flex;
  align-items: center;
  justify-content: space-around;
  background: rgba(255, 251, 245, 0.95);
  backdrop-filter: blur(8px);
  border-top: 1px solid #F5F0EB;
  padding: 6px 0;
  padding-bottom: calc(6px + env(safe-area-inset-bottom));
}

.duo-tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 4px 8px;
  font-size: 10px;
  font-weight: 600;
  color: #A8A29E;
  text-decoration: none;
  transition: all 0.15s;
}
.duo-tab-item--active { color: #292524; }

.duo-tab-icon {
  width: 36px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  transition: background 0.15s;
}
.duo-tab-item--active .duo-tab-icon {
  background: #FEF3C7;
}

/* ── Transitions ── */
.duo-pop-enter-active { transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1); }
.duo-pop-leave-active { transition: all 0.1s ease-in; }
.duo-pop-enter-from { opacity: 0; transform: translateY(-8px) scale(0.95); }
.duo-pop-leave-to { opacity: 0; transform: scale(0.97); }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
