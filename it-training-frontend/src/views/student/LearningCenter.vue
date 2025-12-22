<template>
  <div class="max-w-7xl mx-auto px-4 md:px-8 py-8 space-y-8">
    <!-- Header -->
    <section class="relative overflow-hidden rounded-3xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl shadow-sm">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>

      <div class="relative p-6 md:p-10">
        <div class="flex flex-col md:flex-row md:items-start justify-between gap-6">
          <div class="min-w-0">
            <h1 class="text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">学习中心</h1>
            <p class="mt-2 text-text-secondary">追踪学习进度，保持学习动力。</p>
          </div>

          <div class="inline-flex items-center gap-2 rounded-full bg-bg-primary/40 border border-border-color/60 px-4 py-2 text-sm text-text-secondary">
            <!-- Calendar Icon -->
            <svg class="w-4 h-4 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            {{ monthLabel }}
          </div>
        </div>

        <!-- Stats -->
        <div class="mt-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div class="card p-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
                <!-- Clock Icon -->
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div class="min-w-0">
                <div class="text-xs text-text-muted">累计学习时长</div>
                <div class="text-base font-semibold text-text-primary">{{ dashboard.totalStudyFormatted || '0分钟' }}</div>
              </div>
            </div>
          </div>

          <div class="card p-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-info/10 border border-info/20 flex items-center justify-center text-info">
                <!-- GraduationCap Icon -->
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 14l9-5-9-5-9 5 9 5zm0 0l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14zm-4 6v-7.5l4-2.222" />
                </svg>
              </div>
              <div class="min-w-0">
                <div class="text-xs text-text-muted">完成 / 报名</div>
                <div class="text-base font-semibold text-text-primary tabular-nums">
                  {{ dashboard.totalCoursesCompleted || 0 }}/{{ dashboard.totalCoursesEnrolled || 0 }}
                </div>
              </div>
            </div>
          </div>

          <div class="card p-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-warning/10 border border-warning/20 flex items-center justify-center text-warning">
                <!-- Flame Icon -->
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.879 16.121A3 3 0 1012.015 11L11 14H9c0 .768.293 1.536.879 2.121z" />
                </svg>
              </div>
              <div class="min-w-0">
                <div class="text-xs text-text-muted">连续学习</div>
                <div class="text-base font-semibold text-text-primary tabular-nums">{{ dashboard.currentStreakDays || 0 }} 天</div>
              </div>
            </div>
          </div>

          <div class="card p-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-success/10 border border-success/20 flex items-center justify-center text-success">
                <!-- Trophy Icon -->
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
                </svg>
              </div>
              <div class="min-w-0">
                <div class="text-xs text-text-muted">成就积分</div>
                <div class="text-base font-semibold text-text-primary tabular-nums">{{ dashboard.totalAchievementPoints || 0 }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Content -->
    <div class="grid grid-cols-1 lg:grid-cols-12 gap-6">
      <!-- Left -->
      <section class="lg:col-span-8 space-y-6">
        <!-- Check-in -->
        <div class="card p-6">
          <div class="flex items-center justify-between gap-4">
            <h2 class="text-lg font-semibold text-text-primary">今日打卡</h2>
            <span
              class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold border"
              :class="dashboard.todayCheckedIn ? 'bg-success/10 text-success border-success/30' : 'bg-warning/10 text-warning border-warning/30'"
            >
              {{ dashboard.todayCheckedIn ? '已打卡' : '未打卡' }}
            </span>
          </div>

          <div v-if="!dashboard.todayCheckedIn" class="mt-5 space-y-4">
            <div class="rounded-2xl bg-bg-tertiary/40 border border-border-color/60 p-4">
              <div class="flex items-center justify-between text-sm">
                <span class="text-text-secondary">今日学习时长</span>
                <span class="font-semibold text-text-primary tabular-nums">{{ todayStudyMinutes }} 分钟</span>
              </div>
              <p class="mt-2 text-xs text-text-muted">系统将自动统计你今天的学习时长。</p>
            </div>

            <div>
              <label class="text-sm font-medium text-text-primary">学习笔记（可选）</label>
              <textarea
                v-model="checkinForm.studyContent"
                class="mt-2 w-full min-h-28 rounded-2xl bg-bg-tertiary/40 border border-border-color/60 px-4 py-3 text-sm text-text-primary placeholder-text-muted focus:outline-none focus:ring-2 focus:ring-primary/15 focus:border-primary/40 transition"
                placeholder="记录今天学到了什么..."
              ></textarea>
            </div>

            <div class="flex items-center gap-3">
              <Button variant="primary" :disabled="checkinLoading || todayStudyMinutes === 0" @click="handleCheckin">
                {{ checkinLoading ? '打卡中...' : '立即打卡' }}
              </Button>
              <span v-if="todayStudyMinutes === 0" class="text-xs text-warning">请先学习课程后再打卡</span>
            </div>
          </div>

          <div v-else class="mt-6 flex items-start gap-4">
            <div class="w-10 h-10 rounded-2xl bg-success/10 border border-success/20 flex items-center justify-center text-success">
              <!-- CheckCircle Icon -->
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div class="min-w-0">
              <p class="text-sm text-text-secondary">
                今日已打卡，学习了
                <span class="font-semibold text-text-primary tabular-nums">{{ todayCheckin?.studyMinutes || 0 }}</span>
                分钟
              </p>
              <p v-if="todayCheckin?.studyContent" class="mt-1 text-sm text-text-muted line-clamp-2">
                {{ todayCheckin.studyContent }}
              </p>
            </div>
          </div>
        </div>

        <!-- In progress -->
        <div class="card p-6">
          <div class="flex items-center justify-between gap-4">
            <div>
              <h2 class="text-lg font-semibold text-text-primary">进行中的课程</h2>
              <p class="text-sm text-text-secondary mt-1">继续推进你的学习进度。</p>
            </div>
            <router-link to="/my-courses" class="text-sm font-medium text-primary hover:text-primary-light transition-colors">查看全部</router-link>
          </div>

          <div v-if="dashboard.inProgressCourses?.length" class="mt-5 space-y-4">
            <button
              v-for="course in dashboard.inProgressCourses"
              :key="course.id"
              type="button"
              class="w-full text-left rounded-2xl border border-border-color/60 bg-bg-secondary/60 hover:bg-bg-secondary transition shadow-sm hover:shadow-md px-5 py-4"
              @click="router.push(`/course/${course.courseId || course.id}/study`)"
            >
              <div class="flex items-start justify-between gap-4">
                <div class="min-w-0">
                  <div class="text-sm font-semibold text-text-primary line-clamp-1">{{ course.courseName }}</div>
                  <div class="mt-1 text-xs text-text-muted">
                    {{ course.courseCategory }} · 已学 {{ course.studyDurationFormatted }}
                  </div>
                </div>
                <span class="text-xs font-semibold text-primary tabular-nums">{{ course.progressPercent }}%</span>
              </div>

              <div class="mt-3 h-1.5 rounded-full bg-bg-tertiary overflow-hidden">
                <div class="h-full bg-gradient-to-r from-primary-light to-secondary rounded-full" :style="{ width: `${course.progressPercent}%` }"></div>
              </div>
            </button>
          </div>

          <div v-else class="mt-6">
            <EmptyState
              emoji="📚"
              title="暂无进行中的课程"
              description="去课程中心选择一门课程开始学习。"
              action-text="去选课"
              @action="router.push('/courses')"
            />
          </div>
        </div>
      </section>

      <!-- Right -->
      <aside class="lg:col-span-4 space-y-6">
        <!-- Weekly -->
        <div class="card p-6">
          <div class="flex items-center justify-between gap-4">
            <div>
              <h2 class="text-lg font-semibold text-text-primary">本周学习</h2>
              <p class="text-sm text-text-secondary mt-1">分钟趋势</p>
            </div>
          </div>

          <div v-if="dashboard.weeklyStudyData?.length" class="mt-5 grid grid-cols-7 gap-2 items-end h-32">
            <div v-for="(d, i) in dashboard.weeklyStudyData" :key="i" class="flex flex-col items-center gap-2">
              <div class="w-full h-24 rounded-full bg-bg-tertiary/40 overflow-hidden flex items-end">
                <div class="w-full rounded-full bg-primary" :style="{ height: `${getBarHeight(d.studyMinutes)}%` }"></div>
              </div>
              <div class="text-[11px] text-text-muted">
                {{ weekDays[new Date(d.date).getDay()] }}
              </div>
            </div>
          </div>

          <div v-else class="mt-6">
            <EmptyState emoji="✨" title="暂无统计" description="开始学习后，这里会展示你的本周趋势。" size="sm" />
          </div>
        </div>

        <!-- Calendar -->
        <div class="card p-6">
          <div class="flex items-center justify-between gap-4">
            <h2 class="text-lg font-semibold text-text-primary">打卡日历</h2>
            <div class="flex items-center gap-2">
              <button
                type="button"
                class="w-9 h-9 rounded-full border border-border-color/60 bg-bg-secondary/70 hover:bg-bg-tertiary/60 transition flex items-center justify-center"
                @click="prevMonth"
              >
                <!-- ChevronLeft Icon -->
                <svg class="w-4 h-4 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              <div class="text-sm font-semibold text-text-primary tabular-nums">{{ monthLabel }}</div>
              <button
                type="button"
                class="w-9 h-9 rounded-full border border-border-color/60 bg-bg-secondary/70 hover:bg-bg-tertiary/60 transition flex items-center justify-center"
                @click="nextMonth"
              >
                <!-- ChevronRight Icon -->
                <svg class="w-4 h-4 text-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>
          </div>

          <div class="mt-4 grid grid-cols-7 text-center text-xs text-text-muted">
            <div v-for="d in weekDays" :key="d" class="py-1">{{ d }}</div>
          </div>

          <div class="mt-2 grid grid-cols-7 gap-1">
            <div
              v-for="(day, idx) in calendarDays"
              :key="idx"
              class="aspect-square flex items-center justify-center rounded-xl text-sm transition"
              :class="[
                day.currentMonth ? 'text-text-primary' : 'text-text-muted/60',
                day.checkedIn ? 'bg-success/12 text-success font-semibold' : 'hover:bg-bg-tertiary/60',
                day.isToday ? 'ring-2 ring-primary/30' : '',
              ]"
            >
              {{ day.date }}
            </div>
          </div>

          <div class="mt-4 flex items-center justify-center gap-4 text-xs text-text-muted">
            <span class="inline-flex items-center gap-2"><span class="w-2 h-2 rounded-full bg-success"></span> 已打卡</span>
            <span class="inline-flex items-center gap-2"><span class="w-2 h-2 rounded-full border-2 border-primary"></span> 今天</span>
          </div>
        </div>
      </aside>
    </div>

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
