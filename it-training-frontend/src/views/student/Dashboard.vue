<template>
  <div class="student-dashboard">
    <!-- 欢迎区域 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <div class="user-greeting">
          <h2>👋 欢迎回来，{{ userInfo.realName }}！</h2>
          <div class="today-stats">
            <div class="stat-item">
              <span class="stat-label">今日已学习</span>
              <span class="stat-value">{{ todayStats.studyMinutes }} 分钟</span>
            </div>
            <div class="stat-item streak">
              <el-icon><Sunny /></el-icon>
              <span class="stat-value">连续 {{ todayStats.streakDays }} 天</span>
            </div>
          </div>
          <div class="quick-actions">
            <el-button type="primary" @click="checkin" :disabled="todayStats.checkedIn">
              {{ todayStats.checkedIn ? '✓ 今日已打卡' : '学习打卡' }}
            </el-button>
            <el-button @click="goToLearningPlan">学习计划</el-button>
          </div>
        </div>
        <div class="user-level">
          <div class="level-badge">
            <span class="level-text">Lv.{{ userInfo.level }}</span>
          </div>
          <div class="exp-progress">
            <el-progress
              :percentage="expPercentage"
              :stroke-width="8"
              :show-text="false"
            />
            <span class="exp-text">{{ userInfo.experience }}/{{ userInfo.nextLevelExp }} 经验</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <!-- 左侧主要内容 -->
      <el-col :xs="24" :lg="16">
        <!-- 继续学习 -->
        <el-card class="continue-learning-card" v-if="continueLearning">
          <template #header>
            <div class="card-header">
              <span>📚 继续学习</span>
            </div>
          </template>
          <div class="continue-learning-content" @click="continueCourse">
            <img :src="continueLearning.coverImage" class="course-cover" />
            <div class="course-info">
              <h3>{{ continueLearning.courseName }}</h3>
              <el-progress :percentage="continueLearning.progressPercent" />
              <p class="current-chapter">{{ continueLearning.currentChapter }}</p>
              <el-button type="primary" size="large">继续学习 →</el-button>
            </div>
          </div>
        </el-card>

        <!-- 我的课程 -->
        <el-card class="my-courses-card">
          <template #header>
            <div class="card-header">
              <span>🎯 我的课程 ({{ myCourses.length }})</span>
              <el-button link @click="goToMyCourses">查看全部 →</el-button>
            </div>
          </template>
          <div class="courses-grid">
            <div
              v-for="course in myCourses"
              :key="course.courseId"
              class="course-card"
              @click="goToCourse(course.courseId)"
            >
              <img :src="course.coverImage" class="course-cover" />
              <div class="course-details">
                <h4>{{ course.courseName }}</h4>
                <el-progress :percentage="course.progressPercent" :stroke-width="6" />
                <el-tag :type="getStatusType(course.status)" size="small">
                  {{ course.status }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 最近成就 -->
        <el-card class="achievements-card">
          <template #header>
            <div class="card-header">
              <span>🏆 最近成就</span>
              <el-button link @click="goToAchievements">查看全部 →</el-button>
            </div>
          </template>
          <div class="achievements-list">
            <div
              v-for="achievement in recentAchievements"
              :key="achievement.achievementId"
              class="achievement-item"
            >
              <span class="achievement-icon">{{ achievement.icon }}</span>
              <div class="achievement-info">
                <span class="achievement-name">{{ achievement.name }}</span>
                <span class="achievement-time">{{ achievement.unlockedAt }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧统计 -->
      <el-col :xs="24" :lg="8">
        <!-- 本周学习统计 -->
        <el-card class="stats-card">
          <template #header>
            <span>📊 本周学习统计</span>
          </template>
          <div ref="weeklyChart" class="weekly-chart"></div>
          <div class="stats-summary">
            <div class="summary-item">
              <span class="label">本周总计</span>
              <span class="value">{{ weeklyStats.totalMinutes }} 分钟</span>
            </div>
            <div class="summary-item">
              <span class="label">日均学习</span>
              <span class="value">{{ Math.round(weeklyStats.totalMinutes / 7) }} 分钟</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Sunny } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const router = useRouter()

// 模拟数据
const userInfo = ref({
  username: 'student001',
  realName: '张三',
  level: 5,
  experience: 1250,
  nextLevelExp: 2000
})

const todayStats = ref({
  studyMinutes: 30,
  streakDays: 7,
  checkedIn: false
})

const continueLearning = ref({
  courseId: 2,
  courseName: 'Spring Boot 实战开发',
  coverImage: 'https://via.placeholder.com/300x180?text=Spring+Boot',
  progressPercent: 65,
  currentChapter: '第3章：RESTful API 开发',
  currentChapterId: 8
})

const weeklyStats = ref({
  dailyMinutes: [30, 45, 60, 40, 55, 70, 50],
  totalMinutes: 350
})

const myCourses = ref([
  {
    courseId: 1,
    courseName: 'Java 基础入门',
    coverImage: 'https://via.placeholder.com/200x120?text=Java',
    progressPercent: 45,
    status: '进行中'
  },
  {
    courseId: 2,
    courseName: 'Spring Boot 实战',
    coverImage: 'https://via.placeholder.com/200x120?text=Spring',
    progressPercent: 65,
    status: '进行中'
  },
  {
    courseId: 3,
    courseName: 'Vue 3 从入门到精通',
    coverImage: 'https://via.placeholder.com/200x120?text=Vue3',
    progressPercent: 80,
    status: '进行中'
  },
  {
    courseId: 4,
    courseName: 'MySQL 数据库进阶',
    coverImage: 'https://via.placeholder.com/200x120?text=MySQL',
    progressPercent: 30,
    status: '进行中'
  }
])

const recentAchievements = ref([
  {
    achievementId: 1,
    name: '连续7天学习',
    icon: '🔥🔥',
    unlockedAt: '2小时前'
  },
  {
    achievementId: 2,
    name: '完成首个课程',
    icon: '🎓',
    unlockedAt: '1天前'
  },
  {
    achievementId: 3,
    name: '学习10小时',
    icon: '⏱️',
    unlockedAt: '3天前'
  },
  {
    achievementId: 4,
    name: '优秀学员',
    icon: '⭐',
    unlockedAt: '5天前'
  }
])

const weeklyChart = ref(null)
let chartInstance = null

const expPercentage = computed(() => {
  return Math.round((userInfo.value.experience / userInfo.value.nextLevelExp) * 100)
})

const getStatusType = (status) => {
  const typeMap = {
    '进行中': 'primary',
    '已完成': 'success',
    '未开始': 'info'
  }
  return typeMap[status] || 'info'
}

const checkin = async () => {
  try {
    // TODO: 调用打卡API
    todayStats.value.checkedIn = true
    todayStats.value.streakDays++
    ElMessage.success(`打卡成功！连续学习 ${todayStats.value.streakDays} 天 🔥`)
  } catch (error) {
    ElMessage.error('打卡失败')
  }
}

const continueCourse = () => {
  router.push(`/student/course/${continueLearning.value.courseId}/study`)
}

const goToCourse = (courseId) => {
  router.push(`/student/course/${courseId}/study`)
}

const goToMyCourses = () => {
  router.push('/student/my-courses')
}

const goToLearningPlan = () => {
  router.push('/student/learning-plan')
}

const goToAchievements = () => {
  router.push('/student/achievements')
}

const initWeeklyChart = () => {
  if (!weeklyChart.value) return

  chartInstance = echarts.init(weeklyChart.value)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} 分钟'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      axisLine: {
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    series: [
      {
        data: weeklyStats.value.dailyMinutes,
        type: 'line',
        smooth: true,
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.2)'
        },
        itemStyle: {
          color: '#409eff'
        },
        lineStyle: {
          width: 3
        }
      }
    ],
    grid: {
      left: '10%',
      right: '5%',
      bottom: '10%',
      top: '15%'
    }
  }

  chartInstance.setOption(option)
}

onMounted(async () => {
  await nextTick()
  initWeeklyChart()

  // 响应式处理
  window.addEventListener('resize', () => {
    if (chartInstance) {
      chartInstance.resize()
    }
  })
})
</script>

<style scoped>
.student-dashboard {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 欢迎卡片 */
.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.user-greeting h2 {
  margin: 0 0 16px 0;
  font-size: 24px;
  color: #303133;
}

.today-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-item.streak {
  flex-direction: row;
  align-items: center;
  gap: 8px;
  color: #f56c6c;
  font-weight: 600;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

.quick-actions {
  display: flex;
  gap: 12px;
}

.user-level {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.level-badge {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.level-text {
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.exp-progress {
  width: 150px;
  text-align: center;
}

.exp-text {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  display: block;
}

/* 继续学习卡片 */
.continue-learning-card {
  margin-bottom: 20px;
}

.continue-learning-content {
  display: flex;
  gap: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.continue-learning-content:hover {
  transform: translateY(-4px);
}

.continue-learning-content .course-cover {
  width: 200px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-info h3 {
  margin: 0;
  font-size: 18px;
}

.current-chapter {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

/* 我的课程 */
.my-courses-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.course-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  border-radius: 8px;
  overflow: hidden;
  background: #f5f7fa;
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-card .course-cover {
  width: 100%;
  height: 120px;
  object-fit: cover;
}

.course-details {
  padding: 12px;
}

.course-details h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 成就列表 */
.achievements-card {
  margin-bottom: 20px;
}

.achievements-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.achievement-icon {
  font-size: 32px;
}

.achievement-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.achievement-name {
  font-size: 14px;
  font-weight: 600;
}

.achievement-time {
  font-size: 12px;
  color: #909399;
}

/* 统计卡片 */
.stats-card {
  margin-bottom: 20px;
}

.weekly-chart {
  width: 100%;
  height: 200px;
}

.stats-summary {
  display: flex;
  justify-content: space-around;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.summary-item .label {
  font-size: 12px;
  color: #909399;
}

.summary-item .value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .user-level {
    align-self: center;
  }

  .continue-learning-content {
    flex-direction: column;
  }

  .continue-learning-content .course-cover {
    width: 100%;
  }

  .courses-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .achievements-list {
    grid-template-columns: 1fr;
  }
}
</style>
