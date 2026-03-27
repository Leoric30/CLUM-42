import { useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { TopBar } from '@/features/dashboard/components/TopBar'
import { getMyApplications } from '../services/applicationsService'
import { ResubmitModal } from '../components/ResubmitModal'
import type { Application, RequestStatus } from '@/types'
import styles from './MyApplicationsPage.module.css'

const STATUS_LABEL: Record<RequestStatus, string> = {
  PENDIENTE: 'Pendiente',
  APROBADA: 'Aprobada',
  RECHAZADA: 'Rechazada',
  REINTENTO: 'En reintento',
}

const STATUS_CLASS: Record<RequestStatus, string> = {
  PENDIENTE: 'badgePending',
  APROBADA: 'badgeApproved',
  RECHAZADA: 'badgeRejected',
  REINTENTO: 'badgeResubmitted',
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('es-MX', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

export function MyApplicationsPage() {
  const queryClient = useQueryClient()
  const { data: applications = [], isLoading } = useQuery({
    queryKey: ['my-applications'],
    queryFn: getMyApplications,
  })

  const [resubmitting, setResubmitting] = useState<Application | null>(null)
  const [successMsg, setSuccessMsg] = useState<string | null>(null)

  function handleResubmitSuccess() {
    setSuccessMsg('Tu solicitud fue reintentada. El director la revisará pronto.')
    setResubmitting(null)
    queryClient.invalidateQueries({ queryKey: ['my-applications'] })
  }

  return (
    <div className={styles.page}>
      <TopBar title="Mis solicitudes" />

      <div className={styles.content}>
        <div className={styles.header}>
          <h2 className={styles.heading}>Mis solicitudes de inscripción</h2>
          <p className={styles.subheading}>
            Historial de todas tus solicitudes para unirte a clubes.
          </p>
        </div>

        {successMsg && (
          <div className={styles.success} role="alert">
            {successMsg}
          </div>
        )}

        {isLoading ? (
          <p className={styles.loading}>Cargando solicitudes…</p>
        ) : applications.length === 0 ? (
          <div className={styles.empty}>
            <p>No has enviado ninguna solicitud todavía.</p>
            <a href="/dashboard/clubs" className={styles.clubsLink}>
              Ver clubes disponibles →
            </a>
          </div>
        ) : (
          <div className={styles.table}>
            <div className={styles.tableHeader}>
              <span>Club</span>
              <span>Estado</span>
              <span>Fecha</span>
              <span>Reintentos</span>
              <span>Acción</span>
            </div>

            {applications.map((app) => (
              <div key={app.id} className={styles.row}>
                <span className={styles.clubName}>{app.clubName}</span>

                <span className={`${styles.badge} ${styles[STATUS_CLASS[app.status]]}`}>
                  {STATUS_LABEL[app.status]}
                </span>

                <span className={styles.date}>{formatDate(app.createdAt)}</span>

                <span className={styles.version}>
                  {app.version > 0 ? `× ${app.version}` : '—'}
                </span>

                <span>
                  {app.status === 'RECHAZADA' ? (
                    <button
                      className={styles.resubmitBtn}
                      onClick={() => {
                        setSuccessMsg(null)
                        setResubmitting(app)
                      }}
                    >
                      Reintentar
                    </button>
                  ) : (
                    <span className={styles.noAction}>—</span>
                  )}
                </span>
              </div>
            ))}

            {/* Panel de detalle: motivo de rechazo */}
            {applications.filter((a) => a.status === 'RECHAZADA' && a.rejectionReason).map((app) => (
              <div key={`reason-${app.id}`} className={styles.rejectionRow}>
                <strong>{app.clubName}:</strong>{' '}
                <span className={styles.rejectionText}>{app.rejectionReason}</span>
              </div>
            ))}
          </div>
        )}
      </div>

      {resubmitting && (
        <ResubmitModal
          applicationId={resubmitting.id}
          clubName={resubmitting.clubName}
          onClose={() => setResubmitting(null)}
          onSuccess={handleResubmitSuccess}
        />
      )}
    </div>
  )
}
