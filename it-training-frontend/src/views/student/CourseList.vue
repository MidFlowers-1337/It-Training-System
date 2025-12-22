<template>
  <PageLayout>
    <!-- Hero -->
    <section class="relative rounded-2xl bg-fill-secondary/50 p-8 md:p-10 overflow-hidden">
      <div class="absolute inset-0 pointer-events-none">
        <div class="absolute -top-24 -right-24 w-72 h-72 bg-primary/10 blur-3xl rounded-full"></div>
      </div>
      <div class="relative flex flex-col lg:flex-row lg:items-end justify-between gap-6">
        <div>
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <svg class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" /><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
            </svg>
            课程中心
          </p>
          <h1 class="mt-3 text-4xl font-semibold tracking-tight text-text-primary">探索丰富的 IT 技能课程</h1>
          <p class="mt-2 text-sm text-text-secondary">选择分类与难度，找到适合你的下一门课。</p>
        </div>

        <div class="w-full lg:w-96">
          <Input
            v-model="searchKeyword"
            placeholder="搜索课程..."
            clearable
            @input="handleSearch"
          >
            <template #icon-left>
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" />
              </svg>
            </template>
          </Input>
        </div>
      </div>
    </section>

    <div class="flex flex-col lg:flex-row gap-8 mt-6">
      <!-- Filters -->
      <aside class="w-full lg:w-72 flex-shrink-0 lg:sticky lg:top-24 self-start">
        <div class="rounded-2xl bg-fill-secondary/50 p-6 space-y-6">
          <div class="flex items-center gap-2 text-sm font-medium text-text-primary">
            <svg class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="4" y1="21" x2="4" y2="14" /><line x1="4" y1="10" x2="4" y2="3" />
              <line x1="12" y1="21" x2="12" y2="12" /><line x1="12" y1="8" x2="12" y2="3" />
              <line x1="20" y1="21" x2="20" y2="16" /><line x1="20" y1="12" x2="20" y2="3" />
              <line x1="1" y1="14" x2="7" y2="14" /><line x1="9" y1="8" x2="15" y2="8" /><line x1="17" y1="16" x2="23" y2="16" />
            </svg>
            筛选
          </div>

          <div>
            <h3 class="text-xs font-semibold text-text-tertiary tracking-widest uppercase">分类</h3>
            <InsetGroup class="mt-3">
              <InsetItem clickable @click="selectedCategory = null">
                <template #default>
                  <span class="font-medium" :class="selectedCategory === null ? 'text-text-primary' : 'text-text-secondary'">
                    全部课程
                  </span>
                </template>
                <template #suffix>
                  <svg v-if="selectedCategory === null" class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </template>
              </InsetItem>
              <InsetItem
                v-for="cat in categories"
                :key="cat.value"
                clickable
                @click="selectedCategory = cat.value"
              >
                <template #default>
                  <span class="font-medium" :class="selectedCategory === cat.value ? 'text-text-primary' : 'text-text-secondary'">
                    {{ cat.label }}
                  </span>
                </template>
                <template #suffix>
                  <svg v-if="selectedCategory === cat.value" class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </template>
              </InsetItem>
            </InsetGroup>
          </div>

          <div>
            <h3 class="text-xs font-semibold text-text-tertiary tracking-widest uppercase">难度</h3>
            <InsetGroup class="mt-3">
              <InsetItem clickable @click="selectedDifficulty = null">
                <template #default>
                  <span class="font-medium" :class="selectedDifficulty === null ? 'text-text-primary' : 'text-text-secondary'">
                    全部难度
                  </span>
                </template>
                <template #suffix>
                  <svg v-if="selectedDifficulty === null" class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </template>
              </InsetItem>
              <InsetItem
                v-for="diff in difficulties"
                :key="diff.value"
                clickable
                @click="selectedDifficulty = diff.value"
              >
                <template #default>
                  <span class="font-medium" :class="selectedDifficulty === diff.value ? 'text-text-primary' : 'text-text-secondary'">
                    {{ diff.label }}
                  </span>
                </template>
                <template #suffix>
                  <svg v-if="selectedDifficulty === diff.value" class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                </template>
              </InsetItem>
            </InsetGroup>
          </div>

          <Button variant="ghost" class="w-full justify-center" @click="resetFilters">重置筛选</Button>
        </div>
      </aside>

      <!-- List -->
      <main class="flex-1 min-w-0">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-6">
          <div class="text-sm text-text-tertiary">共 {{ total }} 门课程</div>
          <div class="flex items-center gap-3">
            <span class="text-xs text-text-tertiary hidden sm:block">排序</span>
            <Select v-model="sortBy" :options="sortOptions" class="w-32" />
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <div v-for="i in 6" :key="i" class="rounded-2xl bg-fill-secondary/50 h-80 animate-pulse"></div>
        </div>

        <!-- Course Grid -->
        <div v-else-if="courses.length > 0" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          <CourseCard v-for="course in courses" :key="course.id" :course="course" @click="handleCourseClick" />
        </div>

        <!-- Empty -->
        <EmptyState
          v-else
          emoji="🔍"
          title="未找到相关课程"
          description="试试调整筛选条件或搜索关键词。"
          action-text="重置筛选"
          @action="resetFilters"
        />

        <!-- Pagination -->
        <div v-if="total > pageSize" class="flex justify-center items-center gap-2 mt-10">
          <Button
            variant="secondary"
            size="sm"
            :disabled="currentPage <= 1"
            @click="changePage(currentPage - 1)"
          >
            上一页
          </Button>
          <div class="flex items-center gap-1">
            <button
              v-for="page in visiblePages"
              :key="page"
              type="button"
              class="w-8 h-8 rounded-lg text-sm font-medium transition-colors"
              :class="page === currentPage
                ? 'bg-primary text-white'
                : 'text-text-secondary hover:bg-fill-tertiary'"
              @click="changePage(page)"
            >
              {{ page }}
            </button>
          </div>
          <Button
            variant="secondary"
            size="sm"
            :disabled="currentPage >= totalPages"
            @click="changePage(currentPage + 1)"
          >
            下一页
          </Button>
        </div>
      </main>
    </div>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  PageLayout,
  Button,
  Input,
  Select,
  InsetGroup,
  InsetItem,
  EmptyState,
} from '@/design-system';
import { getCourses } from '@/api/course';
import CourseCard from '@/components/CourseCard.vue';

const router = useRouter();
const route = useRoute();

const selectedCategory = ref<string | null>(null);
const selectedDifficulty = ref<number | null>(null);
const searchKeyword = ref('');
const sortBy = ref('createTime');

const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);

const courses = ref<any[]>([]);
const loading = ref(false);

const categories = [
  { label: '后端开发', value: 'BACKEND' },
  { label: '前端开发', value: 'FRONTEND' },
  { label: '数据库', value: 'DATABASE' },
  { label: '人工智能', value: 'AI' },
  { label: '云计算', value: 'CLOUD' },
];

const difficulties = [
  { label: '入门', value: 1 },
  { label: '初级', value: 2 },
  { label: '中级', value: 3 },
  { label: '高级', value: 4 },
];

const sortOptions = [
  { label: '最新发布', value: 'createTime' },
  { label: '最热门', value: 'enrollmentCount' },
  { label: '课时最多', value: 'durationHours' },
];

const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

const visiblePages = computed(() => {
  const pages: number[] = [];
  const start = Math.max(1, currentPage.value - 2);
  const end = Math.min(totalPages.value, start + 4);
  for (let i = start; i <= end; i++) pages.push(i);
  return pages;
});

const loadCourses = async () => {
  loading.value = true;
  try {
    const params: Record<string, any> = {
      page: currentPage.value,
      size: pageSize.value,
      status: 1,
    };

    if (selectedCategory.value) params.category = selectedCategory.value;
    if (selectedDifficulty.value) params.difficulty = selectedDifficulty.value;
    if (searchKeyword.value) params.keyword = searchKeyword.value;

    if (sortBy.value) {
      params.sortBy = sortBy.value;
      params.sortOrder = 'desc';
    }

    const res = await getCourses(params);
    courses.value = res.data?.records || [];
    total.value = res.data?.total || 0;
  } catch (error) {
    console.error('加载课程失败:', error);
  } finally {
    loading.value = false;
  }
};

let searchTimer: ReturnType<typeof setTimeout> | null = null;
const handleSearch = () => {
  if (searchTimer) clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    currentPage.value = 1;
    loadCourses();
  }, 500);
};

const resetFilters = () => {
  selectedCategory.value = null;
  selectedDifficulty.value = null;
  searchKeyword.value = '';
  sortBy.value = 'createTime';
  currentPage.value = 1;
  loadCourses();
};

const changePage = (page: number) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
  loadCourses();
};

const handleCourseClick = (course: any) => {
  router.push(`/course/${course.id}`);
};

watch([selectedCategory, selectedDifficulty, sortBy], () => {
  currentPage.value = 1;
  loadCourses();
});

onMounted(() => {
  if (route.query.keyword) searchKeyword.value = route.query.keyword as string;
  loadCourses();
});
</script>
