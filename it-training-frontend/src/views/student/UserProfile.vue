<template>
  <div class="page" v-loading="loading">
    <!-- Hero -->
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col md:flex-row md:items-center justify-between gap-8">
        <div class="flex items-center gap-4 min-w-0">
          <el-avatar :size="72" :src="profile.avatar || defaultAvatar" class="flex-shrink-0">
            {{ profile.username?.charAt(0)?.toUpperCase() }}
          </el-avatar>
          <div class="min-w-0">
            <h1 class="text-2xl md:text-4xl font-semibold tracking-tight text-text-primary truncate">
              {{ profile.realName || profile.username || '—' }}
            </h1>
            <div class="mt-2 flex flex-wrap items-center gap-2">
              <el-tag v-if="profile.levelName" :type="getLevelTagType(profile.learningLevel)">
                {{ profile.levelName }}
              </el-tag>
              <span v-if="profile.username" class="text-sm text-text-muted truncate">@{{ profile.username }}</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-3 gap-3 w-full md:w-auto">
          <div class="card p-4 text-center">
            <p class="text-xs text-text-muted">学习时长</p>
            <p class="mt-1 text-lg font-semibold text-text-primary">{{ profile.totalStudyMinutes || 0 }}</p>
            <p class="text-xs text-text-muted">分钟</p>
          </div>
          <div class="card p-4 text-center">
            <p class="text-xs text-text-muted">完成课程</p>
            <p class="mt-1 text-lg font-semibold text-text-primary">{{ profile.completedCourses || 0 }}</p>
            <p class="text-xs text-text-muted">门</p>
          </div>
          <div class="card p-4 text-center">
            <p class="text-xs text-text-muted">获得成就</p>
            <p class="mt-1 text-lg font-semibold text-text-primary">{{ profile.achievementCount || 0 }}</p>
            <p class="text-xs text-text-muted">个</p>
          </div>
        </div>
      </div>
    </section>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Streak -->
      <section class="card p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习状态</h2>
        <div class="inset-group">
          <div class="inset-item">
            <div class="flex items-center gap-2 text-sm text-text-secondary">
              <el-icon><Calendar /></el-icon>
              <span>连续打卡</span>
            </div>
            <div class="text-sm text-text-primary font-medium">
              {{ profile.currentStreak || 0 }} 天
              <span class="text-text-muted font-normal">（最长 {{ profile.maxStreak || 0 }} 天）</span>
            </div>
          </div>
          <div class="inset-divider"></div>
          <div class="inset-item">
            <div class="flex items-center gap-2 text-sm text-text-secondary">
              <el-icon><Trophy /></el-icon>
              <span>成就积分</span>
            </div>
            <div class="text-sm text-text-primary font-medium">{{ profile.achievementPoints || 0 }}</div>
          </div>
        </div>
      </section>

      <!-- Radar -->
      <section class="card p-6">
        <div class="flex items-center justify-between gap-4 mb-4">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习能力评估</h2>
          <el-tag v-if="assessment.learnerType" type="success">{{ assessment.learnerType }}</el-tag>
        </div>
        <div ref="radarChart" class="h-64 w-full"></div>
        <p v-if="assessment.overallScore" class="mt-3 text-sm text-text-secondary">
          综合评分：<span class="font-semibold text-text-primary">{{ assessment.overallScore }}</span>/100
        </p>
      </section>

      <!-- Skills -->
      <section class="card p-6">
        <div class="flex items-center justify-between gap-4 mb-4">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary">技能标签</h2>
          <el-button type="primary" link @click="showSkillDialog = true">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
        </div>
        <div class="flex flex-wrap gap-2">
          <el-tag
            v-for="skill in profile.skillTags"
            :key="skill.tag"
            :type="getSkillTagType(skill.level)"
            class="!inline-flex items-center gap-2"
          >
            {{ skill.tag }}
            <el-rate v-model="skill.level" disabled :max="5" size="small" />
          </el-tag>
          <el-empty v-if="!profile.skillTags?.length" description="暂无技能标签" :image-size="80" />
        </div>
      </section>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Preference -->
      <section class="card p-6">
        <div class="flex items-center justify-between gap-4 mb-4">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习偏好</h2>
          <el-button type="primary" link @click="showPreferenceDialog = true">
            <el-icon><Setting /></el-icon>
            设置
          </el-button>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="偏好类别">
            <el-tag v-for="cat in profile.preference?.preferredCategories" :key="cat" class="mr-2">
              {{ cat }}
            </el-tag>
            <span v-if="!profile.preference?.preferredCategories?.length">未设置</span>
          </el-descriptions-item>
          <el-descriptions-item label="偏好难度">
            {{ profile.preference?.preferredDifficulty || '未设置' }}
          </el-descriptions-item>
          <el-descriptions-item label="每日学习目标">
            {{ profile.preference?.dailyStudyGoal || 30 }} 分钟
          </el-descriptions-item>
          <el-descriptions-item label="偏好学习时间">
            {{ profile.preference?.preferredStudyTime || '未设置' }}
          </el-descriptions-item>
        </el-descriptions>
      </section>

      <!-- Time -->
      <section class="card p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习时间分布</h2>
        <div ref="timeChart" class="h-64 w-full"></div>
      </section>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- Suggestions -->
      <section class="card p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习建议</h2>
        <el-timeline>
          <el-timeline-item
            v-for="(suggestion, index) in assessment.suggestions"
            :key="index"
            :type="index === 0 ? 'primary' : 'info'"
            :hollow="index !== 0"
          >
            {{ suggestion }}
          </el-timeline-item>
        </el-timeline>
        <el-empty v-if="!assessment.suggestions?.length" description="暂无建议" :image-size="80" />
      </section>

      <!-- Milestones -->
      <section class="card p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习里程碑</h2>
        <el-timeline>
          <el-timeline-item
            v-for="milestone in profile.milestones"
            :key="milestone.title"
            :timestamp="formatDate(milestone.achievedAt)"
            placement="top"
          >
            <div class="card p-4">
              <div class="flex items-start gap-3">
                <div class="w-10 h-10 rounded-2xl bg-bg-tertiary/70 border border-border-color/60 flex items-center justify-center">
                  <span class="text-text-primary">{{ milestone.icon || '•' }}</span>
                </div>
                <div class="min-w-0">
                  <h4 class="font-semibold text-text-primary">{{ milestone.title }}</h4>
                  <p class="mt-1 text-sm text-text-secondary">{{ milestone.description }}</p>
                </div>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-if="!profile.milestones?.length" description="暂无里程碑" :image-size="80" />
      </section>
    </div>

    <!-- 编辑技能标签对话框 -->
    <el-dialog v-model="showSkillDialog" title="编辑技能标签" width="520px">
      <el-form>
        <el-form-item label="技能标签">
          <el-select
            v-model="editSkills"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入技能标签"
            style="width: 100%"
          >
            <el-option v-for="skill in availableSkills" :key="skill" :label="skill" :value="skill" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSkillDialog = false">取消</el-button>
        <el-button type="primary" @click="saveSkills" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑学习偏好对话框 -->
    <el-dialog v-model="showPreferenceDialog" title="设置学习偏好" width="520px">
      <el-form :model="editPreference" label-width="100px">
        <el-form-item label="偏好类别">
          <el-checkbox-group v-model="editPreference.preferredCategories">
            <el-checkbox label="PROGRAMMING">编程开发</el-checkbox>
            <el-checkbox label="DATABASE">数据库</el-checkbox>
            <el-checkbox label="FRONTEND">前端开发</el-checkbox>
            <el-checkbox label="BACKEND">后端开发</el-checkbox>
            <el-checkbox label="DEVOPS">运维部署</el-checkbox>
            <el-checkbox label="AI">人工智能</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="偏好难度">
          <el-select v-model="editPreference.preferredDifficulty" style="width: 100%">
            <el-option label="入门" value="入门" />
            <el-option label="初级" value="初级" />
            <el-option label="中级" value="中级" />
            <el-option label="高级" value="高级" />
          </el-select>
        </el-form-item>
        <el-form-item label="每日目标">
          <el-slider v-model="editPreference.dailyStudyGoal" :min="15" :max="180" :step="15" show-input />
          <span class="ml-2 text-xs text-text-muted">分钟/天</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPreferenceDialog = false">取消</el-button>
        <el-button type="primary" @click="savePreferences" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Trophy, Edit, Setting } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getUserProfile, updateSkillTags, updatePreferences, getLearningAbilityAssessment } from '@/api/learning'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const profile = ref({})
const assessment = ref({})
const loading = ref(false)
const saving = ref(false)

const showSkillDialog = ref(false)
const showPreferenceDialog = ref(false)
const editSkills = ref([])
const editPreference = ref({
  preferredCategories: [],
  preferredDifficulty: '中级',
  dailyStudyGoal: 30,
})

const availableSkills = [
  'Java',
  'Python',
  'JavaScript',
  'TypeScript',
  'Vue.js',
  'React',
  'Spring Boot',
  'MySQL',
  'Redis',
  'Docker',
  'Kubernetes',
  'Git',
  'Linux',
  'AWS',
  'Node.js',
  'Go',
  'Rust',
  'C++',
  'HTML/CSS',
]

const radarChart = ref(null)
const timeChart = ref(null)
let radarChartInstance = null
let timeChartInstance = null
let themeObserver = null

const normalizeRgb = (value, fallback) => {
  const cleaned = (value || '').trim()
  if (!cleaned) return fallback
  return cleaned.replace(/\s+/g, ' ')
}

const rgba = (rgb, alpha) => `rgba(${rgb.replace(/\s+/g, ',')}, ${alpha})`

const getThemeColors = () => {
  const style = getComputedStyle(document.documentElement)
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '37 99 235')
  const textSecondaryRgb = normalizeRgb(style.getPropertyValue('--text-secondary-rgb'), '75 85 99')
  const borderRgb = normalizeRgb(style.getPropertyValue('--border-color-rgb'), '229 231 235')
  const bgSecondaryRgb = normalizeRgb(style.getPropertyValue('--bg-secondary-rgb'), '255 255 255')
  return { primaryRgb, textSecondaryRgb, borderRgb, bgSecondaryRgb }
}

const fetchProfile = async () => {
  loading.value = true
  try {
    const res = await getUserProfile()
    profile.value = res.data || {}
    editSkills.value = profile.value.skillTags?.map((s) => s.tag) || []
    if (profile.value.preference) {
      editPreference.value = {
        preferredCategories: profile.value.preference.preferredCategories || [],
        preferredDifficulty: profile.value.preference.preferredDifficulty || '中级',
        dailyStudyGoal: profile.value.preference.dailyStudyGoal || 30,
      }
    }
  } catch (error) {
    console.error('获取用户画像失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchAssessment = async () => {
  try {
    const res = await getLearningAbilityAssessment()
    assessment.value = res.data || {}
    nextTick(() => initRadarChart())
  } catch (error) {
    console.error('获取能力评估失败:', error)
  }
}

const initRadarChart = () => {
  if (!radarChart.value) return

  radarChartInstance?.dispose()
  radarChartInstance = echarts.init(radarChart.value)

  const { primaryRgb, textSecondaryRgb, borderRgb, bgSecondaryRgb } = getThemeColors()
  const radar = assessment.value.radar || {}

  radarChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {},
    radar: {
      indicator: [
        { name: '学习速度', max: 100 },
        { name: '坚持度', max: 100 },
        { name: '理解力', max: 100 },
        { name: '实践能力', max: 100 },
        { name: '知识广度', max: 100 },
        { name: '知识深度', max: 100 },
      ],
      shape: 'polygon',
      splitNumber: 5,
      axisName: {
        color: rgba(textSecondaryRgb, 0.9),
      },
      splitLine: {
        lineStyle: {
          color: [rgba(borderRgb, 0.9)],
        },
      },
      axisLine: {
        lineStyle: {
          color: rgba(borderRgb, 0.7),
        },
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: [rgba(primaryRgb, 0.05), rgba(bgSecondaryRgb, 0.0)],
        },
      },
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: [
              radar.learningSpeed || 50,
              radar.persistence || 50,
              radar.comprehension || 50,
              radar.practiceAbility || 50,
              radar.breadth || 50,
              radar.depth || 50,
            ],
            name: '能力值',
            areaStyle: { color: rgba(primaryRgb, 0.22) },
            lineStyle: { color: rgba(primaryRgb, 0.85), width: 2 },
            itemStyle: { color: rgba(primaryRgb, 0.95) },
          },
        ],
      },
    ],
  })
}

const initTimeChart = () => {
  if (!timeChart.value) return

  timeChartInstance?.dispose()
  timeChartInstance = echarts.init(timeChart.value)

  const { primaryRgb, textSecondaryRgb, borderRgb } = getThemeColors()
  const timeData = profile.value.timeDistribution || []

  timeChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 32, right: 16, top: 18, bottom: 46, containLabel: true },
    xAxis: {
      type: 'category',
      data: timeData.map((t) => t.timeSlot),
      axisLabel: { rotate: 35, color: rgba(textSecondaryRgb, 0.9) },
      axisLine: { lineStyle: { color: rgba(borderRgb, 0.8) } },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      nameTextStyle: { color: rgba(textSecondaryRgb, 0.7) },
      axisLabel: { color: rgba(textSecondaryRgb, 0.85) },
      splitLine: { lineStyle: { color: rgba(borderRgb, 0.6) } },
    },
    series: [
      {
        type: 'bar',
        data: timeData.map((t) => t.minutes),
        barWidth: 18,
        itemStyle: {
          borderRadius: [10, 10, 6, 6],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: rgba(primaryRgb, 0.9) },
            { offset: 1, color: rgba(primaryRgb, 0.18) },
          ]),
        },
      },
    ],
  })
}

const saveSkills = async () => {
  saving.value = true
  try {
    await updateSkillTags(editSkills.value)
    ElMessage.success('技能标签已更新')
    showSkillDialog.value = false
    fetchProfile()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const savePreferences = async () => {
  saving.value = true
  try {
    await updatePreferences(editPreference.value)
    ElMessage.success('学习偏好已更新')
    showPreferenceDialog.value = false
    fetchProfile()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const getLevelTagType = (level) => {
  if (level >= 7) return 'danger'
  if (level >= 5) return 'warning'
  if (level >= 3) return 'success'
  return 'info'
}

const getSkillTagType = (level) => {
  if (level >= 4) return 'danger'
  if (level >= 3) return 'warning'
  if (level >= 2) return 'success'
  return 'info'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const handleResize = () => {
  radarChartInstance?.resize()
  timeChartInstance?.resize()
}

onMounted(() => {
  fetchProfile()
  fetchAssessment()
  window.addEventListener('resize', handleResize)

  themeObserver = new MutationObserver(() => {
    nextTick(() => {
      initRadarChart()
      initTimeChart()
    })
  })
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  themeObserver?.disconnect()
  radarChartInstance?.dispose()
  timeChartInstance?.dispose()
})

watch(
  () => profile.value.timeDistribution,
  () => {
    nextTick(() => initTimeChart())
  },
  { deep: true }
)
</script>
