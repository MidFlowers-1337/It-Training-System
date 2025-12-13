<template>
  <div class="enrollments-page">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="班期">
          <el-select v-model="searchForm.sessionId" placeholder="全部" clearable filterable>
            <el-option
              v-for="session in sessionOptions"
              :key="session.id"
              :label="`${session.sessionCode} - ${session.courseName || ''}`"
              :value="session.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="已报名" :value="0" />
            <el-option label="已取消" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="studentEmail" label="邮箱" width="180" />
        <el-table-column prop="studentPhone" label="手机号" width="130" />
        <el-table-column prop="sessionCode" label="班期编码" width="140" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="enrolledAt" label="报名时间" width="170">
          <template #default="{ row }">
            {{ formatDateTime(row.enrolledAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="cancelReason" label="取消原因" min-width="150">
          <template #default="{ row }">
            <span v-if="row.status === 1">
              {{ row.cancelReason || '无' }}
              <br/>
              <small class="text-muted">{{ formatDateTime(row.canceledAt) }}</small>
            </span>
            <span v-else>-</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import { getEnrollments } from '@/api/enrollment'
import { getSessions } from '@/api/session'

// 搜索表单
const searchForm = reactive({
  sessionId: '',
  status: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 下拉选项
const sessionOptions = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await getEnrollments({
      page: pagination.page,
      size: pagination.size,
      sessionId: searchForm.sessionId || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载报名列表失败:', error)
    ElMessage.error('加载报名列表失败')
  } finally {
    loading.value = false
  }
}

// 加载班期选项
const loadSessionOptions = async () => {
  try {
    const res = await getSessions({ size: 100 })
    sessionOptions.value = res.data.records
  } catch (error) {
    console.error('加载班期选项失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadData()
}

// 重置
const handleReset = () => {
  searchForm.sessionId = ''
  searchForm.status = ''
  handleSearch()
}

// 导出
const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }

  const headers = ['ID', '用户名', '真实姓名', '邮箱', '手机号', '班期编码', '课程名称', '报名时间', '状态', '取消原因', '取消时间']
  const rows = tableData.value.map(row => [
    row.id,
    row.userName || '',
    row.realName || '',
    row.studentEmail || '',
    row.studentPhone || '',
    row.sessionCode || '',
    row.courseName || '',
    formatDateTime(row.enrolledAt),
    row.statusName || '',
    row.cancelReason || '',
    row.status === 1 ? formatDateTime(row.canceledAt) : ''
  ])

  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
  ].join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `报名列表_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)

  ElMessage.success('导出成功')
}

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
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

onMounted(() => {
  loadData()
  loadSessionOptions()
})
</script>

<style scoped>
.enrollments-page {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  min-height: 500px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.text-muted {
  color: #909399;
  font-size: 12px;
}
</style>
