<template>
  <PageLayout>
    <!-- Hero -->
    <section class="relative rounded-2xl bg-fill-secondary/50 p-8 md:p-10 overflow-hidden">
      <div class="absolute inset-0 pointer-events-none">
        <div class="absolute -top-24 -right-24 w-72 h-72 bg-primary/10 blur-3xl rounded-full"></div>
      </div>
      <div class="relative flex flex-col md:flex-row md:items-center justify-between gap-8">
        <div class="flex items-center gap-4 min-w-0">
          <Avatar :src="profile.avatar" :size="72" class="flex-shrink-0">
            {{ profile.username?.charAt(0)?.toUpperCase() }}
          </Avatar>
          <div class="min-w-0">
            <h1 class="text-2xl md:text-4xl font-semibold tracking-tight text-text-primary truncate">
              {{ profile.realName || profile.username || '—' }}
            </h1>
            <div class="mt-2 flex flex-wrap items-center gap-2">
              <Tag v-if="profile.levelName" :type="getLevelTagType(profile.learningLevel)">
                {{ profile.levelName }}
              </Tag>
              <span v-if="profile.username" class="text-sm text-text-tertiary truncate">@{{ profile.username }}</span>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-3 gap-3 w-full md:w-auto">
          <div class="rounded-xl bg-fill-tertiary/50 p-4 text-center">
            <p class="text-xs text-text-tertiary">学习时长</p>
            <p class="mt-1 text-lg font-semibold text-text-primary">{{ profile.totalStudyMinutes || 0 }}</p>
            <p class="text-xs text-text-tertiary">分钟</p>
          </div>
          <div class="rounded-xl bg-fill-tertiary/50 p-4 text-center">
            <p class="text-xs text-text-tertiary">完成课程</p>
            <p class="mt-1 text-lg font-semibold text-text-primary">{{ profile.completedCourses || 0 }}</p>
            <p class="text-xs text-text-tertiary">门</p>
          </div>
          <div class="rounded-xl bg-fill-tertiary/50 p-4 text-center">
            <p class="text-xs text-text-tertiary">获得成就</p>
            <p class="mt-1 text-lg font-semibold text-text-primary">{{ profile.achievementCount || 0 }}</p>
            <p class="text-xs text-text-tertiary">个</p>
          </div>
        </div>
      </div>
    </section>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
      <!-- Streak -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习状态</h2>
        <InsetGroup>
          <InsetItem label="连续打卡" :value="`${profile.currentStreak || 0} 天（最长 ${profile.maxStreak || 0} 天）`">
            <template #icon>
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" />
                <line x1="3" y1="10" x2="21" y2="10" />
              </svg>
            </template>
          </InsetItem>
          <InsetItem label="成就积分" :value="String(profile.achievementPoints || 0)" last>
            <template #icon>
              <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6" /><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18" />
                <path d="M4 22h16" /><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22" />
                <path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22" />
                <path d="M18 2H6v7a6 6 0 0 0 12 0V2Z" />
              </svg>
            </template>
          </InsetItem>
        </InsetGroup>
      </section>

      <!-- Radar -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <div class="flex items-center justify-between gap-4 mb-4">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习能力评估</h2>
          <Tag v-if="assessment.learnerType" type="success">{{ assessment.learnerType }}</Tag>
        </div>
        <div ref="radarChart" class="h-64 w-full"></div>
        <p v-if="assessment.overallScore" class="mt-3 text-sm text-text-secondary">
          综合评分：<span class="font-semibold text-text-primary">{{ assessment.overallScore }}</span>/100
        </p>
      </section>

      <!-- Skills -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <div class="flex items-center justify-between gap-4 mb-4">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary">技能标签</h2>
          <Button variant="ghost" size="sm" @click="showSkillDialog = true">
            <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
            </svg>
            编辑
          </Button>
        </div>
        <div class="flex flex-wrap gap-2">
          <Tag
            v-for="skill in profile.skillTags"
            :key="skill.tag"
            :type="getSkillTagType(skill.level)"
          >
            {{ skill.tag }}
            <span class="ml-1 text-xs opacity-70">Lv.{{ skill.level }}</span>
          </Tag>
          <EmptyState v-if="!profile.skillTags?.length" emoji="🏷️" title="暂无技能标签" size="sm" />
        </div>
      </section>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-6">
      <!-- Preference -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <div class="flex items-center justify-between gap-4 mb-4">
          <h2 class="text-lg font-semibold tracking-tight text-text-primary">学习偏好</h2>
          <Button variant="ghost" size="sm" @click="showPreferenceDialog = true">
            <svg class="w-4 h-4 mr-1" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="3" />
              <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z" />
            </svg>
            设置
          </Button>
        </div>
        <InsetGroup>
          <InsetItem label="偏好类别">
            <template #suffix>
              <div class="flex flex-wrap gap-1 justify-end">
                <Tag v-for="cat in profile.preference?.preferredCategories" :key="cat" size="sm">{{ cat }}</Tag>
                <span v-if="!profile.preference?.preferredCategories?.length" class="text-text-tertiary text-sm">未设置</span>
              </div>
            </template>
          </InsetItem>
          <InsetItem label="偏好难度" :value="profile.preference?.preferredDifficulty || '未设置'" />
          <InsetItem label="每日学习目标" :value="`${profile.preference?.dailyStudyGoal || 30} 分钟`" />
          <InsetItem label="偏好学习时间" :value="profile.preference?.preferredStudyTime || '未设置'" last />
        </InsetGroup>
      </section>

      <!-- Time -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习时间分布</h2>
        <div ref="timeChart" class="h-64 w-full"></div>
      </section>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-6">
      <!-- Suggestions -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习建议</h2>
        <Timeline v-if="assessment.suggestions?.length">
          <TimelineItem
            v-for="(suggestion, index) in assessment.suggestions"
            :key="index"
            :type="index === 0 ? 'primary' : 'info'"
            :hollow="index !== 0"
          >
            {{ suggestion }}
          </TimelineItem>
        </Timeline>
        <EmptyState v-else emoji="💡" title="暂无建议" size="sm" />
      </section>

      <!-- Milestones -->
      <section class="rounded-2xl bg-fill-secondary/50 p-6">
        <h2 class="text-lg font-semibold tracking-tight text-text-primary mb-4">学习里程碑</h2>
        <Timeline v-if="profile.milestones?.length">
          <TimelineItem
            v-for="milestone in profile.milestones"
            :key="milestone.title"
            :timestamp="formatDate(milestone.achievedAt)"
          >
            <div class="rounded-xl bg-fill-tertiary/50 p-4">
              <div class="flex items-start gap-3">
                <div class="w-10 h-10 rounded-2xl bg-fill-secondary border border-border-primary flex items-center justify-center">
                  <span class="text-text-primary">{{ milestone.icon || '•' }}</span>
                </div>
                <div class="min-w-0">
                  <h4 class="font-semibold text-text-primary">{{ milestone.title }}</h4>
                  <p class="mt-1 text-sm text-text-secondary">{{ milestone.description }}</p>
                </div>
              </div>
            </div>
          </TimelineItem>
        </Timeline>
        <EmptyState v-else emoji="🏆" title="暂无里程碑" size="sm" />
      </section>
    </div>

    <!-- Skill Dialog -->
    <Modal v-model="showSkillDialog" title="编辑技能标签" width="520px">
      <FormLayout>
        <FormItem label="技能标签">
          <Select
            v-model="editSkills"
            :options="availableSkillOptions"
            multiple
            placeholder="选择或输入技能标签"
          />
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showSkillDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="saveSkills">保存</Button>
      </template>
    </Modal>

    <!-- Preference Dialog -->
    <Modal v-model="showPreferenceDialog" title="设置学习偏好" width="520px">
      <FormLayout>
        <FormItem label="偏好类别">
          <div class="flex flex-wrap gap-2">
            <label
              v-for="cat in categoryOptions"
              :key="cat.value"
              class="inline-flex items-center gap-2 px-3 py-1.5 rounded-lg cursor-pointer transition-colors"
              :class="editPreference.preferredCategories.includes(cat.value)
                ? 'bg-primary/10 text-primary'
                : 'bg-fill-tertiary text-text-secondary hover:bg-fill-quaternary'"
            >
              <Checkbox
                :model-value="editPreference.preferredCategories.includes(cat.value)"
                @update:model-value="toggleCategory(cat.value)"
              />
              {{ cat.label }}
            </label>
          </div>
        </FormItem>
        <FormItem label="偏好难度">
          <Select v-model="editPreference.preferredDifficulty" :options="difficultyOptions" />
        </FormItem>
        <FormItem label="每日目标">
          <div class="flex items-center gap-3">
            <Input
              v-model.number="editPreference.dailyStudyGoal"
              type="number"
              :min="15"
              :max="180"
              class="w-24"
            />
            <span class="text-sm text-text-secondary">分钟/天</span>
          </div>
        </FormItem>
      </FormLayout>
      <template #footer>
        <Button variant="secondary" @click="showPreferenceDialog = false">取消</Button>
        <Button variant="primary" :loading="saving" @click="savePreferences">保存</Button>
      </template>
    </Modal>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch, onUnmounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';
import {
  PageLayout,
  Button,
  Input,
  Select,
  Checkbox,
  Modal,
  FormLayout,
  FormItem,
  InsetGroup,
  InsetItem,
  Avatar,
  Tag,
  Timeline,
  TimelineItem,
  EmptyState,
} from '@/design-system';
import { getUserProfile, updateSkillTags, updatePreferences, getLearningAbilityAssessment } from '@/api/learning';

const profile = ref<Record<string, any>>({});
const assessment = ref<Record<string, any>>({});
const loading = ref(false);
const saving = ref(false);

const showSkillDialog = ref(false);
const showPreferenceDialog = ref(false);
const editSkills = ref<string[]>([]);
const editPreference = ref({
  preferredCategories: [] as string[],
  preferredDifficulty: '中级',
  dailyStudyGoal: 30,
});

const availableSkills = [
  'Java', 'Python', 'JavaScript', 'TypeScript', 'Vue.js', 'React',
  'Spring Boot', 'MySQL', 'Redis', 'Docker', 'Kubernetes', 'Git',
  'Linux', 'AWS', 'Node.js', 'Go', 'Rust', 'C++', 'HTML/CSS',
];

const availableSkillOptions = computed(() =>
  availableSkills.map(s => ({ label: s, value: s }))
);

const categoryOptions = [
  { label: '编程开发', value: 'PROGRAMMING' },
  { label: '数据库', value: 'DATABASE' },
  { label: '前端开发', value: 'FRONTEND' },
  { label: '后端开发', value: 'BACKEND' },
  { label: '运维部署', value: 'DEVOPS' },
  { label: '人工智能', value: 'AI' },
];

const difficultyOptions = [
  { label: '入门', value: '入门' },
  { label: '初级', value: '初级' },
  { label: '中级', value: '中级' },
  { label: '高级', value: '高级' },
];

const radarChart = ref<HTMLElement | null>(null);
const timeChart = ref<HTMLElement | null>(null);
let radarChartInstance: echarts.ECharts | null = null;
let timeChartInstance: echarts.ECharts | null = null;
let themeObserver: MutationObserver | null = null;

const normalizeRgb = (value: string, fallback: string) => {
  const cleaned = (value || '').trim();
  if (!cleaned) return fallback;
  return cleaned.replace(/\s+/g, ' ');
};

const rgba = (rgb: string, alpha: number) => `rgba(${rgb.replace(/\s+/g, ',')}, ${alpha})`;

const getThemeColors = () => {
  const style = getComputedStyle(document.documentElement);
  const primaryRgb = normalizeRgb(style.getPropertyValue('--primary-color-rgb'), '37 99 235');
  const textSecondaryRgb = normalizeRgb(style.getPropertyValue('--text-secondary-rgb'), '75 85 99');
  const borderRgb = normalizeRgb(style.getPropertyValue('--border-color-rgb'), '229 231 235');
  const bgSecondaryRgb = normalizeRgb(style.getPropertyValue('--bg-secondary-rgb'), '255 255 255');
  return { primaryRgb, textSecondaryRgb, borderRgb, bgSecondaryRgb };
};

const fetchProfile = async () => {
  loading.value = true;
  try {
    const res = await getUserProfile();
    profile.value = res.data || {};
    editSkills.value = profile.value.skillTags?.map((s: any) => s.tag) || [];
    if (profile.value.preference) {
      editPreference.value = {
        preferredCategories: profile.value.preference.preferredCategories || [],
        preferredDifficulty: profile.value.preference.preferredDifficulty || '中级',
        dailyStudyGoal: profile.value.preference.dailyStudyGoal || 30,
      };
    }
  } catch (error) {
    console.error('获取用户画像失败:', error);
  } finally {
    loading.value = false;
  }
};

const fetchAssessment = async () => {
  try {
    const res = await getLearningAbilityAssessment();
    assessment.value = res.data || {};
    nextTick(() => initRadarChart());
  } catch (error) {
    console.error('获取能力评估失败:', error);
  }
};

const initRadarChart = () => {
  if (!radarChart.value) return;
  radarChartInstance?.dispose();
  radarChartInstance = echarts.init(radarChart.value);

  const { primaryRgb, textSecondaryRgb, borderRgb, bgSecondaryRgb } = getThemeColors();
  const radar = assessment.value.radar || {};

  radarChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: {},
    radar: {
      indicator: [
        { name: '学习速度', max: 100 },
        { name: '坚持度', max: 100 },
        { name: '理解力', max: 100 },
        { name: '实践能力', max: 100 },
        { name: '知识广度', max: 100 },
        { name: '知识深度', max: 100 },
      ],
      shape: 'polygon',
      splitNumber: 5,
      axisName: { color: rgba(textSecondaryRgb, 0.9) },
      splitLine: { lineStyle: { color: [rgba(borderRgb, 0.9)] } },
      axisLine: { lineStyle: { color: rgba(borderRgb, 0.7) } },
      splitArea: { show: true, areaStyle: { color: [rgba(primaryRgb, 0.05), rgba(bgSecondaryRgb, 0.0)] } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: [
          radar.learningSpeed || 50,
          radar.persistence || 50,
          radar.comprehension || 50,
          radar.practiceAbility || 50,
          radar.breadth || 50,
          radar.depth || 50,
        ],
        name: '能力值',
        areaStyle: { color: rgba(primaryRgb, 0.22) },
        lineStyle: { color: rgba(primaryRgb, 0.85), width: 2 },
        itemStyle: { color: rgba(primaryRgb, 0.95) },
      }],
    }],
  });
};

const initTimeChart = () => {
  if (!timeChart.value) return;
  timeChartInstance?.dispose();
  timeChartInstance = echarts.init(timeChart.value);

  const { primaryRgb, textSecondaryRgb, borderRgb } = getThemeColors();
  const timeData = profile.value.timeDistribution || [];

  timeChartInstance.setOption({
    backgroundColor: 'transparent',
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 32, right: 16, top: 18, bottom: 46, containLabel: true },
    xAxis: {
      type: 'category',
      data: timeData.map((t: any) => t.timeSlot),
      axisLabel: { rotate: 35, color: rgba(textSecondaryRgb, 0.9) },
      axisLine: { lineStyle: { color: rgba(borderRgb, 0.8) } },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      name: '分钟',
      nameTextStyle: { color: rgba(textSecondaryRgb, 0.7) },
      axisLabel: { color: rgba(textSecondaryRgb, 0.85) },
      splitLine: { lineStyle: { color: rgba(borderRgb, 0.6) } },
    },
    series: [{
      type: 'bar',
      data: timeData.map((t: any) => t.minutes),
      barWidth: 18,
      itemStyle: {
        borderRadius: [10, 10, 6, 6],
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: rgba(primaryRgb, 0.9) },
          { offset: 1, color: rgba(primaryRgb, 0.18) },
        ]),
      },
    }],
  });
};

const toggleCategory = (value: string) => {
  const idx = editPreference.value.preferredCategories.indexOf(value);
  if (idx >= 0) {
    editPreference.value.preferredCategories.splice(idx, 1);
  } else {
    editPreference.value.preferredCategories.push(value);
  }
};

const saveSkills = async () => {
  saving.value = true;
  try {
    await updateSkillTags(editSkills.value);
    ElMessage.success('技能标签已更新');
    showSkillDialog.value = false;
    fetchProfile();
  } catch (error) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const savePreferences = async () => {
  saving.value = true;
  try {
    await updatePreferences(editPreference.value);
    ElMessage.success('学习偏好已更新');
    showPreferenceDialog.value = false;
    fetchProfile();
  } catch (error) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const getLevelTagType = (level: number): 'danger' | 'warning' | 'success' | 'info' => {
  if (level >= 7) return 'danger';
  if (level >= 5) return 'warning';
  if (level >= 3) return 'success';
  return 'info';
};

const getSkillTagType = (level: number): 'danger' | 'warning' | 'success' | 'info' => {
  if (level >= 4) return 'danger';
  if (level >= 3) return 'warning';
  if (level >= 2) return 'success';
  return 'info';
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('zh-CN');
};

const handleResize = () => {
  radarChartInstance?.resize();
  timeChartInstance?.resize();
};

onMounted(() => {
  fetchProfile();
  fetchAssessment();
  window.addEventListener('resize', handleResize);

  themeObserver = new MutationObserver(() => {
    nextTick(() => {
      initRadarChart();
      initTimeChart();
    });
  });
  themeObserver.observe(document.documentElement, { attributes: true, attributeFilter: ['data-theme'] });
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  themeObserver?.disconnect();
  radarChartInstance?.dispose();
  timeChartInstance?.dispose();
});

watch(
  () => profile.value.timeDistribution,
  () => nextTick(() => initTimeChart()),
  { deep: true }
);
</script>
