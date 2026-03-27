import { useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { TopBar } from '@/features/dashboard/components/TopBar'
import { getClubs } from '../services/clubsService'
import { getMyApplications } from '@/features/applications/services/applicationsService'
import { ApplyModal } from '../components/ApplyModal'
import type { Club } from '@/types'
import styles from './ClubsPage.module.css'

export function ClubsPage() {
  const queryClient = useQueryClient()

  const { data: clubs = [], isLoading } = useQuery({
    queryKey: ['clubs'],
    queryFn: getClubs,
  })

  const { data: applications = [] } = useQuery({
    queryKey: ['my-applications'],
    queryFn: getMyApplications,
  })

  const [selectedClub, setSelectedClub] = useState<Club | null>(null)
  const [successClub, setSuccessClub] = useState<string | null>(null)

  // Clubes donde ya fue aprobado — no mostrar
  const memberClubIds = new Set(
    applications.filter((a) => a.status === 'APROBADA').map((a) => a.clubId),
  )

  // Clubes con solicitud activa (PENDIENTE o REINTENTO) — mostrar badge en lugar de botón
  const activeClubIds = new Set(
    applications
      .filter((a) => a.status === 'PENDIENTE' || a.status === 'REINTENTO')
      .map((a) => a.clubId),
  )

  const availableClubs = clubs.filter((c) => !memberClubIds.has(c.id))

  function handleSuccess() {
    setSuccessClub(selectedClub?.name ?? null)
    setSelectedClub(null)
    queryClient.invalidateQueries({ queryKey: ['my-applications'] })
  }

  return (
    <div className={styles.page}>
      <TopBar title="Clubes disponibles" />

      <div className={styles.content}>
        <div className={styles.header}>
          <h2 className={styles.heading}>Únete a un club</h2>
          <p className={styles.subheading}>
            Selecciona el club al que deseas pertenecer y envía tu solicitud.
          </p>
        </div>

        {successClub && (
          <div className={styles.success} role="alert">
            Tu solicitud para unirte a <strong>{successClub}</strong> fue enviada. El director la revisará pronto.
          </div>
        )}

        {isLoading ? (
          <p className={styles.loading}>Cargando clubes…</p>
        ) : availableClubs.length === 0 ? (
          <p className={styles.empty}>Ya eres miembro de todos los clubes disponibles.</p>
        ) : (
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
                      Aplicar
                    </button>
                  )}
                </div>
              )
            })}
          </div>
        )}
      </div>

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
