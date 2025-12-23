/**
 * Design System - Design Tokens 统一导出
 *
 * 用法：
 * import { themes, applyTheme, textStyles, spacing } from '@/design-system/tokens';
 */

// 颜色系统
export {
  type Theme,
  type ThemeColors,
  themes,
  defaultTheme,
  themeNames,
  applyTheme,
} from './colors';

// 字体排印
export {
  fontFamily,
  fontSize,
  lineHeight,
  fontWeight,
  letterSpacing,
  textStyles,
  typographyCssVars,
} from './typography';

// 间距系统
export {
  spacing,
  layoutSpacing,
  componentSizing,
  containerWidth,
  layoutSizing,
  shadows,
  shadowsDark,
  zIndex,
  spacingCssVars,
} from './spacing';

// 动效规范
export {
  duration,
  easing,
  transitions,
  keyframes,
  animations,
  motionCssVars,
} from './motion';
