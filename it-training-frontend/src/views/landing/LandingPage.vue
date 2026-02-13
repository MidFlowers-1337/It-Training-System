<!--
  Landing Page — 主题路由入口
  根据当前主题懒加载对应的设计变体组件
  · Light  → Apple 官网风格
  · Dark   → Vercel 官网风格
  · Warm   → Duolingo 官网风格
  · Pro    → Linear 官网风格
-->
<template>
  <LandingLight v-if="theme === 'light'" v-bind="shared" />
  <LandingDark  v-else-if="theme === 'dark'"  v-bind="shared" />
  <LandingWarm  v-else-if="theme === 'warm'"  v-bind="shared" />
  <LandingPro   v-else                        v-bind="shared" />
</template>

<script setup lang="ts">
import { computed, onMounted, defineAsyncComponent } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useLandingData, type StatItem } from '@/composables/useLandingData'
import type { PublicCourse } from '@/api/publicStats'

/* ── Theme ── */
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Data (fetch once, pass to all variants) ── */
const { stats, featuredCourses, coursesLoading, fetchStats, fetchFeaturedCourses } = useLandingData()

onMounted(() => {
  fetchStats()
  fetchFeaturedCourses(8)
})

/* ── Shared Props ── */
const shared = computed(() => ({
  stats: stats.value,
  featuredCourses: featuredCourses.value,
  coursesLoading: coursesLoading.value,
}))

/* ── Lazy-loaded theme variants ── */
const LandingLight = defineAsyncComponent(() => import('./LandingLight.vue'))
const LandingDark  = defineAsyncComponent(() => import('./LandingDark.vue'))
const LandingWarm  = defineAsyncComponent(() => import('./LandingWarm.vue'))
const LandingPro   = defineAsyncComponent(() => import('./LandingPro.vue'))
</script>
