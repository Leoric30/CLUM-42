/**
 * Tipos compartidos que reflejan los modelos y DTOs del backend Spring Boot.
 * Mantener sincronizados con com.clum.clum.models y com.clum.clum.dto
 */

// ─── Enums (valores en español, tal como los devuelve el backend) ───────────

/** Roles globales del sistema — deben coincidir con el enum SystemRole del backend */
export type SystemRole = 'ADMIN' | 'DIRECTIVA_GENERAL' | 'USUARIO' | 'ASPIRANTE'

export type RequestStatus = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA' | 'REINTENTO'

export type ClubRoleName =
  | 'DIRECTOR'
  | 'SUBDIRECTOR'
  | 'SECRETARIO'
  | 'LOGISTICA'
  | 'MEDIA'
  | 'CAPELLAN'
  | 'TESORERO'
  | 'CONSEJERO'

// ─── Auth ────────────────────────────────────────────────────────────────────

/** Respuesta de GET /api/auth/me */
export interface CurrentUser {
  email: string
  roles: SystemRole[]
  /** Club roles del usuario — requiere que el backend incluya esto en /api/auth/me */
  clubRoles?: ClubRoleName[]
  clubId?: number
}

/** Respuesta de POST /api/auth/login */
export interface LoginResponse {
  authenticated: boolean
  email: string
}

/** Respuesta de POST /api/auth/logout */
export interface LogoutResponse {
  loggedOut: boolean
}

// ─── Enrollment ──────────────────────────────────────────────────────────────

/** Solicitud de inscripción tal como la devuelve el backend */
export interface EnrollmentRequest {
  id: number
  userName: string
  userEmail: string
  clubId: number
  clubName: string
  status: RequestStatus
  createdAt: string // ISO 8601
  paymentReceipt?: string | null // URL pública S3 o "Sin comprobante"
}

/** Payload para aprobar (POST .../approve). roleId es opcional — sin él se asigna MIEMBRO. */
export interface ApproveEnrollmentRequest {
  roleId?: number
}

/** Payload para rechazar (POST .../reject) */
export interface RejectEnrollmentRequest {
  reason: string
}

// ─── Club ─────────────────────────────────────────────────────────────────────

export interface Club {
  id: number
  name: string
  description: string
}

// ─── Applications (nuevo flujo) ───────────────────────────────────────────────

/**
 * DTO extendido de solicitud — respuesta de /api/applications.
 * Añade rejectionReason, resubmittedAt y version al EnrollmentRequest base.
 */
export interface Application {
  id: number
  userId: number
  userName: string
  userEmail: string
  clubId: number
  clubName: string
  status: RequestStatus
  createdAt: string        // ISO 8601
  paymentReceipt?: string | null
  rejectionReason?: string | null
  resubmittedAt?: string | null
  version: number
}

// ─── Register ────────────────────────────────────────────────────────────────

/** Payload de POST /api/auth/register */
export interface RegisterPayload {
  fullName: string
  email: string
  phone?: string
  password: string
}

/** Respuesta de POST /api/auth/register */
export interface RegisterResponse {
  registered: boolean
  email: string
}

// ─── Notifications ────────────────────────────────────────────────────────────

export type NotificationType =
  | 'APPLICATION_APPROVED'
  | 'APPLICATION_REJECTED'
  | 'APPLICATION_RESUBMITTED'

/** Notificación in-app — respuesta de /api/notifications */
export interface Notification {
  id: number
  tipo: NotificationType
  titulo: string
  mensaje: string
  leida: boolean
  entidadId?: number | null
  fechaCreacion: string   // ISO 8601
  fechaLectura?: string | null
}

// ─── Pagination ──────────────────────────────────────────────────────────────

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
