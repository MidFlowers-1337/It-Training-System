import request from '@/utils/request'

// 获取AI课程推荐
// AI 响应较慢，设置更长的超时时间
export function getAiRecommendation(learningGoal) {
  return request({
    url: '/v1/ai/recommend',
    method: 'post',
    data: { learningGoal },
    timeout: 60000 // AI 请求超时时间设置为 60 秒
  })
}
