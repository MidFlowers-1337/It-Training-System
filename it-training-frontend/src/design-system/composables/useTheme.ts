import { ref, watch } from 'vue'
import { usePreferredDark, useStorage } from '@vueuse/core'
import type { ThemeName } from '../tokens/colors'
import { THEME_NAMES } from '../tokens/colors'

const STORAGE_KEY = 'it-training-theme'

// ── 模块级单例：所有 useTheme() 调用共享同一个 reactive ref ──
let _initialized = false
const _stored = useStorage<ThemeName>(STORAGE_KEY, 'light')
const _theme = ref<ThemeName>(_stored.value)

function _applyTheme(name: ThemeName) {
  document.documentElement.setAttribute('data-theme', name)
  _theme.value = name
  _stored.value = name
}

export function useTheme() {
  const prefersDark = usePreferredDark()

  function applyTheme(name: ThemeName) {
    _applyTheme(name)
  }

  function initTheme() {
    const saved = _stored.value
    if (saved && THEME_NAMES.includes(saved)) {
      _applyTheme(saved)
    } else if (prefersDark.value) {
      _applyTheme('dark')
    } else {
      _applyTheme('light')
    }
  }

  function cycleTheme() {
    const idx = THEME_NAMES.indexOf(_theme.value)
    const next = THEME_NAMES[(idx + 1) % THEME_NAMES.length]!
    _applyTheme(next)
  }

  // Watch system preference changes — 只注册一次
  if (!_initialized) {
    _initialized = true
    watch(prefersDark, (dark) => {
      if (!_stored.value) {
        _applyTheme(dark ? 'dark' : 'light')
      }
    })
  }

  return {
    theme: _theme,
    applyTheme,
    initTheme,
    cycleTheme,
    THEME_NAMES,
  }
}
