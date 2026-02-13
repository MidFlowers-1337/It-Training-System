import dayjs from 'dayjs'

export function formatDate(date: string | Date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return dayjs(date).format(format)
}

export function formatDateTime(date: string | Date) {
  return formatDate(date, 'YYYY-MM-DD HH:mm')
}

export function formatDuration(minutes: number): string {
  if (!minutes) return '0分钟'
  const h = Math.floor(minutes / 60)
  const m = minutes % 60
  if (h === 0) return `${m}分钟`
  if (m === 0) return `${h}小时`
  return `${h}小时${m}分钟`
}

export function formatPercent(value: number, decimals = 0): string {
  return `${(value * 100).toFixed(decimals)}%`
}

export function truncate(str: string, maxLen: number): string {
  if (!str) return ''
  return str.length > maxLen ? str.slice(0, maxLen) + '...' : str
}

export function getInitials(name: string): string {
  if (!name) return '?'
  return name.charAt(0).toUpperCase()
}

export function debounce<T extends (...args: any[]) => any>(fn: T, ms = 300) {
  let timer: ReturnType<typeof setTimeout>
  return (...args: Parameters<T>) => {
    clearTimeout(timer)
    timer = setTimeout(() => fn(...args), ms)
  }
}

export const LEVEL_MAP: Record<string, string> = {
  BEGINNER: '入门',
  INTERMEDIATE: '中级',
  ADVANCED: '高级',
}

export const STATUS_MAP: Record<string, string> = {
  ACTIVE: '进行中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  PAUSED: '已暂停',
}

export const ROLE_MAP: Record<string, string> = {
  ADMIN: '管理员',
  INSTRUCTOR: '讲师',
  STUDENT: '学员',
}
