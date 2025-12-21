<template>
  <div class="space-y-6">
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <Users class="w-4 h-4 text-primary" />
            用户管理
          </p>
          <h1 class="mt-3 text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">User Management</h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">Manage system users, roles, and permissions.</p>
        </div>

        <button type="button" class="btn btn-primary" @click="handleAdd">
          <Plus class="w-4 h-4 mr-2" />
          Add User
        </button>
      </div>
    </section>

    <!-- Filters -->
    <div class="glass rounded-3xl border border-border-color/60 p-5 md:p-6">
      <div class="flex flex-col md:flex-row gap-4 items-stretch">
        <div class="flex-1 min-w-[220px]">
          <el-input
            v-model="searchForm.keyword"
            placeholder="Search by username, name or phone"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <Search class="w-4 h-4 text-text-muted" />
            </template>
          </el-input>
        </div>
        <div class="w-full md:w-44">
          <el-select v-model="searchForm.role" placeholder="All Roles" clearable class="w-full" @change="handleSearch">
            <el-option label="Admin" value="ADMIN" />
            <el-option label="Instructor" value="INSTRUCTOR" />
            <el-option label="Student" value="STUDENT" />
          </el-select>
        </div>
        <div class="flex gap-2">
          <button type="button" class="btn btn-secondary" @click="handleSearch">Search</button>
        </div>
      </div>
    </div>

    <!-- Table -->
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
            <span class="badge" :class="getRoleBadge(row.role)">{{ getRoleName(row.role) }}</span>
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

    <!-- Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="space-y-4">
        <el-form-item v-if="!isEdit" label="Username" prop="username">
          <el-input v-model="form.username" placeholder="Enter username" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="Password" prop="password">
          <el-input v-model="form.password" type="password" placeholder="Enter password" show-password />
        </el-form-item>
        <el-form-item label="Full Name" prop="realName">
          <el-input v-model="form.realName" placeholder="Enter full name" />
        </el-form-item>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <el-form-item label="Phone" prop="phone">
            <el-input v-model="form.phone" placeholder="Enter phone" />
          </el-form-item>
          <el-form-item label="Email" prop="email">
            <el-input v-model="form.email" placeholder="Enter email" />
          </el-form-item>
        </div>
        <el-form-item label="Role" prop="role">
          <el-select v-model="form.role" placeholder="Select role" class="w-full">
            <el-option label="Admin" value="ADMIN" />
            <el-option label="Instructor" value="INSTRUCTOR" />
            <el-option label="Student" value="STUDENT" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="isEdit" label="Status" prop="status">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="Active" inactive-text="Disabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-3">
          <button type="button" class="btn btn-secondary" @click="dialogVisible = false">Cancel</button>
          <button type="button" class="btn btn-primary" @click="handleSubmit" :disabled="submitLoading">
            {{ submitLoading ? 'Saving...' : 'Confirm' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Users } from 'lucide-vue-next'
import { getUsers, createUser, updateUser, deleteUser, resetPassword } from '@/api/user'

const searchForm = reactive({
  keyword: '',
  role: '',
})

const tableData = ref([])
const loading = ref(false)
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0,
})

const dialogVisible = ref(false)
const dialogTitle = ref('Add User')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 'STUDENT',
  status: 1,
})

const rules = {
  username: [
    { required: true, message: 'Required', trigger: 'blur' },
    { min: 4, max: 20, message: '4-20 chars', trigger: 'blur' },
  ],
  password: [
    { required: true, message: 'Required', trigger: 'blur' },
    { min: 6, max: 20, message: '6-20 chars', trigger: 'blur' },
  ],
  realName: [{ required: true, message: 'Required', trigger: 'blur' }],
  role: [{ required: true, message: 'Required', trigger: 'change' }],
}

const getRoleName = (role) => {
  const map = { ADMIN: 'Admin', INSTRUCTOR: 'Instructor', STUDENT: 'Student' }
  return map[role] || role
}

const getRoleBadge = (role) => {
  const map = {
    ADMIN: 'bg-error/10 text-error border border-error/20',
    INSTRUCTOR: 'bg-info/10 text-info border border-info/20',
    STUDENT: 'bg-bg-tertiary text-text-secondary border border-border-color/60',
  }
  return map[role] || 'bg-bg-tertiary text-text-secondary border border-border-color/60'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUsers({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      role: searchForm.role,
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('Failed to load users:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleSizeChange = (val) => {
  pagination.size = val
  loadData()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = 'Add User'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = 'Edit User'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.prompt('Enter new password', 'Reset Password', {
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel',
      inputPattern: /^.{6,20}$/,
      inputErrorMessage: '6-20 characters',
    }).then(async ({ value }) => {
      await resetPassword(row.id, { newPassword: value })
      ElMessage.success('Password reset successful')
    })
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('Are you sure you want to delete this user?', 'Warning', {
      type: 'warning',
      confirmButtonText: 'Delete',
      cancelButtonText: 'Cancel',
    })
    await deleteUser(row.id)
    ElMessage.success('Deleted successfully')
    loadData()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          await updateUser(form.id, {
            realName: form.realName,
            phone: form.phone,
            email: form.email,
            role: form.role,
            status: form.status,
          })
          ElMessage.success('Updated successfully')
        } else {
          await createUser(form)
          ElMessage.success('Created successfully')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  form.id = null
  form.username = ''
  form.password = ''
  form.realName = ''
  form.phone = ''
  form.email = ''
  form.role = 'STUDENT'
  form.status = 1
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
})
</script>
