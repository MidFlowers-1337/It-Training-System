<template>
  <div class="aurora-container relative overflow-hidden" :class="$attrs.class">
    <div class="aurora-inner absolute inset-0">
      <div class="aurora-orb aurora-1"></div>
      <div class="aurora-orb aurora-2"></div>
      <div class="aurora-orb aurora-3"></div>
      <div class="aurora-orb aurora-4"></div>
    </div>
    <div class="relative z-10">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.aurora-container {
  background-color: rgb(var(--color-bg));
}
.aurora-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  will-change: transform;
}
.aurora-1 {
  width: 600px;
  height: 600px;
  background: var(--aurora-1);
  top: -20%;
  left: -10%;
  animation: aurora 10s ease-in-out infinite alternate;
}
.aurora-2 {
  width: 500px;
  height: 500px;
  background: var(--aurora-2);
  bottom: -15%;
  right: -5%;
  animation: aurora 12s ease-in-out infinite alternate-reverse;
}
.aurora-3 {
  width: 400px;
  height: 400px;
  background: var(--aurora-3);
  top: 30%;
  left: 40%;
  animation: aurora 8s ease-in-out infinite alternate;
  animation-delay: -2s;
}
.aurora-4 {
  width: 350px;
  height: 350px;
  background: var(--aurora-4);
  top: 60%;
  right: 30%;
  animation: aurora 14s ease-in-out infinite alternate-reverse;
  animation-delay: -4s;
}
@keyframes aurora {
  0% { transform: translate(-20%, -20%) rotate(0deg) scale(1); }
  33% { transform: translate(10%, -10%) rotate(120deg) scale(1.1); }
  66% { transform: translate(-10%, 10%) rotate(240deg) scale(0.9); }
  100% { transform: translate(20%, 20%) rotate(360deg) scale(1); }
}
</style>
