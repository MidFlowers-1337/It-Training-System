import request from '@/utils/request'

// 获取学生Dashboard数据
export function getStudentDashboard() {
  return request({
    url: '/v1/student/dashboard',
    method: 'get'
  })
}

// 学习打卡
export function checkin() {
  return request({
    url: '/v1/student/checkin',
    method: 'post'
  })
}

// 获取学习统计
export function getLearningStats() {
  return request({
    url: '/v1/student/stats',
    method: 'get'
  })
}

// 获取我的成就列表
export function getMyAchievements() {
  return request({
    url: '/v1/student/achievements',
    method: 'get'
  })
}

// 获取连续学习天数
export function getStreak() {
  return request({
    url: '/v1/student/streak',
    method: 'get'
  })
}
