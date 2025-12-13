<template>
  <div class="home-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner animate-slideUp">
      <div class="banner-bg">
        <div class="banner-circle circle-1"></div>
        <div class="banner-circle circle-2"></div>
        <div class="banner-circle circle-3"></div>
      </div>
      <div class="banner-content">
        <div class="banner-left">
          <div class="greeting">
            <span class="greeting-icon">👋</span>
            <span class="greeting-text">{{ getGreeting() }}</span>
          </div>
          <h1 class="banner-title">欢迎回来，{{ userInfo?.realName || '同学' }}！</h1>
          <p class="banner-subtitle">开启您的IT技能学习之旅，让AI为您规划最佳学习路径</p>
          <div class="banner-actions">
            <el-button type="primary" size="large" class="action-btn primary-btn" @click="$router.push('/recommend')">
              <el-icon><MagicStick /></el-icon>
              AI智能选课
            </el-button>
            <el-button size="large" class="action-btn secondary-btn" @click="$router.push('/courses')">
              浏览全部课程
            </el-button>
          </div>
        </div>
        <div class="banner-right">
          <div class="banner-illustration">
            <div class="illustration-item item-1">📚</div>
            <div class="illustration-item item-2">💻</div>
            <div class="illustration-item item-3">🎯</div>
            <div class="illustration-item item-4">🚀</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-card animate-slideUp" style="animation-delay: 0.1s" @click="$router.push('/my-courses')">
          <div class="stat-icon-wrapper enrolled">
            <span class="stat-icon">📚</span>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.enrolledCount }}</div>
            <div class="stat-label">已报名课程</div>
          </div>
          <div class="stat-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="stat-card animate-slideUp" style="animation-delay: 0.2s">
          <div class="stat-icon-wrapper learning">
            <span class="stat-icon">📖</span>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.learningCount }}</div>
            <div class="stat-label">进行中课程</div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="60" :show-text="false" :stroke-width="4" color="#3b82f6" />
          </div>
        </div>

        <div class="stat-card animate-slideUp" style="animation-delay: 0.3s" @click="$router.push('/courses')">
          <div class="stat-icon-wrapper courses">
            <span class="stat-icon">🎯</span>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalCourses }}</div>
            <div class="stat-label">可选课程</div>
          </div>
          <div class="stat-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>

        <div class="stat-card ai-card animate-slideUp" style="animation-delay: 0.4s" @click="$router.push('/recommend')">
          <div class="ai-glow"></div>
          <div class="stat-icon-wrapper ai">
            <span class="stat-icon">✨</span>
          </div>
          <div class="stat-info">
            <div class="stat-number">AI</div>
            <div class="stat-label">智能推荐</div>
          </div>
          <div class="ai-badge">NEW</div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <el-row :gutter="24">
        <!-- 左侧：快速入口 + 热门课程 -->
        <el-col :xs="24" :lg="16">
          <!-- 快速入口 -->
          <div class="section-card animate-slideUp" style="animation-delay: 0.5s">
            <div class="section-header">
              <h3 class="section-title">
                <el-icon><Grid /></el-icon>
                快速入口
              </h3>
            </div>
            <div class="quick-actions">
              <div class="action-card" @click="$router.push('/recommend')">
                <div class="action-icon-wrapper ai-gradient">
                  <span class="action-icon">🎯</span>
                </div>
                <div class="action-content">
                  <h4>AI智能选课</h4>
                  <p>根据您的学习目标，AI为您推荐最适合的课程和学习路径</p>
                </div>
                <div class="action-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>

              <div class="action-card" @click="$router.push('/courses')">
                <div class="action-icon-wrapper blue-gradient">
                  <span class="action-icon">📚</span>
                </div>
                <div class="action-content">
                  <h4>浏览课程</h4>
                  <p>查看所有可选课程，按分类、难度筛选</p>
                </div>
                <div class="action-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>

              <div class="action-card" @click="$router.push('/my-courses')">
                <div class="action-icon-wrapper green-gradient">
                  <span class="action-icon">📋</span>
                </div>
                <div class="action-content">
                  <h4>我的课程</h4>
                  <p>管理已报名的课程，查看学习进度</p>
                </div>
                <div class="action-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
              </div>
            </div>
          </div>

          <!-- 热门课程推荐 -->
          <div class="section-card animate-slideUp" style="animation-delay: 0.6s">
            <div class="section-header">
              <h3 class="section-title">
                <el-icon><TrendCharts /></el-icon>
                热门课程
              </h3>
              <el-button text type="primary" @click="$router.push('/courses')">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="course-list">
              <div class="course-item" v-for="course in hotCourses" :key="course.id">
                <div class="course-cover" :style="{ background: getCourseGradient(course.category) }">
                  <span class="course-emoji">{{ getCourseEmoji(course.category) }}</span>
                </div>
                <div class="course-info">
                  <h4 class="course-name">{{ course.name }}</h4>
                  <div class="course-meta">
                    <el-tag size="small" :type="getDifficultyType(course.difficulty)">
                      {{ getDifficultyName(course.difficulty) }}
                    </el-tag>
                    <span class="course-duration">{{ course.durationHours }}课时</span>
                  </div>
                </div>
                <el-button type="primary" size="small" plain @click="$router.push(`/course/${course.id}`)">
                  查看详情
                </el-button>
              </div>
              <el-empty v-if="hotCourses.length === 0" description="暂无课程" />
            </div>
          </div>
        </el-col>

        <!-- 右侧：学习小贴士 + 公告 -->
        <el-col :xs="24" :lg="8">
          <!-- 学习小贴士 -->
          <div class="section-card tips-card animate-slideUp" style="animation-delay: 0.7s">
            <div class="section-header">
              <h3 class="section-title">
                <el-icon><InfoFilled /></el-icon>
                学习小贴士
              </h3>
            </div>
            <div class="tips-list">
              <div class="tip-item">
                <div class="tip-icon">💡</div>
                <div class="tip-content">
                  <h5>循序渐进</h5>
                  <p>建议从基础课程开始，打好基础再进阶</p>
                </div>
              </div>
              <div class="tip-item">
                <div class="tip-icon">🎯</div>
                <div class="tip-content">
                  <h5>智能推荐</h5>
                  <p>使用AI推荐获取个性化学习路径</p>
                </div>
              </div>
              <div class="tip-item">
                <div class="tip-icon">📅</div>
                <div class="tip-content">
                  <h5>合理规划</h5>
                  <p>关注班期时间，合理安排学习计划</p>
                </div>
              </div>
              <div class="tip-item">
                <div class="tip-icon">🔄</div>
                <div class="tip-content">
                  <h5>持续学习</h5>
                  <p>保持学习节奏，每天进步一点点</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 系统公告 -->
          <div class="section-card notice-card animate-slideUp" style="animation-delay: 0.8s">
            <div class="section-header">
              <h3 class="section-title">
                <el-icon><Bell /></el-icon>
                系统公告
              </h3>
            </div>
            <div class="notice-list">
              <div class="notice-item">
                <div class="notice-dot new"></div>
                <div class="notice-content">
                  <p class="notice-text">AI智能选课功能已上线，快来体验！</p>
                  <span class="notice-time">2025-01-01</span>
                </div>
              </div>
              <div class="notice-item">
                <div class="notice-dot"></div>
                <div class="notice-content">
                  <p class="notice-text">新增多门热门IT课程</p>
                  <span class="notice-time">2024-12-28</span>
                </div>
              </div>
              <div class="notice-item">
                <div class="notice-dot"></div>
                <div class="notice-content">
                  <p class="notice-text">系统升级维护通知</p>
                  <span class="notice-time">2024-12-25</span>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { InfoFilled, ArrowRight, Grid, TrendCharts, Bell, MagicStick } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getMyEnrollments } from '@/api/enrollment'
import { getCourses } from '@/api/course'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const stats = ref({
  enrolledCount: 0,
  learningCount: 0,
  totalCourses: 0
})

const hotCourses = ref([])

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
}

// 获取课程渐变色
const getCourseGradient = (category) => {
  const gradients = {
    'BACKEND': 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'FRONTEND': 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'DATABASE': 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'AI': 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'CLOUD': 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  }
  return gradients[category] || gradients['BACKEND']
}

// 获取课程图标
const getCourseEmoji = (category) => {
  const emojis = {
    'BACKEND': '⚙️',
    'FRONTEND': '🎨',
    'DATABASE': '🗄️',
    'AI': '🤖',
    'CLOUD': '☁️'
  }
  return emojis[category] || '📚'
}

// 获取难度类型
const getDifficultyType = (difficulty) => {
  const types = { 1: 'success', 2: 'info', 3: 'warning', 4: 'danger' }
  return types[difficulty] || 'info'
}

// 获取难度名称
const getDifficultyName = (difficulty) => {
  const names = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
  return names[difficulty] || '未知'
}

const loadStats = async () => {
  try {
    // 获取我的报名
    const enrollRes = await getMyEnrollments()
    const enrollments = enrollRes.data || []
    stats.value.enrolledCount = enrollments.filter(e => e.status === 0).length
    stats.value.learningCount = enrollments.filter(e => e.status === 0).length

    // 获取可选课程数
    const courseRes = await getCourses({ page: 1, size: 5, status: 1 })
    stats.value.totalCourses = courseRes.data?.total || 0
    hotCourses.value = courseRes.data?.records || []
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.home-page {
  max-width: 1400px;
  margin: 0 auto;
}

/* 欢迎横幅 */
.welcome-banner {
  position: relative;
  background: var(--gradient-primary);
  border-radius: var(--radius-xl);
  padding: 48px;
  margin-bottom: 24px;
  overflow: hidden;
}

.banner-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.banner-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -50px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -80px;
  left: 20%;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 30%;
}

.banner-content {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1;
}

.banner-left {
  flex: 1;
  max-width: 600px;
}

.greeting {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.greeting-icon {
  font-size: 24px;
}

.greeting-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.9);
}

.banner-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin-bottom: 12px;
}

.banner-subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.85);
  margin-bottom: 24px;
  line-height: 1.6;
}

.banner-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  height: 44px;
  padding: 0 24px;
  font-size: 15px;
  font-weight: 600;
  border-radius: var(--radius-lg);
}

.primary-btn {
  background: white;
  color: var(--primary-color);
  border: none;
}

.primary-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  color: var(--primary-dark);
}

.secondary-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.secondary-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.banner-right {
  display: none;
}

@media (min-width: 1024px) {
  .banner-right {
    display: block;
  }
}

.banner-illustration {
  position: relative;
  width: 200px;
  height: 200px;
}

.illustration-item {
  position: absolute;
  font-size: 48px;
  animation: float 3s ease-in-out infinite;
}

.item-1 { top: 0; left: 20%; animation-delay: 0s; }
.item-2 { top: 30%; right: 0; animation-delay: 0.5s; }
.item-3 { bottom: 20%; left: 0; animation-delay: 1s; }
.item-4 { bottom: 0; right: 20%; animation-delay: 1.5s; }

/* 统计卡片 */
.stats-section {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon-wrapper.enrolled { background: #dcfce7; }
.stat-icon-wrapper.learning { background: #dbeafe; }
.stat-icon-wrapper.courses { background: #fef3c7; }
.stat-icon-wrapper.ai { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }

.stat-icon {
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.stat-arrow {
  color: var(--text-muted);
  transition: transform var(--transition-fast);
}

.stat-card:hover .stat-arrow {
  transform: translateX(4px);
  color: var(--primary-color);
}

.stat-progress {
  width: 60px;
}

/* AI卡片特殊样式 */
.ai-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.ai-card .stat-number,
.ai-card .stat-label {
  color: white;
}

.ai-card .stat-icon-wrapper {
  background: rgba(255, 255, 255, 0.2);
}

.ai-glow {
  position: absolute;
  top: -50%;
  right: -50%;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
  animation: pulse 2s ease-in-out infinite;
}

.ai-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: white;
  color: var(--primary-color);
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: var(--radius-full);
}

/* 内容区域 */
.section-card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: 24px;
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.section-title .el-icon {
  color: var(--primary-color);
}

/* 快速入口 */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.action-card:hover {
  background: var(--primary-bg);
  transform: translateX(4px);
}

.action-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-gradient { background: var(--gradient-primary); }
.blue-gradient { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); }
.green-gradient { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }

.action-icon {
  font-size: 24px;
}

.action-content {
  flex: 1;
}

.action-content h4 {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.action-content p {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
}

.action-arrow {
  color: var(--text-muted);
  transition: transform var(--transition-fast);
}

.action-card:hover .action-arrow {
  transform: translateX(4px);
  color: var(--primary-color);
}

/* 课程列表 */
.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.course-item:hover {
  background: var(--bg-tertiary);
}

.course-cover {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.course-emoji {
  font-size: 24px;
}

.course-info {
  flex: 1;
  min-width: 0;
}

.course-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 6px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.course-duration {
  font-size: 12px;
  color: var(--text-muted);
}

/* 学习小贴士 */
.tips-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tip-item {
  display: flex;
  gap: 12px;
}

.tip-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.tip-content h5 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 4px 0;
}

.tip-content p {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.5;
}

/* 公告 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: var(--radius-md);
}

.notice-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--text-muted);
  margin-top: 6px;
  flex-shrink: 0;
}

.notice-dot.new {
  background: var(--danger-color);
  animation: pulse 2s ease-in-out infinite;
}

.notice-content {
  flex: 1;
}

.notice-text {
  font-size: 14px;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  line-height: 1.5;
}

.notice-time {
  font-size: 12px;
  color: var(--text-muted);
}

/* 动画 */
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.animate-slideUp {
  animation: slideUp 0.5s ease-out forwards;
  opacity: 0;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
