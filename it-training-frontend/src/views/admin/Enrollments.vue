<template>
  <PageLayout max-width="2xl">
    <!-- 页面头部 -->
    <PageHeader
      title="报名管理"
      subtitle="跟踪和管理学员课程报名情况"
    />

    <!-- 筛选区 -->
    <Section compact>
      <div class="flex flex-wrap gap-4">
        <!-- 班期筛选 -->
        <div class="flex-1 min-w-[200px]">
          <Select
            v-model="searchForm.sessionId"
            :options="sessionSelectOptions"
            placeholder="按班期筛选"
            clearable
          />
        </div>

        <!-- 状态筛选 -->
        <div class="w-40">
          <Select
            v-model="searchForm.status"
            :options="statusOptions"
            placeholder="全部状态"
            clearable
          />
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-2">
          <Button variant="secondary" @click="handleSearch">
            <template #icon-left>
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="11" cy="11" r="8" />
                <path d="M21 21l-4.35-4.35" />
              </svg>
            </template>
            搜索
          </Button>
          <Button variant="secondary" @click="handleExport">
            <template #icon-left>
              <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4" />
                <polyline points="7 10 12 15 17 10" />
                <line x1="12" y1="15" x2="12" y2="3" />
              </svg>
            </template>
            导出
          </Button>
        </div>
      </div>
    </Section>

    <!-- 表格区 -->
    <Section compact>
      <div class="bg-bg-secondary rounded-2xl border border-border-color overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />

          <el-table-column label="学员" min-width="200">
            <template #default="{ row }">
              <div>
                <div class="font-medium text-text-primary">{{ row.realName }}</div>
                <div class="text-xs text-text-muted">{{ row.userName }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="联系方式" min-width="200">
            <template #default="{ row }">
              <div class="text-sm text-text-primary">
                <div>{{ row.studentEmail }}</div>
                <div class="text-text-muted text-xs">{{ row.studentPhone }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="课程信息" min-width="200">
            <template #default="{ row }">
              <div>
                <div class="font-medium text-text-primary">{{ row.courseName }}</div>
                <div class="text-xs text-text-muted">{{ row.sessionCode }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="enrolledAt" label="报名时间" width="180">
            <template #default="{ row }">
              <span class="text-text-secondary">{{ formatDateTime(row.enrolledAt) }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <Tag :type="getStatusTagType(row.status)">
                {{ row.statusName }}
              </Tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="p-4 border-t border-border-color flex justify-end">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </div>
    </Section>

    <!-- Toast 容器 -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="toast.visible"
          class="fixed top-6 left-1/2 -translate-x-1/2 z-[9999] px-5 py-3 rounded-xl shadow-lg flex items-center gap-2"
          :class="toastClasses"
        >
          <!-- 成功图标 -->
          <svg v-if="toast.type === 'success'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 11-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
          <!-- 警告图标 -->
          <svg v-else-if="toast.type === 'warning'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <!-- 错误图标 -->
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
 * Enrollments - 报名管理页面
 *
 * 功能：
 * - 按班期筛选报名记录
 * - 按状态筛选报名记录
 * - 导出报名数据为 CSV
 * - 分页展示报名列表
 */

import { ref, reactive, computed, onMounted } from 'vue';
import { PageLayout, PageHeader, Section, Button, Select, Tag } from '@/design-system';
import { getEnrollments } from '@/api/enrollment';
import { getSessions } from '@/api/session';

// ==================== 类型定义 ====================

/** 报名记录 */
interface Enrollment {
  id: number;
  userName: string;
  realName: string;
  studentEmail: string;
  studentPhone: string;
  courseName: string;
  sessionCode: string;
  enrolledAt: string;
  status: number;
  statusName: string;
}

/** 班期选项 */
interface Session {
  id: number;
  sessionCode: string;
  courseName?: string;
}

/** 分页信息 */
interface Pagination {
  page: number;
  size: number;
  total: number;
}

/** 搜索表单 */
interface SearchForm {
  sessionId: number | null;
  status: number | null;
}

/** Toast 类型 */
type ToastType = 'success' | 'warning' | 'error';

/** Toast 状态 */
interface ToastState {
  visible: boolean;
  message: string;
  type: ToastType;
}

/** Select 选项类型 */
interface SelectOption {
  label: string;
  value: number;
}

// ==================== 状态定义 ====================

// 搜索表单
const searchForm = reactive<SearchForm>({
  sessionId: null,
  status: null,
});

// 表格数据
const tableData = ref<Enrollment[]>([]);

// 加载状态
const loading = ref(false);

// 班期选项
const sessionOptions = ref<Session[]>([]);

// 分页
const pagination = reactive<Pagination>({
  page: 1,
  size: 10,
  total: 0,
});

// Toast 状态
const toast = reactive<ToastState>({
  visible: false,
  message: '',
  type: 'success',
});

// ==================== 计算属性 ====================

// 班期下拉选项
const sessionSelectOptions = computed<SelectOption[]>(() => {
  return sessionOptions.value.map((session) => ({
    label: `${session.sessionCode} - ${session.courseName || ''}`,
    value: session.id,
  }));
});

// 状态下拉选项
const statusOptions: SelectOption[] = [
  { label: '已报名', value: 0 },
  { label: '已取消', value: 1 },
];

// Toast 样式类
const toastClasses = computed(() => {
  const typeClasses: Record<ToastType, string> = {
    success: 'bg-success text-white',
    warning: 'bg-warning text-white',
    error: 'bg-error text-white',
  };
  return typeClasses[toast.type];
});

// ==================== 方法定义 ====================

/**
 * 显示 Toast 提示
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
 * 加载报名数据
 */
const loadData = async () => {
  loading.value = true;
  try {
    const res = await getEnrollments({
      page: pagination.page,
      size: pagination.size,
      sessionId: searchForm.sessionId ?? undefined,
      status: searchForm.status ?? undefined,
    });
    tableData.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    console.error('Failed to load enrollments:', error);
    showToast('加载报名数据失败', 'error');
  } finally {
    loading.value = false;
  }
};

/**
 * 加载班期选项
 */
const loadSessionOptions = async () => {
  try {
    const res = await getSessions({ size: 100 });
    sessionOptions.value = res.data.records;
  } catch (error) {
    console.error('Failed to load sessions:', error);
  }
};

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.page = 1;
  loadData();
};

/**
 * 导出 CSV
 */
const handleExport = () => {
  if (tableData.value.length === 0) {
    showToast('暂无数据可导出', 'warning');
    return;
  }

  const headers = ['ID', 'Username', 'Name', 'Email', 'Phone', 'Session', 'Course', 'Enrolled Date', 'Status'];
  const rows = tableData.value.map((row) => [
    row.id,
    row.userName || '',
    row.realName || '',
    row.studentEmail || '',
    row.studentPhone || '',
    row.sessionCode || '',
    row.courseName || '',
    formatDateTime(row.enrolledAt),
    row.statusName || '',
  ]);

  const csvContent = [
    headers.join(','),
    ...rows.map((row) => row.map((cell) => `"${cell}"`).join(',')),
  ].join('\n');

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `enrollments_${new Date().toISOString().slice(0, 10)}.csv`;
  link.click();
  URL.revokeObjectURL(link.href);

  showToast('导出成功', 'success');
};

/**
 * 获取状态标签类型
 */
const getStatusTagType = (status: number): 'success' | 'default' => {
  return status === 0 ? 'success' : 'default';
};

/**
 * 格式化日期时间
 */
const formatDateTime = (dateStr: string): string => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

/**
 * 分页大小变化
 */
const handleSizeChange = (val: number) => {
  pagination.size = val;
  loadData();
};

/**
 * 当前页变化
 */
const handleCurrentChange = (val: number) => {
  pagination.page = val;
  loadData();
};

// ==================== 生命周期 ====================

onMounted(() => {
  loadData();
  loadSessionOptions();
});
</script>

<style scoped>
/* Toast 动画 */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

/* Element Plus 表格自定义样式 */
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

/* Element Plus 分页自定义样式 */
:deep(.el-pagination) {
  --el-pagination-bg-color: var(--color-bg-tertiary);
  --el-pagination-text-color: var(--color-text-secondary);
  --el-pagination-button-disabled-bg-color: var(--color-bg-tertiary);
  --el-pagination-hover-color: var(--color-primary);
}

:deep(.el-pagination.is-background .el-pager li) {
  background-color: var(--color-bg-tertiary);
  border-radius: 8px;
}

:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: var(--color-primary);
}
</style>
