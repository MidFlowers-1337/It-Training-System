<template>
  <dl class="grid gap-4" :class="cols === 2 ? 'sm:grid-cols-2' : cols === 3 ? 'sm:grid-cols-3' : ''">
    <slot />
  </dl>
</template>
<script setup lang="ts">
withDefaults(defineProps<{ cols?: 1 | 2 | 3 }>(), { cols: 2 })
</script>
