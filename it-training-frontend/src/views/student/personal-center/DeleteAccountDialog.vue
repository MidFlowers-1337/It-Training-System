<template>
  <Modal v-model="visible" title="注销账号" width="400px">
    <Alert type="error" title="警告">
      注销账号后，您的所有数据将被清除且无法恢复，请谨慎操作！
    </Alert>
    <FormLayout class="delete-form">
      <FormItem label="密码确认" :error="error">
        <Input v-model="password" type="password" placeholder="请输入密码确认注销" />
      </FormItem>
    </FormLayout>
    <template #footer>
      <Button variant="secondary" @click="handleCancel">取消</Button>
      <Button variant="danger" :loading="loading" @click="handleSubmit">确认注销</Button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Button, Input, Modal, FormLayout, FormItem, Alert } from '@/design-system';
import { deleteAccount } from '@/api/user';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
}>();

const router = useRouter();
const userStore = useUserStore();

const visible = ref(props.modelValue);
const loading = ref(false);
const password = ref('');
const error = ref('');

// Sync with v-model
watch(() => props.modelValue, (val) => {
  visible.value = val;
});

watch(visible, (val) => {
  emit('update:modelValue', val);
  if (!val) {
    password.value = '';
    error.value = '';
  }
});

function handleCancel() {
  visible.value = false;
}

async function handleSubmit() {
  error.value = '';

  if (!password.value) {
    error.value = '请输入密码';
    return;
  }

  loading.value = true;
  try {
    await deleteAccount(password.value);
    ElMessage.success('账号已注销');
    userStore.logout();
    router.push('/login');
  } catch (err: any) {
    ElMessage.error(err?.message || '注销失败');
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.delete-form {
  margin-top: 16px;
}
</style>
