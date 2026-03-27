import React, { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useQueryClient } from '@tanstack/react-query'
import { login } from '../services/authService'
import styles from './LoginPage.module.css'
import registerStyles from './RegisterPage.module.css'

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const queryClient = useQueryClient()
  const registeredEmail = (location.state as { registered?: boolean; email?: string } | null)?.email
  const [email, setEmail] = useState(registeredEmail ?? '')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e: React.ChangeEvent<HTMLFormElement>) {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      await login({ email, password })
      queryClient.clear()
      navigate('/dashboard', { replace: true })
    } catch (err: unknown) {
      if (
        err &&
        typeof err === 'object' &&
        'response' in err &&
        err.response &&
        typeof err.response === 'object' &&
        'data' in err.response
      ) {
        const data = (err.response as { data: { error?: string } }).data
        setError(data.error ?? 'Error al iniciar sesión')
      } else {
        setError('No se pudo conectar con el servidor')
      }
    } finally {
      setLoading(false)
    }
  }

  const registeredSuccess = (location.state as { registered?: boolean } | null)?.registered

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <h1 className={styles.title}>CLUM</h1>
        <p className={styles.subtitle}>Ingresa a tu cuenta</p>

        {registeredSuccess && (
          <p style={{ color: '#059669', background: '#ecfdf5', border: '1px solid #6ee7b7',
            borderRadius: 6, padding: '0.625rem 0.75rem', fontSize: '0.875rem', marginBottom: '0.5rem' }}>
            Cuenta creada exitosamente. Inicia sesión para continuar.
          </p>
        )}

        <form onSubmit={handleSubmit} noValidate className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="email" className={styles.label}>
              Correo electrónico
            </label>
            <input
              id="email"
              type="email"
              className={styles.input}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="usuario@ejemplo.com"
              required
              autoComplete="email"
              autoFocus
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="password" className={styles.label}>
              Contraseña
            </label>
            <input
              id="password"
              type="password"
              className={styles.input}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              autoComplete="current-password"
            />
          </div>

          {error && (
            <p className={styles.error} role="alert">
              {error}
            </p>
          )}

          <button
            type="submit"
            className={styles.button}
            disabled={loading || !email || !password}
          >
            {loading ? 'Ingresando…' : 'Iniciar sesión'}
          </button>
        </form>
        <p className={registerStyles.loginLink}>
          ¿No tienes cuenta?{' '}
          <Link to="/register" className={registerStyles.link}>
            Regístrate
          </Link>
        </p>
      </div>
    </div>
  )
}
