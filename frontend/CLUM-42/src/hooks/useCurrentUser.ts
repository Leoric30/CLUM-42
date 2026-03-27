import { useQuery } from '@tanstack/react-query'
import { getMe } from '@/features/auth/services/authService'
import type { CurrentUser, ClubRoleName } from '@/types'

export const CURRENT_USER_KEY = ['currentUser'] as const

export function useCurrentUser() {
  return useQuery<CurrentUser>({
    queryKey: CURRENT_USER_KEY,
    queryFn: getMe,
    staleTime: 5 * 60 * 1000, // 5 minutos
    retry: false,
  })
}

/**
 * Retorna el primer rol de club del usuario, o undefined si no tiene.
 * Prioridad: DIRECTOR > SUBDIRECTOR > SECRETARIO > TESORERO > LOGISTICA > CAPELLAN > MEDIA > CONSEJERO
 *
 * NOTA: Requiere que el backend extienda GET /api/auth/me para incluir clubRoles[].
 */
const ROLE_PRIORITY: ClubRoleName[] = [
  'DIRECTOR',
  'SUBDIRECTOR',
  'SECRETARIO',
  'TESORERO',
  'LOGISTICA',
  'CAPELLAN',
  'MEDIA',
  'CONSEJERO',
]

export function getPrimaryClubRole(user: CurrentUser | undefined) {
  if (!user?.clubRoles?.length) return undefined
  return (
    ROLE_PRIORITY.find((role) => user.clubRoles!.includes(role)) ??
    user.clubRoles[0]
  )
}
