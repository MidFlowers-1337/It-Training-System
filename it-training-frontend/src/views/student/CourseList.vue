<template>
  <div class="course-list">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm">
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
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="搜索课程" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="course-grid" v-loading="loading">
      <el-card
        v-for="course in courses"
        :key="course.id"
        class="course-card"
        shadow="hover"
        @click="goToDetail(course.id)"
      >
        <div class="course-cover">
          <img :src="course.coverImage || '/default-course.png'" alt="" />
        </div>
        <div class="course-info">
          <h3 class="course-name">{{ course.name }}</h3>
          <p class="course-desc">{{ course.description || '暂无描述' }}</p>
          <div class="course-meta">
            <el-tag size="small">{{ course.categoryName }}</el-tag>
            <el-tag size="small" type="info">{{ course.difficultyName }}</el-tag>
            <span class="duration">{{ course.durationHours }}课时</span>
          </div>
        </div>
      </el-card>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        layout="prev, pager, next"
        @current-change="loadCourses"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCourses } from '@/api/course'

const router = useRouter()

const searchForm = reactive({
  category: '',
  keyword: ''
})

const courses = ref([])
const loading = ref(false)

const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await getCourses({
      page: pagination.page,
      size: pagination.size,
      category: searchForm.category,
      keyword: searchForm.keyword,
      status: 1 // 只显示已发布的课程
    })
    courses.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载课程失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCourses()
}

const goToDetail = (id) => {
  router.push(`/course/${id}`)
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.course-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  min-height: 300px;
}

.course-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover {
  height: 160px;
  overflow: hidden;
  margin: -20px -20px 15px -20px;
  background: #f0f2f5;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.course-name {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-desc {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 42px;
}

.course-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.duration {
  font-size: 12px;
  color: #909399;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
