<template>
  <div class="space-y-8">
    <!-- ── Dashboard Section (merged from Dashboard.vue) ── -->
    <DashboardSection />

    <!-- ── Divider ── -->
    <div :class="dividerClass" />

    <!-- ── Sections Skeleton ── -->
    <div v-if="sectionsLoading" class="animate-pulse space-y-8">
      <div class="h-8 w-40 rounded-lg mx-auto" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
      <div v-for="n in 2" :key="n" class="space-y-4">
        <div class="h-5 w-32 rounded" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          <div v-for="m in 4" :key="m" class="h-52 rounded-xl"
               :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
        </div>
      </div>
    </div>

    <!-- ================================================================
         ☀️ LIGHT — Stripe：4 列网格卡片 + 顶部色条 + 多层阴影
         ================================================================ -->
    <template v-if="!sectionsLoading && theme === 'light'">
      <!-- Page Title -->
      <ScrollReveal>
        <div class="text-center mb-8">
          <h2 class="text-3xl font-bold text-[#0A2540] mb-2">发现课程</h2>
          <p class="text-[#425466]">探索适合你的学习内容</p>
        </div>
      </ScrollReveal>

      <section v-for="(sec, si) in sections" :key="si">
        <ScrollReveal :delay="si * 100">
          <div class="flex items-center gap-2 mb-4">
            <component :is="sec.icon" class="w-5 h-5" :class="sec.iconClass" :stroke-width="1.75" />
            <h3 class="text-xl font-semibold text-[#0A2540]">{{ sec.title }}</h3>
          </div>
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            <div
              v-for="c in sec.courses" :key="c.id"
              class="bg-white rounded-xl overflow-hidden cursor-pointer group
                     shadow-[0_2px_4px_rgba(0,0,0,0.04)] hover:shadow-[0_15px_35px_rgba(60,66,87,0.08)]
                     hover:-translate-y-1 transition-all border border-[#E3E8EE]"
              @click="$router.push(`/student/courses/${c.id}`)"
            >
              <!-- Top color bar -->
              <div class="h-1.5 bg-gradient-to-r" :class="getCategoryStyle(c.category).gradient" />
              <!-- Cover -->
              <div class="h-28 bg-gradient-to-br flex items-center justify-center"
                   :class="getCategoryStyle(c.category).gradient">
                <component :is="getCategoryStyle(c.category).icon" class="w-10 h-10 text-white/80" :stroke-width="1.25" />
              </div>
              <!-- Content -->
              <div class="p-4">
                <h4 class="font-semibold text-sm text-[#0A2540] line-clamp-2 mb-2">{{ c.title }}</h4>
                <p class="text-xs text-[#425466] line-clamp-2 mb-3">{{ c.description }}</p>
                <div class="flex items-center justify-between text-xs text-[#8898AA]">
                  <span class="px-2 py-0.5 rounded-full text-[11px] font-medium"
                        :class="[getCategoryStyle(c.category).bgClass, getCategoryStyle(c.category).textClass]">
                    {{ c.category }}
                  </span>
                  <span>{{ c.enrollCount || 0 }} 人学习</span>
                </div>
              </div>
            </div>
          </div>
        </ScrollReveal>
      </section>
    </template>

    <!-- ================================================================
         🌙 DARK — Linear：水平滚动行 + Glow 卡片
         ================================================================ -->
    <template v-else-if="!sectionsLoading && theme === 'dark'">
      <ScrollReveal>
        <div class="text-center mb-8">
          <h2 class="text-2xl font-bold text-[#EDEDED] mb-2">发现课程</h2>
          <p class="text-[#6B6B6E] text-sm">探索适合你的学习内容</p>
        </div>
      </ScrollReveal>

      <section v-for="(sec, si) in sections" :key="si" class="mb-8">
        <ScrollReveal :delay="si * 100">
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-2">
              <component :is="sec.icon" class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
              <h3 class="text-base font-semibold text-[#EDEDED]">{{ sec.title }}</h3>
            </div>
            <button class="text-xs text-gray-600 hover:text-[#EDEDED] transition font-mono">查看全部 →</button>
          </div>
          <!-- Horizontal scroll row -->
          <div class="flex gap-4 overflow-x-auto pb-3 scrollbar-none snap-x snap-mandatory">
            <div
              v-for="c in sec.courses" :key="c.id"
              class="flex-shrink-0 w-52 snap-start cursor-pointer group"
              @click="$router.push(`/student/courses/${c.id}`)"
            >
              <div class="rounded-xl overflow-hidden border border-white/[0.06]
                          hover:border-[#818CF8]/20 hover:shadow-[0_0_20px_rgba(129,140,248,0.06)]
                          transition-all bg-[#111113]">
                <div class="h-28 bg-gradient-to-br flex items-center justify-center"
                     :class="getCategoryStyle(c.category).gradient">
                  <component :is="getCategoryStyle(c.category).icon" class="w-8 h-8 text-white/70" :stroke-width="1.25" />
                </div>
                <div class="p-3">
                  <h4 class="text-sm font-medium text-[#EDEDED] line-clamp-2 mb-1">{{ c.title }}</h4>
                  <p class="text-[11px] text-gray-600 line-clamp-1 mb-2">{{ c.description }}</p>
                  <div class="flex items-center justify-between text-[10px] font-mono text-gray-600">
                    <span>{{ c.category }}</span>
                    <span>{{ c.enrollCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </ScrollReveal>
      </section>
    </template>

    <!-- ================================================================
         🌅 WARM — Duolingo 学习路径：蜿蜒路径 + 圆形节点
         ================================================================ -->
    <template v-else-if="!sectionsLoading && theme === 'warm'">
      <ScrollReveal>
        <div class="text-center mb-8">
          <h2 class="text-2xl font-extrabold text-[#292524] mb-2">📚 发现课程</h2>
          <p class="text-[#78716C]">探索适合你的学习内容</p>
        </div>
      </ScrollReveal>

      <section v-for="(sec, si) in sections" :key="si" class="mb-10">
        <ScrollReveal :delay="si * 100">
          <div class="flex items-center gap-2 mb-6">
            <component :is="sec.icon" class="w-5 h-5" :class="sec.iconClass" :stroke-width="1.75" />
            <h3 class="text-lg font-extrabold text-[#292524]">{{ warmSectionTitle(sec.title) }}</h3>
          </div>

          <!-- Skill Tree Path -->
          <div class="flex flex-col items-center gap-1">
            <div
              v-for="(c, ci) in sec.courses" :key="c.id"
              class="flex items-center gap-4 w-full max-w-md cursor-pointer group"
              :class="ci % 2 === 0 ? 'flex-row' : 'flex-row-reverse'"
              @click="$router.push(`/student/courses/${c.id}`)"
            >
              <!-- Node -->
              <div class="flex-shrink-0 relative">
                <div class="w-16 h-16 rounded-full bg-gradient-to-br flex items-center justify-center
                            shadow-[0_4px_0_rgba(0,0,0,0.1)] group-hover:scale-110 transition-transform"
                     :class="getCategoryStyle(c.category).gradient">
                  <component :is="getCategoryStyle(c.category).icon" class="w-7 h-7 text-white" :stroke-width="1.5" />
                </div>
                <!-- Connecting line -->
                <div v-if="ci < sec.courses.length - 1"
                     class="absolute top-full left-1/2 -translate-x-1/2 w-0.5 h-6 bg-[#E5E7EB]" />
              </div>
              <!-- Info Card -->
              <div class="flex-1 min-w-0 p-3 rounded-2xl border-2 border-[#E5E7EB] bg-white
                          shadow-[0_3px_0_#E5E7EB] group-hover:shadow-[0_5px_0_#D6D3D1]
                          group-hover:-translate-y-0.5 transition-all">
                <h4 class="text-sm font-bold text-[#292524] truncate">{{ c.title }}</h4>
                <p class="text-xs text-[#78716C] line-clamp-1 mt-0.5">{{ c.description }}</p>
                <div class="flex items-center gap-2 mt-2">
                  <span class="text-[10px] font-bold px-2 py-0.5 rounded-full"
                        :class="[getCategoryStyle(c.category).bgClass, getCategoryStyle(c.category).textClass]">
                    {{ c.category }}
                  </span>
                  <span class="text-[10px] text-[#A8A29E] font-medium">{{ c.enrollCount || 0 }} 人</span>
                </div>
              </div>
            </div>
          </div>
        </ScrollReveal>
      </section>
    </template>

    <!-- ================================================================
         ❄️ PRO — Vercel：紧凑表格/列表 + 可排序 + monospace
         ================================================================ -->
    <template v-else-if="!sectionsLoading">
      <ScrollReveal>
        <div class="mb-6">
          <h2 class="text-sm font-semibold text-[#0F172A] uppercase tracking-wider">发现课程</h2>
          <p class="text-xs text-[#64748B] mt-1">探索适合你的学习内容</p>
        </div>
      </ScrollReveal>

      <section v-for="(sec, si) in sections" :key="si" class="mb-6">
        <ScrollReveal :delay="si * 80">
          <div class="flex items-center gap-2 mb-3">
            <component :is="sec.icon" class="w-3.5 h-3.5 text-[#0284C7]" :stroke-width="1.75" />
            <h3 class="text-xs font-semibold text-[#0F172A] uppercase tracking-widest">{{ sec.title }}</h3>
            <span class="text-[10px] text-[#94A3B8] font-mono ml-auto">{{ sec.courses.length }} items</span>
          </div>

          <!-- Table -->
          <div class="border border-[#E2E8F0] rounded-md overflow-hidden divide-y divide-[#F1F5F9]">
            <!-- Header -->
            <div class="flex items-center gap-4 px-4 py-2 bg-[#F8FAFC] text-[10px] font-semibold text-[#64748B] uppercase tracking-wider font-mono">
              <span class="flex-1">课程名</span>
              <span class="w-20 text-center hidden sm:block">分类</span>
              <span class="w-16 text-center hidden sm:block">等级</span>
              <span class="w-12 text-right">人数</span>
            </div>
            <!-- Rows -->
            <div
              v-for="c in sec.courses" :key="c.id"
              class="flex items-center gap-4 px-4 py-2.5 hover:bg-[#F8FAFC] cursor-pointer transition-colors"
              @click="$router.push(`/student/courses/${c.id}`)"
            >
              <div class="flex items-center gap-2 flex-1 min-w-0">
                <div class="w-2 h-2 rounded-full flex-shrink-0" :style="{ backgroundColor: getCategoryStyle(c.category).hex }" />
                <span class="text-sm text-[#0F172A] truncate">{{ c.title }}</span>
              </div>
              <code class="w-20 text-center text-[10px] font-mono text-[#64748B] hidden sm:block truncate">{{ c.category }}</code>
              <code class="w-16 text-center text-[10px] font-mono px-1.5 py-0.5 rounded bg-[#F1F5F9] text-[#94A3B8] hidden sm:block">
                {{ (c.level || '').toLowerCase() }}
              </code>
              <span class="w-12 text-right text-xs font-mono text-[#64748B]">{{ c.enrollCount || 0 }}</span>
            </div>
          </div>
        </ScrollReveal>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useHead } from '@unhead/vue'
import { useThemeStore } from '@/stores/theme'
import { recommendApi } from '@/api/recommend'
import { getCategoryStyle } from '@/utils/categoryColors'
import ScrollReveal from '@/components/effects/ScrollReveal.vue'
import DashboardSection from '@/views/student/Dashboard.vue'
import { Crosshair, Flame, Zap } from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

useHead({ title: 'IT 智能培训系统 — 让学习更高效' })

/* ── Data Fetching ── */
const sections = ref<Array<{ title: string; courses: any[]; icon: any; iconClass: string }>>([])
const sectionsLoading = ref(true)

onMounted(async () => {
  const [h, p, n] = await Promise.allSettled([
    recommendApi.home(),
    recommendApi.popular(),
    recommendApi.newCourses(),
  ])
  sections.value = [
    { title: '为你推荐', icon: Crosshair, iconClass: 'text-[#635BFF]', courses: h.status === 'fulfilled' ? (h.value as any) || [] : [] },
    { title: '热门课程', icon: Flame, iconClass: 'text-[#FF9600]', courses: p.status === 'fulfilled' ? (p.value as any) || [] : [] },
    { title: '最新上线', icon: Zap, iconClass: 'text-[#1CB0F6]', courses: n.status === 'fulfilled' ? (n.value as any) || [] : [] },
  ].filter(s => s.courses.length > 0)
  sectionsLoading.value = false
})

/* ── Warm section title with emoji ── */
function warmSectionTitle(title: string) {
  const map: Record<string, string> = {
    '为你推荐': '🎯 为你推荐',
    '热门课程': '🔥 热门课程',
    '最新上线': '✨ 最新上线',
  }
  return map[title] || title
}

/* ── Divider ── */
const dividerClass = computed(() => ({
  light: 'border-t border-[#E3E8EE]',
  dark: 'border-t border-white/[0.04]',
  warm: 'border-t border-[#E7E5E4]',
  pro: 'border-t border-[#E2E8F0]',
}[theme.value] || 'border-t border-border'))
</script>

<style scoped>
/* Hide scrollbar for dark theme horizontal scroll */
.scrollbar-none::-webkit-scrollbar { display: none; }
.scrollbar-none { -ms-overflow-style: none; scrollbar-width: none; }
</style>
