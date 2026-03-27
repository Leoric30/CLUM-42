import { Link } from 'react-router-dom'
import { TopBar } from '../../components/TopBar'
import { StatCard } from '../../components/StatCard'
import styles from '../dashboard.module.css'

export function SecretarioDashboard() {
  return (
    <div className={styles.page}>
      <TopBar title="Panel del Secretario" />

      <div className={styles.pageHeader}>
        <div className={styles.titleGroup}>
          <p className={styles.greeting}>Bienvenido</p>
          <h1 className={styles.title}>Panel de Secretaría</h1>
          <p className={styles.subtitle}>
            Gestión de solicitudes, juntas, asistencias, anuncios y permisos.
          </p>
        </div>
      </div>

      <div className={styles.statsGrid}>
        <StatCard
          label="Solicitudes pendientes"
          value="—"
          sub="Sin datos aún"
          color="amber"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
              <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Juntas este mes"
          value="—"
          sub="Sin datos aún"
          color="indigo"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Anuncios activos"
          value="—"
          sub="Sin datos aún"
          color="sky"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M18 3a1 1 0 00-1.447-.894L8.763 6H5a3 3 0 000 6h.28l1.771 5.316A1 1 0 008 18h1a1 1 0 001-1v-4.382l6.553 3.276A1 1 0 0018 15V3z" />
            </svg>
          }
        />
        <StatCard
          label="Permisos en espera"
          value="—"
          sub="Sin datos aún"
          color="violet"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
            </svg>
          }
        />
      </div>

      <div className={styles.twoCol}>
        {/* Solicitudes */}
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Solicitudes de inscripción</h2>
            <Link to="/dashboard/solicitudes" className={styles.sectionAction}>
              Gestionar
            </Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              Sin solicitudes recientes.{' '}
              <Link to="/dashboard/solicitudes" style={{ color: '#6366f1' }}>Ver todas</Link>.
            </div>
          </div>
        </div>

        {/* Próximas juntas */}
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Próximas juntas</h2>
            <Link to="/dashboard/juntas" className={styles.sectionAction}>
              Ver calendario
            </Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              No hay juntas programadas. Ir a{' '}
              <Link to="/dashboard/juntas" style={{ color: '#6366f1' }}>Juntas</Link>.
            </div>
          </div>
        </div>
      </div>

      {/* Acciones rápidas */}
      <div className={styles.section}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle}>Acciones rápidas</h2>
        </div>
        <div className={styles.sectionBody}>
          <div className={styles.quickActions}>
            <Link to="/dashboard/solicitudes" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Solicitudes</span>
            </Link>
            <Link to="/dashboard/juntas" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Nueva junta</span>
            </Link>
            <Link to="/dashboard/asistencias" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Registrar asistencia</span>
            </Link>
            <Link to="/dashboard/anuncios" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M18 3a1 1 0 00-1.447-.894L8.763 6H5a3 3 0 000 6h.28l1.771 5.316A1 1 0 008 18h1a1 1 0 001-1v-4.382l6.553 3.276A1 1 0 0018 15V3z" />
              </svg>
              <span className={styles.actionLabel}>Nuevo anuncio</span>
            </Link>
            <Link to="/dashboard/permisos" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Gestionar permisos</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
