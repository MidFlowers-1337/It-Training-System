/**
 * Design System - 间距与网格系统
 * 原则：4px 基准网格，大留白
 */

// 基础间距单位（4px 网格）
export const spacing = {
  0: '0px',
  1: '4px',      // 0.25rem
  2: '8px',      // 0.5rem
  3: '12px',     // 0.75rem
  4: '16px',     // 1rem
  5: '20px',     // 1.25rem
  6: '24px',     // 1.5rem
  8: '32px',     // 2rem
  10: '40px',    // 2.5rem
  12: '48px',    // 3rem
  16: '64px',    // 4rem - 区块间距
  20: '80px',    // 5rem
  24: '96px',    // 6rem
} as const;

// 布局间距
export const layoutSpacing = {
  // 页面内边距
  pagePaddingX: '40px',      // 左右
  pagePaddingY: '32px',      // 上下

  // 响应式页面内边距
  pagePaddingXMobile: '16px',
  pagePaddingXTablet: '24px',

  // 区块间距
  sectionGap: '64px',        // 主要区块间

  // 列表项间距
  itemGap: '16px',           // 列表项间
  itemGapCompact: '8px',     // 紧凑列表

  // 组内间距
  groupGap: '24px',          // 分组间
  inlineGap: '8px',          // 行内元素间
} as const;

// 组件尺寸
export const componentSizing = {
  // 输入框 / 按钮高度
  inputHeight: '44px',       // iOS 最小可点击区域
  inputHeightSm: '36px',
  inputHeightLg: '52px',

  // 圆角
  radiusSm: '6px',           // 小圆角
  radiusMd: '8px',           // 中等圆角
  radiusLg: '12px',          // 大圆角
  radiusXl: '16px',          // 超大圆角
  radius2xl: '24px',         // 卡片圆角
  radiusFull: '9999px',      // 全圆

  // 图标尺寸
  iconSm: '16px',
  iconMd: '20px',
  iconLg: '24px',
  iconXl: '32px',
} as const;

// 容器宽度
export const containerWidth = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1440px',
  full: '100%',
} as const;

// 布局尺寸
export const layoutSizing = {
  // 导航栏高度
  navHeight: '64px',
  navHeightMobile: '56px',

  // 侧边栏宽度
  sidebarWidth: '240px',
  sidebarCollapsedWidth: '64px',

  // 内容区最大宽度
  contentMaxWidth: '1280px',
} as const;

// 阴影
export const shadows = {
  none: 'none',
  sm: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
  md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -2px rgba(0, 0, 0, 0.1)',
  lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',
  xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
  '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
} as const;

// 深色模式阴影
export const shadowsDark = {
  sm: '0 1px 2px 0 rgba(0, 0, 0, 0.3)',
  md: '0 4px 6px -1px rgba(0, 0, 0, 0.3)',
  lg: '0 10px 15px -3px rgba(0, 0, 0, 0.3)',
  xl: '0 20px 25px -5px rgba(0, 0, 0, 0.3)',
  '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.4)',
} as const;

// Z-index 层级
export const zIndex = {
  dropdown: 1000,
  sticky: 1020,
  fixed: 1030,
  modalBackdrop: 1040,
  modal: 1050,
  popover: 1060,
  tooltip: 1070,
} as const;

// CSS 变量映射
export const spacingCssVars = {
  '--space-0': spacing[0],
  '--space-1': spacing[1],
  '--space-2': spacing[2],
  '--space-3': spacing[3],
  '--space-4': spacing[4],
  '--space-5': spacing[5],
  '--space-6': spacing[6],
  '--space-8': spacing[8],
  '--space-10': spacing[10],
  '--space-12': spacing[12],
  '--space-16': spacing[16],
  '--space-20': spacing[20],
  '--space-24': spacing[24],
  '--radius-sm': componentSizing.radiusSm,
  '--radius-md': componentSizing.radiusMd,
  '--radius-lg': componentSizing.radiusLg,
  '--radius-xl': componentSizing.radiusXl,
  '--radius-2xl': componentSizing.radius2xl,
  '--radius-full': componentSizing.radiusFull,
  '--shadow-sm': shadows.sm,
  '--shadow-md': shadows.md,
  '--shadow-lg': shadows.lg,
  '--shadow-xl': shadows.xl,
  '--shadow-2xl': shadows['2xl'],
  '--container-sm': containerWidth.sm,
  '--container-md': containerWidth.md,
  '--container-lg': containerWidth.lg,
  '--container-xl': containerWidth.xl,
  '--nav-height': layoutSizing.navHeight,
  '--sidebar-width': layoutSizing.sidebarWidth,
  '--sidebar-collapsed-width': layoutSizing.sidebarCollapsedWidth,
  '--z-dropdown': String(zIndex.dropdown),
  '--z-sticky': String(zIndex.sticky),
  '--z-fixed': String(zIndex.fixed),
  '--z-modal-backdrop': String(zIndex.modalBackdrop),
  '--z-modal': String(zIndex.modal),
  '--z-popover': String(zIndex.popover),
  '--z-tooltip': String(zIndex.tooltip),
} as const;
