<template>
  <PageLayout max-width="xl">
    <!-- Hero Section -->
    <section class="relative overflow-hidden rounded-2xl border border-border-color bg-bg-secondary p-8 md:p-10 mb-8">
      <div class="flex flex-col lg:flex-row lg:items-end justify-between gap-8">
        <div class="max-w-2xl">
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <svg class="w-4 h-4 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="8" r="6" />
              <path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11" />
            </svg>
            我的成就
          </p>
          <h1 class="mt-3 text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">
            记录里程碑，见证成长
          </h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">
            用积分与徽章把学习过程可视化，保持节奏，持续进步。
          </p>

          <div class="mt-6 flex flex-wrap items-center gap-3">
            <span class="px-3 py-1.5 text-sm rounded-lg bg-bg-tertiary text-text-secondary">
              等级 {{ currentLevel }} · {{ levelTitle }}
            </span>
            <span class="px-3 py-1.5 text-sm rounded-lg bg-bg-tertiary text-text-secondary">
              总积分 {{ totalPoints }}
            </span>
            <span v-if="pointsToNextLevel > 0" class="px-3 py-1.5 text-sm rounded-lg bg-bg-tertiary text-text-secondary">
              距下一级 {{ pointsToNextLevel }} 分
            </span>
          </div>

          <!-- 等级进度条 -->
          <div class="mt-5">
            <div class="flex items-center justify-between text-xs text-text-muted mb-2">
              <span>等级进度</span>
              <span class="font-medium text-text-primary">{{ levelProgress }}%</span>
            </div>
            <div class="h-2 rounded-full bg-bg-tertiary overflow-hidden">
              <div
                class="h-full bg-primary rounded-full transition-all duration-slow"
                :style="{ width: levelProgress + '%' }"
              />
            </div>
          </div>
        </div>

        <!-- 统计卡片 -->
        <div class="grid grid-cols-3 gap-3 w-full lg:w-auto">
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5 text-center">
            <p class="text-xs text-text-muted">已获得</p>
            <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ earnedCount }}</p>
          </div>
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5 text-center">
            <p class="text-xs text-text-muted">总成就</p>
            <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ totalCount }}</p>
          </div>
          <div class="bg-bg-tertiary rounded-xl border border-border-color p-5 text-center">
            <p class="text-xs text-text-muted">完成率</p>
            <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ completionRate }}%</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类标签 -->
    <div class="flex items-center gap-2 mb-6 overflow-x-auto pb-2">
      <button
        v-for="cat in categories"
        :key="cat.value"
        type="button"
        class="inline-flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-all whitespace-nowrap"
        :class="activeCategory === cat.value
          ? 'bg-primary text-white'
          : 'bg-bg-tertiary text-text-secondary hover:bg-bg-hover'"
        @click="activeCategory = cat.value"
      >
        <component :is="cat.icon" class="w-4 h-4" />
        {{ cat.label }}
      </button>
    </div>

    <!-- 成就列表 -->
    <div v-if="filteredAchievements.length" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
      <button
        v-for="achievement in filteredAchievements"
        :key="achievement.id"
        type="button"
        class="bg-bg-secondary rounded-xl border p-6 text-left transition-all hover:shadow-md"
        :class="achievement.earned ? 'border-success/30' : 'border-border-color opacity-90'"
        @click="openDetail(achievement)"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex items-start gap-4 min-w-0">
            <div
              class="w-12 h-12 rounded-2xl flex items-center justify-center border"
              :class="achievement.earned
                ? 'bg-success/10 border-success/20 text-success'
                : 'bg-bg-tertiary border-border-color text-text-muted'"
            >
              <component :is="getCategoryIcon(achievement.category)" class="w-6 h-6" />
            </div>
            <div class="min-w-0">
              <h3 class="text-base font-semibold text-text-primary truncate">{{ achievement.name }}</h3>
              <p class="mt-1 text-sm text-text-secondary line-clamp-2">{{ achievement.description }}</p>
            </div>
          </div>

          <div
            v-if="achievement.earned"
            class="flex-shrink-0 w-8 h-8 rounded-full bg-success/10 border border-success/20 flex items-center justify-center"
          >
            <svg class="w-5 h-5 text-success" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
              <polyline points="22 4 12 14.01 9 11.01" />
            </svg>
          </div>
        </div>

        <div class="mt-4 flex items-center justify-between gap-4 text-sm">
          <span class="px-2 py-1 rounded-md bg-bg-tertiary text-text-secondary">
            +{{ achievement.points || 0 }} 分
          </span>
          <span v-if="achievement.earnedAt" class="text-text-muted">
            {{ formatDate(achievement.earnedAt) }}
          </span>
        </div>

        <!-- 进度条（未获得时显示） -->
        <div v-if="!achievement.earned" class="mt-4">
          <div class="flex items-center justify-between text-xs text-text-muted mb-2">
            <span>进度</span>
            <span class="font-medium text-text-primary">
              {{ achievement.currentProgress || 0 }} / {{ achievement.conditionValue || 0 }}
            </span>
          </div>
          <div class="h-2 rounded-full bg-bg-tertiary overflow-hidden">
            <div
              class="h-full bg-primary rounded-full transition-all duration-slow"
              :style="{ width: getProgressPercent(achievement) + '%' }"
            />
          </div>
        </div>
      </button>
    </div>

    <!-- 空状态 -->
    <EmptyState
      v-else
      emoji="🏆"
      title="暂无成就数据"
      description="稍后再来看看，学习过程中会不断解锁新成就。"
    />

    <!-- 成就详情弹窗 -->
    <Modal
      v-model="detailVisible"
      :title="selectedAchievement?.name || '成就详情'"
      :show-footer="true"
      @confirm="detailVisible = false"
    >
      <template #header>
        <div class="flex items-center gap-3">
          <div
            class="w-10 h-10 rounded-2xl flex items-center justify-center border"
            :class="selectedAchievement?.earned
              ? 'bg-success/10 border-success/20 text-success'
              : 'bg-bg-tertiary border-border-color text-text-muted'"
          >
            <component :is="getCategoryIcon(selectedAchievement?.category)" class="w-5 h-5" />
          </div>
          <div class="min-w-0">
            <p class="text-sm font-semibold text-text-primary truncate">
              {{ selectedAchievement?.name || '成就详情' }}
            </p>
            <p class="text-xs text-text-muted truncate">
              {{ selectedAchievement?.category || '—' }}
            </p>
          </div>
        </div>
      </template>

      <div class="space-y-4">
        <p class="text-sm text-text-secondary leading-relaxed">
          {{ selectedAchievement?.description || '—' }}
        </p>

        <div class="flex flex-wrap items-center gap-2">
          <span class="px-2 py-1 rounded-md bg-bg-tertiary text-text-secondary text-sm">
            +{{ selectedAchievement?.points || 0 }} 分
          </span>
          <span class="px-2 py-1 rounded-md bg-bg-tertiary text-sm"
            :class="selectedAchievement?.earned ? 'text-success' : 'text-text-muted'"
          >
            {{ selectedAchievement?.earned ? '已获得' : '未获得' }}
          </span>
        </div>

        <!-- 进度条（未获得时显示） -->
        <div v-if="selectedAchievement && !selectedAchievement.earned">
          <div class="flex items-center justify-between text-xs text-text-muted mb-2">
            <span>进度</span>
            <span class="font-medium text-text-primary">
              {{ selectedAchievement.currentProgress || 0 }} / {{ selectedAchievement.conditionValue || 0 }}
            </span>
          </div>
          <div class="h-2 rounded-full bg-bg-tertiary overflow-hidden">
            <div
              class="h-full bg-primary rounded-full transition-all duration-slow"
              :style="{ width: getProgressPercent(selectedAchievement) + '%' }"
            />
          </div>
        </div>

        <div v-else-if="selectedAchievement?.earnedAt" class="text-sm text-text-muted">
          获得时间：{{ formatDate(selectedAchievement.earnedAt) }}
        </div>
      </div>

      <template #footer>
        <Button variant="secondary" @click="detailVisible = false">关闭</Button>
      </template>
    </Modal>
  </PageLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, type Component } from 'vue';
import { getAllAchievements, getAchievementPoints } from '@/api/learning';
import { PageLayout, EmptyState, Modal, Button } from '@/design-system';

// 图标组件
const IconGrid = {
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" />
    <rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" />
  </svg>`,
};

const IconFlame = {
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z" />
  </svg>`,
};

const IconBook = {
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
  </svg>`,
};

const IconClock = {
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <circle cx="12" cy="12" r="10" /><polyline points="12 6 12 12 16 14" />
  </svg>`,
};

const IconAward = {
  template: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <circle cx="12" cy="8" r="6" /><path d="M15.477 12.89L17 22l-5-3-5 3 1.523-9.11" />
  </svg>`,
};

// 类型定义
interface Achievement {
  id: number;
  name: string;
  description: string;
  category: string;
  points: number;
  earned: boolean;
  earnedAt?: string;
  currentProgress?: number;
  conditionValue?: number;
  progressPercent?: number;
}

interface Category {
  value: string;
  label: string;
  icon: Component;
}

// 状态
const achievements = ref<Achievement[]>([]);
const totalPoints = ref(0);
const activeCategory = ref('all');
const selectedAchievement = ref<Achievement | null>(null);
const detailVisible = ref(false);

// 分类配置
const categories: Category[] = [
  { value: 'all', label: '全部', icon: IconGrid },
  { value: 'streak', label: '连续学习', icon: IconFlame },
  { value: 'course', label: '课程完成', icon: IconBook },
  { value: 'general', label: '学习时长', icon: IconClock },
];

// 等级阈值
const levelThresholds = [
  { level: 1, points: 0, title: '初学者' },
  { level: 2, points: 100, title: '学徒' },
  { level: 3, points: 300, title: '熟练者' },
  { level: 4, points: 600, title: '专家' },
  { level: 5, points: 1000, title: '大师' },
  { level: 6, points: 1500, title: '宗师' },
  { level: 7, points: 2500, title: '传奇' },
];

// 计算属性
const earnedCount = computed(() => achievements.value.filter((a) => a.earned).length);
const totalCount = computed(() => achievements.value.length);
const completionRate = computed(() =>
  totalCount.value ? Math.round((earnedCount.value / totalCount.value) * 100) : 0
);

const filteredAchievements = computed(() => {
  if (activeCategory.value === 'all') return achievements.value;
  return achievements.value.filter((a) => a.category === activeCategory.value);
});

const currentLevel = computed(() => {
  for (let i = levelThresholds.length - 1; i >= 0; i--) {
    if (totalPoints.value >= levelThresholds[i].points) return levelThresholds[i].level;
  }
  return 1;
});

const levelTitle = computed(() =>
  levelThresholds.find((l) => l.level === currentLevel.value)?.title || '初学者'
);

const pointsToNextLevel = computed(() => {
  const nextLevel = levelThresholds.find((l) => l.level === currentLevel.value + 1);
  if (!nextLevel) return 0;
  return Math.max(0, nextLevel.points - totalPoints.value);
});

const levelProgress = computed(() => {
  const currentLevelData = levelThresholds.find((l) => l.level === currentLevel.value);
  const nextLevelData = levelThresholds.find((l) => l.level === currentLevel.value + 1);
  if (!nextLevelData || !currentLevelData) return 100;
  const progress = totalPoints.value - currentLevelData.points;
  const total = nextLevelData.points - currentLevelData.points;
  return Math.max(0, Math.min(100, Math.round((progress / total) * 100)));
});

// 方法
const getCategoryIcon = (category?: string): Component => {
  const map: Record<string, Component> = {
    streak: IconFlame,
    course: IconBook,
    general: IconClock,
  };
  return map[category || ''] || IconAward;
};

const getProgressPercent = (achievement: Achievement | null): number => {
  if (!achievement) return 0;
  if (achievement.progressPercent != null) return achievement.progressPercent;
  const total = Number(achievement.conditionValue || 0);
  const current = Number(achievement.currentProgress || 0);
  if (!total) return 0;
  return Math.max(0, Math.min(100, Math.round((current / total) * 100)));
};

const formatDate = (dateStr?: string): string => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

const openDetail = (achievement: Achievement) => {
  selectedAchievement.value = achievement;
  detailVisible.value = true;
};

// 数据加载
const loadAchievements = async () => {
  try {
    const res = await getAllAchievements();
    if (res.code === 200) achievements.value = res.data || [];
  } catch (error) {
    console.error('加载成就失败:', error);
  }
};

const loadPoints = async () => {
  try {
    const res = await getAchievementPoints();
    if (res.code === 200) totalPoints.value = res.data || 0;
  } catch (error) {
    console.error('加载积分失败:', error);
  }
};

onMounted(() => {
  loadAchievements();
  loadPoints();
});
</script>
