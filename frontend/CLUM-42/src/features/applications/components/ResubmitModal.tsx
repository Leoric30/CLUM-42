import React, { useState, useRef } from 'react'
import { resubmitApplication } from '../services/applicationsService'
import styles from './ResubmitModal.module.css'

interface Props {
  applicationId: number
  clubName: string
  onClose: () => void
  onSuccess: () => void
}

export function ResubmitModal({ applicationId, clubName, onClose, onSuccess }: Props) {
  const [file, setFile] = useState<File | null>(null)
  const [fileError, setFileError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const fileRef = useRef<HTMLInputElement>(null)

  function handleFile(e: React.ChangeEvent<HTMLInputElement>) {
    const selected = e.target.files?.[0]
    setFileError(null)
    if (!selected) return
    const allowed = ['application/pdf', 'image/jpeg', 'image/png']
    if (!allowed.includes(selected.type)) {
      setFileError('Solo se aceptan archivos PDF, JPG o PNG')
      return
    }
    if (selected.size > 5 * 1024 * 1024) {
      setFileError('El archivo no puede superar 5 MB')
      return
    }
    setFile(selected)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!file) return
    setError(null)
    setSubmitting(true)
    try {
      await resubmitApplication(applicationId, file)
      onSuccess()
    } catch (err: unknown) {
      if (
        err && typeof err === 'object' && 'response' in err &&
        err.response && typeof err.response === 'object' && 'data' in err.response
      ) {
        const data = (err.response as { data: { error?: string } }).data
        setError(data.error ?? 'Error al reintentar la solicitud')
      } else {
        setError('No se pudo conectar con el servidor')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <h2 className={styles.title}>Reintentar solicitud</h2>
        <p className={styles.desc}>
          Actualiza tu comprobante de pago para {clubName} y vuelve a someter la solicitud.
        </p>

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>Nuevo comprobante de pago</label>
            <button
              type="button"
              className={styles.fileBtn}
              onClick={() => fileRef.current?.click()}
            >
              {file ? file.name : 'Seleccionar archivo (PDF, JPG, PNG — máx. 5 MB)'}
            </button>
            <input
              ref={fileRef}
              type="file"
              accept=".pdf,.jpg,.jpeg,.png"
              onChange={handleFile}
              style={{ display: 'none' }}
            />
            {fileError && <p className={styles.fieldError}>{fileError}</p>}
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <div className={styles.actions}>
            <button type="button" className={styles.cancelBtn} onClick={onClose}>
              Cancelar
            </button>
            <button
              type="submit"
              className={styles.submitBtn}
              disabled={submitting || !file || !!fileError}
            >
              {submitting ? 'Enviando…' : 'Reintentar solicitud'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
