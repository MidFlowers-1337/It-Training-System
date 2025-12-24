<template>
  <div class="course-study-page">
    <!-- Header -->
    <header class="study-header">
      <div class="header-inner">
        <button type="button" class="back-btn" @click="goBack">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 18l-6-6 6-6" />
          </svg>
          返回
        </button>

        <h2 class="course-name">{{ course.name }}</h2>

        <div class="study-timer">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10" />
            <polyline points="12 6 12 12 16 14" />
          </svg>
          <span>本次学习: {{ formatTime(studySeconds) }}</span>
        </div>
      </div>
    </header>

    <!-- Main Layout -->
    <div class="study-layout">
      <!-- Sidebar: Course Outline -->
      <aside class="outline-sidebar">
        <div class="sidebar-header">
          <h3 class="sidebar-title">课程大纲</h3>
          <span class="progress-badge">{{ completedCount }}/{{ chapters.length }}</span>
        </div>

        <div class="chapter-list">
          <button
            v-for="chapter in chapters"
            :key="chapter.id"
            type="button"
            class="chapter-item"
            :class="{
              active: currentChapter?.id === chapter.id,
              completed: chapter.completed
            }"
            @click="selectChapter(chapter)"
          >
            <span class="chapter-status">
              <svg v-if="chapter.completed" class="status-icon completed" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <svg v-else class="status-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                <circle cx="12" cy="12" r="10" />
              </svg>
            </span>
            <span class="chapter-title">{{ chapter.title }}</span>
            <span class="chapter-duration">{{ formatDuration(chapter.duration) }}</span>
          </button>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="study-content">
        <!-- Video Player -->
        <div class="video-container">
          <video ref="videoPlayer" class="video-js vjs-big-play-centered"></video>
        </div>

        <!-- Content Sections -->
        <div class="content-sections">
          <!-- Progress Card -->
          <section class="section-card progress-card">
            <div class="card-header">
              <h3 class="card-title">学习进度</h3>
              <span class="progress-text">{{ progress.progressPercent || 0 }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: `${progress.progressPercent || 0}%` }"></div>
            </div>
            <div class="progress-meta">
              <span>已学习: {{ progress.studyDurationFormatted || '0分钟' }}</span>
              <span class="auto-save-hint">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10" />
                  <polyline points="12 6 12 12 16 14" />
                </svg>
                自动保存进度
              </span>
            </div>
          </section>

          <!-- Notes Section -->
          <section class="section-card">
            <div class="card-header">
              <h3 class="card-title">学习笔记</h3>
              <Button variant="primary" size="sm" @click="addNote">添加笔记</Button>
            </div>

            <div class="notes-list">
              <template v-if="notes.length > 0">
                <div v-for="note in notes" :key="note.id" class="note-item">
                  <div class="note-header">
                    <span class="note-time">{{ formatTime(note.videoTime) }}</span>
                    <button type="button" class="delete-btn" @click="deleteNote(note.id)">删除</button>
                  </div>
                  <p class="note-content">{{ note.content }}</p>
                </div>
              </template>
              <EmptyState v-else emoji="📝" title="暂无笔记" description="点击添加笔记记录学习心得" size="sm" />
            </div>
          </section>

          <!-- Course Info -->
          <section class="section-card">
            <h3 class="card-title">课程信息</h3>
            <div class="info-list">
              <div class="info-row">
                <span class="info-label">分类</span>
                <span class="info-value">{{ course.categoryName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">难度</span>
                <span class="info-value">{{ course.difficultyName }}</span>
              </div>
              <div class="info-row">
                <span class="info-label">课时</span>
                <span class="info-value">{{ course.durationHours }}小时</span>
              </div>
            </div>
          </section>

          <!-- Stats Chart -->
          <section class="section-card">
            <h3 class="card-title">学习统计</h3>
            <div ref="statsChart" class="stats-chart"></div>
          </section>
        </div>
      </main>
    </div>

    <!-- Add Note Modal -->
    <Modal v-model="noteDialogVisible" title="添加学习笔记" @confirm="saveNote">
      <FormLayout>
        <FormItem label="当前时间">
          <Tag type="info">{{ formatTime(currentVideoTime) }}</Tag>
        </FormItem>
        <FormItem label="笔记内容">
          <Input
            v-model="noteForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入学习笔记..."
          />
        </FormItem>
      </FormLayout>
    </Modal>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Button, Input, Modal, Tag, EmptyState, FormLayout, FormItem } from '@/design-system';
import { getCourseById, getCourseChapters, markChapterCompleted, updateChapterProgress } from '@/api/course';
import { getCourseProgress, updateProgress, checkin, getDashboard } from '@/api/learning';
import { ElMessage } from 'element-plus';
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import * as echarts from 'echarts';

// Types
interface Course {
  name: string;
  categoryName: string;
  difficultyName: string;
  durationHours: number;
}

interface Chapter {
  id: number;
  title: string;
  videoUrl: string;
  duration: number;
  completed: boolean;
  watchDuration?: number;
  lastPosition?: number;
}

interface Progress {
  progressPercent?: number;
  studyDurationFormatted?: string;
  studyDurationMinutes?: number;
}

interface Note {
  id: number;
  videoTime: number;
  content: string;
  chapterId?: number;
  chapterTitle?: string;
  createdAt: string;
}

const route = useRoute();
const router = useRouter();

// State
const course = ref<Course>({} as Course);
const progress = ref<Progress>({});
const studySeconds = ref(0);
const totalStudySeconds = ref(0);
const sessionStartTime = ref<number | null>(null);
const lastSaveTime = ref<number | null>(null);
const videoPlayer = ref<HTMLVideoElement | null>(null);
const statsChart = ref<HTMLElement | null>(null);

let player: ReturnType<typeof videojs> | null = null;
let autoSaveTimer: ReturnType<typeof setInterval> | null = null;
let studyTimer: ReturnType<typeof setInterval> | null = null;
let chartInstance: echarts.ECharts | null = null;

// Chapters
const chapters = ref<Chapter[]>([]);
const currentChapter = ref<Chapter | null>(null);
const chapterProgressMap = ref<Record<number, { watchDuration: number; lastPosition: number }>>({});

const completedCount = computed(() => chapters.value.filter(c => c.completed).length);

// Notes
const notes = ref<Note[]>([]);
const noteDialogVisible = ref(false);
const currentVideoTime = ref(0);
const noteForm = ref({ content: '' });

// Methods
const goBack = () => {
  if (player && !player.paused()) {
    ElMessage.warning('请先暂停视频');
    return;
  }
  router.back();
};

const formatTime = (seconds: number): string => {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  if (h > 0) return `${h}小时${m}分${s}秒`;
  if (m > 0) return `${m}分${s}秒`;
  return `${s}秒`;
};

const formatDuration = (seconds: number): string => {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${m}:${s.toString().padStart(2, '0')}`;
};

// Video Player
const initPlayer = () => {
  if (!videoPlayer.value) return;

  player = videojs(videoPlayer.value, {
    controls: true,
    autoplay: false,
    preload: 'auto',
    fluid: true,
    playbackRates: [0.5, 0.75, 1, 1.25, 1.5, 2],
    controlBar: {
      children: [
        'playToggle',
        'volumePanel',
        'currentTimeDisplay',
        'timeDivider',
        'durationDisplay',
        'progressControl',
        'playbackRateMenuButton',
        'fullscreenToggle',
      ],
    },
  });

  player.on('play', () => {
    if (!sessionStartTime.value) {
      sessionStartTime.value = Date.now();
    }
    startStudyTimer();
  });

  player.on('pause', () => {
    stopStudyTimer();
  });

  player.on('timeupdate', () => {
    currentVideoTime.value = Math.floor(player!.currentTime() || 0);
    if (currentChapter.value && currentVideoTime.value % 10 === 0) {
      saveVideoProgress();
    }
  });

  player.on('ended', () => {
    stopStudyTimer();
    if (currentChapter.value) {
      markCurrentChapterCompleted();
      ElMessage.success('本章节学习完成！');
      autoSaveProgress();
    }
  });
};

const startStudyTimer = () => {
  if (studyTimer) return;
  studyTimer = setInterval(() => {
    studySeconds.value++;
  }, 1000);
};

const stopStudyTimer = () => {
  if (studyTimer) {
    clearInterval(studyTimer);
    studyTimer = null;
  }
};

const selectChapter = async (chapter: Chapter) => {
  if (!player) return;

  if (currentChapter.value) {
    await saveVideoProgress();
  }

  currentChapter.value = chapter;
  player.src({ type: 'video/mp4', src: chapter.videoUrl });
  player.load();

  const savedProgress = chapterProgressMap.value[chapter.id];
  if (savedProgress && savedProgress.lastPosition > 0) {
    player.currentTime(savedProgress.lastPosition);
    ElMessage.info(`正在播放：${chapter.title}（从 ${formatDuration(savedProgress.lastPosition)} 继续）`);
  } else {
    ElMessage.info(`正在播放：${chapter.title}`);
  }
};

const saveVideoProgress = async () => {
  if (!currentChapter.value || !player) return;

  const currentTime = Math.floor(player.currentTime() || 0);
  const duration = Math.floor(player.duration() || 0);

  if (currentTime < 5) return;

  try {
    await updateChapterProgress(currentChapter.value.id, duration, currentTime);
    chapterProgressMap.value[currentChapter.value.id] = {
      watchDuration: duration,
      lastPosition: currentTime,
    };

    if (duration > 0 && currentTime / duration >= 0.95 && !currentChapter.value.completed) {
      await markCurrentChapterCompleted();
    }
  } catch (error) {
    console.error('保存播放进度失败:', error);
  }
};

const startAutoSave = () => {
  lastSaveTime.value = Date.now();
  autoSaveTimer = setInterval(() => {
    if (studySeconds.value > 0) {
      autoSaveProgress();
    }
  }, 60 * 1000);
};

const autoSaveProgress = async () => {
  if (studySeconds.value === 0) return;

  try {
    const studyMinutes = Math.ceil(studySeconds.value / 60);
    const completedChapters = chapters.value.filter((c) => c.completed).length;
    const progressPercent = Math.floor((completedChapters / chapters.value.length) * 100);

    const res = await updateProgress({
      courseId: route.params.id,
      studyMinutes,
      progressPercent,
    });

    await checkin({
      courseId: route.params.id,
      studyMinutes,
    });

    if (res && res.code === 200) {
      const totalMinutes = Math.ceil((totalStudySeconds.value + studySeconds.value) / 60);
      ElMessage.success(`学习进度已保存（累计 ${totalMinutes} 分钟）`);
      totalStudySeconds.value += studySeconds.value;
      studySeconds.value = 0;
      lastSaveTime.value = Date.now();
      await loadProgress();
    }
  } catch (error) {
    console.error('自动保存失败:', error);
    ElMessage.error('保存进度失败，请检查网络连接');
  }
};

// Notes
const addNote = () => {
  if (!player) {
    ElMessage.warning('请先播放视频');
    return;
  }
  currentVideoTime.value = Math.floor(player.currentTime() || 0);
  noteForm.value.content = '';
  noteDialogVisible.value = true;
};

const saveNote = () => {
  if (!noteForm.value.content.trim()) {
    ElMessage.warning('请输入笔记内容');
    return;
  }

  const note: Note = {
    id: Date.now(),
    videoTime: currentVideoTime.value,
    content: noteForm.value.content,
    chapterId: currentChapter.value?.id,
    chapterTitle: currentChapter.value?.title,
    createdAt: new Date().toISOString(),
  };

  notes.value.unshift(note);
  noteDialogVisible.value = false;
  ElMessage.success('笔记已保存');
  localStorage.setItem(`course_notes_${route.params.id}`, JSON.stringify(notes.value));
};

const deleteNote = (noteId: number) => {
  notes.value = notes.value.filter((n) => n.id !== noteId);
  localStorage.setItem(`course_notes_${route.params.id}`, JSON.stringify(notes.value));
  ElMessage.success('笔记已删除');
};

const loadNotes = () => {
  const savedNotes = localStorage.getItem(`course_notes_${route.params.id}`);
  if (savedNotes) {
    notes.value = JSON.parse(savedNotes);
  }
};

// Charts
const initStatsChart = async () => {
  if (!statsChart.value) return;

  try {
    const dashboardRes = await getDashboard();
    let weeklyData = [0, 0, 0, 0, 0, 0, 0];

    if (dashboardRes?.code === 200 && dashboardRes.data?.weeklyStudyData) {
      weeklyData = dashboardRes.data.weeklyStudyData.map((item: { studyMinutes?: number }) => item.studyMinutes || 0);
    }

    const style = getComputedStyle(document.documentElement);
    const primaryRgb = (style.getPropertyValue('--primary-color-rgb') || '37 99 235').trim().replace(/\s+/g, ' ');
    const textSecondaryRgb = (style.getPropertyValue('--text-secondary-rgb') || '75 85 99').trim().replace(/\s+/g, ' ');
    const borderRgb = (style.getPropertyValue('--border-color-rgb') || '229 231 235').trim().replace(/\s+/g, ' ');

    const primary = `rgba(${primaryRgb.replace(/\s+/g, ',')}, 0.9)`;
    const primarySoft = `rgba(${primaryRgb.replace(/\s+/g, ',')}, 0.18)`;
    const textSecondary = `rgba(${textSecondaryRgb.replace(/\s+/g, ',')}, 0.9)`;
    const border = `rgba(${borderRgb.replace(/\s+/g, ',')}, 0.7)`;

    chartInstance = echarts.init(statsChart.value);

    chartInstance.setOption({
      tooltip: { trigger: 'axis', formatter: '{b}: {c} 分钟' },
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
          areaStyle: { color: primarySoft },
          itemStyle: { color: primary },
          lineStyle: { color: primary, width: 2 },
        },
      ],
    });
  } catch (error) {
    console.error('加载学习统计失败:', error);
  }
};

// Data Loading
const loadCourse = async () => {
  try {
    const res = await getCourseById(route.params.id as string);
    course.value = res.data;
  } catch (error) {
    ElMessage.error('加载课程失败');
    router.back();
  }
};

const loadChapters = async () => {
  try {
    const res = await getCourseChapters(route.params.id as string);
    if (res?.code === 200) {
      chapters.value = res.data || [];

      chapters.value.forEach((chapter) => {
        if (chapter.watchDuration || chapter.lastPosition) {
          chapterProgressMap.value[chapter.id] = {
            watchDuration: chapter.watchDuration || 0,
            lastPosition: chapter.lastPosition || 0,
          };
        }
      });

      if (chapters.value.length > 0) {
        await nextTick();
        const firstIncomplete = chapters.value.find((c) => !c.completed);
        selectChapter(firstIncomplete || chapters.value[0]);
      }
    }
  } catch (error) {
    console.error('加载章节失败:', error);
    ElMessage.error('加载章节失败');
  }
};

const loadProgress = async () => {
  try {
    const res = await getCourseProgress(route.params.id as string);
    if (res?.code === 200 && res.data) {
      progress.value = res.data;
      if (res.data.studyDurationMinutes) {
        totalStudySeconds.value = res.data.studyDurationMinutes * 60;
      }
    }
  } catch (error) {
    console.error('加载进度失败:', error);
  }
};

const markCurrentChapterCompleted = async () => {
  if (!currentChapter.value) return;

  try {
    await markChapterCompleted(currentChapter.value.id);
    currentChapter.value.completed = true;
    await loadChapters();
  } catch (error) {
    console.error('标记章节完成失败:', error);
  }
};

// Lifecycle
onMounted(async () => {
  await loadCourse();
  await loadProgress();
  await loadChapters();
  loadNotes();

  await nextTick();
  initPlayer();
  await initStatsChart();
  startAutoSave();
});

onBeforeUnmount(async () => {
  stopStudyTimer();

  if (studySeconds.value > 0) {
    try {
      await updateProgress({
        courseId: route.params.id,
        studyMinutes: Math.ceil(studySeconds.value / 60),
        progressPercent: progress.value.progressPercent || 0,
      });
    } catch (error) {
      console.error('离开前保存失败:', error);
    }
  }

  if (player) player.dispose();
  if (autoSaveTimer) clearInterval(autoSaveTimer);
  if (chartInstance) chartInstance.dispose();
});
</script>

<style scoped>
/* ========================================
   Apple 风格课程学习页
   ======================================== */

.course-study-page {
  min-height: 100vh;
  background: var(--bg-primary);
}

/* ===== Header ===== */
.study-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(var(--bg-secondary-rgb, 255, 255, 255) / 0.72);
  backdrop-filter: saturate(180%) blur(20px);
  -webkit-backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid var(--border-color);
}

.header-inner {
  display: flex;
  align-items: center;
  gap: 20px;
  max-width: 1600px;
  margin: 0 auto;
  padding: 12px 24px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: var(--bg-tertiary);
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.back-btn:hover {
  background: var(--bg-hover);
  color: var(--text-primary);
}

.course-name {
  flex: 1;
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.study-timer {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-color);
}

/* ===== Layout ===== */
.study-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  min-height: calc(100vh - 57px);
}

@media (max-width: 1024px) {
  .study-layout {
    grid-template-columns: 1fr;
  }

  .outline-sidebar {
    display: none;
  }
}

/* ===== Sidebar ===== */
.outline-sidebar {
  position: sticky;
  top: 57px;
  height: calc(100vh - 57px);
  overflow-y: auto;
  background: var(--bg-secondary);
  border-right: 0.5px solid var(--border-color);
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 0.5px solid var(--border-color);
}

.sidebar-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.progress-badge {
  padding: 4px 10px;
  background: var(--primary-color);
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.chapter-list {
  padding: 12px 0;
}

.chapter-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 20px;
  background: none;
  border: none;
  border-left: 3px solid transparent;
  text-align: left;
  cursor: pointer;
  transition: background-color 0.15s ease, border-color 0.15s ease;
}

.chapter-item:hover {
  background: var(--bg-tertiary);
}

.chapter-item.active {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.08);
  border-left-color: var(--primary-color);
}

.chapter-item.completed .chapter-title {
  color: var(--text-muted);
}

.chapter-status {
  flex-shrink: 0;
}

.status-icon {
  color: var(--text-muted);
}

.status-icon.completed {
  color: var(--success);
}

.chapter-title {
  flex: 1;
  font-size: 14px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chapter-duration {
  flex-shrink: 0;
  font-size: 12px;
  color: var(--text-muted);
  font-variant-numeric: tabular-nums;
}

/* ===== Main Content ===== */
.study-content {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.video-container {
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 32px;
}

.content-sections {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

@media (max-width: 1200px) {
  .content-sections {
    grid-template-columns: 1fr;
  }
}

/* ===== Section Card ===== */
.section-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 20px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.04),
    0 2px 4px rgba(0, 0, 0, 0.04);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

/* ===== Progress Card ===== */
.progress-card {
  grid-column: 1 / -1;
}

.progress-text {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
  font-variant-numeric: tabular-nums;
}

.progress-bar {
  height: 6px;
  background: var(--bg-tertiary);
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 12px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--primary-color), var(--primary-light, #5ac8fa));
  border-radius: 3px;
  transition: width 0.5s ease;
}

.progress-meta {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--text-secondary);
}

.auto-save-hint {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--text-muted);
}

/* ===== Notes ===== */
.notes-list {
  max-height: 300px;
  overflow-y: auto;
}

.note-item {
  padding: 12px 0;
  border-bottom: 0.5px solid var(--border-light);
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
  font-weight: 600;
  color: var(--primary-color);
}

.delete-btn {
  padding: 4px 8px;
  background: none;
  border: none;
  font-size: 12px;
  color: var(--error);
  cursor: pointer;
  transition: opacity 0.2s ease;
}

.delete-btn:hover {
  opacity: 0.7;
}

.note-content {
  font-size: 14px;
  line-height: 1.6;
  color: var(--text-secondary);
  white-space: pre-wrap;
}

/* ===== Info List ===== */
.info-list {
  display: flex;
  flex-direction: column;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 0.5px solid var(--border-light);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 13px;
  color: var(--text-muted);
}

.info-value {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}

/* ===== Stats Chart ===== */
.stats-chart {
  width: 100%;
  height: 200px;
}

/* ===== Dark Mode ===== */
[data-theme="dark"] .study-header {
  background: rgba(29, 29, 31, 0.72);
}

[data-theme="dark"] .outline-sidebar {
  background: var(--bg-secondary);
}

[data-theme="dark"] .section-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}

[data-theme="dark"] .chapter-item.active {
  background: rgba(var(--primary-color-rgb, 0, 122, 255) / 0.15);
}
</style>
