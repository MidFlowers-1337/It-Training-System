import { defineStore } from 'pinia'
import { useTheme } from '@/design-system/composables/useTheme'

export const useThemeStore = defineStore('theme', () => {
  const { theme, applyTheme, initTheme, cycleTheme, THEME_NAMES } = useTheme()

  return {
    theme,
    applyTheme,
    initTheme,
    cycleTheme,
    THEME_NAMES,
  }
})
