<template>
  <div class="rounded-2xl bg-surface border border-border overflow-hidden">
    <!-- Header -->
    <div v-if="$slots.header || title" class="px-5 py-4 border-b border-border flex items-center justify-between">
      <h3 v-if="title" class="font-semibold text-text-primary">{{ title }}</h3>
      <slot name="header" />
    </div>
    <!-- Table -->
    <div class="overflow-x-auto">
      <table class="w-full text-sm">
        <thead>
          <tr class="bg-surface-alt/50 border-b border-border">
            <th v-for="col in columns" :key="col.key" class="px-4 py-3 text-left font-medium text-text-secondary whitespace-nowrap" :style="col.width ? { width: col.width } : {}">
              {{ col.label }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(row, i) in data" :key="i" class="border-b border-border last:border-0 hover:bg-surface-alt/30 transition-colors">
            <td v-for="col in columns" :key="col.key" class="px-4 py-3 text-text-primary whitespace-nowrap">
              <slot :name="'cell-' + col.key" :row="row" :value="row[col.key]">
                {{ row[col.key] ?? '-' }}
              </slot>
            </td>
          </tr>
          <tr v-if="!data?.length">
            <td :colspan="columns.length" class="px-4 py-12 text-center text-text-tertiary">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- Footer -->
    <div v-if="$slots.footer" class="px-5 py-3 border-t border-border">
      <slot name="footer" />
    </div>
  </div>
</template>
<script setup lang="ts">
export interface TableColumn { key: string; label: string; width?: string }
defineProps<{ columns: TableColumn[]; data: Record<string, any>[]; title?: string }>()
</script>
