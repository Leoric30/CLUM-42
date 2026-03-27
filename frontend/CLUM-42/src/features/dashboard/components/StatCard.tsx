import styles from './StatCard.module.css'

type Color = 'indigo' | 'green' | 'amber' | 'red' | 'sky' | 'violet'

interface StatCardProps {
  label: string
  value: string | number
  sub?: string
  color?: Color
  icon: React.ReactNode
}

export function StatCard({ label, value, sub, color = 'indigo', icon }: StatCardProps) {
  return (
    <div className={`${styles.card} ${styles[color]}`}>
      <div className={styles.iconWrap}>
        <span className={styles.icon}>{icon}</span>
      </div>
      <div className={styles.body}>
        <p className={styles.label}>{label}</p>
        <p className={styles.value}>{value}</p>
        {sub && <p className={styles.sub}>{sub}</p>}
      </div>
    </div>
  )
}
