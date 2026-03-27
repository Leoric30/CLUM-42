import { useState } from 'react'
import { useUnreadCount } from '../hooks/useNotifications'
import { NotificationDropdown } from './NotificationDropdown'
import styles from './NotificationBell.module.css'

export function NotificationBell() {
  const { data: count = 0 } = useUnreadCount()
  const [open, setOpen] = useState(false)

  return (
    <div className={styles.wrapper}>
      <button
        className={styles.bell}
        onClick={() => setOpen((v) => !v)}
        aria-label={`Notificaciones${count > 0 ? ` (${count} sin leer)` : ''}`}
      >
        {/* Bell icon */}
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>

        {count > 0 && (
          <span className={styles.badge}>
            {count > 99 ? '99+' : count}
          </span>
        )}
      </button>

      {open && (
        <NotificationDropdown onClose={() => setOpen(false)} />
      )}
    </div>
  )
}
