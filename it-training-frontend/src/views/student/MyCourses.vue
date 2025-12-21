<template>
  <PageLayout max-width="xl">
    <!-- Hero Section -->
    <section class="relative overflow-hidden rounded-2xl border border-border-color bg-bg-secondary p-6 md:p-10 mb-8">
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 class="text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">我的课程</h1>
          <p class="mt-2 text-text-secondary">共 {{ totalCourses }} 门课程，继续你的学习。</p>
        </div>

        <div class="flex flex-col sm:flex-row gap-3 items-stretch sm:items-center">
          <!-- 搜索框 -->
          <Input
            v-model="searchKeyword"
            placeholder="搜索课程..."
            clearable
            class="w-full sm:w-80"
            @input="handleSearch"
          >
            <template #icon-left>
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" />
                <path d="m21 21-4.35-4.35" />
              </svg>
            </template>
          </Input>

          <Button variant="primary" @click="goToCourses">去选课</Button>
        </div>
      </div>
    </section>

    <!-- 筛选器 -->
    <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-4 mb-6">
      <!-- 状态筛选 -->
      <div class="inline-flex flex-wrap items-center gap-2 rounded-full bg-bg-secondary border border-border-color p-1">
        <button
          v-for="filter in filters"
          :key="filter.value"
          type="button"
          class="px-4 py-2 rounded-full text-sm font-medium transition-all"
          :class="activeFilter === filter.value
            ? 'bg-bg-tertiary shadow-sm text-text-primary'
            : 'text-text-secondary hover:text-text-primary hover:bg-bg-hover'"
          @click="activeFilter = filter.value; handleFilterChange()"
        >
          <span>{{ filter.label }}</span>
          <span
            class="ml-2 inline-flex items-center justify-center min-w-6 h-6 px-2 rounded-full text-xs font-semibold"
            :class="activeFilter === filter.value ? 'bg-primary/10 text-primary' : 'bg-bg-tertiary text-text-muted'"
          >
            {{ filter.count }}
          </span>
        </button>
      </div>

      <!-- 排序 -->
      <div class="flex items-center gap-3">
        <span class="text-sm text-text-muted">排序</span>
        <Select
          v-model="sortBy"
          :options="sortOptions"
          class="w-40"
          @change="handleSort"
        />
      </div>
    </div>

    <!-- 课程列表 -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="animate-spin w-8 h-8 border-2 border-primary border-t-transparent rounded-full" />
    </div>

    <div v-else-if="paginatedCourses.length === 0" class="bg-bg-secondary rounded-xl border border-border-color p-8">
      <EmptyState
        :emoji="emptyEmoji"
        :title="emptyTitle"
        :description="emptyDescription"
        action-text="去选课"
        @action="goToCourses"
      />
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
      <CourseCard
        v-for="course in paginatedCourses"
        :key="course.id"
        :course="course"
        show-progress
        @click="goToCourseDetail(course.id)"
      >
        <template #actions>
          <Button
            variant="primary"
            size="sm"
            @click.stop="goToStudy(course.id)"
          >
            {{ course.progressPercent >= 100 ? '复习课程' : course.progressPercent > 0 ? '继续学习' : '开始学习' }}
          </Button>

          <div class="relative">
            <button
              type="button"
              class="inline-flex items-center justify-center w-10 h-10 rounded-full border border-border-color bg-bg-secondary hover:bg-bg-tertiary transition"
              @click.stop="toggleMenu(course.id)"
            >
              <svg class="w-4 h-4 text-text-secondary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="1" /><circle cx="19" cy="12" r="1" /><circle cx="5" cy="12" r="1" />
              </svg>
            </button>

            <!-- 下拉菜单 -->
            <div
              v-if="activeMenu === course.id"
              class="absolute right-0 top-12 z-10 w-40 bg-bg-secondary rounded-xl border border-border-color shadow-lg py-1"
            >
              <button
                type="button"
                class="w-full px-4 py-2 text-left text-sm text-text-primary hover:bg-bg-hover transition"
                @click.stop="handleCommand('notes', course)"
              >
                我的笔记
              </button>
              <button
                type="button"
                class="w-full px-4 py-2 text-left text-sm transition"
                :class="course.progressPercent >= 100 ? 'text-text-primary hover:bg-bg-hover' : 'text-text-muted cursor-not-allowed'"
                :disabled="course.progressPercent < 100"
                @click.stop="handleCommand('certificate', course)"
              >
                查看证书
              </button>
              <button
                type="button"
                class="w-full px-4 py-2 text-left text-sm text-text-primary hover:bg-bg-hover transition"
                @click.stop="handleCommand('share', course)"
              >
                分享课程
              </button>
            </div>
          </div>
        </template>
      </CourseCard>
    </div>

    <!-- 分页 -->
    <div v-if="filteredCourses.length > pageSize" class="mt-10 flex justify-center">
      <div class="bg-bg-secondary rounded-xl border border-border-color px-6 py-4 flex items-center gap-4">
        <span class="text-sm text-text-muted">
          共 {{ filteredCourses.length }} 条
        </span>
        <div class="flex items-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            :disabled="currentPage === 1"
            @click="currentPage--; handlePageChange()"
          >
            上一页
          </Button>
          <span class="px-3 text-sm text-text-primary">
            {{ currentPage }} / {{ totalPages }}
          </span>
          <Button
            variant="secondary"
            size="sm"
            :disabled="currentPage >= totalPages"
            @click="currentPage++; handlePageChange()"
          >
            下一页
          </Button>
        </div>
      </div>
    </div>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { getMyEnrollments } from '@/api/enrollment';
import { getCourses } from '@/api/course';
import { getCourseProgress } from '@/api/learning';
import { PageLayout, Input, Button, Select, EmptyState } from '@/design-system';
import CourseCard from '@/components/CourseCard.vue';

// 类型定义
interface Course {
  id: number;
  name: string;
  description?: string;
  progressPercent: number;
  studyDuration: number;
  lastStudyAt?: string;
  chapterCount: number;
  sessionId?: number;
  sessionCode?: string;
  startDate?: string;
  endDate?: string;
}

interface Filter {
  label: string;
  value: string;
  count: number;
}

const router = useRouter();

// 状态
const loading = ref(false);
const allCourses = ref<Course[]>([]);
const searchKeyword = ref('');
const activeFilter = ref('all');
const sortBy = ref('recent');
const currentPage = ref(1);
const pageSize = ref(12);
const activeMenu = ref<number | null>(null);

// 排序选项
const sortOptions = [
  { label: '最近学习', value: 'recent' },
  { label: '进度最高', value: 'progress' },
  { label: '课程名称', value: 'name' },
];

// 计算属性
const totalCourses = computed(() => allCourses.value.length);

const learningCourses = computed(() =>
  allCourses.value.filter((c) => c.progressPercent > 0 && c.progressPercent < 100)
);
const completedCourses = computed(() =>
  allCourses.value.filter((c) => c.progressPercent >= 100)
);
const notStartedCourses = computed(() =>
  allCourses.value.filter((c) => !c.progressPercent || c.progressPercent === 0)
);

const filters = computed<Filter[]>(() => [
  { label: '全部', value: 'all', count: allCourses.value.length },
  { label: '进行中', value: 'learning', count: learningCourses.value.length },
  { label: '已完成', value: 'completed', count: completedCourses.value.length },
  { label: '未开始', value: 'notStarted', count: notStartedCourses.value.length },
]);

const filteredCourses = computed(() => {
  let courses = allCourses.value;

  if (activeFilter.value === 'learning') courses = learningCourses.value;
  else if (activeFilter.value === 'completed') courses = completedCourses.value;
  else if (activeFilter.value === 'notStarted') courses = notStartedCourses.value;

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    courses = courses.filter(
      (c) =>
        c.name.toLowerCase().includes(keyword) ||
        (c.description && c.description.toLowerCase().includes(keyword))
    );
  }

  if (sortBy.value === 'recent') {
    courses = [...courses].sort((a, b) => {
      const timeA = a.lastStudyAt ? new Date(a.lastStudyAt).getTime() : 0;
      const timeB = b.lastStudyAt ? new Date(b.lastStudyAt).getTime() : 0;
      return timeB - timeA;
    });
  } else if (sortBy.value === 'progress') {
    courses = [...courses].sort((a, b) => (b.progressPercent || 0) - (a.progressPercent || 0));
  } else if (sortBy.value === 'name') {
    courses = [...courses].sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'));
  }

  return courses;
});

const totalPages = computed(() => Math.ceil(filteredCourses.value.length / pageSize.value));

const paginatedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredCourses.value.slice(start, start + pageSize.value);
});

const emptyEmoji = computed(() => {
  if (searchKeyword.value) return '🔍';
  if (activeFilter.value === 'completed') return '✅';
  if (activeFilter.value === 'learning') return '📖';
  if (activeFilter.value === 'notStarted') return '📚';
  return '📚';
});

const emptyTitle = computed(() => {
  if (searchKeyword.value) return '未找到相关课程';
  if (activeFilter.value === 'learning') return '暂无进行中的课程';
  if (activeFilter.value === 'completed') return '暂无已完成的课程';
  if (activeFilter.value === 'notStarted') return '暂无未开始的课程';
  return '暂无课程';
});

const emptyDescription = computed(() => {
  if (searchKeyword.value) return `未找到包含"${searchKeyword.value}"的课程`;
  if (activeFilter.value === 'learning') return '开始学习一门课程吧';
  if (activeFilter.value === 'completed') return '完成课程学习解锁成就';
  if (activeFilter.value === 'notStarted') return '开始你的第一门课程';
  return '快去选课开启学习之旅吧！';
});

// 方法
const loadMyCourses = async () => {
  loading.value = true;
  try {
    const enrollRes = await getMyEnrollments();
    const enrollments = enrollRes.data || [];

    const coursePromises = enrollments
      .filter((e: any) => e.status === 0)
      .map(async (enrollment: any) => {
        try {
          const courseRes = await getCourses({ name: enrollment.courseName });
          const course = courseRes.data?.records?.[0];

          if (!course) {
            console.warn('未找到课程:', enrollment.courseName);
            return null;
          }

          let progress = null;
          try {
            const progressRes = await getCourseProgress(course.id);
            progress = progressRes.data;
          } catch (err) {
            console.warn('获取进度失败:', err);
          }

          return {
            ...course,
            progressPercent: progress?.progressPercent || 0,
            studyDuration: progress?.studyDurationMinutes || 0,
            lastStudyAt: progress?.updatedAt || enrollment.enrolledAt,
            chapterCount: 0,
            sessionId: enrollment.sessionId,
            sessionCode: enrollment.sessionCode,
            startDate: enrollment.startDate,
            endDate: enrollment.endDate,
          };
        } catch (err) {
          console.error('获取课程失败:', err);
          return null;
        }
      });

    const courses = await Promise.all(coursePromises);
    allCourses.value = courses.filter((c): c is Course => c !== null);
  } catch (error) {
    console.error('加载课程列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  currentPage.value = 1;
};

const handleFilterChange = () => {
  currentPage.value = 1;
};

const handleSort = () => {
  currentPage.value = 1;
};

const handlePageChange = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const goToCourses = () => router.push('/courses');
const goToStudy = (courseId: number) => router.push(`/course/${courseId}/study`);
const goToCourseDetail = (courseId: number) => router.push(`/course/${courseId}`);

const toggleMenu = (courseId: number) => {
  activeMenu.value = activeMenu.value === courseId ? null : courseId;
};

const handleCommand = (command: string, course: Course) => {
  activeMenu.value = null;
  switch (command) {
    case 'notes':
      console.info('笔记功能开发中...');
      break;
    case 'certificate':
      if (course.progressPercent >= 100) {
        console.info('证书功能开发中...');
      }
      break;
    case 'share':
      console.info('分享功能开发中...');
      break;
  }
};

// 点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement;
  if (!target.closest('.relative')) {
    activeMenu.value = null;
  }
};

onMounted(() => {
  loadMyCourses();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>
