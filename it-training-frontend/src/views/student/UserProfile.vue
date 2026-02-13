<template>
  <div class="space-y-6">
    <!-- Header -->
    <div class="flex items-center justify-between">
      <div>
        <h1 :class="pageTitleClass">学习画像</h1>
        <p :class="pageSubClass">管理你的技能标签、学习偏好与能力评估</p>
      </div>
      <button :class="ghostBtnClass" @click="analyzeBehavior" :disabled="analyzing">
        <RefreshCw class="w-4 h-4" :class="analyzing && 'animate-spin'" :stroke-width="1.75" />
        {{ analyzing ? '分析中...' : '重新分析行为' }}
      </button>
    </div>

    <!-- Skill Tags -->
    <div :class="cardClass">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-2">
          <Tag class="w-4 h-4" :class="accentClass" :stroke-width="1.75" />
          <h3 :class="sectionTitleClass">技能标签</h3>
        </div>
        <button v-if="!editingSkills" :class="textBtnClass" @click="startEditSkills">
          <Pencil class="w-3.5 h-3.5" :stroke-width="1.75" />
          编辑
        </button>
        <div v-else class="flex gap-2">
          <button :class="ghostSmClass" @click="cancelEditSkills">取消</button>
          <button :class="primarySmClass" @click="saveSkills" :disabled="savingSkills">
            {{ savingSkills ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>

      <!-- View Mode -->
      <div v-if="!editingSkills" class="flex flex-wrap gap-2">
        <span v-for="t in profile?.skillTags" :key="t" :class="skillTagClass">{{ t }}</span>
        <span v-if="!profile?.skillTags?.length" :class="metaClass">暂无技能标签，点击编辑添加</span>
      </div>

      <!-- Edit Mode -->
      <div v-else>
        <div class="flex flex-wrap gap-2 mb-3">
          <span v-for="(t, i) in editSkills" :key="t" :class="skillTagEditClass">
            {{ t }}
            <button @click="removeSkill(i)" class="ml-1 hover:text-red-500 transition-colors">
              <X class="w-3 h-3" :stroke-width="2" />
            </button>
          </span>
        </div>
        <div class="flex gap-2">
          <input
            v-model="newSkill"
            type="text"
            placeholder="输入技能名称，回车添加"
            :class="inputClass"
            class="flex-1"
            @keyup.enter="addSkill"
          />
          <button :class="ghostSmClass" @click="addSkill">添加</button>
        </div>
        <div class="flex flex-wrap gap-1.5 mt-3">
          <span :class="metaClass">快速添加：</span>
          <button
            v-for="s in quickSkills" :key="s"
            :class="quickTagClass"
            @click="addQuickSkill(s)"
            :disabled="editSkills.includes(s)"
          >
            + {{ s }}
          </button>
        </div>
      </div>
    </div>

    <!-- Two Column -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-4">
      <!-- Learning Preferences -->
      <div :class="cardClass">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <Settings class="w-4 h-4" :class="accentClass" :stroke-width="1.75" />
            <h3 :class="sectionTitleClass">学习偏好</h3>
          </div>
          <button v-if="!editingPrefs" :class="textBtnClass" @click="editingPrefs = true">
            <Pencil class="w-3.5 h-3.5" :stroke-width="1.75" />
            编辑
          </button>
          <div v-else class="flex gap-2">
            <button :class="ghostSmClass" @click="cancelEditPrefs">取消</button>
            <button :class="primarySmClass" @click="savePrefs" :disabled="savingPrefs">
              {{ savingPrefs ? '保存中...' : '保存' }}
            </button>
          </div>
        </div>

        <div class="flex flex-col gap-4">
          <!-- Learning Style -->
          <div class="flex items-start justify-between gap-3">
            <span :class="prefLabelClass">学习风格</span>
            <select v-if="editingPrefs" v-model="editPrefsData.learningStyle" :class="formSelectClass">
              <option value="">请选择</option>
              <option value="visual">视觉型</option>
              <option value="auditory">听觉型</option>
              <option value="reading">阅读型</option>
              <option value="kinesthetic">实践型</option>
            </select>
            <span v-else :class="prefValueClass">{{ styleLabel(profile?.learningStyle) }}</span>
          </div>

          <!-- Weekly Goal -->
          <div class="flex items-start justify-between gap-3">
            <span :class="prefLabelClass">每周学习目标</span>
            <div v-if="editingPrefs" class="flex items-center gap-2">
              <input v-model.number="editPrefsData.weeklyGoalHours" type="number" min="1" max="40" :class="inputClass" class="w-20 text-center" />
              <span :class="metaClass">小时/周</span>
            </div>
            <span v-else :class="prefValueClass">{{ profile?.weeklyGoalHours || 0 }} 小时/周</span>
          </div>

          <!-- Preferred Categories -->
          <div class="flex items-start justify-between gap-3">
            <span :class="prefLabelClass">偏好类别</span>
            <div v-if="editingPrefs" class="flex flex-wrap gap-1.5">
              <button
                v-for="c in categoryOptions" :key="c"
                :class="[catChipClass, editPrefsData.preferredCategories?.includes(c) && catChipActiveClass]"
                @click="toggleCategory(c)"
              >
                {{ c }}
              </button>
            </div>
            <div v-else class="flex flex-wrap gap-1.5">
              <span v-for="c in profile?.preferredCategories" :key="c" :class="[catChipClass, catChipActiveClass]">{{ c }}</span>
              <span v-if="!profile?.preferredCategories?.length" :class="metaClass">未设置</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Ability Radar Chart -->
      <div :class="cardClass" class="relative">
        <div class="flex items-center justify-between mb-4">
          <div class="flex items-center gap-2">
            <Radar class="w-4 h-4" :class="accentClass" :stroke-width="1.75" />
            <h3 :class="sectionTitleClass">能力评估</h3>
          </div>
          <button :class="textBtnClass" @click="refreshAbility" :disabled="abilityLoading">
            <RefreshCw class="w-3.5 h-3.5" :class="abilityLoading && 'animate-spin'" :stroke-width="1.75" />
            刷新
          </button>
        </div>
        <div ref="radarRef" class="w-full h-64"></div>
        <div v-if="!abilityData && !abilityLoading" class="absolute inset-0 flex items-center justify-center">
          <p :class="metaClass">暂无能力评估数据</p>
        </div>
      </div>
    </div>

    <!-- Ability Detail Breakdown -->
    <div v-if="abilityData && Object.keys(abilityData).length" :class="cardClass">
      <div class="flex items-center gap-2 mb-4">
        <BarChart3 class="w-4 h-4" :class="accentClass" :stroke-width="1.75" />
        <h3 :class="sectionTitleClass">能力明细</h3>
      </div>
      <div class="space-y-3">
        <div v-for="(val, key) in abilityData" :key="key" class="flex items-center gap-3">
          <span :class="abilityLabelClass" class="w-24 truncate">{{ key }}</span>
          <div class="flex-1 overflow-hidden" :class="abilityTrackClass">
            <div class="h-full rounded-full transition-all duration-500" :class="abilityBarColor(val as number)" :style="{ width: val + '%' }"></div>
          </div>
          <span class="w-10 text-right text-xs font-medium" :class="abilityTextColor(val as number)">{{ val }}%</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { learningProfileApi, type UserProfile } from '@/api/learningProfile'
import { useECharts } from '@/composables/useECharts'
import { toast } from '@/composables/useToast'
import {
  Tag, Pencil, X, Settings, Radar, RefreshCw, BarChart3,
} from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

// --- Profile Data ---
const profile = ref<UserProfile | null>(null)

// --- Skill Tags Edit ---
const editingSkills = ref(false)
const editSkills = ref<string[]>([])
const newSkill = ref('')
const savingSkills = ref(false)

const quickSkills = ['Java', 'Python', 'JavaScript', 'Vue', 'React', 'Spring Boot', 'SQL', 'Docker', 'Git', 'Linux']

function startEditSkills() {
  editSkills.value = [...(profile.value?.skillTags || [])]
  editingSkills.value = true
}

function cancelEditSkills() {
  editingSkills.value = false
  newSkill.value = ''
}

function addSkill() {
  const s = newSkill.value.trim()
  if (!s) return
  if (editSkills.value.includes(s)) {
    toast.warning('标签已存在')
    return
  }
  editSkills.value.push(s)
  newSkill.value = ''
}

function addQuickSkill(s: string) {
  if (!editSkills.value.includes(s)) {
    editSkills.value.push(s)
  }
}

function removeSkill(index: number) {
  editSkills.value.splice(index, 1)
}

async function saveSkills() {
  savingSkills.value = true
  try {
    await learningProfileApi.updateSkills(editSkills.value)
    if (profile.value) profile.value.skillTags = [...editSkills.value]
    editingSkills.value = false
    toast.success('技能标签已更新')
  } catch (e: any) {
    toast.error(e.message || '保存失败')
  } finally {
    savingSkills.value = false
  }
}

// --- Preferences Edit ---
const editingPrefs = ref(false)
const savingPrefs = ref(false)
const editPrefsData = ref({
  learningStyle: '',
  weeklyGoalHours: 10,
  preferredCategories: [] as string[],
})

const categoryOptions = ['Java', 'Python', '前端', '后端', '数据库', 'AI/ML', '移动端', '安全', '大数据', 'DevOps']

function cancelEditPrefs() {
  editingPrefs.value = false
  resetPrefsForm()
}

function resetPrefsForm() {
  editPrefsData.value = {
    learningStyle: profile.value?.learningStyle || '',
    weeklyGoalHours: profile.value?.weeklyGoalHours || 10,
    preferredCategories: [...(profile.value?.preferredCategories || [])],
  }
}

function toggleCategory(c: string) {
  const arr = editPrefsData.value.preferredCategories
  const idx = arr.indexOf(c)
  if (idx >= 0) arr.splice(idx, 1)
  else arr.push(c)
}

async function savePrefs() {
  savingPrefs.value = true
  try {
    await learningProfileApi.updatePreferences(editPrefsData.value)
    if (profile.value) {
      profile.value.learningStyle = editPrefsData.value.learningStyle
      profile.value.weeklyGoalHours = editPrefsData.value.weeklyGoalHours
      profile.value.preferredCategories = [...editPrefsData.value.preferredCategories]
    }
    editingPrefs.value = false
    toast.success('学习偏好已更新')
  } catch (e: any) {
    toast.error(e.message || '保存失败')
  } finally {
    savingPrefs.value = false
  }
}

function styleLabel(style?: string) {
  const map: Record<string, string> = { visual: '视觉型', auditory: '听觉型', reading: '阅读型', kinesthetic: '实践型' }
  return map[style || ''] || '未设置'
}

// --- Ability Assessment (ECharts Radar) ---
const radarRef = ref<HTMLElement | null>(null)
const { setOption } = useECharts(radarRef)
const abilityData = ref<Record<string, number> | null>(null)
const abilityLoading = ref(false)

// Theme-specific colors for ECharts
const chartColors = computed(() => ({
  light: { primary: '#635BFF', textSec: '#425466', border: '#E3E8EE', areaAlpha: '26' },
  dark: { primary: '#818CF8', textSec: '#A0A0A5', border: 'rgba(255,255,255,0.08)', areaAlpha: '26' },
  warm: { primary: '#D97706', textSec: '#78716C', border: '#E7E5E4', areaAlpha: '26' },
  pro: { primary: '#0284C7', textSec: '#64748B', border: '#E2E8F0', areaAlpha: '26' },
}[theme.value] || { primary: '#635BFF', textSec: '#425466', border: '#E3E8EE', areaAlpha: '26' }))

async function refreshAbility() {
  abilityLoading.value = true
  try {
    const res: any = await learningProfileApi.getAbilityAssessment()
    const scores = res?.abilityScores || res?.scores || res || {}
    abilityData.value = scores
    renderRadar(scores)
  } catch {
    abilityData.value = null
  } finally {
    abilityLoading.value = false
  }
}

async function renderRadar(scores: Record<string, number>) {
  const keys = Object.keys(scores)
  if (!keys.length) return
  await nextTick()
  const c = chartColors.value
  setOption({
    radar: {
      indicator: keys.map(k => ({ name: k, max: 100 })),
      shape: 'polygon',
      radius: '65%',
      axisName: { color: c.textSec, fontSize: 11 },
      splitArea: { show: false },
      splitLine: { lineStyle: { color: c.border } },
      axisLine: { lineStyle: { color: c.border } },
    },
    series: [{
      type: 'radar',
      data: [{
        value: keys.map(k => scores[k]),
        name: '能力值',
        areaStyle: { color: c.primary + c.areaAlpha },
        lineStyle: { color: c.primary, width: 2 },
        itemStyle: { color: c.primary },
      }],
    }],
    tooltip: {},
  })
}

function abilityBarColor(v: number) {
  if (v >= 80) return 'bg-emerald-500'
  if (v >= 50) return theme.value === 'light' ? 'bg-[#635BFF]' : theme.value === 'dark' ? 'bg-[#818CF8]' : theme.value === 'warm' ? 'bg-[#D97706]' : 'bg-[#0284C7]'
  if (v >= 30) return 'bg-amber-500'
  return 'bg-red-500'
}

function abilityTextColor(v: number) {
  if (v >= 80) return 'text-emerald-500'
  if (v >= 50) return theme.value === 'light' ? 'text-[#635BFF]' : theme.value === 'dark' ? 'text-[#818CF8]' : theme.value === 'warm' ? 'text-[#D97706]' : 'text-[#0284C7]'
  if (v >= 30) return 'text-amber-500'
  return 'text-red-500'
}

// --- Analyze Behavior ---
const analyzing = ref(false)

async function analyzeBehavior() {
  analyzing.value = true
  try {
    await learningProfileApi.analyzeBehavior()
    toast.success('行为分析完成，正在刷新画像...')
    await loadProfile()
    await refreshAbility()
  } catch (e: any) {
    toast.error(e.message || '分析失败')
  } finally {
    analyzing.value = false
  }
}

// --- Init ---
async function loadProfile() {
  try {
    const res: any = await learningProfileApi.getProfile()
    profile.value = res
    resetPrefsForm()
    if (res?.abilityScores && Object.keys(res.abilityScores).length) {
      abilityData.value = res.abilityScores
      await nextTick()
      renderRadar(res.abilityScores)
    }
  } catch {
    profile.value = null
  }
}

onMounted(async () => {
  await loadProfile()
  if (!abilityData.value) {
    refreshAbility()
  }
})

/* ═══════════════════════════════════════════════
   Theme-aware Computed Classes
   ═══════════════════════════════════════════════ */
const T = (map: Record<string, string>) => computed(() => map[theme.value] || map.light || '')

const pageTitleClass = T({
  light: 'text-xl font-bold text-[#0A2540]',
  dark: 'text-xl font-bold text-[#EDEDED]',
  warm: 'text-xl font-bold text-[#292524]',
  pro: 'text-sm font-semibold text-[#0F172A] uppercase tracking-wider',
})

const pageSubClass = T({
  light: 'text-sm text-[#425466] mt-1',
  dark: 'text-sm text-[#6B6B6E] mt-1',
  warm: 'text-sm text-[#78716C] mt-1',
  pro: 'text-xs text-[#64748B] mt-1',
})

const cardClass = T({
  light: 'bg-white rounded-lg border border-[#E3E8EE] p-5 shadow-[0_2px_4px_rgba(0,0,0,0.04)]',
  dark: 'bg-[#111113] rounded-xl border border-white/[0.06] p-5',
  warm: 'bg-[#FFFBF5] rounded-xl border border-[#E7E5E4] p-5',
  pro: 'bg-white rounded-md border border-[#E2E8F0] p-5',
})

const accentClass = T({ light: 'text-[#635BFF]', dark: 'text-[#818CF8]', warm: 'text-[#D97706]', pro: 'text-[#0284C7]' })

const sectionTitleClass = T({
  light: 'text-sm font-semibold text-[#0A2540]',
  dark: 'text-sm font-semibold text-[#EDEDED]',
  warm: 'text-sm font-semibold text-[#292524]',
  pro: 'text-xs font-semibold text-[#0F172A] uppercase tracking-wider',
})

const metaClass = T({
  light: 'text-xs text-[#8898AA]',
  dark: 'text-xs text-[#6B6B6E]',
  warm: 'text-xs text-[#A8A29E]',
  pro: 'text-[10px] text-[#94A3B8] font-mono',
})

const ghostBtnClass = T({
  light: 'px-3.5 py-2 rounded-md text-[13px] font-medium text-[#425466] bg-transparent border border-[#E3E8EE] cursor-pointer transition-all hover:bg-[#F6F9FC] inline-flex items-center gap-1.5 disabled:opacity-50 disabled:cursor-not-allowed',
  dark: 'px-3.5 py-2 rounded-lg text-[13px] font-medium text-[#6B6B6E] bg-transparent border border-white/[0.06] cursor-pointer transition-all hover:bg-white/[0.04] inline-flex items-center gap-1.5 disabled:opacity-50 disabled:cursor-not-allowed',
  warm: 'px-3.5 py-2 rounded-lg text-[13px] font-medium text-[#78716C] bg-transparent border border-[#E7E5E4] cursor-pointer transition-all hover:bg-[#FEF3C7]/50 inline-flex items-center gap-1.5 disabled:opacity-50 disabled:cursor-not-allowed',
  pro: 'px-3 py-1.5 rounded-md text-[11px] font-mono font-medium text-[#64748B] bg-transparent border border-[#E2E8F0] cursor-pointer transition-all hover:bg-[#F1F5F9] inline-flex items-center gap-1.5 disabled:opacity-50 disabled:cursor-not-allowed',
})

const ghostSmClass = T({
  light: 'px-2.5 py-1.5 rounded-md text-xs font-medium text-[#425466] bg-transparent border border-[#E3E8EE] cursor-pointer transition-all hover:bg-[#F6F9FC]',
  dark: 'px-2.5 py-1.5 rounded-lg text-xs font-medium text-[#6B6B6E] bg-transparent border border-white/[0.06] cursor-pointer transition-all hover:bg-white/[0.04]',
  warm: 'px-2.5 py-1.5 rounded-lg text-xs font-medium text-[#78716C] bg-transparent border border-[#E7E5E4] cursor-pointer transition-all hover:bg-[#FEF3C7]/50',
  pro: 'px-2.5 py-1.5 rounded-md text-[10px] font-mono font-medium text-[#64748B] bg-transparent border border-[#E2E8F0] cursor-pointer transition-all hover:bg-[#F1F5F9]',
})

const primarySmClass = T({
  light: 'px-3 py-1.5 rounded-md text-xs font-semibold text-white bg-[#635BFF] border-none cursor-pointer transition-all hover:brightness-110 shadow-[0_1px_2px_rgba(0,0,0,0.06),inset_0_1px_0_rgba(255,255,255,0.1)] disabled:opacity-50 disabled:cursor-not-allowed',
  dark: 'px-3 py-1.5 rounded-lg text-xs font-semibold text-white bg-[#818CF8] border-none cursor-pointer transition-all hover:shadow-[0_0_20px_rgba(129,140,248,0.3)] disabled:opacity-50 disabled:cursor-not-allowed',
  warm: 'px-3 py-1.5 rounded-full text-xs font-bold text-white bg-[#D97706] border-none cursor-pointer transition-all hover:bg-[#B45309] disabled:opacity-50 disabled:cursor-not-allowed',
  pro: 'px-3 py-1.5 rounded-md text-[10px] font-semibold text-white bg-[#0F172A] border-none cursor-pointer transition-all hover:bg-[#1E293B] disabled:opacity-50 disabled:cursor-not-allowed',
})

const textBtnClass = T({
  light: 'px-2 py-1 rounded text-xs font-medium text-[#635BFF] bg-transparent border-none cursor-pointer transition-all hover:bg-[#635BFF]/[0.06] inline-flex items-center gap-1',
  dark: 'px-2 py-1 rounded text-xs font-medium text-[#818CF8] bg-transparent border-none cursor-pointer transition-all hover:bg-[#818CF8]/[0.06] inline-flex items-center gap-1',
  warm: 'px-2 py-1 rounded text-xs font-medium text-[#D97706] bg-transparent border-none cursor-pointer transition-all hover:bg-[#D97706]/[0.06] inline-flex items-center gap-1',
  pro: 'px-2 py-1 rounded text-[10px] font-mono font-medium text-[#0284C7] bg-transparent border-none cursor-pointer transition-all hover:bg-[#0284C7]/[0.06] inline-flex items-center gap-1',
})

const inputClass = T({
  light: 'px-3 py-2 rounded-md border border-[#E3E8EE] bg-white text-[#0A2540] text-[13px] outline-none placeholder-[#8898AA] focus:border-[#635BFF] focus:shadow-[0_0_0_3px_rgba(99,91,255,0.1)] transition-all',
  dark: 'px-3 py-2 rounded-lg border border-white/[0.06] bg-[#08090A] text-[#EDEDED] text-[13px] outline-none placeholder-[#6B6B6E] focus:border-[#818CF8] focus:shadow-[0_0_0_3px_rgba(129,140,248,0.1)] transition-all',
  warm: 'px-3 py-2 rounded-lg border border-[#E7E5E4] bg-white text-[#292524] text-[13px] outline-none placeholder-[#A8A29E] focus:border-[#D97706] focus:shadow-[0_0_0_3px_rgba(217,119,6,0.1)] transition-all',
  pro: 'px-3 py-1.5 rounded-md border border-[#E2E8F0] bg-white text-[#0F172A] text-xs font-mono outline-none placeholder-[#94A3B8] focus:border-[#0284C7] focus:shadow-[0_0_0_3px_rgba(2,132,199,0.1)] transition-all',
})

const formSelectClass = T({
  light: 'px-2.5 py-1.5 rounded-md border border-[#E3E8EE] bg-white text-[#0A2540] text-[13px] outline-none cursor-pointer focus:border-[#635BFF] focus:shadow-[0_0_0_3px_rgba(99,91,255,0.1)]',
  dark: 'px-2.5 py-1.5 rounded-lg border border-white/[0.06] bg-[#111113] text-[#EDEDED] text-[13px] outline-none cursor-pointer focus:border-[#818CF8] focus:shadow-[0_0_0_3px_rgba(129,140,248,0.1)]',
  warm: 'px-2.5 py-1.5 rounded-lg border border-[#E7E5E4] bg-[#FFFBF5] text-[#292524] text-[13px] outline-none cursor-pointer focus:border-[#D97706] focus:shadow-[0_0_0_3px_rgba(217,119,6,0.1)]',
  pro: 'px-2.5 py-1.5 rounded-md border border-[#E2E8F0] bg-white text-[#0F172A] text-xs font-mono outline-none cursor-pointer focus:border-[#0284C7] focus:shadow-[0_0_0_3px_rgba(2,132,199,0.1)]',
})

const skillTagClass = T({
  light: 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-[13px] font-medium bg-[#635BFF]/[0.08] text-[#635BFF]',
  dark: 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-[13px] font-medium bg-[#818CF8]/[0.08] text-[#818CF8]',
  warm: 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-[13px] font-medium bg-[#D97706]/[0.08] text-[#D97706]',
  pro: 'inline-flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-mono font-medium bg-[#0284C7]/[0.08] text-[#0284C7]',
})

const skillTagEditClass = T({
  light: 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-[13px] font-medium bg-[#635BFF]/[0.12] text-[#635BFF] pr-2',
  dark: 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-[13px] font-medium bg-[#818CF8]/[0.12] text-[#818CF8] pr-2',
  warm: 'inline-flex items-center gap-1 px-3 py-1 rounded-full text-[13px] font-medium bg-[#D97706]/[0.12] text-[#D97706] pr-2',
  pro: 'inline-flex items-center gap-1 px-2.5 py-0.5 rounded text-xs font-mono font-medium bg-[#0284C7]/[0.12] text-[#0284C7] pr-1.5',
})

const quickTagClass = T({
  light: 'px-2 py-0.5 rounded-full text-[11px] font-medium bg-[#F6F9FC] text-[#8898AA] border border-[#E3E8EE]/50 cursor-pointer transition-all hover:bg-[#635BFF]/[0.06] hover:text-[#635BFF] hover:border-[#635BFF]/20 disabled:opacity-40 disabled:cursor-not-allowed',
  dark: 'px-2 py-0.5 rounded-full text-[11px] font-medium bg-white/[0.03] text-[#6B6B6E] border border-white/[0.04] cursor-pointer transition-all hover:bg-[#818CF8]/[0.06] hover:text-[#818CF8] hover:border-[#818CF8]/20 disabled:opacity-40 disabled:cursor-not-allowed',
  warm: 'px-2 py-0.5 rounded-full text-[11px] font-medium bg-[#F5F5F4] text-[#A8A29E] border border-[#E7E5E4]/50 cursor-pointer transition-all hover:bg-[#D97706]/[0.06] hover:text-[#D97706] hover:border-[#D97706]/20 disabled:opacity-40 disabled:cursor-not-allowed',
  pro: 'px-2 py-0.5 rounded text-[10px] font-mono font-medium bg-[#F1F5F9] text-[#94A3B8] border border-[#E2E8F0]/50 cursor-pointer transition-all hover:bg-[#0284C7]/[0.06] hover:text-[#0284C7] hover:border-[#0284C7]/20 disabled:opacity-40 disabled:cursor-not-allowed',
})

const prefLabelClass = T({
  light: 'text-[13px] font-medium text-[#425466] min-w-[100px] pt-0.5',
  dark: 'text-[13px] font-medium text-[#A0A0A5] min-w-[100px] pt-0.5',
  warm: 'text-[13px] font-medium text-[#78716C] min-w-[100px] pt-0.5',
  pro: 'text-xs font-medium text-[#64748B] min-w-[100px] pt-0.5',
})

const prefValueClass = T({
  light: 'text-[13px] font-medium text-[#0A2540]',
  dark: 'text-[13px] font-medium text-[#EDEDED]',
  warm: 'text-[13px] font-medium text-[#292524]',
  pro: 'text-xs font-medium text-[#0F172A]',
})

const catChipClass = T({
  light: 'px-2.5 py-0.5 rounded-full text-xs font-medium bg-[#F6F9FC] text-[#8898AA] border border-[#E3E8EE]/50 cursor-pointer transition-all hover:border-[#635BFF]/30',
  dark: 'px-2.5 py-0.5 rounded-full text-xs font-medium bg-white/[0.03] text-[#6B6B6E] border border-white/[0.04] cursor-pointer transition-all hover:border-[#818CF8]/30',
  warm: 'px-2.5 py-0.5 rounded-full text-xs font-medium bg-[#F5F5F4] text-[#A8A29E] border border-[#E7E5E4]/50 cursor-pointer transition-all hover:border-[#D97706]/30',
  pro: 'px-2.5 py-0.5 rounded text-[10px] font-mono font-medium bg-[#F1F5F9] text-[#94A3B8] border border-[#E2E8F0]/50 cursor-pointer transition-all hover:border-[#0284C7]/30',
})

const catChipActiveClass = T({
  light: '!bg-[#635BFF]/10 !text-[#635BFF] !border-[#635BFF]/30',
  dark: '!bg-[#818CF8]/10 !text-[#818CF8] !border-[#818CF8]/30',
  warm: '!bg-[#D97706]/10 !text-[#D97706] !border-[#D97706]/30',
  pro: '!bg-[#0284C7]/10 !text-[#0284C7] !border-[#0284C7]/30',
})

const abilityLabelClass = T({
  light: 'text-sm text-[#0A2540]',
  dark: 'text-sm text-[#EDEDED]',
  warm: 'text-sm text-[#292524]',
  pro: 'text-xs text-[#0F172A] font-mono',
})

const abilityTrackClass = T({
  light: 'h-2 bg-[#E3E8EE]/40 rounded-full',
  dark: 'h-2 bg-white/[0.06] rounded-full',
  warm: 'h-2.5 bg-[#E7E5E4]/40 rounded-full',
  pro: 'h-1.5 bg-[#E2E8F0]/40 rounded-full',
})
</script>
