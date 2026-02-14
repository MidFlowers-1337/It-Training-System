<template>
  <nav
    v-if="items?.length > 1"
    aria-label="面包屑"
    class="breadcrumb"
  >
    <template v-for="(item, i) in items" :key="i">
      <ChevronRight v-if="i > 0" class="breadcrumb-separator" />
      <router-link
        v-if="item.path && i < items.length - 1"
        :to="item.path"
        class="breadcrumb-link"
      >
        {{ item.label }}
      </router-link>
      <span v-else class="breadcrumb-current">
        {{ item.label }}
      </span>
    </template>
  </nav>
</template>

<script setup lang="ts">
import { ChevronRight } from 'lucide-vue-next'

export interface BreadcrumbItem {
  label: string
  path?: string
}

defineProps<{
  items: BreadcrumbItem[]
}>()
</script>

<style scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  margin-bottom: 16px;
}

.breadcrumb-separator {
  width: 14px;
  height: 14px;
  color: rgb(var(--color-text-tertiary));
  flex-shrink: 0;
}

.breadcrumb-link {
  color: rgb(var(--color-text-secondary));
  text-decoration: none;
  transition: color 0.15s;
}

.breadcrumb-link:hover {
  color: rgb(var(--color-text));
}

.breadcrumb-current {
  color: rgb(var(--color-text));
  font-weight: 500;
}
</style>
