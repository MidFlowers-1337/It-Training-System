import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  // Landing
  {
    path: '/',
    name: 'Landing',
    component: () => import('@/views/landing/LandingPage.vue'),
    meta: { layout: 'blank' },
  },

  // Auth
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { layout: 'auth', guest: true },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { layout: 'auth', guest: true },
  },

  // Student
  {
    path: '/student',
    meta: { layout: 'student', requiresAuth: true },
    children: [
      // ── 主导航 ──
      { path: '', redirect: '/student/home' },
      { path: 'home', name: 'StudentHome', component: () => import('@/views/student/Home.vue') },
      { path: 'courses', name: 'CourseList', component: () => import('@/views/student/CourseList.vue') },
      { path: 'courses/:id', name: 'CourseDetail', component: () => import('@/views/student/CourseDetail.vue'), props: true, meta: { breadcrumb: [{ label: '课程浏览', path: '/student/courses' }, { label: '课程详情' }] } },
      { path: 'courses/:id/study', name: 'CourseStudy', component: () => import('@/views/student/CourseStudy.vue'), props: true, meta: { focusMode: true, breadcrumb: [{ label: '课程浏览', path: '/student/courses' }, { label: '学习' }] } },
      { path: 'my-learning', name: 'MyLearning', component: () => import('@/views/student/MyLearning.vue') },
      { path: 'ai-chat', name: 'AiChat', component: () => import('@/views/student/AiChat.vue') },
      // ── 次级 (头像菜单) ──
      { path: 'report', name: 'LearningReport', component: () => import('@/views/student/LearningReport.vue'), meta: { breadcrumb: [{ label: '首页', path: '/student/home' }, { label: '学习报告' }] } },
      { path: 'achievements', name: 'Achievements', component: () => import('@/views/student/Achievements.vue'), meta: { breadcrumb: [{ label: '首页', path: '/student/home' }, { label: '成就' }] } },
      { path: 'profile', name: 'UserProfile', component: () => import('@/views/student/UserProfile.vue'), meta: { breadcrumb: [{ label: '首页', path: '/student/home' }, { label: '学习画像' }] } },
      { path: 'personal', name: 'PersonalCenter', component: () => import('@/views/student/PersonalCenter.vue'), meta: { breadcrumb: [{ label: '首页', path: '/student/home' }, { label: '个人中心' }] } },
      { path: 'settings', name: 'Settings', component: () => import('@/views/student/Settings.vue'), meta: { breadcrumb: [{ label: '首页', path: '/student/home' }, { label: '设置' }] } },
      // ── 向后兼容重定向 ──
      { path: 'dashboard', redirect: '/student/home' },
      { path: 'my-courses', redirect: '/student/my-learning' },
      { path: 'recommend', redirect: '/student/courses' },
      { path: 'learning-center', redirect: '/student/my-learning' },
      { path: 'plan', redirect: '/student/my-learning' },
    ],
  },

  // Admin
  {
    path: '/admin',
    meta: { layout: 'admin', requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', name: 'AdminDashboard', component: () => import('@/views/admin/Dashboard.vue') },
      { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/Users.vue') },
      { path: 'courses', name: 'AdminCourses', component: () => import('@/views/admin/Courses.vue') },
      { path: 'sessions', name: 'AdminSessions', component: () => import('@/views/admin/Sessions.vue') },
      { path: 'enrollments', name: 'AdminEnrollments', component: () => import('@/views/admin/Enrollments.vue') },
    ],
  },

  // Instructor
  {
    path: '/instructor',
    meta: { layout: 'instructor', requiresAuth: true },
    children: [
      { path: '', redirect: '/instructor/sessions' },
      { path: 'sessions', name: 'InstructorSessions', component: () => import('@/views/instructor/MySessions.vue') },
    ],
  },

  // Design Preview (免登录测试入口，上线前删除)
  {
    path: '/preview',
    name: 'DesignPreview',
    component: () => import('@/views/preview/DesignPreview.vue'),
    meta: { layout: 'blank' },
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { layout: 'blank' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  },
})

// ⚠️ DEV_BYPASS: 设为 true 可跳过所有鉴权，纯前端预览（上线前务必改回 false）
const DEV_BYPASS = true

// Navigation guards
router.beforeEach(async (to, from, next) => {
  if (DEV_BYPASS) return next()

  const token = localStorage.getItem('access_token')

  // Redirect logged-in users away from auth pages
  if (to.meta.guest && token) {
    return next('/student/home')
  }

  // Auth check
  if (to.meta.requiresAuth && !token) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  // Admin check (lazy import to avoid circular deps)
  if (to.meta.requiresAdmin && token) {
    try {
      const { useUserStore } = await import('@/stores/user')
      const userStore = useUserStore()
      if (!userStore.userInfo) await userStore.fetchUserInfo()
      if (!userStore.isAdmin) return next('/student/home')
    } catch {
      return next('/login')
    }
  }

  next()
})

export default router
