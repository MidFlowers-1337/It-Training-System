import request from '@/utils/request'

// 获取报名列表
export function getEnrollments(params) {
  return request({
    url: '/enrollments',
    method: 'get',
    params
  })
}

// 报名
export function enroll(sessionId) {
  return request({
    url: '/enrollments',
    method: 'post',
    data: { sessionId }
  })
}

// 取消报名
export function cancelEnrollment(id, reason) {
  return request({
    url: `/enrollments/${id}/cancel`,
    method: 'patch',
    data: { reason }
  })
}

// 获取我的报名列表
export function getMyEnrollments() {
  return request({
    url: '/enrollments/my',
    method: 'get'
  })
}

// 分页查询报名列表（管理端/讲师端）
export function listEnrollments(page, size, userId, sessionId, status) {
  return request({
    url: '/enrollments',
    method: 'get',
    params: { page, size, userId, sessionId, status }
  })
}
