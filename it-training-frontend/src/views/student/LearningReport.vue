<template>
  <div class="max-w-7xl mx-auto px-4 md:px-8 py-8 space-y-8">
    <!-- Loading Overlay -->
    <div v-if="loading" class="fixed inset-0 z-50 flex items-center justify-center bg-bg-primary/80 backdrop-blur-sm">
      <div class="flex flex-col items-center gap-3">
        <div class="w-10 h-10 border-4 border-primary/30 border-t-primary rounded-full animate-spin"></div>
        <span class="text-sm text-text-secondary">加载中...</span>
      </div>
    </div>

    <!-- Hero -->
    <section class="relative overflow-hidden rounded-3xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl shadow-sm p-7 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col lg:flex-row lg:items-end justify-between gap-6">
        <div>
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <!-- BarChart3 Icon -->
            <svg class="w-4 h-4 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            学习报告
          </p>
          <h1 class="mt-3 text-3xl md:text-5xl font-semibold tracking-tight text-text-primary">数据驱动成长</h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">可视化你的学习轨迹与关键指标。</p>
        </div>

        <div class="flex flex-col sm:flex-row gap-3">
          <!-- Report Type Selector -->
          <div class="inline-flex rounded-xl bg-bg-tertiary/60 border border-border-color/60 p-1">
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
              :class="reportType === 'weekly' ? 'bg-bg-primary text-text-primary shadow-sm' : 'text-text-secondary hover:text-text-primary'"
              @click="reportType = 'weekly'"
            >
              周报
            </button>
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
              :class="reportType === 'monthly' ? 'bg-bg-primary text-text-primary shadow-sm' : 'text-text-secondary hover:text-text-primary'"
              @click="reportType = 'monthly'"
            >
              月报
            </button>
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
              :class="reportType === 'yearly' ? 'bg-bg-primary text-text-primary shadow-sm' : 'text-text-secondary hover:text-text-primary'"
              @click="reportType = 'yearly'"
            >
              年报
            </button>
          </div>

          <!-- Date Selector -->
          <Select
            v-if="reportType === 'weekly'"
            v-model="selectedWeekValue"
            :options="weekOptions"
            placeholder="选择周"
            class="w-full sm:w-44"
            @update:modelValue="loadReport"
          />
          <Select
            v-else-if="reportType === 'monthly'"
            v-model="selectedMonthValue"
            :options="monthOptions"
            placeholder="选择月份"
            class="w-full sm:w-40"
            @update:modelValue="loadReport"
          />
          <Select
            v-else
            v-model="selectedYearValue"
            :options="yearOptions"
            placeholder="选择年份"
            class="w-full sm:w-32"
            @update:modelValue="loadReport"
          />
        </div>
      </div>
    </section>

    <template v-if="report">
      <!-- KPI -->
      <section class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-6 gap-4">
        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <!-- Clock Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <span v-if="report.studyTimeChangePercent !== null" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60">
              <!-- ArrowUp/ArrowDown Icon -->
              <svg v-if="report.studyTimeChangePercent >= 0" class="w-4 h-4 mr-1 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18" />
              </svg>
              <svg v-else class="w-4 h-4 mr-1 text-error" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
              </svg>
              {{ Math.abs(report.studyTimeChangePercent) }}%
            </span>
          </div>
          <p class="mt-4 text-xs text-text-muted">学习时长</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">
            {{ formatMinutes(report.totalStudyMinutes) }}
          </p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <!-- CalendarDays Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
            </div>
            <span v-if="report.studyDaysChange !== null" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60">
              <svg v-if="report.studyDaysChange >= 0" class="w-4 h-4 mr-1 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18" />
              </svg>
              <svg v-else class="w-4 h-4 mr-1 text-error" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
              </svg>
              {{ Math.abs(report.studyDaysChange) }}
            </span>
          </div>
          <p class="mt-4 text-xs text-text-muted">学习天数</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.studyDays || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <!-- CheckCircle Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <span v-if="report.completedCoursesChange !== null" class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60">
              <svg v-if="report.completedCoursesChange >= 0" class="w-4 h-4 mr-1 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 10l7-7m0 0l7 7m-7-7v18" />
              </svg>
              <svg v-else class="w-4 h-4 mr-1 text-error" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 14l-7 7m0 0l-7-7m7 7V3" />
              </svg>
              {{ Math.abs(report.completedCoursesChange) }}
            </span>
          </div>
          <p class="mt-4 text-xs text-text-muted">完成课程</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.completedCourses || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <!-- Award Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
              </svg>
            </div>
          </div>
          <p class="mt-4 text-xs text-text-muted">获得成就</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.earnedAchievements || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <!-- Flame Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.879 16.121A3 3 0 1012.015 11L11 14H9c0 .768.293 1.536.879 2.121z" />
              </svg>
            </div>
          </div>
          <p class="mt-4 text-xs text-text-muted">连续打卡</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.streakDays || 0 }}</p>
        </div>

        <div class="card p-5">
          <div class="flex items-start justify-between gap-3">
            <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <!-- Gauge Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
          </div>
          <p class="mt-4 text-xs text-text-muted">日均分钟</p>
          <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ report.avgDailyMinutes || 0 }}</p>
        </div>
      </section>

      <!-- Charts -->
      <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card p-6">
          <div class="flex items-end justify-between gap-6">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习趋势</h2>
              <p class="mt-1 text-sm text-text-muted">每日学习分钟与打卡状态</p>
            </div>
          </div>
          <div ref="trendChartRef" class="h-80 w-full mt-4"></div>
        </div>

        <div class="card p-6">
          <div class="flex items-end justify-between gap-6">
            <div>
              <h2 class="text-lg font-semibold tracking-tight text-text-primary">类别分布</h2>
              <p class="mt-1 text-sm text-text-muted">你把时间花在了哪些方向</p>
            </div>
          </div>
          <div ref="categoryChartRef" class="h-80 w-full mt-4"></div>

          <div v-if="report.categoryDistribution?.length" class="mt-4 space-y-2">
            <div
              v-for="(item, index) in report.categoryDistribution"
              :key="item.categoryName"
              class="flex items-center gap-3"
            >
              <span class="w-2 h-2 rounded-full" :style="{ backgroundColor: categorySwatches[index % categorySwatches.length] }"></span>
              <span class="flex-1 font-medium text-sm text-text-primary">{{ item.categoryName }}</span>
              <span class="text-sm text-text-secondary">{{ formatMinutes(item.minutes) }}</span>
              <span class="text-sm font-semibold text-text-primary w-12 text-right">{{ item.percent }}%</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Courses -->
      <section class="card p-6">
        <div class="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-5">
          <div>
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">课程进度</h2>
            <p class="mt-1 text-sm text-text-muted">进行中与已完成课程概览</p>
          </div>
          <div class="inline-flex rounded-xl bg-bg-tertiary/60 border border-border-color/60 p-1">
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
              :class="activeTab === 'inProgress' ? 'bg-bg-primary text-text-primary shadow-sm' : 'text-text-secondary hover:text-text-primary'"
              @click="activeTab = 'inProgress'"
            >
              进行中
            </button>
            <button
              type="button"
              class="px-4 py-2 text-sm font-medium rounded-lg transition-all"
              :class="activeTab === 'completed' ? 'bg-bg-primary text-text-primary shadow-sm' : 'text-text-secondary hover:text-text-primary'"
              @click="activeTab = 'completed'"
            >
              已完成
            </button>
          </div>
        </div>

        <div v-if="activeTab === 'inProgress'">
          <div v-if="report.inProgressCourses?.length" class="space-y-3">
            <div v-for="course in report.inProgressCourses" :key="course.courseId" class="card p-4">
              <div class="flex items-start justify-between gap-6">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ course.courseName }}</p>
                  <p class="mt-1 text-sm text-text-muted truncate">{{ course.category }}</p>
                </div>
                <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60">{{ course.progressPercent }}%</span>
              </div>
              <div class="mt-3">
                <div class="h-2 rounded-full bg-bg-tertiary/70 overflow-hidden">
                  <div class="h-full bg-primary/80 rounded-full transition-all" :style="{ width: course.progressPercent + '%' }"></div>
                </div>
                <p class="mt-2 text-xs text-text-muted">已学习 {{ formatMinutes(course.studyMinutes) }}</p>
              </div>
            </div>
          </div>
          <EmptyState v-else emoji="📚" title="暂无进行中的课程" description="从课程中心选择一门课程开始学习。" />
        </div>

        <div v-else>
          <div v-if="report.completedCourseList?.length" class="space-y-3">
            <div v-for="course in report.completedCourseList" :key="course.courseId" class="card p-4">
              <div class="flex items-start justify-between gap-6">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ course.courseName }}</p>
                  <p class="mt-1 text-xs text-text-muted">完成时间：{{ course.lastStudyDate || '—' }}</p>
                </div>
                <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-success/10 text-success border border-success/20">已完成</span>
              </div>
              <p class="mt-2 text-sm text-text-secondary">学习时长：{{ formatMinutes(course.studyMinutes) }}</p>
            </div>
          </div>
          <EmptyState v-else emoji="✅" title="本期暂无完成的课程" description="持续学习，很快就会看到成果。" />
        </div>
      </section>

      <!-- Achievements & Suggestions -->
      <section class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="card p-6">
          <div class="flex items-center justify-between gap-6">
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">新获得成就</h2>
          </div>
          <div v-if="report.newAchievements?.length" class="mt-4 space-y-3">
            <div v-for="achievement in report.newAchievements" :key="achievement.id" class="card p-4">
              <div class="flex items-start justify-between gap-6">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ achievement.name }}</p>
                  <p class="mt-1 text-sm text-text-secondary">{{ achievement.description }}</p>
                </div>
                <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60">+{{ achievement.points || 0 }}</span>
              </div>
            </div>
          </div>
          <EmptyState v-else emoji="🏆" title="本期暂无新成就" description="继续保持，成就会不断解锁。" />
        </div>

        <div class="card p-6">
          <div class="flex items-center justify-between gap-6">
            <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习建议</h2>
          </div>
          <div v-if="report.suggestions?.length" class="mt-4 space-y-3">
            <div v-for="(suggestion, index) in report.suggestions" :key="index" class="rounded-2xl border border-border-color/60 bg-bg-secondary/60 p-4">
              <div class="flex items-start gap-3">
                <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary flex-shrink-0">
                  <!-- Lightbulb Icon -->
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
                  </svg>
                </div>
                <p class="text-sm text-text-secondary leading-relaxed">{{ suggestion }}</p>
              </div>
            </div>
          </div>
          <EmptyState v-else emoji="💡" title="暂无建议" description="当前学习节奏良好，继续保持。" />
        </div>
      </section>
    </template>

    <EmptyState
      v-else-if="!loading"
      emoji="📊"
      title="暂无报告数据"
      description="选择时间范围后会生成学习报告。"
    />

    <!-- Toast -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="toast.visible"
          class="fixed top-20 left-1/2 -translate-x-1/2 z-50 px-4 py-2 rounded-xl text-sm font-medium shadow-lg"
          :class="toastClass"
        >
          {{ toast.message }}
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch, onUnmounted } from 'vue';
import { Select, EmptyState } from '@/design-system';
import { getWeeklyReport, getMonthlyReport, getYearlyReport } from '@/api/learning';
import * as echarts from 'echarts';

// Types
interface Report {
  totalStudyMinutes?: number;
  studyTimeChangePercent?: number | null;
  studyDays?: number;
  studyDaysChange?: number | null;
  completedCourses?: number;
  completedCoursesChange?: number | null;
  earnedAchievements?: number;
  streakDays?: number;
  avgDailyMinutes?: number;
  dailyStudyTrend?: Array<{
    date: string;
    minutes: number;
    checkedIn: boolean;
  }>;
  categoryDistribution?: Array<{
    categoryName: string;
    minutes: number;
    percent: number;
  }>;
  inProgressCourses?: Array<{
    courseId: number;
    courseName: string;
    category: string;
    progressPercent: number;
    studyMinutes: number;
  }>;
  completedCourseList?: Array<{
    courseId: number;
    courseName: string;
    lastStudyDate: string;
    studyMinutes: number;
  }>;
  newAchievements?: Array<{
    id: number;
    name: string;
    description: string;
    points: number;
  }>;
  suggestions?: string[];
}

interface SelectOption {
  label: string;
  value: string;
}

// State
const loading = ref(false);
const reportType = ref<'weekly' | 'monthly' | 'yearly'>('weekly');
const report = ref<Report | null>(null);
const activeTab = ref<'inProgress' | 'completed'>('inProgress');

// Date selection values
const selectedWeekValue = ref('');
const selectedMonthValue = ref('');
const selectedYearValue = ref('');

// Chart refs
const trendChartRef = ref<HTMLElement | null>(null);
const categoryChartRef = ref<HTMLElement | null>(null);
let trendChart: echarts.ECharts | null = null;
let categoryChart: echarts.ECharts | null = null;
let themeObserver: MutationObserver | null = null;

// Toast
const toast = ref({ visible: false, message: '', type: 'success' as 'success' | 'warning' | 'error' | 'info' });
const toastClass = computed(() => {
  const classes: Record<string, string> = {
    success: 'bg-success text-white',
    warning: 'bg-warning text-white',
    error: 'bg-error text-white',
    info: 'bg-info text-white',
  };
  return classes[toast.value.type] || classes.success;
});

const showToast = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'success') => {
  toast.value = { visible: true, message, type };
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

// Generate date options
const weekOptions = computed<SelectOption[]>(() => {
  const options: SelectOption[] = [];
  const now = new Date();
  for (let i = 0; i < 12; i++) {
    const d = new Date(now);
    d.setDate(d.getDate() - i * 7);
    const monday = getMonday(d);
    const weekNum = getWeekNumber(new Date(monday));
    options.push({
      label: `${new Date(monday).getFullYear()} 第 ${weekNum} 周`,
      value: monday,
    });
  }
  return options;
});

const monthOptions = computed<SelectOption[]>(() => {
  const options: SelectOption[] = [];
  const now = new Date();
  for (let i = 0; i < 12; i++) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
    const value = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`;
    options.push({
      label: `${d.getFullYear()}年${d.getMonth() + 1}月`,
      value,
    });
  }
  return options;
});

const yearOptions = computed<SelectOption[]>(() => {
  const options: SelectOption[] = [];
  const currentYear = new Date().getFullYear();
  for (let i = 0; i < 5; i++) {
    const year = currentYear - i;
    options.push({
      label: `${year}年`,
      value: String(year),
    });
  }
  return options;
});

// Theme colors
const categorySwatches = ref<string[]>([]);

const normalizeRgb = (value: string, fallback: string): string => {
  const cleaned = (value || '').trim();
  if (!cleaned) return fallback;
  return cleaned.replace(/\s+/g, ' ');
};

const rgba = (rgb: string, alpha: number): string => `rgba(${rgb.replace(/\s+/g, ',')}, ${alpha})`;

const getThemeColors = () => {
  const style = getComputedStyle(document.documentElement);
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '37 99 235');
  const primaryLightRgb = normalizeRgb(style.getPropertyValue('--primary-light-rgb'), '59 130 246');
  const infoRgb = normalizeRgb(style.getPropertyValue('--info-color-rgb'), primaryRgb);
  const successRgb = normalizeRgb(style.getPropertyValue('--success-color-rgb'), '5 150 105');
  const warningRgb = normalizeRgb(style.getPropertyValue('--warning-color-rgb'), '217 119 6');
  const errorRgb = normalizeRgb(style.getPropertyValue('--error-color-rgb'), '220 38 38');
  const textSecondaryRgb = normalizeRgb(style.getPropertyValue('--text-secondary-rgb'), '75 85 99');
  const borderRgb = normalizeRgb(style.getPropertyValue('--border-color-rgb'), '229 231 235');
  const bgSecondaryRgb = normalizeRgb(style.getPropertyValue('--bg-secondary-rgb'), '255 255 255');
  return {
    primaryRgb,
    primaryLightRgb,
    infoRgb,
    successRgb,
    warningRgb,
    errorRgb,
    textSecondaryRgb,
    borderRgb,
    bgSecondaryRgb,
  };
};

const updateSwatches = () => {
  const { primaryRgb, primaryLightRgb, infoRgb, successRgb, warningRgb, errorRgb } = getThemeColors();
  const palette = [primaryRgb, primaryLightRgb, infoRgb, successRgb, warningRgb, errorRgb];
  categorySwatches.value = palette.map((rgb) => rgba(rgb, 0.9));
};

// Utility functions
const formatMinutes = (minutes?: number): string => {
  if (!minutes) return '0分钟';
  if (minutes < 60) return `${minutes}分钟`;
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return mins > 0 ? `${hours}小时${mins}分钟` : `${hours}小时`;
};

const getMonday = (date: Date): string => {
  const d = new Date(date);
  const day = d.getDay();
  const diff = d.getDate() - day + (day === 0 ? -6 : 1);
  d.setDate(diff);
  return d.toISOString().split('T')[0];
};

const getWeekNumber = (date: Date): number => {
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
  return Math.ceil(((d.getTime() - yearStart.getTime()) / 86400000 + 1) / 7);
};

// Load report data
const loadReport = async () => {
  loading.value = true;
  try {
    let res;
    if (reportType.value === 'weekly') {
      const weekStart = selectedWeekValue.value || getMonday(new Date());
      res = await getWeeklyReport(weekStart);
    } else if (reportType.value === 'monthly') {
      let year: number, month: number;
      if (selectedMonthValue.value) {
        const [y, m] = selectedMonthValue.value.split('-');
        year = parseInt(y);
        month = parseInt(m);
      } else {
        const now = new Date();
        year = now.getFullYear();
        month = now.getMonth() + 1;
      }
      res = await getMonthlyReport(year, month);
    } else {
      const year = selectedYearValue.value ? parseInt(selectedYearValue.value) : new Date().getFullYear();
      res = await getYearlyReport(year);
    }

    report.value = res.data;
    updateSwatches();

    await nextTick();
    renderTrendChart();
    renderCategoryChart();
  } catch (error) {
    console.error('加载报告失败:', error);
    showToast('加载报告失败', 'error');
  } finally {
    loading.value = false;
  }
};

// Chart rendering
const renderTrendChart = () => {
  if (!trendChartRef.value || !report.value?.dailyStudyTrend) return;

  trendChart?.dispose();
  trendChart = echarts.init(trendChartRef.value);

  const { primaryRgb, primaryLightRgb, textSecondaryRgb, borderRgb } = getThemeColors();
  const dates = report.value.dailyStudyTrend.map((item) => item.date);
  const minutes = report.value.dailyStudyTrend.map((item) => item.minutes);
  const checkedIn = report.value.dailyStudyTrend.map((item) => item.checkedIn);

  trendChart.setOption({
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      formatter: (params: any) => {
        const data = params[0];
        const isCheckedIn = checkedIn[data.dataIndex];
        return `${data.name}<br/>学习时长：${data.value} 分钟<br/>打卡：${isCheckedIn ? '已打卡' : '未打卡'}`;
      },
    },
    grid: { left: 24, right: 12, top: 18, bottom: 24, containLabel: true },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        color: rgba(textSecondaryRgb, 0.9),
        formatter: (value: string) => {
          const date = new Date(value);
          return `${date.getMonth() + 1}/${date.getDate()}`;
        },
      },
      axisLine: { lineStyle: { color: rgba(borderRgb, 0.8) } },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      minInterval: 1,
      axisLabel: { color: rgba(textSecondaryRgb, 0.85) },
      nameTextStyle: { color: rgba(textSecondaryRgb, 0.7) },
      splitLine: { lineStyle: { color: rgba(borderRgb, 0.6) } },
    },
    series: [
      {
        name: '学习时长',
        type: 'bar',
        data: minutes,
        barWidth: 16,
        itemStyle: {
          borderRadius: [10, 10, 6, 6],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: rgba(primaryLightRgb, 0.9) },
            { offset: 1, color: rgba(primaryRgb, 0.18) },
          ]),
        },
      },
    ],
  });
};

const renderCategoryChart = () => {
  if (!categoryChartRef.value || !report.value?.categoryDistribution) return;

  categoryChart?.dispose();
  categoryChart = echarts.init(categoryChartRef.value);

  const { bgSecondaryRgb } = getThemeColors();
  const data = report.value.categoryDistribution.map((item, index) => ({
    name: item.categoryName,
    value: item.minutes,
    itemStyle: { color: categorySwatches.value[index % categorySwatches.value.length] },
  }));

  categoryChart.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'item', formatter: '{b}: {c}分钟 ({d}%)' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: rgba(bgSecondaryRgb, 0.9),
          borderWidth: 2,
        },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data,
      },
    ],
  });
};

const handleResize = () => {
  trendChart?.resize();
  categoryChart?.resize();
};

// Watch report type changes
watch(reportType, () => {
  loadReport();
});

// Initialize default values
const initDefaultValues = () => {
  const now = new Date();
  selectedWeekValue.value = getMonday(now);
  selectedMonthValue.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
  selectedYearValue.value = String(now.getFullYear());
};

onMounted(() => {
  initDefaultValues();
  loadReport();
  window.addEventListener('resize', handleResize);
  themeObserver = new MutationObserver(() => {
    nextTick(() => {
      updateSwatches();
      renderTrendChart();
      renderCategoryChart();
    });
  });
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  themeObserver?.disconnect();
  trendChart?.dispose();
  categoryChart?.dispose();
});
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}
</style>
