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
            <Clock class="w-4 h-4" />
            {{ course.durationHours }}课时
          </span>
          <span v-if="course.enrollmentCount" class="inline-flex items-center gap-1.5">
            <Users class="w-4 h-4" />
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
import { computed } from 'vue'
import { Bot, Cloud, Clock, Code, Database, Server, Users } from 'lucide-vue-next'

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
    BACKEND: Server,
    FRONTEND: Code,
    DATABASE: Database,
    AI: Bot,
    CLOUD: Cloud,
  }
  return map[props.course.category] || Code
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
