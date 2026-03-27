import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { LoginPage } from '@/features/auth/pages/LoginPage'
import { RegisterPage } from '@/features/auth/pages/RegisterPage'
import { ProtectedRoute } from '@/components/ProtectedRoute'
import { DashboardLayout } from '@/features/dashboard/components/DashboardLayout'
import { DashboardPage } from '@/features/dashboard/pages/DashboardPage'
import { SolicitudesPage } from '@/features/enrollment/pages/SolicitudesPage'
import { ClubsPage } from '@/features/clubs/pages/ClubsPage'
import { MyApplicationsPage } from '@/features/applications/pages/MyApplicationsPage'

// ─── Placeholder para secciones aún no implementadas ───────────────────────
import { TopBar } from '@/features/dashboard/components/TopBar'

function ComingSoon({ title }: { title: string }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
      <TopBar title={title} />
      <div
        style={{
          background: '#fff',
          borderRadius: '12px',
          padding: '3rem',
          textAlign: 'center',
          color: '#94a3b8',
          boxShadow: '0 1px 3px rgba(0,0,0,0.06)',
        }}
      >
        <p style={{ fontSize: '1.1rem', fontWeight: 600, color: '#475569', marginBottom: '0.5rem' }}>
          {title}
        </p>
        <p style={{ fontSize: '0.875rem' }}>Esta sección está en desarrollo. Próximamente disponible.</p>
      </div>
    </div>
  )
}

export function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />

        {/* ─── Rutas públicas ──────────────────────────────────────────── */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* ─── Área autenticada ─────────────────────────────────────────── */}
        <Route element={<ProtectedRoute />}>
          <Route element={<DashboardLayout />}>

            {/* Panel principal — cambia según el rol del usuario */}
            <Route path="/dashboard" element={<DashboardPage />} />

            {/* ── Compartidas (Director, Subdirector, Secretario) ────────── */}
            <Route
              path="/dashboard/solicitudes"
              element={<SolicitudesPage />}
            />
            <Route
              path="/dashboard/miembros"
              element={<ComingSoon title="Miembros del club" />}
            />
            <Route
              path="/dashboard/ideas"
              element={<ComingSoon title="Ideas y propuestas" />}
            />

            {/* ── Secretario ─────────────────────────────────────────────── */}
            <Route
              path="/dashboard/juntas"
              element={<ComingSoon title="Juntas" />}
            />
            <Route
              path="/dashboard/asistencias"
              element={<ComingSoon title="Asistencias" />}
            />
            <Route
              path="/dashboard/anuncios"
              element={<ComingSoon title="Anuncios" />}
            />
            <Route
              path="/dashboard/permisos"
              element={<ComingSoon title="Permisos" />}
            />

            {/* ── Logística ──────────────────────────────────────────────── */}
            <Route
              path="/dashboard/planes"
              element={<ComingSoon title="Planes de trabajo" />}
            />
            <Route
              path="/dashboard/tareas"
              element={<ComingSoon title="Tareas" />}
            />
            <Route
              path="/dashboard/eventos"
              element={<ComingSoon title="Eventos" />}
            />

            {/* ── Media ──────────────────────────────────────────────────── */}
            <Route
              path="/dashboard/galeria"
              element={<ComingSoon title="Galería" />}
            />
            <Route
              path="/dashboard/publicaciones"
              element={<ComingSoon title="Publicaciones" />}
            />
            <Route
              path="/dashboard/multimedia"
              element={<ComingSoon title="Multimedia" />}
            />

            {/* ── Capellán ───────────────────────────────────────────────── */}
            <Route
              path="/dashboard/reuniones"
              element={<ComingSoon title="Reuniones especiales" />}
            />
            <Route
              path="/dashboard/estudios"
              element={<ComingSoon title="Estudios bíblicos" />}
            />
            <Route
              path="/dashboard/actividades"
              element={<ComingSoon title="Actividades de integración" />}
            />

            {/* ── Tesorero ───────────────────────────────────────────────── */}
            <Route
              path="/dashboard/finanzas"
              element={<ComingSoon title="Ingresos y egresos" />}
            />
            <Route
              path="/dashboard/cortes"
              element={<ComingSoon title="Cortes de caja" />}
            />
            <Route
              path="/dashboard/presupuestos"
              element={<ComingSoon title="Presupuestos" />}
            />
            <Route
              path="/dashboard/reportes"
              element={<ComingSoon title="Reportes financieros" />}
            />

            {/* ── Clubes y solicitudes del usuario ───────────────────────── */}
            <Route
              path="/dashboard/clubs"
              element={<ClubsPage />}
            />
            <Route
              path="/my-requests"
              element={<MyApplicationsPage />}
            />
          </Route>
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
