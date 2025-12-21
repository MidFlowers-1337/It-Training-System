/**
 * PrimeVue Unstyled 模式配置
 *
 * 使用 PassThrough API 为组件提供 Tailwind 类名
 * 参考：https://primevue.org/theming/unstyled
 */

import type { PrimeVuePassThroughOptions } from 'primevue';

/**
 * PrimeVue 组件的 PassThrough 预设
 * 使用 Tailwind CSS 类名定义组件样式
 */
export const primeVuePassThrough: PrimeVuePassThroughOptions = {
  // Button 组件
  button: {
    root: ({ props, context }) => ({
      class: [
        // 基础样式
        'inline-flex items-center justify-center gap-2',
        'font-medium text-sm',
        'transition-all duration-fast',
        'focus:outline-none focus:ring-2 focus:ring-offset-2',
        'disabled:opacity-50 disabled:cursor-not-allowed',

        // 圆角
        {
          'rounded-full': props.rounded,
          'rounded-xl': !props.rounded,
        },

        // 尺寸
        {
          'px-4 py-2 text-sm': props.size === 'small',
          'px-5 py-2.5 text-base': !props.size || props.size === 'normal',
          'px-6 py-3 text-lg': props.size === 'large',
        },

        // 变体
        {
          // Primary
          'bg-primary text-white hover:bg-primary-light focus:ring-primary':
            !props.severity || props.severity === 'primary',

          // Secondary
          'bg-transparent border border-border-color text-text-secondary hover:border-primary hover:text-primary':
            props.severity === 'secondary',

          // Success
          'bg-success text-white hover:bg-success/90':
            props.severity === 'success',

          // Warning
          'bg-warning text-white hover:bg-warning/90':
            props.severity === 'warning',

          // Danger
          'bg-error text-white hover:bg-error/90':
            props.severity === 'danger',

          // Info
          'bg-info text-white hover:bg-info/90':
            props.severity === 'info',
        },

        // Text 变体
        {
          'bg-transparent border-0 shadow-none':
            props.text && !props.outlined,
          'text-primary hover:bg-primary/10':
            props.text && (!props.severity || props.severity === 'primary'),
        },

        // Outlined 变体
        {
          'bg-transparent border': props.outlined,
          'border-primary text-primary hover:bg-primary/10':
            props.outlined && (!props.severity || props.severity === 'primary'),
        },
      ],
    }),
    label: {
      class: 'font-medium',
    },
    icon: ({ props }) => ({
      class: [
        props.iconPos === 'left' ? 'mr-1' : 'ml-1',
      ],
    }),
    loadingIcon: {
      class: 'animate-spin',
    },
  },

  // InputText 组件
  inputtext: {
    root: ({ props, context }) => ({
      class: [
        // 基础样式
        'w-full px-4 py-2.5 rounded-xl',
        'text-text-primary placeholder:text-text-muted',
        'bg-bg-tertiary border border-border-color',
        'transition-all duration-fast',
        'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary',

        // 状态
        {
          'opacity-50 cursor-not-allowed': props.disabled,
          'border-error': context.invalid,
        },
      ],
    }),
  },

  // Panel 组件
  panel: {
    root: {
      class: 'rounded-2xl bg-bg-secondary border border-border-color shadow-sm',
    },
    header: ({ props }) => ({
      class: [
        'flex items-center justify-between px-6 py-4',
        'border-b border-border-color',
        {
          'cursor-pointer select-none': props.toggleable,
        },
      ],
    }),
    title: {
      class: 'text-lg font-semibold text-text-primary',
    },
    content: {
      class: 'p-6 text-text-secondary',
    },
    toggler: {
      class: [
        'inline-flex items-center justify-center',
        'w-8 h-8 rounded-full',
        'text-text-secondary hover:text-text-primary hover:bg-bg-hover',
        'transition-all duration-fast',
      ],
    },
  },

  // Dialog / Modal 组件
  dialog: {
    root: {
      class: 'max-h-[90vh] overflow-auto',
    },
    mask: {
      class: 'fixed inset-0 bg-black/50 backdrop-blur-sm z-modal-backdrop',
    },
    container: {
      class: 'flex items-center justify-center min-h-screen p-4',
    },
    content: {
      class: [
        'relative w-full max-w-lg mx-auto',
        'bg-bg-secondary rounded-2xl shadow-2xl',
        'border border-border-color',
      ],
    },
    header: {
      class: 'flex items-center justify-between px-6 py-4 border-b border-border-color',
    },
    title: {
      class: 'text-xl font-semibold text-text-primary',
    },
    headerActions: {
      class: 'flex items-center gap-2',
    },
    body: {
      class: 'p-6 text-text-secondary',
    },
    footer: {
      class: 'flex items-center justify-end gap-3 px-6 py-4 border-t border-border-color',
    },
    closeButton: {
      class: [
        'inline-flex items-center justify-center',
        'w-8 h-8 rounded-full',
        'text-text-secondary hover:text-text-primary hover:bg-bg-hover',
        'transition-all duration-fast',
      ],
    },
  },

  // Select / Dropdown 组件
  select: {
    root: {
      class: 'relative w-full',
    },
    input: {
      class: [
        'w-full px-4 py-2.5 pr-10 rounded-xl',
        'text-text-primary bg-bg-tertiary',
        'border border-border-color',
        'transition-all duration-fast cursor-pointer',
        'focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary',
      ],
    },
    trigger: {
      class: 'absolute right-3 top-1/2 -translate-y-1/2 text-text-muted',
    },
    panel: {
      class: [
        'absolute z-dropdown mt-1 w-full',
        'bg-bg-secondary rounded-xl shadow-lg',
        'border border-border-color',
        'overflow-hidden',
      ],
    },
    list: {
      class: 'max-h-60 overflow-auto py-1',
    },
    option: ({ context }) => ({
      class: [
        'px-4 py-2.5 cursor-pointer',
        'transition-colors duration-fast',
        {
          'bg-bg-hover text-text-primary': context.focused,
          'bg-primary/10 text-primary': context.selected,
          'hover:bg-bg-hover': !context.focused && !context.selected,
        },
      ],
    }),
  },

  // Checkbox 组件
  checkbox: {
    root: {
      class: 'relative inline-flex items-center cursor-pointer',
    },
    input: {
      class: [
        'peer sr-only',
      ],
    },
    box: ({ context }) => ({
      class: [
        'flex items-center justify-center',
        'w-5 h-5 rounded-md',
        'border-2 transition-all duration-fast',
        {
          'border-border-color bg-bg-tertiary': !context.checked,
          'border-primary bg-primary': context.checked,
          'opacity-50 cursor-not-allowed': context.disabled,
        },
      ],
    }),
    icon: {
      class: 'text-white text-xs',
    },
  },

  // Message / Alert 组件
  message: {
    root: ({ props }) => ({
      class: [
        'flex items-start gap-3 p-4 rounded-xl',
        'border',
        {
          'bg-success/10 border-success/30 text-success': props.severity === 'success',
          'bg-info/10 border-info/30 text-info': props.severity === 'info',
          'bg-warning/10 border-warning/30 text-warning': props.severity === 'warn',
          'bg-error/10 border-error/30 text-error': props.severity === 'error',
        },
      ],
    }),
    icon: {
      class: 'text-xl flex-shrink-0',
    },
    text: {
      class: 'flex-1 text-sm',
    },
    closeButton: {
      class: [
        'inline-flex items-center justify-center',
        'w-6 h-6 rounded-full',
        'hover:bg-black/10',
        'transition-all duration-fast',
      ],
    },
  },

  // Toast 组件
  toast: {
    root: {
      class: 'fixed z-tooltip',
    },
    container: ({ props }) => ({
      class: [
        'flex items-start gap-3 p-4 rounded-xl shadow-lg',
        'bg-bg-secondary border border-border-color',
        'min-w-[300px] max-w-md',
      ],
    }),
    content: {
      class: 'flex-1',
    },
    icon: ({ props }) => ({
      class: [
        'text-xl flex-shrink-0',
        {
          'text-success': props.message?.severity === 'success',
          'text-info': props.message?.severity === 'info',
          'text-warning': props.message?.severity === 'warn',
          'text-error': props.message?.severity === 'error',
        },
      ],
    }),
    summary: {
      class: 'font-semibold text-text-primary',
    },
    detail: {
      class: 'text-sm text-text-secondary mt-1',
    },
    closeButton: {
      class: [
        'inline-flex items-center justify-center',
        'w-6 h-6 rounded-full',
        'text-text-muted hover:text-text-primary hover:bg-bg-hover',
        'transition-all duration-fast',
      ],
    },
  },
};

/**
 * PrimeVue 插件配置
 */
export const primeVueConfig = {
  unstyled: true,
  pt: primeVuePassThrough,
};
