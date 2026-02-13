<template>
  <div ref="el" :class="[revealed?'opacity-100 translate-y-0':'opacity-0 translate-y-6']" :style="{transitionDelay:`${delay||0}ms`,transitionDuration:`${duration||600}ms`}" class="transition-all ease-out"><slot /></div>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'
const props = defineProps<{delay?:number;duration?:number;threshold?:number}>()
const el = ref<HTMLElement>()
const revealed = ref(false)
const{stop}=useIntersectionObserver(el,(entries)=>{if(entries[0]?.isIntersecting){revealed.value=true;stop()}},{threshold:props.threshold||0.15})
</script>
