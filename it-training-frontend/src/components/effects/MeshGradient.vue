<template>
  <div class="mesh-gradient-wrap" :class="{ animated }">
    <div class="mesh-orb mesh-orb-1" />
    <div class="mesh-orb mesh-orb-2" />
    <div class="mesh-orb mesh-orb-3" />
    <div class="mesh-orb mesh-orb-4" />
  </div>
</template>
<script setup lang="ts">
defineProps<{
  /** Enable slow animation (default true) */
  animated?: boolean
}>()
</script>
<style scoped>
.mesh-gradient-wrap {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.mesh-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.5;
}
.mesh-orb-1 {
  width: 50%;
  height: 60%;
  top: -10%;
  left: -5%;
  background: var(--aurora-1);
}
.mesh-orb-2 {
  width: 40%;
  height: 50%;
  top: 10%;
  right: -10%;
  background: var(--aurora-2);
}
.mesh-orb-3 {
  width: 45%;
  height: 55%;
  bottom: -15%;
  left: 30%;
  background: var(--aurora-3);
}
.mesh-orb-4 {
  width: 35%;
  height: 40%;
  bottom: 10%;
  right: 20%;
  background: var(--aurora-4);
}
/* Animated variant — slower, more elegant (15-20s) */
.animated .mesh-orb-1 { animation: mesh-float-1 15s ease-in-out infinite alternate; }
.animated .mesh-orb-2 { animation: mesh-float-2 18s ease-in-out infinite alternate; }
.animated .mesh-orb-3 { animation: mesh-float-3 20s ease-in-out infinite alternate; }
.animated .mesh-orb-4 { animation: mesh-float-4 16s ease-in-out infinite alternate; }

@keyframes mesh-float-1 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(5%, 8%) scale(1.1); }
}
@keyframes mesh-float-2 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(-8%, 5%) scale(1.05); }
}
@keyframes mesh-float-3 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(6%, -6%) scale(0.95); }
}
@keyframes mesh-float-4 {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(-4%, -8%) scale(1.08); }
}
/* Respect reduced motion */
@media (prefers-reduced-motion: reduce) {
  .animated .mesh-orb { animation: none !important; }
}
</style>
