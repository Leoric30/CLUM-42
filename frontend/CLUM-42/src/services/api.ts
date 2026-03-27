import axios from 'axios'

/**
 * Instancia central de axios para todas las peticiones al backend.
 *
 * - baseURL viene de la variable de entorno VITE_API_BASE_URL (definida en .env.*)
 * - withCredentials: true → envía la cookie de sesión en cada petición
 *   (Spring Security usa sesiones por cookie, no Bearer token)
 * - El proxy de Vite redirige /api → http://localhost:8080 en desarrollo,
 *   por lo que nunca hay problemas de CORS en local.
 */
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

/**
 * Interceptor de respuesta global:
 * Si el backend devuelve 401 (sesión expirada / no autenticado),
 * redirigimos al usuario al login sin necesidad de manejar esto
 * en cada componente individualmente.
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Solo redirigir si no estamos ya en la página de login
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  },
)

export default api
