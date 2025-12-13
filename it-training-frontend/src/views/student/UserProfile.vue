<template>
  <div class="user-profile-container">
    <!-- 用户基本信息卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="80" :src="profile.avatar || defaultAvatar">
              {{ profile.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
            <div class="profile-info">
              <h2>{{ profile.realName || profile.username }}</h2>
              <el-tag :type="getLevelTagType(profile.learningLevel)">
                {{ profile.levelName }}
              </el-tag>
            </div>
          </div>
          <el-divider />
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">{{ profile.totalStudyMinutes || 0 }}</div>
              <div class="stat-label">学习时长(分钟)</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ profile.completedCourses || 0 }}</div>
              <div class="stat-label">完成课程</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ profile.achievementCount || 0 }}</div>
              <div class="stat-label">获得成就</div>
            </div>
          </div>
          <el-divider />
          <div class="streak-info">
            <el-icon><Calendar /></el-icon>
            <span>连续打卡 <strong>{{ profile.currentStreak || 0 }}</strong> 天</span>
            <span class="max-streak">（最长 {{ profile.maxStreak || 0 }} 天）</span>
          </div>
          <div class="points-info">
            <el-icon><Trophy /></el-icon>
            <span>成就积分 <strong>{{ profile.achievementPoints || 0 }}</strong></span>
          </div>
        </el-card>
      </el-col>

      <!-- 能力雷达图 -->
      <el-col :span="8">
        <el-card class="radar-card">
          <template #header>
            <div class="card-header">
              <span>学习能力评估</span>
              <el-tag v-if="assessment.learnerType" type="success">
                {{ assessment.learnerType }}
              </el-tag>
            </div>
          </template>
          <div ref="radarChart" class="chart-container"></div>
          <div class="overall-score" v-if="assessment.overallScore">
            综合评分: <strong>{{ assessment.overallScore }}</strong>/100
          </div>
        </el-card>
      </el-col>

      <!-- 技能标签 -->
      <el-col :span="8">
        <el-card class="skills-card">
          <template #header>
            <div class="card-header">
              <span>技能标签</span>
              <el-button type="primary" link @click="showSkillDialog = true">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
            </div>
          </template>
          <div class="skills-container">
            <el-tag
              v-for="skill in profile.skillTags"
              :key="skill.tag"
              :type="getSkillTagType(skill.level)"
              class="skill-tag"
            >
              {{ skill.tag }}
              <el-rate
                v-model="skill.level"
                disabled
                :max="5"
                size="small"
                class="skill-rate"
              />
            </el-tag>
            <el-empty v-if="!profile.skillTags?.length" description="暂无技能标签" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习偏好和时间分布 -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card class="preference-card">
          <template #header>
            <div class="card-header">
              <span>学习偏好</span>
              <el-button type="primary" link @click="showPreferenceDialog = true">
                <el-icon><Setting /></el-icon> 设置
              </el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="偏好类别">
              <el-tag
                v-for="cat in profile.preference?.preferredCategories"
                :key="cat"
                class="mr-5"
              >
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
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="time-distribution-card">
          <template #header>
            <span>学习时间分布</span>
          </template>
          <div ref="timeChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习建议和里程碑 -->
    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card class="suggestions-card">
          <template #header>
            <span>学习建议</span>
          </template>
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
          <el-empty v-if="!assessment.suggestions?.length" description="暂无建议" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="milestones-card">
          <template #header>
            <span>学习里程碑</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="milestone in profile.milestones"
              :key="milestone.title"
              :timestamp="formatDate(milestone.achievedAt)"
              placement="top"
            >
              <el-card shadow="hover" class="milestone-item">
                <div class="milestone-content">
                  <span class="milestone-icon">{{ milestone.icon || '🏆' }}</span>
                  <div>
                    <h4>{{ milestone.title }}</h4>
                    <p>{{ milestone.description }}</p>
                  </div>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-if="!profile.milestones?.length" description="暂无里程碑" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑技能标签对话框 -->
    <el-dialog v-model="showSkillDialog" title="编辑技能标签" width="500px">
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
            <el-option
              v-for="skill in availableSkills"
              :key="skill"
              :label="skill"
              :value="skill"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSkillDialog = false">取消</el-button>
        <el-button type="primary" @click="saveSkills" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 编辑学习偏好对话框 -->
    <el-dialog v-model="showPreferenceDialog" title="设置学习偏好" width="500px">
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
          <el-slider
            v-model="editPreference.dailyStudyGoal"
            :min="15"
            :max="180"
            :step="15"
            show-input
          />
          <span class="goal-hint">分钟/天</span>
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
import { ref, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Trophy, Edit, Setting } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import {
  getUserProfile,
  updateSkillTags,
  updatePreferences,
  getLearningAbilityAssessment
} from '@/api/learning'

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
  dailyStudyGoal: 30
})

const availableSkills = [
  'Java', 'Python', 'JavaScript', 'TypeScript', 'Vue.js', 'React',
  'Spring Boot', 'MySQL', 'Redis', 'Docker', 'Kubernetes', 'Git',
  'Linux', 'AWS', 'Node.js', 'Go', 'Rust', 'C++', 'HTML/CSS'
]

const radarChart = ref(null)
const timeChart = ref(null)
let radarChartInstance = null
let timeChartInstance = null

// 获取用户画像
const fetchProfile = async () => {
  loading.value = true
  try {
    const res = await getUserProfile()
    profile.value = res.data || {}
    editSkills.value = profile.value.skillTags?.map(s => s.tag) || []
    if (profile.value.preference) {
      editPreference.value = {
        preferredCategories: profile.value.preference.preferredCategories || [],
        preferredDifficulty: profile.value.preference.preferredDifficulty || '中级',
        dailyStudyGoal: profile.value.preference.dailyStudyGoal || 30
      }
    }
  } catch (error) {
    console.error('获取用户画像失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取能力评估
const fetchAssessment = async () => {
  try {
    const res = await getLearningAbilityAssessment()
    assessment.value = res.data || {}
    nextTick(() => {
      initRadarChart()
    })
  } catch (error) {
    console.error('获取能力评估失败:', error)
  }
}

// 初始化雷达图
const initRadarChart = () => {
  if (!radarChart.value) return
  
  if (radarChartInstance) {
    radarChartInstance.dispose()
  }
  
  radarChartInstance = echarts.init(radarChart.value)
  
  const radar = assessment.value.radar || {}
  const option = {
    tooltip: {},
    radar: {
      indicator: [
        { name: '学习速度', max: 100 },
        { name: '坚持度', max: 100 },
        { name: '理解力', max: 100 },
        { name: '实践能力', max: 100 },
        { name: '知识广度', max: 100 },
        { name: '知识深度', max: 100 }
      ],
      shape: 'polygon',
      splitNumber: 5,
      axisName: {
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: ['#e5e5e5']
        }
      },
      splitArea: {
        show: true,
        areaStyle: {
          color: ['rgba(64, 158, 255, 0.1)', 'rgba(64, 158, 255, 0.2)']
        }
      }
    },
    series: [{
      type: 'radar',
      data: [{
        value: [
          radar.learningSpeed || 50,
          radar.persistence || 50,
          radar.comprehension || 50,
          radar.practiceAbility || 50,
          radar.breadth || 50,
          radar.depth || 50
        ],
        name: '能力值',
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.4)'
        },
        lineStyle: {
          color: '#409EFF'
        },
        itemStyle: {
          color: '#409EFF'
        }
      }]
    }]
  }
  
  radarChartInstance.setOption(option)
}

// 初始化时间分布图
const initTimeChart = () => {
  if (!timeChart.value) return
  
  if (timeChartInstance) {
    timeChartInstance.dispose()
  }
  
  timeChartInstance = echarts.init(timeChart.value)
  
  const timeData = profile.value.timeDistribution || []
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: timeData.map(t => t.timeSlot),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '学习时长(分钟)'
    },
    series: [{
      type: 'bar',
      data: timeData.map(t => t.minutes),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409EFF' },
          { offset: 1, color: '#67C23A' }
        ])
      }
    }]
  }
  
  timeChartInstance.setOption(option)
}

// 保存技能标签
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

// 保存学习偏好
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

// 获取等级标签类型
const getLevelTagType = (level) => {
  if (level >= 7) return 'danger'
  if (level >= 5) return 'warning'
  if (level >= 3) return 'success'
  return 'info'
}

// 获取技能标签类型
const getSkillTagType = (level) => {
  if (level >= 4) return 'danger'
  if (level >= 3) return 'warning'
  if (level >= 2) return 'success'
  return 'info'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 监听窗口大小变化
const handleResize = () => {
  radarChartInstance?.resize()
  timeChartInstance?.resize()
}

onMounted(() => {
  fetchProfile()
  fetchAssessment()
  window.addEventListener('resize', handleResize)
})

watch(() => profile.value.timeDistribution, () => {
  nextTick(() => {
    initTimeChart()
  })
}, { deep: true })
</script>

<style scoped>
.user-profile-container {
  padding: 20px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.profile-info h2 {
  margin: 10px 0 5px;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.streak-info,
.points-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0;
  justify-content: center;
}

.max-streak {
  color: #999;
  font-size: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 250px;
}

.overall-score {
  text-align: center;
  margin-top: 10px;
  font-size: 16px;
}

.overall-score strong {
  color: #409EFF;
  font-size: 24px;
}

.skills-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 5px 10px;
}

.skill-rate {
  margin-left: 5px;
}

.mt-20 {
  margin-top: 20px;
}

.mr-5 {
  margin-right: 5px;
}

.milestone-item {
  padding: 10px;
}

.milestone-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.milestone-icon {
  font-size: 24px;
}

.milestone-content h4 {
  margin: 0;
}

.milestone-content p {
  margin: 5px 0 0;
  color: #666;
  font-size: 12px;
}

.goal-hint {
  margin-left: 10px;
  color: #999;
}
</style>