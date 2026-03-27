package com.clum.clum.models.enums;

/**
 * Estados posibles de una solicitud de inscripción a un club.
 *
 * PENDIENTE  → Recién enviada, esperando revisión del director.
 * APROBADA   → El aspirante fue aceptado y se le asignó un rol en el club.
 * RECHAZADA  → La solicitud fue denegada; el motivo se guarda en rejectionReason.
 * REINTENTO  → El aspirante actualizó su comprobante tras un rechazo y volvió a
 *              someter la solicitud. El registro anterior no se elimina; se reutiliza
 *              con el nuevo comprobante. Tratado igual que PENDIENTE por el director.
 */
public enum RequestStatus {
    PENDIENTE,
    APROBADA,
    RECHAZADA,
    REINTENTO
}
