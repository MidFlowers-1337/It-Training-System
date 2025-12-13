import request from '@/utils/request'

// 获取统计概览
export function getStatsOverview() {
  return request({
    url: '/stats/overview',
    method: 'get'
  })
}

// 获取课程热度排行
export function getCourseHotRanking(limit = 10) {
  return request({
    url: '/stats/course-hot',
    method: 'get',
    params: { limit }
  })
}

// 获取报名趋势
export function getEnrollmentTrend(days = 7) {
  return request({
    url: '/stats/enrollment-trend',
    method: 'get',
    params: { days }
  })
}
