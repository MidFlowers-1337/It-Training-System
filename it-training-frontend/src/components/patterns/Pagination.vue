<template>
  <div class="flex items-center justify-between gap-4 text-sm">
    <span class="text-text-secondary">共 {{ total }} 条</span>
    <div class="flex items-center gap-1">
      <button :disabled="currentPage <= 1" class="px-3 py-1.5 rounded-lg border border-border bg-surface text-text-primary disabled:opacity-40 hover:bg-surface-alt transition-colors" @click="changePage(currentPage - 1)">上一页</button>
      <template v-for="p in visiblePages" :key="p">
        <span v-if="p === '...'" class="px-2 text-text-tertiary">...</span>
        <button v-else :class="['px-3 py-1.5 rounded-lg border transition-colors', p === currentPage ? 'bg-primary text-white border-primary' : 'border-border bg-surface text-text-primary hover:bg-surface-alt']" @click="changePage(p as number)">{{ p }}</button>
      </template>
      <button :disabled="currentPage >= totalPages" class="px-3 py-1.5 rounded-lg border border-border bg-surface text-text-primary disabled:opacity-40 hover:bg-surface-alt transition-colors" @click="changePage(currentPage + 1)">下一页</button>
    </div>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{ currentPage: number; pageSize: number; total: number }>()
const emit = defineEmits<{ 'update:currentPage': [page: number] }>()

const totalPages = computed(() => Math.max(1, Math.ceil(props.total / props.pageSize)))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const t = totalPages.value, c = props.currentPage
  if (t <= 7) { for (let i = 1; i <= t; i++) pages.push(i); return pages }
  pages.push(1)
  if (c > 3) pages.push('...')
  for (let i = Math.max(2, c - 1); i <= Math.min(t - 1, c + 1); i++) pages.push(i)
  if (c < t - 2) pages.push('...')
  pages.push(t)
  return pages
})

function changePage(p: number) { if (p >= 1 && p <= totalPages.value) emit('update:currentPage', p) }
</script>
