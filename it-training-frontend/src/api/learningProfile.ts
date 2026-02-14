import request from './request'

export interface UserProfile {
  userId: number
  learningStyle: string
  skillTags: string[]
  weeklyGoalHours: number
  preferredCategories: string[]
  abilityScores: Record<string, number>
}

export const learningProfileApi = {
  getProfile: () => request.get<UserProfile>('/v1/learning/profile'),
  updateSkills: (skills: string[]) => request.post('/v1/learning/profile/skills', { skillTags: skills }),
  updatePreferences: (data: any) => request.post('/v1/learning/profile/preferences', data),
  getAbilityAssessment: () => request.get('/v1/learning/profile/ability-assessment'),
  analyzeBehavior: () => request.post('/v1/learning/profile/analyze'),
}
