<template>
  <label class="inline-flex items-center gap-2 cursor-pointer select-none">
    <button type="button" role="switch" :aria-checked="modelValue" :disabled="disabled"
      :class="['relative inline-flex h-6 w-11 shrink-0 rounded-full border-2 border-transparent transition-colors duration-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary/30', modelValue ? 'bg-primary' : 'bg-border', disabled && 'opacity-50 cursor-not-allowed']"
      @click="!disabled && $emit('update:modelValue', !modelValue)"
    >
      <span :class="['pointer-events-none inline-block h-5 w-5 rounded-full bg-white shadow-sm transition-transform duration-200', modelValue ? 'translate-x-5' : 'translate-x-0']" />
    </button>
    <span v-if="label" class="text-sm text-text-primary">{{ label }}</span>
  </label>
</template>
<script setup lang="ts">
defineProps<{ modelValue?: boolean; label?: string; disabled?: boolean }>()
defineEmits<{ 'update:modelValue': [value: boolean] }>()
</script>
