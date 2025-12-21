<template>
  <div class="space-y-6">
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
            <el-icon><Calendar /></el-icon>
            班期管理
          </p>
          <h1 class="mt-3 text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">Sessions</h1>
          <p class="mt-2 text-sm md:text-base text-text-secondary">管理课程班期、讲师与报名状态。</p>
        </div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增班期
        </el-button>
      </div>
    </section>

    <!-- 搜索栏 -->
    <div class="card p-6">
      <el-form :inline="true" :model="searchForm" class="flex flex-wrap gap-4 items-end">
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="全部" clearable filterable>
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="未开放" :value="0" />
            <el-option label="报名中" :value="1" />
            <el-option label="已满员" :value="2" />
            <el-option label="进行中" :value="3" />
            <el-option label="已结束" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
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
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 0"
              type="success" link
              @click="handleOpen(row)"
            >开放报名</el-button>
            <el-button
              v-else-if="row.status === 1"
              type="warning" link
              @click="handleClose(row)"
            >关闭报名</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="班期编码" prop="sessionCode" v-if="!isEdit">
          <el-input v-model="form.sessionCode" placeholder="如: JAVA-001-2024S1" />
        </el-form-item>
        <el-form-item label="所属课程" prop="courseId" v-if="!isEdit">
          <el-select v-model="form.courseId" placeholder="请选择课程" filterable>
            <el-option
              v-for="course in courseOptions"
              :key="course.id"
              :label="course.name"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="授课讲师" prop="instructorId">
          <el-select v-model="form.instructorId" placeholder="请选择讲师" filterable>
            <el-option
              v-for="instructor in instructorOptions"
              :key="instructor.id"
              :label="instructor.realName"
              :value="instructor.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开班日期" prop="startDate">
          <el-date-picker
            v-model="form.startDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择开班日期"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
            v-model="form.endDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择结束日期"
          />
        </el-form-item>
        <el-form-item label="上课时间" prop="schedule">
          <el-input v-model="form.schedule" placeholder="如: 每周六 9:00-12:00" />
        </el-form-item>
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="form.location" placeholder="线下地址或线上会议链接" />
        </el-form-item>
        <el-form-item label="最大名额" prop="maxCapacity">
          <el-input-number v-model="form.maxCapacity" :min="1" :max="500" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Plus } from '@element-plus/icons-vue'
import {
  getSessions, createSession, updateSession, deleteSession,
  openEnrollment, closeEnrollment
} from '@/api/session'
import { getPublishedCourses } from '@/api/course'
import { getUsers } from '@/api/user'

// 搜索表单
const searchForm = reactive({
  courseId: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 下拉选项
const courseOptions = ref([])
const instructorOptions = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增班期')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  sessionCode: '',
  courseId: null,
  instructorId: null,
  startDate: '',
  endDate: '',
  schedule: '',
  location: '',
  maxCapacity: 30
})

// 表单验证规则
const rules = {
  sessionCode: [
    { required: true, message: '请输入班期编码', trigger: 'blur' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  instructorId: [
    { required: true, message: '请选择讲师', trigger: 'change' }
  ],
  startDate: [
    { required: true, message: '请选择开班日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  maxCapacity: [
    { required: true, message: '请输入最大名额', trigger: 'blur' }
  ]
}

// 获取状态标签类型
const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'success',
    2: 'warning',
    3: 'primary',
    4: 'danger'
  }
  return map[status] || 'info'
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getSessions({
      page: pagination.page,
      size: pagination.size,
      courseId: searchForm.courseId,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载班期列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载课程选项
const loadCourseOptions = async () => {
  try {
    const res = await getPublishedCourses()
    courseOptions.value = res.data
  } catch (error) {
    console.error('加载课程选项失败:', error)
  }
}

// 加载讲师选项
const loadInstructorOptions = async () => {
  try {
    const res = await getUsers({ role: 'INSTRUCTOR', size: 100 })
    instructorOptions.value = res.data.records
  } catch (error) {
    console.error('加载讲师选项失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.courseId = ''
  searchForm.status = ''
  handleSearch()
}

// 分页
const handleSizeChange = (val) => {
  pagination.size = val
  loadData()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  loadData()
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增班期'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑班期'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 开放报名
const handleOpen = async (row) => {
  try {
    await ElMessageBox.confirm('确定要开放该班期报名吗？', '提示')
    await openEnrollment(row.id)
    ElMessage.success('已开放报名')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('开放报名失败:', error)
    }
  }
}

// 关闭报名
const handleClose = async (row) => {
  try {
    await ElMessageBox.confirm('确定要关闭该班期报名吗？', '提示', { type: 'warning' })
    await closeEnrollment(row.id)
    ElMessage.success('已关闭报名')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('关闭报名失败:', error)
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该班期吗？', '提示', { type: 'warning' })
    await deleteSession(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      await updateSession(form.id, {
        instructorId: form.instructorId,
        startDate: form.startDate,
        endDate: form.endDate,
        schedule: form.schedule,
        location: form.location,
        maxCapacity: form.maxCapacity
      })
      ElMessage.success('更新成功')
    } else {
      await createSession(form)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.id = null
  form.sessionCode = ''
  form.courseId = null
  form.instructorId = null
  form.startDate = ''
  form.endDate = ''
  form.schedule = ''
  form.location = ''
  form.maxCapacity = 30
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
  loadCourseOptions()
  loadInstructorOptions()
})
</script>
