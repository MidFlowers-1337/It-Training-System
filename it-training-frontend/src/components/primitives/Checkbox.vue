<template>
  <label class="inline-flex items-center gap-2 cursor-pointer select-none group">
    <input type="checkbox" :checked="modelValue" :disabled="disabled" class="sr-only peer" @change="$emit('update:modelValue', !modelValue)" />
    <span class="w-5 h-5 rounded-md border-2 border-border bg-surface flex items-center justify-center transition-all peer-checked:bg-primary peer-checked:border-primary peer-focus-visible:ring-2 peer-focus-visible:ring-primary/30 group-hover:border-primary/50">
      <svg v-if="modelValue" class="w-3 h-3 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7" /></svg>
    </span>
    <span v-if="label" class="text-sm text-text-primary" :class="disabled && 'opacity-50'">{{ label }}</span>
  </label>
</template>
<script setup lang="ts">
defineProps<{ modelValue?: boolean; label?: string; disabled?: boolean }>()
defineEmits<{ 'update:modelValue': [value: boolean] }>()
</script>
