import request from '@/utils/request'

// 获取课程列表
export function getCourses(params) {
  return request({
    url: '/v1/courses',
    method: 'get',
    params
  })
}

// 获取已发布课程列表
export function getPublishedCourses() {
  return request({
    url: '/v1/courses/published',
    method: 'get'
  })
}

// 获取课程详情
export function getCourseById(id) {
  return request({
    url: `/v1/courses/${id}`,
    method: 'get'
  })
}

// 创建课程
export function createCourse(data) {
  return request({
    url: '/v1/courses',
    method: 'post',
    data
  })
}

// 更新课程
export function updateCourse(id, data) {
  return request({
    url: `/v1/courses/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/v1/courses/${id}`,
    method: 'delete'
  })
}

// 发布课程
export function publishCourse(id) {
  return request({
    url: `/v1/courses/${id}/publish`,
    method: 'patch'
  })
}

// 下架课程
export function unpublishCourse(id) {
  return request({
    url: `/v1/courses/${id}/unpublish`,
    method: 'patch'
  })
}

// 获取课程章节列表
export function getCourseChapters(courseId) {
  return request({
    url: `/v1/courses/${courseId}/chapters`,
    method: 'get'
  })
}

// 获取章节详情
export function getChapterById(chapterId) {
  return request({
    url: `/v1/courses/chapters/${chapterId}`,
    method: 'get'
  })
}

// 标记章节为已完成
export function markChapterCompleted(chapterId) {
  return request({
    url: `/v1/courses/chapters/${chapterId}/complete`,
    method: 'post'
  })
}

// 更新章节观看进度
export function updateChapterProgress(chapterId, watchDuration, lastPosition) {
  return request({
    url: `/v1/courses/chapters/${chapterId}/progress`,
    method: 'post',
    params: {
      watchDuration,
      lastPosition
    }
  })
}
