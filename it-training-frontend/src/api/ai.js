import request from '@/utils/request'

// 获取AI课程推荐
export function getAiRecommendation(learningGoal) {
  return request({
    url: '/ai/recommend',
    method: 'post',
    data: { learningGoal }
  })
}
