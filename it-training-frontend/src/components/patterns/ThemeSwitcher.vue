<template>
  <div class="relative" ref="containerRef">
    <button @click="open=!open" class="flex items-center gap-2 px-3 py-1.5 rounded-full text-sm transition-all hover:bg-surface-alt">
      <span class="text-base">{{currentIcon}}</span>
      <span class="text-text-secondary text-xs hidden sm:inline">{{currentLabel}}</span>
    </button>
    <Transition name="scale">
      <div v-if="open" class="absolute right-0 top-full mt-2 p-2 rounded-2xl bg-surface border border-border shadow-theme-lg min-w-[180px] z-50">
        <button v-for="t in themes" :key="t.key" @click="select(t.key)" :class="['flex items-center gap-3 w-full px-3 py-2.5 rounded-xl text-sm transition-all',themeStore.theme===t.key?'bg-primary/15 text-primary font-medium':'text-text-secondary hover:bg-surface-alt hover:text-text-primary']">
          <span class="text-lg">{{t.icon}}</span><span>{{t.label}}</span>
          <div class="ml-auto flex gap-1"><span v-for="(c,i) in t.colors" :key="i" class="w-3 h-3 rounded-full border border-border/50" :style="{backgroundColor:c}"></span></div>
        </button>
      </div>
    </Transition>
  </div>
</template>
<script setup lang="ts">
import { ref, computed } from 'vue'
import { onClickOutside } from '@vueuse/core'
import { useThemeStore } from '@/stores/theme'
import type { ThemeName } from '@/design-system/tokens/colors'
const themeStore = useThemeStore()
const open = ref(false)
const containerRef = ref<HTMLElement>()
onClickOutside(containerRef, () => { open.value = false })
const themes = [
  {key:'light' as ThemeName,icon:'☀️',label:'晴空白',colors:['#0071E3','#F5F5F7','#FFF']},
  {key:'dark' as ThemeName,icon:'🌙',label:'深空黑',colors:['#0A84FF','#0A0A0B','#161618']},
  {key:'warm' as ThemeName,icon:'🌅',label:'暖阳橙',colors:['#FF9500','#FFF8F0','#FFF']},
  {key:'pro' as ThemeName,icon:'❄️',label:'冰川蓝',colors:['#0369A1','#F8FAFC','#FFF']},
]
const currentIcon = computed(() => themes.find(t=>t.key===themeStore.theme)?.icon||'☀️')
const currentLabel = computed(() => themes.find(t=>t.key===themeStore.theme)?.label||'晴空白')
function select(t: ThemeName) { themeStore.applyTheme(t); open.value = false }
</script>
