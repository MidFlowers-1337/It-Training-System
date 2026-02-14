import request from './request'

export interface Achievement {
  id: number
  name: string
  description: string
  icon: string
  category: string
  points: number
  condition: string
}

export interface UserAchievement {
  achievementId: number
  achievement: Achievement
  earnedAt: string
}

export const achievementApi = {
  getAll: () => request.get<Achievement[]>('/v1/learning/achievements'),
  getEarned: () => request.get<UserAchievement[]>('/v1/learning/achievements/earned'),
  getRecent: () => request.get('/v1/learning/achievements/recent'),
  getDetail: (id: number) => request.get(`/v1/learning/achievements/${id}`),
  getPoints: () => request.get<number>('/v1/learning/achievements/points'),
}
