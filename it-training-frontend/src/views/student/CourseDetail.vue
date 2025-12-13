<template>
  <div class="course-detail" v-loading="loading">
    <el-page-header @back="goBack" :title="course.name || '课程详情'" />

    <div class="detail-content" v-if="course.id">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card>
            <template #header>
              <h2>{{ course.name }}</h2>
            </template>
            <div class="course-tags">
              <el-tag>{{ course.categoryName }}</el-tag>
              <el-tag type="info">{{ course.difficultyName }}</el-tag>
              <el-tag type="warning">{{ course.durationHours }}课时</el-tag>
            </div>
            <el-divider />
            <h3>课程简介</h3>
            <p class="description">{{ course.description || '暂无课程描述' }}</p>
            <el-divider />
            <h3>技能标签</h3>
            <div class="skill-tags">
              <el-tag
                v-for="tag in skillTags"
                :key="tag"
                type="success"
                effect="plain"
              >{{ tag }}</el-tag>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card>
            <template #header>
              <span>可报名班期</span>
            </template>
            <div v-if="sessions.length === 0" class="no-session">
              暂无可报名的班期
            </div>
            <div v-else class="session-list">
              <div
                v-for="session in sessions"
                :key="session.id"
                class="session-item"
              >
                <div class="session-info">
                  <div class="session-code">{{ session.sessionCode }}</div>
                  <div class="session-date">
                    {{ session.startDate }} ~ {{ session.endDate }}
                  </div>
                  <div class="session-schedule">{{ session.schedule }}</div>
                  <div class="session-quota">
                    剩余名额: {{ session.remainingQuota }} / {{ session.maxCapacity }}
                  </div>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEnroll(session)"
                  :disabled="session.remainingQuota <= 0"
                >
                  {{ session.remainingQuota > 0 ? '立即报名' : '已满员' }}
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCourseById } from '@/api/course'
import { getEnrollableSessions } from '@/api/session'
import { enroll } from '@/api/enrollment'

const route = useRoute()
const router = useRouter()

const course = ref({})
const sessions = ref([])
const loading = ref(false)

const skillTags = computed(() => {
  if (!course.value.tags) return []
  return course.value.tags.split(',').filter(t => t.trim())
})

const goBack = () => {
  router.back()
}

const loadCourse = async () => {
  loading.value = true
  try {
    const res = await getCourseById(route.params.id)
    course.value = res.data
    await loadSessions()
  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('课程不存在')
    router.push('/courses')
  } finally {
    loading.value = false
  }
}

const loadSessions = async () => {
  try {
    const res = await getEnrollableSessions(route.params.id)
    sessions.value = res.data
  } catch (error) {
    console.error('加载班期失败:', error)
  }
}

const handleEnroll = async (session) => {
  try {
    await ElMessageBox.confirm(
      `确定要报名「${session.sessionCode}」班期吗？\n开班日期: ${session.startDate}\n上课时间: ${session.schedule || '待定'}`,
      '确认报名',
      {
        confirmButtonText: '确认报名',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    await enroll(session.id)
    ElMessage.success('报名成功！可在"我的课程"中查看')
    loadSessions() // 刷新班期列表
  } catch (error) {
    if (error !== 'cancel') {
      // 根据错误码显示友好提示
      const errorMsg = error.response?.data?.message || '报名失败，请稍后重试'
      ElMessage.error(errorMsg)
      console.error('报名失败:', error)
    }
  }
}

onMounted(() => {
  loadCourse()
})
</script>

<style scoped>
.course-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.detail-content {
  margin-top: 20px;
}

.course-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.description {
  color: #606266;
  line-height: 1.8;
  white-space: pre-line;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.no-session {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}

.session-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.session-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.session-code {
  font-weight: 600;
  color: #303133;
  margin-bottom: 5px;
}

.session-date,
.session-schedule,
.session-quota {
  font-size: 13px;
  color: #909399;
  margin-top: 3px;
}
</style>
