<template>
  <div class="w-full">
    <label v-if="label" class="block text-sm font-medium text-text-secondary mb-1.5">{{ label }}</label>
    <div class="relative">
      <span v-if="$slots.prefix" class="absolute left-3 top-1/2 -translate-y-1/2 text-text-tertiary"><slot name="prefix" /></span>
      <input
        :type="type" :value="modelValue" :placeholder="placeholder" :disabled="disabled" :readonly="readonly"
        :class="['w-full bg-surface border border-border rounded-xl px-4 py-2.5 text-sm text-text-primary placeholder:text-text-tertiary transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary disabled:opacity-50 disabled:cursor-not-allowed', $slots.prefix && 'pl-10', $slots.suffix && 'pr-10', error && 'border-danger focus:ring-danger/30']"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        v-bind="$attrs"
      />
      <span v-if="$slots.suffix" class="absolute right-3 top-1/2 -translate-y-1/2 text-text-tertiary"><slot name="suffix" /></span>
    </div>
    <p v-if="error" class="mt-1 text-xs text-danger">{{ error }}</p>
    <p v-else-if="hint" class="mt-1 text-xs text-text-tertiary">{{ hint }}</p>
  </div>
</template>
<script setup lang="ts">
defineProps<{
  modelValue?: string | number
  label?: string
  placeholder?: string
  type?: string
  error?: string
  hint?: string
  disabled?: boolean
  readonly?: boolean
}>()
defineEmits<{ 'update:modelValue': [value: string] }>()
</script>
