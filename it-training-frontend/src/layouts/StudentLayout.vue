<template>
  <!-- Focus Mode: bypass layout, render content directly -->
  <div v-if="isFocusMode" class="min-h-screen" :class="focusBgClass">
    <slot />
  </div>
  <!-- Normal Mode: themed layout -->
  <component v-else :is="layoutComponent">
    <slot />
  </component>
</template>

<script setup lang="ts">
/**
 * StudentLayout — 主题路由分发器
 *
 * 根据当前激活的主题动态加载对应的布局组件:
 *   light → StudentNotion (Notion 知识库风, 文字侧边栏)
 *   dark  → StudentLinear (Linear 沉浸风, 暗色全宽顶栏)
 *   warm  → StudentSlack  (Duolingo 活力风, 彩色图标导航条)
 *   pro   → StudentVercel (Vercel 极简风, 极简顶栏)
 *
 * Focus Mode (route.meta.focusMode):
 *   跳过布局壳，直接渲染内容（用于 CourseStudy 专注学习模式）
 */
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import StudentNotion from './student-themes/StudentNotion.vue'
import StudentLinear from './student-themes/StudentLinear.vue'
import StudentSlack from './student-themes/StudentSlack.vue'
import StudentVercel from './student-themes/StudentVercel.vue'

const route = useRoute()
const themeStore = useThemeStore()

const isFocusMode = computed(() => !!route.meta.focusMode)

const focusBgClass = computed(() => ({
  light: 'bg-white',
  dark: 'bg-[#08090A]',
  warm: 'bg-[#FFFBF5]',
  pro: 'bg-[#FAFAFA]',
}[themeStore.theme] || 'bg-white'))

const layouts: Record<string, any> = {
  light: StudentNotion,
  dark: StudentLinear,
  warm: StudentSlack,
  pro: StudentVercel,
}

const layoutComponent = computed(() => layouts[themeStore.theme] || StudentNotion)
</script>
