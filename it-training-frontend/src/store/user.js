import { defineStore } from 'pinia'
import { login, register, getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null')
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 'ADMIN',
    isInstructor: (state) => state.userInfo?.role === 'INSTRUCTOR',
    isStudent: (state) => state.userInfo?.role === 'STUDENT',
    username: (state) => state.userInfo?.username || '',
    realName: (state) => state.userInfo?.realName || ''
  },

  actions: {
    // 登录
    async login(loginForm) {
      const res = await login(loginForm)
      this.setToken(res.data.accessToken)
      this.setUserInfo({
        userId: res.data.userId,
        username: res.data.username,
        realName: res.data.realName,
        role: res.data.role
      })
      return res
    },

    // 注册
    async register(registerForm) {
      const res = await register(registerForm)
      this.setToken(res.data.accessToken)
      this.setUserInfo({
        userId: res.data.userId,
        username: res.data.username,
        realName: res.data.realName,
        role: res.data.role
      })
      return res
    },

    // 获取当前用户信息
    async fetchUserInfo() {
      const res = await getCurrentUser()
      this.setUserInfo({
        userId: res.data.userId,
        username: res.data.username,
        realName: res.data.realName,
        role: res.data.role
      })
      return res
    },

    // 设置Token
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },

    // 登出
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
