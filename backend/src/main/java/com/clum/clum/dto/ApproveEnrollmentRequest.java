package com.clum.clum.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO para el body del endpoint de aprobación de inscripción.
 *
 * POST /api/enrollments/clubs/{clubId}/requests/{enrollmentRequestId}/approve
 *
 * El campo roleId es opcional. Si se omite (o se envía null), el servicio
 * asigna el rol MIEMBRO por defecto. Esto simplifica el flujo del frontend:
 * el director puede aprobar sin necesidad de seleccionar un rol explícito.
 */
@Getter
@Setter
public class ApproveEnrollmentRequest {

    /**
     * ID del rol (tabla roles) que se le asignará al usuario aprobado.
     * Opcional — si es null, el backend asigna MIEMBRO por defecto.
     */
    private Long roleId;
}
