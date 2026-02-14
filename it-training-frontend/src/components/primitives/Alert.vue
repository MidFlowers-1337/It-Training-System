<template>
  <div :class="['flex gap-3 rounded-xl p-4 border', variantClass]" role="alert">
    <component :is="iconComponent" class="w-[18px] h-[18px] flex-shrink-0 mt-0.5" :stroke-width="1.75" />
    <div class="flex-1 min-w-0">
      <p v-if="title" class="font-semibold text-sm mb-0.5">{{ title }}</p>
      <p class="text-sm opacity-90"><slot /></p>
    </div>
    <button v-if="closable" class="flex-shrink-0 opacity-60 hover:opacity-100 transition-opacity" aria-label="关闭" @click="$emit('close')">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>
    </button>
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { Info, CheckCircle, AlertTriangle, XCircle } from 'lucide-vue-next'
const props = withDefaults(defineProps<{
  type?: 'info' | 'success' | 'warning' | 'danger'; title?: string; closable?: boolean
}>(), { type: 'info' })
defineEmits<{ close: [] }>()
const iconComponent = computed(() => ({
  info: Info, success: CheckCircle, warning: AlertTriangle, danger: XCircle,
}[props.type]))
const variantClass = computed(() => ({
  info: 'bg-info/10 border-info/20 text-info',
  success: 'bg-success/10 border-success/20 text-success',
  warning: 'bg-warning/10 border-warning/20 text-warning',
  danger: 'bg-danger/10 border-danger/20 text-danger',
}[props.type]))
</script>
