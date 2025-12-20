<template>
  <div class="course-study">
    <div class="study-header">
      <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
      <h2>{{ course.name }}</h2>
      <div class="timer-display">
        <el-icon><Clock /></el-icon>
        <span>本次学习: {{ formatTime(studySeconds) }}</span>
      </div>
    </div>

    <div class="study-content">
      <div class="main-area">
        <!-- 视频播放器 -->
        <div class="video-area">
          <video ref="videoPlayer" class="video-js vjs-big-play-centered"></video>
        </div>

        <!-- 课程章节列表 -->
        <el-card class="chapters-card">
          <template #header>
            <div class="chapters-header">
              <span>课程章节</span>
              <el-tag>{{ chapters.length }} 章节</el-tag>
            </div>
          </template>
          <div class="chapters-list">
            <div
              v-for="chapter in chapters"
              :key="chapter.id"
              :class="['chapter-item', { active: currentChapter?.id === chapter.id }]"
              @click="selectChapter(chapter)"
            >
              <div class="chapter-info">
                <el-icon v-if="chapter.completed" color="#67c23a"><CircleCheck /></el-icon>
                <el-icon v-else><VideoPlay /></el-icon>
                <span class="chapter-title">{{ chapter.title }}</span>
              </div>
              <span class="chapter-duration">{{ formatDuration(chapter.duration) }}</span>
            </div>
          </div>
        </el-card>

        <!-- 学习笔记 -->
        <el-card class="notes-card">
          <template #header>
            <div class="notes-header">
              <span>学习笔记</span>
              <el-button size="small" type="primary" @click="addNote">添加笔记</el-button>
            </div>
          </template>
          <div class="notes-list">
            <div v-for="note in notes" :key="note.id" class="note-item">
              <div class="note-header">
                <span class="note-time">{{ formatTime(note.videoTime) }}</span>
                <el-button link type="danger" size="small" @click="deleteNote(note.id)">删除</el-button>
              </div>
              <div class="note-content">{{ note.content }}</div>
            </div>
            <el-empty v-if="notes.length === 0" description="暂无笔记" :image-size="80" />
          </div>
        </el-card>
      </div>

      <div class="side-area">
        <el-card>
          <template #header>
            <span>学习进度</span>
          </template>
          <div class="progress-info">
            <el-progress :percentage="progress.progressPercent || 0" />
            <p>已学习: {{ progress.studyDurationFormatted || '0分钟' }}</p>
            <p class="auto-save-hint">
              <el-icon><Clock /></el-icon>
              自动保存进度（每5分钟）
            </p>
          </div>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <span>课程信息</span>
          </template>
          <div class="info-item">
            <span class="label">分类:</span>
            <span>{{ course.categoryName }}</span>
          </div>
          <div class="info-item">
            <span class="label">难度:</span>
            <span>{{ course.difficultyName }}</span>
          </div>
          <div class="info-item">
            <span class="label">课时:</span>
            <span>{{ course.durationHours }}小时</span>
          </div>
        </el-card>

        <!-- 学习统计 -->
        <el-card style="margin-top: 20px;">
          <template #header>
            <span>学习统计</span>
          </template>
          <div ref="statsChart" class="stats-chart"></div>
        </el-card>
      </div>
    </div>

    <!-- 添加笔记对话框 -->
    <el-dialog v-model="noteDialogVisible" title="添加学习笔记" width="500px">
      <el-form :model="noteForm">
        <el-form-item label="当前时间">
          <el-tag>{{ formatTime(currentVideoTime) }}</el-tag>
        </el-form-item>
        <el-form-item label="笔记内容">
          <el-input
            v-model="noteForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入学习笔记..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNote">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Clock, VideoPlay, CircleCheck } from '@element-plus/icons-vue'
import { getCourseById } from '@/api/course'
import { getCourseProgress, updateProgress } from '@/api/learning'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()

const course = ref({})
const progress = ref({})
const studySeconds = ref(0)
const videoPlayer = ref(null)
const statsChart = ref(null)
let player = null
let autoSaveTimer = null
let chartInstance = null

// 章节数据
const chapters = ref([
  { id: 1, title: '第1章：课程介绍', duration: 600, videoUrl: 'https://vjs.zencdn.net/v/oceans.mp4', completed: false },
  { id: 2, title: '第2章：基础知识', duration: 1200, videoUrl: 'https://vjs.zencdn.net/v/oceans.mp4', completed: false },
  { id: 3, title: '第3章：进阶内容', duration: 1800, videoUrl: 'https://vjs.zencdn.net/v/oceans.mp4', completed: false },
  { id: 4, title: '第4章：实战项目', duration: 2400, videoUrl: 'https://vjs.zencdn.net/v/oceans.mp4', completed: false },
  { id: 5, title: '第5章：总结与展望', duration: 900, videoUrl: 'https://vjs.zencdn.net/v/oceans.mp4', completed: false }
])
const currentChapter = ref(null)

// 学习笔记
const notes = ref([])
const noteDialogVisible = ref(false)
const currentVideoTime = ref(0)
const noteForm = ref({
  content: ''
})

const goBack = () => {
  if (player && !player.paused()) {
    ElMessage.warning('请先暂停视频')
    return
  }
  router.back()
}

const formatTime = (seconds) => {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h}小时${m}分${s}秒`
  }
  if (m > 0) {
    return `${m}分${s}秒`
  }
  return `${s}秒`
}

const formatDuration = (seconds) => {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return `${m}:${s.toString().padStart(2, '0')}`
}

// 初始化 Video.js 播放器
const initPlayer = () => {
  if (!videoPlayer.value) return

  player = videojs(videoPlayer.value, {
    controls: true,
    autoplay: false,
    preload: 'auto',
    fluid: true,
    playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2], // 倍速播放选项
    controlBar: {
      children: [
        'playToggle',
        'volumePanel',
        'currentTimeDisplay',
        'timeDivider',
        'durationDisplay',
        'progressControl',
        'playbackRateMenuButton', // 倍速播放按钮
        'fullscreenToggle'
      ]
    }
  })

  // 监听播放时间更新
  player.on('timeupdate', () => {
    currentVideoTime.value = Math.floor(player.currentTime())
    studySeconds.value = Math.floor(player.currentTime())
  })

  // 监听视频结束
  player.on('ended', () => {
    if (currentChapter.value) {
      currentChapter.value.completed = true
      ElMessage.success('本章节学习完成！')
      autoSaveProgress()
    }
  })

  // 加载第一个章节
  if (chapters.value.length > 0) {
    selectChapter(chapters.value[0])
  }
}

// 选择章节
const selectChapter = (chapter) => {
  if (!player) return

  currentChapter.value = chapter
  player.src({
    type: 'video/mp4',
    src: chapter.videoUrl
  })
  player.load()
  ElMessage.info(`正在播放：${chapter.title}`)
}

// 自动保存进度（每5分钟）
const startAutoSave = () => {
  autoSaveTimer = setInterval(() => {
    autoSaveProgress()
  }, 5 * 60 * 1000) // 5分钟
}

const autoSaveProgress = async () => {
  if (studySeconds.value === 0) return

  try {
    const minutes = Math.ceil(studySeconds.value / 60)
    const completedChapters = chapters.value.filter(c => c.completed).length
    const progressPercent = Math.floor((completedChapters / chapters.value.length) * 100)

    await updateProgress({
      courseId: route.params.id,
      studyMinutes: minutes,
      progressPercent: progressPercent
    })

    ElMessage.success('学习进度已自动保存')
    await loadProgress()
  } catch (error) {
    console.error('自动保存失败:', error)
  }
}

// 学习笔记功能
const addNote = () => {
  if (!player) {
    ElMessage.warning('请先播放视频')
    return
  }
  currentVideoTime.value = Math.floor(player.currentTime())
  noteForm.value.content = ''
  noteDialogVisible.value = true
}

const saveNote = () => {
  if (!noteForm.value.content.trim()) {
    ElMessage.warning('请输入笔记内容')
    return
  }

  const note = {
    id: Date.now(),
    videoTime: currentVideoTime.value,
    content: noteForm.value.content,
    chapterId: currentChapter.value?.id,
    chapterTitle: currentChapter.value?.title,
    createdAt: new Date().toISOString()
  }

  notes.value.unshift(note)
  noteDialogVisible.value = false
  ElMessage.success('笔记已保存')

  // 保存到 localStorage
  localStorage.setItem(`course_notes_${route.params.id}`, JSON.stringify(notes.value))
}

const deleteNote = (noteId) => {
  notes.value = notes.value.filter(n => n.id !== noteId)
  localStorage.setItem(`course_notes_${route.params.id}`, JSON.stringify(notes.value))
  ElMessage.success('笔记已删除')
}

// 加载笔记
const loadNotes = () => {
  const savedNotes = localStorage.getItem(`course_notes_${route.params.id}`)
  if (savedNotes) {
    notes.value = JSON.parse(savedNotes)
  }
}

// 初始化学习统计图表
const initStatsChart = () => {
  if (!statsChart.value) return

  chartInstance = echarts.init(statsChart.value)

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      name: '学习时长(分钟)'
    },
    series: [
      {
        data: [30, 45, 60, 40, 55, 70, 50],
        type: 'line',
        smooth: true,
        areaStyle: {
          color: 'rgba(64, 158, 255, 0.2)'
        },
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

const loadCourse = async () => {
  try {
    const res = await getCourseById(route.params.id)
    course.value = res.data
  } catch (error) {
    ElMessage.error('加载课程失败')
    router.back()
  }
}

const loadProgress = async () => {
  try {
    const res = await getCourseProgress(route.params.id)
    progress.value = res.data || {}
  } catch (error) {
    console.error('加载进度失败:', error)
  }
}

onMounted(async () => {
  await loadCourse()
  await loadProgress()
  loadNotes()

  await nextTick()
  initPlayer()
  initStatsChart()
  startAutoSave()
})

onBeforeUnmount(() => {
  if (player) {
    player.dispose()
  }
  if (autoSaveTimer) {
    clearInterval(autoSaveTimer)
  }
  if (chartInstance) {
    chartInstance.dispose()
  }
})
</script>

<style scoped>
.course-study {
  min-height: 100vh;
  background: #f5f7fa;
}

.study-header {
  background: white;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.study-header h2 {
  flex: 1;
  margin: 0;
  font-size: 18px;
}

.timer-display {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.study-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 20px;
  padding: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.main-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-area {
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.video-area video {
  width: 100%;
  height: auto;
}

/* 章节列表 */
.chapters-card {
  background: white;
}

.chapters-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapters-list {
  max-height: 300px;
  overflow-y: auto;
}

.chapter-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.3s;
}

.chapter-item:hover {
  background: #f5f7fa;
}

.chapter-item.active {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}

.chapter-item:last-child {
  border-bottom: none;
}

.chapter-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.chapter-title {
  font-size: 14px;
}

.chapter-duration {
  font-size: 12px;
  color: #909399;
}

/* 学习笔记 */
.notes-card {
  background: white;
}

.notes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notes-list {
  max-height: 400px;
  overflow-y: auto;
}

.note-item {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;
}

.note-item:last-child {
  border-bottom: none;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.note-time {
  font-size: 12px;
  color: #409eff;
  font-weight: 600;
}

.note-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 侧边栏 */
.side-area {
  display: flex;
  flex-direction: column;
}

.progress-info {
  padding: 10px 0;
}

.progress-info p {
  margin-top: 12px;
  color: #606266;
}

.auto-save-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.info-item {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  width: 60px;
  color: #909399;
}

/* 学习统计图表 */
.stats-chart {
  width: 100%;
  height: 200px;
}

@media (max-width: 1024px) {
  .study-content {
    grid-template-columns: 1fr;
  }

  .side-area {
    order: -1;
  }
}
</style>
