import { ref } from 'vue'

const THEME_KEY = 'app-theme'
const DEFAULT_THEME = 'default'

const THEMES = [
  { value: 'default', label: '默认' },
  { value: 'warm', label: '暖色' },
  { value: 'dark', label: '深色' },
]

const THEME_VALUES = new Set(THEMES.map((theme) => theme.value))

const safeReadTheme = () => {
  if (typeof window === 'undefined') return DEFAULT_THEME
  try {
    return localStorage.getItem(THEME_KEY) || DEFAULT_THEME
  } catch {
    return DEFAULT_THEME
  }
}

const normalizeTheme = (value) => (THEME_VALUES.has(value) ? value : DEFAULT_THEME)

const currentTheme = ref(normalizeTheme(safeReadTheme()))

const applyThemeToDom = (theme) => {
  if (typeof document === 'undefined') return
  document.documentElement.setAttribute('data-theme', theme)
}

applyThemeToDom(currentTheme.value)

export function useTheme() {
  const setTheme = (theme) => {
    const normalized = normalizeTheme(theme)
    currentTheme.value = normalized
    applyThemeToDom(normalized)
    if (typeof window === 'undefined') return
    try {
      localStorage.setItem(THEME_KEY, normalized)
    } catch {
      // ignore
    }
  }

  return {
    currentTheme,
    setTheme,
    themes: THEMES,
  }
}

