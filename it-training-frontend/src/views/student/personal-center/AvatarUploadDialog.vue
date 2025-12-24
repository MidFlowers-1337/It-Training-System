<template>
  <Modal v-model="visible" title="更换头像" width="400px">
    <FormLayout>
      <FormItem label="头像URL">
        <Input v-model="avatarUrl" placeholder="请输入头像图片URL" />
      </FormItem>
      <FormItem label="预览">
        <Avatar :src="avatarUrl" :size="80" />
      </FormItem>
    </FormLayout>
    <template #footer>
      <Button variant="secondary" @click="handleCancel">取消</Button>
      <Button variant="primary" :loading="loading" @click="handleSubmit">确认更换</Button>
    </template>
  </Modal>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Button, Input, Modal, FormLayout, FormItem, Avatar } from '@/design-system';
import { uploadAvatar } from '@/api/user';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';

const props = defineProps<{
  modelValue: boolean;
  currentAvatar?: string;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'success', avatar: string): void;
}>();

const userStore = useUserStore();

const visible = ref(props.modelValue);
const loading = ref(false);
const avatarUrl = ref(props.currentAvatar || '');

// Sync with v-model
watch(() => props.modelValue, (val) => {
  visible.value = val;
  if (val) {
    avatarUrl.value = props.currentAvatar || '';
  }
});

watch(visible, (val) => {
  emit('update:modelValue', val);
});

function handleCancel() {
  visible.value = false;
}

async function handleSubmit() {
  if (!avatarUrl.value) {
    ElMessage.error('请输入头像URL');
    return;
  }

  loading.value = true;
  try {
    await uploadAvatar(avatarUrl.value);
    ElMessage.success('头像更新成功');
    userStore.setUserInfo({
      ...userStore.userInfo,
      avatar: avatarUrl.value,
    });
    visible.value = false;
    emit('success', avatarUrl.value);
  } catch (err: any) {
    ElMessage.error(err?.message || '更新失败');
  } finally {
    loading.value = false;
  }
}
</script>
