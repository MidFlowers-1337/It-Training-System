/**
 * ECharts 主题适配工具
 * 读取 CSS 变量自动适配当前主题
 */
export function getEChartsThemeColors() {
  const style = getComputedStyle(document.documentElement)
  const getCssVar = (name: string) => style.getPropertyValue(name).trim()
  const toRgba = (rgb: string, alpha = 1) => `rgba(${rgb.replace(/ /g, ',')},${alpha})`

  const primary = getCssVar('--color-primary')
  const success = getCssVar('--color-success')
  const warning = getCssVar('--color-warning')
  const danger = getCssVar('--color-danger')
  const info = getCssVar('--color-info')
  const text = getCssVar('--color-text')
  const textSecondary = getCssVar('--color-text-secondary')
  const textTertiary = getCssVar('--color-text-tertiary')
  const border = getCssVar('--color-border')
  const surface = getCssVar('--color-surface')

  return {
    colorPalette: [
      toRgba(primary), toRgba(success), toRgba(warning),
      toRgba(danger), toRgba(info),
      toRgba(primary, 0.6), toRgba(success, 0.6),
    ],
    textColor: toRgba(text),
    textSecondaryColor: toRgba(textSecondary),
    axisLineColor: toRgba(border),
    splitLineColor: toRgba(border, 0.5),
    tooltipBg: toRgba(surface),
    primary: toRgba(primary),
    success: toRgba(success),
    warning: toRgba(warning),
    danger: toRgba(danger),
  }
}

/** 生成 ECharts 通用配置 */
export function getEChartsBaseOption() {
  const c = getEChartsThemeColors()
  return {
    color: c.colorPalette,
    textStyle: { color: c.textColor, fontFamily: 'inherit' },
    title: { textStyle: { color: c.textColor, fontSize: 16, fontWeight: 600 } },
    legend: { textStyle: { color: c.textSecondaryColor } },
    tooltip: {
      backgroundColor: c.tooltipBg,
      borderColor: c.axisLineColor,
      textStyle: { color: c.textColor },
      borderWidth: 1,
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      axisLine: { lineStyle: { color: c.axisLineColor } },
      axisTick: { lineStyle: { color: c.axisLineColor } },
      axisLabel: { color: c.textSecondaryColor },
      splitLine: { lineStyle: { color: c.splitLineColor, type: 'dashed' as const } },
    },
    yAxis: {
      axisLine: { lineStyle: { color: c.axisLineColor } },
      axisTick: { lineStyle: { color: c.axisLineColor } },
      axisLabel: { color: c.textSecondaryColor },
      splitLine: { lineStyle: { color: c.splitLineColor, type: 'dashed' as const } },
    },
  }
}
