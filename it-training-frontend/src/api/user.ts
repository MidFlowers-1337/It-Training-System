import request from './request'

export interface User {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  avatar: string
  role: string
  status: number
  createdAt: string
}

export interface UserCreateRequest {
  username: string
  password: string
  realName?: string
  email?: string
  role: string
}

export const userApi = {
  list: (params?: any) => request.get('/v1/users', { params }),
  getById: (id: number) => request.get<User>(`/v1/users/${id}`),
  create: (data: UserCreateRequest) => request.post('/v1/users', data),
  update: (id: number, data: Partial<User>) => request.put(`/v1/users/${id}`, data),
  delete: (id: number) => request.delete(`/v1/users/${id}`),
  resetPassword: (id: number, data: { newPassword: string }) => request.patch(`/v1/users/${id}/password`, data),
  updateStatus: (id: number, data: { status: number }) => request.patch(`/v1/users/${id}/status`, data),
}
