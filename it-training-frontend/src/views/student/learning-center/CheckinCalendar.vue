<template>
  <section class="section-card">
    <div class="card-header">
      <h2 class="card-title">打卡日历</h2>
      <div class="calendar-nav">
        <button type="button" class="nav-btn" @click="$emit('prev')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <span class="nav-month tabular-nums">{{ monthLabel }}</span>
        <button type="button" class="nav-btn" @click="$emit('next')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </div>

    <div class="calendar-weekdays">
      <span v-for="d in weekDays" :key="d" class="weekday">{{ d }}</span>
    </div>

    <div class="calendar-grid">
      <div
        v-for="(day, idx) in calendarDays"
        :key="idx"
        class="calendar-day"
        :class="{
          'other-month': !day.currentMonth,
          'checked-in': day.checkedIn,
          'is-today': day.isToday,
        }"
      >
        {{ day.date }}
      </div>
    </div>

    <div class="calendar-legend">
      <span class="legend-item">
        <span class="legend-dot legend-dot-success"></span>
        已打卡
      </span>
      <span class="legend-item">
        <span class="legend-dot legend-dot-today"></span>
        今天
      </span>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface CalendarDay {
  date: number;
  currentMonth: boolean;
  checkedIn: boolean;
  isToday: boolean;
}

const props = defineProps<{
  year: number;
  month: number;
  checkinDates: string[];
}>();

defineEmits<{
  (e: 'prev'): void;
  (e: 'next'): void;
}>();

const weekDays = ['日', '一', '二', '三', '四', '五', '六'];

const monthLabel = computed(() => `${props.year}-${String(props.month).padStart(2, '0')}`);

const calendarDays = computed<CalendarDay[]>(() => {
  const year = props.year;
  const month = props.month;
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);
  const daysInMonth = lastDay.getDate();
  const startWeekDay = firstDay.getDay();

  const days: CalendarDay[] = [];
  const today = new Date();
  const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

  const prevMonthLastDay = new Date(year, month - 1, 0).getDate();
  for (let i = startWeekDay - 1; i >= 0; i--) {
    days.push({ date: prevMonthLastDay - i, currentMonth: false, checkedIn: false, isToday: false });
  }

  for (let i = 1; i <= daysInMonth; i++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
    days.push({
      date: i,
      currentMonth: true,
      checkedIn: props.checkinDates.includes(dateStr),
      isToday: dateStr === todayStr,
    });
  }

  const remaining = 42 - days.length;
  for (let i = 1; i <= remaining; i++) {
    days.push({ date: i, currentMonth: false, checkedIn: false, isToday: false });
  }

  return days;
});
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
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

/* Calendar Nav */
.calendar-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-tertiary);
  border: none;
  border-radius: 6px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.nav-btn:hover {
  background: var(--bg-hover);
}

.nav-btn svg {
  width: 14px;
  height: 14px;
}

.nav-month {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  min-width: 80px;
  text-align: center;
}

/* Calendar Weekdays */
.calendar-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 8px;
}

.weekday {
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
  text-align: center;
  padding: 4px 0;
}

/* Calendar Grid */
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.calendar-day.other-month {
  color: var(--text-muted);
  opacity: 0.4;
}

.calendar-day.checked-in {
  background: var(--success);
  color: white;
}

.calendar-day.is-today {
  box-shadow: inset 0 0 0 2px var(--primary-color);
}

.calendar-day.is-today.checked-in {
  box-shadow: inset 0 0 0 2px var(--primary-color);
}

/* Calendar Legend */
.calendar-legend {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 0.5px solid var(--border-light);
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: var(--text-muted);
}

.legend-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.legend-dot-success {
  background: var(--success);
}

.legend-dot-today {
  border: 2px solid var(--primary-color);
  background: transparent;
}

.tabular-nums {
  font-variant-numeric: tabular-nums;
}

[data-theme="dark"] .section-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}
</style>
