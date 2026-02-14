import request from './request'

export interface Enrollment {
  id: number
  userId: number
  sessionId: number
  courseName: string
  status: string
  enrolledAt: string
}

export const enrollmentApi = {
  enroll: (data: { sessionId: number }) => request.post('/v1/enrollments', data),
  cancel: (id: number) => request.patch(`/v1/enrollments/${id}/cancel`),
  getMy: (params?: any) => request.get('/v1/enrollments/my', { params }),
  list: (params?: any) => request.get('/v1/enrollments', { params }),
  getById: (id: number) => request.get<Enrollment>(`/v1/enrollments/${id}`),
}
