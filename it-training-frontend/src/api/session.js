import request from '@/utils/request'

// 获取班期列表
export function getSessions(params) {
  return request({
    url: '/sessions',
    method: 'get',
    params
  })
}

// 获取可报名班期列表
export function getEnrollableSessions(courseId) {
  return request({
    url: '/sessions/enrollable',
    method: 'get',
    params: { courseId }
  })
}

// 获取班期详情
export function getSessionById(id) {
  return request({
    url: `/sessions/${id}`,
    method: 'get'
  })
}

// 创建班期
export function createSession(data) {
  return request({
    url: '/sessions',
    method: 'post',
    data
  })
}

// 更新班期
export function updateSession(id, data) {
  return request({
    url: `/sessions/${id}`,
    method: 'put',
    data
  })
}

// 删除班期
export function deleteSession(id) {
  return request({
    url: `/sessions/${id}`,
    method: 'delete'
  })
}

// 开放报名
export function openEnrollment(id) {
  return request({
    url: `/sessions/${id}/open`,
    method: 'patch'
  })
}

// 关闭报名
export function closeEnrollment(id) {
  return request({
    url: `/sessions/${id}/close`,
    method: 'patch'
  })
}

// 获取讲师自己的班期列表
export function getMySessionsAsInstructor() {
  return request({
    url: '/sessions/my',
    method: 'get'
  })
}
