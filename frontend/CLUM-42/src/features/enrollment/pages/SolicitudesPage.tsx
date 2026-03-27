import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { TopBar } from '@/features/dashboard/components/TopBar'
import {
  getClubApplications,
  approveRequest,
  rejectRequest,
} from '../services/enrollmentService'
import { useCurrentUser } from '@/hooks/useCurrentUser'
import type { Application, RequestStatus } from '@/types'
import styles from './SolicitudesPage.module.css'

type FilterStatus = 'TODAS' | RequestStatus

function formatDate(iso: string) {
  return new Date(iso).toLocaleDateString('es-MX', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  })
}

// ─── Modal de rechazo ────────────────────────────────────────────────────────

interface RejectModalProps {
  request: Application
  onClose: () => void
  onConfirm: (reason: string) => void
  loading: boolean
  error: string | null
}

function RejectModal({ request, onClose, onConfirm, loading, error }: RejectModalProps) {
  const [reason, setReason] = useState('')

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <h2 className={styles.modalTitle}>Rechazar solicitud</h2>
        <p className={styles.modalSub}>
          Solicitud de <strong>{request.userName}</strong> — indique el motivo del rechazo.
        </p>

        <label className={styles.modalLabel} htmlFor="rejectReason">
          Motivo del rechazo
        </label>
        <textarea
          id="rejectReason"
          className={styles.modalTextarea}
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          placeholder="Describe el motivo del rechazo…"
        />

        {error && <p className={styles.error}>{error}</p>}

        <div className={styles.modalFooter}>
          <button className={styles.btnCancel} onClick={onClose} disabled={loading}>
            Cancelar
          </button>
          <button
            className={styles.btnConfirmReject}
            onClick={() => onConfirm(reason)}
            disabled={loading || !reason.trim()}
          >
            {loading ? 'Rechazando…' : 'Confirmar rechazo'}
          </button>
        </div>
      </div>
    </div>
  )
}

// ─── Modal de comprobante ────────────────────────────────────────────────────

interface ReceiptModalProps {
  url: string
  userName: string
  onClose: () => void
}


function ReceiptModal({ url, userName, onClose }: ReceiptModalProps) {
  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.receiptModal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.receiptHeader}>
          <div>
            <h2 className={styles.modalTitle}>Comprobante de pago</h2>
            <p className={styles.modalSub}>{userName}</p>
          </div>
          <button className={styles.closeBtn} onClick={onClose} aria-label="Cerrar">✕</button>
        </div>
        <div className={styles.receiptBody}>
          <img src={url} alt="Comprobante de pago" className={styles.receiptImg} />
        </div>
      </div>
    </div>
  )
}

// ─── Componente principal ────────────────────────────────────────────────────

export function SolicitudesPage() {
  const { data: user } = useCurrentUser()
  const queryClient = useQueryClient()

  const clubId = user?.clubId ?? 0

  const [filter, setFilter] = useState<FilterStatus>('PENDIENTE')
  const [page, setPage] = useState(0)
  const [rejectTarget, setRejectTarget] = useState<Application | null>(null)
  const [rejectError, setRejectError] = useState<string | null>(null)
  const [receiptTarget, setReceiptTarget] = useState<Application | null>(null)

  const queryKey = ['solicitudes', clubId, filter, page]

  const { data, isLoading, isError } = useQuery({
    queryKey,
    queryFn: () => getClubApplications(clubId, filter, page),
    enabled: clubId > 0,
  })

  const approveMutation = useMutation({
    mutationFn: (id: number) => approveRequest(clubId, id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['solicitudes'] }),
  })

  const rejectMutation = useMutation({
    mutationFn: ({ id, reason }: { id: number; reason: string }) =>
      rejectRequest(clubId, id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['solicitudes'] })
      setRejectTarget(null)
      setRejectError(null)
    },
    onError: () => setRejectError('Error al rechazar la solicitud. Inténtalo de nuevo.'),
  })

  const allRequests: Application[] = data?.content ?? []

  const FILTERS: { label: string; value: FilterStatus }[] = [
    { label: 'Pendientes', value: 'PENDIENTE' },
    { label: 'En reintento', value: 'REINTENTO' },
    { label: 'Aprobadas', value: 'APROBADA' },
    { label: 'Rechazadas', value: 'RECHAZADA' },
    { label: 'Todas', value: 'TODAS' },
  ]

  return (
    <div className={styles.page}>
      <TopBar title="Solicitudes de inscripción" />

      {/* ─── Cabecera ─────────────────────────────────────────────────── */}
      <div className={styles.header}>
        <div className={styles.titleGroup}>
          <h1 className={styles.title}>Solicitudes de inscripción</h1>
          <p className={styles.subtitle}>
            Gestiona las solicitudes de aspirantes del club.
          </p>
        </div>
      </div>

      {/* ─── Sin club configurado ─────────────────────────────────────── */}
      {clubId === 0 && (
        <div style={{ color: '#dc2626', fontSize: '0.875rem', padding: '1rem', background: '#fef2f2', borderRadius: '8px' }}>
          Tu cuenta no tiene un club asignado. Contacta al administrador para configurar el <code>clubId</code>.
        </div>
      )}

      {clubId > 0 && (
        <>
          {/* ─── Filtros ────────────────────────────────────────────────── */}
          <div className={styles.filters}>
            {FILTERS.map((f) => (
              <button
                key={f.value}
                className={`${styles.filterBtn} ${filter === f.value ? styles.active : ''}`}
                onClick={() => { setFilter(f.value); setPage(0) }}
              >
                {f.label}
              </button>
            ))}
          </div>

          {/* ─── Tabla ──────────────────────────────────────────────────── */}
          <div className={styles.tableWrap}>
            <table className={styles.table}>
              <thead>
                <tr>
                  <th>Aspirante</th>
                  <th>Club</th>
                  <th>Fecha</th>
                  <th>Comprobante</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {isLoading && (
                  <tr className={styles.stateRow}>
                    <td colSpan={6}>Cargando solicitudes…</td>
                  </tr>
                )}
                {isError && (
                  <tr className={styles.stateRow}>
                    <td colSpan={6} style={{ color: '#dc2626' }}>
                      Error al cargar las solicitudes.
                    </td>
                  </tr>
                )}
                {!isLoading && !isError && allRequests.length === 0 && (
                  <tr className={styles.stateRow}>
                    <td colSpan={6}>No hay solicitudes en este estado.</td>
                  </tr>
                )}
                {allRequests.map((req) => (
                  <tr key={req.id}>
                    <td>
                      <div className={styles.nameCell}>{req.userName}</div>
                      <div className={styles.emailCell}>{req.userEmail}</div>
                    </td>
                    <td>{req.clubName}</td>
                    <td className={styles.dateCell}>{formatDate(req.createdAt)}</td>
                    <td>
                      {req.paymentReceipt && req.paymentReceipt !== 'Sin comprobante' ? (
                        <button
                          className={styles.btnReceipt}
                          onClick={() => setReceiptTarget(req)}
                        >
                          Ver comprobante
                        </button>
                      ) : (
                        <span className={styles.noReceipt}>Sin comprobante</span>
                      )}
                    </td>
                    <td>
                      <span className={`${styles.badge} ${styles[`badge${req.status}`]}`}>
                        {req.status}
                      </span>
                    </td>
                    <td>
                      {(req.status === 'PENDIENTE' || req.status === 'REINTENTO') && (
                        <div className={styles.actions}>
                          <button
                            className={styles.btnApprove}
                            disabled={approveMutation.isPending}
                            onClick={() => approveMutation.mutate(req.id)}
                          >
                            Aprobar
                          </button>
                          <button
                            className={styles.btnReject}
                            disabled={rejectMutation.isPending}
                            onClick={() => { setRejectTarget(req); setRejectError(null) }}
                          >
                            Rechazar
                          </button>
                        </div>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* ─── Paginación ─────────────────────────────────────────── */}
            {data && data.totalPages > 1 && (
              <div className={styles.pagination}>
                <button
                  className={styles.pageBtn}
                  disabled={page === 0}
                  onClick={() => setPage((p) => p - 1)}
                >
                  Anterior
                </button>
                <span className={styles.pageInfo}>
                  Página {data.number + 1} de {data.totalPages}
                </span>
                <button
                  className={styles.pageBtn}
                  disabled={page >= data.totalPages - 1}
                  onClick={() => setPage((p) => p + 1)}
                >
                  Siguiente
                </button>
              </div>
            )}
          </div>
        </>
      )}

      {/* ─── Modal rechazo ───────────────────────────────────────────────── */}
      {rejectTarget && (
        <RejectModal
          request={rejectTarget}
          onClose={() => { setRejectTarget(null); setRejectError(null) }}
          onConfirm={(reason) => rejectMutation.mutate({ id: rejectTarget.id, reason })}
          loading={rejectMutation.isPending}
          error={rejectError}
        />
      )}

      {/* ─── Modal comprobante ───────────────────────────────────────────── */}
      {receiptTarget?.paymentReceipt && receiptTarget.paymentReceipt !== 'Sin comprobante' && receiptTarget.paymentReceipt && (
        <ReceiptModal
          url={receiptTarget.paymentReceipt}
          userName={receiptTarget.userName}
          onClose={() => setReceiptTarget(null)}
        />
      )}
    </div>
  )
}
