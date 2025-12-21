/**
 * Design System 初始化脚本
 *
 * 在应用启动时初始化主题和 CSS 变量
 */

import { themes, defaultTheme, type Theme } from './tokens';

// 存储键
const THEME_STORAGE_KEY = 'app-theme';

// 有效主题列表
const VALID_THEMES: Theme[] = ['light', 'dark', 'warm'];

/**
 * 从 localStorage 读取主题
 */
const getStoredTheme = (): Theme | null => {
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
 * 应用 CSS 变量到 :root
 */
const applyCssVars = (theme: Theme): void => {
  const root = document.documentElement;
  const vars = themes[theme];

  // 设置 data-theme 属性
  root.setAttribute('data-theme', theme);

  // 应用 CSS 变量
  Object.entries(vars).forEach(([key, value]) => {
    root.style.setProperty(key, value);
  });
};

/**
 * 初始化 Design System
 * 在应用启动时调用
 */
export const initDesignSystem = (): void => {
  // 获取主题：localStorage > 默认
  const theme = getStoredTheme() || defaultTheme;

  // 应用主题
  applyCssVars(theme);

  // 在控制台输出初始化信息（开发环境）
  if (import.meta.env.DEV) {
    console.log(`[Design System] Initialized with theme: ${theme}`);
  }
};

/**
 * 在 HTML 加载时立即初始化
 * 防止页面闪烁（FOUC）
 */
export const initThemeImmediate = (): void => {
  // 这个脚本可以内联到 index.html 的 <head> 中
  // 以确保主题在页面渲染前应用
  const script = `
    (function() {
      try {
        var theme = localStorage.getItem('${THEME_STORAGE_KEY}') || '${defaultTheme}';
        document.documentElement.setAttribute('data-theme', theme);
      } catch(e) {}
    })();
  `;
  return script;
};
