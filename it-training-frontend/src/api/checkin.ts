import request from './request'

export interface CheckinRecord {
  id: number
  userId: number
  checkinDate: string
  studyMinutes: number
  note: string
  createdAt: string
}

export const checkinApi = {
  checkin: (data?: { note?: string; studyMinutes?: number }) => request.post('/v1/learning/checkin', data),
  isTodayCheckedIn: () => request.get<boolean>('/v1/learning/checkin/today'),
  getTodayDetail: () => request.get<CheckinRecord>('/v1/learning/checkin/today/detail'),
  getHistory: (params?: any) => request.get('/v1/learning/checkin/history', { params }),
  getStreak: () => request.get<number>('/v1/learning/checkin/streak'),
  getMonthlyCalendar: (year: number, month: number) => request.get(`/v1/learning/checkin/calendar/${year}/${month}`),
}
