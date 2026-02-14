import request from './request'

export interface NotificationItem {
  id: number
  title: string
  content: string
  type: string
  isRead: boolean
  createdAt: string
}

export function getNotifications(params?: { page?: number; size?: number }) {
  return request.get('/v1/notifications', { params })
}

export function markAsRead(id: number) {
  return request.put(`/v1/notifications/${id}/read`)
}

export function markAllAsRead() {
  return request.put('/v1/notifications/read-all')
}

export function getUnreadCount() {
  return request.get('/v1/notifications/unread-count')
}
