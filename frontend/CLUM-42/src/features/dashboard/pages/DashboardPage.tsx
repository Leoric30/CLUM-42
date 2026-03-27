import { useCurrentUser, getPrimaryClubRole } from '@/hooks/useCurrentUser'
import { DirectorDashboard } from './director/DirectorDashboard'
import { SubdirectorDashboard } from './subdirector/SubdirectorDashboard'
import { SecretarioDashboard } from './secretario/SecretarioDashboard'
import { LogisticaDashboard } from './logistica/LogisticaDashboard'
import { MediaDashboard } from './media/MediaDashboard'
import { CapellanDashboard } from './capellan/CapellanDashboard'
import { TesoreroDashboard } from './tesorero/TesoreroDashboard'
import { UsuarioDashboard } from './usuario/UsuarioDashboard'

export function DashboardPage() {
  const { data: user } = useCurrentUser()
  const role = getPrimaryClubRole(user)

  switch (role) {
    case 'DIRECTOR':    return <DirectorDashboard />
    case 'SUBDIRECTOR': return <SubdirectorDashboard />
    case 'SECRETARIO':  return <SecretarioDashboard />
    case 'LOGISTICA':   return <LogisticaDashboard />
    case 'MEDIA':       return <MediaDashboard />
    case 'CAPELLAN':    return <CapellanDashboard />
    case 'TESORERO':    return <TesoreroDashboard />
    default:
      // USUARIO o ASPIRANTE sin rol de club — mostrar explorador de clubes
      return <UsuarioDashboard />
  }
}
