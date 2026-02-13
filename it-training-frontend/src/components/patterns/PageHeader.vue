<template>
  <div class="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
    <div>
      <h1 class="text-2xl font-bold text-text-primary">{{ title }}</h1>
      <p v-if="subtitle" class="mt-1 text-sm text-text-secondary">{{ subtitle }}</p>
    </div>
    <div class="flex items-center gap-3">
      <slot name="actions" />
    </div>
  </div>
</template>
<script setup lang="ts">
defineProps<{ title: string; subtitle?: string }>()
</script>
