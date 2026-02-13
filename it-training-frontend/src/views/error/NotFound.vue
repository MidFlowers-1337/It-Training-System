<template>
  <div class="min-h-screen flex items-center justify-center p-6 relative overflow-hidden"
    :class="theme === 'dark' ? 'bg-[#0A0A0B]' : theme === 'warm' ? 'bg-[#FFFBF5]' : theme === 'pro' ? 'bg-[#FAFAFA]' : 'bg-white'">

    <!-- ================================================================
         ☀️ LIGHT — Stripe 404：大号渐变数字 + 极简文案 + 返回按钮
         ================================================================ -->
    <template v-if="theme === 'light'">
      <!-- Background Pattern -->
      <div class="absolute inset-0 overflow-hidden pointer-events-none">
        <div class="absolute top-1/4 -right-20 w-96 h-96 rounded-full bg-[#635BFF]/5 blur-[100px]"></div>
        <div class="absolute -bottom-20 -left-20 w-80 h-80 rounded-full bg-[#80E9FF]/10 blur-[80px]"></div>
        <svg class="absolute inset-0 w-full h-full opacity-[0.03]">
          <defs><pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse"><path d="M 40 0 L 0 0 0 40" fill="none" stroke="#0A2540" stroke-width="0.5"/></pattern></defs>
          <rect width="100%" height="100%" fill="url(#grid)" />
        </svg>
      </div>

      <div class="relative z-10 text-center max-w-md">
        <div class="text-[160px] font-extrabold leading-none tracking-tighter bg-gradient-to-br from-[#635BFF] via-[#80E9FF] to-[#0A2540] bg-clip-text text-transparent select-none">
          404
        </div>
        <h2 class="text-lg font-semibold text-[#0A2540] mt-2">页面未找到</h2>
        <p class="text-sm text-[#425466] mt-2 leading-relaxed">
          您访问的页面不存在或已被移除。<br>请检查 URL 或返回首页。
        </p>
        <div class="flex items-center justify-center gap-3 mt-8">
          <button @click="goHome"
            class="px-6 py-2.5 rounded-lg bg-[#635BFF] text-white text-sm font-medium cursor-pointer hover:brightness-110 transition-all shadow-[0_4px_12px_rgba(99,91,255,0.25)] inline-flex items-center gap-2">
            <ArrowLeft class="w-4 h-4" :stroke-width="2" /> 返回首页
          </button>
          <button @click="goBack"
            class="px-6 py-2.5 rounded-lg border border-[#E3E8EE] text-sm font-medium text-[#425466] cursor-pointer hover:border-[#635BFF] hover:text-[#635BFF] transition-all">
            返回上一页
          </button>
        </div>
        <p class="text-xs text-[#8898AA] mt-8">
          错误代码：<code class="px-1.5 py-0.5 rounded bg-[#F6F9FC] text-[#0A2540] font-mono">404 NOT_FOUND</code>
        </p>
      </div>
    </template>

    <!-- ================================================================
         🌙 DARK — Glitch Terminal 404：故障风 + 终端输出 + 扫描线
         ================================================================ -->
    <template v-else-if="theme === 'dark'">
      <!-- Scan Lines -->
      <div class="absolute inset-0 pointer-events-none opacity-[0.04]"
        style="background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(255,255,255,0.03) 2px, rgba(255,255,255,0.03) 4px)"></div>
      <!-- Glow -->
      <div class="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] rounded-full bg-[#5865F2]/8 blur-[120px] pointer-events-none"></div>

      <div class="relative z-10 text-center max-w-lg">
        <!-- Glitch 404 -->
        <div class="relative select-none">
          <div class="text-[140px] font-black leading-none tracking-tighter text-[#EDEDED] opacity-90"
            style="text-shadow: -3px 0 #5865F2, 3px 0 #ED4245">404</div>
        </div>

        <!-- Terminal Output -->
        <div class="mt-6 mx-auto max-w-sm text-left">
          <div class="bg-white/[0.04] border border-white/[0.06] rounded-lg overflow-hidden">
            <div class="flex items-center gap-2 px-3 py-2 bg-white/[0.02] border-b border-white/[0.04]">
              <span class="w-2.5 h-2.5 rounded-full bg-[#ED4245]"></span>
              <span class="w-2.5 h-2.5 rounded-full bg-[#FEE75C]"></span>
              <span class="w-2.5 h-2.5 rounded-full bg-[#23A55A]"></span>
              <span class="text-[10px] text-[#6B6B6E] font-mono ml-2">terminal</span>
            </div>
            <div class="p-4 space-y-1.5 font-mono text-xs">
              <div class="text-[#6B6B6E]"><span class="text-[#23A55A]">$</span> navigate {{ route.fullPath }}</div>
              <div class="text-[#ED4245]">error: page not found (404)</div>
              <div class="text-[#6B6B6E]">hint: the requested path does not exist</div>
              <div class="text-[#6B6B6E]"><span class="text-[#23A55A]">$</span> <span class="animate-pulse">▊</span></div>
            </div>
          </div>
        </div>

        <div class="flex items-center justify-center gap-3 mt-6">
          <button @click="goHome"
            class="px-5 py-2.5 rounded-md bg-[#5865F2] text-white text-sm font-semibold cursor-pointer hover:bg-[#4752C4] transition inline-flex items-center gap-2">
            <ArrowLeft class="w-4 h-4" :stroke-width="2" /> 返回首页
          </button>
          <button @click="goBack"
            class="px-5 py-2.5 rounded-md bg-white/[0.04] border border-white/[0.06] text-sm font-medium text-[#B5BAC1] cursor-pointer hover:bg-white/[0.06] transition">
            返回上一页
          </button>
        </div>
      </div>
    </template>

    <!-- ================================================================
         🌅 WARM — Notion Playful 404：大 Emoji + 手绘风卡片 + 温暖文案
         ================================================================ -->
    <template v-else-if="theme === 'warm'">
      <!-- Background Blobs -->
      <div class="absolute top-20 right-20 w-40 h-40 rounded-full bg-[#FFC800]/10 blur-[60px] pointer-events-none"></div>
      <div class="absolute bottom-20 left-20 w-60 h-60 rounded-full bg-[#D97706]/5 blur-[80px] pointer-events-none"></div>

      <div class="relative z-10 text-center max-w-md">
        <!-- Fun Card -->
        <div class="notion-card px-8 py-10">
          <Search class="w-16 h-16 text-[#D97706] mb-4 animate-bounce" style="animation-duration: 2s" :stroke-width="1.5" />
          <div class="text-5xl font-black text-[#292524] tracking-tight">哎呀！</div>
          <div class="flex items-center justify-center gap-2 mt-3">
            <span class="px-3 py-1 rounded-full bg-[#D97706]/10 text-[#D97706] text-xs font-extrabold">404 错误</span>
          </div>
          <p class="text-sm text-[#78716C] mt-4 leading-relaxed">
            这个页面好像走丢了呢...<br>
            它可能被搬到了其他地方，或者根本就不存在。
          </p>

          <div class="mt-6 pt-6 border-t-2 border-[#E7E5E4] space-y-3">
            <button @click="goHome"
              class="w-full px-6 py-3 rounded-2xl bg-[#292524] text-white text-sm font-extrabold cursor-pointer hover:brightness-110 transition shadow-[0_3px_0_#1C1917] active:translate-y-[2px] active:shadow-[0_1px_0_#1C1917] inline-flex items-center justify-center gap-2">
              <Home class="w-4 h-4" :stroke-width="2" /> 回到首页
            </button>
            <button @click="goBack"
              class="w-full px-6 py-3 rounded-2xl border-2 border-[#E7E5E4] text-sm font-bold text-[#292524] cursor-pointer hover:border-[#D97706] transition inline-flex items-center justify-center gap-2">
              ← 返回上一页
            </button>
          </div>
        </div>

        <p class="text-xs text-[#A8A29E] mt-6 font-bold">
          访问路径：<code class="px-1.5 py-0.5 rounded-lg bg-[#E7E5E4] text-[#292524]">{{ route.fullPath }}</code>
        </p>
      </div>
    </template>

    <!-- ================================================================
         ❄️ PRO — Grafana System Error：状态码面板 + 日志风格 + monospace
         ================================================================ -->
    <template v-else>
      <div class="relative z-10 w-full max-w-xl">
        <!-- Status Panel -->
        <div class="grafana-panel">
          <div class="grafana-panel-header">
            <div class="flex items-center gap-2">
              <span class="w-2 h-2 rounded-full bg-[#EF4444] animate-pulse"></span>
              <span>Error — Page Not Found</span>
            </div>
            <span class="text-[#94A3B8]">HTTP 404</span>
          </div>

          <div class="p-6">
            <!-- Big Status Code -->
            <div class="text-center mb-6">
              <div class="text-7xl font-mono font-bold text-[#0F172A] tracking-tight">404</div>
              <code class="text-xs text-[#EF4444] mt-1 inline-block">NOT_FOUND</code>
            </div>

            <!-- Error Details -->
            <div class="bg-[#F8FAFC] rounded border border-[#E2E8F0] p-4 font-mono text-[11px] space-y-1.5">
              <div class="flex"><span class="text-[#94A3B8] w-24 flex-shrink-0">timestamp</span><span class="text-[#0F172A]">{{ timestamp }}</span></div>
              <div class="flex"><span class="text-[#94A3B8] w-24 flex-shrink-0">path</span><span class="text-[#EF4444]">{{ route.fullPath }}</span></div>
              <div class="flex"><span class="text-[#94A3B8] w-24 flex-shrink-0">status</span><span class="text-[#EF4444]">404</span></div>
              <div class="flex"><span class="text-[#94A3B8] w-24 flex-shrink-0">message</span><span class="text-[#64748B]">The requested resource could not be found.</span></div>
            </div>

            <!-- Actions -->
            <div class="flex items-center gap-2 mt-6">
              <button @click="goHome"
                class="flex-1 px-4 py-2 rounded bg-[#0F172A] text-white text-[11px] font-mono font-semibold cursor-pointer hover:bg-[#1E293B] transition inline-flex items-center justify-center gap-1.5">
                <ArrowLeft class="w-3.5 h-3.5" :stroke-width="2" /> go_home
              </button>
              <button @click="goBack"
                class="flex-1 px-4 py-2 rounded border border-[#E2E8F0] text-[11px] font-mono font-semibold text-[#64748B] cursor-pointer hover:border-[#0284C7] hover:text-[#0284C7] transition">
                go_back
              </button>
            </div>
          </div>
        </div>

        <!-- System Info -->
        <div class="flex items-center justify-between mt-3 px-1">
          <code class="text-[9px] text-[#94A3B8]">it-training-frontend v1.0.0</code>
          <code class="text-[9px] text-[#94A3B8]">vue-router · {{ route.name || 'NotFound' }}</code>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useThemeStore } from '@/stores/theme'
import { ArrowLeft, Search, Home } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()
const theme = computed(() => themeStore.theme)

const timestamp = computed(() => new Date().toISOString())

function goHome() { router.push('/') }
function goBack() { router.back() }
</script>

<style scoped>
/* ======== NOTION (Warm) ======== */
.notion-card {
  background: #FFFBF5;
  border: 2px solid #E7E5E4;
  border-radius: 24px;
  box-shadow: 0 4px 0 #E7E5E4;
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
</style>
