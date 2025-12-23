<template>
  <div class="min-h-screen bg-bg-primary flex items-center justify-center px-4 py-10">
    <div class="relative w-full max-w-md text-center">
      <div class="absolute inset-0 -z-10 blur-3xl opacity-60" style="background: var(--gradient-hero)"></div>

      <div class="glass rounded-3xl border border-border-color/60 shadow-lg p-10">
        <!-- 内联 SVG 图标 -->
        <div class="w-16 h-16 mx-auto rounded-3xl bg-primary/10 border border-primary/20 flex items-center justify-center text-primary">
          <svg class="w-8 h-8" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3"/>
            <path d="M12 9v4"/>
            <path d="M12 17h.01"/>
          </svg>
        </div>
        <h1 class="mt-6 text-5xl font-semibold tracking-tight text-text-primary">404</h1>
        <p class="mt-2 text-sm text-text-secondary">抱歉，你访问的页面不存在。</p>

        <div class="mt-8 flex justify-center gap-3">
          <Button variant="secondary" @click="router.back()">返回上一页</Button>
          <Button variant="primary" @click="goHome">返回首页</Button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Button } from '@/design-system'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}
</script>
