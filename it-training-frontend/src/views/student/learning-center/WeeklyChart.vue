<template>
  <section class="section-card">
    <div class="card-header">
      <div class="card-header-left">
        <h2 class="card-title">本周学习</h2>
        <p class="card-subtitle">分钟趋势</p>
      </div>
    </div>

    <div v-if="weeklyData?.length" class="weekly-chart">
      <div v-for="(d, i) in weeklyData" :key="i" class="chart-bar-wrapper">
        <div class="chart-bar-container">
          <div class="chart-bar" :style="{ height: `${getBarHeight(d.studyMinutes)}%` }"></div>
        </div>
        <span class="chart-label">{{ weekDays[new Date(d.date).getDay()] }}</span>
      </div>
    </div>

    <EmptyState v-else emoji="✨" title="暂无统计" description="开始学习后，这里会展示你的本周趋势。" size="sm" />
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { EmptyState } from '@/design-system';

interface WeeklyStudyData {
  date: string;
  studyMinutes: number;
}

const props = defineProps<{
  weeklyData: WeeklyStudyData[] | undefined;
}>();

const weekDays = ['日', '一', '二', '三', '四', '五', '六'];

const maxMinutes = computed(() => {
  return Math.max(...(props.weeklyData?.map((d) => d.studyMinutes) || [60]));
});

function getBarHeight(minutes: number): number {
  return maxMinutes.value > 0 ? (minutes / maxMinutes.value) * 100 : 0;
}
</script>

<style scoped>
.section-card {
  background: var(--bg-card);
  border-radius: 16px;
  padding: 24px;
  box-shadow:
    0 1px 1px rgba(0, 0, 0, 0.03),
    0 2px 4px rgba(0, 0, 0, 0.03);
  border: 0.5px solid rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 20px;
}

.card-header-left {
  flex: 1;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.card-subtitle {
  font-size: 13px;
  color: var(--text-muted);
  margin-top: 2px;
}

/* Weekly Chart */
.weekly-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 120px;
  padding-top: 8px;
}

.chart-bar-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.chart-bar-container {
  width: 100%;
  max-width: 24px;
  height: 80px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.chart-bar {
  width: 100%;
  background: var(--primary-color);
  border-radius: 4px 4px 0 0;
  min-height: 4px;
  transition: height 0.3s ease;
}

.chart-label {
  font-size: 11px;
  color: var(--text-muted);
}

[data-theme="dark"] .section-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}
</style>
