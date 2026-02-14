<template>
  <hr :class="['border-0 border-t border-border', vertical && '!border-t-0 !border-l h-full inline-block', spacing]" />
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{ vertical?: boolean; space?: 'sm' | 'md' | 'lg' }>(), { space: 'md' })
const spacing = computed(() => props.vertical ? '' : { sm: 'my-2', md: 'my-4', lg: 'my-6' }[props.space])
</script>
