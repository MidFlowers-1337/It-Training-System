<template>
  <Teleport to="body">
    <Transition name="wizard-fade">
      <div v-if="visible" class="fixed inset-0 z-[2000] flex items-center justify-center p-4"
           :class="backdropClass">
        <!-- Card -->
        <div :class="cardClass" class="animate-[wizardIn_0.35s_cubic-bezier(0.16,1,0.3,1)]">
          <!-- Progress Dots -->
          <div class="flex items-center justify-center gap-2 mb-6">
            <div v-for="(_, i) in steps" :key="i"
                 :class="[dotClass, i === step ? dotActiveClass : dotInactiveClass]"
                 class="transition-all duration-300" />
          </div>

          <!-- Step Content -->
          <Transition :name="slideDirection" mode="out-in">
            <div :key="step" class="text-center">
              <!-- Icon -->
              <div :class="iconBgClass" class="w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-4">
                <component :is="currentStep.icon" class="w-8 h-8" :class="iconColorClass" :stroke-width="1.5" />
              </div>
              <!-- Title -->
              <h3 :class="titleClass" class="mb-2">{{ currentStep.title }}</h3>
              <!-- Description -->
              <p :class="descClass" class="mb-6 max-w-sm mx-auto leading-relaxed">{{ currentStep.desc }}</p>
            </div>
          </Transition>

          <!-- Actions -->
          <div class="flex items-center justify-between">
            <button v-if="step > 0" @click="prev" :class="ghostBtnClass" class="cursor-pointer transition-all">
              上一步
            </button>
            <span v-else />

            <div class="flex items-center gap-3">
              <button @click="skip" :class="skipBtnClass" class="cursor-pointer transition-all text-xs">
                跳过
              </button>
              <button v-if="step < steps.length - 1" @click="next" :class="primaryBtnClass" class="cursor-pointer transition-all">
                下一步
              </button>
              <button v-else @click="finish" :class="finishBtnClass" class="cursor-pointer transition-all">
                开始学习!
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, type Component } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useConfetti } from '@/composables/useConfetti'
import {
  Sparkles, BookOpen, Bot, Trophy,
  BarChart3, Palette,
} from 'lucide-vue-next'

const props = defineProps<{ modelValue: boolean }>()
const emit = defineEmits<{
  'update:modelValue': [val: boolean]
  'complete': []
}>()

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const { pop } = useConfetti()

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const step = ref(0)
const slideDirection = ref('slide-left')

interface WizardStep {
  icon: Component
  title: string
  desc: string
}

const steps: WizardStep[] = [
  {
    icon: Sparkles,
    title: '欢迎来到 IT 智能培训系统',
    desc: '这是一个为你量身打造的学习平台，让我们快速了解几个核心功能吧！',
  },
  {
    icon: BookOpen,
    title: '海量精品课程',
    desc: '浏览涵盖前端、后端、数据库、云计算等领域的专业课程，按章节逐步学习，随时追踪进度。',
  },
  {
    icon: Bot,
    title: 'AI 智能助手',
    desc: '遇到问题？向 AI 助手提问！支持普通对话、学习辅导、代码助手、模拟考试四种模式。',
  },
  {
    icon: Trophy,
    title: '成就与打卡系统',
    desc: '每日打卡保持学习节奏，完成课程解锁成就徽章，让学习充满成就感！',
  },
  {
    icon: Palette,
    title: '四套精美主题',
    desc: '在设置中切换主题风格 —— Light 简约白、Dark 暗夜黑、Warm 活力橙、Pro 专业灰，总有一款适合你。',
  },
]

const currentStep = computed(() => steps[step.value]!)

function next() {
  slideDirection.value = 'slide-left'
  if (step.value < steps.length - 1) step.value++
}

function prev() {
  slideDirection.value = 'slide-right'
  if (step.value > 0) step.value--
}

function skip() {
  markComplete()
  visible.value = false
}

function finish() {
  pop()
  markComplete()
  visible.value = false
  emit('complete')
}

function markComplete() {
  localStorage.setItem('onboarding_completed', 'true')
}

/* ── Theme-aware classes ── */
const backdropClass = computed(() => ({
  light: 'bg-black/30 backdrop-blur-sm',
  dark: 'bg-black/60 backdrop-blur-md',
  warm: 'bg-black/25 backdrop-blur-sm',
  pro: 'bg-black/30 backdrop-blur-sm',
}[theme.value] || 'bg-black/30 backdrop-blur-sm'))

const cardClass = computed(() => ({
  light: 'bg-white rounded-xl p-8 w-full max-w-md shadow-[0_25px_50px_rgba(0,0,0,0.15)] border border-[#E3E8EE]',
  dark: 'bg-[#111113] border border-white/[0.08] rounded-2xl p-8 w-full max-w-md shadow-[0_25px_50px_rgba(0,0,0,0.5)]',
  warm: 'bg-white rounded-3xl p-8 w-full max-w-md border-2 border-[#E5E7EB] shadow-[0_6px_0_#E5E7EB]',
  pro: 'bg-white border border-[#E2E8F0] rounded-lg p-8 w-full max-w-md shadow-[0_25px_50px_rgba(0,0,0,0.08)]',
}[theme.value] || ''))

const dotClass = 'rounded-full'
const dotActiveClass = computed(() => ({
  light: 'w-6 h-2 bg-[#635BFF]',
  dark: 'w-6 h-2 bg-[#818CF8]',
  warm: 'w-6 h-2.5 bg-[#58CC02] shadow-[0_2px_0_#46A302] rounded-full',
  pro: 'w-6 h-1.5 bg-[#0F172A]',
}[theme.value] || 'w-6 h-2 bg-blue-500'))

const dotInactiveClass = computed(() => ({
  light: 'w-2 h-2 bg-[#E3E8EE]',
  dark: 'w-2 h-2 bg-white/[0.08]',
  warm: 'w-2.5 h-2.5 bg-[#E5E7EB]',
  pro: 'w-2 h-1.5 bg-[#E2E8F0]',
}[theme.value] || 'w-2 h-2 bg-gray-200'))

const iconBgClass = computed(() => ({
  light: 'bg-[#635BFF]/10',
  dark: 'bg-[#818CF8]/10',
  warm: 'bg-[#FFC800]/15',
  pro: 'bg-[#F1F5F9]',
}[theme.value] || ''))

const iconColorClass = computed(() => ({
  light: 'text-[#635BFF]',
  dark: 'text-[#818CF8]',
  warm: 'text-[#FF9600]',
  pro: 'text-[#0F172A]',
}[theme.value] || ''))

const titleClass = computed(() => ({
  light: 'text-lg font-bold text-[#0A2540]',
  dark: 'text-lg font-bold text-[#EDEDED]',
  warm: 'text-lg font-extrabold text-[#292524]',
  pro: 'text-sm font-semibold text-[#0F172A] uppercase tracking-wider',
}[theme.value] || ''))

const descClass = computed(() => ({
  light: 'text-sm text-[#425466]',
  dark: 'text-sm text-[#999999]',
  warm: 'text-sm text-[#78716C] font-medium',
  pro: 'text-xs text-[#64748B] leading-relaxed',
}[theme.value] || ''))

const ghostBtnClass = computed(() => ({
  light: 'px-4 py-2 rounded-md text-xs font-medium text-[#425466] border border-[#E3E8EE] hover:bg-[#F6F9FC]',
  dark: 'px-4 py-2 rounded-lg text-xs font-medium text-[#999999] border border-white/[0.08] hover:bg-white/[0.04]',
  warm: 'px-4 py-2 rounded-full text-xs font-bold text-[#78716C] border-2 border-[#E7E5E4] hover:bg-[#FEF3C7]/50',
  pro: 'px-3 py-1.5 rounded-md text-[11px] font-medium text-[#64748B] border border-[#E2E8F0] hover:bg-[#F1F5F9]',
}[theme.value] || ''))

const skipBtnClass = computed(() => ({
  light: 'text-[#8898AA] hover:text-[#425466]',
  dark: 'text-[#6B6B6E] hover:text-[#999999]',
  warm: 'text-[#A8A29E] hover:text-[#78716C] font-bold',
  pro: 'text-[#94A3B8] hover:text-[#64748B] font-mono',
}[theme.value] || ''))

const primaryBtnClass = computed(() => ({
  light: 'px-5 py-2 rounded-md text-xs font-semibold text-white bg-[#635BFF] hover:brightness-110 shadow-[0_1px_3px_rgba(0,0,0,0.08),0_0_0_1px_rgba(0,0,0,0.06)]',
  dark: 'px-5 py-2 rounded-lg text-xs font-medium text-white bg-[#818CF8] hover:shadow-[0_0_20px_rgba(129,140,248,0.3)]',
  warm: 'px-5 py-2.5 rounded-full text-xs font-extrabold text-white bg-[#58CC02] shadow-[0_4px_0_#46A302] hover:brightness-105 active:translate-y-[2px] active:shadow-[0_2px_0_#46A302]',
  pro: 'px-4 py-1.5 rounded-md text-[11px] font-semibold text-white bg-[#0F172A] hover:bg-[#1E293B]',
}[theme.value] || ''))

const finishBtnClass = computed(() => ({
  light: 'px-6 py-2 rounded-md text-xs font-semibold text-white bg-emerald-600 hover:brightness-110 shadow-[0_1px_3px_rgba(0,0,0,0.08)]',
  dark: 'px-6 py-2 rounded-lg text-xs font-medium text-white bg-emerald-500 hover:shadow-[0_0_20px_rgba(16,185,129,0.3)]',
  warm: 'px-6 py-2.5 rounded-full text-xs font-extrabold text-white bg-[#FFC800] shadow-[0_4px_0_#E5A800] hover:brightness-105 active:translate-y-[2px] active:shadow-[0_2px_0_#E5A800]',
  pro: 'px-5 py-1.5 rounded-md text-[11px] font-semibold text-white bg-emerald-600 hover:bg-emerald-700',
}[theme.value] || ''))
</script>

<style scoped>
@keyframes wizardIn {
  from { opacity: 0; transform: scale(0.92) translateY(12px); }
  to { opacity: 1; transform: none; }
}

/* Fade */
.wizard-fade-enter-active { transition: opacity 0.2s ease; }
.wizard-fade-leave-active { transition: opacity 0.15s ease; }
.wizard-fade-enter-from,
.wizard-fade-leave-to { opacity: 0; }

/* Slide transitions for step content */
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.slide-left-enter-from { opacity: 0; transform: translateX(24px); }
.slide-left-leave-to { opacity: 0; transform: translateX(-24px); }
.slide-right-enter-from { opacity: 0; transform: translateX(-24px); }
.slide-right-leave-to { opacity: 0; transform: translateX(24px); }
</style>
