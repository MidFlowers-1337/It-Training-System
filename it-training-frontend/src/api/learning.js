import request from '@/utils/request'

// ==================== 学习仪表盘 ====================

/**
 * 获取学习仪表盘
 */
export function getDashboard() {
  return request({
    url: '/v1/learning/dashboard',
    method: 'get'
  })
}

/**
 * 获取用户学习统计
 */
export function getUserStats() {
  return request({
    url: '/v1/learning/stats',
    method: 'get'
  })
}

// ==================== 学习进度 ====================

/**
 * 获取所有课程学习进度
 */
export function getUserProgress() {
  return request({
    url: '/v1/learning/progress',
    method: 'get'
  })
}

/**
 * 获取指定课程学习进度
 * @param {number} courseId 课程ID
 */
export function getCourseProgress(courseId) {
  return request({
    url: `/v1/learning/progress/course/${courseId}`,
    method: 'get'
  })
}

/**
 * 更新学习进度
 * @param {Object} data 进度数据
 */
export function updateProgress(data) {
  return request({
    url: '/v1/learning/progress/update',
    method: 'post',
    data
  })
}

/**
 * 标记课程完成
 * @param {number} courseId 课程ID
 */
export function markCompleted(courseId) {
  return request({
    url: `/v1/learning/progress/complete/${courseId}`,
    method: 'post'
  })
}

/**
 * 获取进行中的课程
 */
export function getInProgressCourses() {
  return request({
    url: '/v1/learning/progress/in-progress',
    method: 'get'
  })
}

// ==================== 学习打卡 ====================

/**
 * 学习打卡
 * @param {Object} data 打卡数据
 */
export function checkin(data) {
  return request({
    url: '/v1/learning/checkin',
    method: 'post',
    data
  })
}

/**
 * 检查今日是否已打卡
 */
export function isTodayCheckedIn() {
  return request({
    url: '/v1/learning/checkin/today',
    method: 'get'
  })
}

/**
 * 获取今日打卡详情
 */
export function getTodayCheckin() {
  return request({
    url: '/v1/learning/checkin/today/detail',
    method: 'get'
  })
}

/**
 * 获取打卡历史
 * @param {string} startDate 开始日期 (YYYY-MM-DD)
 * @param {string} endDate 结束日期 (YYYY-MM-DD)
 */
export function getCheckinHistory(startDate, endDate) {
  return request({
    url: '/v1/learning/checkin/history',
    method: 'get',
    params: { startDate, endDate }
  })
}

/**
 * 获取当前连续打卡天数
 */
export function getCurrentStreak() {
  return request({
    url: '/v1/learning/checkin/streak',
    method: 'get'
  })
}

/**
 * 获取月度打卡日历
 * @param {number} year 年份
 * @param {number} month 月份
 */
export function getMonthlyCheckinDates(year, month) {
  return request({
    url: `/v1/learning/checkin/calendar/${year}/${month}`,
    method: 'get'
  })
}

// ==================== 成就系统 ====================

/**
 * 获取所有成就（含获得状态）
 */
export function getAllAchievements() {
  return request({
    url: '/v1/learning/achievements',
    method: 'get'
  })
}

/**
 * 获取已获得的成就
 */
export function getUserAchievements() {
  return request({
    url: '/v1/learning/achievements/earned',
    method: 'get'
  })
}

/**
 * 获取最近获得的成就
 * @param {number} limit 数量限制
 */
export function getRecentAchievements(limit = 5) {
  return request({
    url: '/v1/learning/achievements/recent',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取成就详情
 * @param {number} achievementId 成就ID
 */
export function getAchievementDetail(achievementId) {
  return request({
    url: `/v1/learning/achievements/${achievementId}`,
    method: 'get'
  })
}

/**
 * 获取成就积分
 */
export function getAchievementPoints() {
  return request({
    url: '/v1/learning/achievements/points',
    method: 'get'
  })
}

// ==================== 学习计划 ====================

/**
 * 创建学习计划
 * @param {Object} data 计划数据
 */
export function createPlan(data) {
  return request({
    url: '/v1/learning/plans',
    method: 'post',
    data
  })
}

/**
 * 获取所有学习计划
 */
export function getUserPlans() {
  return request({
    url: '/v1/learning/plans',
    method: 'get'
  })
}

/**
 * 获取当前进行中的学习计划
 */
export function getActivePlan() {
  return request({
    url: '/v1/learning/plans/active',
    method: 'get'
  })
}

/**
 * 获取学习计划详情
 * @param {number} planId 计划ID
 */
export function getPlanDetail(planId) {
  return request({
    url: `/v1/learning/plans/${planId}`,
    method: 'get'
  })
}

/**
 * 更新学习计划
 * @param {number} planId 计划ID
 * @param {Object} data 计划数据
 */
export function updatePlan(planId, data) {
  return request({
    url: `/v1/learning/plans/${planId}`,
    method: 'put',
    data
  })
}

/**
 * 暂停学习计划
 * @param {number} planId 计划ID
 */
export function pausePlan(planId) {
  return request({
    url: `/v1/learning/plans/${planId}/pause`,
    method: 'post'
  })
}

/**
 * 恢复学习计划
 * @param {number} planId 计划ID
 */
export function resumePlan(planId) {
  return request({
    url: `/v1/learning/plans/${planId}/resume`,
    method: 'post'
  })
}

/**
 * 取消学习计划
 * @param {number} planId 计划ID
 */
export function cancelPlan(planId) {
  return request({
    url: `/v1/learning/plans/${planId}/cancel`,
    method: 'post'
  })
}

/**
 * 完成学习计划
 * @param {number} planId 计划ID
 */
export function completePlan(planId) {
  return request({
    url: `/v1/learning/plans/${planId}/complete`,
    method: 'post'
  })
}

// ==================== 学习报告 ====================

/**
 * 获取周报
 * @param {string} weekStart 周开始日期 (YYYY-MM-DD)
 */
export function getWeeklyReport(weekStart) {
  return request({
    url: '/v1/learning/reports/weekly',
    method: 'get',
    params: { weekStart }
  })
}

/**
 * 获取月报
 * @param {number} year 年份
 * @param {number} month 月份
 */
export function getMonthlyReport(year, month) {
  return request({
    url: `/v1/learning/reports/monthly/${year}/${month}`,
    method: 'get'
  })
}

/**
 * 获取年报
 * @param {number} year 年份
 */
export function getYearlyReport(year) {
  return request({
    url: `/v1/learning/reports/yearly/${year}`,
    method: 'get'
  })
}

/**
 * 获取自定义时间段报告
 * @param {string} startDate 开始日期 (YYYY-MM-DD)
 * @param {string} endDate 结束日期 (YYYY-MM-DD)
 */
export function getCustomReport(startDate, endDate) {
  return request({
    url: '/v1/learning/reports/custom',
    method: 'get',
    params: { startDate, endDate }
  })
}

// ==================== 智能推荐 ====================

/**
 * 基于学习历史推荐课程
 * @param {number} limit 推荐数量
 */
export function recommendByHistory(limit = 10) {
  return request({
    url: '/v1/learning/recommend/history',
    method: 'get',
    params: { limit }
  })
}

/**
 * 推荐相似课程
 * @param {number} courseId 课程ID
 * @param {number} limit 推荐数量
 */
export function recommendSimilarCourses(courseId, limit = 5) {
  return request({
    url: `/v1/learning/recommend/similar/${courseId}`,
    method: 'get',
    params: { limit }
  })
}

/**
 * 基于技能标签推荐课程
 * @param {number} limit 推荐数量
 */
export function recommendBySkills(limit = 10) {
  return request({
    url: '/v1/learning/recommend/skills',
    method: 'get',
    params: { limit }
  })
}

/**
 * 基于用户偏好推荐课程
 * @param {number} limit 推荐数量
 */
export function recommendByPreference(limit = 10) {
  return request({
    url: '/v1/learning/recommend/preference',
    method: 'get',
    params: { limit }
  })
}

// ==================== 协同过滤推荐 ====================

/**
 * 基于用户的协同过滤推荐
 * @param {number} limit 推荐数量
 */
export function recommendByUserBased(limit = 10) {
  return request({
    url: '/v1/learning/recommend/user-based',
    method: 'get',
    params: { limit }
  })
}

/**
 * 基于物品的协同过滤推荐
 * @param {number} limit 推荐数量
 */
export function recommendByItemBased(limit = 10) {
  return request({
    url: '/v1/learning/recommend/item-based',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取相似用户
 * @param {number} limit 数量限制
 */
export function getSimilarUsers(limit = 10) {
  return request({
    url: '/v1/learning/recommend/similar-users',
    method: 'get',
    params: { limit }
  })
}

// ==================== 混合推荐 ====================

/**
 * 获取混合推荐课程
 * @param {number} limit 推荐数量
 */
export function getHybridRecommendations(limit = 10) {
  return request({
    url: '/v1/learning/recommend/hybrid',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取首页推荐课程
 */
export function getHomePageRecommendations() {
  return request({
    url: '/v1/learning/recommend/home',
    method: 'get'
  })
}

/**
 * 获取猜你喜欢课程
 * @param {number} limit 推荐数量
 */
export function getYouMayLike(limit = 6) {
  return request({
    url: '/v1/learning/recommend/you-may-like',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取热门课程
 * @param {number} limit 推荐数量
 */
export function getPopularCourses(limit = 10) {
  return request({
    url: '/v1/learning/recommend/popular',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取新上架课程
 * @param {number} limit 推荐数量
 */
export function getNewCourses(limit = 10) {
  return request({
    url: '/v1/learning/recommend/new',
    method: 'get',
    params: { limit }
  })
}

// ==================== 用户画像 ====================

/**
 * 获取用户画像
 */
export function getUserProfile() {
  return request({
    url: '/v1/learning/profile',
    method: 'get'
  })
}

/**
 * 更新技能标签
 * @param {Array} skillTags 技能标签列表
 */
export function updateSkillTags(skillTags) {
  return request({
    url: '/v1/learning/profile/skills',
    method: 'post',
    data: skillTags
  })
}

/**
 * 更新学习偏好
 * @param {Object} data 偏好数据
 */
export function updatePreferences(data) {
  return request({
    url: '/v1/learning/profile/preferences',
    method: 'post',
    data
  })
}

/**
 * 获取学习能力评估
 */
export function getLearningAbilityAssessment() {
  return request({
    url: '/v1/learning/profile/ability-assessment',
    method: 'get'
  })
}

/**
 * 分析学习行为
 */
export function analyzeLearningBehavior() {
  return request({
    url: '/v1/learning/profile/analyze',
    method: 'post'
  })
}