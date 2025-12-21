<template>
  <PageLayout max-width="xl">
    <!-- Hero Section -->
    <section class="relative overflow-hidden rounded-2xl border border-border-color bg-bg-secondary p-8 md:p-12 mb-8">
      <div class="flex flex-col lg:flex-row items-start lg:items-center justify-between gap-10">
        <div class="max-w-2xl">
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <svg class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z" />
              <path d="M5 3v4" /><path d="M19 17v4" /><path d="M3 5h4" /><path d="M17 19h4" />
            </svg>
            用 AI 更高效学习
          </p>
          <h1 class="mt-4 text-4xl md:text-5xl font-semibold tracking-tight text-text-primary leading-tight">
            掌握 <span class="text-primary">IT 核心技能</span><br />
            开启职业新篇章
          </h1>
          <p class="mt-5 text-lg md:text-xl text-text-secondary leading-relaxed max-w-xl">
            课程 + AI 路径推荐 + 数据化反馈，帮助你系统化提升。
          </p>

          <div class="mt-8 flex flex-col sm:flex-row gap-3">
            <Button variant="primary" @click="$router.push('/courses')">
              浏览全部课程
              <svg class="w-4 h-4 ml-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
              </svg>
            </Button>
            <Button variant="secondary" @click="$router.push('/recommend')">
              <svg class="w-5 h-5 mr-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M15 4V2" /><path d="M15 16v-2" /><path d="M8 9h2" /><path d="M20 9h2" />
                <path d="M17.8 11.8 20 14" /><path d="M15 9h0" />
                <path d="M17.8 6.2 20 4" /><path d="m3 21 9-9" /><path d="M12.2 6.2 10 4" />
              </svg>
              AI 智能选课
            </Button>
          </div>
        </div>

        <!-- 统计卡片 -->
        <div class="grid grid-cols-2 gap-4 w-full max-w-md">
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5">
            <p class="text-xs text-text-muted">精品课程</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">{{ stats.totalCourses }}+</p>
          </div>
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5">
            <p class="text-xs text-text-muted">智能辅助</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">AI</p>
          </div>
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5">
            <p class="text-xs text-text-muted">正在学习</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">{{ stats.enrolledCount }}</p>
          </div>
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5">
            <p class="text-xs text-text-muted">实战导向</p>
            <p class="mt-2 text-3xl font-semibold tracking-tight text-text-primary">100%</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门课程 -->
    <section class="mb-8">
      <div class="flex items-end justify-between gap-6 mb-6">
        <div>
          <h2 class="text-2xl font-semibold tracking-tight text-text-primary">热门课程</h2>
          <p class="mt-1 text-sm text-text-muted">精选课程，快速建立你的技能栈。</p>
        </div>
        <router-link
          to="/courses"
          class="inline-flex items-center gap-2 text-sm font-medium text-text-secondary hover:text-text-primary transition-colors"
        >
          查看全部
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
          </svg>
        </router-link>
      </div>

      <!-- 加载状态 -->
      <div v-if="loadingHot" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="i in 6" :key="i" class="bg-bg-secondary rounded-xl border border-border-color h-80 animate-pulse" />
      </div>

      <!-- 课程列表 -->
      <div v-else-if="hotCourses.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="course in hotCourses"
          :key="course.id"
          class="bg-bg-secondary rounded-xl border border-border-color overflow-hidden cursor-pointer flex flex-col h-full hover:shadow-md transition-shadow"
          @click="$router.push(`/course/${course.id}`)"
        >
          <div class="h-44 bg-bg-tertiary border-b border-border-color flex items-center justify-center">
            <component :is="getCategoryIcon(course.category)" class="w-14 h-14 text-primary/80" />
          </div>

          <div class="p-6 flex-1 flex flex-col">
            <div class="flex flex-wrap items-center gap-2 mb-3">
              <span class="px-2 py-1 text-xs rounded-md bg-bg-tertiary text-text-secondary">
                {{ course.categoryName || getCategoryName(course.category) }}
              </span>
              <span class="px-2 py-1 text-xs rounded-md bg-bg-tertiary text-text-secondary">
                {{ course.difficultyName || getDifficultyName(course.difficulty) }}
              </span>
            </div>
            <h3 class="text-lg font-semibold text-text-primary mb-2 line-clamp-2 hover:text-primary transition-colors">
              {{ course.name }}
            </h3>
            <p class="text-text-secondary text-sm mb-4 line-clamp-2 flex-1">
              {{ course.description || '暂无描述' }}
            </p>

            <div class="flex items-center justify-between pt-4 border-t border-border-color mt-auto">
              <div class="flex items-center gap-2 text-xs text-text-muted">
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
                </svg>
                <span>{{ course.durationHours }} 课时</span>
              </div>
              <span class="text-primary font-medium text-sm">查看详情</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <EmptyState
        v-else
        emoji="📚"
        title="暂无热门课程"
        description="稍后再来看看，或先浏览全部课程。"
        action-text="浏览课程"
        @action="$router.push('/courses')"
      />
    </section>

    <!-- 功能特性 -->
    <section class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div
        class="bg-bg-secondary rounded-xl border border-border-color p-8 flex flex-col justify-between gap-6 cursor-pointer hover:shadow-md transition-shadow"
        @click="$router.push('/recommend')"
      >
        <div>
          <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
            <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="6" cy="19" r="3" /><path d="M9 19h8.5a3.5 3.5 0 0 0 0-7h-11a3.5 3.5 0 0 1 0-7H15" />
              <circle cx="18" cy="5" r="3" />
            </svg>
          </div>
          <h3 class="mt-5 text-xl font-semibold text-text-primary tracking-tight">AI 智能学习路径</h3>
          <p class="mt-2 text-sm text-text-secondary leading-relaxed">
            让 AI 根据你的基础与目标，推荐更合适的课程顺序与学习节奏。
          </p>
        </div>
        <div class="inline-flex items-center gap-2 text-sm font-medium text-primary">
          开始规划
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
          </svg>
        </div>
      </div>

      <div
        class="bg-bg-secondary rounded-xl border border-border-color p-8 flex flex-col justify-between gap-6 cursor-pointer hover:shadow-md transition-shadow"
        @click="$router.push('/learning')"
      >
        <div>
          <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
            <svg class="w-6 h-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 3v18h18" /><path d="M18 17V9" /><path d="M13 17V5" /><path d="M8 17v-3" />
            </svg>
          </div>
          <h3 class="mt-5 text-xl font-semibold text-text-primary tracking-tight">学习进度追踪</h3>
          <p class="mt-2 text-sm text-text-secondary leading-relaxed">
            记录学习时长、完成度与趋势，让你知道下一步该怎么提升。
          </p>
        </div>
        <div class="inline-flex items-center gap-2 text-sm font-medium text-primary">
          查看进度
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
          </svg>
        </div>
      </div>
    </section>
  </PageLayout>
</template>

<script setup lang="ts">
import { onMounted, ref, type Component } from 'vue';
import { getMyEnrollments } from '@/api/enrollment';
import { getCourses } from '@/api/course';
import { PageLayout, Button, EmptyState } from '@/design-system';

// 图标组件
const IconServer = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="8" rx="2" ry="2" /><rect x="2" y="14" width="20" height="8" rx="2" ry="2" /><line x1="6" y1="6" x2="6.01" y2="6" /><line x1="6" y1="18" x2="6.01" y2="18" /></svg>` };
const IconLayout = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2" /><line x1="3" y1="9" x2="21" y2="9" /><line x1="9" y1="21" x2="9" y2="9" /></svg>` };
const IconDatabase = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><ellipse cx="12" cy="5" rx="9" ry="3" /><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3" /><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5" /></svg>` };
const IconBrain = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96.44 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 1.98-3A2.5 2.5 0 0 1 9.5 2Z" /><path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96.44 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-1.98-3A2.5 2.5 0 0 0 14.5 2Z" /></svg>` };
const IconCloud = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z" /></svg>` };
const IconCode = { template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6" /><polyline points="8 6 2 12 8 18" /></svg>` };

// 类型定义
interface Course {
  id: number;
  name: string;
  description?: string;
  category: string;
  categoryName?: string;
  difficulty: number;
  difficultyName?: string;
  durationHours: number;
}

// 状态
const stats = ref({
  enrolledCount: 0,
  totalCourses: 0,
});

const hotCourses = ref<Course[]>([]);
const loadingHot = ref(false);

// 分类图标映射
const categoryIcons: Record<string, Component> = {
  BACKEND: IconServer,
  FRONTEND: IconLayout,
  DATABASE: IconDatabase,
  AI: IconBrain,
  CLOUD: IconCloud,
};

const getCategoryIcon = (category: string): Component => categoryIcons[category] || IconCode;

const getCategoryName = (category: string): string => {
  const names: Record<string, string> = {
    BACKEND: '后端开发',
    FRONTEND: '前端开发',
    DATABASE: '数据库',
    AI: '人工智能',
    CLOUD: '云计算',
  };
  return names[category] || '课程';
};

const getDifficultyName = (difficulty: number): string => {
  const names: Record<number, string> = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' };
  return names[difficulty] || '未知';
};

// 加载数据
const loadStats = async () => {
  loadingHot.value = true;
  try {
    const enrollRes = await getMyEnrollments();
    const enrollments = enrollRes.data || [];
    stats.value.enrolledCount = enrollments.filter((e: any) => e.status === 0).length;

    const courseRes = await getCourses({ page: 1, size: 6, status: 1 });
    stats.value.totalCourses = courseRes.data?.total || 0;
    hotCourses.value = courseRes.data?.records || [];
  } catch (error) {
    console.error('加载统计数据失败:', error);
  } finally {
    loadingHot.value = false;
  }
};

onMounted(() => {
  loadStats();
});
</script>
