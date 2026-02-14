<template>
  <slot v-if="!error" />
  <div v-else class="flex flex-col items-center justify-center py-16 px-6 text-center">
    <AlertTriangle class="w-12 h-12 text-danger mb-4" :stroke-width="1.5" />
    <h3 class="text-lg font-semibold text-text-primary mb-2">页面出错了</h3>
    <p class="text-sm text-text-secondary max-w-sm mb-6">{{ errorMessage }}</p>
    <button
      class="px-5 py-2.5 rounded-lg bg-primary text-white text-sm font-medium hover:brightness-110 transition-all inline-flex items-center gap-2"
      @click="retry"
    >
      <RefreshCw class="w-4 h-4" :stroke-width="2" />
      重试
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, onErrorCaptured } from 'vue'
import { AlertTriangle, RefreshCw } from 'lucide-vue-next'

const error = ref<Error | null>(null)
const errorMessage = ref('')

onErrorCaptured((err: Error) => {
  error.value = err
  errorMessage.value = err.message || '发生了未知错误，请重试。'
  console.error('[ErrorBoundary]', err)
  return false // prevent propagation
})

function retry() {
  error.value = null
  errorMessage.value = ''
}
</script>
