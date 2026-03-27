package com.clum.clum.models.enums;

/**
 * Tipos de notificaciones in-app del sistema de solicitudes.
 *
 * APPLICATION_APPROVED  → La solicitud del usuario fue aprobada.
 * APPLICATION_REJECTED  → La solicitud fue rechazada; se incluye el motivo.
 * APPLICATION_RESUBMITTED → Un aspirante actualizó su comprobante (REINTENTO).
 *                           Se notifica al director para que revise de nuevo.
 */
public enum NotificationType {
    APPLICATION_APPROVED,
    APPLICATION_REJECTED,
    APPLICATION_RESUBMITTED
}
