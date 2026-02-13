<template><span ref="el">{{displayValue}}{{suffix}}</span></template>
<script setup lang="ts">
import { ref, watch } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'
const props = defineProps<{value:number;duration?:number;suffix?:string;decimals?:number}>()
const el = ref<HTMLElement>()
const displayValue = ref(0)
let done = false
function animate(){
  if(done)return; done=true
  const s=performance.now(),dur=props.duration||2000,end=props.value,dec=props.decimals||0
  function step(now:number){const p=Math.min((now-s)/dur,1);const e=p===1?1:1-Math.pow(2,-10*p);displayValue.value=Number((e*end).toFixed(dec));if(p<1)requestAnimationFrame(step)}
  requestAnimationFrame(step)
}
const{stop}=useIntersectionObserver(el,(entries)=>{if(entries[0]?.isIntersecting){animate();stop()}},{threshold:0.3})
watch(()=>props.value,()=>{done=false;animate()})
</script>
