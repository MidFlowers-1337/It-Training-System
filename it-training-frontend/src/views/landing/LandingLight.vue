<!--
  ☀️ Landing Light — Apple 官网风格
  · 纯白极简, 超大标题 thin→bold 混排
  · 渐变模糊色球 Hero 背景
  · 3 列淡彩 Feature Tiles (24px 圆角)
  · 统计数字极大 (48px), tabular-nums
  · Section 间距 120-160px
-->
<template>
  <div class="min-h-screen bg-white text-gray-900">
    <!-- ═══ Nav — 毛玻璃通栏 ═══ -->
    <nav class="fixed top-0 inset-x-0 z-50 bg-white/80 backdrop-blur-xl border-b border-gray-100/80">
      <div class="max-w-5xl mx-auto px-6 h-12 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2">
          <div class="w-7 h-7 bg-black rounded-lg flex items-center justify-center">
            <span class="text-white text-[10px] font-bold">IT</span>
          </div>
          <span class="font-semibold text-[15px] text-gray-900">智能培训</span>
        </router-link>
        <div class="flex items-center gap-4">
          <ThemeSwitcher />
          <router-link to="/login" class="text-sm text-gray-500 hover:text-gray-900 transition">登录</router-link>
          <router-link to="/register"
            class="text-sm font-medium px-4 py-[7px] rounded-full bg-black text-white hover:bg-gray-800 transition-all">
            免费注册
          </router-link>
        </div>
      </div>
    </nav>

    <!-- ═══ Hero — Apple 大标题 + 渐变色球 ═══ -->
    <section class="relative pt-36 pb-28 px-6 overflow-hidden">
      <!-- Gradient Orbs -->
      <div class="absolute top-20 left-1/2 -translate-x-1/2 w-[600px] h-[600px] rounded-full
                  bg-gradient-to-br from-blue-400/20 via-purple-400/15 to-pink-400/10
                  blur-[120px] pointer-events-none" />
      <div class="absolute top-40 right-1/4 w-[300px] h-[300px] rounded-full
                  bg-gradient-to-br from-cyan-300/15 to-blue-300/10
                  blur-[100px] pointer-events-none" />

      <div class="max-w-3xl mx-auto text-center relative z-10">
        <ScrollReveal>
          <h1 class="text-5xl sm:text-6xl lg:text-[68px] leading-[1.08] tracking-tight mb-6">
            <span class="font-light text-gray-900">探索</span>
            <span class="font-bold text-gray-900"> IT 技能</span><br />
            <span class="font-light text-gray-900">开启成长之路</span>
          </h1>
        </ScrollReveal>

        <ScrollReveal :delay="120">
          <p class="text-xl text-gray-500 max-w-[560px] mx-auto mb-10 leading-relaxed font-light">
            AI 个性化推荐 · 智能学习计划 · 实时进度追踪
          </p>
        </ScrollReveal>

        <ScrollReveal :delay="220">
          <div class="flex items-center justify-center gap-4">
            <router-link to="/register"
              class="inline-flex items-center gap-2 px-7 py-3.5 rounded-full bg-[#0071E3] text-white text-[15px] font-medium
                     hover:bg-[#0077ED] transition-all shadow-[0_4px_14px_rgba(0,113,227,0.35)]">
              免费开始学习
              <ArrowRight class="w-4 h-4" :stroke-width="2" />
            </router-link>
            <router-link to="/login"
              class="inline-flex items-center gap-1 text-[15px] text-[#0071E3] font-medium hover:underline transition">
              登录账号
              <ChevronRight class="w-4 h-4" :stroke-width="2" />
            </router-link>
          </div>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Feature Tiles — 3 列淡彩渐变 ═══ -->
    <section class="py-24 px-6">
      <div class="max-w-4xl mx-auto">
        <ScrollReveal>
          <h2 class="text-3xl font-bold text-center text-gray-900 mb-4">为什么选择我们</h2>
          <p class="text-center text-gray-500 mb-14 text-lg font-light">精心设计的学习体验，助你事半功倍</p>
        </ScrollReveal>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-5">
          <ScrollReveal v-for="(f, i) in features" :key="i" :delay="i * 100">
            <div :class="f.bgClass"
                 class="rounded-3xl p-8 text-center transition-all hover:-translate-y-1 hover:shadow-lg cursor-default">
              <div class="w-14 h-14 rounded-2xl mx-auto mb-5 flex items-center justify-center" :class="f.iconBg">
                <component :is="f.icon" class="w-7 h-7 text-white" :stroke-width="1.5" />
              </div>
              <h3 class="text-lg font-semibold text-gray-900 mb-2">{{ f.title }}</h3>
              <p class="text-sm text-gray-500 leading-relaxed">{{ f.desc }}</p>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Categories — 5 列分类网格 ═══ -->
    <section class="py-20 px-6 bg-[#FAFAFA]">
      <div class="max-w-5xl mx-auto">
        <ScrollReveal>
          <h2 class="text-3xl font-bold text-center text-gray-900 mb-12">浏览分类</h2>
        </ScrollReveal>
        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-4">
          <ScrollReveal v-for="(cat, i) in categories" :key="i" :delay="i * 40">
            <router-link to="/login"
              class="flex flex-col items-center gap-3 p-6 rounded-2xl bg-white border border-gray-100
                     hover:-translate-y-1 hover:shadow-[0_8px_30px_rgba(0,0,0,0.06)] transition-all cursor-pointer group">
              <div class="w-12 h-12 rounded-2xl bg-gradient-to-br flex items-center justify-center shadow-sm"
                   :class="cat.gradient">
                <component :is="cat.icon" class="w-6 h-6 text-white" :stroke-width="1.5" />
              </div>
              <span class="text-sm font-medium text-gray-700 group-hover:text-gray-900 transition">{{ cat.name }}</span>
            </router-link>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Featured Courses — 4 列白色卡片 ═══ -->
    <section class="py-24 px-6">
      <div class="max-w-6xl mx-auto">
        <ScrollReveal>
          <div class="flex items-center justify-between mb-10">
            <h2 class="text-3xl font-bold text-gray-900">精选课程</h2>
            <router-link to="/login" class="text-sm text-gray-400 hover:text-gray-700 transition">
              查看全部 →
            </router-link>
          </div>
        </ScrollReveal>

        <!-- Skeleton -->
        <div v-if="coursesLoading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <div v-for="n in 4" :key="n" class="h-56 rounded-2xl bg-gray-50 animate-pulse" />
        </div>

        <!-- Cards -->
        <div v-else-if="featuredCourses.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <ScrollReveal v-for="(c, i) in featuredCourses" :key="c.id" :delay="i * 60">
            <router-link to="/login"
              class="block bg-white rounded-2xl overflow-hidden border border-gray-100
                     shadow-[0_2px_8px_rgba(0,0,0,0.04)] hover:shadow-[0_12px_40px_rgba(0,0,0,0.08)]
                     hover:-translate-y-1 transition-all cursor-pointer group">
              <!-- Cover -->
              <div class="h-32 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(c.category).gradient">
                <component :is="getCategoryStyle(c.category).icon"
                           class="w-10 h-10 text-white/80" :stroke-width="1.25" />
              </div>
              <!-- Content -->
              <div class="p-4">
                <h4 class="font-semibold text-[15px] text-gray-900 line-clamp-2 mb-1.5">{{ c.name }}</h4>
                <p class="text-sm text-gray-400 line-clamp-2 mb-3">{{ c.description }}</p>
                <div class="flex items-center justify-between text-xs text-gray-400">
                  <span class="px-2.5 py-0.5 rounded-full text-[11px] font-medium"
                        :class="[getCategoryStyle(c.category).bgClass, getCategoryStyle(c.category).textClass]">
                    {{ c.categoryName || c.category }}
                  </span>
                  <span class="flex items-center gap-1">
                    <Clock class="w-3 h-3" :stroke-width="2" />
                    {{ c.durationHours || '—' }}h
                  </span>
                </div>
              </div>
            </router-link>
          </ScrollReveal>
        </div>

        <!-- Empty -->
        <div v-else class="text-center py-20">
          <BookOpen class="w-14 h-14 mx-auto text-gray-200 mb-4" :stroke-width="1" />
          <p class="text-gray-400">暂无课程，敬请期待</p>
        </div>
      </div>
    </section>

    <!-- ═══ Stats — 横排大数字 ═══ -->
    <section class="py-20 px-6 bg-[#FAFAFA]">
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-10">
          <ScrollReveal v-for="(s, i) in stats" :key="i" :delay="i * 100">
            <div class="text-center">
              <div class="text-5xl font-bold text-gray-900 mb-2" style="font-variant-numeric: tabular-nums">
                <NumberCounter :value="s.value" :suffix="s.suffix" />
              </div>
              <div class="text-sm text-gray-500 font-light">{{ s.label }}</div>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ CTA ═══ -->
    <section class="py-32 px-6">
      <div class="max-w-lg mx-auto text-center">
        <ScrollReveal>
          <h2 class="text-3xl font-bold text-gray-900 mb-4">准备好开始了吗？</h2>
          <p class="text-lg text-gray-500 font-light mb-10">
            免费注册，立即开启你的 IT 技能成长之旅
          </p>
          <router-link to="/register"
            class="inline-flex items-center gap-2 px-8 py-4 rounded-full bg-[#0071E3] text-white text-[15px] font-medium
                   hover:bg-[#0077ED] transition-all shadow-[0_4px_14px_rgba(0,113,227,0.35)]">
            免费开始使用
            <ArrowRight class="w-4 h-4" :stroke-width="2" />
          </router-link>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Footer ═══ -->
    <footer class="py-8 px-6 text-center border-t border-gray-100">
      <span class="text-xs text-gray-400">© 2025 IT 智能培训系统 · 毕业设计项目</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import type { StatItem } from '@/composables/useLandingData'
import type { PublicCourse } from '@/api/publicStats'
import { getCategoryStyle } from '@/utils/categoryColors'
import ScrollReveal from '@/components/effects/ScrollReveal.vue'
import NumberCounter from '@/components/effects/NumberCounter.vue'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import {
  ArrowRight, ChevronRight, Clock, BookOpen,
  Terminal, Code2, Globe, Database, Brain,
  Server, Smartphone, Shield, BarChart3, Blocks,
  Sparkles, Target, TrendingUp,
} from 'lucide-vue-next'

defineProps<{
  stats: StatItem[]
  featuredCourses: PublicCourse[]
  coursesLoading: boolean
}>()

/* ── Static Data ── */
const categories = [
  { name: 'Java',      icon: Terminal,    gradient: 'from-amber-500 to-orange-600' },
  { name: 'Python',    icon: Code2,       gradient: 'from-blue-500 to-cyan-500' },
  { name: '前端开发',   icon: Globe,       gradient: 'from-emerald-500 to-teal-500' },
  { name: '数据库',     icon: Database,    gradient: 'from-purple-500 to-violet-600' },
  { name: 'AI / ML',   icon: Brain,       gradient: 'from-rose-500 to-pink-600' },
  { name: '后端开发',   icon: Server,      gradient: 'from-indigo-500 to-blue-600' },
  { name: '移动开发',   icon: Smartphone,  gradient: 'from-cyan-500 to-sky-600' },
  { name: '网络安全',   icon: Shield,      gradient: 'from-red-500 to-rose-600' },
  { name: '大数据',     icon: BarChart3,   gradient: 'from-teal-500 to-emerald-600' },
  { name: 'DevOps',    icon: Blocks,      gradient: 'from-slate-500 to-zinc-600' },
]

const features = [
  {
    icon: Sparkles,
    title: 'AI 智能推荐',
    desc: '基于学习行为与能力画像，精准推荐最适合你的课程路径',
    bgClass: 'bg-gradient-to-br from-blue-50 to-indigo-50/50',
    iconBg: 'bg-gradient-to-br from-blue-500 to-indigo-600',
  },
  {
    icon: Target,
    title: '个性化学习计划',
    desc: '制定专属学习节奏，跟踪每日进度，养成持续学习习惯',
    bgClass: 'bg-gradient-to-br from-purple-50 to-pink-50/50',
    iconBg: 'bg-gradient-to-br from-purple-500 to-pink-600',
  },
  {
    icon: TrendingUp,
    title: '数据驱动成长',
    desc: '可视化学习报告，洞察薄弱环节，让进步有迹可循',
    bgClass: 'bg-gradient-to-br from-emerald-50 to-cyan-50/50',
    iconBg: 'bg-gradient-to-br from-emerald-500 to-cyan-600',
  },
]
</script>
