<template>
  <div class="max-w-4xl mx-auto px-4 md:px-8 py-8 space-y-8">
    <!-- Hero -->
    <section class="relative overflow-hidden rounded-3xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl shadow-sm p-8 md:p-10 text-center">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative">
        <div class="mx-auto w-16 h-16 rounded-3xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
          <!-- Sparkles Icon -->
          <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z" />
          </svg>
        </div>
        <h1 class="mt-6 text-3xl md:text-5xl font-semibold tracking-tight text-text-primary">AI 智能选课助手</h1>
        <p class="mt-3 text-sm md:text-base text-text-secondary">告诉我你的学习目标，我来给你一个更合理的学习顺序。</p>
      </div>
    </section>

    <!-- Input -->
    <section class="card p-6 md:p-8">
      <div class="flex flex-col md:flex-row md:items-end justify-between gap-4 mb-6">
        <div>
          <h2 class="text-xl font-semibold tracking-tight text-text-primary">描述你的学习目标</h2>
          <p class="mt-1 text-sm text-text-muted">描述越具体，推荐越贴近你的实际情况（至少 10 个字）。</p>
        </div>
        <div class="flex items-center gap-3">
          <Button variant="secondary" :disabled="loading || !learningGoal" @click="learningGoal = ''">
            清空
          </Button>
          <Button
            variant="primary"
            :disabled="loading || !learningGoal.trim() || learningGoal.trim().length < 10"
            @click="handleSubmit"
          >
            <template v-if="loading" #icon>
              <svg class="w-4 h-4 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            </template>
            {{ loading ? 'AI 分析中...' : '获取推荐' }}
          </Button>
        </div>
      </div>

      <div class="relative">
        <textarea
          v-model="learningGoal"
          class="w-full min-h-36 px-4 py-3 rounded-2xl bg-bg-tertiary/40 border border-border-color/60 text-text-primary placeholder-text-muted text-sm leading-relaxed focus:outline-none focus:ring-2 focus:ring-primary/15 focus:border-primary/40 transition resize-none"
          placeholder="例如：我想学习 Java 后端开发，未来从事 Web 开发工作；我想从零开始转前端；我有 Python 基础，想深入学习 AI。"
          maxlength="500"
          @submit.prevent="handleSubmit"
        ></textarea>
        <div class="absolute bottom-3 right-3 text-xs text-text-muted">
          {{ learningGoal.length }}/500
        </div>
      </div>

      <div class="mt-6 flex flex-wrap items-center gap-2">
        <span class="text-xs text-text-muted mr-1">快速示例</span>
        <button
          v-for="(example, index) in examples"
          :key="index"
          type="button"
          class="inline-flex items-center px-3 py-1.5 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60 text-text-secondary hover:bg-primary/10 hover:text-primary hover:border-primary/30 transition-colors"
          @click="learningGoal = example"
        >
          {{ example }}
        </button>
      </div>
    </section>

    <!-- Result -->
    <section v-if="recommendation" class="space-y-6">
      <div
        v-if="recommendation.fallback"
        class="rounded-2xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl p-4 flex items-start gap-3 text-sm"
      >
        <!-- Info Icon -->
        <svg class="w-4 h-4 text-text-muted mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <div class="text-text-secondary">
          {{ recommendation.fallbackMessage || 'AI 服务暂时不可用，已为你推荐热门课程。' }}
        </div>
      </div>

      <div class="card p-6 md:p-8">
        <div class="flex items-start justify-between gap-6">
          <div class="flex items-start gap-4">
            <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary flex-shrink-0">
              <!-- Brain Icon -->
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
              </svg>
            </div>
            <div>
              <h3 class="text-lg font-semibold text-text-primary">分析结果</h3>
              <p class="mt-1 text-sm text-text-secondary leading-relaxed">
                {{ recommendation.overallReason || '—' }}
              </p>
              <div v-if="recommendation.learningPath" class="mt-4 text-sm text-text-secondary">
                <p class="font-medium text-text-primary mb-1">学习路径</p>
                <p class="leading-relaxed">{{ recommendation.learningPath }}</p>
              </div>
            </div>
          </div>

          <span class="inline-flex items-center px-3 py-1.5 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60 text-text-secondary flex-shrink-0">
            {{ recommendation.fallback ? '智能匹配' : 'AI 推荐' }}
          </span>
        </div>
      </div>

      <div class="card p-6 md:p-8">
        <div class="flex items-end justify-between gap-6 mb-5">
          <div>
            <h3 class="text-lg font-semibold text-text-primary">推荐学习路径</h3>
            <p class="mt-1 text-sm text-text-muted">
              {{ (recommendation.courses || []).length }} 门课程 · 建议按顺序学习
            </p>
          </div>
        </div>

        <div class="space-y-3">
          <div
            v-for="(course, index) in recommendation.courses || []"
            :key="course.courseId"
            class="rounded-2xl border border-border-color/60 bg-bg-secondary/60 p-5 hover:shadow-md hover:border-primary/30 transition-all cursor-pointer"
            @click="goToCourse(course.courseId)"
          >
            <div class="flex items-start justify-between gap-6">
              <div class="min-w-0">
                <div class="flex items-center gap-3 mb-2">
                  <div class="w-8 h-8 rounded-full bg-bg-tertiary/70 border border-border-color/60 flex items-center justify-center">
                    <span class="text-xs font-semibold text-text-primary">{{ index + 1 }}</span>
                  </div>
                  <h4 class="font-semibold text-text-primary truncate">{{ course.courseName }}</h4>
                </div>

                <div class="flex flex-wrap items-center gap-2 mb-3">
                  <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60 text-text-secondary">
                    {{ getCategoryName(course.category) }}
                  </span>
                  <span
                    class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                    :class="getDifficultyBadge(course.difficulty)"
                  >
                    {{ course.difficultyName || '难度' }}
                  </span>
                </div>

                <p class="text-sm text-text-secondary leading-relaxed">
                  {{ course.reason || '—' }}
                </p>

                <div v-if="course.tags" class="mt-3 flex flex-wrap gap-2">
                  <span
                    v-for="tag in course.tags.split(',')"
                    :key="tag"
                    class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-bg-tertiary/60 border border-border-color/60 text-text-secondary"
                  >
                    {{ tag.trim() }}
                  </span>
                </div>
              </div>

              <Button variant="ghost" class="flex-shrink-0" @click.stop="goToCourse(course.courseId)">
                查看详情
                <template #icon>
                  <!-- ArrowRight Icon -->
                  <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                  </svg>
                </template>
              </Button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <EmptyState
      v-else-if="!loading"
      emoji="✨"
      title="开始你的学习之旅"
      description="输入学习目标，让 AI 为你量身定制学习路径。"
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
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Button, EmptyState } from '@/design-system';
import { getAiRecommendation } from '@/api/ai';

// Types
interface Course {
  courseId: number;
  courseName: string;
  category: string;
  difficulty: number;
  difficultyName?: string;
  reason?: string;
  tags?: string;
}

interface Recommendation {
  fallback?: boolean;
  fallbackMessage?: string;
  overallReason?: string;
  learningPath?: string;
  courses?: Course[];
}

const router = useRouter();

// State
const learningGoal = ref('');
const loading = ref(false);
const recommendation = ref<Recommendation | null>(null);

const examples = ['我想学习 Java 后端开发', '我想转行做前端工程师', '我想学习人工智能'];

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

// Methods
const handleSubmit = async () => {
  if (!learningGoal.value.trim() || learningGoal.value.trim().length < 10) {
    showToast('请输入至少 10 个字符的学习目标描述', 'warning');
    return;
  }

  loading.value = true;
  recommendation.value = null;

  try {
    const res = await getAiRecommendation(learningGoal.value.trim());
    recommendation.value = res.data;

    if (res.data?.fallback) {
      showToast('已为你推荐热门课程', 'info');
    } else {
      showToast('AI 推荐获取成功', 'success');
    }
  } catch (error: any) {
    console.error('获取推荐失败:', error);
    const errorMsg = error.response?.data?.message || '获取推荐失败，请稍后重试';
    showToast(errorMsg, 'error');
  } finally {
    loading.value = false;
  }
};

const getCategoryName = (category: string): string => {
  const categoryMap: Record<string, string> = {
    BACKEND: '后端开发',
    FRONTEND: '前端开发',
    DATABASE: '数据库',
    AI: '人工智能',
    CLOUD: '云计算',
    OTHER: '其他',
  };
  return categoryMap[category] || category;
};

const getDifficultyBadge = (difficulty: number): string => {
  const map: Record<number, string> = {
    1: 'bg-success/10 text-success border border-success/20',
    2: 'bg-info/10 text-info border border-info/20',
    3: 'bg-warning/10 text-warning border border-warning/20',
    4: 'bg-error/10 text-error border border-error/20',
  };
  return map[difficulty] || 'bg-bg-tertiary text-text-secondary border border-border-color';
};

const goToCourse = (courseId: number) => {
  router.push(`/course/${courseId}`);
};
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
