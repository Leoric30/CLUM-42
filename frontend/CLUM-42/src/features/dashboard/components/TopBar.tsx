import { useCurrentUser, getPrimaryClubRole } from '@/hooks/useCurrentUser'
import { NotificationBell } from '@/features/notifications/components/NotificationBell'
import styles from './TopBar.module.css'

const ROLE_LABELS: Record<string, string> = {
  DIRECTOR: 'Director',
  SUBDIRECTOR: 'Subdirector',
  SECRETARIO: 'Secretario',
  LOGISTICA: 'Logística',
  MEDIA: 'Media',
  CAPELLAN: 'Capellán',
  TESORERO: 'Tesorero',
  CONSEJERO: 'Consejero',
  ADMIN: 'Administrador',
}

interface TopBarProps {
  title?: string
}

export function TopBar({ title }: TopBarProps) {
  const { data: user } = useCurrentUser()
  const primaryRole = getPrimaryClubRole(user)

  const initials = user?.email
    ? user.email.slice(0, 2).toUpperCase()
    : '??'

  const roleLabel = primaryRole
    ? ROLE_LABELS[primaryRole]
    : user?.roles?.[0]
    ? ROLE_LABELS[user.roles[0]] ?? user.roles[0]
    : ''

  return (
    <header className={styles.topbar}>
      <p className={styles.pageTitle}>{title ?? 'Panel'}</p>

      <div className={styles.right}>
        <NotificationBell />
        {user && (
          <div className={styles.userInfo}>
            <span className={styles.userEmail}>{user.email}</span>
            {roleLabel && <span className={styles.userRole}>{roleLabel}</span>}
          </div>
        )}
        <div className={styles.avatar}>{initials}</div>
      </div>
    </header>
  )
}
