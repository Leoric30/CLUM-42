import { Link } from 'react-router-dom'
import { TopBar } from '../../components/TopBar'
import { StatCard } from '../../components/StatCard'
import styles from '../dashboard.module.css'

export function CapellanDashboard() {
  return (
    <div className={styles.page}>
      <TopBar title="Panel del Capellán" />

      <div className={styles.pageHeader}>
        <div className={styles.titleGroup}>
          <p className={styles.greeting}>Bienvenido</p>
          <h1 className={styles.title}>Panel del Capellán</h1>
          <p className={styles.subtitle}>
            Reuniones especiales, estudios bíblicos y actividades de integración.
          </p>
        </div>
      </div>

      <div className={styles.statsGrid}>
        <StatCard
          label="Reuniones este mes"
          value="—"
          sub="Sin datos aún"
          color="indigo"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z" />
            </svg>
          }
        />
        <StatCard
          label="Estudios bíblicos"
          value="—"
          sub="Sin datos aún"
          color="violet"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z" />
            </svg>
          }
        />
        <StatCard
          label="Actividades planeadas"
          value="—"
          sub="Sin datos aún"
          color="sky"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M12.395 2.553a1 1 0 00-1.45-.385c-.345.23-.614.558-.822.88-.214.33-.403.713-.57 1.116-.334.804-.614 1.768-.84 2.734a31.365 31.365 0 00-.613 3.58 2.64 2.64 0 01-.945-1.067c-.328-.68-.398-1.534-.398-2.654A1 1 0 005.05 6.05 6.981 6.981 0 003 11a7 7 0 1011.95-4.95c-.592-.591-.98-.985-1.348-1.467-.363-.476-.724-1.063-1.207-2.03zM12.12 15.12A3 3 0 017 13s.879.5 2.5.5c0-1 .5-4 1.25-4.5.5 1 .786 1.293 1.371 1.879A2.99 2.99 0 0113 13a2.99 2.99 0 01-.879 2.121z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Aspirantes en estudio"
          value="—"
          sub="Sin datos aún"
          color="green"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
            </svg>
          }
        />
      </div>

      <div className={styles.twoCol}>
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Próximas reuniones</h2>
            <Link to="/dashboard/reuniones" className={styles.sectionAction}>Ver calendario</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              Sin reuniones próximas. Ir a{' '}
              <Link to="/dashboard/reuniones" style={{ color: '#6366f1' }}>Reuniones</Link>.
            </div>
          </div>
        </div>

        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Estudios bíblicos recientes</h2>
            <Link to="/dashboard/estudios" className={styles.sectionAction}>Ver todos</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              Sin estudios registrados. Ir a{' '}
              <Link to="/dashboard/estudios" style={{ color: '#6366f1' }}>Estudios</Link>.
            </div>
          </div>
        </div>
      </div>

      <div className={styles.section}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle}>Acciones rápidas</h2>
        </div>
        <div className={styles.sectionBody}>
          <div className={styles.quickActions}>
            <Link to="/dashboard/reuniones" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3zM6 8a2 2 0 11-4 0 2 2 0 014 0zM16 18v-3a5.972 5.972 0 00-.75-2.906A3.005 3.005 0 0119 15v3h-3zM4.75 12.094A5.973 5.973 0 004 15v3H1v-3a3 3 0 013.75-2.906z" />
              </svg>
              <span className={styles.actionLabel}>Nueva reunión</span>
            </Link>
            <Link to="/dashboard/estudios" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M9 4.804A7.968 7.968 0 005.5 4c-1.255 0-2.443.29-3.5.804v10A7.969 7.969 0 015.5 14c1.669 0 3.218.51 4.5 1.385A7.962 7.962 0 0114.5 14c1.255 0 2.443.29 3.5.804v-10A7.968 7.968 0 0014.5 4c-1.255 0-2.443.29-3.5.804V12a1 1 0 11-2 0V4.804z" />
              </svg>
              <span className={styles.actionLabel}>Nuevo estudio</span>
            </Link>
            <Link to="/dashboard/actividades" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M12.395 2.553a1 1 0 00-1.45-.385c-.345.23-.614.558-.822.88-.214.33-.403.713-.57 1.116-.334.804-.614 1.768-.84 2.734a31.365 31.365 0 00-.613 3.58 2.64 2.64 0 01-.945-1.067c-.328-.68-.398-1.534-.398-2.654A1 1 0 005.05 6.05 6.981 6.981 0 003 11a7 7 0 1011.95-4.95c-.592-.591-.98-.985-1.348-1.467-.363-.476-.724-1.063-1.207-2.03zM12.12 15.12A3 3 0 017 13s.879.5 2.5.5c0-1 .5-4 1.25-4.5.5 1 .786 1.293 1.371 1.879A2.99 2.99 0 0113 13a2.99 2.99 0 01-.879 2.121z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Nueva actividad</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
