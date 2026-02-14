<template>
  <div class="space-y-1.5">
    <label v-if="label" class="block text-sm font-medium text-text-primary">
      {{ label }}<span v-if="required" class="text-danger ml-0.5">*</span>
    </label>
    <slot />
    <p v-if="error" class="text-xs text-danger">{{ error }}</p>
    <p v-else-if="hint" class="text-xs text-text-tertiary">{{ hint }}</p>
  </div>
</template>
<script setup lang="ts">
defineProps<{ label?: string; required?: boolean; error?: string; hint?: string }>()
</script>
