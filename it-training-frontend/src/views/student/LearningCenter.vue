<template>
  <div class="learning-center">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>学习中心</h1>
      <p class="subtitle">追踪你的学习进度，保持学习动力</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card primary">
        <div class="stat-icon">
          <i class="icon-clock"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.totalStudyFormatted || '0分钟' }}</div>
          <div class="stat-label">累计学习时长</div>
        </div>
      </div>
      
      <div class="stat-card success">
        <div class="stat-icon">
          <i class="icon-book"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.totalCoursesCompleted || 0 }}/{{ dashboard.totalCoursesEnrolled || 0 }}</div>
          <div class="stat-label">完成/报名课程</div>
        </div>
      </div>
      
      <div class="stat-card warning">
        <div class="stat-icon">
          <i class="icon-fire"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.currentStreakDays || 0 }}天</div>
          <div class="stat-label">连续学习</div>
        </div>
      </div>
      
      <div class="stat-card info">
        <div class="stat-icon">
          <i class="icon-trophy"></i>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ dashboard.totalAchievementPoints || 0 }}</div>
          <div class="stat-label">成就积分</div>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <!-- 左侧：学习进度 -->
      <div class="content-left">
        <!-- 今日打卡 -->
        <div class="card checkin-card">
          <div class="card-header">
            <h3>📅 今日打卡</h3>
            <span v-if="dashboard.todayCheckedIn" class="badge success">已打卡</span>
            <span v-else class="badge warning">未打卡</span>
          </div>
          <div class="card-body">
            <div v-if="!dashboard.todayCheckedIn" class="checkin-form">
              <div class="form-group">
                <label>学习时长（分钟）</label>
                <input type="number" v-model.number="checkinForm.studyMinutes" min="1" placeholder="输入学习时长">
              </div>
              <div class="form-group">
                <label>学习笔记（可选）</label>
                <textarea v-model="checkinForm.studyContent" placeholder="记录今天学到了什么..."></textarea>
              </div>
              <button class="btn btn-primary btn-block" @click="handleCheckin" :disabled="checkinLoading">
                {{ checkinLoading ? '打卡中...' : '立即打卡' }}
              </button>
            </div>
            <div v-else class="checkin-done">
              <div class="checkin-icon">✅</div>
              <p>今日已打卡，学习了 <strong>{{ todayCheckin?.studyMinutes || 0 }}</strong> 分钟</p>
              <p class="checkin-note" v-if="todayCheckin?.studyContent">{{ todayCheckin.studyContent }}</p>
            </div>
          </div>
        </div>

        <!-- 进行中的课程 -->
        <div class="card">
          <div class="card-header">
            <h3>📚 进行中的课程</h3>
            <router-link to="/student/my-courses" class="link">查看全部</router-link>
          </div>
          <div class="card-body">
            <div v-if="dashboard.inProgressCourses?.length" class="course-list">
              <div v-for="course in dashboard.inProgressCourses" :key="course.id" class="course-item">
                <div class="course-info">
                  <h4>{{ course.courseName }}</h4>
                  <div class="course-meta">
                    <span class="category">{{ course.courseCategory }}</span>
                    <span class="duration">已学 {{ course.studyDurationFormatted }}</span>
                  </div>
                </div>
                <div class="course-progress">
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{ width: course.progressPercent + '%' }"></div>
                  </div>
                  <span class="progress-text">{{ course.progressPercent }}%</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <p>暂无进行中的课程</p>
              <router-link to="/student/courses" class="btn btn-outline">去选课</router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：成就和日历 -->
      <div class="content-right">
        <!-- 本周学习 -->
        <div class="card">
          <div class="card-header">
            <h3>📊 本周学习</h3>
          </div>
          <div class="card-body">
            <div class="weekly-chart">
              <div v-for="day in dashboard.weeklyStudyData" :key="day.date" class="day-bar">
                <div class="bar-container">
                  <div class="bar" :style="{ height: getBarHeight(day.studyMinutes) + '%' }"></div>
                </div>
                <span class="day-label">{{ day.dayOfWeek }}</span>
                <span class="day-value">{{ day.studyMinutes }}分</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 最近成就 -->
        <div class="card">
          <div class="card-header">
            <h3>🏆 最近成就</h3>
            <router-link to="/student/achievements" class="link">查看全部</router-link>
          </div>
          <div class="card-body">
            <div v-if="dashboard.recentAchievements?.length" class="achievement-list">
              <div v-for="achievement in dashboard.recentAchievements" :key="achievement.id" class="achievement-item">
                <div class="achievement-icon">{{ achievement.icon || '🎖️' }}</div>
                <div class="achievement-info">
                  <h4>{{ achievement.name }}</h4>
                  <p>{{ achievement.description }}</p>
                </div>
                <div class="achievement-points">+{{ achievement.points }}</div>
              </div>
            </div>
            <div v-else class="empty-state">
              <p>继续学习，解锁更多成就！</p>
            </div>
          </div>
        </div>

        <!-- 打卡日历 -->
        <div class="card">
          <div class="card-header">
            <h3>📆 打卡日历</h3>
            <div class="calendar-nav">
              <button @click="prevMonth" class="nav-btn">&lt;</button>
              <span>{{ currentYear }}年{{ currentMonth }}月</span>
              <button @click="nextMonth" class="nav-btn">&gt;</button>
            </div>
          </div>
          <div class="card-body">
            <div class="calendar">
              <div class="calendar-header">
                <span v-for="day in weekDays" :key="day">{{ day }}</span>
              </div>
              <div class="calendar-body">
                <div 
                  v-for="(day, index) in calendarDays" 
                  :key="index" 
                  class="calendar-day"
                  :class="{ 
                    'other-month': !day.currentMonth,
                    'checked-in': day.checkedIn,
                    'today': day.isToday
                  }"
                >
                  {{ day.date }}
                </div>
              </div>
            </div>
            <div class="calendar-legend">
              <span class="legend-item"><span class="dot checked"></span> 已打卡</span>
              <span class="legend-item"><span class="dot today"></span> 今天</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { getDashboard, checkin, getTodayCheckin, getMonthlyCheckinDates } from '@/api/learning'
import { ElMessage } from 'element-plus'

export default {
  name: 'LearningCenter',
  setup() {
    const dashboard = ref({})
    const todayCheckin = ref(null)
    const checkinDates = ref([])
    const checkinLoading = ref(false)
    
    const currentYear = ref(new Date().getFullYear())
    const currentMonth = ref(new Date().getMonth() + 1)
    
    const checkinForm = reactive({
      studyMinutes: 30,
      studyContent: ''
    })
    
    const weekDays = ['日', '一', '二', '三', '四', '五', '六']
    
    // 计算日历天数
    const calendarDays = computed(() => {
      const year = currentYear.value
      const month = currentMonth.value
      const firstDay = new Date(year, month - 1, 1)
      const lastDay = new Date(year, month, 0)
      const daysInMonth = lastDay.getDate()
      const startWeekDay = firstDay.getDay()
      
      const days = []
      const today = new Date()
      const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
      
      // 上月的天数
      const prevMonthLastDay = new Date(year, month - 1, 0).getDate()
      for (let i = startWeekDay - 1; i >= 0; i--) {
        days.push({
          date: prevMonthLastDay - i,
          currentMonth: false,
          checkedIn: false,
          isToday: false
        })
      }
      
      // 本月的天数
      for (let i = 1; i <= daysInMonth; i++) {
        const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`
        days.push({
          date: i,
          currentMonth: true,
          checkedIn: checkinDates.value.includes(dateStr),
          isToday: dateStr === todayStr
        })
      }
      
      // 下月的天数（补齐到42天）
      const remaining = 42 - days.length
      for (let i = 1; i <= remaining; i++) {
        days.push({
          date: i,
          currentMonth: false,
          checkedIn: false,
          isToday: false
        })
      }
      
      return days
    })
    
    // 获取柱状图高度
    const getBarHeight = (minutes) => {
      const maxMinutes = Math.max(...(dashboard.value.weeklyStudyData?.map(d => d.studyMinutes) || [60]))
      return maxMinutes > 0 ? (minutes / maxMinutes) * 100 : 0
    }
    
    // 加载仪表盘数据
    const loadDashboard = async () => {
      try {
        const res = await getDashboard()
        if (res.code === 200) {
          dashboard.value = res.data
        }
      } catch (error) {
        console.error('加载仪表盘失败:', error)
      }
    }
    
    // 加载今日打卡
    const loadTodayCheckin = async () => {
      try {
        const res = await getTodayCheckin()
        if (res.code === 200) {
          todayCheckin.value = res.data
        }
      } catch (error) {
        console.error('加载今日打卡失败:', error)
      }
    }
    
    // 加载月度打卡日期
    const loadMonthlyCheckins = async () => {
      try {
        const res = await getMonthlyCheckinDates(currentYear.value, currentMonth.value)
        if (res.code === 200) {
          checkinDates.value = res.data || []
        }
      } catch (error) {
        console.error('加载打卡日历失败:', error)
      }
    }
    
    // 打卡
    const handleCheckin = async () => {
      if (!checkinForm.studyMinutes || checkinForm.studyMinutes < 1) {
        ElMessage.warning('请输入有效的学习时长')
        return
      }
      
      checkinLoading.value = true
      try {
        const res = await checkin(checkinForm)
        if (res.code === 200) {
          ElMessage.success('打卡成功！')
          if (res.data.newAchievementEarned) {
            ElMessage.success(`🎉 恭喜获得成就：${res.data.newAchievement.name}`)
          }
          await loadDashboard()
          await loadTodayCheckin()
          await loadMonthlyCheckins()
        }
      } catch (error) {
        ElMessage.error('打卡失败，请重试')
      } finally {
        checkinLoading.value = false
      }
    }
    
    // 上一月
    const prevMonth = () => {
      if (currentMonth.value === 1) {
        currentMonth.value = 12
        currentYear.value--
      } else {
        currentMonth.value--
      }
      loadMonthlyCheckins()
    }
    
    // 下一月
    const nextMonth = () => {
      if (currentMonth.value === 12) {
        currentMonth.value = 1
        currentYear.value++
      } else {
        currentMonth.value++
      }
      loadMonthlyCheckins()
    }
    
    onMounted(() => {
      loadDashboard()
      loadTodayCheckin()
      loadMonthlyCheckins()
    })
    
    return {
      dashboard,
      todayCheckin,
      checkinForm,
      checkinLoading,
      currentYear,
      currentMonth,
      weekDays,
      calendarDays,
      getBarHeight,
      handleCheckin,
      prevMonth,
      nextMonth
    }
  }
}
</script>

<style scoped>
.learning-center {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.page-header .subtitle {
  color: var(--text-secondary);
  margin: 0;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-sm);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 主要内容区 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
}

.card {
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 20px;
}

.card-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-header .link {
  font-size: 14px;
  color: var(--primary-color);
  text-decoration: none;
}

.card-body {
  padding: 20px;
}

/* 打卡卡片 */
.checkin-card .badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.badge.success {
  background: #d4edda;
  color: #155724;
}

.badge.warning {
  background: #fff3cd;
  color: #856404;
}

.checkin-form .form-group {
  margin-bottom: 16px;
}

.checkin-form label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

.checkin-form input,
.checkin-form textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.checkin-form textarea {
  min-height: 80px;
  resize: vertical;
}

.btn-block {
  width: 100%;
}

.checkin-done {
  text-align: center;
  padding: 20px;
}

.checkin-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.checkin-note {
  margin-top: 12px;
  padding: 12px;
  background: var(--bg-secondary);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
}

/* 课程列表 */
.course-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: 12px;
}

.course-info h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.course-meta {
  display: flex;
  gap: 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.course-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 150px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: var(--bg-primary);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), #667eea);
  border-radius: 4px;
  transition: width 0.3s;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-color);
  min-width: 40px;
}

/* 本周学习图表 */
.weekly-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 150px;
  padding-top: 20px;
}

.day-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.bar-container {
  width: 24px;
  height: 100px;
  background: var(--bg-secondary);
  border-radius: 12px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
}

.bar {
  width: 100%;
  background: linear-gradient(180deg, var(--primary-color), #667eea);
  border-radius: 12px;
  transition: height 0.3s;
}

.day-label {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.day-value {
  font-size: 11px;
  color: var(--text-muted);
}

/* 成就列表 */
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
  background: var(--bg-secondary);
  border-radius: 10px;
}

.achievement-icon {
  font-size: 28px;
}

.achievement-info {
  flex: 1;
}

.achievement-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.achievement-info p {
  margin: 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.achievement-points {
  font-size: 14px;
  font-weight: 700;
  color: #f5a623;
}

/* 日历 */
.calendar-nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: var(--bg-secondary);
  border-radius: 6px;
  cursor: pointer;
  color: var(--text-primary);
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  margin-bottom: 8px;
}

.calendar-header span {
  font-size: 12px;
  color: var(--text-secondary);
  padding: 8px 0;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  border-radius: 8px;
  color: var(--text-primary);
}

.calendar-day.other-month {
  color: var(--text-muted);
}

.calendar-day.checked-in {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
  font-weight: 600;
}

.calendar-day.today {
  border: 2px solid var(--primary-color);
}

.calendar-legend {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot.checked {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.dot.today {
  border: 2px solid var(--primary-color);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 24px;
  color: var(--text-secondary);
}

.empty-state p {
  margin-bottom: 12px;
}

/* 按钮 */
.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color), #667eea);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-outline {
  background: transparent;
  border: 1px solid var(--primary-color);
  color: var(--primary-color);
}

.btn-outline:hover {
  background: var(--primary-color);
  color: white;
}

/* 响应式 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .content-right {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .content-right {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>