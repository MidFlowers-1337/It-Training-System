import request from './request'

export const recommendApi = {
  byHistory: () => request.get('/v1/learning/recommend/history'),
  similarCourses: (courseId: number) => request.get(`/v1/learning/recommend/similar/${courseId}`),
  bySkills: () => request.get('/v1/learning/recommend/skills'),
  byPreference: () => request.get('/v1/learning/recommend/preference'),
  userBased: () => request.get('/v1/learning/recommend/user-based'),
  itemBased: () => request.get('/v1/learning/recommend/item-based'),
  similarUsers: () => request.get('/v1/learning/recommend/similar-users'),
  hybrid: () => request.get('/v1/learning/recommend/hybrid'),
  home: () => request.get('/v1/learning/recommend/home'),
  youMayLike: () => request.get('/v1/learning/recommend/you-may-like'),
  popular: () => request.get('/v1/learning/recommend/popular'),
  newCourses: () => request.get('/v1/learning/recommend/new'),
}
