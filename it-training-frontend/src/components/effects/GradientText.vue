<template>
  <component :is="tag||'span'" :class="[hero?'gradient-hero':'gradient-text',sizeClass]" class="inline-block font-bold"><slot /></component>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = defineProps<{tag?:string;size?:string;hero?:boolean}>()
const sizeClass = computed(() => ({sm:'text-lg',md:'text-xl',lg:'text-2xl',xl:'text-3xl','2xl':'text-4xl','3xl':'text-5xl','4xl':'text-6xl','5xl':'text-7xl'}[props.size||'']||''))
</script>
