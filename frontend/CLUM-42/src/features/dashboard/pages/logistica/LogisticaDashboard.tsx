import { Link } from 'react-router-dom'
import { TopBar } from '../../components/TopBar'
import { StatCard } from '../../components/StatCard'
import styles from '../dashboard.module.css'

export function LogisticaDashboard() {
  return (
    <div className={styles.page}>
      <TopBar title="Panel de Logística" />

      <div className={styles.pageHeader}>
        <div className={styles.titleGroup}>
          <p className={styles.greeting}>Bienvenido</p>
          <h1 className={styles.title}>Panel de Logística</h1>
          <p className={styles.subtitle}>
            Planes de trabajo, tareas pendientes y organización de eventos.
          </p>
        </div>
      </div>

      <div className={styles.statsGrid}>
        <StatCard
          label="Tareas pendientes"
          value="—"
          sub="Sin datos aún"
          color="amber"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
              <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm9.707 5.707a1 1 0 00-1.414-1.414L9 12.586l-1.293-1.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Tareas completadas"
          value="—"
          sub="Sin datos aún"
          color="green"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Eventos próximos"
          value="—"
          sub="Sin datos aún"
          color="sky"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Planes activos"
          value="—"
          sub="Sin datos aún"
          color="indigo"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M2 11a1 1 0 011-1h2a1 1 0 011 1v5a1 1 0 01-1 1H3a1 1 0 01-1-1v-5zM8 7a1 1 0 011-1h2a1 1 0 011 1v9a1 1 0 01-1 1H9a1 1 0 01-1-1V7zM14 4a1 1 0 011-1h2a1 1 0 011 1v12a1 1 0 01-1 1h-2a1 1 0 01-1-1V4z" />
            </svg>
          }
        />
      </div>

      <div className={styles.twoCol}>
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Tareas pendientes</h2>
            <Link to="/dashboard/tareas" className={styles.sectionAction}>Ver todas</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              No hay tareas registradas. Ir a{' '}
              <Link to="/dashboard/tareas" style={{ color: '#6366f1' }}>Tareas</Link>.
            </div>
          </div>
        </div>

        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Próximos eventos</h2>
            <Link to="/dashboard/eventos" className={styles.sectionAction}>Ver calendario</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              No hay eventos próximos. Ir a{' '}
              <Link to="/dashboard/eventos" style={{ color: '#6366f1' }}>Eventos</Link>.
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
            <Link to="/dashboard/planes" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M2 11a1 1 0 011-1h2a1 1 0 011 1v5a1 1 0 01-1 1H3a1 1 0 01-1-1v-5zM8 7a1 1 0 011-1h2a1 1 0 011 1v9a1 1 0 01-1 1H9a1 1 0 01-1-1V7zM14 4a1 1 0 011-1h2a1 1 0 011 1v12a1 1 0 01-1 1h-2a1 1 0 01-1-1V4z" />
              </svg>
              <span className={styles.actionLabel}>Nuevo plan</span>
            </Link>
            <Link to="/dashboard/tareas" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm9.707 5.707a1 1 0 00-1.414-1.414L9 12.586l-1.293-1.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Nueva tarea</span>
            </Link>
            <Link to="/dashboard/eventos" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Nuevo evento</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
