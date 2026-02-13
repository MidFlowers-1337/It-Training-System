<template>
  <div class="w-full">
    <label v-if="label" class="block text-sm font-medium text-text-secondary mb-1.5">{{ label }}</label>
    <select
      :value="modelValue" :disabled="disabled"
      class="w-full bg-surface border border-border rounded-xl px-4 py-2.5 text-sm text-text-primary transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary disabled:opacity-50 appearance-none cursor-pointer"
      @change="$emit('update:modelValue', ($event.target as HTMLSelectElement).value)"
    >
      <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
      <option v-for="opt in options" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
    </select>
    <p v-if="error" class="mt-1 text-xs text-danger">{{ error }}</p>
  </div>
</template>
<script setup lang="ts">
defineProps<{
  modelValue?: string | number
  label?: string
  placeholder?: string
  options: { value: string | number; label: string }[]
  error?: string
  disabled?: boolean
}>()
defineEmits<{ 'update:modelValue': [value: string] }>()
</script>
