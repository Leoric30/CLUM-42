import api from '@/services/api'
import type { Notification, Page } from '@/types'

export async function getNotifications(params?: {
  unreadOnly?: boolean
  page?: number
  size?: number
}): Promise<Page<Notification>> {
  const { data } = await api.get<Page<Notification>>('/notifications', { params })
  return data
}

export async function getUnreadCount(): Promise<number> {
  const { data } = await api.get<{ count: number }>('/notifications/unread-count')
  return data.count
}

export async function markAsRead(id: number): Promise<void> {
  await api.put(`/notifications/${id}/read`)
}

export async function markAllAsRead(): Promise<void> {
  await api.put('/notifications/read-all')
}
