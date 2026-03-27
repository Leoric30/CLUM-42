import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import type { ReactNode } from 'react'

/**
 * QueryClient global — configura el comportamiento por defecto de TanStack Query.
 *
 * - staleTime: 60s → los datos en caché se consideran "frescos" por 1 minuto.
 *   Durante ese tiempo, no se re-fetcha aunque el componente se monte de nuevo.
 * - retry: 1 → si una petición falla, se reintenta solo 1 vez antes de
 *   mostrar error (evita reintentos infinitos con el backend caído).
 */
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60,
      retry: 1,
    },
  },
})

interface ProvidersProps {
  children: ReactNode
}

/**
 * Envuelve toda la app con los providers necesarios.
 * Añadir aquí futuros providers (ThemeProvider, AuthContext, etc.)
 */
export function Providers({ children }: ProvidersProps) {
  return (
    <QueryClientProvider client={queryClient}>
      {children}
      {/* DevTools solo aparece en desarrollo, no en el build de producción */}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  )
}
