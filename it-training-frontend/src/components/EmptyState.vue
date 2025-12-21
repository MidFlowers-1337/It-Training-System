<template>
  <div class="flex flex-col items-center justify-center text-center py-16 px-6">
    <div
      class="w-24 h-24 rounded-full bg-bg-tertiary border border-border-color flex items-center justify-center text-text-muted"
    >
      <component :is="iconComponent" v-if="iconComponent" class="w-12 h-12" />
      <span v-else class="text-5xl leading-none">{{ emoji }}</span>
    </div>

    <h3 class="mt-6 text-base font-semibold text-text-primary">{{ title }}</h3>
    <p v-if="description" class="mt-2 text-sm text-text-secondary leading-relaxed max-w-md">
      {{ description }}
    </p>

    <div v-if="$slots.action || actionText" class="mt-6">
      <slot name="action">
        <button v-if="actionText" type="button" class="btn btn-primary" @click="handleAction">
          {{ actionText }}
        </button>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  icon: {
    type: [Object, String],
    default: null,
  },
  emoji: {
    type: String,
    default: '—',
  },
  title: {
    type: String,
    default: '暂无数据',
  },
  description: {
    type: String,
    default: '',
  },
  actionText: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['action'])

const iconComponent = computed(() => {
  if (typeof props.icon === 'string') return null
  return props.icon
})

const handleAction = () => {
  emit('action')
}
</script>

