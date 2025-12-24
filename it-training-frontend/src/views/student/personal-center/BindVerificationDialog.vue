<template>
  <Modal v-model="visible" :title="title" width="400px">
    <FormLayout>
      <FormItem :label="inputLabel" :error="inputError">
        <Input v-model="inputValue" :placeholder="inputPlaceholder" />
      </FormItem>
      <FormItem label="验证码" :error="codeError">
        <div class="code-input-group">
          <Input v-model="codeValue" placeholder="请输入验证码" />
          <Button variant="secondary" :disabled="countdown > 0 || sendingCode" @click="handleSendCode">
            {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
          </Button>
        </div>
      </FormItem>
    </FormLayout>
    <template #footer>
      <Button variant="secondary" @click="handleCancel">取消</Button>
      <Button variant="primary" :loading="loading" @click="handleSubmit">确认绑定</Button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { Button, Input, Modal, FormLayout, FormItem } from '@/design-system';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  modelValue: boolean;
  type: 'email' | 'phone';
  sendCodeFn: (value: string) => Promise<any>;
  bindFn: (value: string, code: string) => Promise<any>;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'success'): void;
}>();

const visible = ref(props.modelValue);
const loading = ref(false);
const sendingCode = ref(false);
const countdown = ref(0);
const inputValue = ref('');
const codeValue = ref('');
const inputError = ref('');
const codeError = ref('');

// Computed based on type
const title = computed(() => props.type === 'email' ? '绑定邮箱' : '绑定手机');
const inputLabel = computed(() => props.type === 'email' ? '邮箱' : '手机号');
const inputPlaceholder = computed(() => props.type === 'email' ? '请输入邮箱' : '请输入手机号');

// Sync with v-model
watch(() => props.modelValue, (val) => {
  visible.value = val;
});

watch(visible, (val) => {
  emit('update:modelValue', val);
  if (!val) {
    resetForm();
  }
});

function resetForm() {
  inputValue.value = '';
  codeValue.value = '';
  inputError.value = '';
  codeError.value = '';
}

function validateInput(): boolean {
  inputError.value = '';

  if (!inputValue.value) {
    inputError.value = props.type === 'email' ? '请输入邮箱' : '请输入手机号';
    return false;
  }

  if (props.type === 'email') {
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(inputValue.value)) {
      inputError.value = '请输入正确的邮箱格式';
      return false;
    }
  } else {
    if (!/^1[3-9]\d{9}$/.test(inputValue.value)) {
      inputError.value = '请输入正确的手机号';
      return false;
    }
  }

  return true;
}

function validate(): boolean {
  codeError.value = '';

  if (!validateInput()) return false;

  if (!codeValue.value) {
    codeError.value = '请输入验证码';
    return false;
  }

  return true;
}

function startCountdown() {
  countdown.value = 60;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
}

async function handleSendCode() {
  if (!validateInput()) return;

  sendingCode.value = true;
  try {
    await props.sendCodeFn(inputValue.value);
    ElMessage.success('验证码已发送');
    startCountdown();
  } catch (error: any) {
    ElMessage.error(error?.message || '发送验证码失败');
  } finally {
    sendingCode.value = false;
  }
}

function handleCancel() {
  visible.value = false;
}

async function handleSubmit() {
  if (!validate()) return;

  loading.value = true;
  try {
    await props.bindFn(inputValue.value, codeValue.value);
    ElMessage.success('绑定成功');
    visible.value = false;
    emit('success');
  } catch (error: any) {
    ElMessage.error(error?.message || '绑定失败');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.code-input-group {
  display: flex;
  gap: 8px;
}

.code-input-group :deep(.input) {
  flex: 1;
}
</style>
