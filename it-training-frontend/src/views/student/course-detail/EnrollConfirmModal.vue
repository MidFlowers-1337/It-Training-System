<template>
  <Modal
    v-model="visible"
    title="确认报名"
    confirm-text="确认报名"
    :confirm-loading="loading"
    @confirm="handleConfirm"
    @cancel="handleCancel"
  >
    <div v-if="session" class="enroll-confirm">
      <p class="confirm-text">确定要报名「{{ session.sessionCode }}」班期吗？</p>
      <div class="confirm-details">
        <div class="confirm-row">
          <span class="confirm-label">开班日期</span>
          <span class="confirm-value">{{ session.startDate }}</span>
        </div>
        <div class="confirm-row">
          <span class="confirm-label">上课时间</span>
          <span class="confirm-value">{{ session.schedule || '待定' }}</span>
        </div>
      </div>
    </div>
  </Modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Modal } from '@/design-system';
import { enroll } from '@/api/enrollment';
import { ElMessage } from 'element-plus';

interface Session {
  id: number;
  sessionCode: string;
  startDate: string;
  schedule?: string;
  remainingQuota: number;
}

const props = defineProps<{
  modelValue: boolean;
  session: Session | null;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'success'): void;
}>();

const visible = ref(props.modelValue);
const loading = ref(false);

watch(() => props.modelValue, (val) => {
  visible.value = val;
});

watch(visible, (val) => {
  emit('update:modelValue', val);
});

function handleCancel() {
  visible.value = false;
}

async function handleConfirm() {
  if (!props.session) return;

  loading.value = true;
  try {
    await enroll(props.session.id);
    ElMessage.success('报名成功！可在"我的课程"中查看');
    visible.value = false;
    emit('success');
  } catch (error: any) {
    const errorMsg = error?.response?.data?.message || '报名失败，请稍后重试';
    ElMessage.error(errorMsg);
    console.error('报名失败:', error);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.enroll-confirm {
  padding: 8px 0;
}

.confirm-text {
  font-size: 15px;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.confirm-details {
  background: var(--bg-tertiary);
  border-radius: 8px;
  padding: 12px 16px;
}

.confirm-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}

.confirm-row:not(:last-child) {
  border-bottom: 0.5px solid var(--border-light);
}

.confirm-label {
  font-size: 13px;
  color: var(--text-muted);
}

.confirm-value {
  font-size: 13px;
  color: var(--text-primary);
  font-weight: 500;
}
</style>
