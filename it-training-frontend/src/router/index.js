import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 认证相关路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { guest: true }
  },

  // 根路由重定向
  {
    path: '/',
    redirect: '/home'
  },

  // 学员端路由（使用布局）
  {
    path: '/',
    component: () => import('@/layouts/StudentLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/student/Home.vue')
      },
      {
        path: 'courses',
        name: 'CourseList',
        component: () => import('@/views/student/CourseList.vue')
      },
      {
        path: 'course/:id',
        name: 'CourseDetail',
        component: () => import('@/views/student/CourseDetail.vue')
      },
      {
        path: 'my-courses',
        name: 'MyCourses',
        component: () => import('@/views/student/MyCourses.vue')
      },
      {
        path: 'recommend',
        name: 'SmartRecommend',
        component: () => import('@/views/student/SmartRecommend.vue')
      },
      {
        path: 'learning',
        name: 'LearningCenter',
        component: () => import('@/views/student/LearningCenter.vue'),
        meta: { title: '学习中心' }
      },
      {
        path: 'achievements',
        name: 'Achievements',
        component: () => import('@/views/student/Achievements.vue'),
        meta: { title: '我的成就' }
      },
      {
        path: 'learning-plan',
        name: 'LearningPlan',
        component: () => import('@/views/student/LearningPlan.vue'),
        meta: { title: '学习计划' }
      },
      {
        path: 'learning-report',
        name: 'LearningReport',
        component: () => import('@/views/student/LearningReport.vue'),
        meta: { title: '学习报告' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/student/UserProfile.vue'),
        meta: { title: '我的画像' }
      },
      {
        path: 'personal-center',
        name: 'PersonalCenter',
        component: () => import('@/views/student/PersonalCenter.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('@/views/student/Settings.vue'),
        meta: { title: '账号设置' }
      }
    ]
  },

  // 管理端路由
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('@/views/admin/Courses.vue'),
        meta: { title: '课程管理' }
      },
      {
        path: 'sessions',
        name: 'AdminSessions',
        component: () => import('@/views/admin/Sessions.vue'),
        meta: { title: '班期管理' }
      },
      {
        path: 'enrollments',
        name: 'AdminEnrollments',
        component: () => import('@/views/admin/Enrollments.vue'),
        meta: { title: '报名管理' }
      }
    ]
  },

  // 讲师端路由
  {
    path: '/instructor',
    component: () => import('@/layouts/InstructorLayout.vue'),
    meta: { requiresAuth: true, requiresInstructor: true },
    redirect: '/instructor/sessions',
    children: [
      {
        path: 'sessions',
        name: 'InstructorSessions',
        component: () => import('@/views/instructor/MySessions.vue'),
        meta: { title: '我的班期' }
      }
    ]
  },

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  // 需要认证的路由
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // 需要管理员权限的路由
  if (to.meta.requiresAdmin && userInfo?.role !== 'ADMIN') {
    next('/home')
    return
  }

  // 需要讲师权限的路由
  if (to.meta.requiresInstructor && userInfo?.role !== 'INSTRUCTOR' && userInfo?.role !== 'ADMIN') {
    next('/home')
    return
  }

  // 已登录用户访问登录/注册页面
  if (to.meta.guest && token) {
    // 根据角色跳转到不同首页
    if (userInfo?.role === 'ADMIN') {
      next('/admin/dashboard')
    } else {
      next('/home')
    }
    return
  }

  next()
})

export default router
