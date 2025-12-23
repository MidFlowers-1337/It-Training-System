<template>
  <div class="min-h-screen bg-bg-primary flex">
    <!-- Sidebar -->
    <aside
      class="bg-bg-secondary/70 backdrop-blur-xl border-r border-border-color/60 flex flex-col transition-all duration-300"
      :class="isCollapse ? 'w-16' : 'w-64'"
    >
      <div class="h-16 flex items-center justify-center border-b border-border-color/60">
        <div class="w-8 h-8 rounded-xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-bold text-base">
          IT
        </div>
        <span v-if="!isCollapse" class="ml-3 font-bold text-text-primary text-lg">讲师工作台</span>
      </div>

      <nav class="flex-1 py-4 overflow-y-auto">
        <ul class="space-y-1 px-2">
          <li v-for="item in menuItems" :key="item.path">
            <router-link
              :to="item.path"
              class="flex items-center px-3 py-2.5 rounded-xl text-text-secondary hover:text-text-primary hover:bg-bg-tertiary/60 transition-all border border-transparent"
              active-class="bg-primary/10 text-primary border-primary/20"
            >
              <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
              <span v-if="!isCollapse" class="ml-3 font-medium">{{ item.title }}</span>
            </router-link>
          </li>
        </ul>
      </nav>

      <button
        type="button"
        @click="toggleCollapse"
        class="h-12 flex items-center justify-center border-t border-border-color/60 text-text-secondary hover:text-text-primary hover:bg-bg-tertiary/60 transition-colors"
      >
        <component :is="isCollapse ? IconChevronsRight : IconChevronsLeft" class="w-5 h-5" />
      </button>
    </aside>

    <!-- Main -->
    <div class="flex-1 flex flex-col min-w-0">
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
                {{ userInitial }}
              </div>
              <div class="hidden md:block text-left">
                <div class="text-sm font-medium text-text-primary">{{ userStore.realName || userStore.username || '讲师' }}</div>
                <div class="text-xs text-text-muted">讲师</div>
              </div>
              <IconChevronDown class="w-4 h-4 text-text-muted" />
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="student">学员端</el-dropdown-item>
                <el-dropdown-item command="logout" divided class="text-error">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

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
import { computed, ref, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import ThemeSwitcher from '@/components/ThemeSwitcher.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

// 内联 SVG 图标组件
const IconCalendarDays = {
  render: () => h('svg', { class: 'w-5 h-5', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M8 2v4' }), h('path', { d: 'M16 2v4' }),
    h('rect', { width: '18', height: '18', x: '3', y: '4', rx: '2' }),
    h('path', { d: 'M3 10h18' }),
    h('path', { d: 'M8 14h.01' }), h('path', { d: 'M12 14h.01' }), h('path', { d: 'M16 14h.01' }),
    h('path', { d: 'M8 18h.01' }), h('path', { d: 'M12 18h.01' }), h('path', { d: 'M16 18h.01' })
  ])
}

const IconChevronDown = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'm6 9 6 6 6-6' })
  ])
}

const IconChevronsLeft = {
  render: () => h('svg', { class: 'w-5 h-5', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'm11 17-5-5 5-5' }), h('path', { d: 'm18 17-5-5 5-5' })
  ])
}

const IconChevronsRight = {
  render: () => h('svg', { class: 'w-5 h-5', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'm6 17 5-5-5-5' }), h('path', { d: 'm13 17 5-5-5-5' })
  ])
}

const menuItems = [{ path: '/instructor/sessions', title: '我的班期', icon: IconCalendarDays }]

const currentRouteName = computed(() => {
  const item = menuItems.find((i) => i.path === route.path)
  return item ? item.title : '讲师工作台'
})

const userInitial = computed(() => {
  const name = userStore.realName || userStore.username || 'T'
  return name.charAt(0).toUpperCase()
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'student') {
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
