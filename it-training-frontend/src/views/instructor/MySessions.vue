<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Schedule：时间线列表 + 状态 pill + 进度条 + 摘要栏
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">我的班期</h1>
            <p class="text-sm text-[#425466] mt-0.5">查看您负责的培训班次</p>
          </div>
          <span class="text-xs text-[#8898AA]">共 {{ sessions.length }} 个班期</span>
        </div>

        <!-- Summary Stats -->
        <div v-if="sessions.length" class="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <div v-for="(s, i) in summaryStats" :key="i" class="stripe-card !py-3">
            <div class="text-[10px] text-[#8898AA] uppercase tracking-wider font-semibold">{{ s.label }}</div>
            <div class="text-lg font-bold text-[#0A2540] mt-0.5">{{ s.value }}</div>
          </div>
        </div>

        <!-- Filter -->
        <div class="flex gap-2 items-center">
          <select v-model="filterStatus" class="px-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none cursor-pointer focus:border-[#635BFF]" @change="applyFilter">
            <option value="">全部状态</option>
            <option value="PENDING">待开放</option>
            <option value="ENROLLING">报名中</option>
            <option value="CLOSED">已关闭</option>
            <option value="COMPLETED">已完成</option>
          </select>
        </div>

        <!-- Session List -->
        <div v-if="loading" class="text-center py-12 text-sm text-[#8898AA]">加载中...</div>
        <div v-else-if="!filteredSessions.length" class="text-center py-16 text-sm text-[#8898AA]">暂无班期数据</div>
        <div v-else class="space-y-3">
          <div v-for="s in filteredSessions" :key="s.id"
            class="stripe-card !p-0 overflow-hidden hover:shadow-[0_8px_24px_rgba(99,91,255,0.08)] hover:border-[#635BFF]/20 transition-all">
            <div class="flex items-stretch">
              <!-- Left Status Bar -->
              <div :class="['w-1 flex-shrink-0',
                s.status === 'ENROLLING' ? 'bg-emerald-500' :
                s.status === 'COMPLETED' ? 'bg-[#635BFF]' :
                s.status === 'PENDING' ? 'bg-amber-400' : 'bg-[#CBD5E1]']"></div>
              <!-- Content -->
              <div class="flex-1 px-5 py-4">
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <h3 class="text-sm font-semibold text-[#0A2540]">{{ s.courseName }}</h3>
                    <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', stripeStatusBadge(s.status)]">{{ statusLabel(s.status) }}</span>
                  </div>
                  <span class="text-xs text-[#8898AA] tabular-nums">#{{ s.id }}</span>
                </div>
                <div class="flex items-center gap-6 mt-3 text-xs text-[#425466]">
                  <span class="inline-flex items-center gap-1.5">
                    <CalendarDays class="w-3.5 h-3.5 text-[#8898AA]" :stroke-width="1.75" />
                    {{ s.startDate }} ~ {{ s.endDate }}
                  </span>
                  <span class="inline-flex items-center gap-1.5">
                    <MapPin class="w-3.5 h-3.5 text-[#8898AA]" :stroke-width="1.75" />
                    {{ s.location || '线上' }}
                  </span>
                </div>
                <!-- Enrollment Progress -->
                <div class="flex items-center gap-3 mt-3">
                  <div class="flex-1 h-1.5 bg-[#E3E8EE] rounded-full overflow-hidden">
                    <div class="h-full bg-[#635BFF] rounded-full transition-all" :style="{ width: enrollPercent(s) + '%' }"></div>
                  </div>
                  <span class="text-xs text-[#635BFF] font-medium tabular-nums whitespace-nowrap">{{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }} 人</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Discord Events：暗色事件流 + 状态环 + 渐变进度 + 紧凑信息
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">我的班期</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ sessions.length }} sessions</span>
          </div>
        </div>

        <!-- Status Filter Pills -->
        <div class="flex gap-1">
          <button v-for="sf in statusFilters" :key="sf.value"
            :class="['px-3 py-2 rounded-md text-xs font-medium cursor-pointer transition-all',
              filterStatus === sf.value ? 'bg-[#5865F2] text-white' : 'text-[#B5BAC1] bg-white/[0.04] hover:bg-white/[0.06]']"
            @click="filterStatus = sf.value">
            {{ sf.label }}
          </button>
        </div>

        <!-- Event Cards -->
        <div class="raycast-card !p-0">
          <div v-if="loading" class="py-12 text-center text-sm text-[#6B6B6E]">加载中...</div>
          <div v-else-if="!filteredSessions.length" class="py-12 text-center text-sm text-[#6B6B6E]">暂无班期</div>
          <template v-else>
            <div v-for="(s, i) in filteredSessions" :key="s.id"
              :class="['flex items-center gap-4 px-4 py-3.5 hover:bg-white/[0.03] transition-colors', i < filteredSessions.length - 1 && 'border-b border-white/[0.04]']">
              <!-- Status Ring -->
              <div :class="['w-10 h-10 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 border-2',
                s.status === 'ENROLLING' ? 'border-[#23A55A] text-[#23A55A]' :
                s.status === 'COMPLETED' ? 'border-[#5865F2] text-[#5865F2]' :
                s.status === 'PENDING' ? 'border-[#FEE75C] text-[#FEE75C]' : 'border-[#80848E] text-[#80848E]']">
                <component :is="s.status === 'ENROLLING' ? DoorOpen : s.status === 'COMPLETED' ? CheckCircle : s.status === 'PENDING' ? Clock : DoorClosed" class="w-4 h-4" :stroke-width="1.75" />
              </div>
              <!-- Info -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-[#EDEDED] truncate">{{ s.courseName }}</span>
                  <span :class="['px-1.5 py-0.5 rounded text-[9px] font-bold uppercase', darkStatusBadge(s.status)]">{{ statusLabel(s.status) }}</span>
                </div>
                <div class="text-xs text-[#6B6B6E] mt-0.5">
                  {{ s.startDate }} → {{ s.endDate }} · {{ s.location || '线上' }}
                </div>
              </div>
              <!-- Progress -->
              <div class="flex items-center gap-3 flex-shrink-0">
                <div class="text-right">
                  <div class="text-xs font-mono text-[#818CF8]">{{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}</div>
                  <div class="w-14 h-1 bg-white/[0.06] rounded-full overflow-hidden mt-1">
                    <div class="h-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4] rounded-full" :style="{ width: enrollPercent(s) + '%' }"></div>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- Bottom Summary -->
        <div v-if="sessions.length" class="flex gap-3">
          <div v-for="(s, i) in darkSummary" :key="i" class="flex-1 raycast-card !py-3">
            <div class="flex items-center gap-2">
              <component :is="s.icon" class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
              <span class="text-xs text-[#6B6B6E]">{{ s.label }}</span>
            </div>
            <div class="text-lg font-bold text-[#EDEDED] mt-1 font-mono">{{ s.value }}</div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Calendar Board：2 列卡片 + emoji 属性 + 大进度条
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">📅</span>
            <h1 class="text-xl font-extrabold text-[#292524]">我的班期</h1>
          </div>
          <span class="text-xs font-bold text-[#78716C]">共 {{ sessions.length }} 个班期</span>
        </div>

        <!-- Warm Summary Banner -->
        <div v-if="sessions.length && !loading" class="notion-summary-card">
          <div class="text-2xl mb-1">{{ summaryEmoji }}</div>
          <p class="text-base font-extrabold text-[#292524]">{{ warmSummaryText }}</p>
          <p class="text-xs text-[#78716C] mt-1">{{ warmSubText }}</p>
        </div>

        <!-- Filter Tags -->
        <div class="flex gap-1.5">
          <button v-for="sf in statusFilters" :key="sf.value"
            :class="['px-4 py-2 rounded-full text-xs font-bold border-2 cursor-pointer transition-all',
              filterStatus === sf.value ? 'bg-[#292524] text-white border-[#292524] shadow-[0_2px_0_#1C1917]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:border-[#D97706]']"
            @click="filterStatus = sf.value">
            {{ sf.emoji }} {{ sf.label }}
          </button>
        </div>

        <!-- Card Grid -->
        <div v-if="loading" class="text-center py-12 text-sm text-[#78716C]">加载中...</div>
        <div v-else-if="!filteredSessions.length" class="text-center py-12"><span class="text-3xl">📭</span><p class="text-sm text-[#78716C] mt-2">暂无班期数据</p></div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-for="s in filteredSessions" :key="s.id"
            class="notion-card !p-0 overflow-hidden hover:shadow-[0_4px_0_#D97706] hover:border-[#D97706] transition-all">
            <!-- Card Header -->
            <div class="flex items-center justify-between px-5 py-3 bg-[#FFFBF5] border-b-2 border-[#E7E5E4]">
              <div class="flex items-center gap-2">
                <span class="text-base">{{ warmStatusEmoji(s.status) }}</span>
                <span class="text-sm font-extrabold text-[#292524]">{{ s.courseName }}</span>
              </div>
              <span :class="['px-2.5 py-0.5 rounded-full text-[10px] font-extrabold', warmStatusBadge(s.status)]">{{ statusLabel(s.status) }}</span>
            </div>
            <!-- Card Body -->
            <div class="px-5 py-4 space-y-3">
              <div class="grid grid-cols-2 gap-3 text-xs">
                <div><span class="text-[#A8A29E]">📅 日期</span><div class="font-bold text-[#292524] mt-0.5">{{ s.startDate }}</div></div>
                <div><span class="text-[#A8A29E]">🏁 结束</span><div class="font-bold text-[#292524] mt-0.5">{{ s.endDate }}</div></div>
                <div><span class="text-[#A8A29E]">📍 地点</span><div class="font-bold text-[#292524] mt-0.5">{{ s.location || '线上' }}</div></div>
                <div><span class="text-[#A8A29E]">🔢 编号</span><div class="font-bold text-[#292524] mt-0.5">#{{ s.id }}</div></div>
              </div>
              <!-- Progress Bar -->
              <div>
                <div class="flex items-center justify-between text-xs mb-1">
                  <span class="text-[#78716C]">👥 报名进度</span>
                  <span class="font-extrabold text-[#D97706]">{{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}</span>
                </div>
                <div class="w-full h-2.5 bg-[#E7E5E4] rounded-full overflow-hidden">
                  <div class="h-full bg-gradient-to-r from-[#FFC800] to-[#FF9600] rounded-full transition-all" :style="{ width: enrollPercent(s) + '%' }"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana Sessions Panel：monospace 面板表格 + 绿色指标 + 紧凑行
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">My Sessions</span>
            <code class="text-[10px] font-mono text-[#22C55E] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ sessions.length }} records</code>
          </div>
        </div>

        <!-- Metrics Row -->
        <div v-if="sessions.length && !loading" class="grid grid-cols-4 gap-3">
          <div v-for="(s, i) in proMetrics" :key="i" class="grafana-metric">
            <div class="text-[9px] font-mono text-[#94A3B8] uppercase tracking-wider">{{ s.label }}</div>
            <div class="text-xl font-mono font-semibold text-[#22C55E]">{{ s.value }}</div>
          </div>
        </div>

        <!-- Filter -->
        <div class="flex gap-0.5">
          <button v-for="sf in statusFilters" :key="sf.value"
            :class="['px-2.5 py-1.5 rounded text-[10px] font-mono cursor-pointer transition-all',
              filterStatus === sf.value ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']"
            @click="filterStatus = sf.value">
            {{ sf.labelEn }}
          </button>
        </div>

        <!-- Table Panel -->
        <div class="grafana-panel">
          <div class="grafana-panel-header">
            <span>Table — My Sessions</span>
            <span class="text-[#94A3B8]">{{ filteredSessions.length }} rows</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-[#E2E8F0]">
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">id</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">course</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">period</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">capacity</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">location</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">status</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="6" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">loading...</td></tr>
                <tr v-else-if="!filteredSessions.length"><td colspan="6" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">no data</td></tr>
                <tr v-for="s in filteredSessions" :key="s.id" class="border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ s.id }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#0F172A] font-semibold">{{ s.courseName }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ s.startDate }}~{{ s.endDate }}</td>
                  <td class="px-3 py-2 text-center">
                    <span class="text-[11px] font-mono font-semibold" :class="enrollPercent(s) >= 80 ? 'text-[#EF4444]' : 'text-[#22C55E]'">
                      {{ s.enrolledCount ?? 0 }}/{{ s.maxStudents }}
                    </span>
                  </td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ s.location || '—' }}</td>
                  <td class="px-3 py-2 text-center">
                    <span :class="['text-[9px] font-mono font-semibold', proStatusColor(s.status)]">
                      {{ proStatusText(s.status) }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { sessionApi, type Session } from '@/api/session'
import { useThemeStore } from '@/stores/theme'
import {
  CalendarDays, MapPin, Users, BookOpen,
  DoorOpen, DoorClosed, CheckCircle, Clock,
  Zap, FileText,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Status Filters ── */
const statusFilters = [
  { value: '', label: '全部', labelEn: 'all', emoji: '📅' },
  { value: 'PENDING', label: '待开放', labelEn: 'pending', emoji: '⏳' },
  { value: 'ENROLLING', label: '报名中', labelEn: 'enrolling', emoji: '✅' },
  { value: 'CLOSED', label: '已关闭', labelEn: 'closed', emoji: '🔒' },
  { value: 'COMPLETED', label: '已完成', labelEn: 'completed', emoji: '🏆' },
]

/* ── State ── */
const sessions = ref<Session[]>([])
const loading = ref(false)
const filterStatus = ref('')

/* ── Filtered Sessions ── */
const filteredSessions = computed(() => {
  if (!filterStatus.value) return sessions.value
  return sessions.value.filter(s => s.status === filterStatus.value)
})

function applyFilter() { /* computed handles reactivity */ }

/* ── Load ── */
async function loadSessions() {
  loading.value = true
  try {
    sessions.value = ((await sessionApi.getMySessions()) as any) || []
  } catch {
    sessions.value = []
  } finally {
    loading.value = false
  }
}

/* ── Helpers ── */
function statusLabel(s: string) {
  return ({ ENROLLING: '报名中', CLOSED: '已关闭', PENDING: '待开放', COMPLETED: '已完成' })[s] || s
}

function enrollPercent(s: Session) {
  if (!s.maxStudents) return 0
  return Math.min(Math.round(((s.enrolledCount ?? 0) / s.maxStudents) * 100), 100)
}

/* ── Summary Stats (Light) ── */
const summaryStats = computed(() => {
  const all = sessions.value
  return [
    { label: '总班期', value: all.length },
    { label: '报名中', value: all.filter(s => s.status === 'ENROLLING').length },
    { label: '总学员', value: all.reduce((sum, s) => sum + (s.enrolledCount ?? 0), 0) },
    { label: '已完成', value: all.filter(s => s.status === 'COMPLETED').length },
  ]
})

/* ── Dark Summary ── */
const darkSummary = computed(() => [
  { icon: BookOpen, label: '班期', value: sessions.value.length },
  { icon: Users, label: '学员', value: sessions.value.reduce((sum, s) => sum + (s.enrolledCount ?? 0), 0) },
  { icon: Zap, label: '进行中', value: sessions.value.filter(s => s.status === 'ENROLLING').length },
  { icon: FileText, label: '已完成', value: sessions.value.filter(s => s.status === 'COMPLETED').length },
])

/* ── Warm Summary ── */
const summaryEmoji = computed(() => {
  const enrolling = sessions.value.filter(s => s.status === 'ENROLLING').length
  if (enrolling >= 3) return '🔥'
  if (enrolling >= 1) return '✨'
  return '📋'
})

const warmSummaryText = computed(() => {
  const total = sessions.value.length
  const students = sessions.value.reduce((sum, s) => sum + (s.enrolledCount ?? 0), 0)
  return `您负责 ${total} 个班期，共有 ${students} 名学员报名！`
})

const warmSubText = computed(() => {
  const enrolling = sessions.value.filter(s => s.status === 'ENROLLING').length
  const completed = sessions.value.filter(s => s.status === 'COMPLETED').length
  return `${enrolling} 个正在报名中，${completed} 个已完成`
})

/* ── Pro Metrics ── */
const proMetrics = computed(() => [
  { label: 'total', value: sessions.value.length },
  { label: 'active', value: sessions.value.filter(s => s.status === 'ENROLLING').length },
  { label: 'students', value: sessions.value.reduce((sum, s) => sum + (s.enrolledCount ?? 0), 0) },
  { label: 'completed', value: sessions.value.filter(s => s.status === 'COMPLETED').length },
])

/* ── Theme-specific Badges ── */
function stripeStatusBadge(s: string) {
  return ({
    ENROLLING: 'bg-emerald-50 text-emerald-600',
    PENDING: 'bg-amber-50 text-amber-600',
    CLOSED: 'bg-red-50 text-red-500',
    COMPLETED: 'bg-indigo-50 text-[#635BFF]',
  })[s] || 'bg-gray-50 text-gray-600'
}
function darkStatusBadge(s: string) {
  return ({
    ENROLLING: 'bg-[#23A55A]/15 text-[#23A55A]',
    PENDING: 'bg-[#FEE75C]/15 text-[#FEE75C]',
    CLOSED: 'bg-[#ED4245]/15 text-[#ED4245]',
    COMPLETED: 'bg-[#5865F2]/15 text-[#5865F2]',
  })[s] || 'bg-white/10 text-[#B5BAC1]'
}
function warmStatusBadge(s: string) {
  return ({
    ENROLLING: 'bg-emerald-50 text-emerald-700',
    PENDING: 'bg-amber-50 text-amber-700',
    CLOSED: 'bg-red-50 text-red-600',
    COMPLETED: 'bg-[#D97706]/10 text-[#D97706]',
  })[s] || 'bg-gray-50 text-gray-600'
}
function warmStatusEmoji(s: string) {
  return ({ ENROLLING: '✅', PENDING: '⏳', CLOSED: '🔒', COMPLETED: '🏆' })[s] || '📝'
}
function proStatusColor(s: string) {
  return ({
    ENROLLING: 'text-[#22C55E]',
    PENDING: 'text-[#EAB308]',
    CLOSED: 'text-[#EF4444]',
    COMPLETED: 'text-[#0284C7]',
  })[s] || 'text-[#94A3B8]'
}
function proStatusText(s: string) {
  return ({
    ENROLLING: '● enrolling',
    PENDING: '○ pending',
    CLOSED: '○ closed',
    COMPLETED: '● completed',
  })[s] || s.toLowerCase()
}

/* ── Init ── */
onMounted(loadSessions)
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-card {
  padding: 20px; background: #fff; border-radius: 8px;
  border: 1px solid #E3E8EE; box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04);
}

/* ======== RAYCAST (Dark) ======== */
.raycast-card {
  padding: 16px; border-radius: 12px;
  background: #111113; border: 1px solid rgba(255,255,255,0.06);
}

/* ======== NOTION (Warm) ======== */
.notion-summary-card {
  padding: 20px; text-align: center;
  background: linear-gradient(135deg, #FFF3D6 0%, #FFECD2 100%);
  border: 2px solid #FBCF33; border-radius: 20px; box-shadow: 0 3px 0 #E5A800;
}
.notion-card {
  padding: 20px; background: #FFFBF5;
  border: 2px solid #E7E5E4; border-radius: 20px; box-shadow: 0 3px 0 #E7E5E4;
}

/* ======== GRAFANA (Pro) ======== */
.grafana-metric {
  padding: 12px; background: #fff; border: 1px solid #E2E8F0; border-radius: 4px;
}
.grafana-panel { background: #fff; border: 1px solid #E2E8F0; border-radius: 4px; overflow: hidden; }
.grafana-panel-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; background: #F8FAFC; border-bottom: 1px solid #E2E8F0;
  font-size: 10px; font-weight: 600; color: #0F172A; font-family: monospace; text-transform: uppercase; letter-spacing: 0.05em;
}
</style>
