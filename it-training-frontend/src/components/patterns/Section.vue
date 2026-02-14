<template>
  <section class="mb-8">
    <div v-if="title" class="flex items-center justify-between mb-4">
      <h2 class="text-lg font-semibold text-text-primary">{{ title }}</h2>
      <slot name="header-action" />
    </div>
    <slot />
  </section>
</template>
<script setup lang="ts">
defineProps<{ title?: string }>()
</script>
