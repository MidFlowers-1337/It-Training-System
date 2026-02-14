<template>
  <div :class="['flex items-center gap-3 px-4 py-3 transition-colors', clickable && 'cursor-pointer hover:bg-surface-alt']">
    <div v-if="$slots.avatar" class="flex-shrink-0"><slot name="avatar" /></div>
    <div class="flex-1 min-w-0">
      <p class="text-sm font-medium text-text-primary truncate">{{ title }}</p>
      <p v-if="subtitle" class="text-xs text-text-secondary mt-0.5 truncate">{{ subtitle }}</p>
    </div>
    <div v-if="$slots.trailing" class="flex-shrink-0"><slot name="trailing" /></div>
  </div>
</template>
<script setup lang="ts">
withDefaults(defineProps<{ title: string; subtitle?: string; clickable?: boolean }>(), { clickable: false })
</script>
