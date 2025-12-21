<template>
  <div class="page">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-10 text-center">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative">
        <div
          class="mx-auto w-16 h-16 rounded-3xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary"
        >
          <Sparkles class="w-8 h-8" />
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
          <button type="button" class="btn btn-secondary" :disabled="loading || !learningGoal" @click="learningGoal = ''">
            清空
          </button>
          <button
            type="button"
            class="btn btn-primary"
            :disabled="loading || !learningGoal.trim() || learningGoal.trim().length < 10"
            @click="handleSubmit"
          >
            <Loader2 v-if="loading" class="w-4 h-4 mr-2 animate-spin" />
            {{ loading ? 'AI 分析中...' : '获取推荐' }}
          </button>
        </div>
      </div>

      <el-form @submit.prevent="handleSubmit">
        <el-form-item>
          <el-input
            v-model="learningGoal"
            type="textarea"
            :rows="6"
            placeholder="例如：我想学习 Java 后端开发，未来从事 Web 开发工作；我想从零开始转前端；我有 Python 基础，想深入学习 AI。"
            :maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <div class="mt-6 flex flex-wrap items-center gap-2">
        <span class="text-xs text-text-muted mr-1">快速示例</span>
        <button
          v-for="(example, index) in examples"
          :key="index"
          type="button"
          class="badge badge-secondary hover:bg-primary/10 hover:text-primary transition-colors"
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
        class="glass rounded-2xl border border-border-color/60 p-4 flex items-start gap-3 text-sm"
      >
        <Info class="w-4 h-4 text-text-muted mt-0.5" />
        <div class="text-text-secondary">
          {{ recommendation.fallbackMessage || 'AI 服务暂时不可用，已为你推荐热门课程。' }}
        </div>
      </div>

      <div class="card p-6 md:p-8">
        <div class="flex items-start justify-between gap-6">
          <div class="flex items-start gap-4">
            <div class="w-12 h-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
              <Brain class="w-6 h-6" />
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

          <span class="badge badge-secondary">
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
            class="card-hover p-5 cursor-pointer"
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
                  <span class="badge badge-secondary">{{ getCategoryName(course.category) }}</span>
                  <span class="badge" :class="getDifficultyBadge(course.difficulty)">
                    {{ course.difficultyName || '难度' }}
                  </span>
                </div>

                <p class="text-sm text-text-secondary leading-relaxed">
                  {{ course.reason || '—' }}
                </p>

                <div v-if="course.tags" class="mt-3 flex flex-wrap gap-2">
                  <span v-for="tag in course.tags.split(',')" :key="tag" class="badge badge-secondary">
                    {{ tag.trim() }}
                  </span>
                </div>
              </div>

              <button type="button" class="btn btn-ghost flex-shrink-0" @click.stop="goToCourse(course.courseId)">
                查看详情
                <ArrowRight class="w-4 h-4 ml-2" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <EmptyState
      v-else-if="!loading"
      :icon="Sparkles"
      title="开始你的学习之旅"
      description="输入学习目标，让 AI 为你量身定制学习路径。"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight, Brain, Info, Loader2, Sparkles } from 'lucide-vue-next'
import { getAiRecommendation } from '@/api/ai'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()

const learningGoal = ref('')
const loading = ref(false)
const recommendation = ref(null)

const examples = ['我想学习 Java 后端开发', '我想转行做前端工程师', '我想学习人工智能']

const handleSubmit = async () => {
  if (!learningGoal.value.trim() || learningGoal.value.trim().length < 10) {
    ElMessage.warning('请输入至少 10 个字符的学习目标描述')
    return
  }

  loading.value = true
  recommendation.value = null

  try {
    const res = await getAiRecommendation(learningGoal.value.trim())
    recommendation.value = res.data

    if (res.data?.fallback) {
      ElMessage.info('已为你推荐热门课程')
    } else {
      ElMessage.success('AI 推荐获取成功')
    }
  } catch (error) {
    console.error('获取推荐失败:', error)
    const errorMsg = error.response?.data?.message || '获取推荐失败，请稍后重试'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const getCategoryName = (category) => {
  const categoryMap = {
    BACKEND: '后端开发',
    FRONTEND: '前端开发',
    DATABASE: '数据库',
    AI: '人工智能',
    CLOUD: '云计算',
    OTHER: '其他',
  }
  return categoryMap[category] || category
}

const getDifficultyBadge = (difficulty) => {
  const map = {
    1: 'bg-success/10 text-success border border-success/20',
    2: 'bg-info/10 text-info border border-info/20',
    3: 'bg-warning/10 text-warning border border-warning/20',
    4: 'bg-error/10 text-error border border-error/20',
  }
  return map[difficulty] || 'bg-bg-tertiary text-text-secondary border border-border-color'
}

const goToCourse = (courseId) => {
  router.push(`/course/${courseId}`)
}
</script>
