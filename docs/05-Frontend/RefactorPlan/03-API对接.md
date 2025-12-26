# IT 技能培训智能选课系统 - API 对接文档

> 文档版本：v1.0.0
> 最后更新：2025-12-25
> 文档状态：🚧 进行中
> 关联文档：[01-重构总览.md](01-重构总览.md) | [api.md](../../03-API/api.md) | [schema.md](../../04-DB/schema.md)

---

## 目录

1. [Axios 基础配置](#1-axios-基础配置)
2. [TypeScript 类型定义](#2-typescript-类型定义)
3. [认证模块 API](#3-认证模块-api)
4. [用户管理 API](#4-用户管理-api)
5. [课程管理 API](#5-课程管理-api)
6. [班期管理 API](#6-班期管理-api)
7. [报名管理 API](#7-报名管理-api)
8. [AI 推荐 API](#8-ai-推荐-api)
9. [学习管理 API](#9-学习管理-api)
10. [统计分析 API](#10-统计分析-api)

---

## 1. Axios 基础配置

### 1.1 请求实例配置

```typescript
// api/index.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, AxiosError } from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

// API 基础配置
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'
const TIMEOUT = 30000

// 创建 Axios 实例
const request: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 自动添加 Token
request.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${authStore.token}`
      }
    }
    return config
  },
  (error: AxiosError) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一错误处理
request.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    // 业务逻辑错误处理
    if (data.code && data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }
    return data
  },
  (error: AxiosError) => {
    const { response } = error

    if (response) {
      switch (response.status) {
        case 401:
          // Token 过期或无效
          const authStore = useAuthStore()
          authStore.logout()
          router.push('/login')
          break
        case 403:
          // 无权限
          router.push('/403')
          break
        case 404:
          // 资源不存在
          break
        case 500:
          // 服务器错误
          break
        default:
          break
      }
    }

    return Promise.reject(error)
  }
)

export default request
```

### 1.2 统一响应类型

```typescript
// types/api.ts

/**
 * 后端统一响应格式
 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

/**
 * 分页请求参数
 */
export interface PageParams {
  page?: number
  size?: number
  sort?: string
  order?: 'asc' | 'desc'
}

/**
 * 分页响应数据
 */
export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number  // 当前页码（从0开始）
  first: boolean
  last: boolean
  empty: boolean
}

/**
 * 通用查询参数
 */
export interface QueryParams extends PageParams {
  keyword?: string
  status?: string
  startDate?: string
  endDate?: string
  [key: string]: any
}
```

---

## 2. TypeScript 类型定义

### 2.1 用户相关类型

```typescript
// types/user.ts

/**
 * 用户角色枚举
 */
export enum UserRole {
  ADMIN = 'ADMIN',
  INSTRUCTOR = 'INSTRUCTOR',
  STUDENT = 'STUDENT'
}

/**
 * 用户状态枚举
 */
export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  LOCKED = 'LOCKED'
}

/**
 * 用户基础信息
 */
export interface User {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  avatar?: string
  role: UserRole
  status: UserStatus
  department?: string
  position?: string
  createdAt: string
  updatedAt: string
}

/**
 * 登录请求参数
 */
export interface LoginParams {
  username: string
  password: string
}

/**
 * 登录响应数据
 */
export interface LoginResult {
  token: string
  user: User
  expiresIn: number
}

/**
 * 注册请求参数
 */
export interface RegisterParams {
  username: string
  password: string
  realName: string
  email: string
  phone?: string
  role?: UserRole
}

/**
 * 用户创建/更新参数
 */
export interface UserForm {
  username?: string
  password?: string
  realName: string
  email: string
  phone?: string
  role: UserRole
  status?: UserStatus
  department?: string
  position?: string
}

/**
 * 修改密码参数
 */
export interface ChangePasswordParams {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
```

### 2.2 课程相关类型

```typescript
// types/course.ts

/**
 * 课程难度等级
 */
export enum CourseLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED'
}

/**
 * 课程状态
 */
export enum CourseStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED'
}

/**
 * 课程基础信息
 */
export interface Course {
  id: number
  name: string
  code: string
  category: string
  description: string
  objectives: string[]
  prerequisites: string[]
  level: CourseLevel
  duration: number  // 课时数
  credits: number
  coverImage?: string
  status: CourseStatus
  createdAt: string
  updatedAt: string
}

/**
 * 课程创建/更新参数
 */
export interface CourseForm {
  name: string
  code: string
  category: string
  description: string
  objectives?: string[]
  prerequisites?: string[]
  level: CourseLevel
  duration: number
  credits: number
  coverImage?: string
  status?: CourseStatus
}

/**
 * 课程查询参数
 */
export interface CourseQueryParams extends QueryParams {
  category?: string
  level?: CourseLevel
  status?: CourseStatus
}
```

### 2.3 班期相关类型

```typescript
// types/session.ts

/**
 * 班期状态
 */
export enum SessionStatus {
  PLANNED = 'PLANNED',       // 计划中
  ENROLLING = 'ENROLLING',   // 报名中
  ONGOING = 'ONGOING',       // 进行中
  COMPLETED = 'COMPLETED',   // 已结束
  CANCELLED = 'CANCELLED'    // 已取消
}

/**
 * 班期基础信息
 */
export interface ClassSession {
  id: number
  courseId: number
  courseName: string
  sessionCode: string
  instructorId: number
  instructorName: string
  startDate: string
  endDate: string
  location: string
  capacity: number
  enrolled: number  // 已报名人数
  status: SessionStatus
  description?: string
  createdAt: string
  updatedAt: string
}

/**
 * 班期创建/更新参数
 */
export interface SessionForm {
  courseId: number
  sessionCode?: string
  instructorId: number
  startDate: string
  endDate: string
  location: string
  capacity: number
  status?: SessionStatus
  description?: string
}

/**
 * 班期查询参数
 */
export interface SessionQueryParams extends QueryParams {
  courseId?: number
  instructorId?: number
  status?: SessionStatus
  startDateFrom?: string
  startDateTo?: string
}
```

### 2.4 报名相关类型

```typescript
// types/enrollment.ts

/**
 * 报名状态
 */
export enum EnrollmentStatus {
  PENDING = 'PENDING',       // 待审核
  APPROVED = 'APPROVED',     // 已通过
  REJECTED = 'REJECTED',     // 已拒绝
  CANCELLED = 'CANCELLED',   // 已取消
  COMPLETED = 'COMPLETED'    // 已完成
}

/**
 * 报名记录
 */
export interface Enrollment {
  id: number
  userId: number
  userName: string
  sessionId: number
  sessionCode: string
  courseId: number
  courseName: string
  status: EnrollmentStatus
  enrolledAt: string
  approvedAt?: string
  approvedBy?: number
  approverName?: string
  remark?: string
  createdAt: string
  updatedAt: string
}

/**
 * 报名申请参数
 */
export interface EnrollmentForm {
  sessionId: number
  remark?: string
}

/**
 * 报名审核参数
 */
export interface EnrollmentApprovalParams {
  enrollmentId: number
  approved: boolean
  remark?: string
}

/**
 * 报名查询参数
 */
export interface EnrollmentQueryParams extends QueryParams {
  userId?: number
  sessionId?: number
  courseId?: number
  status?: EnrollmentStatus
}
```

### 2.5 学习管理相关类型

```typescript
// types/learning.ts

/**
 * 学习进度
 */
export interface LearningProgress {
  id: number
  userId: number
  enrollmentId: number
  sessionId: number
  courseId: number
  courseName: string
  progress: number  // 0-100
  score?: number
  startedAt: string
  lastAccessAt: string
  completedAt?: string
  totalStudyTime: number  // 分钟
}

/**
 * 学习计划
 */
export interface LearningPlan {
  id: number
  userId: number
  title: string
  description?: string
  startDate: string
  endDate: string
  goals: PlanGoal[]
  status: 'ACTIVE' | 'COMPLETED' | 'PAUSED'
  createdAt: string
  updatedAt: string
}

/**
 * 计划目标
 */
export interface PlanGoal {
  id: number
  planId: number
  courseId?: number
  courseName?: string
  title: string
  targetValue: number
  currentValue: number
  unit: string
  deadline: string
  completed: boolean
}

/**
 * 学习计划表单
 */
export interface LearningPlanForm {
  title: string
  description?: string
  startDate: string
  endDate: string
  goals?: Omit<PlanGoal, 'id' | 'planId' | 'currentValue' | 'completed'>[]
}

/**
 * 打卡记录
 */
export interface StudyCheckin {
  id: number
  userId: number
  checkinDate: string
  studyDuration: number  // 分钟
  content: string
  mood?: 'GREAT' | 'GOOD' | 'NORMAL' | 'BAD'
  createdAt: string
}

/**
 * 打卡表单
 */
export interface CheckinForm {
  studyDuration: number
  content: string
  mood?: 'GREAT' | 'GOOD' | 'NORMAL' | 'BAD'
}

/**
 * 成就定义
 */
export interface Achievement {
  id: number
  code: string
  name: string
  description: string
  icon: string
  category: string
  condition: string
  points: number
  rarity: 'COMMON' | 'RARE' | 'EPIC' | 'LEGENDARY'
}

/**
 * 用户成就
 */
export interface UserAchievement {
  id: number
  achievementId: number
  achievement: Achievement
  userId: number
  unlockedAt: string
}

/**
 * 学习仪表盘数据
 */
export interface LearningDashboard {
  // 概览统计
  overview: {
    totalCourses: number
    completedCourses: number
    ongoingCourses: number
    totalStudyTime: number
    averageScore: number
    currentStreak: number
    longestStreak: number
  }
  // 近期学习进度
  recentProgress: LearningProgress[]
  // 本周学习时长（按天）
  weeklyStudyTime: {
    date: string
    duration: number
  }[]
  // 待办事项
  todos: {
    id: number
    type: 'COURSE' | 'PLAN' | 'CHECKIN'
    title: string
    deadline?: string
    priority: 'HIGH' | 'MEDIUM' | 'LOW'
  }[]
  // 最近获得的成就
  recentAchievements: UserAchievement[]
}

/**
 * 学习报告
 */
export interface LearningReport {
  period: 'WEEKLY' | 'MONTHLY' | 'YEARLY'
  startDate: string
  endDate: string
  summary: {
    totalStudyTime: number
    coursesCompleted: number
    averageScore: number
    checkinDays: number
    achievementsUnlocked: number
  }
  courseBreakdown: {
    courseId: number
    courseName: string
    studyTime: number
    progress: number
    score?: number
  }[]
  dailyTrend: {
    date: string
    studyTime: number
    checkedIn: boolean
  }[]
  skillsImproved: {
    skill: string
    beforeLevel: number
    afterLevel: number
  }[]
}

/**
 * 用户档案
 */
export interface UserProfile {
  user: User
  learningStats: {
    totalCourses: number
    completedCourses: number
    totalStudyTime: number
    averageScore: number
    totalAchievements: number
    currentLevel: number
    totalPoints: number
  }
  skills: {
    name: string
    level: number
    courses: number
  }[]
  recentActivities: {
    type: string
    content: string
    timestamp: string
  }[]
  achievements: UserAchievement[]
}
```

### 2.6 AI 推荐相关类型

```typescript
// types/ai.ts

/**
 * AI 推荐请求参数
 */
export interface AIRecommendParams {
  userId?: number
  limit?: number
  includeReasons?: boolean
}

/**
 * AI 推荐结果
 */
export interface AIRecommendation {
  course: Course
  score: number  // 匹配度 0-100
  reasons: string[]
  tags: string[]
}

/**
 * AI 推荐响应
 */
export interface AIRecommendResult {
  recommendations: AIRecommendation[]
  basedOn: {
    learningHistory: boolean
    skillGaps: boolean
    popularTrends: boolean
    careerPath: boolean
  }
  generatedAt: string
}

/**
 * 学习路径推荐
 */
export interface LearningPathRecommendation {
  pathId: string
  title: string
  description: string
  totalDuration: number
  courses: {
    order: number
    course: Course
    isCompleted: boolean
    isOptional: boolean
  }[]
  estimatedCompletion: string
}
```

### 2.7 统计相关类型

```typescript
// types/statistics.ts

/**
 * 系统概览统计
 */
export interface SystemOverview {
  totalUsers: number
  totalCourses: number
  totalSessions: number
  totalEnrollments: number
  activeUsers: number
  ongoingSessions: number
}

/**
 * 课程统计
 */
export interface CourseStatistics {
  courseId: number
  courseName: string
  totalEnrollments: number
  completionRate: number
  averageScore: number
  satisfactionRate: number
  monthlyTrend: {
    month: string
    enrollments: number
    completions: number
  }[]
}

/**
 * 用户活跃度统计
 */
export interface UserActivityStats {
  period: string
  activeUsers: number
  newUsers: number
  returningUsers: number
  averageSessionDuration: number
}

/**
 * 报名趋势统计
 */
export interface EnrollmentTrend {
  date: string
  total: number
  approved: number
  pending: number
  rejected: number
}
```

---

## 3. 认证模块 API

### 3.1 API 服务

```typescript
// api/auth.ts
import request from './index'
import type {
  LoginParams,
  LoginResult,
  RegisterParams,
  User
} from '@/types/user'
import type { ApiResponse } from '@/types/api'

/**
 * 认证相关 API
 */
export const authApi = {
  /**
   * 用户登录
   * POST /auth/login
   */
  login(params: LoginParams): Promise<ApiResponse<LoginResult>> {
    return request.post('/auth/login', params)
  },

  /**
   * 用户注册
   * POST /auth/register
   */
  register(params: RegisterParams): Promise<ApiResponse<User>> {
    return request.post('/auth/register', params)
  },

  /**
   * 获取当前用户信息
   * GET /auth/me
   */
  getCurrentUser(): Promise<ApiResponse<User>> {
    return request.get('/auth/me')
  },

  /**
   * 刷新 Token
   * POST /auth/refresh
   */
  refreshToken(): Promise<ApiResponse<{ token: string; expiresIn: number }>> {
    return request.post('/auth/refresh')
  },

  /**
   * 退出登录
   * POST /auth/logout
   */
  logout(): Promise<ApiResponse<void>> {
    return request.post('/auth/logout')
  },

  /**
   * 修改密码
   * PUT /auth/password
   */
  changePassword(params: {
    oldPassword: string
    newPassword: string
  }): Promise<ApiResponse<void>> {
    return request.put('/auth/password', params)
  },

  /**
   * 发送密码重置邮件
   * POST /auth/forgot-password
   */
  forgotPassword(email: string): Promise<ApiResponse<void>> {
    return request.post('/auth/forgot-password', { email })
  },

  /**
   * 重置密码
   * POST /auth/reset-password
   */
  resetPassword(params: {
    token: string
    newPassword: string
  }): Promise<ApiResponse<void>> {
    return request.post('/auth/reset-password', params)
  }
}
```

### 3.2 认证 Store

```typescript
// stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { User, LoginParams, RegisterParams, UserRole } from '@/types/user'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)
  const loading = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const isInstructor = computed(() => user.value?.role === 'INSTRUCTOR')
  const isStudent = computed(() => user.value?.role === 'STUDENT')

  // 登录
  async function login(params: LoginParams) {
    loading.value = true
    try {
      const { data } = await authApi.login(params)
      token.value = data.token
      user.value = data.user
      localStorage.setItem('token', data.token)

      // 根据角色跳转
      redirectByRole(data.user.role)
      return data
    } finally {
      loading.value = false
    }
  }

  // 注册
  async function register(params: RegisterParams) {
    loading.value = true
    try {
      const { data } = await authApi.register(params)
      return data
    } finally {
      loading.value = false
    }
  }

  // 获取当前用户信息
  async function fetchCurrentUser() {
    if (!token.value) return null

    try {
      const { data } = await authApi.getCurrentUser()
      user.value = data
      return data
    } catch (error) {
      logout()
      throw error
    }
  }

  // 退出登录
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  // 根据角色跳转
  function redirectByRole(role: UserRole) {
    switch (role) {
      case 'ADMIN':
        router.push('/admin/dashboard')
        break
      case 'INSTRUCTOR':
        router.push('/instructor/sessions')
        break
      case 'STUDENT':
        router.push('/student/home')
        break
      default:
        router.push('/')
    }
  }

  // 检查权限
  function hasRole(roles: UserRole | UserRole[]): boolean {
    if (!user.value) return false
    const roleArray = Array.isArray(roles) ? roles : [roles]
    return roleArray.includes(user.value.role)
  }

  return {
    // 状态
    token,
    user,
    loading,
    // 计算属性
    isAuthenticated,
    userRole,
    isAdmin,
    isInstructor,
    isStudent,
    // 方法
    login,
    register,
    fetchCurrentUser,
    logout,
    hasRole
  }
})
```

---

## 4. 用户管理 API

### 4.1 API 服务

```typescript
// api/user.ts
import request from './index'
import type { User, UserForm, UserRole, UserStatus } from '@/types/user'
import type { ApiResponse, PageResult, QueryParams } from '@/types/api'

/**
 * 用户查询参数
 */
export interface UserQueryParams extends QueryParams {
  role?: UserRole
  status?: UserStatus
  department?: string
}

/**
 * 用户管理 API
 */
export const userApi = {
  /**
   * 获取用户列表（分页）
   * GET /users
   */
  getUsers(params?: UserQueryParams): Promise<ApiResponse<PageResult<User>>> {
    return request.get('/users', { params })
  },

  /**
   * 获取单个用户详情
   * GET /users/:id
   */
  getUserById(id: number): Promise<ApiResponse<User>> {
    return request.get(`/users/${id}`)
  },

  /**
   * 创建用户
   * POST /users
   */
  createUser(data: UserForm): Promise<ApiResponse<User>> {
    return request.post('/users', data)
  },

  /**
   * 更新用户
   * PUT /users/:id
   */
  updateUser(id: number, data: Partial<UserForm>): Promise<ApiResponse<User>> {
    return request.put(`/users/${id}`, data)
  },

  /**
   * 删除用户
   * DELETE /users/:id
   */
  deleteUser(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/users/${id}`)
  },

  /**
   * 批量删除用户
   * DELETE /users/batch
   */
  batchDeleteUsers(ids: number[]): Promise<ApiResponse<void>> {
    return request.delete('/users/batch', { data: { ids } })
  },

  /**
   * 更新用户状态
   * PUT /users/:id/status
   */
  updateUserStatus(id: number, status: UserStatus): Promise<ApiResponse<User>> {
    return request.put(`/users/${id}/status`, { status })
  },

  /**
   * 重置用户密码
   * POST /users/:id/reset-password
   */
  resetUserPassword(id: number): Promise<ApiResponse<{ tempPassword: string }>> {
    return request.post(`/users/${id}/reset-password`)
  },

  /**
   * 获取讲师列表（下拉选择用）
   * GET /users/instructors
   */
  getInstructors(): Promise<ApiResponse<User[]>> {
    return request.get('/users/instructors')
  },

  /**
   * 上传用户头像
   * POST /users/:id/avatar
   */
  uploadAvatar(id: number, file: File): Promise<ApiResponse<{ url: string }>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post(`/users/${id}/avatar`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
```

---

## 5. 课程管理 API

### 5.1 API 服务

```typescript
// api/course.ts
import request from './index'
import type {
  Course,
  CourseForm,
  CourseQueryParams,
  CourseLevel,
  CourseStatus
} from '@/types/course'
import type { ApiResponse, PageResult } from '@/types/api'

/**
 * 课程管理 API
 */
export const courseApi = {
  /**
   * 获取课程列表（分页）
   * GET /courses
   */
  getCourses(params?: CourseQueryParams): Promise<ApiResponse<PageResult<Course>>> {
    return request.get('/courses', { params })
  },

  /**
   * 获取全部课程（不分页，用于下拉选择）
   * GET /courses/all
   */
  getAllCourses(): Promise<ApiResponse<Course[]>> {
    return request.get('/courses/all')
  },

  /**
   * 获取课程详情
   * GET /courses/:id
   */
  getCourseById(id: number): Promise<ApiResponse<Course>> {
    return request.get(`/courses/${id}`)
  },

  /**
   * 创建课程
   * POST /courses
   */
  createCourse(data: CourseForm): Promise<ApiResponse<Course>> {
    return request.post('/courses', data)
  },

  /**
   * 更新课程
   * PUT /courses/:id
   */
  updateCourse(id: number, data: Partial<CourseForm>): Promise<ApiResponse<Course>> {
    return request.put(`/courses/${id}`, data)
  },

  /**
   * 删除课程
   * DELETE /courses/:id
   */
  deleteCourse(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/courses/${id}`)
  },

  /**
   * 批量删除课程
   * DELETE /courses/batch
   */
  batchDeleteCourses(ids: number[]): Promise<ApiResponse<void>> {
    return request.delete('/courses/batch', { data: { ids } })
  },

  /**
   * 更新课程状态
   * PUT /courses/:id/status
   */
  updateCourseStatus(id: number, status: CourseStatus): Promise<ApiResponse<Course>> {
    return request.put(`/courses/${id}/status`, { status })
  },

  /**
   * 获取课程分类列表
   * GET /courses/categories
   */
  getCategories(): Promise<ApiResponse<string[]>> {
    return request.get('/courses/categories')
  },

  /**
   * 上传课程封面
   * POST /courses/:id/cover
   */
  uploadCover(id: number, file: File): Promise<ApiResponse<{ url: string }>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.post(`/courses/${id}/cover`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  /**
   * 获取课程的班期列表
   * GET /courses/:id/sessions
   */
  getCourseSessions(id: number): Promise<ApiResponse<any[]>> {
    return request.get(`/courses/${id}/sessions`)
  },

  /**
   * 获取课程统计信息
   * GET /courses/:id/statistics
   */
  getCourseStatistics(id: number): Promise<ApiResponse<{
    totalSessions: number
    totalEnrollments: number
    completionRate: number
    averageScore: number
  }>> {
    return request.get(`/courses/${id}/statistics`)
  }
}

---

## 6. 班期管理 API

### 6.1 API 服务

```typescript
// api/session.ts
import request from './index'
import type {
  ClassSession,
  SessionForm,
  SessionQueryParams,
  SessionStatus
} from '@/types/session'
import type { ApiResponse, PageResult } from '@/types/api'
import type { User } from '@/types/user'

/**
 * 班期管理 API
 */
export const sessionApi = {
  /**
   * 获取班期列表（分页）
   * GET /sessions
   */
  getSessions(params?: SessionQueryParams): Promise<ApiResponse<PageResult<ClassSession>>> {
    return request.get('/sessions', { params })
  },

  /**
   * 获取班期详情
   * GET /sessions/:id
   */
  getSessionById(id: number): Promise<ApiResponse<ClassSession>> {
    return request.get(`/sessions/${id}`)
  },

  /**
   * 创建班期
   * POST /sessions
   */
  createSession(data: SessionForm): Promise<ApiResponse<ClassSession>> {
    return request.post('/sessions', data)
  },

  /**
   * 更新班期
   * PUT /sessions/:id
   */
  updateSession(id: number, data: Partial<SessionForm>): Promise<ApiResponse<ClassSession>> {
    return request.put(`/sessions/${id}`, data)
  },

  /**
   * 删除班期
   * DELETE /sessions/:id
   */
  deleteSession(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/sessions/${id}`)
  },

  /**
   * 批量删除班期
   * DELETE /sessions/batch
   */
  batchDeleteSessions(ids: number[]): Promise<ApiResponse<void>> {
    return request.delete('/sessions/batch', { data: { ids } })
  },

  /**
   * 更新班期状态
   * PUT /sessions/:id/status
   */
  updateSessionStatus(id: number, status: SessionStatus): Promise<ApiResponse<ClassSession>> {
    return request.put(`/sessions/${id}/status`, { status })
  },

  /**
   * 获取班期学员列表
   * GET /sessions/:id/students
   */
  getSessionStudents(id: number): Promise<ApiResponse<{
    student: User
    enrollmentId: number
    enrolledAt: string
    status: string
    progress?: number
  }[]>> {
    return request.get(`/sessions/${id}/students`)
  },

  /**
   * 获取讲师的班期列表
   * GET /sessions/instructor/:instructorId
   */
  getInstructorSessions(instructorId: number, params?: {
    status?: SessionStatus
    page?: number
    size?: number
  }): Promise<ApiResponse<PageResult<ClassSession>>> {
    return request.get(`/sessions/instructor/${instructorId}`, { params })
  },

  /**
   * 获取可报名的班期列表（学员用）
   * GET /sessions/available
   */
  getAvailableSessions(params?: {
    courseId?: number
    keyword?: string
    page?: number
    size?: number
  }): Promise<ApiResponse<PageResult<ClassSession>>> {
    return request.get('/sessions/available', { params })
  },

  /**
   * 获取班期统计信息
   * GET /sessions/:id/statistics
   */
  getSessionStatistics(id: number): Promise<ApiResponse<{
    enrolled: number
    capacity: number
    completionRate: number
    averageScore: number
    attendanceRate: number
  }>> {
    return request.get(`/sessions/${id}/statistics`)
  }
}

---

## 7. 报名管理 API

### 7.1 API 服务

```typescript
// api/enrollment.ts
import request from './index'
import type {
  Enrollment,
  EnrollmentForm,
  EnrollmentQueryParams,
  EnrollmentStatus,
  EnrollmentApprovalParams
} from '@/types/enrollment'
import type { ApiResponse, PageResult } from '@/types/api'

/**
 * 报名管理 API
 */
export const enrollmentApi = {
  /**
   * 获取报名列表（分页）- 管理员用
   * GET /enrollments
   */
  getEnrollments(params?: EnrollmentQueryParams): Promise<ApiResponse<PageResult<Enrollment>>> {
    return request.get('/enrollments', { params })
  },

  /**
   * 获取报名详情
   * GET /enrollments/:id
   */
  getEnrollmentById(id: number): Promise<ApiResponse<Enrollment>> {
    return request.get(`/enrollments/${id}`)
  },

  /**
   * 学员报名课程
   * POST /enrollments
   */
  enroll(data: EnrollmentForm): Promise<ApiResponse<Enrollment>> {
    return request.post('/enrollments', data)
  },

  /**
   * 取消报名
   * DELETE /enrollments/:id
   */
  cancelEnrollment(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/enrollments/${id}`)
  },

  /**
   * 审核报名（管理员）
   * PUT /enrollments/:id/approve
   */
  approveEnrollment(params: EnrollmentApprovalParams): Promise<ApiResponse<Enrollment>> {
    return request.put(`/enrollments/${params.enrollmentId}/approve`, {
      approved: params.approved,
      remark: params.remark
    })
  },

  /**
   * 批量审核报名
   * PUT /enrollments/batch-approve
   */
  batchApproveEnrollments(params: {
    ids: number[]
    approved: boolean
    remark?: string
  }): Promise<ApiResponse<void>> {
    return request.put('/enrollments/batch-approve', params)
  },

  /**
   * 获取当前用户的报名列表
   * GET /enrollments/my
   */
  getMyEnrollments(params?: {
    status?: EnrollmentStatus
    page?: number
    size?: number
  }): Promise<ApiResponse<PageResult<Enrollment>>> {
    return request.get('/enrollments/my', { params })
  },

  /**
   * 检查是否已报名某班期
   * GET /enrollments/check
   */
  checkEnrollment(sessionId: number): Promise<ApiResponse<{
    enrolled: boolean
    enrollment?: Enrollment
  }>> {
    return request.get('/enrollments/check', { params: { sessionId } })
  },

  /**
   * 获取班期的报名列表
   * GET /enrollments/session/:sessionId
   */
  getSessionEnrollments(sessionId: number, params?: {
    status?: EnrollmentStatus
    page?: number
    size?: number
  }): Promise<ApiResponse<PageResult<Enrollment>>> {
    return request.get(`/enrollments/session/${sessionId}`, { params })
  },

  /**
   * 导出报名数据
   * GET /enrollments/export
   */
  exportEnrollments(params?: EnrollmentQueryParams): Promise<Blob> {
    return request.get('/enrollments/export', {
      params,
      responseType: 'blob'
    })
  }
}

---

## 8. AI 推荐 API

### 8.1 API 服务

```typescript
// api/ai.ts
import request from './index'
import type {
  AIRecommendParams,
  AIRecommendResult,
  LearningPathRecommendation
} from '@/types/ai'
import type { ApiResponse } from '@/types/api'

/**
 * AI 推荐 API
 */
export const aiApi = {
  /**
   * 获取个性化课程推荐
   * GET /ai/recommend
   */
  getRecommendations(params?: AIRecommendParams): Promise<ApiResponse<AIRecommendResult>> {
    return request.get('/ai/recommend', { params })
  },

  /**
   * 获取学习路径推荐
   * GET /ai/learning-paths
   */
  getLearningPaths(params?: {
    careerGoal?: string
    currentSkills?: string[]
    limit?: number
  }): Promise<ApiResponse<LearningPathRecommendation[]>> {
    return request.get('/ai/learning-paths', { params })
  },

  /**
   * 获取相似课程
   * GET /ai/similar/:courseId
   */
  getSimilarCourses(courseId: number, limit?: number): Promise<ApiResponse<{
    course: any
    similarity: number
  }[]>> {
    return request.get(`/ai/similar/${courseId}`, { params: { limit } })
  },

  /**
   * 获取技能差距分析
   * GET /ai/skill-gap
   */
  getSkillGapAnalysis(params?: {
    targetRole?: string
    currentSkills?: string[]
  }): Promise<ApiResponse<{
    gaps: {
      skill: string
      currentLevel: number
      requiredLevel: number
      recommendedCourses: any[]
    }[]
    overallReadiness: number
  }>> {
    return request.get('/ai/skill-gap', { params })
  },

  /**
   * 刷新推荐
   * POST /ai/recommend/refresh
   */
  refreshRecommendations(): Promise<ApiResponse<AIRecommendResult>> {
    return request.post('/ai/recommend/refresh')
  },

  /**
   * 反馈推荐结果（用于优化推荐算法）
   * POST /ai/recommend/feedback
   */
  sendFeedback(params: {
    recommendationId: string
    action: 'VIEW' | 'ENROLL' | 'DISMISS' | 'HELPFUL' | 'NOT_HELPFUL'
    courseId: number
  }): Promise<ApiResponse<void>> {
    return request.post('/ai/recommend/feedback', params)
  }
}

---

## 9. 学习管理 API

### 9.1 学习仪表盘 API

```typescript
// api/learning/dashboard.ts
import request from '../index'
import type { LearningDashboard } from '@/types/learning'
import type { ApiResponse } from '@/types/api'

/**
 * 学习仪表盘 API
 */
export const dashboardApi = {
  /**
   * 获取学习仪表盘数据
   * GET /learning/dashboard
   */
  getDashboard(): Promise<ApiResponse<LearningDashboard>> {
    return request.get('/learning/dashboard')
  },

  /**
   * 获取学习概览统计
   * GET /learning/dashboard/overview
   */
  getOverview(): Promise<ApiResponse<LearningDashboard['overview']>> {
    return request.get('/learning/dashboard/overview')
  },

  /**
   * 获取本周学习时长
   * GET /learning/dashboard/weekly-study-time
   */
  getWeeklyStudyTime(): Promise<ApiResponse<LearningDashboard['weeklyStudyTime']>> {
    return request.get('/learning/dashboard/weekly-study-time')
  },

  /**
   * 获取待办事项
   * GET /learning/dashboard/todos
   */
  getTodos(): Promise<ApiResponse<LearningDashboard['todos']>> {
    return request.get('/learning/dashboard/todos')
  }
}
```

### 9.2 学习进度 API

```typescript
// api/learning/progress.ts
import request from '../index'
import type { LearningProgress } from '@/types/learning'
import type { ApiResponse, PageResult, QueryParams } from '@/types/api'

/**
 * 学习进度 API
 */
export const progressApi = {
  /**
   * 获取学习进度列表
   * GET /learning/progress
   */
  getProgressList(params?: QueryParams): Promise<ApiResponse<PageResult<LearningProgress>>> {
    return request.get('/learning/progress', { params })
  },

  /**
   * 获取某个课程的学习进度
   * GET /learning/progress/:enrollmentId
   */
  getProgressByEnrollment(enrollmentId: number): Promise<ApiResponse<LearningProgress>> {
    return request.get(`/learning/progress/${enrollmentId}`)
  },

  /**
   * 更新学习进度
   * PUT /learning/progress/:enrollmentId
   */
  updateProgress(enrollmentId: number, data: {
    progress: number
    studyTime?: number
  }): Promise<ApiResponse<LearningProgress>> {
    return request.put(`/learning/progress/${enrollmentId}`, data)
  },

  /**
   * 记录学习时长
   * POST /learning/progress/:enrollmentId/record-time
   */
  recordStudyTime(enrollmentId: number, minutes: number): Promise<ApiResponse<void>> {
    return request.post(`/learning/progress/${enrollmentId}/record-time`, { minutes })
  },

  /**
   * 完成课程
   * POST /learning/progress/:enrollmentId/complete
   */
  completeCourse(enrollmentId: number, score?: number): Promise<ApiResponse<LearningProgress>> {
    return request.post(`/learning/progress/${enrollmentId}/complete`, { score })
  }
}
```

### 9.3 打卡 API

```typescript
// api/learning/checkin.ts
import request from '../index'
import type { StudyCheckin, CheckinForm } from '@/types/learning'
import type { ApiResponse, PageResult, QueryParams } from '@/types/api'

/**
 * 学习打卡 API
 */
export const checkinApi = {
  /**
   * 获取打卡记录列表
   * GET /learning/checkin
   */
  getCheckinList(params?: QueryParams & {
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<PageResult<StudyCheckin>>> {
    return request.get('/learning/checkin', { params })
  },

  /**
   * 今日打卡
   * POST /learning/checkin
   */
  checkin(data: CheckinForm): Promise<ApiResponse<StudyCheckin>> {
    return request.post('/learning/checkin', data)
  },

  /**
   * 获取今日打卡状态
   * GET /learning/checkin/today
   */
  getTodayCheckin(): Promise<ApiResponse<{
    checkedIn: boolean
    checkin?: StudyCheckin
  }>> {
    return request.get('/learning/checkin/today')
  },

  /**
   * 获取打卡统计
   * GET /learning/checkin/statistics
   */
  getCheckinStatistics(params?: {
    year?: number
    month?: number
  }): Promise<ApiResponse<{
    totalDays: number
    currentStreak: number
    longestStreak: number
    thisMonthDays: number
    calendar: {
      date: string
      checkedIn: boolean
      studyDuration?: number
    }[]
  }>> {
    return request.get('/learning/checkin/statistics', { params })
  },

  /**
   * 更新打卡记录
   * PUT /learning/checkin/:id
   */
  updateCheckin(id: number, data: Partial<CheckinForm>): Promise<ApiResponse<StudyCheckin>> {
    return request.put(`/learning/checkin/${id}`, data)
  }
}
```

### 9.4 成就 API

```typescript
// api/learning/achievement.ts
import request from '../index'
import type { Achievement, UserAchievement } from '@/types/learning'
import type { ApiResponse, PageResult, QueryParams } from '@/types/api'

/**
 * 成就系统 API
 */
export const achievementApi = {
  /**
   * 获取所有成就定义
   * GET /learning/achievements
   */
  getAllAchievements(params?: {
    category?: string
  }): Promise<ApiResponse<Achievement[]>> {
    return request.get('/learning/achievements', { params })
  },

  /**
   * 获取用户已解锁的成就
   * GET /learning/achievements/my
   */
  getMyAchievements(params?: QueryParams): Promise<ApiResponse<PageResult<UserAchievement>>> {
    return request.get('/learning/achievements/my', { params })
  },

  /**
   * 获取成就详情
   * GET /learning/achievements/:id
   */
  getAchievementById(id: number): Promise<ApiResponse<Achievement>> {
    return request.get(`/learning/achievements/${id}`)
  },

  /**
   * 获取成就进度
   * GET /learning/achievements/progress
   */
  getAchievementProgress(): Promise<ApiResponse<{
    achievement: Achievement
    currentValue: number
    targetValue: number
    progress: number
  }[]>> {
    return request.get('/learning/achievements/progress')
  },

  /**
   * 获取成就分类
   * GET /learning/achievements/categories
   */
  getAchievementCategories(): Promise<ApiResponse<{
    category: string
    total: number
    unlocked: number
  }[]>> {
    return request.get('/learning/achievements/categories')
  }
}
```

### 9.5 学习计划 API

```typescript
// api/learning/plan.ts
import request from '../index'
import type { LearningPlan, LearningPlanForm, PlanGoal } from '@/types/learning'
import type { ApiResponse, PageResult, QueryParams } from '@/types/api'

/**
 * 学习计划 API
 */
export const planApi = {
  /**
   * 获取学习计划列表
   * GET /learning/plans
   */
  getPlans(params?: QueryParams & {
    status?: 'ACTIVE' | 'COMPLETED' | 'PAUSED'
  }): Promise<ApiResponse<PageResult<LearningPlan>>> {
    return request.get('/learning/plans', { params })
  },

  /**
   * 获取学习计划详情
   * GET /learning/plans/:id
   */
  getPlanById(id: number): Promise<ApiResponse<LearningPlan>> {
    return request.get(`/learning/plans/${id}`)
  },

  /**
   * 创建学习计划
   * POST /learning/plans
   */
  createPlan(data: LearningPlanForm): Promise<ApiResponse<LearningPlan>> {
    return request.post('/learning/plans', data)
  },

  /**
   * 更新学习计划
   * PUT /learning/plans/:id
   */
  updatePlan(id: number, data: Partial<LearningPlanForm>): Promise<ApiResponse<LearningPlan>> {
    return request.put(`/learning/plans/${id}`, data)
  },

  /**
   * 删除学习计划
   * DELETE /learning/plans/:id
   */
  deletePlan(id: number): Promise<ApiResponse<void>> {
    return request.delete(`/learning/plans/${id}`)
  },

  /**
   * 更新计划状态
   * PUT /learning/plans/:id/status
   */
  updatePlanStatus(id: number, status: 'ACTIVE' | 'COMPLETED' | 'PAUSED'): Promise<ApiResponse<LearningPlan>> {
    return request.put(`/learning/plans/${id}/status`, { status })
  },

  /**
   * 添加计划目标
   * POST /learning/plans/:id/goals
   */
  addGoal(planId: number, goal: Omit<PlanGoal, 'id' | 'planId' | 'currentValue' | 'completed'>): Promise<ApiResponse<PlanGoal>> {
    return request.post(`/learning/plans/${planId}/goals`, goal)
  },

  /**
   * 更新目标进度
   * PUT /learning/plans/:planId/goals/:goalId
   */
  updateGoalProgress(planId: number, goalId: number, currentValue: number): Promise<ApiResponse<PlanGoal>> {
    return request.put(`/learning/plans/${planId}/goals/${goalId}`, { currentValue })
  },

  /**
   * 删除计划目标
   * DELETE /learning/plans/:planId/goals/:goalId
   */
  deleteGoal(planId: number, goalId: number): Promise<ApiResponse<void>> {
    return request.delete(`/learning/plans/${planId}/goals/${goalId}`)
  },

  /**
   * 获取当前活跃计划
   * GET /learning/plans/active
   */
  getActivePlan(): Promise<ApiResponse<LearningPlan | null>> {
    return request.get('/learning/plans/active')
  }
}
```

### 9.6 学习报告 API

```typescript
// api/learning/report.ts
import request from '../index'
import type { LearningReport } from '@/types/learning'
import type { ApiResponse } from '@/types/api'

/**
 * 学习报告 API
 */
export const reportApi = {
  /**
   * 获取周报
   * GET /learning/reports/weekly
   */
  getWeeklyReport(params?: {
    weekStart?: string  // YYYY-MM-DD
  }): Promise<ApiResponse<LearningReport>> {
    return request.get('/learning/reports/weekly', { params })
  },

  /**
   * 获取月报
   * GET /learning/reports/monthly
   */
  getMonthlyReport(params?: {
    year?: number
    month?: number
  }): Promise<ApiResponse<LearningReport>> {
    return request.get('/learning/reports/monthly', { params })
  },

  /**
   * 获取年报
   * GET /learning/reports/yearly
   */
  getYearlyReport(params?: {
    year?: number
  }): Promise<ApiResponse<LearningReport>> {
    return request.get('/learning/reports/yearly', { params })
  },

  /**
   * 生成自定义报告
   * POST /learning/reports/custom
   */
  generateCustomReport(params: {
    startDate: string
    endDate: string
  }): Promise<ApiResponse<LearningReport>> {
    return request.post('/learning/reports/custom', params)
  },

  /**
   * 导出报告（PDF）
   * GET /learning/reports/export
   */
  exportReport(params: {
    type: 'WEEKLY' | 'MONTHLY' | 'YEARLY' | 'CUSTOM'
    startDate?: string
    endDate?: string
  }): Promise<Blob> {
    return request.get('/learning/reports/export', {
      params,
      responseType: 'blob'
    })
  }
}
```

### 9.7 学习推荐 API

```typescript
// api/learning/recommendation.ts
import request from '../index'
import type { ApiResponse } from '@/types/api'
import type { Course } from '@/types/course'

/**
 * 学习推荐 API（基于学习行为的推荐）
 */
export const learningRecommendationApi = {
  /**
   * 获取基于学习历史的推荐
   * GET /learning/recommendations/history-based
   */
  getHistoryBasedRecommendations(limit?: number): Promise<ApiResponse<{
    course: Course
    reason: string
    relevance: number
  }[]>> {
    return request.get('/learning/recommendations/history-based', { params: { limit } })
  },

  /**
   * 获取热门课程推荐
   * GET /learning/recommendations/popular
   */
  getPopularRecommendations(params?: {
    category?: string
    limit?: number
  }): Promise<ApiResponse<{
    course: Course
    enrollments: number
    rating: number
  }[]>> {
    return request.get('/learning/recommendations/popular', { params })
  },

  /**
   * 获取即将开课推荐
   * GET /learning/recommendations/upcoming
   */
  getUpcomingRecommendations(limit?: number): Promise<ApiResponse<{
    course: Course
    session: any
    startDate: string
    availableSeats: number
  }[]>> {
    return request.get('/learning/recommendations/upcoming', { params: { limit } })
  },

  /**
   * 获取继续学习推荐
   * GET /learning/recommendations/continue
   */
  getContinueLearningRecommendations(): Promise<ApiResponse<{
    enrollment: any
    course: Course
    lastAccessAt: string
    progress: number
  }[]>> {
    return request.get('/learning/recommendations/continue')
  }
}
```

### 9.8 用户档案 API

```typescript
// api/learning/profile.ts
import request from '../index'
import type { UserProfile } from '@/types/learning'
import type { ApiResponse } from '@/types/api'

/**
 * 用户档案 API
 */
export const profileApi = {
  /**
   * 获取用户档案
   * GET /learning/profile
   */
  getProfile(): Promise<ApiResponse<UserProfile>> {
    return request.get('/learning/profile')
  },

  /**
   * 获取用户技能列表
   * GET /learning/profile/skills
   */
  getSkills(): Promise<ApiResponse<UserProfile['skills']>> {
    return request.get('/learning/profile/skills')
  },

  /**
   * 获取用户活动记录
   * GET /learning/profile/activities
   */
  getActivities(params?: {
    limit?: number
    type?: string
  }): Promise<ApiResponse<UserProfile['recentActivities']>> {
    return request.get('/learning/profile/activities', { params })
  },

  /**
   * 更新用户学习偏好
   * PUT /learning/profile/preferences
   */
  updatePreferences(data: {
    preferredCategories?: string[]
    learningGoal?: string
    weeklyStudyHours?: number
    notificationEnabled?: boolean
  }): Promise<ApiResponse<void>> {
    return request.put('/learning/profile/preferences', data)
  },

  /**
   * 获取用户等级信息
   * GET /learning/profile/level
   */
  getLevelInfo(): Promise<ApiResponse<{
    currentLevel: number
    totalPoints: number
    pointsToNextLevel: number
    levelName: string
    benefits: string[]
  }>> {
    return request.get('/learning/profile/level')
  }
}
```

### 9.9 学习管理 API 统一导出

```typescript
// api/learning/index.ts
export { dashboardApi } from './dashboard'
export { progressApi } from './progress'
export { checkinApi } from './checkin'
export { achievementApi } from './achievement'
export { planApi } from './plan'
export { reportApi } from './report'
export { learningRecommendationApi } from './recommendation'
export { profileApi } from './profile'

// 统一导出为 learningApi
import { dashboardApi } from './dashboard'
import { progressApi } from './progress'
import { checkinApi } from './checkin'
import { achievementApi } from './achievement'
import { planApi } from './plan'
import { reportApi } from './report'
import { learningRecommendationApi } from './recommendation'
import { profileApi } from './profile'

export const learningApi = {
  dashboard: dashboardApi,
  progress: progressApi,
  checkin: checkinApi,
  achievement: achievementApi,
  plan: planApi,
  report: reportApi,
  recommendation: learningRecommendationApi,
  profile: profileApi
}
```

---

## 10. 统计分析 API

### 10.1 API 服务

```typescript
// api/statistics.ts
import request from './index'
import type {
  SystemOverview,
  CourseStatistics,
  UserActivityStats,
  EnrollmentTrend
} from '@/types/statistics'
import type { ApiResponse } from '@/types/api'

/**
 * 统计分析 API
 */
export const statisticsApi = {
  /**
   * 获取系统概览统计
   * GET /statistics/overview
   */
  getOverview(): Promise<ApiResponse<SystemOverview>> {
    return request.get('/statistics/overview')
  },

  /**
   * 获取课程统计
   * GET /statistics/courses
   */
  getCourseStatistics(params?: {
    courseId?: number
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<CourseStatistics[]>> {
    return request.get('/statistics/courses', { params })
  },

  /**
   * 获取单个课程详细统计
   * GET /statistics/courses/:id
   */
  getCourseDetailStatistics(id: number): Promise<ApiResponse<CourseStatistics>> {
    return request.get(`/statistics/courses/${id}`)
  },

  /**
   * 获取用户活跃度统计
   * GET /statistics/user-activity
   */
  getUserActivityStats(params?: {
    period: 'DAILY' | 'WEEKLY' | 'MONTHLY'
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<UserActivityStats[]>> {
    return request.get('/statistics/user-activity', { params })
  },

  /**
   * 获取报名趋势
   * GET /statistics/enrollment-trend
   */
  getEnrollmentTrend(params?: {
    period: 'DAILY' | 'WEEKLY' | 'MONTHLY'
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<EnrollmentTrend[]>> {
    return request.get('/statistics/enrollment-trend', { params })
  },

  /**
   * 获取分类统计
   * GET /statistics/categories
   */
  getCategoryStatistics(): Promise<ApiResponse<{
    category: string
    courseCount: number
    enrollmentCount: number
    completionRate: number
  }[]>> {
    return request.get('/statistics/categories')
  },

  /**
   * 获取讲师排名
   * GET /statistics/instructor-ranking
   */
  getInstructorRanking(params?: {
    limit?: number
    orderBy?: 'enrollments' | 'rating' | 'completionRate'
  }): Promise<ApiResponse<{
    instructor: any
    totalSessions: number
    totalEnrollments: number
    averageRating: number
    completionRate: number
  }[]>> {
    return request.get('/statistics/instructor-ranking', { params })
  },

  /**
   * 获取学习时长统计
   * GET /statistics/study-time
   */
  getStudyTimeStatistics(params?: {
    period: 'DAILY' | 'WEEKLY' | 'MONTHLY'
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<{
    date: string
    totalStudyTime: number
    activeUsers: number
    averageStudyTime: number
  }[]>> {
    return request.get('/statistics/study-time', { params })
  },

  /**
   * 导出统计报表
   * GET /statistics/export
   */
  exportStatistics(params: {
    type: 'OVERVIEW' | 'COURSES' | 'USERS' | 'ENROLLMENTS'
    format: 'EXCEL' | 'PDF'
    startDate?: string
    endDate?: string
  }): Promise<Blob> {
    return request.get('/statistics/export', {
      params,
      responseType: 'blob'
    })
  }
}
```

---

## 11. API 模块统一导出

```typescript
// api/index.ts（补充导出）

// 重新导出所有 API 模块
export { authApi } from './auth'
export { userApi } from './user'
export { courseApi } from './course'
export { sessionApi } from './session'
export { enrollmentApi } from './enrollment'
export { aiApi } from './ai'
export { learningApi } from './learning'
export { statisticsApi } from './statistics'

// 默认导出 request 实例
export { default as request } from './index'
```

---

## 12. 错误处理工具

### 12.1 错误类型定义

```typescript
// utils/error.ts

/**
 * API 错误类型
 */
export class ApiError extends Error {
  code: number
  data?: any

  constructor(message: string, code: number, data?: any) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.data = data
  }
}

/**
 * 业务错误代码
 */
export enum ErrorCode {
  // 通用错误
  UNKNOWN = -1,
  SUCCESS = 200,
  BAD_REQUEST = 400,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_ERROR = 500,

  // 业务错误
  USER_NOT_FOUND = 1001,
  USER_ALREADY_EXISTS = 1002,
  INVALID_PASSWORD = 1003,
  TOKEN_EXPIRED = 1004,
  TOKEN_INVALID = 1005,

  COURSE_NOT_FOUND = 2001,
  SESSION_NOT_FOUND = 2002,
  SESSION_FULL = 2003,
  SESSION_NOT_ENROLLING = 2004,

  ENROLLMENT_NOT_FOUND = 3001,
  ALREADY_ENROLLED = 3002,
  ENROLLMENT_CANCELLED = 3003,

  // 其他业务错误...
}

/**
 * 错误消息映射
 */
export const ErrorMessages: Record<number, string> = {
  [ErrorCode.UNKNOWN]: '未知错误',
  [ErrorCode.BAD_REQUEST]: '请求参数错误',
  [ErrorCode.UNAUTHORIZED]: '未授权，请先登录',
  [ErrorCode.FORBIDDEN]: '没有权限执行此操作',
  [ErrorCode.NOT_FOUND]: '资源不存在',
  [ErrorCode.INTERNAL_ERROR]: '服务器内部错误',

  [ErrorCode.USER_NOT_FOUND]: '用户不存在',
  [ErrorCode.USER_ALREADY_EXISTS]: '用户名已存在',
  [ErrorCode.INVALID_PASSWORD]: '密码错误',
  [ErrorCode.TOKEN_EXPIRED]: '登录已过期，请重新登录',
  [ErrorCode.TOKEN_INVALID]: '无效的登录凭证',

  [ErrorCode.COURSE_NOT_FOUND]: '课程不存在',
  [ErrorCode.SESSION_NOT_FOUND]: '班期不存在',
  [ErrorCode.SESSION_FULL]: '班期已满员',
  [ErrorCode.SESSION_NOT_ENROLLING]: '班期未开放报名',

  [ErrorCode.ENROLLMENT_NOT_FOUND]: '报名记录不存在',
  [ErrorCode.ALREADY_ENROLLED]: '您已报名此班期',
  [ErrorCode.ENROLLMENT_CANCELLED]: '报名已取消',
}

/**
 * 获取错误消息
 */
export function getErrorMessage(code: number): string {
  return ErrorMessages[code] || ErrorMessages[ErrorCode.UNKNOWN]
}
```

### 12.2 通知工具

```typescript
// composables/useNotification.ts
import { ref } from 'vue'

export interface NotificationItem {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  title: string
  message?: string
  duration?: number
}

const notifications = ref<NotificationItem[]>([])

export function useNotification() {
  const show = (item: Omit<NotificationItem, 'id'>) => {
    const id = Date.now().toString()
    const notification: NotificationItem = {
      ...item,
      id,
      duration: item.duration ?? 3000
    }

    notifications.value.push(notification)

    if (notification.duration > 0) {
      setTimeout(() => {
        remove(id)
      }, notification.duration)
    }

    return id
  }

  const remove = (id: string) => {
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }

  const success = (title: string, message?: string) => {
    return show({ type: 'success', title, message })
  }

  const error = (title: string, message?: string) => {
    return show({ type: 'error', title, message, duration: 5000 })
  }

  const warning = (title: string, message?: string) => {
    return show({ type: 'warning', title, message })
  }

  const info = (title: string, message?: string) => {
    return show({ type: 'info', title, message })
  }

  return {
    notifications,
    show,
    remove,
    success,
    error,
    warning,
    info
  }
}
```

---

## 修订记录

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0.0 | 2025-12-25 | Frontend Architect | 初始版本 |
```

