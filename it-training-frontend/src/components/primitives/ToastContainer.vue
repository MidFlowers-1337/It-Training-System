<template>
  <Teleport to="body">
    <div
      v-if="queue.length"
      class="fixed top-4 right-4 z-[9999] flex flex-col gap-3 pointer-events-none"
      aria-label="通知区域"
    >
      <Toast
        v-for="item in queue"
        :key="item.id"
        :type="item.type"
        :message="item.message"
        :visible="item.visible"
        @close="removeToast(item.id)"
      />
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { useToast } from '@/composables/useToast'
import Toast from './Toast.vue'

const { queue, removeToast } = useToast()
</script>
