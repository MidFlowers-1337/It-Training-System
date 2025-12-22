<template>
  <PageLayout max-width="2xl">
    <!-- 页面头部 -->
    <PageHeader
      title="课程管理"
      subtitle="维护课程信息、状态与内容"
    >
      <template #actions>
        <Button variant="primary" @click="handleAdd">
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19" />
              <line x1="5" y1="12" x2="19" y2="12" />
            </svg>
          </template>
          新增课程
        </Button>
      </template>
    </PageHeader>

    <!-- 搜索栏 -->
    <Section compact>
      <div class="card p-6">
        <div class="flex flex-wrap gap-4 items-end">
          <div class="w-64">
            <label class="block text-sm text-text-secondary mb-2">关键词</label>
            <Input
              v-model="searchForm.keyword"
              placeholder="课程名称/编码"
              clearable
            />
          </div>
          <div class="w-48">
            <label class="block text-sm text-text-secondary mb-2">分类</label>
            <Select
              v-model="searchForm.category"
              :options="categoryOptions"
              placeholder="全部"
              clearable
            />
          </div>
          <div class="w-48">
            <label class="block text-sm text-text-secondary mb-2">状态</label>
            <Select
              v-model="searchForm.status"
              :options="statusOptions"
              placeholder="全部"
              clearable
            />
          </div>
          <div class="flex gap-3">
            <Button variant="primary" @click="handleSearch">
              <template #icon-left>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8" />
                  <line x1="21" y1="21" x2="16.65" y2="16.65" />
                </svg>
              </template>
              搜索
            </Button>
            <Button variant="secondary" @click="handleReset">
              <template #icon-left>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="1 4 1 10 7 10" />
                  <path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10" />
                </svg>
              </template>
              重置
            </Button>
          </div>
        </div>
      </div>
    </Section>

    <!-- 数据表格 -->
    <Section compact>
      <div class="card overflow-hidden">
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="code" label="课程编码" width="140" />
          <el-table-column prop="name" label="课程名称" min-width="180" />
          <el-table-column prop="categoryName" label="分类" width="100" />
          <el-table-column prop="difficultyName" label="难度" width="80" />
          <el-table-column prop="durationHours" label="课时" width="80">
            <template #default="{ row }">{{ row.durationHours || '-' }}h</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <Tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.statusName }}
              </Tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="170" />
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <Button variant="ghost" size="sm" @click="handleEdit(row)">编辑</Button>
                <Button
                  v-if="row.status === 0"
                  variant="ghost"
                  size="sm"
                  @click="handlePublish(row)"
                >发布</Button>
                <Button
                  v-else
                  variant="ghost"
                  size="sm"
                  @click="handleUnpublish(row)"
                >下架</Button>
                <Button variant="danger" size="sm" @click="handleDelete(row)">删除</Button>
              </div>
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
        <FormItem v-if="!isEdit" label="课程编码" required :error="formErrors.code">
          <Input v-model="form.code" placeholder="如: JAVA-001" />
        </FormItem>
        <FormItem label="课程名称" required :error="formErrors.name">
          <Input v-model="form.name" placeholder="请输入课程名称" />
        </FormItem>
        <FormItem label="课程分类" required :error="formErrors.category">
          <Select
            v-model="form.category"
            :options="categoryOptions"
            placeholder="请选择分类"
          />
        </FormItem>
        <FormItem label="难度等级" required :error="formErrors.difficulty">
          <Select
            v-model="form.difficulty"
            :options="difficultyOptions"
            placeholder="请选择难度"
          />
        </FormItem>
        <FormItem label="课时数">
          <el-input-number v-model="form.durationHours" :min="1" :max="500" />
        </FormItem>
        <FormItem label="技能标签">
          <Input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </FormItem>
        <FormItem label="课程描述">
          <textarea
            v-model="form.description"
            rows="4"
            placeholder="请输入课程描述"
            class="w-full px-4 py-3 bg-bg-tertiary rounded-xl text-[17px] text-text-primary placeholder:text-text-muted focus:outline-none focus:ring-2 focus:ring-primary transition-all duration-fast resize-none"
          />
        </FormItem>
      </FormLayout>
    </Modal>

    <!-- Toast 容器 -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="toast.visible"
          class="fixed top-6 left-1/2 -translate-x-1/2 z-[9999] px-6 py-3 rounded-xl shadow-lg flex items-center gap-3"
          :class="toastClasses"
        >
          <svg v-if="toast.type === 'success'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
            <polyline points="22 4 12 14.01 9 11.01" />
          </svg>
          <svg v-else-if="toast.type === 'error'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="15" y1="9" x2="9" y2="15" />
            <line x1="9" y1="9" x2="15" y2="15" />
          </svg>
          <svg v-else-if="toast.type === 'warning'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
          <span class="text-sm font-medium">{{ toast.message }}</span>
        </div>
      </Transition>
    </Teleport>

    <!-- 确认对话框 -->
    <Modal
      v-model="confirmDialog.visible"
      :title="confirmDialog.title"
      width="400px"
      confirm-text="确定"
      cancel-text="取消"
      @confirm="confirmDialog.onConfirm"
      @cancel="confirmDialog.onCancel"
    >
      <div class="flex items-start gap-4">
        <div class="flex-shrink-0 w-10 h-10 rounded-full bg-warning/10 flex items-center justify-center">
          <svg class="w-5 h-5 text-warning" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z" />
            <line x1="12" y1="9" x2="12" y2="13" />
            <line x1="12" y1="17" x2="12.01" y2="17" />
          </svg>
        </div>
        <div class="flex-1">
          <p class="text-text-primary">{{ confirmDialog.message }}</p>
        </div>
      </div>
    </Modal>
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
  getCourses,
  createCourse,
  updateCourse,
  deleteCourse,
  publishCourse,
  unpublishCourse,
} from '@/api/course';

// ==================== 类型定义 ====================

/** 课程数据接口 */
interface Course {
  id: number;
  code: string;
  name: string;
  category: string;
  categoryName: string;
  difficulty: number;
  difficultyName: string;
  durationHours: number;
  tags: string;
  description: string;
  status: number;
  statusName: string;
  createdAt: string;
}

/** 搜索表单接口 */
interface SearchForm {
  keyword: string;
  category: string | null;
  status: number | null;
}

/** 课程表单接口 */
interface CourseForm {
  id: number | null;
  code: string;
  name: string;
  category: string | null;
  difficulty: number | null;
  durationHours: number;
  tags: string;
  description: string;
}

/** 表单错误接口 */
interface FormErrors {
  code: string;
  name: string;
  category: string;
  difficulty: string;
}

/** Toast 类型 */
type ToastType = 'success' | 'error' | 'warning' | 'info';

/** Toast 状态接口 */
interface ToastState {
  visible: boolean;
  message: string;
  type: ToastType;
}

/** 确认对话框状态接口 */
interface ConfirmDialogState {
  visible: boolean;
  title: string;
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
}

/** 分页接口 */
interface Pagination {
  page: number;
  size: number;
  total: number;
}

// ==================== 选项配置 ====================

/** 分类选项 */
const categoryOptions = [
  { label: '后端开发', value: 'BACKEND' },
  { label: '前端开发', value: 'FRONTEND' },
  { label: '数据库', value: 'DATABASE' },
  { label: '云计算', value: 'CLOUD' },
  { label: '人工智能', value: 'AI' },
  { label: '其他', value: 'OTHER' },
];

/** 状态选项 */
const statusOptions = [
  { label: '草稿', value: 0 },
  { label: '已发布', value: 1 },
];

/** 难度选项 */
const difficultyOptions = [
  { label: '入门', value: 1 },
  { label: '初级', value: 2 },
  { label: '中级', value: 3 },
  { label: '高级', value: 4 },
];

// ==================== 搜索相关 ====================

/** 搜索表单 */
const searchForm = reactive<SearchForm>({
  keyword: '',
  category: null,
  status: null,
});

// ==================== 表格相关 ====================

/** 表格数据 */
const tableData = ref<Course[]>([]);

/** 加载状态 */
const loading = ref(false);

/** 分页 */
const pagination = reactive<Pagination>({
  page: 1,
  size: 10,
  total: 0,
});

// ==================== 对话框相关 ====================

/** 对话框可见性 */
const dialogVisible = ref(false);

/** 对话框标题 */
const dialogTitle = ref('新增课程');

/** 是否编辑模式 */
const isEdit = ref(false);

/** 提交加载状态 */
const submitLoading = ref(false);

/** 表单数据 */
const form = reactive<CourseForm>({
  id: null,
  code: '',
  name: '',
  category: null,
  difficulty: 1,
  durationHours: 40,
  tags: '',
  description: '',
});

/** 表单错误 */
const formErrors = reactive<FormErrors>({
  code: '',
  name: '',
  category: '',
  difficulty: '',
});

// ==================== Toast 相关 ====================

/** Toast 状态 */
const toast = reactive<ToastState>({
  visible: false,
  message: '',
  type: 'success',
});

/** Toast 样式类 */
const toastClasses = computed(() => {
  const typeStyles: Record<ToastType, string> = {
    success: 'bg-success text-white',
    error: 'bg-error text-white',
    warning: 'bg-warning text-white',
    info: 'bg-info text-white',
  };
  return typeStyles[toast.type];
});

/** Toast 计时器 */
let toastTimer: ReturnType<typeof setTimeout> | null = null;

/**
 * 显示 Toast 消息
 * @param message 消息内容
 * @param type 消息类型
 * @param duration 持续时间（毫秒）
 */
const showToast = (message: string, type: ToastType = 'success', duration = 3000) => {
  if (toastTimer) {
    clearTimeout(toastTimer);
  }
  toast.message = message;
  toast.type = type;
  toast.visible = true;
  toastTimer = setTimeout(() => {
    toast.visible = false;
  }, duration);
};

// ==================== 确认对话框相关 ====================

/** 确认对话框状态 */
const confirmDialog = reactive<ConfirmDialogState>({
  visible: false,
  title: '提示',
  message: '',
  onConfirm: () => {},
  onCancel: () => {},
});

/**
 * 显示确认对话框
 * @param message 确认消息
 * @param title 对话框标题
 * @returns Promise
 */
const showConfirmDialog = (message: string, title = '提示'): Promise<boolean> => {
  return new Promise((resolve) => {
    confirmDialog.title = title;
    confirmDialog.message = message;
    confirmDialog.onConfirm = () => {
      confirmDialog.visible = false;
      resolve(true);
    };
    confirmDialog.onCancel = () => {
      confirmDialog.visible = false;
      resolve(false);
    };
    confirmDialog.visible = true;
  });
};

// ==================== 数据操作 ====================

/**
 * 加载课程数据
 */
const loadData = async () => {
  loading.value = true;
  try {
    const res = await getCourses({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      category: searchForm.category,
      status: searchForm.status,
    });
    tableData.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    console.error('加载课程列表失败:', error);
    showToast('加载课程列表失败', 'error');
  } finally {
    loading.value = false;
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
 * 重置搜索
 */
const handleReset = () => {
  searchForm.keyword = '';
  searchForm.category = null;
  searchForm.status = null;
  handleSearch();
};

/**
 * 分页大小变化
 */
const handleSizeChange = (val: number) => {
  pagination.size = val;
  loadData();
};

/**
 * 页码变化
 */
const handleCurrentChange = (val: number) => {
  pagination.page = val;
  loadData();
};

// ==================== 表单操作 ====================

/**
 * 新增课程
 */
const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '新增课程';
  dialogVisible.value = true;
};

/**
 * 编辑课程
 */
const handleEdit = (row: Course) => {
  isEdit.value = true;
  dialogTitle.value = '编辑课程';
  Object.assign(form, {
    id: row.id,
    code: row.code,
    name: row.name,
    category: row.category,
    difficulty: row.difficulty,
    durationHours: row.durationHours,
    tags: row.tags,
    description: row.description,
  });
  dialogVisible.value = true;
};

/**
 * 发布课程
 */
const handlePublish = async (row: Course) => {
  const confirmed = await showConfirmDialog('确定要发布该课程吗？');
  if (!confirmed) return;

  try {
    await publishCourse(row.id);
    showToast('发布成功', 'success');
    loadData();
  } catch (error) {
    console.error('发布失败:', error);
    showToast('发布失败', 'error');
  }
};

/**
 * 下架课程
 */
const handleUnpublish = async (row: Course) => {
  const confirmed = await showConfirmDialog('确定要下架该课程吗？');
  if (!confirmed) return;

  try {
    await unpublishCourse(row.id);
    showToast('下架成功', 'success');
    loadData();
  } catch (error) {
    console.error('下架失败:', error);
    showToast('下架失败', 'error');
  }
};

/**
 * 删除课程
 */
const handleDelete = async (row: Course) => {
  const confirmed = await showConfirmDialog('确定要删除该课程吗？删除后不可恢复！');
  if (!confirmed) return;

  try {
    await deleteCourse(row.id);
    showToast('删除成功', 'success');
    loadData();
  } catch (error) {
    console.error('删除失败:', error);
    showToast('删除失败', 'error');
  }
};

/**
 * 验证表单
 */
const validateForm = (): boolean => {
  let isValid = true;

  // 重置错误
  formErrors.code = '';
  formErrors.name = '';
  formErrors.category = '';
  formErrors.difficulty = '';

  // 验证课程编码（新增时必填）
  if (!isEdit.value && !form.code.trim()) {
    formErrors.code = '请输入课程编码';
    isValid = false;
  }

  // 验证课程名称
  if (!form.name.trim()) {
    formErrors.name = '请输入课程名称';
    isValid = false;
  }

  // 验证课程分类
  if (!form.category) {
    formErrors.category = '请选择课程分类';
    isValid = false;
  }

  // 验证难度等级
  if (!form.difficulty) {
    formErrors.difficulty = '请选择难度等级';
    isValid = false;
  }

  return isValid;
};

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!validateForm()) {
    return;
  }

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateCourse(form.id!, {
        name: form.name,
        category: form.category,
        difficulty: form.difficulty,
        durationHours: form.durationHours,
        tags: form.tags,
        description: form.description,
      });
      showToast('更新成功', 'success');
    } else {
      await createCourse({
        code: form.code,
        name: form.name,
        category: form.category,
        difficulty: form.difficulty,
        durationHours: form.durationHours,
        tags: form.tags,
        description: form.description,
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
};

/**
 * 重置表单
 */
const resetForm = () => {
  form.id = null;
  form.code = '';
  form.name = '';
  form.category = null;
  form.difficulty = 1;
  form.durationHours = 40;
  form.tags = '';
  form.description = '';

  // 重置错误
  formErrors.code = '';
  formErrors.name = '';
  formErrors.category = '';
  formErrors.difficulty = '';
};

// ==================== 生命周期 ====================

onMounted(() => {
  loadData();
});
</script>

<style scoped>
/* Toast 动画 */
.toast-enter-active {
  transition: all 0.3s ease-out;
}

.toast-leave-active {
  transition: all 0.2s ease-in;
}

.toast-enter-from {
  opacity: 0;
  transform: translate(-50%, -20px);
}

.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -10px);
}
</style>
