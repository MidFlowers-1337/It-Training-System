import request from './request'

export const statsApi = {
  getOverview: () => request.get('/v1/stats/overview'),
  getCourseHot: () => request.get('/v1/stats/course-hot'),
  getEnrollmentTrend: () => request.get('/v1/stats/enrollment-trend'),
}
