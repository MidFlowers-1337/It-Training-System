<template>
  <PageLayout max-width="2xl">
    <!-- 页面头部 -->
    <PageHeader
      title="User Management"
      subtitle="Manage system users, roles, and permissions."
    >
      <template #actions>
        <Button variant="primary" @click="handleAdd">
          <template #icon-left>
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19" />
              <line x1="5" y1="12" x2="19" y2="12" />
            </svg>
          </template>
          Add User
        </Button>
      </template>
    </PageHeader>

    <!-- 筛选区 -->
    <Section compact>
      <div class="glass rounded-2xl border border-border-color/60 p-5">
        <div class="flex flex-col md:flex-row gap-4 items-stretch">
          <div class="flex-1 min-w-[220px]">
            <Input
              v-model="searchForm.keyword"
              placeholder="Search by username, name or phone"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #icon-left>
                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="11" cy="11" r="8" />
                  <line x1="21" y1="21" x2="16.65" y2="16.65" />
                </svg>
              </template>
            </Input>
          </div>
          <div class="w-full md:w-44">
            <Select
              v-model="searchForm.role"
              :options="roleOptions"
              placeholder="All Roles"
              clearable
              @change="handleSearch"
            />
          </div>
          <div class="flex gap-2">
            <Button variant="secondary" @click="handleSearch">
              Search
            </Button>
          </div>
        </div>
      </div>
    </Section>

    <!-- 表格区域 -->
    <Section compact>
      <div class="card overflow-hidden">
        <el-table :data="tableData" v-loading="loading" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="Username" width="180">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-full bg-primary/10 border border-primary/20 flex items-center justify-center text-primary font-semibold text-sm">
                  {{ row.username.charAt(0).toUpperCase() }}
                </div>
                <span class="font-medium text-text-primary">{{ row.username }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="realName" label="Name" width="160" />
          <el-table-column prop="role" label="Role" width="140">
            <template #default="{ row }">
              <Tag :type="getRoleTagType(row.role)">
                {{ getRoleName(row.role) }}
              </Tag>
            </template>
          </el-table-column>
          <el-table-column prop="email" label="Contact" min-width="220">
            <template #default="{ row }">
              <div class="text-sm">
                <div class="text-text-primary">{{ row.email }}</div>
                <div class="text-text-muted text-xs">{{ row.phone }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="Status" width="120">
            <template #default="{ row }">
              <div class="flex items-center gap-2">
                <div :class="['w-2 h-2 rounded-full', row.status === 1 ? 'bg-success' : 'bg-error']"></div>
                <span class="text-sm text-text-secondary">{{ row.status === 1 ? 'Active' : 'Disabled' }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="Actions" width="220" fixed="right">
            <template #default="{ row }">
              <div class="flex items-center gap-3">
                <button type="button" class="text-text-secondary hover:text-text-primary transition-colors" @click="handleEdit(row)">
                  Edit
                </button>
                <button type="button" class="text-text-secondary hover:text-text-primary transition-colors" @click="handleResetPwd(row)">
                  Reset Pwd
                </button>
                <button type="button" class="text-error hover:text-error/80 transition-colors" @click="handleDelete(row)">
                  Delete
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <div class="p-4 border-t border-border-color/60 flex justify-end">
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

    <!-- 用户表单弹窗 -->
    <Modal
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      :confirm-loading="submitLoading"
      confirm-text="Confirm"
      cancel-text="Cancel"
      @confirm="handleSubmit"
      @cancel="resetForm"
      @close="resetForm"
    >
      <FormLayout>
        <FormItem v-if="!isEdit" label="Username" required :error="formErrors.username">
          <Input v-model="form.username" placeholder="Enter username" />
        </FormItem>
        <FormItem v-if="!isEdit" label="Password" required :error="formErrors.password">
          <Input v-model="form.password" type="password" placeholder="Enter password" />
        </FormItem>
        <FormItem label="Full Name" required :error="formErrors.realName">
          <Input v-model="form.realName" placeholder="Enter full name" />
        </FormItem>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormItem label="Phone" :error="formErrors.phone">
            <Input v-model="form.phone" placeholder="Enter phone" />
          </FormItem>
          <FormItem label="Email" :error="formErrors.email">
            <Input v-model="form.email" placeholder="Enter email" />
          </FormItem>
        </div>
        <FormItem label="Role" required :error="formErrors.role">
          <Select
            v-model="form.role"
            :options="roleOptions"
            placeholder="Select role"
          />
        </FormItem>
        <FormItem v-if="isEdit" label="Status">
          <div class="flex items-center gap-3 py-2">
            <Switch v-model="statusBool" @change="handleStatusChange" />
            <span class="text-sm text-text-secondary">
              {{ form.status === 1 ? 'Active' : 'Disabled' }}
            </span>
          </div>
        </FormItem>
      </FormLayout>
    </Modal>

    <!-- 重置密码弹窗 -->
    <Modal
      v-model="promptDialogVisible"
      title="Reset Password"
      width="400px"
      :confirm-loading="promptLoading"
      confirm-text="Confirm"
      cancel-text="Cancel"
      @confirm="handlePromptConfirm"
      @cancel="promptDialogVisible = false"
    >
      <FormLayout>
        <FormItem label="Enter new password" required :error="promptError">
          <Input
            v-model="promptValue"
            type="password"
            placeholder="6-20 characters"
          />
        </FormItem>
      </FormLayout>
    </Modal>

    <!-- 确认弹窗 -->
    <Modal
      v-model="confirmDialogVisible"
      :title="confirmDialogTitle"
      width="400px"
      :confirm-loading="confirmLoading"
      confirm-text="Delete"
      cancel-text="Cancel"
      @confirm="handleConfirmDialogConfirm"
      @cancel="confirmDialogVisible = false"
    >
      <p class="text-text-secondary">{{ confirmDialogMessage }}</p>
    </Modal>

    <!-- Toast 消息 -->
    <Teleport to="body">
      <Transition name="toast-fade">
        <div
          v-if="toastVisible"
          class="fixed top-6 left-1/2 -translate-x-1/2 z-[9999] px-5 py-3 rounded-xl shadow-lg"
          :class="toastTypeClasses"
        >
          <div class="flex items-center gap-2">
            <!-- 成功图标 -->
            <svg v-if="toastType === 'success'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 6L9 17l-5-5" />
            </svg>
            <!-- 错误图标 -->
            <svg v-if="toastType === 'error'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="15" y1="9" x2="9" y2="15" />
              <line x1="9" y1="9" x2="15" y2="15" />
            </svg>
            <!-- 警告图标 -->
            <svg v-if="toastType === 'warning'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
              <line x1="12" y1="9" x2="12" y2="13" />
              <line x1="12" y1="17" x2="12.01" y2="17" />
            </svg>
            <!-- 信息图标 -->
            <svg v-if="toastType === 'info'" class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="16" x2="12" y2="12" />
              <line x1="12" y1="8" x2="12.01" y2="8" />
            </svg>
            <span class="font-medium">{{ toastMessage }}</span>
          </div>
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
  Switch,
} from '@/design-system';
import { getUsers, createUser, updateUser, deleteUser, resetPassword } from '@/api/user';

// ==================== 类型定义 ====================

/** 用户角色枚举 */
type UserRole = 'ADMIN' | 'INSTRUCTOR' | 'STUDENT';

/** 用户状态 */
type UserStatus = 0 | 1;

/** 用户实体 */
interface User {
  id: number;
  username: string;
  realName: string;
  phone: string;
  email: string;
  role: UserRole;
  status: UserStatus;
}

/** 用户表单数据 */
interface UserForm {
  id: number | null;
  username: string;
  password: string;
  realName: string;
  phone: string;
  email: string;
  role: UserRole;
  status: UserStatus;
}

/** 搜索表单 */
interface SearchForm {
  keyword: string;
  role: string;
}

/** 分页数据 */
interface Pagination {
  page: number;
  size: number;
  total: number;
}

/** 表单错误 */
interface FormErrors {
  username: string;
  password: string;
  realName: string;
  phone: string;
  email: string;
  role: string;
}

/** Toast 类型 */
type ToastType = 'success' | 'error' | 'warning' | 'info';

/** Select 选项 */
interface SelectOption {
  label: string;
  value: string;
}

// ==================== 响应式状态 ====================

// 搜索表单
const searchForm = reactive<SearchForm>({
  keyword: '',
  role: '',
});

// 角色选项
const roleOptions: SelectOption[] = [
  { label: 'Admin', value: 'ADMIN' },
  { label: 'Instructor', value: 'INSTRUCTOR' },
  { label: 'Student', value: 'STUDENT' },
];

// 表格数据
const tableData = ref<User[]>([]);
const loading = ref(false);
const pagination = reactive<Pagination>({
  page: 1,
  size: 10,
  total: 0,
});

// 表单弹窗状态
const dialogVisible = ref(false);
const dialogTitle = ref('Add User');
const isEdit = ref(false);
const submitLoading = ref(false);

// 表单数据
const form = reactive<UserForm>({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'STUDENT',
  status: 1,
});

// 表单错误
const formErrors = reactive<FormErrors>({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: '',
});

// 状态开关绑定（Switch 组件需要 boolean）
const statusBool = computed({
  get: () => form.status === 1,
  set: (val: boolean) => {
    form.status = val ? 1 : 0;
  },
});

// 处理状态变更
const handleStatusChange = (val: boolean) => {
  form.status = val ? 1 : 0;
};

// Toast 状态
const toastVisible = ref(false);
const toastMessage = ref('');
const toastType = ref<ToastType>('success');
let toastTimer: ReturnType<typeof setTimeout> | null = null;

// 确认弹窗状态
const confirmDialogVisible = ref(false);
const confirmDialogTitle = ref('');
const confirmDialogMessage = ref('');
const confirmLoading = ref(false);
let confirmResolve: ((value: boolean) => void) | null = null;

// Prompt 弹窗状态
const promptDialogVisible = ref(false);
const promptValue = ref('');
const promptError = ref('');
const promptLoading = ref(false);
let promptResolve: ((value: string | null) => void) | null = null;
let currentResetUser: User | null = null;

// ==================== Toast 函数 ====================

const toastTypeClasses = computed(() => {
  const classes: Record<ToastType, string> = {
    success: 'bg-success text-white',
    error: 'bg-error text-white',
    warning: 'bg-warning text-white',
    info: 'bg-info text-white',
  };
  return classes[toastType.value];
});

const showToast = (message: string, type: ToastType = 'success', duration = 3000) => {
  if (toastTimer) {
    clearTimeout(toastTimer);
  }
  toastMessage.value = message;
  toastType.value = type;
  toastVisible.value = true;
  toastTimer = setTimeout(() => {
    toastVisible.value = false;
  }, duration);
};

// ==================== 确认弹窗函数 ====================

const confirmDialog = (title: string, message: string): Promise<boolean> => {
  return new Promise((resolve) => {
    confirmDialogTitle.value = title;
    confirmDialogMessage.value = message;
    confirmDialogVisible.value = true;
    confirmResolve = resolve;
  });
};

const handleConfirmDialogConfirm = async () => {
  if (confirmResolve) {
    confirmLoading.value = true;
    confirmResolve(true);
    confirmResolve = null;
    confirmLoading.value = false;
    confirmDialogVisible.value = false;
  }
};

// ==================== Prompt 弹窗函数 ====================

const promptDialog = (user: User): Promise<string | null> => {
  return new Promise((resolve) => {
    promptValue.value = '';
    promptError.value = '';
    promptDialogVisible.value = true;
    promptResolve = resolve;
    currentResetUser = user;
  });
};

const handlePromptConfirm = async () => {
  // 验证密码
  if (!promptValue.value) {
    promptError.value = 'Password is required';
    return;
  }
  if (promptValue.value.length < 6 || promptValue.value.length > 20) {
    promptError.value = '6-20 characters required';
    return;
  }

  if (promptResolve && currentResetUser) {
    promptLoading.value = true;
    try {
      await resetPassword(currentResetUser.id, { newPassword: promptValue.value });
      showToast('Password reset successful', 'success');
      promptDialogVisible.value = false;
      promptResolve(promptValue.value);
    } catch (error) {
      console.error(error);
      showToast('Failed to reset password', 'error');
    } finally {
      promptLoading.value = false;
      promptResolve = null;
      currentResetUser = null;
    }
  }
};

// ==================== 工具函数 ====================

const getRoleName = (role: UserRole): string => {
  const map: Record<UserRole, string> = {
    ADMIN: 'Admin',
    INSTRUCTOR: 'Instructor',
    STUDENT: 'Student',
  };
  return map[role] || role;
};

const getRoleTagType = (role: UserRole): 'danger' | 'info' | 'default' => {
  const map: Record<UserRole, 'danger' | 'info' | 'default'> = {
    ADMIN: 'danger',
    INSTRUCTOR: 'info',
    STUDENT: 'default',
  };
  return map[role] || 'default';
};

// ==================== 表单验证 ====================

const validateForm = (): boolean => {
  let isValid = true;

  // 重置错误
  Object.keys(formErrors).forEach((key) => {
    formErrors[key as keyof FormErrors] = '';
  });

  if (!isEdit.value) {
    if (!form.username) {
      formErrors.username = 'Required';
      isValid = false;
    } else if (form.username.length < 4 || form.username.length > 20) {
      formErrors.username = '4-20 chars';
      isValid = false;
    }

    if (!form.password) {
      formErrors.password = 'Required';
      isValid = false;
    } else if (form.password.length < 6 || form.password.length > 20) {
      formErrors.password = '6-20 chars';
      isValid = false;
    }
  }

  if (!form.realName) {
    formErrors.realName = 'Required';
    isValid = false;
  }

  if (!form.role) {
    formErrors.role = 'Required';
    isValid = false;
  }

  return isValid;
};

// ==================== 数据操作 ====================

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getUsers({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      role: searchForm.role,
    });
    tableData.value = res.data.records;
    pagination.total = res.data.total;
  } catch (error) {
    console.error('Failed to load users:', error);
    showToast('Failed to load users', 'error');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.page = 1;
  loadData();
};

const handleSizeChange = (val: number) => {
  pagination.size = val;
  loadData();
};

const handleCurrentChange = (val: number) => {
  pagination.page = val;
  loadData();
};

// ==================== CRUD 操作 ====================

const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = 'Add User';
  dialogVisible.value = true;
};

const handleEdit = (row: User) => {
  isEdit.value = true;
  dialogTitle.value = 'Edit User';
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handleResetPwd = async (row: User) => {
  await promptDialog(row);
};

const handleDelete = async (row: User) => {
  const confirmed = await confirmDialog('Warning', 'Are you sure you want to delete this user?');
  if (confirmed) {
    try {
      await deleteUser(row.id);
      showToast('Deleted successfully', 'success');
      loadData();
    } catch (error) {
      console.error(error);
      showToast('Failed to delete user', 'error');
    }
  }
};

const handleSubmit = async () => {
  if (!validateForm()) return;

  submitLoading.value = true;
  try {
    if (isEdit.value) {
      await updateUser(form.id as number, {
        realName: form.realName,
        phone: form.phone,
        email: form.email,
        role: form.role,
        status: form.status,
      });
      showToast('Updated successfully', 'success');
    } else {
      await createUser(form);
      showToast('Created successfully', 'success');
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    console.error(error);
    showToast(isEdit.value ? 'Failed to update user' : 'Failed to create user', 'error');
  } finally {
    submitLoading.value = false;
  }
};

const resetForm = () => {
  form.id = null;
  form.username = '';
  form.password = '';
  form.realName = '';
  form.phone = '';
  form.email = '';
  form.role = 'STUDENT';
  form.status = 1;

  // 重置错误
  Object.keys(formErrors).forEach((key) => {
    formErrors[key as keyof FormErrors] = '';
  });
};

// ==================== 生命周期 ====================

onMounted(() => {
  loadData();
});
</script>

<style scoped>
/* Toast 动画 */
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.3s ease;
}

.toast-fade-enter-from,
.toast-fade-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}
</style>
