<template>
  <div class="relative">
    <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
      <svg class="w-4 h-4 text-text-tertiary" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
    </div>
    <input type="text" :value="modelValue" :placeholder="placeholder" class="w-full pl-10 pr-4 py-2.5 rounded-xl border border-border bg-surface text-sm text-text-primary placeholder-text-tertiary focus:outline-none focus:ring-2 focus:ring-primary/30 focus:border-primary transition-all" @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)" />
    <button v-if="modelValue" class="absolute inset-y-0 right-0 pr-3 flex items-center text-text-tertiary hover:text-text-primary" @click="$emit('update:modelValue', '')">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
    </button>
  </div>
</template>
<script setup lang="ts">
withDefaults(defineProps<{ modelValue: string; placeholder?: string }>(), { placeholder: '搜索...' })
defineEmits<{ 'update:modelValue': [value: string] }>()
</script>
