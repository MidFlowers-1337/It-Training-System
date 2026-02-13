import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const globalLoading = ref(false)
  const isMobile = ref(false)
  const commandPaletteOpen = ref(false)

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function setMobile(mobile: boolean) {
    isMobile.value = mobile
    if (mobile) sidebarCollapsed.value = true
  }

  function toggleCommandPalette() {
    commandPaletteOpen.value = !commandPaletteOpen.value
  }

  return {
    sidebarCollapsed,
    globalLoading,
    isMobile,
    commandPaletteOpen,
    toggleSidebar,
    setMobile,
    toggleCommandPalette,
  }
})
