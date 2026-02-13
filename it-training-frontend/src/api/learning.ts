import request from './request'

export interface LearningProgress {
  id: number
  userId: number
  courseId: number
  courseName: string
  progress: number
  status: string
  startedAt: string
  completedAt: string
}

export const learningApi = {
  getProgress: (params?: any) => request.get('/v1/learning/progress', { params }),
  getCourseProgress: (courseId: number) => request.get(`/v1/learning/progress/course/${courseId}`),
  updateProgress: (data: any) => request.post('/v1/learning/progress/update', data),
  markCompleted: (courseId: number) => request.post(`/v1/learning/progress/complete/${courseId}`),
  getInProgress: () => request.get('/v1/learning/progress/in-progress'),
}
