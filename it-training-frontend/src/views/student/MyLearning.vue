<template>
  <div>
    <!-- ── Themed Tab Bar ── -->
    <div :class="tabBarWrapperClass">
      <button
        v-for="t in tabs" :key="t.key"
        @click="activeTab = t.key"
        :class="[tabBaseClass, activeTab === t.key ? tabActiveClass : tabIdleClass]"
      >
        <component :is="t.icon" class="w-4 h-4" :stroke-width="1.75" />
        {{ t.label }}
      </button>
    </div>

    <!-- ── Tab Content ── -->
    <KeepAlive>
      <MyCourses v-if="activeTab === 'courses'" />
      <LearningPlanPage v-else-if="activeTab === 'plan'" />
      <LearningCenterPage v-else-if="activeTab === 'checkin'" />
    </KeepAlive>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useThemeStore } from '@/stores/theme'
import MyCourses from '@/views/student/MyCourses.vue'
import LearningPlanPage from '@/views/student/LearningPlan.vue'
import LearningCenterPage from '@/views/student/LearningCenter.vue'
import { Library, CalendarRange, CheckCircle } from 'lucide-vue-next'

const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

const tabs = [
  { key: 'courses', label: '我的课程', icon: Library },
  { key: 'plan', label: '学习计划', icon: CalendarRange },
  { key: 'checkin', label: '打卡日历', icon: CheckCircle },
]

const activeTab = ref('courses')

/* ── Theme-aware Tab Styles ── */
const tabBarWrapperClass = computed(() => ({
  light: 'flex gap-1 mb-6 pb-3 border-b border-[#E3E8EE]',
  dark: 'flex gap-1 mb-6 pb-3 border-b border-white/[0.04]',
  warm: 'flex gap-1.5 mb-6 pb-3 border-b border-[#E7E5E4]',
  pro: 'flex gap-0.5 mb-6 pb-3 border-b border-[#E2E8F0]',
}[theme.value] || 'flex gap-1 mb-6 pb-3 border-b border-border'))

const tabBaseClass = 'inline-flex items-center gap-1.5 px-3.5 py-2 text-[13px] font-medium border-none cursor-pointer transition-all'

const tabActiveClass = computed(() => ({
  light: 'bg-[#635BFF]/10 text-[#635BFF] font-semibold rounded-md',
  dark: 'bg-[#818CF8]/10 text-[#818CF8] font-semibold rounded-lg',
  warm: 'bg-[#D97706]/10 text-[#92400E] font-semibold rounded-full',
  pro: 'text-[#0284C7] font-medium border-b-2 border-[#0284C7] rounded-none pb-2.5',
}[theme.value] || 'bg-primary/10 text-primary rounded-md'))

const tabIdleClass = computed(() => ({
  light: 'text-[#425466] hover:bg-[#F6F9FC] hover:text-[#0A2540] rounded-md',
  dark: 'text-[#6B6B6E] hover:bg-white/[0.03] hover:text-[#A0A0A5] rounded-lg',
  warm: 'text-[#78716C] hover:bg-[#FEF3C7]/50 hover:text-[#292524] rounded-full',
  pro: 'text-[#94A3B8] hover:text-[#475569] rounded-none',
}[theme.value] || 'text-text-secondary hover:bg-surface-alt rounded-md'))
</script>
