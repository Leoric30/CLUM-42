import React, { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { getClubs, registerEnrollment, type Club } from '../services/enrollmentService'
import styles from './RegisterPage.module.css'

type FormState = 'idle' | 'loading' | 'success' | 'error'

const ACCEPTED_TYPES = ['image/jpeg', 'image/png', 'application/pdf']
const MAX_FILE_MB = 5

export function RegisterPage() {
  const navigate = useNavigate()

  const [name, setName]     = useState('')
  const [email, setEmail]   = useState('')
  const [clubId, setClubId] = useState<number | ''>('')
  const [file, setFile]     = useState<File | null>(null)

  const [clubs, setClubs]               = useState<Club[]>([])
  const [clubsLoading, setClubsLoading] = useState(true)
  const [clubsError, setClubsError]     = useState(false)

  const [formState, setFormState] = useState<FormState>('idle')
  const [errorMsg, setErrorMsg]   = useState<string | null>(null)
  const [fileError, setFileError] = useState<string | null>(null)

  useEffect(() => {
    getClubs()
      .then(setClubs)
      .catch(() => setClubsError(true))
      .finally(() => setClubsLoading(false))
  }, [])

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const selected = e.target.files?.[0] ?? null
    setFileError(null)

    if (!selected) { 
      setFile(null)
      return 
    }

    if (!ACCEPTED_TYPES.includes(selected.type)) {
      setFileError('Solo se permiten archivos PDF, JPG o PNG.')
      setFile(null)
      e.target.value = ''
      return
    }

    if (selected.size > MAX_FILE_MB * 1024 * 1024) {
      setFileError(`El archivo no debe superar ${MAX_FILE_MB} MB.`)
      setFile(null)
      e.target.value = ''
      return
    }

    setFile(selected)
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!file || clubId === '') return

    setFormState('loading')
    setErrorMsg(null)

    try {
      await registerEnrollment({
        name,
        email,
        clubId: Number(clubId),
        file
      })

      setFormState('success')

    } catch (err: unknown) {
      setFormState('error')

      const axiosErr = err as { response?: { data?: { error?: string } } }

      setErrorMsg(
        axiosErr?.response?.data?.error ??
        'No se pudo enviar la solicitud. Intenta de nuevo.'
      )
    }
  }

  if (formState === 'success') {
    return (
      <div className={styles.page}>
        <div className={styles.imageSide}></div>

        <div className={styles.formSide}>
          <div className={styles.card}>
            <div className={styles.successIcon}>✓</div>

            <h2 className={styles.successTitle}>
              ¡Solicitud enviada!
            </h2>

            <p className={styles.successText}>
              Tu solicitud fue recibida. El director o secretario del club revisará
              tu información y te notificará cuando sea procesada.
            </p>

            <button
              className={styles.button}
              onClick={() => navigate('/login')}
            >
              Ir al inicio de sesión
            </button>
          </div>
        </div>
      </div>
    )
  }

  const isSubmitting = formState === 'loading'

  const canSubmit =
    !!name &&
    !!email &&
    clubId !== '' &&
    !!file &&
    !fileError &&
    !isSubmitting

  return (
    <div className={styles.page}>

      {/* Columna izquierda con imagen */}
      <div className={styles.imageSide}></div>

      {/* Columna derecha con formulario */}
      <div className={styles.formSide}>
        <div className={styles.card}>

          <h1 className={styles.title}>CLUM</h1>

          <p className={styles.subtitle}>
            Solicitud de inscripción
          </p>

          <p className={styles.hint}>
            Completa el formulario. Un director o secretario revisará tu solicitud.
          </p>

          <form
            onSubmit={handleSubmit}
            noValidate
            className={styles.form}
          >

            <div className={styles.field}>
              <label
                htmlFor="name"
                className={styles.label}
              >
                Nombre completo
              </label>

              <input
                id="name"
                type="text"
                className={styles.input}
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Juan Pérez"
                required
                autoFocus
              />
            </div>

            <div className={styles.field}>
              <label
                htmlFor="email"
                className={styles.label}
              >
                Correo electrónico
              </label>

              <input
                id="email"
                type="email"
                className={styles.input}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="juan@ejemplo.com"
                required
                autoComplete="email"
              />
            </div>

            <div className={styles.field}>
              <label
                htmlFor="club"
                className={styles.label}
              >
                Club al que deseas inscribirte
              </label>

              {clubsLoading && (
                <p className={styles.loadingText}>
                  Cargando clubes…
                </p>
              )}

              {clubsError && (
                <p className={styles.errorInline}>
                  No se pudieron cargar los clubes.
                  Recarga la página.
                </p>
              )}

              {!clubsLoading && !clubsError && (
                <select
                  id="club"
                  className={styles.select}
                  value={clubId}
                  onChange={(e) =>
                    setClubId(
                      e.target.value === ''
                        ? ''
                        : Number(e.target.value)
                    )
                  }
                  required
                >
                  <option value="">
                    — Selecciona un club —
                  </option>

                  {clubs.map((c) => (
                    <option
                      key={c.id}
                      value={c.id}
                    >
                      {c.name}
                      {c.description
                        ? ` — ${c.description}`
                        : ''}
                    </option>
                  ))}
                </select>
              )}
            </div>

            <div className={styles.field}>
              <label
                htmlFor="file"
                className={styles.label}
              >
                Comprobante de pago

                <span className={styles.labelHint}>
                  {' '}
                  (PDF, JPG o PNG · máx. {MAX_FILE_MB} MB)
                </span>
              </label>

              <input
                id="file"
                type="file"
                className={styles.fileInput}
                accept=".pdf,.jpg,.jpeg,.png"
                onChange={handleFileChange}
                required
              />

              {file && !fileError && (
                <p className={styles.fileName}>
                  Seleccionado: {file.name}
                </p>
              )}

              {fileError && (
                <p
                  className={styles.errorInline}
                  role="alert"
                >
                  {fileError}
                </p>
              )}
            </div>

            {formState === 'error' && errorMsg && (
              <p
                className={styles.error}
                role="alert"
              >
                {errorMsg}
              </p>
            )}

            <button
              type="submit"
              className={styles.button}
              disabled={!canSubmit}
            >
              {isSubmitting
                ? 'Enviando solicitud…'
                : 'Enviar solicitud'}
            </button>

          </form>

          <p className={styles.loginLink}>
            ¿Ya tienes cuenta?{' '}
            <Link
              to="/login"
              className={styles.link}
            >
              Inicia sesión
            </Link>
          </p>

        </div>
      </div>
    </div>
  )
}
