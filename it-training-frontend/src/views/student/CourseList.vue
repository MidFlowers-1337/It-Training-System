<template>
  <div>
    <!-- ── Themed Tab Bar ── -->
    <div :class="tabBarWrapperClass">
      <button
        v-for="t in pageTabs" :key="t.key"
        @click="pageTab = t.key"
        :class="[tabBaseClass, pageTab === t.key ? tabActiveClass : tabIdleClass]"
      >
        <component :is="t.icon" class="w-4 h-4" :stroke-width="1.75" />
        {{ t.label }}
      </button>
    </div>

    <!-- ── Tab: 智能推荐 ── -->
    <SmartRecommendPage v-if="pageTab === 'recommend'" />

    <!-- ── Tab: 全部课程 ── -->
    <template v-else>

    <!-- ================================================================
         ☀️ LIGHT — Airbnb：大搜索框 + Filter Chips + 圆角卡片网格
         ================================================================ -->
    <template v-if="theme === 'light'">
      <!-- Airbnb Search -->
      <div class="flex justify-center mb-6">
        <div class="relative w-full max-w-xl">
          <Search class="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#8898AA]" :stroke-width="1.75" />
          <input v-model="kw" type="text" @keyup.enter="search"
            placeholder="搜索你想学的课程..."
            class="w-full pl-12 pr-4 py-3.5 rounded-full border border-[#E3E8EE] bg-white text-[#0A2540]
                   placeholder-[#8898AA] text-sm shadow-[0_2px_8px_rgba(0,0,0,0.06)]
                   focus:border-[#635BFF] focus:shadow-[0_4px_14px_rgba(99,91,255,0.15)] outline-none transition-all" />
        </div>
      </div>

      <!-- Filter Chips -->
      <div class="flex items-center gap-2 mb-6 overflow-x-auto pb-2 scrollbar-none">
        <button
          v-for="chip in filterChips" :key="chip.value"
          @click="toggleFilter(chip)"
          :class="[
            'px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all border',
            activeFilters.includes(chip.value)
              ? 'bg-[#0A2540] text-white border-transparent'
              : 'bg-white text-[#425466] border-[#E3E8EE] hover:border-[#0A2540] hover:text-[#0A2540]'
          ]"
        >
          {{ chip.label }}
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <div v-for="n in 6" :key="n" class="h-64 rounded-2xl bg-gray-50 animate-pulse" />
      </div>

      <!-- Airbnb Card Grid -->
      <div v-else-if="courses.length" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
        <div
          v-for="c in courses" :key="c.id"
          class="bg-white rounded-2xl overflow-hidden cursor-pointer group
                 hover:-translate-y-1 hover:shadow-[0_12px_40px_rgba(0,0,0,0.08)] transition-all"
          @click="$router.push(`/student/courses/${c.id}`)"
        >
          <!-- Cover (40%) -->
          <div class="h-36 bg-gradient-to-br flex items-center justify-center relative"
               :class="getCategoryStyle(c.category).gradient">
            <component :is="getCategoryStyle(c.category).icon" class="w-10 h-10 text-white/80" :stroke-width="1.25" />
            <!-- Level badge -->
            <span class="absolute top-3 left-3 px-2.5 py-1 rounded-lg bg-white/90 backdrop-blur text-[11px] font-semibold text-gray-700 shadow-sm">
              {{ lvMap[c.level] || c.level }}
            </span>
          </div>
          <!-- Content -->
          <div class="p-4">
            <h4 class="font-semibold text-[15px] text-[#0A2540] line-clamp-2 mb-1">{{ c.title }}</h4>
            <p class="text-sm text-[#425466] line-clamp-2 mb-3">{{ c.description }}</p>
            <div class="flex items-center justify-between text-sm">
              <div class="flex items-center gap-1">
                <Star class="w-3.5 h-3.5 text-[#FF5A5F] fill-[#FF5A5F]" :stroke-width="0" />
                <span class="font-semibold text-[#0A2540]">{{ (c.rating || 4.5).toFixed(1) }}</span>
                <span class="text-[#8898AA] text-xs">({{ c.enrollCount || 0 }})</span>
              </div>
              <span class="text-[#8898AA] text-xs flex items-center gap-1">
                <Clock class="w-3 h-3" :stroke-width="2" />
                {{ c.durationHours || '—' }}h
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-16 text-[#8898AA]">暂无课程</div>
    </template>

    <!-- ================================================================
         🌙 DARK — Spotify：分类轮播行 + 正方形卡片
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <!-- Spotify Search (smaller, integrated) -->
      <div class="mb-8">
        <div class="relative max-w-sm">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-600" :stroke-width="1.75" />
          <input v-model="kw" type="text" @keyup.enter="search"
            placeholder="搜索课程..."
            class="w-full pl-10 pr-3 py-2.5 rounded-full border border-white/[0.06] bg-[#111113]
                   text-[#EDEDED] placeholder-gray-600 text-sm outline-none
                   focus:border-[#818CF8] transition-all" />
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="space-y-10">
        <div v-for="n in 2" :key="n">
          <div class="h-5 w-32 rounded bg-white/[0.03] animate-pulse mb-4" />
          <div class="flex gap-4"><div v-for="m in 5" :key="m" class="w-40 h-40 rounded-lg bg-white/[0.03] animate-pulse flex-shrink-0" /></div>
        </div>
      </div>

      <template v-else-if="courses.length">
        <!-- Category sections with horizontal scroll -->
        <div v-for="(group, cat) in coursesByCategory" :key="cat" class="mb-10">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-base font-semibold text-[#EDEDED]">{{ cat }}</h3>
            <button class="text-xs text-gray-500 hover:text-[#EDEDED] transition font-mono">查看全部 →</button>
          </div>
          <div class="flex gap-4 overflow-x-auto pb-4 snap-x snap-mandatory scrollbar-none">
            <div
              v-for="c in group" :key="c.id"
              class="flex-shrink-0 w-40 cursor-pointer group snap-start"
              @click="$router.push(`/student/courses/${c.id}`)"
            >
              <!-- Square card like album cover -->
              <div class="w-40 h-40 rounded-lg bg-gradient-to-br flex items-center justify-center mb-2.5 relative overflow-hidden
                          group-hover:scale-[1.04] transition-transform"
                   :class="getCategoryStyle(c.category).gradient">
                <component :is="getCategoryStyle(c.category).icon" class="w-10 h-10 text-white/70" :stroke-width="1.25" />
                <!-- Bottom gradient overlay -->
                <div class="absolute bottom-0 inset-x-0 h-16 bg-gradient-to-t from-black/50 to-transparent" />
              </div>
              <h4 class="text-sm text-[#EDEDED] font-medium line-clamp-2 mb-0.5">{{ c.title }}</h4>
              <p class="text-[11px] text-gray-600 line-clamp-1">{{ c.description }}</p>
            </div>
          </div>
        </div>

        <!-- Category color blocks grid (Spotify-like browse) -->
        <div class="mt-6">
          <h3 class="text-base font-semibold text-[#EDEDED] mb-4">浏览分类</h3>
          <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-3">
            <router-link
              v-for="cat in spotifyCategories" :key="cat.name"
              :to="`/student/courses?category=${encodeURIComponent(cat.name)}`"
              class="h-24 rounded-lg overflow-hidden relative group cursor-pointer"
              :class="cat.bg"
            >
              <span class="absolute bottom-3 left-3 text-white font-bold text-sm">{{ cat.name }}</span>
              <component :is="cat.icon"
                class="absolute -top-2 -right-2 w-16 h-16 text-white/20 rotate-12 group-hover:rotate-[20deg] transition-transform"
                :stroke-width="1" />
            </router-link>
          </div>
        </div>
      </template>

      <div v-else class="text-center py-16 text-gray-600">暂无课程</div>
    </template>

    <!-- ================================================================
         🌅 WARM — Pinterest：瀑布流 + 不等高卡片
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <!-- Pinterest Search -->
      <div class="flex gap-3 mb-6">
        <div class="relative flex-1">
          <Search class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-[#A8A29E]" :stroke-width="1.75" />
          <input v-model="kw" type="text" @keyup.enter="search"
            placeholder="搜索课程..."
            class="w-full pl-10 pr-3 py-2.5 rounded-2xl border border-[#E7E5E4] bg-white text-[#292524]
                   placeholder-[#A8A29E] text-sm outline-none focus:border-[#D97706] transition-all" />
        </div>
        <select v-model="lv" @change="search"
          class="px-4 py-2.5 rounded-2xl border border-[#E7E5E4] bg-white text-[#292524] text-sm outline-none
                 focus:border-[#D97706] cursor-pointer">
          <option value="">全部等级</option>
          <option value="BEGINNER">入门</option>
          <option value="INTERMEDIATE">中级</option>
          <option value="ADVANCED">高级</option>
        </select>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="columns-2 md:columns-3 gap-4">
        <div v-for="n in 9" :key="n" class="break-inside-avoid mb-4 animate-pulse rounded-2xl bg-[#F5F5F4]"
             :style="{ height: `${140 + (n * 30) % 100}px` }" />
      </div>

      <!-- Masonry Grid -->
      <div v-else-if="courses.length" class="columns-2 md:columns-3 gap-4">
        <div
          v-for="(c, i) in courses" :key="c.id"
          class="break-inside-avoid mb-4 bg-white rounded-2xl border border-[#E7E5E4] overflow-hidden
                 cursor-pointer group hover:shadow-[0_2px_12px_rgba(217,119,6,0.1)] transition-all"
          @click="$router.push(`/student/courses/${c.id}`)"
        >
          <!-- Cover (variable height) -->
          <div class="bg-gradient-to-br flex items-center justify-center relative"
               :class="getCategoryStyle(c.category).gradient"
               :style="{ height: `${120 + (i * 37) % 80}px` }">
            <component :is="getCategoryStyle(c.category).icon"
                       class="w-10 h-10 text-white/70" :stroke-width="1.25" />
            <!-- Heart button (Pinterest-style) -->
            <button class="absolute top-3 right-3 w-8 h-8 rounded-full bg-white/90 flex items-center justify-center
                          opacity-0 group-hover:opacity-100 transition-opacity shadow-sm"
                    @click.stop>
              <Heart class="w-4 h-4 text-[#FF5A5F]" :stroke-width="2" />
            </button>
          </div>
          <!-- Content (不截断, 自然撑高) -->
          <div class="p-3.5">
            <h4 class="font-bold text-sm text-[#292524] mb-1">{{ c.title }}</h4>
            <p class="text-xs text-[#78716C] leading-relaxed mb-3">{{ c.description }}</p>
            <div class="flex items-center gap-2">
              <div class="w-5 h-5 rounded-full bg-[#D97706]/10 flex items-center justify-center">
                <User class="w-3 h-3 text-[#D97706]" :stroke-width="2" />
              </div>
              <span class="text-xs text-[#78716C] font-medium">{{ c.instructor || '讲师' }}</span>
              <span class="ml-auto text-xs font-bold" :class="getCategoryStyle(c.category).textClass">
                {{ lvMap[c.level] || c.level }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="text-center py-16 text-[#A8A29E]">
        <div class="text-4xl mb-3">📚</div>
        暂无课程，去探索新内容吧
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — GitHub Explore：紧凑列表 + 色点 + monospace
         ================================================================ -->
    <template v-else>
      <!-- GitHub-style filter bar -->
      <div class="flex items-center justify-between mb-4 pb-3 border-b border-[#E2E8F0]">
        <div class="relative">
          <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#94A3B8]" :stroke-width="1.75" />
          <input v-model="kw" type="text" @keyup.enter="search"
            placeholder="Search courses..."
            class="pl-8 pr-3 py-1.5 rounded-md border border-[#E2E8F0] bg-white text-[#0F172A]
                   placeholder-[#94A3B8] text-xs font-mono outline-none focus:border-[#0284C7] transition-all w-64" />
        </div>
        <div class="flex items-center gap-3">
          <select v-model="sortBy" @change="search"
            class="px-2.5 py-1.5 rounded-md border border-[#E2E8F0] bg-white text-[#64748B]
                   text-xs font-mono outline-none cursor-pointer">
            <option value="popular">Most popular</option>
            <option value="newest">Newest</option>
            <option value="name">Name A-Z</option>
          </select>
          <!-- View toggle -->
          <div class="flex border border-[#E2E8F0] rounded-md overflow-hidden">
            <button @click="proView = 'list'" :class="['px-2 py-1.5 text-xs cursor-pointer', proView === 'list' ? 'bg-[#F1F5F9] text-[#0F172A]' : 'text-[#94A3B8] hover:text-[#64748B]']">
              <List class="w-3.5 h-3.5" :stroke-width="1.75" />
            </button>
            <button @click="proView = 'grid'" :class="['px-2 py-1.5 text-xs cursor-pointer', proView === 'grid' ? 'bg-[#F1F5F9] text-[#0F172A]' : 'text-[#94A3B8] hover:text-[#64748B]']">
              <LayoutGrid class="w-3.5 h-3.5" :stroke-width="1.75" />
            </button>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="space-y-1">
        <div v-for="n in 8" :key="n" class="h-12 rounded bg-[#F1F5F9] animate-pulse" />
      </div>

      <!-- Compact List View -->
      <template v-else-if="courses.length">
        <div v-if="proView === 'list'" class="border border-[#E2E8F0] rounded-md overflow-hidden divide-y divide-[#F1F5F9]">
          <div
            v-for="c in courses" :key="c.id"
            class="flex items-center gap-4 px-4 py-3 hover:bg-[#F8FAFC] cursor-pointer transition-colors"
            @click="$router.push(`/student/courses/${c.id}`)"
          >
            <!-- Color dot -->
            <div class="w-2.5 h-2.5 rounded-full flex-shrink-0" :style="{ backgroundColor: getCategoryStyle(c.category).hex }" />
            <!-- Name + desc -->
            <div class="flex-1 min-w-0">
              <span class="text-sm text-[#0F172A] font-medium">{{ c.title }}</span>
              <span class="text-xs text-[#94A3B8] ml-2 hidden sm:inline">{{ c.description?.slice(0, 60) }}</span>
            </div>
            <!-- Tags -->
            <code class="text-[10px] font-mono px-1.5 py-0.5 rounded bg-[#F1F5F9] text-[#64748B] flex-shrink-0">
              {{ (c.level || '').toLowerCase() }}
            </code>
            <!-- Stars -->
            <span class="text-xs text-[#64748B] flex items-center gap-1 flex-shrink-0 font-mono">
              <Star class="w-3 h-3 text-amber-400 fill-amber-400" :stroke-width="0" />
              {{ (c.rating || 4.5).toFixed(1) }}
            </span>
            <!-- Users -->
            <span class="text-xs text-[#94A3B8] font-mono flex-shrink-0 w-12 text-right">
              {{ c.enrollCount || 0 }}
            </span>
          </div>
        </div>

        <!-- Grid View (compact) -->
        <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-3">
          <div
            v-for="c in courses" :key="c.id"
            class="rounded-md border border-[#E2E8F0] overflow-hidden
                   hover:border-[#0284C7]/30 hover:shadow-[0_4px_12px_rgba(2,132,199,0.08)]
                   cursor-pointer transition-all"
            @click="$router.push(`/student/courses/${c.id}`)"
          >
            <div class="h-20 bg-gradient-to-br flex items-center justify-center"
                 :class="getCategoryStyle(c.category).gradient">
              <component :is="getCategoryStyle(c.category).icon" class="w-6 h-6 text-white/80" :stroke-width="1.25" />
            </div>
            <div class="p-3 bg-white">
              <h4 class="text-xs font-medium text-[#0F172A] line-clamp-2 mb-1">{{ c.title }}</h4>
              <div class="flex items-center justify-between text-[10px] font-mono text-[#94A3B8]">
                <span>{{ c.categoryName || c.category }}</span>
                <span>{{ c.enrollCount || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <div v-else class="text-center py-12 text-[#94A3B8] text-xs font-mono">No courses found.</div>

      <!-- Pagination (Pro: monospace small) -->
      <div v-if="total > pageSize && courses.length" class="flex justify-center mt-6 font-mono text-xs">
        <div class="flex items-center gap-1 border border-[#E2E8F0] rounded-md overflow-hidden">
          <button @click="page > 1 && goTo(page - 1)" :disabled="page <= 1"
            class="px-3 py-1.5 text-[#64748B] hover:bg-[#F8FAFC] disabled:opacity-30 transition cursor-pointer">
            Previous
          </button>
          <span class="px-3 py-1.5 border-x border-[#E2E8F0] text-[#64748B]">
            {{ page }} / {{ totalPages }}
          </span>
          <button @click="page < totalPages && goTo(page + 1)" :disabled="page >= totalPages"
            class="px-3 py-1.5 text-[#64748B] hover:bg-[#F8FAFC] disabled:opacity-30 transition cursor-pointer">
            Next
          </button>
        </div>
      </div>
    </template>

    <!-- Pagination (Light/Warm) -->
    <div v-if="theme !== 'pro' && theme !== 'dark' && total > pageSize && courses.length" class="flex justify-center mt-8">
      <div class="flex items-center gap-1">
        <button
          v-for="p in totalPages" :key="p" @click="goTo(p)"
          :class="[
            'w-8 h-8 rounded-full text-xs font-medium transition-all',
            page === p
              ? theme === 'warm' ? 'bg-[#D97706] text-white shadow-[0_3px_0_#B45309]' : 'bg-[#0A2540] text-white'
              : theme === 'warm' ? 'text-[#78716C] hover:bg-[#FEF3C7]' : 'text-[#8898AA] hover:bg-gray-50'
          ]"
        >{{ p }}</button>
      </div>
    </div>

    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useHead } from '@unhead/vue'
import { useThemeStore } from '@/stores/theme'
import { courseApi } from '@/api/course'
import { getCategoryStyle } from '@/utils/categoryColors'
import SmartRecommendPage from '@/views/student/SmartRecommend.vue'
import {
  BookOpen, Sparkles, Search, Clock, Star, Heart, User,
  List, LayoutGrid,
  Terminal, Code2, Globe, Database, Brain,
  Server, Smartphone, Shield, BarChart3, Blocks,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

useHead({ title: '课程浏览 — IT 智能培训系统' })

/* ── Page-level Tab ── */
const pageTabs = [
  { key: 'all', label: '全部课程', icon: BookOpen },
  { key: 'recommend', label: '智能推荐', icon: Sparkles },
]
const pageTab = ref('all')

/* ── Theme-aware Tab Styles ── */
const tabBarWrapperClass = computed(() => ({
  light: 'flex gap-1 mb-6 pb-3 border-b border-[#E3E8EE]',
  dark: 'flex gap-1 mb-6 pb-3 border-b border-white/[0.04]',
  warm: 'flex gap-1.5 mb-6 pb-3 border-b border-[#E7E5E4]',
  pro: 'flex gap-0.5 mb-6 pb-3 border-b border-white/[0.04]',
}[theme.value] || 'flex gap-1 mb-6 pb-3 border-b border-border'))

const tabBaseClass = 'inline-flex items-center gap-1.5 px-3.5 py-2 text-[13px] font-medium border-none cursor-pointer transition-all'

const tabActiveClass = computed(() => ({
  light: 'bg-[#635BFF]/10 text-[#635BFF] font-semibold rounded-md',
  dark: 'bg-[#818CF8]/10 text-[#818CF8] font-semibold rounded-lg',
  warm: 'bg-[#D97706]/10 text-[#92400E] font-semibold rounded-full',
  pro: 'text-[#818CF8] font-medium border-b-2 border-[#818CF8] rounded-none pb-2.5',
}[theme.value] || 'bg-primary/10 text-primary rounded-md'))

const tabIdleClass = computed(() => ({
  light: 'text-[#425466] hover:bg-[#F6F9FC] hover:text-[#0A2540] rounded-md',
  dark: 'text-[#6B6B6E] hover:bg-white/[0.03] hover:text-[#A0A0A5] rounded-lg',
  warm: 'text-[#78716C] hover:bg-[#FEF3C7]/50 hover:text-[#292524] rounded-full',
  pro: 'text-gray-600 hover:text-gray-400 rounded-none',
}[theme.value] || 'text-text-secondary hover:bg-surface-alt rounded-md'))

/* ── Data ── */
const kw = ref('')
const lv = ref('')
const sortBy = ref('popular')
const proView = ref<'list' | 'grid'>('list')
const courses = ref<any[]>([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
const lvMap: Record<string, string> = { BEGINNER: '入门', INTERMEDIATE: '中级', ADVANCED: '高级' }

/* ── Light: Filter Chips ── */
const activeFilters = ref<string[]>([])
const filterChips = [
  { label: '全部', value: '' },
  { label: '入门', value: 'BEGINNER' },
  { label: '中级', value: 'INTERMEDIATE' },
  { label: '高级', value: 'ADVANCED' },
  { label: '后端', value: 'backend' },
  { label: '前端', value: 'frontend' },
  { label: '数据库', value: 'database' },
  { label: 'AI', value: 'ai' },
]
function toggleFilter(chip: { value: string }) {
  if (chip.value === '') {
    activeFilters.value = []
    lv.value = ''
  } else if (['BEGINNER', 'INTERMEDIATE', 'ADVANCED'].includes(chip.value)) {
    lv.value = lv.value === chip.value ? '' : chip.value
    activeFilters.value = lv.value ? [lv.value] : []
  }
  search()
}

/* ── Dark: Group courses by category ── */
const coursesByCategory = computed(() => {
  const groups: Record<string, any[]> = {}
  for (const c of courses.value) {
    const cat = c.categoryName || c.category || '其他'
    if (!groups[cat]) groups[cat] = []
    groups[cat].push(c)
  }
  return groups
})

/* ── Dark: Spotify category blocks ── */
const spotifyCategories = [
  { name: '后端开发', icon: Server, bg: 'bg-gradient-to-br from-indigo-600 to-blue-700' },
  { name: '前端开发', icon: Globe, bg: 'bg-gradient-to-br from-emerald-600 to-teal-700' },
  { name: '数据库',   icon: Database, bg: 'bg-gradient-to-br from-purple-600 to-violet-700' },
  { name: 'AI / ML',  icon: Brain, bg: 'bg-gradient-to-br from-rose-600 to-pink-700' },
  { name: 'Java',     icon: Terminal, bg: 'bg-gradient-to-br from-amber-600 to-orange-700' },
  { name: 'Python',   icon: Code2, bg: 'bg-gradient-to-br from-blue-600 to-cyan-700' },
  { name: '网络安全', icon: Shield, bg: 'bg-gradient-to-br from-red-600 to-rose-700' },
  { name: 'DevOps',   icon: Blocks, bg: 'bg-gradient-to-br from-slate-600 to-zinc-700' },
]

/* ── Fetch ── */
async function fetchCourses() {
  loading.value = true
  try {
    const r: any = await courseApi.listPublished({ page: page.value, size: pageSize.value, keyword: kw.value || undefined, level: lv.value || undefined })
    courses.value = r?.records || r?.content || r || []
    total.value = r?.total || r?.totalElements || courses.value.length
  } catch {} finally { loading.value = false }
}
function search() { page.value = 1; fetchCourses() }
function goTo(p: number) { page.value = p; fetchCourses() }
onMounted(fetchCourses)
</script>

<style scoped>
/* Hide scrollbar for chips & horizontal scroll */
.scrollbar-none::-webkit-scrollbar { display: none; }
.scrollbar-none { -ms-overflow-style: none; scrollbar-width: none; }
</style>
