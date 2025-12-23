<template>
  <el-dropdown trigger="click" @command="handleThemeChange">
    <button
      type="button"
      class="glass flex items-center justify-center w-10 h-10 rounded-full transition-colors hover:bg-bg-hover/60 focus:outline-none"
      aria-label="切换主题"
    >
      <component :is="currentIcon" class="w-4 h-4 text-text-secondary" />
    </button>

    <template #dropdown>
      <el-dropdown-menu class="min-w-[180px]">
        <el-dropdown-item
          v-for="option in options"
          :key="option.value"
          :command="option.value"
          :class="{ 'is-active': currentTheme === option.value }"
        >
          <div class="flex items-center gap-2 w-full">
            <component
              :is="option.icon"
              class="w-4 h-4"
              :class="currentTheme === option.value ? 'text-primary' : 'text-text-muted'"
            />
            <span class="text-sm">{{ option.label }}</span>
            <IconCheck v-if="currentTheme === option.value" class="w-4 h-4 ml-auto text-primary" />
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed, h } from 'vue'
import { useTheme } from '@/composables/useTheme'

const { currentTheme, setTheme, themes } = useTheme()

// 内联 SVG 图标组件
const IconSparkles = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M9.937 15.5A2 2 0 0 0 8.5 14.063l-6.135-1.582a.5.5 0 0 1 0-.962L8.5 9.936A2 2 0 0 0 9.937 8.5l1.582-6.135a.5.5 0 0 1 .963 0L14.063 8.5A2 2 0 0 0 15.5 9.937l6.135 1.581a.5.5 0 0 1 0 .964L15.5 14.063a2 2 0 0 0-1.437 1.437l-1.582 6.135a.5.5 0 0 1-.963 0z' }),
    h('path', { d: 'M20 3v4' }), h('path', { d: 'M22 5h-4' }), h('path', { d: 'M4 17v2' }), h('path', { d: 'M5 18H3' })
  ])
}

const IconSun = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('circle', { cx: '12', cy: '12', r: '4' }),
    h('path', { d: 'M12 2v2' }), h('path', { d: 'M12 20v2' }), h('path', { d: 'm4.93 4.93 1.41 1.41' }),
    h('path', { d: 'm17.66 17.66 1.41 1.41' }), h('path', { d: 'M2 12h2' }), h('path', { d: 'M20 12h2' }),
    h('path', { d: 'm6.34 17.66-1.41 1.41' }), h('path', { d: 'm19.07 4.93-1.41 1.41' })
  ])
}

const IconMoon = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z' })
  ])
}

const IconCheck = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M20 6 9 17l-5-5' })
  ])
}

const iconByTheme = {
  default: IconSparkles,
  warm: IconSun,
  dark: IconMoon,
}

const currentIcon = computed(() => iconByTheme[currentTheme.value] || IconSparkles)

const options = computed(() =>
  themes.map((theme) => ({
    ...theme,
    icon: iconByTheme[theme.value] || IconSparkles,
  })),
)

const handleThemeChange = (theme) => setTheme(theme)
</script>

<style scoped>
.is-active {
  color: var(--primary-color);
  background-color: rgb(var(--bg-tertiary-rgb) / 0.6);
  font-weight: 600;
}
</style>
