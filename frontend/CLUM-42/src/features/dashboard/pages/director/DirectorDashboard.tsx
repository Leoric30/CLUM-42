import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { TopBar } from '../../components/TopBar'
import { StatCard } from '../../components/StatCard'
import { useCurrentUser } from '@/hooks/useCurrentUser'
import { getClubApplications } from '@/features/enrollment/services/enrollmentService'
import styles from '../dashboard.module.css'

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('es-MX', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

const STATUS_LABEL: Record<string, string> = {
  PENDIENTE: 'Pendiente',
  REINTENTO: 'Reintento',
  APROBADA: 'Aprobada',
  RECHAZADA: 'Rechazada',
}

const STATUS_COLOR: Record<string, string> = {
  PENDIENTE: '#92400e',
  REINTENTO: '#9a3412',
  APROBADA: '#065f46',
  RECHAZADA: '#991b1b',
}

const STATUS_BG: Record<string, string> = {
  PENDIENTE: '#fef3c7',
  REINTENTO: '#ffedd5',
  APROBADA: '#d1fae5',
  RECHAZADA: '#fee2e2',
}

export function DirectorDashboard() {
  const { data: user } = useCurrentUser()
  const clubId = user?.clubId ?? 0

  // Solicitudes activas (PENDIENTE + REINTENTO): primeras 5 para resumen + totalElements para el conteo
  const { data: activePage, isLoading: loadingActive } = useQuery({
    queryKey: ['solicitudes-dashboard', clubId, 'activas'],
    queryFn: () => getClubApplications(clubId, 'TODAS', 0, 5),
    enabled: clubId > 0,
  })

  // Solicitudes aprobadas (solo necesitamos el total)
  const { data: approvedPage } = useQuery({
    queryKey: ['solicitudes-dashboard', clubId, 'APROBADA'],
    queryFn: () => getClubApplications(clubId, 'APROBADA', 0, 1),
    enabled: clubId > 0,
  })

  // Solicitudes rechazadas (solo necesitamos el total)
  const { data: rejectedPage } = useQuery({
    queryKey: ['solicitudes-dashboard', clubId, 'RECHAZADA'],
    queryFn: () => getClubApplications(clubId, 'RECHAZADA', 0, 1),
    enabled: clubId > 0,
  })

  const pendingCount = activePage?.totalElements ?? '—'
  const approvedCount = approvedPage?.totalElements ?? '—'
  const rejectedCount = rejectedPage?.totalElements ?? '—'
  const recentRequests = activePage?.content ?? []

  return (
    <div className={styles.page}>
      <TopBar title="Panel del Director" />

      {/* ─── Cabecera ─────────────────────────────────────────────────── */}
      <div className={styles.pageHeader}>
        <div className={styles.titleGroup}>
          <p className={styles.greeting}>Bienvenido</p>
          <h1 className={styles.title}>Panel de Dirección</h1>
          <p className={styles.subtitle}>
            Gestión general del club — solicitudes, miembros y revisiones.
          </p>
        </div>
      </div>

      {/* ─── Stats ────────────────────────────────────────────────────── */}
      <div className={styles.statsGrid}>
        <StatCard
          label="Solicitudes activas"
          value={loadingActive ? '…' : String(pendingCount)}
          sub="Pendientes y en reintento"
          color="amber"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
              <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Miembros activos"
          value="—"
          sub="Próximamente"
          color="green"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
            </svg>
          }
        />
        <StatCard
          label="Solicitudes aprobadas"
          value={String(approvedCount)}
          sub="Total histórico"
          color="indigo"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Solicitudes rechazadas"
          value={String(rejectedCount)}
          sub="Total histórico"
          color="red"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
            </svg>
          }
        />
      </div>

      {/* ─── Contenido principal ──────────────────────────────────────── */}
      <div className={styles.twoCol}>
        {/* Solicitudes recientes */}
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Solicitudes activas recientes</h2>
            <Link to="/dashboard/solicitudes" className={styles.sectionAction}>
              Ver todas
            </Link>
          </div>
          <div className={styles.sectionBody}>
            {loadingActive && (
              <p style={{ fontSize: '0.875rem', color: '#94a3b8', padding: '0.5rem 0' }}>
                Cargando…
              </p>
            )}
            {!loadingActive && recentRequests.length === 0 && (
              <div className={styles.emptyState}>
                No hay solicitudes activas. Ir a{' '}
                <Link to="/dashboard/solicitudes" style={{ color: '#6366f1' }}>
                  Solicitudes
                </Link>{' '}
                para gestionar.
              </div>
            )}
            {recentRequests.map((req) => (
              <div
                key={req.id}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '0.625rem 0',
                  borderBottom: '1px solid #f1f5f9',
                }}
              >
                <div>
                  <p style={{ fontSize: '0.875rem', fontWeight: 500, color: '#0f172a', margin: 0 }}>
                    {req.userName}
                  </p>
                  <p style={{ fontSize: '0.75rem', color: '#94a3b8', margin: 0 }}>
                    {formatDate(req.createdAt)}
                  </p>
                </div>
                <span
                  style={{
                    display: 'inline-flex',
                    alignItems: 'center',
                    padding: '0.15rem 0.55rem',
                    borderRadius: '999px',
                    fontSize: '0.7rem',
                    fontWeight: 600,
                    textTransform: 'uppercase',
                    letterSpacing: '0.04em',
                    background: STATUS_BG[req.status] ?? '#f1f5f9',
                    color: STATUS_COLOR[req.status] ?? '#475569',
                  }}
                >
                  {STATUS_LABEL[req.status] ?? req.status}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Acciones rápidas */}
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Acciones rápidas</h2>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.quickActions}>
              <Link to="/dashboard/solicitudes" className={styles.actionCard}>
                <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                  <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                  <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
                </svg>
                <span className={styles.actionLabel}>Ver solicitudes</span>
              </Link>
              <Link to="/dashboard/miembros" className={styles.actionCard}>
                <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                  <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
                </svg>
                <span className={styles.actionLabel}>Ver miembros</span>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
