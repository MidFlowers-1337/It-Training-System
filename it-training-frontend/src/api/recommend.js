import request from '@/utils/request'

// 获取AI课程推荐
export function getRecommendation(learningGoal) {
  return request({
    url: '/recommend',
    method: 'post',
    data: { learningGoal }
  })
}
