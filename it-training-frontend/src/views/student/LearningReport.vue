<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Analytics：趋势统计卡 + 平滑面积图 + 数据表格
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <!-- Header with Tabs -->
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">学习报告</h1>
            <p class="text-sm text-[#425466] mt-0.5">追踪你的学习表现</p>
          </div>
          <div class="flex bg-[#F6F9FC] rounded-lg p-0.5">
            <button v-for="t in tabs" :key="t.key" @click="switchTab(t.key)"
              :class="['px-4 py-1.5 rounded-md text-sm font-medium transition-all cursor-pointer',
                tab === t.key ? 'bg-white text-[#0A2540] shadow-sm' : 'text-[#8898AA] hover:text-[#425466]']">
              {{ t.label }}
            </button>
          </div>
        </div>

        <template v-if="loading">
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div v-for="i in 4" :key="i" class="stripe-card h-24 animate-pulse bg-[#F6F9FC]"></div>
          </div>
          <div class="stripe-card h-64 animate-pulse bg-[#F6F9FC]"></div>
        </template>

        <template v-else-if="rpt">
          <!-- 4 Stat Cards with Trend Arrow -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
            <div v-for="(s, i) in stripeStats" :key="i" class="stripe-card">
              <div class="text-xs text-[#8898AA] uppercase tracking-wider font-medium">{{ s.label }}</div>
              <div class="flex items-end gap-2 mt-1">
                <span class="text-2xl font-bold text-[#0A2540]">{{ s.value }}</span>
                <span v-if="s.unit" class="text-sm text-[#8898AA] mb-0.5">{{ s.unit }}</span>
              </div>
              <div class="flex items-center gap-1 mt-1.5">
                <component :is="s.trendUp ? TrendingUp : TrendingDown"
                  :class="['w-3 h-3', s.trendUp ? 'text-emerald-500' : 'text-red-400']" :stroke-width="2" />
                <span :class="['text-xs font-medium', s.trendUp ? 'text-emerald-500' : 'text-red-400']">
                  {{ s.trendText }}
                </span>
                <span class="text-xs text-[#CBD5E1]">vs 上期</span>
              </div>
            </div>
          </div>

          <!-- Area Chart -->
          <div class="stripe-card">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-semibold text-[#0A2540]">
                <TrendingUp class="w-4 h-4 text-[#635BFF] inline mr-1.5 -mt-0.5" :stroke-width="1.75" />
                学习趋势
              </h3>
              <span class="text-xs text-[#8898AA]">{{ tabLabel }}学习时长分布</span>
            </div>
            <div ref="chartRef" class="w-full h-72"></div>
          </div>

          <!-- Data Table -->
          <div class="stripe-card overflow-hidden">
            <h3 class="text-sm font-semibold text-[#0A2540] mb-3">详细数据</h3>
            <div class="border border-[#E3E8EE] rounded-lg overflow-hidden">
              <div class="flex items-center gap-4 px-4 py-2.5 bg-[#F6F9FC] text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider border-b border-[#E3E8EE]">
                <span class="flex-1">日期</span>
                <span class="w-20 text-right">学习时长</span>
                <span class="w-16 text-right">课程</span>
                <span class="w-16 text-center">打卡</span>
              </div>
              <div class="divide-y divide-[#F0F3F7] max-h-[240px] overflow-y-auto">
                <div v-for="d in dailyData" :key="d.date"
                  class="flex items-center gap-4 px-4 py-2.5 hover:bg-[#F6F9FC]/50 transition-colors">
                  <span class="flex-1 text-sm text-[#0A2540]">{{ formatShortDate(d.date) }}</span>
                  <span class="w-20 text-right text-sm font-medium text-[#635BFF]">{{ d.hours || d.studyHours || 0 }}h</span>
                  <span class="w-16 text-right text-sm text-[#425466]">{{ d.courses || 0 }}</span>
                  <div class="w-16 text-center">
                    <span v-if="d.checkins" class="text-xs text-emerald-600 font-semibold">✓</span>
                    <span v-else class="text-xs text-[#CBD5E1]">—</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="text-center py-16">
          <BarChart3 class="w-12 h-12 text-[#8898AA]/40 mx-auto mb-3" :stroke-width="1" />
          <p class="text-sm text-[#8898AA]">暂无报告数据</p>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Raycast Analytics：紧凑统计 + 渐变柱状图 + 发光
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">学习报告</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ tabLabel }}</span>
          </div>
          <div class="flex gap-1">
            <button v-for="t in tabs" :key="t.key" @click="switchTab(t.key)"
              :class="['px-3 py-1.5 rounded-lg text-xs font-medium transition-all cursor-pointer',
                tab === t.key ? 'bg-[#818CF8] text-white shadow-[0_0_12px_rgba(129,140,248,0.3)]' : 'text-[#6B6B6E] hover:bg-white/[0.04]']">
              {{ t.label }}
            </button>
          </div>
        </div>

        <template v-if="loading">
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <div v-for="i in 4" :key="i" class="raycast-card h-24 animate-pulse"></div>
          </div>
          <div class="raycast-card h-64 animate-pulse"></div>
        </template>

        <template v-else-if="rpt">
          <!-- 4 Stat Cards with mini bar -->
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <div v-for="(s, i) in raycastStats" :key="i" class="raycast-card group">
              <div class="flex items-center justify-between mb-2">
                <span class="text-[10px] text-[#6B6B6E] uppercase tracking-wider font-semibold">{{ s.label }}</span>
                <component :is="s.icon" class="w-3.5 h-3.5 text-[#6B6B6E] group-hover:text-[#818CF8] transition-colors" :stroke-width="1.75" />
              </div>
              <div class="text-2xl font-bold raycast-gradient-text">{{ s.value }}{{ s.unit }}</div>
              <div class="mt-2 h-1 bg-white/[0.04] rounded-full overflow-hidden">
                <div class="h-full rounded-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4] transition-all"
                     :style="{ width: s.barWidth + '%' }"></div>
              </div>
            </div>
          </div>

          <!-- Main Chart: Gradient Bar -->
          <div class="raycast-card">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-semibold text-[#EDEDED]">学习活动</h3>
              <span class="text-[10px] font-mono text-[#6B6B6E]">{{ dailyData.length }} data points</span>
            </div>
            <div ref="chartRef" class="w-full h-72"></div>
          </div>

          <!-- Compact Summary Row -->
          <div class="flex gap-3">
            <div class="flex-1 raycast-card !py-3">
              <div class="flex items-center gap-2">
                <Calendar class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
                <span class="text-xs text-[#6B6B6E]">最活跃日</span>
                <span class="text-xs font-medium text-[#EDEDED] ml-auto font-mono">{{ mostActiveDay }}</span>
              </div>
            </div>
            <div class="flex-1 raycast-card !py-3">
              <div class="flex items-center gap-2">
                <Clock class="w-4 h-4 text-[#06B6D4]" :stroke-width="1.75" />
                <span class="text-xs text-[#6B6B6E]">日均学习</span>
                <span class="text-xs font-medium text-[#EDEDED] ml-auto font-mono">{{ avgHoursPerDay }}h</span>
              </div>
            </div>
            <div class="flex-1 raycast-card !py-3">
              <div class="flex items-center gap-2">
                <Zap class="w-4 h-4 text-[#F59E0B]" :stroke-width="1.75" />
                <span class="text-xs text-[#6B6B6E]">峰值时长</span>
                <span class="text-xs font-medium text-[#EDEDED] ml-auto font-mono">{{ peakHours }}h</span>
              </div>
            </div>
          </div>
        </template>

        <div v-else class="text-center py-16 text-sm text-[#6B6B6E]">暂无报告数据</div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Blocks：文字总结 + 进度环 + 柱状图 + 里程碑
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- Header -->
        <div class="flex items-center justify-between">
          <h1 class="text-xl font-extrabold text-[#292524]">📊 学习报告</h1>
          <div class="flex gap-1.5">
            <button v-for="t in tabs" :key="t.key" @click="switchTab(t.key)"
              :class="['px-4 py-2 rounded-full text-sm font-bold border-2 cursor-pointer transition-all',
                tab === t.key ? 'bg-[#292524] text-white border-[#292524]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:bg-[#F5F5F4]']">
              {{ t.label }}
            </button>
          </div>
        </div>

        <template v-if="loading">
          <div class="notion-summary-card h-32 animate-pulse bg-[#FEF3C7]"></div>
        </template>

        <template v-else-if="rpt">
          <!-- Big Text Summary Block -->
          <div class="notion-summary-card">
            <div class="text-3xl mb-2">{{ summaryEmoji }}</div>
            <p class="text-lg font-extrabold text-[#292524] leading-relaxed">{{ summaryText }}</p>
            <p class="text-sm text-[#78716C] mt-2">{{ summarySubText }}</p>
          </div>

          <!-- 2 Column: Progress Ring + Stat Blocks -->
          <div class="grid lg:grid-cols-2 gap-4">
            <!-- SVG Progress Ring -->
            <div class="notion-card text-center py-8">
              <h3 class="text-sm font-extrabold text-[#292524] mb-4">📈 学习完成度</h3>
              <div class="relative w-40 h-40 mx-auto">
                <svg class="w-40 h-40 -rotate-90" viewBox="0 0 120 120">
                  <circle cx="60" cy="60" r="52" fill="none" stroke="#E7E5E4" stroke-width="8" />
                  <circle cx="60" cy="60" r="52" fill="none" stroke="url(#warmGrad)" stroke-width="8"
                    stroke-linecap="round"
                    :stroke-dasharray="ringCircumference"
                    :stroke-dashoffset="ringOffset" />
                  <defs>
                    <linearGradient id="warmGrad" x1="0" y1="0" x2="1" y2="1">
                      <stop offset="0%" stop-color="#58CC02" />
                      <stop offset="100%" stop-color="#FFC800" />
                    </linearGradient>
                  </defs>
                </svg>
                <div class="absolute inset-0 flex items-center justify-center">
                  <div>
                    <div class="text-3xl font-extrabold text-[#292524]">{{ ringProgress }}%</div>
                    <div class="text-[10px] text-[#A8A29E] font-bold">完成度</div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 4 Stat Blocks (Notion style) -->
            <div class="grid grid-cols-2 gap-3">
              <div v-for="(s, i) in warmStats" :key="i" class="notion-stat-block">
                <span class="text-2xl">{{ s.emoji }}</span>
                <div class="text-2xl font-extrabold" :class="s.color">{{ s.value }}</div>
                <div class="text-xs font-bold text-[#78716C]">{{ s.label }}</div>
              </div>
            </div>
          </div>

          <!-- Bar Chart -->
          <div class="notion-card">
            <h3 class="text-sm font-extrabold text-[#292524] mb-4">📅 每日学习时长</h3>
            <div ref="chartRef" class="w-full h-56"></div>
          </div>

          <!-- Milestone Hint -->
          <div class="notion-hint-card">
            <span class="text-lg mr-2">{{ milestoneEmoji }}</span>
            <span class="text-sm font-bold text-[#292524]">{{ milestoneText }}</span>
          </div>
        </template>

        <div v-else class="text-center py-16">
          <p class="text-3xl mb-2">📊</p>
          <p class="text-sm text-[#78716C]">暂无学习报告</p>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana：多面板仪表盘 + 时间序列 + 饼图 + 表格 + 日活
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <!-- Dashboard Header -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Learning Report</span>
            <code class="text-[10px] font-mono text-[#94A3B8] bg-[#F1F5F9] px-1.5 py-0.5 rounded">dashboard</code>
          </div>
          <div class="flex items-center gap-2">
            <div class="flex gap-0.5">
              <button v-for="t in tabs" :key="t.key" @click="switchTab(t.key)"
                :class="['px-2.5 py-1 rounded text-[10px] font-mono cursor-pointer transition-all',
                  tab === t.key ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']">
                {{ t.labelEn }}
              </button>
            </div>
            <div class="h-4 border-l border-[#E2E8F0]"></div>
            <span class="text-[10px] font-mono text-[#94A3B8]">last updated: now</span>
          </div>
        </div>

        <template v-if="loading">
          <div class="grid grid-cols-4 gap-3">
            <div v-for="i in 4" :key="i" class="grafana-metric h-14 animate-pulse bg-[#F8FAFC]"></div>
          </div>
          <div class="grid lg:grid-cols-2 gap-3">
            <div v-for="i in 4" :key="i" class="grafana-panel h-52 animate-pulse bg-[#F8FAFC]"></div>
          </div>
        </template>

        <template v-else-if="rpt">
          <!-- Top Metrics Row (Grafana green) -->
          <div class="grid grid-cols-4 gap-3">
            <div v-for="(s, i) in grafanaStats" :key="i" class="grafana-metric">
              <div class="text-[9px] font-mono text-[#94A3B8] uppercase tracking-wider">{{ s.label }}</div>
              <div class="text-xl font-mono font-semibold text-[#22C55E]">{{ s.value }}</div>
            </div>
          </div>

          <!-- Panel Grid 2×2 -->
          <div class="grid lg:grid-cols-2 gap-3">
            <!-- Panel 1: Time Series -->
            <div class="grafana-panel">
              <div class="grafana-panel-header">
                <span>Time Series — Study Hours</span>
                <span class="text-[#22C55E]">{{ rpt.totalStudyHours || 0 }}h total</span>
              </div>
              <div ref="chartRef" class="w-full h-52 p-2"></div>
            </div>

            <!-- Panel 2: Distribution Donut -->
            <div class="grafana-panel">
              <div class="grafana-panel-header">
                <span>Distribution — Study Metrics</span>
                <span class="text-[#94A3B8]">4 series</span>
              </div>
              <div ref="chartRef2" class="w-full h-52 p-2"></div>
            </div>

            <!-- Panel 3: Data Table -->
            <div class="grafana-panel">
              <div class="grafana-panel-header">
                <span>Table — Daily Breakdown</span>
                <span class="text-[#94A3B8]">{{ dailyData.length }} rows</span>
              </div>
              <div class="p-2 max-h-52 overflow-y-auto">
                <div class="flex items-center gap-2 text-[9px] font-mono text-[#94A3B8] uppercase tracking-wider pb-1.5 border-b border-[#E2E8F0]">
                  <span class="flex-1">date</span>
                  <span class="w-16 text-right">hours</span>
                  <span class="w-14 text-right">courses</span>
                  <span class="w-14 text-center">checkin</span>
                </div>
                <div v-for="d in dailyData" :key="d.date"
                  class="flex items-center gap-2 py-1.5 border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                  <span class="flex-1 text-[11px] font-mono text-[#0F172A]">{{ d.date || d.day || '—' }}</span>
                  <span class="w-16 text-right text-[11px] font-mono text-[#22C55E] font-semibold">{{ d.hours || d.studyHours || 0 }}</span>
                  <span class="w-14 text-right text-[11px] font-mono text-[#64748B]">{{ d.courses || 0 }}</span>
                  <span class="w-14 text-center text-[11px] font-mono" :class="d.checkins ? 'text-[#22C55E]' : 'text-[#CBD5E1]'">
                    {{ d.checkins ? '●' : '○' }}
                  </span>
                </div>
                <div v-if="!dailyData.length" class="py-4 text-center text-[10px] font-mono text-[#94A3B8]">no data</div>
              </div>
            </div>

            <!-- Panel 4: Daily Activity Bar -->
            <div class="grafana-panel">
              <div class="grafana-panel-header">
                <span>Bar Chart — Daily Activity</span>
                <span class="text-[#94A3B8]">avg: {{ avgHoursPerDay }}h</span>
              </div>
              <div ref="chartRef3" class="w-full h-52 p-2"></div>
            </div>
          </div>
        </template>

        <div v-else class="text-center py-12 text-[#94A3B8] text-xs font-mono">// no report data available</div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { reportApi } from '@/api/report'
import { useECharts } from '@/composables/useECharts'
import {
  BarChart3, TrendingUp, TrendingDown, Clock, Calendar,
  BookOpen, Flame, Award, Zap,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Tab System ── */
const tab = ref('w')
const tabs = [
  { key: 'w', label: '周报', labelEn: 'weekly' },
  { key: 'm', label: '月报', labelEn: 'monthly' },
  { key: 'y', label: '年报', labelEn: 'yearly' },
]
const tabLabel = computed(() => tabs.find(t => t.key === tab.value)?.label || '')

/* ── Core Data ── */
const rpt = ref<any>(null)
const loading = ref(false)

/* ── Charts (3 refs for Grafana multi-panel) ── */
const chartRef = ref<HTMLElement | null>(null)
const chartRef2 = ref<HTMLElement | null>(null)
const chartRef3 = ref<HTMLElement | null>(null)
const { setOption } = useECharts(chartRef)
const { setOption: setOption2 } = useECharts(chartRef2)
const { setOption: setOption3 } = useECharts(chartRef3)

/* ── Derived Data ── */
const dailyData = computed<any[]>(() => rpt.value?.dailyStats || [])

/* ── Stripe Stats (Light) ── */
const stripeStats = computed(() => {
  if (!rpt.value) return []
  const r = rpt.value
  const total = r.totalStudyHours || 0
  const prev = r.prevTotalStudyHours || Math.max(total * 0.8, 1)
  const trend = prev > 0 ? Math.round(((total - prev) / prev) * 100) : 0
  return [
    { value: total, unit: 'h', label: '学习时长', trendUp: trend >= 0, trendText: Math.abs(trend) + '%' },
    { value: r.completedCourses || 0, unit: '', label: '完成课程', trendUp: true, trendText: '+' + (r.completedCourses || 0) },
    { value: r.checkinDays || 0, unit: '天', label: '打卡天数', trendUp: true, trendText: (r.checkinDays || 0) + '天' },
    { value: r.achievementCount || 0, unit: '', label: '获得成就', trendUp: true, trendText: '+' + (r.achievementCount || 0) },
  ]
})

/* ── Raycast Stats (Dark) ── */
const raycastStats = computed(() => {
  if (!rpt.value) return []
  const r = rpt.value
  const maxH = Math.max(r.totalStudyHours || 1, 20)
  return [
    { value: r.totalStudyHours || 0, unit: 'h', label: '学习时长', icon: Clock, barWidth: Math.min(((r.totalStudyHours || 0) / maxH) * 100, 100) },
    { value: r.completedCourses || 0, unit: '', label: '完成课程', icon: BookOpen, barWidth: Math.min(((r.completedCourses || 0) / 10) * 100, 100) },
    { value: r.checkinDays || 0, unit: '天', label: '打卡天数', icon: Flame, barWidth: Math.min(((r.checkinDays || 0) / 7) * 100, 100) },
    { value: r.achievementCount || 0, unit: '', label: '获得成就', icon: Award, barWidth: Math.min(((r.achievementCount || 0) / 10) * 100, 100) },
  ]
})

const mostActiveDay = computed(() => {
  if (!dailyData.value.length) return '—'
  const max = dailyData.value.reduce((a: any, b: any) => ((b.hours || b.studyHours || 0) > (a.hours || a.studyHours || 0) ? b : a))
  return max.date || max.day || '—'
})

const avgHoursPerDay = computed(() => {
  if (!dailyData.value.length) return '0'
  const total = dailyData.value.reduce((s: number, d: any) => s + (d.hours || d.studyHours || 0), 0)
  return (total / dailyData.value.length).toFixed(1)
})

const peakHours = computed(() => {
  if (!dailyData.value.length) return '0'
  return Math.max(...dailyData.value.map((d: any) => d.hours || d.studyHours || 0)).toFixed(1)
})

/* ── Notion Stats (Warm) ── */
const warmStats = computed(() => {
  if (!rpt.value) return []
  const r = rpt.value
  return [
    { emoji: '⏰', value: (r.totalStudyHours || 0) + 'h', label: '学习时长', color: 'text-[#D97706]' },
    { emoji: '📚', value: r.completedCourses || 0, label: '完成课程', color: 'text-[#58CC02]' },
    { emoji: '🔥', value: (r.checkinDays || 0) + '天', label: '打卡天数', color: 'text-[#FF9600]' },
    { emoji: '🏆', value: r.achievementCount || 0, label: '获得成就', color: 'text-[#7C3AED]' },
  ]
})

const ringCircumference = 2 * Math.PI * 52
const ringProgress = computed(() => {
  if (!rpt.value) return 0
  const total = rpt.value.totalStudyHours || 0
  const goal = tab.value === 'w' ? 14 : tab.value === 'm' ? 60 : 300
  return Math.min(Math.round((total / goal) * 100), 100)
})
const ringOffset = computed(() => ringCircumference * (1 - ringProgress.value / 100))

const summaryEmoji = computed(() => {
  const p = ringProgress.value
  if (p >= 100) return '🎉'
  if (p >= 70) return '💪'
  if (p >= 40) return '📈'
  return '🌱'
})
const summaryText = computed(() => {
  if (!rpt.value) return ''
  const r = rpt.value
  const period = tab.value === 'w' ? '本周' : tab.value === 'm' ? '本月' : '今年'
  return `${period}你学习了 ${r.totalStudyHours || 0} 小时，完成了 ${r.completedCourses || 0} 门课程！`
})
const summarySubText = computed(() => {
  if (!rpt.value) return ''
  const r = rpt.value
  return `累计打卡 ${r.checkinDays || 0} 天，获得 ${r.achievementCount || 0} 个成就`
})
const milestoneEmoji = computed(() => {
  const h = rpt.value?.totalStudyHours || 0
  if (h >= 50) return '🏆'
  if (h >= 20) return '🎯'
  if (h >= 10) return '🌟'
  return '💡'
})
const milestoneText = computed(() => {
  const h = rpt.value?.totalStudyHours || 0
  if (h >= 50) return '太棒了！学习时长已突破 50 小时，你是学习大师！'
  if (h >= 20) return `再学 ${50 - h} 小时就能成为学习大师！加油！`
  if (h >= 10) return `已完成 ${h} 小时，距离 20 小时里程碑还需 ${20 - h} 小时`
  return '开始积累吧！学满 10 小时解锁第一个里程碑'
})

/* ── Grafana Stats (Pro) ── */
const grafanaStats = computed(() => {
  if (!rpt.value) return []
  const r = rpt.value
  return [
    { value: (r.totalStudyHours || 0) + 'h', label: 'study_hours' },
    { value: r.completedCourses || 0, label: 'courses' },
    { value: r.checkinDays || 0, label: 'checkins' },
    { value: r.achievementCount || 0, label: 'achievements' },
  ]
})

/* ── Chart Building (theme-specific) ── */
function buildCharts() {
  if (!rpt.value?.dailyStats) return
  const days = dailyData.value.map((d: any) => {
    const dt = d.date || d.day || ''
    return dt.length >= 10 ? dt.slice(5) : dt
  })
  const hours = dailyData.value.map((d: any) => d.hours || d.studyHours || 0)

  const t = theme.value
  if (t === 'light') {
    setOption({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days, boundaryGap: false,
        axisLine: { lineStyle: { color: '#E3E8EE' } }, axisLabel: { color: '#8898AA', fontSize: 10 }, axisTick: { show: false } },
      yAxis: { type: 'value', name: '小时',
        axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F3F7', type: 'dashed' } }, axisLabel: { color: '#8898AA', fontSize: 10 } },
      series: [{ type: 'line', data: hours, smooth: true,
        lineStyle: { width: 2.5, color: '#635BFF' }, itemStyle: { color: '#635BFF' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(99,91,255,0.15)' }, { offset: 1, color: 'rgba(99,91,255,0)' }] } },
        symbolSize: 4, symbol: 'circle' }],
      tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E3E8EE', textStyle: { color: '#0A2540', fontSize: 12 } },
    }, false)
  } else if (t === 'dark') {
    setOption({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days,
        axisLine: { lineStyle: { color: 'rgba(255,255,255,0.06)' } }, axisLabel: { color: '#6B6B6E', fontSize: 10 }, axisTick: { show: false } },
      yAxis: { type: 'value',
        axisLine: { show: false }, splitLine: { lineStyle: { color: 'rgba(255,255,255,0.04)', type: 'dashed' } }, axisLabel: { color: '#6B6B6E', fontSize: 10 } },
      series: [{ type: 'bar', data: hours, barWidth: '60%',
        itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: '#818CF8' }, { offset: 1, color: '#06B6D4' }] },
          borderRadius: [4, 4, 0, 0] } }],
      tooltip: { trigger: 'axis', backgroundColor: '#1E1E1E', borderColor: 'rgba(255,255,255,0.1)', textStyle: { color: '#EDEDED', fontSize: 12 } },
    }, false)
  } else if (t === 'warm') {
    setOption({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days,
        axisLine: { lineStyle: { color: '#E7E5E4' } }, axisLabel: { color: '#A8A29E', fontSize: 10 }, axisTick: { show: false } },
      yAxis: { type: 'value',
        axisLine: { show: false }, splitLine: { lineStyle: { color: '#E7E5E4', type: 'dashed' } }, axisLabel: { color: '#A8A29E', fontSize: 10 } },
      series: [{ type: 'bar', data: hours, barWidth: '50%',
        itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: '#FFC800' }, { offset: 1, color: '#FF9600' }] },
          borderRadius: [8, 8, 0, 0] } }],
      tooltip: { trigger: 'axis', backgroundColor: '#FFFBF5', borderColor: '#E7E5E4', textStyle: { color: '#292524', fontSize: 12 } },
    }, false)
  } else {
    // Grafana: Panel 1 — Time Series (green line)
    setOption({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days,
        axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: '#94A3B8', fontSize: 9, fontFamily: 'monospace' }, axisTick: { show: false } },
      yAxis: { type: 'value',
        axisLine: { show: false }, splitLine: { lineStyle: { color: '#F1F5F9', type: 'dashed' } }, axisLabel: { color: '#94A3B8', fontSize: 9, fontFamily: 'monospace' } },
      series: [{ type: 'line', data: hours, smooth: false,
        lineStyle: { width: 1.5, color: '#22C55E' }, itemStyle: { color: '#22C55E' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(34,197,94,0.1)' }, { offset: 1, color: 'rgba(34,197,94,0)' }] } },
        symbolSize: 3, symbol: 'circle' }],
      tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#0F172A', fontSize: 11, fontFamily: 'monospace' } },
    }, false)

    // Panel 2 — Distribution Donut
    const r = rpt.value
    const pieData = [
      { value: r.totalStudyHours || 0, name: 'Study', itemStyle: { color: '#22C55E' } },
      { value: r.completedCourses || 0, name: 'Courses', itemStyle: { color: '#0284C7' } },
      { value: r.checkinDays || 0, name: 'Checkins', itemStyle: { color: '#EAB308' } },
      { value: r.achievementCount || 0, name: 'Awards', itemStyle: { color: '#A855F7' } },
    ].filter(d => d.value > 0)
    setOption2({
      series: [{ type: 'pie', radius: ['45%', '70%'], center: ['50%', '50%'], data: pieData,
        label: { show: true, color: '#64748B', fontSize: 10, fontFamily: 'monospace' },
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.1)' } } }],
      tooltip: { trigger: 'item', backgroundColor: '#fff', borderColor: '#E2E8F0',
        textStyle: { color: '#0F172A', fontSize: 11, fontFamily: 'monospace' }, formatter: '{b}: {c} ({d}%)' },
    }, false)

    // Panel 4 — Daily Activity Bar (green)
    setOption3({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days,
        axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: '#94A3B8', fontSize: 9, fontFamily: 'monospace' }, axisTick: { show: false } },
      yAxis: { type: 'value',
        axisLine: { show: false }, splitLine: { lineStyle: { color: '#F1F5F9' } }, axisLabel: { color: '#94A3B8', fontSize: 9, fontFamily: 'monospace' } },
      series: [{ type: 'bar', data: hours, barWidth: '50%',
        itemStyle: { color: '#22C55E', borderRadius: [2, 2, 0, 0] } }],
      tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#0F172A', fontSize: 11, fontFamily: 'monospace' } },
    }, false)
  }
}

/* ── Format ── */
function formatShortDate(d: string) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', weekday: 'short' })
}

/* ── Loading ── */
async function switchTab(key: string) {
  tab.value = key
  await load()
}

async function load() {
  loading.value = true
  try {
    const n = new Date()
    if (tab.value === 'w') rpt.value = await reportApi.weekly()
    else if (tab.value === 'm') rpt.value = await reportApi.monthly(n.getFullYear(), n.getMonth() + 1)
    else rpt.value = await reportApi.yearly(n.getFullYear())
  } catch { rpt.value = null }
  finally { loading.value = false }
  await nextTick()
  buildCharts()
}

watch(theme, async () => {
  await nextTick()
  buildCharts()
})

onMounted(load)
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-card {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04);
}

/* ======== RAYCAST (Dark) ======== */
.raycast-card {
  padding: 16px;
  border-radius: 12px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  transition: border-color 0.2s, box-shadow 0.2s;
}
.raycast-card:hover {
  border-color: rgba(129,140,248,0.2);
  box-shadow: 0 0 20px rgba(129,140,248,0.06);
}
.raycast-gradient-text {
  background: linear-gradient(135deg, #EDEDED 0%, #818CF8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ======== NOTION (Warm) ======== */
.notion-card {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
}
.notion-summary-card {
  padding: 24px;
  background: linear-gradient(135deg, #FFF3D6 0%, #FFECD2 100%);
  border: 2px solid #FBCF33;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5A800;
  text-align: center;
}
.notion-stat-block {
  padding: 16px;
  background: white;
  border: 2px solid #E5E7EB;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 2px 0 #E5E7EB;
}
.notion-hint-card {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #FFF3D6 0%, #FFECD2 100%);
  border: 2px solid #FBCF33;
  border-radius: 16px;
  box-shadow: 0 3px 0 #E5A800;
}

/* ======== GRAFANA (Pro) ======== */
.grafana-panel {
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 4px;
  overflow: hidden;
}
.grafana-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background: #F8FAFC;
  border-bottom: 1px solid #E2E8F0;
  font-size: 10px;
  font-weight: 600;
  color: #0F172A;
  font-family: monospace;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}
.grafana-metric {
  padding: 12px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 4px;
}
</style>
