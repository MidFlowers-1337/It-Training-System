<template>
  <div :class="['instructor-layout', `instructor-layout--${theme}`]">
    <!-- ─── Top Navbar ─── -->
    <header :class="['instructor-navbar', scrolled && 'shadow-sm']">
      <div class="flex items-center gap-8">
        <!-- Logo -->
        <router-link to="/instructor/sessions" class="flex items-center gap-2.5 hover:opacity-80 transition-opacity">
          <div class="instructor-logo">
            <span class="text-white font-bold text-xs">讲</span>
          </div>
          <span class="instructor-brand-text">讲师工作台</span>
        </router-link>

        <!-- Desktop Nav -->
        <nav class="hidden lg:flex items-center gap-1">
          <router-link to="/instructor/sessions"
            :class="['instructor-nav-link', route.path === '/instructor/sessions' && 'instructor-nav-link--active']">
            <CalendarRangeIcon class="w-4 h-4" :stroke-width="1.75" />
            我的班期
            <div v-if="route.path === '/instructor/sessions'" class="instructor-active-bar"></div>
          </router-link>
        </nav>
      </div>

      <div class="flex items-center gap-3">
        <ThemeSwitcher />
        <router-link to="/student/dashboard" class="instructor-back-link">
          <ArrowLeft class="w-3.5 h-3.5" :stroke-width="1.75" />
          返回前台
        </router-link>
      </div>
    </header>

    <!-- ─── Mobile Nav (hamburger) ─── -->
    <Transition name="instructor-slide">
      <div v-if="mobileMenuOpen" class="instructor-mobile-menu lg:hidden">
        <router-link to="/instructor/sessions" @click="mobileMenuOpen = false"
          :class="['instructor-mobile-link', route.path === '/instructor/sessions' && 'instructor-mobile-link--active']">
          <CalendarRangeIcon class="w-4 h-4" :stroke-width="1.75" />
          我的班期
        </router-link>
        <div class="instructor-mobile-divider"></div>
        <router-link to="/student/dashboard" @click="mobileMenuOpen = false" class="instructor-mobile-link">
          <ArrowLeft class="w-4 h-4" :stroke-width="1.75" />
          返回前台
        </router-link>
      </div>
    </Transition>

    <!-- Mobile hamburger button (floats at top-right area within navbar on mobile) -->
    <button @click="mobileMenuOpen = !mobileMenuOpen" class="instructor-hamburger lg:hidden">
      <Menu class="w-5 h-5" :stroke-width="1.75" />
    </button>

    <!-- ─── Content ─── -->
    <main class="instructor-content">
      <div class="max-w-7xl mx-auto px-6 lg:px-8 py-6 lg:py-8">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { useScrolled } from '@/composables/useScrolled'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import { CalendarRange as CalendarRangeIcon, ArrowLeft, Menu } from 'lucide-vue-next'

const route = useRoute()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const { scrolled } = useScrolled()
const mobileMenuOpen = ref(false)
</script>

<style scoped>
/* ── Layout ── */
.instructor-layout {
  min-height: 100vh;
}

/* ── Navbar ── */
.instructor-navbar {
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 40;
  transition: box-shadow 0.2s;
}

.instructor-logo {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.instructor-nav-link {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.15s;
}

.instructor-active-bar {
  position: absolute;
  bottom: -9px;
  left: 14px;
  right: 14px;
  height: 2px;
  border-radius: 1px;
}

.instructor-back-link {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  transition: color 0.15s, opacity 0.15s;
}
.instructor-back-link:hover { opacity: 0.8; }

/* ── Content ── */
.instructor-content {
  flex: 1;
  min-height: calc(100vh - 3rem);
}

/* ── Mobile hamburger ── */
.instructor-hamburger {
  position: fixed;
  top: 8px;
  right: 16px;
  z-index: 41;
  padding: 6px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: background 0.15s;
}

/* ── Mobile Menu ── */
.instructor-mobile-menu {
  position: fixed;
  top: 3rem;
  left: 0;
  right: 0;
  z-index: 39;
  padding: 8px 16px;
}

.instructor-mobile-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: background 0.15s;
}

.instructor-mobile-divider {
  height: 1px;
  margin: 4px 0;
}

/* ══════════════════════════════════════════
   Theme: Light (Notion-like)
   ══════════════════════════════════════════ */
.instructor-layout--light {
  background: #F6F9FC;
  color: #0A2540;
}
.instructor-layout--light .instructor-navbar {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid #E3E8EE;
}
.instructor-layout--light .instructor-logo {
  background: #059669;
}
.instructor-layout--light .instructor-brand-text {
  color: #0A2540;
  font-weight: 600;
  font-size: 14px;
}
.instructor-layout--light .instructor-nav-link {
  color: #425466;
}
.instructor-layout--light .instructor-nav-link:hover { color: #0A2540; }
.instructor-layout--light .instructor-nav-link--active { color: #059669; }
.instructor-layout--light .instructor-active-bar {
  background: #059669;
}
.instructor-layout--light .instructor-back-link {
  color: #8898AA;
}
.instructor-layout--light .instructor-back-link:hover { color: #059669; }
.instructor-layout--light .instructor-hamburger {
  color: #425466;
  background: transparent;
}
.instructor-layout--light .instructor-hamburger:hover { background: #F6F9FC; }
.instructor-layout--light .instructor-mobile-menu {
  background: #FFFFFF;
  border-bottom: 1px solid #E3E8EE;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.instructor-layout--light .instructor-mobile-link { color: #425466; }
.instructor-layout--light .instructor-mobile-link:hover { background: #F6F9FC; }
.instructor-layout--light .instructor-mobile-link--active {
  color: #059669;
  background: rgba(5, 150, 105, 0.08);
}
.instructor-layout--light .instructor-mobile-divider { background: #E3E8EE; }

/* ══════════════════════════════════════════
   Theme: Dark (Linear-like)
   ══════════════════════════════════════════ */
.instructor-layout--dark {
  background: #08090A;
  color: #EDEDED;
}
.instructor-layout--dark .instructor-navbar {
  background: rgba(8, 9, 10, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}
.instructor-layout--dark .instructor-logo {
  background: #34D399;
}
.instructor-layout--dark .instructor-brand-text {
  color: #EDEDED;
  font-weight: 600;
  font-size: 14px;
}
.instructor-layout--dark .instructor-nav-link {
  color: #6B6B6E;
}
.instructor-layout--dark .instructor-nav-link:hover { color: #A0A0A5; }
.instructor-layout--dark .instructor-nav-link--active { color: #34D399; }
.instructor-layout--dark .instructor-active-bar {
  background: #34D399;
  box-shadow: 0 0 8px rgba(52, 211, 153, 0.5), 0 0 20px rgba(52, 211, 153, 0.2);
}
.instructor-layout--dark .instructor-back-link {
  color: #6B6B6E;
}
.instructor-layout--dark .instructor-back-link:hover { color: #34D399; }
.instructor-layout--dark .instructor-hamburger {
  color: #6B6B6E;
  background: transparent;
}
.instructor-layout--dark .instructor-hamburger:hover { background: rgba(255, 255, 255, 0.04); }
.instructor-layout--dark .instructor-mobile-menu {
  background: #161618;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.5);
}
.instructor-layout--dark .instructor-mobile-link { color: #A0A0A5; }
.instructor-layout--dark .instructor-mobile-link:hover { background: rgba(255, 255, 255, 0.04); }
.instructor-layout--dark .instructor-mobile-link--active {
  color: #34D399;
  background: rgba(52, 211, 153, 0.08);
}
.instructor-layout--dark .instructor-mobile-divider { background: rgba(255, 255, 255, 0.04); }

/* ══════════════════════════════════════════
   Theme: Warm (Slack-like)
   ══════════════════════════════════════════ */
.instructor-layout--warm {
  background: #FFFBF5;
  color: #292524;
}
.instructor-layout--warm .instructor-navbar {
  background: rgba(255, 251, 245, 0.9);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #E7E5E4;
}
.instructor-layout--warm .instructor-logo {
  background: #059669;
  border-radius: 10px;
}
.instructor-layout--warm .instructor-brand-text {
  color: #292524;
  font-weight: 700;
  font-size: 14px;
}
.instructor-layout--warm .instructor-nav-link {
  color: #78716C;
}
.instructor-layout--warm .instructor-nav-link:hover { color: #292524; }
.instructor-layout--warm .instructor-nav-link--active { color: #059669; }
.instructor-layout--warm .instructor-active-bar {
  background: #059669;
}
.instructor-layout--warm .instructor-back-link {
  color: #78716C;
}
.instructor-layout--warm .instructor-back-link:hover { color: #059669; }
.instructor-layout--warm .instructor-hamburger {
  color: #78716C;
  background: transparent;
}
.instructor-layout--warm .instructor-hamburger:hover { background: #FEF3C7; }
.instructor-layout--warm .instructor-mobile-menu {
  background: #FFFFFF;
  border-bottom: 1px solid #E7E5E4;
  box-shadow: 0 4px 12px rgba(120, 80, 20, 0.08);
}
.instructor-layout--warm .instructor-mobile-link { color: #78716C; }
.instructor-layout--warm .instructor-mobile-link:hover { background: #FEF3C7; }
.instructor-layout--warm .instructor-mobile-link--active {
  color: #059669;
  background: rgba(5, 150, 105, 0.08);
}
.instructor-layout--warm .instructor-mobile-divider { background: #E7E5E4; }

/* ══════════════════════════════════════════
   Theme: Pro (Vercel-like)
   ══════════════════════════════════════════ */
.instructor-layout--pro {
  background: #F8FAFC;
  color: #0F172A;
  font-family: ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
}
.instructor-layout--pro .instructor-navbar {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid #E2E8F0;
}
.instructor-layout--pro .instructor-logo {
  background: transparent;
  width: auto;
  height: auto;
}
.instructor-layout--pro .instructor-logo span {
  color: #059669;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 16px;
  font-weight: 900;
}
.instructor-layout--pro .instructor-brand-text {
  color: #0F172A;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-weight: 600;
  font-size: 13px;
}
.instructor-layout--pro .instructor-nav-link {
  color: #64748B;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
}
.instructor-layout--pro .instructor-nav-link:hover { color: #0F172A; }
.instructor-layout--pro .instructor-nav-link--active { color: #059669; font-weight: 600; }
.instructor-layout--pro .instructor-active-bar {
  background: #059669;
  height: 1px;
}
.instructor-layout--pro .instructor-back-link {
  color: #64748B;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
  font-size: 12px;
}
.instructor-layout--pro .instructor-back-link:hover { color: #059669; }
.instructor-layout--pro .instructor-hamburger {
  color: #64748B;
  background: transparent;
}
.instructor-layout--pro .instructor-hamburger:hover { background: #F1F5F9; }
.instructor-layout--pro .instructor-mobile-menu {
  background: #FFFFFF;
  border-bottom: 1px solid #E2E8F0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}
.instructor-layout--pro .instructor-mobile-link {
  color: #64748B;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', Menlo, monospace;
}
.instructor-layout--pro .instructor-mobile-link:hover { background: #F1F5F9; }
.instructor-layout--pro .instructor-mobile-link--active {
  color: #059669;
  background: rgba(5, 150, 105, 0.06);
}
.instructor-layout--pro .instructor-mobile-divider { background: #E2E8F0; }

/* ── Transitions ── */
.instructor-slide-enter-active { transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1); }
.instructor-slide-leave-active { transition: all 0.15s ease-in; }
.instructor-slide-enter-from { opacity: 0; transform: translateY(-8px); }
.instructor-slide-leave-to { opacity: 0; transform: translateY(-4px); }
</style>
