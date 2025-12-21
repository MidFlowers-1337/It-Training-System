/**
 * Design System - 字体排印系统
 * 原则：严格的层级，使用系统字体栈
 */

// 字体家族
export const fontFamily = {
  // 系统字体栈 - Apple 风格
  sans: '-apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif',
  // 等宽字体
  mono: '"SF Mono", Monaco, "Cascadia Code", "Roboto Mono", Consolas, monospace',
} as const;

// 字体大小（px 值，便于精确控制）
export const fontSize = {
  xs: '12px',     // 12px - 标签、辅助文字
  sm: '14px',     // 14px - 辅助说明
  base: '17px',   // 17px - 正文（Apple 标准）
  lg: '20px',     // 20px - 分组标题
  xl: '24px',     // 24px - 小标题
  '2xl': '28px',  // 28px - 区块标题
  '3xl': '34px',  // 34px - 页面副标题
  '4xl': '40px',  // 40px - 页面大标题
  '5xl': '48px',  // 48px - 超大标题
} as const;

// 行高
export const lineHeight = {
  tight: '1.1',   // 紧凑 - 大标题
  snug: '1.25',   // 较紧 - 标题
  normal: '1.5',  // 正常 - 正文
  relaxed: '1.75', // 宽松 - 长文本
} as const;

// 字重
export const fontWeight = {
  normal: '400',    // 正常
  medium: '500',    // 中等
  semibold: '600',  // 半粗
  bold: '700',      // 粗体
} as const;

// 字间距
export const letterSpacing = {
  tight: '-0.02em',  // 紧凑 - 大标题
  normal: '0',       // 正常
  wide: '0.05em',    // 宽松 - 标签、大写
} as const;

// 预定义的字体样式组合（Tailwind classes）
export const textStyles = {
  // 页面大标题 - 40px, 紧凑行高, 半粗, 紧字距
  h1: 'text-[40px] leading-[1.1] font-semibold tracking-tight text-text-primary',

  // 区块标题 - 28px, 较紧行高, 半粗
  h2: 'text-[28px] leading-[1.2] font-semibold tracking-tight text-text-primary',

  // 分组标题 - 20px, 正常行高, 半粗
  h3: 'text-[20px] leading-[1.3] font-semibold text-text-primary',

  // 小标题 - 17px, 正常行高, 中等
  h4: 'text-[17px] leading-[1.4] font-medium text-text-primary',

  // 正文 - 17px, 宽松行高
  body: 'text-[17px] leading-[1.5] font-normal text-text-primary',

  // 正文（次要）- 17px, 次要色
  bodySecondary: 'text-[17px] leading-[1.5] font-normal text-text-secondary',

  // 辅助说明 - 14px, 次要色
  caption: 'text-[14px] leading-[1.4] font-normal text-text-secondary',

  // 标签 - 12px, 大写, 宽字距
  label: 'text-[12px] leading-[1.3] font-medium uppercase tracking-wider text-text-secondary',

  // 链接样式
  link: 'text-primary hover:text-primary-light transition-colors duration-fast cursor-pointer',
} as const;

// CSS 变量映射（用于 Tailwind 配置）
export const typographyCssVars = {
  '--font-sans': fontFamily.sans,
  '--font-mono': fontFamily.mono,
  '--text-xs': fontSize.xs,
  '--text-sm': fontSize.sm,
  '--text-base': fontSize.base,
  '--text-lg': fontSize.lg,
  '--text-xl': fontSize.xl,
  '--text-2xl': fontSize['2xl'],
  '--text-3xl': fontSize['3xl'],
  '--text-4xl': fontSize['4xl'],
  '--text-5xl': fontSize['5xl'],
  '--font-normal': fontWeight.normal,
  '--font-medium': fontWeight.medium,
  '--font-semibold': fontWeight.semibold,
  '--font-bold': fontWeight.bold,
  '--leading-tight': lineHeight.tight,
  '--leading-snug': lineHeight.snug,
  '--leading-normal': lineHeight.normal,
  '--leading-relaxed': lineHeight.relaxed,
} as const;
