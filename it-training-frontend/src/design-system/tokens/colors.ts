/** Theme names / labels / color constants */
export type ThemeName = 'light' | 'dark' | 'warm' | 'pro'

export const THEME_NAMES: ThemeName[] = ['light', 'dark', 'warm', 'pro']

export const THEME_LABELS: Record<ThemeName, string> = {
  light: '晴空白',
  dark: '深空黑',
  warm: '暖阳橙',
  pro: '冰川蓝',
}

export const THEME_ICONS: Record<ThemeName, string> = {
  light: '☀️',
  dark: '🌙',
  warm: '🌅',
  pro: '❄️',
}

export const THEME_PRIMARY_COLORS: Record<ThemeName, string> = {
  light: '#635BFF',
  dark: '#818CF8',
  warm: '#D97706',
  pro: '#0284C7',
}

export const THEME_PREVIEW_COLORS: Record<ThemeName, string[]> = {
  light: ['#635BFF', '#F6F9FC', '#FFFFFF'],
  dark: ['#818CF8', '#08090A', '#111113'],
  warm: ['#D97706', '#FFFBF5', '#FFFFFF'],
  pro: ['#0284C7', '#F8FAFC', '#FFFFFF'],
}
