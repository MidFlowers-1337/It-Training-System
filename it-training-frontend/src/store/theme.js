import { defineStore } from 'pinia'

const THEME_STORAGE_KEY = 'theme-preference'

/**
 * Theme state management
 * Manages dark/light theme with persistence
 */
export const useThemeStore = defineStore('theme', {
  state: () => ({
    // Current theme: 'light' | 'dark' | 'system'
    theme: localStorage.getItem(THEME_STORAGE_KEY) || 'system',

    // Resolved theme after considering system preference
    resolvedTheme: 'light'
  }),

  getters: {
    /**
     * Check if dark mode is active
     */
    isDark: (state) => state.resolvedTheme === 'dark',

    /**
     * Check if light mode is active
     */
    isLight: (state) => state.resolvedTheme === 'light',

    /**
     * Get current theme preference
     */
    currentTheme: (state) => state.theme
  },

  actions: {
    /**
     * Initialize theme on app start
     */
    initTheme() {
      this.resolveTheme()
      this.applyTheme()
      this.watchSystemTheme()
    },

    /**
     * Set theme preference
     */
    setTheme(theme) {
      this.theme = theme
      localStorage.setItem(THEME_STORAGE_KEY, theme)
      this.resolveTheme()
      this.applyTheme()
    },

    /**
     * Toggle between light and dark
     */
    toggleTheme() {
      const newTheme = this.resolvedTheme === 'dark' ? 'light' : 'dark'
      this.setTheme(newTheme)
    },

    /**
     * Resolve theme based on preference and system
     */
    resolveTheme() {
      if (this.theme === 'system') {
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches
        this.resolvedTheme = prefersDark ? 'dark' : 'light'
      } else {
        this.resolvedTheme = this.theme
      }
    },

    /**
     * Apply theme to document
     */
    applyTheme() {
      const root = document.documentElement

      if (this.resolvedTheme === 'dark') {
        root.classList.add('dark')
        root.setAttribute('data-theme', 'dark')
      } else {
        root.classList.remove('dark')
        root.setAttribute('data-theme', 'light')
      }
    },

    /**
     * Watch system theme changes
     */
    watchSystemTheme() {
      const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')

      mediaQuery.addEventListener('change', () => {
        if (this.theme === 'system') {
          this.resolveTheme()
          this.applyTheme()
        }
      })
    }
  }
})
