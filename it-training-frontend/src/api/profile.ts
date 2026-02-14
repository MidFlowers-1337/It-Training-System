import request from './request'

export const profileApi = {
  get: () => request.get('/v1/profile'),
  update: (data: any) => request.put('/v1/profile', data),
  changePassword: (data: { oldPassword: string; newPassword: string }) => request.post('/v1/profile/password', data),
  uploadAvatar: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/v1/profile/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
  },
  getSecurityInfo: () => request.get('/v1/profile/security'),
  bindEmail: (data: { email: string; code: string }) => request.post('/v1/profile/bind-email', data),
  bindPhone: (data: { phone: string; code: string }) => request.post('/v1/profile/bind-phone', data),
  sendEmailCode: (data: { email: string }) => request.post('/v1/profile/send-email-code', data),
  sendPhoneCode: (data: { phone: string }) => request.post('/v1/profile/send-phone-code', data),
  disableAccount: (data: { password: string }) => request.delete('/v1/profile', { data }),
  clearLearningData: (data: { password: string }) => request.post('/v1/profile/clear-data', data),
}
