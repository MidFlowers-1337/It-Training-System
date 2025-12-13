<template>
  <div class="my-sessions">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的班期</span>
        </div>
      </template>

      <el-table :data="sessions" v-loading="loading" stripe>
        <el-table-column prop="sessionCode" label="班期编码" width="140" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="schedule" label="上课时间" width="150" />
        <el-table-column prop="location" label="上课地点" width="120" />
        <el-table-column label="报名情况" width="120">
          <template #default="{ row }">
            <el-tag :type="row.currentEnrollment >= row.maxCapacity ? 'danger' : 'success'">
              {{ row.currentEnrollment }} / {{ row.maxCapacity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewStudents(row)">
              <el-icon><User /></el-icon>
              学员名单
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && sessions.length === 0" description="暂无班期数据" />
    </el-card>

    <!-- 学员名单对话框 -->
    <el-dialog
      v-model="studentDialogVisible"
      :title="`学员名单 - ${currentSession?.sessionCode || ''}`"
      width="800px"
      destroy-on-close
    >
      <el-table :data="students" v-loading="studentLoading" stripe>
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
            <el-tag :type="getEnrollStatusType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!studentLoading && students.length === 0" description="暂无学员报名" />

      <template #footer>
        <el-button @click="studentDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="exportStudents" :disabled="students.length === 0">
          <el-icon><Download /></el-icon>
          导出名单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Download } from '@element-plus/icons-vue'
import { getMySessionsAsInstructor } from '@/api/session'
import { listEnrollments } from '@/api/enrollment'

const loading = ref(false)
const sessions = ref([])

const studentDialogVisible = ref(false)
const studentLoading = ref(false)
const students = ref([])
const currentSession = ref(null)

// 加载我的班期
const loadSessions = async () => {
  loading.value = true
  try {
    const res = await getMySessionsAsInstructor()
    sessions.value = res.data || []
  } catch (error) {
    console.error('加载班期失败:', error)
    ElMessage.error('加载班期失败')
  } finally {
    loading.value = false
  }
}

// 查看学员名单
const viewStudents = async (session) => {
  currentSession.value = session
  studentDialogVisible.value = true
  studentLoading.value = true

  try {
    const res = await listEnrollments(1, 100, null, session.id, null)
    students.value = res.data?.records || []
  } catch (error) {
    console.error('加载学员名单失败:', error)
    ElMessage.error('加载学员名单失败')
  } finally {
    studentLoading.value = false
  }
}

// 导出学员名单
const exportStudents = () => {
  if (students.value.length === 0) {
    ElMessage.warning('暂无学员数据可导出')
    return
  }

  // 简单的CSV导出
  const headers = ['学员姓名', '用户名', '邮箱', '手机号', '报名时间', '状态']
  const rows = students.value.map(s => [
    s.studentName || '',
    s.studentUsername || '',
    s.studentEmail || '',
    s.studentPhone || '',
    formatDateTime(s.enrollTime),
    s.statusName || ''
  ])

  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
  ].join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `学员名单_${currentSession.value?.sessionCode || 'export'}.csv`
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

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'info',      // 未开放
    1: 'success',   // 报名中
    2: 'warning',   // 进行中
    3: ''           // 已结束
  }
  return typeMap[status] || 'info'
}

// 获取报名状态类型
const getEnrollStatusType = (status) => {
  const typeMap = {
    0: 'warning',   // 待确认
    1: 'success',   // 已确认
    2: 'danger'     // 已取消
  }
  return typeMap[status] || 'info'
}

onMounted(() => {
  loadSessions()
})
</script>

<style scoped>
.my-sessions {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-table {
  margin-top: 0;
}
</style>
