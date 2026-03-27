import { Link } from 'react-router-dom'
import { TopBar } from '../../components/TopBar'
import { StatCard } from '../../components/StatCard'
import styles from '../dashboard.module.css'

export function MediaDashboard() {
  return (
    <div className={styles.page}>
      <TopBar title="Panel de Media" />

      <div className={styles.pageHeader}>
        <div className={styles.titleGroup}>
          <p className={styles.greeting}>Bienvenido</p>
          <h1 className={styles.title}>Panel de Media</h1>
          <p className={styles.subtitle}>
            Organización y gestión del contenido multimedia del club.
          </p>
        </div>
      </div>

      <div className={styles.statsGrid}>
        <StatCard
          label="Fotos en galería"
          value="—"
          sub="Sin datos aún"
          color="indigo"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Publicaciones"
          value="—"
          sub="Sin datos aún"
          color="sky"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Videos"
          value="—"
          sub="Sin datos aún"
          color="violet"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M2 6a2 2 0 012-2h6a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V6zM14.553 7.106A1 1 0 0014 8v4a1 1 0 00.553.894l2 1A1 1 0 0018 13V7a1 1 0 00-1.447-.894l-2 1z" />
            </svg>
          }
        />
        <StatCard
          label="Contenido pendiente"
          value="—"
          sub="Sin datos aún"
          color="amber"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
            </svg>
          }
        />
      </div>

      <div className={styles.twoCol}>
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Galería reciente</h2>
            <Link to="/dashboard/galeria" className={styles.sectionAction}>Ver galería</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              No hay fotos recientes. Ir a{' '}
              <Link to="/dashboard/galeria" style={{ color: '#6366f1' }}>Galería</Link>.
            </div>
          </div>
        </div>

        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Publicaciones recientes</h2>
            <Link to="/dashboard/publicaciones" className={styles.sectionAction}>Ver todas</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              No hay publicaciones. Ir a{' '}
              <Link to="/dashboard/publicaciones" style={{ color: '#6366f1' }}>Publicaciones</Link>.
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
            <Link to="/dashboard/galeria" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Subir foto</span>
            </Link>
            <Link to="/dashboard/publicaciones" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M4 4a2 2 0 012-2h4.586A2 2 0 0112 2.586L15.414 6A2 2 0 0116 7.414V16a2 2 0 01-2 2H6a2 2 0 01-2-2V4zm2 6a1 1 0 011-1h6a1 1 0 110 2H7a1 1 0 01-1-1zm1 3a1 1 0 100 2h6a1 1 0 100-2H7z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Nueva publicación</span>
            </Link>
            <Link to="/dashboard/multimedia" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M2 6a2 2 0 012-2h6a2 2 0 012 2v8a2 2 0 01-2 2H4a2 2 0 01-2-2V6zM14.553 7.106A1 1 0 0014 8v4a1 1 0 00.553.894l2 1A1 1 0 0018 13V7a1 1 0 00-1.447-.894l-2 1z" />
              </svg>
              <span className={styles.actionLabel}>Subir video</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
