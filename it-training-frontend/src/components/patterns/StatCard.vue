<template>
  <div class="rounded-2xl bg-surface border border-border p-5 transition-all hover:shadow-md">
    <div class="flex items-center justify-between mb-3">
      <span class="text-2xl">{{ icon }}</span>
      <Tag v-if="trend" :variant="trend > 0 ? 'success' : trend < 0 ? 'danger' : 'neutral'" size="sm">
        {{ trend > 0 ? '↑' : trend < 0 ? '↓' : '→' }} {{ Math.abs(trend) }}%
      </Tag>
    </div>
    <p class="text-2xl font-bold text-text-primary">{{ value }}</p>
    <p class="text-sm text-text-secondary mt-1">{{ label }}</p>
  </div>
</template>
<script setup lang="ts">
import Tag from '../primitives/Tag.vue'
defineProps<{ icon: string; value: string | number; label: string; trend?: number }>()
</script>
