<template>
  <div class="home-container">
    <el-container>
      <el-header>
        <div class="header-content">
          <h1>IT技能培训智能选课系统</h1>
          <el-button type="primary" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      
      <el-main>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">📚</div>
                <div class="stat-text">
                  <div class="stat-number">0</div>
                  <div class="stat-label">我的课程</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">✍️</div>
                <div class="stat-text">
                  <div class="stat-number">0</div>
                  <div class="stat-label">学习笔记</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">🏆</div>
                <div class="stat-text">
                  <div class="stat-number">0</div>
                  <div class="stat-label">获得证书</div>
                </div>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="6">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-icon">⏱️</div>
                <div class="stat-text">
                  <div class="stat-number">0h</div>
                  <div class="stat-label">学习时长</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="24">
            <el-card>
              <template #header>
                <span>快速入口</span>
              </template>
              <div class="quick-links">
                <el-button type="primary" @click="$router.push('/courses')">浏览课程</el-button>
                <el-button type="success" @click="$router.push('/my-courses')">我的课程</el-button>
                <el-button type="warning">AI推荐</el-button>
                <el-button type="info">学习路径</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const handleLogout = () => {
  localStorage.removeItem('token')
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: #f0f2f5;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  font-size: 40px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.quick-links {
  display: flex;
  gap: 10px;
}
</style>