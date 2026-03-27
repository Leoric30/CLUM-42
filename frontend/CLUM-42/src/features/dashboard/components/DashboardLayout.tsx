import { Outlet } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import styles from './DashboardLayout.module.css'

export function DashboardLayout() {
  return (
    <div className={styles.shell}>
      <Sidebar />
      <div className={styles.main}>
        <div className={styles.content}>
          <Outlet />
        </div>
      </div>
    </div>
  )
}
