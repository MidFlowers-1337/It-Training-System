<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="cmd-overlay" @click.self="close">
        <div class="cmd-backdrop" />
        <div class="cmd-dialog">
          <!-- Search -->
          <div class="cmd-search">
            <Search class="cmd-search-icon" :stroke-width="1.75" />
            <input
              ref="inputRef"
              v-model="query"
              class="cmd-search-input"
              placeholder="搜索命令..."
              @keydown="handleKeydown"
            />
            <kbd class="cmd-kbd">Esc</kbd>
          </div>

          <!-- List -->
          <div class="cmd-list">
            <div
              v-for="(item, i) in filtered"
              :key="item.id"
              :class="['cmd-item', i === selected && 'cmd-item--active']"
              @click="execute(item)"
              @mouseenter="selected = i"
            >
              <component :is="item.icon" class="cmd-item-icon" :stroke-width="1.75" />
              <span class="cmd-item-label">{{ item.label }}</span>
              <span v-if="item.type === 'theme'" class="cmd-item-badge">主题</span>
            </div>
            <div v-if="!filtered.length" class="cmd-empty">
              未找到匹配的命令
            </div>
          </div>

          <!-- Footer -->
          <div class="cmd-footer">
            <span><kbd>↑</kbd><kbd>↓</kbd> 导航</span>
            <span><kbd>Enter</kbd> 执行</span>
            <span><kbd>Esc</kbd> 关闭</span>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStudentNav } from '@/composables/useStudentNav'
import { useThemeStore } from '@/stores/theme'
import { useAppStore } from '@/stores/app'
import { Search, Palette } from 'lucide-vue-next'
import type { Component } from 'vue'

interface CommandItem {
  id: string
  icon: Component
  label: string
  type: 'nav' | 'theme'
  action: () => void
}

const router = useRouter()
const themeStore = useThemeStore()
const appStore = useAppStore()
const { mainNavItems, dropdownItems } = useStudentNav()

const inputRef = ref<HTMLInputElement>()
const query = ref('')
const selected = ref(0)

const isOpen = computed(() => appStore.commandPaletteOpen)

/* ── Build command list ── */
const commands = computed<CommandItem[]>(() => {
  const navCommands: CommandItem[] = [...mainNavItems, ...dropdownItems].map(item => ({
    id: `nav-${item.path}`,
    icon: item.icon,
    label: item.label,
    type: 'nav' as const,
    action: () => {
      router.push(item.path)
      close()
    },
  }))

  const themeCommand: CommandItem = {
    id: 'theme-cycle',
    icon: Palette,
    label: '切换主题',
    type: 'theme' as const,
    action: () => {
      themeStore.cycleTheme()
      close()
    },
  }

  return [...navCommands, themeCommand]
})

/* ── Fuzzy filter ── */
const filtered = computed(() => {
  if (!query.value.trim()) return commands.value
  const q = query.value.toLowerCase()
  return commands.value.filter(cmd => cmd.label.toLowerCase().includes(q))
})

/* ── Keyboard navigation ── */
function handleKeydown(e: KeyboardEvent) {
  switch (e.key) {
    case 'ArrowDown':
      e.preventDefault()
      selected.value = (selected.value + 1) % (filtered.value.length || 1)
      break
    case 'ArrowUp':
      e.preventDefault()
      selected.value = (selected.value - 1 + filtered.value.length) % (filtered.value.length || 1)
      break
    case 'Enter':
      e.preventDefault()
      if (filtered.value.length > 0 && filtered.value[selected.value]) {
        filtered.value[selected.value]!.action()
      }
      break
    case 'Escape':
      e.preventDefault()
      close()
      break
  }
}

function execute(item: CommandItem) {
  item.action()
}

function close() {
  appStore.commandPaletteOpen = false
  query.value = ''
  selected.value = 0
}

/* ── Reset on open ── */
watch(isOpen, (val) => {
  if (val) {
    query.value = ''
    selected.value = 0
    nextTick(() => inputRef.value?.focus())
  }
})

/* ── Reset selected on filter change ── */
watch(filtered, () => {
  selected.value = 0
})

/* ── Global shortcut: Cmd+K / Ctrl+K ── */
function handleGlobalKeydown(e: KeyboardEvent) {
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault()
    appStore.toggleCommandPalette()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleGlobalKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleGlobalKeydown)
})
</script>

<style scoped>
/* ── Overlay ── */
.cmd-overlay {
  position: fixed;
  inset: 0;
  z-index: 9998;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 20vh;
}

.cmd-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
}

/* ── Dialog ── */
.cmd-dialog {
  position: relative;
  width: 100%;
  max-width: 540px;
  margin: 0 16px;
  background: rgb(var(--color-surface));
  border: 1px solid rgb(var(--color-border));
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  overflow: hidden;
}

/* ── Search ── */
.cmd-search {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid rgb(var(--color-border));
}

.cmd-search-icon {
  width: 18px;
  height: 18px;
  color: rgb(var(--color-text-tertiary));
  flex-shrink: 0;
}

.cmd-search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: rgb(var(--color-text));
  line-height: 1.5;
}

.cmd-search-input::placeholder {
  color: rgb(var(--color-text-tertiary));
}

.cmd-kbd {
  padding: 2px 6px;
  font-size: 11px;
  font-family: inherit;
  color: rgb(var(--color-text-tertiary));
  background: rgb(var(--color-surface-alt));
  border: 1px solid rgb(var(--color-border));
  border-radius: 4px;
  flex-shrink: 0;
}

/* ── List ── */
.cmd-list {
  max-height: 320px;
  overflow-y: auto;
  padding: 8px;
}

.cmd-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: var(--radius);
  cursor: pointer;
  transition: background-color 0.1s;
}

.cmd-item--active {
  background: rgb(var(--color-primary) / 0.08);
}

.cmd-item-icon {
  width: 18px;
  height: 18px;
  color: rgb(var(--color-text-secondary));
  flex-shrink: 0;
}

.cmd-item--active .cmd-item-icon {
  color: rgb(var(--color-primary));
}

.cmd-item-label {
  flex: 1;
  font-size: 14px;
  color: rgb(var(--color-text));
}

.cmd-item-badge {
  padding: 2px 8px;
  font-size: 11px;
  color: rgb(var(--color-primary));
  background: rgb(var(--color-primary) / 0.1);
  border-radius: 999px;
}

.cmd-empty {
  padding: 24px 16px;
  text-align: center;
  font-size: 14px;
  color: rgb(var(--color-text-tertiary));
}

/* ── Footer ── */
.cmd-footer {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 16px;
  border-top: 1px solid rgb(var(--color-border));
  font-size: 12px;
  color: rgb(var(--color-text-tertiary));
}

.cmd-footer kbd {
  display: inline-block;
  padding: 1px 5px;
  font-size: 11px;
  font-family: inherit;
  background: rgb(var(--color-surface-alt));
  border: 1px solid rgb(var(--color-border));
  border-radius: 3px;
  margin: 0 2px;
}

/* ── Transitions ── */
.modal-enter-active {
  transition: opacity 0.2s ease;
}
.modal-enter-active .cmd-dialog {
  transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1), opacity 0.2s ease;
}
.modal-leave-active {
  transition: opacity 0.15s ease;
}
.modal-leave-active .cmd-dialog {
  transition: transform 0.15s ease, opacity 0.15s ease;
}
.modal-enter-from {
  opacity: 0;
}
.modal-enter-from .cmd-dialog {
  opacity: 0;
  transform: scale(0.96) translateY(-8px);
}
.modal-leave-to {
  opacity: 0;
}
.modal-leave-to .cmd-dialog {
  opacity: 0;
  transform: scale(0.98);
}
</style>
