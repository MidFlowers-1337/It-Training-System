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
        <CheckinCard
          :is-checked-in="dashboard.todayCheckedIn || false"
          :study-minutes="todayStudyMinutes"
          :checkin-data="todayCheckin"
          @success="handleCheckinSuccess"
        />

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
        <WeeklyChart :weekly-data="dashboard.weeklyStudyData" />

        <!-- Calendar -->
        <CheckinCalendar
          :year="currentYear"
          :month="currentMonth"
          :checkin-dates="checkinDates"
          @prev="prevMonth"
          @next="nextMonth"
        />
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Button, EmptyState } from '@/design-system';
import { getDashboard, getTodayCheckin, getMonthlyCheckinDates } from '@/api/learning';
import { CheckinCard, WeeklyChart, CheckinCalendar } from './learning-center';

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
const todayStudyMinutes = ref(0);

const currentYear = ref(new Date().getFullYear());
const currentMonth = ref(new Date().getMonth() + 1);

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

const handleCheckinSuccess = async () => {
  await loadDashboard();
  await loadTodayCheckin();
  await loadMonthlyCheckins();
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
</style>
