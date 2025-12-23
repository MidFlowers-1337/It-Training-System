/**
 * Design System - 动效规范
 * 原则：快速、隐形。仅用于反馈和状态切换
 */

// 过渡时长
export const duration = {
  instant: '0ms',
  fast: '150ms',      // 快速反馈（hover、点击）
  normal: '200ms',    // 标准过渡
  slow: '300ms',      // 复杂动画（drawer、modal）
  slower: '500ms',    // 大范围动画
} as const;

// 缓动函数
export const easing = {
  // 标准缓动 - 适用于大多数场景
  default: 'cubic-bezier(0.25, 0.1, 0.25, 1.0)',

  // 进入动画 - 元素出现
  easeOut: 'cubic-bezier(0, 0, 0.2, 1)',

  // 退出动画 - 元素消失
  easeIn: 'cubic-bezier(0.4, 0, 1, 1)',

  // 双向动画 - 状态切换
  easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)',

  // 弹性效果 - 强调动画
  spring: 'cubic-bezier(0.175, 0.885, 0.32, 1.275)',

  // 线性 - 进度条、循环动画
  linear: 'linear',
} as const;

// 预定义过渡样式
export const transitions = {
  // 颜色过渡（hover 状态）
  colors: `color ${duration.fast} ${easing.easeOut}, background-color ${duration.fast} ${easing.easeOut}, border-color ${duration.fast} ${easing.easeOut}`,

  // 透明度过渡
  opacity: `opacity ${duration.normal} ${easing.easeOut}`,

  // 变换过渡（scale、translate）
  transform: `transform ${duration.normal} ${easing.easeOut}`,

  // 阴影过渡
  shadow: `box-shadow ${duration.normal} ${easing.easeOut}`,

  // 全属性过渡
  all: `all ${duration.normal} ${easing.easeOut}`,
} as const;

// 动画关键帧（CSS @keyframes 字符串）
export const keyframes = {
  // 淡入
  fadeIn: `
    from { opacity: 0; }
    to { opacity: 1; }
  `,

  // 淡出
  fadeOut: `
    from { opacity: 1; }
    to { opacity: 0; }
  `,

  // 从右侧滑入（drawer）
  slideInRight: `
    from { transform: translateX(100%); opacity: 0; }
    to { transform: translateX(0); opacity: 1; }
  `,

  // 从右侧滑出
  slideOutRight: `
    from { transform: translateX(0); opacity: 1; }
    to { transform: translateX(100%); opacity: 0; }
  `,

  // 从底部滑入（mobile sheet）
  slideInUp: `
    from { transform: translateY(100%); opacity: 0; }
    to { transform: translateY(0); opacity: 1; }
  `,

  // 从底部滑出
  slideOutDown: `
    from { transform: translateY(0); opacity: 1; }
    to { transform: translateY(100%); opacity: 0; }
  `,

  // 缩放进入（modal）
  scaleIn: `
    from { transform: scale(0.95); opacity: 0; }
    to { transform: scale(1); opacity: 1; }
  `,

  // 缩放退出
  scaleOut: `
    from { transform: scale(1); opacity: 1; }
    to { transform: scale(0.95); opacity: 0; }
  `,

  // 加载旋转
  spin: `
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  `,

  // 脉冲（加载指示）
  pulse: `
    0%, 100% { opacity: 1; }
    50% { opacity: 0.5; }
  `,
} as const;

// 预定义动画样式
export const animations = {
  fadeIn: `fadeIn ${duration.normal} ${easing.easeOut} forwards`,
  fadeOut: `fadeOut ${duration.normal} ${easing.easeOut} forwards`,
  slideInRight: `slideInRight ${duration.slow} ${easing.easeOut} forwards`,
  slideOutRight: `slideOutRight ${duration.slow} ${easing.easeIn} forwards`,
  slideInUp: `slideInUp ${duration.slow} ${easing.easeOut} forwards`,
  slideOutDown: `slideOutDown ${duration.slow} ${easing.easeIn} forwards`,
  scaleIn: `scaleIn ${duration.normal} ${easing.easeOut} forwards`,
  scaleOut: `scaleOut ${duration.normal} ${easing.easeIn} forwards`,
  spin: `spin 1s ${easing.linear} infinite`,
  pulse: `pulse 2s ${easing.easeInOut} infinite`,
} as const;

// CSS 变量映射
export const motionCssVars = {
  '--transition-instant': duration.instant,
  '--transition-fast': duration.fast,
  '--transition-normal': duration.normal,
  '--transition-slow': duration.slow,
  '--transition-slower': duration.slower,
  '--ease-default': easing.default,
  '--ease-in': easing.easeIn,
  '--ease-out': easing.easeOut,
  '--ease-in-out': easing.easeInOut,
  '--ease-spring': easing.spring,
  '--ease-linear': easing.linear,
} as const;
