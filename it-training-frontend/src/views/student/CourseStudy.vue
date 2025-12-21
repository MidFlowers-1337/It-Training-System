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
                <el-icon v-if="chapter.completed" color="var(--success-color)"><CircleCheck /></el-icon>
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
              自动保存进度（每1分钟）
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
import { getCourseById, getCourseChapters, markChapterCompleted, updateChapterProgress } from '@/api/course'
import { getCourseProgress, updateProgress, checkin, getDashboard } from '@/api/learning'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()

const course = ref({})
const progress = ref({})
const studySeconds = ref(0) // 本次学习累计秒数
const totalStudySeconds = ref(0) // 历史累计学习秒数
const sessionStartTime = ref(null) // 本次会话开始时间
const lastSaveTime = ref(null) // 上次保存时间
const videoPlayer = ref(null)
const statsChart = ref(null)
let player = null
let autoSaveTimer = null
let studyTimer = null // 学习时长计时器
let chartInstance = null

// 章节数据
const chapters = ref([])
const currentChapter = ref(null)
const chapterProgressMap = ref({}) // 存储章节进度信息

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

  // 监听播放开始 - 启动学习计时器
  player.on('play', () => {
    if (!sessionStartTime.value) {
      sessionStartTime.value = Date.now()
    }
    startStudyTimer()
  })

  // 监听暂停 - 停止学习计时器
  player.on('pause', () => {
    stopStudyTimer()
  })

  // 监听播放时间更新
  player.on('timeupdate', () => {
    currentVideoTime.value = Math.floor(player.currentTime())

    // 每10秒保存一次播放进度
    if (currentChapter.value && currentVideoTime.value % 10 === 0) {
      saveVideoProgress()
    }
  })

  // 监听视频结束
  player.on('ended', () => {
    stopStudyTimer()
    if (currentChapter.value) {
      // 标记章节为已完成
      markCurrentChapterCompleted()
      ElMessage.success('本章节学习完成！')
      autoSaveProgress()
    }
  })
}

// 启动学习计时器（每秒累加）
const startStudyTimer = () => {
  if (studyTimer) return

  studyTimer = setInterval(() => {
    studySeconds.value++
  }, 1000)
}

// 停止学习计时器
const stopStudyTimer = () => {
  if (studyTimer) {
    clearInterval(studyTimer)
    studyTimer = null
  }
}

// 选择章节
const selectChapter = async (chapter) => {
  if (!player) return

  // 保存当前章节的播放进度
  if (currentChapter.value) {
    await saveVideoProgress()
  }

  currentChapter.value = chapter
  player.src({
    type: 'video/mp4',
    src: chapter.videoUrl
  })
  player.load()

  // 恢复上次播放位置
  const savedProgress = chapterProgressMap.value[chapter.id]
  if (savedProgress && savedProgress.lastPosition > 0) {
    player.currentTime(savedProgress.lastPosition)
    ElMessage.info(`正在播放：${chapter.title}（从 ${formatDuration(savedProgress.lastPosition)} 继续）`)
  } else {
    ElMessage.info(`正在播放：${chapter.title}`)
  }
}

// 保存视频播放进度
const saveVideoProgress = async () => {
  if (!currentChapter.value || !player) return

  const currentTime = Math.floor(player.currentTime())
  const duration = Math.floor(player.duration())

  // 只有播放时长大于5秒才保存
  if (currentTime < 5) return

  try {
    await updateChapterProgress(currentChapter.value.id, duration, currentTime)

    // 更新本地进度缓存
    chapterProgressMap.value[currentChapter.value.id] = {
      watchDuration: duration,
      lastPosition: currentTime
    }

    // 检查是否接近结束（播放到95%以上自动标记完成）
    if (duration > 0 && currentTime / duration >= 0.95 && !currentChapter.value.completed) {
      await markCurrentChapterCompleted()
    }
  } catch (error) {
    console.error('保存播放进度失败:', error)
  }
}

// 自动保存进度（每1分钟）
const startAutoSave = () => {
  // 立即保存一次
  lastSaveTime.value = Date.now()

  autoSaveTimer = setInterval(() => {
    if (studySeconds.value > 0) {
      autoSaveProgress()
    }
  }, 60 * 1000) // 1分钟
}

const autoSaveProgress = async () => {
  if (studySeconds.value === 0) return

  try {
    const studyMinutes = Math.ceil(studySeconds.value / 60)
    const completedChapters = chapters.value.filter(c => c.completed).length
    const progressPercent = Math.floor((completedChapters / chapters.value.length) * 100)

    // 1. 更新课程学习进度
    const res = await updateProgress({
      courseId: route.params.id,
      studyMinutes: studyMinutes,
      progressPercent: progressPercent
    })

    // 2. 同步更新 study_checkin 表（今日学习时长）
    // 注意：不传递 studyContent，避免重复追加
    await checkin({
      courseId: route.params.id,
      studyMinutes: studyMinutes
    })

    if (res && res.code === 200) {
      const totalMinutes = Math.ceil((totalStudySeconds.value + studySeconds.value) / 60)
      ElMessage.success(`学习进度已保存（累计 ${totalMinutes} 分钟）`)
      // 更新总学习时长
      totalStudySeconds.value += studySeconds.value
      studySeconds.value = 0 // 重置本次学习秒数
      lastSaveTime.value = Date.now()

      // 重新加载进度以更新显示
      await loadProgress()
    }
  } catch (error) {
    console.error('自动保存失败:', error)
    ElMessage.error('保存进度失败，请检查网络连接')
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
const initStatsChart = async () => {
  if (!statsChart.value) return

  try {
    // 获取本周学习数据
    const dashboardRes = await getDashboard()
    let weeklyData = [0, 0, 0, 0, 0, 0, 0]

    if (dashboardRes && dashboardRes.code === 200 && dashboardRes.data && dashboardRes.data.weeklyStudyData) {
      weeklyData = dashboardRes.data.weeklyStudyData.map(item => item.studyMinutes || 0)
    }

    const style = getComputedStyle(document.documentElement)
    const primaryRgb = (style.getPropertyValue('--primary-color-rgb') || '37 99 235').trim().replace(/\s+/g, ' ')
    const textSecondaryRgb = (style.getPropertyValue('--text-secondary-rgb') || '75 85 99').trim().replace(/\s+/g, ' ')
    const borderRgb = (style.getPropertyValue('--border-color-rgb') || '229 231 235').trim().replace(/\s+/g, ' ')

    const primary = `rgba(${primaryRgb.replace(/\s+/g, ',')}, 0.9)`
    const primarySoft = `rgba(${primaryRgb.replace(/\s+/g, ',')}, 0.18)`
    const textSecondary = `rgba(${textSecondaryRgb.replace(/\s+/g, ',')}, 0.9)`
    const border = `rgba(${borderRgb.replace(/\s+/g, ',')}, 0.7)`

    chartInstance = echarts.init(statsChart.value)

    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: '{b}: {c} 分钟'
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
        axisLabel: { color: textSecondary },
        axisLine: { lineStyle: { color: border } },
        axisTick: { show: false },
      },
      yAxis: {
        type: 'value',
        name: '学习时长(分钟)',
        axisLabel: { color: textSecondary },
        nameTextStyle: { color: textSecondary },
        splitLine: { lineStyle: { color: border } },
      },
      series: [
        {
          data: weeklyData,
          type: 'line',
          smooth: true,
          areaStyle: {
            color: primarySoft
          },
          itemStyle: {
            color: primary
          },
          lineStyle: { color: primary, width: 2 }
        }
      ]
    }

    chartInstance.setOption(option)
  } catch (error) {
    console.error('加载学习统计失败:', error)
    // 失败时使用空数据
    if (chartInstance) {
      chartInstance.setOption({
        xAxis: { type: 'category', data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'] },
        yAxis: { type: 'value', name: '学习时长(分钟)' },
        series: [{ data: [0, 0, 0, 0, 0, 0, 0], type: 'line' }]
      })
    }
  }
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

const loadChapters = async () => {
  try {
    const res = await getCourseChapters(route.params.id)
    if (res && res.code === 200) {
      chapters.value = res.data || []

      // 构建章节进度映射（从后端返回的数据中提取）
      chapters.value.forEach(chapter => {
        if (chapter.watchDuration || chapter.lastPosition) {
          chapterProgressMap.value[chapter.id] = {
            watchDuration: chapter.watchDuration || 0,
            lastPosition: chapter.lastPosition || 0
          }
        }
      })

      // 加载第一个未完成的章节，或第一个章节
      if (chapters.value.length > 0) {
        await nextTick()
        const firstIncomplete = chapters.value.find(c => !c.completed)
        selectChapter(firstIncomplete || chapters.value[0])
      }
    }
  } catch (error) {
    console.error('加载章节失败:', error)
    ElMessage.error('加载章节失败')
  }
}

const loadProgress = async () => {
  try {
    const res = await getCourseProgress(route.params.id)
    if (res && res.code === 200 && res.data) {
      progress.value = res.data

      // 恢复历史学习时长
      if (res.data.studyDurationMinutes) {
        totalStudySeconds.value = res.data.studyDurationMinutes * 60
      }
    }
  } catch (error) {
    console.error('加载进度失败:', error)
  }
}

// 标记当前章节为已完成
const markCurrentChapterCompleted = async () => {
  if (!currentChapter.value) return

  try {
    await markChapterCompleted(currentChapter.value.id)
    currentChapter.value.completed = true

    // 重新加载章节列表以更新完成状态
    await loadChapters()
  } catch (error) {
    console.error('标记章节完成失败:', error)
  }
}

onMounted(async () => {
  await loadCourse()
  await loadProgress()
  await loadChapters()
  loadNotes()

  await nextTick()
  initPlayer()
  await initStatsChart()
  startAutoSave()
})

onBeforeUnmount(async () => {
  // 停止学习计时器
  stopStudyTimer()

  // 保存未保存的进度
  if (studySeconds.value > 0) {
    try {
      await updateProgress({
        courseId: route.params.id,
        studyMinutes: Math.ceil(studySeconds.value / 60),
        progressPercent: progress.value.progressPercent || 0
      })
    } catch (error) {
      console.error('离开前保存失败:', error)
    }
  }

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
  background: var(--bg-primary);
}

.study-header {
  background-color: var(--glass-bg, var(--bg-secondary));
  border-bottom: 1px solid var(--glass-border, var(--border-color));
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 20;
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
  color: var(--primary-color);
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
  background: rgb(0 0 0);
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
  border-bottom: 1px solid var(--separator, var(--border-color));
  cursor: pointer;
  transition: background 0.3s;
}

.chapter-item:hover {
  background: var(--bg-hover);
}

.chapter-item.active {
  background: var(--primary-bg);
  border-left: 3px solid var(--primary-color);
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
  color: var(--text-muted);
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
  border-bottom: 1px solid var(--separator, var(--border-color));
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
  color: var(--primary-color);
  font-weight: 600;
}

.note-content {
  font-size: 14px;
  color: var(--text-secondary);
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
  color: var(--text-secondary);
}

.auto-save-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 8px;
}

.info-item {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid var(--separator, var(--border-color));
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  width: 60px;
  color: var(--text-muted);
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
