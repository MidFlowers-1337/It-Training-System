<template>
  <div ref="el" class="relative overflow-hidden" :style="{minHeight:minHeight||'auto'}">
    <div class="absolute inset-0" :style="{transform:`translateY(${offset}px)`}"><slot name="background" /></div>
    <div class="relative z-10"><slot /></div>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useScroll, useElementBounding } from '@vueuse/core'
const props = defineProps<{speed?:number;minHeight?:string}>()
const el = ref<HTMLElement>()
const { y } = useScroll(window)
const { top } = useElementBounding(el)
const offset = computed(() => top.value * (props.speed||0.3) * -1)
</script>
