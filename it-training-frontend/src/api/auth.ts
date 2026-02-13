import request from './request'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email?: string
  realName?: string
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export interface UserInfo {
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

export const authApi = {
  login: (data: LoginRequest) => request.post<TokenResponse>('/v1/auth/login', data),
  register: (data: RegisterRequest) => request.post('/v1/auth/register', data),
  getCurrentUser: () => request.get<UserInfo>('/v1/auth/me'),
  refresh: (refreshToken: string) => request.post<TokenResponse>('/v1/auth/refresh', { refreshToken }),
  logout: () => request.post('/v1/auth/logout'),
}
