<template>
  <div ref="containerRef" class="notification-wrapper">
    <!-- Bell trigger -->
    <button class="notification-trigger" @click="togglePanel">
      <Bell class="notification-bell-icon" :stroke-width="1.75" />
      <span v-if="unreadCount > 0" class="notification-badge">
        {{ unreadCount > 99 ? '99+' : unreadCount }}
      </span>
    </button>

    <!-- Panel dropdown -->
    <Transition name="notification-pop">
      <div v-if="panelOpen" class="notification-panel">
        <!-- Header -->
        <div class="notification-header">
          <h3 class="notification-title">通知</h3>
          <button
            v-if="unreadCount > 0"
            class="notification-mark-all"
            @click="handleMarkAllAsRead"
          >
            全部已读
          </button>
        </div>

        <!-- List -->
        <div class="notification-list">
          <div v-if="loading" class="notification-loading">
            加载中...
          </div>
          <template v-else-if="notifications.length">
            <div
              v-for="item in notifications"
              :key="item.id"
              :class="['notification-item', !item.isRead && 'notification-item--unread']"
              @click="handleMarkAsRead(item.id)"
            >
              <div class="notification-dot-wrapper">
                <span v-if="!item.isRead" class="notification-dot" />
              </div>
              <div class="notification-content">
                <p class="notification-item-title">{{ item.title }}</p>
                <p class="notification-item-time">{{ formatTime(item.createdAt) }}</p>
              </div>
            </div>
          </template>
          <div v-else class="notification-empty">
            <Bell class="notification-empty-icon" :stroke-width="1.5" />
            <p>暂无通知</p>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { onClickOutside } from '@vueuse/core'
import { Bell } from 'lucide-vue-next'
import { useNotifications } from '@/composables/useNotifications'

const containerRef = ref<HTMLElement>()
const {
  notifications,
  unreadCount,
  loading,
  panelOpen,
  fetchNotifications,
  fetchUnreadCount,
  handleMarkAsRead,
  handleMarkAllAsRead,
  togglePanel,
} = useNotifications()

onClickOutside(containerRef, () => {
  panelOpen.value = false
})

onMounted(() => {
  fetchUnreadCount()
})

function formatTime(dateStr: string): string {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours} 小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days} 天前`
  return date.toLocaleDateString('zh-CN')
}

// Fetch full list on first open
let fetched = false
function onPanelOpen() {
  if (!fetched) {
    fetchNotifications()
    fetched = true
  }
}

// Watch panelOpen to trigger fetch
import { watch } from 'vue'
watch(panelOpen, (val) => {
  if (val) onPanelOpen()
})
</script>

<style scoped>
.notification-wrapper {
  position: relative;
}

/* ── Trigger ── */
.notification-trigger {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
  border-radius: 8px;
  border: none;
  background: transparent;
  color: rgb(var(--color-text-secondary));
  cursor: pointer;
  transition: all 0.15s;
}

.notification-trigger:hover {
  color: rgb(var(--color-text));
  background: rgb(var(--color-surface-alt));
}

.notification-bell-icon {
  width: 18px;
  height: 18px;
}

.notification-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  font-size: 10px;
  font-weight: 600;
  line-height: 16px;
  text-align: center;
  color: #fff;
  background: rgb(var(--color-danger));
  border-radius: 999px;
}

/* ── Panel ── */
.notification-panel {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  width: 360px;
  max-height: 480px;
  background: rgb(var(--color-surface));
  border: 1px solid rgb(var(--color-border));
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-xl);
  z-index: 50;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ── Header ── */
.notification-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid rgb(var(--color-border));
}

.notification-title {
  font-size: 15px;
  font-weight: 600;
  color: rgb(var(--color-text));
}

.notification-mark-all {
  font-size: 13px;
  color: rgb(var(--color-primary));
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  transition: opacity 0.15s;
}

.notification-mark-all:hover {
  opacity: 0.8;
}

/* ── List ── */
.notification-list {
  flex: 1;
  overflow-y: auto;
  max-height: 400px;
}

.notification-loading {
  padding: 32px 16px;
  text-align: center;
  font-size: 14px;
  color: rgb(var(--color-text-tertiary));
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.1s;
}

.notification-item:hover {
  background: rgb(var(--color-surface-alt));
}

.notification-item--unread {
  background: rgb(var(--color-primary) / 0.04);
}

.notification-dot-wrapper {
  width: 8px;
  padding-top: 6px;
  flex-shrink: 0;
}

.notification-dot {
  display: block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgb(var(--color-primary));
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-item-title {
  font-size: 14px;
  color: rgb(var(--color-text));
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-item--unread .notification-item-title {
  font-weight: 500;
}

.notification-item-time {
  font-size: 12px;
  color: rgb(var(--color-text-tertiary));
  margin-top: 2px;
}

/* ── Empty ── */
.notification-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 40px 16px;
  color: rgb(var(--color-text-tertiary));
  font-size: 14px;
}

.notification-empty-icon {
  width: 32px;
  height: 32px;
  opacity: 0.4;
}

/* ── Transitions ── */
.notification-pop-enter-active {
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}
.notification-pop-leave-active {
  transition: all 0.15s ease-in;
}
.notification-pop-enter-from {
  opacity: 0;
  transform: translateY(-6px) scale(0.97);
}
.notification-pop-leave-to {
  opacity: 0;
  transform: translateY(-3px);
}
</style>
