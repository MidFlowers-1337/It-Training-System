<template>
  <div class="learning-center-page">
    <!-- Header Section -->
    <header class="page-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">学习中心</h1>
          <p class="page-subtitle">追踪学习进度，保持学习动力。</p>
        </div>

        <div class="month-indicator">
          <svg class="month-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
            <line x1="16" y1="2" x2="16" y2="6" />
            <line x1="8" y1="2" x2="8" y2="6" />
            <line x1="3" y1="10" x2="21" y2="10" />
          </svg>
          {{ monthLabel }}
        </div>
      </div>

      <!-- Stats Grid -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon stat-icon-primary">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <polyline points="12 6 12 12 16 14" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">累计学习时长</span>
            <span class="stat-value">{{ dashboard.totalStudyFormatted || '0分钟' }}</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stat-icon-info">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 14l9-5-9-5-9 5 9 5z" />
              <path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">完成 / 报名</span>
            <span class="stat-value tabular-nums">
              {{ dashboard.totalCoursesCompleted || 0 }}/{{ dashboard.totalCoursesEnrolled || 0 }}
            </span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stat-icon-warning">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
              <path d="M9.879 16.121A3 3 0 1012.015 11L11 14H9c0 .768.293 1.536.879 2.121z" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">连续学习</span>
            <span class="stat-value tabular-nums">{{ dashboard.currentStreakDays || 0 }} 天</span>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon stat-icon-success">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
            </svg>
          </div>
          <div class="stat-info">
            <span class="stat-label">成就积分</span>
            <span class="stat-value tabular-nums">{{ dashboard.totalAchievementPoints || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- Background Decoration -->
      <div class="header-bg">
        <div class="bg-orb bg-orb-1"></div>
        <div class="bg-orb bg-orb-2"></div>
      </div>
    </header>

    <!-- Main Content -->
    <div class="content-layout">
      <!-- Left Column -->
      <main class="main-column">
        <!-- Check-in Card -->
        <section class="section-card">
          <div class="card-header">
            <h2 class="card-title">今日打卡</h2>
            <span
              class="status-badge"
              :class="dashboard.todayCheckedIn ? 'status-success' : 'status-warning'"
            >
              {{ dashboard.todayCheckedIn ? '已打卡' : '未打卡' }}
            </span>
          </div>

          <!-- Not Checked In -->
          <div v-if="!dashboard.todayCheckedIn" class="checkin-form">
            <div class="study-time-box">
              <div class="study-time-row">
                <span class="study-time-label">今日学习时长</span>
                <span class="study-time-value tabular-nums">{{ todayStudyMinutes }} 分钟</span>
              </div>
              <p class="study-time-hint">系统将自动统计你今天的学习时长。</p>
            </div>

            <div class="note-field">
              <label class="note-label">学习笔记（可选）</label>
              <textarea
                v-model="checkinForm.studyContent"
                class="note-textarea"
                placeholder="记录今天学到了什么..."
              ></textarea>
            </div>

            <div class="checkin-actions">
              <Button variant="primary" :disabled="checkinLoading || todayStudyMinutes === 0" @click="handleCheckin">
                {{ checkinLoading ? '打卡中...' : '立即打卡' }}
              </Button>
              <span v-if="todayStudyMinutes === 0" class="checkin-warning">请先学习课程后再打卡</span>
            </div>
          </div>

          <!-- Already Checked In -->
          <div v-else class="checkin-success">
            <div class="success-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="success-info">
              <p class="success-text">
                今日已打卡，学习了
                <span class="success-minutes tabular-nums">{{ todayCheckin?.studyMinutes || 0 }}</span>
                分钟
              </p>
              <p v-if="todayCheckin?.studyContent" class="success-note">{{ todayCheckin.studyContent }}</p>
            </div>
          </div>
        </section>

        <!-- In Progress Courses -->
        <section class="section-card">
          <div class="card-header">
            <div class="card-header-left">
              <h2 class="card-title">进行中的课程</h2>
              <p class="card-subtitle">继续推进你的学习进度。</p>
            </div>
            <router-link to="/my-courses" class="view-all-link">查看全部</router-link>
          </div>

          <div v-if="dashboard.inProgressCourses?.length" class="course-list">
            <button
              v-for="course in dashboard.inProgressCourses"
              :key="course.id"
              type="button"
              class="course-item"
              @click="router.push(`/course/${course.courseId || course.id}/study`)"
            >
              <div class="course-info">
                <span class="course-name">{{ course.courseName }}</span>
                <span class="course-meta">{{ course.courseCategory }} · 已学 {{ course.studyDurationFormatted }}</span>
              </div>
              <span class="course-progress tabular-nums">{{ course.progressPercent }}%</span>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: `${course.progressPercent}%` }"></div>
              </div>
            </button>
          </div>

          <EmptyState
            v-else
            emoji="📚"
            title="暂无进行中的课程"
            description="去课程中心选择一门课程开始学习。"
            action-text="去选课"
            @action="router.push('/courses')"
          />
        </section>
      </main>

      <!-- Right Column -->
      <aside class="side-column">
        <!-- Weekly Study Chart -->
        <section class="section-card">
          <div class="card-header">
            <div class="card-header-left">
              <h2 class="card-title">本周学习</h2>
              <p class="card-subtitle">分钟趋势</p>
            </div>
          </div>

          <div v-if="dashboard.weeklyStudyData?.length" class="weekly-chart">
            <div v-for="(d, i) in dashboard.weeklyStudyData" :key="i" class="chart-bar-wrapper">
              <div class="chart-bar-container">
                <div class="chart-bar" :style="{ height: `${getBarHeight(d.studyMinutes)}%` }"></div>
              </div>
              <span class="chart-label">{{ weekDays[new Date(d.date).getDay()] }}</span>
            </div>
          </div>

          <EmptyState v-else emoji="✨" title="暂无统计" description="开始学习后，这里会展示你的本周趋势。" size="sm" />
        </section>

        <!-- Calendar -->
        <section class="section-card">
          <div class="card-header">
            <h2 class="card-title">打卡日历</h2>
            <div class="calendar-nav">
              <button type="button" class="nav-btn" @click="prevMonth">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              <span class="nav-month tabular-nums">{{ monthLabel }}</span>
              <button type="button" class="nav-btn" @click="nextMonth">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>
          </div>

          <div class="calendar-weekdays">
            <span v-for="d in weekDays" :key="d" class="weekday">{{ d }}</span>
          </div>

          <div class="calendar-grid">
            <div
              v-for="(day, idx) in calendarDays"
              :key="idx"
              class="calendar-day"
              :class="{
                'other-month': !day.currentMonth,
                'checked-in': day.checkedIn,
                'is-today': day.isToday,
              }"
            >
              {{ day.date }}
            </div>
          </div>

          <div class="calendar-legend">
            <span class="legend-item">
              <span class="legend-dot legend-dot-success"></span>
              已打卡
            </span>
            <span class="legend-item">
              <span class="legend-dot legend-dot-today"></span>
              今天
            </span>
          </div>
        </section>
      </aside>
    </div>

    <!-- Toast -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="toast.visible"
          class="toast"
          :class="toast.type"
        >
          {{ toast.message }}
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Button, EmptyState } from '@/design-system';
import { getDashboard, checkin, getTodayCheckin, getMonthlyCheckinDates } from '@/api/learning';

// Types
interface Dashboard {
  totalStudyFormatted?: string;
  totalCoursesCompleted?: number;
  totalCoursesEnrolled?: number;
  currentStreakDays?: number;
  totalAchievementPoints?: number;
  todayCheckedIn?: boolean;
  inProgressCourses?: Array<{
    id: number;
    courseId?: number;
    courseName: string;
    courseCategory: string;
    studyDurationFormatted: string;
    progressPercent: number;
  }>;
  weeklyStudyData?: Array<{
    date: string;
    studyMinutes: number;
  }>;
}

interface TodayCheckin {
  studyMinutes: number;
  studyContent?: string;
}

const router = useRouter();

// State
const dashboard = ref<Dashboard>({});
const todayCheckin = ref<TodayCheckin | null>(null);
const checkinDates = ref<string[]>([]);
const checkinLoading = ref(false);
const todayStudyMinutes = ref(0);

const currentYear = ref(new Date().getFullYear());
const currentMonth = ref(new Date().getMonth() + 1);

const checkinForm = reactive({
  studyContent: '',
});

// Toast
const toast = ref({ visible: false, message: '', type: 'success' as 'success' | 'warning' | 'error' | 'info' });

const showToast = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'success') => {
  toast.value = { visible: true, message, type };
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

const weekDays = ['日', '一', '二', '三', '四', '五', '六'];

const monthLabel = computed(() => `${currentYear.value}-${String(currentMonth.value).padStart(2, '0')}`);

const calendarDays = computed(() => {
  const year = currentYear.value;
  const month = currentMonth.value;
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);
  const daysInMonth = lastDay.getDate();
  const startWeekDay = firstDay.getDay();

  const days: Array<{ date: number; currentMonth: boolean; checkedIn: boolean; isToday: boolean }> = [];
  const today = new Date();
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

  const prevMonthLastDay = new Date(year, month - 1, 0).getDate();
  for (let i = startWeekDay - 1; i >= 0; i--) {
    days.push({ date: prevMonthLastDay - i, currentMonth: false, checkedIn: false, isToday: false });
  }

  for (let i = 1; i <= daysInMonth; i++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
    days.push({
      date: i,
      currentMonth: true,
      checkedIn: checkinDates.value.includes(dateStr),
      isToday: dateStr === todayStr,
    });
  }

  const remaining = 42 - days.length;
  for (let i = 1; i <= remaining; i++) {
    days.push({ date: i, currentMonth: false, checkedIn: false, isToday: false });
  }

  return days;
});

const getBarHeight = (minutes: number): number => {
  const maxMinutes = Math.max(...(dashboard.value.weeklyStudyData?.map((d) => d.studyMinutes) || [60]));
  return maxMinutes > 0 ? (minutes / maxMinutes) * 100 : 0;
};

const calculateTodayStudyMinutes = () => {
  const today = dashboard.value.weeklyStudyData?.find((d) => {
    const date = new Date(d.date);
    const now = new Date();
    return date.toDateString() === now.toDateString();
  });
  todayStudyMinutes.value = today?.studyMinutes || 0;
};

const loadDashboard = async () => {
  try {
    const res = await getDashboard();
    if (res.code === 200) {
      dashboard.value = res.data;
      calculateTodayStudyMinutes();
    }
  } catch (error) {
    console.error('加载仪表盘失败:', error);
  }
};

const loadTodayCheckin = async () => {
  try {
    const res = await getTodayCheckin();
    if (res.code === 200) todayCheckin.value = res.data;
  } catch (error) {
    console.error('加载今日打卡失败:', error);
  }
};

const loadMonthlyCheckins = async () => {
  try {
    const res = await getMonthlyCheckinDates(currentYear.value, currentMonth.value);
    if (res.code === 200) checkinDates.value = res.data || [];
  } catch (error) {
    console.error('加载打卡日历失败:', error);
  }
};

const handleCheckin = async () => {
  if (todayStudyMinutes.value === 0) {
    showToast('请先学习课程后再打卡', 'warning');
    return;
  }

  checkinLoading.value = true;
  try {
    const res = await checkin({ studyMinutes: todayStudyMinutes.value, studyContent: checkinForm.studyContent });
    if (res.code === 200) {
      showToast('打卡成功！', 'success');
      if (res.data?.newAchievementEarned && res.data?.newAchievement?.name) {
        setTimeout(() => {
          showToast(`恭喜获得成就：${res.data.newAchievement.name}`, 'success');
        }, 1500);
      }
      await loadDashboard();
      await loadTodayCheckin();
      await loadMonthlyCheckins();
    }
  } catch (error) {
    showToast('打卡失败，请重试', 'error');
  } finally {
    checkinLoading.value = false;
  }
};

const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12;
    currentYear.value--;
  } else {
    currentMonth.value--;
  }
  loadMonthlyCheckins();
};

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1;
    currentYear.value++;
  } else {
    currentMonth.value++;
  }
  loadMonthlyCheckins();
};

onMounted(() => {
  loadDashboard();
  loadTodayCheckin();
  loadMonthlyCheckins();
});
</script>

<style scoped>
/* ========================================
   Apple 风格学习中心页
   ======================================== */

.learning-center-page {
  min-height: 100vh;
  background: var(--bg-primary);
  padding: 0 var(--page-padding-x, 48px) 80px;
}

/* ===== Page Header ===== */
.page-header {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 48px 0 32px;
  overflow: hidden;
}

.header-content {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.page-title {
  font-size: 34px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.page-subtitle {
  margin-top: 4px;
  font-size: 15px;
  color: var(--text-secondary);
}

.month-indicator {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--bg-tertiary);
  border: 0.5px solid var(--border-color);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.month-icon {
  width: 16px;
  height: 16px;
  color: var(--primary-color);
}

/* Stats Grid */
.stats-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-top: 24px;
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
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 20px;
  background: var(--bg-card);
  border-radius: 16px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}

.stat-icon svg {
  width: 20px;
  height: 20px;
}

.stat-icon-primary {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.1);
  color: var(--primary-color);
}

.stat-icon-info {
  background: rgba(var(--info-rgb, 0, 122, 255) / 0.1);
  color: var(--info);
}

.stat-icon-warning {
  background: rgba(var(--warning-rgb, 255, 149, 0) / 0.1);
  color: var(--warning);
}

.stat-icon-success {
  background: rgba(var(--success-rgb, 52, 199, 89) / 0.1);
  color: var(--success);
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}

.stat-label {
  font-size: 12px;
  color: var(--text-muted);
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

/* Header Background */
.header-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.bg-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
}

.bg-orb-1 {
  top: -50px;
  right: -100px;
  width: 300px;
  height: 300px;
  background: var(--primary-color);
  opacity: 0.08;
}

.bg-orb-2 {
  bottom: -100px;
  left: 20%;
  width: 200px;
  height: 200px;
  background: var(--success);
  opacity: 0.05;
}

/* ===== Content Layout ===== */
.content-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

@media (max-width: 1024px) {
  .content-layout {
    grid-template-columns: 1fr;
  }
}

.main-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.side-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ===== Section Card ===== */
.section-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.card-header-left {
  min-width: 0;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-subtitle {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 2px;
}

.view-all-link {
  font-size: 13px;
  font-weight: 500;
  color: var(--primary-color);
  text-decoration: none;
  transition: opacity 0.2s ease;
}

.view-all-link:hover {
  opacity: 0.8;
}

/* Status Badge */
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 5px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.status-success {
  background: rgba(52, 199, 89, 0.12);
  color: var(--success);
  border: 0.5px solid rgba(52, 199, 89, 0.3);
}

.status-warning {
  background: rgba(255, 149, 0, 0.12);
  color: var(--warning);
  border: 0.5px solid rgba(255, 149, 0, 0.3);
}

/* ===== Check-in Form ===== */
.checkin-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.study-time-box {
  padding: 16px;
  background: var(--bg-tertiary);
  border-radius: 12px;
  border: 0.5px solid var(--border-color);
}

.study-time-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}

.study-time-label {
  color: var(--text-secondary);
}

.study-time-value {
  font-weight: 600;
  color: var(--text-primary);
}

.study-time-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-muted);
}

.note-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.note-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
}

.note-textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px 16px;
  background: var(--bg-tertiary);
  border: 0.5px solid var(--border-color);
  border-radius: 12px;
  font-size: 14px;
  font-family: inherit;
  color: var(--text-primary);
  resize: vertical;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.note-textarea::placeholder {
  color: var(--text-muted);
}

.note-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(var(--primary-color-rgb, 0, 122, 255) / 0.12);
}

.checkin-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.checkin-warning {
  font-size: 12px;
  color: var(--warning);
}

/* Check-in Success */
.checkin-success {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding-top: 4px;
}

.success-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(52, 199, 89, 0.12);
  border-radius: 12px;
  color: var(--success);
  flex-shrink: 0;
}

.success-icon svg {
  width: 20px;
  height: 20px;
}

.success-info {
  min-width: 0;
}

.success-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.success-minutes {
  font-weight: 600;
  color: var(--text-primary);
}

.success-note {
  margin-top: 6px;
  font-size: 13px;
  color: var(--text-muted);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ===== Course List ===== */
.course-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.course-item {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 16px 20px;
  background: var(--bg-tertiary);
  border: 0.5px solid var(--border-color);
  border-radius: 12px;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
}

.course-item:hover {
  background: var(--bg-secondary);
  box-shadow:
    0 2px 4px rgba(0, 0, 0, 0.04),
    0 4px 8px rgba(0, 0, 0, 0.04);
}

.course-info {
  flex: 1;
  min-width: 0;
}

.course-name {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-meta {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

.course-progress {
  font-size: 13px;
  font-weight: 600;
  color: var(--primary-color);
  flex-shrink: 0;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: var(--bg-primary);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light, #5ac8fa));
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* ===== Weekly Chart ===== */
.weekly-chart {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  height: 140px;
  align-items: end;
}

.chart-bar-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  height: 100%;
}

.chart-bar-container {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: flex-end;
  background: var(--bg-tertiary);
  border-radius: 20px;
  overflow: hidden;
}

.chart-bar {
  width: 100%;
  min-height: 4px;
  background: var(--primary-color);
  border-radius: 20px;
  transition: height 0.3s ease;
}

.chart-label {
  font-size: 11px;
  color: var(--text-muted);
}

/* ===== Calendar ===== */
.calendar-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-tertiary);
  border: 0.5px solid var(--border-color);
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.nav-btn:hover {
  background: var(--bg-hover);
}

.nav-btn svg {
  width: 14px;
  height: 14px;
  color: var(--text-secondary);
}

.nav-month {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  min-width: 80px;
  text-align: center;
}

.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  margin-bottom: 8px;
}

.weekday {
  padding: 8px 0;
  font-size: 12px;
  color: var(--text-muted);
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  font-size: 13px;
  color: var(--text-primary);
  transition: background-color 0.15s ease;
}

.calendar-day:hover {
  background: var(--bg-tertiary);
}

.calendar-day.other-month {
  color: var(--text-muted);
  opacity: 0.5;
}

.calendar-day.checked-in {
  background: rgba(52, 199, 89, 0.15);
  color: var(--success);
  font-weight: 600;
}

.calendar-day.is-today {
  box-shadow: inset 0 0 0 2px rgba(var(--primary-color-rgb, 0, 122, 255) / 0.3);
}

.calendar-legend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 16px;
}

.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-muted);
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.legend-dot-success {
  background: var(--success);
}

.legend-dot-today {
  border: 2px solid var(--primary-color);
}

/* ===== Toast ===== */
.toast {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2000;
  padding: 12px 24px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  color: white;
}

.toast.success {
  background: var(--success);
}

.toast.warning {
  background: var(--warning);
}

.toast.error {
  background: var(--error);
}

.toast.info {
  background: var(--info);
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

/* ===== Utility ===== */
.tabular-nums {
  font-variant-numeric: tabular-nums;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .learning-center-page {
    padding: 0 24px 64px;
  }

  .page-header {
    padding: 32px 0 24px;
  }

  .page-title {
    font-size: 28px;
  }

  .section-card {
    padding: 20px;
  }
}

/* ===== Dark Mode ===== */
[data-theme="dark"] .stat-card,
[data-theme="dark"] .section-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .course-item {
  background: var(--bg-tertiary);
  border-color: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .course-item:hover {
  background: var(--bg-hover);
}

[data-theme="dark"] .note-textarea {
  background: var(--bg-tertiary);
  border-color: rgba(255, 255, 255, 0.08);
}

[data-theme="dark"] .nav-btn {
  background: var(--bg-tertiary);
  border-color: rgba(255, 255, 255, 0.08);
}

[data-theme="dark"] .calendar-day.checked-in {
  background: rgba(52, 199, 89, 0.2);
}
</style>
