<template>
  <div class="learning-plan-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1>
          <el-icon><Calendar /></el-icon>
          学习计划
        </h1>
        <p>制定学习目标，规划学习路径，高效提升技能</p>
      </div>
      <el-button type="primary" @click="showCreateDialog = true" :icon="Plus">
        创建新计划
      </el-button>
    </div>

    <!-- 当前进行中的计划 -->
    <div v-if="activePlan" class="active-plan-section">
      <div class="section-title">
        <el-icon><Flag /></el-icon>
        <span>当前计划</span>
      </div>
      <div class="active-plan-card">
        <div class="plan-header">
          <div class="plan-info">
            <h2>{{ activePlan.planName }}</h2>
            <p class="plan-description">{{ activePlan.description || '暂无描述' }}</p>
          </div>
          <el-dropdown @command="handlePlanAction">
            <el-button type="primary" text>
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">编辑计划</el-dropdown-item>
                <el-dropdown-item command="pause">暂停计划</el-dropdown-item>
                <el-dropdown-item command="complete">完成计划</el-dropdown-item>
                <el-dropdown-item command="cancel" divided>取消计划</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
        
        <div class="plan-progress">
          <div class="progress-stats">
            <div class="stat-item">
              <span class="stat-value">{{ activePlan.progressPercent || 0 }}%</span>
              <span class="stat-label">完成进度</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ activePlan.completedCourses || 0 }}/{{ activePlan.totalCourses || 0 }}</span>
              <span class="stat-label">课程完成</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ activePlan.remainingDays || 0 }}</span>
              <span class="stat-label">剩余天数</span>
            </div>
          </div>
          <el-progress 
            :percentage="activePlan.progressPercent || 0" 
            :stroke-width="12"
            :color="progressColors"
          />
        </div>

        <div class="plan-dates">
          <div class="date-item">
            <el-icon><Calendar /></el-icon>
            <span>开始: {{ formatDate(activePlan.startDate) }}</span>
          </div>
          <div class="date-item">
            <el-icon><Timer /></el-icon>
            <span>结束: {{ formatDate(activePlan.endDate) }}</span>
          </div>
          <div class="date-item">
            <el-icon><Clock /></el-icon>
            <span>每日目标: {{ activePlan.dailyTargetMinutes || 0 }}分钟</span>
          </div>
        </div>

        <!-- 目标课程列表 -->
        <div v-if="activePlan.targetCourses && activePlan.targetCourses.length > 0" class="target-courses">
          <h3>目标课程</h3>
          <div class="course-list">
            <div 
              v-for="course in activePlan.targetCourses" 
              :key="course.courseId"
              class="course-item"
              :class="{ completed: course.completed }"
            >
              <div class="course-info">
                <el-icon v-if="course.completed" class="check-icon"><CircleCheck /></el-icon>
                <el-icon v-else class="pending-icon"><Clock /></el-icon>
                <span class="course-name">{{ course.courseName }}</span>
                <el-tag size="small" type="info">{{ course.category }}</el-tag>
              </div>
              <el-progress 
                :percentage="course.progressPercent || 0" 
                :stroke-width="6"
                :show-text="false"
                style="width: 100px"
              />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 无进行中计划提示 -->
    <div v-else class="no-active-plan">
      <el-empty description="暂无进行中的学习计划">
        <el-button type="primary" @click="showCreateDialog = true">
          创建学习计划
        </el-button>
      </el-empty>
    </div>

    <!-- 历史计划 -->
    <div class="history-section">
      <div class="section-title">
        <el-icon><Document /></el-icon>
        <span>历史计划</span>
      </div>
      
      <div v-if="historyPlans.length > 0" class="history-list">
        <div 
          v-for="plan in historyPlans" 
          :key="plan.id"
          class="history-card"
          :class="plan.status"
        >
          <div class="history-header">
            <h3>{{ plan.planName }}</h3>
            <el-tag :type="getStatusType(plan.status)" size="small">
              {{ getStatusText(plan.status) }}
            </el-tag>
          </div>
          <div class="history-info">
            <span>{{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}</span>
            <span>完成度: {{ plan.progressPercent || 0 }}%</span>
          </div>
          <div class="history-actions" v-if="plan.status === 'paused'">
            <el-button size="small" type="primary" @click="resumePlan(plan.id)">
              恢复计划
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无历史计划" />
    </div>

    <!-- 创建/编辑计划对话框 -->
    <el-dialog 
      v-model="showCreateDialog" 
      :title="editingPlan ? '编辑学习计划' : '创建学习计划'"
      width="600px"
      @close="resetForm"
    >
      <el-form 
        ref="planFormRef"
        :model="planForm" 
        :rules="planRules"
        label-width="100px"
      >
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="planForm.planName" placeholder="请输入计划名称" />
        </el-form-item>
        
        <el-form-item label="计划描述" prop="description">
          <el-input 
            v-model="planForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入计划描述（可选）" 
          />
        </el-form-item>
        
        <el-form-item label="时间范围" prop="dateRange">
          <el-date-picker
            v-model="planForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="每日目标" prop="dailyTargetMinutes">
          <el-input-number 
            v-model="planForm.dailyTargetMinutes" 
            :min="15" 
            :max="480"
            :step="15"
          />
          <span class="unit-text">分钟/天</span>
        </el-form-item>
        
        <el-form-item label="目标课程" prop="targetCourseIds">
          <el-select 
            v-model="planForm.targetCourseIds" 
            multiple 
            filterable
            placeholder="选择要学习的课程"
            style="width: 100%"
          >
            <el-option
              v-for="course in availableCourses"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            >
              <span>{{ course.name }}</span>
              <el-tag size="small" style="margin-left: 8px">{{ course.category }}</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitPlan" :loading="submitting">
          {{ editingPlan ? '保存修改' : '创建计划' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Calendar, Plus, Flag, MoreFilled, Timer, Clock, 
  CircleCheck, Document 
} from '@element-plus/icons-vue'
import { 
  getUserPlans, getActivePlan, createPlan, updatePlan,
  pausePlan as pausePlanApi, resumePlan as resumePlanApi, 
  cancelPlan, completePlan 
} from '@/api/learning'
import { getCourseList } from '@/api/course'

// 状态
const loading = ref(false)
const submitting = ref(false)
const showCreateDialog = ref(false)
const editingPlan = ref(null)
const activePlan = ref(null)
const allPlans = ref([])
const availableCourses = ref([])
const planFormRef = ref(null)

// 表单数据
const planForm = reactive({
  planName: '',
  description: '',
  dateRange: [],
  dailyTargetMinutes: 60,
  targetCourseIds: []
})

// 表单验证规则
const planRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' },
    { min: 2, max: 50, message: '计划名称长度在2-50个字符', trigger: 'blur' }
  ],
  dateRange: [
    { required: true, message: '请选择时间范围', trigger: 'change' }
  ],
  dailyTargetMinutes: [
    { required: true, message: '请设置每日目标', trigger: 'change' }
  ]
}

// 进度条颜色
const progressColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 }
]

// 计算历史计划
const historyPlans = computed(() => {
  return allPlans.value.filter(p => p.status !== 'active')
})

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    active: 'success',
    completed: 'info',
    paused: 'warning',
    canceled: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    active: '进行中',
    completed: '已完成',
    paused: '已暂停',
    canceled: '已取消'
  }
  return texts[status] || status
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const [plansRes, activeRes, coursesRes] = await Promise.all([
      getUserPlans(),
      getActivePlan(),
      getCourseList({ page: 1, size: 100 })
    ])
    
    allPlans.value = plansRes.data || []
    activePlan.value = activeRes.data
    availableCourses.value = coursesRes.data?.records || coursesRes.data || []
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

// 处理计划操作
const handlePlanAction = async (command) => {
  if (!activePlan.value) return
  
  const planId = activePlan.value.id
  
  switch (command) {
    case 'edit':
      editingPlan.value = activePlan.value
      planForm.planName = activePlan.value.planName
      planForm.description = activePlan.value.description
      planForm.dateRange = [
        new Date(activePlan.value.startDate),
        new Date(activePlan.value.endDate)
      ]
      planForm.dailyTargetMinutes = activePlan.value.dailyTargetMinutes
      planForm.targetCourseIds = activePlan.value.targetCourses?.map(c => c.courseId) || []
      showCreateDialog.value = true
      break
      
    case 'pause':
      await ElMessageBox.confirm('确定要暂停当前学习计划吗？', '暂停计划')
      await pausePlanApi(planId)
      ElMessage.success('计划已暂停')
      loadData()
      break
      
    case 'complete':
      await ElMessageBox.confirm('确定要标记当前计划为已完成吗？', '完成计划')
      await completePlan(planId)
      ElMessage.success('恭喜完成学习计划！')
      loadData()
      break
      
    case 'cancel':
      await ElMessageBox.confirm('确定要取消当前学习计划吗？此操作不可恢复。', '取消计划', {
        type: 'warning'
      })
      await cancelPlan(planId)
      ElMessage.success('计划已取消')
      loadData()
      break
  }
}

// 恢复计划
const resumePlan = async (planId) => {
  try {
    await resumePlanApi(planId)
    ElMessage.success('计划已恢复')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '恢复失败')
  }
}

// 提交计划
const submitPlan = async () => {
  if (!planFormRef.value) return
  
  await planFormRef.value.validate()
  
  submitting.value = true
  try {
    const data = {
      planName: planForm.planName,
      description: planForm.description,
      startDate: formatDate(planForm.dateRange[0]),
      endDate: formatDate(planForm.dateRange[1]),
      dailyTargetMinutes: planForm.dailyTargetMinutes,
      targetCourseIds: planForm.targetCourseIds
    }
    
    if (editingPlan.value) {
      await updatePlan(editingPlan.value.id, data)
      ElMessage.success('计划已更新')
    } else {
      await createPlan(data)
      ElMessage.success('计划创建成功')
    }
    
    showCreateDialog.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  editingPlan.value = null
  planForm.planName = ''
  planForm.description = ''
  planForm.dateRange = []
  planForm.dailyTargetMinutes = 60
  planForm.targetCourseIds = []
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.learning-plan-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
}

.header-content h1 {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 28px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}

.header-content p {
  color: var(--el-text-color-secondary);
  margin: 0;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin-bottom: 16px;
}

/* 当前计划卡片 */
.active-plan-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  color: white;
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.plan-info h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.plan-description {
  opacity: 0.9;
  margin: 0;
}

.plan-progress {
  margin-bottom: 24px;
}

.progress-stats {
  display: flex;
  gap: 48px;
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.plan-dates {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.date-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
}

/* 目标课程 */
.target-courses h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 12px 0;
  opacity: 0.9;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  padding: 12px 16px;
}

.course-item.completed {
  background: rgba(255, 255, 255, 0.25);
}

.course-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.check-icon {
  color: #67c23a;
}

.pending-icon {
  opacity: 0.7;
}

.course-name {
  font-weight: 500;
}

/* 无计划提示 */
.no-active-plan {
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: 48px;
  text-align: center;
  margin-bottom: 32px;
}

/* 历史计划 */
.history-section {
  margin-top: 32px;
}

.history-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.history-card {
  background: var(--el-bg-color);
  border-radius: 12px;
  padding: 16px;
  border: 1px solid var(--el-border-color-light);
}

.history-card.completed {
  border-left: 4px solid var(--el-color-success);
}

.history-card.paused {
  border-left: 4px solid var(--el-color-warning);
}

.history-card.canceled {
  border-left: 4px solid var(--el-color-danger);
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.history-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.history-info {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.history-actions {
  margin-top: 12px;
  text-align: right;
}

/* 表单样式 */
.unit-text {
  margin-left: 8px;
  color: var(--el-text-color-secondary);
}

/* 深色模式适配 */
:deep(.el-progress__text) {
  color: white !important;
}

.active-plan-card :deep(.el-progress-bar__outer) {
  background: rgba(255, 255, 255, 0.3);
}

.active-plan-card :deep(.el-progress-bar__inner) {
  background: white;
}

.active-plan-card :deep(.el-button) {
  color: white;
}

.active-plan-card :deep(.el-tag) {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
}
</style>