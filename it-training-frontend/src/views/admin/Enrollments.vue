<template>
  <div>
    <!-- ================================================================
         ☀️ LIGHT — Stripe Payments：交易记录表 + 状态 pill + 操作链接
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div>
            <h1 class="text-xl font-bold text-[#0A2540]">报名管理</h1>
            <p class="text-sm text-[#425466] mt-0.5">查看和管理所有报名记录</p>
          </div>
          <span class="text-xs text-[#8898AA]">共 {{ total }} 条记录</span>
        </div>

        <!-- Search & Filter -->
        <div class="stripe-card !py-3 !px-4">
          <div class="flex gap-3 items-center flex-wrap">
            <div class="relative flex-1 min-w-[220px]">
              <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#8898AA] pointer-events-none" :stroke-width="1.75" />
              <input v-model="searchKeyword" type="text" placeholder="搜索课程或学员..."
                class="w-full pl-9 pr-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none focus:border-[#635BFF] focus:ring-2 focus:ring-[#635BFF]/10 transition placeholder-[#8898AA]"
                @input="onSearchDebounced" />
            </div>
            <select v-model="filterStatus" class="px-3 py-2 rounded-lg border border-[#E3E8EE] bg-[#F6F9FC] text-sm text-[#0A2540] outline-none cursor-pointer focus:border-[#635BFF]" @change="loadData">
              <option value="">全部状态</option>
              <option value="ACTIVE">已报名</option>
              <option value="CANCELLED">已取消</option>
              <option value="COMPLETED">已完成</option>
            </select>
          </div>
        </div>

        <!-- Table -->
        <div class="stripe-card !p-0 overflow-hidden">
          <div class="overflow-x-auto">
            <table class="w-full text-sm">
              <thead>
                <tr class="border-b border-[#E3E8EE]">
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">ID</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">课程</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">学员</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">班期</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">状态</th>
                  <th class="text-left px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">报名时间</th>
                  <th class="text-right px-4 py-3 text-[10px] font-semibold text-[#8898AA] uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="7" class="text-center py-12 text-sm text-[#8898AA]">加载中...</td></tr>
                <tr v-else-if="!enrollments.length"><td colspan="7" class="text-center py-12 text-sm text-[#8898AA]">暂无报名数据</td></tr>
                <tr v-for="e in enrollments" :key="e.id" class="border-b border-[#F0F3F7] last:border-0 hover:bg-[#F6F9FC]/50 transition-colors">
                  <td class="px-4 py-3 text-[#8898AA] tabular-nums">{{ e.id }}</td>
                  <td class="px-4 py-3 font-medium text-[#0A2540]">{{ e.courseName || '-' }}</td>
                  <td class="px-4 py-3 text-[#425466]">{{ e.userName || e.userId }}</td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs tabular-nums">#{{ e.sessionId }}</td>
                  <td class="px-4 py-3">
                    <span :class="['px-2.5 py-0.5 rounded-full text-xs font-medium', stripeStatusBadge(e.status)]">{{ statusLabel(e.status) }}</span>
                  </td>
                  <td class="px-4 py-3 text-[#8898AA] text-xs tabular-nums">{{ formatDate(e.enrolledAt) }}</td>
                  <td class="px-4 py-3">
                    <div class="flex items-center justify-end gap-1">
                      <button v-if="e.status === 'ACTIVE'" class="text-xs text-red-500 hover:underline cursor-pointer" @click="cancelEnrollment(e)">取消报名</button>
                      <span v-else class="text-xs text-[#CBD5E1]">—</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t border-[#E3E8EE]">
            <span class="text-xs text-[#8898AA]">共 <strong class="text-[#0A2540]">{{ total }}</strong> 条</span>
            <div class="flex items-center gap-1">
              <button class="stripe-page-btn" :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#CBD5E1]">…</span>
                <button v-else :class="['stripe-page-btn', p === currentPage && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="stripe-page-btn" :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Discord Activity Log：暗色活动流 + 时间戳 + 角色标签
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <h1 class="text-xl font-bold text-[#EDEDED]">报名管理</h1>
            <span class="text-[10px] font-mono text-[#6B6B6E] bg-white/[0.04] px-2 py-0.5 rounded">{{ total }} records</span>
          </div>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center">
          <div class="relative flex-1">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#6B6B6E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="搜索..."
              class="w-full pl-9 pr-3 py-2 rounded-md bg-white/[0.04] border border-white/[0.06] text-sm text-[#EDEDED] outline-none focus:border-[#5865F2] transition placeholder-[#6B6B6E]"
              @input="onSearchDebounced" />
          </div>
          <div class="flex gap-1">
            <button v-for="sf in statusFilters" :key="sf.value"
              :class="['px-3 py-2 rounded-md text-xs font-medium cursor-pointer transition-all',
                filterStatus === sf.value ? 'bg-[#5865F2] text-white' : 'text-[#B5BAC1] bg-white/[0.04] hover:bg-white/[0.06]']"
              @click="filterStatus = sf.value; loadData()">
              {{ sf.label }}
            </button>
          </div>
        </div>

        <!-- Activity List -->
        <div class="raycast-card !p-0">
          <div v-if="loading" class="py-12 text-center text-sm text-[#6B6B6E]">加载中...</div>
          <div v-else-if="!enrollments.length" class="py-12 text-center text-sm text-[#6B6B6E]">暂无记录</div>
          <template v-else>
            <div v-for="(e, i) in enrollments" :key="e.id"
              :class="['flex items-center gap-4 px-4 py-3.5 hover:bg-white/[0.03] transition-colors', i < enrollments.length - 1 && 'border-b border-white/[0.04]']">
              <!-- Status Indicator -->
              <div :class="['w-9 h-9 rounded-full flex items-center justify-center text-sm flex-shrink-0',
                e.status === 'ACTIVE' ? 'bg-[#23A55A]/15 text-[#23A55A]' :
                e.status === 'COMPLETED' ? 'bg-[#5865F2]/15 text-[#5865F2]' : 'bg-white/[0.06] text-[#80848E]']">
                <component :is="e.status === 'ACTIVE' ? CheckCircle : e.status === 'COMPLETED' ? Award : XCircle" class="w-4 h-4" :stroke-width="1.75" />
              </div>
              <!-- Info -->
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-medium text-[#EDEDED] truncate">{{ e.userName || e.userId }}</span>
                  <span class="text-[10px] text-[#6B6B6E]">报名了</span>
                  <span class="text-sm text-[#818CF8] truncate">{{ e.courseName || '-' }}</span>
                </div>
                <div class="text-xs text-[#6B6B6E] mt-0.5">
                  ID: {{ e.id }} · 班期 #{{ e.sessionId }} · {{ formatDate(e.enrolledAt) }}
                </div>
              </div>
              <!-- Status + Actions -->
              <div class="flex items-center gap-2 flex-shrink-0">
                <span :class="['px-2 py-0.5 rounded text-[9px] font-bold uppercase', darkStatusBadge(e.status)]">{{ statusLabel(e.status) }}</span>
                <button v-if="e.status === 'ACTIVE'" class="w-7 h-7 rounded flex items-center justify-center text-[#B5BAC1] hover:text-[#ED4245] hover:bg-[#ED4245]/10 transition cursor-pointer" title="取消" @click="cancelEnrollment(e)">
                  <XCircle class="w-3.5 h-3.5" :stroke-width="1.75" />
                </button>
              </div>
            </div>
          </template>
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t border-white/[0.04]">
            <span class="text-[10px] font-mono text-[#6B6B6E]">{{ total }} records · page {{ currentPage }}/{{ totalPages }}</span>
            <div class="flex items-center gap-1">
              <button class="discord-page-btn" :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#6B6B6E]">…</span>
                <button v-else :class="['discord-page-btn', p === currentPage && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="discord-page-btn" :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Timeline：时间线卡片 + Emoji 状态 + 属性行
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="text-lg">📋</span>
            <h1 class="text-xl font-extrabold text-[#292524]">报名管理</h1>
          </div>
          <span class="text-xs font-bold text-[#78716C]">共 {{ total }} 条记录</span>
        </div>

        <!-- Filter -->
        <div class="flex items-center gap-2 flex-wrap">
          <div class="relative flex-1 min-w-[200px]">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#A8A29E] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="搜索..."
              class="w-full pl-9 pr-3 py-2.5 rounded-2xl border-2 border-[#E7E5E4] bg-white text-sm text-[#292524] outline-none focus:border-[#D97706] transition placeholder-[#A8A29E]"
              @input="onSearchDebounced" />
          </div>
          <div class="flex gap-1.5">
            <button v-for="sf in statusFilters" :key="sf.value"
              :class="['px-4 py-2 rounded-full text-xs font-bold border-2 cursor-pointer transition-all',
                filterStatus === sf.value ? 'bg-[#292524] text-white border-[#292524] shadow-[0_2px_0_#1C1917]' : 'bg-white text-[#292524] border-[#E7E5E4] hover:border-[#D97706]']"
              @click="filterStatus = sf.value; loadData()">
              {{ sf.emoji }} {{ sf.label }}
            </button>
          </div>
        </div>

        <!-- Card List -->
        <div class="notion-card !p-0 overflow-hidden">
          <div v-if="loading" class="py-12 text-center text-sm text-[#78716C]">加载中...</div>
          <div v-else-if="!enrollments.length" class="py-12 text-center"><span class="text-3xl">📭</span><p class="text-sm text-[#78716C] mt-2">暂无报名记录</p></div>
          <template v-else>
            <div v-for="e in enrollments" :key="e.id"
              class="flex items-center gap-4 px-5 py-4 border-b border-[#E7E5E4] last:border-0 hover:bg-[#D97706]/[0.03] transition-colors">
              <span class="text-2xl flex-shrink-0">{{ warmStatusEmoji(e.status) }}</span>
              <div class="flex-1 min-w-0">
                <div class="flex items-center gap-2">
                  <span class="text-sm font-extrabold text-[#292524]">{{ e.userName || e.userId }}</span>
                  <span class="text-xs text-[#78716C]">→</span>
                  <span class="text-sm font-bold text-[#D97706]">{{ e.courseName || '-' }}</span>
                </div>
                <div class="text-xs text-[#A8A29E] mt-0.5">
                  ID: {{ e.id }} · 班期 #{{ e.sessionId }} · {{ formatDate(e.enrolledAt) }}
                </div>
              </div>
              <div class="flex items-center gap-2 flex-shrink-0">
                <span :class="['px-2.5 py-0.5 rounded-full text-[10px] font-extrabold', warmStatusBadge(e.status)]">{{ statusLabel(e.status) }}</span>
                <button v-if="e.status === 'ACTIVE'" class="w-7 h-7 rounded-xl flex items-center justify-center text-[#78716C] hover:text-red-600 hover:bg-red-600/10 transition cursor-pointer" title="取消" @click="cancelEnrollment(e)">
                  <XCircle class="w-3.5 h-3.5" :stroke-width="2" />
                </button>
              </div>
            </div>
          </template>
          <div v-if="total > 0" class="flex items-center justify-between px-4 py-3 border-t-2 border-[#E7E5E4] bg-[#FFFBF5]">
            <span class="text-xs font-bold text-[#78716C]">共 {{ total }} 条</span>
            <div class="flex items-center gap-1">
              <button class="warm-page-btn" :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-1 text-xs text-[#A8A29E]">…</span>
                <button v-else :class="['warm-page-btn', p === currentPage && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="warm-page-btn" :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana Event Log：monospace + 绿色指标 + 紧凑事件表
         ================================================================ -->
    <template v-else>
      <div class="space-y-3">
        <div class="flex items-center justify-between pb-3 border-b border-[#E2E8F0]">
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">Enrollment Log</span>
            <code class="text-[10px] font-mono text-[#22C55E] bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ total }} records</code>
          </div>
        </div>

        <!-- Search + Filter -->
        <div class="flex gap-2 items-center">
          <div class="relative flex-1">
            <Search class="absolute left-2.5 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#94A3B8] pointer-events-none" :stroke-width="1.75" />
            <input v-model="searchKeyword" type="text" placeholder="search enrollments..."
              class="w-full pl-8 pr-3 py-1.5 rounded border border-[#E2E8F0] bg-[#F8FAFC] text-[11px] font-mono text-[#0F172A] outline-none focus:border-[#0284C7] transition placeholder-[#94A3B8]"
              @input="onSearchDebounced" />
          </div>
          <div class="flex gap-0.5">
            <button v-for="sf in statusFilters" :key="sf.value"
              :class="['px-2.5 py-1.5 rounded text-[10px] font-mono cursor-pointer transition-all',
                filterStatus === sf.value ? 'bg-[#0F172A] text-white' : 'text-[#64748B] hover:bg-[#F1F5F9]']"
              @click="filterStatus = sf.value; loadData()">
              {{ sf.labelEn }}
            </button>
          </div>
        </div>

        <!-- Table -->
        <div class="grafana-panel">
          <div class="grafana-panel-header">
            <span>Table — Enrollments</span>
            <span class="text-[#94A3B8]">{{ enrollments.length }} rows / {{ total }} total</span>
          </div>
          <div class="overflow-x-auto">
            <table class="w-full">
              <thead>
                <tr class="border-b border-[#E2E8F0]">
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">id</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">course</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">user</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">session</th>
                  <th class="text-center px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">status</th>
                  <th class="text-left px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">enrolled_at</th>
                  <th class="text-right px-3 py-2 text-[9px] font-mono font-semibold text-[#94A3B8] uppercase tracking-wider">actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="loading"><td colspan="7" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">loading...</td></tr>
                <tr v-else-if="!enrollments.length"><td colspan="7" class="text-center py-8 text-[10px] font-mono text-[#94A3B8]">no data</td></tr>
                <tr v-for="e in enrollments" :key="e.id" class="border-b border-[#F1F5F9] last:border-0 hover:bg-[#F8FAFC] transition-colors">
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ e.id }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#0F172A] font-semibold">{{ e.courseName || '—' }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#64748B]">{{ e.userName || e.userId }}</td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">#{{ e.sessionId }}</td>
                  <td class="px-3 py-2 text-center">
                    <span :class="['text-[9px] font-mono font-semibold', proStatusColor(e.status)]">
                      {{ e.status === 'ACTIVE' ? '● active' : e.status === 'COMPLETED' ? '● completed' : '○ cancelled' }}
                    </span>
                  </td>
                  <td class="px-3 py-2 text-[11px] font-mono text-[#94A3B8]">{{ e.enrolledAt ? new Date(e.enrolledAt).toISOString().split('T')[0] : '—' }}</td>
                  <td class="px-3 py-2">
                    <div class="flex items-center justify-end">
                      <button v-if="e.status === 'ACTIVE'" class="px-1.5 py-0.5 rounded text-[9px] font-mono text-[#DC2626] hover:bg-[#DC2626]/5 transition cursor-pointer" @click="cancelEnrollment(e)">cancel</button>
                      <span v-else class="text-[9px] font-mono text-[#CBD5E1]">—</span>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-if="total > 0" class="flex items-center justify-between px-3 py-2 border-t border-[#E2E8F0] bg-[#F8FAFC]">
            <code class="text-[9px] text-[#94A3B8]">showing {{ (currentPage - 1) * pageSize + 1 }}-{{ Math.min(currentPage * pageSize, total) }} of {{ total }}</code>
            <div class="flex items-center gap-0.5">
              <button class="grafana-page-btn" :disabled="currentPage <= 1" @click="goPage(currentPage - 1)">‹</button>
              <template v-for="p in displayedPages" :key="p">
                <span v-if="p === '...'" class="px-0.5 text-[9px] text-[#94A3B8]">…</span>
                <button v-else :class="['grafana-page-btn', p === currentPage && 'active']" @click="goPage(p as number)">{{ p }}</button>
              </template>
              <button class="grafana-page-btn" :disabled="currentPage >= totalPages" @click="goPage(currentPage + 1)">›</button>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { enrollmentApi, type Enrollment } from '@/api/enrollment'
import { useThemeStore } from '@/stores/theme'
import { toast } from '@/composables/useToast'
import { Search, CheckCircle, XCircle, Award } from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

/* ── Status Filters ── */
const statusFilters = [
  { value: '', label: '全部', labelEn: 'all', emoji: '📋' },
  { value: 'ACTIVE', label: '已报名', labelEn: 'active', emoji: '✅' },
  { value: 'CANCELLED', label: '已取消', labelEn: 'cancelled', emoji: '❌' },
  { value: 'COMPLETED', label: '已完成', labelEn: 'completed', emoji: '🏆' },
]

/* ── State ── */
const enrollments = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const searchKeyword = ref('')
const filterStatus = ref('')
let searchTimer: ReturnType<typeof setTimeout> | null = null

/* ── Pagination ── */
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)))
const displayedPages = computed(() => {
  const pages: (number | string)[] = []
  const tp = totalPages.value, cp = currentPage.value
  if (tp <= 7) { for (let i = 1; i <= tp; i++) pages.push(i); return pages }
  pages.push(1)
  if (cp > 3) pages.push('...')
  for (let i = Math.max(2, cp - 1); i <= Math.min(tp - 1, cp + 1); i++) pages.push(i)
  if (cp < tp - 2) pages.push('...')
  pages.push(tp)
  return pages
})
function goPage(p: number) { if (p >= 1 && p <= totalPages.value) { currentPage.value = p; loadData() } }

/* ── Load ── */
async function loadData() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: currentPage.value, size: pageSize.value }
    if (searchKeyword.value.trim()) params.keyword = searchKeyword.value.trim()
    if (filterStatus.value) params.status = filterStatus.value
    const res: any = await enrollmentApi.list(params)
    enrollments.value = res?.records || res?.content || res?.data || res || []
    total.value = res?.total ?? res?.totalElements ?? (Array.isArray(res) ? res.length : 0)
  } catch (e: any) {
    toast.error(e?.message || '加载失败')
    enrollments.value = []; total.value = 0
  } finally { loading.value = false }
}

function onSearchDebounced() {
  if (searchTimer) clearTimeout(searchTimer)
  searchTimer = setTimeout(() => { currentPage.value = 1; loadData() }, 300)
}

/* ── Cancel ── */
async function cancelEnrollment(e: any) {
  try {
    await enrollmentApi.cancel(e.id)
    toast.success('已取消报名')
    loadData()
  } catch (err: any) {
    toast.error(err?.message || '取消失败')
  }
}

/* ── Helpers ── */
function statusLabel(s: string) { return ({ ACTIVE: '已报名', CANCELLED: '已取消', COMPLETED: '已完成' })[s] || s }
function formatDate(d: string) { if (!d) return '-'; try { return new Date(d).toLocaleDateString('zh-CN') } catch { return d } }

/* Theme-specific badges */
function stripeStatusBadge(s: string) {
  return ({ ACTIVE: 'bg-emerald-50 text-emerald-600', COMPLETED: 'bg-indigo-50 text-[#635BFF]', CANCELLED: 'bg-red-50 text-red-500' })[s] || 'bg-gray-50 text-gray-600'
}
function darkStatusBadge(s: string) {
  return ({ ACTIVE: 'bg-[#23A55A]/15 text-[#23A55A]', COMPLETED: 'bg-[#5865F2]/15 text-[#5865F2]', CANCELLED: 'bg-[#ED4245]/15 text-[#ED4245]' })[s] || 'bg-white/10 text-[#B5BAC1]'
}
function warmStatusBadge(s: string) {
  return ({ ACTIVE: 'bg-emerald-50 text-emerald-700', COMPLETED: 'bg-[#D97706]/10 text-[#D97706]', CANCELLED: 'bg-red-50 text-red-600' })[s] || 'bg-gray-50 text-gray-600'
}
function warmStatusEmoji(s: string) { return ({ ACTIVE: '✅', COMPLETED: '🏆', CANCELLED: '❌' })[s] || '📝' }
function proStatusColor(s: string) { return ({ ACTIVE: 'text-[#22C55E]', COMPLETED: 'text-[#0284C7]', CANCELLED: 'text-[#EF4444]' })[s] || 'text-[#94A3B8]' }

onMounted(loadData)
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-card { padding: 20px; background: #fff; border-radius: 8px; border: 1px solid #E3E8EE; box-shadow: 0 15px 35px rgba(60,66,87,0.08), 0 5px 15px rgba(0,0,0,0.04); }
.stripe-page-btn { min-width: 28px; height: 28px; display: inline-flex; align-items: center; justify-content: center; padding: 0 6px; border: 1px solid #E3E8EE; border-radius: 6px; background: #fff; color: #425466; font-size: 12px; cursor: pointer; transition: all 0.15s; }
.stripe-page-btn:hover:not(:disabled):not(.active) { border-color: #635BFF; color: #635BFF; }
.stripe-page-btn.active { background: #635BFF; border-color: #635BFF; color: #fff; }
.stripe-page-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* ======== RAYCAST (Dark) ======== */
.raycast-card { padding: 16px; border-radius: 12px; background: #111113; border: 1px solid rgba(255,255,255,0.06); }
.discord-page-btn { min-width: 26px; height: 26px; display: inline-flex; align-items: center; justify-content: center; padding: 0 5px; border: none; border-radius: 4px; background: transparent; color: #B5BAC1; font-size: 12px; cursor: pointer; transition: all 0.15s; }
.discord-page-btn:hover:not(:disabled):not(.active) { background: rgba(255,255,255,0.06); color: #EDEDED; }
.discord-page-btn.active { background: #5865F2; color: #fff; }
.discord-page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* ======== NOTION (Warm) ======== */
.notion-card { padding: 20px; background: #FFFBF5; border: 2px solid #E7E5E4; border-radius: 20px; box-shadow: 0 3px 0 #E7E5E4; }
.warm-page-btn { min-width: 30px; height: 30px; display: inline-flex; align-items: center; justify-content: center; padding: 0 6px; border: 2px solid #E7E5E4; border-radius: 10px; background: #fff; color: #292524; font-size: 12px; font-weight: 700; cursor: pointer; transition: all 0.15s; }
.warm-page-btn:hover:not(:disabled):not(.active) { border-color: #D97706; color: #D97706; }
.warm-page-btn.active { background: #292524; border-color: #292524; color: #fff; box-shadow: 0 2px 0 #1C1917; }
.warm-page-btn:disabled { opacity: 0.3; cursor: not-allowed; }

/* ======== GRAFANA (Pro) ======== */
.grafana-panel { background: #fff; border: 1px solid #E2E8F0; border-radius: 4px; overflow: hidden; }
.grafana-panel-header { display: flex; align-items: center; justify-content: space-between; padding: 8px 12px; background: #F8FAFC; border-bottom: 1px solid #E2E8F0; font-size: 10px; font-weight: 600; color: #0F172A; font-family: monospace; text-transform: uppercase; letter-spacing: 0.05em; }
.grafana-page-btn { min-width: 22px; height: 22px; display: inline-flex; align-items: center; justify-content: center; padding: 0 4px; border: 1px solid #E2E8F0; border-radius: 3px; background: #fff; color: #64748B; font-size: 10px; font-family: monospace; cursor: pointer; transition: all 0.15s; }
.grafana-page-btn:hover:not(:disabled):not(.active) { border-color: #0284C7; color: #0284C7; }
.grafana-page-btn.active { background: #0F172A; border-color: #0F172A; color: #fff; }
.grafana-page-btn:disabled { opacity: 0.3; cursor: not-allowed; }
</style>
