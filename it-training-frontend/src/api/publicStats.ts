import request from './request'

export interface PublicStats {
  totalCourses: number
  totalStudents: number
  totalInstructors: number
  completionRate: number
}

export interface PublicCourse {
  id: number
  code: string
  name: string
  category: string
  categoryName: string
  description: string
  coverImage: string
  difficulty: number
  difficultyName: string
  durationHours: number
  tags: string
}

export const publicStatsApi = {
  getStats: () => request.get<PublicStats>('/v1/public/stats'),
  getFeaturedCourses: (limit = 8) =>
    request.get<PublicCourse[]>('/v1/public/courses/featured', { params: { limit } }),
}
