<template>
  <div class="fixed bottom-8 right-8 z-[999]">
    <button
      type="button"
      class="group relative w-14 h-14 rounded-full bg-gradient-to-br from-primary to-secondary shadow-lg shadow-primary/20 ring-1 ring-white/10 transition-transform duration-200 hover:scale-105 active:scale-95"
      @click="handleClick"
    >
      <span class="absolute inset-0 rounded-full bg-primary/20 blur-md opacity-0 group-hover:opacity-100 transition-opacity"></span>
      <Sparkles class="relative w-6 h-6 text-white" />
      <span class="sr-only">AI 智能助手</span>

      <span
        class="hidden sm:block absolute right-full mr-3 top-1/2 -translate-y-1/2 whitespace-nowrap px-3 py-1.5 rounded-full text-sm font-medium bg-bg-secondary text-text-primary border border-border-color shadow-md opacity-0 translate-x-1 group-hover:opacity-100 group-hover:translate-x-0 transition-all"
      >
        AI 智能助手
      </span>
    </button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Sparkles } from 'lucide-vue-next'

const router = useRouter()

const handleClick = () => {
  router.push('/recommend')
}
</script>

