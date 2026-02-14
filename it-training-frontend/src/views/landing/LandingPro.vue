<!--
  ❄️ Landing Pro — Linear 官网风格
  · 暗色调但不全黑 (#0A0A0B)
  · 渐变文字 (white→indigo), glow 按钮
  · Feature Grid: 3 列无边框, hover 显示淡背景
  · Keyboard Shortcut 展示 (<kbd> UI)
  · Changelog/时间线风格
  · monospace 为主, 小圆角 (4-6px)
-->
<template>
  <div class="min-h-screen bg-[#0A0A0B] text-gray-400">
    <!-- ═══ Nav — 极简暗色 ═══ -->
    <nav class="fixed top-0 inset-x-0 z-50 bg-[#0A0A0B]/80 backdrop-blur-xl border-b border-white/[0.06]">
      <div class="max-w-5xl mx-auto px-6 h-12 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2">
          <div class="w-6 h-6 bg-gradient-to-br from-[#818CF8] to-[#6366F1] rounded-md flex items-center justify-center">
            <span class="text-white text-[9px] font-bold">IT</span>
          </div>
          <span class="font-medium text-sm text-gray-300 tracking-tight">智能培训</span>
        </router-link>
        <div class="flex items-center gap-4">
          <ThemeSwitcher />
          <router-link to="/login" class="text-xs text-gray-500 hover:text-gray-300 transition font-medium">登录</router-link>
          <router-link to="/register"
            class="text-xs font-medium px-4 py-[6px] rounded-md
                   bg-gradient-to-r from-[#818CF8] to-[#6366F1] text-white
                   hover:shadow-[0_0_24px_rgba(129,140,248,0.3)] transition-all">
            开始使用
          </router-link>
        </div>
      </div>
    </nav>

    <!-- ═══ Hero — 渐变大标题 + glow ═══ -->
    <section class="relative pt-40 pb-28 px-6 overflow-hidden">
      <!-- Subtle radial glow -->
      <div class="absolute top-32 left-1/2 -translate-x-1/2 w-[500px] h-[300px]
                  bg-[#818CF8]/[0.06] rounded-full blur-[120px] pointer-events-none" />

      <div class="max-w-3xl mx-auto text-center relative z-10">
        <ScrollReveal>
          <h1 class="text-5xl sm:text-6xl lg:text-[64px] font-bold leading-[1.1] tracking-tight mb-6
                     bg-gradient-to-b from-white via-white/90 to-[#818CF8] bg-clip-text text-transparent">
            探索 IT 技能<br />开启成长之路
          </h1>
        </ScrollReveal>

        <ScrollReveal :delay="100">
          <p class="text-base text-gray-500 max-w-md mx-auto mb-10 leading-relaxed">
            AI 个性化推荐 · 智能学习计划 · 实时进度追踪
          </p>
        </ScrollReveal>

        <ScrollReveal :delay="200">
          <router-link to="/register"
            class="inline-flex items-center gap-2 px-6 py-2.5 rounded-md text-sm font-medium
                   bg-gradient-to-r from-[#818CF8] to-[#6366F1] text-white
                   shadow-[0_0_24px_rgba(129,140,248,0.25)] hover:shadow-[0_0_40px_rgba(129,140,248,0.4)]
                   transition-all">
            Get Started
            <ArrowRight class="w-4 h-4" :stroke-width="2" />
          </router-link>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Feature Grid — 3 列无边框 ═══ -->
    <section class="py-20 px-6">
      <div class="max-w-4xl mx-auto">
        <ScrollReveal>
          <h2 class="text-xl font-semibold text-gray-200 text-center mb-12 tracking-tight">核心能力</h2>
        </ScrollReveal>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-1">
          <ScrollReveal v-for="(f, i) in proFeatures" :key="i" :delay="i * 80">
            <div class="p-6 rounded-lg transition-all hover:bg-white/[0.03] cursor-default group">
              <component :is="f.icon" class="w-5 h-5 mb-3 text-[#818CF8]" :stroke-width="1.75" />
              <h3 class="text-sm font-semibold text-gray-200 mb-1.5 tracking-tight">{{ f.title }}</h3>
              <p class="text-xs text-gray-500 leading-relaxed">{{ f.desc }}</p>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Categories — 紧凑标签 ═══ -->
    <section class="py-16 px-6 border-y border-white/[0.04]">
      <div class="max-w-4xl mx-auto">
        <ScrollReveal>
          <h2 class="text-xl font-semibold text-gray-200 text-center mb-8 tracking-tight">技术分类</h2>
        </ScrollReveal>
        <div class="flex flex-wrap justify-center gap-2">
          <ScrollReveal v-for="(cat, i) in categories" :key="i" :delay="i * 30">
            <router-link to="/login"
              class="flex items-center gap-2 px-3.5 py-2 rounded-md border border-white/[0.06]
                     hover:border-[#818CF8]/20 hover:bg-white/[0.03] transition-all group">
              <div class="w-6 h-6 rounded bg-gradient-to-br flex items-center justify-center" :class="cat.gradient">
                <component :is="cat.icon" class="w-3.5 h-3.5 text-white" :stroke-width="1.5" />
              </div>
              <span class="text-xs font-medium text-gray-400 group-hover:text-gray-300 transition">{{ cat.name }}</span>
            </router-link>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Keyboard Shortcuts — kbd 展示 ═══ -->
    <section class="py-20 px-6">
      <div class="max-w-2xl mx-auto">
        <ScrollReveal>
          <h2 class="text-xl font-semibold text-gray-200 text-center mb-3 tracking-tight">效率至上</h2>
          <p class="text-sm text-gray-500 text-center mb-10">键盘快捷键，让学习触手可及</p>
        </ScrollReveal>
        <div class="space-y-3">
          <ScrollReveal v-for="(sc, i) in shortcuts" :key="i" :delay="i * 60">
            <div class="flex items-center justify-between px-4 py-3 rounded-lg border border-white/[0.04]
                        hover:border-white/[0.08] hover:bg-white/[0.02] transition-all">
              <div class="flex items-center gap-3">
                <component :is="sc.icon" class="w-4 h-4 text-gray-500" :stroke-width="1.75" />
                <span class="text-sm text-gray-300">{{ sc.label }}</span>
              </div>
              <div class="flex gap-1">
                <kbd v-for="(k, j) in sc.keys" :key="j" class="kbd-pro">{{ k }}</kbd>
              </div>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Featured Courses ═══ -->
    <section class="py-20 px-6">
      <div class="max-w-5xl mx-auto">
        <ScrollReveal>
          <div class="flex items-center justify-between mb-8">
            <h2 class="text-xl font-semibold text-gray-200 tracking-tight">精选课程</h2>
            <router-link to="/login" class="text-xs text-gray-500 hover:text-gray-300 transition">
              view all →
            </router-link>
          </div>
        </ScrollReveal>

        <div v-if="coursesLoading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <div v-for="n in 4" :key="n" class="h-44 rounded-lg bg-white/[0.03] animate-pulse" />
        </div>

        <div v-else-if="featuredCourses.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
          <ScrollReveal v-for="(c, i) in featuredCourses" :key="c.id" :delay="i * 50">
            <router-link to="/login"
              class="block rounded-lg border border-white/[0.04] overflow-hidden
                     hover:border-[#818CF8]/15 hover:shadow-[0_0_20px_rgba(129,140,248,0.05)]
                     transition-all cursor-pointer group">
              <div class="h-24 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(c.category).gradient">
                <component :is="getCategoryStyle(c.category).icon"
                           class="w-7 h-7 text-white/80" :stroke-width="1.25" />
              </div>
              <div class="p-3.5 bg-[#111113]">
                <h4 class="font-medium text-xs text-gray-200 line-clamp-2 mb-1 tracking-tight">{{ c.name }}</h4>
                <p class="text-[11px] text-gray-600 line-clamp-2 mb-2.5">{{ c.description }}</p>
                <div class="flex items-center justify-between text-[10px] font-mono">
                  <span class="text-gray-500">{{ c.categoryName || c.category }}</span>
                  <span class="text-gray-600">{{ c.durationHours || '—' }}h</span>
                </div>
              </div>
            </router-link>
          </ScrollReveal>
        </div>

        <div v-else class="text-center py-16">
          <BookOpen class="w-10 h-10 mx-auto text-white/[0.06] mb-3" :stroke-width="1" />
          <p class="text-xs text-gray-600">暂无课程</p>
        </div>
      </div>
    </section>

    <!-- ═══ Changelog / 更新时间线 ═══ -->
    <section class="py-20 px-6 border-t border-white/[0.04]">
      <div class="max-w-2xl mx-auto">
        <ScrollReveal>
          <h2 class="text-xl font-semibold text-gray-200 text-center mb-10 tracking-tight">最近更新</h2>
        </ScrollReveal>
        <div class="relative pl-6 border-l border-white/[0.06]">
          <ScrollReveal v-for="(log, i) in changelog" :key="i" :delay="i * 80">
            <div class="mb-8 relative">
              <!-- Timeline dot -->
              <div class="absolute -left-[31px] top-1 w-3 h-3 rounded-full border-2 border-[#818CF8]"
                   :class="i === 0 ? 'bg-[#818CF8]' : 'bg-[#0A0A0B]'" />
              <div class="text-[10px] font-mono text-gray-600 mb-1">{{ log.date }}</div>
              <h3 class="text-sm font-medium text-gray-300 mb-1">{{ log.title }}</h3>
              <p class="text-xs text-gray-500 leading-relaxed">{{ log.desc }}</p>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Stats ═══ -->
    <section class="py-16 px-6 border-y border-white/[0.04]">
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-8">
          <ScrollReveal v-for="(s, i) in stats" :key="i" :delay="i * 80">
            <div class="text-center">
              <div class="text-3xl font-bold font-mono text-gray-200 mb-1">
                <NumberCounter :value="s.value" :suffix="s.suffix" />
              </div>
              <div class="text-[10px] text-gray-600 font-mono uppercase tracking-wider">{{ s.label }}</div>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ CTA ═══ -->
    <section class="py-28 px-6">
      <div class="max-w-lg mx-auto text-center">
        <ScrollReveal>
          <h2 class="text-3xl font-bold mb-3 tracking-tight
                     bg-gradient-to-r from-white to-[#818CF8] bg-clip-text text-transparent">
            Ready to build?
          </h2>
          <p class="text-sm text-gray-500 mb-10">立即注册，开启你的成长之旅</p>
          <router-link to="/register"
            class="inline-flex items-center gap-2 px-6 py-2.5 rounded-md text-sm font-medium
                   bg-gradient-to-r from-[#818CF8] to-[#6366F1] text-white
                   shadow-[0_0_24px_rgba(129,140,248,0.25)] hover:shadow-[0_0_40px_rgba(129,140,248,0.4)]
                   transition-all">
            Get Started
            <ArrowRight class="w-4 h-4" :stroke-width="2" />
          </router-link>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Footer ═══ -->
    <footer class="py-8 px-6 text-center border-t border-white/[0.04]">
      <span class="text-[10px] text-gray-600 font-mono">© 2025 IT 智能培训系统 · 毕业设计项目</span>
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
  ArrowRight, BookOpen, Search,
  Terminal, Code2, Globe, Database, Brain,
  Server, Smartphone, Shield, BarChart3, Blocks,
  Sparkles, Target, TrendingUp, Command, Zap, ListOrdered,
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

const proFeatures = [
  {
    icon: Sparkles,
    title: 'AI 智能推荐',
    desc: '基于学习行为与能力画像的个性化课程推荐引擎',
  },
  {
    icon: Target,
    title: '学习计划管理',
    desc: '创建、追踪、完成你的个性化学习目标与计划',
  },
  {
    icon: TrendingUp,
    title: '数据分析报告',
    desc: '可视化学习数据，洞察成长趋势与薄弱环节',
  },
]

const shortcuts = [
  { icon: Search,       label: '搜索课程',          keys: ['⌘', 'K'] },
  { icon: ListOrdered,  label: '查看学习计划',      keys: ['⌘', 'P'] },
  { icon: Zap,          label: '快速打卡',           keys: ['⌘', 'D'] },
  { icon: Command,      label: '切换主题',           keys: ['⌘', 'T'] },
]

const changelog = [
  {
    date: '2025-01',
    title: 'AI 推荐引擎 v2.0',
    desc: '全新协同过滤 + 内容推荐混合算法，推荐准确率提升 40%',
  },
  {
    date: '2024-12',
    title: '成就系统上线',
    desc: '新增学习打卡、课程完成、连续学习等 20+ 成就徽章',
  },
  {
    date: '2024-11',
    title: '学习报告功能',
    desc: '支持周报、月报、年报三种维度的学习数据可视化分析',
  },
]
</script>

<style scoped>
/* Pro kbd key */
.kbd-pro {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 24px;
  height: 24px;
  padding: 0 6px;
  border-radius: 4px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  box-shadow: 0 1px 0 rgba(255,255,255,0.04);
  color: rgba(255,255,255,0.4);
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, 'SF Mono', monospace;
}
</style>
