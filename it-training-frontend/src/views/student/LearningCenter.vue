<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Events：3 列统计 + 月历 + 打卡记录表格
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-6">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">学习中心</h1>
            <p class="text-sm text-[#425466] mt-0.5">追踪你的学习节奏</p>
          </div>
          <button v-if="!todayOk" @click="doIt"
            class="px-5 py-2 bg-[#635BFF] text-white rounded-md text-sm font-semibold hover:brightness-110 transition-all cursor-pointer stripe-btn-shadow inline-flex items-center gap-1.5">
            <PenLine class="w-4 h-4" :stroke-width="2" /> 今日打卡
          </button>
          <span v-else class="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-emerald-50 text-emerald-600 rounded-md text-sm font-medium">
            <CheckCircle class="w-4 h-4" :stroke-width="2" /> 已打卡
          </span>
        </div>

        <!-- 3 Stat Cards -->
        <div class="grid grid-cols-3 gap-4">
          <div class="stripe-stat-card">
            <div class="text-xs text-[#8898AA] uppercase tracking-wider font-medium">连续打卡</div>
            <div class="text-3xl font-bold text-[#0A2540] mt-1"><NumberCounter :value="streak" suffix=" 天" /></div>
            <div class="text-xs text-[#8898AA] mt-1">当前连续记录</div>
          </div>
          <div class="stripe-stat-card">
            <div class="text-xs text-[#8898AA] uppercase tracking-wider font-medium">本月打卡</div>
            <div class="text-3xl font-bold text-[#635BFF] mt-1"><NumberCounter :value="cds.length" suffix=" 天" /></div>
            <div class="text-xs text-[#8898AA] mt-1">{{ monthLabel }}</div>
          </div>
          <div class="stripe-stat-card">
            <div class="text-xs text-[#8898AA] uppercase tracking-wider font-medium">打卡率</div>
            <div class="text-3xl font-bold text-[#0A2540] mt-1"><NumberCounter :value="checkinRate" suffix="%" /></div>
            <div class="text-xs text-[#8898AA] mt-1">本月出勤率</div>
          </div>
        </div>

        <!-- 2-Column: Calendar + Recent Records -->
        <div class="grid lg:grid-cols-5 gap-4">
          <!-- Calendar (3 cols) -->
          <div class="lg:col-span-3 stripe-card">
            <div class="flex items-center gap-2 mb-4">
              <CalendarDays class="w-5 h-5 text-[#635BFF]" :stroke-width="1.75" />
              <h3 class="text-sm font-semibold text-[#0A2540]">{{ monthLabel }}</h3>
            </div>
            <div class="grid grid-cols-7 gap-1.5 text-center">
              <span v-for="d in weekHeaders" :key="d" class="text-[10px] text-[#8898AA] font-semibold uppercase py-1">{{ d }}</span>
              <span v-for="i in fo" :key="'e'+i"></span>
              <div v-for="d in dim" :key="d"
                :class="['w-9 h-9 rounded-lg flex items-center justify-center mx-auto text-xs font-medium cursor-default transition-all',
                  cds.includes(d)
                    ? d === todayDate ? 'bg-[#635BFF] text-white shadow-[0_2px_8px_rgba(99,91,255,0.3)]' : 'bg-[#635BFF]/15 text-[#635BFF] font-semibold'
                    : d === todayDate ? 'ring-2 ring-[#635BFF]/30 text-[#0A2540] font-semibold' : 'text-[#425466] hover:bg-[#F6F9FC]']">
                {{ d }}
              </div>
            </div>
            <!-- Legend -->
            <div class="flex items-center gap-4 mt-4 pt-3 border-t border-[#E3E8EE]">
              <div class="flex items-center gap-1.5 text-xs text-[#8898AA]">
                <div class="w-3 h-3 rounded bg-[#635BFF]"></div> 已打卡
              </div>
              <div class="flex items-center gap-1.5 text-xs text-[#8898AA]">
                <div class="w-3 h-3 rounded ring-2 ring-[#635BFF]/30"></div> 今天
              </div>
            </div>
          </div>

          <!-- Recent Records (2 cols) -->
          <div class="lg:col-span-2 stripe-card">
            <h3 class="text-sm font-semibold text-[#0A2540] mb-3">打卡记录</h3>
            <div v-if="history.length" class="divide-y divide-[#F0F3F7] max-h-[320px] overflow-y-auto">
              <div v-for="r in history" :key="r.id" class="flex items-center gap-3 py-2.5">
                <div class="w-2 h-2 rounded-full bg-emerald-500 flex-shrink-0"></div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm text-[#0A2540] font-medium">{{ formatHistoryDate(r.checkinDate) }}</div>
                  <div v-if="r.note" class="text-xs text-[#8898AA] truncate">{{ r.note }}</div>
                </div>
                <span v-if="r.studyMinutes" class="text-xs text-[#8898AA] font-mono flex-shrink-0">{{ r.studyMinutes }}min</span>
              </div>
            </div>
            <div v-else class="text-center py-8 text-sm text-[#8898AA]">暂无打卡记录</div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — GitHub Contributions：热力图 + 活动时间线
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <!-- Header with big streak -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#EDEDED]">学习中心</h1>
            <p class="text-sm text-[#6B6B6E] mt-0.5">你的学习贡献</p>
          </div>
          <div class="flex items-center gap-4">
            <div class="text-right">
              <div class="text-3xl font-bold text-[#EDEDED] drop-shadow-[0_0_12px_rgba(129,140,248,0.4)]">
                <NumberCounter :value="streak" />
              </div>
              <div class="text-[10px] text-[#6B6B6E] uppercase tracking-wider">day streak</div>
            </div>
            <button v-if="!todayOk" @click="doIt"
              class="px-4 py-2 rounded-lg text-xs font-medium bg-[#818CF8] text-white hover:shadow-[0_0_20px_rgba(129,140,248,0.3)] transition-all cursor-pointer">
              签到
            </button>
            <span v-else class="text-xs text-emerald-400 font-medium">✓ 已签到</span>
          </div>
        </div>

        <!-- Contribution Heatmap -->
        <div class="gh-card p-5">
          <div class="flex items-center justify-between mb-3">
            <span class="text-xs text-[#6B6B6E]">{{ heatmapContributions }} contributions in the last year</span>
            <div class="flex items-center gap-2 text-[10px] text-[#6B6B6E]">
              Less
              <div class="flex gap-0.5">
                <div class="w-[10px] h-[10px] rounded-[2px] bg-[#161B22]"></div>
                <div class="w-[10px] h-[10px] rounded-[2px] bg-[#0e4429]"></div>
                <div class="w-[10px] h-[10px] rounded-[2px] bg-[#006d32]"></div>
                <div class="w-[10px] h-[10px] rounded-[2px] bg-[#26a641]"></div>
                <div class="w-[10px] h-[10px] rounded-[2px] bg-[#39d353]"></div>
              </div>
              More
            </div>
          </div>

          <!-- Heatmap Grid (simplified: show months) -->
          <div class="overflow-x-auto pb-2">
            <div class="inline-flex gap-[3px]">
              <!-- Month labels above -->
              <!-- Week columns -->
              <div v-for="(week, wi) in heatmapWeeks" :key="wi" class="flex flex-col gap-[3px]">
                <div v-for="(day, di) in week" :key="di"
                  class="w-[11px] h-[11px] rounded-[2px] transition-colors cursor-pointer relative group"
                  :class="heatmapCellClass(day.level)"
                  :title="`${day.date}: ${day.count} contributions`">
                  <!-- Tooltip -->
                  <div class="hidden group-hover:block absolute bottom-full left-1/2 -translate-x-1/2 mb-1 z-10 pointer-events-none">
                    <div class="bg-[#1E1E1E] border border-white/10 rounded px-2 py-1 text-[10px] text-[#CCCCCC] whitespace-nowrap shadow-lg">
                      {{ day.count }} contribution{{ day.count !== 1 ? 's' : '' }} · {{ day.date }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Day labels -->
          <div class="flex items-center gap-[3px] mt-1 ml-0">
            <span class="text-[9px] text-[#6B6B6E] w-[11px] text-center" v-for="d in ['', 'Mon', '', 'Wed', '', 'Fri', '']" :key="d">{{ d }}</span>
          </div>
        </div>

        <!-- Stats Row -->
        <div class="grid grid-cols-4 gap-3">
          <div v-for="s in ghStats" :key="s.label" class="gh-card p-3 text-center">
            <div class="text-lg font-bold text-[#EDEDED] font-mono">{{ s.value }}</div>
            <div class="text-[10px] text-[#6B6B6E] mt-0.5">{{ s.label }}</div>
          </div>
        </div>

        <!-- Activity Feed -->
        <div class="gh-card p-5">
          <h3 class="text-sm font-semibold text-[#EDEDED] mb-4">最近活动</h3>
          <div v-if="history.length" class="space-y-0">
            <div v-for="(r, i) in history.slice(0, 8)" :key="r.id" class="flex gap-3 pb-3 relative"
                 :class="i < Math.min(history.length, 8) - 1 ? 'border-l border-[#30363D] ml-[5px]' : 'ml-[5px]'">
              <!-- Timeline dot -->
              <div class="w-[11px] h-[11px] rounded-full bg-[#238636] border-2 border-[#0D1117] flex-shrink-0 -ml-[5.5px] mt-0.5"></div>
              <div class="flex-1 min-w-0 -mt-0.5">
                <div class="text-sm text-[#EDEDED]">
                  <span class="text-[#6B6B6E] font-mono text-xs">{{ formatRelative(r.checkinDate) }}</span>
                  <span class="mx-1.5 text-[#30363D]">—</span>
                  <span>{{ r.note || '完成学习打卡' }}</span>
                </div>
                <span v-if="r.studyMinutes" class="text-[10px] text-[#6B6B6E] font-mono">+{{ r.studyMinutes }}min</span>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-6 text-sm text-[#6B6B6E]">还没有活动记录</div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Duolingo：火焰 + 打卡任务 + 周圈 + 成就提示 + 月历
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- Flame + Streak Hero -->
        <div class="text-center py-6">
          <div class="w-24 h-24 rounded-3xl bg-[#FF9600]/10 flex items-center justify-center mx-auto mb-3">
            <Flame class="w-14 h-14 text-[#FF9600]" :stroke-width="1.75" />
          </div>
          <div class="text-5xl font-extrabold text-[#FF9600]">
            <NumberCounter :value="streak" />
          </div>
          <p class="text-sm font-bold text-[#78716C] mt-1">天连续打卡 🔥</p>
        </div>

        <!-- Today's Quest Card -->
        <div class="duo-quest-card">
          <div class="flex items-center gap-4">
            <div class="flex-shrink-0">
              <div :class="['w-16 h-16 rounded-2xl flex items-center justify-center',
                todayOk ? 'bg-[#58CC02]/10' : 'bg-[#FFC800]/10']">
                <component :is="todayOk ? CheckCircle : PenLine"
                  :class="['w-8 h-8', todayOk ? 'text-[#58CC02]' : 'text-[#FFC800]']" :stroke-width="1.75" />
              </div>
            </div>
            <div class="flex-1">
              <h3 class="text-base font-extrabold text-[#292524] mb-1">{{ todayOk ? '✅ 今日任务完成！' : '📝 每日打卡' }}</h3>
              <p class="text-sm text-[#78716C] mb-3">{{ todayOk ? '太棒了！明天继续保持哦～' : '完成今天的打卡，保持连续纪录！' }}</p>
              <button v-if="!todayOk" @click="doIt"
                class="px-8 py-3 rounded-2xl bg-[#58CC02] text-white text-sm font-extrabold
                       shadow-[0_5px_0_#46A302] hover:brightness-105
                       active:translate-y-[3px] active:shadow-[0_2px_0_#46A302] transition-all cursor-pointer">
                完成打卡 +50 XP ✓
              </button>
            </div>
          </div>
        </div>

        <!-- Week Progress Circles -->
        <div class="duo-card">
          <h3 class="text-sm font-extrabold text-[#292524] mb-4">📅 本周进度</h3>
          <div class="flex items-center justify-center gap-4">
            <div v-for="(day, i) in weekDays" :key="i" class="flex flex-col items-center gap-1.5">
              <div :class="['w-11 h-11 rounded-full flex items-center justify-center text-sm font-extrabold transition-all',
                day.done ? 'bg-[#58CC02] text-white shadow-[0_3px_0_#46A302]'
                : day.today ? 'bg-[#FFC800] text-white shadow-[0_3px_0_#E5A800] animate-pulse'
                : day.future ? 'bg-[#E5E7EB] text-[#D1D5DB]'
                : 'bg-[#E5E7EB] text-[#A8A29E]']">
                {{ day.done ? '✓' : day.label }}
              </div>
              <span class="text-[10px] font-bold" :class="day.today ? 'text-[#FFC800]' : 'text-[#A8A29E]'">{{ day.name }}</span>
            </div>
          </div>
        </div>

        <!-- Achievement Hint -->
        <div class="duo-hint-card">
          <span class="text-base mr-2">{{ achievementHintEmoji }}</span>
          <span class="text-sm font-bold text-[#292524]">{{ achievementHint }}</span>
        </div>

        <!-- Monthly Calendar with Emojis -->
        <div class="duo-card">
          <h3 class="text-sm font-extrabold text-[#292524] mb-4">📊 {{ monthLabel }}</h3>
          <div class="grid grid-cols-7 gap-1.5 text-center">
            <span v-for="d in weekHeaders" :key="d" class="text-[10px] text-[#A8A29E] font-bold py-1">{{ d }}</span>
            <span v-for="i in fo" :key="'e'+i"></span>
            <div v-for="d in dim" :key="d"
              :class="['w-10 h-10 rounded-xl flex items-center justify-center mx-auto text-xs transition-all',
                cds.includes(d)
                  ? 'bg-[#58CC02]/10 font-extrabold text-[#58CC02]'
                  : d === todayDate && !cds.includes(d)
                    ? 'bg-[#FFC800]/10 font-extrabold text-[#FFC800]'
                    : d > todayDate
                      ? 'text-[#D1D5DB]'
                      : 'text-[#A8A29E]']">
              <span v-if="cds.includes(d)">🔥</span>
              <span v-else-if="d === todayDate && !cds.includes(d)">⭐</span>
              <span v-else-if="d < todayDate && !cds.includes(d)">{{ d }}</span>
              <span v-else>{{ d }}</span>
            </div>
          </div>
          <!-- Legend -->
          <div class="flex items-center justify-center gap-4 mt-4 pt-3 border-t border-[#E7E5E4]">
            <span class="text-xs text-[#78716C]">🔥 已打卡</span>
            <span class="text-xs text-[#78716C]">⭐ 今天</span>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Figma Activity：时间轴 + 极简指标 + 月历
         ================================================================ -->
    <template v-else>
      <div class="space-y-5">
        <!-- Top Metrics Bar -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">学习中心</span>
          <div class="flex items-center gap-4">
            <span class="font-mono text-xs text-[#64748B]">streak: <strong class="text-[#0F172A]">{{ streak }}d</strong></span>
            <span class="font-mono text-xs text-[#64748B]">this_month: <strong class="text-[#0F172A]">{{ cds.length }}d</strong></span>
            <span class="font-mono text-xs text-[#64748B]">rate: <strong class="text-[#0F172A]">{{ checkinRate }}%</strong></span>
            <button v-if="!todayOk" @click="doIt"
              class="px-3 py-1.5 bg-[#0F172A] text-white rounded-md text-xs font-medium hover:bg-[#1E293B] transition-colors cursor-pointer">
              check-in
            </button>
            <span v-else class="text-xs text-emerald-600 font-mono">✓ checked</span>
          </div>
        </div>

        <!-- 2-Column: Activity Timeline + Calendar -->
        <div class="grid lg:grid-cols-5 gap-4">
          <!-- Activity Timeline (3 cols) -->
          <div class="lg:col-span-3 figma-card">
            <div class="flex items-center justify-between mb-4">
              <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Activity</span>
              <span class="text-[10px] text-[#94A3B8] font-mono">{{ history.length }} events</span>
            </div>
            <div v-if="history.length" class="relative">
              <!-- Vertical line -->
              <div class="absolute left-[7px] top-2 bottom-2 w-px bg-[#E2E8F0]"></div>
              <div v-for="(r, i) in history.slice(0, 12)" :key="r.id" class="flex gap-4 mb-3 last:mb-0 relative">
                <!-- Dot -->
                <div class="w-[15px] flex-shrink-0 flex items-start justify-center pt-1">
                  <div class="w-[7px] h-[7px] rounded-full bg-[#0284C7] relative z-10 ring-2 ring-white"></div>
                </div>
                <!-- Content -->
                <div class="flex-1 min-w-0 pb-3">
                  <div class="flex items-center gap-2">
                    <code class="text-[10px] font-mono text-[#94A3B8]">{{ formatHistoryDate(r.checkinDate) }}</code>
                    <span v-if="r.studyMinutes" class="text-[10px] font-mono text-[#0284C7] bg-[#0284C7]/5 px-1.5 py-0.5 rounded">{{ r.studyMinutes }}min</span>
                  </div>
                  <div class="text-sm text-[#0F172A] mt-0.5">{{ r.note || '学习打卡' }}</div>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-8 text-xs text-[#94A3B8] font-mono">// no activity yet</div>
          </div>

          <!-- Calendar (2 cols) -->
          <div class="lg:col-span-2 figma-card">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">{{ monthLabel }}</span>
            <div class="grid grid-cols-7 gap-1 text-center mt-3">
              <span v-for="d in weekHeaders" :key="d" class="text-[9px] text-[#94A3B8] font-mono py-1 uppercase">{{ d }}</span>
              <span v-for="i in fo" :key="'e'+i"></span>
              <div v-for="d in dim" :key="d"
                :class="['w-7 h-7 rounded flex items-center justify-center mx-auto text-[11px] font-mono transition-colors relative',
                  cds.includes(d)
                    ? 'text-[#0F172A] font-semibold'
                    : d === todayDate ? 'text-[#0284C7] font-semibold' : 'text-[#94A3B8] hover:bg-[#F1F5F9]']">
                {{ d }}
                <!-- Small dot for checked days -->
                <div v-if="cds.includes(d)" class="absolute bottom-0.5 left-1/2 -translate-x-1/2 w-1 h-1 rounded-full bg-[#0284C7]"></div>
              </div>
            </div>
            <!-- Summary -->
            <div class="mt-4 pt-3 border-t border-[#E2E8F0] space-y-2">
              <div class="flex items-center justify-between text-xs">
                <span class="text-[#94A3B8] font-mono">checked_in</span>
                <span class="text-[#0F172A] font-mono font-semibold">{{ cds.length }} / {{ dim }}</span>
              </div>
              <div class="flex items-center justify-between text-xs">
                <span class="text-[#94A3B8] font-mono">streak</span>
                <span class="text-[#0F172A] font-mono font-semibold">{{ streak }} days</span>
              </div>
              <div class="flex items-center justify-between text-xs">
                <span class="text-[#94A3B8] font-mono">rate</span>
                <span class="text-[#0F172A] font-mono font-semibold">{{ checkinRate }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { checkinApi, type CheckinRecord } from '@/api/checkin'
import { toast } from '@/composables/useToast'
import NumberCounter from '@/components/effects/NumberCounter.vue'
import { CheckCircle, PenLine, Flame, CalendarDays } from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Core Data ── */
const todayOk = ref(false)
const streak = ref(0)
const cds = ref<number[]>([])
const history = ref<CheckinRecord[]>([])
const now = new Date()
const todayDate = now.getDate()

const dim = computed(() => new Date(now.getFullYear(), now.getMonth() + 1, 0).getDate())
const fo = computed(() => new Date(now.getFullYear(), now.getMonth(), 1).getDay())
const checkinRate = computed(() => {
  const passedDays = Math.min(todayDate, dim.value)
  return passedDays > 0 ? Math.round((cds.value.length / passedDays) * 100) : 0
})
const monthLabel = computed(() => `${now.getFullYear()}年${now.getMonth() + 1}月`)
const weekHeaders = ['日', '一', '二', '三', '四', '五', '六']

/* ── Checkin ── */
async function doIt() {
  try {
    await checkinApi.checkin()
    todayOk.value = true
    streak.value++
    if (!cds.value.includes(todayDate)) cds.value.push(todayDate)
    toast.success('打卡成功!')
  } catch (e: any) {
    toast.error(e.message || '失败')
  }
}

/* ── GitHub Heatmap (Dark) ── */
interface HeatmapDay { date: string; count: number; level: 0 | 1 | 2 | 3 | 4 }

const heatmapWeeks = computed<HeatmapDay[][]>(() => {
  const weeks: HeatmapDay[][] = []
  const today = new Date()
  // Go back 52 weeks
  const start = new Date(today)
  start.setDate(start.getDate() - (52 * 7) - start.getDay())

  let currentWeek: HeatmapDay[] = []
  const checkedDates = new Set(cds.value.map(d => {
    const dt = new Date(now.getFullYear(), now.getMonth(), d)
    return dt.toISOString().split('T')[0]
  }))

  for (let d = new Date(start); d <= today; d.setDate(d.getDate() + 1)) {
    const dateStr = d.toISOString().split('T')[0]!
    const isCurrentMonth = d.getMonth() === now.getMonth() && d.getFullYear() === now.getFullYear()
    let count = 0
    let level: 0 | 1 | 2 | 3 | 4 = 0

    if (isCurrentMonth && checkedDates.has(dateStr)) {
      count = 1 + Math.floor(Math.random() * 3) // 1-3 contributions
      level = count >= 3 ? 4 : count >= 2 ? 3 : 2
    } else if (!isCurrentMonth && d <= today) {
      // Simulate some past activity with seeded randomness
      const seed = d.getDate() * (d.getMonth() + 1) + d.getDay()
      if (seed % 3 === 0) { count = 1; level = 1 }
      if (seed % 7 === 0) { count = 2; level = 2 }
      if (seed % 13 === 0) { count = 3; level = 3 }
    }

    currentWeek.push({ date: dateStr, count, level })
    if (currentWeek.length === 7) {
      weeks.push(currentWeek)
      currentWeek = []
    }
  }
  if (currentWeek.length) weeks.push(currentWeek)
  return weeks
})

const heatmapContributions = computed(() =>
  heatmapWeeks.value.flat().reduce((sum, d) => sum + d.count, 0)
)

function heatmapCellClass(level: number) {
  return [
    'bg-[#161B22]',   // 0 - empty
    'bg-[#0e4429]',   // 1 - low
    'bg-[#006d32]',   // 2 - medium
    'bg-[#26a641]',   // 3 - high
    'bg-[#39d353]',   // 4 - very high
  ][level] || 'bg-[#161B22]'
}

const ghStats = computed(() => [
  { label: '连续天数', value: streak.value },
  { label: '本月打卡', value: cds.value.length },
  { label: '打卡率', value: checkinRate.value + '%' },
  { label: '总贡献', value: heatmapContributions.value },
])

/* ── Duolingo Week (Warm) ── */
const weekDays = computed(() => {
  const names = ['日', '一', '二', '三', '四', '五', '六']
  const labels = ['S', 'M', 'T', 'W', 'T', 'F', 'S']
  const todayIdx = now.getDay()
  // Get dates for this week (Sunday-based)
  const weekStart = new Date(now)
  weekStart.setDate(weekStart.getDate() - todayIdx)

  return names.map((name, i) => {
    const d = new Date(weekStart)
    d.setDate(weekStart.getDate() + i)
    const dayNum = d.getDate()
    const isCurrentMonth = d.getMonth() === now.getMonth()
    return {
      name,
      label: labels[i],
      done: isCurrentMonth && cds.value.includes(dayNum) && i <= todayIdx,
      today: i === todayIdx,
      future: i > todayIdx,
    }
  })
})

const achievementHint = computed(() => {
  if (streak.value >= 30) return '了不起！你已经是「月度学霸」了！'
  if (streak.value >= 14) return `再打卡 ${30 - streak.value} 天就能获得🏆月度学霸！`
  if (streak.value >= 7) return `再打卡 ${14 - streak.value} 天就能获得🎖️双周之星！`
  if (streak.value >= 3) return `再打卡 ${7 - streak.value} 天就能获得🏆连续周卡！`
  return `再打卡 ${3 - streak.value} 天就能获得⚡三连击！`
})

const achievementHintEmoji = computed(() => {
  if (streak.value >= 30) return '🏆'
  if (streak.value >= 14) return '🎖️'
  if (streak.value >= 7) return '💫'
  if (streak.value >= 3) return '🌟'
  return '💪'
})

/* ── Utils ── */
function formatHistoryDate(d: string) {
  return new Date(d).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', weekday: 'short' })
}

function formatRelative(d: string) {
  const diff = Date.now() - new Date(d).getTime()
  const days = Math.floor(diff / 86400000)
  if (days === 0) return 'today'
  if (days === 1) return 'yesterday'
  if (days < 7) return `${days}d ago`
  if (days < 30) return `${Math.floor(days / 7)}w ago`
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
}

/* ── Data Loading ── */
onMounted(async () => {
  const [todayRes, streakRes, calRes, histRes] = await Promise.allSettled([
    checkinApi.isTodayCheckedIn(),
    checkinApi.getStreak(),
    checkinApi.getMonthlyCalendar(now.getFullYear(), now.getMonth() + 1),
    checkinApi.getHistory({ page: 1, size: 20 }),
  ])
  if (todayRes.status === 'fulfilled') todayOk.value = !!todayRes.value
  if (streakRes.status === 'fulfilled') streak.value = (streakRes.value as any) || 0
  if (calRes.status === 'fulfilled') {
    const d: any = calRes.value
    cds.value = (d || []).map((s: string) => new Date(s).getDate())
  }
  if (histRes.status === 'fulfilled') {
    const d: any = histRes.value
    history.value = (d?.records || d || []) as CheckinRecord[]
  }
})
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-stat-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow: 0 2px 4px rgba(0,0,0,0.04);
}
.stripe-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow:
    0 15px 35px rgba(60,66,87,0.08),
    0 5px 15px rgba(0,0,0,0.04);
}
.stripe-btn-shadow {
  box-shadow:
    0 1px 3px rgba(0,0,0,0.08),
    0 0 0 1px rgba(0,0,0,0.06),
    inset 0 1px 0 rgba(255,255,255,0.1);
}

/* ======== GITHUB (Dark) ======== */
.gh-card {
  background: #0D1117;
  border: 1px solid #30363D;
  border-radius: 6px;
  transition: border-color 0.2s;
}
.gh-card:hover {
  border-color: #484F58;
}

/* ======== DUOLINGO (Warm) ======== */
.duo-quest-card {
  padding: 24px;
  background: #fff;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 4px 0 #E5E7EB;
}
.duo-card {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
}
.duo-hint-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #FFF3D6 0%, #FFECD2 100%);
  border: 2px solid #FBCF33;
  border-radius: 16px;
  box-shadow: 0 3px 0 #E5A800;
}

/* ======== FIGMA (Pro) ======== */
.figma-card {
  padding: 16px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
}
</style>
