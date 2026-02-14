<template>
  <div>
    <Transition name="fade" mode="out-in">
    <!-- Skeleton Loading -->
    <div v-if="loading" key="skeleton" class="animate-pulse space-y-5">
      <!-- Hero skeleton -->
      <div class="rounded-xl h-36" :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
      <!-- Grid skeleton -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="n in 6" :key="n" class="h-24 rounded-xl"
             :class="theme === 'dark' ? 'bg-white/[0.04]' : theme === 'warm' ? 'bg-[#F5F0EB]' : theme === 'pro' ? 'bg-[#F1F5F9]' : 'bg-[#F0F3F7]'" />
      </div>
    </div>

    <div v-else key="content">
    <!-- ================================================================
         ☀️ LIGHT — Xbox Achievement：大 Gamerscore Banner + 解锁网格
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-6">
        <!-- Gamerscore Hero Banner -->
        <div class="xbox-hero relative overflow-hidden rounded-xl">
          <div class="absolute inset-0 bg-gradient-to-r from-[#107C10] to-[#0E6B0E]"></div>
          <div class="absolute top-0 right-0 w-80 h-80 bg-white/5 rounded-full -translate-y-1/2 translate-x-1/3"></div>
          <div class="relative z-10 p-8 flex items-center gap-8">
            <!-- Gamerscore Circle -->
            <div class="w-24 h-24 rounded-full bg-white/10 border-4 border-white/30 flex items-center justify-center flex-shrink-0">
              <div class="text-center">
                <TrophyIcon class="w-6 h-6 text-[#FFC800] mx-auto mb-1" :stroke-width="1.75" />
                <div class="text-2xl font-bold text-white"><NumberCounter :value="pts" /></div>
              </div>
            </div>
            <div>
              <h1 class="text-xl font-bold text-white mb-1">Gamerscore</h1>
              <p class="text-sm text-white/70">{{ earnedCount }} / {{ list.length }} 个成就已解锁</p>
              <!-- Progress to next milestone -->
              <div class="mt-3 flex items-center gap-3">
                <div class="w-48 h-2 bg-white/20 rounded-full overflow-hidden">
                  <div class="h-full bg-[#FFC800] rounded-full transition-all" :style="{ width: unlockRate + '%' }"></div>
                </div>
                <span class="text-xs text-white/60 font-medium">{{ unlockRate }}%</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Achievement Grid (Xbox style cards) -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="a in list" :key="a.id"
               :class="['xbox-card group', a.earned ? 'xbox-unlocked' : 'xbox-locked']">
            <!-- Icon Area -->
            <div class="flex items-start gap-4">
              <div :class="['w-14 h-14 rounded-lg flex items-center justify-center flex-shrink-0 transition-all',
                a.earned ? 'bg-[#107C10] shadow-[0_0_12px_rgba(16,124,16,0.3)]' : 'bg-[#E3E8EE]']">
                <component :is="getAchievementIcon(a)" :class="['w-7 h-7', a.earned ? 'text-white' : 'text-[#8898AA]']" :stroke-width="1.75" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="flex items-center justify-between mb-0.5">
                  <h4 :class="['text-sm font-semibold', a.earned ? 'text-[#0A2540]' : 'text-[#8898AA]']">{{ a.name }}</h4>
                  <span class="text-xs font-bold text-[#107C10]" v-if="a.points">{{ a.points }}G</span>
                </div>
                <p :class="['text-xs line-clamp-2', a.earned ? 'text-[#425466]' : 'text-[#CBD5E1]']">{{ a.description }}</p>
                <!-- Unlock Date -->
                <div v-if="a.earned && a.earnedAt" class="flex items-center gap-1.5 mt-2 text-xs text-emerald-600">
                  <CheckCircleIcon class="w-3.5 h-3.5" :stroke-width="2" />
                  <span>{{ formatDate(a.earnedAt) }} 解锁</span>
                </div>
                <!-- Locked State -->
                <div v-if="!a.earned" class="flex items-center gap-1.5 mt-2 text-xs text-[#CBD5E1]">
                  <Lock class="w-3.5 h-3.5" :stroke-width="2" />
                  <span>未解锁</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!list.length" class="text-center py-16">
          <TrophyIcon class="w-12 h-12 text-[#8898AA]/40 mx-auto mb-3" :stroke-width="1" />
          <p class="text-sm text-[#8898AA]">暂无成就数据</p>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Steam Achievements：暗色列表 + 稀有度 + 发光边框
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <!-- Header with stats -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#EDEDED]">成就</h1>
            <p class="text-sm text-[#6B6B6E] mt-0.5">{{ earnedCount }} / {{ list.length }} ({{ unlockRate }}%)</p>
          </div>
          <div class="flex items-center gap-3">
            <!-- Overall Progress -->
            <div class="w-32 h-2 bg-white/[0.06] rounded-full overflow-hidden">
              <div class="h-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4] rounded-full" :style="{ width: unlockRate + '%' }"></div>
            </div>
            <span class="text-sm font-bold text-[#EDEDED] font-mono"><NumberCounter :value="pts" /> pts</span>
          </div>
        </div>

        <!-- Achievement List (Steam style) -->
        <div v-if="list.length" class="space-y-1.5">
          <div v-for="a in list" :key="a.id"
               :class="['steam-row flex items-center gap-4 px-4 py-3 rounded-lg transition-all',
                 a.earned ? 'steam-unlocked' : 'steam-locked']">
            <!-- Icon -->
            <div :class="['w-12 h-12 rounded-lg flex items-center justify-center flex-shrink-0',
              a.earned ? 'bg-gradient-to-br from-[#818CF8]/20 to-[#06B6D4]/20 ring-1 ring-[#818CF8]/30' : 'bg-white/[0.03]']">
              <component v-if="a.earned" :is="getAchievementIcon(a)" class="w-6 h-6 text-[#818CF8]" :stroke-width="1.75" />
              <HelpCircle v-else class="w-6 h-6 text-[#3A3A3C]" :stroke-width="1.5" />
            </div>
            <!-- Content -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <h4 :class="['text-sm font-medium', a.earned ? 'text-[#EDEDED]' : 'text-[#6B6B6E]']">{{ a.earned ? a.name : '???' }}</h4>
                <!-- Rarity Badge -->
                <span v-if="a.earned" class="text-[9px] font-bold px-1.5 py-px rounded" :class="rarityClass(a)">{{ rarityLabel(a) }}</span>
              </div>
              <p :class="['text-xs mt-0.5', a.earned ? 'text-[#6B6B6E]' : 'text-[#3A3A3C]']">
                {{ a.earned ? a.description : '继续探索以解锁此成就' }}
              </p>
            </div>
            <!-- Right: Date / Rarity % -->
            <div class="flex items-center gap-4 flex-shrink-0">
              <div v-if="a.earned" class="text-right">
                <div class="text-[10px] text-[#6B6B6E] font-mono">{{ formatDate(a.earnedAt) }}</div>
                <div class="text-[10px] font-mono" :class="rarityTextClass(a)">{{ rarityPercent(a) }}% 玩家获得</div>
              </div>
              <!-- Points -->
              <span v-if="a.points" :class="['text-xs font-mono font-semibold w-10 text-right', a.earned ? 'text-[#EDEDED]' : 'text-[#3A3A3C]']">
                +{{ a.points }}
              </span>
            </div>
          </div>
        </div>

        <div v-else class="text-center py-16 text-sm text-[#6B6B6E]">暂无成就数据</div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Duolingo：奖杯架 + 段位分层 + XP 进度条
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-6">
        <!-- XP Hero -->
        <div class="text-center py-6">
          <div class="w-20 h-20 rounded-3xl bg-[#FFC800]/10 flex items-center justify-center mx-auto mb-3">
            <TrophyIcon class="w-10 h-10 text-[#FFC800]" :stroke-width="1.75" />
          </div>
          <div class="text-4xl font-extrabold text-[#FFC800]"><NumberCounter :value="pts" /></div>
          <p class="text-sm font-bold text-[#78716C] mt-1">总 XP</p>
          <p class="text-xs text-[#A8A29E] mt-0.5">{{ earnedCount }} / {{ list.length }} 个成就已收集</p>
        </div>

        <!-- XP Progress to next tier -->
        <div class="duo-hint-card">
          <span class="text-lg mr-2">{{ nextTierEmoji }}</span>
          <div class="flex-1">
            <div class="flex items-center justify-between mb-1">
              <span class="text-sm font-bold text-[#292524]">距离 {{ nextTierName }} 还需</span>
              <span class="text-xs font-bold text-[#D97706]">{{ nextTierGap }} XP</span>
            </div>
            <div class="w-full h-3 bg-[#E5E7EB] rounded-full overflow-hidden">
              <div class="h-full rounded-full transition-all bg-gradient-to-r from-[#FFC800] to-[#FF9600]"
                   :style="{ width: tierProgress + '%' }"></div>
            </div>
          </div>
        </div>

        <!-- Trophy Shelves by Tier -->
        <div v-for="tier in trophyTiers" :key="tier.name" class="duo-shelf">
          <div class="flex items-center gap-2 mb-4">
            <span class="text-xl">{{ tier.emoji }}</span>
            <h3 class="text-base font-extrabold text-[#292524]">{{ tier.name }}</h3>
            <span class="text-xs text-[#A8A29E] font-bold">{{ tier.items.filter(a => a.earned).length }} / {{ tier.items.length }}</span>
          </div>
          <div class="grid grid-cols-3 sm:grid-cols-4 lg:grid-cols-6 gap-3">
            <div v-for="a in tier.items" :key="a.id" class="text-center group">
              <!-- Trophy Icon -->
              <div :class="['w-16 h-16 rounded-2xl mx-auto flex items-center justify-center transition-all',
                a.earned
                  ? `${tier.bgClass} shadow-[0_4px_0_${tier.shadowColor}]`
                  : 'bg-[#E5E7EB]']">
                <component v-if="a.earned" :is="getAchievementIcon(a)" class="w-8 h-8 text-white" :stroke-width="1.5" />
                <Lock v-else class="w-6 h-6 text-[#D1D5DB]" :stroke-width="1.5" />
              </div>
              <!-- Name -->
              <p :class="['text-[11px] font-bold mt-2 line-clamp-1',
                a.earned ? 'text-[#292524]' : 'text-[#D1D5DB]']">{{ a.name }}</p>
              <!-- Points -->
              <p v-if="a.earned && a.points" class="text-[10px] font-bold text-[#D97706]">+{{ a.points }} XP</p>
            </div>
          </div>
        </div>

        <div v-if="!list.length" class="text-center py-16">
          <p class="text-3xl mb-2">🏆</p>
          <p class="text-sm text-[#78716C]">还没有成就，继续加油！</p>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — GitHub Trophy：极简表格 + 分类分组 + monospace
         ================================================================ -->
    <template v-else>
      <div class="space-y-4">
        <!-- Header -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Achievements</span>
            <code class="text-[10px] font-mono text-[#94A3B8] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ earnedCount }}/{{ list.length }}</code>
          </div>
          <span class="text-xs font-mono text-[#0F172A] font-semibold"><NumberCounter :value="pts" /> pts</span>
        </div>

        <!-- Grouped Tables -->
        <div v-for="group in categoryGroups" :key="group.category" class="gh-trophy-section">
          <div class="flex items-center gap-2 mb-2">
            <div class="w-1.5 h-1.5 rounded-full bg-[#0284C7]"></div>
            <span class="text-[10px] font-semibold text-[#0F172A] uppercase tracking-wider">{{ group.category }}</span>
            <span class="text-[10px] font-mono text-[#94A3B8]">{{ group.earnedCount }}/{{ group.items.length }}</span>
          </div>
          <!-- Table -->
          <div class="border border-[#E2E8F0] rounded-md overflow-hidden">
            <!-- Header -->
            <div class="flex items-center gap-3 px-3 py-2 bg-[#F8FAFC] text-[9px] font-semibold text-[#94A3B8] uppercase tracking-wider border-b border-[#E2E8F0]">
              <span class="w-5"></span>
              <span class="flex-1">Name</span>
              <span class="w-48 hidden sm:block">Description</span>
              <span class="w-14 text-center">Status</span>
              <span class="w-12 text-right">Pts</span>
              <span class="w-24 text-right hidden sm:block">Date</span>
            </div>
            <!-- Rows -->
            <div class="divide-y divide-[#F1F5F9]">
              <div v-for="a in group.items" :key="a.id"
                   :class="['flex items-center gap-3 px-3 py-2.5 transition-colors', a.earned ? 'hover:bg-[#F8FAFC]' : 'opacity-40']">
                <!-- Icon -->
                <div class="w-5 flex justify-center">
                  <component :is="getAchievementIcon(a)" :class="['w-3.5 h-3.5', a.earned ? 'text-[#0284C7]' : 'text-[#CBD5E1]']" :stroke-width="2" />
                </div>
                <!-- Name -->
                <span :class="['flex-1 text-xs font-medium truncate', a.earned ? 'text-[#0F172A]' : 'text-[#94A3B8]']">{{ a.name }}</span>
                <!-- Description -->
                <span class="w-48 text-[11px] text-[#64748B] truncate hidden sm:block">{{ a.description }}</span>
                <!-- Status -->
                <div class="w-14 text-center">
                  <span v-if="a.earned" class="text-xs font-mono text-emerald-600 font-semibold">✓</span>
                  <span v-else class="text-xs font-mono text-[#CBD5E1]">✗</span>
                </div>
                <!-- Points -->
                <span :class="['w-12 text-right text-xs font-mono', a.earned ? 'text-[#0F172A] font-semibold' : 'text-[#CBD5E1]']">
                  {{ a.points || '—' }}
                </span>
                <!-- Date -->
                <span class="w-24 text-right text-[10px] font-mono text-[#94A3B8] hidden sm:block">
                  {{ a.earned && a.earnedAt ? formatDateMono(a.earnedAt) : '—' }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!list.length" class="text-center py-12 text-[#94A3B8] text-xs font-mono">
          // no achievements data
        </div>
      </div>
    </template>
    </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { achievementApi } from '@/api/achievement'
import NumberCounter from '@/components/effects/NumberCounter.vue'
import {
  Trophy as TrophyIcon,
  CheckCircle as CheckCircleIcon,
  Medal, Star, Flame, Target, BookOpen, Zap, Award,
  Lock, HelpCircle, Crown, Gem, Shield,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Icon Map ── */
const iconMap: Record<string, any> = {
  '初学者': BookOpen, '学习达人': Star, '打卡之星': Flame,
  '目标达成': Target, '课程大师': Award, '闪电学习': Zap,
  '坚持不懈': Shield, '连续达人': Crown, '收藏家': Gem,
}
function getAchievementIcon(a: any) {
  if (a.name && iconMap[a.name]) return iconMap[a.name]
  if (a.icon && iconMap[a.icon]) return iconMap[a.icon]
  return Medal
}

/* ── Core Data ── */
const list = ref<any[]>([])
const pts = ref(0)
const loading = ref(true)

const earnedCount = computed(() => list.value.filter(a => a.earned).length)
const unlockRate = computed(() => list.value.length ? Math.round((earnedCount.value / list.value.length) * 100) : 0)

/* ── Steam: Rarity System (Dark) ── */
function rarityPercent(a: any) {
  // Deterministic rarity based on id + points (avoids Math.random re-render flicker)
  if (!a.points) return 50
  const hash = ((a.id || 0) * 17 + (a.points || 0) * 13) % 100
  if (a.points >= 100) return (hash % 5) + 2        // 2-6% (very rare)
  if (a.points >= 50) return (hash % 15) + 10        // 10-24%
  return (hash % 30) + 30                             // 30-59%
}

function rarityLabel(a: any) {
  const pct = rarityPercent(a)
  if (pct < 10) return '🥇 稀有'
  if (pct < 25) return '🥈 少见'
  return '🥉 常见'
}

function rarityClass(a: any) {
  const pct = rarityPercent(a)
  if (pct < 10) return 'bg-[#FFD700]/15 text-[#FFD700]'
  if (pct < 25) return 'bg-[#C0C0C0]/15 text-[#C0C0C0]'
  return 'bg-[#CD7F32]/15 text-[#CD7F32]'
}

function rarityTextClass(a: any) {
  const pct = rarityPercent(a)
  if (pct < 10) return 'text-[#FFD700]'
  if (pct < 25) return 'text-[#C0C0C0]'
  return 'text-[#CD7F32]'
}

/* ── Duolingo: Trophy Tiers (Warm) ── */
const trophyTiers = computed(() => {
  const tiers = [
    { name: '钻石', emoji: '💎', min: 100, bgClass: 'bg-gradient-to-br from-[#00B4D8] to-[#0077B6]', shadowColor: '#005F8A' },
    { name: '黄金', emoji: '🏆', min: 50, bgClass: 'bg-gradient-to-br from-[#FFC800] to-[#FF9600]', shadowColor: '#E5A800' },
    { name: '白银', emoji: '🥈', min: 20, bgClass: 'bg-gradient-to-br from-[#94A3B8] to-[#64748B]', shadowColor: '#475569' },
    { name: '青铜', emoji: '🥉', min: 0, bgClass: 'bg-gradient-to-br from-[#CD7F32] to-[#A0522D]', shadowColor: '#8B4513' },
  ]
  return tiers.map(t => ({
    ...t,
    items: list.value.filter(a => {
      const p = a.points || 0
      if (t.min === 100) return p >= 100
      if (t.min === 50) return p >= 50 && p < 100
      if (t.min === 20) return p >= 20 && p < 50
      return p < 20
    }),
  })).filter(t => t.items.length > 0)
})

const tierThresholds = [
  { name: '青铜', emoji: '🥉', pts: 50 },
  { name: '白银', emoji: '🥈', pts: 150 },
  { name: '黄金', emoji: '🏆', pts: 300 },
  { name: '钻石', emoji: '💎', pts: 500 },
  { name: '大师', emoji: '👑', pts: 1000 },
]

const nextTierName = computed(() => {
  for (const t of tierThresholds) {
    if (pts.value < t.pts) return t.name
  }
  return '已满级'
})

const nextTierEmoji = computed(() => {
  for (const t of tierThresholds) {
    if (pts.value < t.pts) return t.emoji
  }
  return '👑'
})

const nextTierGap = computed(() => {
  for (const t of tierThresholds) {
    if (pts.value < t.pts) return t.pts - pts.value
  }
  return 0
})

const tierProgress = computed(() => {
  let prev = 0
  for (const t of tierThresholds) {
    if (pts.value < t.pts) {
      const range = t.pts - prev
      const current = pts.value - prev
      return Math.round((current / range) * 100)
    }
    prev = t.pts
  }
  return 100
})

/* ── GitHub: Category Groups (Pro) ── */
const categoryMap: Record<string, string> = {
  '初学者': '学习', '学习达人': '学习', '课程大师': '学习', '闪电学习': '学习',
  '打卡之星': '打卡', '坚持不懈': '打卡', '连续达人': '打卡',
  '目标达成': '社交', '收藏家': '社交',
}

const categoryGroups = computed(() => {
  const groups: Record<string, any[]> = {}
  list.value.forEach(a => {
    const cat = categoryMap[a.name] || a.category || '其他'
    if (!groups[cat]) groups[cat] = []
    groups[cat].push(a)
  })
  return Object.entries(groups).map(([category, items]) => ({
    category,
    items,
    earnedCount: items.filter(i => i.earned).length,
  }))
})

/* ── Format Utils ── */
function formatDate(d: string) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('zh-CN', { year: 'numeric', month: 'short', day: 'numeric' })
}

function formatDateMono(d: string) {
  if (!d) return '—'
  const date = new Date(d)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

/* ── Data Loading ── */
onMounted(async () => {
  try {
    const [a, e, p] = await Promise.allSettled([
      achievementApi.getAll(),
      achievementApi.getEarned(),
      achievementApi.getPoints(),
    ])
    const al: any[] = a.status === 'fulfilled' ? (a.value as any) || [] : []
    const el: any[] = e.status === 'fulfilled' ? (e.value as any) || [] : []
    const earnedMap = new Map<number, string>()
    el.forEach((x: any) => earnedMap.set(x.achievementId || x.id, x.earnedAt || ''))
    list.value = al.map(x => ({
      ...x,
      earned: earnedMap.has(x.id),
      earnedAt: earnedMap.get(x.id) || '',
    }))
    pts.value = p.status === 'fulfilled' ? (p.value as any) || 0 : 0
  } catch {
    // silently fail
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* ======== XBOX (Light) ======== */
.xbox-hero {
  box-shadow: 0 15px 35px rgba(16,124,16,0.15), 0 5px 15px rgba(0,0,0,0.06);
}
.xbox-card {
  padding: 16px;
  background: #fff;
  border-radius: 10px;
  border: 1px solid #E3E8EE;
  transition: transform 0.15s, box-shadow 0.15s, border-color 0.15s;
}
.xbox-unlocked {
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
}
.xbox-unlocked:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(16,124,16,0.12);
  border-color: #107C10;
}
.xbox-locked {
  opacity: 0.55;
  filter: grayscale(0.3);
}

/* ======== STEAM (Dark) ======== */
.steam-row {
  background: #111113;
  border: 1px solid rgba(255,255,255,0.04);
}
.steam-unlocked {
  border-color: rgba(129,140,248,0.15);
}
.steam-unlocked:hover {
  border-color: rgba(129,140,248,0.3);
  box-shadow: 0 0 20px rgba(129,140,248,0.08);
}
.steam-locked {
  opacity: 0.4;
}

/* ======== DUOLINGO (Warm) ======== */
.duo-hint-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #FFF3D6 0%, #FFECD2 100%);
  border: 2px solid #FBCF33;
  border-radius: 16px;
  box-shadow: 0 3px 0 #E5A800;
}
.duo-shelf {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
}

/* ======== GITHUB TROPHY (Pro) ======== */
.gh-trophy-section {
  margin-bottom: 8px;
}

/* Line clamp */
.line-clamp-1 {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
