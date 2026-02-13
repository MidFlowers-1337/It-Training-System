<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Analytics：趋势统计卡 + 面积图 + 排行列表
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">数据看板</h1>
            <p class="text-sm text-[#425466] mt-0.5">实时数据概览</p>
          </div>
          <span class="text-xs text-[#8898AA]">最后更新：刚刚</span>
        </div>

        <!-- 4 Stat Cards with Trend Arrow -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <template v-if="loading">
            <div v-for="i in 4" :key="i" class="stripe-card h-28 animate-pulse bg-[#F6F9FC]"></div>
          </template>
          <template v-else>
            <div v-for="(s, i) in stats" :key="i" class="stripe-card group hover:shadow-[0_8px_24px_rgba(99,91,255,0.08)] hover:border-[#635BFF]/20 transition-all">
              <div class="flex items-center justify-between mb-1">
                <span class="text-[10px] text-[#8898AA] uppercase tracking-wider font-semibold">{{ s.label }}</span>
                <div :class="['w-8 h-8 rounded-lg flex items-center justify-center', s.bgClass]">
                  <component :is="s.icon" :class="['w-4 h-4', s.iconClass]" :stroke-width="1.75" />
                </div>
              </div>
              <div class="text-2xl font-bold text-[#0A2540]">{{ s.value }}<span v-if="s.unit" class="text-sm text-[#8898AA] ml-0.5">{{ s.unit }}</span></div>
              <div class="flex items-center gap-1 mt-1">
                <TrendingUp class="w-3 h-3 text-emerald-500" :stroke-width="2" />
                <span class="text-xs text-emerald-500 font-medium">{{ s.trend }}</span>
                <span class="text-[10px] text-[#CBD5E1]">vs 上期</span>
              </div>
            </div>
          </template>
        </div>

        <!-- Two Column: Hot Courses + Chart -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-5">
          <!-- Hot Courses Rank List -->
          <div class="stripe-card">
            <h3 class="text-sm font-semibold text-[#0A2540] mb-4 flex items-center">
              <Flame class="w-4 h-4 text-orange-500 mr-1.5 -mt-0.5" :stroke-width="1.75" />
              热门课程
            </h3>
            <div v-if="hot.length" class="space-y-2">
              <div v-for="(c, i) in hot" :key="i"
                class="flex items-center justify-between p-2.5 rounded-lg hover:bg-[#F6F9FC] transition-colors">
                <div class="flex items-center gap-3">
                  <span :class="['w-6 h-6 rounded-full text-[10px] font-bold flex items-center justify-center',
                    i < 3 ? 'bg-[#635BFF] text-white' : 'bg-[#E3E8EE] text-[#8898AA]']">
                    {{ i + 1 }}
                  </span>
                  <span class="text-sm text-[#0A2540]">{{ c.courseName || c.title }}</span>
                </div>
                <div class="flex items-center gap-2">
                  <div class="w-16 h-1.5 bg-[#E3E8EE] rounded-full overflow-hidden">
                    <div class="h-full bg-[#635BFF] rounded-full" :style="{ width: hotBarWidth(c, i) + '%' }"></div>
                  </div>
                  <span class="text-xs text-[#635BFF] font-medium w-8 text-right">{{ c.enrollCount || 0 }}</span>
                </div>
              </div>
            </div>
            <div v-else class="text-center py-8 text-sm text-[#8898AA]">暂无数据</div>
          </div>

          <!-- Enrollment Trend Area Chart -->
          <div class="stripe-card">
            <div class="flex items-center justify-between mb-4">
              <h3 class="text-sm font-semibold text-[#0A2540] flex items-center">
                <TrendingUp class="w-4 h-4 text-[#635BFF] mr-1.5 -mt-0.5" :stroke-width="1.75" />
                报名趋势
              </h3>
              <span class="text-xs text-[#8898AA]">近期数据</span>
            </div>
            <div ref="chartRef" class="w-full h-64"></div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Raycast Analytics：发光统计卡 + 渐变柱状图 + 紧凑底栏
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">数据看板</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">live</span>
          </div>
          <span class="text-[10px] font-mono text-[#6B6B6E]">updated: now</span>
        </div>

        <!-- 4 Stat Cards with glow + mini bar -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <template v-if="loading">
            <div v-for="i in 4" :key="i" class="raycast-card h-28 animate-pulse"></div>
          </template>
          <template v-else>
            <div v-for="(s, i) in stats" :key="i" class="raycast-card group">
              <div class="flex items-center justify-between mb-2">
                <span class="text-[10px] text-[#6B6B6E] uppercase tracking-wider font-semibold">{{ s.label }}</span>
                <component :is="s.icon" class="w-3.5 h-3.5 text-[#6B6B6E] group-hover:text-[#818CF8] transition-colors" :stroke-width="1.75" />
              </div>
              <div class="text-2xl font-bold raycast-gradient-text">{{ s.value }}{{ s.unit }}</div>
              <div class="mt-2 h-1 bg-white/[0.04] rounded-full overflow-hidden">
                <div class="h-full rounded-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4] transition-all"
                     :style="{ width: Math.min(Number(s.value) / (i === 3 ? 1 : 50) * 100, 100) + '%' }"></div>
              </div>
            </div>
          </template>
        </div>

        <!-- Main Chart -->
        <div class="raycast-card">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-sm font-semibold text-[#EDEDED]">报名趋势</h3>
            <span class="text-[10px] font-mono text-[#6B6B6E]">{{ trendData.length }} data points</span>
          </div>
          <div ref="chartRef" class="w-full h-72"></div>
        </div>

        <!-- Hot Courses + Summary Row -->
        <div class="flex gap-3">
          <div class="flex-1 raycast-card">
            <h3 class="text-sm font-semibold text-[#EDEDED] mb-3">热门课程</h3>
            <div v-if="hot.length" class="space-y-1">
              <div v-for="(c, i) in hot.slice(0, 5)" :key="i"
                class="flex items-center justify-between py-2 px-2 rounded-lg hover:bg-white/[0.03] transition-colors">
                <div class="flex items-center gap-2">
                  <span class="text-[10px] font-mono text-[#6B6B6E] w-4">{{ i + 1 }}.</span>
                  <span class="text-sm text-[#EDEDED]">{{ c.courseName || c.title }}</span>
                </div>
                <span class="text-xs font-mono text-[#818CF8]">{{ c.enrollCount || 0 }}</span>
              </div>
            </div>
            <div v-else class="text-center py-6 text-sm text-[#6B6B6E]">暂无数据</div>
          </div>
          <div class="w-48 space-y-3 flex-shrink-0">
            <div class="raycast-card !py-3">
              <div class="flex items-center gap-2">
                <Calendar class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
                <span class="text-xs text-[#6B6B6E]">今日新增</span>
              </div>
              <div class="text-lg font-bold text-[#EDEDED] mt-1 font-mono">{{ todayCount }}</div>
            </div>
            <div class="raycast-card !py-3">
              <div class="flex items-center gap-2">
                <Zap class="w-4 h-4 text-[#F59E0B]" :stroke-width="1.75" />
                <span class="text-xs text-[#6B6B6E]">完课率</span>
              </div>
              <div class="text-lg font-bold text-[#EDEDED] mt-1 font-mono">{{ overviewData.completionRate || 0 }}%</div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Workspace：大字摘要 + emoji blocks + 里程碑
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <div class="flex items-center gap-2">
          <span class="text-lg">📊</span>
          <h1 class="text-xl font-extrabold text-[#292524]">数据看板</h1>
        </div>

        <!-- Big Text Summary -->
        <div v-if="!loading" class="notion-summary-card">
          <div class="text-3xl mb-2">{{ summaryEmoji }}</div>
          <p class="text-lg font-extrabold text-[#292524] leading-relaxed">{{ summaryText }}</p>
          <p class="text-sm text-[#78716C] mt-2">{{ summarySubText }}</p>
        </div>
        <div v-else class="notion-summary-card h-28 animate-pulse bg-[#FEF3C7]"></div>

        <!-- 4 Emoji Stat Blocks -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <template v-if="loading">
            <div v-for="i in 4" :key="i" class="notion-stat h-24 animate-pulse bg-[#FFFBF5]"></div>
          </template>
          <template v-else>
            <div v-for="(s, i) in warmStats" :key="i" class="notion-stat group hover:shadow-[0_4px_0_#D97706] hover:border-[#D97706] transition-all">
              <span class="text-2xl">{{ s.emoji }}</span>
              <div class="text-2xl font-extrabold" :class="s.color">{{ s.value }}</div>
              <div class="text-xs font-bold text-[#78716C]">{{ s.label }}</div>
            </div>
          </template>
        </div>

        <!-- Two Column: Chart + Hot Courses -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <div class="notion-card">
            <h3 class="text-sm font-extrabold text-[#292524] mb-4">📈 报名趋势</h3>
            <div ref="chartRef" class="w-full h-56"></div>
          </div>

          <div class="notion-card">
            <h3 class="text-sm font-extrabold text-[#292524] mb-4">🔥 热门课程</h3>
            <div v-if="hot.length" class="space-y-0">
              <div v-for="(c, i) in hot.slice(0, 5)" :key="i"
                class="flex items-center justify-between py-3 border-b border-[#E7E5E4] last:border-0">
                <div class="flex items-center gap-2">
                  <span class="text-base">{{ ['🥇','🥈','🥉','4️⃣','5️⃣'][i] }}</span>
                  <span class="text-sm font-bold text-[#292524]">{{ c.courseName || c.title }}</span>
                </div>
                <span class="text-xs font-bold text-[#D97706]">{{ c.enrollCount || 0 }} 人</span>
              </div>
            </div>
            <div v-else class="text-center py-8 text-sm text-[#78716C]">暂无数据</div>
          </div>
        </div>

        <!-- Milestone Hint -->
        <div class="notion-hint-card">
          <span class="text-lg mr-2">💡</span>
          <span class="text-sm font-bold text-[#292524]">{{ milestoneText }}</span>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana Dashboard：多面板 + monospace + 绿色指标
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <!-- Dashboard Header -->
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Admin Dashboard</span>
            <code class="text-[10px] font-mono text-[#94A3B8] bg-[#F1F5F9] px-1.5 py-0.5 rounded">live</code>
          </div>
          <code class="text-[10px] font-mono text-[#94A3B8]">last updated: now</code>
        </div>

        <!-- Top Metrics Row (Grafana green) -->
        <div class="grid grid-cols-4 gap-3">
          <template v-if="loading">
            <div v-for="i in 4" :key="i" class="grafana-metric h-16 animate-pulse bg-[#F8FAFC]"></div>
          </template>
          <template v-else>
            <div v-for="(s, i) in stats" :key="i" class="grafana-metric">
              <div class="text-[9px] font-mono text-[#94A3B8] uppercase tracking-wider">{{ s.labelEn }}</div>
              <div class="text-xl font-mono font-semibold text-[#22C55E]">{{ s.value }}<span v-if="s.unit" class="text-xs text-[#94A3B8]">{{ s.unit }}</span></div>
            </div>
          </template>
        </div>

        <!-- 2×2 Panel Grid -->
        <div class="grid lg:grid-cols-2 gap-3">
          <!-- Panel 1: Time Series -->
          <div class="grafana-panel">
            <div class="grafana-panel-header">
              <span>Time Series — Enrollment Trend</span>
              <span class="text-[#22C55E]">{{ trendData.length }} pts</span>
            </div>
            <div ref="chartRef" class="w-full h-52 p-2"></div>
          </div>

          <!-- Panel 2: Hot Courses Table -->
          <div class="grafana-panel">
            <div class="grafana-panel-header">
              <span>Table — Hot Courses</span>
              <span class="text-[#94A3B8]">{{ hot.length }} rows</span>
            </div>
            <div class="p-2 max-h-52 overflow-y-auto">
              <div class="flex items-center gap-2 text-[9px] font-mono text-[#94A3B8] uppercase tracking-wider pb-1.5 border-b border-[#E2E8F0]">
                <span class="w-6">#</span>
                <span class="flex-1">course</span>
                <span class="w-16 text-right">enrolls</span>
              </div>
              <div v-for="(c, i) in hot" :key="i"
                class="flex items-center gap-2 py-1.5 border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                <span class="w-6 text-[11px] font-mono text-[#94A3B8]">{{ i + 1 }}</span>
                <span class="flex-1 text-[11px] font-mono text-[#0F172A] truncate">{{ c.courseName || c.title }}</span>
                <span class="w-16 text-right text-[11px] font-mono text-[#22C55E] font-semibold">{{ c.enrollCount || 0 }}</span>
              </div>
              <div v-if="!hot.length" class="py-4 text-center text-[10px] font-mono text-[#94A3B8]">no data</div>
            </div>
          </div>

          <!-- Panel 3: Distribution Donut -->
          <div class="grafana-panel">
            <div class="grafana-panel-header">
              <span>Pie — Overview Distribution</span>
              <span class="text-[#94A3B8]">4 series</span>
            </div>
            <div ref="chartRef2" class="w-full h-52 p-2"></div>
          </div>

          <!-- Panel 4: Metrics Detail -->
          <div class="grafana-panel">
            <div class="grafana-panel-header">
              <span>Stat — Key Metrics</span>
              <span class="text-[#94A3B8]">detail</span>
            </div>
            <div class="p-4 grid grid-cols-2 gap-4">
              <div v-for="(s, i) in stats" :key="i" class="text-center">
                <div class="text-[9px] font-mono text-[#94A3B8] uppercase tracking-wider mb-1">{{ s.labelEn }}</div>
                <div class="text-2xl font-mono font-bold text-[#22C55E]">{{ s.value }}</div>
                <div class="text-[10px] font-mono text-[#64748B] mt-0.5">{{ s.label }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useHead } from '@unhead/vue'
import { statsApi } from '@/api/stats'
import { useECharts } from '@/composables/useECharts'
import { useThemeStore } from '@/stores/theme'
import {
  BookOpen, Users, FileText, CheckCircle,
  Flame, TrendingUp, Calendar, Zap,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

useHead({ title: '管理后台 — IT 智能培训系统' })

/* ── Data State ── */
const loading = ref(true)
const overviewData = ref<any>({})
const hot = ref<any[]>([])
const trendData = ref<any[]>([])

/* ── Charts ── */
const chartRef = ref<HTMLElement | null>(null)
const chartRef2 = ref<HTMLElement | null>(null)
const { setOption } = useECharts(chartRef)
const { setOption: setOption2 } = useECharts(chartRef2)

/* ── Derived Stats ── */
const stats = computed(() => {
  const o = overviewData.value
  return [
    { icon: BookOpen, value: o.totalCourses || 0, unit: '', label: '课程总数', labelEn: 'courses', trend: '+12%', bgClass: 'bg-indigo-50', iconClass: 'text-indigo-500' },
    { icon: Users, value: o.totalUsers || 0, unit: '', label: '用户总数', labelEn: 'users', trend: '+8%', bgClass: 'bg-blue-50', iconClass: 'text-blue-500' },
    { icon: FileText, value: o.totalEnrollments || 0, unit: '', label: '报名总数', labelEn: 'enrollments', trend: '+15%', bgClass: 'bg-orange-50', iconClass: 'text-orange-500' },
    { icon: CheckCircle, value: o.completionRate || 0, unit: '%', label: '完课率', labelEn: 'completion', trend: '+3%', bgClass: 'bg-emerald-50', iconClass: 'text-emerald-500' },
  ]
})

/* ── Warm Theme Helpers ── */
const warmStats = computed(() => {
  const o = overviewData.value
  return [
    { emoji: '📚', value: o.totalCourses || 0, label: '课程总数', color: 'text-[#D97706]' },
    { emoji: '👥', value: o.totalUsers || 0, label: '用户总数', color: 'text-[#58CC02]' },
    { emoji: '📝', value: o.totalEnrollments || 0, label: '报名总数', color: 'text-[#FF9600]' },
    { emoji: '✅', value: (o.completionRate || 0) + '%', label: '完课率', color: 'text-[#7C3AED]' },
  ]
})

const summaryEmoji = computed(() => {
  const total = overviewData.value.totalEnrollments || 0
  if (total >= 100) return '🎉'
  if (total >= 50) return '💪'
  if (total >= 10) return '📈'
  return '🌱'
})

const summaryText = computed(() => {
  const o = overviewData.value
  return `平台已有 ${o.totalCourses || 0} 门课程，${o.totalUsers || 0} 位用户，${o.totalEnrollments || 0} 次报名！`
})

const summarySubText = computed(() => {
  const o = overviewData.value
  return `当前完课率 ${o.completionRate || 0}%，共有 ${hot.value.length} 门热门课程`
})

const milestoneText = computed(() => {
  const total = overviewData.value.totalEnrollments || 0
  if (total >= 100) return '太棒了！报名数已突破 100，平台运营良好！'
  if (total >= 50) return `再有 ${100 - total} 次报名就突破百人大关了！`
  if (total >= 10) return `已有 ${total} 次报名，继续推广课程吧！`
  return '平台刚起步，多创建优质课程吸引用户吧！'
})

const todayCount = computed(() => {
  if (!trendData.value.length) return 0
  const last = trendData.value[trendData.value.length - 1]
  return last?.count || last?.enrollCount || 0
})

/* ── Hot Courses Bar Width ── */
function hotBarWidth(c: any, _i: number) {
  const max = Math.max(...hot.value.map((h: any) => h.enrollCount || 0), 1)
  return Math.round(((c.enrollCount || 0) / max) * 100)
}

/* ── Chart Builder ── */
function buildCharts() {
  const days = trendData.value.map((d: any) => d.date || d.month || '')
  const counts = trendData.value.map((d: any) => d.count || d.enrollCount || 0)

  const t = theme.value
  if (t === 'light') {
    setOption({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days, boundaryGap: false,
        axisLine: { lineStyle: { color: '#E3E8EE' } }, axisLabel: { color: '#8898AA', fontSize: 10 }, axisTick: { show: false } },
      yAxis: { type: 'value',
        axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F3F7', type: 'dashed' } }, axisLabel: { color: '#8898AA', fontSize: 10 } },
      series: [{ type: 'line', data: counts, smooth: true,
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
      series: [{ type: 'bar', data: counts, barWidth: '60%',
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
      series: [{ type: 'bar', data: counts, barWidth: '50%',
        itemStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: '#FFC800' }, { offset: 1, color: '#FF9600' }] },
          borderRadius: [8, 8, 0, 0] } }],
      tooltip: { trigger: 'axis', backgroundColor: '#FFFBF5', borderColor: '#E7E5E4', textStyle: { color: '#292524', fontSize: 12 } },
    }, false)
  } else {
    // Pro: Grafana green line chart
    setOption({
      grid: { left: '3%', right: '4%', top: '8%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: days,
        axisLine: { lineStyle: { color: '#E2E8F0' } }, axisLabel: { color: '#94A3B8', fontSize: 9, fontFamily: 'monospace' }, axisTick: { show: false } },
      yAxis: { type: 'value',
        axisLine: { show: false }, splitLine: { lineStyle: { color: '#F1F5F9', type: 'dashed' } }, axisLabel: { color: '#94A3B8', fontSize: 9, fontFamily: 'monospace' } },
      series: [{ type: 'line', data: counts, smooth: false,
        lineStyle: { width: 1.5, color: '#22C55E' }, itemStyle: { color: '#22C55E' },
        areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [{ offset: 0, color: 'rgba(34,197,94,0.1)' }, { offset: 1, color: 'rgba(34,197,94,0)' }] } },
        symbolSize: 3, symbol: 'circle' }],
      tooltip: { trigger: 'axis', backgroundColor: '#fff', borderColor: '#E2E8F0', textStyle: { color: '#0F172A', fontSize: 11, fontFamily: 'monospace' } },
    }, false)

    // Pro: Panel 3 — Distribution Donut
    const o = overviewData.value
    const pieData = [
      { value: o.totalCourses || 0, name: 'Courses', itemStyle: { color: '#22C55E' } },
      { value: o.totalUsers || 0, name: 'Users', itemStyle: { color: '#0284C7' } },
      { value: o.totalEnrollments || 0, name: 'Enrolls', itemStyle: { color: '#EAB308' } },
      { value: o.completionRate || 0, name: 'Completion', itemStyle: { color: '#A855F7' } },
    ].filter(d => d.value > 0)
    setOption2({
      series: [{ type: 'pie', radius: ['45%', '70%'], center: ['50%', '50%'], data: pieData,
        label: { show: true, color: '#64748B', fontSize: 10, fontFamily: 'monospace' },
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.1)' } } }],
      tooltip: { trigger: 'item', backgroundColor: '#fff', borderColor: '#E2E8F0',
        textStyle: { color: '#0F172A', fontSize: 11, fontFamily: 'monospace' }, formatter: '{b}: {c} ({d}%)' },
    }, false)
  }
}

/* ── Data Loading ── */
onMounted(async () => {
  try {
    overviewData.value = (await statsApi.getOverview()) || {}
  } catch {
    overviewData.value = {}
  }
  try {
    hot.value = ((await statsApi.getCourseHot()) as any) || []
  } catch { /* empty */ }

  loading.value = false
  await nextTick()

  try {
    trendData.value = ((await statsApi.getEnrollmentTrend?.()) as any) || []
  } catch { /* empty */ }

  await nextTick()
  buildCharts()
})

watch(theme, async () => {
  await nextTick()
  buildCharts()
})
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
.notion-summary-card {
  padding: 24px;
  background: linear-gradient(135deg, #FFF3D6 0%, #FFECD2 100%);
  border: 2px solid #FBCF33;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5A800;
  text-align: center;
}
.notion-stat {
  padding: 16px;
  background: white;
  border: 2px solid #E5E7EB;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 2px 0 #E5E7EB;
  transition: all 0.15s;
}
.notion-card {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
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
.grafana-metric {
  padding: 12px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 4px;
}
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
</style>
