/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  darkMode: ['selector', '[data-theme="dark"]'],
  theme: {
    extend: {
      colors: {
        primary: 'rgb(var(--color-primary) / <alpha-value>)',
        'primary-light': 'rgb(var(--color-primary-light) / <alpha-value>)',
        'primary-dark': 'rgb(var(--color-primary-dark) / <alpha-value>)',
        accent: 'rgb(var(--color-accent) / <alpha-value>)',
        surface: 'rgb(var(--color-surface) / <alpha-value>)',
        'surface-alt': 'rgb(var(--color-surface-alt) / <alpha-value>)',
        background: 'rgb(var(--color-bg) / <alpha-value>)',
        'text-primary': 'rgb(var(--color-text) / <alpha-value>)',
        'text-secondary': 'rgb(var(--color-text-secondary) / <alpha-value>)',
        'text-tertiary': 'rgb(var(--color-text-tertiary) / <alpha-value>)',
        border: 'rgb(var(--color-border) / <alpha-value>)',
        success: 'rgb(var(--color-success) / <alpha-value>)',
        warning: 'rgb(var(--color-warning) / <alpha-value>)',
        danger: 'rgb(var(--color-danger) / <alpha-value>)',
        info: 'rgb(var(--color-info) / <alpha-value>)',
      },
      fontFamily: {
        sans: [
          'Inter', '-apple-system', 'BlinkMacSystemFont', '"SF Pro Display"',
          '"SF Pro Text"', '"Helvetica Neue"', 'Arial',
          '"PingFang SC"', '"Noto Sans SC"', 'sans-serif',
        ],
        heading: [
          '"Space Grotesk"', 'Inter', '-apple-system', 'BlinkMacSystemFont',
          '"SF Pro Display"', '"Helvetica Neue"', 'Arial',
          '"PingFang SC"', '"Noto Sans SC"', 'sans-serif',
        ],
        mono: ['"SF Mono"', '"Fira Code"', '"Cascadia Code"', 'Consolas', 'monospace'],
      },
      borderRadius: {
        'theme': 'var(--radius)',
        'theme-lg': 'var(--radius-lg)',
        'theme-xl': 'var(--radius-xl)',
      },
      boxShadow: {
        'theme-sm': 'var(--shadow-sm)',
        'theme': 'var(--shadow)',
        'theme-lg': 'var(--shadow-lg)',
        'theme-xl': 'var(--shadow-xl)',
        'glass': 'var(--glass-shadow)',
        'glow': 'var(--glow-primary)',
        'glow-hover': 'var(--glow-hover)',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out',
        'slide-up': 'slideUp 0.5s ease-out',
        'scale-in': 'scaleIn 0.3s ease-out',
        'shimmer': 'shimmer 2s infinite',
        'aurora': 'aurora 8s ease-in-out infinite alternate',
        'float': 'float 6s ease-in-out infinite',
        'glow': 'glow 2s ease-in-out infinite alternate',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        scaleIn: {
          '0%': { opacity: '0', transform: 'scale(0.95)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        aurora: {
          '0%': { transform: 'translate(-20%, -20%) rotate(0deg) scale(1)' },
          '33%': { transform: 'translate(10%, -10%) rotate(120deg) scale(1.1)' },
          '66%': { transform: 'translate(-10%, 10%) rotate(240deg) scale(0.9)' },
          '100%': { transform: 'translate(20%, 20%) rotate(360deg) scale(1)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
        glow: {
          '0%': { boxShadow: '0 0 5px rgb(var(--color-primary) / 0.2)' },
          '100%': { boxShadow: '0 0 20px rgb(var(--color-primary) / 0.4)' },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
  ],
}
