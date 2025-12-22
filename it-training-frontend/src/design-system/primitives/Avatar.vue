<script setup lang="ts">
/**
 * Avatar - 头像组件
 * 支持图片、文字回退、多种尺寸
 */
defineOptions({ name: 'DsAvatar' });

interface Props {
  src?: string;
  alt?: string;
  size?: number | 'sm' | 'md' | 'lg' | 'xl';
  rounded?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  rounded: true,
});

const sizeMap: Record<string, number> = {
  sm: 32,
  md: 40,
  lg: 56,
  xl: 80,
};

const computedSize = computed(() => {
  if (typeof props.size === 'number') return props.size;
  return sizeMap[props.size] || 40;
});

const hasError = ref(false);

const handleError = () => {
  hasError.value = true;
};

const showFallback = computed(() => !props.src || hasError.value);
</script>

<template>
  <div
    class="inline-flex items-center justify-center overflow-hidden bg-fill-tertiary text-text-secondary font-medium select-none flex-shrink-0"
    :class="rounded ? 'rounded-full' : 'rounded-lg'"
    :style="{
      width: `${computedSize}px`,
      height: `${computedSize}px`,
      fontSize: `${computedSize * 0.4}px`,
    }"
  >
    <img
      v-if="!showFallback"
      :src="src"
      :alt="alt"
      class="w-full h-full object-cover"
      @error="handleError"
    />
    <slot v-else>
      <!-- Default user icon -->
      <svg
        class="w-1/2 h-1/2"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
        <circle cx="12" cy="7" r="4" />
      </svg>
    </slot>
  </div>
</template>
