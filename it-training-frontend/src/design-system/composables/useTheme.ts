/**
 * Design System - 主题切换 Composable
 *
 * 功能：
 * - 管理三套主题的切换
 * - 将 Design Tokens CSS 变量注入到 DOM
 * - 持久化主题偏好到 localStorage
 * - 支持系统主题检测
 */

import { ref, computed, watch, onMounted } from 'vue';
import {
  type Theme,
  themes,
  defaultTheme,
  themeNames,
  applyTheme,
} from '../tokens';

// 存储键
const THEME_STORAGE_KEY = 'app-theme';

// 有效主题列表
const VALID_THEMES: Theme[] = ['light', 'dark', 'warm'];

// 全局响应式主题状态（单例模式）
const currentTheme = ref<Theme>(defaultTheme);
const isInitialized = ref(false);

/**
 * 安全读取 localStorage
 */
const safeGetStoredTheme = (): Theme | null => {
  if (typeof window === 'undefined') return null;
  try {
    const stored = localStorage.getItem(THEME_STORAGE_KEY);
    if (stored && VALID_THEMES.includes(stored as Theme)) {
      return stored as Theme;
    }
  } catch {
    // localStorage 不可用
  }
  return null;
};

/**
 * 安全写入 localStorage
 */
const safeSetStoredTheme = (theme: Theme): void => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.setItem(THEME_STORAGE_KEY, theme);
  } catch {
    // localStorage 不可用
  }
};

/**
 * 检测系统主题偏好
 */
const getSystemTheme = (): 'light' | 'dark' => {
  if (typeof window === 'undefined') return 'light';
  return window.matchMedia('(prefers-color-scheme: dark)').matches
    ? 'dark'
    : 'light';
};

/**
 * 初始化主题
 */
const initTheme = (): void => {
  if (isInitialized.value) return;

  // 优先级：localStorage > 系统偏好 > 默认
  const storedTheme = safeGetStoredTheme();
  if (storedTheme) {
    currentTheme.value = storedTheme;
  } else {
    // 可选：使用系统主题偏好
    // currentTheme.value = getSystemTheme();
    currentTheme.value = defaultTheme;
  }

  // 应用主题
  applyTheme(currentTheme.value);
  isInitialized.value = true;
};

/**
 * 主题切换 Composable
 */
export function useTheme() {
  // 确保初始化
  onMounted(() => {
    initTheme();
  });

  // 主题信息
  const themeInfo = computed(() => ({
    current: currentTheme.value,
    name: themeNames[currentTheme.value],
    isDark: currentTheme.value === 'dark',
    isLight: currentTheme.value === 'light',
    isWarm: currentTheme.value === 'warm',
  }));

  // 主题列表（用于选择器）
  const themeOptions = computed(() =>
    VALID_THEMES.map((theme) => ({
      value: theme,
      label: themeNames[theme],
    }))
  );

  /**
   * 设置主题
   */
  const setTheme = (theme: Theme): void => {
    if (!VALID_THEMES.includes(theme)) {
      console.warn(`[useTheme] Invalid theme: ${theme}`);
      return;
    }

    currentTheme.value = theme;
    applyTheme(theme);
    safeSetStoredTheme(theme);
  };

  /**
   * 循环切换主题
   */
  const toggleTheme = (): void => {
    const currentIndex = VALID_THEMES.indexOf(currentTheme.value);
    const nextIndex = (currentIndex + 1) % VALID_THEMES.length;
    setTheme(VALID_THEMES[nextIndex]);
  };

  /**
   * 重置为默认主题
   */
  const resetTheme = (): void => {
    setTheme(defaultTheme);
  };

  // 监听系统主题变化（可选功能）
  const watchSystemTheme = (): (() => void) => {
    if (typeof window === 'undefined') return () => {};

    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
    const handler = (e: MediaQueryListEvent) => {
      // 仅在没有用户手动设置时跟随系统
      if (!safeGetStoredTheme()) {
        setTheme(e.matches ? 'dark' : 'light');
      }
    };

    mediaQuery.addEventListener('change', handler);
    return () => mediaQuery.removeEventListener('change', handler);
  };

  return {
    // 状态
    currentTheme,
    themeInfo,
    themeOptions,

    // 方法
    setTheme,
    toggleTheme,
    resetTheme,
    watchSystemTheme,

    // 常量
    themes: VALID_THEMES,
    themeNames,
  };
}

// 导出类型
export type { Theme };
