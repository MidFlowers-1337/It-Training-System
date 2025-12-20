<template>
  <div class="my-courses-page" v-loading="loading">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1>📚 我的课程</h1>
        <p class="subtitle">共 {{ totalCourses }} 门课程</p>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程..."
          :prefix-icon="Search"
          clearable
          style="width: 300px; margin-right: 12px;"
          @input="handleSearch"
        />
        <el-button type="primary" @click="goToCourses">
          <el-icon><Plus /></el-icon>
          去选课
        </el-button>
      </div>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <el-radio-group v-model="activeFilter" @change="handleFilterChange">
        <el-radio-button label="all">全部 ({{ allCourses.length }})</el-radio-button>
        <el-radio-button label="learning">进行中 ({{ learningCourses.length }})</el-radio-button>
        <el-radio-button label="completed">已完成 ({{ completedCourses.length }})</el-radio-button>
        <el-radio-button label="notStarted">未开始 ({{ notStartedCourses.length }})</el-radio-button>
      </el-radio-group>

      <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px; margin-left: 16px;" @change="handleSort">
        <el-option label="最近学习" value="recent" />
        <el-option label="进度最高" value="progress" />
        <el-option label="课程名称" value="name" />
      </el-select>
    </div>

    <!-- 课程网格 -->
    <div v-if="filteredCourses.length === 0" class="empty-state">
      <el-empty :description="emptyDescription">
        <el-button type="primary" @click="goToCourses">去选课</el-button>
      </el-empty>
    </div>

    <div v-else class="courses-grid">
      <div
        v-for="course in filteredCourses"
        :key="course.id"
        class="course-card"
        @click="goToCourseDetail(course.id)"
      >
        <!-- 课程封面 -->
        <div class="course-cover">
          <img :src="course.coverImage || getDefaultCover(course.category)" :alt="course.name" />
          <div class="cover-overlay">
            <el-button type="primary" size="large" @click.stop="goToStudy(course.id)">
              {{ course.progressPercent >= 100 ? '复习课程' : course.progressPercent > 0 ? '继续学习' : '开始学习' }}
            </el-button>
          </div>
          <!-- 完成标记 -->
          <div v-if="course.progressPercent >= 100" class="completed-badge">
            <el-icon><CircleCheck /></el-icon>
            已完成
          </div>
        </div>

        <!-- 课程信息 -->
        <div class="course-body">
          <h3 class="course-title" :title="course.name">{{ course.name }}</h3>

          <div class="course-meta">
            <el-tag :type="getCategoryType(course.category)" size="small">
              {{ getCategoryName(course.category) }}
            </el-tag>
            <el-tag :type="getDifficultyType(course.difficulty)" size="small">
              {{ getDifficultyName(course.difficulty) }}
            </el-tag>
          </div>

          <div class="course-stats">
            <div class="stat-item">
              <el-icon><Clock /></el-icon>
              <span>{{ course.durationHours }}小时</span>
            </div>
            <div class="stat-item">
              <el-icon><Document /></el-icon>
              <span>{{ course.chapterCount || 0 }}章节</span>
            </div>
            <div class="stat-item">
              <el-icon><Timer /></el-icon>
              <span>已学{{ course.studyDuration || 0 }}分钟</span>
            </div>
          </div>

          <!-- 学习进度 -->
          <div class="progress-section">
            <div class="progress-header">
              <span class="progress-label">学习进度</span>
              <span class="progress-percent">{{ course.progressPercent || 0 }}%</span>
            </div>
            <el-progress
              :percentage="course.progressPercent || 0"
              :stroke-width="8"
              :show-text="false"
              :color="getProgressColor(course.progressPercent)"
            />
          </div>

          <!-- 最后学习时间 -->
          <div class="last-study-time">
            <el-icon><Clock /></el-icon>
            <span>{{ getLastStudyTime(course.lastStudyAt) }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="course-actions">
          <el-button link type="primary" @click.stop="goToStudy(course.id)">
            <el-icon><VideoPlay /></el-icon>
            学习
          </el-button>
          <el-button link @click.stop="goToCourseDetail(course.id)">
            <el-icon><View /></el-icon>
            详情
          </el-button>
          <el-dropdown @command="handleCommand($event, course)" trigger="click" @click.stop>
            <el-button link>
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="notes">
                  <el-icon><Notebook /></el-icon>
                  我的笔记
                </el-dropdown-item>
                <el-dropdown-item command="certificate" :disabled="course.progressPercent < 100">
                  <el-icon><Medal /></el-icon>
                  查看证书
                </el-dropdown-item>
                <el-dropdown-item command="share">
                  <el-icon><Share /></el-icon>
                  分享课程
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredCourses.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredCourses.length"
        :page-sizes="[12, 24, 48]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Search, Plus, Clock, Document, Timer, VideoPlay, View, MoreFilled,
  Notebook, Medal, Share, CircleCheck
} from '@element-plus/icons-vue'
import { getMyEnrollments } from '@/api/enrollment'
import { getCourses } from '@/api/course'
import { getCourseProgress } from '@/api/learning'

const router = useRouter()

// 数据状态
const loading = ref(false)
const allCourses = ref([])
const searchKeyword = ref('')
const activeFilter = ref('all')
const sortBy = ref('recent')
const currentPage = ref(1)
const pageSize = ref(12)

// 计算属性
const totalCourses = computed(() => allCourses.value.length)

const learningCourses = computed(() =>
  allCourses.value.filter(c => c.progressPercent > 0 && c.progressPercent < 100)
)

const completedCourses = computed(() =>
  allCourses.value.filter(c => c.progressPercent >= 100)
)

const notStartedCourses = computed(() =>
  allCourses.value.filter(c => !c.progressPercent || c.progressPercent === 0)
)

const filteredCourses = computed(() => {
  let courses = allCourses.value

  // 筛选
  if (activeFilter.value === 'learning') {
    courses = learningCourses.value
  } else if (activeFilter.value === 'completed') {
    courses = completedCourses.value
  } else if (activeFilter.value === 'notStarted') {
    courses = notStartedCourses.value
  }

  // 搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    courses = courses.filter(c =>
      c.name.toLowerCase().includes(keyword) ||
      (c.description && c.description.toLowerCase().includes(keyword))
    )
  }

  // 排序
  if (sortBy.value === 'recent') {
    courses = [...courses].sort((a, b) => {
      const timeA = a.lastStudyAt ? new Date(a.lastStudyAt).getTime() : 0
      const timeB = b.lastStudyAt ? new Date(b.lastStudyAt).getTime() : 0
      return timeB - timeA
    })
  } else if (sortBy.value === 'progress') {
    courses = [...courses].sort((a, b) => (b.progressPercent || 0) - (a.progressPercent || 0))
  } else if (sortBy.value === 'name') {
    courses = [...courses].sort((a, b) => a.name.localeCompare(b.name, 'zh-CN'))
  }

  return courses
})

const emptyDescription = computed(() => {
  if (searchKeyword.value) {
    return `未找到包含"${searchKeyword.value}"的课程`
  }
  if (activeFilter.value === 'learning') {
    return '暂无进行中的课程'
  }
  if (activeFilter.value === 'completed') {
    return '暂无已完成的课程'
  }
  if (activeFilter.value === 'notStarted') {
    return '暂无未开始的课程'
  }
  return '暂无课程，快去选课吧！'
})

// 方法
const loadMyCourses = async () => {
  loading.value = true
  try {
    // 获取我的报名
    const enrollRes = await getMyEnrollments()
    const enrollments = enrollRes.data || []

    // 获取课程详情和进度
    const coursePromises = enrollments
      .filter(e => e.status === 0) // 0-已报名, 1-已取消
      .map(async (enrollment) => {
        try {
          // 通过课程名称获取课程信息（因为 enrollment 中没有 courseId）
          const courseRes = await getCourses({ name: enrollment.courseName })
          const course = courseRes.data?.records?.[0]

          if (!course) {
            console.warn('未找到课程:', enrollment.courseName)
            return null
          }

          // 获取学习进度
          let progress = null
          try {
            const progressRes = await getCourseProgress(course.id)
            progress = progressRes.data
          } catch (err) {
            console.warn('获取进度失败:', err)
          }

          return {
            ...course,
            progressPercent: progress?.progressPercent || 0,
            studyDuration: progress?.studyDurationMinutes || 0,
            lastStudyAt: progress?.updatedAt || enrollment.enrolledAt,
            chapterCount: 0, // TODO: 从章节API获取
            sessionId: enrollment.sessionId,
            sessionCode: enrollment.sessionCode,
            startDate: enrollment.startDate,
            endDate: enrollment.endDate
          }
        } catch (err) {
          console.error('获取课程失败:', err)
          return null
        }
      })

    const courses = await Promise.all(coursePromises)
    allCourses.value = courses.filter(c => c !== null)
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleFilterChange = () => {
  currentPage.value = 1
}

const handleSort = () => {
  currentPage.value = 1
}

const handleSizeChange = () => {
  currentPage.value = 1
}

const handlePageChange = () => {
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const goToCourses = () => {
  router.push('/courses')
}

const goToStudy = (courseId) => {
  router.push(`/course/${courseId}/study`)
}

const goToCourseDetail = (courseId) => {
  router.push(`/course/${courseId}`)
}

const handleCommand = (command, course) => {
  switch (command) {
    case 'notes':
      ElMessage.info('笔记功能开发中...')
      break
    case 'certificate':
      ElMessage.success('证书功能开发中...')
      break
    case 'share':
      ElMessage.info('分享功能开发中...')
      break
  }
}

// 辅助函数
const getDefaultCover = (category) => {
  const covers = {
    'BACKEND': 'https://via.placeholder.com/400x240/409EFF/FFFFFF?text=Backend',
    'FRONTEND': 'https://via.placeholder.com/400x240/67C23A/FFFFFF?text=Frontend',
    'DATABASE': 'https://via.placeholder.com/400x240/E6A23C/FFFFFF?text=Database',
    'CLOUD': 'https://via.placeholder.com/400x240/909399/FFFFFF?text=Cloud',
    'AI': 'https://via.placeholder.com/400x240/F56C6C/FFFFFF?text=AI'
  }
  return covers[category] || 'https://via.placeholder.com/400x240/909399/FFFFFF?text=Course'
}

const getCategoryName = (category) => {
  const names = {
    'BACKEND': '后端开发',
    'FRONTEND': '前端开发',
    'DATABASE': '数据库',
    'CLOUD': '云计算',
    'AI': '人工智能',
    'OTHER': '其他'
  }
  return names[category] || category
}

const getCategoryType = (category) => {
  const types = {
    'BACKEND': 'primary',
    'FRONTEND': 'success',
    'DATABASE': 'warning',
    'CLOUD': 'info',
    'AI': 'danger'
  }
  return types[category] || ''
}

const getDifficultyName = (difficulty) => {
  const names = {
    1: '入门',
    2: '初级',
    3: '中级',
    4: '高级'
  }
  return names[difficulty] || '未知'
}

const getDifficultyType = (difficulty) => {
  const types = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'danger'
  }
  return types[difficulty] || 'info'
}

const getProgressColor = (percent) => {
  if (percent >= 100) return '#67c23a'
  if (percent >= 60) return '#409eff'
  if (percent >= 30) return '#e6a23c'
  return '#f56c6c'
}

const getLastStudyTime = (time) => {
  if (!time) return '尚未开始学习'

  const now = new Date()
  const studyTime = new Date(time)
  const diff = now - studyTime

  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚学习'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  if (days < 30) return `${Math.floor(days / 7)}周前`
  if (days < 365) return `${Math.floor(days / 30)}个月前`
  return `${Math.floor(days / 365)}年前`
}

onMounted(() => {
  loadMyCourses()
})
</script>

<style scoped>
.my-courses-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 20px;
  border-radius: 8px;
}

.header-left h1 {
  margin: 0 0 4px 0;
  font-size: 24px;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 筛选标签 */
.filter-tabs {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background: white;
  padding: 16px 20px;
  border-radius: 8px;
}

/* 空状态 */
.empty-state {
  background: white;
  padding: 60px 20px;
  border-radius: 8px;
  text-align: center;
}

/* 课程网格 */
.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

/* 课程卡片 */
.course-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.course-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* 课程封面 */
.course-cover {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.course-card:hover .course-cover img {
  transform: scale(1.05);
}

.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.course-card:hover .cover-overlay {
  opacity: 1;
}

.completed-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: #67c23a;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 课程信息 */
.course-body {
  padding: 16px;
}

.course-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.course-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

/* 进度条 */
.progress-section {
  margin-bottom: 12px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-label {
  font-size: 12px;
  color: #909399;
}

.progress-percent {
  font-size: 14px;
  font-weight: 600;
  color: #409eff;
}

/* 最后学习时间 */
.last-study-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

/* 操作按钮 */
.course-actions {
  display: flex;
  justify-content: space-around;
  padding: 12px 16px;
  border-top: 1px solid #ebeef5;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  background: white;
  padding: 20px;
  border-radius: 8px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-right {
    width: 100%;
    flex-direction: column;
    gap: 12px;
  }

  .header-right .el-input {
    width: 100% !important;
  }

  .filter-tabs {
    flex-direction: column;
    gap: 12px;
  }

  .courses-grid {
    grid-template-columns: 1fr;
  }
}
</style>
