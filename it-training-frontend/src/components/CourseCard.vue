<template>
  <div class="group card-hover overflow-hidden cursor-pointer flex flex-col h-full" @click="handleClick">
    <div class="relative aspect-video bg-bg-tertiary">
      <img
        v-if="course.coverImage"
        :src="course.coverImage"
        :alt="course.name"
        class="absolute inset-0 w-full h-full object-cover transition-transform duration-500 group-hover:scale-[1.03]"
      />

      <div v-else class="absolute inset-0 flex items-center justify-center">
        <div
          class="w-14 h-14 rounded-2xl bg-bg-secondary/70 backdrop-blur border border-border-color flex items-center justify-center text-primary shadow-sm"
        >
          <component :is="categoryIcon" class="w-7 h-7" />
        </div>
      </div>

      <div class="absolute top-3 right-3">
        <span class="px-2.5 py-1 rounded-full text-xs font-semibold border backdrop-blur" :class="difficultyBadgeClass">
          {{ difficultyName }}
        </span>
      </div>
    </div>

    <div class="p-5 flex flex-col flex-1">
      <div class="flex items-center gap-2 text-xs font-medium text-text-secondary">
        <span class="w-1.5 h-1.5 rounded-full" :style="{ backgroundColor: categoryColor }"></span>
        <span>{{ course.categoryName || '未分类' }}</span>
      </div>

      <h3
        class="mt-2 text-base font-semibold text-text-primary tracking-tight leading-snug line-clamp-2 group-hover:text-primary transition-colors"
      >
        {{ course.name }}
      </h3>

      <p v-if="course.description" class="mt-2 text-sm text-text-secondary leading-relaxed line-clamp-2">
        {{ course.description }}
      </p>

      <div v-if="course.tags && course.tags.length > 0" class="mt-3 flex flex-wrap gap-2">
        <span v-for="(tag, index) in course.tags.slice(0, 3)" :key="index" class="badge badge-secondary">
          {{ tag }}
        </span>
      </div>

      <div class="mt-auto pt-4 border-t border-border-light flex flex-col gap-3">
        <div class="flex items-center justify-between text-xs text-text-muted">
          <span class="inline-flex items-center gap-1.5">
            <IconClock class="w-4 h-4" />
            {{ course.durationHours }}课时
          </span>
          <span v-if="course.enrollmentCount" class="inline-flex items-center gap-1.5">
            <IconUsers class="w-4 h-4" />
            {{ course.enrollmentCount }}人
          </span>
        </div>

        <div v-if="showProgress && course.progress !== undefined" class="flex items-center gap-3">
          <div class="h-1.5 flex-1 rounded-full bg-bg-tertiary overflow-hidden">
            <div
              class="h-full bg-gradient-to-r from-primary-light to-secondary rounded-full transition-[width] duration-300"
              :style="{ width: `${course.progress}%` }"
            ></div>
          </div>
          <span class="text-xs font-semibold text-primary tabular-nums">{{ course.progress }}%</span>
        </div>

        <div v-if="$slots.actions" class="flex items-center justify-between gap-2">
          <slot name="actions" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, h } from 'vue'

const props = defineProps({
  course: {
    type: Object,
    required: true,
  },
  showProgress: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.course)
}

// 内联 SVG 图标组件
const IconServer = {
  render: () => h('svg', { class: 'w-7 h-7', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('rect', { width: '20', height: '8', x: '2', y: '2', rx: '2', ry: '2' }),
    h('rect', { width: '20', height: '8', x: '2', y: '14', rx: '2', ry: '2' }),
    h('line', { x1: '6', x2: '6.01', y1: '6', y2: '6' }),
    h('line', { x1: '6', x2: '6.01', y1: '18', y2: '18' })
  ])
}

const IconCode = {
  render: () => h('svg', { class: 'w-7 h-7', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('polyline', { points: '16 18 22 12 16 6' }),
    h('polyline', { points: '8 6 2 12 8 18' })
  ])
}

const IconDatabase = {
  render: () => h('svg', { class: 'w-7 h-7', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('ellipse', { cx: '12', cy: '5', rx: '9', ry: '3' }),
    h('path', { d: 'M3 5V19A9 3 0 0 0 21 19V5' }),
    h('path', { d: 'M3 12A9 3 0 0 0 21 12' })
  ])
}

const IconBot = {
  render: () => h('svg', { class: 'w-7 h-7', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M12 8V4H8' }),
    h('rect', { width: '16', height: '12', x: '4', y: '8', rx: '2' }),
    h('path', { d: 'M2 14h2' }), h('path', { d: 'M20 14h2' }),
    h('path', { d: 'M15 13v2' }), h('path', { d: 'M9 13v2' })
  ])
}

const IconCloud = {
  render: () => h('svg', { class: 'w-7 h-7', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z' })
  ])
}

const IconClock = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('circle', { cx: '12', cy: '12', r: '10' }),
    h('polyline', { points: '12 6 12 12 16 14' })
  ])
}

const IconUsers = {
  render: () => h('svg', { class: 'w-4 h-4', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': '2', 'stroke-linecap': 'round', 'stroke-linejoin': 'round' }, [
    h('path', { d: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' }),
    h('circle', { cx: '9', cy: '7', r: '4' }),
    h('path', { d: 'M22 21v-2a4 4 0 0 0-3-3.87' }),
    h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75' })
  ])
}

const categoryColor = computed(() => {
  const colors = {
    BACKEND: 'var(--primary-color)',
    FRONTEND: 'var(--primary-light)',
    DATABASE: 'var(--info-color)',
    AI: 'var(--success-color)',
    CLOUD: 'var(--warning-color)',
  }
  return colors[props.course.category] || 'var(--primary-color)'
})

const categoryIcon = computed(() => {
  const map = {
    BACKEND: IconServer,
    FRONTEND: IconCode,
    DATABASE: IconDatabase,
    AI: IconBot,
    CLOUD: IconCloud,
  }
  return map[props.course.category] || IconCode
})

const difficultyName = computed(() => {
  const names = { 1: '入门', 2: '初级', 3: '中级', 4: '高级' }
  return names[props.course.difficulty] || '未知'
})

const difficultyBadgeClass = computed(() => {
  const map = {
    1: 'bg-success/10 text-success border-success/30',
    2: 'bg-info/10 text-info border-info/30',
    3: 'bg-warning/10 text-warning border-warning/30',
    4: 'bg-error/10 text-error border-error/30',
  }
  return map[props.course.difficulty] || 'bg-bg-secondary text-text-secondary border-border-color'
})
</script>
