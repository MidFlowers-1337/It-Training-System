<template>
  <div class="flex flex-col items-center justify-center py-12 px-6 text-center">
    <slot name="icon">
      <Inbox v-if="!icon" class="w-12 h-12 text-text-tertiary mb-4" :stroke-width="1.25" />
      <span v-else class="text-5xl mb-4">{{ icon }}</span>
    </slot>
    <h3 class="text-lg font-semibold text-text-primary mb-2">{{ title }}</h3>
    <p v-if="description" class="text-sm text-text-secondary max-w-sm mb-6">{{ description }}</p>
    <slot name="action" />
  </div>
</template>
<script setup lang="ts">
import { Inbox } from 'lucide-vue-next'
withDefaults(defineProps<{ icon?: string; title?: string; description?: string }>(), {
  icon: '', title: '暂无数据', description: '',
})
</script>
