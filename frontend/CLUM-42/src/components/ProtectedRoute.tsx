import { Navigate, Outlet } from 'react-router-dom'
import { useCurrentUser } from '@/hooks/useCurrentUser'

/**
 * Protege rutas autenticadas.
 * - Si está cargando: muestra spinner.
 * - Si no hay sesión (401 → data undefined): redirige a /login.
 * - Si hay sesión: renderiza <Outlet />.
 */
export function ProtectedRoute() {
  const { data: user, isLoading, isError } = useCurrentUser()

  if (isLoading) {
    return (
      <div style={{ minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <span style={{ color: '#6366f1', fontSize: '1rem' }}>Cargando…</span>
      </div>
    )
  }

  if (isError || !user) {
    return <Navigate to="/login" replace />
  }

  return <Outlet />
}
