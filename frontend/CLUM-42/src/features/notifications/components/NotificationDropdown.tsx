import { useQuery, useQueryClient } from '@tanstack/react-query'
import {
  getNotifications,
  markAsRead,
  markAllAsRead,
} from '../services/notificationsService'
import { UNREAD_COUNT_KEY } from '../hooks/useNotifications'
import type { Notification } from '@/types'
import styles from './NotificationDropdown.module.css'

interface Props {
  onClose: () => void
}

function timeAgo(iso: string): string {
  const diff = Date.now() - new Date(iso).getTime()
  const mins = Math.floor(diff / 60_000)
  if (mins < 1) return 'ahora'
  if (mins < 60) return `hace ${mins} min`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `hace ${hrs} h`
  const days = Math.floor(hrs / 24)
  return `hace ${days} d`
}

export function NotificationDropdown({ onClose }: Props) {
  const queryClient = useQueryClient()

  const { data } = useQuery({
    queryKey: ['notifications', 'list'],
    queryFn: () => getNotifications({ page: 0, size: 10 }),
  })

  const notifications: Notification[] = data?.content ?? []

  async function handleMarkOne(id: number) {
    await markAsRead(id)
    queryClient.invalidateQueries({ queryKey: ['notifications'] })
    queryClient.invalidateQueries({ queryKey: UNREAD_COUNT_KEY })
  }

  async function handleMarkAll() {
    await markAllAsRead()
    queryClient.invalidateQueries({ queryKey: ['notifications'] })
    queryClient.invalidateQueries({ queryKey: UNREAD_COUNT_KEY })
  }

  return (
    <>
      {/* Backdrop transparente para cerrar */}
      <div className={styles.backdrop} onClick={onClose} />

      <div className={styles.dropdown}>
        <div className={styles.header}>
          <span className={styles.title}>Notificaciones</span>
          {notifications.some((n) => !n.leida) && (
            <button className={styles.markAll} onClick={handleMarkAll}>
              Marcar todo como leído
            </button>
          )}
        </div>

        <div className={styles.list}>
          {notifications.length === 0 ? (
            <p className={styles.empty}>No tienes notificaciones.</p>
          ) : (
            notifications.map((n) => (
              <div
                key={n.id}
                className={`${styles.item} ${!n.leida ? styles.unread : ''}`}
                onClick={() => !n.leida && handleMarkOne(n.id)}
              >
                <div className={styles.itemContent}>
                  <p className={styles.itemTitle}>{n.titulo}</p>
                  <p className={styles.itemMsg}>{n.mensaje}</p>
                </div>
                <span className={styles.time}>{timeAgo(n.fechaCreacion)}</span>
              </div>
            ))
          )}
        </div>
      </div>
    </>
  )
}
