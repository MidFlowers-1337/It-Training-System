/**
 * Design System - 颜色系统
 * 支持三套主题：Light (晴空白) | Dark (深空黑) | Warm (暖阳橙)
 *
 * 实现机制：使用 CSS 变量，由主题对象动态注入到 <html> 标签
 */

// 主题类型定义
export type Theme = 'light' | 'dark' | 'warm';

// CSS 变量映射类型
export interface ThemeColors {
  // 背景色
  '--bg-primary': string;
  '--bg-secondary': string;
  '--bg-tertiary': string;
  '--bg-hover': string;
  '--bg-card': string;
  '--bg-inverse': string;

  // 背景色 RGB 值（用于 Tailwind 透明度）
  '--bg-primary-rgb': string;
  '--bg-secondary-rgb': string;
  '--bg-tertiary-rgb': string;
  '--bg-hover-rgb': string;
  '--bg-card-rgb': string;
  '--bg-inverse-rgb': string;

  // 文本色
  '--text-primary': string;
  '--text-secondary': string;
  '--text-muted': string;
  '--text-disabled': string;
  '--text-inverse': string;

  // 文本色 RGB 值
  '--text-primary-rgb': string;
  '--text-secondary-rgb': string;
  '--text-muted-rgb': string;
  '--text-disabled-rgb': string;
  '--text-inverse-rgb': string;

  // 品牌色
  '--primary-color': string;
  '--primary-light': string;
  '--primary-dark': string;
  '--primary-color-alpha': string;

  // 品牌色 RGB 值
  '--primary-color-rgb': string;
  '--primary-light-rgb': string;
  '--primary-dark-rgb': string;

  // 边框色
  '--border-color': string;
  '--border-light': string;
  '--border-dark': string;

  // 边框色 RGB 值
  '--border-color-rgb': string;
  '--border-light-rgb': string;
  '--border-dark-rgb': string;

  // 状态色
  '--success-color': string;
  '--warning-color': string;
  '--error-color': string;
  '--info-color': string;

  // 状态色 RGB 值
  '--success-color-rgb': string;
  '--warning-color-rgb': string;
  '--error-color-rgb': string;
  '--info-color-rgb': string;

  // 玻璃效果
  '--glass-bg': string;
  '--glass-border': string;
  '--separator': string;
}

// 辅助函数：从 HEX 提取 RGB 值
const hexToRgb = (hex: string): string => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (!result) return '0 0 0';
  return `${parseInt(result[1], 16)} ${parseInt(result[2], 16)} ${parseInt(result[3], 16)}`;
};

// Light 主题 (晴空白) - Apple 风格，清新专业
const lightColors = {
  bg: {
    primary: '#F5F5F7',     // Apple Light Gray
    secondary: '#FFFFFF',   // 纯白
    tertiary: '#E5E5E5',    // 浅灰
    hover: '#F9FAFB',       // 悬停高亮
    card: '#FFFFFF',
    inverse: '#1D1D1F',
  },
  text: {
    primary: '#1D1D1F',     // Apple 深灰黑
    secondary: '#86868B',   // 中灰
    muted: '#9CA3AF',       // 浅灰
    disabled: '#D1D5DB',
    inverse: '#FFFFFF',
  },
  brand: {
    primary: '#0071E3',     // Apple Blue
    light: '#0A84FF',       // 亮蓝
    dark: '#0058B0',        // 深蓝
  },
  border: {
    default: '#E5E5E5',
    light: '#F3F4F6',
    dark: '#D1D5DB',
  },
  status: {
    success: '#059669',
    warning: '#D97706',
    error: '#DC2626',
    info: '#0071E3',
  },
};

// Dark 主题 (深空黑) - OLED 友好，高对比度
const darkColors = {
  bg: {
    primary: '#000000',     // Pure Black (OLED)
    secondary: '#1C1C1E',   // Dark Gray
    tertiary: '#38383A',    // 中灰
    hover: '#48484A',       // 悬停
    card: '#1C1C1E',
    inverse: '#F5F5F7',
  },
  text: {
    primary: '#F5F5F7',     // 亮白
    secondary: '#A1A1A6',   // 灰白
    muted: '#86868B',       // 中灰
    disabled: '#48484A',
    inverse: '#1D1D1F',
  },
  brand: {
    primary: '#0A84FF',     // 亮蓝（深色模式专用）
    light: '#5AC8FA',       // 浅蓝
    dark: '#0058B0',        // 深蓝
  },
  border: {
    default: '#38383A',
    light: '#48484A',
    dark: '#1C1C1E',
  },
  status: {
    success: '#30D158',
    warning: '#FFD60A',
    error: '#FF453A',
    info: '#0A84FF',
  },
};

// Warm 主题 (暖阳橙) - 护眼舒适
const warmColors = {
  bg: {
    primary: '#FFF8F0',     // 暖白
    secondary: '#FFFFFF',   // 纯白
    tertiary: '#FFE8CD',    // 浅橙
    hover: '#FFF2EB',       // 悬停
    card: '#FFFFFF',
    inverse: '#4A3B32',
  },
  text: {
    primary: '#4A3B32',     // 深棕
    secondary: '#8C7B70',   // 中棕
    muted: '#A89890',       // 浅棕
    disabled: '#D6D3D1',
    inverse: '#FFFFFF',
  },
  brand: {
    primary: '#FF9500',     // Orange
    light: '#FFAC33',       // 亮橙
    dark: '#CC7700',        // 深橙
  },
  border: {
    default: '#FFE0C0',
    light: '#FFE8D0',
    dark: '#FFD6BA',
  },
  status: {
    success: '#32B566',
    warning: '#FF9500',
    error: '#E53935',
    info: '#FF9500',
  },
};

// 生成主题 CSS 变量对象
const createThemeVars = (colors: typeof lightColors): ThemeColors => ({
  // 背景色
  '--bg-primary': colors.bg.primary,
  '--bg-secondary': colors.bg.secondary,
  '--bg-tertiary': colors.bg.tertiary,
  '--bg-hover': colors.bg.hover,
  '--bg-card': colors.bg.card,
  '--bg-inverse': colors.bg.inverse,

  // 背景色 RGB
  '--bg-primary-rgb': hexToRgb(colors.bg.primary),
  '--bg-secondary-rgb': hexToRgb(colors.bg.secondary),
  '--bg-tertiary-rgb': hexToRgb(colors.bg.tertiary),
  '--bg-hover-rgb': hexToRgb(colors.bg.hover),
  '--bg-card-rgb': hexToRgb(colors.bg.card),
  '--bg-inverse-rgb': hexToRgb(colors.bg.inverse),

  // 文本色
  '--text-primary': colors.text.primary,
  '--text-secondary': colors.text.secondary,
  '--text-muted': colors.text.muted,
  '--text-disabled': colors.text.disabled,
  '--text-inverse': colors.text.inverse,

  // 文本色 RGB
  '--text-primary-rgb': hexToRgb(colors.text.primary),
  '--text-secondary-rgb': hexToRgb(colors.text.secondary),
  '--text-muted-rgb': hexToRgb(colors.text.muted),
  '--text-disabled-rgb': hexToRgb(colors.text.disabled),
  '--text-inverse-rgb': hexToRgb(colors.text.inverse),

  // 品牌色
  '--primary-color': colors.brand.primary,
  '--primary-light': colors.brand.light,
  '--primary-dark': colors.brand.dark,
  '--primary-color-alpha': `${colors.brand.primary}1A`, // 10% alpha

  // 品牌色 RGB
  '--primary-color-rgb': hexToRgb(colors.brand.primary),
  '--primary-light-rgb': hexToRgb(colors.brand.light),
  '--primary-dark-rgb': hexToRgb(colors.brand.dark),

  // 边框色
  '--border-color': colors.border.default,
  '--border-light': colors.border.light,
  '--border-dark': colors.border.dark,

  // 边框色 RGB
  '--border-color-rgb': hexToRgb(colors.border.default),
  '--border-light-rgb': hexToRgb(colors.border.light),
  '--border-dark-rgb': hexToRgb(colors.border.dark),

  // 状态色
  '--success-color': colors.status.success,
  '--warning-color': colors.status.warning,
  '--error-color': colors.status.error,
  '--info-color': colors.status.info,

  // 状态色 RGB
  '--success-color-rgb': hexToRgb(colors.status.success),
  '--warning-color-rgb': hexToRgb(colors.status.warning),
  '--error-color-rgb': hexToRgb(colors.status.error),
  '--info-color-rgb': hexToRgb(colors.status.info),

  // 玻璃效果
  '--glass-bg': `rgb(${hexToRgb(colors.bg.secondary)} / 0.72)`,
  '--glass-border': `rgb(${hexToRgb(colors.border.default)} / 0.55)`,
  '--separator': `rgb(${hexToRgb(colors.border.default)} / 0.65)`,
});

// 主题配置导出
export const themes: Record<Theme, ThemeColors> = {
  light: createThemeVars(lightColors),
  dark: createThemeVars(darkColors),
  warm: createThemeVars(warmColors),
};

// 默认主题
export const defaultTheme: Theme = 'light';

// 主题名称映射（用于 UI 显示）
export const themeNames: Record<Theme, string> = {
  light: '晴空白',
  dark: '深空黑',
  warm: '暖阳橙',
};

// 应用主题到 DOM
export const applyTheme = (theme: Theme): void => {
  const root = document.documentElement;
  const vars = themes[theme];

  // 设置 data-theme 属性
  root.setAttribute('data-theme', theme);

  // 应用 CSS 变量
  Object.entries(vars).forEach(([key, value]) => {
    root.style.setProperty(key, value);
  });
};
