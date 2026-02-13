<template>
  <form class="space-y-5" @submit.prevent="$emit('submit')">
    <slot />
  </form>
</template>
<script setup lang="ts">
defineEmits<{ submit: [] }>()
</script>
