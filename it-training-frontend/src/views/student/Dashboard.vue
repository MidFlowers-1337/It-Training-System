<template>
  <PageLayout max-width="xl">
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="animate-spin w-8 h-8 border-2 border-primary border-t-transparent rounded-full" />
    </div>

    <div v-else class="space-y-8">
      <!-- Hero Section -->
      <section class="relative overflow-hidden rounded-2xl border border-border-color bg-bg-secondary p-6 md:p-10">
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          <div class="lg:col-span-8">
            <div class="flex items-start gap-4">
              <div class="w-11 h-11 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary flex-shrink-0">
                <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z" />
                </svg>
              </div>

              <div class="min-w-0">
                <h1 class="text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">
                  欢迎回来，{{ userInfo.realName || userInfo.username }}。
                </h1>
                <p class="mt-2 text-text-secondary">继续你的学习旅程，保持节奏。</p>
              </div>
            </div>

            <!-- 今日统计 -->
            <div class="mt-6 flex flex-wrap gap-3">
              <div class="flex items-center gap-3 rounded-xl bg-bg-tertiary border border-border-color px-4 py-3">
                <svg class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                </svg>
                <div class="leading-tight">
                  <div class="text-xs text-text-muted">今日学习</div>
                  <div class="text-sm font-semibold text-text-primary tabular-nums">{{ todayStats.studyMinutes }} 分钟</div>
                </div>
              </div>

              <div class="flex items-center gap-3 rounded-xl bg-bg-tertiary border border-border-color px-4 py-3">
                <svg class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z" />
                </svg>
                <div class="leading-tight">
                  <div class="text-xs text-text-muted">连续打卡</div>
                  <div class="text-sm font-semibold text-text-primary tabular-nums">{{ todayStats.streakDays }} 天</div>
                </div>
              </div>

              <div class="flex items-center gap-3 rounded-xl bg-bg-tertiary border border-border-color px-4 py-3">
                <svg
                  class="w-4 h-4"
                  :class="todayStats.checkedIn ? 'text-success' : 'text-warning'"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M3.85 8.62a4 4 0 0 1 4.78-4.77 4 4 0 0 1 6.74 0 4 4 0 0 1 4.78 4.78 4 4 0 0 1 0 6.74 4 4 0 0 1-4.77 4.78 4 4 0 0 1-6.75 0 4 4 0 0 1-4.78-4.77 4 4 0 0 1 0-6.76Z" />
                  <path d="m9 12 2 2 4-4" />
                </svg>
                <div class="leading-tight">
                  <div class="text-xs text-text-muted">今日状态</div>
                  <div class="text-sm font-semibold" :class="todayStats.checkedIn ? 'text-success' : 'text-warning'">
                    {{ todayStats.checkedIn ? '已打卡' : '未打卡' }}
                  </div>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="mt-6 flex flex-wrap gap-3">
              <Button variant="primary" :disabled="todayStats.checkedIn" @click="checkin">
                {{ todayStats.checkedIn ? '今日已打卡' : '学习打卡' }}
              </Button>
              <Button variant="secondary" @click="goToLearningPlan">学习计划</Button>
              <Button variant="ghost" @click="router.push('/courses')">浏览课程</Button>
            </div>
          </div>

          <!-- 等级进度 -->
          <div class="lg:col-span-4 flex items-center justify-center lg:justify-end">
            <div class="text-center">
              <ProgressRing :percentage="expPercentage" :size="140" type="primary">
                <div class="flex flex-col items-center justify-center">
                  <span class="text-3xl font-bold text-text-primary tabular-nums">{{ userInfo.level }}</span>
                  <span class="text-[10px] tracking-widest text-text-muted font-semibold mt-1">LEVEL</span>
                </div>
              </ProgressRing>

              <div class="mt-4 text-xs text-text-muted">
                <div class="font-medium text-text-secondary tabular-nums">
                  {{ userInfo.experience }} / {{ userInfo.nextLevelExp }} 经验
                </div>
                <div class="mt-1">
                  距离下一级还需
                  <span class="font-semibold text-text-primary tabular-nums">{{ userInfo.nextLevelExp - userInfo.experience }}</span>
                  经验
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
        <!-- 主内容区 -->
        <section class="lg:col-span-8 space-y-6">
          <!-- 继续学习 -->
          <div v-if="continueLearning" class="bg-bg-secondary rounded-xl border border-border-color p-6 hover:shadow-md transition-shadow">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">继续学习</h2>
                <p class="text-sm text-text-secondary mt-1">从上次的位置继续。</p>
              </div>
              <Button variant="ghost" @click="continueCourse">
                进入学习
                <svg class="w-4 h-4 ml-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
                </svg>
              </Button>
            </div>

            <div class="mt-5 grid grid-cols-1 md:grid-cols-12 gap-5 items-center">
              <div class="md:col-span-5">
                <div class="relative overflow-hidden rounded-xl border border-border-color bg-bg-tertiary">
                  <img
                    :src="continueLearning.coverImage || getDefaultCover()"
                    class="w-full aspect-video object-cover"
                    alt="课程封面"
                  />
                </div>
              </div>
              <div class="md:col-span-7">
                <h3 class="text-xl font-semibold text-text-primary">{{ continueLearning.courseName }}</h3>
                <p class="mt-1 text-sm text-text-secondary">{{ continueLearning.currentChapter }}</p>

                <div class="mt-4 flex items-center gap-3">
                  <div class="h-2 flex-1 rounded-full bg-bg-tertiary overflow-hidden">
                    <div
                      class="h-full bg-primary rounded-full transition-all duration-slow"
                      :style="{ width: `${continueLearning.progressPercent}%` }"
                    />
                  </div>
                  <span class="text-xs font-semibold text-primary tabular-nums">{{ continueLearning.progressPercent }}%</span>
                </div>

                <div class="mt-5">
                  <Button variant="primary" @click="continueCourse">继续学习</Button>
                </div>
              </div>
            </div>
          </div>

          <!-- 我的课程 -->
          <div class="bg-bg-secondary rounded-xl border border-border-color p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">我的课程</h2>
                <p class="text-sm text-text-secondary mt-1">快速返回你正在学习的内容。</p>
              </div>
              <Button variant="ghost" @click="goToMyCourses">查看全部</Button>
            </div>

            <div v-if="myCourses.length" class="mt-5 grid grid-cols-1 sm:grid-cols-2 gap-4">
              <button
                v-for="course in myCourses.slice(0, 4)"
                :key="course.courseId"
                type="button"
                class="group text-left rounded-xl border border-border-color bg-bg-tertiary hover:bg-bg-hover transition px-4 py-4 flex gap-4"
                @click="goToCourse(course.courseId)"
              >
                <div class="w-16 h-16 rounded-xl overflow-hidden border border-border-color bg-bg-secondary flex-shrink-0">
                  <img :src="course.coverImage || getDefaultCover()" alt="课程封面" class="w-full h-full object-cover" />
                </div>
                <div class="flex-1 min-w-0">
                  <div class="flex items-start justify-between gap-3">
                    <div class="min-w-0">
                      <div class="text-sm font-semibold text-text-primary line-clamp-1">{{ course.courseName }}</div>
                      <div class="mt-1 text-xs text-text-muted">{{ course.status }}</div>
                    </div>
                    <span
                      class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-semibold border"
                      :class="statusBadgeClass(course.status)"
                    >
                      {{ course.progressPercent }}%
                    </span>
                  </div>
                  <div class="mt-3 h-1.5 rounded-full bg-bg-secondary overflow-hidden">
                    <div
                      class="h-full bg-primary rounded-full"
                      :style="{ width: `${course.progressPercent}%` }"
                    />
                  </div>
                </div>
              </button>
            </div>

            <EmptyState
              v-else
              emoji="📚"
              title="暂无课程"
              description="去课程中心选择一门你感兴趣的课程开始学习。"
              action-text="去选课"
              size="sm"
              @action="router.push('/courses')"
            />
          </div>
        </section>

        <!-- 侧边栏 -->
        <aside class="lg:col-span-4 space-y-6">
          <!-- 本周学习 -->
          <div class="bg-bg-secondary rounded-xl border border-border-color p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">本周学习</h2>
                <p class="text-sm text-text-secondary mt-1">学习时长趋势</p>
              </div>
              <div class="text-right">
                <div class="text-2xl font-semibold text-text-primary tabular-nums">{{ weeklyStats.totalMinutes }}</div>
                <div class="text-xs text-text-muted">分钟</div>
              </div>
            </div>

            <div ref="weeklyChart" class="mt-4 h-56 w-full" />

            <div class="mt-4 flex items-center justify-between text-xs text-text-muted">
              <span>日均</span>
              <span class="font-semibold text-text-primary tabular-nums">{{ Math.round(weeklyStats.totalMinutes / 7) }} 分钟</span>
            </div>
          </div>

          <!-- 最近成就 -->
          <div class="bg-bg-secondary rounded-xl border border-border-color p-6">
            <div class="flex items-center justify-between gap-4">
              <div>
                <h2 class="text-lg font-semibold text-text-primary">最近成就</h2>
                <p class="text-sm text-text-secondary mt-1">记录你的进步</p>
              </div>
              <Button variant="ghost" @click="goToAchievements">查看全部</Button>
            </div>

            <div v-if="recentAchievements.length" class="mt-5 space-y-3">
              <div
                v-for="achievement in recentAchievements.slice(0, 5)"
                :key="achievement.achievementId"
                class="flex items-center gap-3 rounded-xl bg-bg-tertiary border border-border-color px-4 py-3"
              >
                <div class="w-9 h-9 rounded-xl bg-bg-secondary border border-border-color flex items-center justify-center text-primary">
                  <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="8" r="6" /><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11" />
                  </svg>
                </div>
                <div class="min-w-0 flex-1">
                  <div class="text-sm font-semibold text-text-primary line-clamp-1">{{ achievement.name }}</div>
                  <div class="text-xs text-text-muted mt-0.5">{{ achievement.unlockedAt }}</div>
                </div>
              </div>
            </div>

            <EmptyState
              v-else
              emoji="🏆"
              title="暂无成就"
              description="完成学习任务解锁成就徽章。"
              size="sm"
            />
          </div>
        </aside>
      </div>
    </div>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import * as echarts from 'echarts';
import { getStudentDashboard } from '@/api/student';
import { checkin as learningCheckin } from '@/api/learning';
import { PageLayout, Button, EmptyState, ProgressRing } from '@/design-system';

// 类型定义
interface UserInfo {
  username: string;
  realName: string;
  level: number;
  experience: number;
  nextLevelExp: number;
}

interface TodayStats {
  studyMinutes: number;
  streakDays: number;
  checkedIn: boolean;
}

interface ContinueLearning {
  courseId: number;
  courseName: string;
  currentChapter: string;
  progressPercent: number;
  coverImage?: string;
}

interface Course {
  courseId: number;
  courseName: string;
  status: string;
  progressPercent: number;
  coverImage?: string;
}

interface Achievement {
  achievementId: number;
  name: string;
  unlockedAt: string;
}

const router = useRouter();

// 状态
const loading = ref(true);
const userInfo = ref<UserInfo>({
  username: '',
  realName: '',
  level: 1,
  experience: 0,
  nextLevelExp: 100,
});

const todayStats = ref<TodayStats>({
  studyMinutes: 0,
  streakDays: 0,
  checkedIn: false,
});

const continueLearning = ref<ContinueLearning | null>(null);

const weeklyStats = ref({
  dailyMinutes: [0, 0, 0, 0, 0, 0, 0],
  totalMinutes: 0,
});

const myCourses = ref<Course[]>([]);
const recentAchievements = ref<Achievement[]>([]);
const weeklyChart = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
let themeObserver: MutationObserver | null = null;

// 计算属性
const expPercentage = computed(() => {
  return Math.round((userInfo.value.experience / userInfo.value.nextLevelExp) * 100);
});

// 方法
const getDefaultCover = () => 'https://via.placeholder.com/400x240?text=Course';

const statusBadgeClass = (status: string): string => {
  if (status === '已完成') return 'bg-success/10 text-success border-success/30';
  if (status === '进行中') return 'bg-primary/10 text-primary border-primary/30';
  if (status === '未开始') return 'bg-info/10 text-info border-info/30';
  return 'bg-bg-secondary text-text-secondary border-border-color';
};

const loadDashboardData = async () => {
  try {
    loading.value = true;
    const res = await getStudentDashboard();

    if (res.code === 200 && res.data) {
      const data = res.data;

      if (data.userInfo) userInfo.value = data.userInfo;
      if (data.todayStats) todayStats.value = data.todayStats;

      continueLearning.value = data.continueLearning || null;
      if (data.weeklyStats) weeklyStats.value = data.weeklyStats;

      myCourses.value = data.myCourses || [];
      recentAchievements.value = data.recentAchievements || [];

      await nextTick();
      initWeeklyChart();
    }
  } catch (error) {
    console.error('加载 Dashboard 数据失败:', error);
  } finally {
    loading.value = false;
  }
};

const checkin = async () => {
  try {
    const res = await learningCheckin({
      studyMinutes: todayStats.value.studyMinutes || 0,
      studyContent: '今日学习打卡',
    });

    if (res.code === 200 && res.data) {
      console.info(`打卡成功！连续学习 ${res.data.currentStreak || 0} 天`);
      await loadDashboardData();
    }
  } catch (error: any) {
    console.error('打卡失败:', error);
  }
};

const continueCourse = () => {
  if (!continueLearning.value) return;
  router.push(`/course/${continueLearning.value.courseId}/study`);
};

const goToCourse = (courseId: number) => {
  router.push(`/course/${courseId}/study`);
};

const goToMyCourses = () => {
  router.push('/my-courses');
};

const goToLearningPlan = () => {
  router.push('/learning-plan');
};

const goToAchievements = () => {
  router.push('/achievements');
};

const initWeeklyChart = () => {
  if (!weeklyChart.value) return;

  const style = getComputedStyle(document.documentElement);

  const readRgb = (name: string, fallback = '0 0 0') => style.getPropertyValue(name).trim() || fallback;
  const toRgb = (rgb: string) => {
    const [r, g, b] = rgb.split(/\s+/).map((value) => Number(value));
    if (![r, g, b].every(Number.isFinite)) return 'rgb(0, 0, 0)';
    return `rgb(${r}, ${g}, ${b})`;
  };

  const primaryRgb = readRgb('--primary-color-rgb', '79 70 229');
  const textPrimary = toRgb(readRgb('--text-primary-rgb', '17 24 39'));
  const textMuted = toRgb(readRgb('--text-muted-rgb', '107 114 128'));
  const border = toRgb(readRgb('--border-color-rgb', '229 231 235'));
  const grid = toRgb(readRgb('--border-light-rgb', '243 244 246'));
  const surface = toRgb(readRgb('--bg-secondary-rgb', '255 255 255'));
  const primary = toRgb(primaryRgb);

  const [r, g, b] = primaryRgb.split(/\s+/).map(Number);
  const areaTop = `rgba(${r}, ${g}, ${b}, 0.22)`;
  const areaBottom = `rgba(${r}, ${g}, ${b}, 0.02)`;

  if (chartInstance) chartInstance.dispose();
  chartInstance = echarts.init(weeklyChart.value);

  chartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: '{b}: {c} 分钟',
      backgroundColor: surface,
      borderColor: border,
      borderWidth: 1,
      textStyle: { color: textPrimary },
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: { lineStyle: { color: border } },
      axisLabel: { color: textMuted, fontSize: 12 },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      nameTextStyle: { color: textMuted, fontSize: 12 },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: textMuted, fontSize: 12 },
      splitLine: { lineStyle: { color: grid, type: 'dashed' } },
    },
    series: [
      {
        data: weeklyStats.value.dailyMinutes,
        type: 'line',
        smooth: true,
        showSymbol: false,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: areaTop },
            { offset: 1, color: areaBottom },
          ]),
        },
        itemStyle: { color: primary },
        lineStyle: { width: 2, color: primary },
      },
    ],
    grid: { left: '10%', right: '6%', bottom: '12%', top: '14%' },
  });
};

const handleResize = () => chartInstance?.resize();

onMounted(async () => {
  await loadDashboardData();
  window.addEventListener('resize', handleResize);

  themeObserver = new MutationObserver(() => {
    initWeeklyChart();
  });
  themeObserver.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['data-theme'],
  });
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  themeObserver?.disconnect();
  themeObserver = null;
  chartInstance?.dispose();
  chartInstance = null;
});
</script>
