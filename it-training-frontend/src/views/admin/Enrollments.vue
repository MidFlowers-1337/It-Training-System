<template>
  <div class="space-y-6">
    <section class="page-hero glass p-8 md:p-10">
      <div class="absolute inset-0 pointer-events-none" style="background: var(--gradient-hero)"></div>
      <div class="relative">
        <p class="inline-flex items-center gap-2 text-sm text-text-secondary">
          <Users class="w-4 h-4 text-primary" />
          报名管理
        </p>
        <h1 class="mt-3 text-3xl md:text-4xl font-semibold tracking-tight text-text-primary">Enrollments</h1>
        <p class="mt-2 text-sm md:text-base text-text-secondary">跟踪和管理学员课程报名情况。</p>
      </div>
    </section>

    <!-- Filters -->
    <div class="glass rounded-3xl border border-border-color/60 p-5 md:p-6 flex flex-wrap gap-4">
      <div class="flex-1 min-w-[200px]">
        <el-select
          v-model="searchForm.sessionId"
          placeholder="按班期筛选"
          clearable
          filterable
          class="w-full"
        >
          <el-option
            v-for="session in sessionOptions"
            :key="session.id"
            :label="`${session.sessionCode} - ${session.courseName || ''}`"
            :value="session.id"
          />
        </el-select>
      </div>
      <div class="w-40">
        <el-select
          v-model="searchForm.status"
          placeholder="全部状态"
          clearable
          class="w-full"
        >
          <el-option label="已报名" :value="0" />
          <el-option label="已取消" :value="1" />
        </el-select>
      </div>
      <div class="flex gap-2">
        <button type="button" @click="handleSearch" class="btn btn-secondary">
          <MagnifyingGlassIcon class="w-5 h-5" />
          搜索
        </button>
        <button type="button" @click="handleExport" class="btn btn-secondary">
          <ArrowDownTrayIcon class="w-5 h-5" />
          导出
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="card overflow-hidden">
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
            <span class="badge" :class="getStatusBadge(row.status)">
              {{ row.statusName }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download as ArrowDownTrayIcon, Search as MagnifyingGlassIcon, Users } from 'lucide-vue-next'
import { getEnrollments } from '@/api/enrollment'
import { getSessions } from '@/api/session'

const searchForm = reactive({
  sessionId: '',
  status: ''
})

const tableData = ref([])
const loading = ref(false)
const sessionOptions = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

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
    console.error('Failed to load enrollments:', error)
  } finally {
    loading.value = false
  }
}

const loadSessionOptions = async () => {
  try {
    const res = await getSessions({ size: 100 })
    sessionOptions.value = res.data.records
  } catch (error) {
    console.error('Failed to load sessions:', error)
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleExport = () => {
  if (tableData.value.length === 0) {
    ElMessage.warning('暂无数据可导出')
    return
  }
  
  const headers = ['ID', 'Username', 'Name', 'Email', 'Phone', 'Session', 'Course', 'Enrolled Date', 'Status']
  const rows = tableData.value.map(row => [
    row.id,
    row.userName || '',
    row.realName || '',
    row.studentEmail || '',
    row.studentPhone || '',
    row.sessionCode || '',
    row.courseName || '',
    formatDateTime(row.enrolledAt),
    row.statusName || ''
  ])

  const csvContent = [
    headers.join(','),
    ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
  ].join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `enrollments_${new Date().toISOString().slice(0, 10)}.csv`
  link.click()
  URL.revokeObjectURL(link.href)
  
  ElMessage.success('导出成功')
}

const getStatusBadge = (status) => {
  if (status === 0) return 'bg-success/10 text-success border border-success/20'
  return 'bg-bg-tertiary text-text-secondary border border-border-color/60'
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

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
