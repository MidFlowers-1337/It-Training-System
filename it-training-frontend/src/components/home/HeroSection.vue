<script setup lang="ts">
/**
 * HeroSection - Apple 风格 Hero 区域组件
 *
 * 设计特征：
 * - 全屏渐变背景 + 动态浮动球体
 * - 超大标题（80px）+ 精致副标题
 * - staggered fade-in 入场动画
 * - 滚动视差效果
 */

import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Button } from '@/design-system'

const router = useRouter()

// 滚动视差
const scrollY = ref(0)

const handleScroll = () => {
  scrollY.value = window.scrollY
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: true })
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <section class="hero">
    <!-- 背景层：渐变球体 -->
    <div
      class="hero-bg"
      :style="{ transform: `translateY(${scrollY * 0.3}px)` }"
    >
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
    </div>

    <!-- 内容层 -->
    <div
      class="hero-content"
      :style="{ transform: `translateY(${scrollY * 0.1}px)` }"
    >
      <!-- 标签 -->
      <p class="hero-badge">
        <svg class="hero-badge-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z" />
        </svg>
        用 AI 更高效学习
      </p>

      <!-- 超大标题 -->
      <h1 class="hero-title">
        <span class="line line-1">掌握 IT 核心技能</span>
        <span class="line line-2">开启职业新篇章</span>
      </h1>

      <!-- 副标题 -->
      <p class="hero-subtitle">
        体系化课程 · AI 路径推荐 · 数据化反馈，帮助你系统化提升
      </p>

      <!-- CTA 按钮组 -->
      <div class="hero-actions">
        <Button
          variant="primary"
          size="lg"
          @click="router.push('/courses')"
        >
          浏览全部课程
          <template #icon-right>
            <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M5 12h14" /><path d="m12 5 7 7-7 7" />
            </svg>
          </template>
        </Button>
        <Button
          variant="secondary"
          size="lg"
          @click="router.push('/recommend')"
        >
          <template #icon-left>
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="6" cy="19" r="3" /><path d="M9 19h8.5a3.5 3.5 0 0 0 0-7h-11a3.5 3.5 0 0 1 0-7H15" />
              <circle cx="18" cy="5" r="3" />
            </svg>
          </template>
          AI 智能选课
        </Button>
      </div>
    </div>

    <!-- 向下滚动提示 -->
    <div class="scroll-indicator">
      <svg class="scroll-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M12 5v14" /><path d="m19 12-7 7-7-7" />
      </svg>
    </div>
  </section>
</template>

<style scoped>
/* ========================================
   Apple 风格 Hero 区域
   ======================================== */

.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: var(--bg-primary);
}

/* ===== 渐变背景球 ===== */
.hero-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  will-change: transform;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  will-change: transform;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #0071E3 0%, #5AC8FA 100%);
  top: -200px;
  right: -100px;
  animation: float 20s ease-in-out infinite;
}

.orb-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #30D158 0%, #34C759 100%);
  bottom: -100px;
  left: -50px;
  animation: float 15s ease-in-out infinite reverse;
}

.orb-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #5856D6 0%, #AF52DE 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 18s ease-in-out infinite 2s;
  opacity: 0.3;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(30px, -30px) scale(1.05);
  }
}

/* ===== 内容层 ===== */
.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding: 0 var(--page-padding-x);
  max-width: var(--content-medium);
  will-change: transform;
}

/* ===== 标签 ===== */
.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--primary-color-alpha);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: var(--primary-color);
  opacity: 0;
  animation: fadeIn 0.6s ease 0.2s forwards;
}

.hero-badge-icon {
  width: 16px;
  height: 16px;
}

/* ===== 超大标题 ===== */
.hero-title {
  margin-top: 24px;
  font-size: var(--text-display);
  font-weight: var(--text-display-weight);
  letter-spacing: var(--text-display-tracking);
  line-height: 1.05;
  color: var(--text-primary);
}

.hero-title .line {
  display: block;
  opacity: 0;
  transform: translateY(40px);
  animation: fadeInUp 0.8s cubic-bezier(0.25, 0.1, 0.25, 1.0) forwards;
}

.hero-title .line-1 {
  animation-delay: 0.3s;
}

.hero-title .line-2 {
  animation-delay: 0.45s;
  background: linear-gradient(135deg, var(--primary-color) 0%, #5AC8FA 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

@keyframes fadeInUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ===== 副标题 ===== */
.hero-subtitle {
  margin-top: 24px;
  font-size: 20px;
  color: var(--text-secondary);
  line-height: 1.6;
  opacity: 0;
  animation: fadeIn 0.8s ease 0.6s forwards;
}

@keyframes fadeIn {
  to {
    opacity: 1;
  }
}

/* ===== CTA 按钮组 ===== */
.hero-actions {
  margin-top: 48px;
  display: flex;
  justify-content: center;
  gap: 16px;
  opacity: 0;
  animation: fadeIn 0.8s ease 0.8s forwards;
}

/* ===== 滚动提示 ===== */
.scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  opacity: 0;
  animation: fadeIn 1s ease 1.2s forwards, bounce 2s ease-in-out 2s infinite;
}

.scroll-icon {
  width: 24px;
  height: 24px;
  color: var(--text-muted);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(8px);
  }
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .hero-title {
    font-size: 48px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .orb-1 {
    width: 300px;
    height: 300px;
    top: -100px;
    right: -50px;
  }

  .orb-2 {
    width: 200px;
    height: 200px;
  }

  .orb-3 {
    width: 150px;
    height: 150px;
  }
}

/* ===== 深色模式适配 ===== */
[data-theme="dark"] .gradient-orb {
  opacity: 0.35;
}

[data-theme="dark"] .orb-1 {
  background: linear-gradient(135deg, #0A84FF 0%, #5AC8FA 100%);
}

[data-theme="dark"] .hero-title .line-2 {
  background: linear-gradient(135deg, #0A84FF 0%, #64D2FF 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ===== 暖阳橙主题适配 ===== */
[data-theme="warm"] .orb-1 {
  background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
}

[data-theme="warm"] .orb-2 {
  background: linear-gradient(135deg, #FF6B00 0%, #FF9500 100%);
}

[data-theme="warm"] .orb-3 {
  background: linear-gradient(135deg, #FF3B30 0%, #FF6B6B 100%);
}

[data-theme="warm"] .hero-title .line-2 {
  background: linear-gradient(135deg, var(--primary-color) 0%, #FFCC00 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>
