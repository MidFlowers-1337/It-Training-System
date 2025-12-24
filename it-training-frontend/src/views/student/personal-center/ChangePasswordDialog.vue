<template>
  <Modal v-model="visible" title="修改密码" width="400px">
    <FormLayout>
      <FormItem label="当前密码" required :error="errors.currentPassword">
        <Input v-model="form.currentPassword" type="password" placeholder="请输入当前密码" />
      </FormItem>
      <FormItem label="新密码" required :error="errors.newPassword">
        <Input v-model="form.newPassword" type="password" placeholder="请输入新密码" />
      </FormItem>
      <FormItem label="确认密码" required :error="errors.confirmPassword">
        <Input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" />
      </FormItem>
    </FormLayout>
    <template #footer>
      <Button variant="secondary" @click="handleCancel">取消</Button>
      <Button variant="primary" :loading="loading" @click="handleSubmit">确认修改</Button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import { Button, Input, Modal, FormLayout, FormItem } from '@/design-system';
import { changePassword } from '@/api/user';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'success'): void;
}>();

const visible = ref(props.modelValue);
const loading = ref(false);

const form = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const errors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

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
  form.currentPassword = '';
  form.newPassword = '';
  form.confirmPassword = '';
  errors.currentPassword = '';
  errors.newPassword = '';
  errors.confirmPassword = '';
}

function validate(): boolean {
  errors.currentPassword = '';
  errors.newPassword = '';
  errors.confirmPassword = '';
  let valid = true;

  if (!form.currentPassword) {
    errors.currentPassword = '请输入当前密码';
    valid = false;
  }

  if (!form.newPassword) {
    errors.newPassword = '请输入新密码';
    valid = false;
  } else if (form.newPassword.length < 6) {
    errors.newPassword = '密码长度不能少于6位';
    valid = false;
  }

  if (!form.confirmPassword) {
    errors.confirmPassword = '请确认新密码';
    valid = false;
  } else if (form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致';
    valid = false;
  }

  return valid;
}

function handleCancel() {
  visible.value = false;
}

async function handleSubmit() {
  if (!validate()) return;

  loading.value = true;
  try {
    await changePassword({
      currentPassword: form.currentPassword,
      newPassword: form.newPassword,
      confirmPassword: form.confirmPassword,
    });
    ElMessage.success('密码修改成功');
    visible.value = false;
    emit('success');
  } catch (error: any) {
    ElMessage.error(error?.message || '密码修改失败');
  } finally {
    loading.value = false;
  }
}
</script>
