<template>
  <div :class="['inline-flex items-center justify-center rounded-full bg-primary/10 text-primary font-semibold overflow-hidden', sizeClass]">
    <img v-if="src" :src="src" :alt="alt" class="w-full h-full object-cover" />
    <span v-else>{{ initials }}</span>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  src?: string; name?: string; alt?: string; size?: 'sm' | 'md' | 'lg' | 'xl'
}>(), { size: 'md' })
const sizeClass = computed(() => ({ sm: 'w-8 h-8 text-xs', md: 'w-10 h-10 text-sm', lg: 'w-12 h-12 text-base', xl: 'w-16 h-16 text-lg' }[props.size]))
const initials = computed(() => props.name ? props.name.split(' ').map(w => w[0]).join('').slice(0, 2).toUpperCase() : '?')
</script>
