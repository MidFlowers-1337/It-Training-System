/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        // 背景色
        'bg-primary': 'rgb(var(--bg-primary-rgb) / <alpha-value>)',
        'bg-secondary': 'rgb(var(--bg-secondary-rgb) / <alpha-value>)',
        'bg-tertiary': 'rgb(var(--bg-tertiary-rgb) / <alpha-value>)',
        'bg-hover': 'rgb(var(--bg-hover-rgb) / <alpha-value>)',
        'bg-card': 'rgb(var(--bg-card-rgb) / <alpha-value>)',
        'bg-inverse': 'rgb(var(--bg-inverse-rgb) / <alpha-value>)',
        
        // 文本色
        'text-primary': 'rgb(var(--text-primary-rgb) / <alpha-value>)',
        'text-secondary': 'rgb(var(--text-secondary-rgb) / <alpha-value>)',
        'text-muted': 'rgb(var(--text-muted-rgb) / <alpha-value>)',
        'text-disabled': 'rgb(var(--text-disabled-rgb) / <alpha-value>)',
        'text-inverse': 'rgb(var(--text-inverse-rgb) / <alpha-value>)',
        
        // 品牌色
        'primary': {
          DEFAULT: 'rgb(var(--primary-color-rgb) / <alpha-value>)',
          light: 'rgb(var(--primary-light-rgb) / <alpha-value>)',
          dark: 'rgb(var(--primary-dark-rgb) / <alpha-value>)',
          alpha: 'rgb(var(--primary-color-rgb) / <alpha-value>)',
        },

        // 辅助品牌色：用于渐变/强调（不新增主题值，基于 primary-light 派生）
        'secondary': {
          DEFAULT: 'rgb(var(--primary-light-rgb) / <alpha-value>)',
          dark: 'rgb(var(--primary-dark-rgb) / <alpha-value>)',
        },
        
        // 边框色
        'border-color': 'rgb(var(--border-color-rgb) / <alpha-value>)',
        'border-light': 'rgb(var(--border-light-rgb) / <alpha-value>)',
        'border-dark': 'rgb(var(--border-dark-rgb) / <alpha-value>)',
        
        // 状态色
        'success': 'rgb(var(--success-color-rgb) / <alpha-value>)',
        'warning': 'rgb(var(--warning-color-rgb) / <alpha-value>)',
        'error': 'rgb(var(--error-color-rgb) / <alpha-value>)',
        'info': 'rgb(var(--info-color-rgb) / <alpha-value>)',
      },
      
      fontFamily: {
        sans: 'var(--font-sans)',
        mono: 'var(--font-mono)',
      },
      
      fontSize: {
        xs: 'var(--text-xs)',
        sm: 'var(--text-sm)',
        base: 'var(--text-base)',
        lg: 'var(--text-lg)',
        xl: 'var(--text-xl)',
        '2xl': 'var(--text-2xl)',
        '3xl': 'var(--text-3xl)',
        '4xl': 'var(--text-4xl)',
        '5xl': 'var(--text-5xl)',
      },
      
      fontWeight: {
        normal: 'var(--font-normal)',
        medium: 'var(--font-medium)',
        semibold: 'var(--font-semibold)',
        bold: 'var(--font-bold)',
      },
      
      lineHeight: {
        tight: 'var(--leading-tight)',
        normal: 'var(--leading-normal)',
        relaxed: 'var(--leading-relaxed)',
      },
      
      spacing: {
        '1': 'var(--space-1)',
        '2': 'var(--space-2)',
        '3': 'var(--space-3)',
        '4': 'var(--space-4)',
        '5': 'var(--space-5)',
        '6': 'var(--space-6)',
        '8': 'var(--space-8)',
        '10': 'var(--space-10)',
        '12': 'var(--space-12)',
        '16': 'var(--space-16)',
        '20': 'var(--space-20)',
        '24': 'var(--space-24)',
      },
      
      borderRadius: {
        'sm': 'var(--radius-sm)',
        'md': 'var(--radius-md)',
        'lg': 'var(--radius-lg)',
        'xl': 'var(--radius-xl)',
        '2xl': 'var(--radius-2xl)',
        'full': 'var(--radius-full)',
      },
      
      boxShadow: {
        'sm': 'var(--shadow-sm)',
        'md': 'var(--shadow-md)',
        'lg': 'var(--shadow-lg)',
        'xl': 'var(--shadow-xl)',
        '2xl': 'var(--shadow-2xl)',
      },
      
      transitionDuration: {
        'fast': 'var(--transition-fast)',
        'normal': 'var(--transition-normal)',
        'slow': 'var(--transition-slow)',
      },
      
      transitionTimingFunction: {
        'in-out': 'var(--ease-in-out)',
        'out': 'var(--ease-out)',
        'in': 'var(--ease-in)',
      },
      
      maxWidth: {
        'container-sm': 'var(--container-sm)',
        'container-md': 'var(--container-md)',
        'container-lg': 'var(--container-lg)',
        'container-xl': 'var(--container-xl)',
      },
      
      height: {
        'nav': 'var(--nav-height)',
      },
      
      width: {
        'sidebar': 'var(--sidebar-width)',
        'sidebar-collapsed': 'var(--sidebar-collapsed-width)',
      },
      
      zIndex: {
        'dropdown': 'var(--z-dropdown)',
        'sticky': 'var(--z-sticky)',
        'fixed': 'var(--z-fixed)',
        'modal-backdrop': 'var(--z-modal-backdrop)',
        'modal': 'var(--z-modal)',
        'popover': 'var(--z-popover)',
        'tooltip': 'var(--z-tooltip)',
      },
    },
  },
  plugins: [],
}
