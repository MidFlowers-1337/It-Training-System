import { ref, onMounted } from 'vue'
import { getNotifications, markAsRead, markAllAsRead, getUnreadCount } from '@/api/notification'
import type { NotificationItem } from '@/api/notification'

export function useNotifications() {
  const notifications = ref<NotificationItem[]>([])
  const unreadCount = ref(0)
  const loading = ref(false)
  const panelOpen = ref(false)

  async function fetchNotifications() {
    loading.value = true
    try {
      // request interceptor unwraps AxiosResponse, so runtime type is the actual data
      const data: any = await getNotifications({ page: 1, size: 20 })
      notifications.value = Array.isArray(data)
        ? data
        : (data?.records || data?.content || [])
    } catch {
      /* ignore */
    } finally {
      loading.value = false
    }
  }

  async function fetchUnreadCount() {
    try {
      // request interceptor unwraps AxiosResponse, so runtime type is the actual data
      const data: any = await getUnreadCount()
      unreadCount.value = typeof data === 'number' ? data : (data?.count || 0)
    } catch {
      /* ignore */
    }
  }

  async function handleMarkAsRead(id: number) {
    try {
      await markAsRead(id)
      const item = notifications.value.find(n => n.id === id)
      if (item) {
        item.isRead = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
      }
    } catch {
      /* ignore */
    }
  }

  async function handleMarkAllAsRead() {
    try {
      await markAllAsRead()
      notifications.value.forEach(n => (n.isRead = true))
      unreadCount.value = 0
    } catch {
      /* ignore */
    }
  }

  function togglePanel() {
    panelOpen.value = !panelOpen.value
  }

  return {
    notifications,
    unreadCount,
    loading,
    panelOpen,
    fetchNotifications,
    fetchUnreadCount,
    handleMarkAsRead,
    handleMarkAllAsRead,
    togglePanel,
  }
}
