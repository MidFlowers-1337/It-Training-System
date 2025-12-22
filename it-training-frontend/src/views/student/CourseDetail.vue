<template>
  <PageLayout>
    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-32">
      <div class="w-8 h-8 border-2 border-primary border-t-transparent rounded-full animate-spin" />
    </div>

    <template v-else-if="course.id">
      <!-- Back Button -->
      <button
        type="button"
        @click="$router.back()"
        class="inline-flex items-center gap-2 text-sm font-medium text-text-secondary hover:text-text-primary transition-colors mb-6"
      >
        <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 18l-6-6 6-6" />
        </svg>
        返回列表
      </button>

      <!-- Hero Section -->
      <section class="rounded-2xl bg-bg-secondary border border-border-color p-6 md:p-8 mb-8">
        <div class="grid grid-cols-1 md:grid-cols-[112px,1fr] gap-6 items-start">
          <!-- Course Icon -->
          <div class="w-28 h-28 rounded-2xl bg-bg-tertiary border border-border-color flex items-center justify-center">
            <component :is="getCategoryIcon(course.category)" class="w-12 h-12 text-primary" />
          </div>

          <div class="min-w-0">
            <!-- Tags -->
            <div class="flex flex-wrap items-center gap-2 mb-3">
              <Tag type="primary">{{ course.categoryName || getCategoryName(course.category) }}</Tag>
              <Tag :type="getDifficultyType(course.difficulty)">{{ course.difficultyName || '难度' }}</Tag>
            </div>

            <!-- Title -->
            <h1 class="text-2xl md:text-4xl font-semibold text-text-primary leading-tight">
              {{ course.name }}
            </h1>

            <!-- Description -->
            <p class="mt-3 text-text-secondary leading-relaxed max-w-3xl">
              {{ course.description }}
            </p>

            <!-- Meta Info -->
            <div class="mt-4 flex flex-wrap gap-5 text-sm text-text-secondary">
              <div class="inline-flex items-center gap-2">
                <svg class="w-4 h-4 text-text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" />
                  <path d="M12 6v6l4 2" />
                </svg>
                <span>{{ course.durationHours }} 课时</span>
              </div>
              <div class="inline-flex items-center gap-2">
                <svg class="w-4 h-4 text-text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                  <circle cx="9" cy="7" r="4" />
                  <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
                  <path d="M16 3.13a4 4 0 0 1 0 7.75" />
                </svg>
                <span>{{ course.enrollmentCount || 0 }} 人已报名</span>
              </div>
              <div class="inline-flex items-center gap-2">
                <svg class="w-4 h-4 text-text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                <span>{{ course.instructorName || '讲师' }}</span>
              </div>
            </div>

            <!-- Skill Tags -->
            <div v-if="skillTags.length" class="mt-4 flex flex-wrap gap-2">
              <Tag v-for="tag in skillTags" :key="tag" type="default">{{ tag }}</Tag>
            </div>
          </div>
        </div>
      </section>

      <!-- Content Grid -->
      <div class="grid grid-cols-1 lg:grid-cols-[1fr,360px] gap-8 items-start">
        <!-- Left: Course Content -->
        <Section title="课程大纲" compact>
          <div v-if="course.content" class="prose prose-sm max-w-none text-text-secondary" v-html="course.content" />
          <EmptyState v-else emoji="📄" title="暂无课程大纲" description="课程大纲正在完善中。" size="sm" />
        </Section>

        <!-- Right: Sidebar -->
        <aside class="rounded-2xl bg-bg-secondary border border-border-color p-5 sticky top-24">
          <h3 class="text-base font-semibold text-text-primary">选择班期报名</h3>
          <p class="mt-1 text-sm text-text-muted">选择合适的开课时间与学习安排。</p>

          <EmptyState
            v-if="sessions.length === 0"
            emoji="📅"
            title="暂无可报名班期"
            description="可以稍后再来查看。"
            size="sm"
          />

          <div v-else class="mt-4 space-y-3">
            <div
              v-for="session in sessions"
              :key="session.id"
              class="rounded-xl bg-bg-primary border border-border-color p-4"
              :class="session.remainingQuota > 0 ? '' : 'opacity-60'"
            >
              <div class="flex items-start justify-between gap-3">
                <div class="min-w-0">
                  <p class="font-semibold text-text-primary truncate">{{ session.sessionCode }}</p>
                  <div class="mt-2 space-y-1 text-sm text-text-secondary">
                    <div class="flex items-center gap-2">
                      <svg class="w-4 h-4 text-text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                        <line x1="16" y1="2" x2="16" y2="6" />
                        <line x1="8" y1="2" x2="8" y2="6" />
                        <line x1="3" y1="10" x2="21" y2="10" />
                      </svg>
                      <span>{{ session.startDate }} 开课</span>
                    </div>
                    <div v-if="session.schedule" class="flex items-center gap-2">
                      <svg class="w-4 h-4 text-text-muted" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10" />
                        <path d="M12 6v6l4 2" />
                      </svg>
                      <span>{{ session.schedule }}</span>
                    </div>
                  </div>
                </div>

                <span
                  class="text-xs font-medium flex-shrink-0"
                  :class="session.remainingQuota < 10 ? 'text-warning' : 'text-success'"
                >
                  剩 {{ session.remainingQuota }} 名额
                </span>
              </div>

              <Button
                class="w-full mt-4"
                variant="primary"
                :disabled="session.remainingQuota <= 0"
                @click="openEnrollModal(session)"
              >
                {{ session.remainingQuota > 0 ? '立即报名' : '已满员' }}
              </Button>
            </div>
          </div>
        </aside>
      </div>
    </template>

    <!-- Not Found -->
    <EmptyState
      v-else
      emoji="⚠️"
      title="课程不存在"
      description="该课程可能已被删除或不存在。"
      action-text="返回课程列表"
      @action="$router.push('/courses')"
    />

    <!-- Enroll Confirm Modal -->
    <Modal
      v-model="enrollModalVisible"
      title="确认报名"
      confirm-text="确认报名"
      :confirm-loading="enrolling"
      @confirm="handleEnroll"
      @cancel="enrollModalVisible = false"
    >
      <div v-if="selectedSession" class="space-y-3">
        <p class="text-text-primary">确定要报名「{{ selectedSession.sessionCode }}」班期吗？</p>
        <div class="text-sm text-text-secondary space-y-1">
          <p>开班日期: {{ selectedSession.startDate }}</p>
          <p>上课时间: {{ selectedSession.schedule || '待定' }}</p>
        </div>
      </div>
    </Modal>

    <!-- Toast -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="showToast"
          class="fixed top-20 left-1/2 -translate-x-1/2 z-50 px-4 py-2 rounded-lg shadow-lg text-sm font-medium"
          :class="toastType === 'success' ? 'bg-success text-white' : 'bg-error text-white'"
        >
          {{ toastMessage }}
        </div>
      </Transition>
    </Teleport>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, markRaw, type Component } from 'vue';
import { useRoute } from 'vue-router';
import { PageLayout, Section, Button, Tag, Modal, EmptyState } from '@/design-system';
import { getCourseById } from '@/api/course';
import { getEnrollableSessions } from '@/api/session';
import { enroll } from '@/api/enrollment';

// Types
interface Course {
  id?: number;
  name?: string;
  description?: string;
  content?: string;
  category?: string;
  categoryName?: string;
  difficulty?: number;
  difficultyName?: string;
  durationHours?: number;
  enrollmentCount?: number;
  instructorName?: string;
  tags?: string;
}

interface Session {
  id: number;
  sessionCode: string;
  startDate: string;
  schedule?: string;
  remainingQuota: number;
}

const route = useRoute();

// State
const course = ref<Course>({});
const sessions = ref<Session[]>([]);
const loading = ref(false);
const enrollModalVisible = ref(false);
const selectedSession = ref<Session | null>(null);
const enrolling = ref(false);
const toastMessage = ref('');
const toastType = ref<'success' | 'error'>('success');
const showToast = ref(false);

// Computed
const skillTags = computed(() => {
  if (!course.value.tags) return [];
  return course.value.tags.split(',').filter((t) => t.trim());
});

// Category Icons (inline SVG components)
const IconServer = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="8" rx="2" ry="2"/><rect x="2" y="14" width="20" height="8" rx="2" ry="2"/><line x1="6" y1="6" x2="6.01" y2="6"/><line x1="6" y1="18" x2="6.01" y2="18"/></svg>`,
});

const IconLayout = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><line x1="3" y1="9" x2="21" y2="9"/><line x1="9" y1="21" x2="9" y2="9"/></svg>`,
});

const IconDatabase = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M21 12c0 1.66-4 3-9 3s-9-1.34-9-3"/><path d="M3 5v14c0 1.66 4 3 9 3s9-1.34 9-3V5"/></svg>`,
});

const IconBrain = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9.5 2A2.5 2.5 0 0 1 12 4.5v15a2.5 2.5 0 0 1-4.96.44 2.5 2.5 0 0 1-2.96-3.08 3 3 0 0 1-.34-5.58 2.5 2.5 0 0 1 1.32-4.24 2.5 2.5 0 0 1 4.44-1.54"/><path d="M14.5 2A2.5 2.5 0 0 0 12 4.5v15a2.5 2.5 0 0 0 4.96.44 2.5 2.5 0 0 0 2.96-3.08 3 3 0 0 0 .34-5.58 2.5 2.5 0 0 0-1.32-4.24 2.5 2.5 0 0 0-4.44-1.54"/></svg>`,
});

const IconCloud = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 10h-1.26A8 8 0 1 0 9 20h9a5 5 0 0 0 0-10z"/></svg>`,
});

const IconCode = markRaw({
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>`,
});

const categoryIcons: Record<string, Component> = {
  BACKEND: IconServer,
  FRONTEND: IconLayout,
  DATABASE: IconDatabase,
  AI: IconBrain,
  CLOUD: IconCloud,
};

const getCategoryIcon = (category?: string): Component => {
  return categoryIcons[category || ''] || IconCode;
};

const getCategoryName = (category?: string): string => {
  const names: Record<string, string> = {
    BACKEND: '后端开发',
    FRONTEND: '前端开发',
    DATABASE: '数据库',
    AI: '人工智能',
    CLOUD: '云计算',
  };
  return names[category || ''] || '课程';
};

const getDifficultyType = (difficulty?: number): 'success' | 'info' | 'warning' | 'danger' | 'default' => {
  const map: Record<number, 'success' | 'info' | 'warning' | 'danger'> = {
    1: 'success',
    2: 'info',
    3: 'warning',
    4: 'danger',
  };
  return map[difficulty || 0] || 'default';
};

// Toast helper
const toast = (message: string, type: 'success' | 'error' = 'success') => {
  toastMessage.value = message;
  toastType.value = type;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// API calls
const loadCourse = async () => {
  loading.value = true;
  try {
    const res = await getCourseById(route.params.id as string);
    course.value = res.data;
    await loadSessions();
  } catch (error) {
    console.error('加载课程详情失败:', error);
  } finally {
    loading.value = false;
  }
};

const loadSessions = async () => {
  try {
    const res = await getEnrollableSessions(route.params.id as string);
    sessions.value = res.data || [];
  } catch (error) {
    console.error('加载班期失败:', error);
  }
};

const openEnrollModal = (session: Session) => {
  if (session.remainingQuota <= 0) return;
  selectedSession.value = session;
  enrollModalVisible.value = true;
};

const handleEnroll = async () => {
  if (!selectedSession.value) return;

  enrolling.value = true;
  try {
    await enroll(selectedSession.value.id);
    enrollModalVisible.value = false;
    toast('报名成功！可在"我的课程"中查看', 'success');
    loadSessions();
  } catch (error: any) {
    const errorMsg = error.response?.data?.message || '报名失败，请稍后重试';
    toast(errorMsg, 'error');
    console.error('报名失败:', error);
  } finally {
    enrolling.value = false;
  }
};

onMounted(() => {
  loadCourse();
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
