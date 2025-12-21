<template>
  <div class="min-h-screen bg-bg-primary flex items-center justify-center px-4 py-10">
    <div class="relative w-full max-w-md text-center">
      <div class="absolute inset-0 -z-10 blur-3xl opacity-60" style="background: var(--gradient-hero)"></div>

      <div class="glass rounded-3xl border border-border-color/60 shadow-lg p-10">
        <div class="w-16 h-16 mx-auto rounded-3xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
          <TriangleAlert class="w-8 h-8" />
        </div>
        <h1 class="mt-6 text-5xl font-semibold tracking-tight text-text-primary">404</h1>
        <p class="mt-2 text-sm text-text-secondary">抱歉，你访问的页面不存在。</p>

        <div class="mt-8 flex justify-center gap-3">
          <button type="button" class="btn btn-secondary" @click="router.back()">返回上一页</button>
          <button type="button" class="btn btn-primary" @click="goHome">返回首页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { TriangleAlert } from 'lucide-vue-next'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}
</script>
