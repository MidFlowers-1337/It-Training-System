import request from './request'

export interface StudentDashboard {
  totalCourses: number
  completedCourses: number
  inProgressCourses: number
  totalStudyHours: number
  weeklyStudyHours: number
  currentStreak: number
  todayCheckedIn: boolean
  recentCourses: any[]
  weeklyStats: any[]
}

export const studentApi = {
  getDashboard: () => request.get<StudentDashboard>('/v1/student/dashboard'),
  getStats: () => request.get('/v1/student/stats'),
  getAchievements: () => request.get('/v1/student/achievements'),
}
