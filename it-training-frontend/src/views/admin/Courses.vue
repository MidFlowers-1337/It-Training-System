<template>
  <div class="courses-page">
    <!-- 搜索和操作栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="课程名称/编码" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="全部" clearable>
            <el-option label="后端开发" value="BACKEND" />
            <el-option label="前端开发" value="FRONTEND" />
            <el-option label="数据库" value="DATABASE" />
            <el-option label="云计算" value="CLOUD" />
            <el-option label="人工智能" value="AI" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增课程
      </el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
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
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 0"
              type="success" link
              @click="handlePublish(row)"
            >发布</el-button>
            <el-button
              v-else
              type="warning" link
              @click="handleUnpublish(row)"
            >下架</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程编码" prop="code" v-if="!isEdit">
          <el-input v-model="form.code" placeholder="如: JAVA-001" />
        </el-form-item>
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="后端开发" value="BACKEND" />
            <el-option label="前端开发" value="FRONTEND" />
            <el-option label="数据库" value="DATABASE" />
            <el-option label="云计算" value="CLOUD" />
            <el-option label="人工智能" value="AI" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度等级" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="请选择难度">
            <el-option label="入门" :value="1" />
            <el-option label="初级" :value="2" />
            <el-option label="中级" :value="3" />
            <el-option label="高级" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="课时数" prop="durationHours">
          <el-input-number v-model="form.durationHours" :min="1" :max="500" />
        </el-form-item>
        <el-form-item label="技能标签" prop="tags">
          <el-input v-model="form.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
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
import { Plus } from '@element-plus/icons-vue'
import {
  getCourses, createCourse, updateCourse, deleteCourse,
  publishCourse, unpublishCourse
} from '@/api/course'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  category: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增课程')
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)

// 表单数据
const form = reactive({
  id: null,
  code: '',
  name: '',
  category: '',
  difficulty: 1,
  durationHours: 40,
  tags: '',
  description: ''
})

// 表单验证规则
const rules = {
  code: [
    { required: true, message: '请输入课程编码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择课程分类', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ]
}

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getCourses({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword,
      category: searchForm.category,
      status: searchForm.status
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载课程列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.category = ''
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
  dialogTitle.value = '新增课程'
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑课程'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 发布
const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm('确定要发布该课程吗？', '提示')
    await publishCourse(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
    }
  }
}

// 下架
const handleUnpublish = async (row) => {
  try {
    await ElMessageBox.confirm('确定要下架该课程吗？', '提示', { type: 'warning' })
    await unpublishCourse(row.id)
    ElMessage.success('下架成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下架失败:', error)
    }
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？删除后不可恢复！', '提示', { type: 'warning' })
    await deleteCourse(row.id)
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
      await updateCourse(form.id, {
        name: form.name,
        category: form.category,
        difficulty: form.difficulty,
        durationHours: form.durationHours,
        tags: form.tags,
        description: form.description
      })
      ElMessage.success('更新成功')
    } else {
      await createCourse(form)
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
  form.code = ''
  form.name = ''
  form.category = ''
  form.difficulty = 1
  form.durationHours = 40
  form.tags = ''
  form.description = ''
  formRef.value?.resetFields()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.courses-page {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 16px;
}

.table-card {
  min-height: 500px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
