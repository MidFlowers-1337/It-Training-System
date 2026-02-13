import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { UserInfo } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('access_token') || '')
  const refreshToken = ref(localStorage.getItem('refresh_token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const loading = ref(false)

  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isInstructor = computed(() => role.value === 'INSTRUCTOR')
  const isStudent = computed(() => role.value === 'STUDENT')

  function setTokens(access: string, refresh: string) {
    token.value = access
    refreshToken.value = refresh
    localStorage.setItem('access_token', access)
    localStorage.setItem('refresh_token', refresh)
  }

  async function login(username: string, password: string) {
    loading.value = true
    try {
      const res: any = await authApi.login({ username, password })
      setTokens(res.accessToken, res.refreshToken)
      await fetchUserInfo()
      return res
    } finally {
      loading.value = false
    }
  }

  async function register(data: { username: string; password: string; email?: string; realName?: string }) {
    return authApi.register(data)
  }

  async function fetchUserInfo() {
    try {
      const res: any = await authApi.getCurrentUser()
      userInfo.value = res
      return res
    } catch (e) {
      logout()
      throw e
    }
  }

  function logout() {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
  }

  // Auto-load user info on store init if token exists
  async function init() {
    if (token.value && !userInfo.value) {
      try {
        await fetchUserInfo()
      } catch {
        logout()
      }
    }
  }

  return {
    token, refreshToken, userInfo, loading,
    isLoggedIn, role, isAdmin, isInstructor, isStudent,
    login, register, fetchUserInfo, logout, init, setTokens,
  }
})
