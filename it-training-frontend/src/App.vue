<template>
  <ErrorBoundary>
    <component :is="layoutComponent">
      <router-view v-slot="{ Component, route }">
        <Transition name="page" mode="out-in">
          <component :is="Component" :key="route.path" />
        </Transition>
      </router-view>
    </component>
  </ErrorBoundary>

  <!-- Global overlays -->
  <ToastContainer />
  <CommandPalette />
</template>

<script setup lang="ts">
import { computed, watchEffect, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useWindowSize } from '@vueuse/core'
import { useAppStore } from '@/stores/app'
import StudentLayout from '@/layouts/StudentLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import InstructorLayout from '@/layouts/InstructorLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'
import ToastContainer from '@/components/primitives/ToastContainer.vue'
import ErrorBoundary from '@/components/patterns/ErrorBoundary.vue'
import CommandPalette from '@/components/patterns/CommandPalette.vue'

const route = useRoute()
const appStore = useAppStore()
const { width } = useWindowSize()
const isMobile = computed(() => width.value < 768)
watchEffect(() => appStore.setMobile(isMobile.value))

const layouts: Record<string, any> = { student: StudentLayout, admin: AdminLayout, instructor: InstructorLayout, auth: AuthLayout, blank: BlankLayout }
const layoutComponent = computed(() => layouts[(route.meta?.layout as string) || 'blank'] || BlankLayout)

/* ── Cmd+K global shortcut ── */
function handleKeydown(e: KeyboardEvent) {
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    appStore.toggleCommandPalette()
  }
}

onMounted(() => document.addEventListener('keydown', handleKeydown))
onUnmounted(() => document.removeEventListener('keydown', handleKeydown))
</script>
