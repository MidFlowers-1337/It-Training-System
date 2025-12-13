<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ overview.courseCount || 0 }}</div>
              <div class="stat-label">课程总数</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: '#67C23A' }">
              <Reading />
            </el-icon>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ overview.studentCount || 0 }}</div>
              <div class="stat-label">学员总数</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: '#409EFF' }">
              <User />
            </el-icon>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ overview.activeSessionCount || 0 }}</div>
              <div class="stat-label">活跃班期</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: '#E6A23C' }">
              <Calendar />
            </el-icon>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value">{{ overview.enrollmentCount || 0 }}</div>
              <div class="stat-label">报名总数</div>
            </div>
            <el-icon class="stat-icon" :style="{ color: '#F56C6C' }">
              <Document />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行统计卡片 -->
    <el-row :gutter="20" class="second-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card mini">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value small">{{ overview.instructorCount || 0 }}</div>
              <div class="stat-label">讲师数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card mini">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value small">{{ overview.sessionCount || 0 }}</div>
              <div class="stat-label">班期总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card mini">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-value small">{{ overview.monthlyEnrollmentCount || 0 }}</div>
              <div class="stat-label">本月报名</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card mini quick-actions-card">
          <div class="quick-btns">
            <el-button type="primary" size="small" @click="$router.push('/admin/courses')">
              <el-icon><Plus /></el-icon> 新增课程
            </el-button>
            <el-button type="success" size="small" @click="$router.push('/admin/sessions')">
              <el-icon><Plus /></el-icon> 新增班期
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>课程热度排行 TOP 10</span>
            </div>
          </template>
          <div class="chart-container" ref="hotChartRef"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>报名趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="loadTrendData">
                <el-radio-button :value="7">7天</el-radio-button>
                <el-radio-button :value="30">30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container" ref="trendChartRef"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { User, Reading, Calendar, Document, Plus } from '@element-plus/icons-vue'
import { getStatsOverview, getCourseHotRanking, getEnrollmentTrend } from '@/api/stats'
import * as echarts from 'echarts'

const overview = ref({})
const courseHotData = ref([])
const trendData = ref([])
const trendDays = ref(7)

const hotChartRef = ref(null)
const trendChartRef = ref(null)
let hotChart = null
let trendChart = null

// 加载概览数据
const loadOverview = async () => {
  try {
    const res = await getStatsOverview()
    overview.value = res.data || {}
  } catch (error) {
    console.error('加载概览数据失败:', error)
  }
}

// 加载课程热度数据
const loadHotData = async () => {
  try {
    const res = await getCourseHotRanking(10)
    courseHotData.value = res.data || []
    renderHotChart()
  } catch (error) {
    console.error('加载课程热度失败:', error)
  }
}

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const res = await getEnrollmentTrend(trendDays.value)
    trendData.value = res.data || []
    renderTrendChart()
  } catch (error) {
    console.error('加载报名趋势失败:', error)
  }
}

// 渲染课程热度图表
const renderHotChart = () => {
  if (!hotChartRef.value) return

  if (!hotChart) {
    hotChart = echarts.init(hotChartRef.value)
  }

  const names = courseHotData.value.map(item => item.courseName)
  const values = courseHotData.value.map(item => item.enrollmentCount)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: names.reverse(),
      axisLabel: {
        width: 100,
        overflow: 'truncate'
      }
    },
    series: [{
      name: '报名人数',
      type: 'bar',
      data: values.reverse(),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      }
    }]
  }

  hotChart.setOption(option)
}

// 渲染趋势图表
const renderTrendChart = () => {
  if (!trendChartRef.value) return

  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const dates = trendData.value.map(item => item.date.substring(5)) // 只显示 MM-DD
  const values = trendData.value.map(item => item.count)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [{
      name: '报名数',
      type: 'line',
      smooth: true,
      data: values,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      lineStyle: {
        color: '#409EFF',
        width: 2
      },
      itemStyle: {
        color: '#409EFF'
      }
    }]
  }

  trendChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  hotChart?.resize()
  trendChart?.resize()
}

onMounted(async () => {
  await loadOverview()
  await nextTick()
  loadHotData()
  loadTrendData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  hotChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-card.mini {
  height: 80px;
}

.stat-card.mini :deep(.el-card__body) {
  padding: 15px;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-value.small {
  font-size: 22px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.stat-icon {
  font-size: 48px;
  opacity: 0.8;
}

.second-row {
  margin-top: -5px;
}

.quick-actions-card {
  display: flex;
  align-items: center;
  justify-content: center;
}

.quick-btns {
  display: flex;
  gap: 10px;
}

.charts-row {
  margin-top: 5px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 350px;
}
</style>
