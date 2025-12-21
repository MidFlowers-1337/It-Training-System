<template>
  <div class="page">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col lg:flex-row lg:items-end justify-between gap-8">
        <div class="max-w-2xl">
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <Award class="w-4 h-4 text-primary" />
            我的成就
          </p>
          <h1 class="mt-3 text-3xl md:text-5xl font-semibold tracking-tight text-text-primary">记录里程碑，见证成长</h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">
            用积分与徽章把学习过程可视化，保持节奏，持续进步。
          </p>

          <div class="mt-6 flex flex-wrap items-center gap-3">
            <span class="badge badge-secondary">等级 {{ currentLevel }} · {{ levelTitle }}</span>
            <span class="badge badge-secondary">总积分 {{ totalPoints }}</span>
            <span v-if="pointsToNextLevel > 0" class="badge badge-secondary">距下一级 {{ pointsToNextLevel }} 分</span>
          </div>

          <div class="mt-5">
            <div class="flex items-center justify-between text-xs text-text-muted mb-2">
              <span>等级进度</span>
              <span class="font-medium text-text-primary">{{ levelProgress }}%</span>
            </div>
            <div class="h-2 rounded-full bg-bg-tertiary/70 overflow-hidden">
              <div class="h-full bg-primary/80 rounded-full transition-all duration-500" :style="{ width: levelProgress + '%' }"></div>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-3 gap-3 w-full lg:w-auto">
          <div class="card p-5 text-center">
            <p class="text-xs text-text-muted">已获得</p>
            <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ earnedCount }}</p>
          </div>
          <div class="card p-5 text-center">
            <p class="text-xs text-text-muted">总成就</p>
            <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ totalCount }}</p>
          </div>
          <div class="card p-5 text-center">
            <p class="text-xs text-text-muted">完成率</p>
            <p class="mt-1 text-2xl font-semibold tracking-tight text-text-primary">{{ completionRate }}%</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Tabs -->
    <div class="flex items-center justify-between gap-6">
      <div class="segmented">
        <button
          v-for="cat in categories"
          :key="cat.value"
          type="button"
          class="segmented-item"
          :class="{ 'is-active': activeCategory === cat.value }"
          @click="activeCategory = cat.value"
        >
          <component :is="cat.icon" class="w-4 h-4 mr-2 inline-block" />
          {{ cat.label }}
        </button>
      </div>
    </div>

    <!-- Grid -->
    <div v-if="filteredAchievements.length" class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
      <button
        v-for="achievement in filteredAchievements"
        :key="achievement.id"
        type="button"
        class="card-hover p-6 text-left"
        :class="achievement.earned ? 'border-success/30' : 'opacity-90'"
        @click="openDetail(achievement)"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="flex items-start gap-4 min-w-0">
            <div
              class="w-12 h-12 rounded-2xl flex items-center justify-center border"
              :class="achievement.earned ? 'bg-success/10 border-success/20 text-success' : 'bg-bg-tertiary/60 border-border-color/60 text-text-muted'"
            >
              <component :is="getCategoryIcon(achievement.category)" class="w-6 h-6" />
            </div>
            <div class="min-w-0">
              <h3 class="text-base font-semibold text-text-primary truncate">{{ achievement.name }}</h3>
              <p class="mt-1 text-sm text-text-secondary line-clamp-2">{{ achievement.description }}</p>
            </div>
          </div>

          <div v-if="achievement.earned" class="flex-shrink-0 w-8 h-8 rounded-full bg-success/10 border border-success/20 flex items-center justify-center">
            <CheckCircle2 class="w-5 h-5 text-success" />
          </div>
        </div>

        <div class="mt-4 flex items-center justify-between gap-4 text-sm">
          <span class="badge badge-secondary">+{{ achievement.points || 0 }} 分</span>
          <span v-if="achievement.earnedAt" class="text-text-muted">{{ formatDate(achievement.earnedAt) }}</span>
        </div>

        <div v-if="!achievement.earned" class="mt-4">
          <div class="flex items-center justify-between text-xs text-text-muted mb-2">
            <span>进度</span>
            <span class="font-medium text-text-primary">
              {{ achievement.currentProgress || 0 }} / {{ achievement.conditionValue || 0 }}
            </span>
          </div>
          <div class="h-2 rounded-full bg-bg-tertiary/70 overflow-hidden">
            <div
              class="h-full bg-primary/80 rounded-full transition-all duration-500"
              :style="{ width: getProgressPercent(achievement) + '%' }"
            ></div>
          </div>
        </div>
      </button>
    </div>

    <EmptyState v-else :icon="Award" title="暂无成就数据" description="稍后再来看看，学习过程中会不断解锁新成就。" />

    <!-- Detail -->
    <el-dialog v-model="detailVisible" width="520px" :show-close="true">
      <template #header>
        <div class="flex items-center gap-3">
          <div
            class="w-10 h-10 rounded-2xl flex items-center justify-center border"
            :class="
              selectedAchievement?.earned
                ? 'bg-success/10 border-success/20 text-success'
                : 'bg-bg-tertiary/60 border-border-color/60 text-text-muted'
            "
          >
            <component :is="getCategoryIcon(selectedAchievement?.category)" class="w-5 h-5" />
          </div>
          <div class="min-w-0">
            <p class="text-sm font-semibold text-text-primary truncate">{{ selectedAchievement?.name || '成就详情' }}</p>
            <p class="text-xs text-text-muted truncate">{{ selectedAchievement?.category || '—' }}</p>
          </div>
        </div>
      </template>

      <div class="space-y-4">
        <p class="text-sm text-text-secondary leading-relaxed">{{ selectedAchievement?.description || '—' }}</p>

        <div class="flex flex-wrap items-center gap-2">
          <span class="badge badge-secondary">+{{ selectedAchievement?.points || 0 }} 分</span>
          <span v-if="selectedAchievement?.earned" class="badge badge-secondary">已获得</span>
          <span v-else class="badge badge-secondary">未获得</span>
        </div>

        <div v-if="selectedAchievement && !selectedAchievement.earned">
          <div class="flex items-center justify-between text-xs text-text-muted mb-2">
            <span>进度</span>
            <span class="font-medium text-text-primary">
              {{ selectedAchievement.currentProgress || 0 }} / {{ selectedAchievement.conditionValue || 0 }}
            </span>
          </div>
          <div class="h-2 rounded-full bg-bg-tertiary/70 overflow-hidden">
            <div
              class="h-full bg-primary/80 rounded-full transition-all duration-500"
              :style="{ width: getProgressPercent(selectedAchievement) + '%' }"
            ></div>
          </div>
        </div>

        <div v-else-if="selectedAchievement?.earnedAt" class="text-sm text-text-muted">
          获得时间：{{ formatDate(selectedAchievement.earnedAt) }}
        </div>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Award, BookOpenCheck, CheckCircle2, Clock, Flame, Grid3X3 } from 'lucide-vue-next'
import { getAllAchievements, getAchievementPoints } from '@/api/learning'
import EmptyState from '@/components/EmptyState.vue'

const achievements = ref([])
const totalPoints = ref(0)
const activeCategory = ref('all')
const selectedAchievement = ref(null)
const detailVisible = ref(false)

const categories = [
  { value: 'all', label: '全部', icon: Grid3X3 },
  { value: 'streak', label: '连续学习', icon: Flame },
  { value: 'course', label: '课程完成', icon: BookOpenCheck },
  { value: 'general', label: '学习时长', icon: Clock },
]

const levelThresholds = [
  { level: 1, points: 0, title: '初学者' },
  { level: 2, points: 100, title: '学徒' },
  { level: 3, points: 300, title: '熟练者' },
  { level: 4, points: 600, title: '专家' },
  { level: 5, points: 1000, title: '大师' },
  { level: 6, points: 1500, title: '宗师' },
  { level: 7, points: 2500, title: '传奇' },
]

const earnedCount = computed(() => achievements.value.filter((a) => a.earned).length)
const totalCount = computed(() => achievements.value.length)
const completionRate = computed(() => (totalCount.value ? Math.round((earnedCount.value / totalCount.value) * 100) : 0))

const filteredAchievements = computed(() => {
  if (activeCategory.value === 'all') return achievements.value
  return achievements.value.filter((a) => a.category === activeCategory.value)
})

const currentLevel = computed(() => {
  for (let i = levelThresholds.length - 1; i >= 0; i--) {
    if (totalPoints.value >= levelThresholds[i].points) return levelThresholds[i].level
  }
  return 1
})

const levelTitle = computed(() => levelThresholds.find((l) => l.level === currentLevel.value)?.title || '初学者')

const pointsToNextLevel = computed(() => {
  const nextLevel = levelThresholds.find((l) => l.level === currentLevel.value + 1)
  if (!nextLevel) return 0
  return Math.max(0, nextLevel.points - totalPoints.value)
})

const levelProgress = computed(() => {
  const currentLevelData = levelThresholds.find((l) => l.level === currentLevel.value)
  const nextLevelData = levelThresholds.find((l) => l.level === currentLevel.value + 1)
  if (!nextLevelData) return 100
  const currentLevelPoints = currentLevelData.points
  const nextLevelPoints = nextLevelData.points
  const progress = totalPoints.value - currentLevelPoints
  const total = nextLevelPoints - currentLevelPoints
  return Math.max(0, Math.min(100, Math.round((progress / total) * 100)))
})

const getCategoryIcon = (category) => {
  const map = {
    streak: Flame,
    course: BookOpenCheck,
    general: Clock,
  }
  return map[category] || Award
}

const getProgressPercent = (achievement) => {
  if (!achievement) return 0
  if (achievement.progressPercent != null) return achievement.progressPercent
  const total = Number(achievement.conditionValue || 0)
  const current = Number(achievement.currentProgress || 0)
  if (!total) return 0
  return Math.max(0, Math.min(100, Math.round((current / total) * 100)))
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const openDetail = (achievement) => {
  selectedAchievement.value = achievement
  detailVisible.value = true
}

const loadAchievements = async () => {
  try {
    const res = await getAllAchievements()
    if (res.code === 200) achievements.value = res.data || []
  } catch (error) {
    console.error('加载成就失败:', error)
  }
}

const loadPoints = async () => {
  try {
    const res = await getAchievementPoints()
    if (res.code === 200) totalPoints.value = res.data || 0
  } catch (error) {
    console.error('加载积分失败:', error)
  }
}

onMounted(() => {
  loadAchievements()
  loadPoints()
})
</script>
