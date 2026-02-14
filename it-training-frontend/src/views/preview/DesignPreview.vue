<template>
  <div class="min-h-screen bg-background flex">
    <!-- Sidebar -->
    <aside :class="['fixed top-0 left-0 h-full z-40 transition-all duration-300 border-r bg-surface border-border/50', collapsed ? 'w-16' : 'w-60']">
      <div class="h-16 flex items-center px-4 border-b border-border/50">
        <div class="w-8 h-8 rounded-lg bg-primary flex items-center justify-center flex-shrink-0">
          <span class="text-white font-bold text-sm">IT</span>
        </div>
        <Transition name="fade"><span v-if="!collapsed" class="ml-3 font-semibold text-text-primary truncate">智能培训 · 预览</span></Transition>
      </div>
      <nav class="p-2 space-y-1 overflow-y-auto" style="height:calc(100% - 4rem)">
        <button
          v-for="item in menuItems" :key="item.id"
          @click="activeTab = item.id"
          :class="['flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm transition-all hover:bg-primary/10 w-full text-left cursor-pointer', activeTab === item.id ? 'bg-primary/15 text-primary font-medium' : 'text-text-secondary hover:text-text-primary']"
        >
          <component :is="item.icon" class="w-[18px] h-[18px] flex-shrink-0" :stroke-width="1.75" />
          <Transition name="fade"><span v-if="!collapsed" class="truncate">{{ item.label }}</span></Transition>
        </button>
      </nav>
    </aside>

    <!-- Main -->
    <div :class="['flex-1 transition-all duration-300', collapsed ? 'ml-16' : 'ml-60']">
      <header class="h-16 flex items-center justify-between px-6 border-b border-border/50 bg-surface/80 backdrop-blur-sm sticky top-0 z-20">
        <div class="flex items-center gap-4">
          <button @click="collapsed = !collapsed" class="p-2 rounded-lg hover:bg-surface-alt transition-colors cursor-pointer">
            <Menu class="w-5 h-5 text-text-secondary" :stroke-width="1.75" />
          </button>
          <h1 class="text-lg font-semibold text-text-primary">{{ currentTitle }}</h1>
          <span class="px-2 py-0.5 text-xs font-medium rounded-full bg-warning/10 text-warning">Preview Mode</span>
        </div>
        <div class="flex items-center gap-3">
          <ThemeSwitcher />
          <router-link to="/login" class="px-3 py-1.5 text-sm text-primary border border-primary/30 rounded-lg hover:bg-primary/10 transition-colors cursor-pointer">去登录</router-link>
        </div>
      </header>
      <main class="p-6">
        <!-- Dashboard Preview -->
        <div v-if="activeTab === 'dashboard'" class="space-y-6">
          <BentoGrid :cols="4" gap="default">
            <ScrollReveal>
              <BentoItem :col-span="2" hover>
                <div class="flex items-center justify-between">
                  <div>
                    <h2 class="text-2xl font-bold text-text-primary tracking-tight">欢迎回来，演示用户！</h2>
                    <p class="text-text-secondary mt-1">这是设计系统预览页面</p>
                  </div>
                  <span class="inline-flex items-center gap-1.5 px-4 py-2 bg-success/10 text-success rounded-xl text-sm font-medium">
                    <CheckCircle class="w-4 h-4" :stroke-width="2" /> 已打卡
                  </span>
                </div>
              </BentoItem>
            </ScrollReveal>
            <ScrollReveal :delay="60">
              <BentoItem hover>
                <div class="flex flex-col items-center justify-center text-center h-full">
                  <Flame class="w-8 h-8 text-warning mb-2" :stroke-width="1.75" />
                  <div class="stat-value text-text-primary"><NumberCounter :value="15" /></div>
                  <div class="stat-label">连续打卡天数</div>
                </div>
              </BentoItem>
            </ScrollReveal>
            <ScrollReveal :delay="120">
              <BentoItem hover>
                <div class="flex flex-col items-center justify-center text-center h-full">
                  <Clock class="w-8 h-8 text-info mb-2" :stroke-width="1.75" />
                  <div class="stat-value text-text-primary"><NumberCounter :value="12" suffix="h" /></div>
                  <div class="stat-label">本周学时</div>
                </div>
              </BentoItem>
            </ScrollReveal>
            <ScrollReveal :delay="180">
              <BentoItem hover>
                <div class="flex items-center gap-4">
                  <div class="w-11 h-11 rounded-xl bg-primary/10 flex items-center justify-center">
                    <BookOpen class="w-5 h-5 text-primary" :stroke-width="1.75" />
                  </div>
                  <div>
                    <div class="stat-value text-text-primary text-xl"><NumberCounter :value="5" /></div>
                    <div class="stat-label">在学课程</div>
                  </div>
                </div>
              </BentoItem>
            </ScrollReveal>
            <ScrollReveal :delay="240">
              <BentoItem hover>
                <div class="flex items-center gap-4">
                  <div class="w-11 h-11 rounded-xl bg-success/10 flex items-center justify-center">
                    <GraduationCap class="w-5 h-5 text-success" :stroke-width="1.75" />
                  </div>
                  <div>
                    <div class="stat-value text-text-primary text-xl"><NumberCounter :value="8" /></div>
                    <div class="stat-label">已完成</div>
                  </div>
                </div>
              </BentoItem>
            </ScrollReveal>
            <ScrollReveal :delay="100">
              <BentoItem :col-span="2">
                <div class="flex items-center gap-2 mb-4">
                  <BookMarked class="w-5 h-5 text-primary" :stroke-width="1.75" />
                  <h3 class="text-lg font-semibold text-text-primary">最近学习</h3>
                </div>
                <div class="space-y-3">
                  <div v-for="c in mockCourses" :key="c.name" class="flex items-center gap-4 p-3 rounded-xl hover:bg-surface-alt cursor-pointer transition-colors">
                    <div class="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center text-primary font-bold text-sm">{{ c.name[0] }}</div>
                    <div class="flex-1 min-w-0">
                      <div class="text-sm font-medium text-text-primary truncate">{{ c.name }}</div>
                      <div class="w-full h-1.5 bg-border/50 rounded-full mt-1.5">
                        <div class="h-full bg-primary rounded-full transition-all" :style="{ width: c.progress + '%' }"></div>
                      </div>
                    </div>
                    <span class="text-xs text-text-secondary font-medium">{{ c.progress }}%</span>
                  </div>
                </div>
              </BentoItem>
            </ScrollReveal>
            <ScrollReveal :delay="200">
              <BentoItem :col-span="2">
                <div class="flex items-center gap-2 mb-4">
                  <BarChart3 class="w-5 h-5 text-primary" :stroke-width="1.75" />
                  <h3 class="text-lg font-semibold text-text-primary">本周学习</h3>
                </div>
                <div class="flex items-end gap-2 h-40">
                  <div v-for="d in mockWeekly" :key="d.day" class="flex-1 flex flex-col items-center gap-1">
                    <div class="w-full bg-primary/80 hover:bg-primary rounded-t-lg transition-all cursor-default" :style="{ height: Math.max(d.pct, 4) + '%' }"></div>
                    <span class="text-xs text-text-tertiary">{{ d.day }}</span>
                  </div>
                </div>
              </BentoItem>
            </ScrollReveal>
          </BentoGrid>
        </div>

        <!-- Components Preview -->
        <div v-else-if="activeTab === 'components'" class="space-y-8 max-w-4xl">
          <ScrollReveal>
            <h2 class="text-2xl font-bold text-text-primary tracking-tight mb-6">组件展示</h2>
          </ScrollReveal>

          <!-- Buttons -->
          <ScrollReveal :delay="50">
            <BentoItem>
              <h3 class="text-lg font-semibold text-text-primary mb-4">Buttons — Stripe Style</h3>
              <div class="space-y-4">
                <div>
                  <p class="text-xs text-text-tertiary mb-2 font-medium uppercase tracking-wider">Variants</p>
                  <div class="flex flex-wrap gap-3">
                    <Button variant="primary">Primary</Button>
                    <Button variant="secondary">Secondary</Button>
                    <Button variant="ghost">Ghost</Button>
                    <Button variant="danger">Danger</Button>
                    <Button variant="success">Success</Button>
                  </div>
                </div>
                <div>
                  <p class="text-xs text-text-tertiary mb-2 font-medium uppercase tracking-wider">Sizes</p>
                  <div class="flex flex-wrap items-center gap-3">
                    <Button size="sm">Small</Button>
                    <Button size="md">Medium</Button>
                    <Button size="lg">Large</Button>
                  </div>
                </div>
                <div>
                  <p class="text-xs text-text-tertiary mb-2 font-medium uppercase tracking-wider">States</p>
                  <div class="flex flex-wrap gap-3">
                    <Button :loading="true">Loading</Button>
                    <Button :disabled="true">Disabled</Button>
                    <Button rounded>Rounded</Button>
                    <Button variant="danger" rounded size="sm">Delete</Button>
                  </div>
                </div>
              </div>
            </BentoItem>
          </ScrollReveal>

          <!-- Glass Cards -->
          <ScrollReveal :delay="100">
            <h3 class="text-lg font-semibold text-text-primary mb-4">Glass Cards</h3>
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <GlassCard hover><div class="text-center"><div class="stat-value text-primary">42</div><div class="stat-label">Default Glass</div></div></GlassCard>
              <div class="liquid-glass rounded-2xl p-6 text-center"><div class="stat-value text-primary">∞</div><div class="stat-label">Liquid Glass</div></div>
              <BentoItem hover><div class="text-center"><div class="stat-value text-primary">99</div><div class="stat-label">Bento Item</div></div></BentoItem>
            </div>
          </ScrollReveal>

          <!-- Gradient Text -->
          <ScrollReveal :delay="150">
            <BentoItem>
              <h3 class="text-lg font-semibold text-text-primary mb-4">Gradient Text (Stripe Mesh)</h3>
              <GradientText tag="p" size="3xl" hero>Stripe 风格渐变文字</GradientText>
              <GradientText tag="p" size="xl" class="mt-2">普通渐变文字</GradientText>
            </BentoItem>
          </ScrollReveal>

          <!-- Mesh Background Demo -->
          <ScrollReveal :delay="200">
            <div class="relative rounded-2xl overflow-hidden h-48">
              <MeshGradient animated />
              <div class="relative z-10 flex items-center justify-center h-full">
                <p class="text-xl font-bold text-text-primary">Stripe Mesh Gradient Background</p>
              </div>
            </div>
          </ScrollReveal>

          <!-- Icons -->
          <ScrollReveal :delay="250">
            <BentoItem>
              <h3 class="text-lg font-semibold text-text-primary mb-4">Lucide Icons</h3>
              <div class="flex flex-wrap gap-4">
                <div v-for="ic in iconShowcase" :key="ic.name" class="flex flex-col items-center gap-1.5 w-16">
                  <div class="w-10 h-10 rounded-xl flex items-center justify-center" :class="ic.bg">
                    <component :is="ic.comp" class="w-5 h-5" :class="ic.color" :stroke-width="1.75" />
                  </div>
                  <span class="text-[11px] text-text-tertiary">{{ ic.name }}</span>
                </div>
              </div>
            </BentoItem>
          </ScrollReveal>
        </div>

        <!-- Typography Preview -->
        <div v-else-if="activeTab === 'typography'" class="space-y-8 max-w-3xl">
          <ScrollReveal>
            <h2 class="text-2xl font-bold text-text-primary tracking-tight mb-6">Swiss Modernism 排版</h2>
          </ScrollReveal>
          <ScrollReveal :delay="50">
            <BentoItem>
              <div class="prose-content">
                <h1>Heading 1 — 大标题</h1>
                <p>这是一段正文内容，使用 Swiss Modernism 排版规范。行宽限制在 72 个字符以内，行高 1.75 确保舒适的阅读体验。字体使用 SF Pro / PingFang SC，中英文混排效果优秀。</p>
                <h2>Heading 2 — 二级标题</h2>
                <p>The quick brown fox jumps over the lazy dog. 每一个排版决策背后都有理性的设计思考。间距使用 4px 网格系统，确保数学化的一致性。</p>
                <h3>Heading 3 — 三级标题</h3>
                <p>Swiss International Style 强调内容即设计，装饰从简，让信息高效传达。配合严格的 12 栏网格，每个元素都有精确的归属位置。</p>
              </div>
            </BentoItem>
          </ScrollReveal>
          <ScrollReveal :delay="100">
            <BentoItem>
              <h3 class="text-lg font-semibold text-text-primary mb-4">字号层级</h3>
              <div class="space-y-3">
                <div v-for="s in typeSizes" :key="s.name" class="flex items-baseline gap-4">
                  <span class="text-xs text-text-tertiary w-16 flex-shrink-0 font-mono">{{ s.size }}</span>
                  <span :class="s.class" class="text-text-primary">{{ s.name }}</span>
                </div>
              </div>
            </BentoItem>
          </ScrollReveal>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import Button from '@/components/primitives/Button.vue'
import BentoGrid from '@/components/effects/BentoGrid.vue'
import BentoItem from '@/components/effects/BentoItem.vue'
import GlassCard from '@/components/effects/GlassCard.vue'
import GradientText from '@/components/effects/GradientText.vue'
import MeshGradient from '@/components/effects/MeshGradient.vue'
import ScrollReveal from '@/components/effects/ScrollReveal.vue'
import NumberCounter from '@/components/effects/NumberCounter.vue'
import ThemeSwitcher from '@/components/patterns/ThemeSwitcher.vue'
import {
  LayoutDashboard, Component, Type, Menu,
  CheckCircle, Flame, Clock, BookOpen, GraduationCap, BookMarked, BarChart3,
  Sparkles, Target, Trophy, User, Palette, TrendingUp,
  Heart, Star, Zap, Shield, Globe, Code, Terminal, Layers,
} from 'lucide-vue-next'

const collapsed = ref(false)
const activeTab = ref('dashboard')

const menuItems = [
  { id: 'dashboard', icon: LayoutDashboard, label: '仪表盘预览' },
  { id: 'components', icon: Component, label: '组件展示' },
  { id: 'typography', icon: Type, label: '排版系统' },
]

const currentTitle = computed(() => menuItems.find(i => i.id === activeTab.value)?.label || '预览')

const mockCourses = [
  { name: 'Spring Boot 实战', progress: 72 },
  { name: 'Vue 3 + TypeScript', progress: 45 },
  { name: 'Docker & Kubernetes', progress: 28 },
  { name: '算法与数据结构', progress: 91 },
]

const mockWeekly = [
  { day: '一', pct: 60 }, { day: '二', pct: 80 }, { day: '三', pct: 40 },
  { day: '四', pct: 95 }, { day: '五', pct: 70 }, { day: '六', pct: 30 },
  { day: '日', pct: 50 },
]

const iconShowcase = [
  { name: 'Sparkles', comp: Sparkles, bg: 'bg-primary/10', color: 'text-primary' },
  { name: 'Target', comp: Target, bg: 'bg-success/10', color: 'text-success' },
  { name: 'Trophy', comp: Trophy, bg: 'bg-warning/10', color: 'text-warning' },
  { name: 'Heart', comp: Heart, bg: 'bg-danger/10', color: 'text-danger' },
  { name: 'Star', comp: Star, bg: 'bg-warning/10', color: 'text-warning' },
  { name: 'Zap', comp: Zap, bg: 'bg-primary/10', color: 'text-primary' },
  { name: 'Shield', comp: Shield, bg: 'bg-success/10', color: 'text-success' },
  { name: 'Globe', comp: Globe, bg: 'bg-info/10', color: 'text-info' },
  { name: 'Code', comp: Code, bg: 'bg-primary/10', color: 'text-primary' },
  { name: 'Terminal', comp: Terminal, bg: 'bg-success/10', color: 'text-success' },
  { name: 'Layers', comp: Layers, bg: 'bg-info/10', color: 'text-info' },
  { name: 'Trending', comp: TrendingUp, bg: 'bg-warning/10', color: 'text-warning' },
]

const typeSizes = [
  { name: 'Display — 大展示标题', size: '48px', class: 'text-5xl font-bold tracking-tight' },
  { name: 'Heading 1', size: '36px', class: 'text-4xl font-bold tracking-tight' },
  { name: 'Heading 2', size: '30px', class: 'text-3xl font-semibold' },
  { name: 'Heading 3', size: '24px', class: 'text-2xl font-semibold' },
  { name: 'Large Body', size: '18px', class: 'text-lg' },
  { name: 'Body', size: '16px', class: 'text-base' },
  { name: 'Small', size: '14px', class: 'text-sm' },
  { name: 'Caption', size: '13px', class: 'text-[13px] text-text-secondary' },
  { name: 'Micro', size: '12px', class: 'text-xs text-text-tertiary' },
]
</script>
