<template>
  <PageLayout max-width="2xl">
    <!-- Page Header -->
    <PageHeader
      title="我的班期"
      subtitle="查看你负责的班期与报名情况。"
    >
      <template #icon>
        <svg class="w-5 h-5 text-primary" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
          <line x1="16" y1="2" x2="16" y2="6" />
          <line x1="8" y1="2" x2="8" y2="6" />
          <line x1="3" y1="10" x2="21" y2="10" />
        </svg>
      </template>
    </PageHeader>

    <!-- Sessions Table -->
    <Section title="班期列表" compact>
      <div class="bg-bg-secondary rounded-2xl border border-border-color overflow-hidden">
        <el-table :data="sessions" v-loading="loading" style="width: 100%">
          <el-table-column prop="sessionCode" label="班期编码" width="140" />
          <el-table-column prop="courseName" label="课程名称" min-width="180" />
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="endDate" label="结束日期" width="120" />
          <el-table-column prop="schedule" label="上课时间" width="150" />
          <el-table-column prop="location" label="上课地点" width="120" />
          <el-table-column label="报名情况" width="120">
            <template #default="{ row }">
              <Tag :type="row.currentEnrollment >= row.maxCapacity ? 'danger' : 'success'">
                {{ row.currentEnrollment }} / {{ row.maxCapacity }}
              </Tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <Tag :type="getStatusType(row.status)">{{ row.statusName }}</Tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <button
                type="button"
                class="inline-flex items-center gap-1 text-primary hover:text-primary-dark transition-colors text-sm font-medium"
                @click="viewStudents(row)"
              >
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
                学员名单
              </button>
            </template>
          </el-table-column>
        </el-table>

        <!-- Empty State -->
        <EmptyState
          v-if="!loading && sessions.length === 0"
          emoji="📋"
          title="暂无班期数据"
          description="你还没有被分配任何班期"
          size="sm"
          class="py-12"
        />
      </div>
    </Section>

    <!-- Student List Modal -->
    <Modal
      v-model="studentDialogVisible"
      :title="`学员名单 - ${currentSession?.sessionCode || ''}`"
      width="800px"
      :show-footer="true"
      @close="studentDialogVisible = false"
    >
      <div class="bg-bg-secondary rounded-xl border border-border-color overflow-hidden">
        <el-table :data="students" v-loading="studentLoading" style="width: 100%">
          <el-table-column prop="studentName" label="学员姓名" width="120" />
          <el-table-column prop="studentUsername" label="用户名" width="120" />
          <el-table-column prop="studentEmail" label="邮箱" width="180" />
          <el-table-column prop="studentPhone" label="手机号" width="130" />
          <el-table-column prop="enrollTime" label="报名时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.enrollTime) }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <Tag :type="getEnrollStatusType(row.status)">{{ row.statusName }}</Tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- Empty State -->
        <EmptyState
          v-if="!studentLoading && students.length === 0"
          emoji="👥"
          title="暂无学员报名"
          description="该班期还没有学员报名"
          size="sm"
          class="py-8"
        />
      </div>

      <template #footer>
        <div class="flex justify-end gap-3">
          <Button variant="secondary" @click="studentDialogVisible = false">
            关闭
          </Button>
          <Button
            variant="primary"
            :disabled="students.length === 0"
            @click="exportStudents"
          >
            <template #icon-left>
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
            </template>
            导出名单
          </Button>
        </div>
      </template>
    </Modal>

    <!-- Toast Container -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="toast.visible"
          class="fixed top-6 left-1/2 -translate-x-1/2 z-[9999] px-5 py-3 rounded-xl shadow-lg flex items-center gap-2"
          :class="toastClasses"
        >
          <!-- Success Icon -->
          <svg v-if="toast.type === 'success'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
          <!-- Warning Icon -->
          <svg v-else-if="toast.type === 'warning'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <!-- Error Icon -->
          <svg v-else-if="toast.type === 'error'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          <span class="font-medium">{{ toast.message }}</span>
        </div>
      </Transition>
    </Teleport>
  </PageLayout>
</template>

<script setup lang="ts">
/**
 * MySessions - 讲师班期管理页面
 *
 * 功能：
 * - 查看讲师负责的班期列表
 * - 查看班期学员名单
 * - 导出学员名单为 CSV
 */

import { ref, reactive, computed, onMounted } from 'vue';
import {
  PageLayout,
  PageHeader,
  Section,
  Button,
  Modal,
  Tag,
  EmptyState,
} from '@/design-system';
import { getMySessionsAsInstructor } from '@/api/session';
import { listEnrollments } from '@/api/enrollment';

// ==================== Type Definitions ====================

/** Session status */
type SessionStatus = 0 | 1 | 2 | 3;

/** Enrollment status */
type EnrollStatus = 0 | 1 | 2;

/** Session entity */
interface Session {
  id: number;
  sessionCode: string;
  courseName: string;
  startDate: string;
  endDate: string;
  schedule: string;
  location: string;
  currentEnrollment: number;
  maxCapacity: number;
  status: SessionStatus;
  statusName: string;
}

/** Student enrollment entity */
interface StudentEnrollment {
  studentName: string;
  studentUsername: string;
  studentEmail: string;
  studentPhone: string;
  enrollTime: string;
  status: EnrollStatus;
  statusName: string;
}

/** Toast type */
type ToastType = 'success' | 'warning' | 'error';

/** Toast state */
interface ToastState {
  visible: boolean;
  message: string;
  type: ToastType;
}

// ==================== Reactive State ====================

// Sessions list
const loading = ref(false);
const sessions = ref<Session[]>([]);

// Student dialog state
const studentDialogVisible = ref(false);
const studentLoading = ref(false);
const students = ref<StudentEnrollment[]>([]);
const currentSession = ref<Session | null>(null);

// Toast state
const toast = reactive<ToastState>({
  visible: false,
  message: '',
  type: 'success',
});

// ==================== Computed ====================

const toastClasses = computed(() => {
  const classes: Record<ToastType, string> = {
    success: 'bg-success text-white',
    warning: 'bg-warning text-white',
    error: 'bg-error text-white',
  };
  return classes[toast.type];
});

// ==================== Methods ====================

/**
 * Show toast message
 */
const showToast = (message: string, type: ToastType = 'success') => {
  toast.message = message;
  toast.type = type;
  toast.visible = true;

  setTimeout(() => {
    toast.visible = false;
  }, 3000);
};

/**
 * Load instructor's sessions
 */
const loadSessions = async () => {
  loading.value = true;
  try {
    const res = await getMySessionsAsInstructor();
    sessions.value = res.data || [];
  } catch (error) {
    console.error('加载班期失败:', error);
    showToast('加载班期失败', 'error');
  } finally {
    loading.value = false;
  }
};

/**
 * View students for a session
 */
const viewStudents = async (session: Session) => {
  currentSession.value = session;
  studentDialogVisible.value = true;
  studentLoading.value = true;

  try {
    const res = await listEnrollments(1, 100, null, session.id, null);
    students.value = res.data?.records || [];
  } catch (error) {
    console.error('加载学员名单失败:', error);
    showToast('加载学员名单失败', 'error');
  } finally {
    studentLoading.value = false;
  }
};

/**
 * Export students to CSV
 */
const exportStudents = () => {
  if (students.value.length === 0) {
    showToast('暂无学员数据可导出', 'warning');
    return;
  }

  const headers = ['学员姓名', '用户名', '邮箱', '手机号', '报名时间', '状态'];
  const rows = students.value.map((s) => [
    s.studentName || '',
    s.studentUsername || '',
    s.studentEmail || '',
    s.studentPhone || '',
    formatDateTime(s.enrollTime),
    s.statusName || '',
  ]);

  const csvContent = [
    headers.join(','),
    ...rows.map((row) => row.map((cell) => `"${cell}"`).join(',')),
  ].join('\n');

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `学员名单_${currentSession.value?.sessionCode || 'export'}.csv`;
  link.click();
  URL.revokeObjectURL(link.href);

  showToast('导出成功', 'success');
};

/**
 * Format date time string
 */
const formatDateTime = (dateStr: string): string => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * Get session status tag type
 */
const getStatusType = (status: SessionStatus): 'info' | 'success' | 'warning' | 'default' => {
  const typeMap: Record<SessionStatus, 'info' | 'success' | 'warning' | 'default'> = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'default',
  };
  return typeMap[status] || 'info';
};

/**
 * Get enrollment status tag type
 */
const getEnrollStatusType = (status: EnrollStatus): 'warning' | 'success' | 'danger' | 'info' => {
  const typeMap: Record<EnrollStatus, 'warning' | 'success' | 'danger'> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
  };
  return typeMap[status] || 'info';
};

// ==================== Lifecycle ====================

onMounted(() => {
  loadSessions();
});
</script>

<style scoped>
/* Toast animation */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

/* Element Plus table custom styles */
:deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-header-bg-color: var(--color-bg-tertiary);
  --el-table-tr-bg-color: transparent;
  --el-table-row-hover-bg-color: var(--color-bg-hover);
  --el-table-border-color: var(--color-border);
  --el-table-text-color: var(--color-text-primary);
  --el-table-header-text-color: var(--color-text-secondary);
}

:deep(.el-table th.el-table__cell) {
  font-weight: 500;
}
</style>
