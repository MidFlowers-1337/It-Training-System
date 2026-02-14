<template>
  <div class="min-h-screen flex" :class="layoutBgClass">
    <aside :class="['fixed top-0 left-0 h-full z-40 transition-all duration-300 border-r', sidebarClass, collapsed ? 'w-16' : 'w-60', isMobile && !sidebarOpen ? '-translate-x-full' : 'translate-x-0']">
      <div :class="['h-14 flex items-center px-4 border-b', sidebarBorderClass]">
        <div :class="['w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0', logoBgClass]">
          <span class="text-white font-bold text-xs">管</span>
        </div>
        <Transition name="fade"><span v-if="!collapsed" class="truncate">管理后台</span></Transition>
      </div>
      <nav class="p-2 space-y-0.5">
        <router-link v-for="item in menuItems" :key="item.path" :to="item.path" :title="collapsed ? item.label : undefined" :class="['group relative flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-all', navItemClass(item.path)]">
          <div v-if="route.path === item.path" :class="activeIndicatorClass"></div>
          <component :is="item.icon" class="w-[18px] h-[18px] flex-shrink-0" :stroke-width="1.75" />
          <Transition name="fade"><span v-if="!collapsed" class="truncate">{{ item.label }}</span></Transition>
        </router-link>
      </nav>
    </aside>
    <div v-if="isMobile && sidebarOpen" class="fixed inset-0 bg-black/30 z-30" @click="sidebarOpen = false"></div>
    <div :class="['flex-1 transition-all duration-300', isMobile ? 'ml-0' : collapsed ? 'ml-16' : 'ml-60']">
      <header :class="['h-14 flex items-center justify-between px-6 border-b sticky top-0 z-20', headerClass, scrolled && 'shadow-sm']">
        <div class="flex items-center gap-4">
          <button @click="isMobile ? (sidebarOpen = !sidebarOpen) : appStore.toggleSidebar()" :class="['p-2 rounded-lg transition-colors', headerBtnClass]">
            <MenuIcon class="w-5 h-5" :stroke-width="1.75" />
          </button>
          <h1 :class="['text-base font-semibold', pageTitleClass]">管理后台</h1>
        </div>
        <div class="flex items-center gap-3">
          <ThemeSwitcher />
          <router-link to="/student/dashboard" :class="['text-sm transition-colors', linkClass]">返回前台</router-link>
        </div>
      </header>
      <main class="p-6"><div class="max-w-7xl mx-auto"><slot /></div></main>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useThemeStore } from '@/stores/theme'
import { useScrolled } from '@/composables/useScrolled'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import { BarChart3, Users, BookOpen, CalendarRange, FileText, Menu as MenuIcon } from 'lucide-vue-next'

const { scrolled } = useScrolled()

const route = useRoute()
const appStore = useAppStore()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const sidebarOpen = ref(false)
const collapsed = computed(() => appStore.sidebarCollapsed)
const isMobile = computed(() => appStore.isMobile)

const menuItems = [
  { path: '/admin/dashboard', icon: BarChart3, label: '数据看板', labelEn: 'Dashboard' },
  { path: '/admin/users', icon: Users, label: '用户管理', labelEn: 'Users' },
  { path: '/admin/courses', icon: BookOpen, label: '课程管理', labelEn: 'Courses' },
  { path: '/admin/sessions', icon: CalendarRange, label: '班期管理', labelEn: 'Sessions' },
  { path: '/admin/enrollments', icon: FileText, label: '报名管理', labelEn: 'Enrollments' },
]

const layoutBgClass = computed(() => ({ light: 'bg-[#F6F9FC]', dark: 'bg-[#08090A]', warm: 'bg-[#FFFBF5]', pro: 'bg-[#F8FAFC]' }[theme.value]))
const sidebarClass = computed(() => ({ light: 'bg-white border-[#E3E8EE]', dark: 'bg-[#0C0C0E] border-white/[0.04]', warm: 'bg-[#FEFCF8] border-[#E7E5E4]', pro: 'bg-[#0F172A] border-[#1E293B]' }[theme.value]))
const sidebarBorderClass = computed(() => ({ light: 'border-[#E3E8EE]', dark: 'border-white/[0.04]', warm: 'border-[#E7E5E4]', pro: 'border-[#1E293B]' }[theme.value]))
const logoBgClass = computed(() => ({ light: 'bg-rose-500', dark: 'bg-rose-400', warm: 'bg-[#DC2626]', pro: 'bg-rose-600' }[theme.value]))
const logoTextClass = computed(() => ({ light: 'text-[#0A2540]', dark: 'text-[#EDEDED]', warm: 'text-[#292524]', pro: 'text-white font-mono text-sm' }[theme.value]))
const headerClass = computed(() => ({ light: 'bg-white/80 backdrop-blur-sm border-[#E3E8EE]', dark: 'bg-[#08090A]/80 backdrop-blur-sm border-white/[0.04]', warm: 'bg-[#FFFBF5]/80 backdrop-blur-sm border-[#E7E5E4]', pro: 'bg-[#F8FAFC]/80 backdrop-blur-sm border-[#E2E8F0]' }[theme.value]))
const headerBtnClass = computed(() => ({ light: 'text-[#8898AA] hover:bg-[#F6F9FC]', dark: 'text-[#6B6B6E] hover:bg-white/[0.03]', warm: 'text-[#A8A29E] hover:bg-[#FEF3C7]/30', pro: 'text-[#64748B] hover:bg-[#F1F5F9]' }[theme.value]))
const pageTitleClass = computed(() => ({ light: 'text-[#0A2540]', dark: 'text-[#EDEDED]', warm: 'text-[#292524]', pro: 'text-[#0F172A] font-mono' }[theme.value]))
const linkClass = computed(() => ({ light: 'text-[#8898AA] hover:text-[#635BFF]', dark: 'text-[#6B6B6E] hover:text-[#818CF8]', warm: 'text-[#78716C] hover:text-[#D97706]', pro: 'text-[#64748B] hover:text-[#0284C7] font-mono text-xs' }[theme.value]))
const activeIndicatorClass = computed(() => ({ light: 'absolute left-0 top-1/2 -translate-y-1/2 w-[3px] h-4 rounded-r-full bg-rose-500', dark: 'absolute left-0 top-1/2 -translate-y-1/2 w-[3px] h-4 rounded-r-full bg-rose-400 shadow-[0_0_8px_rgba(244,63,94,0.4)]', warm: 'absolute left-0 top-1/2 -translate-y-1/2 w-[3px] h-4 rounded-r-full bg-[#DC2626]', pro: 'absolute left-0 top-1/2 -translate-y-1/2 w-[2px] h-3.5 rounded-r-sm bg-rose-600' }[theme.value]))

function navItemClass(path: string) {
  const active = route.path === path
  return ({
    light: active ? 'bg-rose-500/10 text-rose-600 font-medium' : 'text-[#425466] hover:bg-[#F6F9FC] hover:text-[#0A2540]',
    dark: active ? 'bg-rose-400/10 text-rose-400 font-medium' : 'text-[#6B6B6E] hover:bg-white/[0.03] hover:text-[#EDEDED]',
    warm: active ? 'bg-[#DC2626]/10 text-[#DC2626] font-medium' : 'text-[#78716C] hover:bg-[#FEF3C7]/50 hover:text-[#292524]',
    pro: active ? 'bg-rose-600/15 text-rose-300 font-medium' : 'text-[#94A3B8] hover:bg-white/[0.05] hover:text-white',
  }[theme.value] || '')
}
</script>
