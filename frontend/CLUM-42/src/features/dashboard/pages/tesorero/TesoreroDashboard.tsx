import { Link } from 'react-router-dom'
import { TopBar } from '../../components/TopBar'
import { StatCard } from '../../components/StatCard'
import styles from '../dashboard.module.css'

export function TesoreroDashboard() {
  return (
    <div className={styles.page}>
      <TopBar title="Panel del Tesorero" />

      <div className={styles.pageHeader}>
        <div className={styles.titleGroup}>
          <p className={styles.greeting}>Bienvenido</p>
          <h1 className={styles.title}>Panel de Tesorería</h1>
          <p className={styles.subtitle}>
            Registro de ingresos y egresos, cortes de caja, presupuestos y reportes financieros.
          </p>
        </div>
      </div>

      <div className={styles.statsGrid}>
        <StatCard
          label="Balance del mes"
          value="$0.00"
          sub="Sin movimientos"
          color="green"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M8.433 7.418c.155-.103.346-.196.567-.267v1.698a2.305 2.305 0 01-.567-.267C8.07 8.34 8 8.114 8 8c0-.114.07-.34.433-.582zM11 12.849v-1.698c.22.071.412.164.567.267.364.243.433.468.433.582 0 .114-.07.34-.433.582a2.305 2.305 0 01-.567.267z" />
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-13a1 1 0 10-2 0v.092a4.535 4.535 0 00-1.676.662C6.602 6.234 6 7.009 6 8c0 .99.602 1.765 1.324 2.246.48.32 1.054.545 1.676.662v1.941c-.391-.127-.68-.317-.843-.504a1 1 0 10-1.51 1.31c.562.649 1.413 1.076 2.353 1.253V15a1 1 0 102 0v-.092a4.535 4.535 0 001.676-.662C13.398 13.766 14 12.991 14 12c0-.99-.602-1.765-1.324-2.246A4.535 4.535 0 0011 9.092V7.151c.391.127.68.317.843.504a1 1 0 101.511-1.31c-.563-.649-1.413-1.076-2.354-1.253V5z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Total ingresos"
          value="$0.00"
          sub="Sin datos aún"
          color="indigo"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M3.293 9.707a1 1 0 010-1.414l6-6a1 1 0 011.414 0l6 6a1 1 0 01-1.414 1.414L11 5.414V17a1 1 0 11-2 0V5.414L4.707 9.707a1 1 0 01-1.414 0z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Total egresos"
          value="$0.00"
          sub="Sin datos aún"
          color="red"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M16.707 10.293a1 1 0 010 1.414l-6 6a1 1 0 01-1.414 0l-6-6a1 1 0 111.414-1.414L9 14.586V3a1 1 0 012 0v11.586l4.293-4.293a1 1 0 011.414 0z" clipRule="evenodd" />
            </svg>
          }
        />
        <StatCard
          label="Presupuestos activos"
          value="—"
          sub="Sin datos aún"
          color="amber"
          icon={
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M5 3a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2H5zM5 11a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2v-2a2 2 0 00-2-2H5zM11 5a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V5zM14 11a1 1 0 011 1v1h1a1 1 0 110 2h-1v1a1 1 0 11-2 0v-1h-1a1 1 0 110-2h1v-1a1 1 0 011-1z" />
            </svg>
          }
        />
      </div>

      <div className={styles.twoCol}>
        {/* Movimientos recientes */}
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Movimientos recientes</h2>
            <Link to="/dashboard/finanzas" className={styles.sectionAction}>Ver todos</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              Sin movimientos registrados. Ir a{' '}
              <Link to="/dashboard/finanzas" style={{ color: '#6366f1' }}>Ingresos y egresos</Link>.
            </div>
          </div>
        </div>

        {/* Cortes de caja */}
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>Últimos cortes de caja</h2>
            <Link to="/dashboard/cortes" className={styles.sectionAction}>Ver historial</Link>
          </div>
          <div className={styles.sectionBody}>
            <div className={styles.emptyState}>
              Sin cortes registrados. Ir a{' '}
              <Link to="/dashboard/cortes" style={{ color: '#6366f1' }}>Cortes de caja</Link>.
            </div>
          </div>
        </div>
      </div>

      {/* Presupuestos */}
      <div className={styles.section}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle}>Presupuestos activos</h2>
          <Link to="/dashboard/presupuestos" className={styles.sectionAction}>Ver todos</Link>
        </div>
        <div className={styles.sectionBody}>
          <div className={styles.emptyState}>
            Sin presupuestos activos. Ir a{' '}
            <Link to="/dashboard/presupuestos" style={{ color: '#6366f1' }}>Presupuestos</Link>.
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
            <Link to="/dashboard/finanzas" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M8.433 7.418c.155-.103.346-.196.567-.267v1.698a2.305 2.305 0 01-.567-.267C8.07 8.34 8 8.114 8 8c0-.114.07-.34.433-.582zM11 12.849v-1.698c.22.071.412.164.567.267.364.243.433.468.433.582 0 .114-.07.34-.433.582a2.305 2.305 0 01-.567.267z" />
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-13a1 1 0 10-2 0v.092a4.535 4.535 0 00-1.676.662C6.602 6.234 6 7.009 6 8c0 .99.602 1.765 1.324 2.246.48.32 1.054.545 1.676.662v1.941c-.391-.127-.68-.317-.843-.504a1 1 0 10-1.51 1.31c.562.649 1.413 1.076 2.353 1.253V15a1 1 0 102 0v-.092a4.535 4.535 0 001.676-.662C13.398 13.766 14 12.991 14 12c0-.99-.602-1.765-1.324-2.246A4.535 4.535 0 0011 9.092V7.151c.391.127.68.317.843.504a1 1 0 101.511-1.31c-.563-.649-1.413-1.076-2.354-1.253V5z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Registrar movimiento</span>
            </Link>
            <Link to="/dashboard/cortes" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M4 4a2 2 0 00-2 2v4a2 2 0 002 2V6h10a2 2 0 00-2-2H4zm2 6a2 2 0 012-2h8a2 2 0 012 2v4a2 2 0 01-2 2H8a2 2 0 01-2-2v-4zm6 4a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Nuevo corte de caja</span>
            </Link>
            <Link to="/dashboard/presupuestos" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path d="M5 3a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2V5a2 2 0 00-2-2H5zM5 11a2 2 0 00-2 2v2a2 2 0 002 2h2a2 2 0 002-2v-2a2 2 0 00-2-2H5zM11 5a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V5zM14 11a1 1 0 011 1v1h1a1 1 0 110 2h-1v1a1 1 0 11-2 0v-1h-1a1 1 0 110-2h1v-1a1 1 0 011-1z" />
              </svg>
              <span className={styles.actionLabel}>Nuevo presupuesto</span>
            </Link>
            <Link to="/dashboard/reportes" className={styles.actionCard}>
              <svg className={styles.actionIcon} viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M3 3a1 1 0 000 2v8a2 2 0 002 2h2.586l-1.293 1.293a1 1 0 101.414 1.414L10 15.414l2.293 2.293a1 1 0 001.414-1.414L12.414 15H15a2 2 0 002-2V5a1 1 0 100-2H3zm11 4a1 1 0 10-2 0v4a1 1 0 102 0V7zm-3 1a1 1 0 10-2 0v3a1 1 0 102 0V8zM8 9a1 1 0 00-2 0v2a1 1 0 102 0V9z" clipRule="evenodd" />
              </svg>
              <span className={styles.actionLabel}>Generar reporte</span>
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}
