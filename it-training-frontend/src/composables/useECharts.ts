import { ref, onMounted, onBeforeUnmount, watch, type Ref } from 'vue'
import * as echarts from 'echarts'
import { useThemeStore } from '@/stores/theme'
import { getEChartsBaseOption } from '@/utils/echarts-theme'

/**
 * ECharts composable - 自动初始化/销毁/主题跟随
 */
export function useECharts(container: Ref<HTMLElement | null>) {
  const chart = ref<echarts.ECharts | null>(null)
  const themeStore = useThemeStore()

  function init() {
    if (!container.value) return
    chart.value?.dispose()
    chart.value = echarts.init(container.value)
  }

  function setOption(option: echarts.EChartsOption, merge = true) {
    if (!chart.value) init()
    const base = getEChartsBaseOption()
    chart.value?.setOption({ ...base, ...option } as any, !merge)
  }

  function resize() {
    chart.value?.resize()
  }

  // 主题切换时重建图表
  watch(() => themeStore.theme, () => {
    const opt = chart.value?.getOption()
    init()
    if (opt) chart.value?.setOption({ ...getEChartsBaseOption(), ...opt } as any)
  })

  onMounted(() => {
    window.addEventListener('resize', resize)
  })

  onBeforeUnmount(() => {
    window.removeEventListener('resize', resize)
    chart.value?.dispose()
    chart.value = null
  })

  return { chart, init, setOption, resize }
}
