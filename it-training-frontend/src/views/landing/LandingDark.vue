<!--
  🌙 Landing Dark — Vercel 官网风格
  · 极暗背景 (#08090A), 网格线 pattern
  · Bento Grid 不等大小暗色卡片
  · Command Palette (⌘K) 模拟展示
  · monospace 数字, 发光边框 hover
  · 文字渐变 white→gray
-->
<template>
  <div class="min-h-screen bg-[#08090A] text-gray-300">
    <!-- ═══ Nav — 半透明暗色条 ═══ -->
    <nav class="fixed top-0 inset-x-0 z-50 bg-[#08090A]/80 backdrop-blur-xl border-b border-white/[0.06]">
      <div class="max-w-5xl mx-auto px-6 h-12 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2">
          <div class="w-7 h-7 bg-white rounded-lg flex items-center justify-center">
            <span class="text-black text-[10px] font-bold">IT</span>
          </div>
          <span class="font-semibold text-[15px] text-[#EDEDED]">智能培训</span>
        </router-link>
        <div class="flex items-center gap-4">
          <ThemeSwitcher />
          <router-link to="/login" class="text-sm text-gray-500 hover:text-[#EDEDED] transition">登录</router-link>
          <router-link to="/register"
            class="text-sm font-medium px-4 py-[7px] rounded-lg bg-white text-black hover:bg-gray-200 transition-all">
            Get Started
          </router-link>
        </div>
      </div>
    </nav>

    <!-- ═══ Hero — 网格背景 + 渐变标题 ═══ -->
    <section class="relative pt-36 pb-24 px-6 overflow-hidden">
      <!-- Grid pattern bg -->
      <div class="absolute inset-0 grid-bg opacity-40" />
      <!-- Radial gradient overlay from bottom -->
      <div class="absolute inset-0 bg-gradient-to-t from-[#08090A] via-[#08090A]/60 to-transparent" />

      <div class="max-w-3xl mx-auto text-center relative z-10">
        <ScrollReveal>
          <h1 class="text-5xl sm:text-6xl lg:text-[68px] font-bold leading-[1.08] tracking-tight mb-6
                     bg-gradient-to-b from-white via-white to-gray-500 bg-clip-text text-transparent">
            探索 IT 技能<br />开启成长之路
          </h1>
        </ScrollReveal>

        <ScrollReveal :delay="120">
          <!-- Code-style feature list -->
          <div class="flex items-center justify-center gap-6 mb-10 font-mono text-sm text-gray-500">
            <span class="flex items-center gap-1.5">
              <span class="text-[#818CF8]">▸</span> AI 推荐
            </span>
            <span class="flex items-center gap-1.5">
              <span class="text-[#818CF8]">▸</span> 进度追踪
            </span>
            <span class="flex items-center gap-1.5">
              <span class="text-[#818CF8]">▸</span> 智能计划
            </span>
          </div>
        </ScrollReveal>

        <ScrollReveal :delay="220">
          <router-link to="/register"
            class="inline-flex items-center gap-2 px-7 py-3 rounded-lg bg-white text-black text-sm font-medium
                   hover:shadow-[0_0_30px_rgba(255,255,255,0.15)] transition-all">
            Get Started
            <ArrowRight class="w-4 h-4" :stroke-width="2" />
          </router-link>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Bento Feature Grid — 不等大小卡片 ═══ -->
    <section class="py-20 px-6">
      <div class="max-w-5xl mx-auto">
        <ScrollReveal>
          <h2 class="text-2xl font-bold text-[#EDEDED] text-center mb-12">构建你的技能图谱</h2>
        </ScrollReveal>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <!-- Big card (spans 2 cols) -->
          <ScrollReveal class="md:col-span-2">
            <div class="bento-card h-full p-8 flex flex-col justify-between">
              <div>
                <div class="w-10 h-10 rounded-lg bg-[#818CF8]/10 flex items-center justify-center mb-4">
                  <Sparkles class="w-5 h-5 text-[#818CF8]" :stroke-width="1.75" />
                </div>
                <h3 class="text-lg font-semibold text-[#EDEDED] mb-2">AI 智能推荐引擎</h3>
                <p class="text-sm text-gray-500 leading-relaxed max-w-md">
                  基于你的学习行为、能力画像与协同过滤算法，自动推荐最适合你的课程路径。
                  告别盲目选课，让 AI 成为你的学习顾问。
                </p>
              </div>
              <div class="flex gap-2 mt-6">
                <span class="px-2 py-0.5 rounded-md text-[11px] font-mono bg-[#818CF8]/10 text-[#818CF8]">collaborative-filtering</span>
                <span class="px-2 py-0.5 rounded-md text-[11px] font-mono bg-white/[0.04] text-gray-500">content-based</span>
              </div>
            </div>
          </ScrollReveal>

          <!-- Small cards stacked -->
          <div class="flex flex-col gap-4">
            <ScrollReveal :delay="100">
              <div class="bento-card p-6">
                <div class="w-10 h-10 rounded-lg bg-emerald-500/10 flex items-center justify-center mb-3">
                  <Target class="w-5 h-5 text-emerald-400" :stroke-width="1.75" />
                </div>
                <h3 class="text-sm font-semibold text-[#EDEDED] mb-1">学习计划</h3>
                <p class="text-xs text-gray-500 leading-relaxed">定制个性化目标，按节奏推进</p>
              </div>
            </ScrollReveal>
            <ScrollReveal :delay="180">
              <div class="bento-card p-6">
                <div class="w-10 h-10 rounded-lg bg-amber-500/10 flex items-center justify-center mb-3">
                  <TrendingUp class="w-5 h-5 text-amber-400" :stroke-width="1.75" />
                </div>
                <h3 class="text-sm font-semibold text-[#EDEDED] mb-1">数据分析</h3>
                <p class="text-xs text-gray-500 leading-relaxed">可视化报告，追踪学习成效</p>
              </div>
            </ScrollReveal>
          </div>

          <!-- Bottom full-width card -->
          <ScrollReveal class="md:col-span-3" :delay="200">
            <div class="bento-card p-6 flex items-center gap-6">
              <div class="flex gap-3">
                <div v-for="cat in categories.slice(0, 6)" :key="cat.name"
                     class="w-10 h-10 rounded-lg bg-gradient-to-br flex items-center justify-center"
                     :class="cat.gradient">
                  <component :is="cat.icon" class="w-5 h-5 text-white/80" :stroke-width="1.5" />
                </div>
              </div>
              <div class="flex-1">
                <h3 class="text-sm font-semibold text-[#EDEDED] mb-1">10+ 技术分类</h3>
                <p class="text-xs text-gray-500">涵盖 Java、Python、前端、数据库、AI、后端等全栈领域</p>
              </div>
              <router-link to="/login" class="text-xs text-[#818CF8] hover:text-[#A5B4FC] transition font-mono">
                explore →
              </router-link>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Command Palette 展示 ═══ -->
    <section class="py-16 px-6">
      <div class="max-w-2xl mx-auto">
        <ScrollReveal>
          <div class="text-center mb-8">
            <h2 class="text-2xl font-bold text-[#EDEDED] mb-3">智能搜索，一键触达</h2>
            <p class="text-sm text-gray-500 font-mono">⌘ K · 快速搜索课程、讲师与学习路径</p>
          </div>
        </ScrollReveal>
        <ScrollReveal :delay="100">
          <div class="cmd-palette rounded-xl border border-white/[0.08] bg-[#111113] overflow-hidden shadow-2xl">
            <div class="px-4 py-3 border-b border-white/[0.06] flex items-center gap-3">
              <Search class="w-4 h-4 text-gray-500" :stroke-width="1.75" />
              <span class="text-sm text-gray-500 font-mono">搜索课程...</span>
              <div class="ml-auto flex gap-1">
                <kbd class="kbd-key">⌘</kbd>
                <kbd class="kbd-key">K</kbd>
              </div>
            </div>
            <div class="divide-y divide-white/[0.04]">
              <div v-for="(item, i) in cmdItems" :key="i"
                   class="px-4 py-2.5 flex items-center gap-3 hover:bg-white/[0.03] transition"
                   :class="i === 0 ? 'bg-white/[0.03]' : ''">
                <component :is="item.icon" class="w-4 h-4 text-gray-500" :stroke-width="1.5" />
                <span class="text-sm" :class="i === 0 ? 'text-[#EDEDED]' : 'text-gray-500'">{{ item.label }}</span>
                <span class="ml-auto text-[10px] font-mono text-gray-600">{{ item.type }}</span>
              </div>
            </div>
          </div>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Featured Courses — 暗色水平滚动 ═══ -->
    <section class="py-20 px-6">
      <div class="max-w-6xl mx-auto">
        <ScrollReveal>
          <div class="flex items-center justify-between mb-8">
            <h2 class="text-xl font-bold text-[#EDEDED]">精选课程</h2>
            <router-link to="/login" class="text-xs font-mono text-gray-500 hover:text-[#EDEDED] transition">
              view all →
            </router-link>
          </div>
        </ScrollReveal>

        <!-- Skeleton -->
        <div v-if="coursesLoading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div v-for="n in 4" :key="n" class="h-52 rounded-xl bg-white/[0.03] animate-pulse" />
        </div>

        <!-- Cards -->
        <div v-else-if="featuredCourses.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <ScrollReveal v-for="(c, i) in featuredCourses" :key="c.id" :delay="i * 50">
            <router-link to="/login" class="bento-card block overflow-hidden cursor-pointer group">
              <div class="h-28 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(c.category).gradient">
                <component :is="getCategoryStyle(c.category).icon"
                           class="w-8 h-8 text-white/80" :stroke-width="1.25" />
              </div>
              <div class="p-4">
                <h4 class="font-semibold text-sm text-[#EDEDED] line-clamp-2 mb-1.5">{{ c.name }}</h4>
                <p class="text-xs text-gray-600 line-clamp-2 mb-3">{{ c.description }}</p>
                <div class="flex items-center justify-between text-xs">
                  <span class="px-2 py-0.5 rounded-md text-[10px] font-mono bg-white/[0.04] text-gray-500">
                    {{ c.categoryName || c.category }}
                  </span>
                  <span class="text-gray-600 font-mono">{{ c.durationHours || '—' }}h</span>
                </div>
              </div>
            </router-link>
          </ScrollReveal>
        </div>

        <div v-else class="text-center py-16">
          <BookOpen class="w-12 h-12 mx-auto text-white/[0.06] mb-3" :stroke-width="1" />
          <p class="text-sm text-gray-600">暂无课程</p>
        </div>
      </div>
    </section>

    <!-- ═══ Stats — monospace 数字 ═══ -->
    <section class="py-16 px-6 border-y border-white/[0.06]">
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-8">
          <ScrollReveal v-for="(s, i) in stats" :key="i" :delay="i * 80">
            <div class="text-center">
              <div class="text-4xl font-bold font-mono mb-1
                          bg-gradient-to-r from-[#EDEDED] to-[#818CF8] bg-clip-text text-transparent">
                <NumberCounter :value="s.value" :suffix="s.suffix" />
              </div>
              <div class="text-xs text-gray-600 font-mono">{{ s.label }}</div>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ CTA ═══ -->
    <section class="py-28 px-6">
      <div class="max-w-lg mx-auto text-center">
        <ScrollReveal>
          <h2 class="text-3xl font-bold mb-3
                     bg-gradient-to-r from-white to-[#818CF8] bg-clip-text text-transparent">
            Deploy your learning
          </h2>
          <p class="text-gray-500 mb-10 text-sm font-mono">立即注册，开始你的成长之旅</p>
          <router-link to="/register"
            class="inline-flex items-center gap-2 px-7 py-3 rounded-lg bg-white text-black text-sm font-medium
                   hover:shadow-[0_0_30px_rgba(255,255,255,0.15)] transition-all">
            Get Started
            <ArrowRight class="w-4 h-4" :stroke-width="2" />
          </router-link>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Footer ═══ -->
    <footer class="py-8 px-6 text-center border-t border-white/[0.06]">
      <span class="text-xs text-gray-600 font-mono">© 2025 IT 智能培训系统 · 毕业设计项目</span>
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
  ArrowRight, Clock, BookOpen, Search,
  Terminal, Code2, Globe, Database, Brain,
  Server, Smartphone, Shield, BarChart3, Blocks,
  Sparkles, Target, TrendingUp, GraduationCap, Zap,
} from 'lucide-vue-next'

defineProps<{
  stats: StatItem[]
  featuredCourses: PublicCourse[]
  coursesLoading: boolean
}>()

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

const cmdItems = [
  { icon: GraduationCap, label: 'Java 核心基础',   type: 'course' },
  { icon: Code2,         label: 'Python 数据分析', type: 'course' },
  { icon: Globe,         label: 'Vue 3 实战开发',  type: 'course' },
  { icon: Zap,           label: '快速开始学习',     type: 'action' },
]
</script>

<style scoped>
/* Grid background pattern */
.grid-bg {
  background-image:
    linear-gradient(rgba(255,255,255,0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255,255,255,0.03) 1px, transparent 1px);
  background-size: 60px 60px;
}

/* Bento card */
.bento-card {
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 14px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.bento-card:hover {
  border-color: rgba(129,140,248,0.2);
  box-shadow: 0 0 20px rgba(129,140,248,0.06);
}

/* Keyboard key */
.kbd-key {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 22px;
  height: 22px;
  padding: 0 5px;
  border-radius: 5px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.4);
  font-size: 10px;
  font-family: ui-monospace, monospace;
}

/* Command palette */
.cmd-palette {
  box-shadow:
    0 0 0 1px rgba(255,255,255,0.06),
    0 16px 70px rgba(0,0,0,0.7);
}
</style>
