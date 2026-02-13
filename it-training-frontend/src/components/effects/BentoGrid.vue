<template>
  <div :class="['bento-grid', sizeClass]">
    <slot />
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{
  /** 'compact' = gap-3, 'default' = gap-4, 'loose' = gap-6 */
  gap?: 'compact' | 'default' | 'loose'
  /** Grid columns: 2 | 3 | 4 (default 4) */
  cols?: 2 | 3 | 4
}>()
const sizeClass = computed(() => {
  const g = { compact: 'gap-3', default: 'gap-4', loose: 'gap-6' }[props.gap || 'default']
  const c = {
    2: 'grid-cols-1 sm:grid-cols-2',
    3: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3',
    4: 'grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4',
  }[props.cols || 4]
  return `${g} ${c}`
})
</script>
<style scoped>
.bento-grid {
  display: grid;
  grid-auto-rows: minmax(0, auto);
}
</style>
