import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { TopBar } from '../../components/TopBar'
import { useCurrentUser } from '@/hooks/useCurrentUser'
import { getClubs } from '@/features/clubs/services/clubsService'
import { getMyApplications } from '@/features/applications/services/applicationsService'
import { ApplyModal } from '@/features/clubs/components/ApplyModal'
import type { Club } from '@/types'
import styles from './UsuarioDashboard.module.css'

const STATUS_LABEL: Record<string, string> = {
  PENDIENTE: 'Pendiente',
  REINTENTO: 'En reintento',
  APROBADA: 'Aprobada',
  RECHAZADA: 'Rechazada',
}

const STATUS_COLOR: Record<string, { bg: string; text: string }> = {
  PENDIENTE: { bg: '#fef3c7', text: '#92400e' },
  REINTENTO: { bg: '#ffedd5', text: '#9a3412' },
  APROBADA:  { bg: '#d1fae5', text: '#065f46' },
  RECHAZADA: { bg: '#fee2e2', text: '#991b1b' },
}

export function UsuarioDashboard() {
  const { data: user } = useCurrentUser()
  const queryClient = useQueryClient()

  const { data: clubs = [], isLoading: loadingClubs } = useQuery({
    queryKey: ['clubs'],
    queryFn: getClubs,
  })

  const { data: applications = [] } = useQuery({
    queryKey: ['my-applications'],
    queryFn: getMyApplications,
  })

  const [selectedClub, setSelectedClub] = useState<Club | null>(null)
  const [successClub, setSuccessClub] = useState<string | null>(null)

  // Ids de clubes con solicitud activa (PENDIENTE o REINTENTO) — no mostrar botón "Aplicar"
  const activeClubIds = new Set(
    applications
      .filter((a) => a.status === 'PENDIENTE' || a.status === 'REINTENTO')
      .map((a) => a.clubId),
  )

  // Ids de clubes donde el usuario ya fue aprobado — ya es miembro
  const memberClubIds = new Set(
    applications.filter((a) => a.status === 'APROBADA').map((a) => a.clubId),
  )

  function handleSuccess() {
    setSuccessClub(selectedClub?.name ?? null)
    setSelectedClub(null)
    queryClient.invalidateQueries({ queryKey: ['my-applications'] })
  }

  const firstName = user?.email?.split('@')[0] ?? ''

  return (
    <div className={styles.page}>
      <TopBar title="Inicio" />

      {/* ─── Bienvenida ───────────────────────────────────────────────── */}
      <div className={styles.welcome}>
        <div>
          <h1 className={styles.welcomeTitle}>Bienvenido, {firstName}</h1>
          <p className={styles.welcomeSub}>
            Explora los clubes disponibles y envía tu solicitud de inscripción.
          </p>
        </div>
        {applications.length > 0 && (
          <Link to="/my-requests" className={styles.myRequestsLink}>
            Ver mis solicitudes ({applications.length})
          </Link>
        )}
      </div>

      {/* ─── Solicitudes activas del usuario ──────────────────────────── */}
      {applications.length > 0 && (
        <div className={styles.statusSection}>
          <h2 className={styles.sectionTitle}>Mis solicitudes</h2>
          <div className={styles.statusList}>
            {applications.slice(0, 3).map((app) => {
              const colors = STATUS_COLOR[app.status] ?? { bg: '#f1f5f9', text: '#475569' }
              return (
                <div key={app.id} className={styles.statusCard}>
                  <div className={styles.statusCardLeft}>
                    <span className={styles.statusClubName}>{app.clubName}</span>
                    {app.status === 'RECHAZADA' && app.rejectionReason && (
                      <span className={styles.statusRejReason}>{app.rejectionReason}</span>
                    )}
                  </div>
                  <span
                    className={styles.statusBadge}
                    style={{ background: colors.bg, color: colors.text }}
                  >
                    {STATUS_LABEL[app.status] ?? app.status}
                  </span>
                </div>
              )
            })}
            {applications.length > 3 && (
              <Link to="/my-requests" className={styles.viewAllLink}>
                Ver todas ({applications.length})
              </Link>
            )}
          </div>
        </div>
      )}

      {/* ─── Mensaje de éxito ─────────────────────────────────────────── */}
      {successClub && (
        <div className={styles.successBanner} role="alert">
          Tu solicitud para unirte a <strong>{successClub}</strong> fue enviada. El director la revisará pronto.
        </div>
      )}

      {/* ─── Clubes disponibles ───────────────────────────────────────── */}
      <div>
        <h2 className={styles.sectionTitle}>Clubes disponibles</h2>
        <p className={styles.sectionSub}>
          Selecciona el club al que deseas pertenecer y envía tu solicitud con el comprobante de pago.
        </p>

        {loadingClubs ? (
          <p className={styles.loading}>Cargando clubes…</p>
        ) : (() => {
          const availableClubs = clubs.filter((c) => !memberClubIds.has(c.id))
          if (availableClubs.length === 0) {
            return (
              <p className={styles.emptyMessage}>
                Ya eres miembro de todos los clubes disponibles.
              </p>
            )
          }
          return (
            <div className={styles.grid}>
              {availableClubs.map((club) => {
                const hasPending = activeClubIds.has(club.id)
                return (
                  <div key={club.id} className={styles.card}>
                    <div className={styles.cardIcon}>
                      {club.name.charAt(0).toUpperCase()}
                    </div>
                    <h3 className={styles.cardTitle}>{club.name}</h3>
                    <p className={styles.cardDesc}>{club.description}</p>

                    {hasPending ? (
                      <span className={styles.pendingBadge}>Solicitud enviada</span>
                    ) : (
                      <button
                        className={styles.applyBtn}
                        onClick={() => {
                          setSuccessClub(null)
                          setSelectedClub(club)
                        }}
                      >
                        Solicitar inscripción
                      </button>
                    )}
                  </div>
                )
              })}
            </div>
          )
        })()}
      </div>

      {/* ─── Modal de solicitud ───────────────────────────────────────── */}
      {selectedClub && (
        <ApplyModal
          club={selectedClub}
          onClose={() => setSelectedClub(null)}
          onSuccess={handleSuccess}
        />
      )}
    </div>
  )
}
