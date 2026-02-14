import request from './request'

export interface LearningPlan {
  id: number
  userId: number
  title: string
  description: string
  status: string
  startDate: string
  endDate: string
  targetCourseIds: number[]
  createdAt: string
}

export const planApi = {
  create: (data: Partial<LearningPlan>) => request.post('/v1/learning/plans', data),
  list: (params?: any) => request.get('/v1/learning/plans', { params }),
  getActive: () => request.get('/v1/learning/plans/active'),
  getDetail: (id: number) => request.get<LearningPlan>(`/v1/learning/plans/${id}`),
  update: (id: number, data: Partial<LearningPlan>) => request.put(`/v1/learning/plans/${id}`, data),
  pause: (id: number) => request.post(`/v1/learning/plans/${id}/pause`),
  resume: (id: number) => request.post(`/v1/learning/plans/${id}/resume`),
  cancel: (id: number) => request.post(`/v1/learning/plans/${id}/cancel`),
  complete: (id: number) => request.post(`/v1/learning/plans/${id}/complete`),
}
