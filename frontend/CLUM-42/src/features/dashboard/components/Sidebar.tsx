import { NavLink, useNavigate } from 'react-router-dom'
import { logout } from '@/features/auth/services/authService'
import { useQueryClient } from '@tanstack/react-query'
import { CURRENT_USER_KEY, useCurrentUser, getPrimaryClubRole } from '@/hooks/useCurrentUser'
import type { ClubRoleName } from '@/types'
import styles from './Sidebar.module.css'

// ─── Tipos ──────────────────────────────────────────────────────────────────

interface NavItem {
  label: string
  path: string
  icon: React.ReactNode
}

interface NavSection {
  title?: string
  items: NavItem[]
}

// ─── Iconos SVG inline ──────────────────────────────────────────────────────

const Icons = {
  panel: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M3 4a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 12a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H4a1 1 0 01-1-1v-4zM11 4a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1V4zM11 12a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1v-4z" />
    </svg>
  ),
  solicitudes: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
      <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
    </svg>
  ),
  miembros: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
    </svg>
  ),
  juntas: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
    </svg>
  ),
  asistencias: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
    </svg>
  ),
  anuncios: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M18 3a1 1 0 00-1.447-.894L8.763 6H5a3 3 0 000 6h.28l1.771 5.316A1 1 0 008 18h1a1 1 0 001-1v-4.382l6.553 3.276A1 1 0 0018 15V3z" />
    </svg>
  ),
  permisos: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
    </svg>
  ),
  tareas: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
      <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm9.707 5.707a1 1 0 00-1.414-1.414L9 12.586l-1.293-1.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
    </svg>
  ),
  eventos: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
    </svg>
  ),
  planes: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M2 11a1 1 0 011-1h2a1 1 0 011 1v5a1 1 0 01-1 1H3a1 1 0 01-1-1v-5zM8 7a1 1 0 011-1h2a1 1 0 011 1v9a1 1 0 01-1 1H9a1 1 0 01-1-1V7zM14 4a1 1 0 011-1h2a1 1 0 011 1v12a1 1 0 01-1 1h-2a1 1 0 01-1-1V4z" />
    </svg>
  ),
  galeria: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd" />
    </svg>
  ),
  publicaciones: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
    </svg>
  ),
  multimedia: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M2 6a2 2 0 012-2h6a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V6zM14.553 7.106A1 1 0 0014 8v4a1 1 0 00.553.894l2 1A1 1 0 0018 13V7a1 1 0 00-1.447-.894l-2 1z" />
    </svg>
  ),
  reuniones: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z" />
    </svg>
  ),
  estudios: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z" />
    </svg>
  ),
  actividades: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M12.395 2.553a1 1 0 00-1.45-.385c-.345.23-.614.558-.822.88-.214.33-.403.713-.57 1.116-.334.804-.614 1.768-.84 2.734a31.365 31.365 0 00-.613 3.58 2.64 2.64 0 01-.945-1.067c-.328-.68-.398-1.534-.398-2.654A1 1 0 005.05 6.05 6.981 6.981 0 003 11a7 7 0 1011.95-4.95c-.592-.591-.98-.985-1.348-1.467-.363-.476-.724-1.063-1.207-2.03zM12.12 15.12A3 3 0 017 13s.879.5 2.5.5c0-1 .5-4 1.25-4.5.5 1 .786 1.293 1.371 1.879A2.99 2.99 0 0113 13a2.99 2.99 0 01-.879 2.121z" clipRule="evenodd" />
    </svg>
  ),
  finanzas: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M8.433 7.418c.155-.103.346-.196.567-.267v1.698a2.305 2.305 0 01-.567-.267C8.07 8.34 8 8.114 8 8c0-.114.07-.34.433-.582zM11 12.849v-1.698c.22.071.412.164.567.267.364.243.433.468.433.582 0 .114-.07.34-.433.582a2.305 2.305 0 01-.567.267z" />
      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-13a1 1 0 10-2 0v.092a4.535 4.535 0 00-1.676.662C6.602 6.234 6 7.009 6 8c0 .99.602 1.765 1.324 2.246.48.32 1.054.545 1.676.662v1.941c-.391-.127-.68-.317-.843-.504a1 1 0 10-1.51 1.31c.562.649 1.413 1.076 2.353 1.253V15a1 1 0 102 0v-.092a4.535 4.535 0 001.676-.662C13.398 13.766 14 12.991 14 12c0-.99-.602-1.765-1.324-2.246A4.535 4.535 0 0011 9.092V7.151c.391.127.68.317.843.504a1 1 0 101.511-1.31c-.563-.649-1.413-1.076-2.354-1.253V5z" clipRule="evenodd" />
    </svg>
  ),
  cortes: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
    </svg>
  ),
  presupuestos: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M5 3a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2H5zM5 11a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2v-2a2 2 0 00-2-2H5zM11 5a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V5zM14 11a1 1 0 011 1v1h1a1 1 0 110 2h-1v1a1 1 0 11-2 0v-1h-1a1 1 0 110-2h1v-1a1 1 0 011-1z" />
    </svg>
  ),
  reportes: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M3 3a1 1 0 000 2v8a2 2 0 002 2h2.586l-1.293 1.293a1 1 0 101.414 1.414L10 15.414l2.293 2.293a1 1 0 001.414-1.414L12.414 15H15a2 2 0 002-2V5a1 1 0 100-2H3zm11 4a1 1 0 10-2 0v4a1 1 0 102 0V7zm-3 1a1 1 0 10-2 0v3a1 1 0 102 0V8zM8 9a1 1 0 00-2 0v2a1 1 0 102 0V9z" clipRule="evenodd" />
    </svg>
  ),
  ideas: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M11 3a1 1 0 10-2 0v1a1 1 0 102 0V3zM15.657 5.757a1 1 0 00-1.414-1.414l-.707.707a1 1 0 001.414 1.414l.707-.707zM18 10a1 1 0 01-1 1h-1a1 1 0 110-2h1a1 1 0 011 1zM5.05 6.464A1 1 0 106.464 5.05l-.707-.707a1 1 0 00-1.414 1.414l.707.707zM5 10a1 1 0 01-1 1H3a1 1 0 110-2h1a1 1 0 011 1zM8 16v-1h4v1a2 2 0 11-4 0zM12 14c.015-.298.023-.597.023-.9a7 7 0 10-7.977 6.936A2 2 0 0012 14z" />
    </svg>
  ),
  clubs: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z" />
    </svg>
  ),
  mySolicitudes: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
    </svg>
  ),
  logout: (
    <svg className={styles.navIcon} viewBox="0 0 20 20" fill="currentColor">
      <path fillRule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clipRule="evenodd" />
    </svg>
  ),
}

// ─── Configuración de navegación por rol ────────────────────────────────────

const NAV_CONFIG: Partial<Record<ClubRoleName, NavSection[]>> = {
  DIRECTOR: [
    {
      items: [
        { label: 'Panel', path: '/dashboard', icon: Icons.panel },
        { label: 'Solicitudes', path: '/dashboard/solicitudes', icon: Icons.solicitudes },
        { label: 'Miembros', path: '/dashboard/miembros', icon: Icons.miembros },
      ],
    },
  ],
  SUBDIRECTOR: [
    {
      items: [
        { label: 'Panel', path: '/dashboard', icon: Icons.panel },
        { label: 'Solicitudes', path: '/dashboard/solicitudes', icon: Icons.solicitudes },
        { label: 'Miembros', path: '/dashboard/miembros', icon: Icons.miembros },
        { label: 'Ideas y propuestas', path: '/dashboard/ideas', icon: Icons.ideas },
      ],
    },
  ],
  SECRETARIO: [
    {
      items: [{ label: 'Panel', path: '/dashboard', icon: Icons.panel }],
    },
    {
      title: 'Inscripciones',
      items: [
        { label: 'Solicitudes', path: '/dashboard/solicitudes', icon: Icons.solicitudes },
        { label: 'Permisos', path: '/dashboard/permisos', icon: Icons.permisos },
      ],
    },
    {
      title: 'Reuniones',
      items: [
        { label: 'Juntas', path: '/dashboard/juntas', icon: Icons.juntas },
        { label: 'Asistencias', path: '/dashboard/asistencias', icon: Icons.asistencias },
        { label: 'Anuncios', path: '/dashboard/anuncios', icon: Icons.anuncios },
      ],
    },
  ],
  LOGISTICA: [
    {
      items: [{ label: 'Panel', path: '/dashboard', icon: Icons.panel }],
    },
    {
      title: 'Organización',
      items: [
        { label: 'Planes de trabajo', path: '/dashboard/planes', icon: Icons.planes },
        { label: 'Tareas', path: '/dashboard/tareas', icon: Icons.tareas },
        { label: 'Eventos', path: '/dashboard/eventos', icon: Icons.eventos },
      ],
    },
  ],
  MEDIA: [
    {
      items: [{ label: 'Panel', path: '/dashboard', icon: Icons.panel }],
    },
    {
      title: 'Contenido',
      items: [
        { label: 'Galería', path: '/dashboard/galeria', icon: Icons.galeria },
        { label: 'Publicaciones', path: '/dashboard/publicaciones', icon: Icons.publicaciones },
        { label: 'Multimedia', path: '/dashboard/multimedia', icon: Icons.multimedia },
      ],
    },
  ],
  CAPELLAN: [
    {
      items: [{ label: 'Panel', path: '/dashboard', icon: Icons.panel }],
    },
    {
      title: 'Espiritual',
      items: [
        { label: 'Reuniones', path: '/dashboard/reuniones', icon: Icons.reuniones },
        { label: 'Estudios bíblicos', path: '/dashboard/estudios', icon: Icons.estudios },
        { label: 'Actividades', path: '/dashboard/actividades', icon: Icons.actividades },
      ],
    },
  ],
  TESORERO: [
    {
      items: [{ label: 'Panel', path: '/dashboard', icon: Icons.panel }],
    },
    {
      title: 'Finanzas',
      items: [
        { label: 'Ingresos y egresos', path: '/dashboard/finanzas', icon: Icons.finanzas },
        { label: 'Cortes de caja', path: '/dashboard/cortes', icon: Icons.cortes },
        { label: 'Presupuestos', path: '/dashboard/presupuestos', icon: Icons.presupuestos },
        { label: 'Reportes', path: '/dashboard/reportes', icon: Icons.reportes },
      ],
    },
  ],
}

const ROLE_LABELS: Partial<Record<ClubRoleName, string>> = {
  DIRECTOR: 'Director',
  SUBDIRECTOR: 'Subdirector',
  SECRETARIO: 'Secretario',
  LOGISTICA: 'Logística',
  MEDIA: 'Media',
  CAPELLAN: 'Capellán',
  TESORERO: 'Tesorero',
  CONSEJERO: 'Consejero',
}

// ─── Componente ─────────────────────────────────────────────────────────────

export function Sidebar() {
  const { data: user } = useCurrentUser()
  const primaryRole = getPrimaryClubRole(user)
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const userNavFallback: NavSection[] = [
    {
      items: [
        { label: 'Inicio', path: '/dashboard', icon: Icons.panel },
        { label: 'Clubes disponibles', path: '/dashboard/clubs', icon: Icons.clubs },
        { label: 'Mis solicitudes', path: '/my-requests', icon: Icons.mySolicitudes },
      ],
    },
  ]

  const sections = (primaryRole && NAV_CONFIG[primaryRole]) ?? userNavFallback

  async function handleLogout() {
    await logout()
    queryClient.clear()
    navigate('/login', { replace: true })
  }

  return (
    <aside className={styles.sidebar}>
      <div className={styles.header}>
        <h1 className={styles.brand}>CLUM</h1>
        <p className={styles.clubName}>Club Universitario Doulos</p>
      </div>

      {primaryRole && (
        <div className={styles.roleBadge}>{ROLE_LABELS[primaryRole] ?? primaryRole}</div>
      )}

      <nav className={styles.nav}>
        {sections.map((section, i) => (
          <div key={i}>
            {section.title && (
              <p className={styles.sectionLabel}>{section.title}</p>
            )}
            {section.items.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                end={item.path === '/dashboard'}
                className={({ isActive }) =>
                  `${styles.navLink} ${isActive ? styles.active : ''}`
                }
              >
                {item.icon}
                {item.label}
              </NavLink>
            ))}
          </div>
        ))}
      </nav>

      <div className={styles.footer}>
        <button className={styles.logoutBtn} onClick={handleLogout}>
          {Icons.logout}
          Cerrar sesión
        </button>
      </div>
    </aside>
  )
}
