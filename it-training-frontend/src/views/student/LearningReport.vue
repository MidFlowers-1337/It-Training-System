<template>
  <div class="learning-report-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><DataAnalysis /></el-icon>
          学习报告
        </h1>
        <p>查看您的学习数据分析和进度报告</p>
      </div>
      
      <!-- 报告类型选择 -->
      <div class="report-selector">
        <el-radio-group v-model="reportType" @change="loadReport">
          <el-radio-button value="weekly">周报</el-radio-button>
          <el-radio-button value="monthly">月报</el-radio-button>
          <el-radio-button value="yearly">年报</el-radio-button>
        </el-radio-group>
        
        <!-- 日期选择器 -->
        <el-date-picker
          v-if="reportType === 'weekly'"
          v-model="selectedWeek"
          type="week"
          format="YYYY 第 ww 周"
          placeholder="选择周"
          @change="loadReport"
        />
        <el-date-picker
          v-else-if="reportType === 'monthly'"
          v-model="selectedMonth"
          type="month"
          format="YYYY年MM月"
          placeholder="选择月份"
          @change="loadReport"
        />
        <el-date-picker
          v-else
          v-model="selectedYear"
          type="year"
          format="YYYY年"
          placeholder="选择年份"
          @change="loadReport"
        />
      </div>
    </div>

    <div v-loading="loading" class="report-content">
      <template v-if="report">
        <!-- 概览卡片 -->
        <div class="overview-section">
          <div class="overview-card total-time">
            <div class="card-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="card-content">
              <span class="card-value">{{ formatMinutes(report.totalStudyMinutes) }}</span>
              <span class="card-label">学习时长</span>
              <span v-if="report.studyTimeChangePercent !== null" class="card-change" 
                    :class="report.studyTimeChangePercent >= 0 ? 'positive' : 'negative'">
                {{ report.studyTimeChangePercent >= 0 ? '+' : '' }}{{ report.studyTimeChangePercent }}%
              </span>
            </div>
          </div>
          
          <div class="overview-card study-days">
            <div class="card-icon">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="card-content">
              <span class="card-value">{{ report.studyDays }}</span>
              <span class="card-label">学习天数</span>
              <span v-if="report.studyDaysChange !== null" class="card-change"
                    :class="report.studyDaysChange >= 0 ? 'positive' : 'negative'">
                {{ report.studyDaysChange >= 0 ? '+' : '' }}{{ report.studyDaysChange }}天
              </span>
            </div>
          </div>
          
          <div class="overview-card completed">
            <div class="card-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="card-content">
              <span class="card-value">{{ report.completedCourses }}</span>
              <span class="card-label">完成课程</span>
              <span v-if="report.completedCoursesChange !== null" class="card-change"
                    :class="report.completedCoursesChange >= 0 ? 'positive' : 'negative'">
                {{ report.completedCoursesChange >= 0 ? '+' : '' }}{{ report.completedCoursesChange }}门
              </span>
            </div>
          </div>
          
          <div class="overview-card achievements">
            <div class="card-icon">
              <el-icon><Trophy /></el-icon>
            </div>
            <div class="card-content">
              <span class="card-value">{{ report.earnedAchievements }}</span>
              <span class="card-label">获得成就</span>
            </div>
          </div>
          
          <div class="overview-card streak">
            <div class="card-icon">
              <el-icon><Flame /></el-icon>
            </div>
            <div class="card-content">
              <span class="card-value">{{ report.streakDays }}</span>
              <span class="card-label">连续打卡</span>
            </div>
          </div>
          
          <div class="overview-card avg-daily">
            <div class="card-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="card-content">
              <span class="card-value">{{ report.avgDailyMinutes }}分钟</span>
              <span class="card-label">日均学习</span>
            </div>
          </div>
        </div>

        <!-- 学习趋势图表 -->
        <div class="chart-section">
          <div class="section-title">
            <el-icon><TrendCharts /></el-icon>
            <span>学习趋势</span>
          </div>
          <div class="chart-container" ref="trendChartRef"></div>
        </div>

        <!-- 类别分布 -->
        <div class="distribution-section">
          <div class="section-title">
            <el-icon><PieChart /></el-icon>
            <span>学习分布</span>
          </div>
          <div class="distribution-content">
            <div class="chart-container" ref="categoryChartRef"></div>
            <div class="category-list">
              <div 
                v-for="(item, index) in report.categoryDistribution" 
                :key="item.category"
                class="category-item"
              >
                <div class="category-color" :style="{ background: categoryColors[index % categoryColors.length] }"></div>
                <span class="category-name">{{ item.categoryName }}</span>
                <span class="category-time">{{ formatMinutes(item.minutes) }}</span>
                <span class="category-percent">{{ item.percent }}%</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 课程进度 -->
        <div class="courses-section">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="进行中" name="inProgress">
              <div v-if="report.inProgressCourses && report.inProgressCourses.length > 0" class="course-list">
                <div v-for="course in report.inProgressCourses" :key="course.courseId" class="course-card">
                  <div class="course-info">
                    <h4>{{ course.courseName }}</h4>
                    <el-tag size="small" type="info">{{ course.category }}</el-tag>
                  </div>
                  <div class="course-progress">
                    <el-progress :percentage="course.progressPercent" :stroke-width="8" />
                    <span class="study-time">已学习 {{ formatMinutes(course.studyMinutes) }}</span>
                  </div>
                </div>
              </div>
              <el-empty v-else description="暂无进行中的课程" />
            </el-tab-pane>
            
            <el-tab-pane label="已完成" name="completed">
              <div v-if="report.completedCourseList && report.completedCourseList.length > 0" class="course-list">
                <div v-for="course in report.completedCourseList" :key="course.courseId" class="course-card completed">
                  <div class="course-info">
                    <h4>
                      <el-icon class="check-icon"><CircleCheck /></el-icon>
                      {{ course.courseName }}
                    </h4>
                    <el-tag size="small" type="success">已完成</el-tag>
                  </div>
                  <div class="course-meta">
                    <span>学习时长: {{ formatMinutes(course.studyMinutes) }}</span>
                    <span>完成日期: {{ course.lastStudyDate }}</span>
                  </div>
                </div>
              </div>
              <el-empty v-else description="本期暂无完成的课程" />
            </el-tab-pane>
          </el-tabs>
        </div>

        <!-- 成就与建议 -->
        <div class="bottom-section">
          <!-- 新获得的成就 -->
          <div class="achievements-section">
            <div class="section-title">
              <el-icon><Trophy /></el-icon>
              <span>新获得成就</span>
            </div>
            <div v-if="report.newAchievements && report.newAchievements.length > 0" class="achievement-list">
              <div v-for="achievement in report.newAchievements" :key="achievement.id" class="achievement-item">
                <div class="achievement-icon">
                  <img v-if="achievement.iconUrl" :src="achievement.iconUrl" :alt="achievement.name" />
                  <el-icon v-else><Medal /></el-icon>
                </div>
                <div class="achievement-info">
                  <span class="achievement-name">{{ achievement.name }}</span>
                  <span class="achievement-date">{{ achievement.earnedDate }}</span>
                </div>
                <span class="achievement-points">+{{ achievement.points }}分</span>
              </div>
            </div>
            <el-empty v-else description="本期暂无新成就" :image-size="80" />
          </div>

          <!-- 学习建议 -->
          <div class="suggestions-section">
            <div class="section-title">
              <el-icon><Lightbulb /></el-icon>
              <span>学习建议</span>
            </div>
            <div class="suggestion-list">
              <div v-for="(suggestion, index) in report.suggestions" :key="index" class="suggestion-item">
                <el-icon><InfoFilled /></el-icon>
                <span>{{ suggestion }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <el-empty v-else-if="!loading" description="暂无报告数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  DataAnalysis, Clock, Calendar, CircleCheck, Trophy, TrendCharts,
  PieChart, Medal, InfoFilled
} from '@element-plus/icons-vue'
import { getWeeklyReport, getMonthlyReport, getYearlyReport } from '@/api/learning'
import * as echarts from 'echarts'

// 状态
const loading = ref(false)
const reportType = ref('weekly')
const selectedWeek = ref(new Date())
const selectedMonth = ref(new Date())
const selectedYear = ref(new Date())
const report = ref(null)
const activeTab = ref('inProgress')

// 图表引用
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null

// 类别颜色
const categoryColors = [
  '#667eea', '#764ba2', '#f093fb', '#f5576c', 
  '#4facfe', '#00f2fe', '#43e97b', '#38f9d7'
]

// 自定义图标组件
const Flame = {
  template: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 23c-3.866 0-7-3.134-7-7 0-2.5 1.5-4.5 3-6 .5-.5 1-1 1.5-1.5.5.5 1 1 1.5 1.5 1.5 1.5 3 3.5 3 6 0 3.866-3.134 7-7 7zm0-12c-1 1-2 2-2.5 3-.5 1-.5 2-.5 3 0 2.761 2.239 5 5 5s5-2.239 5-5c0-1-.5-2-.5-3-.5-1-1.5-2-2.5-3-1.5-1.5-3-3-4-4.5-1 1.5-2.5 3-4 4.5z"/></svg>`
}

const Lightbulb = {
  template: `<svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C8.14 2 5 5.14 5 9c0 2.38 1.19 4.47 3 5.74V17c0 .55.45 1 1 1h6c.55 0 1-.45 1-1v-2.26c1.81-1.27 3-3.36 3-5.74 0-3.86-3.14-7-7-7zm2 15H10v-1h4v1zm0-2H10v-1h4v1zm1.31-3.26L14 12.67V15h-4v-2.33l-1.31-.93C7.63 10.97 7 9.54 7 9c0-2.76 2.24-5 5-5s5 2.24 5 5c0 .54-.63 1.97-1.69 2.74z"/></svg>`
}

// 格式化分钟
const formatMinutes = (minutes) => {
  if (!minutes) return '0分钟'
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`
}

// 获取周一日期
const getMonday = (date) => {
  const d = new Date(date)
  const day = d.getDay()
  const diff = d.getDate() - day + (day === 0 ? -6 : 1)
  d.setDate(diff)
  return d.toISOString().split('T')[0]
}

// 加载报告
const loadReport = async () => {
  loading.value = true
  try {
    let res
    if (reportType.value === 'weekly') {
      const weekStart = getMonday(selectedWeek.value)
      res = await getWeeklyReport(weekStart)
    } else if (reportType.value === 'monthly') {
      const year = selectedMonth.value.getFullYear()
      const month = selectedMonth.value.getMonth() + 1
      res = await getMonthlyReport(year, month)
    } else {
      const year = selectedYear.value.getFullYear()
      res = await getYearlyReport(year)
    }
    
    report.value = res.data
    
    // 渲染图表
    await nextTick()
    renderTrendChart()
    renderCategoryChart()
  } catch (error) {
    console.error('加载报告失败:', error)
    ElMessage.error('加载报告失败')
  } finally {
    loading.value = false
  }
}

// 渲染趋势图表
const renderTrendChart = () => {
  if (!trendChartRef.value || !report.value?.dailyStudyTrend) return
  
  if (trendChart) {
    trendChart.dispose()
  }
  
  trendChart = echarts.init(trendChartRef.value)
  
  const dates = report.value.dailyStudyTrend.map(item => item.date)
  const minutes = report.value.dailyStudyTrend.map(item => item.minutes)
  const checkedIn = report.value.dailyStudyTrend.map(item => item.checkedIn)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const data = params[0]
        const isCheckedIn = checkedIn[data.dataIndex]
        return `${data.name}<br/>学习时长: ${data.value}分钟<br/>打卡: ${isCheckedIn ? '✓' : '✗'}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        formatter: (value) => {
          const date = new Date(value)
          return `${date.getMonth() + 1}/${date.getDate()}`
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      minInterval: 1
    },
    series: [
      {
        name: '学习时长',
        type: 'bar',
        data: minutes,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 渲染类别分布图表
const renderCategoryChart = () => {
  if (!categoryChartRef.value || !report.value?.categoryDistribution) return
  
  if (categoryChart) {
    categoryChart.dispose()
  }
  
  categoryChart = echarts.init(categoryChartRef.value)
  
  const data = report.value.categoryDistribution.map((item, index) => ({
    name: item.categoryName,
    value: item.minutes,
    itemStyle: { color: categoryColors[index % categoryColors.length] }
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}分钟 ({d}%)'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: data
      }
    ]
  }
  
  categoryChart.setOption(option)
}

// 监听窗口大小变化
const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
}

onMounted(() => {
  loadReport()
  window.addEventListener('resize', handleResize)
})

// 清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.learning-report-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-content h1 {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}

.header-content p {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.report-selector {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 概览卡片 */
.overview-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 32px;
}

.overview-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--el-box-shadow-light);
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.total-time .card-icon { background: linear-gradient(135deg, #667eea, #764ba2); color: white; }
.study-days .card-icon { background: linear-gradient(135deg, #f093fb, #f5576c); color: white; }
.completed .card-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); color: white; }
.achievements .card-icon { background: linear-gradient(135deg, #fa709a, #fee140); color: white; }
.streak .card-icon { background: linear-gradient(135deg, #f5576c, #f093fb); color: white; }
.avg-daily .card-icon { background: linear-gradient(135deg, #43e97b, #38f9d7); color: white; }

.card-content {
  display: flex;
  flex-direction: column;
}

.card-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.card-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.card-change {
  font-size: 12px;
  font-weight: 500;
}

.card-change.positive { color: var(--el-color-success); }
.card-change.negative { color: var(--el-color-danger); }

/* 图表区域 */
.chart-section,
.distribution-section {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

/* 分布区域 */
.distribution-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  justify-content: center;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-color {
  width: 12px;
  height: 12px;
  border-radius: 3px;
}

.category-name {
  flex: 1;
  font-size: 14px;
}

.category-time {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.category-percent {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  min-width: 40px;
  text-align: right;
}

/* 课程区域 */
.courses-section {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-card {
  background: var(--el-fill-color-light);
  border-radius: 8px;
  padding: 16px;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.course-info h4 {
  flex: 1;
  margin: 0;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.check-icon {
  color: var(--el-color-success);
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-progress .el-progress {
  flex: 1;
}

.study-time {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  white-space: nowrap;
}

.course-meta {
  display: flex;
  gap: 24px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

/* 底部区域 */
.bottom-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.achievements-section,
.suggestions-section {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 24px;
  box-shadow: var(--el-box-shadow-light);
}

.achievement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
}

.achievement-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #fa709a, #fee140);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.achievement-icon img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.achievement-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.achievement-name {
  font-weight: 600;
  font-size: 14px;
}

.achievement-date {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.achievement-points {
  font-weight: 600;
  color: var(--el-color-warning);
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: var(--el-fill-color-light);
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.suggestion-item .el-icon {
  color: var(--el-color-primary);
  margin-top: 2px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
  }
  
  .report-selector {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .distribution-content {
    grid-template-columns: 1fr;
  }
  
  .bottom-section {
    grid-template-columns: 1fr;
  }
}
</style>