import request from './request'

export interface Session {
  id: number
  courseId: number
  courseName: string
  instructorId: number
  instructorName: string
  startDate: string
  endDate: string
  maxStudents: number
  enrolledCount: number
  status: string
  location: string
}

export const sessionApi = {
  list: (params?: any) => request.get('/v1/sessions', { params }),
  listEnrollable: (params?: any) => request.get('/v1/sessions/enrollable', { params }),
  getById: (id: number) => request.get<Session>(`/v1/sessions/${id}`),
  create: (data: Partial<Session>) => request.post('/v1/sessions', data),
  update: (id: number, data: Partial<Session>) => request.put(`/v1/sessions/${id}`, data),
  delete: (id: number) => request.delete(`/v1/sessions/${id}`),
  open: (id: number) => request.patch(`/v1/sessions/${id}/open`),
  close: (id: number) => request.patch(`/v1/sessions/${id}/close`),
  getMySessions: () => request.get('/v1/sessions/my'),
}
