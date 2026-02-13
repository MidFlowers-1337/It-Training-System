<template>
  <div :class="['flex items-center gap-3 px-4 py-3 transition-colors', clickable && 'cursor-pointer hover:bg-surface-alt', !last && 'border-b border-border']">
    <div v-if="$slots.prefix" class="flex-shrink-0"><slot name="prefix" /></div>
    <div class="flex-1 min-w-0">
      <p class="text-sm font-medium text-text-primary truncate">{{ label }}</p>
      <p v-if="description" class="text-xs text-text-secondary mt-0.5 truncate">{{ description }}</p>
    </div>
    <div class="flex-shrink-0 text-text-tertiary">
      <slot name="suffix">
        <svg v-if="clickable" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-width="2" d="M9 5l7 7-7 7" /></svg>
      </slot>
    </div>
  </div>
</template>
<script setup lang="ts">
withDefaults(defineProps<{ label: string; description?: string; clickable?: boolean; last?: boolean }>(), {
  clickable: false, last: false,
})
</script>
