<template>
  <div>
    <Transition name="fade" mode="out-in">
    <!-- Skeleton Loading -->
    <div v-if="loading" key="skeleton">
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div v-for="i in 6" :key="i" class="rounded-2xl bg-surface-alt h-32 animate-pulse" :class="i <= 2 ? 'sm:col-span-2' : ''" />
      </div>
    </div>

    <!-- Theme Content -->
    <div v-else key="content">
    <!-- ================================================================
         ☀️ LIGHT — Stripe Dashboard：干净实色卡片 + 多层阴影
         ================================================================ -->
    <template v-if="theme === 'light'">
      <div class="space-y-6">
        <!-- Welcome Bar -->
        <ScrollReveal>
        <div class="stripe-dash-welcome">
          <div>
            <h2 class="text-xl font-semibold text-[#0A2540]">
              欢迎回来{{ userName ? '，' + userName : '' }}
            </h2>
            <p class="text-sm text-[#425466] mt-0.5">今天也要努力学习哦</p>
          </div>
          <button
            v-if="!db?.todayCheckedIn"
            @click="doCheckin"
            class="px-5 py-2 bg-[#635BFF] text-white rounded-md text-sm font-medium hover:brightness-110 transition-all cursor-pointer stripe-btn-shadow"
          >今日打卡</button>
          <span v-else class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-50 text-emerald-600 rounded-md text-sm font-medium">
            <CheckCircle class="w-4 h-4" :stroke-width="2" /> 已打卡
          </span>
        </div>
        </ScrollReveal>

        <!-- 4 Stat Cards -->
        <ScrollReveal :delay="100">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <div v-for="s in stripeStats" :key="s.label" class="stripe-stat-card">
            <div class="text-xs text-[#8898AA] uppercase tracking-wider font-medium">{{ s.label }}</div>
            <div class="text-2xl font-bold text-[#0A2540] mt-1">
              <NumberCounter :value="s.value" :suffix="s.suffix" />
            </div>
          </div>
        </div>
        </ScrollReveal>

        <!-- 2-Column: Recent Courses | Weekly Chart -->
        <ScrollReveal :delay="200">
        <div class="grid lg:grid-cols-2 gap-4">
          <!-- Recent -->
          <div class="stripe-card">
            <h3 class="text-sm font-semibold text-[#0A2540] mb-4">最近学习</h3>
            <div v-if="db?.recentCourses?.length" class="divide-y divide-[#E3E8EE]">
              <div
                v-for="c in db.recentCourses" :key="c.courseId"
                class="flex items-center gap-3 py-3 cursor-pointer hover:bg-[#F6F9FC] -mx-5 px-5 transition-colors"
                @click="$router.push(`/student/courses/${c.courseId}`)"
              >
                <div class="w-8 h-8 rounded-md bg-[#635BFF]/10 flex items-center justify-center text-[#635BFF] font-semibold text-xs">{{ (c.courseName||'')[0] }}</div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium text-[#0A2540] truncate">{{ c.courseName }}</div>
                  <div class="w-full h-1 bg-[#E3E8EE] rounded-full mt-1.5">
                    <div class="h-full bg-[#635BFF] rounded-full" :style="{ width: (c.progress||0) + '%' }"></div>
                  </div>
                </div>
                <span class="text-xs text-[#8898AA] font-mono">{{ c.progress||0 }}%</span>
              </div>
            </div>
            <div v-else class="text-center py-8 text-[#8898AA] text-sm">暂无学习记录</div>
          </div>

          <!-- Weekly -->
          <div class="stripe-card">
            <h3 class="text-sm font-semibold text-[#0A2540] mb-4">本周学习</h3>
            <div v-if="db?.weeklyStats?.length" class="flex items-end gap-3 h-36">
              <div v-for="(d, i) in db.weeklyStats" :key="i" class="flex-1 flex flex-col items-center gap-2">
                <div
                  class="w-full rounded-sm transition-all bg-[#635BFF]"
                  :style="{ height: Math.max((d.hours / mx) * 100, 4) + '%', opacity: 0.6 + (d.hours / mx) * 0.4 }"
                />
                <span class="text-[10px] text-[#8898AA] font-medium">{{ d.day }}</span>
              </div>
            </div>
            <div v-else class="text-center py-8 text-[#8898AA] text-sm">本周暂无数据</div>
          </div>
        </div>
        </ScrollReveal>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Linear Dashboard：Bento + Glow + 渐变数字
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <div class="space-y-5">
        <!-- Welcome -->
        <ScrollReveal>
        <div class="linear-dash-welcome">
          <div>
            <h2 class="text-2xl font-bold">
              <span class="linear-gradient-text">欢迎回来</span>{{ userName ? '，' + userName : '' }}
            </h2>
            <p class="text-sm text-[#6B6B6E] mt-1">今天也要努力学习哦</p>
          </div>
          <button
            v-if="!db?.todayCheckedIn"
            @click="doCheckin"
            class="px-5 py-2 rounded-lg text-sm font-medium bg-[#818CF8] text-white hover:shadow-[0_0_20px_rgba(129,140,248,0.3)] transition-all cursor-pointer"
          >今日打卡</button>
          <span v-else class="inline-flex items-center gap-1.5 px-3 py-1.5 bg-emerald-500/10 text-emerald-400 rounded-lg text-sm font-medium">
            <CheckCircle class="w-4 h-4" :stroke-width="2" /> 已打卡
          </span>
        </div>
        </ScrollReveal>

        <!-- Bento Stats -->
        <ScrollReveal :delay="100">
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
          <div class="linear-bento-card group" v-for="s in linearStats" :key="s.label">
            <component :is="s.icon" class="w-5 h-5 text-[#6B6B6E] group-hover:text-[#818CF8] transition-colors mb-2" :stroke-width="1.75" />
            <div class="text-2xl font-bold linear-gradient-text">
              <NumberCounter :value="s.value" :suffix="s.suffix" />
            </div>
            <div class="text-xs text-[#6B6B6E] mt-0.5">{{ s.label }}</div>
          </div>
        </div>
        </ScrollReveal>

        <!-- Bento Grid: Recent (large) + Weekly (large) -->
        <ScrollReveal :delay="200">
        <div class="grid lg:grid-cols-5 gap-3">
          <!-- Recent — 3 cols -->
          <GlowCard class="lg:col-span-3 p-5">
            <div class="flex items-center gap-2 mb-4">
              <BookMarked class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
              <h3 class="text-sm font-semibold text-[#EDEDED]">最近学习</h3>
            </div>
            <div v-if="db?.recentCourses?.length" class="space-y-2">
              <div
                v-for="c in db.recentCourses" :key="c.courseId"
                class="flex items-center gap-3 p-2.5 rounded-lg hover:bg-white/[0.03] cursor-pointer transition-colors"
                @click="$router.push(`/student/courses/${c.courseId}`)"
              >
                <div class="w-8 h-8 rounded-lg bg-[#818CF8]/10 flex items-center justify-center text-[#818CF8] font-semibold text-xs">{{ (c.courseName||'')[0] }}</div>
                <div class="flex-1 min-w-0">
                  <div class="text-sm font-medium text-[#EDEDED] truncate">{{ c.courseName }}</div>
                  <div class="w-full h-1 bg-white/[0.06] rounded-full mt-1.5">
                    <div class="h-full rounded-full bg-gradient-to-r from-[#818CF8] to-[#06B6D4]" :style="{ width: (c.progress||0) + '%' }"></div>
                  </div>
                </div>
                <span class="text-xs text-[#6B6B6E] font-mono">{{ c.progress||0 }}%</span>
              </div>
            </div>
            <div v-else class="text-center py-8 text-[#6B6B6E] text-sm">暂无学习记录</div>
          </GlowCard>

          <!-- Weekly — 2 cols -->
          <GlowCard class="lg:col-span-2 p-5">
            <div class="flex items-center gap-2 mb-4">
              <BarChart3 class="w-4 h-4 text-[#818CF8]" :stroke-width="1.75" />
              <h3 class="text-sm font-semibold text-[#EDEDED]">本周学习</h3>
            </div>
            <div v-if="db?.weeklyStats?.length" class="flex items-end gap-2 h-32">
              <div v-for="(d, i) in db.weeklyStats" :key="i" class="flex-1 flex flex-col items-center gap-2">
                <div
                  class="w-full rounded-sm bg-gradient-to-t from-[#818CF8] to-[#06B6D4] transition-all"
                  :style="{ height: Math.max((d.hours / mx) * 100, 6) + '%', opacity: 0.5 + (d.hours / mx) * 0.5 }"
                />
                <span class="text-[10px] text-[#6B6B6E]">{{ d.day }}</span>
              </div>
            </div>
            <div v-else class="text-center py-8 text-[#6B6B6E] text-sm">本周暂无数据</div>
          </GlowCard>
        </div>
        </ScrollReveal>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Duolingo Dashboard：游戏化 XP + 打卡 + 进度环
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <div class="space-y-5">
        <!-- Welcome + Level Badge -->
        <ScrollReveal>
        <div class="flex items-center justify-between">
          <div>
            <h2 class="text-2xl font-extrabold text-[#292524]">
              👋 {{ userName ? userName + '，' : '' }}继续你的冒险！
            </h2>
            <p class="text-[#78716C] mt-0.5 font-medium">保持学习节奏，解锁更多成就</p>
          </div>
          <div class="flex items-center gap-2">
            <div class="w-12 h-12 rounded-2xl bg-[#FFC800] shadow-[0_3px_0_#E5A800] flex items-center justify-center">
              <span class="text-white text-sm font-extrabold">Lv.{{ duoLevel }}</span>
            </div>
          </div>
        </div>
        </ScrollReveal>

        <!-- XP Progress Bar -->
        <ScrollReveal :delay="100">
        <div class="duo-xp-card">
          <div class="flex items-center justify-between mb-2">
            <span class="text-sm font-bold text-[#292524]">🏆 经验值</span>
            <span class="text-xs font-bold text-[#78716C]">{{ duoCurrentXP }} / {{ duoNextXP }} XP</span>
          </div>
          <div class="w-full h-6 bg-[#E5E7EB] rounded-full overflow-hidden">
            <div class="h-full bg-gradient-to-r from-[#58CC02] to-[#7AE82F] rounded-full transition-all relative"
                 :style="{ width: duoXPPercent + '%' }">
              <div class="absolute inset-0 bg-white/20 rounded-full"
                   style="background: repeating-linear-gradient(90deg, transparent, transparent 8px, rgba(255,255,255,0.15) 8px, rgba(255,255,255,0.15) 16px)" />
            </div>
          </div>
          <p class="text-xs text-[#A8A29E] mt-1.5 font-medium">距离下一等级还需 {{ duoNextXP - duoCurrentXP }} XP</p>
        </div>
        </ScrollReveal>

        <!-- Daily Quest (打卡) -->
        <ScrollReveal :delay="200">
        <div class="duo-quest-card">
          <div class="flex items-center gap-4">
            <!-- Flame + Streak -->
            <div class="flex-shrink-0 text-center">
              <div class="w-16 h-16 rounded-2xl bg-[#FF9600]/10 flex items-center justify-center mb-1">
                <Flame class="w-9 h-9 text-[#FF9600]" :stroke-width="2" />
              </div>
              <span class="text-2xl font-extrabold text-[#FF9600]">{{ db?.currentStreak || 0 }}</span>
              <span class="block text-[10px] text-[#78716C] font-bold">天连续</span>
            </div>
            <!-- Quest Info -->
            <div class="flex-1">
              <h3 class="text-base font-extrabold text-[#292524] mb-1">🔥 每日打卡</h3>
              <p class="text-sm text-[#78716C] mb-3">坚持每天学习，保持连续纪录！</p>
              <button
                v-if="!db?.todayCheckedIn"
                @click="doCheckin"
                class="px-6 py-2.5 rounded-2xl bg-[#58CC02] text-white text-sm font-bold
                       shadow-[0_4px_0_#46A302] hover:brightness-105
                       active:translate-y-[2px] active:shadow-[0_2px_0_#46A302] transition-all cursor-pointer"
              >完成打卡 +50 XP</button>
              <span v-else class="inline-flex items-center gap-1.5 px-4 py-2 bg-[#58CC02]/10 text-[#58CC02] rounded-2xl text-sm font-bold">
                <CheckCircle class="w-4 h-4" :stroke-width="2.5" /> 今日已完成！
              </span>
            </div>
          </div>
          <!-- Week dots -->
          <div class="flex items-center justify-center gap-3 mt-4 pt-4 border-t border-[#E7E5E4]">
            <div v-for="(day, i) in weekDays" :key="i" class="flex flex-col items-center gap-1">
              <div class="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold"
                   :class="day.done
                     ? 'bg-[#58CC02] text-white shadow-[0_2px_0_#46A302]'
                     : day.today
                       ? 'bg-[#FFC800] text-white shadow-[0_2px_0_#E5A800] animate-pulse'
                       : 'bg-[#E5E7EB] text-[#A8A29E]'">
                {{ day.done ? '✓' : day.label }}
              </div>
              <span class="text-[9px] font-bold" :class="day.today ? 'text-[#FFC800]' : 'text-[#A8A29E]'">{{ day.name }}</span>
            </div>
          </div>
        </div>
        </ScrollReveal>

        <!-- Course Progress Cards -->
        <ScrollReveal :delay="300">
        <div>
          <h3 class="text-base font-extrabold text-[#292524] mb-3">📚 学习进度</h3>
          <div v-if="db?.recentCourses?.length" class="space-y-3">
            <div
              v-for="(c, i) in db.recentCourses" :key="c.courseId"
              class="duo-course-card cursor-pointer"
              :class="courseColor(i).card"
              @click="$router.push(`/student/courses/${c.courseId}`)"
            >
              <div class="flex items-center gap-4">
                <!-- SVG Progress Ring -->
                <div class="relative w-14 h-14 flex-shrink-0">
                  <svg class="w-14 h-14 -rotate-90" viewBox="0 0 56 56">
                    <circle cx="28" cy="28" r="24" fill="none" stroke="#E5E7EB" stroke-width="5" />
                    <circle cx="28" cy="28" r="24" fill="none"
                      :stroke="courseColor(i).ring"
                      stroke-width="5" stroke-linecap="round"
                      :stroke-dasharray="`${(c.progress||0) * 1.508} 150.8`" />
                  </svg>
                  <span class="absolute inset-0 flex items-center justify-center text-xs font-extrabold text-[#292524]">
                    {{ c.progress||0 }}%
                  </span>
                </div>
                <!-- Info -->
                <div class="flex-1 min-w-0">
                  <h4 class="text-sm font-bold text-[#292524] truncate">{{ c.courseName }}</h4>
                  <p class="text-xs text-[#78716C] mt-0.5">继续学习获取更多 XP</p>
                </div>
                <!-- CTA -->
                <button class="px-4 py-2 rounded-xl bg-white text-sm font-bold border-2 border-[#E5E7EB]
                               shadow-[0_3px_0_#E5E7EB] hover:shadow-[0_4px_0_#D6D3D1] hover:-translate-y-0.5
                               active:translate-y-[1px] active:shadow-[0_1px_0_#E5E7EB] transition-all"
                        :class="courseColor(i).btn"
                        @click.stop="$router.push(`/student/courses/${c.courseId}/study`)">
                  继续
                </button>
              </div>
            </div>
          </div>
          <div v-else class="text-center py-8">
            <div class="text-4xl mb-2">🎮</div>
            <p class="text-sm text-[#A8A29E] font-medium">还没开始冒险？去选择你的第一门课程吧！</p>
          </div>
        </div>
        </ScrollReveal>

        <!-- Weekly Activity Chart -->
        <ScrollReveal :delay="400">
        <div>
          <h3 class="text-base font-extrabold text-[#292524] mb-3">📊 本周活动</h3>
          <div v-if="db?.weeklyStats?.length" class="duo-weekly-card">
            <div class="flex items-end gap-3 h-32">
              <div v-for="(d, i) in db.weeklyStats" :key="i" class="flex-1 flex flex-col items-center gap-1.5">
                <span class="text-[10px] font-bold text-[#78716C]">{{ d.hours }}h</span>
                <div class="w-full rounded-xl transition-all"
                     :class="barColor(i)"
                     :style="{ height: Math.max((Number(d.hours) / mx) * 100, 8) + '%' }" />
                <span class="text-[10px] font-bold text-[#78716C]">{{ d.day }}</span>
              </div>
            </div>
            <div class="flex items-center justify-between mt-3 pt-3 border-t border-[#E7E5E4]">
              <span class="text-xs font-bold text-[#78716C]">合计</span>
              <span class="text-sm font-extrabold text-[#292524]">{{ db?.weeklyStudyHours || 0 }}h</span>
            </div>
          </div>
          <p v-else class="text-sm text-[#A8A29E] py-4 font-medium">本周暂无数据，快去学习吧！</p>
        </div>
        </ScrollReveal>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Vercel Dashboard：数据密集 + monospace + 极简
         ================================================================ -->
    <template v-else>
      <div class="space-y-5">
        <!-- Top Metrics Bar -->
        <ScrollReveal>
        <div class="pro-metrics-bar">
          <div class="flex items-center gap-2">
            <h2 class="text-sm font-semibold text-[#0F172A]">总览</h2>
            <span class="text-xs text-[#64748B]">{{ userName }}</span>
          </div>
          <div class="flex items-center gap-1">
            <button
              v-if="!db?.todayCheckedIn"
              @click="doCheckin"
              class="px-3 py-1.5 bg-[#0F172A] text-white rounded-md text-xs font-medium hover:bg-[#1E293B] transition-colors cursor-pointer"
            >签到</button>
            <span v-else class="text-xs text-emerald-600 font-medium">✓ 已签到</span>
          </div>
        </div>
        </ScrollReveal>

        <!-- Inline Stats Row -->
        <ScrollReveal :delay="100">
        <div class="pro-stats-row">
          <div v-for="(s, i) in proStats" :key="s.label" class="pro-stat-item" :class="i < proStats.length - 1 ? 'border-r border-[#E2E8F0]' : ''">
            <span class="font-mono text-xl font-semibold text-[#0F172A]"><NumberCounter :value="s.value" :suffix="s.suffix" /></span>
            <span class="text-[11px] text-[#64748B] uppercase tracking-wider">{{ s.label }}</span>
          </div>
        </div>
        </ScrollReveal>

        <!-- 2 Columns: Activity Log | Weekly -->
        <ScrollReveal :delay="200">
        <div class="grid lg:grid-cols-2 gap-4">
          <!-- Recent — Terminal style -->
          <div class="pro-card">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">最近学习</span>
              <span class="text-[10px] text-[#94A3B8]">{{ db?.recentCourses?.length || 0 }} 条</span>
            </div>
            <div v-if="db?.recentCourses?.length" class="space-y-0 divide-y divide-[#F1F5F9]">
              <div
                v-for="c in db.recentCourses" :key="c.courseId"
                class="flex items-center gap-3 py-2.5 cursor-pointer hover:bg-[#F8FAFC] -mx-4 px-4 transition-colors"
                @click="$router.push(`/student/courses/${c.courseId}`)"
              >
                <div class="w-1.5 h-1.5 rounded-full" :class="(c.progress||0) >= 100 ? 'bg-emerald-500' : 'bg-[#0284C7]'"></div>
                <span class="text-sm text-[#0F172A] flex-1 truncate">{{ c.courseName }}</span>
                <code class="text-xs text-[#64748B] font-mono bg-[#F1F5F9] px-1.5 py-0.5 rounded">{{ c.progress||0 }}%</code>
              </div>
            </div>
            <div v-else class="text-center py-6 text-[#94A3B8] text-xs">暂无学习记录</div>
          </div>

          <!-- Weekly — Minimal bars -->
          <div class="pro-card">
            <div class="flex items-center justify-between mb-3">
              <span class="text-xs font-semibold text-[#0F172A] uppercase tracking-wider">本周学习</span>
              <span class="font-mono text-xs text-[#64748B]">{{ db?.weeklyStudyHours || 0 }}h 合计</span>
            </div>
            <div v-if="db?.weeklyStats?.length" class="flex items-end gap-1.5 h-28">
              <div v-for="(d, i) in db.weeklyStats" :key="i" class="flex-1 flex flex-col items-center gap-1.5">
                <div
                  class="w-full rounded-[2px] bg-[#0284C7] transition-all"
                  :style="{ height: Math.max((d.hours / mx) * 100, 4) + '%', opacity: 0.4 + (d.hours / mx) * 0.6 }"
                />
                <span class="text-[10px] text-[#94A3B8] font-mono">{{ d.day }}</span>
              </div>
            </div>
            <div v-else class="text-center py-6 text-[#94A3B8] text-xs">本周暂无数据</div>
          </div>
        </div>
        </ScrollReveal>
      </div>
    </template>
    </div>
    </Transition>

    <!-- New User Onboarding Wizard -->
    <WelcomeWizard v-model="showWelcome" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useHead } from '@unhead/vue'
import { useUserStore } from '@/stores/user'
import { useThemeStore } from '@/stores/theme'
import { studentApi } from '@/api/student'
import { checkinApi } from '@/api/checkin'
import { toast } from '@/composables/useToast'
import NumberCounter from '@/components/effects/NumberCounter.vue'
import ScrollReveal from '@/components/effects/ScrollReveal.vue'
import GlowCard from '@/components/effects/GlowCard.vue'
import WelcomeWizard from '@/components/WelcomeWizard.vue'
import { useConfetti } from '@/composables/useConfetti'
import {
  CheckCircle, Flame, Clock, BookOpen,
  GraduationCap, BookMarked, BarChart3,
} from 'lucide-vue-next'

const userStore = useUserStore()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)
const { pop } = useConfetti()

useHead({ title: '仪表盘 — IT 智能培训系统' })
const userName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '')
const db = ref<any>(null)
const loading = ref(true)

/* ── Welcome Wizard (first-time user onboarding) ── */
const showWelcome = ref(false)
const mx = computed(() => Math.max(...(db.value?.weeklyStats?.map((d: any) => d.hours) || [1]), 1))

/* Per-theme stat configs */
const stripeStats = computed(() => [
  { label: '连续打卡', value: db.value?.currentStreak || 0, suffix: '天' },
  { label: '本周学时', value: db.value?.weeklyStudyHours || 0, suffix: 'h' },
  { label: '在学课程', value: db.value?.inProgressCourses || 0, suffix: '' },
  { label: '已完成', value: db.value?.completedCourses || 0, suffix: '' },
])

const linearStats = computed(() => [
  { label: '连续打卡', value: db.value?.currentStreak || 0, suffix: '天', icon: Flame },
  { label: '本周学时', value: db.value?.weeklyStudyHours || 0, suffix: 'h', icon: Clock },
  { label: '在学课程', value: db.value?.inProgressCourses || 0, suffix: '', icon: BookOpen },
  { label: '已完成', value: db.value?.completedCourses || 0, suffix: '', icon: GraduationCap },
])

/* ── Duolingo (Warm) Data ── */
const duoLevel = computed(() => Math.floor((db.value?.completedCourses || 0) * 2 + (db.value?.currentStreak || 0) / 7) + 1)
const duoCurrentXP = computed(() => ((db.value?.completedCourses || 0) * 200 + (db.value?.currentStreak || 0) * 50) % 1000)
const duoNextXP = 1000
const duoXPPercent = computed(() => Math.min((duoCurrentXP.value / duoNextXP) * 100, 100))

const duoCourseColors = [
  { card: 'bg-[#DDF4FF]', ring: '#1CB0F6', btn: 'text-[#1CB0F6]' },
  { card: 'bg-[#D7FFB8]', ring: '#58CC02', btn: 'text-[#58CC02]' },
  { card: 'bg-[#FFF3D6]', ring: '#FFC800', btn: 'text-[#FF9600]' },
  { card: 'bg-[#F3E8FF]', ring: '#CE82FF', btn: 'text-[#A855F7]' },
  { card: 'bg-[#FFE0E0]', ring: '#FF4B4B', btn: 'text-[#FF4B4B]' },
]

const duoBarColors = [
  'bg-[#58CC02]', 'bg-[#1CB0F6]', 'bg-[#FFC800]',
  'bg-[#CE82FF]', 'bg-[#FF4B4B]', 'bg-[#FF9600]', 'bg-[#58CC02]',
]

/* ── Theme-safe array helpers (avoids vue-tsc TS2362 in template) ── */
function courseColor(i: number | string) { return duoCourseColors[+i % duoCourseColors.length]! }
function barColor(i: number | string) { return duoBarColors[+i % duoBarColors.length]! }

const weekDays = computed(() => {
  const days = ['日', '一', '二', '三', '四', '五', '六']
  const now = new Date()
  const todayIdx = now.getDay()
  return days.map((name, i) => ({
    name,
    label: days[i]!.charAt(0),
    done: db.value?.weeklyStats?.[i]?.hours > 0 && i < todayIdx,
    today: i === todayIdx,
  }))
})

const proStats = computed(() => [
  { label: '连续打卡', value: db.value?.currentStreak || 0, suffix: '天' },
  { label: '本周学时', value: db.value?.weeklyStudyHours || 0, suffix: 'h' },
  { label: '在学课程', value: db.value?.inProgressCourses || 0, suffix: '' },
  { label: '已完成', value: db.value?.completedCourses || 0, suffix: '' },
])

async function doCheckin() {
  try {
    await checkinApi.checkin()
    toast.success('打卡成功！')
    if (db.value) db.value.todayCheckedIn = true
    pop()
    // Haptic feedback for mobile devices
    if (navigator.vibrate) navigator.vibrate([50, 30, 100])
  } catch (e: any) {
    toast.error(e.message || '打卡失败')
  }
}

onMounted(async () => {
  try {
    db.value = await studentApi.getDashboard()
  } catch {}
  finally {
    loading.value = false
    // Show welcome wizard for first-time users
    if (!localStorage.getItem('onboarding_completed')) {
      showWelcome.value = true
    }
  }
})
</script>

<style scoped>
/* ======== STRIPE (Light) ======== */
.stripe-dash-welcome {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #E3E8EE;
  box-shadow:
    0 2px 4px rgba(0,0,0,0.04),
    0 0 0 1px rgba(0,0,0,0.02);
}
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

/* ======== LINEAR (Dark) ======== */
.linear-dash-welcome {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-radius: 12px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
}
.linear-gradient-text {
  background: linear-gradient(135deg, #EDEDED 0%, #818CF8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.linear-bento-card {
  padding: 20px;
  border-radius: 12px;
  background: #111113;
  border: 1px solid rgba(255,255,255,0.06);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}
.linear-bento-card:hover {
  border-color: rgba(129,140,248,0.2);
  box-shadow: 0 0 20px rgba(129,140,248,0.08);
}

/* ======== DUOLINGO (Warm) ======== */
.duo-xp-card {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
}
.duo-quest-card {
  padding: 20px;
  background: #fff;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
}
.duo-course-card {
  padding: 16px;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}
.duo-course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 0 #D6D3D1;
}
.duo-weekly-card {
  padding: 20px;
  background: #FFFBF5;
  border: 2px solid #E5E7EB;
  border-radius: 20px;
  box-shadow: 0 3px 0 #E5E7EB;
}

/* ======== PRO (Vercel) ======== */
.pro-metrics-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #E2E8F0;
}
.pro-stats-row {
  display: flex;
  padding: 16px 0;
}
.pro-stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 0 16px;
}
.pro-card {
  padding: 16px;
  background: #fff;
  border: 1px solid #E2E8F0;
  border-radius: 6px;
}
</style>
