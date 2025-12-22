<template>
  <div class="max-w-5xl mx-auto px-4 md:px-8 py-8 space-y-8">
    <!-- Hero -->
    <section class="relative overflow-hidden rounded-3xl border border-border-color/60 bg-bg-secondary/70 backdrop-blur-xl shadow-sm p-7 md:p-10">
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

        <Button variant="primary" @click="showCreateDialog = true">
          <template #icon>
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
            </svg>
          </template>
          创建计划
        </Button>
      </div>
    </section>

    <!-- Active Plan -->
    <section v-if="activePlan" class="space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold text-text-primary">当前计划</h2>
        <Tag type="primary">进行中</Tag>
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

              <!-- Dropdown Menu -->
              <div class="relative">
                <Button variant="ghost" class="!rounded-full !px-3 !py-2" @click="toggleDropdown">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <circle cx="12" cy="5" r="2" />
                    <circle cx="12" cy="12" r="2" />
                    <circle cx="12" cy="19" r="2" />
                  </svg>
                </Button>
                <Transition name="dropdown">
                  <div
                    v-if="dropdownVisible"
                    class="absolute right-0 top-full mt-2 w-40 bg-bg-secondary border border-border-color/60 rounded-xl shadow-lg py-2 z-20"
                  >
                    <button type="button" class="w-full px-4 py-2 text-sm text-left text-text-primary hover:bg-bg-tertiary/60 transition" @click="handlePlanAction('edit')">
                      编辑计划
                    </button>
                    <button type="button" class="w-full px-4 py-2 text-sm text-left text-text-primary hover:bg-bg-tertiary/60 transition" @click="handlePlanAction('pause')">
                      暂停计划
                    </button>
                    <button type="button" class="w-full px-4 py-2 text-sm text-left text-text-primary hover:bg-bg-tertiary/60 transition" @click="handlePlanAction('complete')">
                      完成计划
                    </button>
                    <div class="border-t border-border-color/60 my-1"></div>
                    <button type="button" class="w-full px-4 py-2 text-sm text-left text-error hover:bg-bg-tertiary/60 transition" @click="handlePlanAction('cancel')">
                      取消计划
                    </button>
                  </div>
                </Transition>
              </div>
            </div>

            <!-- Stats Grid -->
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

            <!-- Target Courses -->
            <div v-if="activePlan.targetCourses?.length > 0" class="space-y-3">
              <h4 class="text-sm font-semibold text-text-primary">目标课程</h4>
              <div class="rounded-2xl border border-border-color/60 bg-bg-secondary/60 overflow-hidden">
                <div
                  v-for="(course, idx) in activePlan.targetCourses"
                  :key="course.courseId"
                  class="flex items-center gap-4 px-4 py-3"
                  :class="idx !== activePlan.targetCourses.length - 1 ? 'border-b border-border-color/60' : ''"
                >
                  <div
                    class="w-9 h-9 rounded-full flex items-center justify-center flex-shrink-0"
                    :class="course.completed ? 'bg-success/15 text-success border border-success/20' : 'bg-bg-tertiary/60 text-text-muted border border-border-color/60'"
                  >
                    <svg v-if="course.completed" class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                    </svg>
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
            </div>
          </div>

          <!-- Progress Ring -->
          <div class="flex items-center justify-center lg:w-64 flex-shrink-0">
            <div class="rounded-3xl bg-bg-tertiary/40 border border-border-color/60 p-6">
              <div class="relative w-44 h-44 flex items-center justify-center">
                <svg class="w-full h-full transform -rotate-90">
                  <circle cx="88" cy="88" r="80" stroke="currentColor" stroke-width="12" fill="transparent" class="text-bg-tertiary" />
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

    <!-- Empty State -->
    <section v-else class="card p-8">
      <EmptyState
        emoji="🎯"
        title="还没有进行中的计划"
        description="创建一个学习计划，开始系统化学习。"
        action-text="创建学习计划"
        @action="showCreateDialog = true"
      />
    </section>

    <!-- History Plans -->
    <section class="space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold text-text-primary">历史计划</h2>
      </div>

      <div v-if="historyPlans.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="plan in historyPlans" :key="plan.id" class="card p-6 hover:shadow-md transition-shadow">
          <div class="flex justify-between items-start gap-4 mb-4">
            <h3 class="font-semibold text-text-primary truncate">{{ plan.planName }}</h3>
            <Tag :type="getStatusType(plan.status)">{{ getStatusText(plan.status) }}</Tag>
          </div>

          <div class="space-y-2 text-sm text-text-secondary mb-6">
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <span>{{ formatDate(plan.startDate) }} - {{ formatDate(plan.endDate) }}</span>
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
              <span>完成度: {{ plan.progressPercent || 0 }}%</span>
            </div>
          </div>

          <Button v-if="plan.status === 'paused'" variant="secondary" class="w-full justify-center" @click="resumePlan(plan.id)">
            恢复计划
          </Button>
        </div>
      </div>

      <div v-else class="card p-8">
        <EmptyState emoji="⏰" title="暂无历史计划" description="完成或暂停的计划会在这里展示，便于回顾你的成长轨迹。" />
      </div>
    </section>

    <!-- Create/Edit Dialog -->
    <Modal v-model="showCreateDialog" :title="editingPlan ? '编辑学习计划' : '创建学习计划'" width="600px" @close="resetForm">
      <FormLayout>
        <FormItem label="计划名称" required :error="formErrors.planName">
          <Input v-model="planForm.planName" placeholder="给你的计划起个名字" />
        </FormItem>

        <FormItem label="计划描述">
          <Input v-model="planForm.description" type="textarea" :rows="3" placeholder="描述你的学习目标（可选）" />
        </FormItem>

        <div class="grid grid-cols-2 gap-4">
          <FormItem label="开始日期" required :error="formErrors.startDate">
            <input
              v-model="planForm.startDate"
              type="date"
              :min="minDate"
              class="w-full h-11 px-4 rounded-xl border border-border-color/60 bg-bg-tertiary/40 text-text-primary focus:outline-none focus:ring-2 focus:ring-primary/15 focus:border-primary/40 transition"
            />
          </FormItem>
          <FormItem label="结束日期" required :error="formErrors.endDate">
            <input
              v-model="planForm.endDate"
              type="date"
              :min="planForm.startDate || minDate"
              class="w-full h-11 px-4 rounded-xl border border-border-color/60 bg-bg-tertiary/40 text-text-primary focus:outline-none focus:ring-2 focus:ring-primary/15 focus:border-primary/40 transition"
            />
          </FormItem>
        </div>

        <FormItem label="每日目标 (分钟)" required>
          <div class="flex items-center gap-3">
            <input
              v-model.number="planForm.dailyTargetMinutes"
              type="number"
              min="15"
              max="480"
              step="15"
              class="flex-1 h-11 px-4 rounded-xl border border-border-color/60 bg-bg-tertiary/40 text-text-primary focus:outline-none focus:ring-2 focus:ring-primary/15 focus:border-primary/40 transition"
            />
            <span class="text-sm text-text-muted">分钟/天</span>
          </div>
        </FormItem>

        <FormItem label="选择课程">
          <Select
            v-model="planForm.targetCourseIds"
            :options="courseOptions"
            multiple
            placeholder="选择要加入计划的课程"
          />
        </FormItem>
      </FormLayout>

      <template #footer>
        <div class="flex justify-end gap-3">
          <Button variant="secondary" @click="showCreateDialog = false">取消</Button>
          <Button variant="primary" :disabled="submitting" @click="submitPlan">
            {{ submitting ? '保存中...' : editingPlan ? '保存修改' : '创建计划' }}
          </Button>
        </div>
      </template>
    </Modal>

    <!-- Confirm Dialog -->
    <Modal v-model="confirmDialog.visible" :title="confirmDialog.title" @confirm="confirmDialog.onConfirm">
      <p class="text-text-secondary">{{ confirmDialog.message }}</p>
    </Modal>

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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { Button, Input, Select, Modal, Tag, EmptyState, FormLayout, FormItem } from '@/design-system';
import {
  getUserPlans,
  getActivePlan,
  createPlan,
  updatePlan,
  pausePlan as pausePlanApi,
  resumePlan as resumePlanApi,
  cancelPlan,
  completePlan,
} from '@/api/learning';
import { getCourses } from '@/api/course';

// Types
interface Plan {
  id: number;
  planName: string;
  description?: string;
  startDate: string;
  endDate: string;
  dailyTargetMinutes: number;
  status: 'active' | 'completed' | 'paused' | 'canceled';
  progressPercent?: number;
  completedCourses?: number;
  totalCourses?: number;
  remainingDays?: number;
  targetCourses?: Array<{
    courseId: number;
    courseName: string;
    category: string;
    progressPercent: number;
    completed: boolean;
  }>;
}

interface Course {
  id: number;
  name: string;
  category: string;
}

// State
const loading = ref(false);
const submitting = ref(false);
const showCreateDialog = ref(false);
const editingPlan = ref<Plan | null>(null);
const activePlan = ref<Plan | null>(null);
const allPlans = ref<Plan[]>([]);
const availableCourses = ref<Course[]>([]);
const dropdownVisible = ref(false);

const planForm = reactive({
  planName: '',
  description: '',
  startDate: '',
  endDate: '',
  dailyTargetMinutes: 60,
  targetCourseIds: [] as number[],
});

const formErrors = reactive({
  planName: '',
  startDate: '',
  endDate: '',
});

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

// Confirm Dialog
const confirmDialog = ref({
  visible: false,
  title: '',
  message: '',
  onConfirm: () => {},
});

const showConfirm = (title: string, message: string): Promise<boolean> => {
  return new Promise((resolve) => {
    confirmDialog.value = {
      visible: true,
      title,
      message,
      onConfirm: () => {
        confirmDialog.value.visible = false;
        resolve(true);
      },
    };
  });
};

// Computed
const historyPlans = computed(() => allPlans.value.filter((plan) => plan.status !== 'active'));

const minDate = computed(() => {
  const today = new Date();
  return today.toISOString().split('T')[0];
});

const courseOptions = computed(() =>
  availableCourses.value.map((course) => ({
    label: course.name,
    value: course.id,
  }))
);

// Methods
const formatDate = (dateStr: string): string => {
  if (!dateStr) return '-';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

const getStatusType = (status: string): 'success' | 'info' | 'warning' | 'error' | 'default' => {
  const types: Record<string, 'success' | 'info' | 'warning' | 'error'> = {
    active: 'success',
    completed: 'info',
    paused: 'warning',
    canceled: 'error',
  };
  return types[status] || 'default';
};

const getStatusText = (status: string): string => {
  const texts: Record<string, string> = {
    active: '进行中',
    completed: '已完成',
    paused: '已暂停',
    canceled: '已取消',
  };
  return texts[status] || status;
};

const toggleDropdown = () => {
  dropdownVisible.value = !dropdownVisible.value;
};

const closeDropdown = (e: MouseEvent) => {
  const target = e.target as HTMLElement;
  if (!target.closest('.relative')) {
    dropdownVisible.value = false;
  }
};

const loadData = async () => {
  loading.value = true;
  try {
    const [plansRes, activeRes, coursesRes] = await Promise.all([
      getUserPlans(),
      getActivePlan(),
      getCourses({ page: 1, size: 100 }),
    ]);

    allPlans.value = plansRes.data || [];
    activePlan.value = activeRes.data;
    availableCourses.value = coursesRes.data?.records || coursesRes.data || [];
  } catch (error) {
    console.error('加载数据失败:', error);
    showToast('加载数据失败', 'error');
  } finally {
    loading.value = false;
  }
};

const handlePlanAction = async (command: string) => {
  dropdownVisible.value = false;
  if (!activePlan.value) return;

  const planId = activePlan.value.id;

  switch (command) {
    case 'edit':
      editingPlan.value = activePlan.value;
      planForm.planName = activePlan.value.planName;
      planForm.description = activePlan.value.description || '';
      planForm.startDate = activePlan.value.startDate;
      planForm.endDate = activePlan.value.endDate;
      planForm.dailyTargetMinutes = activePlan.value.dailyTargetMinutes;
      planForm.targetCourseIds = activePlan.value.targetCourses?.map((course) => course.courseId) || [];
      showCreateDialog.value = true;
      break;

    case 'pause':
      if (await showConfirm('暂停计划', '确定要暂停当前学习计划吗？')) {
        try {
          await pausePlanApi(planId);
          showToast('计划已暂停', 'success');
          loadData();
        } catch (error) {
          showToast('操作失败', 'error');
        }
      }
      break;

    case 'complete':
      if (await showConfirm('完成计划', '确定要标记当前计划为已完成吗？')) {
        try {
          await completePlan(planId);
          showToast('恭喜完成学习计划！', 'success');
          loadData();
        } catch (error) {
          showToast('操作失败', 'error');
        }
      }
      break;

    case 'cancel':
      if (await showConfirm('取消计划', '确定要取消当前学习计划吗？此操作不可恢复。')) {
        try {
          await cancelPlan(planId);
          showToast('计划已取消', 'success');
          loadData();
        } catch (error) {
          showToast('操作失败', 'error');
        }
      }
      break;
  }
};

const resumePlan = async (planId: number) => {
  try {
    await resumePlanApi(planId);
    showToast('计划已恢复', 'success');
    loadData();
  } catch (error: any) {
    showToast(error.message || '恢复失败', 'error');
  }
};

const validateForm = (): boolean => {
  formErrors.planName = '';
  formErrors.startDate = '';
  formErrors.endDate = '';

  let valid = true;

  if (!planForm.planName.trim()) {
    formErrors.planName = '请输入计划名称';
    valid = false;
  } else if (planForm.planName.length < 2 || planForm.planName.length > 50) {
    formErrors.planName = '计划名称长度在2-50个字符';
    valid = false;
  }

  if (!planForm.startDate) {
    formErrors.startDate = '请选择开始日期';
    valid = false;
  }

  if (!planForm.endDate) {
    formErrors.endDate = '请选择结束日期';
    valid = false;
  }

  return valid;
};

const submitPlan = async () => {
  if (!validateForm()) return;

  submitting.value = true;
  try {
    const data = {
      planName: planForm.planName,
      description: planForm.description,
      startDate: planForm.startDate,
      endDate: planForm.endDate,
      dailyTargetMinutes: planForm.dailyTargetMinutes,
      targetCourseIds: planForm.targetCourseIds,
    };

    if (editingPlan.value) {
      await updatePlan(editingPlan.value.id, data);
      showToast('计划已更新', 'success');
    } else {
      await createPlan(data);
      showToast('计划创建成功', 'success');
    }

    showCreateDialog.value = false;
    loadData();
  } catch (error: any) {
    showToast(error.message || '操作失败', 'error');
  } finally {
    submitting.value = false;
  }
};

const resetForm = () => {
  editingPlan.value = null;
  planForm.planName = '';
  planForm.description = '';
  planForm.startDate = '';
  planForm.endDate = '';
  planForm.dailyTargetMinutes = 60;
  planForm.targetCourseIds = [];
  formErrors.planName = '';
  formErrors.startDate = '';
  formErrors.endDate = '';
};

onMounted(() => {
  loadData();
  document.addEventListener('click', closeDropdown);
});

onUnmounted(() => {
  document.removeEventListener('click', closeDropdown);
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

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
