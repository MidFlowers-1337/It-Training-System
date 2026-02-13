<template><canvas ref="c" class="absolute inset-0 pointer-events-none" style="opacity:0.4"></canvas></template>
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
const props = defineProps<{count?:number}>()
const c = ref<HTMLCanvasElement>()
let aid:number, ps:Array<{x:number;y:number;vx:number;vy:number;s:number;o:number}>=[]
onMounted(()=>{
  const cv=c.value!,ctx=cv.getContext('2d')!,n=props.count||40
  function rs(){cv.width=cv.parentElement!.clientWidth;cv.height=cv.parentElement!.clientHeight}
  rs();window.addEventListener('resize',rs)
  for(let i=0;i<n;i++)ps.push({x:Math.random()*cv.width,y:Math.random()*cv.height,vx:(Math.random()-.5)*.5,vy:(Math.random()-.5)*.5,s:Math.random()*2+1,o:Math.random()*.5+.1})
  function draw(){
    ctx.clearRect(0,0,cv.width,cv.height)
    const st=getComputedStyle(document.documentElement),p=st.getPropertyValue('--color-primary').trim()||'0 113 227'
    const[r,g,b]=p.split(' ').map(Number)
    ps.forEach(p=>{p.x+=p.vx;p.y+=p.vy;if(p.x<0||p.x>cv.width)p.vx*=-1;if(p.y<0||p.y>cv.height)p.vy*=-1
      ctx.beginPath();ctx.arc(p.x,p.y,p.s,0,Math.PI*2);ctx.fillStyle=`rgba(${r},${g},${b},${p.o})`;ctx.fill()})
    aid=requestAnimationFrame(draw)
  }
  draw()
  onUnmounted(()=>{cancelAnimationFrame(aid);window.removeEventListener('resize',rs)})
})
</script>
