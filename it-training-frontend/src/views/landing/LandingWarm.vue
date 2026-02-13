<!--
  🌅 Landing Warm — Duolingo 官网风格
  · 暖白底色 (#FFFBF5), 圆润字体
  · Hero: 左文右图, 3D 凸起按钮 (底部加深色 border)
  · Feature Cards: 彩色卡片, 4px 底部 border (3D 感)
  · Gamification: XP 进度条, 成就预览
  · 圆角极大 (16-24px)
-->
<template>
  <div class="min-h-screen bg-[#FFFBF5] text-[#292524]">
    <!-- ═══ Nav — 圆润暖色 ═══ -->
    <nav class="fixed top-0 inset-x-0 z-50 bg-[#FFFBF5]/90 backdrop-blur-xl border-b border-[#E7E5E4]">
      <div class="max-w-5xl mx-auto px-6 h-14 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2.5">
          <div class="w-9 h-9 bg-[#58CC02] rounded-xl flex items-center justify-center shadow-[0_3px_0_#1B8A2E]">
            <span class="text-white text-xs font-bold">IT</span>
          </div>
          <span class="font-bold text-[16px] text-[#292524]">智能培训</span>
        </router-link>
        <div class="flex items-center gap-4">
          <ThemeSwitcher />
          <router-link to="/login"
            class="text-sm font-bold text-[#1CB0F6] hover:text-[#0C99DB] transition">
            登录
          </router-link>
          <router-link to="/register"
            class="text-sm font-bold px-5 py-2.5 rounded-2xl bg-[#58CC02] text-white
                   shadow-[0_4px_0_#46A302] hover:brightness-105
                   active:translate-y-[2px] active:shadow-[0_2px_0_#46A302] transition-all">
            免费注册
          </router-link>
        </div>
      </div>
    </nav>

    <!-- ═══ Hero — 左文右图, 游戏化 ═══ -->
    <section class="pt-28 pb-16 px-6">
      <div class="max-w-5xl mx-auto flex flex-col lg:flex-row items-center gap-12">
        <!-- Left: Text -->
        <div class="flex-1 text-center lg:text-left">
          <ScrollReveal>
            <h1 class="text-4xl sm:text-5xl font-extrabold text-[#292524] leading-tight mb-4">
              学编程，<br class="hidden lg:block" />
              <span class="text-[#58CC02]">像玩游戏一样</span>简单
            </h1>
          </ScrollReveal>
          <ScrollReveal :delay="100">
            <p class="text-lg text-[#78716C] mb-8 leading-relaxed max-w-md mx-auto lg:mx-0">
              跟随 AI 学习助手，通过打卡、成就、等级系统，让学习成为你最好的日常习惯。
            </p>
          </ScrollReveal>
          <ScrollReveal :delay="200">
            <div class="flex flex-col sm:flex-row items-center gap-3 justify-center lg:justify-start">
              <router-link to="/register"
                class="px-8 py-3.5 rounded-2xl bg-[#58CC02] text-white text-[15px] font-bold
                       shadow-[0_4px_0_#46A302] hover:brightness-105
                       active:translate-y-[2px] active:shadow-[0_2px_0_#46A302]
                       transition-all inline-flex items-center gap-2">
                开始免费学习
                <ArrowRight class="w-5 h-5" :stroke-width="2.5" />
              </router-link>
              <router-link to="/login"
                class="px-8 py-3.5 rounded-2xl bg-white text-[#1CB0F6] text-[15px] font-bold
                       border-2 border-[#E5E7EB] shadow-[0_4px_0_#E5E7EB]
                       hover:bg-[#F0F9FF] active:translate-y-[2px] active:shadow-[0_2px_0_#E5E7EB]
                       transition-all">
                已有账号
              </router-link>
            </div>
          </ScrollReveal>
        </div>

        <!-- Right: Illustration (CSS art) -->
        <ScrollReveal :delay="150" class="flex-shrink-0">
          <div class="relative w-72 h-72 lg:w-80 lg:h-80">
            <!-- Decorative circles -->
            <div class="absolute inset-0 rounded-full bg-[#FFC800]/15 animate-pulse" />
            <div class="absolute inset-4 rounded-full bg-[#58CC02]/10" />
            <div class="absolute inset-8 rounded-full bg-white shadow-lg flex items-center justify-center">
              <div class="text-center">
                <div class="text-5xl mb-2">🎓</div>
                <div class="text-2xl font-extrabold text-[#58CC02]">Lv.1</div>
                <div class="text-sm text-[#78716C] font-medium">开始冒险！</div>
                <div class="mt-3 w-32 h-3 bg-[#E5E7EB] rounded-full overflow-hidden mx-auto">
                  <div class="h-full w-1/4 bg-gradient-to-r from-[#58CC02] to-[#7AE82F] rounded-full" />
                </div>
                <div class="text-[10px] text-[#A8A29E] mt-1 font-medium">25 / 100 XP</div>
              </div>
            </div>
          </div>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Feature Cards — 彩色 3D 卡片 ═══ -->
    <section class="py-16 px-6">
      <div class="max-w-4xl mx-auto">
        <ScrollReveal>
          <h2 class="text-2xl font-extrabold text-center text-[#292524] mb-10">为什么选择我们？</h2>
        </ScrollReveal>
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <ScrollReveal v-for="(f, i) in warmFeatures" :key="i" :delay="i * 80">
            <div class="rounded-2xl p-5 text-center cursor-default transition-all hover:-translate-y-1"
                 :class="f.cardClass">
              <div class="text-3xl mb-3">{{ f.emoji }}</div>
              <h3 class="text-sm font-bold text-white mb-1">{{ f.title }}</h3>
              <p class="text-xs text-white/80 leading-relaxed">{{ f.desc }}</p>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Categories — 彩色胶囊 ═══ -->
    <section class="py-16 px-6 bg-white/50">
      <div class="max-w-4xl mx-auto">
        <ScrollReveal>
          <h2 class="text-2xl font-extrabold text-center text-[#292524] mb-8">探索学习分类</h2>
        </ScrollReveal>
        <div class="flex flex-wrap justify-center gap-3">
          <ScrollReveal v-for="(cat, i) in categories" :key="i" :delay="i * 40">
            <router-link to="/login"
              class="flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-white border-2 border-[#E5E7EB]
                     shadow-[0_3px_0_#E5E7EB] hover:shadow-[0_4px_0_#D6D3D1] hover:-translate-y-0.5
                     active:translate-y-[1px] active:shadow-[0_1px_0_#E5E7EB] transition-all group">
              <div class="w-8 h-8 rounded-lg bg-gradient-to-br flex items-center justify-center" :class="cat.gradient">
                <component :is="cat.icon" class="w-4 h-4 text-white" :stroke-width="1.5" />
              </div>
              <span class="text-sm font-bold text-[#292524]">{{ cat.name }}</span>
            </router-link>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ Featured Courses — 彩色卡片网格 ═══ -->
    <section class="py-16 px-6">
      <div class="max-w-5xl mx-auto">
        <ScrollReveal>
          <div class="flex items-center justify-between mb-8">
            <h2 class="text-2xl font-extrabold text-[#292524]">🔥 热门课程</h2>
            <router-link to="/login" class="text-sm font-bold text-[#1CB0F6] hover:underline">
              查看全部 →
            </router-link>
          </div>
        </ScrollReveal>

        <div v-if="coursesLoading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div v-for="n in 4" :key="n" class="h-48 rounded-2xl bg-[#F5F5F4] animate-pulse" />
        </div>

        <div v-else-if="featuredCourses.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <ScrollReveal v-for="(c, i) in featuredCourses" :key="c.id" :delay="i * 60">
            <router-link to="/login"
              class="block bg-white rounded-2xl overflow-hidden border-2 border-[#E5E7EB]
                     shadow-[0_3px_0_#E5E7EB] hover:-translate-y-1 hover:shadow-[0_5px_0_#D6D3D1]
                     transition-all cursor-pointer group">
              <div class="h-24 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(c.category).gradient">
                <component :is="getCategoryStyle(c.category).icon"
                           class="w-8 h-8 text-white/80" :stroke-width="1.25" />
              </div>
              <div class="p-3.5">
                <h4 class="font-bold text-sm text-[#292524] line-clamp-2 mb-1">{{ c.name }}</h4>
                <p class="text-xs text-[#78716C] line-clamp-2 mb-2.5">{{ c.description }}</p>
                <div class="flex items-center justify-between text-xs">
                  <span class="px-2 py-0.5 rounded-full text-[11px] font-bold"
                        :class="[getCategoryStyle(c.category).bgClass, getCategoryStyle(c.category).textClass]">
                    {{ c.categoryName || c.category }}
                  </span>
                  <span class="text-[#A8A29E] flex items-center gap-1 font-medium">
                    <Clock class="w-3 h-3" :stroke-width="2" />
                    {{ c.durationHours || '—' }}h
                  </span>
                </div>
              </div>
            </router-link>
          </ScrollReveal>
        </div>

        <div v-else class="text-center py-16">
          <div class="text-5xl mb-3">📚</div>
          <p class="text-[#78716C] font-medium">暂无课程，敬请期待</p>
        </div>
      </div>
    </section>

    <!-- ═══ Stats — 彩色圆形统计 ═══ -->
    <section class="py-16 px-6 bg-[#FEF3C7]/30 border-y border-[#E7E5E4]">
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-8">
          <ScrollReveal v-for="(s, i) in statsWithEmoji" :key="i" :delay="i * 80">
            <div class="text-center">
              <div class="w-20 h-20 rounded-full mx-auto mb-3 flex items-center justify-center text-3xl"
                   :class="s.ringClass">
                {{ s.emoji }}
              </div>
              <div class="text-3xl font-extrabold text-[#292524] mb-1">
                <NumberCounter :value="s.value" :suffix="s.suffix" />
              </div>
              <div class="text-sm text-[#78716C] font-medium">{{ s.label }}</div>
            </div>
          </ScrollReveal>
        </div>
      </div>
    </section>

    <!-- ═══ CTA — 大绿色 3D 按钮 ═══ -->
    <section class="py-24 px-6">
      <div class="max-w-lg mx-auto text-center">
        <ScrollReveal>
          <div class="text-5xl mb-4">🚀</div>
          <h2 class="text-2xl font-extrabold text-[#292524] mb-3">准备好开始你的学习冒险了吗？</h2>
          <p class="text-[#78716C] mb-10">免费注册，立刻获得 100 XP 新手奖励！</p>
          <router-link to="/register"
            class="inline-flex items-center gap-2 px-10 py-4 rounded-2xl bg-[#58CC02] text-white text-lg font-bold
                   shadow-[0_5px_0_#46A302] hover:brightness-105
                   active:translate-y-[3px] active:shadow-[0_2px_0_#46A302] transition-all">
            开始免费学习
            <ArrowRight class="w-5 h-5" :stroke-width="2.5" />
          </router-link>
        </ScrollReveal>
      </div>
    </section>

    <!-- ═══ Footer ═══ -->
    <footer class="py-8 px-6 text-center border-t border-[#E7E5E4]">
      <span class="text-xs text-[#A8A29E]">© 2025 IT 智能培训系统 · 毕业设计项目 ❤️</span>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { StatItem } from '@/composables/useLandingData'
import type { PublicCourse } from '@/api/publicStats'
import { getCategoryStyle } from '@/utils/categoryColors'
import ScrollReveal from '@/components/effects/ScrollReveal.vue'
import NumberCounter from '@/components/effects/NumberCounter.vue'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import {
  ArrowRight, Clock,
  Terminal, Code2, Globe, Database, Brain,
  Server, Smartphone, Shield, BarChart3, Blocks,
} from 'lucide-vue-next'

const props = defineProps<{
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

const warmFeatures = [
  {
    emoji: '🤖',
    title: 'AI 推荐',
    desc: '智能匹配最适合你的课程',
    cardClass: 'bg-[#58CC02] shadow-[0_4px_0_#46A302]',
  },
  {
    emoji: '🎯',
    title: '学习计划',
    desc: '定制你的专属学习节奏',
    cardClass: 'bg-[#1CB0F6] shadow-[0_4px_0_#0C99DB]',
  },
  {
    emoji: '🔥',
    title: '每日打卡',
    desc: '保持连续学习纪录',
    cardClass: 'bg-[#FFC800] shadow-[0_4px_0_#E5A800]',
  },
  {
    emoji: '🏆',
    title: '成就系统',
    desc: '解锁徽章，收获荣誉',
    cardClass: 'bg-[#CE82FF] shadow-[0_4px_0_#A855F7]',
  },
]

const statsEmojis = ['📚', '👨‍🎓', '👨‍🏫', '🎯']
const statsRings  = [
  'bg-[#58CC02]/10',
  'bg-[#1CB0F6]/10',
  'bg-[#FFC800]/10',
  'bg-[#CE82FF]/10',
]

const statsWithEmoji = computed(() =>
  props.stats.map((s, i) => ({
    ...s,
    emoji: statsEmojis[i] || '📊',
    ringClass: statsRings[i] || 'bg-gray-100',
  })),
)
</script>
