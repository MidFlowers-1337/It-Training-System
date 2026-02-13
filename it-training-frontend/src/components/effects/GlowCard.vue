<template>
  <div
    ref="card"
    class="glow-card relative rounded-xl overflow-hidden transition-all duration-200"
    @mousemove="handleMove"
    @mouseleave="handleLeave"
  >
    <!-- Glow overlay -->
    <div
      class="glow-overlay pointer-events-none absolute inset-0 opacity-0 transition-opacity duration-300 z-0"
      :style="glowStyle"
    />
    <!-- Border glow -->
    <div
      class="glow-border pointer-events-none absolute inset-0 rounded-xl opacity-0 transition-opacity duration-300 z-0"
      :style="borderStyle"
    />
    <!-- Content -->
    <div class="relative z-10">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = withDefaults(defineProps<{
  color?: string
  intensity?: number
  borderOnly?: boolean
}>(), {
  color: '129, 140, 248',   // indigo-400 RGB
  intensity: 0.08,
  borderOnly: false,
})

const card = ref<HTMLElement>()
const mouseX = ref(0)
const mouseY = ref(0)
const isHovered = ref(false)

function handleMove(e: MouseEvent) {
  if (!card.value) return
  const rect = card.value.getBoundingClientRect()
  mouseX.value = e.clientX - rect.left
  mouseY.value = e.clientY - rect.top
  isHovered.value = true
}

function handleLeave() {
  isHovered.value = false
}

const glowStyle = computed(() => ({
  opacity: isHovered.value && !props.borderOnly ? 1 : 0,
  background: `radial-gradient(600px circle at ${mouseX.value}px ${mouseY.value}px, rgba(${props.color}, ${props.intensity}), transparent 40%)`,
}))

const borderStyle = computed(() => ({
  opacity: isHovered.value ? 1 : 0,
  background: `radial-gradient(400px circle at ${mouseX.value}px ${mouseY.value}px, rgba(${props.color}, 0.25), transparent 40%)`,
  mask: 'linear-gradient(#000 0 0) content-box, linear-gradient(#000 0 0)',
  maskComposite: 'exclude',
  WebkitMaskComposite: 'xor',
  padding: '1px',
  borderRadius: 'inherit',
}))
</script>

<style scoped>
.glow-card {
  background: #111113;
  border: 1px solid rgba(255, 255, 255, 0.06);
}
.glow-card:hover {
  border-color: rgba(129, 140, 248, 0.15);
}
</style>
