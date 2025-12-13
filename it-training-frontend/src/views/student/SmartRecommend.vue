<template>
  <div class="smart-recommend">
    <div class="page-header">
      <h2>智能选课</h2>
      <p class="subtitle">告诉我您的学习目标，AI将为您推荐最合适的课程和学习路径</p>
    </div>

    <el-card class="input-card">
      <el-form @submit.prevent="handleSubmit">
        <el-form-item>
          <el-input
            v-model="learningGoal"
            type="textarea"
            :rows="4"
            placeholder="请描述您的学习目标，例如：
- 我想学习Java后端开发，将来从事Web开发工作
- 我是前端工程师，想学习Vue框架
- 我想转行做数据分析，需要学习哪些技能"
            :maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            :disabled="!learningGoal.trim() || learningGoal.trim().length < 5"
            @click="handleSubmit"
          >
            <el-icon v-if="!loading"><MagicStick /></el-icon>
            {{ loading ? '正在分析...' : '获取推荐' }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div v-if="recommendation" class="result-section">
      <!-- 降级提示 -->
      <el-alert
        v-if="recommendation.fallback"
        type="info"
        :title="recommendation.fallbackMessage || 'AI服务暂时不可用，为您推荐热门课程'"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      />

      <el-card class="reason-card">
        <template #header>
          <div class="card-header">
            <span>推荐理由</span>
            <el-tag size="small" :type="recommendation.fallback ? 'info' : 'success'">
              {{ recommendation.fallback ? '智能匹配' : 'AI推荐' }}
            </el-tag>
          </div>
        </template>
        <div class="reason-content">
          <p>{{ recommendation.overallReason }}</p>
          <p v-if="recommendation.learningPath" class="learning-path">
            <strong>学习路径：</strong>{{ recommendation.learningPath }}
          </p>
        </div>
      </el-card>

      <h3 class="section-title">推荐课程 ({{ recommendation.courses.length }}门)</h3>

      <div class="course-list">
        <el-card
          v-for="course in recommendation.courses"
          :key="course.courseId"
          class="course-card"
          shadow="hover"
        >
          <div class="course-order">{{ course.order }}</div>
          <div class="course-content">
            <h4 class="course-name">{{ course.courseName }}</h4>
            <p class="course-reason">{{ course.reason }}</p>
            <div class="course-meta">
              <el-tag size="small">{{ getCategoryName(course.category) }}</el-tag>
              <el-tag size="small" type="info">{{ course.difficultyName }}</el-tag>
            </div>
            <div class="course-tags" v-if="course.tags">
              <el-tag
                v-for="tag in course.tags.split(',')"
                :key="tag"
                size="small"
                type="success"
                effect="plain"
              >{{ tag.trim() }}</el-tag>
            </div>
          </div>
          <div class="course-action">
            <el-button type="primary" @click="goToCourse(course.courseId)">
              查看详情
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <el-empty description="输入您的学习目标，获取个性化课程推荐">
        <template #image>
          <div class="empty-icon">🎯</div>
        </template>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MagicStick } from '@element-plus/icons-vue'
import { getAiRecommendation } from '@/api/ai'

const router = useRouter()

const learningGoal = ref('')
const loading = ref(false)
const recommendation = ref(null)

const handleSubmit = async () => {
  if (!learningGoal.value.trim() || learningGoal.value.trim().length < 5) {
    ElMessage.warning('请输入至少5个字符的学习目标描述')
    return
  }

  loading.value = true
  recommendation.value = null

  try {
    const res = await getAiRecommendation(learningGoal.value.trim())
    recommendation.value = res.data
    if (res.data.fallback) {
      ElMessage.info('为您推荐热门课程')
    } else {
      ElMessage.success('AI推荐获取成功')
    }
  } catch (error) {
    console.error('获取推荐失败:', error)
    const errorMsg = error.response?.data?.message || '获取推荐失败，请稍后重试'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
  }
}

const goToCourse = (courseId) => {
  router.push(`/course/${courseId}`)
}

const getCategoryName = (category) => {
  const map = {
    'BACKEND': '后端开发',
    'FRONTEND': '前端开发',
    'DATABASE': '数据库',
    'CLOUD': '云计算',
    'AI': '人工智能',
    'OTHER': '其他'
  }
  return map[category] || category
}
</script>

<style scoped>
.smart-recommend {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.input-card {
  margin-bottom: 30px;
}

.input-card :deep(.el-textarea__inner) {
  font-size: 15px;
  line-height: 1.6;
}

.result-section {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.reason-card {
  margin-bottom: 25px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.reason-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 15px 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  font-weight: 600;
}

.reason-content {
  padding: 10px 0;
}

.reason-content p {
  margin: 0 0 10px 0;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.95);
}

.reason-content p:last-child {
  margin-bottom: 0;
}

.learning-path {
  padding-top: 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
}

.section-title {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 18px;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.course-card {
  position: relative;
  padding-left: 50px;
}

.course-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 20px;
}

.course-order {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
}

.course-content {
  flex: 1;
}

.course-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
}

.course-reason {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.course-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.course-action {
  flex-shrink: 0;
}

.empty-state {
  padding: 60px 0;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}
</style>
