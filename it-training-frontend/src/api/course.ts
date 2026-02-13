import request from './request'

export interface Course {
  id: number
  title: string
  description: string
  category: string
  level: string
  coverImage: string
  instructor: string
  duration: number
  status: string
  enrollCount: number
  rating: number
  createdAt: string
}

export interface Chapter {
  id: number
  courseId: number
  title: string
  orderNum: number
  duration: number
  contentType: string
  contentUrl: string
}

export interface CourseQuery {
  page?: number
  size?: number
  keyword?: string
  category?: string
  level?: string
}

export const courseApi = {
  list: (params?: CourseQuery) => request.get('/v1/courses', { params }),
  listPublished: (params?: CourseQuery) => request.get('/v1/courses/published', { params }),
  getById: (id: number) => request.get<Course>(`/v1/courses/${id}`),
  create: (data: Partial<Course>) => request.post('/v1/courses', data),
  update: (id: number, data: Partial<Course>) => request.put(`/v1/courses/${id}`, data),
  delete: (id: number) => request.delete(`/v1/courses/${id}`),
  publish: (id: number) => request.patch(`/v1/courses/${id}/publish`),
  unpublish: (id: number) => request.patch(`/v1/courses/${id}/unpublish`),
  getChapters: (courseId: number) => request.get<Chapter[]>(`/v1/courses/${courseId}/chapters`),
  getChapter: (chapterId: number) => request.get<Chapter>(`/v1/courses/chapters/${chapterId}`),
  completeChapter: (chapterId: number) => request.post(`/v1/courses/chapters/${chapterId}/complete`),
  updateChapterProgress: (chapterId: number, data: any) => request.post(`/v1/courses/chapters/${chapterId}/progress`, data),
}
