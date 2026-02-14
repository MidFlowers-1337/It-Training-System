<template>
  <div class="relative inline-block" @mouseenter="show = true" @mouseleave="show = false">
    <slot />
    <Transition name="fade">
      <div v-if="show" :class="['absolute z-40 px-3 py-1.5 text-xs font-medium text-white bg-gray-900 rounded-lg shadow-lg whitespace-nowrap pointer-events-none', positionClass]">
        {{ content }}
        <div :class="['absolute w-2 h-2 bg-gray-900 rotate-45', arrowClass]" />
      </div>
    </Transition>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
const props = withDefaults(defineProps<{ content: string; position?: 'top' | 'bottom' | 'left' | 'right' }>(), { position: 'top' })
const show = ref(false)
const positionClass = computed(() => ({
  top: 'bottom-full left-1/2 -translate-x-1/2 mb-2', bottom: 'top-full left-1/2 -translate-x-1/2 mt-2',
  left: 'right-full top-1/2 -translate-y-1/2 mr-2', right: 'left-full top-1/2 -translate-y-1/2 ml-2',
}[props.position]))
const arrowClass = computed(() => ({
  top: '-bottom-1 left-1/2 -translate-x-1/2', bottom: '-top-1 left-1/2 -translate-x-1/2',
  left: '-right-1 top-1/2 -translate-y-1/2', right: '-left-1 top-1/2 -translate-y-1/2',
}[props.position]))
</script>
