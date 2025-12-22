<template>
  <div class="min-h-screen bg-bg-primary">
    <!-- Header -->
    <header class="sticky top-0 z-20 bg-glass border-b border-border-color/60 backdrop-blur-xl px-6 py-4">
      <div class="flex items-center gap-5">
        <Button variant="ghost" @click="goBack">
          <template #icon>
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </template>
          返回
        </Button>

        <h2 class="flex-1 text-lg font-semibold text-text-primary truncate">{{ course.name }}</h2>

        <div class="flex items-center gap-2 text-primary font-semibold">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <span>本次学习: {{ formatTime(studySeconds) }}</span>
        </div>
      </div>
    </header>

    <!-- Content -->
    <div class="grid grid-cols-1 lg:grid-cols-[1fr_350px] gap-6 p-6 max-w-[1600px] mx-auto">
      <!-- Main Area -->
      <div class="space-y-6">
        <!-- Video Player -->
        <div class="bg-black rounded-2xl overflow-hidden">
          <video ref="videoPlayer" class="video-js vjs-big-play-centered w-full"></video>
        </div>

        <!-- Chapters -->
        <Section title="课程章节">
          <template #action>
            <Tag>{{ chapters.length }} 章节</Tag>
          </template>

          <div class="max-h-[300px] overflow-y-auto">
            <div
              v-for="chapter in chapters"
              :key="chapter.id"
              class="flex items-center justify-between px-4 py-3 border-b border-border-color/60 last:border-b-0 cursor-pointer transition-colors"
              :class="currentChapter?.id === chapter.id ? 'bg-primary/10 border-l-3 border-l-primary' : 'hover:bg-bg-secondary'"
              @click="selectChapter(chapter)"
            >
              <div class="flex items-center gap-3 flex-1 min-w-0">
                <svg v-if="chapter.completed" class="w-5 h-5 text-success flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <svg v-else class="w-5 h-5 text-text-muted flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z" />
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span class="text-sm text-text-primary truncate">{{ chapter.title }}</span>
              </div>
              <span class="text-xs text-text-muted flex-shrink-0">{{ formatDuration(chapter.duration) }}</span>
            </div>
          </div>
        </Section>

        <!-- Notes -->
        <Section title="学习笔记">
          <template #action>
            <Button variant="primary" size="sm" @click="addNote">添加笔记</Button>
          </template>

          <div class="max-h-[400px] overflow-y-auto">
            <template v-if="notes.length > 0">
              <div
                v-for="note in notes"
                :key="note.id"
                class="px-4 py-3 border-b border-border-color/60 last:border-b-0"
              >
                <div class="flex items-center justify-between mb-2">
                  <span class="text-xs font-semibold text-primary">{{ formatTime(note.videoTime) }}</span>
                  <Button variant="ghost" size="sm" class="text-error" @click="deleteNote(note.id)">删除</Button>
                </div>
                <p class="text-sm text-text-secondary leading-relaxed whitespace-pre-wrap">{{ note.content }}</p>
              </div>
            </template>
            <EmptyState v-else emoji="📝" title="暂无笔记" description="点击添加笔记记录学习心得" size="sm" />
          </div>
        </Section>
      </div>

      <!-- Sidebar -->
      <div class="space-y-6">
        <!-- Progress -->
        <Section title="学习进度">
          <div class="space-y-4">
            <div>
              <div class="flex items-center justify-between text-sm mb-2">
                <span class="text-text-secondary">完成度</span>
                <span class="font-semibold text-text-primary">{{ progress.progressPercent || 0 }}%</span>
              </div>
              <div class="h-2 bg-bg-tertiary rounded-full overflow-hidden">
                <div
                  class="h-full bg-primary rounded-full transition-all duration-500"
                  :style="{ width: `${progress.progressPercent || 0}%` }"
                ></div>
              </div>
            </div>
            <p class="text-sm text-text-secondary">已学习: {{ progress.studyDurationFormatted || '0分钟' }}</p>
            <p class="flex items-center gap-2 text-xs text-text-muted">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              自动保存进度（每1分钟）
            </p>
          </div>
        </Section>

        <!-- Course Info -->
        <Section title="课程信息">
          <div class="space-y-3">
            <div class="flex items-center justify-between py-2 border-b border-border-color/60">
              <span class="text-sm text-text-muted">分类</span>
              <span class="text-sm text-text-primary">{{ course.categoryName }}</span>
            </div>
            <div class="flex items-center justify-between py-2 border-b border-border-color/60">
              <span class="text-sm text-text-muted">难度</span>
              <span class="text-sm text-text-primary">{{ course.difficultyName }}</span>
            </div>
            <div class="flex items-center justify-between py-2">
              <span class="text-sm text-text-muted">课时</span>
              <span class="text-sm text-text-primary">{{ course.durationHours }}小时</span>
            </div>
          </div>
        </Section>

        <!-- Stats Chart -->
        <Section title="学习统计">
          <div ref="statsChart" class="w-full h-[200px]"></div>
        </Section>
      </div>
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

    <!-- Toast -->
    <Teleport to="body">
      <Transition name="toast">
        <div
          v-if="toast.visible"
          class="fixed top-20 left-1/2 -translate-x-1/2 z-50 px-4 py-2 rounded-xl text-sm font-medium shadow-lg"
          :class="toastClass"
        >
          {{ toast.message }}
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Button, Input, Modal, Tag, EmptyState, Section, FormLayout, FormItem } from '@/design-system';
import { getCourseById, getCourseChapters, markChapterCompleted, updateChapterProgress } from '@/api/course';
import { getCourseProgress, updateProgress, checkin, getDashboard } from '@/api/learning';
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

// Notes
const notes = ref<Note[]>([]);
const noteDialogVisible = ref(false);
const currentVideoTime = ref(0);
const noteForm = ref({ content: '' });

// Toast
const toast = ref({ visible: false, message: '', type: 'success' as 'success' | 'warning' | 'error' | 'info' });
const toastClass = computed(() => {
  const classes: Record<string, string> = {
    success: 'bg-success text-white',
    warning: 'bg-warning text-white',
    error: 'bg-error text-white',
    info: 'bg-info text-white',
  };
  return classes[toast.value.type] || classes.success;
});

const showToast = (message: string, type: 'success' | 'warning' | 'error' | 'info' = 'success') => {
  toast.value = { visible: true, message, type };
  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
};

// Methods
const goBack = () => {
  if (player && !player.paused()) {
    showToast('请先暂停视频', 'warning');
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
      showToast('本章节学习完成！', 'success');
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
    showToast(`正在播放：${chapter.title}（从 ${formatDuration(savedProgress.lastPosition)} 继续）`, 'info');
  } else {
    showToast(`正在播放：${chapter.title}`, 'info');
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
      showToast(`学习进度已保存（累计 ${totalMinutes} 分钟）`, 'success');
      totalStudySeconds.value += studySeconds.value;
      studySeconds.value = 0;
      lastSaveTime.value = Date.now();
      await loadProgress();
    }
  } catch (error) {
    console.error('自动保存失败:', error);
    showToast('保存进度失败，请检查网络连接', 'error');
  }
};

// Notes
const addNote = () => {
  if (!player) {
    showToast('请先播放视频', 'warning');
    return;
  }
  currentVideoTime.value = Math.floor(player.currentTime() || 0);
  noteForm.value.content = '';
  noteDialogVisible.value = true;
};

const saveNote = () => {
  if (!noteForm.value.content.trim()) {
    showToast('请输入笔记内容', 'warning');
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
  showToast('笔记已保存', 'success');
  localStorage.setItem(`course_notes_${route.params.id}`, JSON.stringify(notes.value));
};

const deleteNote = (noteId: number) => {
  notes.value = notes.value.filter((n) => n.id !== noteId);
  localStorage.setItem(`course_notes_${route.params.id}`, JSON.stringify(notes.value));
  showToast('笔记已删除', 'success');
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
    showToast('加载课程失败', 'error');
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
    showToast('加载章节失败', 'error');
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
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translate(-50%, -20px);
}

.border-l-3 {
  border-left-width: 3px;
}
</style>
