<template>
  <section class="section-card">
    <div class="card-header">
      <h2 class="card-title">今日打卡</h2>
      <span
        class="status-badge"
        :class="isCheckedIn ? 'status-success' : 'status-warning'"
      >
        {{ isCheckedIn ? '已打卡' : '未打卡' }}
      </span>
    </div>

    <!-- Not Checked In -->
    <div v-if="!isCheckedIn" class="checkin-form">
      <div class="study-time-box">
        <div class="study-time-row">
          <span class="study-time-label">今日学习时长</span>
          <span class="study-time-value tabular-nums">{{ studyMinutes }} 分钟</span>
        </div>
        <p class="study-time-hint">系统将自动统计你今天的学习时长。</p>
      </div>

      <div class="note-field">
        <label class="note-label">学习笔记（可选）</label>
        <textarea
          v-model="studyContent"
          class="note-textarea"
          placeholder="记录今天学到了什么..."
        ></textarea>
      </div>

      <div class="checkin-actions">
        <Button variant="primary" :disabled="loading || studyMinutes === 0" @click="handleCheckin">
          {{ loading ? '打卡中...' : '立即打卡' }}
        </Button>
        <span v-if="studyMinutes === 0" class="checkin-warning">请先学习课程后再打卡</span>
      </div>
    </div>

    <!-- Already Checked In -->
    <div v-else class="checkin-success">
      <div class="success-icon">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      </div>
      <div class="success-info">
        <p class="success-text">
          今日已打卡，学习了
          <span class="success-minutes tabular-nums">{{ checkinData?.studyMinutes || 0 }}</span>
          分钟
        </p>
        <p v-if="checkinData?.studyContent" class="success-note">{{ checkinData.studyContent }}</p>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { Button } from '@/design-system';
import { checkin } from '@/api/learning';
import { ElMessage } from 'element-plus';

interface CheckinData {
  studyMinutes: number;
  studyContent?: string;
}

const props = defineProps<{
  isCheckedIn: boolean;
  studyMinutes: number;
  checkinData: CheckinData | null;
}>();

const emit = defineEmits<{
  (e: 'success'): void;
}>();

const loading = ref(false);
const studyContent = ref('');

async function handleCheckin() {
  loading.value = true;
  try {
    await checkin({
      studyMinutes: props.studyMinutes,
      studyContent: studyContent.value,
    });
    ElMessage.success('打卡成功！继续保持！');
    studyContent.value = '';
    emit('success');
  } catch (error: any) {
    ElMessage.error(error?.message || '打卡失败');
  } finally {
    loading.value = false;
  }
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
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-primary);
}

.status-badge {
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.status-success {
  background: rgba(52, 199, 89, 0.1);
  color: var(--success);
}

.status-warning {
  background: rgba(255, 149, 0, 0.1);
  color: var(--warning);
}

/* Checkin Form */
.checkin-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.study-time-box {
  padding: 16px;
  background: var(--bg-tertiary);
  border-radius: 12px;
}

.study-time-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.study-time-label {
  font-size: 14px;
  color: var(--text-secondary);
}

.study-time-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

.study-time-hint {
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-muted);
}

.note-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.note-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.note-textarea {
  width: 100%;
  min-height: 80px;
  padding: 12px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  font-size: 14px;
  color: var(--text-primary);
  resize: vertical;
  transition: border-color 0.2s ease;
}

.note-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
}

.checkin-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.checkin-warning {
  font-size: 12px;
  color: var(--text-muted);
}

/* Checkin Success */
.checkin-success {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(52, 199, 89, 0.08);
  border-radius: 12px;
}

.success-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--success);
  color: white;
  border-radius: 50%;
  flex-shrink: 0;
}

.success-icon svg {
  width: 24px;
  height: 24px;
}

.success-text {
  font-size: 15px;
  color: var(--text-primary);
}

.success-minutes {
  font-weight: 600;
  color: var(--success);
}

.success-note {
  margin-top: 6px;
  font-size: 13px;
  color: var(--text-secondary);
}

.tabular-nums {
  font-variant-numeric: tabular-nums;
}

[data-theme="dark"] .section-card {
  background: var(--bg-secondary);
  border-color: rgba(255, 255, 255, 0.05);
}
</style>
