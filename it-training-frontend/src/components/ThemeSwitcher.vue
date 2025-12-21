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
            <Check v-if="currentTheme === option.value" class="w-4 h-4 ml-auto text-primary" />
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { Check, Moon, Sparkles, Sun } from 'lucide-vue-next'
import { useTheme } from '@/composables/useTheme'

const { currentTheme, setTheme, themes } = useTheme()

const iconByTheme = {
  default: Sparkles,
  warm: Sun,
  dark: Moon,
}

const currentIcon = computed(() => iconByTheme[currentTheme.value] || Sparkles)

const options = computed(() =>
  themes.map((theme) => ({
    ...theme,
    icon: iconByTheme[theme.value] || Sparkles,
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

