<template>
  <PageLayout max-width="2xl">
    <!-- 页面头部 -->
    <PageHeader
      title="班期管理"
      subtitle="管理课程班期、讲师与报名状态"
    >
      <template #actions>
        <Button variant="primary" @click="handleAdd">
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19" />
              <line x1="5" y1="12" x2="19" y2="12" />
            </svg>
          </template>
          新增班期
        </Button>
      </template>
    </PageHeader>

    <!-- 搜索区域 -->
    <Section compact>
      <div class="card p-6">
        <div class="flex flex-wrap gap-4 items-end">
          <div class="w-56">
            <label class="block text-sm text-text-secondary mb-2">课程</label>
            <Select
              v-model="searchForm.courseId"
              :options="courseSelectOptions"
              placeholder="全部课程"
              clearable
            />
          </div>
          <div class="w-40">
            <label class="block text-sm text-text-secondary mb-2">状态</label>
            <Select
              v-model="searchForm.status"
              :options="statusOptions"
              placeholder="全部状态"
              clearable
            />
          </div>
          <div class="flex gap-2">
            <Button variant="primary" @click="handleSearch">
              <template #icon-left>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8" />
                  <line x1="21" y1="21" x2="16.65" y2="16.65" />
                </svg>
              </template>
              搜索
            </Button>
            <Button variant="secondary" @click="handleReset">重置</Button>
          </div>
        </div>
      </div>
    </Section>

    <!-- 数据表格 -->
    <Section compact>
      <div class="card overflow-hidden">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="sessionCode" label="班期编码" width="160" />
          <el-table-column prop="courseName" label="课程名称" min-width="150" />
          <el-table-column prop="instructorName" label="讲师" width="100" />
          <el-table-column label="日期" width="200">
            <template #default="{ row }">
              {{ row.startDate }} ~ {{ row.endDate }}
            </template>
          </el-table-column>
          <el-table-column label="名额" width="100">
            <template #default="{ row }">
              {{ row.currentEnrollment || 0 }} / {{ row.maxCapacity }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <Tag :type="getStatusType(row.status)">{{ row.statusName }}</Tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <Button variant="ghost" size="sm" @click="handleEdit(row)">编辑</Button>
              <Button
                v-if="row.status === 0"
                variant="ghost"
                size="sm"
                @click="handleOpen(row)"
              >开放报名</Button>
              <Button
                v-else-if="row.status === 1"
                variant="ghost"
                size="sm"
                @click="handleClose(row)"
              >关闭报名</Button>
              <Button variant="danger" size="sm" @click="handleDelete(row)">删除</Button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="p-4 border-t border-border-color/60 flex justify-end">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :page-sizes="[10, 20, 50]"
            :total="pagination.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </Section>

    <!-- 新增/编辑对话框 -->
    <Modal
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :confirm-loading="submitLoading"
      @confirm="handleSubmit"
      @close="resetForm"
    >
      <FormLayout layout="horizontal" label-width="100px">
        <FormItem v-if="!isEdit" label="班期编码" required :error="formErrors.sessionCode">
          <Input
            v-model="form.sessionCode"
            placeholder="如: JAVA-001-2024S1"
            :error="!!formErrors.sessionCode"
          />
        </FormItem>

        <FormItem v-if="!isEdit" label="所属课程" required :error="formErrors.courseId">
          <Select
            v-model="form.courseId"
            :options="courseSelectOptions"
            placeholder="请选择课程"
            :error="!!formErrors.courseId"
          />
        </FormItem>

        <FormItem label="授课讲师" required :error="formErrors.instructorId">
          <Select
            v-model="form.instructorId"
            :options="instructorSelectOptions"
            placeholder="请选择讲师"
            :error="!!formErrors.instructorId"
          />
        </FormItem>

        <FormItem label="开班日期" required :error="formErrors.startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择开班日期"
            class="w-full"
          />
        </FormItem>

        <FormItem label="结束日期" required :error="formErrors.endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择结束日期"
            class="w-full"
          />
        </FormItem>

        <FormItem label="上课时间" :error="formErrors.schedule">
          <Input
            v-model="form.schedule"
            placeholder="如: 每周六 9:00-12:00"
          />
        </FormItem>

        <FormItem label="上课地点" :error="formErrors.location">
          <Input
            v-model="form.location"
            placeholder="线下地址或线上会议链接"
          />
        </FormItem>

        <FormItem label="最大名额" required :error="formErrors.maxCapacity">
          <el-input-number v-model="form.maxCapacity" :min="1" :max="500" />
        </FormItem>
      </FormLayout>
    </Modal>

    <!-- 确认对话框 -->
    <Modal
      v-model="confirmDialog.visible"
      :title="confirmDialog.title"
      width="400px"
      :confirm-loading="confirmDialog.loading"
      @confirm="confirmDialog.onConfirm"
      @cancel="confirmDialog.visible = false"
    >
      <p class="text-text-secondary">{{ confirmDialog.message }}</p>
    </Modal>

    <!-- Toast 消息容器 -->
    <Teleport to="body">
      <Transition name="toast-fade">
        <div
          v-if="toast.visible"
          class="fixed top-6 right-6 z-toast flex items-center gap-3 px-4 py-3 rounded-xl shadow-lg"
          :class="toastTypeClasses"
        >
          <!-- 成功图标 -->
          <svg v-if="toast.type === 'success'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
          <!-- 错误图标 -->
          <svg v-else-if="toast.type === 'error'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          <!-- 警告图标 -->
          <svg v-else-if="toast.type === 'warning'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <!-- 信息图标 -->
          <svg v-else class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="16" x2="12" y2="12" />
            <line x1="12" y1="8" x2="12.01" y2="8" />
          </svg>
          <span class="text-sm font-medium">{{ toast.message }}</span>
        </div>
      </Transition>
    </Teleport>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import {
  PageLayout,
  PageHeader,
  Section,
  Button,
  Input,
  Select,
  Modal,
  FormLayout,
  FormItem,
  Tag,
} from '@/design-system';
import {
  getSessions,
  createSession,
  updateSession,
  deleteSession,
  openEnrollment,
  closeEnrollment,
} from '@/api/session';
import { getPublishedCourses } from '@/api/course';
import { getUsers } from '@/api/user';

// ============================================================================
// 类型定义
// ============================================================================

/** 课程选项 */
interface CourseOption {
  id: number;
  name: string;
}

/** 讲师选项 */
interface InstructorOption {
  id: number;
  realName: string;
}

/** 班期状态类型 */
type SessionStatus = 0 | 1 | 2 | 3 | 4;

/** 班期数据 */
interface SessionRow {
  id: number;
  sessionCode: string;
  courseName: string;
  courseId: number;
  instructorId: number;
  instructorName: string;
  startDate: string;
  endDate: string;
  schedule: string;
  location: string;
  currentEnrollment: number;
  maxCapacity: number;
  status: SessionStatus;
  statusName: string;
}

/** 搜索表单 */
interface SearchForm {
  courseId: number | null;
  status: SessionStatus | null;
}

/** 班期表单 */
interface SessionForm {
  id: number | null;
  sessionCode: string;
  courseId: number | null;
  instructorId: number | null;
  startDate: string;
  endDate: string;
  schedule: string;
  location: string;
  maxCapacity: number;
}

/** 表单错误 */
interface FormErrors {
  sessionCode: string;
  courseId: string;
  instructorId: string;
  startDate: string;
  endDate: string;
  schedule: string;
  location: string;
  maxCapacity: string;
}

/** Select 选项类型 */
interface SelectOption {
  label: string;
  value: number | string;
}

/** Toast 类型 */
type ToastType = 'success' | 'error' | 'warning' | 'info';

/** Toast 状态 */
interface ToastState {
  visible: boolean;
  message: string;
  type: ToastType;
}

/** 确认对话框状态 */
interface ConfirmDialogState {
  visible: boolean;
  title: string;
  message: string;
  loading: boolean;
  onConfirm: () => void;
}

// ============================================================================
// 搜索相关
// ============================================================================

const searchForm = reactive<SearchForm>({
  courseId: null,
  status: null,
});

/** 状态选项 */
const statusOptions: SelectOption[] = [
  { label: '未开放', value: 0 },
  { label: '报名中', value: 1 },
  { label: '已满员', value: 2 },
  { label: '进行中', value: 3 },
  { label: '已结束', value: 4 },
];

// ============================================================================
// 表格相关
// ============================================================================

const tableData = ref<SessionRow[]>([]);
const loading = ref(false);

// ============================================================================
// 下拉选项
// ============================================================================

const courseOptions = ref<CourseOption[]>([]);
const instructorOptions = ref<InstructorOption[]>([]);

/** 课程下拉选项（适配 Select 组件） */
const courseSelectOptions = computed<SelectOption[]>(() =>
  courseOptions.value.map((c) => ({ label: c.name, value: c.id }))
);

/** 讲师下拉选项（适配 Select 组件） */
const instructorSelectOptions = computed<SelectOption[]>(() =>
  instructorOptions.value.map((i) => ({ label: i.realName, value: i.id }))
);

// ============================================================================
// 分页
// ============================================================================

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
});

// ============================================================================
// 对话框
// ============================================================================

const dialogVisible = ref(false);
const dialogTitle = ref('新增班期');
const isEdit = ref(false);
const submitLoading = ref(false);

// ============================================================================
// 表单
// ============================================================================

const form = reactive<SessionForm>({
  id: null,
  sessionCode: '',
  courseId: null,
  instructorId: null,
  startDate: '',
  endDate: '',
  schedule: '',
  location: '',
  maxCapacity: 30,
});

const formErrors = reactive<FormErrors>({
  sessionCode: '',
  courseId: '',
  instructorId: '',
  startDate: '',
  endDate: '',
  schedule: '',
  location: '',
  maxCapacity: '',
});

// ============================================================================
// Toast 消息
// ============================================================================

const toast = reactive<ToastState>({
  visible: false,
  message: '',
  type: 'success',
});

let toastTimer: ReturnType<typeof setTimeout> | null = null;

/** Toast 类型样式 */
const toastTypeClasses = computed(() => {
  const map: Record<ToastType, string> = {
    success: 'bg-success/90 text-white',
    error: 'bg-error/90 text-white',
    warning: 'bg-warning/90 text-white',
    info: 'bg-info/90 text-white',
  };
  return map[toast.type];
});

/** 显示 Toast 消息 */
function showToast(message: string, type: ToastType = 'success', duration = 3000): void {
  if (toastTimer) {
    clearTimeout(toastTimer);
  }
  toast.message = message;
  toast.type = type;
  toast.visible = true;

  toastTimer = setTimeout(() => {
    toast.visible = false;
  }, duration);
}

// ============================================================================
// 确认对话框
// ============================================================================

const confirmDialog = reactive<ConfirmDialogState>({
  visible: false,
  title: '提示',
  message: '',
  loading: false,
  onConfirm: () => {},
});

/** 显示确认对话框 */
function showConfirmDialog(options: {
  title?: string;
  message: string;
  onConfirm: () => Promise<void> | void;
}): void {
  confirmDialog.title = options.title || '提示';
  confirmDialog.message = options.message;
  confirmDialog.loading = false;
  confirmDialog.onConfirm = async () => {
    confirmDialog.loading = true;
    try {
      await options.onConfirm();
      confirmDialog.visible = false;
    } catch (error) {
      console.error('操作失败:', error);
    } finally {
      confirmDialog.loading = false;
    }
  };
  confirmDialog.visible = true;
}

// ============================================================================
// 状态标签类型映射
// ============================================================================

type TagType = 'default' | 'primary' | 'success' | 'warning' | 'danger' | 'info';

function getStatusType(status: SessionStatus): TagType {
  const map: Record<SessionStatus, TagType> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'primary',
    4: 'danger',
  };
  return map[status] || 'info';
}

// ============================================================================
// 数据加载
// ============================================================================

async function loadData(): Promise<void> {
  loading.value = true;
  try {
    const res = await getSessions({
      page: pagination.page,
      size: pagination.size,
      courseId: searchForm.courseId ?? undefined,
      status: searchForm.status ?? undefined,
    });
    tableData.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    console.error('加载班期列表失败:', error);
    showToast('加载班期列表失败', 'error');
  } finally {
    loading.value = false;
  }
}

async function loadCourseOptions(): Promise<void> {
  try {
    const res = await getPublishedCourses();
    courseOptions.value = res.data;
  } catch (error) {
    console.error('加载课程选项失败:', error);
  }
}

async function loadInstructorOptions(): Promise<void> {
  try {
    const res = await getUsers({ role: 'INSTRUCTOR', size: 100 });
    instructorOptions.value = res.data.records;
  } catch (error) {
    console.error('加载讲师选项失败:', error);
  }
}

// ============================================================================
// 搜索与分页
// ============================================================================

function handleSearch(): void {
  pagination.page = 1;
  loadData();
}

function handleReset(): void {
  searchForm.courseId = null;
  searchForm.status = null;
  handleSearch();
}

function handleSizeChange(val: number): void {
  pagination.size = val;
  loadData();
}

function handleCurrentChange(val: number): void {
  pagination.page = val;
  loadData();
}

// ============================================================================
// 表单验证
// ============================================================================

function clearFormErrors(): void {
  formErrors.sessionCode = '';
  formErrors.courseId = '';
  formErrors.instructorId = '';
  formErrors.startDate = '';
  formErrors.endDate = '';
  formErrors.schedule = '';
  formErrors.location = '';
  formErrors.maxCapacity = '';
}

function validateForm(): boolean {
  clearFormErrors();
  let valid = true;

  if (!isEdit.value) {
    if (!form.sessionCode.trim()) {
      formErrors.sessionCode = '请输入班期编码';
      valid = false;
    }
    if (!form.courseId) {
      formErrors.courseId = '请选择课程';
      valid = false;
    }
  }

  if (!form.instructorId) {
    formErrors.instructorId = '请选择讲师';
    valid = false;
  }

  if (!form.startDate) {
    formErrors.startDate = '请选择开班日期';
    valid = false;
  }

  if (!form.endDate) {
    formErrors.endDate = '请选择结束日期';
    valid = false;
  }

  if (form.startDate && form.endDate && form.startDate > form.endDate) {
    formErrors.endDate = '结束日期不能早于开班日期';
    valid = false;
  }

  if (!form.maxCapacity || form.maxCapacity < 1) {
    formErrors.maxCapacity = '请输入有效的最大名额';
    valid = false;
  }

  return valid;
}

// ============================================================================
// CRUD 操作
// ============================================================================

function handleAdd(): void {
  isEdit.value = false;
  dialogTitle.value = '新增班期';
  dialogVisible.value = true;
}

function handleEdit(row: SessionRow): void {
  isEdit.value = true;
  dialogTitle.value = '编辑班期';
  Object.assign(form, {
    id: row.id,
    sessionCode: row.sessionCode,
    courseId: row.courseId,
    instructorId: row.instructorId,
    startDate: row.startDate,
    endDate: row.endDate,
    schedule: row.schedule,
    location: row.location,
    maxCapacity: row.maxCapacity,
  });
  dialogVisible.value = true;
}

function handleOpen(row: SessionRow): void {
  showConfirmDialog({
    title: '开放报名',
    message: '确定要开放该班期报名吗？',
    onConfirm: async () => {
      await openEnrollment(row.id);
      showToast('已开放报名', 'success');
      loadData();
    },
  });
}

function handleClose(row: SessionRow): void {
  showConfirmDialog({
    title: '关闭报名',
    message: '确定要关闭该班期报名吗？',
    onConfirm: async () => {
      await closeEnrollment(row.id);
      showToast('已关闭报名', 'success');
      loadData();
    },
  });
}

function handleDelete(row: SessionRow): void {
  showConfirmDialog({
    title: '删除班期',
    message: '确定要删除该班期吗？此操作不可恢复。',
    onConfirm: async () => {
      await deleteSession(row.id);
      showToast('删除成功', 'success');
      loadData();
    },
  });
}

async function handleSubmit(): Promise<void> {
  if (!validateForm()) {
    return;
  }

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateSession(form.id!, {
        instructorId: form.instructorId!,
        startDate: form.startDate,
        endDate: form.endDate,
        schedule: form.schedule,
        location: form.location,
        maxCapacity: form.maxCapacity,
      });
      showToast('更新成功', 'success');
    } else {
      await createSession({
        sessionCode: form.sessionCode,
        courseId: form.courseId!,
        instructorId: form.instructorId!,
        startDate: form.startDate,
        endDate: form.endDate,
        schedule: form.schedule,
        location: form.location,
        maxCapacity: form.maxCapacity,
      });
      showToast('创建成功', 'success');
    }

    dialogVisible.value = false;
    loadData();
  } catch (error) {
    console.error('提交失败:', error);
    showToast('提交失败，请重试', 'error');
  } finally {
    submitLoading.value = false;
  }
}

function resetForm(): void {
  form.id = null;
  form.sessionCode = '';
  form.courseId = null;
  form.instructorId = null;
  form.startDate = '';
  form.endDate = '';
  form.schedule = '';
  form.location = '';
  form.maxCapacity = 30;
  clearFormErrors();
}

// ============================================================================
// 生命周期
// ============================================================================

onMounted(() => {
  loadData();
  loadCourseOptions();
  loadInstructorOptions();
});
</script>

<style scoped>
/* Toast 动画 */
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 300ms ease-out;
}

.toast-fade-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.toast-fade-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* z-index for toast */
.z-toast {
  z-index: 9999;
}

/* 修复 el-date-picker 宽度 */
:deep(.el-date-editor.el-input) {
  width: 100%;
}

:deep(.el-date-editor .el-input__wrapper) {
  width: 100%;
}
</style>
