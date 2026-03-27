import { useQuery, useQueryClient } from '@tanstack/react-query'
import { getUnreadCount } from '../services/notificationsService'

export const UNREAD_COUNT_KEY = ['notifications', 'unread-count']

/**
 * Hook que hace polling del conteo de notificaciones no leídas cada 60 s.
 * Usado para mostrar el badge de la campana en la TopBar.
 */
export function useUnreadCount() {
  return useQuery({
    queryKey: UNREAD_COUNT_KEY,
    queryFn: getUnreadCount,
    refetchInterval: 60_000,  // Polling cada 60 segundos
    staleTime: 30_000,
  })
}

/**
 * Invalida el conteo para que se refresque inmediatamente.
 * Llamar después de marcar notificaciones como leídas.
 */
export function useInvalidateUnreadCount() {
  const queryClient = useQueryClient()
  return () => queryClient.invalidateQueries({ queryKey: UNREAD_COUNT_KEY })
}
