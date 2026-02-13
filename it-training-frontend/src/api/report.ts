import request from './request'

export const reportApi = {
  weekly: () => request.get('/v1/learning/reports/weekly'),
  monthly: (year: number, month: number) => request.get(`/v1/learning/reports/monthly/${year}/${month}`),
  yearly: (year: number) => request.get(`/v1/learning/reports/yearly/${year}`),
  custom: (params: { startDate: string; endDate: string }) => request.get('/v1/learning/reports/custom', { params }),
}
