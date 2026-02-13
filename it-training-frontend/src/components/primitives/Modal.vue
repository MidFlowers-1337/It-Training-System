<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center p-4" @click.self="closable && $emit('update:modelValue', false)">
        <div class="fixed inset-0 bg-black/40 backdrop-blur-sm" />
        <div :class="['relative bg-surface rounded-2xl shadow-xl border border-border p-6 w-full transition-all', widthClass]">
          <div v-if="title || closable" class="flex items-center justify-between mb-4">
            <h3 v-if="title" class="text-lg font-semibold text-text-primary">{{ title }}</h3>
            <button v-if="closable" class="p-1 rounded-lg hover:bg-surface-alt transition-colors text-text-tertiary" @click="$emit('update:modelValue', false)">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
            </button>
          </div>
          <slot />
          <div v-if="$slots.footer" class="mt-6 flex justify-end gap-3"><slot name="footer" /></div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>
<script setup lang="ts">
import { computed } from 'vue'
const props = withDefaults(defineProps<{
  modelValue: boolean; title?: string; closable?: boolean; size?: 'sm' | 'md' | 'lg'
}>(), { closable: true, size: 'md' })
defineEmits<{ 'update:modelValue': [value: boolean] }>()
const widthClass = computed(() => ({ sm: 'max-w-sm', md: 'max-w-lg', lg: 'max-w-2xl' }[props.size]))
</script>
