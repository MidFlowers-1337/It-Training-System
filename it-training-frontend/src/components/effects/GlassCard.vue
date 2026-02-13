<template>
  <div :class="['rounded-2xl transition-all duration-300 dark-glow',hover?'hover:shadow-theme-lg hover:-translate-y-1':'']" class="gc"><div :class="padding||'p-6'"><slot /></div></div>
</template>
<script setup lang="ts">
defineProps<{hover?:boolean;padding?:string}>()
</script>
<style scoped>
.gc{background:var(--glass-bg);backdrop-filter:blur(var(--glass-blur));-webkit-backdrop-filter:blur(var(--glass-blur));border:1px solid var(--glass-border);box-shadow:var(--glass-shadow)}
</style>
