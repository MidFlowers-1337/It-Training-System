<template>
  <div class="page" v-loading="loading">
    <section class="page-hero glass p-7 md:p-10">
      <div class="absolute inset-0 pointer-events-none">
        <div class="absolute -top-24 -right-24 w-72 h-72 bg-primary/15 blur-3xl rounded-full"></div>
        <div class="absolute -bottom-28 -left-28 w-72 h-72 bg-secondary/20 blur-3xl rounded-full"></div>
      </div>

      <div class="relative flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div class="max-w-2xl">
          <p class="text-sm text-text-secondary">学习计划</p>
          <h1 class="mt-2 text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">
            系统化学习，稳步进阶
          </h1>
          <p class="mt-3 text-text-secondary">制定目标，规划路径，把每日投入变成可量化的成长。</p>
        </div>

        <button type="button" class="btn btn-primary gap-2" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          创建计划
        </button>
      </div>
    </section>

    <section v-if="activePlan" class="space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold text-text-primary">当前计划</h2>
        <span class="badge badge-primary">进行中</span>
      </div>

      <div class="card p-6 md:p-8 relative overflow-hidden">
        <div class="absolute top-0 right-0 w-72 h-72 bg-primary/10 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2 pointer-events-none"></div>

        <div class="relative flex flex-col lg:flex-row gap-8 lg:gap-12">
          <div class="flex-1 space-y-8">
            <div class="flex items-start justify-between gap-4">
              <div class="min-w-0">
                <h3 class="text-xl md:text-2xl font-semibold tracking-tight text-text-primary truncate">
                  {{ activePlan.planName }}
                </h3>
                <p class="mt-1 text-sm text-text-secondary">
                  {{ activePlan.description || '暂无描述' }}
                </p>
              </div>

              <el-dropdown @command="handlePlanAction" trigger="click">
                <button type="button" class="btn btn-ghost px-3 py-2 !rounded-full">
                  <el-icon><MoreFilled /></el-icon>
                </button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑计划</el-dropdown-item>
                    <el-dropdown-item command="pause">暂停计划</el-dropdown-item>
                    <el-dropdown-item command="complete">完成计划</el-dropdown-item>
                    <el-dropdown-item command="cancel" divided class="text-error">取消计划</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <div class="rounded-2xl bg-bg-tertiary/60 border border-border-color/60 p-4">
                <div class="text-text-secondary text-xs">课程完成</div>
                <div class="mt-1 text-lg font-semibold text-text-primary">
                  {{ activePlan.completedCourses || 0 }}/{{ activePlan.totalCourses || 0 }}
                </div>
              </div>
              <div class="rounded-2xl bg-bg-tertiary/60 border border-border-color/60 p-4">
                <div class="text-text-secondary text-xs">剩余天数</div>
                <div class="mt-1 text-lg font-semibold text-text-primary">{{ activePlan.remainingDays || 0 }}</div>
              </div>
              <div class="rounded-2xl bg-bg-tertiary/60 border border-border-color/60 p-4">
                <div class="text-text-secondary text-xs">开始日期</div>
                <div class="mt-1 text-base font-semibold text-text-primary">{{ formatDate(activePlan.startDate) }}</div>
              </div>
              <div class="rounded-2xl bg-bg-tertiary/60 border border-border-color/60 p-4">
                <div class="text-text-secondary text-xs">每日目标</div>
                <div class="mt-1 text-base font-semibold text-text-primary">
                  {{ activePlan.dailyTargetMinutes || 0 }} 分钟
                </div>
              </div>
            </div>

            <div v-if="activePlan.targetCourses?.length > 0" class="space-y-3">
              <h4 class="text-sm font-semibold text-text-primary">目标课程</h4>
              <div class="inset-group">
                <template v-for="(course, idx) in activePlan.targetCourses" :key="course.courseId">
                  <div class="inset-item">
                    <div class="flex items-center gap-4 min-w-0 flex-1">
                      <div
                        class="w-9 h-9 rounded-full flex items-center justify-center flex-shrink-0"
                        :class="
                          course.completed
                            ? 'bg-success/15 text-success border border-success/20'
                            : 'bg-bg-tertiary/60 text-text-muted border border-border-color/60'
                        "
                      >
                        <el-icon v-if="course.completed"><Check /></el-icon>
                        <span v-else class="text-xs font-semibold">{{ course.progressPercent || 0 }}%</span>
                      </div>

                      <div class="min-w-0 flex-1">
                        <div class="flex items-center justify-between gap-3">
                          <span class="font-medium text-text-primary truncate">{{ course.courseName }}</span>
                          <span class="text-xs text-text-secondary flex-shrink-0">{{ course.category }}</span>
                        </div>
                        <div class="mt-2 h-1.5 bg-bg-tertiary/70 rounded-full overflow-hidden">
                          <div
                            class="h-full bg-primary transition-all duration-500"
                            :style="{ width: (course.progressPercent || 0) + '%' }"
                          ></div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-if="idx !== activePlan.targetCourses.length - 1" class="inset-divider"></div>
                </template>
              </div>
            </div>
          </div>

          <div class="flex items-center justify-center lg:w-64 flex-shrink-0">
            <div class="glass rounded-3xl p-6">
              <div class="relative w-44 h-44 flex items-center justify-center">
                <svg class="w-full h-full transform -rotate-90">
                  <circle
                    cx="88"
                    cy="88"
                    r="80"
                    stroke="currentColor"
                    stroke-width="12"
                    fill="transparent"
                    class="text-bg-tertiary"
                  />
                  <circle
                    cx="88"
                    cy="88"
                    r="80"
                    stroke="currentColor"
                    stroke-width="12"
                    fill="transparent"
                    :stroke-dasharray="2 * Math.PI * 80"
                    :stroke-dashoffset="2 * Math.PI * 80 - ((activePlan.progressPercent || 0) / 100) * (2 * Math.PI * 80)"
                    class="text-primary transition-all duration-1000 ease-out"
                    stroke-linecap="round"
                  />
                </svg>
                <div class="absolute inset-0 flex flex-col items-center justify-center">
                  <span class="text-3xl font-semibold text-text-primary">{{ activePlan.progressPercent || 0 }}%</span>
                  <span class="text-sm text-text-secondary mt-1">总进度</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section v-else class="card">
      <EmptyState
        :icon="Target"
        title="还没有进行中的计划"
        description="创建一个学习计划，开始系统化学习。"
        action-text="创建学习计划"
        @action="showCreateDialog = true"
      />
    </section>

    <section class="space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold text-text-primary">历史计划</h2>
      </div>

      <div v-if="historyPlans.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="plan in historyPlans" :key="plan.id" class="card-hover p-6">
          <div class="flex justify-between items-start gap-4 mb-4">
            <h3 class="font-semibold text-text-primary truncate">{{ plan.planName }}</h3>
            <span class="px-2 py-1 rounded-full text-xs font-semibold" :class="getStatusClass(plan.status)">
              {{ getStatusText(plan.status) }}
            </span>
          </div>

          <div class="space-y-2 text-sm text-text-secondary mb-6">
            <div class="flex items-center gap-2">
              <el-icon><Calendar /></el-icon>
              <span>{{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}</span>
            </div>
            <div class="flex items-center gap-2">
              <el-icon><Check /></el-icon>
              <span>完成度: {{ plan.progressPercent || 0 }}%</span>
            </div>
          </div>

          <button
            v-if="plan.status === 'paused'"
            type="button"
            class="btn btn-secondary w-full justify-center"
            @click="resumePlan(plan.id)"
          >
            恢复计划
          </button>
        </div>
      </div>

      <div v-else class="card">
        <EmptyState :icon="Clock" title="暂无历史计划" description="完成或暂停的计划会在这里展示，便于回顾你的成长轨迹。" />
      </div>
    </section>

    <el-dialog
      v-model="showCreateDialog"
      :title="editingPlan ? '编辑学习计划' : '创建学习计划'"
      width="600px"
      @close="resetForm"
      class="dark-dialog"
    >
      <el-form ref="planFormRef" :model="planForm" :rules="planRules" label-position="top">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="planForm.planName" placeholder="给你的计划起个名字" />
        </el-form-item>

        <el-form-item label="计划描述" prop="description">
          <el-input v-model="planForm.description" type="textarea" :rows="3" placeholder="描述你的学习目标（可选）" />
        </el-form-item>

        <div class="grid grid-cols-2 gap-4">
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

          <el-form-item label="每日目标 (分钟)" prop="dailyTargetMinutes">
            <el-input-number v-model="planForm.dailyTargetMinutes" :min="15" :max="480" :step="15" style="width: 100%" />
          </el-form-item>
        </div>

        <el-form-item label="选择课程" prop="targetCourseIds">
          <el-select v-model="planForm.targetCourseIds" multiple filterable placeholder="选择要加入计划的课程" style="width: 100%">
            <el-option v-for="course in availableCourses" :key="course.id" :label="course.name" :value="course.id">
              <div class="flex items-center justify-between">
                <span>{{ course.name }}</span>
                <span class="text-xs text-text-secondary">{{ course.category }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="flex justify-end gap-3">
          <button type="button" class="btn btn-secondary" @click="showCreateDialog = false">取消</button>
          <button type="button" class="btn btn-primary" :disabled="submitting" @click="submitPlan">
            {{ submitting ? '保存中...' : editingPlan ? '保存修改' : '创建计划' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, MoreFilled, Calendar, Check } from '@element-plus/icons-vue'
import { Clock, Target } from 'lucide-vue-next'
import EmptyState from '@/components/EmptyState.vue'
import {
  getUserPlans,
  getActivePlan,
  createPlan,
  updatePlan,
  pausePlan as pausePlanApi,
  resumePlan as resumePlanApi,
  cancelPlan,
  completePlan,
} from '@/api/learning'
import { getCourses } from '@/api/course'

const loading = ref(false)
const submitting = ref(false)
const showCreateDialog = ref(false)
const editingPlan = ref(null)
const activePlan = ref(null)
const allPlans = ref([])
const availableCourses = ref([])
const planFormRef = ref(null)

const planForm = reactive({
  planName: '',
  description: '',
  dateRange: [],
  dailyTargetMinutes: 60,
  targetCourseIds: [],
})

const planRules = {
  planName: [
    { required: true, message: '请输入计划名称', trigger: 'blur' },
    { min: 2, max: 50, message: '计划名称长度在2-50个字符', trigger: 'blur' },
  ],
  dateRange: [{ required: true, message: '请选择时间范围', trigger: 'change' }],
  dailyTargetMinutes: [{ required: true, message: '请设置每日目标', trigger: 'change' }],
}

const historyPlans = computed(() => allPlans.value.filter((plan) => plan.status !== 'active'))

const disabledDate = (time) => time.getTime() < Date.now() - 24 * 60 * 60 * 1000

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getStatusClass = (status) => {
  const classes = {
    active: 'bg-success/20 text-success',
    completed: 'bg-info/20 text-info',
    paused: 'bg-warning/20 text-warning',
    canceled: 'bg-error/20 text-error',
  }
  return classes[status] || 'bg-bg-tertiary/60 text-text-secondary'
}

const getStatusText = (status) => {
  const texts = {
    active: '进行中',
    completed: '已完成',
    paused: '已暂停',
    canceled: '已取消',
  }
  return texts[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const [plansRes, activeRes, coursesRes] = await Promise.all([
      getUserPlans(),
      getActivePlan(),
      getCourses({ page: 1, size: 100 }),
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

const handlePlanAction = async (command) => {
  if (!activePlan.value) return

  const planId = activePlan.value.id

  switch (command) {
    case 'edit':
      editingPlan.value = activePlan.value
      planForm.planName = activePlan.value.planName
      planForm.description = activePlan.value.description
      planForm.dateRange = [new Date(activePlan.value.startDate), new Date(activePlan.value.endDate)]
      planForm.dailyTargetMinutes = activePlan.value.dailyTargetMinutes
      planForm.targetCourseIds = activePlan.value.targetCourses?.map((course) => course.courseId) || []
      showCreateDialog.value = true
      break

    case 'pause':
      await ElMessageBox.confirm('确定要暂停当前学习计划吗？', '暂停计划', { customClass: 'dark-message-box' })
      await pausePlanApi(planId)
      ElMessage.success('计划已暂停')
      loadData()
      break

    case 'complete':
      await ElMessageBox.confirm('确定要标记当前计划为已完成吗？', '完成计划', { customClass: 'dark-message-box' })
      await completePlan(planId)
      ElMessage.success('恭喜完成学习计划！')
      loadData()
      break

    case 'cancel':
      await ElMessageBox.confirm('确定要取消当前学习计划吗？此操作不可恢复。', '取消计划', {
        type: 'warning',
        customClass: 'dark-message-box',
      })
      await cancelPlan(planId)
      ElMessage.success('计划已取消')
      loadData()
      break
  }
}

const resumePlan = async (planId) => {
  try {
    await resumePlanApi(planId)
    ElMessage.success('计划已恢复')
    loadData()
  } catch (error) {
    ElMessage.error(error.message || '恢复失败')
  }
}

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
      targetCourseIds: planForm.targetCourseIds,
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
