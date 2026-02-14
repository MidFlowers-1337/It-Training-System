import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import {
  Home, BookOpen, Library, Bot,
  BarChart3, Trophy, User, UserCircle, Settings,
} from 'lucide-vue-next'
import type { Component } from 'vue'

/* ── Types ── */
export interface NavItem {
  path: string
  icon: Component
  label: string
  matchPaths?: string[]
}

/* ── Composable ── */
export function useStudentNav() {
  const route = useRoute()
  const router = useRouter()
  const userStore = useUserStore()
  const themeStore = useThemeStore()
  const theme = computed(() => themeStore.theme)

  /* ── Reactive state ── */
  const avatarMenuOpen = ref(false)
  const mobileMenuOpen = ref(false)

  /* ── User info ── */
  const userInitial = computed(() =>
    userStore.userInfo?.realName?.charAt(0) ||
    userStore.userInfo?.username?.charAt(0) || '?',
  )
  const userName = computed(() =>
    userStore.userInfo?.realName || userStore.userInfo?.username || '用户',
  )
  const userEmail = computed(() => userStore.userInfo?.email || '')

  /* ── Navigation items ── */
  const mainNavItems: NavItem[] = [
    { path: '/student/home', icon: Home, label: '首页', matchPaths: ['/student/home'] },
    { path: '/student/courses', icon: BookOpen, label: '课程', matchPaths: ['/student/courses'] },
    { path: '/student/my-learning', icon: Library, label: '我的学习', matchPaths: ['/student/my-learning'] },
    { path: '/student/ai-chat', icon: Bot, label: 'AI 助手', matchPaths: ['/student/ai-chat'] },
  ]

  const dropdownItems: NavItem[] = [
    { path: '/student/report', icon: BarChart3, label: '学习报告' },
    { path: '/student/achievements', icon: Trophy, label: '成就' },
    { path: '/student/profile', icon: User, label: '学习画像' },
    { path: '/student/personal', icon: UserCircle, label: '个人中心' },
    { path: '/student/settings', icon: Settings, label: '设置' },
  ]

  const mobileTabItems: NavItem[] = [
    { path: '/student/home', icon: Home, label: '首页', matchPaths: ['/student/home'] },
    { path: '/student/courses', icon: BookOpen, label: '课程', matchPaths: ['/student/courses'] },
    { path: '/student/my-learning', icon: Library, label: '学习', matchPaths: ['/student/my-learning'] },
    { path: '/student/ai-chat', icon: Bot, label: 'AI', matchPaths: ['/student/ai-chat'] },
    { path: '/student/personal', icon: UserCircle, label: '我的', matchPaths: ['/student/personal', '/student/settings', '/student/profile', '/student/report', '/student/achievements'] },
  ]

  /* ── Helpers ── */
  function isNavActive(item: NavItem): boolean {
    const paths = item.matchPaths || [item.path]
    return paths.some(p => route.path === p || route.path.startsWith(p + '/'))
  }

  function handleLogout() {
    userStore.logout()
    router.push('/login')
  }

  function closeMenus() {
    avatarMenuOpen.value = false
    mobileMenuOpen.value = false
  }

  return {
    theme, route,
    avatarMenuOpen, mobileMenuOpen,
    userInitial, userName, userEmail,
    mainNavItems, dropdownItems, mobileTabItems,
    isNavActive, handleLogout, closeMenus,
  }
}
