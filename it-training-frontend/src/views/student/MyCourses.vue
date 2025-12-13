<template>
  <div class="my-courses" v-loading="loading">
    <div class="page-header">
      <h2>我的课程</h2>
      <el-button type="primary" @click="goToCourses">去选课</el-button>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="进行中" name="active">
        <div v-if="activeCourses.length === 0" class="empty">
          <el-empty description="暂无进行中的课程">
            <el-button type="primary" @click="goToCourses">去选课</el-button>
          </el-empty>
        </div>
        <div v-else class="course-list">
          <el-card
            v-for="enrollment in activeCourses"
            :key="enrollment.id"
            class="course-card"
          >
            <div class="card-content">
              <div class="course-info">
                <h3>{{ enrollment.courseName }}</h3>
                <p class="session-code">班期: {{ enrollment.sessionCode }}</p>
                <p class="session-date">
                  上课时间: {{ enrollment.startDate }} ~ {{ enrollment.endDate }}
                </p>
                <p class="enroll-time">报名时间: {{ formatTime(enrollment.enrolledAt) }}</p>
              </div>
              <div class="card-actions">
                <el-button type="danger" text @click="handleCancel(enrollment)">
                  取消报名
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>

      <el-tab-pane label="已取消" name="canceled">
        <div v-if="canceledCourses.length === 0" class="empty">
          <el-empty description="暂无已取消的报名" />
        </div>
        <div v-else class="course-list">
          <el-card
            v-for="enrollment in canceledCourses"
            :key="enrollment.id"
            class="course-card canceled"
          >
            <div class="card-content">
              <div class="course-info">
                <h3>{{ enrollment.courseName }}</h3>
                <p class="session-code">班期: {{ enrollment.sessionCode }}</p>
                <p class="cancel-time">取消时间: {{ formatTime(enrollment.canceledAt) }}</p>
                <p class="cancel-reason">取消原因: {{ enrollment.cancelReason || '无' }}</p>
              </div>
            </div>
          </el-card>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyEnrollments, cancelEnrollment } from '@/api/enrollment'

const router = useRouter()

const activeTab = ref('active')
const activeCourses = ref([])
const canceledCourses = ref([])
const loading = ref(false)

const loadEnrollments = async () => {
  loading.value = true
  try {
    const res = await getMyEnrollments()
    const enrollments = res.data || []
    activeCourses.value = enrollments.filter(e => e.status === 0)
    canceledCourses.value = enrollments.filter(e => e.status === 1)
  } catch (error) {
    console.error('加载报名列表失败:', error)
    ElMessage.error('加载报名列表失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = async (enrollment) => {
  try {
    const { value } = await ElMessageBox.prompt(
      `确定要取消「${enrollment.sessionCode}」班期的报名吗？\n请输入取消原因（选填）`,
      '取消报名',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '返回',
        type: 'warning',
        inputPlaceholder: '请输入取消原因'
      }
    )
    await cancelEnrollment(enrollment.id, value)
    ElMessage.success('已取消报名')
    loadEnrollments()
  } catch (error) {
    if (error !== 'cancel') {
      const errorMsg = error.response?.data?.message || '取消报名失败，请稍后重试'
      ElMessage.error(errorMsg)
      console.error('取消报名失败:', error)
    }
  }
}

const goToCourses = () => {
  router.push('/courses')
}

// 格式化时间
const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadEnrollments()
})
</script>

<style scoped>
.my-courses {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.empty {
  padding: 40px 0;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.course-card {
  transition: all 0.3s;
}

.course-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.course-card.canceled {
  opacity: 0.7;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.session-code,
.session-date,
.enroll-time,
.cancel-time,
.cancel-reason {
  margin: 5px 0;
  font-size: 14px;
  color: #909399;
}
</style>
